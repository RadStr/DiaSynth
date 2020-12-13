package synthesizer.synth.generators.noise.freq;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.synth.generators.noise.NoiseGeneratorWithFrequency;
import synthesizer.synth.Unit;

import java.util.Random;

public class WhiteNoiseGeneratorWithFrequency extends NoiseGeneratorWithFrequency {
    public WhiteNoiseGeneratorWithFrequency(Unit u) {
        super(u);
    }

    public WhiteNoiseGeneratorWithFrequency(DiagramPanel panelWithUnits) {
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

    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}
