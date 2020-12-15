package player.operations.nowave;

import player.plugin.ifaces.user.nowave.OperationOnWavePluginIFace;
import Rocnikovy_Projekt.DoubleWave;

public class SetSamplesToZeroOnWaveOperation implements OperationOnWavePluginIFace {
    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        SetSamplesOnWaveOperation.setSamples(audio, startIndex, endIndex, 0);
    }

    @Override
    public boolean shouldWaitForParametersFromUser() {
        return false;
    }

    @Override
    public boolean isUsingDefaultJPanel() {
        return true;
    }

    @Override
    public String getPluginName() {
        return "Set samples to 0";
    }

    @Override
    public String getPluginTooltip() {
        return "Sets all the samples to 0 (which is neutral value for mixing)";
    }
}