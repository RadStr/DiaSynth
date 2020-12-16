package synthesizer.synth.plugin.test;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.synth.Unit;
import synthesizer.synth.generators.noise.NoiseGenerator;

public class TestSynthPlugin extends NoiseGenerator {
    public TestSynthPlugin(Unit u) {
        super(u);
    }

    public TestSynthPlugin(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    /**
     * Generates noise between 0 and 1.
     *
     * @return
     */
    @Override
    public double generateNoise() {
        return Math.random();
    }

    @Override
    public String getDefaultPanelName() {
        return "TEST";
    }

    @Override
    public String getTooltip() {
        return "This is test plugin that generates random samples between 0 and 1.";
    }

    /**
     * The method copies the state of the given parameter to the instance on which the method was called. State is
     * everything which is needed for calculation except the fields already defined in the Unit.
     *
     * @param copySource contains the content that we should copy.
     */
    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}
