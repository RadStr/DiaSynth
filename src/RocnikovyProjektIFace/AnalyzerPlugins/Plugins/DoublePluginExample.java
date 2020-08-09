package RocnikovyProjektIFace.AnalyzerPlugins.Plugins;

import RocnikovyProjektIFace.AnalyzerPlugins.PluginIFaces.AnalyzerDoublePluginIFace;
import RocnikovyProjektIFace.Pair;
import Rocnikovy_Projekt.DoubleWave;

public class DoublePluginExample implements AnalyzerDoublePluginIFace {
    @Override
    public Pair<String, String> analyze(DoubleWave wave) {
        String name = "Double_plugin_name";
        String analyzedValue = "Double plugin - analyzed value";
        return new Pair<>(name, analyzedValue);
    }

    @Override
    public String getName() {
        return "Analyze - Double plugin";
    }
}
