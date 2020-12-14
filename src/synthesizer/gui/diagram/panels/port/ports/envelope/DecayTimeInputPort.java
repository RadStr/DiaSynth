package synthesizer.gui.diagram.panels.port.ports.envelope;

import synthesizer.gui.diagram.ifaces.InputPortToGUIAdderIFace;
import synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class DecayTimeInputPort extends InputPort {
    public DecayTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                              int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder, double neutralValue) {
        super(u, panelWhichContainsPort, "D_T", "Decay time", connectorIndex, inputPortToGUIAdder,
                "This port controls the length of the decay phase of envelope (length is in seconds)",
                neutralValue);
    }


    public DecayTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                               int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder) {
        this(u, panelWhichContainsPort, connectorIndex, inputPortToGUIAdder,0.5);
    }
}
