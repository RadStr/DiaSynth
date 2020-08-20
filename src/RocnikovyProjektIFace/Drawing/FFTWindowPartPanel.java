package RocnikovyProjektIFace.Drawing;

public class FFTWindowPartPanel extends FFTWindowPanelAbstract {
    public FFTWindowPartPanel(FFTWindowRealAndImagPanel controlPanel, double[] song, int windowSize, int startIndex, int sampleRate, int numberOfChannels) {
        this(controlPanel, song, windowSize, startIndex, Rocnikovy_Projekt.Program.getFreqJump(sampleRate, windowSize), numberOfChannels);
    }

    public FFTWindowPartPanel(FFTWindowRealAndImagPanel controlPanel, double[] song, int windowSize, int startIndex, double freqJump, int numberOfChannels) {
        super(song, windowSize, startIndex, freqJump, numberOfChannels);
        this.controlPanel = controlPanel;

        // TODO: NEZDA SE MI
        for(int i = 0; i < drawValuesStrings.length; i++) {
            setDrawValue(i, normalizeValue(drawValues[i]));
        }
        // TODO: NEZDA SE MI

        setLastPartOfTooltip();
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
    protected void setBinMeasure(int bin, int y) {
        int h = this.getHeight();
        double binValue = 1 - (y / (double)h);
        if(binValue < 0) {
            binValue = 0;
        }
        else if(binValue > 1) {
            binValue = 1;
        }

        controlPanel.setMeasures(this, bin, binValue);
    }


}