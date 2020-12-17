package analyzer.bpm;

import util.Pair;


/**
 * the subbandCount is set based on the given sample rate (or given sample rate - check comment at constructor)
 * that means the subbandCount parameter is ignored as parameter in the methods.
 * To get the subband count just look at variable SUBBAND_COUNT.
 */
public class SubbandSplitterLinear implements SubbandSplitterIFace {
    public final int SUBBAND_COUNT;
    @Override
    public int getSubbandCount() {
        return SUBBAND_COUNT;
    }

    public SubbandSplitterLinear(int subbandCount) {
        SUBBAND_COUNT = subbandCount;
    }

    /**
     * binCount is the number of bins without the first bin.
     */
    private int binCount = Integer.MIN_VALUE;
    private int[] binStartIndices;
    private int[] binSizes;
    public void setBinCount(int binCount) {
        this.binCount = binCount;

        binSizes = new int[SUBBAND_COUNT];
        int sum = 0;
        for (int i = 0; i < binSizes.length; i++) {
            binSizes[i] = i + 1;
            sum += binSizes[i];
        }

        if (binCount > sum) {
            int freeSpaceMultiplier = 2;
            while(sum * freeSpaceMultiplier < binCount) {
                freeSpaceMultiplier++;
            }
            freeSpaceMultiplier--;
            if(freeSpaceMultiplier >= 2) {
                for(int i = 0; i < binSizes.length - 1; i++) {
                    binSizes[i] *= freeSpaceMultiplier;
                }
                sum -= binSizes[binSizes.length - 1];         // Set the last bin to 0, we will set it to the real value after that
                sum *= freeSpaceMultiplier;
            }
            binSizes[binSizes.length - 1] = binCount - sum;
        } else {
            int index = binSizes.length - 1;
            int binsOver = (int) Math.round(sum) + (int) Math.round(binSizes[index]) - binCount;
            int cycle = 0;
            while (binsOver > 0) {
                binSizes[index]--;
                binsOver--;
                index--;
                if (index <= cycle) {
                    cycle++;
                    index = binSizes.length - 1;
                }
            }
        }

        binStartIndices = new int[binSizes.length];
        sum = 1;        // because we ignore the first bin
        for(int i = 0; i < binSizes.length; i++) {
            binStartIndices[i] = sum;
            sum += binSizes[i];
        }
    }


    @Override
    public Pair<Integer, Integer> getSubbandIndices(int arrayLen, int subbandCount, int subband) {
        return getStartIndAndLen(arrayLen, subband);
    }

    /**
     *
     * @param binCount is the number of bins. which is windowSize / 2 + 1.
     * @param subband
     * @return
     */
    private Pair<Integer, Integer> getStartIndAndLen(int binCount, int subband) {
        if(binCount - 1 != this.binCount) {
            setBinCount(binCount - 1);
        }
        Pair<Integer, Integer> retPair;

        // TODO: DEBUG
//        double sum2 = Program.performAggregation(arr, Aggregation.SUM);
//        if (sum2 != binCount) {
//            ProgramTest.debugPrint("FAILED", sum, sum2, binCount);
//            System.exit(56487);
//        }
//
//        ProgramTest.debugPrint("LINEAR DELKA:", (int)Math.round(sum) + 1,
//                (int) Math.round(arr[subband]), getSubbandCount());
        // TODO: DEBUG

        // TODO: 0-th BIN
        // + 1 for the start index, because the first bin doesn't count
//        retPair = new Pair((int)Math.round(sum) + 1, (int) Math.round(arr[subband]));
        retPair = new Pair(binStartIndices[subband], binSizes[subband]);

//        retPair = new Pair((int)Math.round(sum), (int) Math.round(arr[subband]));
        // TODO: 0-th BIN
//        }
        // TODO: Doesn't work

        return retPair;
    }
}