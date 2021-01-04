package str.rad.player.operations.wave.arithmetic;

import str.rad.plugin.PluginParameterAnnotation;
import str.rad.player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import str.rad.util.audio.wave.DoubleWave;
import str.rad.util.math.ArithmeticOperation;

public class LogarithmOnWave implements OperationOnWavePluginIFace {
    // "" + Double.MIN_NORMAL is workaround (it has to be constant, so I can't use toString)
    @PluginParameterAnnotation(name = "Base of logarithm:", lowerBound = "" + Double.MIN_NORMAL, defaultValue = "0",
                               parameterTooltip = "is the base of the logarithm")
    private double value;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] wave = audio.getSong();
        ArithmeticOperation.performOperationOnSamples(wave, startIndex, endIndex, value, ArithmeticOperation.LOG);
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
        return "Logarithm samples";
    }

    @Override
    public String getPluginTooltip() {
        return "Logarithm the samples with base of user given value";
    }
}
