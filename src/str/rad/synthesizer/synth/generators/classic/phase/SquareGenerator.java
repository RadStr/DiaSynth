package str.rad.synthesizer.synth.generators.classic.phase;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.synth.Unit;

public class SquareGenerator extends SineGenerator {
    public SquareGenerator(Unit u) {
        super(u);
    }

    public SquareGenerator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    public double generateSampleConst(double timeInSecs, int diagramFrequency, double amp,
                                      double freq, double phase) {
        double genVal = super.generateSampleConst(timeInSecs, diagramFrequency, amp, freq, phase);
        if (genVal > 0) {
            genVal = amp;
        }
        else {
            genVal = -amp;
        }

        return genVal;
    }

    @Override
    public String getDefaultPanelName() {
        return "SQUARE";
    }

    @Override
    public String getTooltip() {
        return "Generates square wave";
    }
}
