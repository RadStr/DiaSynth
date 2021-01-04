package str.rad.synthesizer.gui.diagram.panels.ifaces;

import str.rad.synthesizer.gui.diagram.DiagramPanel;

import java.awt.*;

public interface MovablePanelSpecificGetMethodsIFace extends PortsGetterIFace, ShapeSpecificGetMethodsIFace {
    boolean getIsBeingMoved();

    Point getRelativePosToReferencePanel();

    Point getLocation();

    Dimension getSize();

    DiagramPanel getDiagramPanel();
}
