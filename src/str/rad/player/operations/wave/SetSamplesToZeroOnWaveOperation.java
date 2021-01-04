package str.rad.player.operations.wave;

import str.rad.player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import str.rad.util.audio.wave.DoubleWave;

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
    public boolean isUsingPanelCreatedFromAnnotations() {
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