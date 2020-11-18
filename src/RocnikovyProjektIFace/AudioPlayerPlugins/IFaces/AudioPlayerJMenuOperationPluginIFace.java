package RocnikovyProjektIFace.AudioPlayerPlugins.IFaces;

import DiagramSynthPackage.Synth.Unit;
import RocnikovyProjektIFace.AudioPlayerPlugins.Plugins.TestingEnumWithValue;
import Rocnikovy_Projekt.MyLogger;
import Rocnikovy_Projekt.Program;
import Rocnikovy_Projekt.ProgramTest;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;


public interface AudioPlayerJMenuOperationPluginIFace extends PluginDefaultIFace {
    /**
     *
     * @return Returns tooltip which will be shown when hovering over the button which will perform the operation.
     */
    String getPluginTooltip();


    public static <T> List<T> loadPlugins(Class<T> pluginIface) {
        // TODO: RML
        // TODO: make the path relative and also, I have to take in consideration that I may distribute it using .jar
        String packageContainingPlugins = TestingEnumWithValue.class.getPackage().getName();
        return loadPlugins(pluginIface, packageContainingPlugins);
    }

    // I have to differ between running in jar and not running in jar, because working with internals of .jar file can't
    // be done the same way as working with normal filesystem.
    // When I work with the jar the same way as with classic file system, I work with files around jar
    // not inside jar.
    public static <T> List<T> loadPlugins(Class<T> pluginIface, String pluginPackage) {
        // https://stackoverflow.com/questions/482560/can-you-tell-on-runtime-if-youre-running-java-from-within-a-jar
        Class<?> c = PluginDefaultIFace.class;
        // TODO: DEBUG
//        System.out.println("loadPlugins:\t" + c.getName() + "\t" + c.getCanonicalName() + "\t" + c.getResource("").toString());
//        System.out.println("loadPlugins:\t" + c.getName().substring(c.getName().lastIndexOf(".") + 1) + ".class" + "\t" +
//                c.getResource(c.getName().substring(c.getName().lastIndexOf(".") + 1) + ".class").toString());
//
        //if(c.getResource(c.getName() + ".class").toString().startsWith("jar:")) {
        //if(c.getResource("").toString().startsWith("jar:")) {
        // TODO: DEBUG
        if(isJar(c)) {
            String pathToJar = getPathToJar(c);
            MyLogger.log("Runs in jar with path: " + pathToJar, 0);
            return loadPluginsInJar(pluginIface, pluginPackage, pathToJar);
        }
        else {
            MyLogger.log("Doesn't run in jar", 0);
            return loadPluginsNotInJar(pluginIface, pluginPackage);
        }
    }


    // The path to file in jar starts with "jar:" and after the path to the jar file, there is the path inside the
    // jar, [note: the path to the jar file ends with !/ (or rather just !, the / marks the start of new path)]
    // The path inside the jar is the same as in classic filesystem, so path/to/file/file.class
    /**
     *
     * @param c is the class inside the jar
     * @return
     */
    public static boolean isJar(Class<?> c) {
        return getResourceForClass(c).toString().startsWith("jar:");
    }

    public static boolean isInJar() {
        return isJar(AudioPlayerJMenuOperationPluginIFace.class);
    }


    /**
     *
     * @param c is the class for which we want to find URL
     * @return
     */
    public static URL getResourceForClass(Class<?> c) {
        return c.getResource(c.getName().substring(c.getName().lastIndexOf(".") + 1) + ".class");
    }

    /**
     * Returns path to jar, without the jar:file: at start and without the !/ which marks end of path to jar.
     * @param classInsideJar
     * @return
     */
    public static String getPathToJar(Class<?> classInsideJar) {
        String fullPath = getResourceForClass(classInsideJar).toString();
        int jarNameExtensionStartIndex = fullPath.indexOf(".jar!/");
        int pathStartIndex = "jar:file:".length();
        String pathToJar = fullPath.substring(pathStartIndex, jarNameExtensionStartIndex + ".jar".length());
        return pathToJar;
    }

    public static String getJarName(Class<?> classInsideJar) {
        String jarName;
        String path = getResourceForClass(classInsideJar).toString();
        MyLogger.log("FULL JAR PATH: " + path, 0);
        int jarNameExtensionStartIndex = path.indexOf(".jar!/");
        path = path.substring(0, jarNameExtensionStartIndex + ".jar".length());
        int jarNameStartIndex = path.lastIndexOf('/') + 1;
        jarName = path.substring(jarNameStartIndex);

        return jarName;
    }


    // Modified old code from my advanced java course
    public static <T> List<T> loadPluginsNotInJar(Class<T> pluginIface, String pluginPackage) {
        List<T> list = new ArrayList<>();
        String path = pluginPackage.replace('.', '/');
        final File classFilesDir = AudioPlayerJMenuOperationPluginIFace.getClassFilesDirectory();
        path = classFilesDir + "/" + path;
        // Find all the candidates for plugins
        final File folder = new File(path);

        // TODO: PROGRAMO
//        ClassLoader loader = pluginIface.getClassLoader();
//        URL urlToPlugin = loader.getResource(pluginIface.getName());
//        final File folder = new File(urlToPlugin.getPath());
        // TODO: PROGRAMO
        List<String> pluginNames = new ArrayList<>();
        AudioPlayerJMenuOperationPluginIFace.search(".*\\.class", folder, pluginNames);

        for(String pluginName : pluginNames) {
            pluginName = pluginName.replace(".class", "");
            pluginName = pluginPackage + "." + pluginName;
            try {
                Class<?> clazz = Class.forName(pluginName);
                Class<?>[] interfaces = clazz.getInterfaces();
                for (int j = 0; j < interfaces.length; j++) {
                    if (interfaces[j] == pluginIface) {
                        addInstanceToList(clazz, list);
                        break;
                    }
                }
            }
            catch (Exception e) {
                MyLogger.logException(e);
            }
        }
        return list;
    }

    public static <T> void addInstanceToList(Class<?> clazz, List<T> list) throws NoSuchMethodException,
                                                            IllegalAccessException, InstantiationException {
        Constructor<?> constructor = clazz.getConstructor();
        if (constructor == null) {
            MyLogger.log("Doesn't have constructor without parameters - " + clazz.getName(), 0);
        } else {
            T newInstance = (T) clazz.newInstance();
            list.add(newInstance);
        }
    }



    // Modified code from:
    // https://stackoverflow.com/questions/25449/how-to-create-a-pluginable-java-program
    // https://alexiyorlov.github.io/tutorials/java-plugins.html
    /**
     *
     * @param pluginIface
     * @param packageContainingPlugins is the package which contains the plugins
     * @param <T>
     * @return
     */
    public static <T> List<T> loadPluginsInJar(Class<T> pluginIface, String packageContainingPlugins, String pathToJar) {
        String path = packageContainingPlugins.replace('.', '/');

        // TODO: RML
        //path = "C:/Users/Radek/eclipse-workspace/BakalarskaPrace/out/production/BakalarskaPrace/" + path;
        // TODO: RML

        // Find all the candidates for plugins
        final File pluginFolder = new File(path);
        List<T> loadedPlugins = new ArrayList<>();

// TODO: DEBUG
//        MyLogger.log("Plugin interface canonical name: " + pluginIface.getCanonicalName(), 0);
//        MyLogger.log("Plugins package: " + packageContainingPlugins, 0);
//        MyLogger.log("Plugins name: " + pluginFolder.getName(), 0);
//        MyLogger.log("Plugins path: " + pluginFolder.getAbsolutePath(), 0);
// TODO: DEBUG

        ArrayList<URL> urls = new ArrayList<>();
        ArrayList<String> classes = new ArrayList<>();
        File file = new File(pathToJar);
// TODO: DEBUG
//        MyLogger.log("JAR NAME: " + jarName, 0);
//        MyLogger.log("JAR ABSOLUTE PATH: " + file.getAbsolutePath(), 0);
// TODO: DEBUG
        try {
            JarFile jarFile = new JarFile(file);

            urls.add(new URL("jar:file:" + file.getName() + "!/"));
            MyLogger.log("JAR PATH: " + urls.get(urls.size() - 1), 0);
            jarFile.stream().forEach(jarEntry -> {
                // It is the path in jar so x/y/z
                String jarEntryName = jarEntry.getName();
// TODO: DEBUG
//                MyLogger.log("Plugin folder (relative): " + path, 0);
//                MyLogger.log("JAR ENTRY NAME: " + jarEntryName, 0);
// TODO: DEBUG
                if (jarEntryName.startsWith(path) && jarEntryName.endsWith(".class")) {
                    MyLogger.log("JAR ENTRY (.class): " + jarEntry.getName(), 0);
                    classes.add(jarEntry.getName());
                }
            });
        } catch (IOException e) {
            MyLogger.logException(e);
        }
        URLClassLoader pluginLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
        classes.forEach(s -> {
            try {
                Class classs = pluginLoader.loadClass(s.replaceAll("/", ".").replace(".class", ""));
                Class[] interfaces = classs.getInterfaces();
                for (Class anInterface : interfaces) {
                    if (anInterface == pluginIface) {
                        MyLogger.log("LOADED CLASS: " + s.replaceAll("/", ".").replace(".class", ""), 0);
                        addInstanceToList(classs, loadedPlugins);
                        break;
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                MyLogger.logException(e);
            }
        });
        return loadedPlugins;
    }

    public static boolean isImplementingIFace(Class<?> iface, Class<?> classToCheck) {
        Class<?>[] interfaces = classToCheck.getInterfaces();
        for(Class<?> i : interfaces) {
            if(iface == i) {
                return true;
            }
        }

        return false;
    }

    // Taken from https://mkyong.com/java/java-how-to-list-all-files-in-a-directory/
    public static void search(final String pattern, final File folder, List<String> result) {
        for (final File f : folder.listFiles()) {

            if (f.isDirectory()) {
                search(pattern, f, result);
            }

            if (f.isFile()) {
                if (f.getName().matches(pattern)) {
                    result.add(f.getName());
                }
            }

        }
    }


    public static File getClassFilesDirectory() {
        // https://stackoverflow.com/questions/11747833/getting-filesystem-path-of-class-being-executed
        return new File(Unit.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }
}