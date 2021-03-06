package str.rad.player.experimental;

import str.rad.util.Aggregation;
import org.jtransforms.fft.DoubleFFT_1D;
import str.rad.util.audio.AudioUtilities;
import str.rad.util.audio.FFT;

import java.awt.*;

public abstract class FFTWindowPanelAbstract extends DrawPanel {
    public FFTWindowPanelAbstract(int windowSize, int sampleRate,
                                  boolean isEditable, boolean areValuesSigned,
                                  Color backgroundColor, boolean shouldDrawLabelsAtTop,
                                  boolean shouldDrawLineInMiddle) {
        this(windowSize, AudioUtilities.computeFreqJump(sampleRate, windowSize), isEditable,
             areValuesSigned, backgroundColor, shouldDrawLabelsAtTop, shouldDrawLineInMiddle);
    }

    public FFTWindowPanelAbstract(int windowSize, double freqJump,
                                  boolean isEditable, boolean areValuesSigned,
                                  Color backgroundColor, boolean shouldDrawLabelsAtTop,
                                  boolean shouldDrawLineInMiddle) {
        super(FFT.getBinCountRealForward(windowSize), "Frequency",
              isEditable, areValuesSigned, false, backgroundColor,
              shouldDrawLabelsAtTop, shouldDrawLineInMiddle);
        this.WINDOW_SIZE = windowSize;
        this.FREQ_JUMP = freqJump;
        int binCount = FFT.getBinCountRealForward(windowSize);
        labels = AudioUtilities.computeFreqs(binCount, freqJump, 0, 1, 3);

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


    //The normalization is quite problematic, I am still not sure what is the right way to solve it.
    // Either like this where I just divide (multiply before IFFT) by 2 * windowSize or
    // just divide everything except the first bin by windowSize and the 0th bin divide by 2 * windowSize.
    @Override
    protected double normalizeValue(double value) {
        return value / (2 * DRAW_VALUES.length);
    }


    protected void drawBin(Graphics g, double drawValue, int currX, int binWidth, int h) {
        int height = (int) (drawValue * h);
        g.fillRect(currX, h - height, binWidth, height);
    }


    public abstract FFTWindowPanelAbstract createNewFFTPanel(int windowSize, boolean shouldChangeWindowSize,
                                                             int sampleRate, boolean shouldChangeSampleRate);


    private double maxAbsolute;

    /**
     * @return Returns the maximum absolute value in the FFT bins.
     */
    public double makeRelativeValues() {
        if (getIsEditable()) {
            return -1;
        }
        else {
            setIsEditable(true);
            maxAbsolute = Aggregation.performAggregation(DRAW_VALUES, Aggregation.ABS_MAX);
            for (int i = 0; i < DRAW_VALUES.length; i++) {
                setDrawValue(i, DRAW_VALUES[i] / maxAbsolute);
            }
            setIsEditable(false);
            repaint();
            return maxAbsolute;
        }
    }

    public void makeAbsoluteValues() {
        if (!getIsEditable()) {
            setIsEditable(true);
            for (int i = 0; i < DRAW_VALUES.length; i++) {
                setDrawValue(i, DRAW_VALUES[i] * maxAbsolute);
            }
            setIsEditable(false);
            repaint();
        }
    }
}
