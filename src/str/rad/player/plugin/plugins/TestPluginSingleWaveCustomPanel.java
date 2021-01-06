package str.rad.player.plugin.plugins;

import str.rad.player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import str.rad.util.audio.wave.DoubleWave;

import javax.swing.*;
import java.awt.*;

public class TestPluginSingleWaveCustomPanel extends JPanel
        implements OperationOnWavePluginIFace {

    public TestPluginSingleWaveCustomPanel() {
        this.setLayout(new GridLayout(0, 2));

        label = new JLabel("Custom label");
        this.add(label);

        textField = new JTextField("Put double here");
        this.add(textField);
    }

    private double val;
    private JLabel label;
    private JTextField textField;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        try {
            val = Double.parseDouble(textField.getText());
        }
        catch(Exception e) {
            return;
        }
        double[] song = audio.getSong();
        for (int i = startIndex; i < endIndex; i++) {
            song[i] = val;
        }
    }

    @Override
    public boolean shouldWaitForParametersFromUser() {
        return true;
    }

    @Override
    public boolean isUsingPanelCreatedFromAnnotations() {
        return false;
    }

    @Override
    public String getPluginName() {
        return "Test plugin - Single wave and custom panel";
    }

    @Override
    public String getPluginTooltip() {
        return "This plugin uses custom pane and sets song to given parameter";
    }
}
