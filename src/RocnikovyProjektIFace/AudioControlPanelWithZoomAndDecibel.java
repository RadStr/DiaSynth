package RocnikovyProjektIFace;

import RocnikovyProjektIFace.DecibelDetectorPackage.DecibelDetectorMainClassData;
import RocnikovyProjektIFace.DecibelDetectorPackage.GetValuesIFace;

import java.awt.event.ActionListener;

/**
 * Contains the play/pause button, mute button, volume slider, zoom buttons and decibel detector
 */
public class AudioControlPanelWithZoomAndDecibel extends AudioControlPanelWithZoom {
    public AudioControlPanelWithZoomAndDecibel(GetValuesIFace panelWithSamples, ActionListener playButtonActionListener,
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
