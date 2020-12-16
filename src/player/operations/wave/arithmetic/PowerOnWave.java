package player.operations.wave.arithmetic;

import player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import player.plugin.ifaces.PluginParameterAnnotation;
import Rocnikovy_Projekt.DoubleWave;
import util.math.ArithmeticOperation;
import Rocnikovy_Projekt.Program;

public class PowerOnWave implements OperationOnWavePluginIFace {
    @PluginParameterAnnotation(name = "Power:", defaultValue = "0",
            parameterTooltip = "is the power to which will be the samples raised.")
    private double value;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] wave = audio.getSong();
        Program.performOperationOnSamples(wave, startIndex, endIndex, value, ArithmeticOperation.POWER);
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
        return "Raise samples to power";
    }

    @Override
    public String getPluginTooltip() {
        return "Use samples as base for power (samples ^ userValue)";
    }
}
