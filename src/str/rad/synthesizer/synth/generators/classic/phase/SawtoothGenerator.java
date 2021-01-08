package str.rad.synthesizer.synth.generators.classic.phase;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.synth.generators.Generator;
import str.rad.synthesizer.synth.Unit;

// https://en.wikipedia.org/wiki/Sawtooth_wave
public class SawtoothGenerator extends Generator {
    public SawtoothGenerator(Unit u) {
        super(u);
    }

    public SawtoothGenerator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    // Saw wave can't be reset to 0 just by moving phase, because the function is always rising (unless 2*pi is reached - one full cycle)
    // So it isn't raising only at 1 point ... by moving the phase of 1 saw wave by PI we can half the amplitude of the addition result ...
    // talking about the case when adding 2 saw waves of same frequency
    @Override
    public double generateSampleConst(double timeInSeconds, int diagramFrequency, double amp,
                                      double freq, double phase) {
        double genVal;
        // IMPORTANT NOTE: It isn't explained the best and also maybe incorrect, but I think it is correct,
        // since it makes sense and also works as seen on calculations:
        // phase is divided by (2 * Math.PI) because when working with sine the one cycle is reached at 2 * Math.PI.
        // But for saw it shrank and phase = 1 means 1 cycle, if we used 2 * Math.PI in calculation,
        // so we need to scale it accordingly.
        // We do this phaseScaleFactor = C / (2*Math.PI) where C is the constant which multiplies the
        // frequency in equation to calculate sample. You will probably understand the comment after taking a look at
        // the second variant for calculation of saw wave.
        // (Just reminder that frequency == 1 means we reach 1 cycle at 2 * Math.PI).
        double currRad = timeInSeconds * freq + phase / (2 * Math.PI);
        double floor = Math.floor(currRad + 1 / 2.0);
        genVal = amp * (2 * (currRad - floor));

        // Second variant
        // The phase needs to be / 2 because we are multiplying the frequency by Math.PI instead of 2 * Math.PI
        //genVal = - 2 * amp / Math.PI * Math.atan(calculateCotangent(phase / 2 + Math.PI * timeInSeconds * freq));
        return genVal;
    }

    public static double calculateCotangent(double rads) {
        return 1.0 / Math.tan(rads);
    }

    @Override
    public String getDefaultPanelName() {
        return "SAW";
    }

    /**
     * Resets to the default state (as if no sample was ever before played)
     */
    @Override
    public void resetToDefaultState() {
        // EMPTY
    }

    @Override
    public String getTooltip() {
        return "Generates sawtooth wave";
    }


    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}
