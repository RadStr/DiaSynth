package str.rad.analyzer;

import str.rad.analyzer.observer.DataModelObserverIFace;
import str.rad.analyzer.observer.DataModelSubject;
import str.rad.analyzer.observer.DataModelSubjectIFace;
import str.rad.util.Aggregation;
import str.rad.util.Pair;
import str.rad.analyzer.bpm.*;
import str.rad.analyzer.util.UneditableTableModel;
import str.rad.analyzer.plugin.ifaces.AnalyzerBytePluginIFace;
import str.rad.analyzer.plugin.ifaces.AnalyzerDoublePluginIFace;
import str.rad.analyzer.plugin.ifaces.AnalyzerIntPluginIFace;
import str.rad.util.Time;
import str.rad.util.audio.AudioUtilities;
import str.rad.util.audio.wave.ByteWave;
import str.rad.util.audio.wave.DoubleWave;
import str.rad.util.logging.DiasynthLogger;
import str.rad.util.swing.ErrorFrame;
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
import java.util.List;

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


    @Override
    public void leavingPanel() {
        int rowCount = table.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            dataModel.removeRow(0);
        }
    }


    public AnalyzerPanel(JFrame thisWindow, DataModelObserverIFace[] observers) {
        frame = thisWindow;
        this.setLayout(new BorderLayout());
        DiasynthLogger.log("Creating Data model subject", 1);
        subject = new DataModelSubject(observers, thisWindow);
        DiasynthLogger.log("Created data model subject", -1);

        DiasynthLogger.log("Adding analyzer panel components", 1);
        buttonPanel = new JPanel(new FlowLayout());
        buttons = new JButton[3];
        fileChooseButton = new JButton("Choose file/directory to analyze");
        analyzeButton = new JButton("Analyze selected songs");
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rowCount = dataModel.getRowCount();
                boolean anyFileAnalyzed = false;
                for (int i = 0; i < rowCount; i++) {
                    analyze((String) dataModel.getValueAt(0, 0));
                    anyFileAnalyzed = true;
                    dataModel.removeRow(0);
                }
                if (anyFileAnalyzed) {
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

        for (int i = 0; i < buttons.length; i++) {
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
        for (int i = 0; i < checkBoxes.length; i++) {
            box.add(checkBoxes[i]);
            checkBoxes[i].setSelected(true);
        }
        DiasynthLogger.log("Added analyzer panel components", -1);

        DiasynthLogger.log("Adding analyzer plugins", 1);
        loadPlugins();
        addPluginsToBox(box);
        DiasynthLogger.log("Added analyzer plugins", -1);

        checkBoxScrollPane = new JScrollPane(box);
        this.add(checkBoxScrollPane, BorderLayout.EAST);


        String[] header = {"Selected files"};
        String[] names = new String[]{};

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
                if (currentDirectory != null) {
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
        DiasynthLogger.log("Adding analyzer byte plugins", 1);
        List<AnalyzerBytePluginIFace> bytePlugins = AnalyzerBytePluginIFace.loadPlugins();
        for (AnalyzerBytePluginIFace p : bytePlugins) {
            JCheckBox checkBox = new JCheckBox(p.getName());
            checkBox.setToolTipText(p.getTooltip());
            checkBox.setSelected(true);
            bytePluginPairs.add(new Pair<>(checkBox, p));
        }
        DiasynthLogger.log("Added analyzer byte plugins", -1);

        DiasynthLogger.log("Adding analyzer int plugins", 1);
        List<AnalyzerIntPluginIFace> intPlugins = AnalyzerIntPluginIFace.loadPlugins();
        for (AnalyzerIntPluginIFace p : intPlugins) {
            JCheckBox checkBox = new JCheckBox(p.getName());
            checkBox.setToolTipText(p.getTooltip());
            checkBox.setSelected(true);
            intPluginPairs.add(new Pair<>(checkBox, p));
        }
        DiasynthLogger.log("Added analyzer int plugins", -1);

        DiasynthLogger.log("Adding analyzer double plugins", 1);
        List<AnalyzerDoublePluginIFace> doublePlugins = AnalyzerDoublePluginIFace.loadPlugins();
        for (AnalyzerDoublePluginIFace p : doublePlugins) {
            JCheckBox checkBox = new JCheckBox(p.getName());
            checkBox.setToolTipText(p.getTooltip());
            checkBox.setSelected(true);
            doublePluginPairs.add(new Pair<>(checkBox, p));
        }
        DiasynthLogger.log("Added analyzer int plugins", -1);
    }

    private void addPluginsToBox(Box box) {
        for (Pair<JCheckBox, AnalyzerBytePluginIFace> p : bytePluginPairs) {
            box.add(p.getKey());
        }
        for (Pair<JCheckBox, AnalyzerIntPluginIFace> p : intPluginPairs) {
            box.add(p.getKey());
        }
        for (Pair<JCheckBox, AnalyzerDoublePluginIFace> p : doublePluginPairs) {
            box.add(p.getKey());
        }
    }

    private void runSelectedPlugins(ByteWave byteWave, List<Pair<String, String>> list) {
        runSelectedPluginsByte(byteWave, list);
        runSelectedPluginsInt(byteWave, list);
        runSelectedPluginsDouble(byteWave, list);
    }

    private void runSelectedPluginsByte(ByteWave byteWave, List<Pair<String, String>> list) {
        for (Pair<JCheckBox, AnalyzerBytePluginIFace> p : bytePluginPairs) {
            if (p.getKey().isSelected()) {
                list.add(analyzeBytePlugin(byteWave, p.getValue()));
            }
        }
    }

    private Pair<String, String> analyzeBytePlugin(ByteWave byteWave, AnalyzerBytePluginIFace plugin) {
        return plugin.analyze(byteWave.getSong(), byteWave.getNumberOfChannels(), byteWave.getSampleSizeInBytes(),
                              byteWave.getSampleRate(), byteWave.getIsBigEndian(), byteWave.getIsSigned());
    }

    private void runSelectedPluginsInt(ByteWave byteWave, List<Pair<String, String>> list) {
        int[] wave = null;
        for (Pair<JCheckBox, AnalyzerIntPluginIFace> p : intPluginPairs) {
            if (p.getKey().isSelected()) {
                if (wave == null) {
                    try {
                        wave = byteWave.convertBytesToSamples();
                    }
                    catch (IOException e) {
                        return;
                    }
                }
                list.add(p.getValue().analyze(wave, byteWave.getNumberOfChannels(), byteWave.getSampleRate()));
            }
        }
    }

    private void runSelectedPluginsDouble(ByteWave byteWave, List<Pair<String, String>> list) {
        DoubleWave wave = null;
        for (Pair<JCheckBox, AnalyzerDoublePluginIFace> p : doublePluginPairs) {
            if (p.getKey().isSelected()) {
                if (wave == null) {
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
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                addFilesToModel(files[i].listFiles());
            }
            else {
                try {
                    dataModel.addRow(new String[]{files[i].getCanonicalPath()});
                }
                catch (IOException e) {            // Shouldn't happen
                    DiasynthLogger.logException(e);
                    new ErrorFrame(frame, "Unknown error");
                }
            }
        }
    }


    public void analyze(String filename) {
        File file = new File(filename);
        List<Pair<String, String>> list = new ArrayList<>();
        Pair<String, String> pair = new Pair<>(SongLibraryPanel.HEADER_NAME_COLUMN_TITLE, file.getName());
        list.add(pair);
        pair = new Pair<>("Path", file.getAbsolutePath());
        list.add(pair);

        ByteWave byteWave;
        try {
            byteWave = ByteWave.loadSong(filename, true);
            if (byteWave == null) {
                DiasynthLogger.logWithoutIndentation("Error in method analyze(String filename) in AnalyzerPanel\n" +
                                               filename + "\n" + AudioUtilities.LOG_MESSAGE_WHEN_SET_VARIABLES_RETURN_FALSE);
                return;
            }
        }
        catch (IOException e) {
            DiasynthLogger.logException(e);
            return;
        }

        int numberOfChannels = byteWave.getNumberOfChannels();

        try {
            byteWave.convertToMono();
        }
        catch (IOException e) {
            return;
        }


        if (checkBoxes[0].isSelected()) {
            list.add(analyzeSizeInBytes(byteWave));
        }
        if (checkBoxes[1].isSelected()) {
            list.add(analyzeSongLength(byteWave));
        }
        if (checkBoxes[2].isSelected()) {
            list.add(analyzeFileFormat(byteWave));
        }
        if (checkBoxes[3].isSelected()) {
            list.add(analyzeEncoding(byteWave));
        }
        if (checkBoxes[4].isSelected()) {
            list.add(analyzeSampleSize(byteWave));
        }
        if (checkBoxes[5].isSelected()) {
            list.add(analyzeSampleRate(byteWave));
        }
        if (checkBoxes[6].isSelected()) {
            list.add(analyzeNumberOfChannels(numberOfChannels));
        }
        if (checkBoxes[7].isSelected()) {
            list.add(analyzeEndianness(byteWave));
        }

        double[] mods = null;
        if (checkBoxes[8].isSelected() || checkBoxes[9].isSelected() || checkBoxes[10].isSelected()) {
            mods = byteWave.calculateAllAggregations();
        }
        if (checkBoxes[8].isSelected()) {
            list.add(analyzeSampleMax(mods));
            list.add(analyzeSampleMin(mods));
        }
        if (checkBoxes[9].isSelected()) {
            list.add(analyzeSampleAverage(mods));
        }
        if (checkBoxes[10].isSelected()) {
            list.add(analyzeSampleRMS(mods));
        }
        if (checkBoxes[11].isSelected()) {
            list.add(analyzeBPMSimpleFull(byteWave));
            list.add(analyzeBPMAdvancedFullLinear(byteWave));
            list.add(analyzeBPMAllPart(byteWave));
            list.add(analyzeBPMBarycenterPart(byteWave));
        }

        runSelectedPlugins(byteWave, list);


        Node node = AnalyzerXML.getFirstSongNodeMatchingGivenName(file.getName());
        if (node == null) {                           // The song wasn't analyzed before
            AnalyzerXML.addAnalyzedFileToXML(AnalyzerXML.getXMLDoc(), list, "songs", "song");
        }
        else {
            NodeList childNodes = node.getChildNodes();
            for (Pair<String, String> p : list) {
                Node currentPairNode = AnalyzerXML.findFirstNodeWithGivenAttribute(childNodes, p.getKey());
                if (currentPairNode == null) {        // Add new node
                    AnalyzerXML.addNewNode(node, p);
                }
                else {                                // Change existing node
                    AnalyzerXML.getValueNodeFromInfoNode(currentPairNode).setTextContent(p.getValue());
                }
            }
        }
    }


    private static Pair<String, String> analyzeFileFormat(ByteWave byteWave) {
        return new Pair<String, String>("File format", byteWave.getFileFormatType());
    }

    private static Pair<String, String> analyzeSampleRate(ByteWave byteWave) {
        return new Pair<String, String>("Sample rate", ((Integer) byteWave.getSampleRate()).toString());
    }

    private static Pair<String, String> analyzeSongLength(ByteWave byteWave) {
        return new Pair<String, String>(SongLibraryPanel.HEADER_LENGTH_COLUMN_TITLE,
                                        Time.convertSecondsToTime(byteWave.getLengthOfAudioInSeconds(), -1));
    }

    private static Pair<String, String> analyzeSizeInBytes(ByteWave byteWave) {
        Integer len = byteWave.getWholeFileSize();
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
        if (byteWave.getIsBigEndian()) {
            return new Pair<String, String>("Endianness", "Big endian");
        }
        else {
            return new Pair<String, String>("Endianness", "Little endian");
        }
    }

    private static Pair<String, String> analyzeEncoding(ByteWave byteWave) {
        return new Pair<String, String>("Encoding", byteWave.getEncoding().toString());
    }

    private static Pair<String, String> analyzeSampleSize(ByteWave byteWave) {
        return new Pair<String, String>("Sample Size (In bytes)",
                                        ((Integer) (byteWave.getSampleSizeInBytes())).toString());
    }

    private static Pair<String, String> analyzeNumberOfChannels(int numberOfChannels) {
        return new Pair<String, String>("Number of channels", ((Integer) numberOfChannels).toString());
    }

    private static Pair<String, String> analyzeBPMSimpleFull(ByteWave byteWave) {
        return new Pair<String, String>("BPM simple", ((Integer) byteWave.computeBPMSimple()).toString());
    }


    private static Pair<String, String> analyzeBPMAdvancedFullLinear(ByteWave byteWave) {
        int subbandCount = 8;
        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);

        return new Pair<String, String>("BPM - NOT ALWAYS CORRECT", ((Integer) byteWave.computeBPMSimpleWithFreqBands(subbandCount,
                                                                                                                 splitter, 2.5, 6, 0.16)).toString());
    }


    public static Pair<String, String> analyzeBPMBarycenterPart(ByteWave byteWave) {
        int subbandCount = 6;
        SubbandSplitterIFace splitter = new SubbandSplitter(byteWave.getSampleRate(), 200, 200, subbandCount);
        int startBPM = 60;
        int jumpBPM = 10;
        int upperBoundBPM = 290;
        double numberOfSeconds;
        int numberOfBeats;
        int bpm;
        CombFilterBPMGetterIFace combFilterAlg;

        numberOfSeconds = 6.15;
        numberOfBeats = (int) Math.ceil(numberOfSeconds);
        combFilterAlg = new CombFilterBPMBarycenterGetter();      // Barycenter version
        bpm = combFilterAlg.computeBPM(startBPM, jumpBPM, upperBoundBPM,
                                       numberOfSeconds, subbandCount, splitter, numberOfBeats, byteWave);

        return new Pair<String, String>("BPM comb filter - barycenter", ((Integer) bpm).toString());
    }

    public static Pair<String, String> analyzeBPMAllPart(ByteWave byteWave) {
        int subbandCount = 6;
        SubbandSplitterIFace splitter = new SubbandSplitter(byteWave.getSampleRate(), 200, 200, subbandCount);

        int startBPM = 60;
        int jumpBPM = 10;
        int upperBoundBPM = 290;
        double numberOfSeconds;
        int numberOfBeats;
        int bpm;
        CombFilterBPMGetterIFace combFilterAlg;


        numberOfSeconds = 2.2;
        numberOfBeats = (int) Math.ceil(numberOfSeconds);
        combFilterAlg = new CombFilterBPMAllSubbandsGetter();     // All sub-bands version
        bpm = combFilterAlg.computeBPM(startBPM, jumpBPM, upperBoundBPM, numberOfSeconds,
                                       subbandCount, splitter, numberOfBeats, byteWave);

        return new Pair<String, String>("BPM comb filter - sum", ((Integer) bpm).toString());
    }
}
