package player.experimental;


import Rocnikovy_Projekt.Program;
import util.Time;

import java.awt.*;

public class TimeWaveDrawPanel extends WaveDrawPanel {
    /**
     * @param binCount
     */
    public TimeWaveDrawPanel(int timeInMs, int binCount, boolean isEditable,
                             Color backgroundColor, boolean shouldDrawLabelsAtTop) {
        super(binCount, "Time", isEditable, backgroundColor, shouldDrawLabelsAtTop);
        setTimeInMs(timeInMs);
        setLabels();
        normalizeAndSetDrawValues();
        setLastPartOfTooltip();
    }

    private int timeInMs;
    public int getTimeInMs() {
        return timeInMs;
    }
    private String timeInMsString;
    public void setTimeInMs(int timeInMs) {
        this.timeInMs = timeInMs;
        timeInMsString = Double.toString(timeInMs);
        fillTimeLabels(labels, timeInMs);
        setLastPartOfTooltip();
        repaint();
    }


    /**
     * Isn't called anywhere it is just marker, that the labels needs to be set in deriving class.
     */
    @Override
    protected void setLabels() {
        labels = createLabels(timeInMs, DRAW_VALUES.length);
    }

    public static String[] createLabels(int timeInMs, int binCount) {
        String[] labels = new String[binCount];
        fillTimeLabels(labels, timeInMs);
        return labels;
    }

    private static void fillTimeLabels(String[] labels, int timeInMs) {
        String timeInMsLabel = Time.convertMillisecondsToTime(timeInMs, -1);
        int recursionDepth = Program.calculateCharOccurrences(timeInMsLabel, ':');


        for(int i = 0; i < labels.length; i++) {
            int labelTime = (int)(timeInMs * (i / (double)labels.length));
            labels[i] = Time.convertMillisecondsToTime(labelTime, recursionDepth);
        }
    }


    public double[] getOneSecondWave(int sampleRate) {
        return getOneSecondWave(DRAW_VALUES, sampleRate);
    }

    public static double[] getOneSecondWave(double[] wave, int sampleRate) {
        return getNPeriods(wave, sampleRate, 1, 1000);
    }




    public double[] getNPeriods(int sampleRate, int periodCount) {
        return getNPeriods(DRAW_VALUES, sampleRate, periodCount, timeInMs);
    }

    /**
     * @param periodTime is in milliseconds
     * @return
     */
    public double[] getNPeriods(int sampleRate, int periodCount, double periodTime) {
        return getNPeriods(DRAW_VALUES, sampleRate, periodCount, periodTime);
    }


    /**
     * @param periodTime is in milliseconds
     * @return
     */
    public static double[] getNPeriods(double[] wave, int sampleRate, int periodCount, double periodTime) {
        int len = (int)((periodTime / 1000) * sampleRate);
        double[] arr = new double[len * periodCount];
        double samplesPerPixel = len / (double)(wave.length - 1);
        fillArrWithValues(arr, wave, samplesPerPixel);
        Program.copyArr(arr, len);
        return arr;
    }



    public void fillArrWithValues(double[] arr, double samplesPerPixel) {
        fillArrWithValues(arr, DRAW_VALUES, samplesPerPixel);
    }

    public void fillArrWithValues(double[] arr) {
        fillArrWithValues(arr, DRAW_VALUES);
    }



    private static double calculateSamplesPerPixel(double[] inputArr, double[] outputArr) {
        double spp = outputArr.length / (double)(inputArr.length - 1);
        return spp;
    }


    public static void fillArrWithValues(double[] arr, double[] wave) {
        double samplesPerPixel = calculateSamplesPerPixel(wave, arr);
        fillArrWithValues(arr, wave, samplesPerPixel);
    }

    public static void fillArrWithValues(double[] arr, double[] wave, double samplesPerPixel) {
        double modulo;
        if((int)samplesPerPixel == 0) {
            modulo = samplesPerPixel;
        }
        else {
            modulo = samplesPerPixel % (int)samplesPerPixel;
        }
        double currentSamplesPerPixel = samplesPerPixel;
        double currSample;
        double nextSample = wave[0];
// TODO: VYMAZAT
//        double nextSample = 1 - 2*wave[0];	// 1 in wave == -1, 0 in wave == 1, 0.5 in wave == 0
//        // 0.75 == -0.5, 0.25 == 0.5 ... so it is 1 - 2*wave
// TODO: VYMAZAT
        for(int i = 0, outputIndex = 0; i < wave.length - 1; i++, currentSamplesPerPixel += modulo) {
            currSample = nextSample;
            nextSample = wave[i + 1];
            // TODO: VYMAZAT
            //nextSample = 1 - 2*wave[i + 1];
            // TODO: VYMAZAT

            double jump = (nextSample - currSample) / currentSamplesPerPixel;
            double val = currSample;
            for(int j = 0; j < (int)currentSamplesPerPixel; j++, outputIndex++, val += jump) {
                arr[outputIndex] = val;
            }

            if(currentSamplesPerPixel >= ((int)samplesPerPixel + 1)) {
// TODO: DEBUG				System.out.println("OVER:\t" + i + "\t" + outputIndex + "\t" + currentSamplesPerPixel + "\t" + ((int)samplesPerPixel + 1) + "\t" + modulo);
// TODO: DEBUG				System.out.println(samplesPerPixel * (wave.length - 1));
                currentSamplesPerPixel--;
            }
        }
    }
}