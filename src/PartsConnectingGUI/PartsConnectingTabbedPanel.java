package PartsConnectingGUI;

import DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage.MainPanelWithEverything;
import RocnikovyProjektIFace.AudioFormatChooserPackage.AudioFormatWithSign;
import RocnikovyProjektIFace.AudioPlayerPanelIFaceImplementation;
import RocnikovyProjektIFace.FrameWithFocusControl;
import RocnikovyProjektIFace.MenuPanel;
import Rocnikovy_Projekt.MyLogger;
import Rocnikovy_Projekt.Program;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;

public class PartsConnectingTabbedPanel extends JTabbedPane implements AddToAudioPlayerIFace {
    public PartsConnectingTabbedPanel(FrameWithFocusControl frame) {
        MyLogger.log("Creating parts", 1);
        MyLogger.log("Creating Analyzer", 1);
        analyzerTab = new MenuPanel(frame, this);
        this.addTab("Analyser", null, analyzerTab, "Audio analyzer");
        this.setMnemonicAt(0, KeyEvent.VK_1);
        MyLogger.log("Created Analyzer", -1);

        MyLogger.log("Creating Audio player", 1);
        audioPlayerTab = new AudioPlayerPanelIFaceImplementation(frame);
        this.addTab("Audio player", null, audioPlayerTab, "Audio player");
        this.setMnemonicAt(1, KeyEvent.VK_2);
        MyLogger.log("Created Audio player", -1);

        MyLogger.log("Creating synthesizer", 1);
        synthTab = new MainPanelWithEverything(frame, this);
        this.addTab("Diagram synthesizer", null, synthTab, "Diagram synthesizer");
        this.setMnemonicAt(2, KeyEvent.VK_3);
        MyLogger.log("Created synthesizer", -1);


        // Modified code from https://stackoverflow.com/questions/6799731/jtabbedpane-changelistener
        this.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ChangeJMenuBarIFace p = getPanelFromSelectedIndex(getSelectedIndex());
                if(oldTab != null) {
                    oldTab.changedTabAction(false);
                }
                p.changedTabAction(true);
                oldTab = p;
            }
        });

        this.setSelectedIndex(2);
        MyLogger.log("Created parts", -1);
    }


    public static final int MAXIMIZED_FRAME_WIDTH;
    public static final int MAXIMIZED_FRAME_HEIGHT;
    static {
        Dimension size = Program.calculateMaximizedFrameSize();
        MAXIMIZED_FRAME_WIDTH = size.width;
        MAXIMIZED_FRAME_HEIGHT = size.height;
    }
    public static final int MAX_LABEL_FONT_SIZE;
    static {
        JLabel testLabel = new JLabel("T");
        MAX_LABEL_FONT_SIZE = Program.findMaxFontSize(testLabel);
    }

    private ChangeJMenuBarIFace oldTab;
    private MenuPanel analyzerTab;
    private AudioPlayerPanelIFaceImplementation audioPlayerTab;
    @Override
    public void addToAudioPlayer(byte[] audio, int len, AudioFormatWithSign format,
                                 boolean shouldConvertToPlayerOutputFormat) {
        audioPlayerTab.addWaves(new ByteArrayInputStream(audio), len, format, shouldConvertToPlayerOutputFormat);
    }
    private MainPanelWithEverything synthTab;

    private ChangeJMenuBarIFace getPanelFromSelectedIndex(int selectedIndex) {
        switch (selectedIndex) {
            case 0:
                return analyzerTab;
            case 1:
                return audioPlayerTab;
            case 2:
                return synthTab;
            default:
                MyLogger.logWithoutIndentation("Invalid selected index inside method getPanelFromSelectedIndex in PartsConnectingTabbedPanel class");
                System.exit(15);
                return null;
        }
    }

    @Override
    public void addToAudioPlayer(String path) {
        audioPlayerTab.addWaves(path);
    }
}
