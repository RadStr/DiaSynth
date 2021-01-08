package str.rad.synthesizer.gui.diagram.panels.ifaces;

import java.awt.*;

public interface MovablePanelIFace extends GetTopLeftIFace, UpdateIFace, MovablePanelSpecificMethodsIFace,
                                           StopConnectingIFace {
    void updateXandYWhenZooming(int difX, int difY);

    void recalculateCablesAbsolutePaths(Point referencePanelLoc, Dimension panelSize, int borderWidth, int borderHeight,
                                        int panelSizeWithBorderWidth, int panelSizeWithBorderHeight, int pixelsPerElevation);
}
