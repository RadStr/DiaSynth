package str.rad.synthesizer.synth.generators.classic.nophase;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.synth.generators.classic.phase.SawtoothGenerator;
import str.rad.synthesizer.synth.generators.GeneratorNoPhase;
import str.rad.synthesizer.synth.Unit;

// https://en.wikipedia.org/wiki/Triangle_wave
public class SawtoothGeneratorNoPhase extends SawtoothGenerator {
    public SawtoothGeneratorNoPhase(Unit u) {
        super(u);
    }

    public SawtoothGeneratorNoPhase(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        return GeneratorNoPhase.createInputPorts(this, panelWithUnits, neutralValues);
    }
}
