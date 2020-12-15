package player.plugin.ifaces.user.wave;

import player.plugin.ifaces.AudioPlayerJMenuOperationPluginIFace;
import Rocnikovy_Projekt.DoubleWave;

import java.util.List;

public interface OperationOnWavePluginIFace extends AudioPlayerJMenuOperationPluginIFace {
    void performOperation(DoubleWave audio, int startIndex, int endIndex);

    public static List<OperationOnWavePluginIFace> loadPlugins() {
        Class<OperationOnWavePluginIFace> pluginIface = OperationOnWavePluginIFace.class;
        return AudioPlayerJMenuOperationPluginIFace.loadPlugins(pluginIface);
    }
}
