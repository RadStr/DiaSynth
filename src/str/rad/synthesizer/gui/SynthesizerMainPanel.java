package str.rad.synthesizer.gui;

import str.rad.synthesizer.gui.util.DiagramFileFilter;
import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.diagram.panels.port.util.PortChooser;
import str.rad.synthesizer.synth.Unit;
import str.rad.main.AddToAudioPlayerIFace;
import str.rad.main.TabChangeIFace;
import str.rad.util.audio.format.AudioFormatJPanel;
import str.rad.util.audio.format.AudioFormatWithSign;
import str.rad.plugin.util.JTextFieldWithBounds;
import str.rad.plugin.util.FieldSetterIFace;
import str.rad.player.AudioPlayerPanel;
import str.rad.util.audio.format.FileFilterAudioFormats;
import str.rad.player.control.AudioControlPanelWithZoom;
import str.rad.util.logging.DiasynthLogger;
import str.rad.util.swing.BooleanButton;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.lang.reflect.Field;
import java.util.List;

public class SynthesizerMainPanel extends JPanel implements TabChangeIFace, SynthesizerMainPanelIFace {
    public SynthesizerMainPanel(JFrame frame, AddToAudioPlayerIFace audioPlayerAddIFace) {
        this.frame = frame;
        this.audioPlayerAddIFace = audioPlayerAddIFace;
        buttonPanel = new JPanel();
        GridBagLayout mainLayout = new GridBagLayout();
        this.setLayout(mainLayout);
        GridBagConstraints constraints = new GridBagConstraints();

        iteratorButton = new JButton("ITERATOR");
        iteratorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DiagramPanel diagramPanel = botPanel.getDiagramPanel();
                JList panelList = new JList(new ListModelForPanels(diagramPanel.getPanels()));
                panelList.addListSelectionListener(new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        //https://stackoverflow.com/questions/3812744/valuechanged-in-listselectionlistener-not-working
                        int index = ((JList) e.getSource()).getSelectedIndex();
                        diagramPanel.zoomToPanel(index);
                    }
                });

                JOptionPane.showOptionDialog(null, panelList, "Zoom to panel",
                                             JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                             null, new Object[]{}, null);
            }
        });

        DiasynthLogger.log("Creating wave visualizer inside synth part", 1);
        waveVisualizer = new PlayedWaveVisualizer();
        DiasynthLogger.log("Created wave visualizer inside synth part", -1);
        DiasynthLogger.log("Creating bottom panel inside synth part", 1);
        botPanel = new DiagramJSplitPane(this, waveVisualizer);
        DiasynthLogger.log("Created bottom panel inside synth part", -1);

        DiasynthLogger.log("Adding rest of synth part", 1);
        playerButtons = new AudioControlPanelWithZoom(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (playerButtons.getPlayButton().getBoolVar()) {
                            botPanel.getDiagramPanel().resetAudio();
                            waveVisualizer.pause();
                        }
                        else {
                            botPanel.getDiagramPanel().startAudio();
                            waveVisualizer.start();
                        }
                    }
                },
                botPanel.getDiagramPanel().getAudioThread(),
                (e) -> botPanel.getDiagramPanel().zoomToMiddle(1),
                (e) -> botPanel.getDiagramPanel().zoomToMiddle(-1));
        playerButtons.getZoomPanel().setNewZoom(DiagramPanel.ZOOM_COUNT_FROM_START_TO_MIN, false);
        playerButtons.setMasterGainToCurrentSlideValue();

        buttonPanel.add(iteratorButton);


        playerButtons.add(waveVisualizer);
        GridBagConstraintsSetter.setConstraint(constraints, 0, 0, 1, 1,
                                               0, 0, 0, 0, GridBagConstraints.NONE);
        this.add(playerButtons, constraints);

        GridBagConstraintsSetter.setConstraint(constraints, 0, 1, 1, 1,
                                               0, 0, 0, 0, GridBagConstraints.NONE);
        this.add(buttonPanel, constraints);
        GridBagConstraintsSetter.setConstraint(constraints, 0, 2, 1, 1,
                                               0, 0, 1, 1, GridBagConstraints.BOTH);
        this.add(botPanel, constraints);

        menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        addNewToMenu(menu);
        addSaveDiagramToMenu(menu);
        addLoadDiagramToMenu(menu);
        addChangeOutputFormatToMenu(menu);
        menuBar.add(menu);

        menu = new JMenu("Record");
        addSetRecordInfoToMenu(menu);
        menu.addSeparator();

        instantRecordingCheckbox = new JCheckBoxMenuItem("Record instantly");
        instantRecordingCheckbox.addItemListener((e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                botPanel.getDiagramPanel().recordInstantly();
            }
            instantRecordingCheckbox.setSelected(false);
        });
        menu.add(instantRecordingCheckbox);
        instantRecordingCheckbox.setToolTipText("Records audio without playing it " +
                                                "(so it takes only fraction of the total play time of recorded audio)");

        realTimeRecordingCheckbox = new JCheckBoxMenuItem("Record in real-time");
        realTimeRecordingCheckbox.addItemListener((e) -> botPanel.getDiagramPanel().recordRealTime());
        menu.add(realTimeRecordingCheckbox);
        menu.addSeparator();

        playerRecordCheckBoxMenuItem = new JCheckBoxMenuItem("Record to player");
        playerRecordCheckBoxMenuItem.addItemListener((e) -> {
            botPanel.getDiagramPanel().setIsRecordingToPlayer();
            setEnabledRecordingCheckboxes();
        });
        playerRecordCheckBoxMenuItem.setSelected(true);
        menu.add(playerRecordCheckBoxMenuItem);

        fileRecordCheckBoxMenuItem = new JCheckBoxMenuItem("Record to file");
        fileRecordCheckBoxMenuItem.addItemListener((e) -> {
            botPanel.getDiagramPanel().setIsRecordingToFile();
            setEnabledRecordingCheckboxes();
        });
        fileRecordCheckBoxMenuItem.setSelected(false);
        menu.add(fileRecordCheckBoxMenuItem);
        menuBar.add(menu);

        menu = new JMenu("View");
        JCheckBox shouldViewWaveCheckbox = new JCheckBox("Draw wave", true);
        shouldViewWaveCheckbox.setToolTipText("If the checkbox is checked, " +
                                              "then the generated samples are drawn, otherwise not.");
        shouldViewWaveCheckbox.addItemListener(
                (e) -> waveVisualizer.setShouldViewWave(e.getStateChange() == ItemEvent.SELECTED));
        menu.add(shouldViewWaveCheckbox);
        menuBar.add(menu);
        DiasynthLogger.log("Added rest of synth part", -1);
    }

    private AddToAudioPlayerIFace audioPlayerAddIFace;

    private PlayedWaveVisualizer waveVisualizer;

    private JButton iteratorButton;
    private AudioControlPanelWithZoom playerButtons;
    private JPanel buttonPanel;
    private DiagramJSplitPane botPanel;
    private JFrame frame;

    private JMenuBar menuBar;

    private RecordInfoGetterPanel recordInfoGetter;
    private JCheckBoxMenuItem realTimeRecordingCheckbox;
    private JCheckBoxMenuItem instantRecordingCheckbox;

    private JCheckBoxMenuItem playerRecordCheckBoxMenuItem;
    private JCheckBoxMenuItem fileRecordCheckBoxMenuItem;


    @Override
    public int getTopButtonsPreferredHeight() {
        return buttonPanel.getPreferredSize().height + playerButtons.getPreferredSize().height;
    }


    /**
     * Enables the recording checkboxes based on the selection of the 2 other checkboxes which give info where should
     * be the record put.
     */
    private void setEnabledRecordingCheckboxes() {
        boolean canRecord = playerRecordCheckBoxMenuItem.isSelected() || fileRecordCheckBoxMenuItem.isSelected();
        if (realTimeRecordingCheckbox.isEnabled() != canRecord) {
            setEnabledRecordingCheckboxes(canRecord);
        }
    }

    private void setEnabledRecordingCheckboxes(boolean enabled) {
        realTimeRecordingCheckbox.setEnabled(enabled);
        instantRecordingCheckbox.setEnabled(enabled);
    }

    private void addNewToMenu(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("New");
        menuItem.setToolTipText("Removes all panels from board");


        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DiagramPanel diagramPanel = botPanel.getDiagramPanel();
                diagramPanel.clearPanelsExceptOutputs();
                diagramPanel.repaint();
            }
        });

        menu.add(menuItem);
    }


    private void addSaveDiagramToMenu(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("Save");
        menuItem.setToolTipText("Saves the diagram to file");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
        fileChooser.addChoosableFileFilter(new DiagramFileFilter());
        // Set default name
        fileChooser.setSelectedFile(new File("diagram"));


        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showSaveDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    f = new File(f.getAbsolutePath() + DiagramFileFilter.DIAGRAM_EXTENSION);
                    try {
                        f.createNewFile();
                    }
                    catch (IOException ex) {
                        DiasynthLogger.logException(ex);
                        return;
                    }
                    try (PrintWriter pw = new PrintWriter(f)) {
                        botPanel.getDiagramPanel().save(pw);
                    }
                    catch (FileNotFoundException ex) {
                        DiasynthLogger.logException(ex);
                    }
                }
            }
        });

        menu.add(menuItem);
    }

    private void addLoadDiagramToMenu(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("Load");
        menuItem.setToolTipText("Loads the diagram from file (also removes all currently added panels)");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
        fileChooser.addChoosableFileFilter(new DiagramFileFilter());
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    try {
                        Reader r = new FileReader(f);
                        botPanel.getDiagramPanel().load(new BufferedReader(r));
                    }
                    catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        menu.add(menuItem);
    }


    // Close to the code from audio player, but needs to be a bit modified
    private void addChangeOutputFormatToMenu(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("Change output audio format");

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AudioFormatJPanel p = new AudioFormatJPanel(botPanel.getDiagramPanel().getOutputAudioFormat());
                int result = JOptionPane.showConfirmDialog(null, p, "Audio format chooser",
                                                           JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    botPanel.getDiagramPanel().setOutputAudioFormat(p.getFormat().createJavaAudioFormat(true));
                }
            }
        });
        menu.add(menuItem);
    }

    private void addSetRecordInfoToMenu(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("SET RECORD INFO");

        recordInfoGetter = new RecordInfoGetterPanel();
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showOptionDialog(null, recordInfoGetter,
                                                          "Record parameters setter", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                          null, new Object[]{}, null);
            }
        });

        menu.add(menuItem);
    }

    @Override
    public void clickRealTimeRecordingCheckbox() {
        SwingUtilities.invokeLater(() -> realTimeRecordingCheckbox.doClick());
    }

    @Override
    public void putRecordedWaveToPlayer(byte[] record, int len, AudioFormatWithSign outputFormat,
                                        boolean shouldConvertToPlayerOutputFormat) {
        audioPlayerAddIFace.addToAudioPlayer(record, len, outputFormat, shouldConvertToPlayerOutputFormat);
    }

    public class RecordInfoGetterPanel extends JPanel implements FieldSetterIFace {
        public RecordInfoGetterPanel() {
            this.setLayout(new GridLayout(0, 2));
            this.add(new JLabel("Record time in seconds: "));
            Field[] declaredFields = RecordInfoGetterPanel.class.getDeclaredFields();
            Field field = null;
            for (Field f : declaredFields) {
                if ("recordTimeInSecs".equals(f.getName())) {
                    field = f;
                    field.setAccessible(true);
                    break;
                }
            }

            try {
                this.add(new JTextFieldWithBounds(true, 0, 60000,
                                                  "Put in the time in seconds (so for example 0.5 for half a second)",
                                                  field, this, this));
            }
            catch (IllegalAccessException e) {
                DiasynthLogger.logException(e);
            }

            JLabel shouldConvertLabel = new JLabel("Convert to audio player output audio format:");
            shouldConvertLabel.setToolTipText("Converted only when adding to the player, " +
                                              "when adding to file the output audioFormat from synth part is used");

            this.add(shouldConvertLabel);
            JCheckBox shouldConvertCheckBox = new JCheckBox();
            this.add(shouldConvertCheckBox);
            shouldConvertCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    botPanel.getDiagramPanel().setShouldConvertToPlayerFormat(e.getStateChange() == ItemEvent.SELECTED);
                }
            });
            shouldConvertCheckBox.setToolTipText("Converted only when adding to the player, " +
                                                 "when adding to file the output audioFormat from synth part is used");
            shouldConvertCheckBox.setSelected(true);


            JButton recordButton = new JButton("CHOOSE RECORD FILE");
            this.add(recordButton);

            JButton closeButton = new JButton("CLOSE");
            closeButton.addActionListener((e) -> PortChooser.closeDialog(this));
            this.add(closeButton);

            recordButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fileChooser = AudioPlayerPanel.getFileChooserForSaving(chosenFile);

                    int returnVal = fileChooser.showSaveDialog(frame);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        chosenFile = fileChooser.getSelectedFile();
                        filter = (FileFilterAudioFormats) fileChooser.getFileFilter();
                        botPanel.getDiagramPanel().setRecordPathRelatedValues(chosenFile, filter.AUDIO_TYPE);
                        closeButton.doClick();
                    }
                }
            });


            {
                JFileChooser fileChooser = AudioPlayerPanel.getFileChooserForSaving(null);
                File file = fileChooser.getSelectedFile();
                filter = (FileFilterAudioFormats) fileChooser.getFileFilter();
                botPanel.getDiagramPanel().setRecordPathRelatedValues(file, filter.AUDIO_TYPE);
            }
        }


        private double recordTimeInSecs = 3;
        private File chosenFile;
        private FileFilterAudioFormats filter;


        @Override
        public void setField(Field field, String value) {
            if ("-".equals(value)) {
                return;
            }
            else if ("".equals(value)) {
                setEnabledRecordingCheckboxes(false);
                recordTimeInSecs = 0;
                botPanel.getDiagramPanel().setRecordTimeInSeconds(recordTimeInSecs);
            }
            else {
                double val = Double.parseDouble(value);
                recordTimeInSecs = val;
                if (val == 0) {
                    setEnabledRecordingCheckboxes(false);
                }
                else {
                    if (!instantRecordingCheckbox.isEnabled()) {
                        setEnabledRecordingCheckboxes();
                    }
                }

                botPanel.getDiagramPanel().setRecordTimeInSeconds(recordTimeInSecs);
            }
        }
    }


    @Override
    public void changedTabAction(boolean hasFocus) {
        if (hasFocus) {
            frame.setJMenuBar(menuBar);
        }
        else {
            resetPlaying();
        }
    }

    private void resetPlaying() {
        BooleanButton playButton = playerButtons.getPlayButton();
        if (!playButton.getBoolVar()) {
            playButton.doClick();
        }
        else {
            botPanel.getDiagramPanel().resetAudio();
        }
    }

    @Override
    public AudioControlPanelWithZoom getAudioControlPanel() {
        return playerButtons;
    }

    private static class ListModelForPanels implements ListModel {
        public ListModelForPanels(List<Unit> panels) {
            this.panels = panels;
        }

        private List<Unit> panels;

        @Override
        public int getSize() {
            return panels.size();
        }

        @Override
        public Object getElementAt(int index) {
            return panels.get(index).getPanelName();
        }

        @Override
        public void addListDataListener(ListDataListener l) {
            // EMPTY - no changes occur
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            // EMPTY - no changes occur
        }
    }
}
