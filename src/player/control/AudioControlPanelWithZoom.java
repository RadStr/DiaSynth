package player.control;

import java.awt.event.ActionListener;

public class AudioControlPanelWithZoom extends AudioControlPanel {
    public AudioControlPanelWithZoom(ActionListener playButtonActionListener,
                                     VolumeControlGetterIFace masterGainGetter,
                                     ActionListener zoomListener,
                                     ActionListener unzoomListener) {
        super(playButtonActionListener, masterGainGetter);

        zoomPanel = new ZoomPanel(zoomListener, unzoomListener);
        addToInternalPanel(zoomPanel);
    }

    private ZoomPanel zoomPanel;
    public ZoomPanel getZoomPanel() {
        return zoomPanel;
    }
}
