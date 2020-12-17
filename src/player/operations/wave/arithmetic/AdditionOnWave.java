package player.operations.wave.arithmetic;

import plugin.PluginParameterAnnotation;
import player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import util.audio.wave.DoubleWave;
import util.math.ArithmeticOperation;

public class AdditionOnWave implements OperationOnWavePluginIFace {
    @PluginParameterAnnotation(name = "Addition constant:", lowerBound = "-1", upperBound = "1", defaultValue = "0",
        parameterTooltip = "is the constant to be added to the samples")
    private double value;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] wave = audio.getSong();
        ArithmeticOperation.performOperationOnSamples(wave, startIndex, endIndex, value, ArithmeticOperation.PLUS);
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
        return "Add value to samples";
    }

    @Override
    public String getPluginTooltip() {
        return "Adds given parameter to all samples";
    }
}
