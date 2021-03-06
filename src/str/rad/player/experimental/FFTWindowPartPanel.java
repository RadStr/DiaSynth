package str.rad.player.experimental;

import str.rad.util.audio.AudioUtilities;

import java.awt.*;
import java.awt.event.MouseEvent;

public class FFTWindowPartPanel extends FFTWindowPanelAbstract {
    public FFTWindowPartPanel(FFTWindowRealAndImagWrapper controlPanel, int windowSize,
                              int sampleRate, boolean isEditable,
                              Color backgroundColor, boolean shouldDrawLabelsAtTop) {
        this(controlPanel, windowSize, AudioUtilities.computeFreqJump(sampleRate, windowSize),
             isEditable, backgroundColor, shouldDrawLabelsAtTop);
    }

    public FFTWindowPartPanel(FFTWindowRealAndImagWrapper controlPanel, int windowSize,
                              double freqJump, boolean isEditable,
                              Color backgroundColor, boolean shouldDrawLabelsAtTop) {
        super(windowSize, freqJump, isEditable, true,
              backgroundColor, shouldDrawLabelsAtTop, true);
        this.controlPanel = controlPanel;
    }

    private FFTWindowRealAndImagWrapper controlPanel;

    /**
     * Isn't called anywhere it is just marker, that the labels needs to be set in deriving class.
     */
    @Override
    protected void setLabels() {
        // EMPTY
    }


    @Override
    public FFTWindowPanelAbstract createNewFFTPanel(int windowSize, boolean shouldChangeWindowSize,
                                                    int sampleRate, boolean shouldChangeSampleRate) {
        if (!shouldChangeWindowSize) {
            windowSize = this.WINDOW_SIZE;
        }

        double freqJump;
        if (!shouldChangeSampleRate) {
            freqJump = this.FREQ_JUMP;
        }
        else {
            freqJump = AudioUtilities.computeFreqJump(sampleRate, windowSize);
        }


        return new FFTWindowPartPanel(controlPanel, windowSize, freqJump, getIsEditable(),
                                      getBackgroundColor(), getShouldDrawLabelsAtTop());
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


    // Because there will be 2 in one panel I need to half the height
    private Dimension prefSize = new Dimension();

    @Override
    public Dimension getPreferredSize() {
        Dimension superPrefSize = super.getPreferredSize();
        prefSize.width = superPrefSize.width;
        prefSize.height = (superPrefSize.height - FFTWindowRealAndImagWrapper.SPACE_BETWEEN_PARTS) / 2;
        return prefSize;
    }
}