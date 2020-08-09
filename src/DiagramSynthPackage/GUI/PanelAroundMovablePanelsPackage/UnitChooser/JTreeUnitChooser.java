package DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage.UnitChooser;

// Modified code from:
// https://coderanch.com/t/339685/java/JTree-Setting-node-JPanel
// And also this https://docs.oracle.com/javase/tutorial/uiswing/components/table.html#editrender for reference


import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.*;
import DiagramSynthPackage.Synth.OutputUnit;
import DiagramSynthPackage.Synth.Unit;
import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.AudioPlayerJMenuOperationPluginIFace;
import Rocnikovy_Projekt.MyLogger;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.jar.JarFile;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public class JTreeUnitChooser extends JTree {
    public JTreeUnitChooser(JPanelWithMovableJPanels  panelWithMovableJPanels) {
        createTree(this, panelWithMovableJPanels);

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


    public static JTreeCellClickedCallbackIFace getRoot(JPanelWithMovableJPanels panelWithMovableJPanels) {
        JTreeCellClickedCallbackIFace root;
        root = loadJTreeElements(Unit.class.getPackage().getName(), panelWithMovableJPanels);
        return root;
    }


    private static void createTree(JTree tree, JPanelWithMovableJPanels panelWithMovableJPanels) {
        JTreeCellClickedCallbackIFace root = getRoot(panelWithMovableJPanels);
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
                                                          JPanelWithMovableJPanels panelWithMovableJPanels) {
        if (AudioPlayerJMenuOperationPluginIFace.isJar(Unit.class)) {
            String folderName = getStringAfterLastChar(pluginPackage, '.');
            JTreeCellTextForUnits treeCell = new JTreeCellTextForUnits(folderName);
            String jarName = AudioPlayerJMenuOperationPluginIFace.getJarName(Unit.class);
            return setMainTreeCellJarVersion(panelWithMovableJPanels, pluginPackage, jarName, treeCell);
        }
        else {
            String path = "src/" + pluginPackage.replace('.', '/');
            final File folder = new File(path);
            return setMainTreeCellNonJarVersion(".*\\.java", folder, pluginPackage, panelWithMovableJPanels);
        }
    }

    public static String getStringAfterLastChar(String text, char c) {
        int index = text.lastIndexOf(c);
        String folderName = text.substring(index + 1);
        return folderName;
    }


    private static void addChildrenToTreeCellNonJarVersion(String pluginName, String pluginPackage,
                                                           JPanelWithMovableJPanels panelWithMovableJPanels,
                                                           JTreeCellTextForUnits treeCell) {
        pluginName = pluginName.replace(".java", "");
        pluginName = pluginPackage + "." + pluginName;
        try {
            Class<?> clazz = Class.forName(pluginName);
            if(Unit.class.isAssignableFrom(clazz) && clazz != OutputUnit.class) {
                Constructor<?> constructor = clazz.getConstructor(JPanelWithMovableJPanels.class);
                if(Modifier.isAbstract(clazz.getModifiers())) {
                    // EMPTY
                }
                else if (constructor == null) {
                    MyLogger.logWithoutIndentation("Doesn't have corresponding constructor");
                }
                else {
                    Unit u;
                    u = (Unit) constructor.newInstance(new Object[] { panelWithMovableJPanels });
                    treeCell.addChildren(u);
                }
            }
        }
        catch (Exception e) {
            MyLogger.logException(e);
        }
    }


    private static void addChildrenToTreeCellJarVersion(String className, URLClassLoader pluginLoader,
                                                        JPanelWithMovableJPanels panelWithMovableJPanels,
                                                        JTreeCellTextForUnits treeCell) {
        try {
            Class<?> clazz = pluginLoader.loadClass(className.replaceAll("/", ".").replace(".class", ""));
            if (Unit.class.isAssignableFrom(clazz) && clazz != OutputUnit.class) {
                Constructor<?> constructor = clazz.getConstructor(JPanelWithMovableJPanels.class);
                if (Modifier.isAbstract(clazz.getModifiers())) {
                    // EMPTY
                } else if (constructor == null) {
                    MyLogger.logWithoutIndentation("Doesn't have corresponding constructor");
                } else {
                    MyLogger.log("Adding Unit: " + className, 0);
                    Unit u;
                    u = (Unit) constructor.newInstance(new Object[] { panelWithMovableJPanels });
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
                                                                     JPanelWithMovableJPanels panelWithMovableJPanels) {
        JTreeCellTextForUnits treeCell = new JTreeCellTextForUnits(folder.getName());
        setTreeCellRecursive(pattern, folder, pluginPackage, panelWithMovableJPanels, treeCell);
        return treeCell;
    }
    public static void setTreeCellRecursive(final String pattern, final File folder, String pluginPackage,
                                            JPanelWithMovableJPanels panelWithMovableJPanels,
                                            JTreeCellTextForUnits treeCell) {
        for (final File f : folder.listFiles()) {
            if (f.isDirectory()) {
                JTreeCellTextForUnits newTreeCell = new JTreeCellTextForUnits(f.getName());
                treeCell.addChildren(newTreeCell);
                setTreeCellRecursive(pattern, f, pluginPackage + "." + f.getName(),
                        panelWithMovableJPanels, newTreeCell);
            }
            else if (f.isFile()) {
                if (f.getName().matches(pattern)) {
                    addChildrenToTreeCellNonJarVersion(f.getName(), pluginPackage, panelWithMovableJPanels, treeCell);
                }
            }
        }
    }





    // Modified code from:
    // https://stackoverflow.com/questions/25449/how-to-create-a-pluginable-java-program
    // https://alexiyorlov.github.io/tutorials/java-plugins.html
    /**
     *
     * @param packageContainingPlugins is the package which contains the plugins
     * @return
     */
    public static JTreeCellTextForUnits setMainTreeCellJarVersion(JPanelWithMovableJPanels panelWithMovableJPanels,
                                                        String packageContainingPlugins, String jarName,
                                                        JTreeCellTextForUnits treeCell) {
        // + "/" Because I want it to behave as jar directory (which ends with /)
        String path = packageContainingPlugins.replace('.', '/') + "/";

        // TODO: RML
        //path = "C:/Users/Radek/eclipse-workspace/BakalarskaPrace/out/production/BakalarskaPrace/" + path;
        // TODO: RML

// TODO: DEBUG
//        MyLogger.log("Plugin interface canonical name: " + pluginIface.getCanonicalName(), 0);
//        MyLogger.log("Plugins package: " + packageContainingPlugins, 0);
//        MyLogger.log("Plugins name: " + pluginFolder.getName(), 0);
//        MyLogger.log("Plugins path: " + pluginFolder.getAbsolutePath(), 0);
// TODO: DEBUG

        File file = new File(jarName);
// TODO: DEBUG
//        MyLogger.log("JAR NAME: " + jarName, 0);
//        MyLogger.log("JAR ABSOLUTE PATH: " + file.getAbsolutePath(), 0);
// TODO: DEBUG
        try {
            JarFile jarFile = new JarFile(file);

            URL url = new URL("jar:file:" + file.getName() + "!/");
            URLClassLoader pluginLoader = new URLClassLoader(new URL[] { url });

            MyLogger.log("JAR PATH: " + url, 0);

            // Basically the same algorithm as for non jar version, but here we can't have recursion, so we have to
            // simulate it with stack
            Stack<JTreeCellTextForUnits> cells = new Stack<JTreeCellTextForUnits>();
            jarFile.stream().forEach(jarEntry -> {
                // TODO: Podle TOHODLE TO MUSIM NĚJAK UDĚLAT
//                if (f.isDirectory()) {
//                    JTreeCellTextForUnits newTreeCell = new JTreeCellTextForUnits(f.getName());
//                    treeCell.addChildren(newTreeCell);
//                    setTreeCellRecursive(pattern, f, pluginPackage + "." + f.getName(),
//                            panelWithMovableJPanels, newTreeCell);
//                }
//                else if (f.isFile()) {
//                    if (f.getName().matches(pattern)) {
//                        addChildrenToTreeCellNonJarVersion(f.getName(), pluginPackage, panelWithMovableJPanels, treeCell);
//                    }
//                }

                String jarEntryName = jarEntry.getName();
                if(jarEntryName.startsWith(path) && jarEntryName.length() != path.length()) {
                    MyLogger.log("PATH: " + path, 0);
                    MyLogger.log("JAR ENTRY NAME: " + jarEntryName, 0);
                    while(!cells.empty() && !jarEntryName.contains(cells.peek().getText())) {
                        cells.pop();
                    }

                    if (jarEntry.isDirectory()) {
                        jarEntryName = jarEntryName.substring(0, jarEntryName.length() - 1);    // Because there is / at the end of JAR directory
                        jarEntryName = getStringAfterLastChar(jarEntryName, '/');
                        JTreeCellTextForUnits newTreeCell = new JTreeCellTextForUnits(jarEntryName);
                        if (cells.empty()) {
                            treeCell.addChildren(newTreeCell);
                        } else {
                            cells.peek().addChildren(newTreeCell);
                        }
                        cells.push(newTreeCell);
                    } else {
                        if (jarEntryName.startsWith(path) && jarEntryName.endsWith(".class")) {
                            if(cells.empty()) {
                                addChildrenToTreeCellJarVersion(jarEntryName, pluginLoader, panelWithMovableJPanels, treeCell);
                            }
                            else {
                                addChildrenToTreeCellJarVersion(jarEntryName, pluginLoader, panelWithMovableJPanels, cells.peek());
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            MyLogger.logException(e);
        }

        return treeCell;
        // TODO: Podle TOHODLE TO MUSIM NĚJAK UDĚLAT



// TODO: VYMAZAT
//                String jarEntryName = jarEntry.getName();
//// TODO: DEBUG
////                MyLogger.log("Plugin folder (relative): " + path, 0);
////                MyLogger.log("JAR ENTRY NAME: " + jarEntryName, 0);
//// TODO: DEBUG
//
//                if (jarEntryName.startsWith(path) && jarEntryName.endsWith(".class")) {
//                    MyLogger.log("JAR ENTRY (.class): " + jarEntry.getName(), 0);
//                    classes.add(jarEntry.getName());
//                }
//            });
//        } catch (IOException e) {
//            MyLogger.logException(e);
//        }
//        URLClassLoader pluginLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
//        classes.forEach(s -> {
//            try {
//                Class classs = pluginLoader.loadClass(s.replaceAll("/", ".").replace(".class", ""));
//                Class[] interfaces = classs.getInterfaces();
//                for (Class anInterface : interfaces) {
//                    if (anInterface == pluginIface) {
//                        MyLogger.log("LOADED CLASS: " + s.replaceAll("/", ".").replace(".class", ""), 0);
//                        addInstanceToList(classs, loadedPlugins);
//                        break;
//                    }
//                }
//            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
//                MyLogger.logException(e);
//            }
//        });
//        return loadedPlugins;
// TODO: VYMAZAT
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