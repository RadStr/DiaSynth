package deprecatedclasses;

import synthesizer.gui.SynthesizerMainPanel;

import javax.swing.*;
import java.awt.*;

// TODO: RML
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
        frame.getContentPane().setLayout(new BorderLayout()); // TODO:
        SynthesizerMainPanel panel = new SynthesizerMainPanel(frame, null);
        // DiagramItemsMenu panel = new DiagramItemsMenu();
        //frame.add(panel);
        frame.add(panel, BorderLayout.CENTER);    // TODO:
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // if you want the X button to close the app
    }
}
// TODO: RML