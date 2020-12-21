package main;

import plugin.util.PluginLoader;
import util.swing.ErrorFrame;
import util.swing.FrameWithFocusControl;
import util.logging.MyLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


// How I created the .jar
// https://stackoverflow.com/questions/1082580/how-to-build-jars-from-intellij-properly - the one with pictures
// The META-INF in the src of directory is created because of that (because I set the directory to the rc - I don't change anything)
// File -> Project Structure -> modules -> + -> JAR -> From module with dependencies... -> set Main Class to PartsConnectionGUI.main
// -> directory for META-INF/MANIFEST.MF doesn't need to be changed -> OK
// Creating the .jar: Build -> Build Artifacts... -> Build

// How to add jar libraries in intellij:
// https://stackoverflow.com/questions/1051640/correct-way-to-add-external-jars-lib-jar-to-an-intellij-idea-project
public class Main {
    public static void main(String[] args) {
        if(!PluginLoader.isInJar()) {
            PluginLoader.removePreviouslyLoadedPlugins();
            PluginLoader.copyPlugins();
        }
        MyLogger.logWithoutIndentation("Starting program");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    // https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TabbedPaneDemoProject/src/components/TabbedPaneDemo.java
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from
     * the event dispatch thread.
     */
    private static void createAndShowGUI() {
        FrameWithFocusControl frame = null;

        try {
            //Create and set up the window.
            MyLogger.log("Creating frame", 1);
            frame = new FrameWithFocusControl("DiaSynth") {
                private Dimension minSize = new Dimension(1024, 768);

                @Override
                public Dimension getMinimumSize() {
                    return minSize;
                }
            };
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    MyLogger.close();
                }
            });
            MyLogger.log("Created frame", -1);

            //Add content to the window.
            DiasynthTabbedPanel program = new DiasynthTabbedPanel(frame);
            frame.add(program, BorderLayout.CENTER);

            // Display the window.
            // I have to call setMinimumSize even when I had overridden the getMinimumSize in the frame, because
            // java needs some impulse to take the minimum size into consideration
            // (I could set it to random min size here and it would still have the 1024, 768 min size)
            // But it changes the start size if it is bigger than the min size
            frame.setMinimumSize(new Dimension(1024, 768));
            frame.setVisible(true);

            MyLogger.logWithoutIndentation("Showed GUI");
        }
        catch(Exception e) {
            MyLogger.logWithoutIndentation("UNEXPECTED EXCEPTION INSIDE PROGRAM ... ENDING PROGRAM");
            MyLogger.logException(e);
            if(frame == null) {
                MyLogger.logWithoutIndentation("Program ended before the frame was created.");
            }
            MyLogger.logWithoutIndentation("PROGRAM ENDED");
            new ErrorFrame(frame, "UNEXPECTED EXCEPTION - check log file");
        }
    }
}
