package analyzer;

import analyzer.observer.DataModelObserverIFace;
import analyzer.observer.DataModelSubject;
import analyzer.observer.DataModelSubjectIFace;
import util.Pair;
import analyzer.bpm.*;
import analyzer.util.UneditableTableModel;
import analyzer.plugin.ifaces.AnalyzerBytePluginIFace;
import analyzer.plugin.ifaces.AnalyzerDoublePluginIFace;
import analyzer.plugin.ifaces.AnalyzerIntPluginIFace;
import player.util.ErrorFrame;
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

import Rocnikovy_Projekt.*;
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

    private Program p;


    @Override
    public void leavingPanel() {
        int rowCount = table.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            dataModel.removeRow(0);
        }
    }


    public AnalyzerPanel(JFrame thisWindow, DataModelObserverIFace[] observers) {
        p = new Program();

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

    private void runSelectedPlugins(Program prog, List<Pair<String, String>> list) {
        runSelectedPluginsByte(prog, list);
        runSelectedPluginsInt(prog, list);
        runSelectedPluginsDouble(prog, list);
    }

    private void runSelectedPluginsByte(Program prog, List<Pair<String, String>> list) {
        for(Pair<JCheckBox, AnalyzerBytePluginIFace> p : bytePluginPairs) {
            if(p.getKey().isSelected()) {
                list.add(analyzeBytePlugin(prog, p.getValue()));
            }
        }
    }

    private Pair<String, String> analyzeBytePlugin(Program prog, AnalyzerBytePluginIFace plugin) {
        return plugin.analyze(prog.song, prog.numberOfChannels, prog.sampleSizeInBytes,
            prog.sampleRate, prog.isBigEndian, prog.isSigned);
    }

    private void runSelectedPluginsInt(Program prog, List<Pair<String, String>> list) {
        int[] wave = null;
        for(Pair<JCheckBox, AnalyzerIntPluginIFace> p : intPluginPairs) {
            if(p.getKey().isSelected()) {
                if(wave != null) {
                    try {
                        wave = Program.convertBytesToSamples(prog.song, prog.sampleSizeInBytes, prog.isBigEndian, prog.isSigned);
                    } catch (IOException e) {
                        return;
                    }
                }
                list.add(p.getValue().analyze(wave, prog.numberOfChannels, prog.sampleRate));
            }
        }
    }

    private void runSelectedPluginsDouble(Program prog, List<Pair<String, String>> list) {
        DoubleWave wave = null;
        for(Pair<JCheckBox, AnalyzerDoublePluginIFace> p : doublePluginPairs) {
            if(p.getKey().isSelected()) {
                if(wave != null) {
                    wave = new DoubleWave(prog, false);
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
            //This is where a real application would open the file.
        }
    }

    // TODO: neni vhodny predavat boolean, idealni je volat tutez metodu co ale na konci nema createXML

    private void addFilesToModel(File[] files) {
        for(int i = 0; i < files.length; i++) {
            if(files[i].isDirectory()) {
                addFilesToModel(files[i].listFiles());
            }
            else {
                // TODO: Mam se divat jestli maji spravnou extension uz ted nebo az potom ... navic ani vlastne
                // extension mit nemusi ale ono se to pozna podle magic - Nutny nejak vyresit !!!!!!!!!!!!!!!!!
                try {
                    dataModel.addRow(new String[]{ files[i].getCanonicalPath() });
                } catch (IOException e) {			// Shouldn't happen
                    MyLogger.logException(e);
                    new ErrorFrame(frame, "Unknown error");		// TODO: Asi ok reseni
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


    public static void findCoefs(String filename,
                                 List<Pair<String, Pair<String, Integer>>> bpmList) {
        ProgramTest.debugPrint("Currently working with:", filename);
        Program p = new Program();

        try {
            if(!p.setVariables(filename, true)) {        // TODO: Zasadni ... nastavit ty hodnoty
                MyLogger.logWithoutIndentation("Error in method analyze(String filename) in AnalyzerPanel\n" +
                        filename + "\n" + Program.LOG_MESSAGE_WHEN_SET_VARIABLES_RETURN_FALSE);
                return;
            }
        } catch (IOException e) {
            MyLogger.logException(e);
            return;         // TODO: podle me je lepsi proste ten soubor preskocit ... do budoucna by bylo lepsi psat i proc jsem je preskocil ... hlavne u tech setVariables !!!!!!!!!!!!!!!!!!!!!!
            //new ErrorFrame(frame, "Couldn't set variables for song:\n" + e.getMessage());
        }

        // TODO: !!!!!Jen pro ted - chci zpracovavat kazdej kanal zvlast a pro kazdej mit vlastni informace - a ne to delat na mono
        try {
            p.convertToMono();
            //p.convertSampleRate(22050);
        }
        catch(IOException e) {
            return;
        }
        // TODO: !!!!!!

        addSongBPMToList(p, bpmList);
    }


    public void analyze(String filename) {
        File file = new File(filename);
        List<Pair<String, String>> list = new ArrayList<>();
        Pair<String, String> pair = new Pair<>(SongLibraryPanel.HEADER_NAME_COLUMN_TITLE, file.getName());
        list.add(pair);
        pair = new Pair<>("Path", file.getAbsolutePath());
        list.add(pair);

        try {
            if(!p.setVariables(filename, true)) {        // TODO: Zasadni ... nastavit ty hodnoty
                MyLogger.logWithoutIndentation("Error in method analyze(String filename) in AnalyzerPanel\n" +
                        filename + "\n" + Program.LOG_MESSAGE_WHEN_SET_VARIABLES_RETURN_FALSE);
                return;
            }
        } catch (IOException e) {
            MyLogger.logException(e);
            return;         // TODO: podle me je lepsi proste ten soubor preskocit ... do budoucna by bylo lepsi psat i proc jsem je preskocil ... hlavne u tech setVariables !!!!!!!!!!!!!!!!!!!!!!
            //new ErrorFrame(frame, "Couldn't set variables for song:\n" + e.getMessage());
        }

        int numberOfChannels = p.numberOfChannels;

        // TODO: !!!!!Jen pro ted - chci zpracovavat kazdej kanal zvlast a pro kazdej mit vlastni informace - a ne to delat na mono
        try {
            p.convertToMono();
            //p.convertSampleRate(22050);
        }
        catch(IOException e) {
            return;
        }
        // TODO: !!!!!!





        if(checkBoxes[0].isSelected()) {
            list.add(analyzeSizeInBytes(p));
        }
        if(checkBoxes[1].isSelected()) {
            list.add(analyzeSongLength(p));
        }
        if(checkBoxes[2].isSelected()) {
            list.add(analyzeFileFormat(p));
        }
        if(checkBoxes[3].isSelected()) {
            list.add(analyzeEncoding(p));
        }
        if(checkBoxes[4].isSelected()) {
            list.add(analyzeSampleSize(p));
        }
        if(checkBoxes[5].isSelected()) {
            list.add(analyzeSampleRate(p));
        }
        if(checkBoxes[6].isSelected()) {
            list.add(analyzeNumberOfChannels(numberOfChannels));
        }
        if(checkBoxes[7].isSelected()) {
            list.add(analyzeEndianness(p));
        }

        double[] mods = null;
        if(checkBoxes[8].isSelected() || checkBoxes[9].isSelected() || checkBoxes[10].isSelected()) {		// TODO: nemel bych vybirat takhle natvrdo ty indexy
            try {
                mods = Program.calculateAllAggregations(p.song, p.sampleSizeInBytes, p.isBigEndian, p.isSigned);
            } catch (IOException e) {
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
            list.add(analyzeBPMSimpleFull(p));
            list.add(analyzeBPMAdvancedFullLinear(p));
//            list.add(analyzeBPMAdvancedFullLinear2(p));
//            list.add(analyzeBPMAdvancedFullLinear3(p));
//            list.add(analyzeBPMAdvancedFullLinear4(p));
//            list.add(analyzeBPMAdvancedFullLinear5(p));


            // TODO: BPM - HLEDANI
//            for(int i = 5; i < 10; i++) {
//                SubbandSplitterIFace splitter = new SubbandSplitterLinear(16);
//                list.add(new Pair<String, String>("BPMAdvancedFullLinearWin" + i, ((Integer)p.calculateBPMSimpleWithFreqBands(16, splitter, 2.72, i, 0.16)).toString()));
//            }


//            int sc = 8;
//            SubbandSplitterIFace splitter = new SubbandSplitterLinear(sc);
//            list.add(new Pair<String, String>("BPMAdvancedFullLinear" + 1, ((Integer)p.calculateBPMSimpleWithFreqBands(sc, splitter, 2.5, 6, 0.16)).toString()));
//            sc = 8;
//            splitter = new SubbandSplitterLinear(sc);
//            list.add(new Pair<String, String>("BPMAdvancedFullLinear" + 2, ((Integer)p.calculateBPMSimpleWithFreqBands(sc, splitter, 2.5, 6, 0.32)).toString()));
//
//            sc = 16;
//            splitter = new SubbandSplitterLinear(sc);
//            list.add(new Pair<String, String>("BPMAdvancedFullLinear" + 3, ((Integer)p.calculateBPMSimpleWithFreqBands(sc, splitter, 2.7, 6, 0.04)).toString()));


            // TODO: Tohle je dulezity
//            list.add(analyzeBPMAdvancedFullLinear(p));



//            list.add(analyzeBPMAdvancedFullLog(p));


//            list.add(analyzeBPMAdvancedFullOldConstant(p));
//            list.add(analyzeBPMAdvancedFullOld(p));
//            list.add(analyzeBPMAdvancedFullOld32(p));


//            analyzeBPMAdvancedFullLogarithmicTEST(p, list);


//            analyzeBPMAdvancedFullLinearTEST(p, list);
//            list.add(analyzeBPMAdvancedFullLinear6(p));
//            list.add(analyzeBPMAdvancedFullLinear8(p));
//            list.add(analyzeBPMAdvancedFullLinear16(p));
//            list.add(analyzeBPMAdvancedFullLinear32(p));
//            list.add(analyzeBPMAdvancedFullLinear64(p));
            // TODO: BPM - HLEDANI




            list.add(analyzeBPMAllPart(p));
            // TODO: BPM - HLEDANI
//            list.add(analyzeBPMAllPartConstant(p));
//            list.add(analyzeBPMAllPartLinear(p));
//            list.add(analyzeBPMAllPart(p));
            // TODO: BPM - HLEDANI
//
            list.add(analyzeBPMBarycenterPart(p));
            // TODO: BPM - HLEDANI
//            list.add(analyzeBPMBarycenterPartConstant(p));
//            list.add(analyzeBPMBarycenterPartLinear(p));
//            list.add(analyzeBPMBarycenterPart(p));
            // TODO: BPM - HLEDANI
        }

        runSelectedPlugins(p, list);


//		char[] c = new char[] {'a'};		// TODO:
//		for(int i = 0; i < 5; i++) {
//			pair = new Pair<>(new String(c), new String(c));		// TODO: tady by se mela provest ta analyza featur
//			list.add(pair);
//			c[0]++;
//		}



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


    private static Pair<String, String> analyzeFileFormat(Program prog) {
        return new Pair<String, String>("File format", prog.getFileFormatType());
    }

    private static Pair<String, String> analyzeSampleRate(Program prog) {
        return new Pair<String, String>("Sample rate", ((Integer)prog.sampleRate).toString());
    }

    private static Pair<String, String> analyzeSongLength(Program prog) {
        return new Pair<String, String>(SongLibraryPanel.HEADER_LENGTH_COLUMN_TITLE,
                Program.convertSecondsToTime(prog.lengthOfAudioInSeconds, -1));
    }

    private static Pair<String, String> analyzeSizeInBytes(Program prog) {
        Integer len = prog.wholeFileSize;
        return new Pair<String, String>("File size (in bytes)", len.toString());
    }

    private static Pair<String, String> analyzeSampleMin(double[] mods) {
        String s = String.format("%.2f", mods[Aggregations.MIN.ordinal()]);
        return new Pair<String, String>("Minimum sample value", s);
    }

    private static Pair<String, String> analyzeSampleMax(double[] mods) {
        String s = String.format("%.2f", mods[Aggregations.MAX.ordinal()]);
        return new Pair<String, String>("Maximum sample value", s);
    }

    private static Pair<String, String> analyzeSampleAverage(double[] mods) {
        String s = String.format("%.2f", mods[Aggregations.AVG.ordinal()]);
        return new Pair<String, String>("Average", s);
    }

    private static Pair<String, String> analyzeSampleRMS(double[] mods) {
        String s = String.format("%.2f", mods[Aggregations.RMS.ordinal()]);
        return new Pair<String, String>("RMS", s);
    }

    private static Pair<String, String> analyzeEndianness(Program prog) {
        if(prog.isBigEndian) {
            return new Pair<String, String>("Endianness", "Big endian");
        }
        else {
            return new Pair<String, String>("Endianness", "Little endian");
        }
    }

    private static Pair<String, String> analyzeEncoding(Program prog) {
        return new Pair<String, String>("Encoding", prog.encoding.toString());// TODO: To chce asi pres swithch spis
    }

    private static Pair<String, String> analyzeSampleSize(Program prog) {
        return new Pair<String, String>("Sample Size (In bytes)", ((Integer)(prog.sampleSizeInBits / 8)).toString());
    }

    private static Pair<String, String> analyzeNumberOfChannels(int numberOfChannels) {
        return new Pair<String, String>("Number of channels", ((Integer)numberOfChannels).toString());
    }

    private static Pair<String, String> analyzeBPMSimpleFull(Program prog) {
        return new Pair<String, String>("BPM (Simple full)", ((Integer)prog.calculateBPMSimple()).toString());
    }



    // TODO: BPM - HLEDANI
//    private static Pair<String, String> analyzeBPMAdvancedFullLog(Program prog) {
//        int subbandCount = 8;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        return new Pair<String, String>("BPMAdvancedFullOldLog", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceOld(splitter)).toString());
//    }
//
//    private static Pair<String, String> analyzeBPMAdvancedFullOldConstant(Program prog) {
//        int subbandCount = 6;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitterConstant(subbandCount);
//        return new Pair<String, String>("BPMAdvancedFullOldConstant", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceOld(splitter)).toString());
//    }
//
//    private static Pair<String, String> analyzeBPMAdvancedFullOld(Program prog) {
//        int subbandCount = 6;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//
//        // TODO: DEBUG
////        ProgramTest.debugPrint("NEW FULL SPLITTER:");
//        // TODO: DEBUG
//        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, 200, subbandCount);
//        // TODO: DEBUG
//////        Proste napisu metodu co mi hodi Hz tak aby to napasovalo na ten subbandCount - bud to hodi tech 200 nebo maximalne tak aby to vyslo na ten poskytnutej subbandCount
//////        Tohle pod timhle komentem je to co se pouziva
////        new Pair<String, String>("BPMAdvancedFullOld", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceOld(splitter.getSubbandCount(), splitter)).toString());
////        ProgramTest.debugPrint("\nOLD FULL SPLITTER:");
////        splitter = new SubbandSplitterOld(prog.sampleRate, 200, subbandCount);
////        new Pair<String, String>("BPMAdvancedFullOld", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceOld(splitter.getSubbandCount(), splitter)).toString());
//        // TODO: DEBUG
////        SubbandSplitterIFace splitter = new SubbandSplitterOld(prog.sampleRate, 200, subbandCount);
////        return new Pair<String, String>("BPMAdvancedFullOld", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceOld(splitter.getSubbandCount(), splitter)).toString());
//
//        // TODO: DEBUG
////        System.exit(4548744);
//        if(subbandCount != splitter.getSubbandCount()) {
//            int todo = 4;
//            ProgramTest.debugPrint(subbandCount, splitter.getSubbandCount());
//            System.exit(487);
//        }
//        // TODO: ted jsem odstranil tu vec s 0 .tym binem a presunul to do tamty metody primo kde se to pocita - ale ono to bez toho nefunguje
//        // TODO: DEBUG
//        return new Pair<String, String>("BPMAdvancedFullOld", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceOld(splitter)).toString());
//    }
//
//    private static Pair<String, String> analyzeBPMAdvancedFullOld32(Program prog) {
//        int subbandCount = 32;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//
//        // TODO: DEBUG
////        ProgramTest.debugPrint("NEW FULL SPLITTER:");
//        // TODO: DEBUG
//        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: DEBUG
//////        Proste napisu metodu co mi hodi Hz tak aby to napasovalo na ten subbandCount - bud to hodi tech 200 nebo maximalne tak aby to vyslo na ten poskytnutej subbandCount
//////        Tohle pod timhle komentem je to co se pouziva
////        new Pair<String, String>("BPMAdvancedFullOld", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceOld(splitter.getSubbandCount(), splitter)).toString());
////        ProgramTest.debugPrint("\nOLD FULL SPLITTER:");
////        splitter = new SubbandSplitterOld(prog.sampleRate, 200, subbandCount);
////        new Pair<String, String>("BPMAdvancedFullOld", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceOld(splitter.getSubbandCount(), splitter)).toString());
//        // TODO: DEBUG
////        SubbandSplitterIFace splitter = new SubbandSplitterOld(prog.sampleRate, 200, subbandCount);
////        return new Pair<String, String>("BPMAdvancedFullOld", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceOld(splitter.getSubbandCount(), splitter)).toString());
//
//        // TODO: DEBUG
////        System.exit(4548744);
//        if(subbandCount != splitter.getSubbandCount()) {
//            int todo = 4;
//            ProgramTest.debugPrint(subbandCount, splitter.getSubbandCount());
//            System.exit(487);
//        }
//        // TODO: ted jsem odstranil tu vec s 0 .tym binem a presunul to do tamty metody primo kde se to pocita - ale ono to bez toho nefunguje
//        // TODO: DEBUG
//        return new Pair<String, String>("BPMAdvancedFullOld32", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceOld(splitter)).toString());
//    }
    // TODO: BPM - HLEDANI



    private static Pair<String, String> analyzeBPMAdvancedFullLinear(Program prog) {
        int subbandCount = 8;
        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        return new Pair<String, String>("BPMAdvancedFull3", ((Integer)prog.calculateBPMSimpleWithFreqBands(subbandCount,
//                splitter, 2.5, 6, 0.16)).toString());
//        return new Pair<String, String>("BPMAdvancedFull4", ((Integer)prog.calculateBPMSimpleWithFreqBands(subbandCount,
//                splitter, 2.5, 4, 0.0)).toString());
//        return new Pair<String, String>("BPMAdvancedFull5", ((Integer)prog.calculateBPMSimpleWithFreqBands(subbandCount,
//                splitter, 2.2, 6, 0.0)).toString());


        return new Pair<String, String>("BPM (Advanced full)", ((Integer)prog.calculateBPMSimpleWithFreqBands(subbandCount,
                splitter, 2.5, 6, 0.16)).toString());
//        return new Pair<String, String>("BPMAdvancedFull", ((Integer)prog.calculateBPMSimpleWithFreqBands(subbandCount,
//                splitter, 2.5, 6, 1.12)).toString());


//        int subbandCount = 16;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        return new Pair<String, String>("BPMAdvancedFull4", ((Integer)prog.calculateBPMSimpleWithFreqBands(subbandCount,
//                splitter, 2.74, 6, 0.0)).toString());


//        splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        return new Pair<String, String>("BPMAdvancedFullLog", ((Integer)prog.calculateBPMSimpleWithFreqBands(subbandCount,
//                splitter, 2.72, 4, 0.0)).toString());
    }


    // TODO: BPM - HLEDANI (bylo to kdyz jsem hledal ty parametry)
//    private static Pair<String, String> analyzeBPMAdvancedFullLinear2(Program prog) {
//        int subbandCount = 8;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        return new Pair<String, String>("BPMAdvancedFullBigVariance", ((Integer)prog.calculateBPMSimpleWithFreqBands(subbandCount,
//                splitter, 2.5, 6, 1.12)).toString());
//    }
//
//    private static Pair<String, String> analyzeBPMAdvancedFullLinear3(Program prog) {
//        int subbandCount = 8;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        return new Pair<String, String>("BPMAdvancedFullBigVariance2", ((Integer)prog.calculateBPMSimpleWithFreqBands(subbandCount,
//                splitter, 2.5, 6, 5.12)).toString());
//    }
//
//    private static Pair<String, String> analyzeBPMAdvancedFullLinear4(Program prog) {
//        int subbandCount = 8;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        return new Pair<String, String>("BPMAdvancedFullBigVariance3", ((Integer)prog.calculateBPMSimpleWithFreqBands(subbandCount,
//                splitter, 2.5, 6, 10.12)).toString());
//    }
//
//    private static Pair<String, String> analyzeBPMAdvancedFullLinear5(Program prog) {
//        int subbandCount = 8;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        return new Pair<String, String>("BPMAdvancedFullBigVariance4", ((Integer)prog.calculateBPMSimpleWithFreqBands(subbandCount,
//                splitter, 2.5, 6, 30.12)).toString());
//    }
//
//
//    private static Pair<String, String> analyzeBPMAdvancedFullLinear(Program prog) {
//        int subbandCount = 16;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        return new Pair<String, String>("BPMAdvancedFullLinear2", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceNew(subbandCount, splitter)).toString());
//    }
//
//
//    private static Pair<String, String> analyzeBPMAdvancedFullLinear6(Program prog) {
//        int subbandCount = 6;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        return new Pair<String, String>("BPMAdvancedFullLinear6", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceNew(subbandCount, splitter)).toString());
//    }
//
//    private static Pair<String, String> analyzeBPMAdvancedFullLinear8(Program prog) {
//        int subbandCount = 8;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        return new Pair<String, String>("BPMAdvancedFullLinear8", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceNew(subbandCount, splitter)).toString());
//    }
//
//    private static Pair<String, String> analyzeBPMAdvancedFullLinear16(Program prog) {
//        int subbandCount = 16;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        return new Pair<String, String>("BPMAdvancedFullLinear16", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceNew(subbandCount, splitter)).toString());
//    }
//
//    private static Pair<String, String> analyzeBPMAdvancedFullLinear32(Program prog) {
//        int subbandCount = 32;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        return new Pair<String, String>("BPMAdvancedFullLinear32", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceNew(subbandCount, splitter)).toString());
//    }
//
//    private static Pair<String, String> analyzeBPMAdvancedFullLinear64(Program prog) {
//        int subbandCount = 64;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        return new Pair<String, String>("BPMAdvancedFullLinear64", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVarianceNew(subbandCount, splitter)).toString());
//    }
//
//
//    private static void analyzeBPMAdvancedFullLinearTEST(Program prog, List list) {
//        int subbandCount = 64;
//        double coef = 3;
//        while(coef < 3.07) {
//            for(double varianceLimit = 0; varianceLimit < 1.4; varianceLimit += 0.2) {
//                for (int windowsBetweenBeats = 0; windowsBetweenBeats < 6; windowsBetweenBeats++) {
//                    for (int i = 0; i < 5; i++) {
//                        switch (i) {
//                            case 0:
//                                subbandCount = 6;
//                                break;
//                            case 1:
//                                subbandCount = 8;
//                                break;
//                            case 2:
//                                subbandCount = 16;
//                                break;
//                            case 3:
//                                subbandCount = 32;
//                                break;
//                            case 4:
//                                subbandCount = 64;
//                                break;
//                        }
//
//
//                        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//                        int bpm = prog.calculateBPMSimpleWithFreqBands(subbandCount, splitter, coef, windowsBetweenBeats, varianceLimit);
//                        String name = "BPMAdvancedFullLinear" + subbandCount + "Coef" + (int) Math.round(2 * coef) + "Win" + windowsBetweenBeats;
//                        name += "Var" + (int) Math.round(1 * varianceLimit);
//                        list.add(new Pair<String, String>(name, ((Integer) bpm).toString()));
//                    }
//                }
//            }
//
//            coef += 0.08;
//        }
//    }
//
//
////tahle verze je vydelan o 32 - 18 (za koeficienty) + 14 (za varianci)
//    private static void analyzeBPMAdvancedFullLogarithmicTEST(Program prog, List list) {
//        int subbandCount = 64;
//        double coef = 2;
//        while(coef < 3) {
//            for(double varianceLimit = 0; varianceLimit < 1.4; varianceLimit += 0.16) {
//                for (int windowsBetweenBeats = 0; windowsBetweenBeats < 5; windowsBetweenBeats++) {
////                    if(windowsBetweenBeats != 0 && windowsBetweenBeats != 4) continue;
//                    for (int i = 0; i < 5; i++) {
//                        switch (i) {
//                            case 0:
//                                subbandCount = 6;
//                                break;
//                            case 1:
//                                subbandCount = 8;
//                                break;
//                            case 2:
//                                subbandCount = 16;
//                                break;
//                            case 3:
//                                subbandCount = 32;
//                                break;
//                            case 4:
//                                subbandCount = 64;
//                                break;
//                        }
//
//
//                        SubbandSplitterIFace splitter;
//
////                        if (subbandCount == 6) {
////                            if (prog.sampleRate < 30000) {
////                                splitter = new SubbandSplitter(prog.sampleRate, 100, 100, subbandCount);
////                            } else {
////                                splitter = new SubbandSplitter(prog.sampleRate, 200, 200, subbandCount);
////                            }
////                        } else {
////                            splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
////                        }
//
//                        splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//                        int bpm = prog.calculateBPMSimpleWithFreqBands(subbandCount, splitter, coef,
//                                windowsBetweenBeats, varianceLimit);
//
//                        String name = "BPMAdvancedFullLog" + subbandCount + "Coef" + (int) Math.round(100 * coef) +
//                                "Win" + windowsBetweenBeats;
//                        name += "Var" + (int)Math.round(100 * varianceLimit);
//ProgramTest.debugPrint("alg:", name);
//                        list.add(new Pair<String, String>(name, ((Integer) bpm).toString()));
//                    }
//                }
//            }
//
//            coef += 0.08;
//        }
//    }
//
//
////    private static void analyzeBPMAdvancedFullLogarithmicTEST(Program prog, List list) {
////        int subbandCount = 64;
////        double coef = 2;
////        while(coef < 3) {
////            for(double varianceLimit = 0; varianceLimit < 1.4; varianceLimit += 0.05) {
////                for (int windowsBetweenBeats = 0; windowsBetweenBeats < 5; windowsBetweenBeats++) {
//////                    if(windowsBetweenBeats != 0 && windowsBetweenBeats != 4) continue;
////                    for (int i = 0; i < 5; i++) {
////                        switch (i) {
////                            case 0:
////                                subbandCount = 6;
////                                break;
////                            case 1:
////                                subbandCount = 8;
////                                break;
////                            case 2:
////                                subbandCount = 16;
////                                break;
////                            case 3:
////                                subbandCount = 32;
////                                break;
////                            case 4:
////                                subbandCount = 64;
////                                break;
////                        }
////
////
////                        SubbandSplitterIFace splitter;
////
//////                        if (subbandCount == 6) {
//////                            if (prog.sampleRate < 30000) {
//////                                splitter = new SubbandSplitter(prog.sampleRate, 100, 100, subbandCount);
//////                            } else {
//////                                splitter = new SubbandSplitter(prog.sampleRate, 200, 200, subbandCount);
//////                            }
//////                        } else {
//////                            splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//////                        }
////
////                        splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
////                        int bpm = prog.calculateBPMSimpleWithFreqBands(subbandCount, splitter, coef,
////                                windowsBetweenBeats, varianceLimit);
////
////                        String name = "BPMAdvancedFullLog" + subbandCount + "Coef" + (int) Math.round(100 * coef) +
////                                "Win" + windowsBetweenBeats;
////                        name += "Var" + (int)Math.round(100 * varianceLimit);
////
////                        list.add(new Pair<String, String>(name, ((Integer) bpm).toString()));
////                    }
////                }
////            }
////
////            coef += 0.05;
////        }
////    }
    // TODO: BPM - HLEDANI



    private static void findBestCoefsAdvancedFullLinear(Program prog, List list) {
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
                        int bpm = prog.calculateBPMSimpleWithFreqBands(subbandCount, splitter, coef, windowsBetweenBeats, varianceLimit);
                        String name = "BPMAdvancedFullLinear" + subbandCount + "Coef" + (int) Math.round(100 * coef) + "Win" + windowsBetweenBeats;
                        name += "Var" + (int) Math.round(100 * varianceLimit);

//                        ProgramTest.debugPrint("Current alg:", name);

                        referenceBPM = addBPMToList(prog, name, list, bpm, referenceBPM);
                    }
                }
            }

            coef += 0.08;
            ProgramTest.debugPrint("Coeficient:", coef);
        }
    }


    private static void findBestCoefsAdvancedFullLogarithmic(Program prog, List list) {
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

//                        if (subbandCount == 6) {
//                            if (prog.sampleRate < 30000) {
//                                splitter = new SubbandSplitter(prog.sampleRate, 100, 100, subbandCount);
//                            } else {
//                                splitter = new SubbandSplitter(prog.sampleRate, 200, 200, subbandCount);
//                            }
//                        } else {
//                            splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//                        }

                        splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
                        int bpm = prog.calculateBPMSimpleWithFreqBands(subbandCount, splitter, coef,
                                windowsBetweenBeats, varianceLimit);

                        String name = "BPMAdvancedFullLog" + subbandCount + "Coef" + (int) Math.round(100 * coef) +
                                "Win" + windowsBetweenBeats;
                        name += "Var" + (int)Math.round(100 * varianceLimit);

//                        ProgramTest.debugPrint("Current alg:", name);

                        referenceBPM = addBPMToList(prog, name, list, bpm, referenceBPM);
                    }
                }
            }

            coef += 0.08;
            ProgramTest.debugPrint("Coeficient:", coef);
        }
    }





    // TODO: BPM - HLEDANI
//    private static Pair<String, String> analyzeBPMBarycenterPart(Program prog) {
//        int subbandCount = 16;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, subbandCount);
//        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, 200, subbandCount);
//        int startBPM = 60;
//        int jumpBPM = 10;
//        int upperBoundBPM = 290;
//        double numberOfSeconds;
//        int numberOfBeats;
//        int bpm;
//        CombFilterBPMGetterIFace combFilterAlg;
//
//
//        numberOfSeconds = 6.15;       // Maybe 6.2 but this feels ok
//        numberOfBeats = (int)Math.ceil(numberOfSeconds);
//        combFilterAlg = new CombFilterBPMBarycenterGetter();      // Barycenter version
//        bpm = combFilterAlg.calculateBPM(startBPM, jumpBPM, upperBoundBPM,
//            numberOfSeconds, subbandCount, splitter, numberOfBeats, prog);
//
//        return new Pair<String, String>("BPMBarycenterPart16Subbands", ((Integer)bpm).toString());
//    }
//
//    private static Pair<String, String> analyzeBPMAllPart(Program prog) {
//        int subbandCount = 16;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, 200, subbandCount);
//        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//
//        int startBPM = 60;
//        int jumpBPM = 10;
//        int upperBoundBPM = 290;
//        double numberOfSeconds;
//        int numberOfBeats;
//        int bpm;
//        CombFilterBPMGetterIFace combFilterAlg;
//
//
//        numberOfSeconds = 2.2;
//        numberOfBeats = (int)Math.ceil(numberOfSeconds);
//        combFilterAlg = new CombFilterBPMAllSubbandsGetter();     // All subbands version
//        bpm = combFilterAlg.calculateBPM(startBPM, jumpBPM, upperBoundBPM,
//            numberOfSeconds, subbandCount, splitter, numberOfBeats, prog);
//
//        return new Pair<String, String>("BPMAllPart16Subbands", ((Integer)bpm).toString());
//    }
//
//
//    private static Pair<String, String> analyzeBPMBarycenterPartLinear(Program prog) {
//        int subbandCount = 16;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, subbandCount);
//        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, 200, subbandCount);
//        int startBPM = 60;
//        int jumpBPM = 10;
//        int upperBoundBPM = 290;
//        double numberOfSeconds;
//        int numberOfBeats;
//        int bpm;
//        CombFilterBPMGetterIFace combFilterAlg;
//
//
//        numberOfSeconds = 6.15;       // Maybe 6.2 but this feels ok
//        numberOfBeats = (int)Math.ceil(numberOfSeconds);
//        combFilterAlg = new CombFilterBPMBarycenterGetter();      // Barycenter version
//        bpm = combFilterAlg.calculateBPM(startBPM, jumpBPM, upperBoundBPM,
//                numberOfSeconds, subbandCount, splitter, numberOfBeats, prog);
//
//        return new Pair<String, String>("BPMBarycenterPart16SubbandsLinear", ((Integer)bpm).toString());
//    }
//
//    private static Pair<String, String> analyzeBPMAllPartLinear(Program prog) {
//        int subbandCount = 16;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, 200, subbandCount);
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
//        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//
//        int startBPM = 60;
//        int jumpBPM = 10;
//        int upperBoundBPM = 290;
//        double numberOfSeconds;
//        int numberOfBeats;
//        int bpm;
//        CombFilterBPMGetterIFace combFilterAlg;
//
//
//        numberOfSeconds = 2.2;
//        numberOfBeats = (int)Math.ceil(numberOfSeconds);
//        combFilterAlg = new CombFilterBPMAllSubbandsGetter();     // All subbands version
//        bpm = combFilterAlg.calculateBPM(startBPM, jumpBPM, upperBoundBPM,
//                numberOfSeconds, subbandCount, splitter, numberOfBeats, prog);
//
//        return new Pair<String, String>("BPMAllPart16SubbandsLinear", ((Integer)bpm).toString());
//    }
//
//
//    private static Pair<String, String> analyzeBPMBarycenterPartConstant(Program prog) {
//        int subbandCount = 16;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, subbandCount);
////        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        SubbandSplitterIFace splitter = new SubbandSplitterConstant(subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, 200, subbandCount);
//        int startBPM = 60;
//        int jumpBPM = 10;
//        int upperBoundBPM = 290;
//        double numberOfSeconds;
//        int numberOfBeats;
//        int bpm;
//        CombFilterBPMGetterIFace combFilterAlg;
//
//
//        numberOfSeconds = 6.15;       // Maybe 6.2 but this feels ok
//        numberOfBeats = (int)Math.ceil(numberOfSeconds);
//        combFilterAlg = new CombFilterBPMBarycenterGetter();      // Barycenter version
//        bpm = combFilterAlg.calculateBPM(startBPM, jumpBPM, upperBoundBPM,
//                numberOfSeconds, subbandCount, splitter, numberOfBeats, prog);
//
//        return new Pair<String, String>("BPMBarycenterPart16SubbandsConstant", ((Integer)bpm).toString());
//    }
//
//    private static Pair<String, String> analyzeBPMAllPartConstant(Program prog) {
//        int subbandCount = 16;
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, subbandCount);
//        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, 200, subbandCount);
////        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 0, subbandCount);
////        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
//        SubbandSplitterIFace splitter = new SubbandSplitterConstant(subbandCount);
//
//        int startBPM = 60;
//        int jumpBPM = 10;
//        int upperBoundBPM = 290;
//        double numberOfSeconds;
//        int numberOfBeats;
//        int bpm;
//        CombFilterBPMGetterIFace combFilterAlg;
//
//
//        numberOfSeconds = 2.2;
//        numberOfBeats = (int)Math.ceil(numberOfSeconds);
//        combFilterAlg = new CombFilterBPMAllSubbandsGetter();     // All subbands version
//        bpm = combFilterAlg.calculateBPM(startBPM, jumpBPM, upperBoundBPM,
//                numberOfSeconds, subbandCount, splitter, numberOfBeats, prog);
//
//        return new Pair<String, String>("BPMAllPart16SubbandsConstant", ((Integer)bpm).toString());
//    }
    // TODO: BPM - HLEDANI



    private static Pair<String, String> analyzeBPMBarycenterPart(Program prog) {
        int subbandCount = 6;
        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitterOld(prog.sampleRate, 200, subbandCount);
        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, 200, subbandCount);
        int startBPM = 60;
        int jumpBPM = 10;
        int upperBoundBPM = 290;
        double numberOfSeconds;
        int numberOfBeats;
        int bpm;
        CombFilterBPMGetterIFace combFilterAlg;

//        numberOfSeconds = 2.2;
        numberOfSeconds = 6.15;       // Maybe 6.2 but this feels ok
        numberOfBeats = (int)Math.ceil(numberOfSeconds);
        combFilterAlg = new CombFilterBPMBarycenterGetter();      // Barycenter version
        bpm = combFilterAlg.calculateBPM(startBPM, jumpBPM, upperBoundBPM,
                numberOfSeconds, subbandCount, splitter, numberOfBeats, prog);

        return new Pair<String, String>("BPM (Barycenter part)", ((Integer)bpm).toString());
    }

    private static Pair<String, String> analyzeBPMAllPart(Program prog) {
        int subbandCount = 6;
        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, subbandCount);
        // TODO: Vymazat - respektive vyzkouset, az pak vymazat
//        SubbandSplitterIFace splitter = new SubbandSplitterOld(prog.sampleRate, 200, subbandCount);
        SubbandSplitterIFace splitter = new SubbandSplitter(prog.sampleRate, 200, 200, subbandCount);

        int startBPM = 60;
        int jumpBPM = 10;
        int upperBoundBPM = 290;
        double numberOfSeconds;
        int numberOfBeats;
        int bpm;
        CombFilterBPMGetterIFace combFilterAlg;


        numberOfSeconds = 2.2;
//        numberOfSeconds = 6.15;       // Maybe 6.2 but this feels ok
//        numberOfSeconds = 5.2;
//        numberOfSeconds = 3.2;
//        numberOfSeconds = 4.15;
        numberOfBeats = (int)Math.ceil(numberOfSeconds);
        combFilterAlg = new CombFilterBPMAllSubbandsGetter();     // All subbands version
        bpm = combFilterAlg.calculateBPM(startBPM, jumpBPM, upperBoundBPM,
                numberOfSeconds, subbandCount, splitter, numberOfBeats, prog);

        return new Pair<String, String>("BPM (All part)", ((Integer)bpm).toString());
    }


    public static void addSongBPMToList(Program prog, List<Pair<String, Pair<String, Integer>>> list) {
        findBestCoefsAdvancedFullLinear(prog, list);
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
     * @param prog
     * @param algName
     * @param list
     * @param calculatedBPM is the bpm of the currently compared algorithm
     * @param referenceBPM is used if bpm > 0
     * @return Returns the calculated BPM reference value
     */
    public static int addBPMToList(Program prog, String algName,
                                    List<Pair<String, Pair<String, Integer>>> list,
                                    int calculatedBPM, int referenceBPM) {
        int difference;
        int bpm;

        if(referenceBPM > 0) {
            bpm = referenceBPM;
            difference = calculateDif(bpm, calculatedBPM);
        }
        else {
            if (prog.getFileName().toUpperCase().contains("BPM")) {
                bpm = getBPMFromName(prog.getFileName());
                difference = BPM_DIF_MULT_FACTOR * calculateDif(bpm, calculatedBPM);
            } else {
                Pair<String, String> tmpPair;
                tmpPair = analyzeBPMAllPart(prog);
                int bpmAll = Integer.parseInt(tmpPair.getValue());

                tmpPair = analyzeBPMBarycenterPart(prog);
                int bpmBarycenter = Integer.parseInt(tmpPair.getValue());
                bpm = bpmAll + bpmBarycenter;
                bpm /= 2;

                difference = calculateDif(bpm, calculatedBPM);
            }
        }


        Pair<String, Integer> valuePair = new Pair<>(prog.getFileName(), difference);
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
//            ProgramTest.debugPrint(difList.get(i));
        }
    }
}
