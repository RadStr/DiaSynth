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
    public final int SAMPLE_RATE;
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
    public void getSubband(double[] fftMeasures, int subbandCount, int subband, double[] result) {
        int currentSubbandSize;
        int startIndex;

        Pair<Integer, Integer> pair = getStartIndAndLen(fftMeasures.length, subband);
        startIndex = pair.getKey();
        currentSubbandSize = pair.getValue();

// TODO: DEBUG
/*
        double jumpHzTODO = (double)SAMPLE_RATE / fftResult.length;
        System.out.println("Inside:\t" + subband + "\t" + startIndex + "\t" + currentSubbandSize + "\t" + (startIndex/2 * jumpHzTODO) + "\t" + jumpHzTODO);
/**/
        System.arraycopy(fftMeasures, startIndex, result, startIndex, currentSubbandSize);
    }

    @Override
    public Pair<Integer, Integer> getSubbandIndices(int arrayLen, int subbandCount, int subband) {
        return getStartIndAndLen(arrayLen, subband);
    }

    /**
     *
     * @param binCount is the number of bins. which is windowSize / 2 + 1
     * @param subband
     * @return
     */
    private Pair<Integer, Integer> getStartIndAndLen(int binCount, int subband) {
        Pair<Integer, Integer> retPair;
        setPreviousStartIndex(subband);
        int len;
        int subbandRangeInHz;

        // Div by 2 because we go up to nyquist and binCount - 1, because we are calculating the jump.
        double jumpHZ = (SAMPLE_RATE / (double)2) / (binCount - 1);

        if(subband == 0) {
            subbandRangeInHz = START_HZ;
        }
        else if (subband < SUBBAND_COUNT - 1) {
            subbandRangeInHz = START_HZ * (1 << (subband - 1));
        }
        else if(subband == SUBBAND_COUNT - 1) {
            return new Pair<>(previousStartIndex, binCount - 2 * previousStartIndex);
        }
        else {
            return null;
        }


        len = (int)Math.ceil((subbandRangeInHz - previousHzOverflow) / jumpHZ);
        previousHzOverflow += len * jumpHZ;     // += because to get how much we overshot from the starting point
        previousHzOverflow %= subbandRangeInHz;

        retPair = new Pair<>(previousStartIndex, len);
        previousStartIndex += len;

        // TODO: DEBUG
        ProgramTest.debugPrint("DELKA:", len);
        // TODO: DEBUG
        return retPair;
    }

    private void setPreviousStartIndex(int subband) {
        if(subband == 0) {
            previousStartIndex = 0;
            previousHzOverflow = 0;
        }
    }
}
