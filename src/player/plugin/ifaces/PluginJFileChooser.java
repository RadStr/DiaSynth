package player.plugin.ifaces;

import javax.swing.*;

public class PluginJFileChooser extends JFileChooser implements PluginBaseIFace {
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
        return "Get file";
    }
}