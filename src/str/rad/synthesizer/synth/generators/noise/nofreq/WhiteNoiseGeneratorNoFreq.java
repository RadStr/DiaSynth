package str.rad.synthesizer.synth.generators.noise.nofreq;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.diagram.panels.port.ports.AmplitudeInputPort;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.synth.generators.noise.freq.WhiteNoiseGenerator;
import str.rad.synthesizer.synth.Unit;

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
        if (neutralValues != null && neutralValues.length >= inputPorts.length) {
            inputPorts[0] = new AmplitudeInputPort(this, shapedPanel, 0, panelWithUnits, neutralValues[0]);
        }
        else {
            inputPorts[0] = new AmplitudeInputPort(this, shapedPanel, 0, panelWithUnits);
        }
        return inputPorts;
    }
}
