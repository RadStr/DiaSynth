package player.plugin.ifaces;

import javax.swing.*;

public class JFileChooserPlugin extends JFileChooser implements PluginBaseIFace {
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
        return "Get file";
    }
}