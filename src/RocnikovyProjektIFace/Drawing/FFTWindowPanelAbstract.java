package RocnikovyProjektIFace.Drawing;

import Rocnikovy_Projekt.Aggregations;
import Rocnikovy_Projekt.Program;
import org.jtransforms.fft.DoubleFFT_1D;

import java.awt.*;

public abstract class FFTWindowPanelAbstract extends DrawPanel {
    public FFTWindowPanelAbstract(double[] song, int windowSize, int startIndex,
                                  int sampleRate, boolean isEditable, boolean areValuesSigned,
                                  Color backgroundColor, boolean shouldDrawLabelsAtTop,
                                  boolean shouldDrawLineInMiddle) {
        this(song, windowSize, startIndex,
                Rocnikovy_Projekt.Program.getFreqJump(sampleRate, windowSize),
                isEditable, areValuesSigned, backgroundColor, shouldDrawLabelsAtTop, shouldDrawLineInMiddle);
    }

    public FFTWindowPanelAbstract(double[] song, int windowSize,
                                  int startIndex, double freqJump,
                                  boolean isEditable, boolean areValuesSigned,
                                  Color backgroundColor, boolean shouldDrawLabelsAtTop,
                                  boolean shouldDrawLineInMiddle) {
        super(Rocnikovy_Projekt.Program.getBinCountRealForward(windowSize), "Frequency",
                isEditable, areValuesSigned, false, backgroundColor, shouldDrawLabelsAtTop,
                shouldDrawLineInMiddle);
        this.WINDOW_SIZE = windowSize;
        this.FREQ_JUMP = freqJump;
        int binCount = Rocnikovy_Projekt.Program.getBinCountRealForward(windowSize);
        labels = Rocnikovy_Projekt.Program.getFreqs(binCount, freqJump, 0, 1);

        fftResult = new double[windowSize];
        fft = new DoubleFFT_1D(windowSize);
    }

    protected final double[] fftResult;
    protected final DoubleFFT_1D fft;
    public final int WINDOW_SIZE;
    public final double FREQ_JUMP;


    private final Color BIN_COLOR_RED = new Color(230, 0, 0);
    @Override
    protected Color getBinColor(int bin) {
        return BIN_COLOR_RED;
    }


    @Override
    protected double normalizeValue(double value) {
        return value / DRAW_VALUES.length;
    }


    protected void drawBin(Graphics g, double drawValue, int currX, int binWidth, int h) {
        int height = (int) (drawValue * h);
        g.fillRect(currX, h - height, binWidth, height);
    }


    public abstract FFTWindowPanelAbstract createNewFFTPanel(int windowSize, boolean shouldChangeWindowSize,
                                                             int sampleRate, boolean shouldChangeSampleRate);



    private double maxAbsolute;
    public double makeRelativeValues() {
        if(getIsEditable()) {
            return -1;
        }
        else {
            setIsEditable(true);
            maxAbsolute = Program.performAggregation(DRAW_VALUES, Aggregations.ABS_MAX);
            for (int i = 0; i < DRAW_VALUES.length; i++) {
                setDrawValue(i, DRAW_VALUES[i] / maxAbsolute);
            }
            setIsEditable(false);
            repaint();
            return maxAbsolute;
        }
    }

    public void makeAbsoluteValues() {
        if(!getIsEditable()) {
            setIsEditable(true);
            for (int i = 0; i < DRAW_VALUES.length; i++) {
                setDrawValue(i, DRAW_VALUES[i] * maxAbsolute);
            }
            setIsEditable(false);
            repaint();
        }
    }
}
