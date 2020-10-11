package Rocnikovy_Projekt;

import RocnikovyProjektIFace.Pair;

// TODO: Just remove it - doesn't make sense to keep after I looked at the horrible code
@Deprecated     // deprecated I guess, or not deprecated but I don't know why am I not using it. The SubbandSplitter is sufficient for what I am doing (logarithm frequency splitting)
public class SubbandSplitterLinear implements SubbandSplitterIFace {
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
    @Override
    public void getSubband(double[] fftMeasures, int subbandCount, int subband, double[] result) {
        int numberCount;
        int subbandSize;
        int startIndex;
        if (subbandCount == 1) {
            System.arraycopy(fftMeasures, 0, result, 0, fftMeasures.length);
            return;
        }

        if (fftMeasures.length % 2 == 0) {            // It's even
            numberCount = fftMeasures.length / 2 + 1;
        } else {
            numberCount = (fftMeasures.length + 1) / 2;
        }

        subbandSize = numberCount / subbandCount;
        subbandSize *= 2;
        startIndex = subband * subbandSize;

        if (subband == 0) {
            result[0] = fftMeasures[0];
            startIndex = 2;
            subbandSize -= 2;
        } else if (subband == subbandCount - 1) {
            subbandSize += fftMeasures.length - (startIndex + subbandSize);        // TODO: Asi ok      // We add the remaining elements
            result[1] = fftMeasures[1];
        }

        System.arraycopy(fftMeasures, startIndex, result, startIndex, subbandSize);
    }

    @Override
    public Pair<Integer, Integer> getSubbandIndices(int arrayLen, int subbandCount, int subband) {
        int subbandSize;
        int startIndex;

        subbandSize = arrayLen / subbandCount;
        startIndex = subband * subbandSize;
        if (subband == subbandCount - 1) {
            subbandSize += arrayLen - (startIndex + subbandSize);       // We add the remaining elements
        }

        return new Pair<>(startIndex, subbandSize);
    }
}
