package synthesizer.synth.generators.noise.nofreq;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.port.ports.AmplitudeInputPort;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.generators.noise.freq.WhiteNoiseGenerator;
import synthesizer.synth.Unit;

public class WhiteNoiseGeneratorNoFreq extends WhiteNoiseGenerator {
    public WhiteNoiseGeneratorNoFreq(Unit u) {
        super(u);
    }
    public WhiteNoiseGeneratorNoFreq(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        InputPort[] inputPorts = new InputPort[1];
        if(neutralValues != null && neutralValues.length >= inputPorts.length) {
            inputPorts[0] = new AmplitudeInputPort(this, shapedPanel, 0, panelWithUnits, neutralValues[0]);
        }
        else {
            inputPorts[0] = new AmplitudeInputPort(this, shapedPanel, 0, panelWithUnits);
        }
        return inputPorts;
    }
}
