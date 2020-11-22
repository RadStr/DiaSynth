package RocnikovyProjektIFace.AnalyzerPlugins.Plugins;

import RocnikovyProjektIFace.AnalyzerPlugins.PluginIFaces.AnalyzerBytePluginIFace;
import RocnikovyProjektIFace.Pair;

public class BytePluginExample implements AnalyzerBytePluginIFace {
    public Pair<String, String> analyze(byte[] samples, int numberOfChannels, int sampleSize,
                                        int sampleRate, boolean isBigEndian, boolean isSigned) {
        String name = "Byte plugin name";
        String analyzedValue = "Byte plugin - analyzed value";
        return new Pair<>(name, analyzedValue);
    }

    @Override
    public String getName() {
        return "Analyze - Byte plugin";
    }

    @Override
    public String getTooltip() {
        return "This is example of tooltip for byte plugin.";
    }
}
