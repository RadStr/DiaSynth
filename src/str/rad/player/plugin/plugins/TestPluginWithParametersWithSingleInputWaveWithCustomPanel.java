package str.rad.player.plugin.plugins;

import str.rad.player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import str.rad.util.audio.wave.DoubleWave;

import javax.swing.*;
import java.awt.*;

public class TestPluginWithParametersWithSingleInputWaveWithCustomPanel extends JPanel
        implements OperationOnWavePluginIFace {

    public TestPluginWithParametersWithSingleInputWaveWithCustomPanel() {
        this.setLayout(new GridLayout(0, 2));

        label = new JLabel("TestPluginWithParametersWithSingleInputWaveWithCustomPanel");
        this.add(label);

        textField = new JTextField("Put here double");
        this.add(textField);
    }

    private double val;
    private JLabel label;
    private JTextField textField;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        val = Double.parseDouble(textField.getText());
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
        return "TestPluginWithParametersWithSingleInputWaveWithCustomPanel";
    }

    @Override
    public String getPluginTooltip() {
        return "This plugin uses custom pane and sets song to given parameter";
    }
}
