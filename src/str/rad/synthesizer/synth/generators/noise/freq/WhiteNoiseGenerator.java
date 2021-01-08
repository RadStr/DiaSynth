package str.rad.synthesizer.synth.generators.noise.freq;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.synth.generators.noise.NoiseGenerator;
import str.rad.synthesizer.synth.Unit;

import java.util.Random;

public class WhiteNoiseGenerator extends NoiseGenerator {
    public WhiteNoiseGenerator(Unit u) {
        super(u);
    }

    public WhiteNoiseGenerator(DiagramPanel panelWithUnits) {
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
