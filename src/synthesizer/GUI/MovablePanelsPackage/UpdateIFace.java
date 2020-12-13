package synthesizer.GUI.MovablePanelsPackage;

import java.awt.*;

public interface UpdateIFace {
    void updateSize(Dimension newSize);
    default void updateLocation(int updateXVal, int updateYVal) {
        updateX(updateXVal);
        updateY(updateYVal);
    }
    void updateX(int updateVal);
    void updateY(int updateVal);
}
