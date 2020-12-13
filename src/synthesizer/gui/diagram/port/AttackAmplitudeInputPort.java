package synthesizer.gui.diagram.port;

import synthesizer.gui.diagram.AddInputPortToGUIIFace;
import synthesizer.gui.diagram.MovablePanelViewForPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class AttackAmplitudeInputPort extends InputPort {
    public AttackAmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                    int connectorIndex,
                                    AddInputPortToGUIIFace addInputPortToGUIIFace, double neutralValue) {
        super(u, panelWhichContainsPort, "A_A", "Attack amplitude", connectorIndex, addInputPortToGUIIFace,
                "This port controls the amplitude which is reached at the end of attack phase",
                neutralValue);
    }

    public AttackAmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace) {
        this(u, panelWhichContainsPort, connectorIndex, addInputPortToGUIIFace, 1);
    }
}
