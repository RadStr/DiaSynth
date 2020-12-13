package synthesizer.synth.generators.ClassicGenerators.NoPhase;

import synthesizer.gui.MovablePanelsPackage.DiagramPanel;
import synthesizer.gui.MovablePanelsPackage.port.InputPort;
import synthesizer.synth.generators.ClassicGenerators.Phase.SineGeneratorWithPhase;
import synthesizer.synth.generators.Generator;
import synthesizer.synth.Unit;

public class SineGenerator extends SineGeneratorWithPhase {
    public SineGenerator(Unit u) { super(u);}
    public SineGenerator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        return Generator.createInputPorts(this, panelWithUnits, neutralValues);
    }
}