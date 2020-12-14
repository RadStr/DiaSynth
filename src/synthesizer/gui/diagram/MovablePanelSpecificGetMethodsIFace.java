package synthesizer.gui.diagram;

import synthesizer.gui.diagram.panels.ifaces.PortsGetterIFace;

import java.awt.*;

public interface MovablePanelSpecificGetMethodsIFace extends PortsGetterIFace, ShapeSpecificGetMethodsIFace {
    boolean getIsBeingMoved();
    Point getRelativePosToReferencePanel();
    Point getLocation();
    Dimension getSize();

    DiagramPanel getDiagramPanel();
}
