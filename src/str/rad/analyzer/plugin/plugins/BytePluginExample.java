package str.rad.analyzer.plugin.plugins;

import str.rad.analyzer.plugin.ifaces.AnalyzerBytePluginIFace;
import str.rad.util.Pair;

public class BytePluginExample implements AnalyzerBytePluginIFace {
    public Pair<String, String> analyze(byte[] samples, int numberOfChannels, int sampleSize,
                                        int sampleRate, boolean isBigEndian, boolean isSigned) {
        String name = "Byte plugin name";
        String analyzedValue = "Byte plugin - analyzed value";

        if(samples != null && samples.length > 0) {
            analyzedValue += " " + samples[0];
        }
        return new Pair<>(name, analyzedValue);
    }

    @Override
    public String getName() {
        return "Analyze - Byte plugin";
    }

    @Override
    public String getTooltip() {
        return "This is example of tooltip for byte plugin. Prints String followed by first value in samples array.";
    }
}
