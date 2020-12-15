package synthesizer.synth.generators.noise;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.port.ports.AmplitudeInputPort;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.gui.diagram.panels.port.ports.NoiseFrequencyInputPort;
import synthesizer.synth.Unit;

public abstract class NoiseGeneratorWithFrequency extends NoiseGeneratorNoFreq {
    public NoiseGeneratorWithFrequency(Unit u) {
        super(u);
    }

    public NoiseGeneratorWithFrequency(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        InputPort[] inputPorts = new InputPort[2];
        if(neutralValues != null && neutralValues.length >= inputPorts.length) {
            inputPorts[0] = new AmplitudeInputPort(this, shapedPanel,
                    0, panelWithUnits, neutralValues[0]);
            inputPorts[1] = new NoiseFrequencyInputPort(this, shapedPanel,
                    1, panelWithUnits, neutralValues[1]);
        }
        else {
            inputPorts[0] = new AmplitudeInputPort(this, shapedPanel, 0, panelWithUnits);
            inputPorts[1] = new NoiseFrequencyInputPort(this, shapedPanel, 1,
                    panelWithUnits, panelWithUnits.getSynthDiagram().getOutputFrequency());
        }
        return inputPorts;
    }

    @Override
    public double[] getNeutralValues() {
        return null;
    }

    protected int randNumUseCount = Integer.MAX_VALUE;
    protected double oldRandNum;
    @Override
    public void resetToDefaultState() {
        randNumUseCount = Integer.MAX_VALUE;
    }


    /**
     *
     * @param amp
     * @param useCount is the diagram frequency / frequency - so it is the number which says how many times should be generated the same rand number.
     * @return
     */
    public double generateNoise(double amp, double useCount) {
        if(randNumUseCount >= useCount) {
            randNumUseCount = 1;
            oldRandNum = generateNoise();
        }
        else {
            randNumUseCount++;
        }

        amp = Math.abs(amp);
        double randomValue = -amp + (2 * amp) * oldRandNum;
        return randomValue;
    }

    @Override
    public void calculateSamples() {
        if(inputPorts.length <= 1) {
            super.calculateSamples();
            return;
        }
        double[] amps = inputPorts[0].getValues();
        double[] freqs = inputPorts[1].getValues();
        double diagramFreq = panelWithUnits.getSynthDiagram().getOutputFrequency();
        if(inputPorts[1].getIsConst()) {
            double useCount = diagramFreq / freqs[0];
            for (int i = 0; i < results.length; i++) {
                results[i] = generateNoise(amps[i], useCount);
            }
        }
        else {
            for (int i = 0; i < results.length; i++) {
                double useCount = diagramFreq / freqs[i];
                results[i] = generateNoise(amps[i], useCount);
            }
        }
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
