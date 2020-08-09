package DiagramSynthPackage.Synth.Generators.ClassicGenerators.NoPhase;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.InputPort;
import DiagramSynthPackage.Synth.Generators.ClassicGenerators.Phase.SawtoothGeneratorWithPhase;
import DiagramSynthPackage.Synth.Generators.Generator;
import DiagramSynthPackage.Synth.Generators.GeneratorWithPhase;
import DiagramSynthPackage.Synth.Unit;

// https://en.wikipedia.org/wiki/Triangle_wave
public class SawtoothGenerator extends SawtoothGeneratorWithPhase {
    public SawtoothGenerator(Unit u) {
        super(u);
    }

    public SawtoothGenerator(JPanelWithMovableJPanels panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected InputPort[] createInputPorts(JPanelWithMovableJPanels panelWithUnits, double[] neutralValues) {
        return Generator.createInputPorts(this, panelWithUnits, neutralValues);
    }
}
