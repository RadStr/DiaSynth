package player.plugin.ifaces.PluginIFacesForUsers.WithInputWavePackage;

import player.plugin.ifaces.AudioPlayerJMenuOperationPluginIFace;
import Rocnikovy_Projekt.DoubleWave;

import java.util.List;

public interface WithInputWavePluginIFace extends AudioPlayerJMenuOperationPluginIFace {
    /**
     * Input is the wave to use for modification of output and output is some wave which should be modified
     */
    void performOperation(DoubleWave input, DoubleWave output,
                          int inputStartIndex, int inputEndIndex,
                          int outputStartIndex, int outputEndIndex);

    public static List<WithInputWavePluginIFace> loadPlugins() {
        Class<WithInputWavePluginIFace> pluginIface = WithInputWavePluginIFace.class;
        return AudioPlayerJMenuOperationPluginIFace.loadPlugins(pluginIface);
    }
}
