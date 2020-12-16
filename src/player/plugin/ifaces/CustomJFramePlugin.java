package player.plugin.ifaces;

import javax.swing.*;

public class CustomJFramePlugin extends JFrame implements PluginBaseIFace {
    public CustomJFramePlugin(String pluginName) {
        PLUGIN_NAME = pluginName;
    }
    private final String PLUGIN_NAME;

    /**
     * @return Returns true if the operation needs parameters - so user needs to put them to the JPanel.
     * If it returns false, then it doesn't need parameters from user and the operation can start immediately
     */
    @Override
    public boolean shouldWaitForParametersFromUser() {
        return true;
    }

    /**
     * This parameter matters only when shouldWaitForParametersFromUser returns true
     *
     * @return
     */
    @Override
    public boolean isUsingDefaultJPanel() {
        return false;
    }

    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }
}
