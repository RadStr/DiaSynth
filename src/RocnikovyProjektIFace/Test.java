package RocnikovyProjektIFace;

import Rocnikovy_Projekt.Program;
import Rocnikovy_Projekt.ProgramTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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


        int w = AudioWavePanelOnlyWave.START_DEFAULT_WAVE_WIDTH_IN_PIXELS;
// Just simple example test
//        w = 1;
// Just simple example test

        int maxZoom = AudioWavePanelOnlyWave.calculateMaxCacheZoom(input.length, w);
        int maxWidth = (int)(w * Math.pow(AudioWavePanelOnlyWave.ZOOM_VALUE, maxZoom));
        double[] output = new double[2 * maxWidth];
        double samplesPerPixel = AudioWavePanelOnlyWave.calculateInputValsPerOutputValsPure(input.length, maxWidth);

        double[][] cachedResults = AudioWavePanelOnlyWave.cacheToHDDTest(maxZoom, samplesPerPixel, input, output);
        double[] testOutputArr;
        for (int i = cachedResults.length - 1; i >= 0; i--, w *= 2) {
            testOutputArr = new double[2*w];
//            samplesPerPixel = AudioWavePanelOnlyWave.calculateInputValsPerOutputValsPure(input.length, w);
            AudioWavePanelOnlyWave.findExtremesInValues(input, testOutputArr, 0, 0, input.length, w);
            result = ProgramTest.checkEqualityOfArraysOneDim(cachedResults[i], testOutputArr, 0,0) && result;
        }

        return result;
    }
}
