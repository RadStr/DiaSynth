package str.rad.synthesizer.gui.diagram.panels.port.ports.envelope;

import str.rad.synthesizer.gui.diagram.ifaces.InputPortToGUIAdderIFace;
import str.rad.synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.synth.UnitGeneratedValuesInfo;

public class SustainAmplitudeInputPort extends InputPort {
    public SustainAmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                     int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder,
                                     double neutralValue) {
        super(u, panelWhichContainsPort, "S_A", "Sustain amplitude",
              connectorIndex, inputPortToGUIAdder,
              "This port controls the amplitude which the envelope has during sustain phase",
              neutralValue);
    }


    public SustainAmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                                     int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder) {
        this(u, panelWhichContainsPort, connectorIndex, inputPortToGUIAdder, 0.8);
    }
}
