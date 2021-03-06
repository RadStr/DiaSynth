package str.rad.synthesizer.synth.generators.noise;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.diagram.panels.port.ports.AmplitudeInputPort;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.gui.diagram.panels.shape.internals.ConstantTextInternals;
import str.rad.synthesizer.gui.diagram.panels.shape.RectangleShapedPanel;
import str.rad.synthesizer.gui.diagram.panels.shape.ShapedPanel;
import str.rad.synthesizer.synth.Unit;

public abstract class NoiseGeneratorNoFreq extends Unit {
    public NoiseGeneratorNoFreq(Unit u) {
        super(u);
    }

    public NoiseGeneratorNoFreq(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new RectangleShapedPanel(panelWithUnits, new ConstantTextInternals(getPanelName()), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            DiagramPanel panelWithUnits) {
        ShapedPanel sp = new RectangleShapedPanel(relativeX, relativeY, w, h, panelWithUnits,
                                                  new ConstantTextInternals(getPanelName()), this);
        return sp;
    }


    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        InputPort[] inputPorts = new InputPort[1];
        if (neutralValues != null && neutralValues.length >= inputPorts.length) {
            inputPorts[0] = new AmplitudeInputPort(this, shapedPanel, 0, panelWithUnits, neutralValues[0]);
        }
        else {
            inputPorts[0] = new AmplitudeInputPort(this, shapedPanel, 0, panelWithUnits);
        }
        return inputPorts;
    }

    @Override
    public double[] getNeutralValuesForPorts() {
        // Not used
        return null;
    }


    @Override
    public boolean getIsConst() {
        return false;
    }

    @Override
    public boolean getIsNoiseGen() {
        return true;
    }


    @Override
    protected void setPropertiesPanel() {
        propertiesPanel = null;
    }

    @Override
    public void updateAfterPropertiesCall() {
        // EMPTY
    }

    /**
     * Generates noise between 0 and 1.
     *
     * @return
     */
    public abstract double generateNoise();

    public double generateNoise(double amp) {
        return convertNoise(amp, generateNoise());
    }


    /**
     * converts random number between 0 and 1 to number between -amp and amp
     */
    public static double convertNoise(double amp, double rand) {
        // Modified from https://stackoverflow.com/questions/3680637/generate-a-random-double-in-a-range
        amp = Math.abs(amp);
        double randomValue = -amp + (2 * amp) * rand;
        return randomValue;
    }


    @Override
    public void calculateSamples() {
        double[] amps = inputPorts[0].getValues();
        for (int i = 0; i < results.length; i++) {
            results[i] = generateNoise(amps[i]);
        }
    }

    @Override
    public double getMaxAbsValue() {
        return inputPorts[0].getMaxAbsValue();
    }

    @Override
    public double getMinValue() {
        return -inputPorts[0].getMinValue();
    }
    @Override
    public double getMaxValue() {
        return inputPorts[0].getMaxValue();
    }


    /**
     * Returns Double.MAX_VALUE if it doesn't have modulation frequency (if it is envelope or operation or noise generator).
     * Or returns its modulation frequency if it is generator.
     *
     * @return
     */
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