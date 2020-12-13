package synthesizer.Synth.Generators.ClassicGenerators.NoPhase;

import synthesizer.GUI.MovablePanelsPackage.DiagramPanel;
import synthesizer.GUI.MovablePanelsPackage.Ports.InputPort;
import synthesizer.Synth.Generators.ClassicGenerators.Phase.WaveTableGeneratorWithPhase;
import synthesizer.Synth.Generators.Generator;
import synthesizer.Synth.Unit;

public class WaveTableGenerator extends WaveTableGeneratorWithPhase {
    public WaveTableGenerator(Unit u) {
        super(u);
    }

    public WaveTableGenerator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        return Generator.createInputPorts(this, panelWithUnits, neutralValues);
    }
}
