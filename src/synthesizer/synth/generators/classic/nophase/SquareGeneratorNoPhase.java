package synthesizer.synth.generators.classic.nophase;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.generators.classic.phase.SquareGenerator;
import synthesizer.synth.generators.GeneratorNoPhase;
import synthesizer.synth.Unit;

public class SquareGeneratorNoPhase extends SquareGenerator {
    public SquareGeneratorNoPhase(Unit u) {
        super(u);
    }
    public SquareGeneratorNoPhase(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        return GeneratorNoPhase.createInputPorts(this, panelWithUnits, neutralValues);
    }
}
