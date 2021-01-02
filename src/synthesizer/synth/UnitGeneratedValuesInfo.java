package synthesizer.synth;

/**
 * Used by ports, when the question is asked on input port, it gets the values from the output port connected to them.
 */
public interface UnitGeneratedValuesInfo {
    boolean getIsConst();

    boolean getIsNoiseGen();

    double getMaxAbsValue();
    double getMinValue();
    double getMaxValue();

    double getValue(int index);

    double[] getValues();

    /**
     * Returns Double.MAX_VALUE if it doesn't have modulation frequency
     * (if it is envelope or operation or noise generator).
     * Or returns its modulation frequency if it is generator.
     *
     * @return
     */
    default double getModulationFrequency() {
        double[] modWaveFreqs = getModulatingWaveFreqs();
        if (modWaveFreqs != null) {
            return modWaveFreqs[0];
        }

        return Double.MAX_VALUE;
    }

    boolean isBinaryPlus();

    /**
     * Returns the first constant in input ports or Double.MAX_VALUE if there are no constants on input ports
     *
     * @return
     */
    double getConstant();

    /**
     * Returns the n-th non-constant in input ports or null if there are no non-constants on input ports.
     * Doesn't work recursively.
     *
     * @return
     */
    double[] getNonConstant(int n);


    /**
     * Returns the amplitudes of the modulating wave.
     * NOTE: This method isn't used anymore, but It may be useful in future, so it is kept in here
     *
     * @return
     */
    default double[] getModulatingWaveAmps() {
        return getWaveAmps(0);
    }

    /**
     * Returns the frequencies of the modulating wave.
     *
     * @return
     */
    default double[] getModulatingWaveFreqs() {
        return getWaveFreqs(0);
    }


    /**
     * Returns the amplitudes of waveIndex-th found generator. Currently not in use but may be useful later.
     *
     * @return
     */
    double[] getWaveAmps(int waveIndex);

    /**
     * Returns the frequencies of waveIndex-th found generator. This is only used in case if we have FM synthesis
     * and the carrier signal isn't constant. So it doesn't work recursively, basically it just goes through all
     * the input ports of operator takes and returns the frequency of the waveIndex-th non-constant input.
     *
     * @return
     */
    double[] getWaveFreqs(int waveIndex);
}
