package str.rad.player.experimental;

import str.rad.plugin.CustomJFramePlugin;

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
