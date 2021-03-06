package str.rad.synthesizer.synth.generators.classic.phase;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.synth.Unit;

/**
 * https://en.wikipedia.org/wiki/Triangle_wave
 */
public class TriangleGenerator extends SineGenerator {
    public TriangleGenerator(Unit u) {
        super(u);
    }

    public TriangleGenerator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    public double generateSampleConst(double timeInSecs, int diagramFrequency, double amp,
                                      double freq, double phase) {
// Another implementation
//        double genVal;
//        double time = timeInSamples / (double)diagramFrequency;
//        double currRad = time * freq + phase / (2 * Math.PI);
//        double floor = Math.floor(currRad + 1 / 2.0);
//        genVal = amp * 2 * Math.abs(2 * (currRad - floor)) - 1;
//        return genVal;

        double genVal = super.generateSampleConst(timeInSecs, diagramFrequency, 1, freq, phase);
        genVal = amp * 2 / Math.PI * Math.asin(genVal);
        return genVal;
    }


    @Override
    public String getDefaultPanelName() {
        return "TRI";
    }

    @Override
    public String getTooltip() {
        return "Generates triangle wave";
    }
}
