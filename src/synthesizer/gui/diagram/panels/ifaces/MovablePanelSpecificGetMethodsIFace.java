package synthesizer.gui.diagram.panels.ifaces;

import synthesizer.gui.diagram.DiagramPanel;

import java.awt.*;

public interface MovablePanelSpecificGetMethodsIFace extends PortsGetterIFace, ShapeSpecificGetMethodsIFace {
    boolean getIsBeingMoved();

    Point getRelativePosToReferencePanel();

    Point getLocation();

    Dimension getSize();

    DiagramPanel getDiagramPanel();
}
