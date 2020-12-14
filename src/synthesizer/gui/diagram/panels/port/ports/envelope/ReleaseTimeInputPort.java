package synthesizer.gui.diagram.panels.port.ports.envelope;

import synthesizer.gui.diagram.ifaces.InputPortToGUIAdderIFace;
import synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class ReleaseTimeInputPort extends InputPort {
    public ReleaseTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdderIFace, double neutralValue) {
        super(u, panelWhichContainsPort, "R_T", "Release time", connectorIndex, inputPortToGUIAdderIFace,
                "This port controls the length of the release phase of envelope (length is in seconds)",
                neutralValue);
    }

    public ReleaseTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                               int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdderIFace) {
        this(u, panelWhichContainsPort, connectorIndex, inputPortToGUIAdderIFace, 1);
    }
}
