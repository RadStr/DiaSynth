package player.operations.wave.arithmetic;

import player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import player.plugin.ifaces.PluginParameterAnnotation;
import util.audio.wave.DoubleWave;
import util.math.ArithmeticOperation;
import Rocnikovy_Projekt.Program;

public class MultiplicationOnWave implements OperationOnWavePluginIFace {
    @PluginParameterAnnotation(name = "Multiplication constant:", lowerBound = "-1", upperBound = "1", defaultValue = "0",
        parameterTooltip = "is the constant to multiply the samples with")
    private double value;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] wave = audio.getSong();
        Program.performOperationOnSamples(wave, startIndex, endIndex, value, ArithmeticOperation.MULTIPLY);
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
