package player.operations.wave;

import plugin.PluginParameterAnnotation;
import player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import util.Aggregation;
import util.audio.wave.DoubleWave;
import util.math.ArithmeticOperation;

public class WaveStretcherOnWave implements OperationOnWavePluginIFace {
    @PluginParameterAnnotation(name = "New amplitude:", lowerBound = "-1", upperBound = "1", defaultValue = "0",
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
        Aggregation.convertNSamplesToMinAndMax(wave, startIndex, endIndex, minMax);
        double extreme = findAbsoluteExtreme(minMax);

        newAbsoluteMax = Math.abs(newAbsoluteMax);
        newAbsoluteMax = Math.min(newAbsoluteMax, 1);
        // If == then it is already stretched as much as it should be, don't do anything
        if (extreme != newAbsoluteMax && extreme != 0) {
            double ratio = newAbsoluteMax / extreme;
            ArithmeticOperation.performOperationOnSamples(wave, startIndex, endIndex,
                                                          ratio, ArithmeticOperation.MULTIPLY);
        }
    }


    @Override
    public boolean shouldWaitForParametersFromUser() {
        return true;
    }

    @Override
    public boolean isUsingPanelCreatedFromAnnotations() {
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
     * Finds the absolute extreme, the bigger of the two values in absolute value.
     *
     * @param minMax contains min of the [start,end] of wave at [0] and max at [1]
     * @return
     */
    public static double findAbsoluteExtreme(double[] minMax) {
        minMax[0] = Math.abs(minMax[0]);
        minMax[1] = Math.abs(minMax[1]);
        double extreme = Math.max(minMax[0], minMax[1]);
        return extreme;
    }
}
