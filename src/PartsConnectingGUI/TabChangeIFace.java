package PartsConnectingGUI;

public interface TabChangeIFace {
    /**
     * Called when the tab changes (This method is called on the panel which was associated with the tab).
     * Should at least change JMenuBar.
     * @param hasFocus is true when the tab was changed to this class, false if it was visible before, but now it changed to other tab.
     */
    void changedTabAction(boolean hasFocus);
}
