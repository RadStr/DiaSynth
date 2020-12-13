package RocnikovyProjektIFace.drawing;

import RocnikovyProjektIFace.AudioPlayerPlugins.ifaces.CustomJFramePlugin;

import javax.swing.*;

public class DrawJFrame extends CustomJFramePlugin {
    public DrawJFrame(JPanel drawPanel, String pluginName) {
        super(pluginName);
        this.drawPanel = drawPanel;
    }

    private JPanel drawPanel;
    public JPanel getDrawPanel() {
        return drawPanel;
    }
}
