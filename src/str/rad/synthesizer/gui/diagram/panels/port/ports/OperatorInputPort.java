package str.rad.synthesizer.gui.diagram.panels.port.ports;

import str.rad.synthesizer.gui.diagram.ifaces.InputPortToGUIAdderIFace;
import str.rad.synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.synth.UnitGeneratedValuesInfo;

public class OperatorInputPort extends InputPort {
    public OperatorInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                             int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder, double neutralValue) {
        super(u, panelWhichContainsPort, "OP" + connectorIndex, "Operand " + connectorIndex,
              connectorIndex, inputPortToGUIAdder,
              "This port gets values for operand number " + connectorIndex,
              neutralValue);
    }

    public OperatorInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                             int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder) {
        this(u, panelWhichContainsPort, connectorIndex, inputPortToGUIAdder, 1);
    }
}
