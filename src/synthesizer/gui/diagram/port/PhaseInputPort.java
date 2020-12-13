package synthesizer.gui.diagram.port;

import synthesizer.gui.diagram.AddInputPortToGUIIFace;
import synthesizer.gui.diagram.MovablePanelViewForPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class PhaseInputPort extends InputPort {
    public PhaseInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                          int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace, double neutralValue) {
        super(u, panelWhichContainsPort, "PH", "PHASE", connectorIndex, addInputPortToGUIIFace,
                "This port controls the phase of generated wave. (In degrees ... 180° == PI)", neutralValue);
    }

    public PhaseInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                              int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace) {
        this(u, panelWhichContainsPort, connectorIndex, addInputPortToGUIIFace, 0);
    }
}
