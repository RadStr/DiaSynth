package analyzer.bpm;

import util.Pair;

// Not used, I am just using the linear subbandSplitter
public class SubbandSplitterConstant implements SubbandSplitterIFace {
    private final int SUBBAND_COUNT;
    @Override
    public int getSubbandCount() {
        return SUBBAND_COUNT;
    }

    public SubbandSplitterConstant(int subbandCount) {
        SUBBAND_COUNT = subbandCount;
    }


    @Override
    public Pair<Integer, Integer> getSubbandIndices(int arrayLen, int subbandCount, int subband) {
        arrayLen--;         // Because we don't use the 0-th bin
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
