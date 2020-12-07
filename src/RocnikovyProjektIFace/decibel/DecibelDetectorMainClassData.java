package RocnikovyProjektIFace.decibel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Doesn't add the decibel detector to the panel/frame
 */
public class DecibelDetectorMainClassData {
    public DecibelDetectorMainClassData(SamplesGetterIFace mainClass, int numberOfChannels) {
        changeNumberOfChannels(numberOfChannels);
        setDecibelDetector(mainClass);
    }

    private Timer decibelTimer;
    private DecibelDetector decibelDetector;
    public DecibelDetector getDecibelDetector() {
        return decibelDetector;
    }
    private double[] channelAmplitudes;
    public double[] getChannelAmplitudes() {
        return channelAmplitudes;
    }
    public void changeNumberOfChannels(int newChannelCount) {
        channelAmplitudes = new double[newChannelCount];
    }

    public void setDecibelDetector(SamplesGetterIFace mainClass) {
        decibelDetector = new DecibelDetector(mainClass);

        decibelTimer = new Timer(96, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decibelDetector.setShouldFindNewDecibels();
                decibelDetector.repaint();
            }
        });

        decibelTimer.start();
    }
}
