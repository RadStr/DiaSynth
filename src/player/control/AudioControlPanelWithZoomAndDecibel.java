package player.control;

import player.decibel.DecibelMeterMain;
import player.decibel.SamplesGetterIFace;

import java.awt.event.ActionListener;

/**
 * Contains the play/pause button, mute button, volume slider, zoom buttons and decibel meter
 */
public class AudioControlPanelWithZoomAndDecibel extends AudioControlPanelWithZoom {
    public AudioControlPanelWithZoomAndDecibel(SamplesGetterIFace panelWithSamples, ActionListener playButtonActionListener,
                                               VolumeControlGetterIFace masterGainGetter, ActionListener zoomListener,
                                               ActionListener unzoomListener, int numberOfChannels) {
        super(playButtonActionListener, masterGainGetter, zoomListener, unzoomListener);

        decibelMeterMain = new DecibelMeterMain(panelWithSamples, numberOfChannels);
        this.add(decibelMeterMain.getDecibelMeter());
    }

    private DecibelMeterMain decibelMeterMain;

    public DecibelMeterMain getDecibelMeterMain() {
        return decibelMeterMain;
    }
}
