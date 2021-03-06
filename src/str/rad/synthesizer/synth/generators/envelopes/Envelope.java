package str.rad.synthesizer.synth.generators.envelopes;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.diagram.panels.port.*;
import str.rad.synthesizer.gui.diagram.panels.port.ports.envelope.*;
import str.rad.synthesizer.gui.diagram.panels.shape.internals.ConstantTextInternals;
import str.rad.synthesizer.gui.diagram.panels.shape.ShapedPanel;
import str.rad.synthesizer.gui.diagram.panels.shape.TrapeziumShapedPanel;
import str.rad.synthesizer.synth.SynthDiagram;
import str.rad.synthesizer.synth.Unit;


public abstract class Envelope extends Unit {
    public Envelope(Unit u) {
        super(u);
    }

    public Envelope(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    protected void setPropertiesPanel() {
        propertiesPanel = null;
    }

    @Override
    public void updateAfterPropertiesCall() {
        // EMPTY
    }


    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        InputPort[] inputPorts = new InputPort[6];
        if (neutralValues != null && neutralValues.length >= inputPorts.length) {
            inputPorts[0] = new AttackTimeInputPort(this, shapedPanel, 0, panelWithUnits, neutralValues[0]);
            inputPorts[1] = new AttackAmplitudeInputPort(this, shapedPanel, 1, panelWithUnits, neutralValues[1]);
            inputPorts[2] = new DecayTimeInputPort(this, shapedPanel, 2, panelWithUnits, neutralValues[2]);
            inputPorts[3] = new SustainTimeInputPort(this, shapedPanel, 3, panelWithUnits, neutralValues[3]);
            inputPorts[4] = new SustainAmplitudeInputPort(this, shapedPanel, 4, panelWithUnits, neutralValues[4]);
            inputPorts[5] = new ReleaseTimeInputPort(this, shapedPanel, 5, panelWithUnits, neutralValues[5]);
        }
        else {
            inputPorts[0] = new AttackTimeInputPort(this, shapedPanel, 0, panelWithUnits);
            inputPorts[1] = new AttackAmplitudeInputPort(this, shapedPanel, 1, panelWithUnits);
            inputPorts[2] = new DecayTimeInputPort(this, shapedPanel, 2, panelWithUnits);
            inputPorts[3] = new SustainTimeInputPort(this, shapedPanel, 3, panelWithUnits);
            inputPorts[4] = new SustainAmplitudeInputPort(this, shapedPanel, 4, panelWithUnits);
            inputPorts[5] = new ReleaseTimeInputPort(this, shapedPanel, 5, panelWithUnits);
        }
        return inputPorts;
    }

    @Override
    public double[] getNeutralValuesForPorts() {
        return new double[0];
    }

    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new TrapeziumShapedPanel(panelWithUnits, new ConstantTextInternals(getPanelName()), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            DiagramPanel panelWithUnits) {
        ShapedPanel sp = new TrapeziumShapedPanel(relativeX, relativeY, w, h, panelWithUnits,
                                                  new ConstantTextInternals(getPanelName()), this);
        return sp;
    }

    @Override
    public void resetToDefaultState() {
        // EMPTY
    }

    // NOTE: All values can be generators, but only amplitude values are treated as such.
    // From all others only first value is taken.
    @Override
    public void calculateSamples() {
        SynthDiagram synthDiagram = panelWithUnits.getSynthDiagram();
        int timeInSamples = synthDiagram.getTimeInSamples();
        int diagramFreq = synthDiagram.getOutputFrequency();

        double attTime = inputPorts[0].getValue(0);
        double[] attAmp = inputPorts[1].getValues();

        double decTime = inputPorts[2].getValue(0);
        decTime += attTime;

        double sustainTime = inputPorts[3].getValue(0);
        sustainTime += decTime;

        double[] sustainAmp = inputPorts[4].getValues();

        double releaseTime = inputPorts[5].getValue(0);
        releaseTime += sustainTime;

        double timeInSecs = timeInSamples / (double) diagramFreq;
        double timeJump = 1 / (double) diagramFreq;
        for (int i = 0; i < results.length; i++, timeInSamples++, timeInSecs += timeJump) {
            results[i] = generateEnvelopeSample(timeInSecs, attTime, attAmp[i], decTime,
                                                sustainTime, sustainAmp[i], releaseTime);
        }
    }

    /**
     * The time parameters are already converted to total time instead of relative
     * for example (0.5, 0.6, 2, 0.4) -> (0.5, 1.1, 3.1, 3.5)
     */
    public double generateEnvelopeSample(int timeInSamples, int diagramFrequency,
                                         double attTime, double attAmp, double decTime,
                                         double sustainTime, double sustainAmp, double releaseTime) {
        double timeInSecs = timeInSamples / (double) diagramFrequency;
        return generateEnvelopeSample(timeInSecs, attTime, attAmp, decTime, sustainTime, sustainAmp, releaseTime);
    }


    /**
     * The time parameters are already converted to total time instead of relative
     * for example (0.5, 0.6, 2, 0.4) -> (0.5, 1.1, 3.1, 3.5)
     */
    public abstract double generateEnvelopeSample(double timeInSecs,
                                                  double attTime, double attAmp, double decTime,
                                                  double sustainTime, double sustainAmp, double releaseTime);

    @Override
    public boolean getIsConst() {
        return false;
    }

    @Override
    public boolean getIsNoiseGen() {
        return false;
    }


    @Override
    public double getMaxAbsValue() {
        // The max shouldn't be needed, but you never know if user won't put bigger amplitude to sustain than attack.
        return Math.max(inputPorts[1].getMaxAbsValue(), inputPorts[4].getMaxAbsValue());
    }

    @Override
    public double getMinValue() {
        // The min should be 0, if there isn't negative amplitude thrown in there (it depends on the user input).
        double min = Math.min(inputPorts[1].getMinValue(), 0);
        return Math.min(min, inputPorts[4].getMinValue());
    }
    @Override
    public double getMaxValue() {
        // The max should be inputPorts[1].getMaxValue(), but it depends on the user input.
        double max = Math.max(inputPorts[1].getMaxValue(), 0);
        return Math.max(max, inputPorts[4].getMaxValue());
    }




    @Override
    public double getModulationFrequency() {
        return Double.MAX_VALUE;
    }

    @Override
    public double[] getWaveAmps(int waveIndex) {
        return null;
    }

    @Override
    public double[] getWaveFreqs(int waveIndex) {
        return null;
    }
}
