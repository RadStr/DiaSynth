package player.plugin.ifaces;

import plugin.PluginBaseIFace;
import plugin.util.PluginLoader;
import java.util.List;



public interface AudioPlayerJMenuPluginIFace extends PluginBaseIFace {
    /**
     *
     * @return Returns tooltip which will be shown when hovering over the button which will perform the operation.
     */
    String getPluginTooltip();


    public static <T> List<T> loadPlugins(Class<T> pluginIface) {
        String thisPackage = AudioPlayerJMenuPluginIFace.class.getPackage().getName();
        int lastDot = thisPackage.lastIndexOf('.');
        String packageContainingPlugins = thisPackage.substring(0, lastDot + 1) + "plugins";
        return PluginLoader.loadPlugins(pluginIface, packageContainingPlugins);
    }
}