package RocnikovyProjektIFace.AudioPlayerPlugins.plugins;

import RocnikovyProjektIFace.AudioPlayerPlugins.ifaces.PluginIFacesForUsers.WithoutInputWavePackage.WithoutInputWavePluginIFace;
import Rocnikovy_Projekt.DoubleWave;

public class TestPluginWithoutParametersWithoutInputWave implements WithoutInputWavePluginIFace {

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] song = audio.getSong();
        for(int i = startIndex; i < endIndex; i++) {
            song[i] = -0.5;
        }
    }

    @Override
    public boolean shouldWaitForParametersFromUser() {
        return false;
    }

    @Override
    public boolean isUsingDefaultJPanel() {
        return false;
    }

    @Override
    public String getPluginName() {
        return "TestPluginWithoutParametersWithoutInputWave";
    }

    @Override
    public String getPluginTooltip() {
        return "Sets all values in song to -0.5";
    }
}
