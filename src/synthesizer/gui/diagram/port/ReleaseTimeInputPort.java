package synthesizer.gui.diagram.port;

import synthesizer.gui.diagram.AddInputPortToGUIIFace;
import synthesizer.gui.diagram.MovablePanelViewForPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class ReleaseTimeInputPort extends InputPort {
    public ReleaseTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace, double neutralValue) {
        super(u, panelWhichContainsPort, "R_T", "Release time", connectorIndex, addInputPortToGUIIFace,
                "This port controls the length of the release phase of envelope (length is in seconds)",
                neutralValue);
    }

    public ReleaseTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                               int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace) {
        this(u, panelWhichContainsPort, connectorIndex, addInputPortToGUIIFace, 1);
    }
}
