package player.wave;

import player.AudioPlayerPanel;
import player.wave.util.LimitDocumentFilterInt;
import player.wave.util.LimitGetterIFace;
import util.audio.format.ChannelCount;
import player.popup.WavePanelPopupMenuActionsIFace;
import util.swing.JTextFieldResizeable;
import util.audio.wave.DoubleWave;
import test.ProgramTest;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.*;

public class WaveMainPanel extends JPanel implements WaveMixPanelUpdaterIFace,
                                                     WavePanelPopupMenuActionsIFace {
    private final String FONT_NAME = "Serif";

    private WaveMixPanel mixPanel;

    public void updateChannelSliders(ChannelCount channelCount) {
        mixPanel.updateChannelCount(channelCount);
    }

    public double getMixMultiplier(int channel) {
        return mixPanel.getNthChannelMixSliderNormalizedVal(channel);
    }

    @Override
    public void update(int channel, double newValue) {
        panelWithWaves.updateMultFactors(this, channel, newValue);
    }

    private WavePanel wave;
    private WaveButtonPanel buttonPanel;


    private AudioPlayerPanel panelWithWaves;

    /**
     * Wave index: Indexed from 1
     */
    private JTextFieldResizeable waveIndexTextField;

    public String getWaveIndexTextFieldText() {
        return waveIndexTextField.getText();
    }

    public void setWaveIndexTextField(String val) {
        waveIndexTextField.setText(val);
    }

    public Dimension upgradeWaveIndexTextFieldPreferredSize(int charCount) {
        Dimension newSize = waveIndexTextField.setPreferredSize(charCount);
        return newSize;
    }

    public void upgradeWaveIndexTextFieldPreferredSize(Dimension preferredSize) {
        waveIndexTextField.setInternalPreferredSize(preferredSize);
    }

    /**
     * Is the index of the wave ... Indexed from 1
     */
    private int waveIndex;

    public int getWaveIndex() {
        return waveIndex;
    }

    public void setWaveIndex(int val) {
        waveIndex = val;
    }


    public void setCurrentFontSize(int currentFontSize) {
        Graphics g = this.getGraphics();
        g.setFont(new Font(FONT_NAME, Font.BOLD, currentFontSize));
    }

    private boolean focusLostByEnterPress;
    private boolean dragging;

    public WaveMainPanel(DoubleWave doubleWave, AudioPlayerPanel panelWithWaves,
                         int waveIndex, ChannelCount channelCountInOutputAudio) {
        WaveMainPanel thisPanel = this;
        dragging = false;

        this.panelWithWaves = panelWithWaves;
        this.waveIndex = waveIndex;


        Border border = BorderFactory.createLineBorder(Color.black);

        this.setBorder(border);
        this.setLayout(new GridBagLayout());


        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!dragging) {
                    dragging = true;
                    thisPanel.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragging) {
                    dragging = false;
                    thisPanel.repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                panelWithWaves.tryMoveSwap(thisPanel, e);

                int x = e.getX();
                int y = e.getY();
                Point p = new Point(x, y);
                SwingUtilities.convertPointToScreen(p, thisPanel);
                panelWithWaves.tryMoveJSplitPane(p);
            }
        };

        this.addMouseMotionListener(adapter);
        this.addMouseListener(adapter);


        //this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        int currGridX = 0;
        GridBagConstraints constraints;

        // Put in the number of wave
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = currGridX;
        currGridX++;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        waveIndexTextField = new JTextFieldResizeable(Integer.toString(waveIndex));
        waveIndexTextField.setToolTipText("Wave index (change the value to swap position with other wave)");


        LimitGetterIFace getterImpl = new LimitGetterIFace() {
            @Override
            public int getLimit() {
                return panelWithWaves.getWaveCount();
            }

            @Override
            public void revalidateMethod() {
                thisPanel.revalidate();
            }
        };
        ((AbstractDocument) waveIndexTextField.getDocument()).setDocumentFilter(new LimitDocumentFilterInt(getterImpl));

        focusLostByEnterPress = false;

        // Listener for when 'enter' was pressed that means we need to swap the waves
        waveIndexTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // We know that it is int, because only ints can be written to the JTextFieldResizeable
                // (It is checked in LimitDocumentFilterInt)
                thisPanel.focusLostByEnterPress = true;
                int newIndex = Integer.parseInt(e.getActionCommand());
                panelWithWaves.swapSplitterComponents(thisPanel.getWaveIndex(), newIndex,
                                                      waveIndexTextField.getText());

                panelWithWaves.revalidateAndRepaintWaves();
                panelWithWaves.revalidate();
                panelWithWaves.repaint();
                thisPanel.revalidate();
                thisPanel.repaint();
            }
        });


        FocusListener textFieldFocusListener = new FocusListener() {
            public void focusGained(FocusEvent focusEvent) {
                // EMPTY
            }

            public void focusLost(FocusEvent focusEvent) {
                if (thisPanel.focusLostByEnterPress) {
                    thisPanel.focusLostByEnterPress = false;
                }
                else {
                    String oldIndex = Integer.toString(thisPanel.getWaveIndex());
                    setWaveIndexTextField(oldIndex);
                }
            }
        };

        waveIndexTextField.addFocusListener(textFieldFocusListener);
        this.add(waveIndexTextField, constraints);


        int minSliderVal = 0;
        int maxSliderVal = 100;
        int defSliderVal = maxSliderVal;
        mixPanel = new WaveMixPanel(SwingConstants.HORIZONTAL, minSliderVal, maxSliderVal, defSliderVal,
                                    true, channelCountInOutputAudio, this);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = currGridX;
        currGridX++;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0.1;
        this.add(mixPanel, constraints);
        mixPanel.setBorder(border);

        buttonPanel = new WaveButtonPanel(this);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = currGridX;
        currGridX++;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0.1;
        this.add(buttonPanel, constraints);

        VerticalReferencesPanel referenceValues = new VerticalReferencesPanel(-1, 1);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = currGridX;
        currGridX++;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0.1;
        this.add(referenceValues, constraints);

        wave = new WavePanel(doubleWave, this);
        // It is not needed to create new instance for each component, but it is safer,
        // since if I don't reset certain components it can introduce bugs
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = currGridX;
        currGridX++;
        constraints.gridy = 0;
        constraints.weightx = 0.1;
        constraints.weighty = 0.1;
        this.add(wave, constraints);

        WavePanelMouseListener mouseListenerForWave = new WavePanelMouseListener(this);
        wave.addMouseListener(mouseListenerForWave);
        wave.addMouseMotionListener(mouseListenerForWave);

        prefSize = super.getPreferredSize();
        setWavePreferredHeight();
    }


    public static int getVerticalInsets(Insets insets) {
        return insets.top + insets.bottom;
    }


    public int calculateWavePreferredHeight() {
        int insets = getVerticalInsets(wave.getInsets());
        return prefSize.height - insets;
    }

    private void setWavePreferredHeight() {
        int newPrefHeight = calculateWavePreferredHeight();
        wave.setPreferredHeight(newPrefHeight);
    }


    public void visibleWidthChangedCallback() {
        wave.visibleWidthChangedCallback();
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (dragging) {
            waveIndexTextField.setBackground(Color.red);
        }
        else {
            waveIndexTextField.setBackground(Color.white);
        }
    }


    // Because I don't want java to control the size when resizing happens, etc.
    @Override
    public Dimension getSize() {
        return prefSize;
    }

    @Override
    public int getWidth() {
        return prefSize.width;
    }

    @Override
    public int getHeight() {
        return prefSize.height;
    }

    private Dimension prefSize = null;

    @Override
    public Dimension getPreferredSize() {
        return prefSize;
    }

    @Override
    public void setPreferredSize(Dimension d) {
        Dimension min = this.getMinimumSize();
        if (d.width < prefSize.width) {
            d.width = prefSize.width;
        }
        if (d.height < min.height) {
            d.height = min.height;
        }

        prefSize = d;
        setWavePreferredHeight();
    }

    public void setPreferredSize(int w, int h) {
        Dimension d = new Dimension(w, h);
        setPreferredSize(d);
        setWavePreferredHeight();
    }

    public void setPreferredSize(int h) {
        Dimension min = this.getMinimumSize();
        if (h < min.height) {
            h = min.height;
        }

        prefSize.height = h;
        setWavePreferredHeight();
    }

    /**
     * @param h is the new height
     * @return Returns the difference between the new height and minimum height or 0 if the minimum height is bigger than the new height.
     * Returned number is always <= 0.
     */
    public int setPreferredSizeByAdding(int h) {
        Dimension min = this.getMinimumSize();
        h += prefSize.height;
        int dif = h - min.height;
        if (dif < 0) {
            h = min.height;
        }
        else {
            dif = 0;
        }

        prefSize.height = h;
        setWavePreferredHeight();
        return dif;
    }

    public void setPrefSizeToMin() {
        Dimension min = this.getMinimumSize();
        Dimension d = new Dimension(prefSize.width, min.height);
        prefSize = d;
        setWavePreferredHeight();
    }

    /**
     * Adds the parameter to the preferred size and if the result is smaller than min height than returns the difference of
     * the new height and min height else returns 0.
     *
     * @param h
     * @return
     */
    public int getDif(int h) {
        Dimension min = this.getMinimumSize();
        h += prefSize.height;
        int dif = h - min.height;
        if (dif >= 0) {
            dif = 0;
        }
        return dif;
    }

    public void updatePreferredSize() {
        prefSize.width = super.getPreferredSize().width;
        ProgramTest.debugPrint("updatePreferredSize", getPreferredSize(), wave.getPreferredSize());
        if (prefSize.height < getMinimumSize().height) {
            prefSize.height = super.getPreferredSize().height;
        }
    }

    // TODO: Tohle je dost iffy asi to změním a předám rovnou interface těm třídám, ať si to volají.
    // This panel also serves like interface between the panels on this panel and the on which is this panel
    // That means the child components can call only methods on the AudioPlayerPanel through this panel
    // and AudioPlayerPanel can only get info of child components through this panel

    public int getWaveStartX() {
        int x;
        x = wave.getX();
        return x;
    }

    public int getWaveEndX() {
        int x;
        x = wave.getX() + wave.getWidth();
        return x;
    }

    public int getWaveWidth() {
        int w;
        w = wave.getWaveWidth();
        return w;
    }

    public int getDefaultWaveWidth() {
        int w = wave.getDefaultWaveWidthInPixels();
        return w;
    }

    /**
     * Returns the real visible width of wave
     * (When the wave isn't visible, then the getVisibleRect().width on wave returns 0, that is not real what I want.
     * I want the visible width as if it was visible, which this method returns).
     *
     * @return
     */
    public int getWaveVisibleWidth() {
        return panelWithWaves.getWavesVisibleWidth();
    }

    public int getWaveVisibleHeight() {
        return panelWithWaves.getWavesVisibleHeight();
    }

    public Rectangle getWaveRectangle() {
        return panelWithWaves.getScrollPanelViewRect();
    }


    public double getTimeLineX() {
        return panelWithWaves.getTimeLineX();
    }

    public boolean getShouldIncludeInMixing() {
        return buttonPanel.shouldIncludeInMixing();
    }

    public boolean getShouldIncludeInOperations() {
        return buttonPanel.shouldIncludeInOperations();
    }

    public boolean getShouldMarkPart() {
        return panelWithWaves.getShouldMarkPart();
    }

    public int getMarkStartXPixel() {
        if (getShouldMarkPart()) {
            return panelWithWaves.getMarkStartXPixel();
        }
        else {
            return 0;
        }
    }

    public int getMarkStartXSample() {
        if (getShouldMarkPart()) {
            return panelWithWaves.getMarkStartXSample();
        }
        else {
            return 0;
        }
    }


    public int getMarkEndXPixel() {
        if (getShouldMarkPart()) {
            return panelWithWaves.getMarkEndXPixel();
        }
        else {
            return getDoubleWaveLength();
        }
    }

    public int getMarkEndXSample() {
        if (getShouldMarkPart()) {
            return panelWithWaves.getMarkEndXSample();
        }
        else {
            return 0;
        }
    }


    public int getCurrentHorizontalScroll() {
        return panelWithWaves.getCurrentHorizontalScroll();
    }

    public int getMaxHorizontalScroll() {
        return panelWithWaves.getMaxHorizontalScroll();
    }

    public int getCurrentZoom() {
        return panelWithWaves.getCurrentZoom();
    }


    public int getDoubleWaveLength() {
        return panelWithWaves.getDoubleWaveLength();
    }

    public int getOutputSampleRate() {
        return panelWithWaves.getOutputSampleRate();
    }

    public int getNumberOfChannelsInOutputFormat() {
        return panelWithWaves.getNumberOfChannelsInOutputFormat();
    }

    public void processUserClickedWaveEvent(double timeLineX, int userSelectedSample, int currPlayTimeInMillis) {
        panelWithWaves.setCurrSampleUserSelected(userSelectedSample);
        panelWithWaves.setTimeLineXUserSelected(timeLineX);
        // TODO: Show play time
//        panelWithWaves.setCurrPlayTimeInMillis(currPlayTimeInMillis);
        // TODO: Show play time
        panelWithWaves.setUserClickedWave(true);
        panelWithWaves.switchToUserSelectedSampleIfPaused();
    }

    public void setShouldMarkPart(boolean val) {
        panelWithWaves.setShouldMarkPart(val);
    }

    public void setMarkStartXVariablesBasedOnPixel(int sample) {
        panelWithWaves.setMarkStartXVariablesBasedOnPixel(sample);
    }

    public void setMarkEndXVariablesBasedOnPixel(int pixel) {
        panelWithWaves.setMarkEndXVariablesBasedOnPixel(pixel);
    }


    public void draggingWave() {
        panelWithWaves.setWaveMarkIsBeingDragged(true);
    }

    public void stoppedDraggingWave() {
        panelWithWaves.setWaveMarkIsBeingDragged(false);
    }


    public void repaintPanelWithMultipleWaves() {
        panelWithWaves.repaint();
    }

    public double getNthSample(int n) {
        return wave.getNthSample(n);
    }

    public int getSongLen() {
        return wave.getSongLen();
    }


    public void setWaveTooltipText(String text) {
        wave.setToolTipText(text);
    }


    public void updateZoom(int newZoom, int scrollBeforeZoom, boolean shouldZoomToMid, boolean shouldZoomToEnd) {
        wave.updateZoom(newZoom, scrollBeforeZoom, shouldZoomToMid, shouldZoomToEnd);
    }

    public void updateWaveDrawValues(int oldVal, int newVal) {
        wave.updateWaveDrawValues(oldVal, newVal);
    }

    public void mouseButtonPressed() {
        panelWithWaves.setMouseButtonPress(true);
    }

    public void mouseButtonUnpressed() {
        panelWithWaves.setMouseButtonPress(false);
    }

    public int getAudioLenInFrames() {
        return wave.getSongLen();
    }

    public int getMaxPossibleZoom() {
        return wave.getMaxPossibleZoom();
    }

    public void reloadDrawValues() {
        wave.reloadDrawValues();
    }


    public void updateHorizontalScrollSize() {
        int newScrollWidth = getHorizontalScrollSizeForThisWave();
        panelWithWaves.setWaveScrollPanelsSizes(wave.getX(), newScrollWidth);
    }

    public int getHorizontalScrollSizeForThisWave() {
        int newScrollWidth = wave.getWaveWidth();
        return newScrollWidth;
    }

    public int getHorizontalScrollBarWidth() {
        return panelWithWaves.getPanelWithWavesVerticalScrollbarWidth();
    }

    public JPanel getWave() {
        return wave;
    }

    public DoubleWave getDoubleWave() {
        return wave.getDoubleWave();
    }


    // These 2 methods are only called from wave, to sync with the other waves.
    public int getDefaultWaveWidthFromMainPanel() {
        int waveWidth = panelWithWaves.getDefaultWaveWidth();
        if (waveWidth == 0) {
            waveWidth = WavePanel.START_DEFAULT_WAVE_WIDTH_IN_PIXELS;
        }
        return waveWidth;
    }

    public int getWaveWidthFromMainPanel() {
        int defaultWaveWidth = panelWithWaves.getWaveWidth();
        if (defaultWaveWidth == 0) {
            defaultWaveWidth = WavePanel.START_DEFAULT_WAVE_WIDTH_IN_PIXELS;
        }
        return defaultWaveWidth;
    }


    public void updateWavesForMixing() {
        panelWithWaves.updateWavesForMixing();
        this.repaint();
    }


    public void setNewDoubleWave(int newLen, int startPasteIndex, int copyLen) {
        wave.setNewDoubleWave(newLen, startPasteIndex, copyLen);
    }

    public void setNewDoubleWave(int newLen) {
        wave.setNewDoubleWave(newLen);
    }


    public int paste(double[] arrToCopy, int startCopyIndex, int startPasteIndex, int len, int copyCount, boolean isCut) {
        return wave.paste(arrToCopy, startCopyIndex, startPasteIndex, len, copyCount, isCut);
    }

    public int pasteWithOverwriting(double[] arrToCopy, int startCopyIndex, int startPasteIndex, int len, int copyCount, boolean isCut) {
        return wave.pasteWithOverwriting(arrToCopy, startCopyIndex, startPasteIndex, len, copyCount, isCut);
    }

    public int moveWave(int oldStartIndex, int newStartIndex, int len) {
        return wave.moveWave(oldStartIndex, newStartIndex, len);
    }

    public void removeWavePart(int startSample, int endSample) {
        wave.remove(startSample, endSample);
    }

    public int getBotDividerLoc() {
        return panelWithWaves.getJSplitPaneDividerLoc(this);
    }


    public AudioPlayerPanel.ClipboardDrawView getClipboardDrawView() {
        return panelWithWaves.getClipboardDrawView();
    }


    @Override
    public void copyWave() {
        panelWithWaves.copyWave(this, false);
    }

    @Override
    public void cutWave() {
        panelWithWaves.copyWave(this, true);
    }

    @Override
    public void pasteWaveWithOverwriting(int copyCount) {
        panelWithWaves.pasteWaveWithOverwriting(this, lastRightButtonPressMouseEvent, copyCount);
    }

    @Override
    public void pasteWave(int copyCount) {
        panelWithWaves.pasteWave(this, lastRightButtonPressMouseEvent, copyCount);
    }

    @Override
    public void moveWave() {
        panelWithWaves.moveWave(this, lastRightButtonPressMouseEvent);
    }

    @Override
    public void cleanWave() {
        panelWithWaves.cleanWave(this);
    }

    @Override
    public void removeWave() {
        panelWithWaves.removeWave(this);
    }

    private MouseEvent lastRightButtonPressMouseEvent = null;

    public void setLastRightPressMouseEvent(MouseEvent e) {
        wave.setEnabledWithWavePopUpItems(panelWithWaves.getIsWaveInClipboard());
        lastRightButtonPressMouseEvent = e;
    }


    public void setWaveToNewSampleRate(int newSampleRate) {
        wave.setWaveToNewSampleRate(newSampleRate);
    }


    public int convertSampleToMillis(int sampleIndex) {
        return DoubleWave.convertSampleToMillis(sampleIndex, wave.getDoubleWave().getSampleRate());
    }


    public boolean getScrollReceivedResizeEvent() {
        return panelWithWaves.getScrollReceivedResizeEvent();
    }

    public void processScrollReceivedResizeEvent() {
        panelWithWaves.processScrollReceivedResizeEvent();
    }

    public boolean isInProcessOfZooming() {
        return !panelWithWaves.getCanZoom();
    }

    public boolean isLastWave() {
        return waveIndex == panelWithWaves.getWaveCount();
    }

    public void saveZoomBridgeImg() {
        wave.saveZoomBridgeImg();
    }
}
