package player.operations.wave;

import plugin.PluginParameterAnnotation;
import player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import util.Utilities;
import util.audio.wave.DoubleWave;

public class SetSamplesOnWaveOperation implements OperationOnWavePluginIFace {
    @PluginParameterAnnotation(name = "Value:", lowerBound = "-1", upperBound = "1",
        defaultValue = "0", parameterTooltip = "Represents the value to set the samples to.")
    private double value;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        setSamples(audio, startIndex, endIndex, value);
    }

    public static void setSamples(DoubleWave audio, int startIndex, int endIndex, double value) {
        double[] wave = audio.getSong();
        Utilities.setOneDimArr(wave, startIndex, endIndex, value);
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
        return "Set to value";
    }

    @Override
    public String getPluginTooltip() {
        return "Sets samples to value given by user";
    }
}