package RocnikovyProjektIFace.AudioPlayerPlugins.plugins;

import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginIFacesForUsers.WithoutInputWavePackage.WithoutInputWavePluginIFace;
import Rocnikovy_Projekt.DoubleWave;

import javax.swing.*;
import java.awt.*;

public class TestPluginWithParametersWithoutWaveWithCustomJPaneInput extends JPanel
    implements WithoutInputWavePluginIFace {

    public TestPluginWithParametersWithoutWaveWithCustomJPaneInput() {
        this.setLayout(new GridLayout(0, 2));

        label = new JLabel("TestPluginWithParametersWithoutWaveWithDefaultJPaneInput");
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
        for(int i = startIndex; i < endIndex; i++) {
            song[i] = val;
        }
    }

    @Override
    public boolean shouldWaitForParametersFromUser() {
        return true;
    }

    @Override
    public boolean isUsingDefaultJPanel() {
        return false;
    }

    @Override
    public String getPluginName() {
        return "TestPluginWithParametersWithoutWaveWithCustomJPaneInput";
    }

    @Override
    public String getPluginTooltip() {
        return "This plugin uses custom pane and sets song to given parameter";
    }
}
