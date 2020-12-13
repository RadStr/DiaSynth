package mixer;

/**
 * Just implement this class, if new implementation of mixing is wanted -
 * (or just override DefaultAudioMixer and implement these 3 abstract methods).
 */
public interface AudioMixerIFace extends UpdateIFace {
    /**
     * Mixes 1 frame. Mixes vals.length values and puts the result in to outputArr at outputArrIndex. and that is done for each channel
     *@param vals is the 2D array with samples. vals.length is number of mixed values for 1 channel.
     * @param outputArr is the array to which is put the resulting mix samples.
     * @param outputArrIndex is the index in outputArr where is put first byte of the resulting sample.
     * @param multFactors is the 2D array with factors to multiply the samples in vals with. multFactors.length == vals.length, multFactors[].length == number of channels in output audioFormat
     *                    multFactors[wave][channel]
     * @param sampleSize is the sample size of the output
     * @param isBigEndian true if the output samples are big endian
     * @param isSigned true if the output samples are signed
     * @param index is the index in the 2nd dimension of vals array. (The index says which sample it is)
     * @return Returns the first index after the inserted mixed values in outputArr
     */
    int mix(double[][] vals, byte[] outputArr, int outputArrIndex, double[][] multFactors, int sampleSize,
            boolean isBigEndian, boolean isSigned, int maxAbsoluteValue, int index);

    default void mixAllToOutputArr(double[][] vals, byte[] outputArr, double[][] multFactors, int sampleSize,
                                   boolean isBigEndian, boolean isSigned, int maxAbsoluteValue) {
        for(int inputIndex = 0, outputIndex = 0; inputIndex < vals[0].length; inputIndex++) {
            outputIndex = mix(vals, outputArr, outputIndex, multFactors, sampleSize,
                    isBigEndian, isSigned, maxAbsoluteValue, inputIndex);
        }
    }



    /**
     * Mixes 1 frame. Mixes vals.length values and puts the result in to outputArr at outputArrIndex. and that is done for each channel
     * @param vals is the 2D array with samples. vals.length is number of mixed values for 1 channel.
     * @param outputArr is the array to which is put the resulting mix samples.
     * @param outputArrIndex is the index in outputArr where is put first sample of the result.
     * @param multFactors is the 2D array with factors to multiply the samples in vals with. multFactors.length == vals.length, multFactors[].length == number of channels in output audioFormat
     *                    multFactors[wave][channel]
     * @param index is the index in the 2nd dimension of vals array. (The index says which sample it is)
     * @return Returns the first index after the inserted mixed values in outputArr
     */
    int mix(double[][] vals, double[] outputArr, int outputArrIndex, double[][] multFactors, int index);

    /**
     * Mixes input tracks to outputArr channels at index and number of mixed frames is outputLen
     * @param vals
     * @param outputArr
     * @param outputArrIndex
     * @param multFactors
     * @param index
     * @param outputLen
     * @return
     */
    int mix(double[][] vals, double[][] outputArr, int outputArrIndex, int outputLen, double[][] multFactors, int index);

    default void mixAllToOutputArr(double[][] vals, double[] outputArr, double[][] multFactors) {
        for(int inputIndex = 0, outputIndex = 0; inputIndex < vals[0].length; inputIndex++) {
            outputIndex = mix(vals, outputArr, outputIndex, multFactors, inputIndex);
        }
    }
}
