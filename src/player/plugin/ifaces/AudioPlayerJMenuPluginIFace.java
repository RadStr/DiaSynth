package player.plugin.ifaces;

import plugin.PluginBaseIFace;
import plugin.util.PluginLoader;
import synthesizer.synth.Unit;
import util.logging.MyLogger;
import test.ProgramTest;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;


public interface AudioPlayerJMenuPluginIFace extends PluginBaseIFace {
    /**
     *
     * @return Returns tooltip which will be shown when hovering over the button which will perform the operation.
     */
    String getPluginTooltip();


    public static <T> List<T> loadPlugins(Class<T> pluginIface) {
        // TODO: RML
        // TODO: make the path relative and also, I have to take in consideration that I may distribute it using .jar
        String thisPackage = AudioPlayerJMenuPluginIFace.class.getPackage().getName();
        int lastDot = thisPackage.lastIndexOf('.');
        String packageContainingPlugins = thisPackage.substring(0, lastDot + 1) + "plugins";
        return PluginLoader.loadPlugins(pluginIface, packageContainingPlugins);
    }
}