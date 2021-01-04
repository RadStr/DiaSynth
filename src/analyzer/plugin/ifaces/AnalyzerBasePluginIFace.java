package analyzer.plugin.ifaces;

import plugin.util.PluginLoader;

import java.util.List;

public interface AnalyzerBasePluginIFace {
    /**
     * Returns the name of the checkbox, which will be shown
     * to user in analyzer panel.(BPM, RMS, ..).
     *
     * @return
     */
    String getName();

    /**
     * Returns the tooltip for the checkbox in analyzer panel.
     *
     * @return
     */
    String getTooltip();

    public static <T> List<T> loadPlugins(Class<T> pluginIface) {
        String thisPackage = AnalyzerBasePluginIFace.class.getPackage().getName();
        int lastDot = thisPackage.lastIndexOf('.');
        String packageContainingPlugins = thisPackage.substring(0, lastDot + 1) + "plugins";
        return PluginLoader.loadPlugins(pluginIface, packageContainingPlugins);
    }
}
