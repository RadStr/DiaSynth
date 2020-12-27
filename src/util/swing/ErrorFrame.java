package util.swing;

import javax.swing.*;
import java.awt.*;

/**
 * This class creates new frame and closes the old one.
 * This frame is created, when some error occurs.
 */
public class ErrorFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JPanel errorPanel;
    private ExitButton exit;
    private JLabel errorMessage;

    public ErrorFrame(JFrame frame, String errorMessageText) {
        errorMessage = new JLabel(errorMessageText);
        errorPanel = new JPanel();
        exit = new ExitButton();
        this.add(errorPanel);
        this.setSize(500, 500);
        errorPanel.setLayout(new BorderLayout());
        errorPanel.add(errorMessage, BorderLayout.CENTER);
        errorPanel.add(exit, BorderLayout.PAGE_END);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        if (frame != null) {
            frame.dispose();
        }
    }
}
