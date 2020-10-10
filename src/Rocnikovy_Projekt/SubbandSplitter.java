package Rocnikovy_Projekt;

import RocnikovyProjektIFace.Pair;

/**
 * the subbandCount is set based on the given sample rate (or given sample rate - check comment at constructor)
 * that means the subbandCount parameter is ignored as parameter in the methods.
 * To get the subband count just look at variable SUBBAND_COUNT.
 */
public class SubbandSplitter implements SubbandSplitterIFace {
    private int previousStartIndex = 0;
    private double previousHzOverflow;
    private final int SAMPLE_RATE;
    public final int SUBBAND_COUNT;
    public final int START_HZ;

    /**
     * The subbandCount will only used, if it will be smaller than then the maximum subband count which we is based on given
     * sample rate and startHz parameters.
     * @param sampleRate
     * @param startHz
     * @param subbandCount
     */
    public SubbandSplitter(int sampleRate, int startHz, int subbandCount) {
        this.SAMPLE_RATE = sampleRate;
        SUBBAND_COUNT = Math.min(subbandCount, Program.getFirstPowerExponentOfNBeforeNumber(startHz, sampleRate, 2));
        START_HZ = startHz;
    }


    @Override
    public void getSubbandRealForward(double[] fftResult, int subbandCount, int subband, double[] result) {
        int currentSubbandSize;
        int startIndex;

        Pair<Integer, Integer> pair = getStartIndAndLen(fftResult.length, subband);
        startIndex = pair.getKey();
        currentSubbandSize = pair.getValue();


        if (subband == 0) {
            currentSubbandSize *= 2;
            result[0] = fftResult[0];
            startIndex = 2;
            currentSubbandSize -= 2;
        }
        else {
            if (subband != SUBBAND_COUNT - 1) {
                currentSubbandSize *= 2;
            }
            startIndex *= 2;        // TODO: Shouldn't currentSubbandSize - 2 or -1, probably not since it isn't in the Logarithmic as well
            if (subband == SUBBAND_COUNT - 1) {
                result[1] = fftResult[1];
            }
        }

// TODO: DEBUG
/*
        double jumpHzTODO = (double)SAMPLE_RATE / fftResult.length;
        System.out.println("Inside:\t" + subband + "\t" + startIndex + "\t" + currentSubbandSize + "\t" + (startIndex/2 * jumpHzTODO) + "\t" + jumpHzTODO);
/**/
        System.arraycopy(fftResult, startIndex, result, startIndex, currentSubbandSize);
    }

    @Override
    public void getSubbandComplexForward(double[] fftResult, int subbandCount, int subband, double[] result) {
        int arrayLen = fftResult.length;    // TODO: It should be / 2 and then startIndex*2 and currentSubbandSize*2, but if I pass fftResult.length, then I dont have to multiply later
        Pair<Integer, Integer> pair = getStartIndAndLen(arrayLen, subband);
        int startIndex = pair.getKey();
        int currentSubbandSize = pair.getValue();
        System.arraycopy(fftResult, startIndex, result, startIndex, currentSubbandSize);
    }

    @Override
    public Pair<Integer, Integer> getSubbandIndexesRealForward(int arrayLen, int subbandCount, int subband) {
        return getStartIndAndLen(arrayLen, subband);
    }

    @Override
    public Pair<Integer, Integer> getSubbandIndexesComplexForward(int arrayLen, int subbandCount, int subband) {
        arrayLen /= 2;
        return getStartIndAndLen(arrayLen, subband);
    }

    private Pair<Integer, Integer> getStartIndAndLen(int windowSize, int subband) {
        Pair<Integer, Integer> retPair;
        setPreviousStartIndex(subband);
        int len;
        int subbandRangeInHz;
        // It is divided by arrayLen because, we can analyze only up to nyquist frequency which is SAMPLE_RATE / 2 and
        // every complex number is made of 2 numbers so we will get up to nyquist frequency from the arrayLen / 2 complex numbers
        // When arrayLen == windowSize == number of samples put to FFT
        double jumpHZ = (double) SAMPLE_RATE / windowSize;

        if(subband == 0) {
            subbandRangeInHz = START_HZ;
        }
        else if (subband < SUBBAND_COUNT - 1) {
            subbandRangeInHz = START_HZ  * 1 << (subband - 1);
        }
        else if(subband == SUBBAND_COUNT - 1) {
            return new Pair<>(previousStartIndex, windowSize - 2 * previousStartIndex);
        }
        else {
            return null;
        }


        len = (int)Math.ceil((subbandRangeInHz - previousHzOverflow) / jumpHZ);
        previousHzOverflow += len * jumpHZ;     // += because to get how much I overshot from the starting point
        previousHzOverflow %= subbandRangeInHz;

        retPair = new Pair<>(previousStartIndex, len);
        previousStartIndex += len;

        return retPair;
    }

    private void setPreviousStartIndex(int subband) {
        if(subband == 0) {
            previousStartIndex = 0;
            previousHzOverflow = 0;
        }
    }
}
