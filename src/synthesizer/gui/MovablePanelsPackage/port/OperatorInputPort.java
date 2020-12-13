package synthesizer.gui.MovablePanelsPackage.port;

import synthesizer.gui.MovablePanelsPackage.AddInputPortToGUIIFace;
import synthesizer.gui.MovablePanelsPackage.MovablePanelViewForPort;
import synthesizer.Synth.UnitGeneratedValuesInfo;

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
