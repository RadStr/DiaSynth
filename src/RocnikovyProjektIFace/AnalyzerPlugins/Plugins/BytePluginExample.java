package RocnikovyProjektIFace.AnalyzerPlugins.Plugins;

import RocnikovyProjektIFace.AnalyzerPlugins.PluginIFaces.AnalyzerBytePluginIFace;
import RocnikovyProjektIFace.Pair;

public class BytePluginExample implements AnalyzerBytePluginIFace {
    public Pair<String, String> analyze(byte[] samples, int numberOfChannels, int sampleSize,
                                        int sampleRate, boolean isBigEndian, boolean isSigned) {
        String name = "Byte_plugin_name";
        String analyzedValue = "Byte plugin - analyzed value";
        return new Pair<>(name, analyzedValue);
    }

    @Override
    public String getName() {
        return "Analyze - Byte plugin";
    }
}
