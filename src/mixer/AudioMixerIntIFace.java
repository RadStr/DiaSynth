package mixer;

public interface AudioMixerIntIFace {
    /**
     * Mixes vals.length samples at vals[][index] to 1 sample. multFactors[].length "==" number of channels
     * @param vals is the 2D array with samples.
     * @param multFactors is the 2D array with factors to multiply the vals with.
     * @param index is the index in the 2nd dimension of vals array. (The index says which sample it is)
     * @param outputArr is the array to which are put the results. outputArr.length >= multFactors[].length
     */
    void mix(int[][] vals, double[][] multFactors, int index, int[] outputArr);        // Same comments as for the double variant


    /**
     * Takes samples at index vals[][index] and mixes them to one by using multFactors and then finalMultFactor on the result.
     * Same as the variant without finalMultFactors, but this one performs one more mixing on the final results. using finalMultFactors
     * If the finalMultFactors is null then don't perform the final mixing.
     * @param vals are the samples
     * @param multFactors are the factors of mixing. multFactors[wave][channel]
     * @param finalMultFactors are the final factors of mixing (are of length number of channels in result (== multFactrs[].length)). It is used on the mixed result.
     * @param index is the index in the vals array to mix
     * @param outputArr is the array to contain the result of mixing, have to be of length at least multFactors[].length
     */
    void mix(int[][] vals, double[][] multFactors, double[] finalMultFactors, int index, int[] outputArr);


    /**
     * Mixes the input array together, so if the multFactors[].length == 1 then only 1 value is returned, which is the mix of the array. vals.length == multFactors.length, multFactors[].length == number of channels in output audioFormat
     * @param vals is the input array, 1 index = 1 value to mix
     * @param multFactors is the 2D array with factors to multiply the vals with. multFactors[wave][channel]
     * @param outputArr is the array to which are put the results. outputArr.length >= multFactors[].length
     */
    void mix(int[] vals, double[][] multFactors, int[] outputArr);      // Same comments as for the double variant



    // Help method
    /**
     * Takes samples at index vals[][index] and multFactors[][channel] and mixes them to 1 sample.
     * @param vals are the samples
     * @param multFactors are the factors of mixing. multFactors[wave][channel]
     * @param channel is the channel to calculate the mixing for.
     * @param index is the index in the vals array to mix
     */
    int mix(int[][] vals, double[][] multFactors, int channel, int index);
}
