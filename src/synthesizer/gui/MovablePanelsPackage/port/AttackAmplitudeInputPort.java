package synthesizer.gui.MovablePanelsPackage.port;

import synthesizer.gui.MovablePanelsPackage.AddInputPortToGUIIFace;
import synthesizer.gui.MovablePanelsPackage.MovablePanelViewForPort;
import synthesizer.Synth.UnitGeneratedValuesInfo;

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
