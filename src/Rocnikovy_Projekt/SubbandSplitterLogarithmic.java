package Rocnikovy_Projekt;

import RocnikovyProjektIFace.Pair;

public class SubbandSplitterLogarithmic implements SubbandSplitterIFace {
    private double previousSubbandSize = 0;
    private double previousStartIndex = 0;
    private double q;

// TODO: the subband splits will be 2,2,4,8,16,32,64 .. tohle nefunguje ... to je sice logaritmicky ale neodpovida to tomu poctu subbandCount


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
    public void getSubbandRealForward(double[] fftResult, int subbandCount, int subband, double[] result) {     // TODO: Basically copied from SubbandSplitterLinear
        int numberCount;
        int currentSubbandSize;
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
        Pair<Integer, Integer> pair = getResult(numberCount, subbandCount, subband);
        startIndex = pair.getKey();
        currentSubbandSize = 2 * pair.getValue();


        if (subband == 0) {
            result[0] = fftResult[0];
            startIndex = 2;
            currentSubbandSize -= 2;
        } else {
            startIndex = 2 * startIndex;        // TODO: Not sure about this
            if (subband == subbandCount - 1) {
                currentSubbandSize += fftResult.length - (startIndex + currentSubbandSize);  // TODO: Asi ok      // We add the remaining elements
                result[1] = fftResult[1];
//                if (fftResult.length % 2 == 0) {
//                    currentSubbandSize -= 2;
//                } else {
//                    currentSubbandSize--;
//                }
            }
        }
        System.arraycopy(fftResult, startIndex, result, startIndex, currentSubbandSize);
//        } else {
//            numberCount = (fftResult.length + 1) / 2;
//            q = numberCount / (subbandCount * subbandCount + subbandCount);
//            previousSubbandSize = subband * q;
//            startIndex = ((subband + 1) * previousSubbandSize) / 2; // The formula is for arithmetic sequence is n * (n-1) * q / 2 where n is indexed from 1 ... so we use subband + 1 instead of subband.
//            currentSubbandSize = q + previousSubbandSize;
//
//            if(subband == 0) {
//                result[0] = fftResult[0];
//                startIndex = 2;
//                System.arraycopy(fftResult, startIndex, result, startIndex, currentSubbandSize - 1);
//            }
//            else if(subband == subbandCount - 1) {
//                currentSubbandSize += numberCount - (startIndex + currentSubbandSize);  // TODO: Asi ok      // We add the remaining elements
//                result[1] = fftResult[1];
//                System.arraycopy(fftResult, startIndex, result, startIndex, currentSubbandSize - 1);  // TODO: ma tu byt -1 nebo ne      // TODO: Should be ok
//                result[fftResult.length - 1] = fftResult[fftResult.length - 1];
//            }
//            else {
//                System.arraycopy(fftResult, startIndex, result, startIndex, currentSubbandSize);
//            }
//        }
    }


    @Override
    public void getSubbandComplexForward(double[] fftResult, int subbandCount, int subband, double[] result) {
        int arrayLen = fftResult.length / 2;
        Pair<Integer, Integer> pair = getResult(arrayLen, subbandCount, subband);
        int startIndex = pair.getKey();
        int currentSubbandSize = pair.getValue();
        System.arraycopy(fftResult, startIndex, result, startIndex, currentSubbandSize);
    }

// TODO: Commented because this variants is for case when I pass the length of the fft result in arrayLen instead of only the measures
/*
    @Override
    public Pair<Integer, Integer> getSubbandIndexesRealForward(int arrayLen, int subbandCount, int subband) {
        int numberCount;
        int q;
        int previousSubbandSize;
        int currentSubbandSize;
        int startIndex;

        if(arrayLen % 2 == 0) {            // It's even
            numberCount = arrayLen / 2 + 1;
            q = numberCount / (subbandCount * subbandCount + subbandCount);
            previousSubbandSize = subband * q;
            startIndex = ((subband + 1) * previousSubbandSize) / 2; // The formula is for arithmetic sequence is n * (n-1) * q / 2 where n is indexed from 1 ... so we use subband + 1 instead of subband.
            currentSubbandSize = q + previousSubbandSize;

            if (subband == subbandCount - 1) {
                currentSubbandSize += numberCount - (startIndex + currentSubbandSize);  // We add the remaining elements
            }
        } else {
            numberCount = (arrayLen + 1) / 2;
            q = numberCount / (subbandCount * subbandCount + subbandCount);
            previousSubbandSize = subband * q;
            startIndex = ((subband + 1) * previousSubbandSize) / 2; // The formula is for arithmetic sequence is n * (n-1) * q / 2 where n is indexed from 1 ... so we use subband + 1 instead of subband.
            currentSubbandSize = q + previousSubbandSize;

            if (subband == subbandCount - 1) {
                currentSubbandSize += numberCount - (startIndex + currentSubbandSize);  // We add the remaining elements
            }
        }

        return new Pair<>(startIndex, currentSubbandSize);
    }
*/

    @Override
    public Pair<Integer, Integer> getSubbandIndexesRealForward(int arrayLen, int subbandCount, int subband) {
        return getResult(arrayLen, subbandCount, subband);
    }

    @Override
    public Pair<Integer, Integer> getSubbandIndexesComplexForward(int arrayLen, int subbandCount, int subband) {
        arrayLen /= 2;
        return getResult(arrayLen, subbandCount, subband);
    }

    private Pair<Integer, Integer> getResult(int arrayLen, int subbandCount, int subband) {
        if(subband == 0) {  // Expected to be called in succesive order
            previousStartIndex = 0;
            previousSubbandSize = 0;
            q = 2 * (arrayLen-subbandCount) / (double)(subbandCount * (subbandCount+1));       // From the formula for arithmetic sum
            // arrayLen-subbandCount because by taking the Math.ceil we get for every subband +1
        }
        int currentSubbandSize;
        int startIndex;

        startIndex = (int)Math.ceil(previousStartIndex + previousSubbandSize);
        currentSubbandSize = (int)Math.ceil(q + previousSubbandSize);
// TODO:        System.out.println(subband + "\t" + q + "\t" + previousSubbandSize + "\t" + currentSubbandSize + "\t" + startIndex);
        if (subband == subbandCount - 1) {
            currentSubbandSize += arrayLen - (startIndex + currentSubbandSize);  // We add the remaining elements
        }


        previousStartIndex = startIndex;
        previousSubbandSize = q + previousSubbandSize;
        return new Pair<>(startIndex, currentSubbandSize);
    }
}
