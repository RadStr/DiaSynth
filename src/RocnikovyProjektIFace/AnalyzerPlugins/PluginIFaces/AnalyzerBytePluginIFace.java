package RocnikovyProjektIFace.AnalyzerPlugins.PluginIFaces;

import RocnikovyProjektIFace.Pair;

import java.util.List;

public interface AnalyzerBytePluginIFace extends BaseAnalyzerPluginIFace {
    /**
     * @return Returns pair. FIRST VALUE OF PAIR CAN'T CONTAIN WHITESPACE CHARACTERS, IF IT DOES THE RESULT WON'T BE SHOWN (THIS IS DUE TO USAGE OF XML).
     * First value is the name which will be showed on left next to the analyzed value.
     * Second value is the analyzed value converted to string.
     */
    Pair<String, String> analyze(byte[] samples, int numberOfChannels, int sampleSize,
                                 int sampleRate, boolean isBigEndian, boolean isSigned);


    public static List<AnalyzerBytePluginIFace> loadPlugins() {
        // TODO: RML
        // TODO: make the path relative and also, I have to take in consideration that I may distribute it using .jar
        Class<AnalyzerBytePluginIFace> pluginIFace = AnalyzerBytePluginIFace.class;
        return BaseAnalyzerPluginIFace.loadPlugins(pluginIFace);
    }
}
