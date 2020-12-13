package synthesizer.synth.generators.classic.NoPhase;

import synthesizer.gui.MovablePanelsPackage.DiagramPanel;
import synthesizer.gui.MovablePanelsPackage.port.InputPort;
import synthesizer.synth.generators.classic.phase.SquareGeneratorWithPhase;
import synthesizer.synth.generators.Generator;
import synthesizer.synth.Unit;

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
