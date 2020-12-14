package synthesizer.gui.diagram.panels.port.ports;

import synthesizer.gui.diagram.ifaces.AddInputPortToGUIIFace;
import synthesizer.gui.diagram.MovablePanelViewForPort;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class OperatorInputPort extends InputPort {
    public OperatorInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                             int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace, double neutralValue) {
        super(u, panelWhichContainsPort, "OP" + connectorIndex, "Operand " + connectorIndex,
                connectorIndex, addInputPortToGUIIFace,
                "This port gets values for operand number " + connectorIndex,
                neutralValue);
    }

    public OperatorInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                             int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace) {
        this(u, panelWhichContainsPort, connectorIndex, addInputPortToGUIIFace, 1);
    }
}
