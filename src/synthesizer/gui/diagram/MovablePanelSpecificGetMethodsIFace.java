package synthesizer.gui.diagram;

import java.awt.*;

public interface MovablePanelSpecificGetMethodsIFace extends PortsGetterIFace, ShapeSpecificGetMethodsIFace {
    boolean getIsBeingMoved();
    Point getRelativePosToReferencePanel();
    Point getLocation();
    Dimension getSize();

    DiagramPanel getDiagramPanel();
}
