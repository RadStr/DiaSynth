package player;


import deprecatedclasses.RobotUserEventsGenerator;
import mixer.*;
import player.control.AudioControlPanel;
import player.control.AudioControlPanelWithZoomAndDecibel;
import player.control.ZoomPanel;
import player.operations.nowave.*;
import player.operations.nowave.arithmetic.LogarithmOnWave;
import player.operations.nowave.arithmetic.MultiplicationOnWave;
import player.operations.nowave.arithmetic.AdditionOnWave;
import player.operations.nowave.arithmetic.PowerOnWave;
import player.wave.WaveMainPanel;
import player.wave.WavePanel;
import synthesizer.synth.audio.AudioThread;
import main.TabChangeIFace;
import player.format.AudioFormatJPanel;
import player.format.AudioFormatJPanelWithConvertFlag;
import player.format.AudioFormatWithSign;
import player.format.ChannelCount;
import player.plugin.ifaces.PluginIFacesForUsers.WithInputWavePackage.WithInputWavePluginIFace;
import player.plugin.ifaces.PluginIFacesForUsers.WithoutInputWavePackage.WithoutInputWavePluginIFace;
import player.plugin.ifaces.PluginDefaultIFace;
import player.plugin.PluginJPanelBasedOnAnnotations;
import player.operations.wave.FillWaveWithOtherWaveOperation;
import player.operations.wave.arithmetic.LogarithmOnWaves;
import player.operations.wave.arithmetic.MultiplicationOnWaves;
import player.operations.wave.arithmetic.AdditionOnWaves;
import player.operations.wave.arithmetic.PowerOnWaves;
import player.operations.nowave.filters.LowPassFilter;
import player.decibel.DecibelMeter;
import player.decibel.SamplesGetterIFace;
import player.drawing.*;
import player.drawing.FFTWindowPanel;
import player.util.BooleanButton;
import player.util.EmptyPanelWithoutSetMethod;
import dialogs.EmptyWaveMakerDialog;
import dialogs.LengthDialog;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.MyLogger;
import Rocnikovy_Projekt.Program;
import Rocnikovy_Projekt.ProgramTest;
import debug.DEBUG_CLASS;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class AudioPlayerPanel extends JPanel implements MouseListener,
        AudioPlayerPanelZoomUpdateIFace, WaveScrollEventCallbackIFace, SamplesGetterIFace,
        TabChangeIFace, AudioControlPanel.VolumeControlGetterIFace, AddWaveIFace {

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
        if(outputAudioFormat.getChannels() < newFormat.getChannels()) {
            channelCountChanged = true;         // The splitters divider locs will be moved, but we don't want to trigger the listeners
        }
        outputAudioFormat = newFormat;
        ChannelCount channelCount = getChannelCount();
        for(WaveMainPanel waveMainPanel : waves) {
            waveMainPanel.updateChannelSliders(channelCount);
        }

        if(shouldConvertAudio) {
            for(WaveMainPanel waveMainPanel : waves) {
                // TODO: SYNTH - AUDIO PLAYER AUDIO THREAD
                waveMainPanel.setWaveToNewSampleRate((int)newFormat.getSampleRate());
                // TODO: SYNTH - AUDIO PLAYER AUDIO THREAD
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
    private void setWaveScrollPanelsSizes(Dimension size) {
        setWaveScrollPanelsSizes(size.width, size.height);
    }

    private void setWaveScrollPanelsSizes(int leftPanelWidth, int rightPanelWidth, int h) {
// TODO: DEBUG
//        ProgramTest.debugPrint("EMPTY PANEL SETTING SIZE:", leftPanelWidth, waves.get(0).getWaveStartX(), rightPanelWidth, waves.get(0).getWaveWidth());
// TODO: DEBUG
        waveScrollerWrapperPanel.setEmptyPanelsSizes(leftPanelWidth, rightPanelWidth, h);
    }

    /**
     * Sets the sizes of panels except the one representing the vertical scrollbar
     * @param leftPanelWidth
     * @param rightPanelWidth
     */
    public void setWaveScrollPanelsSizes(int leftPanelWidth, int rightPanelWidth) {
        setWaveScrollPanelsSizes(leftPanelWidth, rightPanelWidth, 0);
    }

    private Timer waveScrollerPollTimer;

    /**
     * How does adding to the panel works: the bottom panel is always panel of size 0 (So the bottom component can be moved)
     * The inside of the panel are again JScrollPane where at the bottom is wave and at the top is JScrollPane, etc.
     * The first JScrollPane (the most internal one) has at the top wave but also wave at the bottom, it is the only panel which has 2 valid waves inside without any recursion
     */
    private JScrollPane panelWithWaves;
    // TODO: PROGRAMO
    public int getPanelWithWavesHorizontalScrollBarSize() {
        return panelWithWaves.getHorizontalScrollBar().getSize().height;
    }
    // TODO: PROGRAMO
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
    private List<WaveMainPanel> waves;       // TODO: waves - private
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
        if(removedAllWavesFromPlayer) {
            clickPauseButtonIfPlaying();
            audioThread.reset();
        }
    }

    // TODO: PROGRAMO - NEJSEM SI UPLNE JISTEJ JESTLI TO SKUTECNE VSECHNO VYCISTI - MUZE MI TAM ZUSTAT REFERENCE TAKZE SE
    // TODO: PROGRAMO - TO NEUKLIDI PRI GC BA CO HUR CO KDYZ TO BUDE POUZIVAT NEJAKA KOMPONENTA - PAK TO ANI NEBUDE FUNGOVAT SPRAVNE
    private void removeAllWaves() {
        shouldMarkPart = false;
        setEnabledAllMenus(false);
        clipboard.removeWaveFromClipboard();
        waveScrollerPollTimer.stop();
        //removeWave(waves.size() - 1);
//        removeWave(5);
//        for(int i = waves.size() - 1; i >= 0; i++) {
//            removeWave(i);
//        }

        removeOldListeners();
        //panelWithWaves.removeAll();
        panelWithWaves.setViewportView(null);
        for(JSplitPane s : splitters) {
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
////        for(WaveMainPanel wave : waves) {
////            panelWithWaves.remove(wave);
////        }
////
////        waves.clear();
    }

    /**
     * Index starting from 0.
     * @param index
     */
    private void removeWave(int index) {
        int len = waves.size();
        boolean isRemovingLastRemainingWave = len == 1;
        if(isRemovingLastRemainingWave) {
            removeWaveLastRemaining();
        }
        else if(len > 1) {
            removeWaveMoreThanOneRemaining(index);
        }

        postDeletionAction(isRemovingLastRemainingWave);
    }

    private void removeWaveLastRemaining() {
        removeAllWaves();
    }


    private void removeWaveMoreThanOneRemaining(int index) {
        // TODO: DEBUG
        // WaveMainPanel todoLast = waves.get(waves.size() - 1);
        // TODO: DEBUG

        swapWithLastAndRemove(index);
        EmptyPanelWithoutSetMethod zeroSizePanel = new EmptyPanelWithoutSetMethod();
        getLastJSplitPane().setBottomComponent(zeroSizePanel);
        //setNewListeners();
        //setNewLastJSplitPane();
        setSplittersMouseListener();
        // TODO: DEBUG
//        Component jc = getLastJSplitPane().getBottomComponent();
//        if(getLastJSplitPane().getBottomComponent() == todoLast) {
//            System.exit(888);
//        }
        // TODO: DEBUG
    }



//    ono ja nemusim vsechny ty listenery mazat me staci akorat vymazat ten posledni a zmenit ten posledni na novej a to staci
//        a totez nemusim s tim swapovanim proste si zapamatuju posledni vymazu ji
//        nahodim znova listenery a az pak to poswapuju - tohle je vic java proof podle me

    private void swapWithLastAndRemove(int index) {
        WaveMainPanel deletedWave = waves.get(index);
        clipboard.removeWaveFromClipboard(deletedWave);
        JSplitPane lastSplitter = getLastJSplitPane();
        int lastWaveIndex = waves.size() - 1;
        if(index != lastWaveIndex) {
            moveSwapSplitter(index, lastWaveIndex);
        }
        removeOldListeners();
        //removeAdapterFomLastJSplitPaneDivider();

        splitters.remove(splitters.size() - 1);
        waves.remove(waves.size() - 1);
        setNewLastJSplitPane();

        lastSplitter.setTopComponent(null);
        lastSplitter.setBottomComponent(null);

//        this.remove(lastSplitter); - Tohle je spatne to v this vubec neni - v cem ja mam ale problem je ze to vypada ze maji vsechny dividery na sobe ten last listener
//            poznamka jak udelam ty with input wave operations - proste si oznacim cast vlny - a pak dam copy
//            to oznaceni mi zmizi a ted mam vlnu - a kdyz mam vlnu tak povolim dalsi operace jako paste a vsechny ty ostatni
//            co maji input wave
    }

    private void removeOldListeners() {
        removeSplittersMouseListener();
        removeAdapterFomLastJSplitPaneDivider();
    }

    private void setNewListeners() {
        if(waves.size() != 0) {
            setNewLastJSplitPane();
            setSplittersMouseListener();
        }
    }


    private void alignToLongestWave(DoubleWave wave) {
        if(waves.size() != 0) {
            int waveLen = wave.getSongLength();
            int oldWavesLen = waves.get(0).getSongLen();

            if(waveLen < oldWavesLen) {         // Make the wave longer
                wave.setSong(oldWavesLen);
            }
            else if(waveLen > oldWavesLen) {    // Make all other waves longer
                alignAllWavesToLen(waveLen);
            }
        }
    }

    private void alignAllWavesToLen(int newLen) {
        clipboard.removeWaveFromClipboard();
        // The copied wave has already values set to 0 if there is cutting involved
        for(WaveMainPanel wave : waves) {
            wave.setNewDoubleWave(newLen);
        }

        audioThread.wavesLengthChanged();
    }

    private void alignAllWavesToLenWhileOverwritePasting(int newLen, DoubleWave pasteWave) {
        for(WaveMainPanel wave : waves) {
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

        // TODO: PROGRAMO - asi dobre ale je mozny ze se to pozdeji rozbije - proste musim s kazdnou zmenou i resetovat listenery pro splittery
        removeOldListeners();
        // TODO: PROGRAMO

        JSplitPane lastSplitPane;
        if(waves.size() == 0) {
            lastSplitPane = addFirstWave(wave);
        }
        else {
            //removeAdapterFomLastJSplitPaneDivider();
            lastSplitPane = addNonFirstWave(wave);
        }

        //setNewLastJSplitPane();

        // TODO: PROGRAMO
        setNewListeners();
        // TODO: PROGRAMO

        // TODO: PROGRAMO 2
        setVariablesWhichNeededSize();
        // TODO: PROGRAMO 2


        // Take a look at the comment inside addNonFirstWave, this is here for the same reason.
        JScrollBar verticalScrollBar = panelWithWaves.getVerticalScrollBar();
        int maxVerticalScroll = getMaxVerticalScroll();
        SwingUtilities.invokeLater(() -> verticalScrollBar.setValue(maxVerticalScroll));

//        for(WaveMainPanel waveMainPanel : waves) {
//            waveMainPanel.getWave().revalidate();
//            waveMainPanel.getWave().repaint();
//            waveMainPanel.revalidate();
//            waveMainPanel.repaint();
//        }

//        panelWithWaves.revalidate();
//        panelWithWaves.repaint();
//        this.revalidate();
//        this.repaint();
//        thisFrame.revalidate();
//        thisFrame.repaint();
//        thisFrame.validate();
//        thisFrame.repaint();

        updateWavesForMixing();
//        Added because when I create wave using fft window.
//        Unless I call revalidate the horizontal scroll isn't shown and repaint repaints the waves.
        revalidate();
        repaint();
        return lastSplitPane;
    }

    private JSplitPane addFirstWave(DoubleWave wave) {
//        WaveMainPanel newWavePanel = new WaveMainPanel(wave, this, waves.size() + 1, outputAudioFormat.getChannels());
//        waves.add(newWavePanel);        // TODO: PROGRAMO
//        panelWithWaves.add(newWavePanel);

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
// TODO: PROGRAMO - furt stejny
//        newWavePanel.revalidate();
//        newWavePanel.repaint();
//        newWavePanel.setVariablesWhichNeededSize();
// TODO: PROGRAMO
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
        for(WaveMainPanel w : waves) {
            w.updateZoom(getCurrentZoom(), getCurrentZoom(), false, false);
        }
    }

    private JSplitPane addNonFirstWave(DoubleWave wave) {
        // Now just keep adding to the old splitter new splitter which has in top component the sound waves and in bottom component empty panel
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

// TODO: PROGRAMO - height
//        if(lastSplitterDivLoc != -1) {
//            lastSplitterDivLoc = lastSplitterDivLoc + newWavePanel.getPreferredSize().height + DIVIDER_SIZE;
//            lastSplitter.setDividerLocation(lastSplitterDivLoc);
//        }
// TODO: PROGRAMO - height

        waves.add(newWavePanel);
        splitters.add(lastSplitter);
        flattenJSplitPane(lastSplitter);        // Delete borders

// TODO: PROGRAMO - furt stejny
//        newWavePanel.getWave().revalidate();
//        newWavePanel.getWave().repaint();
//        newWavePanel.revalidate();
//        newWavePanel.repaint();
//        newWavePanel.setVariablesWhichNeededSize();
        //newWavePanel.setPrefSizeToMin();
// TODO: PROGRAMO
        return lastSplitter;
    }

    public void addWaves(DoubleWave[] waves) {
        for(DoubleWave wave : waves) {
            addWave(wave);
        }
    }


    private FrameWithFocusControl thisFrame;

    public void setVariablesWhichNeededSize() {
        // TODO: PROGRAMO 2 - musim ten scroll updatovat kdyz pridam novou pisnicku co je delsi nez vsecnho ostatni
        setWaveScrollPanelsSizes();
        // TODO: PROGRAMO 2
        if(!waveScrollerPollTimer.isRunning()) {
            waveScrollerPollTimer.start();
        }
    }

    private void setWaveScrollPanelsSizes() {
        // https://stackoverflow.com/questions/19869751/get-size-of-jpanel-before-setvisible-called
        int maxWaveWidth = Integer.MIN_VALUE;
        WaveMainPanel maxWave = null;
        for(WaveMainPanel w : waves) {
//            w.setVariablesWhichNeededSize();
            int newPossibleMaxWidth = w.getHorizontalScrollSizeForThisWave();
            if(maxWaveWidth < newPossibleMaxWidth) {
                maxWaveWidth = newPossibleMaxWidth;
                maxWave = w;
            }
        }

        setWaveScrollPanelsSizes(maxWave.getWaveStartX(), maxWaveWidth);
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
     * Used for internal caching of wave draw values
     */
    private final int windowCountToTheRight = 0;        // TODO: Prozatim final, kdyztak dat moznost at si to uzivatel nastavi
    public int getWindowCountToTheRight() {
        return windowCountToTheRight;
    }


    /**
     * Used to disable change of preferred size when it is in swapping.
     */
    private boolean isSwapping = false;


    private int currPlayTimeInMillis;
    public void setCurrPlayTimeInMillis(int val) {
        currPlayTimeInMillis = val;
    }
    private String songLenInSecs;

    private double[][] songs = null;

    private int calculateNumberOfWavesIncludedInMixing() {
        int count = 0;
        for(WaveMainPanel w : waves) {
            if(w.getShouldIncludeInMixing()) {
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
        if(waveCount == 0) {
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
     * @param waveCount
     */
    private void setSongs(int waveCount) {
        if(waveCount == 0) {
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
     * @param waveCount
     */
    private void setMultFactors(int waveCount) {
        if(waveCount == 0) {
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
        if(multFactors != null && wave.getShouldIncludeInMixing()) {
            int index = findIndexInMixing(wave);
            multFactors[index][channel] = newValue;
            audioThread.mixer.update(multFactors);
        }
    }

    private int findIndexInMixing(WaveMainPanel wave) {
        for(int i = 0, outIndex = 0; i < waves.size(); i++) {
            WaveMainPanel w = waves.get(i);
            if(wave == w) {
                return outIndex;
            }
            if(w.getShouldIncludeInMixing()) {
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

        //    program = p;
        zoomVariables = new ZoomVariablesAllWaves();          // TODO: Ted takhle natvrdo
        AudioPlayerPanelMouseWheelListener mouseWheelListener = new AudioPlayerPanelMouseWheelListener(this);
        this.addMouseWheelListener(mouseWheelListener);


        splitters = new ArrayList<>();
//        panelWithWaves.setLayout(null);

        //panelWithWaves.setBounds(0, 100, 600, 600);
        //    panelWithWaves.setPreferredSize(new Dimension(100, 100));

//        panelWithWaves.add(waveMainPanel);
//        this.add(panelWithWaves);




        // TODO: The preffered size of the panelWithWaves has to be smaller than the size of to AudioWavePanel, else the scroll doesn't appear




//        waves = new ArrayList<AudioWavePanel>();
//        Container cont = new Container();
//        int yJump = 400;
//        //waveMainPanel.setBounds(0, 0, 200, 200);
//        //waveMainPanel.setBounds(200, 200, 2000, 2000);
//       // waveMainPanel.setBounds(0, 0, 2000, 2000);
//
//        int TODOTestCount = 2;
//        for(int i = 0, y = 0; i < TODOTestCount; i++, y += yJump) {
//            int TODOVymazatMe = 64;
//            AudioWavePanel waveMainPanel = new AudioWavePanel(program, waveWidth, waveStartX, waveEndX,
//                0, yJump, TODOVymazatMe);  // TODO: jen pro testovani ted
//            waveMainPanel.setPreferredSize(new Dimension(150, 150));
//            waves.add(waveMainPanel);
//            panelWithWaves.add(waveMainPanel);
//            cont.add(waveMainPanel);
////            waveMainPanel.setBounds(0, y, this.getWidth(), yJump);
//        }
//
//        panelWithWaves.getViewport().setView(cont);
//        //cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
//        cont.setLayout(new GridLayout(TODOTestCount, 1));
//        this.add(panelWithWaves);
//    //    panelWithWaves = new JScrollPane(waveMainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//    //    panelWithWaves.setBounds(0, 20, 600, 600);


        setSizes(frame.getWidth(), frame.getHeight());
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

        addConvertToMonoToMenu(audioModJMenu);      // TODO: PROGRAMO

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




// TODO: ONLY WAVE
        waveScrollerWrapperPanel = new WaveScrollerWrapperPanel(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS, this);


        waveScrollerPollTimer = new Timer(64, new ActionListener() {        // TODO: Parameter to play with
            @Override
            public void actionPerformed(ActionEvent e) {
                pollMovement();
            }
        });

        panelWithWaves = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //panelWithWaves = new PanelWithWavesJScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        panelWithWaves.getVerticalScrollBar().setUnitIncrement(VERTICAL_SCROLL_UNIT_INCREMENT);
        panelWithWaves.setWheelScrollingEnabled(false);


        JViewport view = panelWithWaves.getViewport();
        //view.addChangeListener(new ViewportChangeListener());


        // TODO: DYNAMIC LABEL
        // I had to change this, because java was doing weird resizing when digit count changes
        // (It just for 2 seconds broke layout and I couldn't find fix, so instead of that I will have 2(3) digits constantly)
        //waves = new WaveArrayList<WaveMainPanel>();
        waves = new ArrayList<WaveMainPanel>();


        // Create TIMESTAMP PANEL
        int currGridY = 1;
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;       // BOTH because else there is space with nothing at the bottom of page
        timestampPanel = new TimestampsPanel(this);
//        timestampPanel.setPreferredSize(new Dimension(800, 100));
//        timestampPanel.setSize(new Dimension(800, 100));
        constraints.gridx = 0;
        constraints.gridy = currGridY;
        currGridY++;
        constraints.weightx = 0.1;
        constraints.weighty = 0.005;
        // Pad is used just for this, because otherwise the timestamps are shown incorrectly in some cases
        // (when added first wave, and making the panel smaller than it is)
        constraints.ipady = 10;
        // TODO: LALA
        this.add(timestampPanel, constraints);
        // TODO: LALA
        constraints.ipady = 0;

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;       // BOTH because else there is space with nothing at the bottom of page
        //this.add(waveMainPanel, BorderLayout.CENTER);


////        // Now just keep adding to the old splitter new splitter which has in top component the sound waves and in bottom component empty panel
//        for(int i = 0; i < 3; i++, currGridY++) {
//            // Change the empty bottom panel to the wave
//            waveMainPanel = new WaveMainPanel(doubleWave, this, waveIndex, outputAudioFormat.getChannels());
//            waveIndex++;
//            splitter.setBottomComponent(waveMainPanel);
////            splitter2.setBottomComponent(waveMainPanel.getWave());   // TODO: ONLY WAVE
//
//            // Add empty panel at the bottom so the bottom wave can be pulled down
//            splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitter, ep);
////            splitter2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitter2, ep);           // TODO: ONLY WAVE
//            // TODO: Tuhle cast s constraints vymazat - protoze je stejne neprirazuju
//            constraints.gridx = 0;
//            constraints.gridy = currGridY;
//            constraints.weightx = 0.1;
//            constraints.weighty = 0.1;
//            splitter.setDividerSize(DIVIDER_SIZE);
////            splitter2.setDividerSize(DIVIDER_SIZE);                      // TODO: ONLY WAVE
////            this.add(splitter, constraints);
//            waves.add(waveMainPanel);
//            splitters.add(splitter);
//            flattenJSplitPane(splitter);        // Delete borders
////            flattenJSplitPane(splitter2);                               // TODO: ONLY WAVE
//        }
        // TODO: PROGRAMO



// TODO: ASI VYMAZAT
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        // https://stackoverflow.com/questions/14426472/detecting-when-jsplitpane-divider-is-being-dragged-not-component-being-resized
//        // The old value is the old position of divider new is the new one, it is in the direction of the splitting
//        splitter.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
//            new PropertyChangeListener() {
//                @Override
//                public void propertyChange(PropertyChangeEvent evt) {
//                    ProgramTest.printNTimes("´´´´´´´´´´´´´´´´´´´´´´´´´´´´´´", 2);
//                    System.out.println(evt.getOldValue() + "\t" + evt.getNewValue());
//                    debugPrintWithSep("\t", evt.getOldValue(), evt.getNewValue());
////                    }
//                }
//            });

        // inside method for initialization of frame components,
// after splitPane is created and added to the frame content pane
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        // TODO: LALA
//        constraints.gridy = 0;
        // TODO: LALA
        this.add(panelWithWaves, constraints);
        panelWithWaves.addMouseWheelListener(mouseWheelListener);

// TODO: ONLY WAVE
//        constraints.gridy = 3;
//        waveScrollerWrapperPanel.setViewportView(splitter2);
//        this.add(waveScrollerWrapperPanel, constraints);


        constraints.weighty = 0;
        constraints.gridy = 3;
        // TODO: LALA
        this.add(waveScrollerWrapperPanel, constraints);
        // TODO: LALA


        AdjustmentListener[] listeners = panelWithWaves.getHorizontalScrollBar().getAdjustmentListeners();
        for(AdjustmentListener l : listeners) {
            System.out.println(l);
        }
        System.out.println("LEN:\t" + listeners.length);
        System.out.println(panelWithWaves.getListeners(java.util.EventListener.class).length);
        System.out.println(panelWithWaves.getViewport().getChangeListeners().length);

        //panelWithWaves.getViewport().removeChangeListener(panelWithWaves.getViewport().getChangeListeners()[0]);
        System.out.println(panelWithWaves.getViewport().getChangeListeners().length);
//        panelWithWaves.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);

        System.out.println(panelWithWaves.getHorizontalScrollBar().getAdjustmentListeners().length);
        System.out.println(panelWithWaves.getVerticalScrollBar().getAdjustmentListeners().length);

// TODO: nemeni nic
//        panelWithWaves.getHorizontalScrollBar().setUnitIncrement(0);
//        panelWithWaves.getHorizontalScrollBar().setBlockIncrement(0);

        ComponentListener resizeListener = new ComponentAdapter(){
            @Override
            public void componentShown(ComponentEvent e) {
                for(DoubleWave d : wavesToAddLater) {
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
                int h = c.getHeight();
                setSizes(w, h);
                callOnResize();

//                timestampPanel.revalidate();
//                timestampPanel.repaint();
//                c.revalidate();
//                c.repaint();
//                panelWithWaves.revalidate();
//                panelWithWaves.repaint();

                if(oldVisibleWidth != w && waves.size() != 0) {
                    oldVisibleWidth = w;
                    visibleWidthChangedCallback();
                    setWaveScrollPanelsSizes();
                }

//                timestampPanel.revalidate();
//                timestampPanel.repaint();

//                waveScrollerWrapperPanel.revalidateEmptyPanel();
//                c.revalidate();
//                c.repaint();
//                panelWithWaves.revalidate();
//                panelWithWaves.repaint();
// TODO: DEBUG
                ProgramTest.debugPrint("WWWImplementation:", w, getVisibleRect().width);
// TODO: DEBUG


// TODO: DEBUG
//                for(WaveMainPanel waveMainPanel : waves) {
//                    ProgramTest.debugPrint(waveMainPanel.getSize());
//                }
//                ProgramTest.debugPrint("EMPTY PANEL HORIZONTAL SIZE:", getEmptyPanelForHorizontalScrollSizeDebug());
// TODO: DEBUG
            }
        };

        this.addComponentListener(resizeListener);

        this.addMouseListener(this);

        currSample = 0;
        currSampleUserSelected = 0;
        timeLineX = 0;
        currPlayTimeInMillis = 0;
        audioThread = new PlayerAudioThread(true, 100, 20);
        audioThread.start();
// TODO: PROGRAMO - potrebuje k nastaveni splittery - horsi je ze ani nevim jestli to potrebuju nebo ne
//        setSplittersMouseListener();
// TODO: PROGRAMO
    }


    private void visibleWidthChangedCallback() {
        for(WaveMainPanel waveMainPanel : waves) {
            waveMainPanel.visibleWidthChangedCallback();
        }
    }


    private void reloadDrawValuesForAllWaves() {
        for(WaveMainPanel w : waves) {
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
        for(int i = 1; i < splitters.size() - 1; i++) {
            splitter = nextSplitter;
            nextSplitter = splitters.get(i);
            listener = new CompoundSplitterChangeListener(splitter, nextSplitter);
            splittersPropertyChangeListeners.add(listener);
            nextSplitter.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, listener);
        }

        // TODO: PROGRAMO - java bug
        if(splitters.size() >= 2) {
            JSplitPane lastSplitter = splitters.get(splitters.size() - 1);
            listener = new CompoundLastSplitterChangeListener(lastSplitter);
            splittersPropertyChangeListeners.add(listener);
            lastSplitter.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, listener);
        }
        // TODO: PROGRAMO - java bug
    }

// TODO: Jen vymazat - jen kopie abych se mel k cemu vracet
//    private class FirstSplitterChangeListener implements PropertyChangeListener {
//        JSplitPane splitter;
//        public FirstSplitterChangeListener(JSplitPane splitter) {
//            this.splitter = splitter;
//        }
//        @Override
//        public void propertyChange(PropertyChangeEvent evt) {
//            WaveMainPanel top;
//            WaveMainPanel bot;
//            top = (WaveMainPanel) splitter.getTopComponent();
//            bot = (WaveMainPanel) splitter.getBottomComponent();
//            int h;
//            h = top.getHeight();
//            top.setPreferredSize(h);
//            h = bot.getHeight();
//            bot.setPreferredSize(h);
//        }
//    }
//
//    // Every splitter except the first one and the last one
//    private class CompoundSplitterChangeListener implements PropertyChangeListener {
//        JSplitPane topSplitter;
//        JSplitPane botSplitter;
//
//        /**
//         * The arguments are the splitters in which are the waves which are divided by this divider.
//         * The waves sizes changes with the move of divider, so we change their preferred sizes.
//         * @param topSplitter
//         * @param botSplitter
//         */
//        public CompoundSplitterChangeListener(JSplitPane topSplitter, JSplitPane botSplitter) {
//            this.topSplitter = topSplitter;
//            this.botSplitter = botSplitter;
//        }
//        @Override
//        public void propertyChange(PropertyChangeEvent evt) {
//            WaveMainPanel top;
//            WaveMainPanel bot;
//            top = (WaveMainPanel) topSplitter.getBottomComponent();
//            bot = (WaveMainPanel) botSplitter.getBottomComponent();
//            int h;
//            h = top.getHeight();
//            top.setPreferredSize(h);
//            h = bot.getHeight();
//            bot.setPreferredSize(h);
//        }
//    }


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
                    ProgramTest.debugPrint("first splitter Prop change:", evt.getNewValue(), evt.getOldValue());
                    // If divider moved down - dif > 0 (so bot component will be smaller, top bigger) else < 0
                    int oldValue = (int) evt.getOldValue();
                    int newValue = (int) evt.getNewValue();
                    if (oldValue <= 0) {
                        System.out.println("OLD SMALLER\t" + oldValue + "\t" + newValue);
                    }
                    if (newValue <= 0) {
                        System.out.println("NEW SMALLER\t" + oldValue + "\t" + newValue);
                    }
                    if (oldValue >= 0) {
                        // TODO: PROGRAMO - ala
                        if(newValue < splitter.getMinimumDividerLocation()) {
                            ProgramTest.debugPrint("First - Smaller than min divider:", newValue, splitter.getMinimumDividerLocation());
                            //return;
                            System.exit(24);
                        }
                        // TODO: PROGRAMO - ala

                        int dif = newValue - oldValue;
                        WaveMainPanel top;
                        WaveMainPanel bot;
                        top = (WaveMainPanel) splitter.getTopComponent();
                        if(waves.size() != 1) {
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
                if(!channelCountChanged) {
                    int oldValue = (int) evt.getOldValue();
                    int newValue = (int) evt.getNewValue();
                    if (oldValue <= 0) {
                        System.out.println("OLD SMALLER\t" + oldValue + "\t" + newValue);
                    }
                    if (newValue <= 0) {
                        System.out.println("NEW SMALLER\t" + oldValue + "\t" + newValue);
                    }
                    if (oldValue >= 0) {
                        int dif = newValue - oldValue;
                        WaveMainPanel top;
                        WaveMainPanel bot;
                        if (newValue < topSplitter.getMinimumDividerLocation()) {
                            ProgramTest.debugPrint("Compound - Smaller than min divider top:", newValue, topSplitter.getMinimumDividerLocation());
                            // TODO: PROGRAMO - ala
                            //return;
                            // TODO: PROGRAMO - ala
                            System.exit(123);
                        }
                        if (newValue < botSplitter.getMinimumDividerLocation()) {
                            ProgramTest.debugPrint("Compound - Smaller than min divider bot:", newValue, botSplitter.getMinimumDividerLocation());
                            // TODO: PROGRAMO - ala
                            //return;
                            // TODO: PROGRAMO - ala
                            System.exit(1234);
                        }
                        top = (WaveMainPanel) topSplitter.getBottomComponent();
                        bot = (WaveMainPanel) botSplitter.getBottomComponent();
//                    top.setPreferredSizeByAdding(dif);
//                    bot.setPreferredSizeByAdding(-dif);


                        ProgramTest.debugPrint("In compound property change",
                                top.getWaveIndex() - 1, top.getPreferredSize(), bot.getWaveIndex() - 1, bot.getPreferredSize());
                        setPrefSizes(bot, top, dif);
                    }
                }
            }
        }
    }


    // TODO: PROGRAMO - java bug
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
            // TODO: PROGRAMO - ala
//            if(oldValue < lastSplitter.getMinimumDividerLocation() || newValue < lastSplitter.getMinimumDividerLocation()) {
//                ProgramTest.debugPrint("Compound last - Smaller than min divider:", newValue, lastSplitter.getMinimumDividerLocation());
//                return;
//            }
//            ProgramTest.debugPrint("last splitter", lastSplitter.getMinimumDividerLocation(), oldValue, newValue);
            // TODO: PROGRAMO - ala


            // || because java doesn't ensure the order of calling listeners
            if(!isSwapping && oldValue != -1 && !channelCountChanged &&
                    !((lastSplitterMoved && !lastSplitterDrag) || lastSplitterDrag)) {
                lastSplitterDrag = true;
                oldLocationFromPropertyChangeListener = oldValue;
                int x = 0;

                //int y = newValue;
                int y = newValue - oldValue;
                ProgramTest.debugPrint("property change last", oldValue, newValue, y, lastSplitter.getDividerLocation(), lastSplitter.getLastDividerLocation());
                MouseEvent mouseEvent = new MouseEvent(lastSplitter, MouseEvent.MOUSE_RELEASED, 0, 0, x, y, 1,false);
                lastSplitterMouseAdapter.mouseReleased(mouseEvent);
                lastSplitterMoved = false;

//                int oldValue = (int) evt.getOldValue();
//                int newValue = (int) evt.getNewValue();
//                if (oldValue >= 0) {
//                    int dif = newValue - oldValue;
//                    WaveMainPanel top;
//                    WaveMainPanel bot;
//                    if (newValue < lastSplitter.getMinimumDividerLocation()) {
//                        ProgramTest.debugPrint("New value:", newValue, lastSplitter.getMinimumDividerLocation());
//                        System.exit(lastSplitter.getMinimumDividerLocation());
//                    }
//
//                    top = (WaveMainPanel) lastSplitter.getTopComponent(); spatne nevim proc
//                    bot = new WaveMainPanel(null, null, -1, -1);    // Doesn't matter
//                    setPrefSizes(bot, top, dif);
//                }
            }
            else {
                resetOldLocationFromPropertyChangeListenerToDefaultVal();
                lastSplitterMoved = false;
            }
        }
    }
    // TODO: PROGRAMO - java bug


    private void setPrefSizes(WaveMainPanel bot, WaveMainPanel top, int dif) {
        if(dif < 0) {       // If moving up
            if (movingDivsRecursively) {
//                    int div1;
//                    if (dif > 0) {
//                        div1 = bot.setPreferredSizeByAdding(dif);
//                    } else {
//                        div1 = top.setPreferredSizeByAdding(dif);
//                    }
//                        dividerRemainder -= div1;

                int todoDif = top.getDif(dif);
                //dividerRemainder -= todoDif;
                if(todoDif == 0) {
                    // When going up I make the upper smaller and the bot set to min
                    movingDivsRecursively = false;
                    ProgramTest.debugPrint("Before compound splitter", top.getWaveIndex() - 1, top.getPreferredSize());
                    top.setPreferredSizeByAdding(dif);
                    ProgramTest.debugPrint("After compound splitter", top.getWaveIndex() - 1, top.getPreferredSize());
                    //        bot.setPreferredSizeByAdding(-todoDif);
                    //    bot.setPreferredSizeByAdding(dif);  // not -dif because we want to make it smaller
                    bot.setPrefSizeToMin();
                }
                else {
                    bot.setPrefSizeToMin();
                }

//                    dividerRemainder -= dif;
//                    if(dividerRemainder == 0) {
//                        movingDivsRecursively = false;
//                        top.setPreferredSizeByAdding(dif);
//                //        bot.setPreferredSizeByAdding(-todoDif);
//                        //bot.setPreferredSizeByAdding(-dif);
//                    }
//                    else {
//                        bot.setPrefSizeToMin();
//                    }
                //TODO: Not need to set the bottom one, already set from the divider down bot.setPreferredSizeByAdding(-dif);
            } else {
                int div1 = top.setPreferredSizeByAdding(dif);
                if (div1 < 0) {
                    movingDivsRecursively = true;
                    dividerRemainder = div1;
                }

                // TODO: PROGRAMO - If it isn't last, else it is already set by the listener of the last splitter
                // If it isn't last, else it is already set by the listener of the last splitter
//                 TODO: PROGRAMO volam to kdyz zmensim ten posledni a tim zvetsim ten nad nim to nechci ale na druhou stranu to chci volat kdyz zvetsuju ten predposledni
//                 TODO: PROGRAMO movingLastSplitter nefunguje protoze to nevynuluju a ani moc nemam kde - proste kdykoliv pohnu s tim last splitter tak to ovlivni ten cokoliv co udelam nasledujiciho - bude to nastaveny na true
//                TODO: PROGRAMO - az to budu zkouset tak to delat i s ruznym DIVIDER_SIZE
                if(bot.getWaveIndex() != waves.size() || !movingLastSplitter) {
                    ProgramTest.debugPrint("Compound:", movingLastSplitter);
                    bot.setPreferredSizeByAdding(-dif);
                }
                else {
                    movingLastSplitter = false;
                }
                // TODO: PROGRAMO
            }

//                if (dividerRemainder == 0) {
//                    movingDivsRecursively = false;
//                }
        }
        else {          // If moving down - there is no recursive movement involved
            top.setPreferredSizeByAdding(dif);
            bot.setPreferredSizeByAdding(-dif);
        }
        debugPrintSplitters();
        ProgramTest.debugPrint(top.getWaveIndex() - 1, top.getMinimumSize(), top.getPreferredSize());
        ProgramTest.debugPrint(bot.getWaveIndex() - 1, bot.getMinimumSize(), bot.getPreferredSize());
    }







    @Deprecated     // I was testing something.
    public void TODOMETHOD() {
        int w = panelWithWaves.getWidth();
        int startX = panelWithWaves.getX();
        int endX = startX + w;
        Point pStartX = new Point(startX + 1, 0);
        SwingUtilities.convertPointToScreen(pStartX, panelWithWaves);
        Point pEndX = new Point(endX - 1, 0);
        SwingUtilities.convertPointToScreen(pEndX, panelWithWaves);
        int halfW = w / 2;
        JSplitPane lastSplitter = getLastJSplitPane();
        Point p1 = new Point(halfW, lastSplitter.getDividerLocation() + 1);  // +1 because mouse reacts to the divider on next pixel
        SwingUtilities.convertPointToScreen(p1, panelWithWaves);

        //https://stackoverflow.com/questions/48837741/java-robot-mousemovex-y-not-producing-correct-results
//                        int maxTimes = 10;
//                        double x;
//                        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
//                        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
//                        for(int count = 0; ((x = MouseInfo.getPointerInfo().getLocation().getX()) < pStartX.x || x > pEndX.x ||
//                            MouseInfo.getPointerInfo().getLocation().getY() != p.y) &&
//                            count < maxTimes; count++) {
//                            bot.mouseMove(p.x, p.y);
//                        }

        RobotUserEventsGenerator userEventsGenerator = new RobotUserEventsGenerator();
        TODOSLEEP(3000);
        userEventsGenerator.moveTo(p1, pStartX, pEndX);
        TODOSLEEP(3000);
        userEventsGenerator.click(InputEvent.BUTTON1_DOWN_MASK);
        userEventsGenerator.click(InputEvent.BUTTON1_DOWN_MASK);
        Point p2 = new Point(p1.x, p1.y + 20);
        TODOSLEEP(3000);
        userEventsGenerator.moveTo(p2, pStartX, pEndX);
        TODOSLEEP(3000);
        userEventsGenerator.release(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void TODOSLEEP(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private int convertToPixelMovement(int increaseSpeed) {
//        int movement = increaseSpeed / 5; // Every 5 moved pixels the speed of scrolling/making the wave larger speeds up by 1 pixel per drag
//        return movement;
        return increaseSpeed;
    }

    private int increaseJScrollPane(int increaseSpeed) {
        JViewport view = panelWithWaves.getViewport();
        Point oldPos = view.getViewPosition();
        int increasedSize = convertToPixelMovement(increaseSpeed);
        if(increasedSize > 0) {
            int bottomY = oldPos.y + view.getViewRect().height;
            int viewH = view.getViewSize().height;
            if (bottomY == viewH) {     // Increase the size of JSplitPane
                WaveMainPanel wave = waves.get(waves.size() - 1);
                Dimension prefSize = wave.getPreferredSize();
                wave.setPreferredSizeByAdding(increasedSize);
                wave.revalidate();
                wave.repaint();
            } else {                    // Just scroll
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



// TODO: Vymazat - vyhodil jsem to do toho WaveArrayListu
//    /**
//     * Calls updateWaveIndexTextFields if digt count changed.
//     */
//    private void upgradeTextFieldIfDigitCountChanges() {
//        int oldDigitCount = getDigitCount(waves.size());
//        int NEW_SIZE = 0;                               // TODO:
//        int newDigitCount = getDigitCount(NEW_SIZE);
//        System.exit(666);                       // TODO:
//        // If digit count changed
//        if(oldDigitCount != newDigitCount) {
//            updateWaveIndexTextFields(newDigitCount);
//        }
//    }
//
//    /**
//     * Needs to be called to every time wave count is changed. Respectively every time it gets/loses new digit.
//     * Upgrades the size of the text labels.
//     */
//    private void updateWaveIndexTextFields() {
//        int digitCount = getDigitCount(waves.size());
//        updateWaveIndexTextFields(digitCount);
//    }
//
//    private void updateWaveIndexTextFields(int digitCount) {
//        int len = waves.size();
//        if(len > 0) {
//            WaveMainPanel wave;
//            wave = waves.get(0);
//            Dimension newSize = wave.upgradeWaveIndexTextFieldPreferredSize(digitCount);
//            for(int i = 1; i < len; i++) {
//                wave = waves.get(i);
//                wave.upgradeWaveIndexTextFieldPreferredSize(newSize);
//            }
//        }
//    }
//
//
//
//    private int getDigitCount(int number) {
//        int digitCount = 1;
//        while(number >= 10) {
//            digitCount++;
//            number /= 10;
//        }
//        return digitCount;
//    }


    int TODOIND = 0;
    /**
     * Swaps 2 waves. Indexes in parameters are indexed from 1
     * @param oldIndex is the old index of the wave. Starting at 1
     * @param newIndex is the new index of the wave. Starting at 1
     */
    public void swapSplitterComponents(int oldIndex, String oldIndexString, int newIndex, String newIndexString) {
        for(int i = 0; i < waves.size(); i++) {
            isSwapping = true;
        }
        System.out.println(",,," + (TODOIND++));
        debugPrintSplitters();
// TODO: Jen Zkouseni s tim proc nefunguje reset - jestli to nahodou neni tim ze tam je malo mista
//        debugPrintSplitters();
//        JViewport view = panelWithWaves.getViewport();
//        Dimension oldViewSize = view.getViewSize();
//        Dimension newViewSize = new Dimension(oldViewSize.visibleWidth, oldViewSize.height + 200);
//        view.setViewSize(newViewSize);


        int oldIndexZero = oldIndex - 1;
        int newIndexZero = newIndex - 1;
        WaveMainPanel waveMainPanel1 = waves.get(oldIndexZero);
        WaveMainPanel waveMainPanel2 = waves.get(newIndexZero);
        System.out.println(splitters.get(0).getTopComponent());
        System.out.println(splitters.get(0).getBottomComponent());

        if(panelWithWaves.getViewport().getViewPosition().y < 0) {
            System.out.println(panelWithWaves.getViewport().getViewSize().height);
            System.exit(-1000);    // TODO:
        }

        swap2WavesIndexes(oldIndex, oldIndexString, oldIndexZero,
                newIndex, newIndexString, newIndexZero, waveMainPanel1, waveMainPanel2);

        // Now the indexes are swapped, so waveMainPanel2 has lowerBound oldIndexZero. and waveMainPanel1 lowerBound newIndexZero.
        int waveMainPanel1Index = newIndexZero;
        int waveMainPanel2Index = oldIndexZero;
        //        One component can't be part of 2 splitpanes, so we have to take them out and then swap them
        swapComponentsInSplitters(waveMainPanel1Index, waveMainPanel2Index);
//        if(DEBUG_CLASS.DEBUG) {
//            System.out.println(splitters.get(0).getTopComponent());
//            System.out.println(splitters.get(0).getBottomComponent());
//            ProgramTest.debugPrintWithSep(waveMainPanel1Index, waveMainPanel2Index);
//            ProgramTest.debugPrintWithSep(waveMainPanel1, waveMainPanel2);
//            System.out.println();
//        }
//
//        // Now solve the problem that after swaping the sizes of waves changes
//        // Basically we just go through all the splitpanes between the waves
//        // and change the divider of location of the by the difference of the sizes of the swapped components
//        int lowerBound;
//        int upperBound;
//        int topComponentHeight;
//        int botComponentHeight;
//        int divLocDif;
//        boolean containsFirst = false;
//        if(waveMainPanel2Index > waveMainPanel1Index) {
//            lowerBound = waveMainPanel1Index;
//            upperBound = waveMainPanel2Index;
//            if(waveMainPanel1Index == 0) {
//                containsFirst = true;
//            }
//            topComponentHeight = waveMainPanel1.getPreferredSize().height;
//            topComponentHeight = waveMainPanel1.getHeight();
//            botComponentHeight = waveMainPanel2.getPreferredSize().height;
//            botComponentHeight = waveMainPanel2.getHeight();
//        }
//        else {
//            lowerBound = waveMainPanel2Index;
//            upperBound = waveMainPanel1Index;
//            if(waveMainPanel2Index == 0) {
//                containsFirst = true;
//            }
//            topComponentHeight = waveMainPanel2.getPreferredSize().height;
//            topComponentHeight = waveMainPanel2.getHeight();
//            botComponentHeight = waveMainPanel1.getPreferredSize().height;
//            botComponentHeight = waveMainPanel1.getHeight();
//        }
//        divLocDif = topComponentHeight - botComponentHeight;
//        ProgramTest.printCharKTimesOnNLines('*');
//        ProgramTest.debugPrintWithSep(topComponentHeight, botComponentHeight, divLocDif);
//
//
//        // Set the top swapped panel. It is the divLocation of the old one + div
////        ProgramTest.debugPrintWithSep(topPane.getDividerLocation());
////        topPane.setDividerLocation(botPaneDivLoc + divLocDif);
////        ProgramTest.debugPrintWithSep(topPane.getDividerLocation());
////        lowerBound++;
//
//        // Update the divider locations
//        // Set the middle. Just add the difference to every component. There is no middle part if there are no waves in between.
//
//        for(int TODODEBUG = 0; TODODEBUG < splitters.size(); TODODEBUG++) {
//            JSplitPane tmp = splitters.get(TODODEBUG);
//            ProgramTest.debugPrintWithSep(tmp.getDividerLocation(), '+');
//        }
//        JSplitPane pane;
//        int index = upperBound - 1;
//        for(; index >= lowerBound; index--) {
//            pane = getJSplitPaneContainingWaveFromWaveIndex(index + 1);
//            int divLoc = pane.getDividerLocation();
//            pane.setDividerLocation(divLoc + divLocDif);
//        }
//        for(int TODODEBUG = 0; TODODEBUG < splitters.size(); TODODEBUG++) {
//            JSplitPane tmp = splitters.get(TODODEBUG);
//            ProgramTest.debugPrintWithSep(tmp.getDividerLocation(), '-');
//        }
//
////        for(; lowerBound < upperBound; lowerBound++) {
////            pane = getJSplitPaneContainingWaveFromWaveIndex(lowerBound + 1);
//////            if(lowerBound == 0) {
////////                pane.setDividerLocation(topComponentHeight);
////////            }
////            int divLoc = pane.getDividerLocation();
////            pane.setDividerLocation(divLoc + divLocDif);
////        }
//        // Set the bottom swapped panel. It is the dividerLoc - the difference
//// TODO: Vymazat        ProgramTest.debugPrintWithSep(botPane.getDividerLocation());
//        //botPane.setDividerLocation(topPaneDivLoc - divLocDif);
//        //botPane.setDividerLocation(botPane.getDividerLocation() - divLocDif);
//// TODO: Vymazat        ProgramTest.debugPrintWithSep(botPane.getDividerLocation());
//// TODO: Vymazat        ProgramTest.debugPrintWithSep(topPane.getDividerLocation(), botPane.getDividerLocation());
//
//
//

// TODO: Varianta pres minimum - ale pak to nejde zmensovat dragovanim protoze to nejde zmensit pod tu min size - a navic obcas se stane ze to tu minSize ignoruje
//        Dimension[] TODOoldMins = new Dimension[waves.size()];
//        for(int i = 0; i < waves.size(); i++) {
//            JPanel w = waves.get(i);
//            TODOoldMins[i] = w.getMinimumSize();
//            w.setMinimumSize(w.getPreferredSize());
//        }
//        panelWithWaves.revalidate();
//        panelWithWaves.repaint();

//        for(int i = 0; i < waves.size(); i++) {
//            JPanel w = waves.get(i);
//            w.setMinimumSize(TODOoldMins[i]);
//        }



///////////////////////////////////////////////////////////////////// TODO:
//        JViewport view = panelWithWaves.getViewport();
//        //if(view.getViewSize().height != 653) System.exit(view.getViewSize().height);    // TODO:
//        if(view.getViewPosition().y < 0) {
//            System.out.println(view.getViewSize().height);
//            System.exit(-100);    // TODO:
//        }
//        Point oldViewPos = view.getViewPosition();
//        if(oldViewPos.y != 0) {
//            System.out.println(oldViewPos);
//        }
//        for(int i = splitters.size() - 1; i >= 0; i--) {
//            JSplitPane debugtmp = splitters.get(i);
//            debugtmp.setTopComponent(null);
//            debugtmp.setBottomComponent(null);
//            panelWithWaves.remove(debugtmp);
////            debugtmp.resetToPreferredSizes();
//        }
//
//        JSplitPane debugtmp;
//        for(int i = 0; i < waves.size(); i++) {
//            if(i == 0) {
//                debugtmp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, waves.get(0), waves.get(1));
//                splitters.set(0, debugtmp);
//                i++;
//            }
//            else {
//                debugtmp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitters.get(i - 2), waves.get(i));
//                splitters.set(i-1, debugtmp);
//            }
//            debugtmp.setDividerSize(DIVIDER_SIZE);
//            flattenJSplitPane(debugtmp);
//        //    debugtmp.resetToPreferredSizes();
//        }
//        int tmpInd = splitters.size() - 1;
//        debugtmp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitters.get(tmpInd - 1), new EmptyPanelWithoutSetMethod());
//        splitters.set(tmpInd, debugtmp);
//        debugtmp.setDividerSize(DIVIDER_SIZE);
//        flattenJSplitPane(debugtmp);
////        this.remove(panelWithWaves);
////
////        panelWithWaves.setViewportView(debugtmp);
////        GridBagConstraints constraints = new GridBagConstraints();
////        constraints.fill = GridBagConstraints.BOTH;
////        constraints.gridx = 0;
////        constraints.gridy = 2;
////        constraints.weightx = 1;
////        constraints.weighty = 1;
////        this.add(panelWithWaves, constraints);
//
//        panelWithWaves.setViewportView(debugtmp);
//        view = panelWithWaves.getViewport();
//        view.setViewPosition(oldViewPos);
//
//        panelWithWaves.revalidate();
//        panelWithWaves.repaint();
//        debugPrintWaves();
//
//        // TODO: Debug tests
//        if(view.getViewPosition().y < 0) {
//            System.out.println(view.getViewSize().height);
//            System.exit(view.getViewPosition().y);    // TODO:
//        }
//        if(debugtmp.getDividerLocation() == -1) {
//            System.out.println("TODO - -1");
//        }
/////////////////////////////////////////////////////////////////// TODO:
//for(int i = 0; i < splitters.size(); i++) {
//    JSplitPane split = splitters.get(i);
//    split.resetToPreferredSizes();
//}

        debugInitTodoPanes();
        //      debugPrintSplitters();
        panelWithWaves.validate();
        panelWithWaves.revalidate();
        //      debugPrintSplitters();
        panelWithWaves.repaint();
        //setDivLocsBasedOnPrefSize();
//
//        for(int i = splitters.size() - 1; i >= 0; i--) {
//            JSplitPane split = splitters.get(i);
//            split.resetToPreferredSizes();
//        }
//        // TODO: Testing
//        JPanel wave = waves.get(waves.size() - 2);
//        for(int i = 0; i < splitters.size(); i++) {
//            JSplitPane split = splitters.get(i);
//            if(split.getBottomComponent() == wave) {
//                if(i != splitters.size() - 3) {
//                    System.exit(i);
//                }
//            }
//        }
//        JViewport view = panelWithWaves.getViewport();


//        view.setViewSize(new Dimension(view.getSize().visibleWidth, 10000));
        // TODO: Testing
        //     debugCheckCorrectnessOfSetDivLocsBasedOnPrefSize();
        panelWithWaves.revalidate();
        //    debugCheckCorrectnessOfSetDivLocsBasedOnPrefSize();
        panelWithWaves.repaint();
        //    debugCheckCorrectnessOfSetDivLocsBasedOnPrefSize();
        //      debugPrintSplitters();
        //    debugCheckCorrectnessOfSetDivLocsBasedOnPrefSize();
        //    debugPrintWaves();
        //    debugCheckCorrectnessOfSetDivLocsBasedOnPrefSize();

        // TODO: Problem byl v tom ze ono se to v tyhle chvili ejste neupdatovalo a to co s cim jsem pracoval jsou ty stary velikosti jeste pred swapem
        // TODO: Proto nefungoval ani ten resetToPreferredSizes - protoze to jeste nebylo zmeneny - ty preferred sizes byly porad ty stary - takze se to zmenilo ale ne do niceho novyho
        //setDivLocsBasedOnPrefSize();
        //debugCheckCorrectnessOfSetDivLocsBasedOnPrefSize();
//        for(int i = splitters.size() - 1; i >= 0; i--) {
//            JSplitPane split = splitters.get(i);
//            split.resetToPreferredSizes();
//        }
        //    setDivLocsBasedOnPrefSize();
//        for(int j = 0; j < 400; j++) {
//            for (int i = splitters.size() - 1; i >= 0; i--) {
//                JSplitPane split = splitters.get(i);
//                split.resetToPreferredSizes();
//            }
//        }
        //      debugPrintSplitters();
        //      System.out.println("§§§§§");
        //setDivLocsBasedOnPrefSize();
        for(int i = 0; i < waves.size(); i++) {
            isSwapping = false;
        }
        //debugPrintSplitters();
        debugPrintSplitters();

// TODO: Tohle nemusim revalidate, staci jen ten hlavni panel
//        this.revalidate();
//        this.repaint();





        if(DEBUG_CLASS.DEBUG) {
            for (int i = 0; i < waves.size(); i++) {
                System.out.println("VAL:\t" + i + "\t" + waves.get(i).getWaveIndex());
            }

            ProgramTest.printNTimes("===============================================", 2);

            JSplitPane splitter = splitters.get(0);
            WaveMainPanel waveMainPanelDEBUG;
            waveMainPanelDEBUG = (WaveMainPanel) splitter.getTopComponent();
            int indexDEBUG;
            indexDEBUG = waveMainPanelDEBUG.getWaveIndex();
            System.out.println("VAL:\t" + 0 + "\t" + indexDEBUG);
            waveMainPanelDEBUG = (WaveMainPanel) splitter.getBottomComponent();
            indexDEBUG = waveMainPanelDEBUG.getWaveIndex();
            System.out.println("VAL:\t" + 1 + "\t" + indexDEBUG);

            if (waves.get(0) != splitters.get(0).getTopComponent()) {
                System.exit(0);
            }
            if (waves.get(1) != splitters.get(0).getBottomComponent()) {
                System.exit(1);
            }

            for (int i = 1; i < splitters.size() - 1; i++) {
                splitter = splitters.get(i);
                waveMainPanelDEBUG = (WaveMainPanel) splitter.getBottomComponent();
                indexDEBUG = waveMainPanelDEBUG.getWaveIndex();
                System.out.println("VAL:\t" + (i + 1) + "\t" + indexDEBUG);
                if (waves.get(i + 1) != splitter.getBottomComponent()) {
                    System.exit(i + 1);
                }
            }
            System.out.println(waves.size() == splitters.size());
            int TODO = 0;
        }
    }


    private void setDivLocsBasedOnPrefSize() {
        System.out.println("§§§§§");
        debugPrintSplitters();

        int[] newDivLocs = new int[splitters.size()];
        Dimension[] prefSizes = new Dimension[splitters.size()];
        for(int i = 0; i < prefSizes.length; i++) {
            JSplitPane split = splitters.get(i);
            int divLoc;
            Dimension prefSize = null;
            if(i == 0) {
                prefSize = split.getPreferredSize();
                Dimension wavePrefSize = waves.get(0).getPreferredSize();
                divLoc = wavePrefSize.height;
            }
            else {
                JSplitPane prevSplit = splitters.get(i - 1);
                prefSize = split.getPreferredSize();
                Dimension prevPrefSize = prevSplit.getPreferredSize();
                divLoc = prevPrefSize.height;
            }
            prefSize = new Dimension(prefSize.width, prefSize.height);
            prefSizes[i] = prefSize;
            newDivLocs[i] = divLoc;
        }


        for(int i = 0; i < splitters.size(); i++) {
            JSplitPane split = splitters.get(i);
            Dimension oldPrefSize = split.getPreferredSize();

            split.setSize(prefSizes[i]);
            split.setDividerLocation(newDivLocs[i]);
            Dimension prefSize = new Dimension(prefSizes[i].width, prefSizes[i].height);
            split.setPreferredSize(prefSize);

            Dimension todoRealSize = split.getSize();
            Dimension todoPrefSize = split.getPreferredSize();
            ProgramTest.debugPrint(i, split.getDividerLocation(), todoPrefSize, todoRealSize);

            if(!oldPrefSize.equals(split.getPreferredSize())) {
                System.out.println(oldPrefSize);
                System.out.println(split.getPreferredSize());
                System.exit(-10);
            }
        }

        for(int i = 0; i < waves.size(); i++) {
            WaveMainPanel w = waves.get(i);
            Dimension pf = w.getPreferredSize();
            Dimension d = new Dimension(pf.width, pf.height);
            w.setSize(d);
        }

//
////        for(int i = 0; i < splitters.size(); i++) {
//        for(int i = splitters.size() - 1; i >= 0; i--) {
//            JSplitPane split = splitters.get(i);
//            Dimension realDivLoc = null;
//            if(i == 0) {
//                Dimension prefSize = split.getPreferredSize();
//                split.setSize(prefSize);
//                Dimension wavePrefSize = waves.get(0).getPreferredSize();
//                realDivLoc = wavePrefSize;
//                split.setDividerLocation(wavePrefSize.height);
//                //setDivLoc(split, wavePrefSize.height);
//                if(split.getDividerLocation() != wavePrefSize.height) System.exit(-1);
//
//                if(!prefSize.equals(split.getSize())) {
//                    System.out.println(prefSize);
//                    System.out.println(split.getSize());
//                    System.exit(-2);
//                }
//            }
//            else {
//                JSplitPane prevSplit = splitters.get(i - 1);
//                Dimension prefSize = split.getPreferredSize();
//                split.setSize(prefSize);
//                Dimension prevPrefSize = prevSplit.getPreferredSize();
//                realDivLoc = prevPrefSize;
//                split.setDividerLocation(prevPrefSize.height);
//                //setDivLoc(split, prevPrefSize.height);
//                if(split.getDividerLocation() != prevPrefSize.height) System.exit(-3);
//
//                if(!prefSize.equals(split.getSize())) {
//                    System.out.println(prefSize);
//                    System.out.println(split.getSize());
//                    System.exit(-4);
//                }
//            }
//
//            Dimension todoRealSize = split.getSize();
//            Dimension todoPrefSize = split.getPreferredSize();
//            ProgramTest.debugPrintWithSep(i, split.getDividerLocation(), realDivLoc, todoPrefSize, todoRealSize);
//        }

        System.out.println("§§§§§");
        debugPrintSplitters();
        System.out.println("§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§");
    }



    private void setDivLoc(JSplitPane splitter, int newDivLoc) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                splitter.setDividerLocation(newDivLoc);
            }
        });
    }


    private void debugCheckCorrectnessOfSetDivLocsBasedOnPrefSize() {
        System.out.println("--------------------------");
        for(int i = 0; i < splitters.size(); i++) {
            JSplitPane split = splitters.get(i);
            ProgramTest.debugPrint(i, split.getDividerLocation());
            if(i == 0) {
                Dimension wavePrefSize = waves.get(0).getPreferredSize();
//                if(split.getDividerLocation() != 10000) {
//                    System.out.println(i);
//                    System.exit(-2);
//                }
                if(split.getDividerLocation() != wavePrefSize.height) {
                    System.out.println(i);
                    System.exit(-2);
                }
            }
            else {
                JSplitPane prevSplit = splitters.get(i - 1);
                Dimension prevPrefSize = prevSplit.getPreferredSize();
//                if(split.getDividerLocation() != 10000) {
//                    System.out.println(i);
//                    System.exit(-3);
//                }
                if(split.getDividerLocation() != prevPrefSize.height) {
                    System.out.println(i);
                    System.exit(-3);
                }
            }
        }
    }

    private void removeAdapterFomLastJSplitPaneDivider() {
        if(lastSplitterMouseAdapter != null) {
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
        for(int i = 0; i < splitters.size(); i++) {
            addMouseAdapterToDivider(splitters.get(i), anySplitterDraggedListener);
        }
    }
    private void removeAllAnySplitterDraggedListeners() {
        for(int i = 0; i < splitters.size(); i++) {
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




    // TODO: PROGRAMO - java bug
    private boolean lastSplitterMoved = false;
    // TODO: PROGRAMO - java bug

    private boolean movingLastSplitter = false;
    private SplitPaneUI lastSplitterUI;
    private boolean lastSplitterDrag = false;
    public boolean getIsLastSplitterDragged() {
        return lastSplitterDrag;
    }
    private void setNewLastJSplitPane() {
        if(splitters.size() >= 2) {
            JSplitPane lastSplitter = getLastJSplitPane();
            lastSplitterUI = lastSplitter.getUI();

            lastSplitterMouseAdapter = new MouseAdapter() {
                private int previousY = -1;
                private int TODOclick = 0;

                @Override
                public void mouseClicked(MouseEvent e) {
// TODO: VYMAZAT !!!!
//                    // TODO:
//                    debugPrintWaves();
//                    TODOclick++;
//                    final int NEW_VAL = TODOclick * 50;
//
//
//                    //for(int i = 0; i < waves.size(); i++) {
//                    for (int i = waves.size() - 1; i >= 0; i--) {
//                        JPanel tmpWave = waves.get(i);
//                        Dimension oldPrefSize = tmpWave.getPreferredSize();
//                        int dif = NEW_VAL - oldPrefSize.height;
//                        Dimension d = new Dimension(oldPrefSize.width, NEW_VAL);
//                        tmpWave.setPreferredSize(d);
//                        tmpWave.setMinimumSize(d);
//                        //    TODO: Zmena te preferred size u te vlny ovlivni ten splitter - takze tohle podtim vubec neni potreba pro update PrefSize u splitteru
//                        int currDif = (i + 1) * dif;      // i+1 * dif because we make larger all of them, so it sums up
//                        JSplitPane currSplit = splitters.get(i);
//                        d = currSplit.getPreferredSize();
//                        Dimension newDim;
//                        if (i == 0) {
//                            newDim = new Dimension(d.width, d.height + 2 * currDif);
//                        } else if (i < waves.size() - 1) {
//                            JSplitPane prevSplit = splitters.get(i - 1);
//                            Dimension prevDim = prevSplit.getPreferredSize();
//                            newDim = new Dimension(d.width, prevDim.height + tmpWave.getPreferredSize().height + 5);
//                        } else {          // The last component, where is the empty panel - so it same as the else if above but we the pref size height is 0
//                            JSplitPane prevSplit = splitters.get(i - 1);
//                            Dimension prevDim = prevSplit.getPreferredSize();
//                            newDim = new Dimension(d.width, prevDim.height + 5);
//                        }
////                        currSplit.setPreferredSize(newDim);
//
//                        //    currSplit.setDividerLocation(currSplit.getDividerLocation() + currDif);
//                        //    currSplit.setSize(currSplit.getPreferredSize());
//                        //   tmpWave.revalidate();
//                        //   tmpWave.repaint();
//                    }
//
//
//                    //            if(TODOclick == 1) {
////                    for (int i = splitters.size() - 1; i >= 0; i--) {
////                        JSplitPane currSplit = splitters.get(i);
////                        //currSplit.setLayout(new BorderLayout());
////                        //currSplit.setLayout(new BoxLayout(currSplit, BoxLayout.Y_AXIS));
////                        //currSplit.setLayout(new BoxLayout(currSplit, BoxLayout.X_AXIS));
////                        //currSplit.setLayout(new GridLayout(0, 1));
////                        //currSplit.setLayout(new GridBagLayout());
////
////
////                        int dif = NEW_VAL - 50;         // -50 is the old preferredSize height of wave
////                        int currDif = (i + 1) * dif;      // i+1 * dif because we make larger all of them, so it sums up)
//////                            currSplit.setSize(currSplit.getPreferredSize());
////
////                        JPanel tmpWave = waves.get(i);
////                        tmpWave.setSize(tmpWave.getPreferredSize());
////
//////                            currSplit.setDividerLocation(currSplit.getDividerLocation() + currDif);
////                    }
////
////                    for (int i = 0; i < splitters.size() - 1; i++) {
////                        JSplitPane split = splitters.get(i);
////                        if (i == 0) {
////                            split.setTopComponent(null);
////                        }
////                        split.setBottomComponent(null);
////                    }
////                    for (int i = 0; i < splitters.size() - 1; i++) {
////                        JSplitPane split = splitters.get(i);
////                        if (i == 0) {
////                            split.setTopComponent(waves.get(0));
////                        }
////                        split.setBottomComponent(waves.get(i + 1));
////                    }
//
//
//                    final int CONST_INT = 0;
//                    for (int i = splitters.size() - 1; i >= 0; i--) {
//                        JSplitPane currSplit = splitters.get(i);
//                        //currSplit.setLayout(new BorderLayout());
//                        //currSplit.setLayout(new BoxLayout(currSplit, BoxLayout.Y_AXIS));
//                        //currSplit.setLayout(new BoxLayout(currSplit, BoxLayout.X_AXIS));
//                        //currSplit.setLayout(new GridLayout(0, 1));
//                        //currSplit.setLayout(new GridBagLayout());
//
//
//                        int dif = NEW_VAL - 50;         // -50 is the old preferredSize height of wave
//                        int currDif = (i + 1) * dif;      // i+1 * dif because we make larger all of them, so it sums up)
//
//                        Dimension pf;
//                        pf = currSplit.getPreferredSize();
//                        Dimension d = new Dimension(pf.width, pf.height);
//                        int jump = CONST_INT * (i + 1);
//                        d.height += jump + CONST_INT;
//                        Dimension d2 = new Dimension(d.width, d.height);
//                        if(i != splitters.size() - 1) {
//                            //currSplit.setSize(currSplit.getPreferredSize());
//                   //         currSplit.setPreferredSize(d2); // Problem is that the preferred size of wave and currSplit is the same
////                            currSplit.setSize(d);
//            //               currSplit.setDividerLocation(currSplit.getDividerLocation() + currDif);
////                            currSplit.setDividerLocation(currSplit.getDividerLocation() + currDif + jump);
//                        }
//                        else {
//                    //        currSplit.setDividerLocation(currSplit.getDividerLocation() + currDif);
//
//                        //    d.height += CONST_INT;
//                            d.height -= CONST_INT;
////                            currSplit.setSize(d);
//            //                currSplit.setDividerLocation(currSplit.getDividerLocation() + currDif);
////                            currSplit.setDividerLocation(currSplit.getDividerLocation() + currDif + jump);
//
////                            Dimension d2 = new Dimension(d.visibleWidth, d.height + CONST_INT);
////                            //specVal.height += 20000;
////                            currSplit.setPreferredSize(d);
////                            currSplit.setSize(d2);
////                            currSplit.setDividerLocation(currSplit.getDividerLocation() + currDif + CONST_INT);
//                        }
//                    }
//                    for(int i = 0; i < waves.size(); i++) {
//                        JPanel w = waves.get(i);
//                        Dimension d = new Dimension(w.getPreferredSize().width, w.getPreferredSize().height + CONST_INT);
//                        w.setPreferredSize(d);
//                        Dimension d2 = new Dimension(d.width, d.height);
//                        w.setSize(d);
//                    }
//
//
//
//
//
//
////                    final int CONST_INT = 200;
////                    for (int i = splitters.size() - 1; i >= 0; i--) {
////                        JSplitPane currSplit = splitters.get(i);
////                        //currSplit.setLayout(new BorderLayout());
////                        //currSplit.setLayout(new BoxLayout(currSplit, BoxLayout.Y_AXIS));
////                        //currSplit.setLayout(new BoxLayout(currSplit, BoxLayout.X_AXIS));
////                        //currSplit.setLayout(new GridLayout(0, 1));
////                        //currSplit.setLayout(new GridBagLayout());
////
////
////                        int dif = NEW_VAL - 50;         // -50 is the old preferredSize height of wave
////                        int currDif = (i + 1) * dif;      // i+1 * dif because we make larger all of them, so it sums up)
////
////                        Dimension pf;
////                        pf = currSplit.getPreferredSize();
////                        Dimension d = new Dimension(pf.visibleWidth, pf.height);
////                        int jump = CONST_INT * (i + 1);
////                        d.height += jump + CONST_INT;
////                        Dimension d2 = new Dimension(d.visibleWidth, d.height);
////                        if(i != splitters.size() - 1) {
////                            //currSplit.setSize(currSplit.getPreferredSize());
////                            //         currSplit.setPreferredSize(d2); // Problem is that the preferred size of wave and currSplit is the same
////                            currSplit.setSize(d);
////                            //               currSplit.setDividerLocation(currSplit.getDividerLocation() + currDif);
////                            currSplit.setDividerLocation(currSplit.getDividerLocation() + currDif + jump);
////                        }
////                        else {
////                            //        currSplit.setDividerLocation(currSplit.getDividerLocation() + currDif);
////
////                            //    d.height += CONST_INT;
////                            d.height -= CONST_INT;
////                            currSplit.setSize(d);
////                            //                currSplit.setDividerLocation(currSplit.getDividerLocation() + currDif);
////                            currSplit.setDividerLocation(currSplit.getDividerLocation() + currDif + jump);
////
//////                            Dimension d2 = new Dimension(d.visibleWidth, d.height + CONST_INT);
//////                            //specVal.height += 20000;
//////                            currSplit.setPreferredSize(d);
//////                            currSplit.setSize(d2);
//////                            currSplit.setDividerLocation(currSplit.getDividerLocation() + currDif + CONST_INT);
////                        }
////                    }
////                    for(int i = 0; i < waves.size(); i++) {
////                        JPanel w = waves.get(i);
////                        Dimension d = new Dimension(w.getPreferredSize().visibleWidth, w.getPreferredSize().height + CONST_INT);
////                        w.setPreferredSize(d);
////                        Dimension d2 = new Dimension(d.visibleWidth, d.height);
////                        w.setSize(d);
////                    }
//                    panelWithWaves.revalidate();
//                    panelWithWaves.repaint();
//                    debugPrintWaves();
// TODO: VYMAZAT !!!!
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // TODO: PROGRAMO
                    debugPrintSplitters();
                    // TODO: PROGRAMO
                    super.mousePressed(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
//                     TODO: PROGRAMO - JE TO o 5 mimo kdyz to vytahnu dolu tak ze zvetsim scrollpane a pak to dam min nez je to scrollpane ted
////                        KDYZ PRIDAM NOVOU VLNU TAK MA SPATNOU VYSKU A I DELKU
                    if (lastSplitterDrag) {
                        // TODO: PROGRAMO - java bug
//                        if(e.getSource() != lastSplitter) {
//                            ProgramTest.debugPrint(e.getSource().toString());
//                            System.exit(111);
//                        }
                        lastSplitterMoved = true;
                        // TODO: PROGRAMO - java bug

                        movingLastSplitter = false;
                        lastSplitterDrag = false;
                        JViewport view = panelWithWaves.getViewport();
                        Point p = e.getPoint();
                        Point oldPos = view.getViewPosition();  // TODO:
                        if (DEBUG_CLASS.DEBUG) {
                            ProgramTest.printCharKTimesOnNLines('[', 20, 1);
                        }

                        int oldLoc;

                        // TODO: PROGRAMO - java bug
                        // If all is normal
                        if (isOldLocationFromPropertyChangeListenerAtDefaultVal()) {
                            oldLoc = lastSplitter.getLastDividerLocation();
                        } else {      // If we are moving the divider by the last pixel (the java bug)
                            oldLoc = oldLocationFromPropertyChangeListener;
                            resetOldLocationFromPropertyChangeListenerToDefaultVal();     // reset to the default value
                        }
                        // TODO: PROGRAMO - java bug

                        int divLoc = oldLoc + p.y;
                        int divSize = lastSplitter.getDividerSize();
                        int minDivLoc = lastSplitter.getMinimumDividerLocation();
                        int viewHeight = view.getViewSize().height;
                        int visibleRectH = view.getVisibleRect().height;
                        WaveMainPanel wave = waves.get(waves.size() - 1);
                        int minH = wave.getMinimumSize().height;
                        Dimension oldPrefSize = wave.getPreferredSize();
                        int newPrefHeight = -1;
                        if (divLoc < minDivLoc) {       // If it is moved so much up that it makes the panel above shorter
// TODO: PROGRAMO
//                            //if(splitters.size() != 1 && divLoc < getJSplitPaneContainingWaveFromWaveIndex(waves.size() - 1).getDividerLocation())
//                            ProgramTest.debugPrint("minDivLoc", divLoc, minDivLoc);
//                            movingLastSplitter = true;
// TODO: PROGRAMO
                            divLoc = minDivLoc;

                            // TODO: PROGRAMO - java bug
                            lastSplitterMoved = false;      // When this happens, for some reason the event in change property listener in the last splitter doesn't happen
                            // TODO: PROGRAMO - java bug
                        }
// We take - divSize because if we choose the if(visibleRectH == viewHeight)
// then we need to have the whole result in the visible rectangle, we don't want to enlarge the scrollpane
// because making the divider location larger than the scrollpane doesn't work
                        else if (divLoc > viewHeight - divSize) {
                            divLoc = viewHeight;
                            divLoc -= divSize;
                        }


                        if (visibleRectH == viewHeight) {       // If scrollpane can't scroll (all waves are visible without scrolling)
                            int y = wave.getY();
                            newPrefHeight = divLoc - y;
                        } else {
                            int previousDivLoc = getJSplitPaneDividerLoc(waves.get(wave.getWaveIndex() - 2));
                            newPrefHeight = divLoc - previousDivLoc - divSize;
                            ProgramTest.debugPrint("releasedEventELSE", oldPos, p, oldPos.y + p.y, lastSplitter.getLastDividerLocation(), oldLoc, oldLoc + p.y,
                                    oldPrefSize, newPrefHeight, panelWithWaves.getHeight(), view.getHeight(), view.getViewSize().height);
                        }

                        ProgramTest.debugPrint("Last splitter listener before", wave.getPreferredSize(), wave.getSize(),
                                getJSplitPaneDividerLoc(waves.get(wave.getWaveIndex() - 2)),
                                getJSplitPaneDividerLoc(waves.get(wave.getWaveIndex() - 2)) + wave.getPreferredSize().height,
                                wave.getWaveIndex() - 1, divLoc, divSize);

                        int oldPrefHeight = oldPrefSize.height;

                        // TODO: PROGRAMO
                        int dif = newPrefHeight - wave.getMinimumSize().height;
                        if (dif < 0 && existsPanelBiggerThanMinHeight(wave)) {
                            movingLastSplitter = true;
                        }
                        // TODO: PROGRAMO

                        //debugPrintSplitters();
                        wave.setPreferredSize(newPrefHeight);
                        lastSplitter.setDividerLocation(divLoc);

                        if (DEBUG_CLASS.DEBUG) {
                            debugPrintSplitters();
                            ProgramTest.debugPrintWithSep("**********************************", oldPrefHeight, newPrefHeight, p.y);
                            ProgramTest.debugPrintWithSep("**********************************", wave.getSize());
                        }

                        ProgramTest.debugPrint("Last splitter listener after", wave.getPreferredSize(), wave.getSize(),
                                getJSplitPaneDividerLoc(waves.get(wave.getWaveIndex() - 2)),
                                getJSplitPaneDividerLoc(waves.get(wave.getWaveIndex() - 2)) + wave.getPreferredSize().height,
                                wave.getWaveIndex() - 1, divLoc, divSize);

                        wave.revalidate();
                        wave.repaint();
                        panelWithWaves.revalidate();
                        panelWithWaves.repaint();
                    } else {
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
                    if (DEBUG_CLASS.DEBUG) {
                        ProgramTest.printNTimes("-------------------------------", 5);
                        System.out.println(e.getY());
                    }

                    Point cursorPoint = MouseInfo.getPointerInfo().getLocation();
                    if (!lastSplitterDrag) {
                        lastSplitterDrag = true;
                        previousY = cursorPoint.y;
                    }

                    int panelStartY = getPanelWithWavesStartOnScreenY();
                    int panelEndY = getPanelWithWavesEndOnScreenY();
                    int startDif;
                    int endDif;
                    // TODO: PROGRAMO
                    // If we are below the panel with waves, and we moved up (in mouse movement sense)
                    if ((startDif = panelStartY - cursorPoint.y) > 0 && cursorPoint.y < previousY) {
//                        JViewport view = panelWithWaves.getViewport();
//                        Point oldPos = view.getViewPosition();
//                        int movementUp = convertToPixelMovement(startDif);
//                        Point newPos = new Point(oldPos.x, oldPos.y - movementUp);
//                        if(newPos.y < 0) {
//                            newPos.y = 0;
//                        }
//                        view.setViewPosition(newPos);
                    }
                    // TODO: PROGRAMO
                    // If we are above the panel with waves, and we moved down (in mouse movement sense)
                    else if ((endDif = cursorPoint.y - panelEndY) > 0 && cursorPoint.y > previousY) {
                        int increaseSize = increaseJScrollPane(endDif);
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
        if(oldView != null) {
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
        if(splitters == null || splitters.size() == 0) {
            return null;
        }
        return splitters.get(splitters.size() - 1);

//        if(waves == null) {
//            return null;
//        }
//        return getJSplitPaneContainingWaveFromWaveIndex(waves.size());
    }



    /**
     * Index is starting from 0. Returns the splitpane containing the wave so for first 2 waves it is 0th splitter, and then it is for n-th (n - 1)th splitter (when indexing all from 0)
     * @param index
     * @return
     */
    private JSplitPane getJSplitPaneContainingWaveFromWaveIndex(int index) {
        JSplitPane splitter;
        if(index == 0) {
            splitter = splitters.get(index);
        }
        else {
            splitter = splitters.get(index - 1); // -1 because the first JSplitPane contains 2 waves, for explanation check panelWithWaves documentation
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
        if(index == 0) {
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
     *
     * @param wave is the wave from which we should look up and find in the panels above there is some panel which satisfies the condition that it is bigger than min size.
     * @return
     */
    private boolean existsPanelBiggerThanMinHeight(WaveMainPanel wave) {
        int arrayIndexOfWaveAbove = wave.getWaveIndex() - 2;
        while(arrayIndexOfWaveAbove >= 0) {
            WaveMainPanel waveAbove = waves.get(arrayIndexOfWaveAbove);
            if(waveAbove.getMinimumSize().height != waveAbove.getPreferredSize().height) {
                return true;
            }

            arrayIndexOfWaveAbove--;
        }

        return false;
    }


    /**
     * Indexing from 0
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
        if(index == 0) {
            splitter = splitters.get(index);
//            JPanel jp = new JPanel();
//            jp.setMinimumSize(component.getMinimumSize());
//            jp.setMaximumSize(component.getMaximumSize());
//            jp.setPreferredSize(component.getPreferredSize());
//            component = jp;

//            Component c1, c2;
//            c1 = splitter.getTopComponent();
//            splitter.setTopComponent(null);
//            c2 = splitter.getBottomComponent();
//            splitter.setBottomComponent(null);
//            JSplitPane newSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, component, c2);
//            splitters.set(index - 1, newSplitter);
//            splitters.get(index).setTopComponent(newSplitter);
//            if(newSplitter.getTopComponent() == null) System.exit(-1);
//            if(newSplitter.getBottomComponent() == null) System.exit(-1);
            splitter.setTopComponent(component);   ///////////////// TODO: BYLO PUVODNE
        }
        else {
            splitter = splitters.get(index - 1); // -1 because the first JSplitPane contains 2 waves, for explanation check panelWithWaves documentation
//            JPanel jp = new JPanel();
//            jp.setMinimumSize(component.getMinimumSize());
//            jp.setMaximumSize(component.getMaximumSize());
//            jp.setPreferredSize(component.getPreferredSize());
//            jp.setSize(component.getSize());
//            component = jp;

//            Component c1, c2;
//            c1 = splitter.getTopComponent();
//            splitter.setTopComponent(null);
//            c2 = splitter.getBottomComponent();
//            splitter.setBottomComponent(null);
//            JSplitPane newSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, c1, component);
//            splitters.set(index - 1, newSplitter);
//            splitters.get(index).setTopComponent(newSplitter);
//            if(newSplitter.getTopComponent() == null) System.exit(-1);
//            if(newSplitter.getBottomComponent() == null) System.exit(-1);
            splitter.setBottomComponent(component);   ///////////////// TODO: BYLO PUVODNE
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
     * @param from is the index of wave which we want to move. First wave has index = 0.
     * @param to is the index of the wave where we want the wave move. First wave has index = 0.
     */
    public void moveSwapSplitter(int from, int to) {
//        debugPrintWaves();
        WaveMainPanel waveMainPanel1;
        WaveMainPanel waveMainPanel2;

        if(panelWithWaves.getViewport().getViewPosition().y < 0) {
            System.out.println(panelWithWaves.getViewport().getViewSize().height);
            System.exit(-10000);    // TODO:
        }

        if(from < to) {     // Swapping from up to down
            waveMainPanel1 = waves.get(from);
            from++;
            do {
                waveMainPanel2 = waves.get(from);
                from++;
                // Keep swapping the component which is being dragged with the components between the index from and to.
                swapSplitterComponents(waveMainPanel1.getWaveIndex(), waveMainPanel1.getWaveIndexTextFieldText(),
                        waveMainPanel2.getWaveIndex(), waveMainPanel2.getWaveIndexTextFieldText());
            } while(from <= to);
        }
        else if(from > to) {    // Swapping from down to up
            waveMainPanel1 = waves.get(from);
            from--;
            do {
                waveMainPanel2 = waves.get(from);
                from--;
                // Keep swapping the component which is being dragged with the components between the index from and to.
                swapSplitterComponents(waveMainPanel1.getWaveIndex(), waveMainPanel1.getWaveIndexTextFieldText(),
                        waveMainPanel2.getWaveIndex(), waveMainPanel2.getWaveIndexTextFieldText());
            } while(from >= to);
        }



//        JSplitPane splitter1;
//        JSplitPane splitter2;
//        if(from0 < to0) {     // Swaping from up to down
//            splitter1 = splitters.get(from0);
//            from0++;
//            splitter2 = splitters.get(from0);
//            from0++;
//            for(; (from0-1) < to0; from0++) {
//                splitter1.setTopComponent(splitter2.getTopComponent());
//                splitter1 = splitter2;
//                splitter2 = splitters.get(from0);
//            }
//            splitter1.setTopComponent(splitter2.getTopComponent());
//        }
//        else if(from0 > to0) {    // Swaping from down to up
//            splitter1 = splitters.get(from0);
//            from0--;
//            splitter2 = splitters.get(from0);
//            from0--;
//            for(; (from0+1) > to0; from0--) {
//                splitter1.setTopComponent(splitter2.getTopComponent());
//                splitter1 = splitter2;
//                splitter2 = splitters.get(from0);
//            }
//            splitter1.setTopComponent(splitter2.getTopComponent());
//        }
    }

    /**
     * Used for swaping. Used in logic: index1 is from waveMainPanel1 and index2 from awp2
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
        if(DEBUG_CLASS.DEBUG) {
            System.out.println(index1 + "\t" + index1String + "\t" + index2 + "\t" + index2String);
        }
    }


    public void tryMoveSwap(WaveMainPanel waveMainPanel, MouseEvent e) {
        if(panelWithWaves.getViewport().getViewPosition().y < 0) {
            System.out.println(panelWithWaves.getViewport().getViewSize().height);
            System.exit(-100000);    // TODO:
        }
        WaveMainPanel wave;
        int y = waveMainPanel.getY();
        int mouseY = e.getY();
        mouseY += y;
        int from = waveMainPanel.getWaveIndex();        // Index is from 1
        from--;                                 // Now indexed from 0
        // If it isn't the last wave and if it is at least as low or lower as the start of the wave below
        if(from < waves.size() - 1 && mouseY >= waves.get(from + 1).getY()) {   // If it is wave below
            int to;
            for(to = from + 1; to < waves.size(); to++) {       // Check how much below it is
                wave = waves.get(to);
                if(mouseY < wave.getY()) {  // The wave before is the wave with which we should swap
                    break;
                }
            }
            to--;

            moveSwapSplitter(from, to);
        }
        // If it isn't the first wave and if it is at least as high or higher than the start of the wave above
        else if(from > 0 && mouseY <= waves.get(from - 1).getY()) {
            int to;
            for(to = from - 1; to >= 0; to--) {     // Check how much above it is
                wave = waves.get(to);
                if(mouseY > wave.getY()) {  // The wave after is the wave with which we should swap
                    break;
                }
            }
            to++;

            moveSwapSplitter(from, to);
        }

// TODO: Old version - it is the same - but if we were swaping smaller wave with bigger it started flickering (furious swaping)
// TODO: Delete later
//        if(from < waves.size() - 1 && mouseY >= waves.get(from + 1).getY()) {   // If it is wave below
//            int to;
//            for(to = from + 1; to < waves.size(); to++) {       // Check how much below it is
//                wave = waves.get(to);
//                if(mouseY < wave.getY()) {  // The wave before is the wave with which we should swap
//                    break;
//                }
//            }
//            to--;
//            if(to > from + 1) {
//                to = from + 1;          // TODO: neni nutny tam pocitat ten pocet vln, kdyz to budu delat jen po jedny
//                System.out.println("OVER");
//            }
//
//            moveSwapSplitter(from, to);
//        }
//        // If it isn't the first wave and if it is at least as high or higher than the start of the wave above
//        else if(from > 0 && mouseY <= waves.get(from - 1).getY() + waves.get(from - 1).getHeight()) {
//            int to;
//            for(to = from - 1; to >= 0; to--) {     // Check how much above it is
//                wave = waves.get(to);
//                if(mouseY > wave.getY() + wave.getHeight()) {  // The wave after is the wave with which we should swap
//                    break;
//                }
//            }
//            to++;
//
//            if(to < from - 1) {
//                to = from - 1;          // TODO: neni nutny tam pocitat ten pocet vln, kdyz to budu delat jen po jedny
//                System.out.println("OVER");
//            }
//            moveSwapSplitter(from, to);
//        }
    }


    // TODO: We have to solve the following problem:
    // by nesting JSplitPane the borders are getting bigger with each nesting
    // So I set the borders of jSplitPane to empty left borders, and also set divider border to null to remove small border above waves
    // source: https://stackoverflow.com/questions/12799640/why-does-jsplitpane-add-a-border-to-my-components-and-how-do-i-stop-it
    private void flattenJSplitPane(JSplitPane splitter) {
        SplitPaneUI ui;
        ui = splitter.getUI();
        if( ui instanceof BasicSplitPaneUI ) {
            ((BasicSplitPaneUI)ui).getDivider().setBorder( null );
        }
        splitter.setBorder(BorderFactory.createEmptyBorder());
    }
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
// TODO: Nevim jestli chci podporvat multiple files selection
//                fileChooser.setMultiSelectionEnabled(true);
//                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
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
        // TODO: PROGRAMO MOD
        JMenuItem menuItem = new JMenuItem("Add waves");
        menuItem.setToolTipText("Converts the wave in file to mono and adds it to the waves in audio player.");

        JFileChooser fileChooser = new JFileChooser();
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
// TODO: Nevim jestli chci podporvat multiple files selection
//                fileChooser.setMultiSelectionEnabled(true);
//                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = fileChooser.showOpenDialog(thisFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    stopAndModifyAudio(true, new ModifyAudioIFace() {
                        @Override
                        public void modifyAudio() {
                            try {
                                addWaves(f, false);
                            } catch (IOException exception) {
                                MyLogger.logException(exception);
                            }
                        }
                    }, true, false);
                }
            }
        });

        menu.add(menuItem);
        // TODO: PROGRAMO MOD
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
        if(lengthFromDialog <= 0) {
            return;
        }
        int oldWavesLen = getDoubleWaveLength();
        int emptyWaveLen = lengthFromDialog * (int)outputAudioFormat.getSampleRate();
        if(oldWavesLen >= emptyWaveLen) {
            emptyWaveLen = oldWavesLen;
        }

        for(int i = 0; i < numberOfWavesFromDialog; i++) {
            DoubleWave doubleWave = new DoubleWave(new double[emptyWaveLen], (int)outputAudioFormat.getSampleRate(),
                    1,"Empty wave", false);
            addWave(doubleWave);
        }
    }

    private void addOpenMonoFileToMenu(JMenu menu) {
        // TODO: PROGRAMO Jeste musim zmenit to jmeno pro DoubleWave protoze muzu tu samou vlnu nacist vickrat
        // TODO: PROGRAMO a ono by se pak spolu bylo to cachovani protoze by to bylo pod stejny jmenem
        JMenuItem menuItem = new JMenuItem("Open file mono");
        menuItem.setToolTipText("Removes all current waves, converts to file to mono and puts that wave as only wave in player.");

        JFileChooser fileChooser = new JFileChooser();
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
// TODO: Nevim jestli chci podporvat multiple files selection
//                fileChooser.setMultiSelectionEnabled(true);
//                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
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
     *
     * @param f
     * @return Returns true if the wave was correctly added, false otherwise.
     */
    private boolean addMonoWave(File f) {
        DoubleWave wave = loadMonoDoubleWave(f, getOutputSampleRate(), true);
        if(wave != null) {
            addWave(wave);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Loads audio file and converts it to mono if it already isn't and also converts it to correct sample rate.
     * @param f
     * @param newSampleRate if < 0 then no conversion is performed
     * @param shouldLog
     * @return
     */
    public static DoubleWave loadMonoDoubleWave(File f, int newSampleRate, boolean shouldLog) {
        DoubleWave wave = null;
        try {
            Program p = new Program();
            boolean audioLoaded = p.setVariables(f, true);
            if(!audioLoaded) {
                if(shouldLog) {
                    MyLogger.logWithoutIndentation("Couldn't load audio in addMonoWave(File f) method.\n" +
                            Program.LOG_MESSAGE_WHEN_SET_VARIABLES_RETURN_FALSE);
                }
                return null;
            }
            p.convertToMono();
            wave = new DoubleWave(p, false, newSampleRate);
        } catch (IOException exception) {
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
// TODO: Nevim jestli chci podporvat multiple files selection
//                fileChooser.setMultiSelectionEnabled(true);
//                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = fileChooser.showOpenDialog(thisFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    stopAndModifyAudio(true, new ModifyAudioIFace() {
                        @Override
                        public void modifyAudio() {
                            try {
                                removeAllWaves();
                                addWaves(f, false);
                            } catch (IOException exception) {
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
        Program p = new Program();
        boolean audioLoaded = p.setVariables(f, false);
        if(!audioLoaded) {
            MyLogger.logWithoutIndentation("Couldn't load audio in addWaves(File f) method.\n" +
                    Program.LOG_MESSAGE_WHEN_SET_VARIABLES_RETURN_FALSE);
            return;
        }

        int audioLen = p.getOnlyAudioSizeInBytes();
        ProgramTest.debugPrint("Adding waves", audioLen, p.decodedAudioStream.getFrameLength(),
                p.frameSize);
        ProgramTest.debugPrint("properties:", p.decodedAudioStream.getFormat().properties(),
                p.lengthOfAudioInSeconds, p.getOnlyAudioSizeInBytes(), p.wholeFileSize);
        double[][] waves = Program.separateChannelsDouble(p.decodedAudioStream, p.numberOfChannels, p.sampleSizeInBytes,
                p.isBigEndian, p.isSigned, audioLen);

        for(int i = 0; i < waves.length; i++) {
            waves[i] = Program.convertSampleRate(waves[i], p.numberOfChannels, p.sampleRate, getOutputSampleRate(),
                    true);
        }
        addWaves(waves, p.getFileName(), getOutputSampleRate(), shouldAddLater);
    }

    /**
     *
     * @param waves
     * @param filename
     * @param sampleRate
     * @param shouldAddLater if true, then it is put to list and the waves will be put to player next time the tab is clicked
     */
    private void addWaves(double[][] waves, String filename, int sampleRate, boolean shouldAddLater) {
        DoubleWave[] doubleWaves = new DoubleWave[waves.length];
        filename = Program.getNameWithoutExtension(filename);
        for(int i = 0; i < doubleWaves.length; i++) {
            String channelFilename = filename + "_" + i;
            doubleWaves[i] = new DoubleWave(waves[i], sampleRate, 1, channelFilename, false);
        }

        if(shouldAddLater) {
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
     * @param audio
     * @param format
     * @param audioLen
     */
    public void addWaves(InputStream audio, int audioLen, AudioFormatWithSign format, boolean shouldConvertSampleRate) {
        double[][] waves;
        try {
            waves = Program.separateChannelsDouble(audio, format.getChannels(), format.getSampleSizeInBits() / 8,
                    format.isBigEndian(), format.isSigned, audioLen);
            if(shouldConvertSampleRate) {
                int outputSampleRate = getOutputSampleRate();
                for(int i = 0; i < waves.length; i++) {
                    waves[i] = Program.convertSampleRate(waves[i], 1, (int)format.getSampleRate(),
                            outputSampleRate, true);
                }

                addWaves(waves, "", outputSampleRate, true);
            }
            else {
                addWaves(waves, "", getOutputSampleRate(), true);
            }
        } catch (IOException e) {
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
        catch(IOException e) {
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
                            int newLengthInSamples = lengthDialog.getLength() * (int)outputAudioFormat.getSampleRate();
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
                            ProgramTest.debugPrint("outputAudioFormat", outputAudioFormat, p.getFormat());
                        }
                    }
                }, false, false);
            }
        });
        menu.add(menuItem);
    }



    /**
     *
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
        if(AudioSystem.isFileTypeSupported(audioType)) {
            fileChooser.addChoosableFileFilter(new FileFilterAudioFormats(audioType));
        }

        audioType = AudioFileFormat.Type.AIFF;
        if(AudioSystem.isFileTypeSupported(audioType)) {
            fileChooser.addChoosableFileFilter(new FileFilterAudioFormats(audioType));
        }

        audioType = AudioFileFormat.Type.AU;
        if(AudioSystem.isFileTypeSupported(audioType)) {
            fileChooser.addChoosableFileFilter(new FileFilterAudioFormats(audioType));
        }

        audioType = AudioFileFormat.Type.SND;
        if(AudioSystem.isFileTypeSupported(audioType)) {
            fileChooser.addChoosableFileFilter(new FileFilterAudioFormats(audioType));
        }

        // Set default filter and default name
        fileChooser.setFileFilter(wavFileFilter);
        if(file == null) {
            file = new File(fileChooser.getCurrentDirectory() + "/audio");
        }
        fileChooser.setSelectedFile(file);
        return fileChooser;
    }

    private void addSaveFileToMenu(JMenu menu) {
        JMenuItem menuItem =  new JMenuItem("Save file");
        JFileChooser fileChooser = getFileChooserForSaving(null);

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showSaveDialog(thisFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
// TODO: DEBUG
//                    ProgramTest.debugPrint("Saving file:", f);
//                    System.exit(454);
// TODO: DEBUG
                    FileFilterAudioFormats filter = (FileFilterAudioFormats)fileChooser.getFileFilter();

                    byte[] outputWave = getOutputWaveBytes();
                    try {
                        Program.saveAudio(f.getAbsolutePath(), outputAudioFormat, outputWave, filter.audioType);
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        menu.add(menuItem);
    }

    // TODO: PROGRAMO
//    private void addAddOutputToWaves(JMenu menu) {
//        TODO:
//    }
//
//    private void addReplaceWavesWithOutput(JMenu menu) {
//        TODO:
//    }
    // TODO: PROGRAMO


//    private void addAudioTrack(JMenu menu) {
//        JMenuItem menuItem;
//
//        upgradeTextFieldIfDigitCountChanges();
//        menu.add(menuItem);
//    }

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

        // Otherwise there will be issue with the drawing of wave,
        // it will be drawn twice, but even after this, it will still be drawn incorrectly (the wave will be moved to right)
        waveScrollerWrapperPanel.scrollToStart();

        // To fix the wave being moved to right
        fakeZoomUpdate();

        revalidate();
        repaint();
    }


    private void deleteMarkedPart() {
        if(shouldMarkPart) {
            int startIndex = getMarkStartXSample();
            int endIndex = getMarkEndXSample();
            deletePart(startIndex, endIndex);
        }
        else {
            removeAllWaves();
        }
    }

    private void deletePart(int startIndex, int endIndex) {
        if(startIndex == 0 && endIndex == getSongLengthInSamples()) {
            removeAllWaves();
        }
        else {
            for(WaveMainPanel w : waves) {
                if(clipboard.isEqualToClipboardWavePanel(w)) {
                    int clipboardStartIndex = clipboard.getMarkStartSample();
                    int clipboardEndIndex = clipboard.getMarkEndSample();

                    // Note when comparing start index and clipboard end index is the only place where should be < or >
                    // | is the removed part, ( and ) is clipboard
                    if(startIndex <= clipboardStartIndex && endIndex >= clipboardEndIndex) {      // | ( ) |
                        clipboard.removeWaveFromClipboard();
                    }
                    else if (startIndex >= clipboardStartIndex && startIndex < clipboardEndIndex &&
                            endIndex >= clipboardEndIndex) {                             // ( | ) |
                        clipboard.setMarkEndSample(startIndex);
                    }
                    else if(startIndex <= clipboardStartIndex && endIndex >= clipboardStartIndex) {       // | ( | )
                        clipboard.setMarkStartSample(startIndex);
                        // But I also have to move the end sample
                        clipboard.setMarkEndSample(clipboardEndIndex - (endIndex - startIndex));
                    }
                    else if(startIndex >= clipboardStartIndex && endIndex <= clipboardEndIndex) {          // ( | | )
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
        for(final DRAW_PANEL_TYPES DRAW_TYPE : DRAW_PANEL_TYPES.values()) {
            switch(DRAW_TYPE) {
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
                    menuItem.setToolTipText("Creates fft window with both real and imaginary part result of FFT. Doesn't allow editing");
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
                if(getShouldMarkPart()) {
                    for (WaveMainPanel w : waves) {
                        if(w.getShouldIncludeInOperations()) {
                            wave = w.getDoubleWave().getSong();
                            break;
                        }
                    }
                }


                JFrame f = createDrawFrame(DRAW_TYPE, getOutputSampleRate(), thisAudioPlayerClass,
                        wave, getMarkStartXSample(), markLen);
                if(f != null) {
                    f.setVisible(true);
                }
            }
        };
    }



    /**
     *
     * @param DRAW_TYPE
     * @param sampleRate is only needed for the FFT draw panels.
     * @param waveAdder is needed everywhere except waveshaper.
     * @param inputArr is the array which will be used as input for the draw panel, can be null. Also used only for the FFT draw panels.
     * @param startIndex useful only when inputArr is non-null. If < 0 then set to 0. Also used only for the FFT draw panels.
     * @param windowSize useful only when inputArr is non-null. If <= 0 then set to 1024. Also used only for the FFT draw panels.
     * @return
     */
    public static DrawJFrame createDrawFrame(final DRAW_PANEL_TYPES DRAW_TYPE, int sampleRate, AddWaveIFace waveAdder,
                                             double[] inputArr, int startIndex, int windowSize) {
        JPanel drawPanel;
        if(inputArr == null) {
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
        if(isViewOnly && inputArr == null) {
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
                drawPanel = WaveShaper.createMaxSizeWaveShaper(Color.LIGHT_GRAY,
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
                if(DRAW_TYPE == DRAW_PANEL_TYPES.FFT_MEASURES || DRAW_TYPE == DRAW_PANEL_TYPES.FFT_MEASURES_VIEW_ONLY) {
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
                if(DRAW_TYPE == DRAW_PANEL_TYPES.TIME) {
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
                ProgramTest.debugPrint("Resize content:",
                        f.getContentPane().getSize(), drawPanel.getSize(), f.getSize(),
                        drawPanel.getPreferredSize(), f.getPreferredSize());
                ProgramTest.debugPrint(
                        f.getContentPane().getMinimumSize(), drawPanel.getMinimumSize(), f.getMinimumSize());

                if(f.getSize().width < f.getMinimumSize().width) {
                    f.setMinimumSize(new Dimension());
                    f.pack();
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

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





    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addAudioOperationsWithoutWave(JMenu menu) {
        WithoutInputWavePluginIFace op;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        op = new MultiplicationOnWave();
        addAudioOperation(op, menu);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        op = new AdditionOnWave();
        addAudioOperation(op, menu);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        op = new LogarithmOnWave();
        addAudioOperation(op, menu);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        op = new PowerOnWave();
        addAudioOperation(op, menu);


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        op = new InvertOnWave();
        addAudioOperation(op, menu);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        op = new WaveStretcherOnWave();
        addAudioOperation(op, menu);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        op = new WaveStretcherMaximumOnWave();
        addAudioOperation(op, menu);


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        op = new SetSamplesOnWaveOperation();
        addAudioOperation(op, menu);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        op = new SetSamplesToZeroOnWaveOperation();
        addAudioOperation(op, menu);
    }

    private void addAudioOperation(WithoutInputWavePluginIFace operation, JMenu menu) {
        JMenuItem menuItem = new JMenuItem(operation.getPluginName());
        menuItem.setToolTipText(operation.getPluginTooltip());

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean canContinueOperation = loadPluginParameters(operation, true);
                if(canContinueOperation) {
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
        if(hasFocus) {
            thisFrame.setJMenuBar(menuBar);
        }
        else {
            clickPauseButtonIfPlaying();
            audioThread.reset();
        }
    }

    private List<JMenuItem> withInputWaveMenuItems = new ArrayList<>();
    private void setEnabledWithWaveMenuItems(boolean enable) {
        for(JMenuItem item : withInputWaveMenuItems) {
            item.setEnabled(enable);
        }
    }

    // Hot to get menu items : https://stackoverflow.com/questions/24850424/get-jmenuitems-from-jmenubar
    private void setEnabledAllMenus(boolean enable) {
        JMenuBar bar = this.thisFrame.getJMenuBar();
        for(int i = 2; i < bar.getMenuCount() - 1; i++) {
            JMenu menu = bar.getMenu(i);
            menu.setEnabled(enable);
            // Disabling only menus is better - it is enough and I don't have to deal with the problem when calling setEnabledWithWaveMenuItems
//            for(int j = 0; j < menu.getItemCount(); j++) {
//                JMenuItem menuItem = menu.getItem(j);
//                menuItem.setEnabled(enable);
//            }
        }
    }


    private void addAudioOperationsWithWave(JMenu menu) {
        WithInputWavePluginIFace op;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        op = new FillWaveWithOtherWaveOperation();
        addAudioOperation(op, menu);



        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        op = new MultiplicationOnWaves();
        addAudioOperation(op, menu);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        op = new AdditionOnWaves();
        addAudioOperation(op, menu);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        op = new LogarithmOnWaves();
        addAudioOperation(op, menu);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        op = new PowerOnWaves();
        addAudioOperation(op, menu);
    }

    private void addAudioOperation(WithInputWavePluginIFace operation, JMenu menu) {
        JMenuItem menuItem = new JMenuItem(operation.getPluginName());
        withInputWaveMenuItems.add(menuItem);
        menuItem.setToolTipText(operation.getPluginTooltip());

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean canContinueOperation = loadPluginParameters(operation, true);
                if(canContinueOperation) {
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
        WithoutInputWavePluginIFace op = new LowPassFilter();
        addAudioOperation(op, menu);
    }


    private void addPlugins(JMenu menu) {
        addWithWavePlugins(menu);
        menu.addSeparator();
        addWithoutWavePlugins(menu);
    }

    private void addWithWavePlugins(JMenu menu) {
        List<WithInputWavePluginIFace> plugins = WithInputWavePluginIFace.loadPlugins();
        for(WithInputWavePluginIFace plugin : plugins) {
            addPlugin(plugin, menu);
        }
    }

    private void addWithoutWavePlugins(JMenu menu) {
        List<WithoutInputWavePluginIFace> plugins = WithoutInputWavePluginIFace.loadPlugins();
        for(WithoutInputWavePluginIFace plugin : plugins) {
            addPlugin(plugin, menu);
        }
    }


    // These addPlugin methods are basically copy pasted. I have to do that because they work with different methods, and to get the correct Class I have to call getClass on the final class.
    private void addPlugin(WithoutInputWavePluginIFace pluginToAdd, JMenu menu) {
        JMenuItem menuItem = new JMenuItem(pluginToAdd.getPluginName());
        menuItem.setToolTipText(pluginToAdd.getPluginTooltip());

        menuItem.addActionListener(new ActionListener() {
            WithoutInputWavePluginIFace plugin = pluginToAdd;

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: ASI PREKOMBINOVANY - Podle me staci jen to spodni co je zakomentovany, ted me nenapada duvod
                // Proc jsem vytvarel i novou instanci - Napadlo me to prootze ten kod je prakticky totoznej s tou metodu
                // addAudioOperation(WithoutInputWavePluginIFace operation, JMenu menu)
                // Totez plati pro tu druhou addPlugin metodu s WithInputWavePluginIFace

                // To reset the plugin
                Class<?> clazz = plugin.getClass();
                try {
                    Constructor<?> constructor = clazz.getConstructor();
                    // This is already checked when creating the first instance (pluginToAdd), only way this would fail would be when the source codes changes during runtime
                    if (constructor == null) {
                        System.out.println("Doesn't have constructor without parameters - inside method addPlugin in action listener");
                        return;
                    }
                    plugin = (WithoutInputWavePluginIFace) clazz.newInstance();
                }
                catch(Exception exception) {
                    exception.printStackTrace();
                    return;
                }
                // To reset the plugin

                boolean canContinueOperation = loadPluginParameters(plugin, true);
                if(canContinueOperation) {
                    stopAndModifyAudio(false, new ModifyAudioIFace() {
                        @Override
                        public void modifyAudio() {
                            performOperationInternal(plugin);
                        }
                    }, true, false);
                }
            }


//                boolean canContinueOperation = loadPluginParameters(pluginToAdd, true);
//                if(canContinueOperation) {
//                    stopAndModifyAudio(false, new ModifyAudioIFace() {
//                        @Override
//                        public void modifyAudio() {
//                            performOperationInternal(pluginToAdd);
//                        }
//                    }, true, false);
//                }
//            }
            // TODO: ASI PREKOMBINOVANY

        });

        menu.add(menuItem);
    }

    private void addPlugin(WithInputWavePluginIFace pluginToAdd, JMenu menu) {
        JMenuItem menuItem = new JMenuItem(pluginToAdd.getPluginName());
        withInputWaveMenuItems.add(menuItem);
        menuItem.setToolTipText(pluginToAdd.getPluginTooltip());

        menuItem.addActionListener(new ActionListener() {
            WithInputWavePluginIFace plugin = pluginToAdd;

            @Override
            public void actionPerformed(ActionEvent e) {
                // To reset the plugin
                Class<?> clazz = plugin.getClass();
                try {
                    Constructor<?> constructor = clazz.getConstructor();
                    // This is already checked when creating the first instance (pluginToAdd), only way this would fail would be when the source codes changes during runtime
                    if (constructor == null) {
                        System.out.println("Doesn't have constructor without parameters - inside method addPlugin in action listener");
                        return;
                    }
                    plugin = (WithInputWavePluginIFace) clazz.newInstance();
                }
                catch(Exception exception) {
                    exception.printStackTrace();
                    return;
                }
                // To reset the plugin

                boolean canContinueOperation = loadPluginParameters(plugin, true);
                if(canContinueOperation) {
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
     * @param plugin
     * @return
     */
    public static boolean loadPluginParameters(PluginDefaultIFace plugin, boolean containsCancelOption) {
        boolean canContinueOperation;

        if (plugin.shouldWaitForParametersFromUser()) {
            Object panelInDialog;
            if(plugin.isUsingDefaultJPanel()) {
                PluginJPanelBasedOnAnnotations pl = new PluginJPanelBasedOnAnnotations(plugin, plugin.getClass());
                panelInDialog = pl;
            }
            else {
                panelInDialog = plugin;
            }

            if(panelInDialog == plugin && plugin instanceof JFileChooser) {
                int result = ((JFileChooser)plugin).showOpenDialog(null);
                if(result == JFileChooser.APPROVE_OPTION) {
                    canContinueOperation = true;
                }
                else {
                    canContinueOperation = false;
                }
            }
            else if(plugin instanceof JFrame) {
                ((JFrame) plugin).setVisible(true);
                // When using frame plugin, then have look at how does the code work, since you will usually have to call
                // special method to perform operation etc. For example stopAndModifyAudio when adding plugin to player or
                // updateAfterPropertiesCall when using the plugin in properties inside synth part. Or in the second case just
                // take care of the update inside the frame methods.

                // I don't see any simple way how to make dialog from JFrame, especially when I am using the size
                // of frame inside the panel. I will repair it later maybe, but currently I don't have that much time
                // and I just don't see how to do it


                // TODO: Vymazat
//////                JDialog d = new JDialog((JFrame)plugin, "asdawsdaftwsgggswg", true);
//////                d.setModal(true);
//////                d.pack();
//////                d.setVisible(true);
////
//                ((JFrame) plugin).setVisible(true);
//                int result = JOptionPane.showConfirmDialog((JFrame) plugin, plugin,
//                        //(JFrame)plugin, ((DiagramSynthPackage.Synth.Operators.UnaryOperations.WaveShaper.JFrameTest)plugin).panel,
//                        //((DiagramSynthPackage.Synth.Operators.UnaryOperations.WaveShaper.JFrameTest)plugin).panel, null,
//                        //(JFrame)plugin, null,
//                        "Dialog: " + plugin.getPluginName(), JOptionPane.OK_CANCEL_OPTION,
//                        JOptionPane.PLAIN_MESSAGE);
//                if (result == JOptionPane.OK_OPTION) {
//                    canContinueOperation = true;
//                } else {
//                    canContinueOperation = false;
//                }
////
////
////                ((DiagramSynthPackage.Synth.Operators.UnaryOperations.WaveShaper.JFrameTest) plugin).setVisible(true);
////
////                JPanel shutDownPanel = new JPanel();
////                Timer t = new Timer(1000, new ActionListener() {
////                    @Override
////                    public void actionPerformed(ActionEvent e) {
////                        Window w = SwingUtilities.getWindowAncestor(shutDownPanel);
////                        w.setVisible(!w.isVisible());
////                    }
////                });
////                t.start();
////                int result = JOptionPane.showConfirmDialog(null, shutDownPanel,
////                        "Dialog: " + plugin.getPluginName(), JOptionPane.OK_CANCEL_OPTION,
////                        JOptionPane.PLAIN_MESSAGE);
//////                System.exit(465484);
////
//////                JDialog d = new JDialog((JFrame)plugin, "asdawsdaftwsgggswg", true);
//////                d.setModal(true);
//////                d.pack();
//////                d.setVisible(true);
////
//////                try {
//////                while(true)Thread.sleep(500);
//////                } catch (InterruptedException e) {
//////                    e.printStackTrace();
//////                }
////
////
//////                try {
//////                    Thread.sleep(10000);
//////                } catch (InterruptedException e) {
//////                    e.printStackTrace();
//////                }
////
////                canContinueOperation = false;
                canContinueOperation = false;
            }
            else {
                int result;
                if(containsCancelOption) {
                    result = JOptionPane.showConfirmDialog(null, panelInDialog,
                            "Dialog: " + plugin.getPluginName(), JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);
                }
                else {
                    result = JOptionPane.showConfirmDialog(null, panelInDialog,
                            "Dialog: " + plugin.getPluginName(), JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE);
                }
                if (result == JOptionPane.OK_OPTION) {
                    canContinueOperation = true;
                } else {
                    canContinueOperation = false;
                }
            }
        }
        else {
            canContinueOperation = true;
        }

        return canContinueOperation;
    }


    private void performOperationInternal(WithInputWavePluginIFace operation) {
        for (WaveMainPanel waveMainPanel : waves) {
            if (waveMainPanel.getShouldIncludeInOperations()) {
                DoubleWave doubleWave = waveMainPanel.getDoubleWave();
                if(doubleWave == clipboard.getWave()) {
                    continue;
                }
                if(shouldMarkPart) {
                    operation.performOperation(clipboard.getWave(), doubleWave,
                            clipboard.getMarkStartSample(), clipboard.getMarkEndSample(),
                            getMarkStartXSample(), getMarkEndXSample());
                }
                else {
                    operation.performOperation(clipboard.getWave(), doubleWave,
                            clipboard.getMarkStartSample(), clipboard.getMarkEndSample(),
                            0, doubleWave.getSongLength());
                }

                waveMainPanel.reloadDrawValues();
            }
        }
    }

    private void performOperationInternal(WithoutInputWavePluginIFace operation) {
        for (WaveMainPanel waveMainPanel : waves) {
            if (waveMainPanel.getShouldIncludeInOperations()) {
                DoubleWave doubleWave = waveMainPanel.getDoubleWave();
                if(shouldMarkPart) {
                    operation.performOperation(doubleWave, getMarkStartXSample(), getMarkEndXSample());
                }
                else {
                    operation.performOperation(doubleWave, 0, doubleWave.getSongLength());
                }

                waveMainPanel.reloadDrawValues();
            }
        }
    }

    //
//    private void addSetAudioFormatToMenu(JMenu menu) {
//        JMenuItem menuItem;
//
//        menu.add(menuItem);
//    }
//
//    private void addSampleRateConvertorToMenu(JMenu menu) {
//        JMenuItem menuItem;
//
//        menu.add(menuItem);
//    }
//
    private void addConvertToMonoToMenu(JMenu menu) {
// TODO: PROGRAMO MOD
//        JMenuItem menuItem = new JMenuItem("Convert to mono");
//
//
//        ModifyAudioIFace modAudioIFace = new ModifyAudioIFace() {
//            @Override
//            public boolean modifyAudio() {
//                try {
//                    if (program.numberOfChannels != 1) {         // TODO:
//                        program.convertToMono();
//                        return true;
//                    }
//                    return false;
//                } catch (IOException e) {
//                    System.exit(0);         // TODO:
//                }
//
//                return true;            // TODO: Unreachable code, but java doesn't see it
//            }
//        };
//
//        menuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                stopAndModifyAudio(modAudioIFace);
//            }
//        });
//        menu.add(menuItem);
// TODO: PROGRAMO MOD
    }



    // TODO: PROGRAMO - tohle je obecny filtrovani
    private void addPerfomFilterToMenu(JMenu menu, List<JMenuItem> menuItemsWorkingWithSongPartsList) {
// TODO: PROGRAMO MOD
//        JMenuItem menuItem = new JMenuItem("Filter");
//
//
//        ModifyAudioIFace modAudioIFace = new ModifyAudioIFace() {
//            @Override
//            public boolean modifyAudio() {
//                try {
//                    double[] coefs = new double[32];
//                    for(int i = 0; i < coefs.length; i++) {
//                        coefs[i] = 1/(double)coefs.length;
//                    }
//                    boolean isRecursiveFilter = false;
//                    boolean filterChangesAudio = true;
//                    int i = 0;
//                    for(; i < coefs.length; i++) {
//                        if(coefs[i] != 1) {
//                            break;
//                        }
//                    }
//                    if(i == coefs.length) {
//                        return false;
//                    }
//
//                    if(isRecursiveFilter) {
//                        double[] coefsOutput = new double[] { 1/3.0, 1/3.0, 1/3.0 };
//                        Program.performRecursiveFilter(program.song, coefs, coefsOutput);       // TODO: !!!!! Spatne - vubec neberu k uvahu sample size
//                    }
//                    else {
//                        // TODO: nejak nefunguje
//                        //program.song = Program.runLowPassFilter(program.song, 200, 32, program.sampleRate,
//                        //    program.numberOfChannels, program.sampleSizeInBytes, program.frameSize, program.isBigEndian, program.isSigned);
//                        program.song = Program.performNonRecursiveFilter(program.song, coefs, program.numberOfChannels,
//                            program.sampleSizeInBytes, program.frameSize, program.isBigEndian, program.isSigned);
//                    }
//                } catch (IOException e) {
//                    System.exit(0);         // TODO:
//                }
//
//                return true;            // TODO: Unreachable code, but java doesn't see it
//            }
//        };
//
//        menuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                stopAndModifyAudio(modAudioIFace);
//            }
//        });
//        menu.add(menuItem);
//        menuItemsWorkingWithSongPartsList.add(menuItem);
// TODO: PROGRAMO MOD
    }


//
//    private void addConvertToSilenceToMenu(JMenu menu) {
//        JMenuItem menuItem;
//
//        menu.add(menuItem);
//    }
//
//    private void addReverseToMenu(JMenu menu) {
//        JMenuItem menuItem;
//
//        menu.add(menuItem);
//    }
//
//    private void addPerformOperationOnSamplesToMenu(JMenu menu) {
//        JMenuItem menuItem;
//
//        menu.add(menuItem);
//    }
//
//    // TODO: Tohle je tam vic veci s tim
//    private void addSetSamplesToRandomNumbersToMenu(JMenu menu) {
//        JMenuItem menuItem;
//
//        menu.add(menuItem);
//    }
//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    private void addAnalyzeBPMToMenu(JMenu menu) {
//        JMenuItem menuItem;
//
//        menu.add(menuItem);
//    }
//
//    private void addAnalyzeFFTToMenu(JMenu menu) {
//        JMenuItem menuItem;
//
//        menu.add(menuItem);
//    }
//
//    private void addCreateSpectrogramToMenu(JMenu menu) {
//        JMenuItem menuItem;
//
//        menu.add(menuItem);
//    }
//
//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    private void addShowAlsoTheSpectrogramWithTheWaveToMenu(JMenu menu) {
//        JMenuItem menuItem;
//
//        menu.add(menuItem);
//    }



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
    // TODO: K nicemu pravdepodobne
    public int getMarkStartXWithoutScroll() {
        return markStartXPixel - getCurrentHorizontalScroll();
    }
    public void setMarkStartXPixel(int val) {
        markStartXPixel = val;
    }
    private int markStartXSample;
    /**
     * Returns the smaller sample number of the mark start and mark end
     * @return
     */
    public int getMarkStartXSample() {
        return Math.min(markStartXSample, markEndXSample);
    }
    public void setMarkStartXSample(int val) {
        markStartXSample = val;
    }
    // TODO: Musim brat k uvahu i to jakej je nejlevejsi pixel, co prave zobrazuju
    public void setMarkStartXVariablesBasedOnPixel(int pixel) {
        int sample = calculateSampleFromWavePixel(pixel);
        setMarkStartXVariablesBasedOnSample(sample);
    }
    public void setMarkStartXVariablesBasedOnSample(int sample) {
        markStartXSample = sample;
        double pixel = calculatePixel(sample);
        markStartXPixel = (int)pixel;

        ProgramTest.debugPrint("mark start", markStartXPixel, markStartXSample);
    }


    private int markEndXPixel;
    public int getMarkEndXPixel() {
        return markEndXPixel;
    }
    // TODO: K nicemu pravdepodobne
    public int getMarkEndXWithoutScroll() {
        return markEndXPixel - getCurrentHorizontalScroll();
    }
    public void setMarkEndXPixel(int val) {
        markEndXPixel = val;
    }
    private int markEndXSample;

    /**
     * Returns the bigger sample number of the mark start and mark end
     * @return
     */
    public int getMarkEndXSample() {
        return Math.max(markStartXSample, markEndXSample);
    }
    public void setMarkEndXSample(int val) {
        markEndXSample = val;
    }
    // TODO: Musim brat k uvahu i to jakej je nejlevejsi pixel, co prave zobrazuju

    public void updateMarkEndXVariablesBasedOnPixel(int update, int oldHorizontalScroll) {
        // oldHorizontalScroll moves it to the start, and update updates it.
        // So now the pixel represents relative pixel distance to the visible start of wave.
        int pixel = markEndXPixel + update - oldHorizontalScroll;
        setMarkEndXVariablesBasedOnPixel(pixel);
    }

    /**
     * Pixel is relative horizontal distance to the visible start of the wave.
     * @param pixel
     */
    public void setMarkEndXVariablesBasedOnPixel(int pixel) {
        int sample = calculateSampleFromWavePixel(pixel);
        setMarkEndXVariablesBasedOnSample(sample);
    }
    public void setMarkEndXVariablesBasedOnSample(int sample) {
        int songLen;
        if(sample < 0) {
            sample = 0;
        }
        else if(sample > (songLen = getSongLengthInSamples())) {
            sample = songLen;
        }

        markEndXSample = sample;
        double pixel = calculatePixel(sample);
        markEndXPixel = (int)pixel;
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
        int sample = (int)(widthOfAudioInSamples * (pixel / (double)getWaveWidth()));
        ProgramTest.debugPrint("WAVE_WIDTH_0", sample, widthOfAudioInSamples, getWaveWidth(), pixel);
        return sample;
    }

    private double calculatePixel(int sample) {
        int widthOfAudioInSamples = getDoubleWaveLength();
        double pixel = sample / (double)widthOfAudioInSamples;     // TODO: SAMPLE
        pixel *= getWaveWidth();
        return pixel;
    }


    private int samplesPerPixel;

    private BufferedImage audioPicture;


    private final String fontName = "Serif";        // TODO: Idealne moznost nastavit font, ale nevim jak to bude s casem

    private int currentFontSize;    // TODO: Povolit jen pres setter jinak vubec
    private FontMetrics fontMetrics;

    public int getCurrentFontSize() {
        return currentFontSize;
    }
    public void setCurrentFontSize(int currentFontSize) {   // Ten font vezmu z vrchni listy
        this.currentFontSize = currentFontSize;
        Graphics g = this.getGraphics();
        // TODO: Problem s tim resizovanim g.setFont(new Font(fontName, Font.BOLD, this.currentFontSize));
        this.setFont(new Font(fontName, Font.BOLD, this.currentFontSize));
        fontMetrics = this.getFontMetrics(this.getFont());
        // TODO: Problem s tim resizovanim fontMetrics = g.getFontMetrics();
// TODO:
//        for(AudioWavePanel awp : waves) {
//            awp.setCurrentFontSize(currentFontSize);
//        }
    }


// TODO: VYMAZAT NEPOUZITO
//    // Shows sample info, but also stops showing the old one
////    @Override
////    public void drawSampleInfo(Graphics g) {
////        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
////        Point p = pointerInfo.getLocation();
////        Point windowLoc = this.getLocationOnScreen(); // TODO: Volam na komponente;
////        int x = p.x - windowLoc.x;
////        int y = p.y - windowLoc.y;      // TODO: y ani nepotrebuju
////        if(DEBUG_CLASS.DEBUG) {
////            System.out.println("SSI:\t" + p.x + "\t" + windowLoc.x + "\t" + x);
////        }
////        if(x > waveEndX) {
////            x = waveEndX;
////        }
////        else if(x < waveStartX) {
////            x = waveStartX;     // Set to default value
////        }
////        else {
////            if(DEBUG_CLASS.DEBUG) {
////                System.out.println("X is in wave");     // TODO: Remove
////            }
////        }
////        x -= waveStartX;
////        this.drawSampleInfo(x, g);        // Shows sample info, but also stops showing the old one
////    }
////
////
////    private void drawSampleInfo(int x, Graphics g) {
////        int sampleIndex = convertXToSampleIndex(x);
////        if(DEBUG_CLASS.DEBUG) {
////            System.out.println("---------\t" + sampleIndex + "\t" + x);
////        }
////        drawInfo(sampleIndex, g);
////    }
////
////    private int convertXToSampleIndex(int x) {
////        double numberOfSamplesPerPixel = sampleRange / (double)waveWidth;
////        int sampleIndex = (int)(numberOfSamplesPerPixel * x);
////        return sampleIndex;
////    }
////
////
////    private void drawInfo(int sampleIndex, Graphics graphics) {
////        double[] song = doubleWave.getSong();
////        if(DEBUG_CLASS.DEBUG) {
////            System.out.println("DI:\t" + sampleIndex + "\t" + song.length);
////        }
////        double value = song[sampleIndex];
////        String sampleIndexString = Integer.toString(sampleIndex);
////        String valueString = Double.toString(value);
////// TODO: Int varianta:
//////        int[] song = null;
//////        int value = song[sampleIndex];
//////        String sampleIndexString = Integer.toString(sampleIndex);
//////        String valueString = Integer.toString(value);       // TODO: Ta value asi pres doubly, jako v audacity
////
////
//////        int visibleWidth = audioPicture.getWidth();        // TODO: Ted mirne zmatenej jestli to ma byt audioPicture nebo ten cely panel
//////        int height = audioPicture.getHeight();
////        int width = this.getWidth();
////        int height = this.getHeight();
////
////        int maxTextWidth;
////        int w1 = fontMetrics.stringWidth(valueString);
////        int w2 = fontMetrics.stringWidth(sampleIndexString);
////        if(w1 > w2) {
////            maxTextWidth = w1;
////        }
////        else {
////            maxTextWidth = w2;
////        }
////
////        int sampleInfoX = width - maxTextWidth;
////        int sampleInfoY = height - 2 * fontMetrics.getHeight();
////
////        graphics.setColor(Color.BLACK);
////        graphics.drawLine(sampleInfoX, height, sampleInfoX, sampleInfoY);
////        graphics.drawLine(sampleInfoX, sampleInfoY, width, sampleInfoY);
////
////        graphics.drawString(sampleIndexString, width - maxTextWidth, height);
////        graphics.drawString(valueString, width - maxTextWidth, height - fontMetrics.getHeight());
////    }
////
////
////    @Override
////    public void markPart(Graphics g) {
////        if(shouldMarkPart) {
////            g.setColor(Color.red);
////            if(markStartXPixel > markEndXPixel) {
////                g.fillRect(markEndXPixel, 0,  markStartXPixel - markEndXPixel, this.getHeight());
////            }
////            else {
////                g.fillRect(markStartXPixel, 0,  markEndXPixel - markStartXPixel, this.getHeight());
////            }
////            if(DEBUG_CLASS.DEBUG) {
////                System.out.println("MARK PART:\t" + markStartXPixel + "\t" + (markEndXPixel - markStartXPixel));
////            }
////        }
////
////        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
////        Point p = pointerInfo.getLocation();
////        Point windowLoc = this.getLocationOnScreen(); // TODO: Volam na komponente;
////    }
//
//
//
//    private void resetScreen(Graphics graphics, Color c) {
//        // Reset
//        graphics.setColor(c);
//        graphics.fillRect(0, 0,  this.getWidth(), this.getHeight()); // TODO: Ted mirne zmatenej jestli to ma byt audioPicture nebo ten cely panel
//    }
// TODO: VYMAZAT NEPOUZITO


    @Override
    public void paintComponent(Graphics g) {
        // TODO: DEBUG
        // TODO: todoMark - DEBUG
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        // TODO: todoMark - DEBUG
        // TODO: DEBUG
        super.paintComponent(g);
        if(canZoom) {
            canPoll = true;
        }
        if(channelCountChanged && thisFrame.isEnabled()) {
            disableZooming();
            thisFrame.setEnabled(false);

            if (splitters.size() >= 1) {
                JSplitPane currSplitter = splitters.get(0);
                ProgramTest.debugPrint("min div loc:", currSplitter.getMinimumDividerLocation());
                WaveMainPanel top;
                WaveMainPanel bot;
                top = (WaveMainPanel) currSplitter.getTopComponent();
                top.setPrefSizeToMin();
                if (waves.size() >= 2) {         // Else there is only 1 wave so the bot panel is empty panel
                    bot = (WaveMainPanel) currSplitter.getBottomComponent();
                    bot.setPrefSizeToMin();
                }

                setDivLocToMinDivLoc(currSplitter);
                ProgramTest.debugPrint("min div loc:", currSplitter.getMinimumDividerLocation());

                for (int i = 1; i < splitters.size() - 1; i++) {
                    currSplitter = splitters.get(i);
                    ProgramTest.debugPrint("min div loc:", currSplitter.getMinimumDividerLocation());
                    bot = (WaveMainPanel) currSplitter.getBottomComponent();
                    bot.setPrefSizeToMin();
                    ProgramTest.debugPrint("bot pref size", bot.getPreferredSize());
                    setDivLocToMinDivLoc(currSplitter);
                    ProgramTest.debugPrint("min div loc:", currSplitter.getMinimumDividerLocation());
                }

                JSplitPane lastSplitter = splitters.get(splitters.size() - 1);
                setDivLocToMinDivLoc(lastSplitter);
            }
            Timer t = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    channelCountChanged = false;
                    // https://stackoverflow.com/questions/54173626/how-do-i-stop-timer-task-after-executed-one-time
                    ((Timer)e.getSource()).stop();


                    //int currScroll = panelWithWaves.getVerticalScrollBar().getValue();
                    // I have to scroll to the end because else the size will be bigger than the preferred size - I don't want that
                    int maxScroll = getMaxVerticalScroll();

                    panelWithWaves.getVerticalScrollBar().setValue(maxScroll);
                    panelWithWaves.revalidate();
                    panelWithWaves.repaint();
                    // I put the enabling with delay just to be sure the user doesn't break it
                    Timer t2 = new Timer(100, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ((Timer)e.getSource()).stop();
                            // It is better to take the control once to the maximum scroll, then to max and the to currScroll - the blink is very unpleasant
//                            panelWithWaves.getVerticalScrollBar().setValue(currScroll);
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
// TODO: NOT USED ANYMORE
//    @Override
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//
////        System.out.println(panelWithWaves.getX() + "\t" + panelWithWaves.getY());
////        System.out.println(panelWithWaves.getWidth() + "\t" + panelWithWaves.getHeight());
////        System.out.println("-----------------------");
////        for(AudioWavePanel awp : waves) {
////            System.out.println(awp.getX() + "\t" + awp.getY());
////            System.out.println(awp.getWidth() + "\t" + awp.getHeight());
////            System.out.println("\n\n");
////        }
//////        if(panelWithWaves.getWidth() != 0 || panelWithWaves.getHeight() != 0) {
//////            System.exit(0);
//////        }
//
////       resetScreen(g, Color.WHITE);           // TODO:
////
////        markPart(g);
////        drawAudioWave(g);
////        drawSampleInfo(g);
////
////        drawTimeLine(g);
////        drawCurrentPlayTime(g, Color.black);
////        drawSongLen(g, Color.black);
////
////        drawSamplesValueRangeDouble(g);
////        drawTimestamps(program.sampleSizeInBytes, program.sampleRate, g);
//    }


    private static void setDivLocToMinDivLoc(JSplitPane splitPane) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                splitPane.setDividerLocation(splitPane.getMinimumDividerLocation());
            }
        });
    }




    private MouseEvent pressedEvent;

    private int waveStartX;
    private int waveEndX;

    private int waveMouseRegisterStartY;
    private int waveMouseRegisterEndY;

    private int waveStartY;         // TODO:
    private int waveEndY;
    private int waveHeight;


    private int timestampsY;
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
    @Override
    public void mouseClicked(MouseEvent e) {    // mouseClicked is when the mouse button has been pressed and released.
        // EMPTY
    }

    @Override
    public void mousePressed(MouseEvent e) {    // mousePressed is when the mouse button has been pressed.
        shouldMarkPart = false;
        pressedEvent = e;
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
        return (int)outputAudioFormat.getSampleRate();
    }
    public int getNumberOfChannelsInOutputFormat() {
        return outputAudioFormat.getChannels();
    }

    public int getDoubleWaveLength() {
        int len;
        if(waves.size() == 0) {
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
        currPlayTimeInMillis = 0;
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
        if(!playButton.getBoolVar()) {
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
                if(audioControlPanel.getPlayButton().getBoolVar()) {
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
                this, zoomListener, unzoomListener, outputAudioFormat.getChannels());


        // TODO: LALA
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        this.add(audioControlPanel, c);
        // TODO: LALA
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
        if(songsConc == null || multFactorsConc == null || currSampleConc >= songsConc[0].length) {
            for(int i = 0; i < arr.length; i++) {
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
         *
         * @param shouldPause
         * @param maxPlayTimeInMs size how many ms can be maximally at single moment inside source data line
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
            try {
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, outputAudioFormat);
                line = null;
                try {
                    line = (SourceDataLine) AudioSystem.getLine(info);
                    line.open(outputAudioFormat);
                } catch (LineUnavailableException e) {
                    MyLogger.logException(e);
                }

                muteControl = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
                muteControl.setValue(audioControlPanel.getMuteButton().getBoolVar());
                masterGainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);      // TODO: Type.VOLUME isn't available control

                audioControlPanel.setMasterGainToCurrentSlideValue();
                line.start();
            } catch (Exception e) {
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

            int sampleRate = (int)outputAudioFormat.getSampleRate();
            if(oldSampleRate > 0) {
                currSample *= sampleRate / (double)oldSampleRate;
            }
            oldSampleRate = sampleRate;

            setDataLine();
            audioLineMaxAvailableBytes = line.available();

            sampleSizeInBytes = outputAudioFormat.getSampleSizeInBits();
            maxAbsoluteValue = Program.getMaxAbsoluteValueSigned(sampleSizeInBytes);
            sampleSizeInBytes /= 8;
            int frameSize = sampleSizeInBytes * outputAudioFormat.getChannels();
            // Now it contains length of one second in bytes.

            int maxByteCountInAudioLine = AudioThread.convertMsToByteLen(sampleRate, frameSize, maxPlayTimeDivFactor);
            minAllowedAvailableSize = audioLineMaxAvailableBytes - maxByteCountInAudioLine;
            int audioArrLen = AudioThread.convertMsToByteLen(sampleRate, frameSize, internalBufferTimeDivFactor);

            // The size is at least frameSize and usually it is at least 2 ms
            audioArrLen = Math.max(frameSize, Math.min(audioArrLen, maxByteCountInAudioLine / 2));
            Program.convertToMultipleUp(audioArrLen, frameSize);
            audioArr = new byte[audioArrLen];
            //audioArr = new byte[Program.convertToMultipleUp(maxByteCountInAudioLine / 2, frameSize)];

            callOnResize();       // TODO: Volat na resize vzdycky
            wavesLengthChanged();
        }

        private void wavesLengthChanged() {
            outputEndIndex = getDoubleWaveLength();
            int songSizeInSecs = DoubleWave.convertSampleToSecs(outputEndIndex, (int) outputAudioFormat.getSampleRate());
            songLenInSecs = Program.convertSecondsToTime(songSizeInSecs, -1);
            indexJumpInDoubleArr = audioArr.length / Program.calculateFrameSize(outputAudioFormat);
            setTimeLinePixelsPerPlayPart();
        }


        private void playAudioLoop() {
            while (true) {
                updateWavesForMixing();

                int nextSample;       // TODO: Maybe not that effective
                synchronized (audioLock) {
                    tryPause();
                    for (nextSample = currSample + indexJumpInDoubleArr; nextSample < outputEndIndex; nextSample = currSample + indexJumpInDoubleArr) {
                        tryPause();

//                while (playButton.getBoolVar()) {
//
//                }

//            System.out.println("Current sample 1:\t" + currSample);

                        // TODO: CONCURRENCY
                        // Just to be sure that there is no concurrency issue - like setting the songs to null midway though the for cycle
                        boolean filledWithWaveSamples = performMixing(songs, multFactors, audioArr.length);

//                for (int j = 0; j < arr.length; j++, currSample++) {
//                    arr[j] = program.song[currSample];
//                }


                        // This is here for the app to be more responsive, because we usually fill the buffer to be played much faster, than we actually play it.
                        while (line.available() - minAllowedAvailableSize < 0) {
                            // Active waiting
                        }
                        line.write(audioArr, 0, audioArr.length);

                        //System.out.println(len + "\t" + i + "\t" + playChunkSize + "\t" + line.available() + "\t" + line.getBufferSize() + "\t" + line.isActive() + "\t" + line.isRunning());

                        if(filledWithWaveSamples) {
                            if (userClickedWave) {
                                switchToUserSelectedSample();
                            } else {
                                timeLineX += timeLinePixelsPerPlayPart;
                                //currPlayTimeInMillis += playTimeJumpInMillis;
// TODO: DEBUG
//                    ProgramTest.debugPrint("Audio loop", timeLineX, timeLinePixelsPerPlayPart);
// TODO: DEBUG
                            }
                        }
                        repaint();
                    }

                    // Write the last few bytes
                    int remainingLen = (outputEndIndex - currSample) *
                                       outputAudioFormat.getSampleSizeInBits() / 8 * outputAudioFormat.getChannels();
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
                } catch (InterruptedException e) {
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
         * @param currSongs
         * @param currMultFactors
         * @param lenInBytes
         * @return Returns false if there wasn't any wave so it had to be filled with 0s
         */
        private boolean performMixing(double[][] currSongs, double[][] currMultFactors, int lenInBytes) {
            if (hasAtLeastOneWave) {
                // currSample++ because I make from 1 sample multiple samples
                for (int outputArrIndex = 0; outputArrIndex < lenInBytes; currSample++) {
//                        outputArrIndex = mixer.mix(songs, arr, outputArrIndex, multFactors, sampleSizeInBytes,
//                            outputAudioFormat.isBigEndian(), isOutputFormatSigned, maxAbsoluteValue, currSample);
                    outputArrIndex = mixer.mix(currSongs, audioArr, outputArrIndex, currMultFactors, sampleSizeInBytes,
                            outputAudioFormat.isBigEndian(), outputAudioFormat.isSigned, maxAbsoluteValue, currSample);
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
            // Not sure if I should also move in the waves
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
        return new DoubleWave(outputWave, (int)outputAudioFormat.getSampleRate(),
                outputAudioFormat.getChannels(), filename, false);
    }


    private byte[] getOutputWaveBytes() {
        updateWavesForMixing();
        int outputLen = getDoubleWaveLength() * outputAudioFormat.getFrameSize();

        return getOutputWaveBytes(songs, multFactors, outputLen, audioThread.mixer, outputAudioFormat);
    }

    public static byte[] getOutputWaveBytes(double[][] songs, double[][] multFactors, int outputLen,
                                            AudioMixerIFace mixer, AudioFormatWithSign outputAudioFormat) {
        int maxAbsoluteValue = Program.getMaxAbsoluteValueSigned(outputAudioFormat.getSampleSizeInBits());
        byte[] outputWave = new byte[outputLen];
        mixer.mixAllToOutputArr(songs, outputWave, multFactors, outputAudioFormat.getSampleSizeInBits() / 8,
                outputAudioFormat.isBigEndian(), outputAudioFormat.isSigned, maxAbsoluteValue);

        return outputWave;
    }


    public void switchToUserSelectedSampleIfPaused() {
        if(audioControlPanel.getPlayButton().getBoolVar()) {
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


    private void drawSongLen(Graphics g, Color c) {
        int y = this.getHeight() - g.getFontMetrics().getHeight();
        Program.drawStringWithSpace(g, c, songLenInSecs, waveStartX, audioControlPanel.getPlayButton().getX(), y);
    }

    private void drawCurrentPlayTime(Graphics g, Color c) {
        String time = getCurrentPlayTime();
        int y = this.getHeight();
        Program.drawStringWithSpace(g, c, time, waveStartX, audioControlPanel.getPlayButton().getX(), y);
    }

    public String getCurrentPlayTime() {
        return Program.convertMillisecondsToTime(currPlayTimeInMillis, -1);
    }


    private void drawTimeLine(Graphics g) {
        // TODO: Not sure if I should take Math.ceil
        drawTimeLine(g, (int)/*TODO: Math.ceil*/(timeLineX), timestampsY, waveEndY);
    }

    private void drawTimeLine(Graphics g, int x, int startY, int endY) {
        g.setColor(Color.GREEN);
        if(DEBUG_CLASS.DEBUG) {
            System.out.println("X:\t" + x);
        }
        g.drawLine(x, startY, x, endY);
    }

    /**
     * Sets sizes of components
     */
    private void setSizes(int width, int height) {
        // Set size of window for wave
        waveStartX = 30;
        waveEndX = 3 * width / 4;
//        waveEndX = 700;

        waveStartY = 200;      // TODO:
        waveEndY = 3 * height / 4;
//        waveEndY = 800;
        waveHeight = waveEndY - waveStartY;
        timestampsY = waveStartY;

        waveMouseRegisterStartY = 0;
        waveMouseRegisterEndY = waveEndY;
    }

    private void updateTimeLineX(int waveWidth) {     // TODO: Prekryv jmen promennych - ale tu starou ve tride uz stejne nepouzivam
        ProgramTest.debugPrint("time line before", timeLineX);
        int audioLenInSamples = getDoubleWaveLength();
        timeLineX = currSample / (double) audioLenInSamples;
        timeLineX *= waveWidth;
        ProgramTest.debugPrint("time line after", timeLineX, waveWidth);
    }

    private void callOnResize() {
        setTimeLinePixelsPerPlayPart();

        // Resize marking if used
        if(shouldMarkPart) {
            // Not using the get methods because it was incorrectly for zooming
            int startXSample = markStartXSample;
            int endXSample = markEndXSample;
            setMarkStartXVariablesBasedOnSample(startXSample);
            setMarkEndXVariablesBasedOnSample(endXSample);
        }
    }

    private void setTimeLinePixelsPerPlayPart() {
        int waveWidth = getWaveWidth();             // TODO: Prekryv jmen promennych - ale tu starou ve tride uz stejne nepouzivam
        int audioLenInSamples = getDoubleWaveLength();
        int arrLenInFrames = audioThread.audioArr.length / Program.calculateFrameSize(outputAudioFormat);
        timeLinePixelsPerPlayPart = arrLenInFrames / (double) audioLenInSamples;
// TODO: PROGRAMO TADY TO JE SPATNE KDYZ TO VOLAM TAK JESTE NEZNAM WAVEWIDTH
        timeLinePixelsPerPlayPart *= waveWidth;
// TODO: PROGRAMO
        updateTimeLineX(waveWidth);
    }


// TODO: VYMAZAT - nepouzito
//    // Currently not used, but can be useful later, if I need to resize pixel and I don't have any other info to recalculate it from
//    private int getPixelAfterResize(int pixel, int oldWidth, int newWidth) {
//        double p = pixel / (double)oldWidth;
//        p *= newWidth;
//        return (int)p;
//    }
//
//    private void drawTimestamps(int sampleSize, Graphics g) {
//        int sampleLen = endSample - startSample;        // Range in doubles
//        sampleLen *= sampleSize;
//        double numOfSecs = sampleLen / (double) program.getSizeOfOneSecInBytes();
//
//        Color color = Color.black;
//        g.setColor(color);
//        g.drawLine(0, timestampsY, this.getWidth(), timestampsY);
//
//
//        int timestampCountBetweenTwoMainTimeStamps = 3;
//        int labelCount = 10;
//        double timeJump = numOfSecs / labelCount;
//        int timestampsMultiples = 5;
//        int timeJumpInt;
//        timeJumpInt = (int)timeJump;
//        if(timeJumpInt >= timestampsMultiples) {
//            if(timeJumpInt % timestampsMultiples != 0) {
//                timeJumpInt += timestampsMultiples - (timeJumpInt % timestampsMultiples);
//            }
//        }
//
//
//        double pixelJump = timeJumpInt / numOfSecs;
//        pixelJump *= waveWidth;
//        labelCount = (int)(waveWidth / pixelJump);
//        labelCount *= timestampCountBetweenTwoMainTimeStamps;
//        pixelJump /= timestampCountBetweenTwoMainTimeStamps;
//
//        labelCount++;
//        String timeString;
//        int timeInt = 0;
//        double x = waveStartX;
//        // TODO: Rozhodne nechci x = 30 takhle defaultne ... i kdyz mozna chci
//        for (int i = 0; i < labelCount; x += pixelJump, i++) {
//            int xInt = (int)x;
//            g.drawLine(xInt, 0, xInt, timestampsY);
//            g.drawLine(xInt, 0, xInt, this.getHeight());
//            g.setColor(color);
//
//            if(i % timestampCountBetweenTwoMainTimeStamps == 0) {
//                timeString = Program.convertSecondsToTime(timeInt);
//                Program.drawStringWithDefinedMidLoc(g, color, timeString, xInt, timestampsY);
//                timeInt += timeJumpInt;
//            }
//        }
//
//
//
//
//
//
//
//
////        labelCount += 2;
////        String timeString;
////        // TODO: Rozhodne nechci x = 30 takhle defaultne ... i kdyz mozna chci
////        for (int i = 0, nextX = x + spaceSizeBetweenTimestampsInPixels; i < labelCount; x = nextX, nextX += spaceSizeBetweenTimestampsInPixels, time += timeJump, i++) {
////            g.drawLine(x, 0, x, y);
////            timeString = Program.convertSecondsToTime((int) time);
////// TODO: DEBUG            System.out.println(time + "\t" + timeString + "\t" + timeJump + "\t" + numOfSecs);
////            //Program.drawStringWithSpace(g, color, timeString, x / 2, nextX / 2, y, fontMetrics);
////            g.setColor(color);
////            g.drawString(timeString, x, y);
////
//////
//////            g.setColor(Color.black);
//////            int textLen = fontMetrics.stringWidth(binFreqs[bin]);
//////            int textStart = (currBinWidth - textLen) / 2;
//////            g.drawString(binFreqs[bin], currX + textStart, windowHeight);
////        }
//    }
// TODO: VYMAZAT

    private void drawSamplesValueRangeDouble(Graphics g) {
        drawSamplesValueRange(g, -1, 1);
    }

    private void drawSamplesValueRangeInt(Graphics g, int sampleSizeInBits) {
        double valMax = Program.getMaxAbsoluteValueSigned(sampleSizeInBits);
        double valMin = -valMax - 1;
        drawSamplesValueRange(g, valMin, valMax);
    }


    private void drawSamplesValueRange(Graphics g, double valMin, double valMax) {
        int spaceSizeBetweenLabelsInPixels = 50;
        int halfHeight = waveHeight / 2;
        int labelCount = halfHeight / spaceSizeBetweenLabelsInPixels;
        double pixelJump = halfHeight / (double)(labelCount+1);     // +1 because there will be max value + the other labels
        double valRange = valMax - valMin;
        double halfValRange = valRange / 2;
        double halfValJump = halfValRange / (labelCount+1);
// TODO: DEBUG
//        System.out.println(labelCount + "\t" + waveHeight + "\t" + pixelJump);
// TODO: DEBUG

        Color color = Color.black;
        g.setColor(color);
        int x = waveStartX;
        g.drawLine(x, waveStartY, x, waveEndY);
//        double valRange = valMax - valMin;
//        double halfValRange = valRange / 2;
//        double halfValJump = halfValRange / labelCount;
//        System.out.println(halfValJump + "\t" + halfValRange + "\t" + Math.ceil(labelCount / (double)2));
////        double valJump = valueRange / labelCount;   // For each space we perform 1 jump, so labelCount currently represents number of spaces
//        labelCount++;       // To add the minimum value, now it represents real label count
//        String valString;
        int startXForLine = x;

// TODO: DEBUG        int DEBUG = (int)(waveStartY + (1 + 0.633) * waveHeight / 2.0);        // TODO: !!!
// TODO: DEBUG        g.drawLine(0, DEBUG, this.getWidth(), DEBUG);


        double val = valMax;
        double y = waveStartY;
        // +3 because maxVal, 0, minVal
        for(int i = 0; i < 3 + 2 * labelCount; y += pixelJump, val -= halfValJump, i++) {
            drawOneValue(startXForLine, x, (int)y, val, color, g);
        }

//        val = valMin;
//        y = waveEndY;
//        for(int i = 0; i < labelCount; y -= pixelJump, val += halfValJump, i++) {
//            drawOneValue(startXForLine, x, (int)y, val, color, g);
//        }
        // Draw 0
//        drawOneValue(startXForLine, x, (waveStartY + halfHeight), 0, color, g);
    }


    private void drawOneValue(int startXForLine, int x, int y, double valToDraw, Color color, Graphics g) {
        // TODO: Correct version:
        g.drawLine(startXForLine, y, x, y);
        // TODO: Debug version:
        g.drawLine(0, y, this.getWidth(), y);
        String valString = String.format("%.2f", valToDraw);
        Program.drawStringWithSpace(g, color, valString, 0, startXForLine, y + fontMetrics.getHeight() / 4);
    }


// TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//    public void resetAudio(File file) {
//
//    }


//    private void resetAudio() {
//        zoom = 1;
//        startSample = 0;
//        endSample = program.song.length;
//        timeLineX = waveStartX;
//
//        int len = program.song.length - (program.song.length % program.frameSize);
//        int remainder = len % playChunkSize;			// TODO: !!!!!!!!! Important, there is no check inside the write method
//        lastFullChunkEndIndex = len - remainder;
//    }


    private void stopAndModifyAudio(boolean isSongsOrMultFactorsUpdateNeeded, ModifyAudioIFace modifyAudioImpl,
                                    boolean shouldResume, boolean shouldResetAudio) {
        disableZooming();
        BooleanButton playButton = audioControlPanel.getPlayButton();

// TODO: PROGRAMO MOD
        boolean wasNotPaused = !playButton.getBoolVar();
        if (wasNotPaused) {     // If the playing is not paused, then pause it
            playButton.doClick();
            while(!audioThread.isPaused) {
                audioThread.setShouldPause(true);
            }
        }
//        Point windowLoc = this.getLocationOnScreen();
//        int x = waveStartX + windowLoc.x;
//        int y = waveStartY + windowLoc.y;
//        userEventsGenerator.moveToWithClickWithReturn(x, y);

        modifyAudioImpl.modifyAudio();

        if(shouldResetAudio) {
            audioThread.reset();
        }
        if(wasNotPaused && shouldResume) {
            playButton.doClick();
        }

        if(isSongsOrMultFactorsUpdateNeeded) {
            postWaveAdditionAction();
        }
// TODO: PROGRAMO MOD
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
        if(waves.size() == 0) {
            return getDefaultWaveWidth();
        }
        int w = Math.max(waves.get(0).getWaveWidth(), getDefaultWaveWidth());
        return w;
    }

    public int getDefaultWaveWidth() {
        if(waves.size() == 0) {
            return WavePanel.START_DEFAULT_WAVE_WIDTH_IN_PIXELS;
        }
        int w = waves.get(0).getDefaultWaveWidth();
        return w;
    }


    public int getSongLengthInSamples() {
        if(waves.size() != 0) {
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
        Point panelStart = new Point(0,0);
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
        if(pointOnScreen.y < (startY = getPanelWithWavesStartOnScreenY())) {
            moveSpeed = startY - pointOnScreen.y;
            moveJScrollPaneUp(moveSpeed);
        }
        else if(pointOnScreen.y > (endY = getPanelWithWavesEndOnScreenY())) {
            moveSpeed = pointOnScreen.y - endY;
            moveJScrollPaneDown(moveSpeed);
        }
    }


    private int moveJScrollPaneUp(int moveSpeed) {
        JViewport view = panelWithWaves.getViewport();
        Point oldPos = view.getViewPosition();
        int movedSize = convertToPixelMovement(moveSpeed);
        if(movedSize > 0) {
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
        if(panelWithWaves.getViewport().getViewPosition().y < 0) {
            System.out.println(panelWithWaves.getViewport().getViewSize().height);
            System.exit(-1000000);    // TODO: Vymazat
        }
        JViewport view = panelWithWaves.getViewport();
        Point oldPos = view.getViewPosition();
        int movedSize = convertToPixelMovement(moveSpeed);
        if(movedSize > 0) {
// TODO:            if(view.getViewSize().height != 653) System.exit(view.getViewSize().height);
// TODO: VYMAZAT
            int bottomY = oldPos.y + view.getViewRect().height;
            int bottomY2 = oldPos.y + view.getViewSize().height;    // TODO: Vymazat
            int bottomY3 = oldPos.y + view.getView().getHeight();   // TODO: Vymazat
            int bottomY4 = oldPos.y + view.getVisibleRect().height; // TODO: Vymazat
            int viewH = view.getViewSize().height;
            Dimension todoSize = view.getSize();                    // TODO: Vymazat
            Dimension extentSize = view.getExtentSize();            // TODO: Vymazat
            int todoScrollH = panelWithWaves.getHeight();           // TODO: Vymazat
            Insets todoPanelInsets = panelWithWaves.getInsets();    // TODO: Vymazat
            Insets todoViewInsets = view.getInsets();               // TODO: Vymazat
            // Just scroll
            if (bottomY < viewH) {     // Increase the size of JSplitPane
                int dif = viewH - bottomY;
                if (dif < movedSize) {
                    movedSize = dif;
                }
                ProgramTest.debugPrint(view.getInsets().top, panelWithWaves.getInsets().top, view.getInsets().bottom, panelWithWaves.getInsets().bottom);
                Point newPos = new Point(oldPos.x, oldPos.y + movedSize);
                view.setViewPosition(newPos);
            }
        }
        // TODO: Tohle vymazat - je to na vic mistech - - ve swap metodach
        if(panelWithWaves.getViewport().getViewPosition().y < 0) {
            System.out.println(panelWithWaves.getViewport().getViewSize().height);
            System.exit(-10000000);    // TODO: Vymazat
        }
        return movedSize;
    }


    private void debugPrintWaves() {
        // TODO: Jen neco zkousim - Vymazat - je to vedlejsi efekt
//        final int divSpace = splitters.get(0).getDividerSize();
//        int space = divSpace;
//
//        JSplitPane s = null;
//        JSplitPane previousS = null;
//        for(int i = 0; i < splitters.size(); i++) {
//            previousS = s;
//            s = splitters.get(i);
//    //        s.resetToPreferredSizes();
//            if(i == 0) {
//                s.setDividerLocation(s.getPreferredSize().height - waves.get(0).getPreferredSize().height - divSpace);
//            }
//            else {
//                s.setDividerLocation(previousS.getPreferredSize().height);
//            }
////            s.setDividerLocation(s.getPreferredSize().height);
////            space += divSpace;
//        }
//        panelWithWaves.revalidate();
//        panelWithWaves.repaint();
//        this.revalidate();
//        this.repaint();

        ProgramTest.printCharKTimesOnNLines('/');
        ProgramTest.printCharKTimesOnNLines('+');
        for(int i = 0; i < waves.size(); i++) {
            WaveMainPanel w = waves.get(i);
            int divLoc = getJSplitPaneContainingWaveFromWaveIndex(i + 1).getDividerLocation();
            ProgramTest.debugPrint(w.getMinimumSize(), w.getPreferredSize(),
                    w.getHeight(), divLoc, splitters.get(i).getPreferredSize(), splitters.get(i).getHeight());

            // TODO: V tomhle je ten problem Ten divider je -1 on se nenastavi z nejakyho duvodu
            if(divLoc < 0) {
                System.out.println(i + "\t" + divLoc);
            }
        }
        int li = splitters.size() - 1;
        JSplitPane lastSplitter = splitters.get(li);
        if(li != waves.size() - 1) {
            ProgramTest.debugPrint(lastSplitter.getPreferredSize(), lastSplitter.getHeight());
        }
        Component lastEmptyPanel = lastSplitter.getBottomComponent();
        ProgramTest.debugPrint(lastEmptyPanel.getPreferredSize(), lastEmptyPanel.getSize());
    }



    public void debugPrintSplitters() {
        System.out.println("splitters count:\t" + splitters.size());
        for(int i = 0; i < splitters.size(); i++) {

            JSplitPane splitter = splitters.get(i);
//            if(todoPanes[i] != splitter || !todoPanes[i].equals(splitter)) {
//                System.out.println(i);
//                ProgramTest.debugPrintWithSep(splitter);
//                ProgramTest.debugPrintWithSep(todoPanes[i]);
//                System.exit(123546);
//            }
            Component comp;
            int waveIndex;
            if(i == 0 || i == splitters.size() - 1) {
                comp = splitter.getTopComponent();
                waveIndex = getWaveIndex(comp);
                ProgramTest.debugPrint(i, " top:", splitter.getDividerLocation(), splitter.getPreferredSize(), splitter.getSize(),
                        comp.getPreferredSize(), comp.getSize(), waveIndex);
            }

            comp = splitter.getBottomComponent();
            if(comp == null) {
                System.out.println(i);
                System.exit(-1);        // TODO:
            }
            waveIndex = getWaveIndex(comp);
            ProgramTest.debugPrint(i, " bot:", splitter.getDividerLocation(), splitter.getPreferredSize(), splitter.getSize(),
                    comp.getPreferredSize(), comp.getSize(), waveIndex);
        }
    }


    public int getWaveIndex(Component waveMainPanel) {
        for(int i = 0; i < waves.size(); i++) {
            if(waveMainPanel == waves.get(i)) {
                return i;
            }
        }
        return -1;
    }


    private JSplitPane[] todoPanes;
    private void debugInitTodoPanes() {
        todoPanes = new JSplitPane[splitters.size()];
        for(int i = 0; i < todoPanes.length; i++) {
            todoPanes[i] = splitters.get(i);
        }
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
//        waveScrollerPollTimer.restart();
//        // Artificial time, but I can't know how long it will take to establish the waves
//        // TODO: PROGRAMO I guess that I could just set canPoll = true when paintComponent is called and canZoom == true
//        waveScrollerPollTimer.setInitialDelay(1000);
//        waveScrollerPollTimer.start();
        canZoom = true;
        this.revalidate();
        this.repaint();
    }

    public int getMaxPossibleZoom() {
        int maxPossibleZoom = Integer.MAX_VALUE;
        for(WaveMainPanel waveMainPanel : waves) {
            maxPossibleZoom = Math.min(waveMainPanel.getMaxPossibleZoom(), maxPossibleZoom);
        }

        if(maxPossibleZoom == Integer.MAX_VALUE) {
            return 0;
        }
        return maxPossibleZoom;
    }

    public void setZoomToMaxZoom(int maxAllowedZoom) {
        zoomVariables.setMaxAllowedZoom(maxAllowedZoom);
        zoomVariables.zoom = maxAllowedZoom;
        setCurrentZoom(maxAllowedZoom);
    }

    /**
     * Doesn't do anything for zoomChange == 0
     */
    @Override
    public void updateZoom(int zoomChange) {
        System.out.println("Can zoom: " + getCanZoom());
        if(zoomChange != 0 && getCanZoom() && !waveScrollerWrapperPanel.getIsScrollbarBeingUsed() && waves.size() != 0) {
            disableZooming();
            setMaxAllowedZoom();
            if(zoomChange > 0) {     // When zooming, we need to check if we are zooming too much
                if(zoomVariables.getIsZoomAtMax()) {
                    enableZooming();
                    return;
                }
                else {
                    int newZoom = zoomVariables.zoom + zoomChange;
                    if(newZoom > zoomVariables.getMaxAllowedZoom()) {
                        int zoomDif = newZoom - zoomVariables.getMaxAllowedZoom();
                        zoomChange -= zoomDif;
                    }
                }
            }
            else {
                if(zoomVariables.getIsZoomAtZero()) {
                    enableZooming();
                    return;
                }
                else {
                    int newZoom = zoomVariables.zoom + zoomChange;
                    if(newZoom < 0) {
                        zoomChange -= newZoom;
                    }
                }
            }

            // Save images for all waves to bridge the zoom, to understand this problem check javadocs at zoomBridgeImg variable
            for(WaveMainPanel w : waves) {
                w.saveZoomBridgeImg();
            }


            waveScrollerWrapperPanel.setOldScrollbarValue(waveScrollerWrapperPanel.getCurrentHorizontalScroll());
            setOldWaveVisibleWidth();
            timeLineXForZooming = timeLineX;
            shouldZoomToMid = false;       // Is set to true only when zooming to mid
            shouldZoomToEnd = false;       // Is set to true only when zooming to end

            //horizontalBarAdjustmentListener.setShouldNotifyWaves(false);
            //waveScrollerWrapperPanel.setIsResizeEvent(true);

            if (zoomChange > 0) {
                // Just pass 1 (-1 when unzooming), because I am not sure if it works for larger numbers (it may works, but this works 100%),
                // I had similar problem in the diagram window
                for(int i = 0; i < zoomChange; i++) {
                    zooming(1);
                }
            } else if (zoomChange < 0) {
                for(int i = 0; i > zoomChange; i--) {
                    unzooming(-1);
                }
            }

            // TODO: !!! TED
            callOnResize();
            setWaveScrollPanelsSizes();
//            waveScrollerWrapperPanel.updateWhenZooming();
            //waveScrollerWrapperPanel.repaintEmptyPanel();
// TODO: DEBUG
//            ProgramTest.debugPrint("WIDTH:", waveScrollerWrapperPanel.getEmptyPanelSizeDebug(), waves.get(0).getWaveWidth());
// TODO: DEBUG

//        waveScrollerWrapperPanel.revalidate();
//        waveScrollerWrapperPanel.repaint();

//        waveScrollerWrapperPanel.revalidate();
//        waveScrollerWrapperPanel.repaint();

//            this.timestampPanel.repaint();

            this.revalidate();
            this.repaint();

            //horizontalBarAdjustmentListener.setShouldNotifyWaves(false);
            //waveScrollerWrapperPanel.getHorizontalScrollBar().setValue(waveScrollerWrapperPanel.getHorizontalScrollBar().getMaximum());
            //horizontalBarAdjustmentListener.setShouldNotifyWaves(true);
        }

// TODO: VYMAZAT
//        ProgramTest.debugPrint("EMPTY:", emptyPanelForHorizontalScroll.getSize());
//        for(WaveMainPanel waveMainPanel : waves) {
//            ProgramTest.debugPrint(waveMainPanel.getSize());
//        }
//        waveScrollerWrapperPanel.revalidate();
//        waveScrollerWrapperPanel.repaint();
//        panelWithWaves.revalidate();
//        panelWithWaves.repaint();
//        this.revalidate();
//        this.repaint();
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
        if(newZoom > maxAllowedZoom) {
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
        if(newZoom == zoomVariables.zoom) {
            enableZooming();
        }
        else {
            zoomToGivenPosition(newZoom, isZoom);
//            passZoomChangeToWaves(newZoom);
//            zoomVariables.zoom = newZoom;
        }
    }


    public void passZoomChangeToWaves(int newZoom, boolean setToMid, boolean setToEnd) {
        for(WaveMainPanel wave : waves) {
            wave.updateZoom(newZoom, waveScrollerWrapperPanel.getOldScrollbarValue(), setToMid, setToEnd);
            wave.revalidate();
        }
    }


    /**
     * Depending on situation zooms. If the time line is visible then zooms in such way that the line is in the middle.
     * If it is not visible then zoom to the middle of visible part.
     * If the time line is close to start (or end)
     * then zoom in such way that the scroll is at the start which is 0 (or at the end which is wave width - visible width)
     * @param newZoom is the new zoom value.
     * @param isZoom is true when zooming, false when unzooming
     */
    public void zoomToGivenPosition(int newZoom, boolean isZoom) {
        if(isTimeLineVisible()) {
            if(isTimeLineNearStart()) {
                zoomToStart();
            }
            else if(isTimeLineNearEnd()) {
                zoomToEnd();
            }
            else {
                if(isZoom) {
                    zoomToTimeLine();
                }
                else {
                    zoomToMid();
                }
            }
        }
        else {
//            if(isMidNearEnd()) {
//                zoomToEnd();
//            }
//            else {
            if(!isZoom) {
                int visibleWaveWidth = getWavesVisibleWidth();
                int currScroll = getCurrentHorizontalScroll();
                if(currScroll < visibleWaveWidth / WavePanel.ZOOM_VALUE) {     // It would go to negative numbers
                    zoomToStart();
                }
                else {
                    zoomToMid();
                }
            }
            else {
                zoomToMid();
            }
//            }
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
        return isNearEnd((int)timeLineXForZooming);
    }

    public boolean isNearEnd(int x) {
        return x > getWaveWidth() - getWavesVisibleWidth() / 4;
    }

    private void zoomToStart() {
//        waveScrollerWrapperPanel.getWaveScroller().getHorizontalScrollBar().setValue(0);
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
        JScrollBar scrollBar = waveScrollerWrapperPanel.getWaveScroller().getHorizontalScrollBar();
        int timeLineInMidScroll = (int)getTimeLineXForZooming() - getOldWaveVisibleWidth() / 2;
//        ProgramTest.debugPrint("zoomToTimeLine", scrollBar.getValue(), (int)getTimeLineXForZooming(), timeLineInMidScroll);
//        timeLineInMidScroll -= getOldWaveVisibleWidth() / 2;
        //scrollBar.setValue(timeLineInMidScroll);            // TODO: Tady chci aby bylo nastaveni disabled
        waveScrollerWrapperPanel.setOldScrollbarValue(timeLineInMidScroll);
        zoomToMid();
    }



    public boolean isTimeLineVisible() {
        int timeLine = (int)getTimeLineX();
        int scroll = getCurrentHorizontalScroll();
        int waveVisibleWidth = getWavesVisibleWidth();
        return timeLine >= scroll && timeLine <= scroll + waveVisibleWidth;
    }


    public int getWavesVisibleWidth() {
        int visibleWidth = 0;
        // TODO: PROGRAMO - height
        if(waves.size() > 0) {
            int firstWaveStart = waves.get(0).getWave().getX();
//            int width = panelWithWaves.getViewport().getWidth() -
//                panelWithWaves.getInsets().left - panelWithWaves.getInsets().right;
//            Insets a = waves.get(0).getInsets();
//            Insets b = waves.get(0).getWave().getInsets();
//            Insets c = panelWithWaves.getInsets();
//            Insets d = panelWithWaves.getViewport().getInsets();
            int width = panelWithWaves.getViewport().getWidth();

            for (WaveMainPanel waveMainPanel : waves) {
                int waveVisibleWidth = waveMainPanel.getWave().getVisibleRect().width;
                if (visibleWidth < waveVisibleWidth && waveVisibleWidth + firstWaveStart <= width) {        // TODO: PROGRAMO - height
                    visibleWidth = waveVisibleWidth;
                }

                //visibleWidth = Math.max(visibleWidth, waveMainPanel.getWave().getVisibleRect().width);
// TODO: PROGRAMO - DEBUG
//                ProgramTest.debugPrint("getWavesVisibleWidth", visibleWidth);
//                if(visibleWidth == 571) {
//                    int a = 4;
//                }
// TODO: PROGRAMO - DEBUG
            }
        }
        // TODO: PROGRAMO - height
        return visibleWidth;
    }
    public int getWavesVisibleHeight() {
        int visibleHeighth = 0;
        for(WaveMainPanel waveMainPanel : waves) {
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

        if(isMouseButtonPressed && shouldMarkPart) {
            int update = newVal - oldVal;
            if(update < 0) {
                update = -update;
            }
            updateMarkEndXVariablesBasedOnPixel(update, oldVal);
        }

        this.repaint();
    }

    private void passHorizontalScrollChangeToWaves(int oldVal, int newVal) {
        for(WaveMainPanel wave : waves) {
            wave.updateWaveDrawValues(oldVal, newVal);  // TODO: Possible bug - I will have to recalculate it somehow - maybe it is enough to just pass this
        }
    }


    private class ViewportChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {

//            passHorizontalScrollChangeToWaves(0, 0);      // TODO: HNED
            //System.out.println(e);
//            System.exit(123456);
        }
    }


    public void pollMovement() {
        if (thisFrame.getHasFocus()) {
            // TODO: CAN ZOOMif(getCanZoom()) {
            if (canPoll) {
                waveScrollerWrapperPanel.setIsScrollbarBeingUsed(true);
                Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(mouseLoc, panelWithWaves);

                if(getWaveMarkIsBeingDragged()) {
                    int w = this.getWidth();
                    int distanceFromBorder = w / 16;
                    // Micro-optim put these into branches - but it should be done by compiler
                    JScrollBar bar = waveScrollerWrapperPanel.getWaveScroller().getHorizontalScrollBar();
                    int horizontalMovement = HORIZONTAL_SCROLL_UNIT_INCREMENT;

                    WaveMainPanel lastWave = waves.get(waves.size() - 1);
                    int lastWaveEndY = lastWave.getY() + lastWave.getHeight();

                    if (mouseLoc.y > 0 && mouseLoc.y < lastWaveEndY) {
                        int boundToMoveRight = w - getPanelWithWavesVerticalScrollbarWidth();
                        int boundToMoveLeft = waves.get(0).getWaveStartX();
                        // TODO: DEBUG
//                    int boundToMoveRight = w - getPanelWithWavesVerticalScrollbarWidth() - 100;
//                    int boundToMoveLeft = waves.get(0).getWaveStartX() + 100;
                        // TODO: DEBUG

                        if (mouseLoc.x >= boundToMoveRight) {
                            bar.setValue(bar.getValue() + horizontalMovement);
                        } else if (!waves.isEmpty() && mouseLoc.x < boundToMoveLeft) {
                            bar.setValue(bar.getValue() - horizontalMovement);
                        }
                    }
                }


                JViewport view = panelWithWaves.getViewport();
                if(getIsAnySplitterDragged()) {
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
                    } else if (mouseLoc.y > view.getViewRect().height && lastSplitterUI != null && !getIsLastSplitterDragged()) {
                        final int verticalMovement = VERTICAL_SCROLL_UNIT_INCREMENT;
                        int movementDown = convertToPixelMovement(verticalMovement);

                        Point oldPos = view.getViewPosition();
                        Point newPos = new Point(oldPos.x, oldPos.y + movementDown);
                        int maxY = view.getViewSize().height - view.getViewRect().height;
                        if (newPos.y > maxY) {
                            newPos.y = maxY;
                        }
// TODO: DEBUG
//                    ProgramTest.debugPrint("Polling vertical down",
//                        panelWithWaves.getVerticalScrollBar().getValue(),
//                        panelWithWaves.getVerticalScrollBar().getMaximum() - panelWithWaves.getVerticalScrollBar().getModel().getExtent());
// TODO: DEBUG
                        view.setViewPosition(newPos);
                    }
                }

                panelWithWaves.revalidate();
                panelWithWaves.repaint();
                waveScrollerWrapperPanel.setIsScrollbarBeingUsed(false);
            }

// TODO: DEBUG PRINT
//        for(WaveMainPanel wave : waves) {
//            System.out.println(wave.getLocation());
//        }
//        ProgramTest.debugPrint(mouseLoc.y > panelWithWaves.getY(), mouseLoc.y < lastWaveEndY);
//        ProgramTest.debugPrint(mouseLoc.y, panelWithWaves.getY(), mouseLoc.y, lastWaveEndY,
//            lastWave.getY(), lastWave.getHeight(), this.getY());
            // TODO: CAN ZOOM}
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
         * @param wp
         */
        public void removeWaveFromClipboard(WaveMainPanel wp) {
            if(isEqualToClipboardWavePanel(wp)) {
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
        if(shouldMarkPart) {
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
                int newLen = wave.pasteWithOverwriting(clipboard.getWave().getSong(), clipboard.getMarkStartSample(), startPasteIndex,
                        clipboardLen, copyCount, clipboard.getIsCut());
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
                int newLen = wave.paste(clipboard.getWave().getSong(), clipboard.getMarkStartSample(), startPasteIndex,
                        clipboardLen, copyCount, clipboard.getIsCut());
                alignAllWavesToLen(wave.getDoubleWave(), newLen, startPasteIndex, copyLen);
            }
        });
    }

    private void alignAllWavesToLen(DoubleWave pasteWave, int newLen, int startPasteIndex, int copyLen) {
        // The copied wave has already values set to 0 if there is cutting involved
        for(WaveMainPanel wave : waves) {
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
                if(newLen > oldLen) {
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
                if(shouldMarkPart) {
                    double[] waveAudio = wave.getDoubleWave().getSong();
                    Program.setOneDimArr(waveAudio, getMarkStartXSample(), getMarkEndXSample(), 0);
                }
                else {
                    double[] waveAudio = wave.getDoubleWave().getSong();
                    int len = wave.getDoubleWave().getSongLength();
                    Program.setOneDimArr(waveAudio, 0, len, 0);
                }

                wave.reloadDrawValues();
            }
        }, true, false);

    }


    public boolean getIsWaveInClipboard() {
        return clipboard.isWaveInClipboard();
    }
}
