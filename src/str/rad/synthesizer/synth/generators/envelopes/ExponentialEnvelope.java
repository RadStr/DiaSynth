package str.rad.synthesizer.synth.generators.envelopes;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.synth.Unit;

public class ExponentialEnvelope extends Envelope {
    public ExponentialEnvelope(Unit u) {
        super(u);
    }

    public ExponentialEnvelope(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    public String getDefaultPanelName() {
        return "EXPEN";
    }

    /**
     * The time parameters are already converted to total time instead of relative
     * for example (0.5, 0.6, 2, 0.4) -> (0.5, 1.1, 3.1, 3.5)
     */
    @Override
    public double generateEnvelopeSample(double timeInSecs,
                                         double attTime, double attAmp, double decTime,
                                         double sustainTime, double sustainAmp, double releaseTime) {
        double genVal;
        if (timeInSecs > releaseTime) {         // After release phase
            genVal = 0;
        }
        else if (timeInSecs > sustainTime) {    // Release phase
            timeInSecs -= sustainTime;
            releaseTime -= sustainTime;
            double pow = 1 - (timeInSecs / releaseTime);
            genVal = sustainAmp * Math.pow(POW_BASE, pow) / POW_BASE;
        }
        else if (timeInSecs > decTime) {        // Sustain phase
            genVal = sustainAmp;
        }
        else if (timeInSecs > attTime) {        // decay phase
            timeInSecs -= attTime;
            decTime -= attTime;
            double pow = 1 - (timeInSecs / decTime);
            genVal = sustainAmp + (attAmp - sustainAmp) * (Math.pow(POW_BASE, pow) / POW_BASE);
        }
        else {                                  // Attack phase
            genVal = attAmp * Math.pow(POW_BASE, (timeInSecs / attTime)) / POW_BASE;
        }
        return genVal;
    }

    private static final int POW_BASE = 64;

    @Override
    public String getTooltip() {
        return "Generates envelope with exponential slopes";
    }

    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}
