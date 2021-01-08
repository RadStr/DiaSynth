package str.rad.synthesizer.gui.diagram.panels.ifaces;

public interface LockUpdateIFace extends GetTopLeftIFace {
    void setLocation(int x, int y);

    void correctPositionBasedOnRefPosition();

    void resetToStatePositionBeforeDragging();
}
