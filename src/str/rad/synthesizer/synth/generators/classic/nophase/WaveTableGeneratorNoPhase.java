package str.rad.synthesizer.synth.generators.classic.nophase;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.synth.generators.classic.phase.WaveTableGenerator;
import str.rad.synthesizer.synth.generators.GeneratorNoPhase;
import str.rad.synthesizer.synth.Unit;

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
