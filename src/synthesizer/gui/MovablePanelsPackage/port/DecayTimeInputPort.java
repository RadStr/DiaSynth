package synthesizer.gui.MovablePanelsPackage.port;

import synthesizer.gui.MovablePanelsPackage.AddInputPortToGUIIFace;
import synthesizer.gui.MovablePanelsPackage.MovablePanelViewForPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class DecayTimeInputPort extends InputPort {
    public DecayTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                              int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace, double neutralValue) {
        super(u, panelWhichContainsPort, "D_T", "Decay time", connectorIndex, addInputPortToGUIIFace,
                "This port controls the length of the decay phase of envelope (length is in seconds)",
                neutralValue);
    }


    public DecayTimeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                               int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace) {
        this(u, panelWhichContainsPort, connectorIndex, addInputPortToGUIIFace,0.5);
    }
}
