package Rocnikovy_Projekt;

import RocnikovyProjektIFace.Pair;

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
    public void getSubbandRealForward(double[] fftResult, int subbandCount, int subband, double[] result) {
        int numberCount;
        int subbandSize;
        int startIndex;
        if (subbandCount == 1) {
            System.arraycopy(fftResult, 0, result, 0, fftResult.length);
            return;
        }

        if (fftResult.length % 2 == 0) {            // It's even
            numberCount = fftResult.length / 2 + 1;
        } else {
            numberCount = (fftResult.length + 1) / 2;
        }

        subbandSize = numberCount / subbandCount;
        subbandSize *= 2;
        startIndex = subband * subbandSize;

        if (subband == 0) {
            result[0] = fftResult[0];
            startIndex = 2;
            subbandSize -= 2;
        } else if (subband == subbandCount - 1) {
            subbandSize += fftResult.length - (startIndex + subbandSize);        // TODO: Asi ok      // We add the remaining elements
            result[1] = fftResult[1];
        }

        System.arraycopy(fftResult, startIndex, result, startIndex, subbandSize);
    }


    @Override
    public void getSubbandComplexForward(double[] fftResult, int subbandCount, int subband, double[] result) {
        int numberCount = fftResult.length / 2;
        int subbandSize = numberCount / subbandCount;
        int startIndex = subband * subbandSize;
        if(subband == subbandCount - 1) {
            subbandSize += numberCount - (startIndex + subbandSize);       // TODO: Asi ok      // We add the remaining elements
            System.arraycopy(fftResult, startIndex, result, startIndex, subbandSize);
        }
        else {
            System.arraycopy(fftResult, startIndex, result, startIndex, subbandSize);
        }
    }


    // TODO: Commented because this variants is for case when I pass the length of the fft result in arrayLen instead of only the measures
/*
    @Override
    public Pair<Integer, Integer> getSubbandIndexesRealForward(int arrayLen, int subbandCount, int subband) {
        int numberCount;
        int subbandSize;
        int startIndex;

        if(arrayLen % 2 == 0) {			// It's even
            numberCount = arrayLen / 2 + 1;
            subbandSize = numberCount / subbandCount;
            startIndex = subband * subbandSize;

            if(subband == subbandCount - 1) {
                subbandSize += numberCount - (startIndex + subbandSize);        // We add the remaining elements
            }
        } else {
            numberCount = (arrayLen + 1) / 2;
            subbandSize = numberCount / subbandCount;
            startIndex = subband * subbandSize;

            if(subband == subbandCount - 1) {
                subbandSize += numberCount - (startIndex + subbandSize);       // We add the remaining elements
            }
        }

        return new Pair<>(startIndex, subbandSize);
    }
*/

    @Override
    public Pair<Integer, Integer> getSubbandIndexesRealForward(int arrayLen, int subbandCount, int subband) {
        int subbandSize;
        int startIndex;

        subbandSize = arrayLen / subbandCount;
        startIndex = subband * subbandSize;
        if (subband == subbandCount - 1) {
            subbandSize += arrayLen - (startIndex + subbandSize);       // We add the remaining elements
        }

        return new Pair<>(startIndex, subbandSize);
    }

    @Override
    public Pair<Integer, Integer> getSubbandIndexesComplexForward(int arrayLen, int subbandCount, int subband) {
        int numberCount = arrayLen / 2;
        int subbandSize = numberCount / subbandCount;
        int startIndex = subband * subbandSize;
        if(subband == subbandCount - 1) {
            subbandSize += numberCount - (startIndex + subbandSize);       // We add the remaining elements
        }
        return new Pair<>(startIndex, subbandSize);
    }
}
