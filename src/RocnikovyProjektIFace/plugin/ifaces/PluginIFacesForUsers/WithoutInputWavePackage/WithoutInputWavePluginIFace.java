package RocnikovyProjektIFace.plugin.ifaces.PluginIFacesForUsers.WithoutInputWavePackage;

import RocnikovyProjektIFace.plugin.ifaces.AudioPlayerJMenuOperationPluginIFace;
import Rocnikovy_Projekt.DoubleWave;

import java.util.List;

public interface WithoutInputWavePluginIFace extends AudioPlayerJMenuOperationPluginIFace {
    void performOperation(DoubleWave audio, int startIndex, int endIndex);

    public static List<WithoutInputWavePluginIFace> loadPlugins() {
        Class<WithoutInputWavePluginIFace> pluginIface = WithoutInputWavePluginIFace.class;
        return AudioPlayerJMenuOperationPluginIFace.loadPlugins(pluginIface);
    }
}
