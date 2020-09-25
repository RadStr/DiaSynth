package RocnikovyProjektIFace.Drawing;

import DiagramSynthPackage.Synth.Operators.UnaryOperations.WaveShaper;

import javax.swing.*;

public class DrawJFrame extends WaveShaper.CustomFramePlugin {
    public DrawJFrame(JPanel drawPanel, String pluginName) {
        super(pluginName);
        this.drawPanel = drawPanel;
    }

    private JPanel drawPanel;
    public JPanel getDrawPanel() {
        return drawPanel;
    }
}
