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
    public final double NYQUIST_FREQ;
    public final int SUBBAND_COUNT;
    public final int START_HZ;
    public final double FREQ_RANGE;
    public final double SUBBAND_RANGE;


    // The constructor with all parameters.
    /**
     * The subbandCount will only used, if it will be smaller than then the maximum subband count which we is based on given
     * sample rate and startHz parameters.
     * @param sampleRate
     * @param startHz
     * @param subbandCount
     */
    public SubbandSplitter(int sampleRate, int startHz, double jumpHz, int subbandCount) {
        START_HZ = startHz;
        SAMPLE_RATE = sampleRate;
        NYQUIST_FREQ = SAMPLE_RATE / (double)2;
        FREQ_RANGE = NYQUIST_FREQ - START_HZ;

        int i = 1;
        double x = startHz;
        while(x < NYQUIST_FREQ) {
            x *= 2;
            i++;
        }
        SUBBAND_COUNT = Math.min(i, subbandCount);
        SUBBAND_RANGE = (int)Math.ceil(Math.pow(sampleRate, 1 / SUBBAND_COUNT));
    }


    /**
     * The subbandCount will only used, if it will be smaller than then the maximum subband count which we is based on given
     * sample rate and startHz parameters.
     * @param sampleRate
     * @param startHz
     * @param subbandCount
     */
    public SubbandSplitter(int sampleRate, int startHz, int subbandCount) {
        START_HZ = startHz;
        SAMPLE_RATE = sampleRate;
        NYQUIST_FREQ = SAMPLE_RATE / (double)2;
        FREQ_RANGE = NYQUIST_FREQ - START_HZ;
        SUBBAND_COUNT = subbandCount;
        SUBBAND_RANGE = FREQ_RANGE / (1 << SUBBAND_COUNT);
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
if(subband == 0) {
    startIndex = 1; // skip the 0-th bin it is just bias
    currentSubbandSize--;
}
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
        double jumpHZ = NYQUIST_FREQ / (binCount - 1);
        double jumpHz = Math.pow(NYQUIST_FREQ, 1 / binCount);

        if(subband == 0) {
            subbandRangeInHz = START_HZ;
        }
        else if (subband < SUBBAND_COUNT - 1) {
            subbandRangeInHz = START_HZ * (1 << (subband - 1));
        }
        else if(subband == SUBBAND_COUNT - 1) {
//            return new Pair<>(previousStartIndex, binCount - 2 * previousStartIndex);     // TODO: To tu uz nema co delat asi kdyz uz nenasobim 2ma
            ProgramTest.debugPrint("LAST BIN:", previousStartIndex, binCount - previousStartIndex);
            return new Pair<>(previousStartIndex, binCount - previousStartIndex);
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
        if(len == 880) {
            int todo = 44444;
        }
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




// TODO: Old BPM - it gave some nice results, but was pretty weird and incorrect, but I may incorporate it as next algorithm since the results were
// TODO: really pretty good

//package Rocnikovy_Projekt;
//
//import RocnikovyProjektIFace.Pair;
//
///**
// * the subbandCount is set based on the given sample rate (or given sample rate - check comment at constructor)
// * that means the subbandCount parameter is ignored as parameter in the methods.
// * To get the subband count just look at variable SUBBAND_COUNT.
// */
//public class SubbandSplitter implements SubbandSplitterIFace {
//    private int previousStartIndex = 0;
//    private double previousHzOverflow;
//    private final int SAMPLE_RATE;
//    public final int SUBBAND_COUNT;
//    public final int START_HZ;
//
//    /**
//     * The subbandCount will only used, if it will be smaller than then the maximum subband count which we is based on given
//     * sample rate and startHz parameters.
//     * @param sampleRate
//     * @param startHz
//     * @param subbandCount
//     */
//    public SubbandSplitter(int sampleRate, int startHz, int subbandCount) {
//        this.SAMPLE_RATE = sampleRate;
//        SUBBAND_COUNT = Math.min(subbandCount, Program.getFirstPowerExponentOfNBeforeNumber(startHz, sampleRate, 2));
//        START_HZ = startHz;
//    }
//
//
//    @Override
//    public void getSubband(double[] fftResult, int subbandCount, int subband, double[] result) {
//        int currentSubbandSize;
//        int startIndex;
//
//        Pair<Integer, Integer> pair = getStartIndAndLen(fftResult.length, subband);
//        startIndex = pair.getKey();
//        currentSubbandSize = pair.getValue();
//
//        if (subband != SUBBAND_COUNT - 1) {
//            currentSubbandSize *= 2;
//        }
//        startIndex *= 2;
//
//
////        if (subband == 0) {
////            currentSubbandSize *= 2;
////            result[0] = fftResult[0];
////            startIndex = 2;
////            currentSubbandSize -= 2;
////        }
////        else {
////            if (subband != SUBBAND_COUNT - 1) {
////                currentSubbandSize *= 2;
////            }
////            startIndex *= 2;        // TODO: Shouldn't currentSubbandSize - 2 or -1, probably not since it isn't in the Logarithmic as well
////            if (subband == SUBBAND_COUNT - 1) {
////                result[1] = fftResult[1];
////            }
////        }
//
//// TODO: DEBUG
///*
//        double jumpHzTODO = (double)SAMPLE_RATE / fftResult.length;
//        System.out.println("Inside:\t" + subband + "\t" + startIndex + "\t" + currentSubbandSize + "\t" + (startIndex/2 * jumpHzTODO) + "\t" + jumpHzTODO);
///**/
//        System.arraycopy(fftResult, startIndex, result, startIndex, currentSubbandSize);
//    }
//
//    private Pair<Integer, Integer> getStartIndAndLen(int windowSize, int subband) {
//        Pair<Integer, Integer> retPair;
//        setPreviousStartIndex(subband);
//        int len;
//        int subbandRangeInHz;
//        // It is divided by arrayLen because, we can analyze only up to nyquist frequency which is SAMPLE_RATE / 2 and
//        // every complex number is made of 2 numbers so we will get up to nyquist frequency from the arrayLen / 2 complex numbers
//        // When arrayLen == windowSize == number of samples put to FFT
//        double jumpHZ = (double) SAMPLE_RATE / windowSize;
//
//        if(subband == 0) {
//            subbandRangeInHz = START_HZ;
//        }
//        else if (subband < SUBBAND_COUNT - 1) {
//            subbandRangeInHz = START_HZ  * (1 << (subband - 1));
//        }
//        else if(subband == SUBBAND_COUNT - 1) {
//            return new Pair<>(previousStartIndex, windowSize - 2 * previousStartIndex);
//        }
//        else {
//            return null;
//        }
//
//
//        len = (int)Math.ceil((subbandRangeInHz - previousHzOverflow) / jumpHZ);
//        previousHzOverflow += len * jumpHZ;     // += because to get how much I overshot from the starting point
//        previousHzOverflow %= subbandRangeInHz;
//
//        retPair = new Pair<>(previousStartIndex, len);
//        previousStartIndex += len;
//
//        return retPair;
//    }
//
//
//    @Override
//    public Pair<Integer, Integer> getSubbandIndices(int arrayLen, int subbandCount, int subband) {
//        return getStartIndAndLen(arrayLen, subband);
//    }
//
//    private void setPreviousStartIndex(int subband) {
//        if(subband == 0) {
//            previousStartIndex = 0;
//            previousHzOverflow = 0;
//        }
//    }
//}