package str.rad.synthesizer.synth.generators.classic.nophase;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.synth.generators.classic.phase.SquareGenerator;
import str.rad.synthesizer.synth.generators.GeneratorNoPhase;
import str.rad.synthesizer.synth.Unit;

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
