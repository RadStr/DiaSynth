package synthesizer.gui.diagram.panels.ifaces;

import java.awt.*;

public interface MovablePanelSpecificMethodsIFace extends MovablePanelSpecificGetMethodsIFace, MovablePanelControlMethodsIFace {
    void setLocation(Point loc);

    void setLocation(int x, int y);

    /**
     * This method is called, when scrolling, also when zooming, but zooming includes scrolling (for zooming to cursor).
     */
    void mouseLocationChangedWithoutMouseMovement();

    void noteAdditionToDiagram();
}