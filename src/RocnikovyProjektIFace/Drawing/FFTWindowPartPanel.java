package RocnikovyProjektIFace.Drawing;

import java.awt.*;
import java.awt.event.MouseEvent;

public class FFTWindowPartPanel extends FFTWindowPanelAbstract {
    public FFTWindowPartPanel(FFTWindowRealAndImagPanel controlPanel, double[] song, int windowSize,
                              int startIndex, int sampleRate, int numberOfChannels, boolean isEditable,
                              Color backgroundColor) {
        this(controlPanel, song, windowSize, startIndex,
                Rocnikovy_Projekt.Program.getFreqJump(sampleRate, windowSize),
                numberOfChannels, isEditable, backgroundColor);
    }

    public FFTWindowPartPanel(FFTWindowRealAndImagPanel controlPanel, double[] song, int windowSize,
                              int startIndex, double freqJump, int numberOfChannels, boolean isEditable,
                              Color backgroundColor) {
        super(song, windowSize, startIndex, freqJump, numberOfChannels,
                isEditable, true, backgroundColor);
        this.controlPanel = controlPanel;
    }

    private FFTWindowRealAndImagPanel controlPanel;

    /**
     * Isn't called anywhere it is just marker, that the labels needs to be set in deriving class.
     */
    @Override
    protected void setLabels() {
        // EMPTY
    }


    @Override
    protected void setBinValue(int bin, int y) {
        int h = this.getHeight();
        double binValue = WaveDrawPanel.calculateBinValue(y, h);
        controlPanel.setBinValues(this, bin, binValue);
    }

    @Override
    protected void drawBin(Graphics g, double drawValue, int currX, int binWidth, int h) {
        WaveDrawPanel.drawBinValueBetweenMinusOneAndOne(g, drawValue, currX, binWidth, h, true);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        controlPanel.setTheOtherPartSelectedBin(this, selectedBin);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        controlPanel.setTheOtherPartSelectedBin(this, selectedBin);
    }
}