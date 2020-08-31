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
        int w = this.getWidth();
        int h = this.getHeight();
        int hh = h / 2;
        double jump;

        y -= hh;
        double binValue = -y / (double) hh; // -y because it is upside-down, value of 1 is at the top of window which is y = 0
        binValue = Math.min(binValue, 1);
        binValue = Math.max(binValue, -1);

        //setDrawValue(bin, value);
        controlPanel.setBinValues(this, bin, binValue);
    }

    @Override
    protected void drawBin(Graphics g, double drawValue, int currX, int binWidth, int h) {
        int midY = h / 2;
        int y = midY - (int) (drawValue * midY);
        if (y < midY) {
            g.drawRect(currX, y, binWidth, midY - y);
        } else {
            g.drawRect(currX, midY, binWidth, y - midY);
        }
    }

//    @Override
//    protected void setBinValue(int bin, int y) {
//        int h = this.getHeight();
//        double binValue = 1 - (y / (double)h);
//        if(binValue < 0) {
//            binValue = 0;
//        }
//        else if(binValue > 1) {
//            binValue = 1;
//        }
//
//        controlPanel.setBinValues(this, bin, binValue);
//    }

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