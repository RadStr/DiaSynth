package DiagramSynthPackage.GUI.MovablePanelsPackage;

import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.InputPort;
import Rocnikovy_Projekt.Direction;

import java.awt.*;

public interface MovablePanelSpecificGetMethodsIFace extends PortsGetterIFace, ShapeSpecificGetMethodsIFace {
    boolean getIsBeingMoved();
    Point getRelativePosToReferencePanel();
    Point getLocation();
    Dimension getSize();

    DiagramPanel getDiagramPanel();
}
