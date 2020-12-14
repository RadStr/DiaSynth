package synthesizer.gui.diagram.panels.port.ports.envelope;

import synthesizer.gui.diagram.ifaces.InputPortToGUIAdderIFace;
import synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class AttackAmplitudeInputPort extends InputPort {
    public AttackAmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                    int connectorIndex,
                                    InputPortToGUIAdderIFace inputPortToGUIAdderIFace, double neutralValue) {
        super(u, panelWhichContainsPort, "A_A", "Attack amplitude", connectorIndex, inputPortToGUIAdderIFace,
                "This port controls the amplitude which is reached at the end of attack phase",
                neutralValue);
    }

    public AttackAmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdderIFace) {
        this(u, panelWhichContainsPort, connectorIndex, inputPortToGUIAdderIFace, 1);
    }
}
