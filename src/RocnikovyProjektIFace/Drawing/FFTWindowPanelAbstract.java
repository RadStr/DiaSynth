package RocnikovyProjektIFace.Drawing;

import Rocnikovy_Projekt.Aggregations;
import Rocnikovy_Projekt.Program;
import org.jtransforms.fft.DoubleFFT_1D;

import java.awt.*;
import java.util.Arrays;
public abstract class FFTWindowPanelAbstract extends DrawPanel {
    public FFTWindowPanelAbstract(double[] song, int windowSize, int startIndex,
                                  int sampleRate, int numberOfChannels,
                                  boolean isEditable, boolean areValuesSigned) {
        this(song, windowSize, startIndex,
                Rocnikovy_Projekt.Program.getFreqJump(sampleRate, windowSize), numberOfChannels,
                isEditable, areValuesSigned);
    }

    public FFTWindowPanelAbstract(double[] song, int windowSize, int startIndex,
                                  double freqJump, int numberOfChannels,
                                  boolean isEditable, boolean areValuesSigned) {
        super(Rocnikovy_Projekt.Program.getBinCountRealForward(windowSize), "Frequency",
                isEditable, areValuesSigned, false);
        this.freqJump = freqJump;
        int binCount = Rocnikovy_Projekt.Program.getBinCountRealForward(windowSize);
        labels = Rocnikovy_Projekt.Program.getFreqs(binCount, freqJump, 0, 1);

        fftResult = new double[windowSize];
        fft = new DoubleFFT_1D(windowSize);
    }


    protected final double[] fftResult;
    protected final DoubleFFT_1D fft;
    protected final double freqJump;

    @Override
    protected Color getBinColor(int bin) {
        return Color.red;
    }


    @Override
    protected double normalizeValue(double value) {
        return value / drawValues.length;
    }


    protected void drawBin(Graphics g, double drawValue, int currX, int binWidth, int h) {
        int height = (int) (drawValue * h);
        g.fillRect(currX, h - height, binWidth, height);
    }
}