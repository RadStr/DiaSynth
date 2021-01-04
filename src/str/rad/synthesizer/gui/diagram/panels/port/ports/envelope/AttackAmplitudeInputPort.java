package str.rad.synthesizer.gui.diagram.panels.port.ports.envelope;

import str.rad.synthesizer.gui.diagram.ifaces.InputPortToGUIAdderIFace;
import str.rad.synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.synth.UnitGeneratedValuesInfo;

public class AttackAmplitudeInputPort extends InputPort {
    public AttackAmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                    int connectorIndex,
                                    InputPortToGUIAdderIFace inputPortToGUIAdder, double neutralValue) {
        super(u, panelWhichContainsPort, "A_A", "Attack amplitude", connectorIndex, inputPortToGUIAdder,
              "This port controls the amplitude which is reached at the end of attack phase",
              neutralValue);
    }

    public AttackAmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                    int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder) {
        this(u, panelWhichContainsPort, connectorIndex, inputPortToGUIAdder, 1);
    }
}
