package analyzer.bpm;

import util.Pair;

// TODO: Just remove it - doesn't make sense to keep after I looked at the horrible code
@Deprecated     // deprecated I guess, or not deprecated but I don't know why am I not using it. The SubbandSplitter is sufficient for what I am doing (logarithm frequency splitting)
public class SubbandSplitterLogarithmic implements SubbandSplitterIFace {
    private double previousSubbandSize = 0;
    private double previousStartIndex = 0;
    private double q;

// TODO: the subband splits will be 2,2,4,8,16,32,64 .. tohle nefunguje ... to je sice logaritmicky ale neodpovida to tomu poctu subbandCount


    @Override
    public int getSubbandCount() {
        return -10;
    }

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
    public void getSubband(double[] fftMeasures, int subbandCount, int subband, double[] result) {     // TODO: Basically copied from SubbandSplitterLinear
        int numberCount;
        int currentSubbandSize;
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
        Pair<Integer, Integer> pair = getResult(numberCount, subbandCount, subband);
        startIndex = pair.getKey();
        currentSubbandSize = 2 * pair.getValue();


        if (subband == 0) {
            result[0] = fftMeasures[0];
            startIndex = 2;
            currentSubbandSize -= 2;
        } else {
            startIndex = 2 * startIndex;        // TODO: Not sure about this
            if (subband == subbandCount - 1) {
                currentSubbandSize += fftMeasures.length - (startIndex + currentSubbandSize);  // TODO: Asi ok      // We add the remaining elements
                result[1] = fftMeasures[1];
//                if (fftResult.length % 2 == 0) {
//                    currentSubbandSize -= 2;
//                } else {
//                    currentSubbandSize--;
//                }
            }
        }
        System.arraycopy(fftMeasures, startIndex, result, startIndex, currentSubbandSize);
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
    public Pair<Integer, Integer> getSubbandIndices(int arrayLen, int subbandCount, int subband) {
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
