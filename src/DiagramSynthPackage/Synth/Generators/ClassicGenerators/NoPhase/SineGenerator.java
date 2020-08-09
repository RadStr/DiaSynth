package DiagramSynthPackage.Synth.Generators.ClassicGenerators.NoPhase;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.InputPort;
import DiagramSynthPackage.Synth.Generators.ClassicGenerators.Phase.SineGeneratorWithPhase;
import DiagramSynthPackage.Synth.Generators.Generator;
import DiagramSynthPackage.Synth.Generators.GeneratorWithPhase;
import DiagramSynthPackage.Synth.Unit;

public class SineGenerator extends SineGeneratorWithPhase {
    public SineGenerator(Unit u) { super(u);}
    public SineGenerator(JPanelWithMovableJPanels panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected InputPort[] createInputPorts(JPanelWithMovableJPanels panelWithUnits, double[] neutralValues) {
        return Generator.createInputPorts(this, panelWithUnits, neutralValues);
    }
}