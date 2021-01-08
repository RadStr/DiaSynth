package str.rad.player.control;

import str.rad.plugin.util.PluginLoader;
import str.rad.util.logging.MyLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class ZoomPanel extends JPanel {
    public ZoomPanel(ActionListener zoomButtonListener, ActionListener unzoomButtonListener) {
        unzoomButton = new JButton();
        unzoomButton.addActionListener(unzoomButtonListener);
        disableUnzoomButton();
        this.add(unzoomButton);

        zoomButton = new JButton();
        zoomButton.addActionListener(zoomButtonListener);
        enableZoomButton();
        this.add(zoomButton);


        String resourcesDir = "resources/images/";
        File file = null;
        Image img;
        try {
            if (PluginLoader.isJar(getClass())) {
                // Using the variant with getResource(), getResourceAsStream() returns null
                // https://stackoverflow.com/questions/31127/java-swing-displaying-images-from-within-a-jar
                img = ImageIO.read(getClass().getResource("/" + resourcesDir + "PlusTrans.png"));
            }
            else {
                file = new File(resourcesDir + "PlusTrans.png");
                img = ImageIO.read(file);
            }

            // The sizes have to be artificial, since for some reason when it is set to the preferred size,
            // it doesn't fill the whole free space of button and also the button gets larger.
            // So I can't call it component listener with the preferred size, which is listening for resizing events.
            img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            zoomButton.setIcon(new ImageIcon(img));

            if (PluginLoader.isJar(getClass())) {
                // Using the variant with getResource(), getResourceAsStream() returns null
                // https://stackoverflow.com/questions/31127/java-swing-displaying-images-from-within-a-jar
                img = ImageIO.read(getClass().getResource("/" + resourcesDir + "MinusTrans.png"));
            }
            else {
                file = new File(resourcesDir + "MinusTrans.png");
                img = ImageIO.read(file);
            }

            img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            unzoomButton.setIcon(new ImageIcon(img));
        }
        catch (Exception ex) {
            MyLogger.logWithoutIndentation("IMG file is probably missing: " + file.getAbsolutePath());
            MyLogger.logException(ex);
        }

        zoomLabel = new JLabel("0");
        this.add(zoomLabel);
    }

    private JLabel zoomLabel;

    public void setNewZoom(int newZoom, boolean isMaxZoomReached) {
        if (newZoom == 0) {
            disableUnzoomButton();
        }
        else {
            if (!unzoomButton.isEnabled()) {
                enableUnzoomButton();
            }
        }

        if (isMaxZoomReached) {
            disableZoomButton();
        }
        else {
            if (!zoomButton.isEnabled()) {
                enableZoomButton();
            }
        }


        zoomLabel.setText(Integer.toString(newZoom));
    }

    private JButton unzoomButton;

    private void enableUnzoomButton() {
        unzoomButton.setEnabled(true);
    }

    private void disableUnzoomButton() {
        unzoomButton.setEnabled(false);
    }

    private JButton zoomButton;

    private void enableZoomButton() {
        zoomButton.setEnabled(true);
    }

    private void disableZoomButton() {
        zoomButton.setEnabled(false);
    }
}