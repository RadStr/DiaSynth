package DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage;

import javax.swing.*;
import java.awt.*;
// TODO: RML
@Deprecated
public class Main {

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
        MainPanelWithEverything panel = new MainPanelWithEverything(frame, null);
       // MenuWithItems panel = new MenuWithItems();
        //frame.add(panel);
        frame.add(panel, BorderLayout.CENTER);    // TODO:
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // if you want the X button to close the app
    }
}
// TODO: RML