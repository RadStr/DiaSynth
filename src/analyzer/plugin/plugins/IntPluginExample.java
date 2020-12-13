package analyzer.plugin.plugins;

import analyzer.plugin.ifaces.AnalyzerIntPluginIFace;
import util.Pair;

public class IntPluginExample implements AnalyzerIntPluginIFace {
    @Override
    public Pair<String, String> analyze(int[] samples, int numberOfChannels, int sampleRate) {
        String name = "Int plugin name";
        String analyzedValue = "Int plugin - analyzed value";
        return new Pair<>(name, analyzedValue);
    }

    @Override
    public String getName() {
        return "Analyze - Int plugin";
    }
    @Override
    public String getTooltip() {
        return "This is example of tooltip for int plugin.";
    }
}
