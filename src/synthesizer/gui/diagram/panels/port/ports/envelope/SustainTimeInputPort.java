package synthesizer.gui.diagram.panels.port.ports.envelope;

import synthesizer.gui.diagram.ifaces.InputPortToGUIAdderIFace;
import synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class SustainTimeInputPort extends InputPort {
    public SustainTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdderIFace, double neutralValue) {
        super(u, panelWhichContainsPort, "S_T", "Sustain time", connectorIndex, inputPortToGUIAdderIFace,
                "This port controls the length of the sustain phase of envelope (length is in seconds)",
                neutralValue);
    }

    public SustainTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdderIFace) {
        this(u, panelWhichContainsPort, connectorIndex, inputPortToGUIAdderIFace, 1);
    }
}
