package deprecatedclasses;

import synthesizer.gui.SynthesizerMainPanel;

import javax.swing.*;
import java.awt.*;

@Deprecated
public class OtherMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createTestWindow();
            }
        });
    }

    private static void createTestWindow() {
        JFrame frame = new JFrame();
        //frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().setLayout(new BorderLayout());
        SynthesizerMainPanel panel = new SynthesizerMainPanel(frame, null);
        // DiagramItemsMenu panel = new DiagramItemsMenu();
        //frame.add(panel);
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        // If we want the X button to close the app
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}