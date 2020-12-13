package analyzer.plugin.PluginIFaces;

import RocnikovyProjektIFace.Pair;
import Rocnikovy_Projekt.DoubleWave;

import java.util.List;

public interface AnalyzerDoublePluginIFace extends BaseAnalyzerPluginIFace {
    /**
     * @return Returns pair.
     * First value is the name which will be showed on left, next to the analyzed value.
     * Second value is the analyzed value converted to string.
     */
    Pair<String, String> analyze(DoubleWave wave);

    public static List<AnalyzerDoublePluginIFace> loadPlugins() {
        Class<AnalyzerDoublePluginIFace> pluginIFace = AnalyzerDoublePluginIFace.class;
        return BaseAnalyzerPluginIFace.loadPlugins(pluginIFace);
    }
}
