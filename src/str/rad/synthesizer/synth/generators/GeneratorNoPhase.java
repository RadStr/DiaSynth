package str.rad.synthesizer.synth.generators;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.diagram.panels.port.ports.AmplitudeInputPort;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.gui.diagram.panels.port.ports.FrequencyInputPort;
import str.rad.synthesizer.gui.diagram.panels.shape.ArcShapedPanel;
import str.rad.synthesizer.gui.diagram.panels.shape.internals.arc.ArcConstantTextInternals;
import str.rad.synthesizer.gui.diagram.panels.shape.ShapedPanel;
import str.rad.synthesizer.synth.SynthDiagram;
import str.rad.synthesizer.synth.Unit;

abstract public class GeneratorNoPhase extends Unit {
    public GeneratorNoPhase(Unit u) {
        super(u);
    }

    public GeneratorNoPhase(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new ArcShapedPanel(panelWithUnits, new ArcConstantTextInternals(getPanelName()), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            DiagramPanel panelWithUnits) {
        ShapedPanel sp = new ArcShapedPanel(relativeX, relativeY, w, h, panelWithUnits,
                                            new ArcConstantTextInternals(getPanelName()), this);
        return sp;
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
        return createInputPorts(this, panelWithUnits, neutralValues);
    }

    public static InputPort[] createInputPorts(Unit u, DiagramPanel panelWithUnits, double[] neutralValues) {
        InputPort[] inputPorts = new InputPort[2];
        ShapedPanel shapedPanel = u.getShapedPanel();
        if (neutralValues != null && neutralValues.length >= inputPorts.length) {
            inputPorts[0] = new AmplitudeInputPort(u, shapedPanel, 0, panelWithUnits, neutralValues[0]);
            inputPorts[1] = new FrequencyInputPort(u, shapedPanel, 1, panelWithUnits, neutralValues[1]);
        }
        else {
            inputPorts[0] = new AmplitudeInputPort(u, shapedPanel, 0, panelWithUnits);
            inputPorts[1] = new FrequencyInputPort(u, shapedPanel, 1, panelWithUnits);
        }
        return inputPorts;
    }


    @Override
    public double[] getNeutralValuesForPorts() {
        return null;
    }

    @Override
    public boolean getIsConst() {
        return false;
    }

    @Override
    public boolean getIsNoiseGen() {
        return false;
    }


    public final boolean getIsFreqConst() {
        return inputPorts[1].getIsConst();
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
     * Generates sample, expecting the frequency parameter (freq) to be constant
     */
    public double generateSampleConst(int timeInSamples, int diagramFrequency, double amp, double freq) {
        return generateSampleConst(timeInSamples / (double) diagramFrequency, diagramFrequency, amp, freq);
    }

    /**
     * Generates sample, expecting the frequency parameter (freq) to be constant
     */
    public abstract double generateSampleConst(double timeInSecs, int diagramFrequency,
                                               double amp, double freq);

    /**
     * Generates sample, expecting the frequency parameter (freq) to be changing value
     *
     * @param timeInSamples          is current time in samples to generate the sample in.
     * @param diagramFrequency       is the output frequency of the diagram
     * @param amp                    is the amplitude of the result wave
     * @param carrierFreq            is the carrier frequency.
     * @param modulatingWaveFreq     is the modulation frequency.
     *                               Modulation index is (max deviation in freq / modulating wave freq)
     *                               so it is basically the amplitude of the modulating wave / its frequency
     *                               And since the input modulating wave is already multiplied by amplitude, I get the modulation
     *                               index by currentInputFreq / modulatingWaveFreq
     * @param modulatingWaveOutValue is the output value of the modulating wave.
     * @param currentInputFreq       is the value generated by the modulating wave.
     * @return
     */
    public double generateSampleFM(int timeInSamples, int diagramFrequency, double amp,
                                   double carrierFreq, double modulatingWaveFreq,
                                   double modulatingWaveOutValue, double currentInputFreq) {
        return generateSampleFM(timeInSamples / (double) diagramFrequency, diagramFrequency,
                                amp, carrierFreq, modulatingWaveFreq, modulatingWaveOutValue, currentInputFreq);
    }


    /**
     * Generates sample, expecting the frequency parameter (freq) to be changing value
     *
     * @param timeInSecs             is the time to generate the sample for
     * @param diagramFrequency       is the output frequency of the diagram
     * @param amp                    is the amplitude of the result wave
     * @param carrierFreq            is the carrier frequency.
     * @param modulatingWaveFreq     is the modulation frequency.
     *                               Modulation index is (max deviation in freq / modulating wave freq)
     *                               so it is basically the amplitude of the modulating wave / its frequency
     *                               And since the input modulating wave is already multiplied by amplitude, I get the modulation
     *                               index by currentInputFreq / modulatingWaveFreq
     * @param modulatingWaveOutValue is the output value of the modulating wave.
     * @param currentInputFreq       is the value generated by the modulating wave.
     * @return
     */
    public abstract double generateSampleFM(double timeInSecs, int diagramFrequency, double amp,
                                            double carrierFreq, double modulatingWaveFreq,
                                            double modulatingWaveOutValue, double currentInputFreq);

    public static double calculateModulationIndex(double maxDeviationInFreq, double modulatingWaveFreq) {
        return maxDeviationInFreq / modulatingWaveFreq;
    }

    // Same as the code inside the Generator but we omit phase here, couldn't think of simple way how to not have
    // it copy-pasted.
    // Frequency modulation where the carrier frequency also varies doesn't probably makes sense, since I can't
    // find any information on that. I really think it doesn't make sense.
    // But implemented it anyways, so it behaves correctly, even if it may produce weird results
    @Override
    public void calculateSamples() {
        SynthDiagram diagram = panelWithUnits.getSynthDiagram();
        int timeInSamples = diagram.getTimeInSamples();
        int diagramFrequency = diagram.getOutputFrequency();
        double[] amps = inputPorts[0].getValues();
        double[] freqs = inputPorts[1].getValues();
        boolean isFreqConst = inputPorts[1].getIsConst();

        double timeInSeconds = timeInSamples / (double) diagramFrequency;
        double timeJump = 1 / (double) diagramFrequency;

        // I don't want to perform fm when the noise gen is connected to input port since, it doesn't really make sense.
        // It basically says now for n samples create wave at frequency x and after that move phase to random value and
        // do that in loop. It just does clipping nothing else
        if (isFreqConst || inputPorts[1].getIsNoiseGen()) {
            for (int i = 0; i < results.length; i++, timeInSeconds += timeJump) {
                results[i] = generateSampleConst(timeInSeconds, diagramFrequency, amps[i], freqs[i]);
            }
        }
        else {
            double[] modWaveFreqs = inputPorts[1].getModulatingWaveFreqs();

            if (inputPorts[1].isBinaryPlus()) {
                double carrierFreq = inputPorts[1].getConstant();
                if (carrierFreq != Double.MAX_VALUE) {
                    double[] modWaveOutValues = inputPorts[1].getNonConstant(0);

                    for (int i = 0; i < results.length; i++, timeInSeconds += timeJump) {
                        results[i] = generateSampleFM(timeInSeconds, diagramFrequency, amps[i],
                                                      carrierFreq, modWaveFreqs[i], modWaveOutValues[i], freqs[i]);
                    }
                }
                else {
                    double[] carrierWaveFreqs = inputPorts[1].getWaveFreqs(1);
                    double[] modWaveOutValues = inputPorts[1].getNonConstant(0);

                    for (int i = 0; i < results.length; i++, timeInSeconds += timeJump) {
                        results[i] = generateSampleFM(timeInSeconds, diagramFrequency, amps[i],
                                                      carrierWaveFreqs[i], modWaveFreqs[i], modWaveOutValues[i], freqs[i]);
                    }
                }
            }
            else {
                // Most likely incorrect, but gives pretty interesting results.
//                for (int i = 0; i < results.length; i++, timeInSeconds += timeJump) {
//                    results[i] = generateSampleConst(timeInSeconds, diagramFrequency, amps[i], freqs[i]);
//                }
                // This makes much more sense, also gives ok results
                for (int i = 0; i < results.length; i++, timeInSeconds += timeJump) {
                    results[i] = generateSampleFM(timeInSeconds, diagramFrequency, amps[i],
                                                  0, modWaveFreqs[i], freqs[i], freqs[i]);
                }
            }
        }
    }


    @Override
    public double getModulationFrequency() {
        return inputPorts[1].getMaxAbsValue();
    }


    @Override
    public double[] getWaveAmps(int waveIndex) {
        return inputPorts[0].getValues();
    }


    @Override
    public double[] getWaveFreqs(int waveIndex) {
        return inputPorts[1].getValues();
    }
}