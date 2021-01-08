package str.rad.player.plugin.ifaces.user.wave;

import str.rad.player.plugin.ifaces.AudioPlayerJMenuPluginIFace;
import str.rad.util.audio.wave.DoubleWave;

import java.util.List;

public interface OperationOnWavePluginIFace extends AudioPlayerJMenuPluginIFace {
    void performOperation(DoubleWave audio, int startIndex, int endIndex);

    public static List<OperationOnWavePluginIFace> loadPlugins() {
        Class<OperationOnWavePluginIFace> pluginIface = OperationOnWavePluginIFace.class;
        return AudioPlayerJMenuPluginIFace.loadPlugins(pluginIface);
    }
}
