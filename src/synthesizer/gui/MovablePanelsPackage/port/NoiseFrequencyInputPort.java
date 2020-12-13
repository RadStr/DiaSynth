package synthesizer.gui.MovablePanelsPackage.port;

import synthesizer.gui.MovablePanelsPackage.AddInputPortToGUIIFace;
import synthesizer.gui.MovablePanelsPackage.MovablePanelViewForPort;
import synthesizer.synth.UnitGeneratedValuesInfo;

public class NoiseFrequencyInputPort extends InputPort {
    public NoiseFrequencyInputPort(UnitGeneratedValuesInfo u,
                              MovablePanelViewForPort panelWhichContainsPort,
                              int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace,
                              double neutralValue) {
        super(u, panelWhichContainsPort, "FRQ", "FREQUENCY",
                connectorIndex, addInputPortToGUIIFace,
                "<html>This port controls the frequency of the generated noise.<br> " +
                        "For example when sample rate is 1000Hz, this port gets 10 on input<br>." +
                        "That means the noise generator will generate new value every 100 samples.</html>",
                neutralValue);
    }


    public NoiseFrequencyInputPort(UnitGeneratedValuesInfo u,
                                   MovablePanelViewForPort panelWhichContainsPort,
                                   int connectorIndex, AddInputPortToGUIIFace addInputPortToGUIIFace) {
        this(u, panelWhichContainsPort, connectorIndex, addInputPortToGUIIFace, 8000);
    }
}
