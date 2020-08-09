package RocnikovyProjektIFace.AudioPlayerPlugins.IFaces;

/**
 * The class implementing this interface should either return true in isUsingDefaultJPane()
 * or if it returns false, then it should extend JPanel since it will be used in dialog, but it doesn't have to
 * since dialog takes object, but for it to be useful it should.
 */
public interface PluginDefaultIFace {
    /**
     *
     * @return Returns true if the operation needs parameters - so user needs to put them to the JPanel.
     * If it returns false, then it doesn't need parameters from user and the operation can start immediately
     */
    boolean shouldWaitForParametersFromUser();

    /**
     * This parameter matters only when shouldWaitForParametersFromUser returns true
     * @return
     */
    boolean isUsingDefaultJPane();

    String getPluginName();
}