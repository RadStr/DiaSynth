package synthesizer.synth.generators.classic.phase;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.synth.generators.Generator;
import synthesizer.synth.Unit;

public class SineGenerator extends Generator {
    public SineGenerator(Unit u) { super(u);}
    public SineGenerator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    public String getDefaultPanelName() {
        return "Sine";
    }

    @Override
    public void resetToDefaultState() {
        // EMPTY
    }


    /**
     * Creates array of length len, and fills it with periodCount periods of sine wave.
     * @param phase is in radians
     * @return
     */
    public static double[] createSine(int len, double amp, double periodCount, double phase) {
        double[] sine = new double[len];
        for(int i = 0; i < sine.length; i++) {
            sine[i] = amp * Math.sin(freqToRad(periodCount) * (i / (double)sine.length) + phase);
        }

        return sine;
    }


    /**
     * Creates array of length len, and fills it with sine wave of frequency freq.
     * @param phase is in radians
     * @return
     */
    public static double[] createSine(int len, double amp, double freq, int sampleRate, double phase) {
        double[] sine = new double[len];
        for(int i = 0; i < sine.length; i++) {
            sine[i] = amp * Math.sin(freqToRad(freq) * (i / (double)sampleRate) + phase);
        }

        return sine;
    }


    @Override
    public double generateSampleConst(double timeInSecs, int diagramFrequency, double amp, double freq,
                                      double phase) {
        double genVal;
        genVal = amp * Math.sin(freqToRad(freq) * timeInSecs + phase);
        return genVal;
    }

    @Override
    public String getTooltip() {
        return "Generates sine wave";
    }


    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}