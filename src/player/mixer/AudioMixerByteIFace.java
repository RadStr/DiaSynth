package player.mixer;

public interface AudioMixerByteIFace {
    /**
     * Mixes 1 frame. Mixes vals.length values and puts the result in to outputArr at outputArrIndex. and that is done for each channel
     * @param vals is the 2D array with samples. vals.length is number of mixed values. And we take values from vals[][index] to vals[][index + sampleSize] and mix them
     * @param outputArr is the array to which is put the resulting mix samples.
     * @param outputArrIndex is the index in outputArr where is put first byte of the resulting sample.
     * @param multFactors is the 2D array with factors to multiply the samples in vals with. multFactors.length == vals.length, multFactors[].length == number of channels in output audioFormat
     *                    multFactors[wave][channel]
     * @param sampleSize is the sample size
     * @param mask is the mask used for calculation
     * @param isBigEndian true if the samples are big endian
     * @param isSigned true if the samples are signed
     * @param index is the index in the 2nd dimension of vals array. (The index says which sample it is)
     * @return Returns the first index after the inserted mixed values in outputArr
     */
    int mix(byte[][] vals, byte[] outputArr, int outputArrIndex, double[][] multFactors, int sampleSize,
            int mask, boolean isBigEndian, boolean isSigned, int index);



    /**
     * Mixes vals.length values to 1 sample. vals.length == multFactors.length, multFactors[].length == number of channels in output audioFormat
     * @param vals is the 2D array with samples, where vals[i].length == sampleSize and vals.length == number of samples to mix
     * @param multFactors is the 2D array with factors to multiply the vals with. multFactors[wave][channel]
     * @param mask is the mask used for calculation
     * @param isBigEndian true if the samples are big endian
     * @param isSigned true if the samples are signed
     * @return
     */
    int mix(byte[][] vals, double[][] multFactors, int mask, boolean isBigEndian, boolean isSigned);
}
