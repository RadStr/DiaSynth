package analyzer.plugin.Plugins;

import analyzer.plugin.ifaces.AnalyzerDoublePluginIFace;
import RocnikovyProjektIFace.Pair;
import Rocnikovy_Projekt.DoubleWave;

public class DoublePluginExample implements AnalyzerDoublePluginIFace {
    @Override
    public Pair<String, String> analyze(DoubleWave wave) {
        String name = "Double plugin name";
        String analyzedValue = "Double plugin - analyzed value";
        return new Pair<>(name, analyzedValue);
    }

    @Override
    public String getName() {
        return "Analyze - Double plugin";
    }
    @Override
    public String getTooltip() {
        return "This is example of tooltip for double plugin.";
    }
}
