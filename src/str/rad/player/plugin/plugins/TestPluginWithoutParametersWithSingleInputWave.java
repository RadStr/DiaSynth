package str.rad.player.plugin.plugins;

import str.rad.player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import str.rad.util.audio.wave.DoubleWave;

public class TestPluginWithoutParametersWithSingleInputWave implements OperationOnWavePluginIFace {

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] song = audio.getSong();
        for (int i = startIndex; i < endIndex; i++) {
            song[i] = -0.5;
        }
    }

    @Override
    public boolean shouldWaitForParametersFromUser() {
        return false;
    }

    @Override
    public boolean isUsingPanelCreatedFromAnnotations() {
        return false;
    }

    @Override
    public String getPluginName() {
        return "TestPluginWithoutParametersWithSingleInputWave";
    }

    @Override
    public String getPluginTooltip() {
        return "Sets all values in song to -0.5";
    }
}
