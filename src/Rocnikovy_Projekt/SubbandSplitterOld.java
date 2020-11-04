package Rocnikovy_Projekt;



import RocnikovyProjektIFace.Pair;

/**
 * the subbandCount is set based on the given sample rate (or given sample rate - check comment at constructor)
 * that means the subbandCount parameter is ignored as parameter in the methods.
 * To get the subband count just look at variable SUBBAND_COUNT.
 */
@Deprecated
public class SubbandSplitterOld implements SubbandSplitterIFace {
    private int previousStartIndex = 0;
    private double previousHzOverflow;
    private final int SAMPLE_RATE;
    public final int SUBBAND_COUNT;
    @Override
    public int getSubbandCount() {
        return SUBBAND_COUNT;
    }
    public final int START_HZ;

    /**
     * The subbandCount will only used, if it will be smaller than then the maximum subband count which we is based on given
     * sample rate and startHz parameters.
     * @param sampleRate
     * @param startHz
     * @param subbandCount
     */
    public SubbandSplitterOld(int sampleRate, int startHz, int subbandCount) {
        this.SAMPLE_RATE = sampleRate;
        SUBBAND_COUNT = Math.min(subbandCount, Program.getFirstPowerExponentOfNBeforeNumber(startHz, sampleRate, 2));
        START_HZ = startHz;
    }



//    private Pair<Integer, Integer> getStartIndAndLen(int windowSize, int subband) {
//        Pair<Integer, Integer> retPair;
//        int len;
//        double subbandRangeInHz;
//        // It is divided by arrayLen because, we can analyze only up to nyquist frequency which is SAMPLE_RATE / 2 and
//        // every complex number is made of 2 numbers so we will get up to nyquist frequency from the arrayLen / 2 complex numbers
//        // When arrayLen == windowSize == number of samples put to FFT
//        double jumpHZ = (double) SAMPLE_RATE / windowSize;
//
//        if(subband == 0) {
//            subbandRangeInHz = START_HZ;
//            previousStartIndex = 0;        // Because the first bin doesn't count
//            previousHzOverflow = 0;
//        }
//        else if (subband < SUBBAND_COUNT - 1) {
//            subbandRangeInHz = START_HZ  * (1 << (subband - 1));
//        }
//        else if(subband == SUBBAND_COUNT - 1) {
//            return new Pair<>(previousStartIndex, windowSize - previousStartIndex);
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

//    private Pair<Integer, Integer> getStartIndAndLen(int windowSize, int subband) {
//        Pair<Integer, Integer> retPair;
//        int len;
//        double subbandRangeInHz;
//        // It is divided by arrayLen because, we can analyze only up to nyquist frequency which is SAMPLE_RATE / 2 and
//        // every complex number is made of 2 numbers so we will get up to nyquist frequency from the arrayLen / 2 complex numbers
//        // When arrayLen == windowSize == number of samples put to FFT
//        double jumpHZ = (double) SAMPLE_RATE / windowSize;           Mam tu dat windowSize -1???
//
//        if(subband == 0) {
//            subbandRangeInHz = START_HZ;
//            previousStartIndex = 1;    OK     // Because the first bin doesn't count
//            previousHzOverflow = 0;
//        }
//        else if (subband < SUBBAND_COUNT - 1) {
//            subbandRangeInHz = START_HZ  * (1 << (subband - 1));
//        }
//        else if(subband == SUBBAND_COUNT - 1) {
//            tady nema byt 2 * ale - tak to ted vyzkousim kdyz tma neni to 2x jakej z toho bude vysledek mozna to nejde
//                    (nejde jako ze nesedi ty vysledky) prave kvuli tomuhle.
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


    TODO: TODO_COUNTER DAT PRYC
    private int TODO_COUNTER = 0;


    private Pair<Integer, Integer> getStartIndAndLen(int windowSize, int subband) {
        windowSize--;           // Because we don't use the 0-th bin.
        Pair<Integer, Integer> retPair;
        int len;
        double subbandRangeInHz;
        // It is divided by arrayLen because, we can analyze only up to nyquist frequency which is SAMPLE_RATE / 2 and
        // every complex number is made of 2 numbers so we will get up to nyquist frequency from the arrayLen / 2 complex numbers
        // When arrayLen == windowSize == number of samples put to FFT
//        double jumpHZ = (double) SAMPLE_RATE / windowSize;
        double jumpHZ = (double) SAMPLE_RATE / windowSize;

        if(subband == 0) {


            TODO_COUNTER++;


            subbandRangeInHz = START_HZ;
            previousStartIndex = 1;         // Because the first bin doesn't count
            previousHzOverflow = 0;
        }
        else if (subband < SUBBAND_COUNT - 1) {
            subbandRangeInHz = START_HZ  * (1 << (subband - 1));
        }
        else if(subband == SUBBAND_COUNT - 1) {
            return new Pair<>(previousStartIndex, windowSize - previousStartIndex);
        }
        else {
            return null;
        }


        len = (int)Math.ceil((subbandRangeInHz - previousHzOverflow) / jumpHZ);
        previousHzOverflow += len * jumpHZ;     // += because to get how much I overshot from the starting point
        previousHzOverflow %= subbandRangeInHz;

        retPair = new Pair<>(previousStartIndex, len);
        previousStartIndex += len;


        // TODO: DEBUG
        if(TODO_COUNTER < 2) {
            ProgramTest.debugPrint("GET_SUBBAND:", subband, SUBBAND_COUNT, retPair.getKey(), retPair.getValue());
        }
        // TODO: DEBUG
        return retPair;
    }


    @Override
    public Pair<Integer, Integer> getSubbandIndices(int arrayLen, int subbandCount, int subband) {
        return getStartIndAndLen(arrayLen, subband);
    }
}
