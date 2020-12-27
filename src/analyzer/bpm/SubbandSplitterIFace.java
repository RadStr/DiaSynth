package analyzer.bpm;

import util.Pair;

public interface SubbandSplitterIFace {
    int getSubbandCount();


    // From documentation:
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]

    /**
     * Copies the fftMeasures array part corresponding to the specified subband to the result array (to the same positions).
     * Method doesn't change other indices of the result array.
     *
     * @param fftResult
     * @param subbandCount
     * @param subband
     * @param result
     */
    default void getSubband(double[] fftResult, int subbandCount, int subband, double[] result) {
        int currentSubbandSize;
        int startIndex;

        Pair<Integer, Integer> pair = getSubbandIndices(fftResult.length, subbandCount, subband);
        startIndex = pair.getKey();
        currentSubbandSize = pair.getValue();

        startIndex++;       // Because we want to skip the [1], the [0] is already skipped
        if (subband == subbandCount - 1) {
            currentSubbandSize--;
            result[1] = fftResult[1];
        }

        System.arraycopy(fftResult, startIndex, result, startIndex, currentSubbandSize);
    }

    /**
     * Copies the fftMeasures array part corresponding to the specified subband to newly created array (to the same positions).
     * It is equivalent to setting all other indices except the subbands one to 0.
     *
     * @param fftMeasures
     * @param subbandCount
     * @param subband
     * @return
     */
    default public double[] getSubband(double[] fftMeasures, int subbandCount, int subband) {
        double[] result = new double[fftMeasures.length];
        getSubband(fftMeasures, subbandCount, subband, result);
        return result;
    }

    /**
     * Takes sum of the specified subband in the given fftMeasures array. The resulting sum is averaged (divided by the array length)
     * and then multiplied by the length of the subband.
     *
     * @param fftMeasures
     * @param subbandCount
     * @param subband
     * @return
     */
    // Code duplication (Take a look at bottom of interface)
    default double getSubbandEnergy(double[] fftMeasures, int subbandCount, int subband) {
        Pair<Integer, Integer> indices = getSubbandIndices(fftMeasures.length, subbandCount, subband);
        double energy = 0;
        int index = indices.getKey();
        int len = indices.getValue();
        int endIndex = index + len;
        for (; index < endIndex; index++) {
            energy += fftMeasures[index];
        }

        energy = len * energy / fftMeasures.length;
        return energy;
    }

    /**
     * Help method, which shouldn't be called from outside. Gets the start index and length for the given subband.
     *
     * @param arrayLen
     * @param subbandCount
     * @param subband
     * @return
     */
    public Pair<Integer, Integer> getSubbandIndices(int arrayLen, int subbandCount, int subband);


//  Unfortunately there can't be private methods in interface (available from java 9), So I have to duplicate code
//  private double getEnergyInternalCalculation(double[] fftMeasures, int subbandCount, int subband, Pair<Integer, Integer> indexes)
}