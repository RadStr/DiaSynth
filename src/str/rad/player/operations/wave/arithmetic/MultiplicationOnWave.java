package str.rad.player.operations.wave.arithmetic;

import str.rad.player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import str.rad.plugin.PluginParameterAnnotation;
import str.rad.util.audio.wave.DoubleWave;
import str.rad.util.math.ArithmeticOperation;

public class MultiplicationOnWave implements OperationOnWavePluginIFace {
    @PluginParameterAnnotation(name = "Multiplication constant:", defaultValue = "0",
                               parameterTooltip = "is the constant to multiply the samples with")
    private double value;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] wave = audio.getSong();
        ArithmeticOperation.performOperationOnSamples(wave, startIndex, endIndex, value, ArithmeticOperation.MULTIPLY);
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
        return "Multiply samples with value";
    }

    @Override
    public String getPluginTooltip() {
        return "Multiplies all samples with given parameter";
    }
}
