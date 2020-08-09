package RocnikovyProjektIFace.AnalyzerPlugins.Plugins;

import RocnikovyProjektIFace.AnalyzerPlugins.PluginIFaces.AnalyzerIntPluginIFace;
import RocnikovyProjektIFace.Pair;

public class IntPluginExample implements AnalyzerIntPluginIFace {
    @Override
    public Pair<String, String> analyze(int[] samples, int numberOfChannels, int sampleRate) {
        String name = "Int_plugin_name";
        String analyzedValue = "Int plugin - analyzed value";
        return new Pair<>(name, analyzedValue);
    }

    @Override
    public String getName() {
        return "Analyze - Int plugin";
    }
}
