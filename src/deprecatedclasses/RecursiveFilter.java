package deprecatedclasses;

@Deprecated
public class RecursiveFilter {
    private RecursiveFilter() {}        // Allow only static access


    /**
     * Performs recursive filter, result is returned in new array. Recursive filter is this (y[n] is n-th output sample and x[n] is n-th input sample):
     * <br>
     * y[n] = coef[0] * x[n - coef.length + 1] + ... + coef[coef.length] x[n] + coefOutput[0] * y[n - coefOutput.length] + coefOutput[coefOutput.length] * y[n-1]
     * @param samples is the input array. It isn't changed.
     * @param coef are the coefficients for the input samples. The last index contains index for the currently computed output. The first index is the (coef.length+1)-th before the current sample.
     * @param coefOutput are the coefficients for the output samples. The last index contains index for the currently for the output before the currently calculated one. The first index is the coef.length -th before the current sample.
     * @return Returns new array gotten from input samples array by recursive filter.
     */
    public static byte[] performRecursiveFilter(byte[] samples, double[] coef, double[] coefOutput) {
        byte[] retArr = new byte[samples.length];
        byte val;
        int index;
        int startingCoefInd;
        int startingCoefOutputInd;
        int len = Math.max(coef.length, coefOutput.length);

        // Filter for the first indexes is a bit different, since they dont have all the preceding samples for the filtering.
        // It's for optimization because we need to check if there are the preceding samples.
        startingCoefInd = -coef.length + 1;		// +1 because the current sample can be used (Simple check of correctness is if we had just 1 coef)
        startingCoefOutputInd = -coefOutput.length;
        int i;
        for(i = 0; i < len; i++, startingCoefInd++, startingCoefOutputInd++) {
            val = 0;
            index = startingCoefInd;
            for(int j = 0; j < coef.length; j++, index++) {
                if(index >= 0) {
                    val += coef[j] * samples[index];
                }
            }

            index = startingCoefOutputInd;
            for(int j = 0; j < coefOutput.length; j++, index++) {
                if(index >= 0) {
                    val += coefOutput[j] * retArr[index];
                }
            }

            retArr[i] = val;
        }

        // Now we just perform do filtering for the rest, we don't need to check for the preceding elements anymore.
        startingCoefInd = 0;
        for(; i < samples.length; i++, startingCoefInd++, startingCoefOutputInd++) {
            val = 0;
            index = startingCoefInd;
            for(int j = 0; j < coef.length; j++, index++) {
                val += coef[j] * samples[index];
            }

            index = startingCoefOutputInd;
            for(int j = 0; j < coefOutput.length; j++, index++) {
                if(index >= 0) {
                    val += coefOutput[j] * retArr[index];
                }
            }

            retArr[i] = val;
        }

        return retArr;
    }
}
