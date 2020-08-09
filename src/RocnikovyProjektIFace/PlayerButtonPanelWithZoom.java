package RocnikovyProjektIFace;

import java.awt.event.ActionListener;

public class PlayerButtonPanelWithZoom extends PlayerButtonPanelSimple {
    public PlayerButtonPanelWithZoom(ActionListener playButtonActionListener,
                                     SoundControlGetterIFace masterGainGetter,
                                     ActionListener zoomListener,
                                     ActionListener unzoomListener) {
        super(playButtonActionListener, masterGainGetter);

        zoomGUI = new ZoomGUI(zoomListener, unzoomListener);
        addToInternalPanel(zoomGUI);
    }

    private ZoomGUI zoomGUI;
    public ZoomGUI getZoomGUI() {
        return zoomGUI;
    }
}
