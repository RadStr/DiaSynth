package synthesizer.synth.generators.envelopes;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.synth.Unit;

public class LinearEnvelope extends Envelope {
    public LinearEnvelope(Unit u) {
        super(u);
    }

    public LinearEnvelope(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    public String getDefaultPanelName() {
        return "LINEN";
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
            genVal = sustainAmp * (1 - ((timeInSecs - sustainTime) / (releaseTime - sustainTime)));
        }
        else if (timeInSecs > decTime) {        // Sustain phase
            genVal = sustainAmp;
        }
        else if (timeInSecs > attTime) {        // decay phase
            genVal = sustainAmp + (attAmp - sustainAmp) * (1 - ((timeInSecs - attTime) / (decTime - attTime)));
        }
        else {                                  // Attack phase
            genVal = attAmp * (timeInSecs / attTime);
        }
        return genVal;
    }

    @Override
    public String getTooltip() {
        return "Generates envelope with linear slopes";
    }


    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}