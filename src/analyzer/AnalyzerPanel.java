package analyzer;

import analyzer.observer.DataModelObserverIFace;
import analyzer.observer.DataModelSubject;
import analyzer.observer.DataModelSubjectIFace;
import test.ProgramTest;
import util.Aggregation;
import util.Pair;
import analyzer.bpm.*;
import analyzer.util.UneditableTableModel;
import analyzer.plugin.ifaces.AnalyzerBytePluginIFace;
import analyzer.plugin.ifaces.AnalyzerDoublePluginIFace;
import analyzer.plugin.ifaces.AnalyzerIntPluginIFace;
import util.Time;
import util.audio.AudioConverter;
import util.audio.AudioUtilities;
import util.audio.wave.ByteWave;
import util.audio.wave.DoubleWave;
import util.swing.ErrorFrame;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import util.logging.MyLogger;

public class AnalyzerPanel extends JPanel implements LeavingPanelIFace {
    private static final long serialVersionUID = 1L;
    public static final String ANALYZED_AUDIO_XML_FILENAME = "ANALYZED_AUDIO.xml";

    private JFrame frame;

    private JPanel buttonPanel;
    private JButton fileChooseButton;
    private JButton analyzeButton;
    private JButton removeSelectedButton;
    private JButton[] buttons;

    private JLabel checkBoxTextLabel;
    private JScrollPane checkBoxScrollPane;
    private JCheckBox[] checkBoxes;

    private JScrollPane selectedFilesPane;
    private DefaultTableModel dataModel;
    private JTable table;

    private JFileChooser fileChooser;

    private DataModelSubjectIFace subject;

    private ByteWave byteWave;


    @Override
    public void leavingPanel() {
        int rowCount = table.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            dataModel.removeRow(0);
        }
    }


    public AnalyzerPanel(JFrame thisWindow, DataModelObserverIFace[] observers) {
        byteWave = new ByteWave();

        frame = thisWindow;
        this.setLayout(new BorderLayout());
        MyLogger.log("Creating Data model subject", 1);
        subject = new DataModelSubject(observers, thisWindow);
        MyLogger.log("Created data model subject", -1);

        MyLogger.log("Adding analyzer panel components", 1);
        buttonPanel = new JPanel(new FlowLayout());
        buttons = new JButton[3];
        fileChooseButton = new JButton("Choose file/directory to analyze");
        analyzeButton = new JButton("Analyze selected songs");
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rowCount = dataModel.getRowCount();
                boolean anyFileAnalyzed = false;
                for(int i = 0; i < rowCount; i++) {
                    analyze((String)dataModel.getValueAt(0, 0));
                    anyFileAnalyzed = true;
                    dataModel.removeRow(0);
                }
                if(anyFileAnalyzed) {
                    AnalyzerXML.createXMLFile(ANALYZED_AUDIO_XML_FILENAME, AnalyzerXML.getXMLDoc().getFirstChild(), frame);
                    subject.notifyObservers();
                }
            }
        });

        removeSelectedButton = new JButton("Remove marked lines");
        removeSelectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] rows = table.getSelectedRows();
                Arrays.sort(rows);
                for (int i = rows.length - 1; i >= 0; i--) {
                    dataModel.removeRow(rows[i]);
                }
            }
        });

        buttons[0] = fileChooseButton;
        buttons[1] = analyzeButton;
        buttons[2] = removeSelectedButton;

        for(int i = 0; i < buttons.length; i++) {
            buttonPanel.add(buttons[i]);
        }
        this.add(buttonPanel, BorderLayout.SOUTH);


        Box box = Box.createVerticalBox();
        checkBoxTextLabel = new JLabel("What information should be extracted from audio file.");
        checkBoxes = new JCheckBox[12];
        checkBoxes[0] = new JCheckBox("Find size in bytes");
        checkBoxes[1] = new JCheckBox("Find length");
        checkBoxes[2] = new JCheckBox("File format");
        checkBoxes[3] = new JCheckBox("Encoding");
        checkBoxes[4] = new JCheckBox("Sample size");
        checkBoxes[5] = new JCheckBox("Find sampling rate");
        checkBoxes[6] = new JCheckBox("number of channels");
        checkBoxes[7] = new JCheckBox("Endianness");
        checkBoxes[8] = new JCheckBox("Find sample peaks");
        checkBoxes[9] = new JCheckBox("Find sample average");
        checkBoxes[10] = new JCheckBox("Find RMS (special average)");
        checkBoxes[11] = new JCheckBox("BPM");

        box.add(checkBoxTextLabel);
        for(int i = 0; i < checkBoxes.length; i++) {
            box.add(checkBoxes[i]);
            checkBoxes[i].setSelected(true);
        }
        MyLogger.log("Added analyzer panel components", -1);

        MyLogger.log("Adding analyzer plugins", 1);
        loadPlugins();
        addPluginsToBox(box);
        MyLogger.log("Added analyzer plugins", -1);

        checkBoxScrollPane = new JScrollPane(box);
        this.add(checkBoxScrollPane, BorderLayout.EAST);



        String[] header = {"Selected files"};
        String[] names = new String[] {};

        String[][] data = new String[names.length][1];
        for (int i = 0; i < names.length; i++) {
            data[i][0] = names[i];
        }
        dataModel = new UneditableTableModel(data, header);
        table = new JTable(dataModel);
        selectedFilesPane = new JScrollPane(table);
        this.add(selectedFilesPane, BorderLayout.WEST);
        this.add(selectedFilesPane, BorderLayout.CENTER);


        fileChooser = new JFileChooser();
        fileChooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                if(currentDirectory != null) {
                    fileChooser.setCurrentDirectory(currentDirectory);
                }
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                performActionForFileChooser(fileChooser.showOpenDialog(thisWindow));
            }
        });
    }


    private List<Pair<JCheckBox, AnalyzerBytePluginIFace>> bytePluginPairs = new ArrayList<>();
    private List<Pair<JCheckBox, AnalyzerIntPluginIFace>> intPluginPairs = new ArrayList<>();
    private List<Pair<JCheckBox, AnalyzerDoublePluginIFace>> doublePluginPairs = new ArrayList<>();

    private void loadPlugins() {
        MyLogger.log("Adding analyzer byte plugins", 1);
        List<AnalyzerBytePluginIFace> bytePlugins = AnalyzerBytePluginIFace.loadPlugins();
        for(AnalyzerBytePluginIFace p : bytePlugins) {
            JCheckBox checkBox = new JCheckBox(p.getName());
            checkBox.setToolTipText(p.getTooltip());
            checkBox.setSelected(true);
            bytePluginPairs.add(new Pair<>(checkBox, p));
        }
        MyLogger.log("Added analyzer byte plugins", -1);

        MyLogger.log("Adding analyzer int plugins", 1);
        List<AnalyzerIntPluginIFace> intPlugins = AnalyzerIntPluginIFace.loadPlugins();
        for(AnalyzerIntPluginIFace p : intPlugins) {
            JCheckBox checkBox = new JCheckBox(p.getName());
            checkBox.setToolTipText(p.getTooltip());
            checkBox.setSelected(true);
            intPluginPairs.add(new Pair<>(checkBox, p));
        }
        MyLogger.log("Added analyzer int plugins", -1);

        MyLogger.log("Adding analyzer double plugins", 1);
        List<AnalyzerDoublePluginIFace> doublePlugins = AnalyzerDoublePluginIFace.loadPlugins();
        for(AnalyzerDoublePluginIFace p : doublePlugins) {
            JCheckBox checkBox = new JCheckBox(p.getName());
            checkBox.setToolTipText(p.getTooltip());
            checkBox.setSelected(true);
            doublePluginPairs.add(new Pair<>(checkBox, p));
        }
        MyLogger.log("Added analyzer int plugins", -1);
    }

    private void addPluginsToBox(Box box) {
        for(Pair<JCheckBox, AnalyzerBytePluginIFace> p : bytePluginPairs) {
            box.add(p.getKey());
        }
        for(Pair<JCheckBox, AnalyzerIntPluginIFace> p : intPluginPairs) {
            box.add(p.getKey());
        }
        for(Pair<JCheckBox, AnalyzerDoublePluginIFace> p : doublePluginPairs) {
            box.add(p.getKey());
        }
    }

    private void runSelectedPlugins(ByteWave byteWave, List<Pair<String, String>> list) {
        runSelectedPluginsByte(byteWave, list);
        runSelectedPluginsInt(byteWave, list);
        runSelectedPluginsDouble(byteWave, list);
    }

    private void runSelectedPluginsByte(ByteWave byteWave, List<Pair<String, String>> list) {
        for(Pair<JCheckBox, AnalyzerBytePluginIFace> p : bytePluginPairs) {
            if(p.getKey().isSelected()) {
                list.add(analyzeBytePlugin(byteWave, p.getValue()));
            }
        }
    }

    private Pair<String, String> analyzeBytePlugin(ByteWave byteWave, AnalyzerBytePluginIFace plugin) {
        return plugin.analyze(byteWave.song, byteWave.numberOfChannels, byteWave.sampleSizeInBytes,
                              byteWave.sampleRate, byteWave.isBigEndian, byteWave.isSigned);
    }

    private void runSelectedPluginsInt(ByteWave byteWave, List<Pair<String, String>> list) {
        int[] wave = null;
        for(Pair<JCheckBox, AnalyzerIntPluginIFace> p : intPluginPairs) {
            if(p.getKey().isSelected()) {
                if(wave != null) {
                    try {
                        wave = AudioConverter.convertBytesToSamples(byteWave.song, byteWave.sampleSizeInBytes,
                                                                    byteWave.isBigEndian, byteWave.isSigned);
                    } catch (IOException e) {
                        return;
                    }
                }
                list.add(p.getValue().analyze(wave, byteWave.numberOfChannels, byteWave.sampleRate));
            }
        }
    }

    private void runSelectedPluginsDouble(ByteWave byteWave, List<Pair<String, String>> list) {
        DoubleWave wave = null;
        for(Pair<JCheckBox, AnalyzerDoublePluginIFace> p : doublePluginPairs) {
            if(p.getKey().isSelected()) {
                if(wave != null) {
                    wave = new DoubleWave(byteWave, false);
                }
                list.add(p.getValue().analyze(wave));
            }
        }
    }


    private File currentDirectory;
    private void performActionForFileChooser(int returnVal) {
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            currentDirectory = fileChooser.getCurrentDirectory();
            File[] files = fileChooser.getSelectedFiles();
            addFilesToModel(files);
        }
    }

    private void addFilesToModel(File[] files) {
        for(int i = 0; i < files.length; i++) {
            if(files[i].isDirectory()) {
                addFilesToModel(files[i].listFiles());
            }
            else {
                try {
                    dataModel.addRow(new String[]{ files[i].getCanonicalPath() });
                } catch (IOException e) {			// Shouldn't happen
                    MyLogger.logException(e);
                    new ErrorFrame(frame, "Unknown error");
                }
            }
        }
    }


    public static List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> createAndPrintDifList() {
        List<Pair<String, Pair<String, Integer>>> bpmList = new ArrayList<>();
        // the total length in files is 16 - 1 + 10 = 25 -> TODO:
        // trva to minimalne 8x tolik - vzhledem k tomu ze ty ostatni
        // testovaci stopy jsou delsi nez 1 minuta takze to trva 8 * 4 = 32x dyl + nejaky drobny, takze 35x rekneme
        //
        String[] paths = new String[] {
                "C:\\Users\\Radek\\Documents\\bpmTestFiles",                                                        //0
                "C:\\Users\\Radek\\Documents\\Anthem Of The Peaceful Army (Album)\\09 Brave New World.mp3",
                "C:\\Users\\Radek\\Documents\\Anthem Of The Peaceful Army (Album)\\01 Age Of Man.mp3",
                "C:\\Users\\Radek\\Documents\\Anthem Of The Peaceful Army (Album)\\03 When The Curtain Falls.mp3",
                "D:\\MP3 HEAVY METAL\\Iron Maiden\\1981 Iron Maiden - Killers\\02 - Wrathchild.mp3",                //4
                "D:\\MP3 HEAVY METAL\\Iron Maiden\\1981 Iron Maiden - Killers\\05 - Genghis Khnan.mp3",
                "D:\\MP3 HEAVY METAL\\Iron Maiden\\1981 Iron Maiden - Killers\\10 - Drifter.mp3",
                "D:\\MP3 HEAVY METAL\\Ghost\\Ghost - Prequelle (Deluxe 2018) [320]\\03. Faith.mp3",
                "D:\\MP3 HEAVY METAL\\Ghost\\Ghost - Prequelle (Deluxe 2018) [320]\\06. Dance Macabre.mp3",         //8
                "D:\\MP3 HEAVY METAL\\Ghost\\Ghost - Prequelle (Deluxe 2018) [320]\\11.  It's a Sin.mp3",
                "D:\\MP3 HEAVY METAL\\Burzum\\1992 - Burzum (Deathlike Silence Productions)\\08 - My Journey To The Stars.mp3",
                "D:\\MP3 HEAVY METAL\\Ennio Morricone\\Ennio Morricone - Best of the West by r.rickie\\1965 - Pro pár dolarů navíc.mp3",
                "D:\\MP3 HEAVY METAL\\Abba\\1975 - ABBA\\01 Mamma Mia.mp3",                                         //12
                "D:\\MP3 HEAVY METAL\\Abba\\1974 - WATERLOO\\01 Waterloo.mp3",
                "D:\\MP3 HEAVY METAL\\Talking heads\\(01) TALKING HEADS - Talking Heads '77   1977  (2006 Remastered) + (Video Live)\\10 - Psycho Killer.mp3",
                "D:\\MP3 HEAVY METAL\\AC DC\\acdc-rock\\01. Rock or Bust.mp3"                                       //15
        };
        for(int i = 0; i < paths.length; i++) {
            File dir = new File(paths[i]);
            if(dir.isDirectory()) {
                for (File f : dir.listFiles()) {
                    String path = f.getAbsolutePath();
                    findCoefs(path, bpmList);
                }
            }
            else {
                findCoefs(paths[i], bpmList);
            }
        }

        List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> difList = createDifList(bpmList);
        sortDifList(difList);
        printDifList(difList, 4);
        return difList;
    }


    public static void findCoefs(String filename, List<Pair<String, Pair<String, Integer>>> bpmList) {
        ProgramTest.debugPrint("Currently working with:", filename);
        ByteWave byteWave = new ByteWave();

        try {
            if(!byteWave.loadSong(filename, true)) {
                MyLogger.logWithoutIndentation("Error in method analyze(String filename) in AnalyzerPanel\n" +
                        filename + "\n" + AudioUtilities.LOG_MESSAGE_WHEN_SET_VARIABLES_RETURN_FALSE);
                return;
            }
        } catch (IOException e) {
            MyLogger.logException(e);
            return;
        }

        try {
            byteWave.convertToMono();
        }
        catch(IOException e) {
            return;
        }

        addSongBPMToList(byteWave, bpmList);
    }


    public void analyze(String filename) {
        File file = new File(filename);
        List<Pair<String, String>> list = new ArrayList<>();
        Pair<String, String> pair = new Pair<>(SongLibraryPanel.HEADER_NAME_COLUMN_TITLE, file.getName());
        list.add(pair);
        pair = new Pair<>("Path", file.getAbsolutePath());
        list.add(pair);

        try {
            if(!byteWave.loadSong(filename, true)) {
                MyLogger.logWithoutIndentation("Error in method analyze(String filename) in AnalyzerPanel\n" +
                        filename + "\n" + AudioUtilities.LOG_MESSAGE_WHEN_SET_VARIABLES_RETURN_FALSE);
                return;
            }
        } catch (IOException e) {
            MyLogger.logException(e);
            return;
        }

        int numberOfChannels = byteWave.numberOfChannels;

        try {
            byteWave.convertToMono();
        }
        catch(IOException e) {
            return;
        }





        if(checkBoxes[0].isSelected()) {
            list.add(analyzeSizeInBytes(byteWave));
        }
        if(checkBoxes[1].isSelected()) {
            list.add(analyzeSongLength(byteWave));
        }
        if(checkBoxes[2].isSelected()) {
            list.add(analyzeFileFormat(byteWave));
        }
        if(checkBoxes[3].isSelected()) {
            list.add(analyzeEncoding(byteWave));
        }
        if(checkBoxes[4].isSelected()) {
            list.add(analyzeSampleSize(byteWave));
        }
        if(checkBoxes[5].isSelected()) {
            list.add(analyzeSampleRate(byteWave));
        }
        if(checkBoxes[6].isSelected()) {
            list.add(analyzeNumberOfChannels(numberOfChannels));
        }
        if(checkBoxes[7].isSelected()) {
            list.add(analyzeEndianness(byteWave));
        }

        double[] mods = null;
        if(checkBoxes[8].isSelected() || checkBoxes[9].isSelected() || checkBoxes[10].isSelected()) {
            try {
                mods = Aggregation.calculateAllAggregations(byteWave.song, byteWave.sampleSizeInBytes,
                                                            byteWave.isBigEndian, byteWave.isSigned);
            } catch (IOException e) {
                MyLogger.logException(e);
                new ErrorFrame(frame, "Invalid sample size:\t" + e.getMessage());
            }
        }
        if(checkBoxes[8].isSelected()) {
            list.add(analyzeSampleMax(mods));
            list.add(analyzeSampleMin(mods));
        }
        if(checkBoxes[9].isSelected()) {
            list.add(analyzeSampleAverage(mods));
        }
        if(checkBoxes[10].isSelected()) {
            list.add(analyzeSampleRMS(mods));
        }
        if(checkBoxes[11].isSelected()) {
            list.add(analyzeBPMSimpleFull(byteWave));
            list.add(analyzeBPMAdvancedFullLinear(byteWave));
            list.add(analyzeBPMAllPart(byteWave));
            list.add(analyzeBPMBarycenterPart(byteWave));
        }

        runSelectedPlugins(byteWave, list);


        Node node = AnalyzerXML.getFirstSongNodeMatchingGivenName(file.getName());
        if(node == null) {		// The song wasn't analyzed before
            AnalyzerXML.addAnalyzedFileToXML(AnalyzerXML.getXMLDoc(), list, "songs", "song");
        }
        else {
            NodeList childNodes = node.getChildNodes();
            for(Pair<String, String> p : list) {
                Node currentPairNode = AnalyzerXML.findFirstNodeWithGivenAttribute(childNodes, p.getKey());
                if(currentPairNode == null) {		// Add new node
                    AnalyzerXML.addNewNode(node, p);
                }
                else {								// Change existing node
                    AnalyzerXML.getValueNodeFromInfoNode(currentPairNode).setTextContent(p.getValue());
                }
            }
        }
    }


    private static Pair<String, String> analyzeFileFormat(ByteWave byteWave) {
        return new Pair<String, String>("File format", byteWave.getFileFormatType());
    }

    private static Pair<String, String> analyzeSampleRate(ByteWave byteWave) {
        return new Pair<String, String>("Sample rate", ((Integer)byteWave.sampleRate).toString());
    }

    private static Pair<String, String> analyzeSongLength(ByteWave byteWave) {
        return new Pair<String, String>(SongLibraryPanel.HEADER_LENGTH_COLUMN_TITLE,
                Time.convertSecondsToTime(byteWave.getLengthOfAudioInSeconds(), -1));
    }

    private static Pair<String, String> analyzeSizeInBytes(ByteWave byteWave) {
        Integer len = byteWave.wholeFileSize;
        return new Pair<String, String>("File size (in bytes)", len.toString());
    }

    private static Pair<String, String> analyzeSampleMin(double[] mods) {
        String s = String.format("%.2f", mods[Aggregation.MIN.ordinal()]);
        return new Pair<String, String>("Minimum sample value", s);
    }

    private static Pair<String, String> analyzeSampleMax(double[] mods) {
        String s = String.format("%.2f", mods[Aggregation.MAX.ordinal()]);
        return new Pair<String, String>("Maximum sample value", s);
    }

    private static Pair<String, String> analyzeSampleAverage(double[] mods) {
        String s = String.format("%.2f", mods[Aggregation.AVG.ordinal()]);
        return new Pair<String, String>("Average", s);
    }

    private static Pair<String, String> analyzeSampleRMS(double[] mods) {
        String s = String.format("%.2f", mods[Aggregation.RMS.ordinal()]);
        return new Pair<String, String>("RMS", s);
    }

    private static Pair<String, String> analyzeEndianness(ByteWave byteWave) {
        if(byteWave.isBigEndian) {
            return new Pair<String, String>("Endianness", "Big endian");
        }
        else {
            return new Pair<String, String>("Endianness", "Little endian");
        }
    }

    private static Pair<String, String> analyzeEncoding(ByteWave byteWave) {
        return new Pair<String, String>("Encoding", byteWave.encoding.toString());
    }

    private static Pair<String, String> analyzeSampleSize(ByteWave byteWave) {
        return new Pair<String, String>("Sample Size (In bytes)", ((Integer)(byteWave.sampleSizeInBits / 8)).toString());
    }

    private static Pair<String, String> analyzeNumberOfChannels(int numberOfChannels) {
        return new Pair<String, String>("Number of channels", ((Integer)numberOfChannels).toString());
    }

    private static Pair<String, String> analyzeBPMSimpleFull(ByteWave byteWave) {
        return new Pair<String, String>("BPM (Simple full)", ((Integer)byteWave.computeBPMSimple()).toString());
    }


    private static Pair<String, String> analyzeBPMAdvancedFullLinear(ByteWave byteWave) {
        int subbandCount = 8;
        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);

        return new Pair<String, String>("BPM (Advanced full)", ((Integer)byteWave.computeBPMSimpleWithFreqBands(subbandCount,
                splitter, 2.5, 6, 0.16)).toString());
    }




    private static void findBestCoefsAdvancedFullLinear(ByteWave byteWave, List list) {
        int referenceBPM = -1;
        int subbandCount = 64;
        double coef = 2.3;
        while(coef < 2.9) {
            for(double varianceLimit = 0; varianceLimit < 0.19; varianceLimit += 0.02) {
                for (int windowsBetweenBeats = 4; windowsBetweenBeats < 9; windowsBetweenBeats++) {
                    for (int i = 0; i < 5; i++) {
                        switch (i) {
                            case 0:
                                subbandCount = 6;
                                break;
                            case 1:
                                subbandCount = 8;
                                break;
                            case 2:
                                subbandCount = 16;
                                break;
                            case 3:
                                subbandCount = 32;
                                break;
                            case 4:
                                subbandCount = 64;
                                break;
                        }


                        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
                        int bpm = byteWave.computeBPMSimpleWithFreqBands(subbandCount, splitter, coef, windowsBetweenBeats, varianceLimit);
                        String name = "BPMAdvancedFullLinear" + subbandCount + "Coef" + (int) Math.round(100 * coef) + "Win" + windowsBetweenBeats;
                        name += "Var" + (int) Math.round(100 * varianceLimit);

                        referenceBPM = addBPMToList(byteWave, name, list, bpm, referenceBPM);
                    }
                }
            }

            coef += 0.08;
            ProgramTest.debugPrint("Coeficient:", coef);
        }
    }


    private static void findBestCoefsAdvancedFullLogarithmic(ByteWave byteWave, List list) {
        int referenceBPM = -1;
        int subbandCount = 64;
        double coef = 2;
        while(coef < 3) {
            for(double varianceLimit = 0; varianceLimit < 1.4; varianceLimit += 0.16) {
                for (int windowsBetweenBeats = 0; windowsBetweenBeats < 5; windowsBetweenBeats++) {
                    for (int i = 0; i < 5; i++) {
                        switch (i) {
                            case 0:
                                subbandCount = 6;
                                break;
                            case 1:
                                subbandCount = 8;
                                break;
                            case 2:
                                subbandCount = 16;
                                break;
                            case 3:
                                subbandCount = 32;
                                break;
                            case 4:
                                subbandCount = 64;
                                break;
                        }


                        SubbandSplitterIFace splitter;
                        splitter = new SubbandSplitter(byteWave.sampleRate, 0, subbandCount);
                        int bpm = byteWave.computeBPMSimpleWithFreqBands(subbandCount, splitter, coef,
                                windowsBetweenBeats, varianceLimit);

                        String name = "BPMAdvancedFullLog" + subbandCount + "Coef" + (int) Math.round(100 * coef) +
                                "Win" + windowsBetweenBeats;
                        name += "Var" + (int)Math.round(100 * varianceLimit);

                        referenceBPM = addBPMToList(byteWave, name, list, bpm, referenceBPM);
                    }
                }
            }

            coef += 0.08;
            ProgramTest.debugPrint("Coeficient:", coef);
        }
    }




    private static Pair<String, String> analyzeBPMBarycenterPart(ByteWave byteWave) {
        int subbandCount = 6;
        SubbandSplitterIFace splitter = new SubbandSplitter(byteWave.sampleRate, 200, 200, subbandCount);
        int startBPM = 60;
        int jumpBPM = 10;
        int upperBoundBPM = 290;
        double numberOfSeconds;
        int numberOfBeats;
        int bpm;
        CombFilterBPMGetterIFace combFilterAlg;

        numberOfSeconds = 6.15;
        numberOfBeats = (int)Math.ceil(numberOfSeconds);
        combFilterAlg = new CombFilterBPMBarycenterGetter();      // Barycenter version
        bpm = combFilterAlg.computeBPM(startBPM, jumpBPM, upperBoundBPM,
                numberOfSeconds, subbandCount, splitter, numberOfBeats, byteWave);

        return new Pair<String, String>("BPM (Barycenter part)", ((Integer)bpm).toString());
    }

    private static Pair<String, String> analyzeBPMAllPart(ByteWave byteWave) {
        int subbandCount = 6;
        SubbandSplitterIFace splitter = new SubbandSplitter(byteWave.sampleRate, 200, 200, subbandCount);

        int startBPM = 60;
        int jumpBPM = 10;
        int upperBoundBPM = 290;
        double numberOfSeconds;
        int numberOfBeats;
        int bpm;
        CombFilterBPMGetterIFace combFilterAlg;


        numberOfSeconds = 2.2;
        numberOfBeats = (int)Math.ceil(numberOfSeconds);
        combFilterAlg = new CombFilterBPMAllSubbandsGetter();     // All sub-bands version
        bpm = combFilterAlg.computeBPM(startBPM, jumpBPM, upperBoundBPM, numberOfSeconds,
                                       subbandCount, splitter, numberOfBeats, byteWave);

        return new Pair<String, String>("BPM (All part)", ((Integer)bpm).toString());
    }


    public static void addSongBPMToList(ByteWave byteWave, List<Pair<String, Pair<String, Integer>>> list) {
        findBestCoefsAdvancedFullLinear(byteWave, list);
//        findBestCoefsAdvancedFullLogarithmic(prog, list);
    }


    // Idea is quite simple we create list of Pair<String, Pair<String, Integer>> where
    // Pair<String, Pair<String, Integer>> first string is the name of the algorithm, the value is also pair,
    // where the first value is name of the file and the second value is the difference of bpm that file for given bpm algorithm
    // and the reference bpm

    // Then we take this list, for each bpm algorithm we sum the differences (note: the differences are always positive.)
    // and put the results to new list of Pair<String, Integer>, where the first value is the name of the algorithm
    // and the second is the sum of differences.
    // Based on that we choose the result with the smallest difference, which will be the first one in the sorted array.
    public static final int BPM_DIF_MULT_FACTOR = 5;


    /**
     *
     * @param byteWave
     * @param algName
     * @param list
     * @param calculatedBPM is the bpm of the currently compared algorithm
     * @param referenceBPM is used if bpm > 0
     * @return Returns the calculated BPM reference value
     */
    public static int addBPMToList(ByteWave byteWave, String algName,
                                   List<Pair<String, Pair<String, Integer>>> list,
                                   int calculatedBPM, int referenceBPM) {
        int difference;
        int bpm;

        if(referenceBPM > 0) {
            bpm = referenceBPM;
            difference = calculateDif(bpm, calculatedBPM);
        }
        else {
            if (byteWave.getFileName().toUpperCase().contains("BPM")) {
                bpm = getBPMFromName(byteWave.getFileName());
                difference = BPM_DIF_MULT_FACTOR * calculateDif(bpm, calculatedBPM);
            } else {
                Pair<String, String> tmpPair;
                tmpPair = analyzeBPMAllPart(byteWave);
                int bpmAll = Integer.parseInt(tmpPair.getValue());

                tmpPair = analyzeBPMBarycenterPart(byteWave);
                int bpmBarycenter = Integer.parseInt(tmpPair.getValue());
                bpm = bpmAll + bpmBarycenter;
                bpm /= 2;

                difference = calculateDif(bpm, calculatedBPM);
            }
        }


        Pair<String, Integer> valuePair = new Pair<>(byteWave.getFileName(), difference);
        Pair<String, Pair<String, Integer>> retPair = new Pair<>(algName, valuePair);
        list.add(retPair);

        return bpm;
    }

    public static int calculateDif(int referenceBPM, int calculatedBPM) {
        int dif = referenceBPM - calculatedBPM;
        dif = Math.abs(dif);
        return dif;
    }

    public static int getBPMFromName(String name) {
        int bpm;
        int startIndex = -1;
        int endIndex = -1;
        for(int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if(Character.isDigit(c)) {
                if(startIndex < 0) {
                    startIndex = i;
                }
            }
            else {
                if(startIndex >= 0) {
                    endIndex = i - 1;
                    break;
                }
            }
        }

        String bpmString = name.substring(startIndex, endIndex + 1);
        bpm = Integer.parseInt(bpmString);
        return bpm;
    }

    public static List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> createDifList(List<Pair<String, Pair<String, Integer>>> list) {
        final List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> difList = new ArrayList<>();
        final List<Pair<String, Pair<String, Integer>>> currAlgPairs = new ArrayList<>();
        int count = 1;
        String firstName = list.get(0).getKey();
        for(int i = 1; i < list.size(); i++) {
            Pair<String, Pair<String, Integer>> currPair = list.get(i);
            if(firstName.equals(currPair.getKey())) {
                count++;
            }
        }
        final double[] difs = new double[count];


        for(int i = 0; i < list.size(); i++) {
            if(difListContainsName(difList, list.get(i).getKey())) {
                continue;
            }
            String name = null;
            int dif = 0;
            currAlgPairs.clear();
            for(int j = i; j < list.size(); j++) {
                // TODO: DEBUG
//                if(name != null) {
//                    ProgramTest.debugPrint("ALG:", name, dif);
//                }
                // TODO: DEBUG
                Pair<String, Pair<String, Integer>> currPair = list.get(j);
                if(name == null) {
                    if (!difListContainsName(difList, currPair.getKey())) {
                        name = currPair.getKey();
                        dif = currPair.getValue().getValue();
                        currAlgPairs.add(currPair);
                    }
                }
                else {
                    if(name.equals(currPair.getKey())) {
                        dif += currPair.getValue().getValue();
                        currAlgPairs.add(currPair);
                    }
                }
            }



            double avg = dif / (double)currAlgPairs.size();
            double variance = calculateVariance(avg, currAlgPairs);
            for(int k = 0; k < currAlgPairs.size(); k++) {
                Pair<String, Pair<String, Integer>> p = currAlgPairs.get(k);
                difs[k] = Math.abs(p.getValue().getValue() - avg);
            }
            Arrays.sort(difs);


            Pair<String, Integer> resultKey = new Pair<>(name, dif);
            Pair<Pair<String, Integer>, double[]> pair = new Pair(resultKey, difs);
            difList.add(new Pair(pair, variance));
        }

        return difList;
    }

    private static double calculateVariance(double avg, List<Pair<String, Pair<String, Integer>>> vals) {
        double variance = 0;
        for(Pair<String, Pair<String, Integer>> p : vals) {
            int val = p.getValue().getValue();
            double tmp = val - avg;
            variance += tmp * tmp;
        }

        return variance / vals.size();
    }

    private static boolean difListContainsName(List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> difList, String name) {
        for(int i = 0; i < difList.size(); i++) {
            Pair<String, Integer> p = difList.get(i).getKey().getKey();
            if(p.getKey().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static void sortDifList(List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> difList) {
        difList.sort(new Comparator<Pair<Pair<Pair<String, Integer>, double[]>, Double>>() {
            @Override
            public int compare(Pair<Pair<Pair<String, Integer>, double[]>, Double> o1,
                               Pair<Pair<Pair<String, Integer>, double[]>, Double> o2) {
                int val1 = o1.getKey().getKey().getValue();
                int val2 = o2.getKey().getKey().getValue();
                return Integer.compare(val1, val2);
            }
        });
    }

    public static void printDifList(List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> difList, int difPrintCount) {
        for(int i = 0; i < difList.size(); i++) {
            MyLogger.log(difList.get(i).getKey().toString() +
                    "\t" + difList.get(i).getValue().toString(), 0);
            double[] arr = difList.get(i).getKey().getValue();
            for(int j = 0; j < difPrintCount; j++) {
                MyLogger.log(String.format("%.2f", arr[arr.length - j - 1]), 0);
            }
            MyLogger.log("----", 0);
        }
    }
}
