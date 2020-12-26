package util.audio.filter;

import util.Utilities;
import util.audio.AudioConverter;
import util.audio.AudioUtilities;

import java.io.IOException;

public class NonRecursiveFilter {
    private NonRecursiveFilter() {}          // Allow only static access


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Moving window average
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Performs moving window average on windows of size windowSize. Moving window average averages last
     * windowSize samples and the average is stored in the last sample of window. Running average is special
     * Changes the input array. Note running average is filter, but this implementation is more efficient.
     * @param samples is the input array to perform the window average on.
     * @param windowSize is the size of window on which the averaging will be performed.
     *                   For example if == 1 then we average just 1 sample in each channel, so the output won't change.
     *
     * @deprecated Old method works only for mono
     */
    public static void performMovingWindowAverageByRef(double[] samples, int windowSize) {
        double[] oldSampleValues = new double[windowSize];
        double windowSum = 0;
        int i = 0;
        for(; i < windowSize; i++) {		// Sum of first window
            windowSum += samples[i];
            oldSampleValues[i] = samples[i];
        }
        i--;	// We are calculating the average from values including the "current" one


        // Now we will just move the window (subtract first element of that window and add the last one)
        int firstIndexInWindow = 0;
        // 2 Variants because of optimization
        if(windowSize % 2 == 0) {
            for(; i < samples.length - 1; i++, firstIndexInWindow++) {
                samples[i] = windowSum / windowSize;
                windowSum = windowSum - oldSampleValues[firstIndexInWindow % windowSize] + samples[i+1];
                oldSampleValues[firstIndexInWindow % windowSize] = samples[i+1];
            }
        }
        else {
            for(; i < samples.length - 1; i++, firstIndexInWindow++) {
                samples[i] = windowSum / windowSize;
                if(firstIndexInWindow == windowSize) {
                    firstIndexInWindow = 0;
                }
                windowSum = windowSum - oldSampleValues[firstIndexInWindow] + samples[i+1];
                oldSampleValues[firstIndexInWindow] = samples[i+1];
            }
        }
        samples[samples.length - 1] = windowSum / windowSize;
    }

    /**
     * Performs moving window average on windows of size windowSize. Moving window average averages last
     * windowSize samples and the average is stored in the last sample of window.
     * Changes the input array.
     * @param samples is the input array to perform the window average on.
     * @param windowSize is the size of window on which the averaging will be performed.
     *                   For example if == 1 then we average just 1 sample in each channel, so the output won't change.
     * @param numberOfChannels is the number of channels.
     */
    public static void performMovingWindowAverageByRef(double[] samples, int windowSize, int numberOfChannels) {
        double[][] oldSampleValues = new double[numberOfChannels][windowSize];
        double[] windowSum = new double[numberOfChannels];
        int sampleInd = 0;
        int indexCheck = windowSize * windowSum.length;
        if(indexCheck > samples.length) {
            return;
        }
        for(int i = 0; i < windowSize; i++) {		// Sum of first window
            for(int ch = 0; ch < windowSum.length; ch++, sampleInd++) {
                windowSum[ch] += samples[sampleInd];
                oldSampleValues[ch][i] = samples[sampleInd];
            }
        }
        sampleInd -= numberOfChannels;		// We are calculating the average from values including the "current" one


        // Now we will just move the window (subtract first element of that window and add the last one)
        int firstIndexInWindow = 0;
        double oldVal;
        // 2 Variants because of optimization
        if(windowSize % 2 == 0)
        {
            for(; sampleInd < samples.length - numberOfChannels; firstIndexInWindow++) {
                for(int ch = 0; ch < numberOfChannels; ch++, sampleInd++) {
                    samples[sampleInd] = windowSum[ch] / windowSize;
                    windowSum[ch] = windowSum[ch] - oldSampleValues[ch][firstIndexInWindow % windowSize] +
                                    samples[sampleInd + numberOfChannels];
                    oldSampleValues[ch][firstIndexInWindow % windowSize] = samples[sampleInd + numberOfChannels];
                }
            }
        }
        else {
            for(; sampleInd < samples.length - numberOfChannels; firstIndexInWindow++) {
                for(int ch = 0; ch < numberOfChannels; ch++, sampleInd++) {
                    samples[sampleInd] = windowSum[ch] / windowSize;
                    if (firstIndexInWindow == windowSize) {
                        firstIndexInWindow = 0;
                    }
                    windowSum[ch] = windowSum[ch] - oldSampleValues[ch][firstIndexInWindow] +
                            samples[sampleInd + numberOfChannels];
                    oldSampleValues[ch][firstIndexInWindow] = samples[sampleInd + numberOfChannels];
                }
            }
        }
        for(int i = 0; i < windowSum.length; i++, sampleInd++) {
            samples[sampleInd] = windowSum[i] / windowSize;
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Moving window average
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Non-recursive filter main methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Performs non-recursive filter, result is returned in new array. Non-recursive filter is this
     * (y[n] is n-th output sample and x[n] is n-th input sample):
     * <br>
     * y[n] = coef[0] * x[n - coef.length + 1] + ... + coef[coef.length] x[n]
     * @param samples is the input array. It isn't changed.
     * @param coef are the coefficients for the input samples. The last index contains index for the currently
     *             computed output. The first index is the (coef.length+1)-th before the current sample.
     * @param numberOfChannels represents the number of channels
     * @param sampleSize is the size of 1 sample
     * @param frameSize is the size of 1 frame
     * @param isBigEndian true if the samples are in big endian, false otherwise.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @return Returns new array gotten from input samples array by non-recursive filter.
     * @throws IOException is thrown by method calculateMask if the sampleSize is invalid.
     */
    @Deprecated
    public static byte[] performNonRecursiveFilter(byte[] samples, double[] coef, int numberOfChannels,
                                                   int sampleSize, int frameSize,
                                                   boolean isBigEndian, boolean isSigned) throws IOException {
        byte[] retArr = new byte[samples.length];
        int[] vals = new int[numberOfChannels];
        int index;
        int startingCoefInd;
        int sample;
        byte[] sampleBytes = new byte[sampleSize];
        int mask = AudioUtilities.calculateMask(sampleSize);

        // Filter for the first indexes is a bit different,
        // since they dont have all the preceding samples for the filtering.
        // It's for optimization because we need to check if there are the preceding samples.
        // +frameSize because the current sample can be used (Simple check of correctness is if we had just 1 coef)
        startingCoefInd = -coef.length * frameSize + frameSize;
        int resInd;
        int coefInd;
        for(resInd = 0, coefInd = 0; coefInd < coef.length - 1; startingCoefInd += frameSize, coefInd++) {
            // Covers the case when there is more coefficients than frames,
            // but sample.length is expected to be containing only full frames,
            // that is samples.length % frameSize == 0
            if(resInd >= retArr.length) {
                return retArr;
            }
            for(int ch = 0; ch < vals.length; ch++) {
                vals[ch] = 0;
            }
            index = startingCoefInd;
            for (int j = 0; j < coef.length; j++) {
                if (index >= 0) {
                    for (int ch = 0; ch < vals.length; ch++, index += sampleSize) {
                        sample = AudioConverter.convertBytesToInt(samples, sampleSize, mask, index,
                                isBigEndian, isSigned);
                        vals[ch] += coef[j] * sample;
                    }
                }
                else {
                    index += frameSize;
                }
            }

            for (int ch = 0; ch < vals.length; ch++) {
                AudioConverter.convertIntToByteArr(sampleBytes, vals[ch], isBigEndian);
                for(int j = 0; j < sampleBytes.length; j++, resInd++) {
                    retArr[resInd] = sampleBytes[j];
                }
            }
        }

        // Now we just perform do filtering for the rest, we don't need to check for the preceding elements anymore.
        for(; resInd < retArr.length; startingCoefInd += frameSize) {
            for(int ch = 0; ch < vals.length; ch++) {
                vals[ch] = 0;
            }
            index = startingCoefInd;
            for(int j = 0; j < coef.length; j++) {
                for (int ch = 0; ch < vals.length; ch++, index += sampleSize) {
                    sample = AudioConverter.convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
                    vals[ch] += coef[j] * sample;
                }
            }

            for (int ch = 0; ch < vals.length; ch++) {
                AudioConverter.convertIntToByteArr(sampleBytes, vals[ch], isBigEndian);
                for(int j = 0; j < sampleBytes.length; j++, resInd++) {
                    retArr[resInd] = sampleBytes[j];
                }
            }
        }

        return retArr;
    }


    /**
     * Performs non-recursive filter on input array, result is returned in output array
     * (Input and output array can be the same).
     * Non-recursive filter is this (y[n] is n-th output sample and x[n] is n-th input sample):
     * <br>
     * y[n] = coef[0] * x[n - coef.length + 1] + ... + coef[coef.length] x[n]
     * @param samples is the input array. It isn't changed.
     * @param coef are the coefficients for the input samples. The last index contains index for the currently
     *             computed output. The first index is the (coef.length+1)-th before the current sample.
     * @param numberOfChannels represents the number of channels
     * @param retArr is he array which will contain the result of filter.
     * @param retArrStartIndex is the start index in the output array (retArr) - inclusive
     * @param retArrEndIndex is the end index in the output array (retArr) - exclusive
     * @return Returns -1 if the output array was shorter than length of coefs array else returns 1.
     * Returns -2 if the input array isn't long enough. If 1 is returned the result of filter is in retArr.
     * Else the retArr isn't changed in any way.
     */
    // Implementation note: since retArrEndIndex is exclusive,
    // we need to use retArrEndIndex - 1 when we are referring to valid indices
    public static int performNonRecursiveFilter(double[] samples, int samplesStartIndex,
                                                double[] coef, int numberOfChannels,
                                                double[] retArr, int retArrStartIndex, final int retArrEndIndex) {
        int bufferLen = 4096;
        bufferLen = Math.max(bufferLen, Utilities.getFirstPowerOfNAfterNumber(coef.length, 2));
        int indexToStopCopyFrom = bufferLen;
        int indexCountToWaitWithForNextIteration = coef.length - 1;
        bufferLen += indexCountToWaitWithForNextIteration;

        double[][] vals = new double[numberOfChannels][bufferLen];
        int index;
        int startingCoefInd;

        // Filter for the first indexes is a bit different, since they dont have all the preceding samples for the filtering.
        // It's for optimization because we need to check if there are the preceding samples.
        startingCoefInd = samplesStartIndex + -indexCountToWaitWithForNextIteration * numberOfChannels;
        int resInd = retArrStartIndex;

        if (retArrStartIndex + numberOfChannels * coef.length >= retArrEndIndex) {
            return -1;
        }
        if (samplesStartIndex + numberOfChannels * coef.length >= samples.length ||
                retArrEndIndex - retArrStartIndex > samples.length - samplesStartIndex) {
            return -2;
        }

        Utilities.resetTwoDimArr(vals, 0, vals[0].length);
        for (int i = 0, coefInd = 0; i < vals[0].length;
             i++, startingCoefInd += numberOfChannels, coefInd++) {
            index = startingCoefInd;
            if (coefInd >= indexCountToWaitWithForNextIteration) {
                break;
            }
            for (int j = 0; j < coef.length; j++) {
                if (index >= 0) {
                    for (int ch = 0; ch < vals.length; ch++, index++) {
                        vals[ch][i] += coef[j] * samples[index];
                    }
                } else {
                    index += numberOfChannels;
                }
            }
        }
        // Now we just perform do filtering for the rest, we don't need to check for the preceding elements anymore.
        int firstInvalidIndexInChannel = -1;
        for ( ; resInd < retArrEndIndex - 1; ) {
            // This represents the current result index (where we are in the out array currently)
            int virtualResInd = resInd + indexCountToWaitWithForNextIteration * vals.length;

            for (int i = indexCountToWaitWithForNextIteration; i < vals[0].length;
                 i++, startingCoefInd += numberOfChannels, virtualResInd += numberOfChannels) {
                if (virtualResInd >= retArrEndIndex - 1) {
                    firstInvalidIndexInChannel = i;
                    break;
                }
                index = startingCoefInd;
                for (int j = 0; j < coef.length; j++) {
                    for (int ch = 0; ch < vals.length; ch++, index++) {
                        vals[ch][i] += coef[j] * samples[index];
                    }
                }
            }

            resInd = setRetArrInLowPassFilter(resInd, firstInvalidIndexInChannel, vals, indexToStopCopyFrom, retArr);
            for (int ch = 0; ch < vals.length; ch++) {
                System.arraycopy(vals[ch], indexToStopCopyFrom, vals[ch], 0,
                                 indexCountToWaitWithForNextIteration);
            }
            Utilities.resetTwoDimArr(vals, indexCountToWaitWithForNextIteration, vals[0].length);
        }

        return 1;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Non-recursive filter main methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Low-pass filter methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static int setRetArrInLowPassFilter(int resInd, int firstInvalidIndexInChannel, double[][] vals,
                                                int endIndex, double[] retArr) {
        int startResInd = resInd;
        final int resIndAndMethodStart = resInd;
        int len;
        if(firstInvalidIndexInChannel < 0) {
            len = endIndex;
        }
        else {
            len = firstInvalidIndexInChannel;
        }
        for (int ch = 0; ch < vals.length; ch++, startResInd++, resInd = startResInd) {
            for (int i = 0; i < len; i++, resInd += vals.length) {
                retArr[resInd] = vals[ch][i];
            }
        }

        return resIndAndMethodStart + len * vals.length;
    }


    /**
     * Performs low pass filtering with cutoffFreq on given samples, which are supposed to be sampled at sampleRate.
     * @param samples are the samples to perform the low pass filter on.
     * @param cutoffFreq is the cut-off frequency of the filter.
     * @param coefCount is the number of the coefficients used for filtering
     *                  (How many last samples should be used for calculating the current one in the filter).
     *                  Usually the more the better filter.
     * @param sampleRate is the sampling rate of the given samples
     * @param numberOfChannels represents the number of channels
     * @param sampleSize is the size of 1 sample
     * @param frameSize is the size of 1 frame
     * @param isBigEndian true if the samples are in big endian, false otherwise.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @return Returns copy of the samples array on which was performed low pass filter.
     * @throws IOException is thrown by method calculateMask if the sampleSize is invalid.
     */
    @Deprecated
    public static byte[] runLowPassFilter(byte[] samples, double cutoffFreq, int coefCount, int sampleRate,
                                          int numberOfChannels, int sampleSize, int frameSize,
                                          boolean isBigEndian, boolean isSigned) throws IOException {
        double[] coef = calculateCoefForLowPass(cutoffFreq, coefCount, sampleRate);
        return performNonRecursiveFilter(samples, coef, numberOfChannels, sampleSize, frameSize, isBigEndian, isSigned);
    }


    public static int runLowPassFilter(double[] samples, int samplesStartIndex,
                                       int numberOfChannels, int sampleRate,
                                       double cutoffFreq, int coefCount,
                                       double[] retArr, int retArrStartIndex, int retArrEndIndex) {
        double[] coef = calculateCoefForLowPass(cutoffFreq, coefCount, sampleRate);
        int retVal = performNonRecursiveFilter(samples, samplesStartIndex,
                coef, numberOfChannels, retArr, retArrStartIndex, retArrEndIndex);
        return retVal;
    }


    /**
     * Caculates coefficients for low pass filter and returns them in array.
     * @param cutOffFreq is the cut-off frequency of the filter
     * @param coefCount is the number of the coefficients which will be returned
     * @param sampleRate is the sampling rate of the samples on which will be used filtering.
     * @return Returns double array containing the coefficients for non-recursive low pass filtering.
     */
    public static double[] calculateCoefForLowPass(double cutOffFreq, int coefCount, int sampleRate) {
        double[] coefForCalc = new double[coefCount];
        double[] coef = new double[coefCount];
        int jump = sampleRate / coefCount;
        int currFreq = 0;

        // Calculate values which are used for calculating the coefficients for filters
        int index = 0;
        while(currFreq < cutOffFreq) {
            coefForCalc[index] = 1;
            currFreq += jump;
            index++;
        }
        if(index < coefForCalc.length) {   // So the jump in filter isn't abrupt (Page 208 Dodge - Computer music)
            coefForCalc[index] = 1 / (double)2;
            index++;
        }
        for(; index < coefForCalc.length; index++) {
            coefForCalc[index] = 0;
        }

        // Calculate coefs for filter
        for(int k = 0; k < (coefCount - 1) / (double)2; k++) {      // From Page 206 Dodge
            double currCoef = 0;
            for(int i = 1; i < (coefCount - 1) / (double)2; i++) {
                double tmp = (2 * Math.PI * i / coefCount) * (k - (coefCount - 1) / (double)2);
                currCoef += Math.abs(coefForCalc[i]) * Math.cos(tmp);
            }
            coef[k] =  coefForCalc[0] + 2 * currCoef;
            coef[k] /= coefCount;
        }

        // The rest is symmetric
        for(int k = 0; k < (coefCount - 1) / (double)2; k++) {
            coef[coefCount - k - 1] = coef[k];
        }

        // Normalize the coefficients
        double sum = 0;
        for(int i = 0; i < coef.length; i++) {
            sum += coef[i];
        }
        if(sum > 1) {
            for (int i = 0; i < coef.length; i++) {
                coef[i] /= sum;
            }
        }
        // Now we reverse the array
        Utilities.reverseArr(coef);
        return coef;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Low-pass filter methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
