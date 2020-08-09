package RocnikovyProjektIFace;

import RocnikovyProjektIFace.DecibelDetectorPackage.DecibelDetectorMainClassData;
import RocnikovyProjektIFace.DecibelDetectorPackage.GetValuesIFace;

import java.awt.event.ActionListener;

/**
 * Contains the play/pause button, mute button, volume slider, zoom buttons and decibel detector
 */
public class PlayerButtonPanelWithZoomAdvanced extends PlayerButtonPanelWithZoom {
    public PlayerButtonPanelWithZoomAdvanced(GetValuesIFace panelWithSamples, ActionListener playButtonActionListener,
                                             SoundControlGetterIFace masterGainGetter, ActionListener zoomListener,
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
