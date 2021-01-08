package str.rad.synthesizer.synth.generators.classic.nophase;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.synth.generators.classic.phase.TriangleGenerator;
import str.rad.synthesizer.synth.generators.GeneratorNoPhase;
import str.rad.synthesizer.synth.Unit;

/**
 * https://en.wikipedia.org/wiki/Triangle_wave
 */
public class TriangleGeneratorNoPhase extends TriangleGenerator {
    public TriangleGeneratorNoPhase(Unit u) {
        super(u);
    }

    public TriangleGeneratorNoPhase(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        return GeneratorNoPhase.createInputPorts(this, panelWithUnits, neutralValues);
    }
}
