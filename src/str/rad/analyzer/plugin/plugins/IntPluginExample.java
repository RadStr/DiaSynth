package str.rad.analyzer.plugin.plugins;

import str.rad.analyzer.plugin.ifaces.AnalyzerIntPluginIFace;
import str.rad.util.Pair;

public class IntPluginExample implements AnalyzerIntPluginIFace {
    @Override
    public Pair<String, String> analyze(int[] samples, int numberOfChannels, int sampleRate) {
        String name = "Int plugin name";
        String analyzedValue = "Int plugin - analyzed value";

        if(samples != null && samples.length > 0) {
            analyzedValue += " " + samples[0];
        }
        return new Pair<>(name, analyzedValue);
    }

    @Override
    public String getName() {
        return "Analyze - Int plugin";
    }

    @Override
    public String getTooltip() {
        return "This is example of tooltip for int plugin. Prints String followed by first value in samples array.";
    }
}
