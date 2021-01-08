package str.rad.synthesizer.gui.diagram.panels.port.ports.envelope;

import str.rad.synthesizer.gui.diagram.ifaces.InputPortToGUIAdderIFace;
import str.rad.synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.synth.UnitGeneratedValuesInfo;

public class SustainTimeInputPort extends InputPort {
    public SustainTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder, double neutralValue) {
        super(u, panelWhichContainsPort, "S_T", "Sustain time", connectorIndex, inputPortToGUIAdder,
              "This port controls the length of the sustain phase of envelope (length is in seconds)",
              neutralValue);
    }

    public SustainTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder) {
        this(u, panelWhichContainsPort, connectorIndex, inputPortToGUIAdder, 1);
    }
}
