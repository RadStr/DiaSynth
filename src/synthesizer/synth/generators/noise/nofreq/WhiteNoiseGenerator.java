package synthesizer.synth.generators.noise.nofreq;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.port.AmplitudeInputPort;
import synthesizer.gui.diagram.port.InputPort;
import synthesizer.synth.generators.noise.freq.WhiteNoiseGeneratorWithFrequency;
import synthesizer.synth.Unit;

public class WhiteNoiseGenerator extends WhiteNoiseGeneratorWithFrequency {
    public WhiteNoiseGenerator(Unit u) {
        super(u);
    }
    public WhiteNoiseGenerator(DiagramPanel panelWithUnits) {
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
