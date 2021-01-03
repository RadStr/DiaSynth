package player;


import player.plugin.ifaces.user.waves.util.AlignmentOnWavesOperation;
import player.wave.WavePanelMouseListener;
import util.audio.wave.ByteWave;
import player.mixer.*;
import player.control.AudioControlPanel;
import player.control.AudioControlPanelWithZoomAndDecibel;
import player.control.ZoomPanel;
import player.mixer.ifaces.AudioMixerIFace;
import player.operations.wave.*;
import player.operations.wave.arithmetic.LogarithmOnWave;
import player.operations.wave.arithmetic.MultiplicationOnWave;
import player.operations.wave.arithmetic.AdditionOnWave;
import player.operations.wave.arithmetic.PowerOnWave;
import plugin.util.AnnotationPanel;
import player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import player.plugin.ifaces.user.waves.OperationOnWavesPluginIFace;
import player.wave.WaveMainPanel;
import player.wave.WavePanel;
import synthesizer.synth.audio.AudioThread;
import main.TabChangeIFace;
import util.Utilities;
import util.audio.AudioConverter;
import util.audio.AudioUtilities;
import util.audio.format.AudioFormatJPanel;
import util.audio.format.AudioFormatJPanelWithConvertFlag;
import util.audio.format.AudioFormatWithSign;
import util.audio.format.ChannelCount;
import plugin.PluginBaseIFace;
import player.operations.waves.FillWaveWithOtherWaveOperation;
import player.operations.waves.arithmetic.LogarithmOnWaves;
import player.operations.waves.arithmetic.MultiplicationOnWaves;
import player.operations.waves.arithmetic.AdditionOnWaves;
import player.operations.waves.arithmetic.PowerOnWaves;
import player.operations.wave.filters.LowPassFilter;
import player.decibel.DecibelMeter;
import player.decibel.SamplesGetterIFace;
import player.experimental.*;
import player.experimental.FFTWindowPanel;
import util.audio.io.AudioWriter;
import util.swing.BooleanButton;
import util.swing.EmptyPanelWithoutSetMethod;
import dialogs.EmptyWaveMakerDialog;
import dialogs.LengthDialog;
import util.audio.wave.DoubleWave;
import util.logging.MyLogger;
import test.ProgramTest;
import util.audio.format.FileFilterAudioFormats;
import util.swing.FrameWithFocusControl;


import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class AudioPlayerPanel extends JPanel implements MouseListener,
                                                        AudioPlayerPanelZoomUpdateIFace, WaveScrollEventCallbackIFace,
                                                        SamplesGetterIFace, TabChangeIFace,
                                                        AudioControlPanel.VolumeControlGetterIFace, WaveAdderIFace {
    public static final int HORIZONTAL_SCROLL_UNIT_INCREMENT = 32;
    public static final int VERTICAL_SCROLL_UNIT_INCREMENT = 32;

    public interface ModifyAudioIFace {
        // Returns true if the audio was modified
        void modifyAudio();
    }


    public int getCurrentHorizontalScroll() {
        return waveScrollerWrapperPanel.getCurrentHorizontalScroll();
    }

    public int getMaxHorizontalScroll() {
        return waveScrollerWrapperPanel.getMaxHorizontalScroll();
    }

    public int getMaxVerticalScroll() {
        JScrollBar scrollBar = panelWithWaves.getVerticalScrollBar();
        int max = scrollBar.getMaximum();
        max -= scrollBar.getModel().getExtent();
        return max;
    }


    private AudioFormatWithSign outputAudioFormat;

    private void setOutputAudioFormatToDefault() {
        outputAudioFormat = new AudioFormatWithSign(44100, 16, 2, true, false);
        outputAudioFormat = AudioFormatJPanel.getSupportedAudioFormat(outputAudioFormat);
    }

    private void setOutputAudioFormat(AudioFormatWithSign newFormat, boolean shouldConvertAudio) {
        thisFrame.setEnabled(false);
        audioThread.pause();
        if (outputAudioFormat.getChannels() < newFormat.getChannels()) {
            // The splitters divider locs will be moved, but we don't want to trigger the listeners
            channelCountChanged = true;
        }
        outputAudioFormat = newFormat;
        ChannelCount channelCount = getChannelCount();
        for (WaveMainPanel waveMainPanel : waves) {
            waveMainPanel.updateChannelSliders(channelCount);
        }

        if (shouldConvertAudio) {
            for (WaveMainPanel waveMainPanel : waves) {
                waveMainPanel.setWaveToNewSampleRate((int) newFormat.getSampleRate());
            }
        }
        audioThread.outputFormatChanged();
        thisFrame.setEnabled(true);
    }

    private boolean channelCountChanged = false;

    private ChannelCount getChannelCount() {
        return ChannelCount.convertNumberToEnum(outputAudioFormat.getChannels());
    }


    /**
     * Used for horizontal scrolling on wave
     */
    private WaveScrollerWrapperPanel waveScrollerWrapperPanel;

    public int getEmptyPanelForHorizontalScrollScrollBarHeight() {
        return waveScrollerWrapperPanel.getWaveScroller().getHorizontalScrollBar().getHeight();
    }

    public Dimension getEmptyPanelForHorizontalScrollSizeDebug() {
        return new Dimension(waveScrollerWrapperPanel.getEmptyPanelSizeDebug());
    }

    private void setWaveScrollerPanelsSizes(Dimension size) {
        setWaveScrollerPanelsSizes(size.width, size.height);
    }

    private void setWaveScrollerPanelsSizes(int leftPanelWidth, int rightPanelWidth, int h) {
        waveScrollerWrapperPanel.setEmptyPanelsSizes(leftPanelWidth, rightPanelWidth, h);
    }

    /**
     * Sets the sizes of panels except the one representing the vertical scrollbar
     *
     * @param leftPanelWidth
     * @param rightPanelWidth
     */
    public void setWaveScrollerPanelsSizes(int leftPanelWidth, int rightPanelWidth) {
        setWaveScrollerPanelsSizes(leftPanelWidth, rightPanelWidth, 0);
    }

    private Timer waveScrollerPollTimer;

    /**
     * How does adding to the panel works: the bottom panel is always panel of size 0 (So the bottom component can be moved)
     * The inside of the panel are again JScrollPane where at the bottom is wave and at the top is JScrollPane, etc.
     * The first JScrollPane (the most internal one) has at the top wave but also wave at the bottom,
     * it is the only panel which has 2 valid waves inside without any recursion
     */
    private JScrollPane panelWithWaves;

    public int getPanelWithWavesVerticalScrollbarWidth() {
        return panelWithWaves.getVerticalScrollBar().getWidth();
    }

    public Rectangle getScrollPanelViewRect() {
        return panelWithWaves.getViewport().getViewRect();
    }

    /**
     * When working with waves, I have to not only change the JSplitPanes but also propagate it to this array.
     * For example when wave swapping.
     */
    private List<WaveMainPanel> waves;

    public int getWaveCount() {
        return waves.size();
    }


    public void revalidateAndRepaintWaves() {
        for (int i = 0; i < waves.size(); i++) {
            JPanel wave = waves.get(i);
            wave.revalidate();
            wave.repaint();
        }
    }


    private void postDeletionAction(boolean removedAllWavesFromPlayer) {
        updateWavesForMixing();
        if (removedAllWavesFromPlayer) {
            clickPauseButtonIfPlaying();
            audioThread.reset();
        }
    }


    private void removeAllWaves() {
        shouldMarkPart = false;
        setEnabledAllMenus(false);
        clipboard.removeWaveFromClipboard();
        waveScrollerPollTimer.stop();

        removeOldListeners();
        panelWithWaves.setViewportView(null);
        for (JSplitPane s : splitters) {
            s.setTopComponent(null);
            s.setBottomComponent(null);
        }
        waves.clear();
        splitters.clear();
        resetZoom();
        postDeletionAction(true);
        waveScrollerWrapperPanel.resetEmptyPanelSize();
        waveScrollerWrapperPanel.revalidate();
        waveScrollerWrapperPanel.repaint();
        this.revalidate();
        this.repaint();
    }

    /**
     * Index starting from 0.
     *
     * @param index
     */
    private void removeWave(int index) {
        int len = waves.size();
        boolean isRemovingLastRemainingWave = len == 1;
        if (isRemovingLastRemainingWave) {
            removeWaveLastRemaining();
        }
        else if (len > 1) {
            removeWaveMoreThanOneRemaining(index);
        }

        postDeletionAction(isRemovingLastRemainingWave);
    }

    private void removeWaveLastRemaining() {
        removeAllWaves();
    }


    private void removeWaveMoreThanOneRemaining(int index) {
        swapWithLastAndRemove(index);
        EmptyPanelWithoutSetMethod zeroSizePanel = new EmptyPanelWithoutSetMethod();
        getLastJSplitPane().setBottomComponent(zeroSizePanel);
        setSplittersMouseListener();
    }


    private void swapWithLastAndRemove(int index) {
        WaveMainPanel deletedWave = waves.get(index);
        clipboard.removeWaveFromClipboard(deletedWave);
        JSplitPane lastSplitter = getLastJSplitPane();
        int lastWaveIndex = waves.size() - 1;
        if (index != lastWaveIndex) {
            moveSwapSplitter(index, lastWaveIndex);
        }
        removeOldListeners();

        splitters.remove(splitters.size() - 1);
        waves.remove(waves.size() - 1);
        setNewLastJSplitPane();

        lastSplitter.setTopComponent(null);
        lastSplitter.setBottomComponent(null);
    }

    private void removeOldListeners() {
        removeSplittersMouseListener();
        removeAdapterFomLastJSplitPaneDivider();
    }

    private void setNewListeners() {
        if (waves.size() != 0) {
            setNewLastJSplitPane();
            setSplittersMouseListener();
        }
    }


    private void alignToLongestWave(DoubleWave wave) {
        if (waves.size() != 0) {
            int waveLen = wave.getSongLength();
            int oldWavesLen = waves.get(0).getSongLen();

            if (waveLen < oldWavesLen) {         // Make the wave longer
                wave.setSong(oldWavesLen);
            }
            else if (waveLen > oldWavesLen) {    // Make all other waves longer
                alignAllWavesToLen(waveLen);
            }
        }
    }

    private void alignAllWavesToLen(int newLen) {
        clipboard.removeWaveFromClipboard();
        // The copied wave has already values set to 0 if there is cutting involved
        for (WaveMainPanel wave : waves) {
            wave.setNewDoubleWave(newLen);
        }

        audioThread.wavesLengthChanged();
    }

    private void alignAllWavesToLenWhileOverwritePasting(int newLen, DoubleWave pasteWave) {
        for (WaveMainPanel wave : waves) {
            if (pasteWave != wave.getDoubleWave()) {
                wave.setNewDoubleWave(newLen);
            }
        }

        clipboard.removeWaveFromClipboard();
    }


    @Override
    public JSplitPane addWave(double[] wave) {
        DoubleWave doubleWave = new DoubleWave(wave, getOutputSampleRate(), 1,
                                               "Doesn't matter I don't create file anyways", false);
        return addWave(doubleWave);
    }

    public JSplitPane addWave(DoubleWave wave) {
        alignToLongestWave(wave);
        removeOldListeners();

        JSplitPane lastSplitPane;
        if (waves.size() == 0) {
            lastSplitPane = addFirstWave(wave);
        }
        else {
            lastSplitPane = addNonFirstWave(wave);
        }

        setNewListeners();
        setVariablesWhichNeededSize();


        // Take a look at the comment inside addNonFirstWave, this is here for the same reason.
        JScrollBar verticalScrollBar = panelWithWaves.getVerticalScrollBar();
        int maxVerticalScroll = getMaxVerticalScroll();
        SwingUtilities.invokeLater(() -> verticalScrollBar.setValue(maxVerticalScroll));
        updateWavesForMixing();
        // Added because when I create wave using fft window.
        // Unless I call revalidate the horizontal scroll isn't shown and repaint repaints the waves.
        revalidate();
        repaint();
        return lastSplitPane;
    }

    private JSplitPane addFirstWave(DoubleWave wave) {
        ChannelCount channelCount = getChannelCount();
        EmptyPanelWithoutSetMethod zeroSizePanel = new EmptyPanelWithoutSetMethod();
        int waveIndex = waves.size() + 1;
        WaveMainPanel newWavePanel = new WaveMainPanel(wave, this, waveIndex, channelCount);
        JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, newWavePanel, zeroSizePanel);
        splitter.setDividerSize(DIVIDER_SIZE);
        waves.add(newWavePanel);
        splitters.add(splitter);
        flattenJSplitPane(splitter);        // Delete borders

        resetZoom();
        waveScrollerPollTimer.start();
        setEnabledAllMenus(true);
        audioThread.wavesLengthChanged();
        // This is here because when the first wave is so small that it is already smaller then the available pixels
        // for the drawing of wave then we perform fake zooming to draw only the individual samples instead of drawing
        // aggregation of values
        fakeZoomUpdate();
        return splitter;
    }


    /**
     * This method performs "zoom" for all waves, but the zoom is from the current zoom to the current zoom.
     * This method is usually called to upgrade all the waves. Because in certain situations, for example when removing
     * part of waves, there is no switch between the individual and aggregate wave visualisation.
     */
    public void fakeZoomUpdate() {
        for (WaveMainPanel w : waves) {
            w.updateZoom(getCurrentZoom(), getCurrentZoom(), false, false);
        }
    }

    private JSplitPane addNonFirstWave(DoubleWave wave) {
        // Now just keep adding to the old splitter new splitter which has in top component the sound waves and
        // in bottom component empty panel
        ChannelCount channelCount = getChannelCount();
        EmptyPanelWithoutSetMethod zeroSizePanel = new EmptyPanelWithoutSetMethod();
        int waveIndex = waves.size() + 1;
        WaveMainPanel newWavePanel = new WaveMainPanel(wave, this, waveIndex, channelCount);
        JSplitPane lastSplitter = getLastJSplitPane();
        lastSplitter.setBottomComponent(newWavePanel);

        // These 2 lines may seem useless, but it has very important purpose. When new wave is added, and it isn't in the visible window
        // Java for some reason decides, that it will ignore the old divider location. So instead of just adding
        // the newWavePanel at the bot and not doing anything with divider loc. it pushes the divider location up.
        // Which makes 2 huge problems. 1st - it is very unexpected behavior - it makes the old larger panel smaller and
        // the new one bigger.
        // 2nd - which does most of the problems - by pushing the divider location up, in such a way that all waves above
        // the newly added are now at minimal size. It creates inconsistency between the real sizes of the waves and its
        // preferred sizes, which makes the waves draw incorrectly
        int lastSplitterDivLoc = lastSplitter.getDividerLocation();
        lastSplitter.setDividerLocation(lastSplitterDivLoc);

        // Add empty panel at the bottom so the bottom wave can be pulled down
        lastSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, lastSplitter, zeroSizePanel);
        lastSplitter.setDividerSize(DIVIDER_SIZE);

        waves.add(newWavePanel);
        splitters.add(lastSplitter);
        flattenJSplitPane(lastSplitter);        // Delete borders
        return lastSplitter;
    }

    public void addWaves(DoubleWave[] waves) {
        for (DoubleWave wave : waves) {
            addWave(wave);
        }
    }


    private FrameWithFocusControl thisFrame;

    public void setVariablesWhichNeededSize() {
        setWaveScrollerPanelsSizes();
        if (!waveScrollerPollTimer.isRunning()) {
            waveScrollerPollTimer.start();
        }
    }

    private void setWaveScrollerPanelsSizes() {
        // https://stackoverflow.com/questions/19869751/get-size-of-jpanel-before-setvisible-called
        int maxWaveWidth = Integer.MIN_VALUE;
        WaveMainPanel maxWave = null;
        for (WaveMainPanel w : waves) {
            int newPossibleMaxWidth = w.getHorizontalScrollSizeForThisWave();
            if (maxWaveWidth < newPossibleMaxWidth) {
                maxWaveWidth = newPossibleMaxWidth;
                maxWave = w;
            }
        }

        setWaveScrollerPanelsSizes(maxWave.getWaveStartX(), maxWaveWidth);
        waveScrollerWrapperPanel.setLastEmptyPanelWidth(getPanelWithWavesVerticalScrollbarWidth());
        waveScrollerWrapperPanel.revalidateEmptyPanel();
        waveScrollerWrapperPanel.repaintEmptyPanel();
    }


    private ZoomVariablesAllWaves zoomVariables;

    public int getCurrentZoom() {
        return zoomVariables.zoom;
    }

    public void setCurrentZoom(int val) {
        zoomVariables.zoom = val;
        ZoomPanel zoomPanel = audioControlPanel.getZoomPanel();
        zoomPanel.setNewZoom(zoomVariables.zoom, zoomVariables.getIsZoomAtMax());
    }

    private void setMaxAllowedZoom() {
        zoomVariables.setMaxAllowedZoom(getMaxPossibleZoom());
    }

    private void resetZoom() {
        setMaxAllowedZoom();
        setCurrentZoom(0);
    }


    /**
     * Used to disable change of preferred size when it is in swapping.
     */
    private boolean isSwapping = false;

    private double[][] songs = null;

    private int calculateNumberOfWavesIncludedInMixing() {
        int count = 0;
        for (WaveMainPanel w : waves) {
            if (w.getShouldIncludeInMixing()) {
                count++;
            }
        }

        return count;
    }


    private volatile boolean hasAtLeastOneWave = false;

    /**
     * Called when wave is added/removed
     */
    public void updateWavesForMixing() {
        int waveCount = calculateNumberOfWavesIncludedInMixing();
        if (waveCount == 0) {
            hasAtLeastOneWave = false;
        }
        setSongs(waveCount);
        setMultFactors(waveCount);
        int numberOfChannels = outputAudioFormat.getChannels();
        audioControlPanel.getDecibelMeterMain().changeNumberOfChannels(numberOfChannels);
        hasAtLeastOneWave = waveCount != 0;
    }

    private void setSongs() {
        int waveCount = calculateNumberOfWavesIncludedInMixing();
        setSongs(waveCount);
    }

    /**
     * Internal method for setSongs without parameters
     *
     * @param waveCount
     */
    private void setSongs(int waveCount) {
        if (waveCount == 0) {
            songs = null;
        }
        else {
            songs = new double[waveCount][];
            for (int outIndex = 0, waveIndex = 0; outIndex < songs.length; waveIndex++) {
                WaveMainPanel wave = waves.get(waveIndex);
                if (wave.getShouldIncludeInMixing()) {
                    songs[outIndex] = wave.getDoubleWave().getSong();
                    outIndex++;
                }
            }
        }
    }

    private double[][] multFactors = null;

    private void setMultFactors() {
        int waveCount = calculateNumberOfWavesIncludedInMixing();
        setMultFactors(waveCount);
    }

    /**
     * Internal method for setMultFactors without parameters
     *
     * @param waveCount
     */
    private void setMultFactors(int waveCount) {
        if (waveCount == 0) {
            multFactors = null;
        }
        else {
            multFactors = new double[waveCount][outputAudioFormat.getChannels()];
            for (int outIndex = 0, waveIndex = 0; outIndex < multFactors.length; waveIndex++) {
                WaveMainPanel wave = waves.get(waveIndex);
                if (wave.getShouldIncludeInMixing()) {
                    for (int ch = 0; ch < multFactors[outIndex].length; ch++) {
                        multFactors[outIndex][ch] = wave.getMixMultiplier(ch);
                    }
                    outIndex++;
                }
            }
        }

        audioThread.mixer.update(multFactors);
    }

    public void updateMultFactors(WaveMainPanel wave, int channel, double newValue) {
        if (multFactors != null && wave.getShouldIncludeInMixing()) {
            int index = findIndexInMixing(wave);
            multFactors[index][channel] = newValue;
            audioThread.mixer.update(multFactors);
        }
    }

    private int findIndexInMixing(WaveMainPanel wave) {
        for (int i = 0, outIndex = 0; i < waves.size(); i++) {
            WaveMainPanel w = waves.get(i);
            if (wave == w) {
                return outIndex;
            }
            if (w.getShouldIncludeInMixing()) {
                outIndex++;
            }
        }

        return -1;
    }


    private List<JSplitPane> splitters;

    private MouseAdapter lastSplitterMouseAdapter;

    private final static int DIVIDER_SIZE = 15;

    private TimestampsPanel timestampPanel;

    private int oldVisibleWidth = -1;

    public AudioPlayerPanel(FrameWithFocusControl frame) {
        this();

        zoomVariables = new ZoomVariablesAllWaves();
        AudioPlayerPanelMouseWheelListener mouseWheelListener = new AudioPlayerPanelMouseWheelListener(this);
        this.addMouseWheelListener(mouseWheelListener);

        splitters = new ArrayList<>();
        thisFrame = frame;
        shouldMarkPart = false;

        // Add menus
        menuBar = new JMenuBar();

        MyLogger.log("Adding JMenuBar internals", 1);
        // Saving/loading
        JMenu fileManipulationJMenu = new JMenu("File");
        addAddEmptyWaveToWaves(fileManipulationJMenu);
        addAddFileToWaves(fileManipulationJMenu);
        addAddMonoFileToWaves(fileManipulationJMenu);

        addOpenEmptyFileToWaves(fileManipulationJMenu);
        addOpenFileToMenu(fileManipulationJMenu);
        addOpenMonoFileToMenu(fileManipulationJMenu);

        addSetWaveLengths(fileManipulationJMenu);

        addRemoveAllWaves(fileManipulationJMenu);

        fileManipulationJMenu.addSeparator();
        addSaveFileToMenu(fileManipulationJMenu);
        addChangeOutputFormatToMenu(fileManipulationJMenu);
        menuBar.add(fileManipulationJMenu);


        // Audio modification
        JMenu audioModJMenu = new JMenu("Audio modification");
        audioModJMenu.setMnemonic(KeyEvent.VK_A);
        audioModJMenu.getAccessibleContext().setAccessibleDescription("Menu with audio modification methods");

        addTotallyRemoveAudioPart(audioModJMenu);

        addAudioOperationsWithoutWave(audioModJMenu);
        audioModJMenu.addSeparator();

        JMenu drawJMenu = new JMenu("EXPERIMENTAL");
        drawJMenu.setToolTipText("FFT and wave drawing");
        addDrawWindowsOperations(drawJMenu);
        menuBar.add(drawJMenu);

        addFilters(audioModJMenu);
        audioModJMenu.addSeparator();

        addAudioOperationsWithWave(audioModJMenu);
        audioModJMenu.addSeparator();

        MyLogger.log("Adding audio player plugins", 1);
        addPlugins(audioModJMenu);
        MyLogger.log("Added audio player plugins", -1);

        menuBar.add(audioModJMenu);


        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem shouldViewDecibelsMenuItem = new JCheckBoxMenuItem("Show decibel meter");
        shouldViewDecibelsMenuItem.setSelected(true);
        shouldViewDecibelsMenuItem.addItemListener(e -> {
            DecibelMeter dm = audioControlPanel.getDecibelMeterMain().getDecibelMeter();
            dm.setIsDrawingEnabled(e.getStateChange() == ItemEvent.SELECTED);
        });
        viewMenu.add(shouldViewDecibelsMenuItem);
        menuBar.add(viewMenu);


        frame.setJMenuBar(menuBar);

        setEnabledAllMenus(false);
        setEnabledWithWaveMenuItems(false);
        MyLogger.log("Added JMenuBar internals", -1);


        waveScrollerWrapperPanel = new WaveScrollerWrapperPanel(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                                                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS, this);


        waveScrollerPollTimer = new Timer(64, new ActionListener() {        // Parameter to play with
            @Override
            public void actionPerformed(ActionEvent e) {
                pollMovement();
            }
        });

        panelWithWaves = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panelWithWaves.getVerticalScrollBar().setUnitIncrement(VERTICAL_SCROLL_UNIT_INCREMENT);
        panelWithWaves.setWheelScrollingEnabled(false);
        waves = new ArrayList<WaveMainPanel>();


        // Create TIMESTAMP PANEL
        int currGridY = 1;
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;       // BOTH because else there is space with nothing at the bottom of page
        timestampPanel = new TimestampsPanel(this);
        constraints.gridx = 0;
        constraints.gridy = currGridY;
        currGridY++;
        constraints.weightx = 0.1;
        constraints.weighty = 0.005;
        // Pad is used just for this, because otherwise the timestamps are shown incorrectly in some cases
        // (when added first wave, and making the panel smaller than it is)
        constraints.ipady = 10;
        this.add(timestampPanel, constraints);
        constraints.ipady = 0;

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;       // BOTH because else there is space with nothing at the bottom of page


        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        this.add(panelWithWaves, constraints);
        panelWithWaves.addMouseWheelListener(mouseWheelListener);

        constraints.weighty = 0;
        constraints.gridy = 3;
        this.add(waveScrollerWrapperPanel, constraints);


        ComponentListener resizeListener = new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                for (DoubleWave d : wavesToAddLater) {
                    addWave(d);
                }
                wavesToAddLater.clear();
                revalidate();
                repaint();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                Component c = e.getComponent();
                int w = c.getWidth();
                callOnResize();

                if (oldVisibleWidth != w && waves.size() != 0) {
                    oldVisibleWidth = w;
                    visibleWidthChangedCallback();
                    setWaveScrollerPanelsSizes();
                }
            }
        };

        this.addComponentListener(resizeListener);

        this.addMouseListener(this);

        currSample = 0;
        currSampleUserSelected = 0;
        timeLineX = 0;
        audioThread = new PlayerAudioThread(true, 100, 20);
        audioThread.start();
    }


    private void visibleWidthChangedCallback() {
        for (WaveMainPanel waveMainPanel : waves) {
            waveMainPanel.visibleWidthChangedCallback();
        }
    }


    private void reloadDrawValuesForAllWaves() {
        for (WaveMainPanel w : waves) {
            w.reloadDrawValues();
        }
    }


    private List<PropertyChangeListener> splittersPropertyChangeListeners = new ArrayList<>();

    private void removeSplittersMouseListener() {
        removeAllAnySplitterDraggedListeners();

        JSplitPane splitter;
        for (int i = 0; i < splittersPropertyChangeListeners.size(); i++) {
            splitter = splitters.get(i);
            splitter.removePropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
                                                  splittersPropertyChangeListeners.get(i));
        }
        splittersPropertyChangeListeners.clear();
    }

    public void setSplittersMouseListener() {
        setAnySplitterDraggedListenersForAllSplitters();

        PropertyChangeListener listener;
        JSplitPane splitter;
        splitter = splitters.get(0);
        listener = new FirstSplitterChangeListener(splitter);
        splittersPropertyChangeListeners.add(listener);
        splitter.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, listener);

        JSplitPane nextSplitter = splitter;
        for (int i = 1; i < splitters.size() - 1; i++) {
            splitter = nextSplitter;
            nextSplitter = splitters.get(i);
            listener = new CompoundSplitterChangeListener(splitter, nextSplitter);
            splittersPropertyChangeListeners.add(listener);
            nextSplitter.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, listener);
        }

        // Java bug - 1 pixel splitter
        if (splitters.size() >= 2) {
            JSplitPane lastSplitter = splitters.get(splitters.size() - 1);
            listener = new CompoundLastSplitterChangeListener(lastSplitter);
            splittersPropertyChangeListeners.add(listener);
            lastSplitter.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, listener);
        }
    }


    /**
     * if == 0 we moved the last component (divider) in the recursive moving.
     */
    private int dividerRemainder = 0;
    private boolean movingDivsRecursively = false;

    private class FirstSplitterChangeListener implements PropertyChangeListener {
        private JSplitPane splitter;

        public FirstSplitterChangeListener(JSplitPane splitter) {
            this.splitter = splitter;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (!isSwapping) {
                if (!channelCountChanged) {
                    // If divider moved down - dif > 0 (so bot component will be smaller, top bigger) else < 0
                    int oldValue = (int) evt.getOldValue();
                    int newValue = (int) evt.getNewValue();

                    if (oldValue >= 0) {
                        if (newValue < splitter.getMinimumDividerLocation()) {
                            MyLogger.log("CRITICAL SIZE ERROR INSIDE FirstSplitterChangeListener (PARAMETERS ON NEXT LINE):\n" +
                                         "Smaller than min divider:\t" + newValue + "\t" +
                                         splitter.getMinimumDividerLocation(), 0);
                        }

                        int dif = newValue - oldValue;
                        WaveMainPanel top;
                        WaveMainPanel bot;
                        top = (WaveMainPanel) splitter.getTopComponent();
                        if (waves.size() != 1) {
                            bot = (WaveMainPanel) splitter.getBottomComponent();
                            setPrefSizes(bot, top, dif);
                        }
                        else {
                            top.setPreferredSize(newValue);
                        }
                    }
                }
            }
        }
    }

    // Every splitter except the first one and the last one
    private class CompoundSplitterChangeListener implements PropertyChangeListener {
        private JSplitPane topSplitter;
        private JSplitPane botSplitter;


        /**
         * The arguments are the splitters in which are the waves which are divided by this divider.
         * The waves sizes changes with the move of divider, so we change their preferred sizes.
         *
         * @param topSplitter
         * @param botSplitter
         */
        public CompoundSplitterChangeListener(JSplitPane topSplitter, JSplitPane botSplitter) {
            this.topSplitter = topSplitter;
            this.botSplitter = botSplitter;
        }


        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (!isSwapping) {
                if (!channelCountChanged) {
                    int oldValue = (int) evt.getOldValue();
                    int newValue = (int) evt.getNewValue();

                    if (oldValue >= 0) {
                        int dif = newValue - oldValue;
                        WaveMainPanel top;
                        WaveMainPanel bot;
                        if (newValue < topSplitter.getMinimumDividerLocation()) {
                            MyLogger.log("CRITICAL SIZE ERROR INSIDE CompoundSplitterChangeListener (PARAMETERS ON NEXT LINE):\n" +
                                         "Smaller than min divider top:\t" + newValue + "\t" +
                                         topSplitter.getMinimumDividerLocation(), 0);
                        }
                        if (newValue < botSplitter.getMinimumDividerLocation()) {
                            MyLogger.log("CRITICAL SIZE ERROR INSIDE CompoundSplitterChangeListener (PARAMETERS ON NEXT LINE):\n" +
                                         "Smaller than min divider bot:\t" + newValue + "\t" +
                                         botSplitter.getMinimumDividerLocation(), 0);
                        }
                        top = (WaveMainPanel) topSplitter.getBottomComponent();
                        bot = (WaveMainPanel) botSplitter.getBottomComponent();
                        setPrefSizes(bot, top, dif);
                    }
                }
            }
        }
    }


    // Java bug - 1 pixel splitter
    private int oldLocationFromPropertyChangeListener = getOldLocationFromPropertyChangeListenerDefaultVal();

    private static int getOldLocationFromPropertyChangeListenerDefaultVal() {
        return -1;
    }

    private void resetOldLocationFromPropertyChangeListenerToDefaultVal() {
        oldLocationFromPropertyChangeListener = getOldLocationFromPropertyChangeListenerDefaultVal();
    }

    private boolean isOldLocationFromPropertyChangeListenerAtDefaultVal() {
        return oldLocationFromPropertyChangeListener == getOldLocationFromPropertyChangeListenerDefaultVal();
    }

    public class CompoundLastSplitterChangeListener implements PropertyChangeListener {
        public CompoundLastSplitterChangeListener(JSplitPane lastSplitter) {
            this.lastSplitter = lastSplitter;
        }

        private JSplitPane lastSplitter;

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            int oldValue = (int) evt.getOldValue();
            int newValue = (int) evt.getNewValue();


            // || because java doesn't ensure the order of calling listeners
            if (!isSwapping && oldValue != -1 && !channelCountChanged &&
                !((lastSplitterMoved && !lastSplitterDrag) || lastSplitterDrag)) {
                lastSplitterDrag = true;
                oldLocationFromPropertyChangeListener = oldValue;
                int x = 0;

                //int y = newValue;
                int y = newValue - oldValue;
                MouseEvent mouseEvent = new MouseEvent(lastSplitter, MouseEvent.MOUSE_RELEASED, 0, 0, x, y, 1, false);
                lastSplitterMouseAdapter.mouseReleased(mouseEvent);
                lastSplitterMoved = false;
            }
            else {
                resetOldLocationFromPropertyChangeListenerToDefaultVal();
                lastSplitterMoved = false;
            }
        }
    }
    // Java bug - 1 pixel splitter


    private void setPrefSizes(WaveMainPanel bot, WaveMainPanel top, int dif) {
        if (dif < 0) {       // If moving up
            if (movingDivsRecursively) {
                int topDif = top.getDif(dif);
                if (topDif == 0) {
                    // When going up I make the upper smaller and the bot set to min
                    movingDivsRecursively = false;
                    top.setPreferredSizeByAdding(dif);
                    bot.setPrefSizeToMin();
                }
                else {
                    bot.setPrefSizeToMin();
                }
            }
            else {
                int div1 = top.setPreferredSizeByAdding(dif);
                if (div1 < 0) {
                    movingDivsRecursively = true;
                    dividerRemainder = div1;
                }

                // If it isn't last, else it is already set by the listener of the last splitter
                if (bot.getWaveIndex() != waves.size() || !movingLastSplitter) {
                    bot.setPreferredSizeByAdding(-dif);
                }
                else {
                    movingLastSplitter = false;
                }
            }
        }
        else {          // If moving down - there is no recursive movement involved
            top.setPreferredSizeByAdding(dif);
            bot.setPreferredSizeByAdding(-dif);
        }
    }


    private int convertToPixelMovement(int increaseSpeed) {
        return increaseSpeed;
    }

    private int increaseJScrollPane(int increaseSpeed) {
        JViewport view = panelWithWaves.getViewport();
        Point oldPos = view.getViewPosition();
        int increasedSize = convertToPixelMovement(increaseSpeed);
        if (increasedSize > 0) {
            int bottomY = oldPos.y + view.getViewRect().height;
            int viewH = view.getViewSize().height;
            if (bottomY == viewH) {     // Increase the size of JSplitPane
                WaveMainPanel wave = waves.get(waves.size() - 1);
                wave.setPreferredSizeByAdding(increasedSize);
                wave.revalidate();
                wave.repaint();
            }
            else {                    // Just scroll
                int dif = viewH - bottomY;
                if (dif < increasedSize) {
                    increasedSize = dif;
                }
            }
            Point newPos = new Point(oldPos.x, oldPos.y + increasedSize);
            view.setViewPosition(newPos);
        }
        return increasedSize;
    }


    /**
     * Swaps 2 waves. Indexes in parameters are indexed from 1
     *
     * @param oldIndex is the old index of the wave. Starting at 1
     * @param newIndex is the new index of the wave. Starting at 1
     */
    public void swapSplitterComponents(int oldIndex, String oldIndexString, int newIndex, String newIndexString) {
        for (int i = 0; i < waves.size(); i++) {
            isSwapping = true;
        }

        int oldIndexZero = oldIndex - 1;
        int newIndexZero = newIndex - 1;
        WaveMainPanel waveMainPanel1 = waves.get(oldIndexZero);
        WaveMainPanel waveMainPanel2 = waves.get(newIndexZero);

        swap2WavesIndexes(oldIndex, oldIndexString, oldIndexZero,
                          newIndex, newIndexString, newIndexZero, waveMainPanel1, waveMainPanel2);

        // Now the indexes are swapped, so waveMainPanel2 has oldIndexZero. and waveMainPanel1 newIndexZero.
        int waveMainPanel1Index = newIndexZero;
        int waveMainPanel2Index = oldIndexZero;
        //  One component can't be part of 2 splitpanes, so we have to take them out and then swap them
        swapComponentsInSplitters(waveMainPanel1Index, waveMainPanel2Index);


        panelWithWaves.validate();
        panelWithWaves.revalidate();
        panelWithWaves.repaint();
        // I don't know why I am revalidating again, probably just mistake, but I will keep it just in case.
        panelWithWaves.revalidate();
        panelWithWaves.repaint();
        for (int i = 0; i < waves.size(); i++) {
            isSwapping = false;
        }
    }


    private void removeAdapterFomLastJSplitPaneDivider() {
        if (lastSplitterMouseAdapter != null) {
            removeAdapterFromDivider(getLastJSplitPane(), lastSplitterMouseAdapter);
            lastSplitterMouseAdapter = null;
        }
    }

    private static void removeAdapterFromDivider(JSplitPane splitPane, MouseAdapter mouseAdapter) {
        SplitPaneUI spui = splitPane.getUI();
        ((BasicSplitPaneUI) spui).getDivider().removeMouseMotionListener(mouseAdapter);
        ((BasicSplitPaneUI) spui).getDivider().removeMouseListener(mouseAdapter);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////// So we can perform vertical polling when there is splitter dragged
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private AnySplitterDraggedListener anySplitterDraggedListener;

    private void setAnySplitterDraggedListenersForAllSplitters() {
        anySplitterDraggedListener = new AnySplitterDraggedListener();
        for (int i = 0; i < splitters.size(); i++) {
            addMouseAdapterToDivider(splitters.get(i), anySplitterDraggedListener);
        }
    }

    private void removeAllAnySplitterDraggedListeners() {
        for (int i = 0; i < splitters.size(); i++) {
            JSplitPane splitPane = splitters.get(i);
            removeAdapterFromDivider(splitPane, anySplitterDraggedListener);
        }

        anySplitterDraggedListener = null;
    }

    private boolean isAnySplitterDragged = false;

    private boolean getIsAnySplitterDragged() {
        return isAnySplitterDragged;
    }

    private void setIsAnySplitterDragged(boolean value) {
        isAnySplitterDragged = value;
    }


    private class AnySplitterDraggedListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            // EMPTY
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // EMPTY
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            setIsAnySplitterDragged(false);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // EMPTY
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // EMPTY
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            // EMPTY
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            setIsAnySplitterDragged(true);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            // EMPTY
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    // Java bug - 1 pixel splitter
    private boolean lastSplitterMoved = false;


    private boolean movingLastSplitter = false;
    private SplitPaneUI lastSplitterUI;
    private boolean lastSplitterDrag = false;

    public boolean getIsLastSplitterDragged() {
        return lastSplitterDrag;
    }

    private void setNewLastJSplitPane() {
        if (splitters.size() >= 2) {
            JSplitPane lastSplitter = getLastJSplitPane();
            lastSplitterUI = lastSplitter.getUI();

            lastSplitterMouseAdapter = new MouseAdapter() {
                private int previousY = -1;

                @Override
                public void mouseClicked(MouseEvent e) {
                    // EMPTY
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (lastSplitterDrag) {
                        // Java bug - 1 pixel splitter
                        lastSplitterMoved = true;

                        movingLastSplitter = false;
                        lastSplitterDrag = false;
                        JViewport view = panelWithWaves.getViewport();
                        Point p = e.getPoint();
                        Point oldPos = view.getViewPosition();


                        // Java bug - 1 pixel splitter
                        int oldLoc;
                        // If all is normal
                        if (isOldLocationFromPropertyChangeListenerAtDefaultVal()) {
                            oldLoc = lastSplitter.getLastDividerLocation();
                        }
                        else {      // If we are moving the divider by the last pixel (the java bug)
                            oldLoc = oldLocationFromPropertyChangeListener;
                            resetOldLocationFromPropertyChangeListenerToDefaultVal();     // reset to the default value
                        }
                        // Java bug - 1 pixel splitter


                        int divLoc = oldLoc + p.y;
                        int divSize = lastSplitter.getDividerSize();
                        int minDivLoc = lastSplitter.getMinimumDividerLocation();
                        int viewHeight = view.getViewSize().height;
                        int visibleRectH = view.getVisibleRect().height;
                        WaveMainPanel wave = waves.get(waves.size() - 1);
                        Dimension oldPrefSize = wave.getPreferredSize();
                        int newPrefHeight = -1;
                        if (divLoc < minDivLoc) {   // If it is moved so much up that it makes the panel above shorter
                            divLoc = minDivLoc;

                            // Java bug - 1 pixel splitter
                            // When this happens, for some reason the event in change property listener
                            // in the last splitter doesn't happen
                            lastSplitterMoved = false;
                            // Java bug - 1 pixel splitter
                        }
                        else if (divLoc > viewHeight - divSize) {
                            // We take - divSize because if we choose the if(visibleRectH == viewHeight)
                            // then we need to have the whole result in the visible rectangle,
                            // we don't want to enlarge the scrollpane
                            // because making the divider location larger than the scrollpane doesn't work
                            divLoc = viewHeight;
                            divLoc -= divSize;
                        }


                        // If scrollpane can't scroll (all waves are visible without scrolling)
                        if (visibleRectH == viewHeight) {
                            int y = wave.getY();
                            newPrefHeight = divLoc - y;
                        }
                        else {
                            int previousDivLoc = getJSplitPaneDividerLoc(waves.get(wave.getWaveIndex() - 2));
                            newPrefHeight = divLoc - previousDivLoc - divSize;
                        }


                        int dif = newPrefHeight - wave.getMinimumSize().height;
                        if (dif < 0 && existsPanelBiggerThanMinHeight(wave)) {
                            movingLastSplitter = true;
                        }

                        wave.setPreferredSize(newPrefHeight);
                        lastSplitter.setDividerLocation(divLoc);

                        wave.revalidate();
                        wave.repaint();
                        panelWithWaves.revalidate();
                        panelWithWaves.repaint();
                    }
                    else {
                        super.mouseReleased(e);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                }

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    super.mouseWheelMoved(e);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    Point cursorPoint = MouseInfo.getPointerInfo().getLocation();
                    if (!lastSplitterDrag) {
                        lastSplitterDrag = true;
                        previousY = cursorPoint.y;
                    }

                    int panelEndY = getPanelWithWavesEndOnScreenY();
                    int endDif;
                    // If we are above the panel with waves, and we moved down (in mouse movement sense)
                    if ((endDif = cursorPoint.y - panelEndY) > 0 && cursorPoint.y > previousY) {
                        increaseJScrollPane(endDif);
                    }

                    previousY = cursorPoint.y;
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    super.mouseMoved(e);
                }

            };

            addMouseAdapterToDivider(lastSplitter, lastSplitterMouseAdapter);
        }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Component oldView = panelWithWaves.getViewport().getView();
        if (oldView != null) {
            oldView.removeMouseListener(this);
        }
        panelWithWaves.setViewportView(getLastJSplitPane());
        panelWithWaves.getViewport().getView().addMouseListener(this);
    }


    private static void addMouseAdapterToDivider(JSplitPane splitPane, MouseAdapter mouseAdapter) {
        SplitPaneUI splitterUI = splitPane.getUI();
        if (splitterUI instanceof BasicSplitPaneUI) {
            // Setting a mouse listener directly on split pane does not work, because no events are being received.
            ((BasicSplitPaneUI) splitterUI).getDivider().addMouseMotionListener(mouseAdapter);
            ((BasicSplitPaneUI) splitterUI).getDivider().addMouseListener(mouseAdapter);
        }
    }


    private JSplitPane getLastJSplitPane() {
        if (splitters == null || splitters.size() == 0) {
            return null;
        }
        return splitters.get(splitters.size() - 1);
    }


    /**
     * Index is starting from 0. Returns the splitpane containing the wave so for first 2 waves it is 0th splitter, and then it is for n-th (n - 1)th splitter (when indexing all from 0)
     *
     * @param index
     * @return
     */
    private JSplitPane getJSplitPaneContainingWaveFromWaveIndex(int index) {
        JSplitPane splitter;
        if (index == 0) {
            splitter = splitters.get(index);
        }
        else {
            // -1 because the first JSplitPane contains 2 waves, for explanation check panelWithWaves documentation
            splitter = splitters.get(index - 1);
        }

        return splitter;
    }


    public int getJSplitPaneDividerLoc(WaveMainPanel w) {
        // I am using the index from 1 because I want the divider and first wave has divider in first splitter, second in second, ...
        // I need the divider location under the wave that is why it is +1
        return getJSplitPaneContainingWaveFromWaveIndex(w.getWaveIndex()).getDividerLocation();
    }

    private Component clearSplitterComponent(int index) {
        JSplitPane p = getJSplitPaneContainingWaveFromWaveIndex(index);
        Component c;
        if (index == 0) {
            c = p.getTopComponent();
            p.setTopComponent(null);
        }
        else {
            c = p.getBottomComponent();
            p.setBottomComponent(null);
        }
        return c;
    }


    /**
     * @param wave is the wave from which we should look up and find in the panels above there is some panel which satisfies the condition that it is bigger than min size.
     * @return
     */
    private boolean existsPanelBiggerThanMinHeight(WaveMainPanel wave) {
        int arrayIndexOfWaveAbove = wave.getWaveIndex() - 2;
        while (arrayIndexOfWaveAbove >= 0) {
            WaveMainPanel waveAbove = waves.get(arrayIndexOfWaveAbove);
            if (waveAbove.getMinimumSize().height != waveAbove.getPreferredSize().height) {
                return true;
            }

            arrayIndexOfWaveAbove--;
        }

        return false;
    }


    /**
     * Indexing from 0
     *
     * @param index1
     * @param index2
     */
    private void swapComponentsInSplitters(int index1, int index2) {
        Component c1 = clearSplitterComponent(index1);
        Component c2 = clearSplitterComponent(index2);
        setSplitterComponent(index2, c1);
        setSplitterComponent(index1, c2);
    }


    private JSplitPane setSplitterComponent(int index, Component component) {
        JSplitPane splitter;
        if (index == 0) {
            splitter = splitters.get(index);
            splitter.setTopComponent(component);
        }
        else {
            // -1 because the first JSplitPane contains 2 waves, for explanation check panelWithWaves documentation
            splitter = splitters.get(index - 1);
            splitter.setBottomComponent(component);
        }

        return splitter;
    }

    public void swapSplitterComponents(int oldIndex, String oldIndexString, int newIndex) {
        swapSplitterComponents(oldIndex, oldIndexString, newIndex, Integer.toString(newIndex));
    }

    public void swapSplitterComponents(int oldIndex, int newIndex, String newIndexString) {
        swapSplitterComponents(oldIndex, Integer.toString(oldIndex), newIndex, newIndexString);
    }

    public void swapSplitterComponents(int oldIndex, int newIndex) {
        swapSplitterComponents(oldIndex, Integer.toString(oldIndex), newIndex, Integer.toString(newIndex));
    }

    /**
     * Moves wave on index from to index to. Swaps waves on the way. For example If we have waves 1,2,3 and from = 1, to = 3,
     * then the result order will look like this: 2,3,1
     *
     * @param from is the index of wave which we want to move. First wave has index = 0.
     * @param to   is the index of the wave where we want the wave move. First wave has index = 0.
     */
    public void moveSwapSplitter(int from, int to) {
        WaveMainPanel waveMainPanel1;
        WaveMainPanel waveMainPanel2;


        if (from < to) {     // Swapping from up to down
            waveMainPanel1 = waves.get(from);
            from++;
            do {
                waveMainPanel2 = waves.get(from);
                from++;
                // Keep swapping the component which is being dragged with the components between the index from and to.
                swapSplitterComponents(waveMainPanel1.getWaveIndex(), waveMainPanel1.getWaveIndexTextFieldText(),
                                       waveMainPanel2.getWaveIndex(), waveMainPanel2.getWaveIndexTextFieldText());
            } while (from <= to);
        }
        else if (from > to) {    // Swapping from down to up
            waveMainPanel1 = waves.get(from);
            from--;
            do {
                waveMainPanel2 = waves.get(from);
                from--;
                // Keep swapping the component which is being dragged with the components between the index from and to.
                swapSplitterComponents(waveMainPanel1.getWaveIndex(), waveMainPanel1.getWaveIndexTextFieldText(),
                                       waveMainPanel2.getWaveIndex(), waveMainPanel2.getWaveIndexTextFieldText());
            } while (from >= to);
        }
    }

    /**
     * Used for swaping. Used in logic: index1 is from waveMainPanel1 and index2 from awp2
     *
     * @param index1
     * @param index1String
     * @param index1FromZero
     * @param index2
     * @param index2String
     * @param index2FromZero
     * @param waveMainPanel1
     * @param waveMainPanel2
     */
    private void swap2WavesIndexes(int index1, String index1String, int index1FromZero,
                                   int index2, String index2String, int index2FromZero,
                                   WaveMainPanel waveMainPanel1, WaveMainPanel waveMainPanel2) {
        waves.set(index1FromZero, waveMainPanel2);
        waveMainPanel2.setWaveIndex(index1);
        waveMainPanel2.setWaveIndexTextField(index1String);

        waves.set(index2FromZero, waveMainPanel1);
        waveMainPanel1.setWaveIndex(index2);
        waveMainPanel1.setWaveIndexTextField(index2String);
    }


    public void tryMoveSwap(WaveMainPanel waveMainPanel, MouseEvent e) {
        WaveMainPanel wave;
        int y = waveMainPanel.getY();
        int mouseY = e.getY();
        mouseY += y;
        int from = waveMainPanel.getWaveIndex();        // Index is from 1
        from--;                                 // Now indexed from 0
        // If it isn't the last wave and if it is at least as low or lower as the start of the wave below
        // (Moving down)
        if (from < waves.size() - 1 && mouseY >= waves.get(from + 1).getY()) {
            int to;
            for (to = from + 1; to < waves.size(); to++) {       // Check how much below it is
                wave = waves.get(to);
                if (mouseY < wave.getY()) {  // The wave before is the wave with which we should swap
                    break;
                }
            }
            to--;

            moveSwapSplitter(from, to);
        }
        // If it isn't the first wave and if it is at least as high or higher than the start of the wave above
        // (Moving up)
        else if (from > 0 && mouseY <= waves.get(from - 1).getY()) {
            int to;
            for (to = from - 1; to >= 0; to--) {     // Check how much above it is
                wave = waves.get(to);
                if (mouseY > wave.getY()) {  // The wave after is the wave with which we should swap
                    break;
                }
            }
            to++;

            moveSwapSplitter(from, to);
        }
    }


    // We have to solve the following problem:
    // by nesting JSplitPane the borders are getting bigger with each nesting
    // So I set the borders of jSplitPane to empty left borders,
    // and also set divider border to null to remove small border above waves
    // source:
    // https://stackoverflow.com/questions/12799640/why-does-jsplitpane-add-a-border-to-my-components-and-how-do-i-stop-it
    private void flattenJSplitPane(JSplitPane splitter) {
        SplitPaneUI ui;
        ui = splitter.getUI();
        if (ui instanceof BasicSplitPaneUI) {
            ((BasicSplitPaneUI) ui).getDivider().setBorder(null);
        }
        splitter.setBorder(BorderFactory.createEmptyBorder());
    }
// Different solution from same page - but the comment said, that this may not work good on different platforms
//    /**
//     * Makes a split pane invisible. Only contained components are shown.
//     *
//     * @param splitPane
//     */
//    public static void flattenJSplitPane(JSplitPane splitPane) {
//        splitPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
//        BasicSplitPaneUI flatDividerSplitPaneUI = new BasicSplitPaneUI() {
//            @Override
//            public BasicSplitPaneDivider createDefaultDivider() {
//                return new BasicSplitPaneDivider(this) {
//                    @Override
//                    public void setBorder(Border b) {
//                    }
//                };
//            }
//        };
//        splitPane.setUI(flatDividerSplitPaneUI);
//        splitPane.setBorder(null);
//    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addAddEmptyWaveToWaves(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("Add empty wave(s)");
        menuItem.setToolTipText("Adds new wave of given length with sample values == 0 to the player.");

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EmptyWaveMakerDialog emptyWaveMakerDialog = new EmptyWaveMakerDialog();
                int result = JOptionPane.showConfirmDialog(null, emptyWaveMakerDialog,
                                                           "Choose empty wave dialog", JOptionPane.OK_CANCEL_OPTION,
                                                           JOptionPane.PLAIN_MESSAGE);


                if (result == JOptionPane.OK_OPTION) {
                    stopAndModifyAudio(true, new ModifyAudioIFace() {
                        @Override
                        public void modifyAudio() {
                            addEmptyWaves(emptyWaveMakerDialog.getLength(), emptyWaveMakerDialog.getNumberOfWaves());
                        }
                    }, true, false);
                }
            }
        });

        menu.add(menuItem);
    }

    private void addAddMonoFileToWaves(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("Add mono wave");
        menuItem.setToolTipText("Converts the wave in file to mono and adds it to the waves in audio player.");

        JFileChooser fileChooser = new JFileChooser();
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(thisFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    stopAndModifyAudio(true, new ModifyAudioIFace() {
                        @Override
                        public void modifyAudio() {
                            addMonoWave(f);
                        }
                    }, true, false);
                }
            }
        });

        menu.add(menuItem);
    }

    private void addAddFileToWaves(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("Add waves");
        menuItem.setToolTipText("Converts the wave in file to mono and adds it to the waves in audio player.");

        JFileChooser fileChooser = new JFileChooser();
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(thisFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    stopAndModifyAudio(true, new ModifyAudioIFace() {
                        @Override
                        public void modifyAudio() {
                            try {
                                addWaves(f, false);
                            }
                            catch (IOException exception) {
                                MyLogger.logException(exception);
                            }
                        }
                    }, true, false);
                }
            }
        });

        menu.add(menuItem);
    }


    private void addOpenEmptyFileToWaves(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("Open empty wave");
        menuItem.setToolTipText("Removes all current waves and adds empty wave (sample values == 0) of given length");

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EmptyWaveMakerDialog emptyWaveMakerDialog = new EmptyWaveMakerDialog();
                int result = JOptionPane.showConfirmDialog(null, emptyWaveMakerDialog,
                                                           "Choose empty wave dialog", JOptionPane.OK_CANCEL_OPTION,
                                                           JOptionPane.PLAIN_MESSAGE);


                if (result == JOptionPane.OK_OPTION) {
                    stopAndModifyAudio(true, new ModifyAudioIFace() {
                        @Override
                        public void modifyAudio() {
                            removeAllWaves();
                            addEmptyWaves(emptyWaveMakerDialog.getLength(), emptyWaveMakerDialog.getNumberOfWaves());
                        }
                    }, false, true);
                }
            }
        });


        menu.add(menuItem);
    }

    private void addEmptyWaves(int lengthFromDialog, int numberOfWavesFromDialog) {
        if (lengthFromDialog <= 0) {
            return;
        }
        int oldWavesLen = getDoubleWaveLength();
        int emptyWaveLen = lengthFromDialog * (int) outputAudioFormat.getSampleRate();
        if (oldWavesLen >= emptyWaveLen) {
            emptyWaveLen = oldWavesLen;
        }

        for (int i = 0; i < numberOfWavesFromDialog; i++) {
            DoubleWave doubleWave = new DoubleWave(new double[emptyWaveLen], (int) outputAudioFormat.getSampleRate(),
                                                   1, "Empty wave",
                                                   false);
            addWave(doubleWave);
        }
    }

    private void addOpenMonoFileToMenu(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("Open file mono");
        menuItem.setToolTipText("Removes all current waves, converts to file to mono and puts that wave as only wave in player.");

        JFileChooser fileChooser = new JFileChooser();
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(thisFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    stopAndModifyAudio(true, new ModifyAudioIFace() {
                        @Override
                        public void modifyAudio() {
                            removeAllWaves();
                            addMonoWave(f);
                        }
                    }, false, true);
                }
            }
        });

        menu.add(menuItem);
    }


    /**
     * @param f
     * @return Returns true if the wave was correctly added, false otherwise.
     */
    private boolean addMonoWave(File f) {
        DoubleWave wave = loadMonoDoubleWave(f, getOutputSampleRate(), true);
        if (wave != null) {
            addWave(wave);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Loads audio file and converts it to mono if it already isn't and also converts it to correct sample rate.
     *
     * @param f
     * @param newSampleRate if < 0 then no conversion is performed
     * @param shouldLog
     * @return
     */
    public static DoubleWave loadMonoDoubleWave(File f, int newSampleRate, boolean shouldLog) {
        DoubleWave wave = null;
        try {
            ByteWave byteWave = ByteWave.loadSong(f, true);
            if (byteWave == null) {
                if (shouldLog) {
                    MyLogger.logWithoutIndentation("Couldn't load audio in addMonoWave(File f) method.\n" +
                                                   AudioUtilities.LOG_MESSAGE_WHEN_SET_VARIABLES_RETURN_FALSE);
                }
                return null;
            }
            byteWave.convertToMono();
            wave = new DoubleWave(byteWave, false, newSampleRate);
        }
        catch (IOException exception) {
            MyLogger.logException(exception);
        }

        return wave;
    }


    private void addOpenFileToMenu(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("Open file");
        menuItem.setToolTipText("Removes all current waves and puts channels of the file as the new waves.");

        JFileChooser fileChooser = new JFileChooser();
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(thisFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    stopAndModifyAudio(true, new ModifyAudioIFace() {
                        @Override
                        public void modifyAudio() {
                            try {
                                removeAllWaves();
                                addWaves(f, false);
                            }
                            catch (IOException exception) {
                                MyLogger.logException(exception);
                            }
                        }
                    }, false, true);
                }
            }
        });

        menu.add(menuItem);
    }

    private void addWaves(File f, boolean shouldAddLater) throws IOException {
        ByteWave byteWave = ByteWave.loadSong(f, false);
        if (byteWave == null) {
            MyLogger.logWithoutIndentation("Couldn't load audio in addWaves(File f) method.\n" +
                                           AudioUtilities.LOG_MESSAGE_WHEN_SET_VARIABLES_RETURN_FALSE);
            return;
        }

        double[][] waves = byteWave.separateChannelsDouble();
        for (int i = 0; i < waves.length; i++) {
            waves[i] = AudioConverter.convertSampleRate(waves[i], byteWave.getNumberOfChannels(),
                                                        byteWave.getSampleRate(), getOutputSampleRate(), true);
        }
        addWaves(waves, byteWave.getFileName(), getOutputSampleRate(), shouldAddLater);
    }

    /**
     * @param waves
     * @param filename
     * @param sampleRate
     * @param shouldAddLater if true, then it is put to list and the waves will be put to player next time the tab is clicked
     */
    private void addWaves(double[][] waves, String filename, int sampleRate, boolean shouldAddLater) {
        DoubleWave[] doubleWaves = new DoubleWave[waves.length];
        filename = Utilities.getNameWithoutExtension(filename);
        for (int i = 0; i < doubleWaves.length; i++) {
            String channelFilename = filename + "_" + i;
            doubleWaves[i] = new DoubleWave(waves[i], sampleRate, 1,
                                            channelFilename, false);
        }

        if (shouldAddLater) {
            for (DoubleWave d : doubleWaves) {
                wavesToAddLater.add(d);
            }
        }
        else {
            addWaves(doubleWaves);
        }
    }


    private List<DoubleWave> wavesToAddLater = new ArrayList<>();

    /**
     * Works in such way that it is saved but added to the player after the audio player is shown
     *
     * @param audio
     * @param format
     * @param audioLen
     */
    public void addWaves(InputStream audio, int audioLen, AudioFormatWithSign format, boolean shouldConvertSampleRate) {
        double[][] waves;
        try {
            waves = AudioConverter.separateChannelsDouble(audio, format.getChannels(), format.getSampleSizeInBits() / 8,
                                                          format.isBigEndian(), format.isSigned, audioLen);
            if (shouldConvertSampleRate) {
                int outputSampleRate = getOutputSampleRate();
                for (int i = 0; i < waves.length; i++) {
                    waves[i] = AudioConverter.convertSampleRate(waves[i], 1, (int) format.getSampleRate(),
                                                                outputSampleRate, true);
                }

                addWaves(waves, "", outputSampleRate, true);
            }
            else {
                addWaves(waves, "", getOutputSampleRate(), true);
            }
        }
        catch (IOException e) {
            MyLogger.logException(e);
        }
    }

    public void addWaves(String path, boolean shouldAddLater) {
        try {
            File f = new File(path);
            if (f.exists()) {
                addWaves(f, shouldAddLater);
            }
        }
        catch (IOException e) {
            MyLogger.logException(e);
        }
    }

    private void postWaveAdditionAction() {
        updateWavesForMixing();
    }


    private void addSetWaveLengths(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("Set wave lengths");
        menuItem.setToolTipText("<html>" +
                                "Sets the length of all waves to given length." +
                                "</html>");


        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LengthDialog lengthDialog = new LengthDialog();
                int result = JOptionPane.showConfirmDialog(null, lengthDialog,
                                                           "Choose length for waves dialog", JOptionPane.OK_CANCEL_OPTION,
                                                           JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    stopAndModifyAudio(true, new ModifyAudioIFace() {
                        @Override
                        public void modifyAudio() {
                            int newLengthInSamples = lengthDialog.getLength() * (int) outputAudioFormat.getSampleRate();
                            deletePart(newLengthInSamples);
                        }
                    }, false, true);
                }
            }
        });

        menu.add(menuItem);
    }


    private void addRemoveAllWaves(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("Remove all waves");
        menuItem.setToolTipText("Removes all loaded waves");

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopAndModifyAudio(true, new ModifyAudioIFace() {
                    @Override
                    public void modifyAudio() {
                        removeAllWaves();
                    }
                }, false, true);
            }
        });


        menu.add(menuItem);
    }


    private void addChangeOutputFormatToMenu(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("Change output audioFormat");

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopAndModifyAudio(true, new ModifyAudioIFace() {
                    @Override
                    public void modifyAudio() {
                        AudioFormatJPanelWithConvertFlag p = new AudioFormatJPanelWithConvertFlag(outputAudioFormat);
                        int result = JOptionPane.showConfirmDialog(null, p,
                                                                   "Audio audioFormat chooser", JOptionPane.OK_CANCEL_OPTION,
                                                                   JOptionPane.PLAIN_MESSAGE);
                        if (result == JOptionPane.OK_OPTION) {
                            setOutputAudioFormat(p.getFormat().createJavaAudioFormat(true), p.getShouldConvert());
                        }
                    }
                }, false, false);
            }
        });
        menu.add(menuItem);
    }


    /**
     * @param file is the default file for the JFileChoser.
     *             If it is null then the name will be "audio" and the directory will be current directory of the JFileChooser.
     * @return
     */
    public static JFileChooser getFileChooserForSaving(File file) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
        AudioFileFormat.Type audioType = AudioFileFormat.Type.WAVE;

        // No need to check with if it is available - it should be available totally everywhere
        FileFilterAudioFormats wavFileFilter = new FileFilterAudioFormats(audioType);
        fileChooser.addChoosableFileFilter(wavFileFilter);

        audioType = AudioFileFormat.Type.AIFC;
        if (AudioSystem.isFileTypeSupported(audioType)) {
            fileChooser.addChoosableFileFilter(new FileFilterAudioFormats(audioType));
        }

        audioType = AudioFileFormat.Type.AIFF;
        if (AudioSystem.isFileTypeSupported(audioType)) {
            fileChooser.addChoosableFileFilter(new FileFilterAudioFormats(audioType));
        }

        audioType = AudioFileFormat.Type.AU;
        if (AudioSystem.isFileTypeSupported(audioType)) {
            fileChooser.addChoosableFileFilter(new FileFilterAudioFormats(audioType));
        }

        audioType = AudioFileFormat.Type.SND;
        if (AudioSystem.isFileTypeSupported(audioType)) {
            fileChooser.addChoosableFileFilter(new FileFilterAudioFormats(audioType));
        }

        // Set default filter and default name
        fileChooser.setFileFilter(wavFileFilter);
        if (file == null) {
            file = new File(fileChooser.getCurrentDirectory() + "/audio");
        }
        fileChooser.setSelectedFile(file);
        return fileChooser;
    }

    private void addSaveFileToMenu(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("Save file");
        JFileChooser fileChooser = getFileChooserForSaving(null);

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showSaveDialog(thisFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    FileFilterAudioFormats filter = (FileFilterAudioFormats) fileChooser.getFileFilter();

                    byte[] outputWave = getOutputWaveBytes();
                    AudioWriter.saveAudio(f.getAbsolutePath(), outputAudioFormat, outputWave, filter.AUDIO_TYPE);
                }
            }
        });

        menu.add(menuItem);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addTotallyRemoveAudioPart(JMenu menu) {
        JMenuItem menuItem = new JMenuItem("Delete song part");
        menuItem.setToolTipText("<html>" +
                                "Deletes song parts - from all waves even those not marked in operation.<br>" +
                                "Delete in a sense that the part after the deleted part is moved to the left, to start of the removed mark part.<br>" +
                                "If there is no mark part (respectively whole wave is marked) it is equivalent to removing all waves" +
                                "</html>");

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performWaveLengthChangingAction(() -> deleteMarkedPart());
            }
        });

        menu.add(menuItem);
    }

    private void performWaveLengthChangingAction(ModifyAudioIFace action) {
        stopAndModifyAudio(true, action, false, true);
        postProcessingAfterChangingWaveLength();
    }

    private void postProcessingAfterChangingWaveLength() {
        shouldMarkPart = false;
        scrollToStart();
        audioThread.wavesLengthChanged();
    }

    private void scrollToStart() {
        // What we do is first move the scroll to start so there won't be any issue with invalid scroll location and
        // then fake the zoom to fix size inconsistencies.
        // Otherwise there would be issue with the drawing of wave,
        // it would be drawn twice, but even after this, it would still be drawn incorrectly (the wave would be moved to right)


        waveScrollerWrapperPanel.scrollToStart();
        // To fix the wave being moved to right
        fakeZoomUpdate();
        revalidate();
        repaint();
    }


    private void deleteMarkedPart() {
        if (shouldMarkPart) {
            int startIndex = getMarkStartXSample();
            int endIndex = getMarkEndXSample();
            deletePart(startIndex, endIndex);
        }
        else {
            removeAllWaves();
        }
    }

    private void deletePart(int startIndex, int endIndex) {
        if (startIndex == 0 && endIndex == getSongLengthInSamples()) {
            removeAllWaves();
        }
        else {
            for (WaveMainPanel w : waves) {
                if (clipboard.isEqualToClipboardWavePanel(w)) {
                    int clipboardStartIndex = clipboard.getMarkStartSample();
                    int clipboardEndIndex = clipboard.getMarkEndSample();

                    // Note when comparing start index and clipboard end index is the only place where should be < or >
                    // | is the removed part, ( and ) is clipboard
                    if (startIndex <= clipboardStartIndex && endIndex >= clipboardEndIndex) {      // | ( ) |
                        clipboard.removeWaveFromClipboard();
                    }
                    else if (startIndex >= clipboardStartIndex && startIndex < clipboardEndIndex &&
                             endIndex >= clipboardEndIndex) {                             // ( | ) |
                        clipboard.setMarkEndSample(startIndex);
                    }
                    else if (startIndex <= clipboardStartIndex && endIndex >= clipboardStartIndex) {       // | ( | )
                        clipboard.setMarkStartSample(startIndex);
                        // But I also have to move the end sample
                        clipboard.setMarkEndSample(clipboardEndIndex - (endIndex - startIndex));
                    }
                    else if (startIndex >= clipboardStartIndex && endIndex <= clipboardEndIndex) {          // ( | | )
                        clipboard.setMarkEndSample(startIndex + (clipboardEndIndex - endIndex));
                    }
                    // Else don't do anything clipboard is outside the removed part
                }
                w.removeWavePart(startIndex, endIndex);
            }
        }

        audioThread.wavesLengthChanged();
    }

    private void deletePart(int startIndex) {
        int endIndex = getSongLengthInSamples();
        deletePart(startIndex, endIndex);
    }


    private void addDrawWindowsOperations(JMenu menu) {
        JMenuItem menuItem = null;
        for (final DRAW_PANEL_TYPES DRAW_TYPE : DRAW_PANEL_TYPES.values()) {
            switch (DRAW_TYPE) {
                case TIME:
                    menuItem = new JMenuItem("Draw wave window");
                    menuItem.setToolTipText("Creates draw wave window, where it is possible to draw wave");
                    break;
                case FFT_MEASURES:
                    menuItem = new JMenuItem("FFT window measures");
                    menuItem.setToolTipText("Creates fft window with measures");
                    break;
                case FFT_MEASURES_VIEW_ONLY:
                    menuItem = new JMenuItem("FFT window measures (view only)");
                    menuItem.setToolTipText("Creates fft window with measures. Doesn't allow editing");
                    break;
                case FFT_COMPLEX:
                    menuItem = new JMenuItem("FFT window complex");
                    menuItem.setToolTipText("Creates fft window with both real and imaginary part result of FFT");
                    break;
                case FFT_COMPLEX_VIEW_ONLY:
                    menuItem = new JMenuItem("FFT window complex (view only)");
                    menuItem.setToolTipText("Creates fft window with both real and imaginary part result of FFT. " +
                                            "Doesn't allow editing");
                    break;
                case WAVESHAPER:
                    menuItem = new JMenuItem("Waveshaper");
                    menuItem.setToolTipText("Creates waveshaper window which will be used on the marked wave parts.");
                    break;
            }


            menuItem.addActionListener(createDrawWindowActionListener(DRAW_TYPE));

            menu.add(menuItem);
        }
    }


    public enum DRAW_PANEL_TYPES {
        TIME,
        FFT_MEASURES,
        FFT_MEASURES_VIEW_ONLY,
        FFT_COMPLEX,
        FFT_COMPLEX_VIEW_ONLY,
        WAVESHAPER;

        public boolean isViewOnly() {
            return this == DRAW_PANEL_TYPES.FFT_MEASURES_VIEW_ONLY || this == DRAW_PANEL_TYPES.FFT_COMPLEX_VIEW_ONLY;
        }
    }

    private ActionListener createDrawWindowActionListener(final DRAW_PANEL_TYPES DRAW_TYPE) {
        AudioPlayerPanel thisAudioPlayerClass = this;
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int markLen = getMarkEndXSample() - getMarkStartXSample();
                double[] wave = null;
                if (getShouldMarkPart()) {
                    for (WaveMainPanel w : waves) {
                        if (w.getShouldIncludeInOperations()) {
                            wave = w.getDoubleWave().getSong();
                            break;
                        }
                    }
                }


                JFrame f = createDrawFrame(DRAW_TYPE, getOutputSampleRate(), thisAudioPlayerClass,
                                           wave, getMarkStartXSample(), markLen);
                if (f != null) {
                    f.setVisible(true);
                }
            }
        };
    }


    /**
     * @param DRAW_TYPE
     * @param sampleRate is only needed for the FFT draw panels.
     * @param waveAdder  is needed everywhere except waveshaper.
     * @param inputArr   is the array which will be used as input for the draw panel, can be null.
     *                   Also used only for the FFT draw panels.
     * @param startIndex useful only when inputArr is non-null. If < 0 then set to 0. Also used only for the FFT draw panels.
     * @param windowSize useful only when inputArr is non-null. If <= 0 then set to 1024. Also used only for the FFT draw panels.
     * @return
     */
    public static DrawJFrame createDrawFrame(final DRAW_PANEL_TYPES DRAW_TYPE, int sampleRate, WaveAdderIFace waveAdder,
                                             double[] inputArr, int startIndex, int windowSize) {
        JPanel drawPanel;
        if (inputArr == null) {
            startIndex = 0;
            windowSize = 1024;
        }
        else {
            startIndex = Math.max(startIndex, 0);
            windowSize = Math.max(windowSize, FFTWindowPanel.MIN_WINDOW_SIZE);
            windowSize = Math.min(windowSize, FFTWindowPanel.MAX_WINDOW_SIZE);
        }

        boolean isViewOnly = DRAW_TYPE.isViewOnly();
        boolean isEditable = !isViewOnly;
        if (isViewOnly && inputArr == null) {
            return null;
        }

        switch (DRAW_TYPE) {
            case TIME:
                drawPanel = TimeWaveDrawWrapper.createMaxSizeTimeWaveDrawWrapper(500,
                                                                                 true, Color.LIGHT_GRAY, true);
                break;
            case FFT_MEASURES:
            case FFT_MEASURES_VIEW_ONLY:
                drawPanel = new FFTWindowWrapper(inputArr, windowSize, startIndex,
                                                 sampleRate, isEditable, Color.LIGHT_GRAY,
                                                 0, 1, true);
                break;
            case FFT_COMPLEX:
            case FFT_COMPLEX_VIEW_ONLY:
                drawPanel = new FFTWindowRealAndImagWrapper(inputArr, windowSize,
                                                            startIndex, sampleRate, isEditable,
                                                            Color.LIGHT_GRAY, Color.LIGHT_GRAY, true);
                break;
            case WAVESHAPER:
                drawPanel = WaveShaperPanel.createMaxSizeWaveShaper(Color.LIGHT_GRAY,
                                                                    -1, 1, true);
                break;
            default:
                drawPanel = null;
                break;
        }


        DrawJFrame f;
        String pluginName;
        switch (DRAW_TYPE) {
            case FFT_MEASURES:
            case FFT_MEASURES_VIEW_ONLY:
            case FFT_COMPLEX:
            case FFT_COMPLEX_VIEW_ONLY:
                if (DRAW_TYPE == DRAW_PANEL_TYPES.FFT_MEASURES || DRAW_TYPE == DRAW_PANEL_TYPES.FFT_MEASURES_VIEW_ONLY) {
                    pluginName = "FFT Measures";
                }
                else {
                    pluginName = "FFT complex numbers";
                }
                f = new DrawJFrame(drawPanel, pluginName) {
                    private Dimension minSize = new Dimension();

                    @Override
                    public Dimension getMinimumSize() {
                        Insets insets = getInsets();
                        minSize.width = drawPanel.getMinimumSize().width + insets.left + insets.right;
                        // https://stackoverflow.com/questions/10123735/get-effective-screen-size-from-java
                        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                        Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
                        int taskBarSize = scnMax.bottom;
                        minSize.height = screenSize.height - taskBarSize;
                        return minSize;
                    }
                };

                break;
            case TIME:
            case WAVESHAPER:
                if (DRAW_TYPE == DRAW_PANEL_TYPES.TIME) {
                    pluginName = "Wave drawing";
                }
                else {
                    pluginName = "Waveshaper";
                }
                f = new DrawJFrame(drawPanel, pluginName) {
                    private Dimension minSize = new Dimension();

                    @Override
                    public Dimension getMinimumSize() {
                        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                        minSize.width = screenSize.width;
                        // https://stackoverflow.com/questions/10123735/get-effective-screen-size-from-java
                        Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
                        int taskBarSize = scnMax.bottom;
                        minSize.height = screenSize.height - taskBarSize;
                        return minSize;
                    }
                };
                break;
            default:
                f = null;
                break;
        }

        f.setMinimumSize(new Dimension());
        f.setLayout(new FlowLayout());
        f.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                drawPanel.revalidate();
                drawPanel.repaint();

                if (f.getSize().width < f.getMinimumSize().width) {
                    f.setMinimumSize(new Dimension());
                    f.pack();
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                // EMPTY
            }

            @Override
            public void componentShown(ComponentEvent e) {
                // EMPTY
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // EMPTY
            }
        });


        f.add(drawPanel);
        JMenuBar menuBar = new JMenuBar();
        ((DrawWrapperIFace) drawPanel).addMenus(menuBar, waveAdder);
        f.setJMenuBar(menuBar);
        f.pack();       // Have to be called otherwise, min size is ignored
        f.setResizable(false);
        f.setLocation(-1, -1);
        return f;
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addAudioOperationsWithoutWave(JMenu menu) {
        OperationOnWavePluginIFace op;


        op = new MultiplicationOnWave();
        addAudioOperation(op, menu);

        op = new AdditionOnWave();
        addAudioOperation(op, menu);

        op = new LogarithmOnWave();
        addAudioOperation(op, menu);

        op = new PowerOnWave();
        addAudioOperation(op, menu);


        op = new InvertOnWave();
        addAudioOperation(op, menu);

        op = new WaveStretcherOnWave();
        addAudioOperation(op, menu);

        op = new WaveStretcherMaximumOnWave();
        addAudioOperation(op, menu);


        op = new SetSamplesOnWaveOperation();
        addAudioOperation(op, menu);

        op = new SetSamplesToZeroOnWaveOperation();
        addAudioOperation(op, menu);
    }

    private void addAudioOperation(OperationOnWavePluginIFace operation, JMenu menu) {
        JMenuItem menuItem = new JMenuItem(operation.getPluginName());
        menuItem.setToolTipText(operation.getPluginTooltip());

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean canContinueOperation = loadPluginParameters(operation, true);
                if (canContinueOperation) {
                    stopAndModifyAudio(false, new ModifyAudioIFace() {
                        @Override
                        public void modifyAudio() {
                            performOperationInternal(operation);
                        }
                    }, true, false);
                }
            }
        });

        menu.add(menuItem);
    }


    private JMenuBar menuBar;

    @Override
    public void changedTabAction(boolean hasFocus) {
        if (hasFocus) {
            thisFrame.setJMenuBar(menuBar);
            WavePanelMouseListener.startTooltipTimer();
        }
        else {
            clickPauseButtonIfPlaying();
            audioThread.reset();
            WavePanelMouseListener.stopTooltipTimer();
        }
    }

    private List<JMenuItem> withInputWaveMenuItems = new ArrayList<>();

    private void setEnabledWithWaveMenuItems(boolean enable) {
        for (JMenuItem item : withInputWaveMenuItems) {
            item.setEnabled(enable);
        }
    }

    // Hot to get menu items : https://stackoverflow.com/questions/24850424/get-jmenuitems-from-jmenubar
    private void setEnabledAllMenus(boolean enable) {
        JMenuBar bar = this.thisFrame.getJMenuBar();
        for (int i = 2; i < bar.getMenuCount() - 1; i++) {
            JMenu menu = bar.getMenu(i);
            menu.setEnabled(enable);
        }
    }


    private void addAudioOperationsWithWave(JMenu menu) {
        OperationOnWavesPluginIFace op;


        op = new FillWaveWithOtherWaveOperation();
        addAudioOperation(op, menu);


        op = new MultiplicationOnWaves();
        addAudioOperation(op, menu);

        op = new AdditionOnWaves();
        addAudioOperation(op, menu);

        op = new LogarithmOnWaves();
        addAudioOperation(op, menu);

        op = new PowerOnWaves();
        addAudioOperation(op, menu);
    }


    private AlignmentOnWavesOperation alignPanel = new AlignmentOnWavesOperation();


    private void addAudioOperation(OperationOnWavesPluginIFace operation, JMenu menu) {
        JMenuItem menuItem = new JMenuItem(operation.getPluginName());
        withInputWaveMenuItems.add(menuItem);
        menuItem.setToolTipText(operation.getPluginTooltip());

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alignPanel.resetAlignment();
                boolean canContinueOperation = loadPluginParameters(alignPanel, true);
                if (!canContinueOperation) {
                    return;
                }

                canContinueOperation = loadPluginParameters(operation, true);
                if (canContinueOperation) {
                    stopAndModifyAudio(false, new ModifyAudioIFace() {
                        @Override
                        public void modifyAudio() {
                            performOperationInternal(operation);
                        }
                    }, true, false);
                }
            }
        });

        menu.add(menuItem);
    }


    private void addFilters(JMenu menu) {
        OperationOnWavePluginIFace op = new LowPassFilter();
        addAudioOperation(op, menu);
    }


    private void addPlugins(JMenu menu) {
        addTwoInputWavesPlugins(menu);
        menu.addSeparator();
        addSingleInputWavePlugins(menu);
    }

    private void addTwoInputWavesPlugins(JMenu menu) {
        List<OperationOnWavesPluginIFace> plugins = OperationOnWavesPluginIFace.loadPlugins();
        for (OperationOnWavesPluginIFace plugin : plugins) {
            addPlugin(plugin, menu);
        }
    }

    private void addSingleInputWavePlugins(JMenu menu) {
        List<OperationOnWavePluginIFace> plugins = OperationOnWavePluginIFace.loadPlugins();
        for (OperationOnWavePluginIFace plugin : plugins) {
            addPlugin(plugin, menu);
        }
    }


    // These addPlugin methods are basically copy pasted.
    // I have to do that because they work with different methods
    // and to get the correct Class I have to call getClass on the final class.
    private void addPlugin(OperationOnWavePluginIFace pluginToAdd, JMenu menu) {
        JMenuItem menuItem = new JMenuItem(pluginToAdd.getPluginName());
        menuItem.setToolTipText(pluginToAdd.getPluginTooltip());

        menuItem.addActionListener(new ActionListener() {
            OperationOnWavePluginIFace plugin = pluginToAdd;

            @Override
            public void actionPerformed(ActionEvent e) {
                // To reset the plugin
                Class<?> clazz = plugin.getClass();
                try {
                    Constructor<?> constructor = clazz.getConstructor();
                    // This is already checked when creating the first instance (pluginToAdd),
                    // only way this would fail would be when the source code changes during runtime
                    if (constructor == null) {
                        MyLogger.log("Error in action listener inside " +
                                     "addPlugin(OperationOnWavePluginIFace pluginToAdd, JMenu menu): " +
                                     pluginToAdd.getPluginName() + "\t(Doesn't have constructor without parameters)",
                                     0);
                        return;
                    }
                    plugin = (OperationOnWavePluginIFace) clazz.newInstance();
                }
                catch (Exception exception) {
                    MyLogger.logException(exception);
                    return;
                }
                // To reset the plugin

                boolean canContinueOperation = loadPluginParameters(plugin, true);
                if (canContinueOperation) {
                    stopAndModifyAudio(false, new ModifyAudioIFace() {
                        @Override
                        public void modifyAudio() {
                            performOperationInternal(plugin);
                        }
                    }, true, false);
                }
            }
        });

        menu.add(menuItem);
    }

    private void addPlugin(OperationOnWavesPluginIFace pluginToAdd, JMenu menu) {
        JMenuItem menuItem = new JMenuItem(pluginToAdd.getPluginName());
        withInputWaveMenuItems.add(menuItem);
        menuItem.setToolTipText(pluginToAdd.getPluginTooltip());

        menuItem.addActionListener(new ActionListener() {
            OperationOnWavesPluginIFace plugin = pluginToAdd;

            @Override
            public void actionPerformed(ActionEvent e) {
                // To reset the plugin
                Class<?> clazz = plugin.getClass();
                try {
                    Constructor<?> constructor = clazz.getConstructor();
                    // This is already checked when creating the first instance (pluginToAdd), only way this would fail would be when the source codes changes during runtime
                    if (constructor == null) {
                        MyLogger.log("Error in action listener inside " +
                                     "addPlugin(OperationOnWavesPluginIFace pluginToAdd, JMenu menu): " +
                                     pluginToAdd.getPluginName() + "\t(Doesn't have constructor without parameters)",
                                     0);
                        return;
                    }
                    plugin = (OperationOnWavesPluginIFace) clazz.newInstance();
                }
                catch (Exception exception) {
                    MyLogger.logException(exception);
                    return;
                }
                // To reset the plugin


                alignPanel.resetAlignment();
                boolean canContinueOperation = loadPluginParameters(alignPanel, true);
                if (!canContinueOperation) {
                    return;
                }
                canContinueOperation = loadPluginParameters(plugin, true);
                if (canContinueOperation) {
                    stopAndModifyAudio(false, new ModifyAudioIFace() {
                        @Override
                        public void modifyAudio() {
                            performOperationInternal(plugin);
                        }
                    }, true, false);
                }
            }
        });

        menu.add(menuItem);
    }

    /**
     * Returns true if operation should be started
     *
     * @param plugin
     * @return
     */
    public static boolean loadPluginParameters(PluginBaseIFace plugin, boolean containsCancelOption) {
        boolean canContinueOperation;

        if (plugin.shouldWaitForParametersFromUser()) {
            Object panelInDialog;
            if (plugin.isUsingPanelCreatedFromAnnotations()) {
                AnnotationPanel ap = AnnotationPanel.createAnnotationPanel(plugin, plugin.getClass());
                if(ap == null) {
                    return true;
                }
                panelInDialog = ap;
            }
            else {
                panelInDialog = plugin;
            }

            if (panelInDialog == plugin && plugin instanceof JFileChooser) {
                int result = ((JFileChooser) plugin).showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    canContinueOperation = true;
                }
                else {
                    canContinueOperation = false;
                }
            }
            else if (plugin instanceof JFrame) {
                // Now the user has to do everything in the frame by himself. Frames aren't currently easily pluginable.
                // WE are not making dialog from the frame.
                //
                // When using frame plugin, then the programmer have look at how does the code work,
                // since you will usually have to call
                // special method to perform operation etc. For example stopAndModifyAudio when adding plugin to player or
                // updateAfterPropertiesCall when using the plugin in properties inside synth part.
                // Or in the second case just take care of the update inside the frame methods.
                //
                // I don't see any simple way how to make dialog from JFrame, especially when I am using the size
                // of frame inside the panel. I will repair it later maybe, but currently I don't have that much time
                // and I just don't see how to do it

                ((JFrame) plugin).setVisible(true);
                canContinueOperation = false;
            }
            else {
                int result;
                if (containsCancelOption) {
                    result = JOptionPane.showConfirmDialog(null, panelInDialog,
                                                           "Dialog: " + plugin.getPluginName(),
                                                           JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                }
                else {
                    result = JOptionPane.showConfirmDialog(null, panelInDialog,
                                                           "Dialog: " + plugin.getPluginName(),
                                                           JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
                }
                if (result == JOptionPane.OK_OPTION) {
                    canContinueOperation = true;
                }
                else {
                    canContinueOperation = false;
                }
            }
        }
        else {
            canContinueOperation = true;
        }

        return canContinueOperation;
    }


    private void performOperationInternal(OperationOnWavesPluginIFace operation) {
        for (WaveMainPanel waveMainPanel : waves) {
            if (waveMainPanel.getShouldIncludeInOperations()) {
                DoubleWave doubleWave = waveMainPanel.getDoubleWave();
                if (doubleWave == clipboard.getWave()) {
                    continue;
                }

                if (shouldMarkPart) {
                    alignPanel.performOperation(clipboard.getWave(), doubleWave,
                                                clipboard.getMarkStartSample(), clipboard.getMarkEndSample(),
                                                getMarkStartXSample(), getMarkEndXSample());

                    operation.performOperation(clipboard.getWave(), doubleWave,
                                               clipboard.getMarkStartSample(), alignPanel.getInputWaveEndIndex(),
                                               getMarkStartXSample(), alignPanel.getOutputWaveEndIndex());
                }
                else {
                    alignPanel.performOperation(clipboard.getWave(), doubleWave,
                                                clipboard.getMarkStartSample(), clipboard.getMarkEndSample(),
                                                getMarkStartXSample(), getMarkEndXSample());

                    operation.performOperation(clipboard.getWave(), doubleWave,
                                               clipboard.getMarkStartSample(), alignPanel.getInputWaveEndIndex(),
                                               0, alignPanel.getOutputWaveEndIndex());
                }

                waveMainPanel.reloadDrawValues();
            }
        }
    }

    private void performOperationInternal(OperationOnWavePluginIFace operation) {
        for (WaveMainPanel waveMainPanel : waves) {
            if (waveMainPanel.getShouldIncludeInOperations()) {
                DoubleWave doubleWave = waveMainPanel.getDoubleWave();
                if (shouldMarkPart) {
                    operation.performOperation(doubleWave, getMarkStartXSample(), getMarkEndXSample());
                }
                else {
                    operation.performOperation(doubleWave, 0, doubleWave.getSongLength());
                }

                waveMainPanel.reloadDrawValues();
            }
        }
    }


    private boolean waveMarkIsBeingDragged = false;

    public boolean getWaveMarkIsBeingDragged() {
        return waveMarkIsBeingDragged;
    }

    public void setWaveMarkIsBeingDragged(boolean isBeingDragged) {
        waveMarkIsBeingDragged = isBeingDragged;
    }

    private boolean isMouseButtonPressed = false;

    public void setMouseButtonPress(boolean val) {
        isMouseButtonPressed = val;
    }


    private boolean shouldMarkPart;

    public boolean getShouldMarkPart() {
        return shouldMarkPart;
    }

    public void setShouldMarkPart(boolean val) {
        shouldMarkPart = val;
    }

    private int markStartXPixel;

    public int getMarkStartXPixel() {
        return markStartXPixel;
    }

    private int markStartXSample;

    /**
     * Returns the smaller sample number of the mark start and mark end
     *
     * @return
     */
    public int getMarkStartXSample() {
        return Math.min(markStartXSample, markEndXSample);
    }

    // Have to take into consideration the most left index I currently show
    public void setMarkStartXVariablesBasedOnPixel(int pixel) {
        int sample = calculateSampleFromWavePixel(pixel);
        setMarkStartXVariablesBasedOnSample(sample);
    }

    public void setMarkStartXVariablesBasedOnSample(int sample) {
        markStartXSample = sample;
        double pixel = calculatePixel(sample);
        markStartXPixel = (int) pixel;
    }


    private int markEndXPixel;

    public int getMarkEndXPixel() {
        return markEndXPixel;
    }

    private int markEndXSample;

    /**
     * Returns the bigger sample number of the mark start and mark end
     *
     * @return
     */
    public int getMarkEndXSample() {
        return Math.max(markStartXSample, markEndXSample);
    }

    // Have to take into consideration the most left index I currently show
    public void updateMarkEndXVariablesBasedOnPixel(int update, int oldHorizontalScroll) {
        // oldHorizontalScroll moves it to the start, and update updates it.
        // So now the pixel represents relative pixel distance to the visible start of wave.
        int pixel = markEndXPixel + update - oldHorizontalScroll;
        setMarkEndXVariablesBasedOnPixel(pixel);
    }

    /**
     * Pixel is relative horizontal distance to the visible start of the wave.
     *
     * @param pixel
     */
    public void setMarkEndXVariablesBasedOnPixel(int pixel) {
        int sample = calculateSampleFromWavePixel(pixel);
        setMarkEndXVariablesBasedOnSample(sample);
    }

    public void setMarkEndXVariablesBasedOnSample(int sample) {
        int songLen;
        if (sample < 0) {
            sample = 0;
        }
        else if (sample > (songLen = getSongLengthInSamples())) {
            sample = songLen;
        }

        markEndXSample = sample;
        double pixel = calculatePixel(sample);
        markEndXPixel = (int) pixel;
    }


    private int convertPixelFromWaveToTotalWavePixel(int pixel) {
        return getCurrentHorizontalScroll() + pixel;
    }

    private int calculateSampleFromWavePixel(int pixel) {
        int totalPixel = convertPixelFromWaveToTotalWavePixel(pixel);
        return calculateSampleFromTotalWavePixel(totalPixel);
    }

    private int calculateSampleFromTotalWavePixel(int pixel) {
        int widthOfAudioInSamples = getDoubleWaveLength();
        int sample = (int) (widthOfAudioInSamples * (pixel / (double) getWaveWidth()));
        return sample;
    }

    private double calculatePixel(int sample) {
        int widthOfAudioInSamples = getDoubleWaveLength();
        double pixel = sample / (double) widthOfAudioInSamples;
        pixel *= getWaveWidth();
        return pixel;
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (canZoom) {
            canPoll = true;
        }
        if (channelCountChanged && thisFrame.isEnabled()) {
            disableZooming();
            thisFrame.setEnabled(false);

            if (splitters.size() >= 1) {
                JSplitPane currSplitter = splitters.get(0);
                WaveMainPanel top;
                WaveMainPanel bot;
                top = (WaveMainPanel) currSplitter.getTopComponent();
                top.setPrefSizeToMin();
                if (waves.size() >= 2) {
                    bot = (WaveMainPanel) currSplitter.getBottomComponent();
                    bot.setPrefSizeToMin();
                }
                // Else there is only 1 wave so the bot panel is empty panel

                setDivLocToMinDivLoc(currSplitter);
                for (int i = 1; i < splitters.size() - 1; i++) {
                    currSplitter = splitters.get(i);
                    bot = (WaveMainPanel) currSplitter.getBottomComponent();
                    bot.setPrefSizeToMin();
                    setDivLocToMinDivLoc(currSplitter);
                }

                JSplitPane lastSplitter = splitters.get(splitters.size() - 1);
                setDivLocToMinDivLoc(lastSplitter);
            }
            Timer t = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    channelCountChanged = false;
                    // https://stackoverflow.com/questions/54173626/how-do-i-stop-timer-task-after-executed-one-time
                    ((Timer) e.getSource()).stop();


                    // I have to scroll to the end because else the size will be bigger than the preferred size - I don't want that
                    int maxScroll = getMaxVerticalScroll();

                    panelWithWaves.getVerticalScrollBar().setValue(maxScroll);
                    panelWithWaves.revalidate();
                    panelWithWaves.repaint();
                    // I put the enabling with delay just to be sure the user doesn't break it
                    Timer t2 = new Timer(100, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ((Timer) e.getSource()).stop();
                            // It is better to take the control once to the maximum scroll,
                            // than to max and then to currScroll - the blink is very unpleasant
                            // panelWithWaves.getVerticalScrollBar().setValue(currScroll);
                            thisFrame.setEnabled(true);
                            enableZooming();
                        }
                    });
                    t2.start();
                }
            });

            t.start();
            panelWithWaves.revalidate();
            panelWithWaves.repaint();
        }
    }


    private static void setDivLocToMinDivLoc(JSplitPane splitPane) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                splitPane.setDividerLocation(splitPane.getMinimumDividerLocation());
            }
        });
    }


    private double timeLineXUserSelected;

    public void setTimeLineXUserSelected(double val) {
        timeLineXUserSelected = val;
    }

    private double timeLineX;

    public double getTimeLineX() {
        return timeLineX;
    }

    private double timeLinePixelsPerPlayPart;


    ////////// MouseListener

    // mouseClicked is when the mouse button has been pressed and released.
    @Override
    public void mouseClicked(MouseEvent e) {
        // EMPTY
    }

    // mousePressed is when the mouse button has been pressed.
    @Override
    public void mousePressed(MouseEvent e) {
        shouldMarkPart = false;
        markStartXPixel = e.getX();
        this.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // EMPTY
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // EMPTY
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // EMPTY
    }


    @Override
    public int getOutputSampleRate() {
        return (int) outputAudioFormat.getSampleRate();
    }

    public int getNumberOfChannelsInOutputFormat() {
        return outputAudioFormat.getChannels();
    }

    public int getDoubleWaveLength() {
        int len;
        if (waves.size() == 0) {
            len = -1;
        }
        else {
            len = waves.get(0).getDoubleWave().getSongLength();
        }
        return len;
    }


    private int currSample;

    private void resetPlayVariables() {
        currSample = 0;
        currSampleUserSelected = 0;
        timeLineX = 0;
    }

    private boolean userClickedWave;

    public void setUserClickedWave(boolean val) {
        userClickedWave = val;
    }

    private int currSampleUserSelected;

    public void setCurrSampleUserSelected(int val) {
        currSampleUserSelected = val;
    }

    private FloatControl masterGainControl;
    private BooleanControl muteControl;
    private AudioControlPanelWithZoomAndDecibel audioControlPanel;

    private void clickPauseButtonIfPlaying() {
        BooleanButton playButton = audioControlPanel.getPlayButton();
        if (!playButton.getBoolVar()) {
            playButton.doClick();
        }
    }

    private ActionListener playButtonListener;

    @Override
    public FloatControl getGain() {
        return masterGainControl;
    }

    @Override
    public BooleanControl getMuteControl() {
        return muteControl;
    }


    private AudioPlayerPanel() {
        setOutputAudioFormatToDefault();
        this.setLayout(new GridBagLayout());

        playButtonListener = new ActionListener() {
            // Open lock
            @Override
            public void actionPerformed(ActionEvent e) {
                if (audioControlPanel.getPlayButton().getBoolVar()) {
                    audioThread.pause();
                }
                else {
                    audioThread.play();
                }
            }
        };

        ActionListener zoomListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateZoom(1);
            }
        };

        ActionListener unzoomListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateZoom(-1);
            }
        };
        audioControlPanel = new AudioControlPanelWithZoomAndDecibel(this, playButtonListener,
                                                                    this, zoomListener, unzoomListener,
                                                                    outputAudioFormat.getChannels());


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        this.add(audioControlPanel, c);
    }

    @Override
    public double[] getCurrentSamples() {
        double[] outputArr = audioControlPanel.getDecibelMeterMain().getChannelSamples();
        fillArrayWithCurrentlyPlayedValues(outputArr);
        return outputArr;
    }

    private void fillArrayWithCurrentlyPlayedValues(double[] arr) {
        double[][] songsConc = songs;
        double[][] multFactorsConc = multFactors;
        int currSampleConc = currSample;
        if (songsConc == null || multFactorsConc == null || currSampleConc >= songsConc[0].length) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = 0;
            }
        }
        else {
            for (int i = 0; i < songsConc.length; i++) {
                audioThread.mixer.mix(songsConc, arr, 0, multFactorsConc, currSampleConc);
            }
        }
    }


    private PlayerAudioThread audioThread;

    public class PlayerAudioThread extends Thread {
        /**
         * @param shouldPause
         * @param maxPlayTimeInMs        size how many ms can be maximally at single moment inside source data line
         * @param internalBufferSizeInMs is the size of the array used for feeding values to the source data line in ms.
         */
        public PlayerAudioThread(boolean shouldPause, int maxPlayTimeInMs, int internalBufferSizeInMs) {
            maxPlayTimeDivFactor = AudioThread.convertTimeInMsToDivFactor(maxPlayTimeInMs);
            internalBufferTimeDivFactor = AudioThread.convertTimeInMsToDivFactor(internalBufferSizeInMs);
            this.shouldPause = shouldPause;
            isPaused = shouldPause;
            setDataLine();
        }

        private final double maxPlayTimeDivFactor;
        private final double internalBufferTimeDivFactor;

        private SourceDataLine line = null;

        private void setDataLine() {
            // Close the old line if it isn't closed
            try {
                if (line != null) {
                    if (line.isOpen()) {
                        line.close();
                    }
                }
            }
            catch (Exception e) {
                MyLogger.logException(e);
            }


            try {
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, outputAudioFormat);
                line = null;
                try {
                    line = (SourceDataLine) AudioSystem.getLine(info);
                    line.open(outputAudioFormat);
                }
                catch (LineUnavailableException e) {
                    MyLogger.logException(e);
                }

                muteControl = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
                muteControl.setValue(audioControlPanel.getMuteButton().getBoolVar());
                //  Type.VOLUME isn't available control, so we use master gain
                masterGainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);

                audioControlPanel.setMasterGainToCurrentSlideValue();
                line.start();
            }
            catch (Exception e) {
                MyLogger.logException(e);
            }

            setMixer();
        }

        private AudioMixerIFace mixer;

        private void setMixer() {
            if (mixer == null) {
                setMixer(new MixerWithPostProcessingSumDivision(multFactors));
                //mixer = new AverageMixerWithPossibleClipping(multFactors);
                //mixer = new AverageMixerWithoutClipping(songs.length);
                //mixer = new SimpleAverageMixer();
            }
        }

        private void setMixer(AudioMixerIFace mixer) {
            this.mixer = mixer;
        }


        private Object pauseLock = new Object();

        private void setShouldPause(boolean val) {
            synchronized (pauseLock) {
                shouldPause = val;
            }
        }

        private volatile boolean shouldPause;
        private volatile boolean isPaused = false;

        @Override
        public void run() {
            outputFormatChanged();
            playAudioLoop();
        }


        private Object audioLock = new Object();

        public void pause() {
            setShouldPause(true);
        }

        public void play() {
            synchronized (audioLock) {
                audioLock.notifyAll();
            }
        }

        public void reset() {
            while (!isPaused) {       // Active waiting
                pause();
            }

            resetValues();
        }

        private void resetValues() {
            byte[] arr = audioArr;
            if (arr != null) {
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = 0;
                }
            }

            resetPlayVariables();
            wavesLengthChanged();
        }


        private byte[] audioArr;
        private int sampleSizeInBytes;
        private int maxAbsoluteValue;
        private int outputEndIndex;
        private int indexJumpInDoubleArr;
        private int audioLineMaxAvailableBytes;
        private int minAllowedAvailableSize;
        private int oldSampleRate = 0;

        public void outputFormatChanged() {
            pause();
            while (!isPaused) {
                setShouldPause(true);
            }

            int sampleRate = (int) outputAudioFormat.getSampleRate();
            if (oldSampleRate > 0) {
                currSample *= sampleRate / (double) oldSampleRate;
            }
            oldSampleRate = sampleRate;

            setDataLine();
            audioLineMaxAvailableBytes = line.available();

            sampleSizeInBytes = outputAudioFormat.getSampleSizeInBits();
            maxAbsoluteValue = AudioUtilities.getMaxAbsoluteValueSigned(sampleSizeInBytes);
            sampleSizeInBytes /= 8;
            int frameSize = sampleSizeInBytes * outputAudioFormat.getChannels();
            // Now it contains length of one second in bytes.

            int maxByteCountInAudioLine = AudioThread.convertMsToByteLen(sampleRate, frameSize, maxPlayTimeDivFactor);
            minAllowedAvailableSize = audioLineMaxAvailableBytes - maxByteCountInAudioLine;
            int audioArrLen = AudioThread.convertMsToByteLen(sampleRate, frameSize, internalBufferTimeDivFactor);

            // The size is at least frameSize and usually it is at least 2 ms
            audioArrLen = Math.max(frameSize, Math.min(audioArrLen, maxByteCountInAudioLine / 2));
            Utilities.convertToMultipleUp(audioArrLen, frameSize);
            audioArr = new byte[audioArrLen];

            callOnResize();
            wavesLengthChanged();
        }

        private void wavesLengthChanged() {
            outputEndIndex = getDoubleWaveLength();
            indexJumpInDoubleArr = audioArr.length / AudioUtilities.calculateFrameSize(outputAudioFormat);
            setTimeLinePixelsPerPlayPart();
        }


        private void playAudioLoop() {
            while (true) {
                updateWavesForMixing();

                int nextSample;
                synchronized (audioLock) {
                    tryPause();
                    for (nextSample = currSample + indexJumpInDoubleArr; nextSample < outputEndIndex; nextSample = currSample + indexJumpInDoubleArr) {
                        tryPause();
                        boolean filledWithWaveSamples = performMixing(songs, multFactors, audioArr.length);

                        // This is here for the app to be more responsive,
                        // because we usually fill the buffer to be played much faster, than we actually play it.
                        while (line.available() - minAllowedAvailableSize < 0) {
                            // Active waiting
                        }
                        line.write(audioArr, 0, audioArr.length);

                        if (filledWithWaveSamples) {
                            if (userClickedWave) {
                                switchToUserSelectedSample();
                            }
                            else {
                                timeLineX += timeLinePixelsPerPlayPart;
                            }
                        }
                        repaint();
                    }

                    // Write the last few bytes
                    int remainingLen = (outputEndIndex - currSample) *
                                       (outputAudioFormat.getSampleSizeInBits() / 8) * outputAudioFormat.getChannels();
                    if (remainingLen > 0) {
                        performMixing(songs, multFactors, remainingLen);
                        line.write(audioArr, 0, remainingLen);
                    }

                    setShouldPause(true);
                    resetValues();
                    SwingUtilities.invokeLater(() -> audioControlPanel.getPlayButton().doClick());
                }
            }
        }


        private void tryPause() {
            // First I check if pause button was clicked and after that I play the current part
            if (shouldPause) {
                line.drain();
                line.stop();
                isPaused = true;

                try {
                    audioLock.wait();        // Passive waiting
                }
                catch (InterruptedException e) {
                    MyLogger.logException(e);
                }

                isPaused = false;
                setShouldPause(false);
                line.start();
                if (userClickedWave) {
                    switchToUserSelectedSample();
                }
            }
        }


        /**
         * These are references to array so it is save from concurrency issue like setting the array to null midway through loop
         *
         * @param currSongs
         * @param currMultFactors
         * @param lenInBytes
         * @return Returns false if there wasn't any wave so it had to be filled with 0s
         */
        private boolean performMixing(double[][] currSongs, double[][] currMultFactors, int lenInBytes) {
            if (hasAtLeastOneWave) {
                // currSample++ because I make from 1 sample multiple samples
                for (int outputArrIndex = 0; outputArrIndex < lenInBytes; currSample++) {
                    outputArrIndex = mixer.mix(currSongs, audioArr, outputArrIndex, currMultFactors, sampleSizeInBytes,
                                               outputAudioFormat.isBigEndian(), outputAudioFormat.isSigned,
                                               maxAbsoluteValue, currSample);
                }

                return true;
            }
            else {
                fillPlayArrWithZeros();
                return false;
            }
        }

        private void fillPlayArrWithZeros() {
            for (int i = 0; i < audioArr.length; i++) {
                audioArr[i] = 0;
            }

            // Not sure if I should also move in the waves like this:
            // currSample += audioArr.length;
        }
    }


    private DoubleWave getOutputWaveDouble(String filename) {
        updateWavesForMixing();
        int outputLen = getDoubleWaveLength() * outputAudioFormat.getChannels();
        return getOutputWaveDouble(songs, multFactors, outputLen, audioThread.mixer, outputAudioFormat, filename);
    }

    public static DoubleWave getOutputWaveDouble(double[][] songs, double[][] multFactors, int outputLen,
                                                 AudioMixerIFace mixer, AudioFormat outputAudioFormat, String filename) {
        double[] outputWave = new double[outputLen];
        mixer.mixAllToOutputArr(songs, outputWave, multFactors);
        return new DoubleWave(outputWave, (int) outputAudioFormat.getSampleRate(),
                              outputAudioFormat.getChannels(), filename, false);
    }


    private byte[] getOutputWaveBytes() {
        updateWavesForMixing();
        int outputLen = getDoubleWaveLength() * outputAudioFormat.getFrameSize();

        return getOutputWaveBytes(songs, multFactors, outputLen, audioThread.mixer, outputAudioFormat);
    }

    public static byte[] getOutputWaveBytes(double[][] songs, double[][] multFactors, int outputLen,
                                            AudioMixerIFace mixer, AudioFormatWithSign outputAudioFormat) {
        int maxAbsoluteValue = AudioUtilities.getMaxAbsoluteValueSigned(outputAudioFormat.getSampleSizeInBits());
        byte[] outputWave = new byte[outputLen];
        mixer.mixAllToOutputArr(songs, outputWave, multFactors, outputAudioFormat.getSampleSizeInBits() / 8,
                                outputAudioFormat.isBigEndian(), outputAudioFormat.isSigned, maxAbsoluteValue);

        return outputWave;
    }


    public void switchToUserSelectedSampleIfPaused() {
        if (audioControlPanel.getPlayButton().getBoolVar()) {
            switchToUserSelectedSample();
            this.repaint();
        }
    }

    private void switchToUserSelectedSample() {
        currSample = currSampleUserSelected;
        timeLineX = timeLineXUserSelected;
        userClickedWave = false;
    }


    private static void closeLine(SourceDataLine line) {
        line.drain();
        line.stop();
        line.close();
    }


    private void updateTimeLineX(int waveWidth) {
        int audioLenInSamples = getDoubleWaveLength();
        timeLineX = currSample / (double) audioLenInSamples;
        timeLineX *= waveWidth;
    }

    private void callOnResize() {
        setTimeLinePixelsPerPlayPart();

        // Resize marking if used
        if (shouldMarkPart) {
            // Not using the get methods because it was incorrectly for zooming
            int startXSample = markStartXSample;
            int endXSample = markEndXSample;
            setMarkStartXVariablesBasedOnSample(startXSample);
            setMarkEndXVariablesBasedOnSample(endXSample);
        }
    }

    private void setTimeLinePixelsPerPlayPart() {
        int waveWidth = getWaveWidth();
        int audioLenInSamples = getDoubleWaveLength();
        int arrLenInFrames = audioThread.audioArr.length / AudioUtilities.calculateFrameSize(outputAudioFormat);
        timeLinePixelsPerPlayPart = arrLenInFrames / (double) audioLenInSamples;
        timeLinePixelsPerPlayPart *= waveWidth;
        updateTimeLineX(waveWidth);
    }


    private void stopAndModifyAudio(boolean isSongsOrMultFactorsUpdateNeeded, ModifyAudioIFace modifyAudioImpl,
                                    boolean shouldResume, boolean shouldResetAudio) {
        disableZooming();
        BooleanButton playButton = audioControlPanel.getPlayButton();

        boolean wasNotPaused = !playButton.getBoolVar();
        if (wasNotPaused) {     // If the playing is not paused, then pause it
            playButton.doClick();
            while (!audioThread.isPaused) {
                audioThread.setShouldPause(true);
            }
        }


        modifyAudioImpl.modifyAudio();

        if (shouldResetAudio) {
            audioThread.reset();
        }
        if (wasNotPaused && shouldResume) {
            playButton.doClick();
        }

        if (isSongsOrMultFactorsUpdateNeeded) {
            postWaveAdditionAction();
        }

        enableZooming();
    }


    public int getWaveStartX() {
        int x;
        x = waves.get(0).getWaveStartX();
        return x;
    }

    public int getWaveEndX() {
        int x;
        x = waves.get(0).getWaveStartX() + waves.get(0).getWaveWidth();
        return x;
    }

    public int getWaveWidth() {
        if (waves.size() == 0) {
            return getDefaultWaveWidth();
        }
        int w = Math.max(waves.get(0).getWaveWidth(), getDefaultWaveWidth());
        return w;
    }

    public int getDefaultWaveWidth() {
        if (waves.size() == 0) {
            return WavePanel.START_DEFAULT_WAVE_WIDTH_IN_PIXELS;
        }
        int w = waves.get(0).getDefaultWaveWidth();
        return w;
    }


    public int getSongLengthInSamples() {
        if (waves.size() != 0) {
            return waves.get(0).getSongLen();
        }
        return -1;
    }


    public int getPanelWithWavesStartOnScreenX() {
        Point panelStart = new Point(0, 0);
        SwingUtilities.convertPointToScreen(panelStart, panelWithWaves);
        return panelStart.x;
    }

    public int getPanelWithWavesStartOnScreenY() {
        Point panelStart = new Point(0, 0);
        SwingUtilities.convertPointToScreen(panelStart, panelWithWaves);
        return panelStart.y;
    }

    public int getPanelWithWavesEndOnScreenY() {
        Point panelEnd = new Point(0, this.getHeight());
        SwingUtilities.convertPointToScreen(panelEnd, this);
        return panelEnd.y;
    }

    public Point getPanelWithWavesStartOnScreen() {
        Point panelStart = new Point(0, 0);
        SwingUtilities.convertPointToScreen(panelStart, panelWithWaves);
        return panelStart;
    }


    public void tryMoveJSplitPane(Point pointOnScreen) {
        int startY;
        int endY;
        int moveSpeed;
        if (pointOnScreen.y < (startY = getPanelWithWavesStartOnScreenY())) {
            moveSpeed = startY - pointOnScreen.y;
            moveJScrollPaneUp(moveSpeed);
        }
        else if (pointOnScreen.y > (endY = getPanelWithWavesEndOnScreenY())) {
            moveSpeed = pointOnScreen.y - endY;
            moveJScrollPaneDown(moveSpeed);
        }
    }


    private int moveJScrollPaneUp(int moveSpeed) {
        JViewport view = panelWithWaves.getViewport();
        Point oldPos = view.getViewPosition();
        int movedSize = convertToPixelMovement(moveSpeed);
        if (movedSize > 0) {
            int y = oldPos.y;
            int viewY = view.getY() - view.getInsets().top - panelWithWaves.getInsets().top;
            // Just scroll
            if (y != viewY) {     // Increase the size of JSplitPane
                int dif = y - viewY;
                if (dif < movedSize) {
                    movedSize = dif;
                }
                Point newPos = new Point(oldPos.x, oldPos.y - movedSize);
                view.setViewPosition(newPos);
            }
        }
        return movedSize;
    }


    private int moveJScrollPaneDown(int moveSpeed) {
        JViewport view = panelWithWaves.getViewport();
        Point oldPos = view.getViewPosition();
        int movedSize = convertToPixelMovement(moveSpeed);
        if (movedSize > 0) {
            int bottomY = oldPos.y + view.getViewRect().height;
            int viewH = view.getViewSize().height;
            // Just scroll
            if (bottomY < viewH) {     // Increase the size of JSplitPane
                int dif = viewH - bottomY;
                if (dif < movedSize) {
                    movedSize = dif;
                }

                Point newPos = new Point(oldPos.x, oldPos.y + movedSize);
                view.setViewPosition(newPos);
            }
        }

        return movedSize;
    }


    public int getWaveIndex(Component waveMainPanel) {
        for (int i = 0; i < waves.size(); i++) {
            if (waveMainPanel == waves.get(i)) {
                return i;
            }
        }
        return -1;
    }


    private boolean shouldZoomToMid = false;

    @Override
    public boolean getShouldZoomToMid() {
        return shouldZoomToMid;
    }

    private boolean shouldZoomToEnd = false;

    @Override
    public boolean getShouldZoomToEnd() {
        return shouldZoomToEnd;
    }

    private double timeLineXForZooming;

    public double getTimeLineXForZooming() {
        return timeLineXForZooming;
    }

    private int oldWaveVisibleWidth;

    public int getOldWaveVisibleWidth() {
        return oldWaveVisibleWidth;
    }

    private void setOldWaveVisibleWidth(int val) {
        oldWaveVisibleWidth = val;
    }

    public void setOldWaveVisibleWidth() {
        oldWaveVisibleWidth = getWavesVisibleWidth();
    }


    private boolean canPoll = true;
    private boolean canZoom = true;

    @Override
    public boolean getCanZoom() {
        return canZoom;
    }

    private void disableZooming() {
        //waveScrollerPollTimer.stop();
        canZoom = false;
        canPoll = false;
    }

    @Override
    public void enableZooming() {
        canZoom = true;
        this.revalidate();
        this.repaint();
    }

    public int getMaxPossibleZoom() {
        int maxPossibleZoom = Integer.MAX_VALUE;
        for (WaveMainPanel waveMainPanel : waves) {
            maxPossibleZoom = Math.min(waveMainPanel.getMaxPossibleZoom(), maxPossibleZoom);
        }

        if (maxPossibleZoom == Integer.MAX_VALUE) {
            return 0;
        }
        return maxPossibleZoom;
    }

    /**
     * Doesn't do anything for zoomChange == 0
     */
    @Override
    public void updateZoom(int zoomChange) {
        if (zoomChange != 0 && getCanZoom() && !waveScrollerWrapperPanel.getIsScrollbarBeingUsed() && waves.size() != 0) {
            disableZooming();
            setMaxAllowedZoom();
            if (zoomChange > 0) {     // When zooming, we need to check if we are zooming too much
                if (zoomVariables.getIsZoomAtMax()) {
                    enableZooming();
                    return;
                }
                else {
                    int newZoom = zoomVariables.zoom + zoomChange;
                    if (newZoom > zoomVariables.getMaxAllowedZoom()) {
                        int zoomDif = newZoom - zoomVariables.getMaxAllowedZoom();
                        zoomChange -= zoomDif;
                    }
                }
            }
            else {
                if (zoomVariables.getIsZoomAtZero()) {
                    enableZooming();
                    return;
                }
                else {
                    int newZoom = zoomVariables.zoom + zoomChange;
                    if (newZoom < 0) {
                        zoomChange -= newZoom;
                    }
                }
            }

            // Save images for all waves to bridge the zoom, to understand this problem check javadocs at zoomBridgeImg variable
            for (WaveMainPanel w : waves) {
                w.saveZoomBridgeImg();
            }


            waveScrollerWrapperPanel.setOldScrollbarValue(waveScrollerWrapperPanel.getCurrentHorizontalScroll());
            setOldWaveVisibleWidth();
            timeLineXForZooming = timeLineX;
            shouldZoomToMid = false;       // Is set to true only when zooming to mid
            shouldZoomToEnd = false;       // Is set to true only when zooming to end


            if (zoomChange > 0) {
                // Just pass 1 (-1 when unzooming), because I am not sure if it works for larger numbers (it may works, but this works 100%),
                // I had similar problem in the diagram window
                for (int i = 0; i < zoomChange; i++) {
                    zooming(1);
                }
            }
            else if (zoomChange < 0) {
                for (int i = 0; i > zoomChange; i--) {
                    unzooming(-1);
                }
            }


            callOnResize();
            setWaveScrollerPanelsSizes();
            this.revalidate();
            this.repaint();
        }
    }

    public boolean getScrollReceivedResizeEvent() {
        return waveScrollerWrapperPanel.getScrollReceivedResizeEvent();
    }

    public void processScrollReceivedResizeEvent() {
        waveScrollerWrapperPanel.processScrollReceivedResizeEvent();
    }


    private void zooming(int wheelRotation) {
        int newZoom = wheelRotation + zoomVariables.zoom;
        int maxAllowedZoom = zoomVariables.getMaxAllowedZoom();
        if (newZoom > maxAllowedZoom) {
            newZoom = maxAllowedZoom;
        }

        performZoomingActions(newZoom, true);
    }

    private void unzooming(int wheelRotation) {
        // Basically same code as in zooming but "reversed"
        int newZoom = wheelRotation + zoomVariables.zoom;

        if (newZoom < 0) {
            newZoom = 0;
        }

        performZoomingActions(newZoom, false);
    }


    private void performZoomingActions(int newZoom, boolean isZoom) {
        if (newZoom == zoomVariables.zoom) {
            enableZooming();
        }
        else {
            zoomToGivenPosition(newZoom, isZoom);
        }
    }


    public void passZoomChangeToWaves(int newZoom, boolean setToMid, boolean setToEnd) {
        for (WaveMainPanel wave : waves) {
            wave.updateZoom(newZoom, waveScrollerWrapperPanel.getOldScrollbarValue(), setToMid, setToEnd);
            wave.revalidate();
        }
    }


    /**
     * Depending on situation zooms. If the time line is visible then zooms in such way that the line is in the middle.
     * If it is not visible then zoom to the middle of visible part.
     * If the time line is close to start (or end)
     * then zoom in such way that the scroll is at the start which is 0 (or at the end which is wave width - visible width)
     *
     * @param newZoom is the new zoom value.
     * @param isZoom  is true when zooming, false when unzooming
     */
    public void zoomToGivenPosition(int newZoom, boolean isZoom) {
        if (isTimeLineVisible()) {
            if (isTimeLineNearStart()) {
                zoomToStart();
            }
            else if (isTimeLineNearEnd()) {
                zoomToEnd();
            }
            else {
                if (isZoom) {
                    zoomToTimeLine();
                }
                else {
                    zoomToMid();
                }
            }
        }
        else {
            if (!isZoom) {
                int visibleWaveWidth = getWavesVisibleWidth();
                int currScroll = getCurrentHorizontalScroll();
                if (currScroll < visibleWaveWidth / WavePanel.ZOOM_VALUE) {     // It would go to negative numbers
                    zoomToStart();
                }
                else {
                    zoomToMid();
                }
            }
            else {
                zoomToMid();
            }
        }

        passZoomChangeToWaves(newZoom, shouldZoomToMid, shouldZoomToEnd);
        setCurrentZoom(newZoom);
    }

    public boolean isTimeLineNearStart() {
        return timeLineX < getWavesVisibleWidth() / 4;
    }

    public boolean isMidNearEnd() {
        int currentScroll = getCurrentHorizontalScroll();
        int visibleWidth = getWavesVisibleWidth();
        return isNearEnd(currentScroll + visibleWidth / 2);
    }

    public boolean isTimeLineNearEnd() {
        return isNearEnd((int) timeLineXForZooming);
    }

    public boolean isNearEnd(int x) {
        return x > getWaveWidth() - getWavesVisibleWidth() / 4;
    }

    private void zoomToStart() {
        waveScrollerWrapperPanel.setOldScrollbarValue(0);
    }

    private void zoomToEnd() {
        JScrollBar scrollBar = waveScrollerWrapperPanel.getWaveScroller().getHorizontalScrollBar();
        int extent = scrollBar.getModel().getExtent();
        int max = scrollBar.getMaximum() - extent;
        waveScrollerWrapperPanel.setOldScrollbarValue(max);

        shouldZoomToEnd = true;
    }

    private void zoomToMid() {
        shouldZoomToMid = true;
    }

    private void zoomToTimeLine() {
        int timeLineInMidScroll = (int) getTimeLineXForZooming() - getOldWaveVisibleWidth() / 2;
        waveScrollerWrapperPanel.setOldScrollbarValue(timeLineInMidScroll);
        zoomToMid();
    }


    public boolean isTimeLineVisible() {
        int timeLine = (int) getTimeLineX();
        int scroll = getCurrentHorizontalScroll();
        int waveVisibleWidth = getWavesVisibleWidth();
        return timeLine >= scroll && timeLine <= scroll + waveVisibleWidth;
    }


    public int getWavesVisibleWidth() {
        int visibleWidth = 0;
        if (waves.size() > 0) {
            int firstWaveStart = waves.get(0).getWave().getX();
            int width = panelWithWaves.getViewport().getWidth();

            for (WaveMainPanel waveMainPanel : waves) {
                int waveVisibleWidth = waveMainPanel.getWave().getVisibleRect().width;
                if (visibleWidth < waveVisibleWidth && waveVisibleWidth + firstWaveStart <= width) {
                    visibleWidth = waveVisibleWidth;
                }
            }
        }
        return visibleWidth;
    }

    public int getWavesVisibleHeight() {
        int visibleHeighth = 0;
        for (WaveMainPanel waveMainPanel : waves) {
            visibleHeighth = Math.max(visibleHeighth, waveMainPanel.getWave().getVisibleRect().height);
        }
        return visibleHeighth;
    }


    @Override
    public void revalidateTimestamps() {
        this.timestampPanel.revalidate();
        this.revalidate();
        this.repaint();
    }


    @Override
    public void scrollChangeCallback(int oldVal, int newVal) {
        passHorizontalScrollChangeToWaves(oldVal, newVal);

        if (isMouseButtonPressed && shouldMarkPart) {
            int update = newVal - oldVal;
            if (update < 0) {
                update = -update;
            }
            updateMarkEndXVariablesBasedOnPixel(update, oldVal);
        }

        this.repaint();
    }

    private void passHorizontalScrollChangeToWaves(int oldVal, int newVal) {
        for (WaveMainPanel wave : waves) {
            wave.updateWaveDrawValues(oldVal, newVal);
        }
    }


    public void pollMovement() {
        if (thisFrame.getHasFocus()) {
            if (canPoll) {
                waveScrollerWrapperPanel.setIsScrollbarBeingUsed(true);
                Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(mouseLoc, panelWithWaves);

                if (getWaveMarkIsBeingDragged()) {
                    int w = this.getWidth();
                    JScrollBar bar = waveScrollerWrapperPanel.getWaveScroller().getHorizontalScrollBar();
                    int horizontalMovement = HORIZONTAL_SCROLL_UNIT_INCREMENT;

                    WaveMainPanel lastWave = waves.get(waves.size() - 1);
                    int lastWaveEndY = lastWave.getY() + lastWave.getHeight();

                    if (mouseLoc.y > 0 && mouseLoc.y < lastWaveEndY) {
                        int boundToMoveRight = w - getPanelWithWavesVerticalScrollbarWidth();
                        int boundToMoveLeft = waves.get(0).getWaveStartX();

                        if (mouseLoc.x >= boundToMoveRight) {
                            bar.setValue(bar.getValue() + horizontalMovement);
                        }
                        else if (!waves.isEmpty() && mouseLoc.x < boundToMoveLeft) {
                            bar.setValue(bar.getValue() - horizontalMovement);
                        }
                    }
                }


                JViewport view = panelWithWaves.getViewport();
                if (getIsAnySplitterDragged()) {
                    // If we are below the panel with waves, and we moved up (in mouse movement sense)
                    if (mouseLoc.y < 0) {
                        final int verticalMovement = VERTICAL_SCROLL_UNIT_INCREMENT;
                        int movementUp = convertToPixelMovement(verticalMovement);

                        Point oldPos = view.getViewPosition();
                        Point newPos = new Point(oldPos.x, oldPos.y - movementUp);
                        if (newPos.y < 0) {
                            newPos.y = 0;
                        }
                        view.setViewPosition(newPos);
                    }
                    else if (mouseLoc.y > view.getViewRect().height && lastSplitterUI != null && !getIsLastSplitterDragged()) {
                        final int verticalMovement = VERTICAL_SCROLL_UNIT_INCREMENT;
                        int movementDown = convertToPixelMovement(verticalMovement);

                        Point oldPos = view.getViewPosition();
                        Point newPos = new Point(oldPos.x, oldPos.y + movementDown);
                        int maxY = view.getViewSize().height - view.getViewRect().height;
                        if (newPos.y > maxY) {
                            newPos.y = maxY;
                        }

                        view.setViewPosition(newPos);
                    }
                }

                panelWithWaves.revalidate();
                panelWithWaves.repaint();
                waveScrollerWrapperPanel.setIsScrollbarBeingUsed(false);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////// wave mouse right click callbacks
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * To reset clipboard call removeWaveFromClipboard()
     */
    private ClipboardWave clipboard = new ClipboardWave();
    private ClipboardDrawView clipboardDraw = new ClipboardDrawView();

    public ClipboardDrawView getClipboardDrawView() {
        return clipboardDraw;
    }

    private class ClipboardWave {
        public ClipboardWave() {
            markStartSampleClipboardWave = -1;
            markEndSampleClipboardWave = -1;
            wavePanel = null;
        }

        private boolean isCut;

        public boolean getIsCut() {
            return isCut;
        }

        private int markStartSampleClipboardWave;

        public int getMarkStartSample() {
            return markStartSampleClipboardWave;
        }

        private void setMarkStartSample(int sample) {
            markStartSampleClipboardWave = sample;
        }

        private int markEndSampleClipboardWave;

        public int getMarkEndSample() {
            return markEndSampleClipboardWave;
        }

        private void setMarkEndSample(int sample) {
            markEndSampleClipboardWave = sample;
        }

        private WaveMainPanel wavePanel;

        public DoubleWave getWave() {
            return wavePanel.getDoubleWave();
        }

        public void setValues(WaveMainPanel wave, int markStartSampleClipboardWave,
                              int markEndSampleClipboardWave, boolean isCut) {
            this.wavePanel = wave;
            this.markStartSampleClipboardWave = markStartSampleClipboardWave;
            this.markEndSampleClipboardWave = markEndSampleClipboardWave;
            this.isCut = isCut;
            setEnabledWithWaveMenuItems(true);
        }

        public boolean isEqualToClipboardWavePanel(WaveMainPanel wp) {
            return wp == wavePanel;
        }

        public boolean isWaveInClipboard() {
            return wavePanel != null;
        }

        public void removeWaveFromClipboard() {
            setEnabledWithWaveMenuItems(false);
            wavePanel = null;
        }

        /**
         * If equal to the internal remove then wave is removed from clipboard.
         *
         * @param wp
         */
        public void removeWaveFromClipboard(WaveMainPanel wp) {
            if (isEqualToClipboardWavePanel(wp)) {
                removeWaveFromClipboard();
            }
        }
    }

    public class ClipboardDrawView {
        public ClipboardDrawView() {
            this.clipboardWave = AudioPlayerPanel.this.clipboard;
        }

        private final ClipboardWave clipboardWave;


        public boolean isCut() {
            return clipboardWave.isCut;
        }

        public boolean isEqualToClipboardWavePanel(WaveMainPanel wp) {
            return clipboardWave.isEqualToClipboardWavePanel(wp);
        }


        public int getClipboardMarkStartPixel() {
            return (int) AudioPlayerPanel.this.calculatePixel(clipboardWave.markStartSampleClipboardWave);
        }

        public int getClipboardMarkEndPixel() {
            return (int) AudioPlayerPanel.this.calculatePixel(clipboardWave.markEndSampleClipboardWave);
        }
    }


    public void copyWave(WaveMainPanel wave, boolean isCut) {
        DoubleWave doubleWave = wave.getDoubleWave();
        if (shouldMarkPart) {
            clipboard.setValues(wave, getMarkStartXSample(), getMarkEndXSample(), isCut);
        }
        else {
            clipboard.setValues(wave, 0, doubleWave.getSongLength(), isCut);
        }
    }

    public void removeWave(WaveMainPanel wave) {
        int index = wave.getWaveIndex() - 1;
        removeWave(index);
    }


    public void pasteWaveWithOverwriting(WaveMainPanel wave, MouseEvent rightButtonPressMouseEvent, int copyCount) {
        int startPasteIndex = calculateSampleFromWavePixel(rightButtonPressMouseEvent.getX());
        performWaveLengthChangingAction(new ModifyAudioIFace() {
            @Override
            public void modifyAudio() {
                int clipboardLen = clipboard.getMarkEndSample() - clipboard.getMarkStartSample();
                int newLen = wave.pasteWithOverwriting(clipboard.getWave().getSong(), clipboard.getMarkStartSample(),
                                                       startPasteIndex, clipboardLen, copyCount, clipboard.getIsCut());
                alignAllWavesToLenWhileOverwritePasting(newLen, wave.getDoubleWave());
            }
        });
    }


    public void pasteWave(WaveMainPanel wave, MouseEvent rightButtonPressMouseEvent, int copyCount) {
        int startPasteIndex = calculateSampleFromWavePixel(rightButtonPressMouseEvent.getX());
        performWaveLengthChangingAction(new ModifyAudioIFace() {
            @Override
            public void modifyAudio() {
                int clipboardLen = clipboard.getMarkEndSample() - clipboard.getMarkStartSample();
                int copyLen = clipboardLen * copyCount;
                int newLen = wave.paste(clipboard.getWave().getSong(), clipboard.getMarkStartSample(),
                                        startPasteIndex, clipboardLen, copyCount, clipboard.getIsCut());
                alignAllWavesToLen(wave.getDoubleWave(), newLen, startPasteIndex, copyLen);
            }
        });
    }

    private void alignAllWavesToLen(DoubleWave pasteWave, int newLen, int startPasteIndex, int copyLen) {
        // The copied wave has already values set to 0 if there is cutting involved
        for (WaveMainPanel wave : waves) {
            if (pasteWave != wave.getDoubleWave()) {
                wave.setNewDoubleWave(newLen, startPasteIndex, copyLen);
            }
        }

        clipboard.removeWaveFromClipboard();
    }


    public void moveWave(WaveMainPanel wave, MouseEvent rightButtonPressMouseEvent) {
        int startPasteIndex = calculateSampleFromWavePixel(rightButtonPressMouseEvent.getX());

        performWaveLengthChangingAction(new ModifyAudioIFace() {
            @Override
            public void modifyAudio() {
                int oldLen = wave.getDoubleWave().getSongLength();
                int len = getMarkEndXSample() - getMarkStartXSample();
                int newLen = wave.moveWave(getMarkStartXSample(), startPasteIndex, len);
                if (newLen > oldLen) {
                    alignAllWavesToLenWhileOverwritePasting(newLen, wave.getDoubleWave());
                }
                else {
                    clipboard.removeWaveFromClipboard();
                }
            }
        });
    }


    public void cleanWave(WaveMainPanel wave) {
        stopAndModifyAudio(false, new ModifyAudioIFace() {
            @Override
            public void modifyAudio() {
                if (shouldMarkPart) {
                    double[] waveAudio = wave.getDoubleWave().getSong();
                    Utilities.setOneDimArr(waveAudio, getMarkStartXSample(), getMarkEndXSample(), 0);
                }
                else {
                    double[] waveAudio = wave.getDoubleWave().getSong();
                    int len = wave.getDoubleWave().getSongLength();
                    Utilities.setOneDimArr(waveAudio, 0, len, 0);
                }

                wave.reloadDrawValues();
            }
        }, true, false);

    }

    public boolean getIsWaveInClipboard() {
        return clipboard.isWaveInClipboard();
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
/////////////////// DEBUG METHODS
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void debugPrintWaves() {
        ProgramTest.printCharKTimesOnNLines('/');
        ProgramTest.printCharKTimesOnNLines('+');
        for (int i = 0; i < waves.size(); i++) {
            WaveMainPanel w = waves.get(i);
            int divLoc = getJSplitPaneContainingWaveFromWaveIndex(i + 1).getDividerLocation();
            ProgramTest.debugPrint(w.getMinimumSize(), w.getPreferredSize(),
                                   w.getHeight(), divLoc, splitters.get(i).getPreferredSize(), splitters.get(i).getHeight());
            if (divLoc < 0) {
                System.out.println(i + "\t" + divLoc);
            }
        }
        int li = splitters.size() - 1;
        JSplitPane lastSplitter = splitters.get(li);
        if (li != waves.size() - 1) {
            ProgramTest.debugPrint(lastSplitter.getPreferredSize(), lastSplitter.getHeight());
        }
        Component lastEmptyPanel = lastSplitter.getBottomComponent();
        ProgramTest.debugPrint(lastEmptyPanel.getPreferredSize(), lastEmptyPanel.getSize());
    }


    public void debugPrintSplitters() {
        System.out.println("splitters count:\t" + splitters.size());
        for (int i = 0; i < splitters.size(); i++) {

            JSplitPane splitter = splitters.get(i);
            Component comp;
            int waveIndex;
            if (i == 0 || i == splitters.size() - 1) {
                comp = splitter.getTopComponent();
                waveIndex = getWaveIndex(comp);
                ProgramTest.debugPrint(i, " top:", splitter.getDividerLocation(), splitter.getPreferredSize(), splitter.getSize(),
                                       comp.getPreferredSize(), comp.getSize(), waveIndex);
            }

            comp = splitter.getBottomComponent();
            if (comp == null) {
                System.out.println(i);
                System.exit(-1);
            }
            waveIndex = getWaveIndex(comp);
            ProgramTest.debugPrint(i, " bot:", splitter.getDividerLocation(), splitter.getPreferredSize(), splitter.getSize(),
                                   comp.getPreferredSize(), comp.getSize(), waveIndex);
        }
    }


    private JSplitPane[] todoPanes;
    private void debugInitTodoPanes() {
        todoPanes = new JSplitPane[splitters.size()];
        for (int i = 0; i < todoPanes.length; i++) {
            todoPanes[i] = splitters.get(i);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
/////////////////// DEBUG METHODS
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
