package player;

import test.ProgramTest;
import player.wave.WavePanel;

public class Test {

    public static boolean testAll() {
        boolean result = true;

        result = result && testCachingCorrectness();

        return result;
    }

    /**
     * Tests caching from the wave.
     */
    public static boolean testCachingCorrectness() {
        boolean result = true;
        double[] input = new double[44000 * 40];
        for(int i = 0; i < input.length; i++) {
            input[i] = i / 44000d;
            input[i] = Math.random();
            if(Math.random() < 0.5) {
                input[i] *= -1;
            }
        }

// Just simple example test
//        input = new double[] {1,2,3,4,5,6,7,8,9,10};
// Just simple example test


        int w = WavePanel.START_DEFAULT_WAVE_WIDTH_IN_PIXELS;
// Just simple example test
//        w = 1;
// Just simple example test

        int maxZoom = WavePanel.calculateMaxCacheZoom(input.length, w);
        int maxWidth = (int)(w * Math.pow(WavePanel.ZOOM_VALUE, maxZoom));
        double[] output = new double[2 * maxWidth];
        double samplesPerPixel = WavePanel.calculateInputValsPerOutputValsPure(input.length, maxWidth);

        double[][] cachedResults = WavePanel.cacheToHDDTest(maxZoom, samplesPerPixel, input, output);
        double[] testOutputArr;
        for (int i = cachedResults.length - 1; i >= 0; i--, w *= 2) {
            testOutputArr = new double[2*w];
//            samplesPerPixel = WavePanel.calculateInputValsPerOutputValsPure(input.length, w);
            WavePanel.findExtremesInValues(input, testOutputArr, 0, 0, input.length, w);
            result = ProgramTest.checkEqualityOfArraysOneDim(cachedResults[i], testOutputArr, 0,0) && result;
        }

        return result;
    }
}
