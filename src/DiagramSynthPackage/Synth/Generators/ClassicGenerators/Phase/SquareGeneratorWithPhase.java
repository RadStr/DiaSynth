package DiagramSynthPackage.Synth.Generators.ClassicGenerators.Phase;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.Synth.Unit;

public class SquareGeneratorWithPhase extends SineGeneratorWithPhase {
    public SquareGeneratorWithPhase(Unit u) {
        super(u);
    }

    public SquareGeneratorWithPhase(JPanelWithMovableJPanels panelWithUnits) {
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
