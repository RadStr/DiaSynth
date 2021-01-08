package str.rad.synthesizer.gui.diagram.panels.port.ports;

import str.rad.synthesizer.gui.diagram.ifaces.InputPortToGUIAdderIFace;
import str.rad.synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.synth.UnitGeneratedValuesInfo;

public class NoiseFrequencyInputPort extends InputPort {
    public NoiseFrequencyInputPort(UnitGeneratedValuesInfo u,
                                   MovablePanelViewForPort panelWhichContainsPort,
                                   int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder,
                                   double neutralValue) {
        super(u, panelWhichContainsPort, "FRQ", "FREQUENCY",
              connectorIndex, inputPortToGUIAdder,
              "<html>This port controls the frequency of the generated noise.<br> " +
              "For example when sample rate is 1000Hz, this port gets 10 on input<br>." +
              "That means the noise generator will generate new value every 100 samples.</html>",
              neutralValue);
    }


    public NoiseFrequencyInputPort(UnitGeneratedValuesInfo u,
                                   MovablePanelViewForPort panelWhichContainsPort,
                                   int connectorIndex, InputPortToGUIAdderIFace inputPortToGUIAdder) {
        this(u, panelWhichContainsPort, connectorIndex, inputPortToGUIAdder, 8000);
    }
}
