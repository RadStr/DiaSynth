package synthesizer.gui.diagram.port;

import synthesizer.gui.diagram.AddInputPortToGUIIFace;
import synthesizer.gui.diagram.MovablePanelViewForPort;
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
