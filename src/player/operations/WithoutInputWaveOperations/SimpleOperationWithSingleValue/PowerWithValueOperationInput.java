package player.operations.WithoutInputWaveOperations.SimpleOperationWithSingleValue;

import player.plugin.ifaces.PluginIFacesForUsers.WithoutInputWavePackage.WithoutInputWavePluginIFace;
import player.plugin.ifaces.PluginParametersAnnotation;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.math.ArithmeticOperation;
import Rocnikovy_Projekt.Program;

public class PowerWithValueOperationInput implements WithoutInputWavePluginIFace {
    @PluginParametersAnnotation(name = "Power:", defaultValue = "0",
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
    public boolean isUsingDefaultJPanel() {
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
