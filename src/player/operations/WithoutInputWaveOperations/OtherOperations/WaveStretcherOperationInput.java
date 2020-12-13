package player.operations.WithoutInputWaveOperations.OtherOperations;

import player.plugin.ifaces.PluginIFacesForUsers.WithoutInputWavePackage.WithoutInputWavePluginIFace;
import player.plugin.ifaces.PluginParametersAnnotation;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.math.ArithmeticOperation;
import Rocnikovy_Projekt.Program;

public class WaveStretcherOperationInput implements WithoutInputWavePluginIFace {
    @PluginParametersAnnotation(name = "New amplitude:", lowerBound = "-1", upperBound = "1", defaultValue = "0",
            parameterTooltip = "The new maximum value to which will be the wave stretched")
    private double newAbsoluteMax;


    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        stretchWave(audio, startIndex, endIndex, newAbsoluteMax);
    }

    public static void stretchWave(DoubleWave audio, int startIndex, int endIndex, double newAbsoluteMax) {
        double[] wave = audio.getSong();
        stretchWave(wave, startIndex, endIndex, newAbsoluteMax);
    }


    public static void stretchWave(double[] wave, int startIndex, int endIndex, double newAbsoluteMax) {
        double[] minMax = new double[2];
        Program.convertNSamplesToMinAndMax(wave, startIndex, endIndex, minMax);
        double extreme = findAbsoluteExtreme(minMax);

        newAbsoluteMax = Math.abs(newAbsoluteMax);
        newAbsoluteMax = Math.min(newAbsoluteMax, 1);
        // If == then it is already stretched as much as it should be, don't do anything
        if(extreme != newAbsoluteMax && extreme != 0) {
            double ratio = newAbsoluteMax / extreme;
            Program.performOperationOnSamples(wave, startIndex, endIndex, ratio, ArithmeticOperation.MULTIPLY);
        }
    }


    @Override
    public boolean shouldWaitForParametersFromUser() {
        return true;
    }

    @Override
    public boolean isUsingDefaultJPanel() {
        return true;
    }

    @Override
    public String getPluginName() {
        return "Wave Stretcher";
    }

    @Override
    public String getPluginTooltip() {
        return "Stretches wave in such way that the absolute value of sample si set to the given value";
    }

    /**
     * Finds the absolute extreme, calls on minMax[0] and [1] Math.abs and on the result calls Math.max
     * @param minMax contains min of the [start,end] of wave wave at minMax[0] and max at [1]
     * @return
     */
    public static double findAbsoluteExtreme(double[] minMax) {
        minMax[0] = Math.abs(minMax[0]);
        minMax[1] = Math.abs(minMax[1]);
        double extreme = Math.max(minMax[0], minMax[1]);
        return extreme;
    }
}
