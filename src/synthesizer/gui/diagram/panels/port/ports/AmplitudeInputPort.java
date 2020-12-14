package synthesizer.gui.diagram.panels.port.ports;

import synthesizer.gui.diagram.ifaces.AddInputPortToGUIIFace;
import synthesizer.gui.diagram.MovablePanelViewForPort;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class AmplitudeInputPort extends InputPort {
    public AmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                              int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace, double neutralValue) {
        super(u, panelWhichContainsPort, "AMP", "AMPLITUDE", connectorIndex, addInputPortToGUIIFace,
                "This port controls the amplitude of generated wave.", neutralValue);
    }

    public AmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                              int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace) {
        this(u, panelWhichContainsPort, connectorIndex, addInputPortToGUIIFace, 0.2);
    }
}
