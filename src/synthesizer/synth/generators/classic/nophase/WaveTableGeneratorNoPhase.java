package synthesizer.synth.generators.classic.nophase;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.synth.generators.classic.phase.WaveTableGenerator;
import synthesizer.synth.generators.GeneratorNoPhase;
import synthesizer.synth.Unit;

public class WaveTableGeneratorNoPhase extends WaveTableGenerator {
    public WaveTableGeneratorNoPhase(Unit u) {
        super(u);
    }

    public WaveTableGeneratorNoPhase(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        return GeneratorNoPhase.createInputPorts(this, panelWithUnits, neutralValues);
    }
}
