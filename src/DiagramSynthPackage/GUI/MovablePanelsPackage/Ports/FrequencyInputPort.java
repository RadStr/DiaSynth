package DiagramSynthPackage.GUI.MovablePanelsPackage.Ports;

import DiagramSynthPackage.GUI.MovablePanelsPackage.AddInputPortToGUIIFace;
import DiagramSynthPackage.GUI.MovablePanelsPackage.MovablePanelViewForPort;
import DiagramSynthPackage.Synth.Unit;
import DiagramSynthPackage.Synth.UnitGeneratedValuesInfo;

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
