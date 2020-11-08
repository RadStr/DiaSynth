package RocnikovyProjektIFace;

import RocnikovyProjektIFace.AudioFormatChooserPackage.ChannelCount;
import RocnikovyProjektIFace.AudioWavePanelOnlyWavePopupMenuPackage.AudioWavePanelOnlyWavePopupMenuCommunicationIFace;
import RocnikovyProjektIFace.SpecialSwingClasses.JTextFieldResizeable;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.ProgramTest;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.*;

public class AudioWavePanelEverything extends JPanel implements AudioWavePanelOnlyMixSliderUpdateIFace,
    AudioWavePanelOnlyWavePopupMenuCommunicationIFace {
    private final String fontName = "Serif";        // TODO: Idealne moznost nastavit font, ale nevim jak to bude s casem

    private AudioWavePanelOnlyMixSlider mixPanel;
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

    private AudioWavePanelOnlyWave wave;
    private AudioWavePanelOnlyButtons buttonsOnRight;


    private AudioPlayerPanelIFaceImplementation panelWithWaves;

    // Indexed from 1
    private JTextFieldResizeable waveIndexTextField;          // TODO: !!!!!!!!!!!

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


    public void setCurrentFontSize(int currentFontSize) {   // Ten font vezmu z vrchni listy
        Graphics g = this.getGraphics();
        g.setFont(new Font(fontName, Font.BOLD, currentFontSize));
    }

    private boolean focusLostByEnterPress;
    private boolean dragging;

    public AudioWavePanelEverything(DoubleWave doubleWave, AudioPlayerPanelIFaceImplementation panelWithWaves,
                                    int waveIndex, ChannelCount channelCountInOutputAudio) {
// TODO: DEBUG
//        ProgramTest.debugPrint("AudioWavePanelEverything constructor start", super.getPreferredSize());
// TODO: DEBUG
        AudioWavePanelEverything thisPanel = this;
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
//        TODO:)
        // TODO: resize when there are more digits, also write listener, that doesn't let you write bigger number than there are waves
//        TODO:)
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

        // Listener when enter was pressed ... swap the waves
        waveIndexTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Udelat swapovani vlny
                // TODO: Udelat at jsou vsechny stejne velky - aby ten vysledek odpovidal timestampum, tohle by mohlo pomoc:
                // https://stackoverflow.com/questions/2897506/how-can-i-control-the-width-of-jtextfields-in-java-swing

                // We know that it is int, because only ints can be written to the JTextFieldResizeable (It is checked in LimitDocumentFilterInt)
                thisPanel.focusLostByEnterPress = true;
                int newIndex = Integer.parseInt(e.getActionCommand());
                panelWithWaves.swapSplitterComponents(thisPanel.getWaveIndex(), newIndex, waveIndexTextField.getText());

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
                } else {
                    String oldIndex = Integer.toString(thisPanel.getWaveIndex());
                    setWaveIndexTextField(oldIndex);
                }
            }
        };

        waveIndexTextField.addFocusListener(textFieldFocusListener);


//        waveIndexTextField.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
////                swapWaves();
//            }
//        });
//
//        waveIndexTextField.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                int num;
//                if((num = Integer.parseInt(waveIndexTextField.getText())) >= panelWithWaves.getWaveCount()) {
//                    waveIndexTextField.setText();
//                }
//
//                thisPanel.revalidate();
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//            }
//        });


        this.add(waveIndexTextField, constraints);


        int minSliderVal = 0;
        int maxSliderVal = 100;
        //int defSliderVal = (minSliderVal + maxSliderVal) / 2;
        int defSliderVal = maxSliderVal;
        mixPanel = new AudioWavePanelOnlyMixSlider(SwingConstants.HORIZONTAL, minSliderVal, maxSliderVal, defSliderVal,
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

        buttonsOnRight = new AudioWavePanelOnlyButtons(this);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = currGridX;
        currGridX++;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0.1;
        this.add(buttonsOnRight, constraints);


        // TODO: Kdyz zvetsim okno tak odstranim vsechny labely a prekreslim, takze v jeden okamzik tam nejsou zadny labely
        // TODO: Proto bych to mel odstranovat nejak lip asi
        AudioWavePanelReferenceValues referenceValues = new AudioWavePanelReferenceValues(-1, 1);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = currGridX;
        currGridX++;
        constraints.gridy = 0;
        constraints.weightx = 0;        // TODO:
        //constraints.weightx = 1;        // TODO:
        constraints.weighty = 0.1;
        this.add(referenceValues, constraints);

        wave = new AudioWavePanelOnlyWave(doubleWave, this);
        // TODO: It is not needed to create new instance for each component, but it is safer, since if I don't reset certain components it can introduce bugs
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = currGridX;
        currGridX++;
        constraints.gridy = 0;
        constraints.weightx = 0.1;
        constraints.weighty = 0.1;
        this.add(wave, constraints);

        AudioWavePanelOnlyWaveMouseListener mouseListenerForWaves = new AudioWavePanelOnlyWaveMouseListener(this);
        wave.addMouseListener(mouseListenerForWaves);
        wave.addMouseMotionListener(mouseListenerForWaves);

        prefSize = super.getPreferredSize();

// TODO: PROGRAMO - height
        setWavePreferredHeight();
// TODO: PROGRAMO - height
// TODO: DEBUG
//        ProgramTest.debugPrint("AudioWavePanelEverything constructor end", super.getPreferredSize());
// TODO: DEBUG
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
// TODO: DEBUG
        ProgramTest.debugPrint("WWWEverything:", this.getVisibleRect());
// TODO: DEBUG
        wave.visibleWidthChangedCallback();
    }

    public void setHorizontalScrollToMax() {
        //panelWithWaves.setHorizontalScrollToMax();
    }


// TODO:
//    @Override
//    public Dimension getPreferredSize() {
//        int w = this.getWidth();
//        int h = 300;
//
//
//        return new Dimension(w, h);
//    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (dragging) {
            waveIndexTextField.setBackground(Color.red);
        } else {
            waveIndexTextField.setBackground(Color.white);
        }
        //System.exit(66);
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
        // TODO: PROGRAMO
        return prefSize.height;
        //return super.getPreferredSize().height;
        // TODO: PROGRAMO
    }

    private Dimension prefSize = null;
    @Override
    public Dimension getPreferredSize() {
        // TODO: PROGRAMO
        //return new Dimension(prefSize.width, super.getPreferredSize().height);
        return prefSize;
        // TODO: PROGRAMO
        // TODO: ZOOM
        //return new Dimension(10000, prefSize.height);
        // TODO: ZOOM
    }

    @Override
    public void setPreferredSize(Dimension d) {
        Dimension min = this.getMinimumSize();
        if (d.width < prefSize.width) {
            // TODO: PROGRAMO
            d.width = prefSize.width;
            // TODO: PROGRAMO
        }
        if (d.height < min.height) {
            d.height = min.height;
        }
        prefSize = d;

// TODO: PROGRAMO - height
        setWavePreferredHeight();
// TODO: PROGRAMO - height
    }

    public void setPreferredSize(int w, int h) {
        Dimension d = new Dimension(w, h);
        setPreferredSize(d);
// TODO: PROGRAMO - height
        setWavePreferredHeight();
// TODO: PROGRAMO - height
    }

    public void setPreferredSize(int h) {
        Dimension min = this.getMinimumSize();
        if (h < min.height)
            h = min.height;
        prefSize.height = h;
// TODO: PROGRAMO - height
        setWavePreferredHeight();
// TODO: PROGRAMO - height
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
        } else {
            dif = 0;
        }
        prefSize.height = h;

// TODO: PROGRAMO - height
        setWavePreferredHeight();
// TODO: PROGRAMO - height
        return dif;
    }

    public void setPrefSizeToMin() {
        Dimension min = this.getMinimumSize();
        // TODO: PROGRAMO
        Dimension d = new Dimension(prefSize.width, min.height);
        // TODO: PROGRAMO
        prefSize = d;

// TODO: PROGRAMO - height
        setWavePreferredHeight();
// TODO: PROGRAMO - height
    }

    /**
     * Adds the parameter to the preferred size and if the result is smaller than min height than returns the difference of
     * the new height and min height else returns 0.
     * @param h
     * @return
     */
    public int getDif(int h) {
        Dimension min = this.getMinimumSize();
        h += prefSize.height;
        int dif = h - min.height;
        if (dif < 0) {
            h = min.height;
        }
        else {
            dif = 0;
        }
        return dif;
    }

    public void updatePreferredSize() {
        prefSize.width = super.getPreferredSize().width;
        ProgramTest.debugPrint("updatePreferredSize", getPreferredSize(), wave.getPreferredSize());
        // TODO: PROGRAMO
        if(prefSize.height < getMinimumSize().height) {
            prefSize.height = super.getPreferredSize().height;
        }
        // TODO: PROGRAMO
    }

// TODO: REVALIDATE
//    public void revalidateAll() {
//        this.revalidate();
//        this.repaint();
//        panelWithWaves.revalidate();
//        panelWithWaves.repaint();
//    }
// TODO: REVALIDATE


    // TODO: AudioPlayerPanelIFaceImplementation - nazev teto tridy zmenim - takze pozor na to aby se zmenili i stringy v komentarich pri rename
    // This panel also serves like interface between the panels on this panel and the on which is this panel
    // That means the child components can call only methods on the AudioPlayerPanelIFaceImplementation through this panel
    // and AudioPlayerPanelIFaceImplementation can only get info of child components through this panel

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
        // TODO: BYLO wave.getWidth()
        //w = wave.getPreferredSize().width;
        w = wave.getWaveWidth();
        // TODO: BYLO WAVE.getWidth()
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
     * @return
     */
    public int getWaveVisibleWidth() {
        return panelWithWaves.getWavesVisibleWidth();
    }
    public int getWaveVisibleHeight() {
        return panelWithWaves.getWavesVisibleHeight();
    }

    private WaveProportions waveProportions;

    public Rectangle getWaveRectangle() {
        return panelWithWaves.getScrollPanelViewRect();
    }


    public double getTimeLineX() {
        return panelWithWaves.getTimeLineX();
    }

    public boolean getShouldIncludeInMixing() {
        return buttonsOnRight.shouldIncludeInMixing();
    }

    public boolean getShouldIncludeInOperations() {
        return buttonsOnRight.shouldIncludeInOperations();
    }

    public boolean getShouldMarkPart() {
        return panelWithWaves.getShouldMarkPart();
    }

    public int getMarkStartXPixel() {
        if(getShouldMarkPart()) {
            return panelWithWaves.getMarkStartX();
        }
        else {
            return 0;
        }
    }

    public int getMarkStartXSample() {
        if(getShouldMarkPart()) {
            return panelWithWaves.getMarkStartXSample();
        }
        else {
            return 0;
        }
    }


    public int getMarkEndXPixel() {
        if(getShouldMarkPart()) {
            return panelWithWaves.getMarkEndX();
        }
        else {
            return getDoubleWaveLength();
        }
    }

    public int getMarkEndXSample() {
        if (getShouldMarkPart()) {
            return panelWithWaves.getMarkEndXSample();
        } else {
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

    public void setTimeLineXUserSelected(double val) {
        panelWithWaves.setTimeLineXUserSelected(val);
    }

    public void setCurrSampleUserSelected(int val) {
        panelWithWaves.setCurrSampleUserSelected(val);
    }

    public void setCurrPlayTimeInMillis(int currPlayTimeInMillis) {
        panelWithWaves.setCurrPlayTimeInMillis(currPlayTimeInMillis);
    }

    public void setUserClickedWave(boolean val) {
        panelWithWaves.setUserClickedWave(val);
    }

    public void performUserClickedWaveVariableSetPaused() {
        panelWithWaves.performUserClickedWaveVariableSetPaused();
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

    // TODO: PROGRAMO
    public void setVariablesWhichNeededSize() {
        wave.setVariablesWhichNeededSize();
    }
    // TODO: PROGRAMO


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
        panelWithWaves.setEmptyPanelForHorizontalScrollSize(wave.getX(), newScrollWidth);
    }

    public int getHorizontalScrollSizeForThisWave() {
        int newScrollWidth = wave.getWaveWidth();
// TODO: OLD SCROLL SIZE
//        newScrollWidth += panelWithWaves.getPanelWithWavesEverythingVerticalScrollbarWidth();
// TODO: OLD SCROLL SIZE
        ProgramTest.debugPrint("getHorizontalScrollSizeForThisWave", newScrollWidth);
        return newScrollWidth;
    }

    public int getHorizontalScrollBarWidth() {
        return panelWithWaves.getPanelWithWavesEverythingVerticalScrollbarWidth();
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
        // TOOD: PROGRAMO - maybe problem
        // Solves case when there is only 1 wave and also case when adding multiple waves and non of them are set yet
        if(waveWidth == 0) {
            waveWidth = AudioWavePanelOnlyWave.START_DEFAULT_WAVE_WIDTH_IN_PIXELS;
        }
        // TOOD: PROGRAMO - maybe problem

        return waveWidth;
    }
    public int getWaveWidthFromMainPanel() {
        int defaultWaveWidth = panelWithWaves.getWaveWidth();
        // TOOD: PROGRAMO - maybe problem
        // Solves case when there is only 1 wave and also case when adding multiple waves and non of them are set yet
        if(defaultWaveWidth == 0) {
            defaultWaveWidth = AudioWavePanelOnlyWave.START_DEFAULT_WAVE_WIDTH_IN_PIXELS;
        }
        // TOOD: PROGRAMO - maybe problem

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



    public AudioPlayerPanelIFaceImplementation.ClipboardDrawView getClipboardDrawView() {
        return panelWithWaves.getClipboardDrawView();
    }


    // TODO: Just testing if the reset feature works
    @Override
    public void finalize() throws Throwable {
        super.finalize();
        System.out.println("Garbage collected");
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






    public boolean getTodoMarkIsComponentResizing() {
        return panelWithWaves.getTodoMarkIsComponentResizing();
    }
    public boolean getTodoMarkIsZooming() {
        return panelWithWaves.todoMarkIsZooming;
    }
    public void resetTodoMark() {
        panelWithWaves.todoMarkIsZooming = false;
        panelWithWaves.resetTodoMarkIsComponentResizing();
    }

    public boolean getTheZoomingStarted() {
        return !panelWithWaves.getCanZoom();
    }
}
