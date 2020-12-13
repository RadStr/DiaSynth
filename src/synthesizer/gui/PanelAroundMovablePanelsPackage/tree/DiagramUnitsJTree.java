package synthesizer.gui.PanelAroundMovablePanelsPackage.tree;

// Modified code from:
// https://coderanch.com/t/339685/java/JTree-Setting-node-JPanel
// And also this https://docs.oracle.com/javase/tutorial/uiswing/components/table.html#editrender for reference


import synthesizer.gui.MovablePanelsPackage.DiagramPanel;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.*;
import synthesizer.synth.OutputUnit;
import synthesizer.synth.Unit;
import RocnikovyProjektIFace.plugin.ifaces.AudioPlayerJMenuOperationPluginIFace;
import Rocnikovy_Projekt.MyLogger;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public class DiagramUnitsJTree extends JTree {
    public DiagramUnitsJTree(DiagramPanel diagramPanel) {
        createTree(this, diagramPanel);

        this.setCellRenderer(new PanelRenderer());
        this.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getNewLeadSelectionPath();
                if(path != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

                    JTreeCellClickedCallbackIFace callback = (JTreeCellClickedCallbackIFace) node.getUserObject();
                    callback.clickCallback();
                    clearSelection();
                }
            }
        });
    }


    public static JTreeCellClickedCallbackIFace getRoot(DiagramPanel diagramPanel) {
        JTreeCellClickedCallbackIFace root;
        root = loadJTreeElements(Unit.class.getPackage().getName(), diagramPanel);
        return root;
    }


    private static void createTree(JTree tree, DiagramPanel diagramPanel) {
        JTreeCellClickedCallbackIFace root = getRoot(diagramPanel);
        List<JTreeCellClickedCallbackIFace> roots = new ArrayList<>();
        roots.add(root);

        DefaultMutableTreeNode invisibleRoot = new DefaultMutableTreeNode(new JTreeCellText("") {
            @Override
            public List<JTreeCellClickedCallbackIFace> getChildren() {
                return null;
            }
        });

        addChildren(invisibleRoot, roots, true);
        DefaultTreeModel model = new DefaultTreeModel(invisibleRoot);
        tree.setModel(model);
    }

    private static void addChildren(DefaultMutableTreeNode root,
                                    List<JTreeCellClickedCallbackIFace> children, boolean isFirst) {
        if(children != null) {
            for (int i = 0; i < children.size(); i++) {
                if(isFirst) {
                    JTreeCellClickedCallbackIFace nodeView = children.get(i);
                    addChildren(root, nodeView.getChildren(), false);
                }
                else {
                    JTreeCellClickedCallbackIFace nodeView = children.get(i);
                    // It is unit or it is package with at least one class implementing IFace in package
                    if(nodeView.getChildren() == null || nodeView.getChildren().size() > 0) {
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeView);
                        root.add(node);
                        addChildren(node, nodeView.getChildren(), false);
                    }
                }
            }
        }
    }



    // Copied from the plugins for audio player with slight changes
    public static JTreeCellTextForUnits loadJTreeElements(String pluginPackage,
                                                          DiagramPanel diagramPanel) {
        if (AudioPlayerJMenuOperationPluginIFace.isJar(Unit.class)) {
            String folderName = getStringAfterLastChar(pluginPackage, '.');
            JTreeCellTextForUnits treeCell = new JTreeCellTextForUnits(folderName);
            String pathToJar = AudioPlayerJMenuOperationPluginIFace.getPathToJar(Unit.class);
            return setMainTreeCellJarVersion(diagramPanel, pluginPackage, pathToJar, treeCell);
        }
        else {
            final File classFilesDir = AudioPlayerJMenuOperationPluginIFace.getClassFilesDirectory();
            String path = classFilesDir + "/" + pluginPackage.replace('.', '/');
            final File folder = new File(path);
            // \\ is there because * is special character in regex
            return setMainTreeCellNonJarVersion(".*\\.class", folder, pluginPackage, diagramPanel);
        }
    }

    public static String getStringAfterLastChar(String text, char c) {
        int index = text.lastIndexOf(c);
        String folderName = text.substring(index + 1);
        return folderName;
    }


    private static void addChildrenToTreeCellNonJarVersion(String pluginName, String pluginPackage,
                                                           DiagramPanel diagramPanel,
                                                           JTreeCellTextForUnits treeCell) {
        pluginName = pluginName.replace(".class", "");
        pluginName = pluginPackage + "." + pluginName;
        try {
            Class<?> clazz = Class.forName(pluginName);
            if(Unit.class.isAssignableFrom(clazz) && clazz != OutputUnit.class) {
                Constructor<?> constructor = clazz.getConstructor(DiagramPanel.class);
                if(Modifier.isAbstract(clazz.getModifiers())) {
                    // EMPTY
                }
                else if (constructor == null) {
                    MyLogger.logWithoutIndentation("Doesn't have corresponding constructor");
                }
                else {
                    Unit u;
                    u = (Unit) constructor.newInstance(new Object[] { diagramPanel });
                    treeCell.addChildren(u);
                }
            }
        }
        catch (Exception e) {
            MyLogger.logException(e);
        }
    }


    private static void addChildrenToTreeCellJarVersion(String className, URLClassLoader pluginLoader,
                                                        DiagramPanel diagramPanel,
                                                        JTreeCellTextForUnits treeCell) {
        try {
            Class<?> clazz = pluginLoader.loadClass(className.replaceAll("/", ".").replace(".class", ""));
            if (Unit.class.isAssignableFrom(clazz) && clazz != OutputUnit.class) {
                Constructor<?> constructor = clazz.getConstructor(DiagramPanel.class);
                if (Modifier.isAbstract(clazz.getModifiers())) {
                    // EMPTY
                } else if (constructor == null) {
                    MyLogger.logWithoutIndentation("Doesn't have corresponding constructor");
                } else {
                    MyLogger.log("Adding Unit: " + className, 0);
                    Unit u;
                    u = (Unit) constructor.newInstance(new Object[] { diagramPanel });
                    treeCell.addChildren(u);
                    MyLogger.log("Added Unit: " + className, 0);
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            MyLogger.logException(e);
        }
    }


    // Modified code from https://mkyong.com/java/java-how-to-list-all-files-in-a-directory/
    public static JTreeCellTextForUnits setMainTreeCellNonJarVersion(final String pattern, final File folder, String pluginPackage,
                                                                     DiagramPanel diagramPanel) {
        JTreeCellTextForUnits treeCell = new JTreeCellTextForUnits(folder.getName());
        setTreeCellRecursive(pattern, folder, pluginPackage, diagramPanel, treeCell);
        return treeCell;
    }
    public static void setTreeCellRecursive(final String pattern, final File folder, String pluginPackage,
                                            DiagramPanel diagramPanel,
                                            JTreeCellTextForUnits treeCell) {
        for (final File f : folder.listFiles()) {
            if (f.isDirectory()) {
                JTreeCellTextForUnits newTreeCell = new JTreeCellTextForUnits(f.getName());
                treeCell.addChildren(newTreeCell);
                setTreeCellRecursive(pattern, f, pluginPackage + "." + f.getName(),
                        diagramPanel, newTreeCell);
            }
            else if (f.isFile()) {
                if (f.getName().matches(pattern)) {
                    addChildrenToTreeCellNonJarVersion(f.getName(), pluginPackage, diagramPanel, treeCell);
                }
            }
        }
    }


    public static JTreeCellTextForUnits setMainTreeCellJarVersion(DiagramPanel diagramPanel,
                                                                  String packageContainingPlugins, String pathToJar,
                                                                  JTreeCellTextForUnits treeCell) {
                // + "/" Because I want it to behave as jar directory (which ends with /)
        String path = packageContainingPlugins.replace('.', '/') + "/";
        MyLogger.log("packageContainingPlugins: " + path, 0);
        MyLogger.log("PATH TO JAR: " + pathToJar, 0);

        // TODO: RML
        //path = "C:/Users/Radek/eclipse-workspace/BakalarskaPrace/out/production/BakalarskaPrace/" + path;
        // TODO: RML

// TODO: DEBUG
//        MyLogger.log("Plugin interface canonical name: " + pluginIface.getCanonicalName(), 0);
//        MyLogger.log("Plugins package: " + packageContainingPlugins, 0);
//        MyLogger.log("Plugins name: " + pluginFolder.getName(), 0);
//        MyLogger.log("Plugins path: " + pluginFolder.getAbsolutePath(), 0);
// TODO: DEBUG

        File file = new File(pathToJar);
// TODO: DEBUG
//        MyLogger.log("JAR NAME: " + jarName, 0);
//        MyLogger.log("JAR ABSOLUTE PATH: " + file.getAbsolutePath(), 0);
// TODO: DEBUG
        try {
            JarFile jarFile = new JarFile(file);

            URL url = new URL("jar:file:" + file.getName() + "!/");
            URLClassLoader pluginLoader = new URLClassLoader(new URL[] { url });

            MyLogger.log("JAR PATH: " + url, 0);


            Enumeration<JarEntry> entries = jarFile.entries();
            int entryCount = 0;
            while(entries.hasMoreElements()) {
                entries.nextElement();
                entryCount++;
            }
            boolean[] processedEntries = new boolean[entryCount];

            entries = jarFile.entries();
            while(entries.hasMoreElements()) {
                // TODO: DEBUG
//                MyLogger.log("NEXT ENTRY MAIN", 0);
                // TODO: DEBUG
                JarEntry jarEntry = entries.nextElement();
                String jarEntryName = jarEntry.getName();
                if(!jarEntryName.startsWith(path) || jarEntryName.length() == path.length()) {
                    continue;
                }
                setTreeCellRecursiveJar(jarEntry, jarFile, path, diagramPanel,
                                        treeCell, pluginLoader, processedEntries);
            }

        } catch (Exception e) {
            MyLogger.logException(e);
        }

        return treeCell;
    }


    public static void setTreeCellRecursiveJar(JarEntry jarEntry, JarFile jarFile, String path,
                                               DiagramPanel diagramPanel,
                                               JTreeCellTextForUnits treeCell, URLClassLoader pluginLoader,
                                               boolean[] processedEntries) {
        Enumeration<JarEntry> entries = jarFile.entries();
        int index = -1;
        while(entries.hasMoreElements()) {
            JarEntry currEntry = entries.nextElement();
            String currJarEntryName = currEntry.getName();
            index++;
            if( !currJarEntryName.startsWith(path) || currJarEntryName.length() == path.length() ||
                    !currJarEntryName.startsWith(jarEntry.getName()) ) {
                continue;
            }

            // TODO: DEBUG
//            MyLogger.log("ENTRY: " + index, 0);
            // TODO: DEBUG
            if(processedEntries[index]) {
                continue;
            }
            processedEntries[index] = true;
            if(currEntry.isDirectory()) {
                currJarEntryName = currJarEntryName.substring(0, currJarEntryName.length() - 1);    // Because there is / at the end of JAR directory
                currJarEntryName = getStringAfterLastChar(currJarEntryName, '/');
                JTreeCellTextForUnits newTreeCell = new JTreeCellTextForUnits(currJarEntryName);
                treeCell.addChildren(newTreeCell);

                setTreeCellRecursiveJar(currEntry, jarFile, path, diagramPanel,
                                        newTreeCell, pluginLoader, processedEntries);
            }
            else {
                addChildrenToTreeCellJarVersion(currEntry.getName(), pluginLoader, diagramPanel, treeCell);
            }
        }
    }




    private class PanelRenderer implements TreeCellRenderer {
        public Component getTreeCellRendererComponent(JTree tree,
                                                      Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row,
                                                      boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if(userObject instanceof Unit) {
                return ((Unit) userObject).getShapedPanel();
            }
            else {
                return (Component) (userObject);
            }
        }
    }


    private static class JTreeCellTextForUnits extends JTreeCellText {
        public JTreeCellTextForUnits(String text) {
            super(text);
            children = new ArrayList<>();
        }

        private List<JTreeCellClickedCallbackIFace> children;
        @Override
        public List<JTreeCellClickedCallbackIFace> getChildren() {
            return children;
        }

        public void addChildren(Unit u) {
            ShapedPanel sp = u.getShapedPanel();
            sp.setPreferredSize(new Dimension(64, 64));
            sp.reshape(sp.getPreferredSize());
            children.add(u);
        }

        public void addChildren(JTreeCellClickedCallbackIFace cell) {
            children.add(cell);
        }
    }
}