package RocnikovyProjektIFace.AnalyzerPlugins.PluginIFaces;

import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.AudioPlayerJMenuOperationPluginIFace;
import Rocnikovy_Projekt.ProgramTest;

import java.util.List;

public interface BaseAnalyzerPluginIFace {
    /**
     * Returns the name of the checkbox which will be shown to user when choosing what should be analyzed (BPM, RMS, ..).
     * @return
     */
    String getName();
    String getTooltip();

    public static <T> List<T> loadPlugins(Class<T> pluginIface) {
        // TODO: RML
        // TODO: make the path relative and also, I have to take in consideration that I may distribute it using .jar
        String thisPackage = BaseAnalyzerPluginIFace.class.getPackage().getName();
        int lastDot = thisPackage.lastIndexOf('.');
        String packageContainingPlugins = thisPackage.substring(0, lastDot + 1) + "Plugins";
        return AudioPlayerJMenuOperationPluginIFace.loadPlugins(pluginIface, packageContainingPlugins);
    }
}
