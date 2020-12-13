package player.decibel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Doesn't add the decibel meter to the panel/frame
 */
public class DecibelMeterMain {
    public DecibelMeterMain(SamplesGetterIFace mainClass, int numberOfChannels) {
        changeNumberOfChannels(numberOfChannels);
        setDecibelMeter(mainClass);
    }

    private Timer decibelTimer;
    private DecibelMeter decibelMeter;
    public DecibelMeter getDecibelMeter() {
        return decibelMeter;
    }
    private double[] channelSamples;
    public double[] getChannelSamples() {
        return channelSamples;
    }
    public void changeNumberOfChannels(int newChannelCount) {
        channelSamples = new double[newChannelCount];
    }

    // Play with - old value was 96, but I would need to do some averaging to make it work, it just flashes too quick
    public static final int DECIBEL_METER_TIMER_DELAY = 128;

    public void setDecibelMeter(SamplesGetterIFace mainClass) {
        decibelMeter = new DecibelMeter(mainClass);

        decibelTimer = new Timer(DECIBEL_METER_TIMER_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decibelMeter.setShouldFindNewDecibels();
                decibelMeter.repaint();
            }
        });

        decibelTimer.start();
    }
}
