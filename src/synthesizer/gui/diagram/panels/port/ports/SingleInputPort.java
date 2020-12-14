package synthesizer.gui.diagram.panels.port.ports;

import synthesizer.gui.diagram.ifaces.AddInputPortToGUIIFace;
import synthesizer.gui.diagram.MovablePanelViewForPort;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class SingleInputPort extends InputPort {
    public SingleInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                           AddInputPortToGUIIFace addInputPortToGUIIFace, double neutralValue) {
        super(u, panelWhichContainsPort, "INPUT", "INPUT PORT", 0,
                addInputPortToGUIIFace, "Input port for the panel.", neutralValue);
    }


    public SingleInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                           AddInputPortToGUIIFace addInputPortToGUIIFace) {
        this(u, panelWhichContainsPort, addInputPortToGUIIFace, 1);
    }
}
