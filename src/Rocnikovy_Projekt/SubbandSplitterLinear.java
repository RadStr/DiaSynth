package Rocnikovy_Projekt;

import RocnikovyProjektIFace.Pair;


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


    @Override
    public Pair<Integer, Integer> getSubbandIndices(int arrayLen, int subbandCount, int subband) {
        return getStartIndAndLen(arrayLen, subband);
    }

    Zoptimalizovat to, treba tak ze dam do konstruktoru binCount a pak to udelam podle toho - a to pole pak vypocitam jen jendou
    a nebudu ho vytvaret pri kazdym volani

    /**
     *
     * @param binCount is the number of bins. which is windowSize / 2 + 1
     * @param subband
     * @return
     */
    private Pair<Integer, Integer> getStartIndAndLen(int binCount, int subband) {
        // TODO: 0-th BIN
        binCount--;     // Because the first bin doesn't count
        // TODO: 0-th BIN
        Pair<Integer, Integer> retPair;
        // TODO: Doesn't work - this is basically how it is in the text - and the coef has to be around 4 and even then it doesn't work correctly
        // So I will jsut keep it as it was
        double[] arr = new double[SUBBAND_COUNT];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = i + 1;
        }
        double sum = Program.performAggregation(arr, Aggregations.SUM);
        if (binCount > sum) {
            int freeSpaceMultiplier = 2;
            while(sum * freeSpaceMultiplier < binCount) {
                freeSpaceMultiplier++;
            }
            freeSpaceMultiplier--;
            if(freeSpaceMultiplier >= 2) {
                for(int i = 0; i < arr.length - 1; i++) {
                    arr[i] *= freeSpaceMultiplier;
                }
                sum -= arr[arr.length - 1];         // Set the last bin to 0, we will set it to the real value after that
                sum *= freeSpaceMultiplier;
            }
            arr[arr.length - 1] = binCount - sum;
        } else {
            int index = arr.length - 1;
            int binsOver = (int) Math.round(sum) + (int) Math.round(arr[index]) - binCount;
            int cycle = 0;
            while (binsOver > 0) {
                arr[index]--;
                binsOver--;
                index--;
                if (index <= cycle) {
                    cycle++;
                    index = arr.length - 1;
                }
            }
        }
        sum = 0;
        for (int i = 0; i < subband; i++) {
            sum += arr[i];
        }

        double sum2 = Program.performAggregation(arr, Aggregations.SUM);

        // TODO: DEBUG
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
        retPair = new Pair((int)Math.round(sum) + 1, (int) Math.round(arr[subband]));

//        retPair = new Pair((int)Math.round(sum), (int) Math.round(arr[subband]));
        // TODO: 0-th BIN
//        }
        // TODO: Doesn't work

        return retPair;
    }
}