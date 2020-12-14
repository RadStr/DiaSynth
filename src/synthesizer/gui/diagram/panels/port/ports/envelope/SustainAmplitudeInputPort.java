package synthesizer.gui.diagram.panels.port.ports.envelope;

import synthesizer.gui.diagram.ifaces.InputPortToGUIAdderIFace;
import synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class SustainAmplitudeInputPort extends InputPort {
    public SustainAmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                     int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdderIFace,
                                     double neutralValue) {
        super(u, panelWhichContainsPort, "S_A", "Sustain amplitude",
                connectorIndex, inputPortToGUIAdderIFace,
                "This port controls the amplitude which the envelope has during sustain phase",
                neutralValue);
    }


    public SustainAmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                    int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdderIFace) {
        this(u, panelWhichContainsPort, connectorIndex, inputPortToGUIAdderIFace,0.8);
    }
}
