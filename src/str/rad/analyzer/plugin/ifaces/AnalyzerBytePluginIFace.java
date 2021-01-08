package str.rad.analyzer.plugin.ifaces;

import str.rad.util.Pair;

import java.util.List;

public interface AnalyzerBytePluginIFace extends AnalyzerBasePluginIFace {
    /**
     * @return Returns pair.
     * First value is the name which will be showed on left, next to the analyzed value.
     * Second value is the analyzed value converted to string.
     */
    Pair<String, String> analyze(byte[] samples, int numberOfChannels, int sampleSize,
                                 int sampleRate, boolean isBigEndian, boolean isSigned);


    public static List<AnalyzerBytePluginIFace> loadPlugins() {
        Class<AnalyzerBytePluginIFace> pluginIFace = AnalyzerBytePluginIFace.class;
        return AnalyzerBasePluginIFace.loadPlugins(pluginIFace);
    }
}
