package player;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class AudioPlayerPanelMouseWheelListener implements MouseWheelListener {
    private AudioPlayerPanelZoomUpdateIFace audioPlayerPanel;

    public AudioPlayerPanelMouseWheelListener(AudioPlayerPanelZoomUpdateIFace audioPlayerPanel) {
        this.audioPlayerPanel = audioPlayerPanel;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int wheelRotation = e.getWheelRotation();
        // Because mouse wheel event is < 0 when scrolling up and we want it to be > 0 because it is zooming
        wheelRotation *= -1;
        audioPlayerPanel.updateZoom(wheelRotation);
    }
}
