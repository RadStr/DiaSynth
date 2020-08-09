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
    public void getSubbandRealForward(double[] fftResult, int subbandCount, int subband, double[] result);

    default public double[] getSubbandRealForward(double[] fftResult, int subbandCount, int subband) {          // TODO: Subband indexed from 0 ... so the last subband has number subbandCount - 1
        double[] result = new double[fftResult.length];
        getSubbandRealForward(fftResult, subbandCount, subband, result);
        return result;
    }

    default public double getSubbandRealForwardEnergy(double[] fftMeasures, int subbandCount, int subband) {
        Pair<Integer, Integer> indexes = getSubbandIndexesRealForward(fftMeasures.length, subbandCount, subband);       // Code duplication (Take a look at bottom of interface)
        double energy = 0;
        int index = indexes.getKey();
        int len = indexes.getValue();
        int endIndex = index + len;
        for (; index < endIndex; index++) {
            energy += fftMeasures[index];
        }

//        energy = energy * subbandCount / fftMeasures.length;  // TODO: Ted nevim jestli se to deli to delkou
        energy = subbandCount * energy * (1/(double)len) / fftMeasures.length;             // TODO:
// TODO:        System.out.println("getSubbandRealForwardEnergy: " + subband + "\t" + (index-len) + ":\t" + len + ":\t" + energy);
        return energy;
    }

    public void getSubbandComplexForward(double[] fftResult, int subbandCount, int subband, double[] result);  // TODO: Subband indexed from 0 ... so the last subband has number subbandCount - 1

    default public double[] getSubbandComplexForward(double[] fftResult, int subbandCount, int subband) {
        double[] result = new double[fftResult.length];
        getSubbandComplexForward(fftResult, subbandCount, subband, result);
        return result;
    }

    default public double getSubbandComplexForwardEnergy(double[] fftMeasures, int subbandCount, int subband) {
        Pair<Integer, Integer> indexes = getSubbandIndexesComplexForward(fftMeasures.length, subbandCount, subband);    // Code duplication (Take a look at bottom of interface)
        double energy = 0;
        int index = indexes.getKey();
        int len = indexes.getValue();
        int endIndex = index + len;
        for (; index < endIndex; index++) {
            energy += fftMeasures[index];
        }

//        energy /= len;  // TODO: Ted nevim jestli se to deli to delkou
//        energy = energy * subbandCount / fftMeasures.length;
        energy = subbandCount * energy * (1/(double)len) / fftMeasures.length;     // TODO:
        return energy;
    }

    public Pair<Integer, Integer> getSubbandIndexesRealForward(int arrayLen, int subbandCount, int subband);

    public Pair<Integer, Integer> getSubbandIndexesComplexForward(int arrayLen, int subbandCount, int subband);


// TODO: unfortunately there can't be private methods in interface (available from java 9), So I have to duplicate code
//  private double getEnergyInternalCalculation(double[] fftMeasures, int subbandCount, int subband, Pair<Integer, Integer> indexes)
}
