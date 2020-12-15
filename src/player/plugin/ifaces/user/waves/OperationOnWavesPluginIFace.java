package player.plugin.ifaces.user.waves;

import player.plugin.ifaces.AudioPlayerJMenuOperationPluginIFace;
import Rocnikovy_Projekt.DoubleWave;

import java.util.List;

public interface OperationOnWavesPluginIFace extends AudioPlayerJMenuOperationPluginIFace {
    /**
     * Input is the wave to use for modification of output and output is some wave which should be modified
     */
    void performOperation(DoubleWave input, DoubleWave output,
                          int inputStartIndex, int inputEndIndex,
                          int outputStartIndex, int outputEndIndex);

    public static List<OperationOnWavesPluginIFace> loadPlugins() {
        Class<OperationOnWavesPluginIFace> pluginIface = OperationOnWavesPluginIFace.class;
        return AudioPlayerJMenuOperationPluginIFace.loadPlugins(pluginIface);
    }
}
