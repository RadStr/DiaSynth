package DiagramSynthPackage.Synth.Generators.NoiseGenerators.WithoutFrequency;

import DiagramSynthPackage.GUI.MovablePanelsPackage.DiagramPanel;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.AmplitudeInputPort;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.InputPort;
import DiagramSynthPackage.Synth.Generators.NoiseGenerators.WithFrequency.WhiteNoiseGeneratorWithFrequency;
import DiagramSynthPackage.Synth.Unit;

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
