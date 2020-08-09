package DiagramSynthPackage.GUI.MovablePanelsPackage.Ports;

import DiagramSynthPackage.GUI.MovablePanelsPackage.AddInputPortToGUIIFace;
import DiagramSynthPackage.GUI.MovablePanelsPackage.MovablePanelViewForPort;
import DiagramSynthPackage.Synth.Unit;
import DiagramSynthPackage.Synth.UnitGeneratedValuesInfo;

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
