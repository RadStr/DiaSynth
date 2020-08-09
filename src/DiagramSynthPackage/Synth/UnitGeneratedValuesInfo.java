package DiagramSynthPackage.Synth;

import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.InputPort;

/**
 * Used by ports, when the question is asked on input port, it gets the values from the output port connected to them.
 */
public interface UnitGeneratedValuesInfo {
    boolean getIsConst();
    boolean getIsNoiseGen();
    double getMaxAbsValue();
    double getValue(int index);
    double[] getValues();

    /**
     * Returns Double.MAX_VALUE if it doesn't have modulation frequency (if it is envelope or operation or noise generator).
     * Or returns its modulation frequency if it is generator.
     * @return
     */
    default double getModulationFrequency() {
        double[] modWaveFreqs = getModulatingWaveFreqs();
        if(modWaveFreqs != null) {
            return modWaveFreqs[0];
        }

        return Double.MAX_VALUE;
    }
    boolean isBinaryPlus();

    /**
     * Returns the first constant in input ports or Double.MAX_VALUE if there are no constants on input ports
     * @return
     */
    double getConstant();


    /**
     * Returns the amplitudes of the modulating wave
     * @return
     */
    default double[] getModulatingWaveAmps() {
        return getWaveAmps(0);
    }

    /**
     * Returns the frequencies of the modulating wave.
     * @return
     */
    default double[] getModulatingWaveFreqs() {
        return getWaveFreqs(0);
    }


    /**
     * Returns the amplitudes of waveIndex-th found generator. Currently not in use but may be useful later.
     * @return
     */
    double[] getWaveAmps(int waveIndex);

    /**
     * Returns the frequencies of waveIndex-th found generator. This is only used in case if I have FM synthesis
     * and the carrier signal isn't constant.
     * @return
     */
    double[] getWaveFreqs(int waveIndex);
}
