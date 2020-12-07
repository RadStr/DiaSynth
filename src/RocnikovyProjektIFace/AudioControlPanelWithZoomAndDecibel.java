package RocnikovyProjektIFace;

import RocnikovyProjektIFace.decibel.DecibelDetectorMainClassData;
import RocnikovyProjektIFace.decibel.SamplesGetterIFace;

import java.awt.event.ActionListener;

/**
 * Contains the play/pause button, mute button, volume slider, zoom buttons and decibel detector
 */
public class AudioControlPanelWithZoomAndDecibel extends AudioControlPanelWithZoom {
    public AudioControlPanelWithZoomAndDecibel(SamplesGetterIFace panelWithSamples, ActionListener playButtonActionListener,
                                               VolumeControlGetterIFace masterGainGetter, ActionListener zoomListener,
                                               ActionListener unzoomListener, int numberOfChannels) {
        super(playButtonActionListener, masterGainGetter, zoomListener, unzoomListener);

        decibelDetectorData = new DecibelDetectorMainClassData(panelWithSamples, numberOfChannels);
        this.add(decibelDetectorData.getDecibelDetector());
    }

    private DecibelDetectorMainClassData decibelDetectorData;
    public DecibelDetectorMainClassData getDecibelDetectorData() {
        return decibelDetectorData;
    }
}
