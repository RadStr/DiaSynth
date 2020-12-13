package synthesizer.GUI.MovablePanelsPackage.Ports;

import synthesizer.GUI.MovablePanelsPackage.AddInputPortToGUIIFace;
import synthesizer.GUI.MovablePanelsPackage.MovablePanelViewForPort;
import synthesizer.Synth.UnitGeneratedValuesInfo;

public class AttackTimeInputPort extends InputPort {
    public AttackTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                               int connectorIndex,
                               AddInputPortToGUIIFace addInputPortToGUIIFace, double neutralValue) {
        super(u, panelWhichContainsPort, "A_T", "Attack time", connectorIndex, addInputPortToGUIIFace,
                "This port controls the length of the attack phase of envelope (length is in seconds)",
                neutralValue);
    }

    public AttackTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                               int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace) {
        this(u, panelWhichContainsPort, connectorIndex, addInputPortToGUIIFace,1);
    }
}