package synthesizer.Synth.Generators.ClassicGenerators.NoPhase;

import synthesizer.GUI.MovablePanelsPackage.DiagramPanel;
import synthesizer.GUI.MovablePanelsPackage.Ports.InputPort;
import synthesizer.Synth.Generators.ClassicGenerators.Phase.SquareGeneratorWithPhase;
import synthesizer.Synth.Generators.Generator;
import synthesizer.Synth.Unit;

public class SquareGenerator extends SquareGeneratorWithPhase {
    public SquareGenerator(Unit u) {
        super(u);
    }
    public SquareGenerator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        return Generator.createInputPorts(this, panelWithUnits, neutralValues);
    }
}
