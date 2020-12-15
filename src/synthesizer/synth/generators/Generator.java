package synthesizer.synth.generators;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.port.ports.AmplitudeInputPort;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.gui.diagram.panels.port.ports.PhaseInputPort;
import synthesizer.gui.diagram.panels.port.ports.FrequencyInputPort;
import synthesizer.gui.diagram.panels.shape.ShapedPanel;
import synthesizer.synth.SynthDiagram;
import synthesizer.synth.Unit;

public abstract class Generator extends GeneratorNoPhase {
    public Generator(Unit u) {
        super(u);
    }

    public Generator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        return createInputPorts(this, panelWithUnits, neutralValues);
    }

    public static InputPort[] createInputPorts(Unit u, DiagramPanel panelWithUnits, double[] neutralValues) {
        InputPort[] inputPorts = new InputPort[3];
        ShapedPanel shapedPanel = u.getShapedPanel();
        if(neutralValues != null && neutralValues.length >= inputPorts.length) {
            inputPorts[0] = new AmplitudeInputPort(u, shapedPanel, 0, panelWithUnits, neutralValues[0]);
            inputPorts[1] = new FrequencyInputPort(u, shapedPanel, 1, panelWithUnits, neutralValues[1]);
            inputPorts[2] = new PhaseInputPort(u, shapedPanel, 2, panelWithUnits, neutralValues[2]);
        }
        else {
            inputPorts[0] = new AmplitudeInputPort(u, shapedPanel, 0, panelWithUnits);
            inputPorts[1] = new FrequencyInputPort(u, shapedPanel, 1, panelWithUnits);
            inputPorts[2] = new PhaseInputPort(u, shapedPanel, 2, panelWithUnits);
        }
        return inputPorts;
    }


    public double generateSampleConst(double timeInSecs, int diagramFrequency,
                                      double amp, double freq) {
        return generateSampleConst(timeInSecs, diagramFrequency, amp, freq, 0);
    }

    /**
     * @param phase is in radians
     * @return
     */
    public abstract double generateSampleConst(double timeInSecs, int diagramFrequency,
                                               double amp, double freq, double phase);


    @Override
    public double generateSampleFM(double timeInSecs, int diagramFrequency, double amp,
                                   double carrierFreq, double modulatingWaveFreq,
                                   double modulatingWaveOutValue, double currentInputFreq) {
        return generateSampleFM(timeInSecs, diagramFrequency, amp,
                carrierFreq, modulatingWaveFreq, modulatingWaveOutValue, currentInputFreq, 0);
    }



    // https://ccrma.stanford.edu/~jos/sasp/Frequency_Modulation_FM_Synthesis.html


    // When looking at the article the d * sin(Beta * t) is the value which we get from the input so all we have to do
    // is to divide it with the modulating frequency (frequency of the modulating oscillator),
    // peak deviation (d) is equal to the maximum absolute value from the input oscillator - which is the amplitude of the modulating signal.
    // So all we have to do is to divide the values on the output of modulating oscillator by the modulating frequency (which is frequency of that oscillator)
    // To get the output of modulating oscillator we have to subtract carrierFreq from the given input freq we get, which is currentInputFreq
    // To save some instructions, we will get the value straight from the output of the modulating oscillator and it is the
    // modulatingWaveOutValue parameter
    // The alpha and beta are the f_c and f_m but converted to rad/s (so it is just freqToRad())
    // https://ccrma.stanford.edu/sites/default/files/user/jc/fm_synthesispaper-2.pdf


    // https://www.sfu.ca/sonic-studio-webdav/handbook/Frequency_Modulation.html
    // https://www.sfu.ca/sonic-studio-webdav/handbook/Graphics/Frequency_Modulation2.gif
    // https://web.sonoma.edu/esee/courses/ee442/lectures/sp2017/lect08_angle_mod.pdf - page 9, 13
    public double generateSampleFM(double timeInSecs, int diagramFrequency, double amp,
                                   double carrierFreq, double modulatingWaveFreq,
                                   double modulatingWaveOutValue,
                                   double currentInputFreq, double phase) {
        // TODO: DEBUG
        // TODO: Just testing correctness - if it is either bigger than the value + epsilon or smaller than value - epsilon
        // then it is incorrect, because the difference is too big then
//        double epsilon = 0.00001;
//        if(currentInputFreq - carrierFreq > modulatingWaveOutValue + epsilon || currentInputFreq - carrierFreq < modulatingWaveOutValue - epsilon) {
//            ProgramTest.debugPrint("NOT EQUAL:", modulatingWaveOutValue, currentInputFreq, carrierFreq, currentInputFreq - carrierFreq);
//            System.exit(489746);
//        }
        // TODO: DEBUG


        if(modulatingWaveFreq != 0) {
//            phase += (currentInputFreq - carrierFreq) / modulatingWaveFreq;       // Also works
            phase += modulatingWaveOutValue / modulatingWaveFreq;
        }
        return generateSampleConst(timeInSecs, diagramFrequency, amp, carrierFreq, phase);
    }


    // Frequency modulation where the carrier frequency also varies doesn't probably makes sense, since I can't
    // find any information on that, I guess I could rewrite but, I really think it doesn't make sense.
    // But I guess I should implement it, so it behaves correctly, even if it produces weird results
    // Rewritten it
    @Override
    public void calculateSamples() {
        // TODO: SYNTH - HNED TED
        if(inputPorts.length <= 2) {     // It doesn't contain phase
            super.calculateSamples();
            return;
        }

        SynthDiagram diagram = panelWithUnits.getSynthDiagram();
        int timeInSamples = diagram.getTimeInSamples();
        int diagramFrequency = diagram.getOutputFrequency();
        double[] amps = inputPorts[0].getValues();
        double[] freqs = inputPorts[1].getValues();
        double[] phases = inputPorts[2].getValues();
        boolean isFreqConst = inputPorts[1].getIsConst();

        double timeInSeconds = timeInSamples / (double)diagramFrequency;
        double timeJump = 1 / (double)diagramFrequency;


        if(inputPorts[2].getIsConst()) {
            calculateSamples(isFreqConst, timeInSeconds, timeJump, diagramFrequency, amps, freqs, phases[0]);
        }
        else {
            calculateSamples(isFreqConst, timeInSeconds, timeJump, diagramFrequency, amps, freqs, phases);
        }
    }


    /**
     * Phase is in degrees
     * @param isFreqConst
     * @param timeInSeconds
     * @param timeJump
     * @param diagramFrequency
     * @param amps
     * @param freqs
     * @param phase
     */
    private void calculateSamples(boolean isFreqConst, double timeInSeconds, double timeJump,
                                  int diagramFrequency, double[] amps, double[] freqs, double phase) {
        phase = Math.toRadians(phase);

        // I don't want to perform fm when the noise gen is connected to input port since, it doesn't really make sense.
        // It basically says now for n samples create wave at frequency x and after that phase to random value, and that in loop
        // it just does clipping nothing else
        if(isFreqConst || inputPorts[1].getIsNoiseGen()) {
            for (int i = 0; i < results.length; i++, timeInSeconds += timeJump) {
                results[i] = generateSampleConst(timeInSeconds, diagramFrequency, amps[i], freqs[i], phase);
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
                                carrierFreq, modWaveFreqs[i], modWaveOutValues[i], freqs[i], phase);
                    }
                }
                else {
                    double[] carrierWaveFreqs = inputPorts[1].getWaveFreqs(1);
                    double[] modWaveOutValues = inputPorts[1].getNonConstant(0);

                    for (int i = 0; i < results.length; i++, timeInSeconds += timeJump) {
                        results[i] = generateSampleFM(timeInSeconds, diagramFrequency, amps[i],
                                carrierWaveFreqs[i], modWaveFreqs[i], modWaveOutValues[i], freqs[i], phase);
                    }
                }
            }
            else {
                // This makes much more sense, also gives ok results
                for (int i = 0; i < results.length; i++, timeInSeconds += timeJump) {
                    results[i] = generateSampleFM(timeInSeconds, diagramFrequency, amps[i],
                            0, modWaveFreqs[i], freqs[i], freqs[i], phase);
                }
            }
        }
    }


    private void calculateSamples(boolean isFreqConst, double timeInSeconds, double timeJump,
                                    int diagramFrequency, double[] amps, double[] freqs, double[] phases) {
        if(isFreqConst || inputPorts[1].getIsNoiseGen()) {
            for (int i = 0; i < results.length; i++, timeInSeconds += timeJump) {
                results[i] = generateSampleConst(timeInSeconds, diagramFrequency, amps[i], freqs[i], phases[i]);
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
                                carrierFreq, modWaveFreqs[i], modWaveOutValues[i], freqs[i], phases[i]);
                    }
                }
                else {
                    double[] carrierWaveFreqs = inputPorts[1].getWaveFreqs(1); // 1 because the 0 are modWaveFreqs
                    double[] modWaveOutValues = inputPorts[1].getNonConstant(0);

                    for (int i = 0; i < results.length; i++, timeInSeconds += timeJump) {
                        results[i] = generateSampleFM(timeInSeconds, diagramFrequency, amps[i],
                                carrierWaveFreqs[i], modWaveFreqs[i], modWaveOutValues[i], freqs[i], phases[i]);
                    }
                }
            }
            else {
                // This makes much more sense, also gives ok results
                for (int i = 0; i < results.length; i++, timeInSeconds += timeJump) {
                    results[i] = generateSampleFM(timeInSeconds, diagramFrequency, amps[i],
                            0, modWaveFreqs[i], freqs[i], freqs[i], phases[i]);
                }
            }
        }
    }
}