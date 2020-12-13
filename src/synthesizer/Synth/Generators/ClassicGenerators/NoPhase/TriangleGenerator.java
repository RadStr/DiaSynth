package synthesizer.Synth.Generators.ClassicGenerators.NoPhase;

import synthesizer.gui.MovablePanelsPackage.DiagramPanel;
import synthesizer.gui.MovablePanelsPackage.Ports.InputPort;
import synthesizer.Synth.Generators.ClassicGenerators.Phase.TriangleGeneratorWithPhase;
import synthesizer.Synth.Generators.Generator;
import synthesizer.Synth.Unit;

/**
 * https://en.wikipedia.org/wiki/Triangle_wave
 */
public class TriangleGenerator extends TriangleGeneratorWithPhase {
    public TriangleGenerator(Unit u) {
        super(u);
    }

    public TriangleGenerator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        return Generator.createInputPorts(this, panelWithUnits, neutralValues);
    }
}
