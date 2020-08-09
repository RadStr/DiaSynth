package DiagramSynthPackage.Synth.Generators.ClassicGenerators.NoPhase;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.InputPort;
import DiagramSynthPackage.Synth.Generators.ClassicGenerators.Phase.SineGeneratorWithPhase;
import DiagramSynthPackage.Synth.Generators.ClassicGenerators.Phase.SquareGeneratorWithPhase;
import DiagramSynthPackage.Synth.Generators.Generator;
import DiagramSynthPackage.Synth.Unit;

public class SquareGenerator extends SquareGeneratorWithPhase {
    public SquareGenerator(Unit u) {
        super(u);
    }
    public SquareGenerator(JPanelWithMovableJPanels panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected InputPort[] createInputPorts(JPanelWithMovableJPanels panelWithUnits, double[] neutralValues) {
        return Generator.createInputPorts(this, panelWithUnits, neutralValues);
    }
}
