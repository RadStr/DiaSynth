package Rocnikovy_Projekt;

import RocnikovyProjektIFace.Pair;

public interface SubbandSplitterIFace {
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
    // TODO: tehle text dat do popisu funkce:  Result array should be set to 0s, the method sets only the indexes in result array which correspond to the subband, others aren't touched

    /**
     * Copies the fftMeasures array part corresponding to the specified subband to the result array (to the same positions).
     * Method doesn't change other indices of the result array.
     * @param fftMeasures
     * @param subbandCount
     * @param subband
     * @param result
     */
    public void getSubband(double[] fftMeasures, int subbandCount, int subband, double[] result);

    /**
     * Copies the fftMeasures array part corresponding to the specified subband to newly created array (to the same positions).
     * It is equivalent to setting all other indices except the subbands one to 0.
     * @param fftMeasures
     * @param subbandCount
     * @param subband
     * @return
     */
    default public double[] getSubband(double[] fftMeasures, int subbandCount, int subband) {          // TODO: Subband indexed from 0 ... so the last subband has number subbandCount - 1
        double[] result = new double[fftMeasures.length];
        getSubband(fftMeasures, subbandCount, subband, result);
        return result;
    }

    /**
     * Takes sum of the specified subband in the given fftMeasures array. The resulting sum is averaged (divided by the array length)
     * and then multiplied by the length of the subband.
     * @param fftMeasures
     * @param subbandCount
     * @param subband
     * @return
     */
    default public double getSubbandEnergy(double[] fftMeasures, int subbandCount, int subband) {
        Pair<Integer, Integer> indexes = getSubbandIndices(fftMeasures.length, subbandCount, subband);       // Code duplication (Take a look at bottom of interface)
        double energy = 0;
        int index = indexes.getKey();
        int len = indexes.getValue();
        int endIndex = index + len;
        for (; index < endIndex; index++) {
            energy += fftMeasures[index];
        }

//        energy = energy * subbandCount / fftMeasures.length;  // TODO: Ted nevim jestli se to deli to delkou
        // TODO: NOVY BPM - ASI BUG
//        energy /= (double) len;       // Average energy per bin
//        energy = subbandCount * energy / fftMeasures.length;

        energy = len * energy / fftMeasures.length;
        // TODO: NOVY BPM - ASI BUG

// TODO:        System.out.println("getSubbandEnergy: " + subband + "\t" + (index-len) + ":\t" + len + ":\t" + energy);
        return energy;
    }

    /**
     * Help method, which shouldn't be called from outside. Gets the start index and length for the given subband.
     * @param arrayLen
     * @param subbandCount
     * @param subband
     * @return
     */
    public Pair<Integer, Integer> getSubbandIndices(int arrayLen, int subbandCount, int subband);


// TODO: unfortunately there can't be private methods in interface (available from java 9), So I have to duplicate code
//  private double getEnergyInternalCalculation(double[] fftMeasures, int subbandCount, int subband, Pair<Integer, Integer> indexes)
}
