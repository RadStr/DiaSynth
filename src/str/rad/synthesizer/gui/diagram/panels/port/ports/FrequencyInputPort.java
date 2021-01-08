package str.rad.synthesizer.gui.diagram.panels.port.ports;

import str.rad.synthesizer.gui.diagram.ifaces.InputPortToGUIAdderIFace;
import str.rad.synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.synth.UnitGeneratedValuesInfo;

public class FrequencyInputPort extends InputPort {
    public FrequencyInputPort(UnitGeneratedValuesInfo u,
                              MovablePanelViewForPort panelWhichContainsPort, int connectorIndex,
                              InputPortToGUIAdderIFace inputPortToGUIAdder, double neutralValue) {
        super(u, panelWhichContainsPort, "FRQ", "FREQUENCY",
              connectorIndex, inputPortToGUIAdder,
              "This port controls the frequency of the generated wave", neutralValue);
    }


    public FrequencyInputPort(UnitGeneratedValuesInfo u,
                              MovablePanelViewForPort panelWhichContainsPort,
                              int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder) {
        this(u, panelWhichContainsPort, connectorIndex, inputPortToGUIAdder, 500);
    }
}
