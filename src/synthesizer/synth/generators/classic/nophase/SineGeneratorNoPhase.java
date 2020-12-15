package synthesizer.synth.generators.classic.nophase;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.generators.classic.phase.SineGeneratorWithPhase;
import synthesizer.synth.generators.GeneratorNoPhase;
import synthesizer.synth.Unit;

public class SineGeneratorNoPhase extends SineGeneratorWithPhase {
    public SineGeneratorNoPhase(Unit u) { super(u);}
    public SineGeneratorNoPhase(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        return GeneratorNoPhase.createInputPorts(this, panelWithUnits, neutralValues);
    }
}