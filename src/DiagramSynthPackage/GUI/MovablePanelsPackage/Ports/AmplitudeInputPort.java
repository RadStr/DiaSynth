package DiagramSynthPackage.GUI.MovablePanelsPackage.Ports;

import DiagramSynthPackage.GUI.MovablePanelsPackage.AddInputPortToGUIIFace;
import DiagramSynthPackage.GUI.MovablePanelsPackage.MovablePanelViewForPort;
import DiagramSynthPackage.Synth.Unit;
import DiagramSynthPackage.Synth.UnitGeneratedValuesInfo;

public class AmplitudeInputPort extends InputPort {
    public AmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                              int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace, double neutralValue) {
        super(u, panelWhichContainsPort, "AMP", "AMPLITUDE", connectorIndex, addInputPortToGUIIFace,
                "This port controls the amplitude of generated wave.", neutralValue);
    }

    public AmplitudeInputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                              int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace) {
        this(u, panelWhichContainsPort, connectorIndex, addInputPortToGUIIFace, 0.2);
    }
}
