package synthesizer.GUI.MovablePanelsPackage.Ports;

import synthesizer.GUI.MovablePanelsPackage.AddInputPortToGUIIFace;
import synthesizer.GUI.MovablePanelsPackage.MovablePanelViewForPort;
import synthesizer.Synth.UnitGeneratedValuesInfo;

public class SustainTimeInputPort extends InputPort {
    public SustainTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace, double neutralValue) {
        super(u, panelWhichContainsPort, "S_T", "Sustain time", connectorIndex, addInputPortToGUIIFace,
                "This port controls the length of the sustain phase of envelope (length is in seconds)",
                neutralValue);
    }

    public SustainTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace) {
        this(u, panelWhichContainsPort, connectorIndex, addInputPortToGUIIFace, 1);
    }
}
