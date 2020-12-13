package synthesizer.GUI.MovablePanelsPackage.Ports;

import synthesizer.GUI.MovablePanelsPackage.AddInputPortToGUIIFace;
import synthesizer.GUI.MovablePanelsPackage.MovablePanelViewForPort;
import synthesizer.Synth.UnitGeneratedValuesInfo;

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
