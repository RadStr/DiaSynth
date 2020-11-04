package DiagramSynthPackage.Synth.Generators.NoiseGenerators;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.AmplitudeInputPort;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.InputPort;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals.ConstantTextInternals;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.RectangleShapedPanel;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import DiagramSynthPackage.Synth.Generators.Generator;
import DiagramSynthPackage.Synth.Unit;
import Rocnikovy_Projekt.Program;

public abstract class NoiseGenerator extends Unit {
    public NoiseGenerator(Unit u) {
        super(u);
    }

    public NoiseGenerator(JPanelWithMovableJPanels panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    protected ShapedPanel createShapedPanel(JPanelWithMovableJPanels panelWithUnits) {
        ShapedPanel sp = new RectangleShapedPanel(panelWithUnits, new ConstantTextInternals(getPanelName()), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            JPanelWithMovableJPanels panelWithUnits) {
        ShapedPanel sp = new RectangleShapedPanel(relativeX, relativeY, w, h, panelWithUnits,
                new ConstantTextInternals(getPanelName()), this);
        return sp;
    }


    @Override
    protected InputPort[] createInputPorts(JPanelWithMovableJPanels panelWithUnits, double[] neutralValues) {
        InputPort[] inputPorts = new InputPort[1];
        if(neutralValues != null && neutralValues.length >= inputPorts.length) {
            inputPorts[0] = new AmplitudeInputPort(this, shapedPanel, 0, panelWithUnits, neutralValues[0]);
        }
        else {
            inputPorts[0] = new AmplitudeInputPort(this, shapedPanel, 0, panelWithUnits);
        }
        return inputPorts;
    }

    @Override
    public double[] getNeutralValues() {
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
     * @return
     */
    public abstract double generateNoise();

    public double generateNoise(double amp) {
        // Modified from https://stackoverflow.com/questions/3680637/generate-a-random-double-in-a-range
        amp = Math.abs(amp);
        double randomValue = -amp + (2 * amp) * generateNoise();
        return randomValue;
    }


    @Override
    public void calculateSamples() {
        double[] amps = inputPorts[0].getValues();
        for(int i = 0; i < results.length; i++) {
            results[i] = generateNoise(amps[i]);
        }
    }

    @Override
    public double getMaxAbsValue() {
        return inputPorts[0].getMaxAbsValue();
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