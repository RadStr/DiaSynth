package str.rad.synthesizer.gui.diagram.panels.port.ports;

import str.rad.synthesizer.gui.diagram.ifaces.InputPortToGUIAdderIFace;
import str.rad.synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.synth.UnitGeneratedValuesInfo;

public class SingleInputPort extends InputPort {
    public SingleInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                           InputPortToGUIAdderIFace inputPortToGUIAdder, double neutralValue) {
        super(u, panelWhichContainsPort, "INPUT", "INPUT PORT", 0,
              inputPortToGUIAdder, "Input port for the panel.", neutralValue);
    }


    public SingleInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                           InputPortToGUIAdderIFace inputPortToGUIAdder) {
        this(u, panelWhichContainsPort, inputPortToGUIAdder, 1);
    }
}
