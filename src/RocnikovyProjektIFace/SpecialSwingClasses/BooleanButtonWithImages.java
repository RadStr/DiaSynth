package RocnikovyProjektIFace.SpecialSwingClasses;

import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.AudioPlayerJMenuOperationPluginIFace;
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
     * Note for the path parameters: If we are in jar then it should be the absolute path inside the jar,
     * if in classic file system then either relative or absolute is ok
     */
    public BooleanButtonWithImages(boolean startValue, String pathToIfTrueImage, String pathToIfFalseImage) {
        this.boolVar = startValue;
        setImageIcons(pathToIfTrueImage, pathToIfFalseImage);
        setButtonVisuals();
    }


    // TODO: https://stackoverflow.com/questions/21720013/cant-get-imageicon-to-display-swing
    private void setImageIcons(String pathToIfTrueImage, String pathToIfFalseImage) {
        Image img;
        try {
            if (AudioPlayerJMenuOperationPluginIFace.isJar(getClass())) {
                // Using the variant with getResource(), getResourceAsStream() returns null
                // https://stackoverflow.com/questions/31127/java-swing-displaying-images-from-within-a-jar
                img = ImageIO.read(getClass().getResource(pathToIfTrueImage));
            }
            else {
                File imageCheckIfTrue = new File(pathToIfTrueImage);
                img = ImageIO.read(imageCheckIfTrue);
            }

            // The sizes have to be artificial, since for some reason when it is set to the preferred size,
            // it doesn't fill the whole free space of button and also the button gets larger.
            // So I can't call it in component listener with the preferred size, which is listening for resizing events.
            img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            ifTrueImageIcon = new ImageIcon(img);
        } catch (Exception ex) {
            MyLogger.logException(ex);
        }

        try {
            if (AudioPlayerJMenuOperationPluginIFace.isJar(getClass())) {
                // Using the variant with getResource(), getResourceAsStream() returns null
                // https://stackoverflow.com/questions/31127/java-swing-displaying-images-from-within-a-jar
                img = ImageIO.read(getClass().getResource(pathToIfFalseImage));
            }
            else {
                File imageCheckIfFalse = new File(pathToIfFalseImage);
                img = ImageIO.read(imageCheckIfFalse);
            }
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