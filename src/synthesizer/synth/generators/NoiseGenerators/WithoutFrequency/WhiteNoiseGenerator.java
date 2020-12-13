package synthesizer.synth.generators.NoiseGenerators.WithoutFrequency;

import synthesizer.gui.MovablePanelsPackage.DiagramPanel;
import synthesizer.gui.MovablePanelsPackage.port.AmplitudeInputPort;
import synthesizer.gui.MovablePanelsPackage.port.InputPort;
import synthesizer.synth.generators.NoiseGenerators.WithFrequency.WhiteNoiseGeneratorWithFrequency;
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
