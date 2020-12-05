package RocnikovyProjektIFace;

import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.AudioPlayerJMenuOperationPluginIFace;
import RocnikovyProjektIFace.SpecialSwingClasses.BooleanButton;
import RocnikovyProjektIFace.SpecialSwingClasses.BooleanButtonWithImages;

import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Contains the play/pause button, mute button, volume slider
 */
public class AudioControlPanel extends JPanel {
    public AudioControlPanel(ActionListener playButtonActionListener, VolumeControlGetterIFace volumeControlGetter) {
        this.volumeControlGetter = volumeControlGetter;
        // TODO: make the path relative
        String resourcesDir = "resources/images/";

        this.setLayout(new GridLayout(0, 2));
        // TODO: PROGAMO
        insideControlPanel = new JPanel(new FlowLayout());
//        insideControlPanel = new JPanel();
//        insideControlPanel.setLayout(new BoxLayout(insideControlPanel, BoxLayout.LINE_AXIS));
        // TODO: PROGAMO
        buttons = new JButton[2];
        if (AudioPlayerJMenuOperationPluginIFace.isJar(getClass())) {
            playButton = new BooleanButtonWithImages(true,
                    "/" + resourcesDir + "PlayButtonTrans.png",
                    "/" + resourcesDir + "PauseButtonTrans.png");
        }
        else {
            playButton = new BooleanButtonWithImages(true,
                    resourcesDir + "PlayButtonTrans.png",
                    resourcesDir + "PauseButtonTrans.png");
        }
        // TODO: Don't know which one is better

        playButton.addActionListener(playButtonActionListener);


        buttons[0] = playButton;
        playButton.setToolTipText("Play/Pause button");

        if (AudioPlayerJMenuOperationPluginIFace.isJar(getClass())) {
            muteButton = new BooleanButtonWithImages(false,
                    "/" + resourcesDir + "soundIconOffTrans.png",
                    "/" + resourcesDir + "soundIconOnTrans.png");
        }
        else {
            muteButton = new BooleanButtonWithImages(false,
                    resourcesDir + "soundIconOffTrans.png",
                    resourcesDir + "soundIconOnTrans.png");
        }
        buttons[1] = muteButton;
        muteButton.setToolTipText("Mute button");
        muteButton.addActionListener((e) -> volumeControlGetter.getMuteControl().setValue(muteButton.getBoolVar()));

        for(int i = 0; i < buttons.length; i++) {
            insideControlPanel.add(buttons[i]);
        }

        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
        volumeSlider.setToolTipText("Volume slider");
        volumeSlider.addChangeListener(new ChangeListener() {
            // Volume change
            @Override
            public void stateChanged(ChangeEvent e) {
                setMasterGainToCurrentSlideValue();
            }
        });


        insideControlPanel.add(volumeSlider);

        this.add(insideControlPanel);
        setMasterGainToCurrentSlideValue();
    }


    private VolumeControlGetterIFace volumeControlGetter;
    public void setMasterGainToCurrentSlideValue() {
        FloatControl masterGainControl = volumeControlGetter.getGain();
        if(masterGainControl != null) {
            // maxGain is 0, because with sound boost, there could be clipping.
            double maxGain = 0;
            // minGain is / 2 because then the volume is too low and for the sound to be heard there is needed way to big sound amplification from speakers.
            // TODO: But it doesn't have to be / 2
            double minGain = masterGainControl.getMinimum() / 2;
            double minGainAbs = Math.abs(minGain);
            double range = maxGain + minGainAbs;
            double skip = range / volumeSlider.getMaximum();
            double val = skip * volumeSlider.getValue();
            val -= minGainAbs; // Shift it so the val is between minGain and maxGain

            // The ifs are just in case there will be some precision error
            if (val < minGain) {
                val = minGain;
            }
            else if (val > maxGain) {
                val = maxGain;
            }
            System.out.println("setMasterGainToCurrentSlideValue" + val + "\t" + minGainAbs + "\t" + maxGain);
            masterGainControl.setValue((float) val);
        }
    }

    private JButton[] buttons;
    private BooleanButton playButton;
    public BooleanButton getPlayButton() {
        return playButton;
    }

    private BooleanButton muteButton;
    public BooleanButton getMuteButton() {
        return muteButton;
    }

    private JSlider volumeSlider;
    public JSlider getVolumeSlider() {
        return volumeSlider;
    }

    private JPanel insideControlPanel;

    public void addToInternalPanel(JPanel p) {
        insideControlPanel.add(p);
    }


    public interface VolumeControlGetterIFace {
        FloatControl getGain();
        BooleanControl getMuteControl();
    }
}
