package str.rad.main;

import str.rad.synthesizer.gui.SynthesizerMainPanel;
import str.rad.util.audio.format.AudioFormatWithSign;
import str.rad.player.AudioPlayerPanel;
import str.rad.util.logging.DiasynthLogger;
import str.rad.util.swing.FrameWithFocusControl;
import str.rad.analyzer.AnalyzerMainPanel;
import str.rad.util.swing.SwingUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;

public class DiasynthTabbedPanel extends JTabbedPane implements AddToAudioPlayerIFace {
    public DiasynthTabbedPanel(FrameWithFocusControl frame) {
        DiasynthLogger.log("Creating parts", 1);
        DiasynthLogger.log("Creating Analyzer", 1);
        analyzerTab = new AnalyzerMainPanel(frame, this);
        this.addTab("Analyser", null, analyzerTab, "Audio analyzer");
        this.setMnemonicAt(0, KeyEvent.VK_1);
        DiasynthLogger.log("Created Analyzer", -1);

        DiasynthLogger.log("Creating Audio player", 1);
        audioPlayerTab = new AudioPlayerPanel(frame);
        this.addTab("Audio player", null, audioPlayerTab, "Audio player");
        this.setMnemonicAt(1, KeyEvent.VK_2);
        DiasynthLogger.log("Created Audio player", -1);

        DiasynthLogger.log("Creating synthesizer", 1);
        synthTab = new SynthesizerMainPanel(frame, this);
        this.addTab("Diagram synthesizer", null, synthTab, "Diagram synthesizer");
        this.setMnemonicAt(2, KeyEvent.VK_3);
        DiasynthLogger.log("Created synthesizer", -1);


        // Modified code from https://stackoverflow.com/questions/6799731/jtabbedpane-changelistener
        this.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                TabChangeIFace p = getPanelFromSelectedIndex(getSelectedIndex());
                if (oldTab != null) {
                    oldTab.changedTabAction(false);
                }
                p.changedTabAction(true);
                oldTab = p;
            }
        });

        this.setSelectedIndex(2);
        DiasynthLogger.log("Created parts", -1);
    }


    public static final int MAXIMIZED_FRAME_WIDTH;
    public static final int MAXIMIZED_FRAME_HEIGHT;

    static {
        Dimension size = SwingUtils.calculateMaximizedFrameSize();
        MAXIMIZED_FRAME_WIDTH = size.width;
        MAXIMIZED_FRAME_HEIGHT = size.height;
    }

    public static final int MAX_LABEL_FONT_SIZE;
    static {
        JLabel testLabel = new JLabel("T");
        int fontSize = 24;
        try {
            fontSize = SwingUtils.findMaxFontSize(testLabel);
        }
        catch(Exception e) {
            fontSize = 24;
            DiasynthLogger.logException(e);
        }
        finally {
            MAX_LABEL_FONT_SIZE = fontSize;
        }

        DiasynthLogger.logWithoutIndentation("max FONT size: " + Integer.toString(MAX_LABEL_FONT_SIZE));
    }

    private TabChangeIFace oldTab;
    private AnalyzerMainPanel analyzerTab;
    private AudioPlayerPanel audioPlayerTab;

    @Override
    public void addToAudioPlayer(byte[] audio, int len, AudioFormatWithSign format,
                                 boolean shouldConvertToPlayerOutputFormat) {
        audioPlayerTab.addWaves(new ByteArrayInputStream(audio), len, format, shouldConvertToPlayerOutputFormat);
    }

    private SynthesizerMainPanel synthTab;

    private TabChangeIFace getPanelFromSelectedIndex(int selectedIndex) {
        switch (selectedIndex) {
            case 0:
                return analyzerTab;
            case 1:
                return audioPlayerTab;
            case 2:
                return synthTab;
            default:
                DiasynthLogger.logWithoutIndentation("Invalid selected index inside method " +
                                               "getPanelFromSelectedIndex in DiasynthTabbedPanel class");
                System.exit(15);
                return null;
        }
    }

    @Override
    public void addToAudioPlayer(String path) {
        audioPlayerTab.addWaves(path, true);
    }
}
