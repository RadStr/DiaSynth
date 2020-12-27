package player.experimental;

import util.Aggregation;
import org.jtransforms.fft.DoubleFFT_1D;
import util.Utilities;
import util.audio.AudioUtilities;
import util.audio.FFT;

import java.awt.*;

public class FFTWindowPanel extends FFTWindowPanelAbstract {
    public static final int MAX_WINDOW_SIZE = 8192;
    public static final String MAX_WINDOW_SIZE_STRING = "8192";
    public static final int MIN_WINDOW_SIZE = 2;
    public static final String MIN_WINDOW_SIZE_STRING = "2";

    public FFTWindowPanel(double[] song, int windowSize, int startIndex, int sampleRate,
                          boolean isEditable, Color backgroundColor, boolean shouldDrawLabelsAtTop) {
        this(song, windowSize, startIndex, AudioUtilities.computeFreqJump(sampleRate, windowSize),
             isEditable, backgroundColor, shouldDrawLabelsAtTop);
    }

    public FFTWindowPanel(double[] song, int windowSize, int startIndex, double freqJump,
                          boolean isEditable, Color backgroundColor, boolean shouldDrawLabelsAtTop) {
        super(windowSize, freqJump, isEditable, false,
              backgroundColor, shouldDrawLabelsAtTop, false);

        if (song != null) {
            FFT.calculateFFTRealForward(song, startIndex, fftResult.length, 1, fft, fftResult);
        }
        else {
            Utilities.setOneDimArr(fftResult, 0, fftResult.length, 0);
        }
        FFT.convertResultsOfFFTToRealRealForward(fftResult, DRAW_VALUES);
        normalizeAndSetDrawValues();
        setLastPartOfTooltip();
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

        return new FFTWindowPanel(null, windowSize, -1, freqJump,
                                  getIsEditable(), getBackgroundColor(), getShouldDrawLabelsAtTop());
    }


    /**
     * Isn't called anywhere it is just marker, that the labels needs to be set in deriving class.
     */
    @Override
    protected void setLabels() {
        // EMPTY
    }

    @Override
    protected Color getBinColor(int bin) {
        return Color.red;
    }

    @Override
    protected void setBinValue(int bin, int y) {
        int h = this.getHeight();
        double binValue = 1 - (y / (double) h);
        if (binValue < 0) {
            binValue = 0;
        }
        else if (binValue > 1) {
            binValue = 1;
        }

        setDrawValue(bin, binValue);
    }

    public static void normalizeFFTResultsRealForward(double[] fftMeasures) {
        // Normalization and getting string representation
        for (int i = 0; i < fftMeasures.length; i++) {
            fftMeasures[i] *= 2;
            fftMeasures[i] /= (fftMeasures.length / 2);
        }
    }


    public static void getRealIFFT(double[] fftArr, DoubleFFT_1D fft) {
        fft.realInverse(fftArr, true);
    }


    public double[] getIFFTResult(boolean setImagPartToZero, int periodCount) {
        if (setImagPartToZero) {
            FFT.convertFFTAmplitudesToClassicFFTArr(DRAW_VALUES, fftResult);
        }
        else {
            FFT.convertFFTAmplitudesToClassicFFTArrRandom(DRAW_VALUES, fftResult);
        }

        for (int i = 0; i < fftResult.length; i++) {
            // *2 because of the 0-th bin, otherwise *1 would be sufficient
            fftResult[i] *= 2 * DRAW_VALUES.length;
        }

        getRealIFFT(fftResult, fft);
        normalize(fftResult);
        double[] ifftResult = Utilities.copyArr(fftResult, fftResult.length, periodCount);
        return ifftResult;
    }


    public static void normalize(double[] arr) {
        double max = Aggregation.performAggregation(arr, Aggregation.ABS_MAX);

        if (max > 1) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] /= max;
            }
        }
    }
}
