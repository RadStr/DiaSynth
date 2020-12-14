package synthesizer.gui.diagram.panels.port.ports;

import synthesizer.gui.diagram.ifaces.AddInputPortToGUIIFace;
import synthesizer.gui.diagram.MovablePanelViewForPort;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class FrequencyInputPort extends InputPort {
    public FrequencyInputPort(UnitGeneratedValuesInfo u,
                              MovablePanelViewForPort panelWhichContainsPort, int connectorIndex,
                              AddInputPortToGUIIFace addInputPortToGUIIFace, double neutralValue) {
        super(u, panelWhichContainsPort, "FRQ", "FREQUENCY",
                connectorIndex, addInputPortToGUIIFace,
                "This port controls the frequency of the generated wave", neutralValue);
    }


    public FrequencyInputPort(UnitGeneratedValuesInfo u,
                              MovablePanelViewForPort panelWhichContainsPort,
                              int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace) {
        this(u, panelWhichContainsPort, connectorIndex, addInputPortToGUIIFace,500);
    }
}
