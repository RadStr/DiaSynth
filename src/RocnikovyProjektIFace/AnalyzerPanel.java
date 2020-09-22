package RocnikovyProjektIFace;

import RocnikovyProjektIFace.AnalyzerPlugins.PluginIFaces.AnalyzerBytePluginIFace;
import RocnikovyProjektIFace.AnalyzerPlugins.PluginIFaces.AnalyzerDoublePluginIFace;
import RocnikovyProjektIFace.AnalyzerPlugins.PluginIFaces.AnalyzerIntPluginIFace;
import RocnikovyProjektIFace.SpecialSwingClasses.ErrorFrame;
import org.w3c.dom.Element;
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

import Rocnikovy_Projekt.*;


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
                    XML.createXMLFile(ANALYZED_AUDIO_XML_FILENAME, XML.xmlDoc.getFirstChild(), frame);
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
        checkBoxes = new JCheckBox[11];
        checkBoxes[0] = new JCheckBox("Find sampling rate");
        checkBoxes[1] = new JCheckBox("Find length");
        checkBoxes[2] = new JCheckBox("Find size in bytes");
        checkBoxes[3] = new JCheckBox("Find sample peaks");
        checkBoxes[4] = new JCheckBox("Find sample average");
        checkBoxes[5] = new JCheckBox("Find RMS (special average)");
        checkBoxes[6] = new JCheckBox("Endianity");
        checkBoxes[7] = new JCheckBox("Encoding");
        checkBoxes[8] = new JCheckBox("Sample size");
        checkBoxes[9] = new JCheckBox("number of channels");
        checkBoxes[10] = new JCheckBox("BPM");

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
            checkBox.setSelected(true);
            bytePluginPairs.add(new Pair<>(checkBox, p));
        }
        MyLogger.log("Added analyzer byte plugins", -1);

        MyLogger.log("Adding analyzer int plugins", 1);
        List<AnalyzerIntPluginIFace> intPlugins = AnalyzerIntPluginIFace.loadPlugins();
        for(AnalyzerIntPluginIFace p : intPlugins) {
            JCheckBox checkBox = new JCheckBox(p.getName());
            checkBox.setSelected(true);
            intPluginPairs.add(new Pair<>(checkBox, p));
        }
        MyLogger.log("Added analyzer int plugins", -1);

        MyLogger.log("Adding analyzer double plugins", 1);
        List<AnalyzerDoublePluginIFace> doublePlugins = AnalyzerDoublePluginIFace.loadPlugins();
        for(AnalyzerDoublePluginIFace p : doublePlugins) {
            JCheckBox checkBox = new JCheckBox(p.getName());
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

    private void runCheckedPlugins(Program prog, List<Pair<String, String>> list) {
        runCheckedPluginsByte(prog, list);
        runCheckedPluginsInt(prog, list);
        runCheckedPluginsDouble(prog, list);
    }

    private void runCheckedPluginsByte(Program prog, List<Pair<String, String>> list) {
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

    private void runCheckedPluginsInt(Program prog, List<Pair<String, String>> list) {
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

    private void runCheckedPluginsDouble(Program prog, List<Pair<String, String>> list) {
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


    private NodeList nList;

    private void performActionForFileChooser(int returnVal) {
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            nList = XML.xmlDoc.getElementsByTagName("name");
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
                    new ErrorFrame(frame, "Unknown error");		// TODO: Asi ok reseni
                }
            }
        }
    }

    public void analyze(String filename) {
        File file = new File(filename);
        List<Pair<String, String>> list = new ArrayList<>();
        Pair<String, String> pair = new Pair<>("name", file.getName());
        list.add(pair);

        try {
            if(!p.setVariables(filename, true)) {        // TODO: Zasadni ... nastavit ty hodnoty
                MyLogger.logWithoutIndentation("Error in analyze(String filename) in AnalyzerPanel\n" +
                             Program.LOG_MESSAGE_WHEN_SET_VARIABLES_RETURN_FALSE);
                return;
            }
        } catch (IOException e) {
            MyLogger.logException(e);
            return;         // TODO: podle me je lepsi proste ten soubor preskocit ... do budoucna by bylo lepsi psat i proc jsem je preskocil ... hlavne u tech setVariables !!!!!!!!!!!!!!!!!!!!!!
            //new ErrorFrame(frame, "Couldn't set variables for song:\n" + e.getMessage());
        }

        // TODO: !!!!!Jen pro ted - chci zpracovavat kazdej kanal zvlast a pro kazdej mit vlastni informace - a ne to delat na mono
        try {
            p.convertMultiChannelToMono();
            //p.convertSampleRates(22050);
        }
        catch(IOException e) {
            return;
        }
        // TODO: !!!!!!

        if(checkBoxes[0].isSelected()) {
            list.add(analyzeSampleRate(p));
        }
        if(checkBoxes[1].isSelected()) {
            list.add(analyzeSongLength(p));
        }
        if(checkBoxes[2].isSelected()) {
            list.add(analyzeSizeInBytes(p));
        }
        int[] mods = null;
        if(checkBoxes[3].isSelected() || checkBoxes[4].isSelected() || checkBoxes[5].isSelected()) {		// TODO: nemel bych vybirat takhle natvrdo ty indexy
            try {
                mods = Program.getAllMods(p.song, p.sampleSizeInBytes, p.isBigEndian, p.isSigned);
            } catch (IOException e) {
                new ErrorFrame(frame, "Invalid sample size:\t" + e.getMessage());
            }
        }
        if(checkBoxes[3].isSelected()) {
            list.add(analyzeSampleMin(mods));
            list.add(analyzeSampleMax(mods));
        }
        if(checkBoxes[4].isSelected()) {
            list.add(analyzeSampleAverage(mods));
        }
        if(checkBoxes[5].isSelected()) {
            list.add(analyzeSampleRMS(mods));
        }
        if(checkBoxes[6].isSelected()) {
            list.add(analyzeEndianity(p));
        }
        if(checkBoxes[7].isSelected()) {
            list.add(analyzeEncoding(p));
        }
        if(checkBoxes[8].isSelected()) {
            list.add(analyzeSampleSize(p));
        }
        if(checkBoxes[9].isSelected()) {
            list.add(analyzeNumberOfChannels(p));
        }
        if(checkBoxes[10].isSelected()) {
            list.add(analyzeBPMSimpleFull(p));
            list.add(analyzeBPMAdvancedFull(p));
            list.add(analyzeBPMAllPart(p));
            list.add(analyzeBPMBarycenterPart(p));
        }

        runCheckedPluginsByte(p, list);
        runCheckedPluginsInt(p, list);
        runCheckedPluginsDouble(p, list);


//		char[] c = new char[] {'a'};		// TODO:
//		for(int i = 0; i < 5; i++) {
//			pair = new Pair<>(new String(c), new String(c));		// TODO: tady by se mela provest ta analyza featur
//			list.add(pair);
//			c[0]++;
//		}
        pair = new Pair<>("path", file.getAbsolutePath());
        list.add(pair);


        // Check if the name wasn't already analyzed, if yes then just change the node (and change only the analyzed attributes)
        NodeList sList = XML.xmlDoc.getElementsByTagName("song"); // TODO: Zase ocekavam ze je jen 1 songs, coz ma byt
        NodeList nList = XML.xmlDoc.getElementsByTagName("name");
        System.out.println("file:\t" + file.getName());
        int index = XML.findNodeWithValue(nList, file.getName());
        if(index == -1) {		// The song wasn't analyzed before
            XML.addAnalyzedFileToXML(XML.xmlDoc, list, "songs", "song");
        }
        else {
            Node node = sList.item(index);
            NodeList childNodes = node.getChildNodes();
            for(Pair<String, String> p : list) {
                Node currentPairNode = XML.findNodeXML(childNodes, p.getKey());
                if(currentPairNode == null) {		// Add new node
                    Element elem;
                    try {
                        elem = XML.xmlDoc.createElement(p.getKey());
                    }
                    catch(Exception e) {
                        MyLogger.logWithoutIndentation("Invalid name in xml tree");
                        MyLogger.logException(e);
                        continue;
                    }
                    elem.appendChild(XML.xmlDoc.createTextNode(p.getValue()));
                    node.appendChild(elem);
                }
                else {								// Change existing node
                    currentPairNode.setTextContent(p.getValue());
                }
            }
        }
    }

    private static Pair<String, String> analyzeSampleRate(Program prog) {
        return new Pair<String, String>("sampleRate", ((Integer)prog.sampleRate).toString());
    }

    private static Pair<String, String> analyzeSongLength(Program prog) {
        return new Pair<String, String>("length",
                Program.convertSecondsToTime(prog.lengthOfAudioInSeconds, -1));
    }

    private static Pair<String, String> analyzeSizeInBytes(Program prog) {
        Integer len = prog.wholeFileSize;
        return new Pair<String, String>("fileSizeInBytes", len.toString());
    }

    private static Pair<String, String> analyzeSampleMin(int[] mods) {
        Integer min = mods[0];							// TODO: nemel bych to vybirat takhle natvrdo ty indexy
        return new Pair<String, String>("sampleMINVal", min.toString());
    }

    private static Pair<String, String> analyzeSampleMax(int[] mods) {
        Integer max = mods[1];
        return new Pair<String, String>("sampleMAXVal", max.toString());
    }

    private static Pair<String, String> analyzeSampleAverage(int[] mods) {
        Integer avg = mods[2];
        return new Pair<String, String>("sampleAVGVal", avg.toString());
    }

    private static Pair<String, String> analyzeSampleRMS(int[] mods) {
        Integer rms = mods[3];
        return new Pair<String, String>("sampleRMSVal", rms.toString());
    }

    private static Pair<String, String> analyzeEndianity(Program prog) {
        if(prog.isBigEndian) {
            return new Pair<String, String>("endianity", "big endian");
        }
        else {
            return new Pair<String, String>("endianity", "little endian");
        }
    }

    private static Pair<String, String> analyzeEncoding(Program prog) {
        return new Pair<String, String>("encoding", prog.encoding.toString());// TODO: To chce asi pres swithch spis
    }

    private static Pair<String, String> analyzeSampleSize(Program prog) {
        return new Pair<String, String>("sampleSize", ((Integer)(prog.sampleSizeInBits / 8)).toString());
    }

    private static Pair<String, String> analyzeNumberOfChannels(Program prog) {
        return new Pair<String, String>("numberOfChannels", ((Integer)prog.numberOfChannels).toString());
    }

    private static Pair<String, String> analyzeBPMSimpleFull(Program prog) {
        return new Pair<String, String>("BPMSimpleFull", ((Integer)prog.getBPMSimple()).toString());
    }

    private static Pair<String, String> analyzeBPMAdvancedFull(Program prog) {
        SubbandSplitterIFace splitter = new SubbandSplitterConstant(prog.sampleRate);
        int subbandCount = 6;
        return new Pair<String, String>("BPMAdvancedFull", ((Integer)prog.getBPMSimpleWithFreqDomainsWithVariance(subbandCount, splitter)).toString());
    }

    private static Pair<String, String> analyzeBPMBarycenterPart(Program prog) {
        SubbandSplitterIFace splitter = new SubbandSplitterConstant(prog.sampleRate);
        int subbandCount = 6;
        int startBPM = 60;
        int jumpBPM = 10;
        int upperBoundBPM = 290;
        double numberOfSeconds;
        int numberOfBeats;
        int bpm;
        GetBPMUsingCombFilterIFace combFilterAlg;


        numberOfSeconds = 6.15;       // Maybe 6.2 but this feels ok
        numberOfBeats = (int)Math.ceil(numberOfSeconds);
        combFilterAlg = new GetBPMUsingCombFilterBarycenter();      // Barycenter version
        bpm = combFilterAlg.calculateBPM(startBPM, jumpBPM, upperBoundBPM,
            numberOfSeconds, subbandCount, splitter, numberOfBeats, prog);

        return new Pair<String, String>("BPMBarycenterPart", ((Integer)bpm).toString());
    }

    private static Pair<String, String> analyzeBPMAllPart(Program prog) {
        SubbandSplitterIFace splitter = new SubbandSplitterConstant(prog.sampleRate);

        int subbandCount = 6;
        int startBPM = 60;
        int jumpBPM = 10;
        int upperBoundBPM = 290;
        double numberOfSeconds;
        int numberOfBeats;
        int bpm;
        GetBPMUsingCombFilterIFace combFilterAlg;


        numberOfSeconds = 2.2;
        numberOfBeats = (int)Math.ceil(numberOfSeconds);
        combFilterAlg = new GetBPMUsingCombFilterAllSubbands();     // All subbands version
        bpm = combFilterAlg.calculateBPM(startBPM, jumpBPM, upperBoundBPM,
            numberOfSeconds, subbandCount, splitter, numberOfBeats, prog);

        return new Pair<String, String>("BPMAllPart", ((Integer)bpm).toString());
    }
}
