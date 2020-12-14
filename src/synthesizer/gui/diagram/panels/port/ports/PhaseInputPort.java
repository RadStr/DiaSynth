package synthesizer.gui.diagram.panels.port.ports;

import synthesizer.gui.diagram.ifaces.InputPortToGUIAdderIFace;
import synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class PhaseInputPort extends InputPort {
    public PhaseInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                          int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder, double neutralValue) {
        super(u, panelWhichContainsPort, "PH", "PHASE", connectorIndex, inputPortToGUIAdder,
                "This port controls the phase of generated wave. (In degrees ... 180° == PI)", neutralValue);
    }

    public PhaseInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                              int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder) {
        this(u, panelWhichContainsPort, connectorIndex, inputPortToGUIAdder, 0);
    }
}
