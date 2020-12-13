package synthesizer.gui.MovablePanelsPackage.Ports;

import synthesizer.gui.MovablePanelsPackage.AddInputPortToGUIIFace;
import synthesizer.gui.MovablePanelsPackage.MovablePanelViewForPort;
import synthesizer.Synth.UnitGeneratedValuesInfo;

public class SustainAmplitudeInputPort extends InputPort {
    public SustainAmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                     int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace,
                                     double neutralValue) {
        super(u, panelWhichContainsPort, "S_A", "Sustain amplitude",
                connectorIndex, addInputPortToGUIIFace,
                "This port controls the amplitude which the envelope has during sustain phase",
                neutralValue);
    }


    public SustainAmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                    int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace) {
        this(u, panelWhichContainsPort, connectorIndex, addInputPortToGUIIFace,0.8);
    }
}
