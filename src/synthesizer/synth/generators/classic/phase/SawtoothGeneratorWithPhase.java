package synthesizer.synth.generators.classic.phase;

import synthesizer.gui.MovablePanelsPackage.DiagramPanel;
import synthesizer.synth.generators.GeneratorWithPhase;
import synthesizer.synth.Unit;

// https://en.wikipedia.org/wiki/Sawtooth_wave
public class SawtoothGeneratorWithPhase extends GeneratorWithPhase {
    public SawtoothGeneratorWithPhase(Unit u) {
        super(u);
    }

    public SawtoothGeneratorWithPhase(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    // Saw wave can't be reset to 0 just by moving phase, because the function is always rising (unless 2*pi is reached - one full cycle)
    // So it isn't raising only at 1 point ... by moving the phase of 1 saw wave by PI we can half the amplitude of the addition result ...
    // talking about the case when adding 2 saw waves of same frequency
    @Override
    public double generateSampleConst(double timeInSeconds, int diagramFrequency, double amp,
                                      double freq, double phase) {
        double genVal;
        // IMPORTANT NOTE: It isn't explained the best and also maybe it isn't correct, but I think it is correct,
        // since it makes sense and also works as seen on calculations:
        // / (2 * Math.PI) because when working with sine the one cycle is reached at 2 * Math.PI
        // but here it is shrinked and when phase = 1 then it is 1 cycle so I need to scale it accordingly,
        // Basically the scaling of phase is always C / (2*Math.PI) where C is the constant which multiplies the
        // frequency in equation to calculate sample, because when having frequency == 1 then we want to reach 1 cycle at 2 * Math.PI
        double currRad = timeInSeconds * freq + phase / (2 * Math.PI);
        double floor = Math.floor(currRad + 1 / 2.0);
        genVal = amp * (2 * (currRad - floor));

        // Second variant
        // The phase needs to be / 2 because also I am multiplying by Math.PI instead of 2 * Math.PI
        //genVal = - 2 * amp / Math.PI * Math.atan(calculateCotangent(phase / 2 + Math.PI * timeInSeconds * freq));

        //        ProgramTest.debugPrint("cot:", - 2 * amp / Math.PI * Math.atan(calculateCotangent(0)),
//                - 2 * amp / Math.PI * Math.atan(calculateCotangent(Math.PI / 2)),
//                - 2 * amp / Math.PI * Math.atan(calculateCotangent(Math.PI)),
//                - 2 * amp / Math.PI * Math.atan(calculateCotangent(2 * Math.PI)),
//                genVal);
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
