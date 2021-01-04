package str.rad.analyzer.plugin.ifaces;

import str.rad.util.Pair;

import java.util.List;

public interface AnalyzerIntPluginIFace extends AnalyzerBasePluginIFace {
    /**
     * @return Returns pair.
     * First value is the name which will be showed on left, next to the analyzed value.
     * Second value is the analyzed value converted to string.
     */
    Pair<String, String> analyze(int[] samples, int numberOfChannels, int sampleRate);

    public static List<AnalyzerIntPluginIFace> loadPlugins() {
        Class<AnalyzerIntPluginIFace> pluginIFace = AnalyzerIntPluginIFace.class;
        return AnalyzerBasePluginIFace.loadPlugins(pluginIFace);
    }
}
