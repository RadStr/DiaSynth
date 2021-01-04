package str.rad.analyzer.plugin.plugins;

import str.rad.analyzer.plugin.ifaces.AnalyzerDoublePluginIFace;
import str.rad.util.Pair;
import str.rad.util.audio.wave.DoubleWave;

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
