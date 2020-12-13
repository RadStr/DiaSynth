package synthesizer.synth.generators.classic.nophase;

import synthesizer.gui.MovablePanelsPackage.DiagramPanel;
import synthesizer.gui.MovablePanelsPackage.port.InputPort;
import synthesizer.synth.generators.classic.phase.SawtoothGeneratorWithPhase;
import synthesizer.synth.generators.Generator;
import synthesizer.synth.Unit;

// https://en.wikipedia.org/wiki/Triangle_wave
public class SawtoothGenerator extends SawtoothGeneratorWithPhase {
    public SawtoothGenerator(Unit u) {
        super(u);
    }

    public SawtoothGenerator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        return Generator.createInputPorts(this, panelWithUnits, neutralValues);
    }
}
