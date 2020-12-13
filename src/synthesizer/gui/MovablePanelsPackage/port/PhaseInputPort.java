package synthesizer.gui.MovablePanelsPackage.port;

import synthesizer.gui.MovablePanelsPackage.AddInputPortToGUIIFace;
import synthesizer.gui.MovablePanelsPackage.MovablePanelViewForPort;
import synthesizer.Synth.UnitGeneratedValuesInfo;

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
