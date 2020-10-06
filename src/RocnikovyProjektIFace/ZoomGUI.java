package RocnikovyProjektIFace;

import DebugPackage.DEBUG_CLASS;
import Rocnikovy_Projekt.MyLogger;
import Rocnikovy_Projekt.ProgramTest;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class ZoomGUI extends JPanel {
    public ZoomGUI(ActionListener zoomButtonListener, ActionListener unzoomButtonListener) {
        unzoomButton = new JButton();
        unzoomButton.addActionListener(unzoomButtonListener);
        disableUnzoomButton();
        this.add(unzoomButton);

        zoomButton = new JButton();
        zoomButton.addActionListener(zoomButtonListener);
        enableZoomButton();
        this.add(zoomButton);

        // TODO: make the path relative
        String resourcesDir = "resources/images/";

        File file = null;
        Image img;
        try {
            file = new File(resourcesDir + "NewPlusFilled.png");
            img = ImageIO.read(file);
            // The sizes have to be artificial, since for some reason when it is set to the preferred size,
            // it doesn't fill the whole free space of button and also the button gets larger.
            // So I can't call it component listener with the preferred size, which is listening for resizing events.
            img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH) ;
            zoomButton.setIcon(new ImageIcon(img));

            file = new File(resourcesDir + "NewMinusFilled.png");
            img = ImageIO.read(file);
            img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH) ;
            unzoomButton.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            ProgramTest.debugPrint("IMG file:", file.getAbsolutePath());
            MyLogger.logException(ex);
            System.exit(179);
        }

        zoomLabel = new JLabel("0");
        this.add(zoomLabel);
    }

    private JLabel zoomLabel;
    public void setNewZoom(int newZoom, boolean isMaxZoomReached) {
        if(newZoom == 0) {
            disableUnzoomButton();
        }
        else {
            if(!unzoomButton.isEnabled()) {
                enableUnzoomButton();
            }
        }

        if(isMaxZoomReached) {
            disableZoomButton();
        }
        else {
            if(!zoomButton.isEnabled()) {
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
