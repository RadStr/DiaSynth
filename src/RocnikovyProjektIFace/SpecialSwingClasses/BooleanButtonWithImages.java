package RocnikovyProjektIFace.SpecialSwingClasses;

import Rocnikovy_Projekt.MyLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class BooleanButtonWithImages extends BooleanButton {
    private ImageIcon ifTrueImageIcon;
    private ImageIcon ifFalseImageIcon;

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public BooleanButtonWithImages(boolean bool, String pathToIfTrueImage, String pathToIfFalseImage) {
        this.boolVar = bool;
        setImageIcons(pathToIfTrueImage, pathToIfFalseImage);
        setButtonVisuals();
    }


    // TODO: https://stackoverflow.com/questions/21720013/cant-get-imageicon-to-display-swing
    private void setImageIcons(String pathToIfTrueImage, String pathToIfFalseImage) {
        File imageCheckIfTrue = new File(pathToIfTrueImage);
        File imageCheckIfFalse = new File(pathToIfFalseImage);

        Image img;
        try {
            img = ImageIO.read(imageCheckIfTrue);
            // The sizes have to be artificial, since for some reason when it is set to the preferred size,
            // it doesn't fill the whole free space of button and also the button gets larger.
            // So I can't call it in component listener with the preferred size, which is listening for resizing events.
            img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            ifTrueImageIcon = new ImageIcon(img);
        } catch (Exception ex) {
            MyLogger.logException(ex);
        }

        try {
            img = ImageIO.read(imageCheckIfFalse);
            img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            ifFalseImageIcon = new ImageIcon(img);
        } catch (Exception ex) {
            MyLogger.logException(ex);
        }
    }

    @Override
    protected void setButtonVisuals() {
        if(boolVar) {
            this.setIcon(ifTrueImageIcon);
        }
        else {
            this.setIcon(ifFalseImageIcon);
        }
    }
}