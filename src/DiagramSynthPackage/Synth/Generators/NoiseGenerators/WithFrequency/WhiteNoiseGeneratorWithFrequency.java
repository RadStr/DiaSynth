package DiagramSynthPackage.Synth.Generators.NoiseGenerators.WithFrequency;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.Synth.Generators.NoiseGenerators.NoiseGeneratorWithFrequency;
import DiagramSynthPackage.Synth.Unit;

import java.util.Random;

public class WhiteNoiseGeneratorWithFrequency extends NoiseGeneratorWithFrequency {
    public WhiteNoiseGeneratorWithFrequency(Unit u) {
        super(u);
    }

    public WhiteNoiseGeneratorWithFrequency(JPanelWithMovableJPanels panelWithUnits) {
        super(panelWithUnits);
    }

    private Random r = new Random();
    @Override
    public double generateNoise() {
        return r.nextDouble();
    }

    @Override
    public String getDefaultPanelName() {
        return "WHITE";
    }

    @Override
    public String getTooltip() {
        return "This generator generates white noise";
    }
}
