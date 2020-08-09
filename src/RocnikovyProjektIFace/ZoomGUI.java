package RocnikovyProjektIFace;

import DebugPackage.DEBUG_CLASS;
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
            file = new File(resourcesDir + "Plus.png");
            img = ImageIO.read(file);
            img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH) ;    // TODO: Ty rozmery nejsou idealni, ale nenapada me jak lip to vyresit, pres pomery nemuzu, kdyz je pak skladam vedle sebe tak musim upravit ty rozmery
            zoomButton.setIcon(new ImageIcon(img));

            file = new File(resourcesDir + "Minus.png");
            img = ImageIO.read(file);
            img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH) ;    // TODO: Ty rozmery nejsou idealni, ale nenapada me jak lip to vyresit, pres pomery nemuzu, kdyz je pak skladam vedle sebe tak musim upravit ty rozmery
            unzoomButton.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            ProgramTest.debugPrint("IMG file:", file.getAbsolutePath());
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
