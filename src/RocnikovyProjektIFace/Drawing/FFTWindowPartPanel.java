package RocnikovyProjektIFace.Drawing;

public class FFTWindowPartPanel extends FFTWindowPanel {
    public FFTWindowPartPanel(FFTWindowRealAndImagPanel controlPanel, double[] song, int windowSize, int startIndex, int sampleRate, int numberOfChannels) {
        super(song, windowSize, startIndex, sampleRate, numberOfChannels);
        this.controlPanel = controlPanel;
    }

    public FFTWindowPartPanel(FFTWindowRealAndImagPanel controlPanel, double[] song, int windowSize, int startIndex, double freqJump, int numberOfChannels) {
        super(song, windowSize, startIndex, freqJump, numberOfChannels);
        this.controlPanel = controlPanel;
    }

    private FFTWindowRealAndImagPanel controlPanel;

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
