package analyzer.plugin.ifaces;

import RocnikovyProjektIFace.Pair;

import java.util.List;

public interface AnalyzerIntPluginIFace extends BaseAnalyzerPluginIFace {
    /**
     * @return Returns pair.
     * First value is the name which will be showed on left, next to the analyzed value.
     * Second value is the analyzed value converted to string.
     */
    Pair<String, String> analyze(int[] samples, int numberOfChannels, int sampleRate);

    public static List<AnalyzerIntPluginIFace> loadPlugins() {
        Class<AnalyzerIntPluginIFace> pluginIFace = AnalyzerIntPluginIFace.class;
        return BaseAnalyzerPluginIFace.loadPlugins(pluginIFace);
    }
}
