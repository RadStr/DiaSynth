package synthesizer.gui.diagram;

public interface LockUpdateIFace extends GetTopLeftIFace {
    void setLocation(int x, int y);
    void correctPositionBasedOnRefPosition();
    void resetToStatePositionBeforeDragging();
}
