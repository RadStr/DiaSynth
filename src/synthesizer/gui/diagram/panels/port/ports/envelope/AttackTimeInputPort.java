package synthesizer.gui.diagram.panels.port.ports.envelope;

import synthesizer.gui.diagram.ifaces.InputPortToGUIAdderIFace;
import synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class AttackTimeInputPort extends InputPort {
    public AttackTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                               int connectorIndex,
                               InputPortToGUIAdderIFace inputPortToGUIAdder, double neutralValue) {
        super(u, panelWhichContainsPort, "A_T", "Attack time", connectorIndex, inputPortToGUIAdder,
                "This port controls the length of the attack phase of envelope (length is in seconds)",
                neutralValue);
    }

    public AttackTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                               int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder) {
        this(u, panelWhichContainsPort, connectorIndex, inputPortToGUIAdder,1);
    }
}