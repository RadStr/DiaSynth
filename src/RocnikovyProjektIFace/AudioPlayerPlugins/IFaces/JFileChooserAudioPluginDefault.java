package RocnikovyProjektIFace.AudioPlayerPlugins.IFaces;

import javax.swing.*;

public class JFileChooserAudioPluginDefault extends JFileChooser implements PluginDefaultIFace {
    @Override
    public boolean shouldWaitForParametersFromUser() {
        return true;
    }

    @Override
    public boolean isUsingDefaultJPane() {
        return false;
    }

    @Override
    public String getPluginName() {
        return "Get file";
    }
}