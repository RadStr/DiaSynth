package Rocnikovy_Projekt;

import RocnikovyProjektIFace.Pair;


// TODO: Old BPM - it gave some nice results, but was pretty weird and incorrect, but I may incorporate it as next algorithm since the results were
// TODO: really pretty good





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
    @Override
    public int getSubbandCount() {
        return SUBBAND_COUNT;
    }
    public final double START_HZ;
    public final double SUBBAND_RANGE;

    // The constructor with all parameters.
    /**
     * The subbandCount will only used, if it will be smaller than then the maximum subband count which we is based on given
     * sample rate and startHz parameters.
     * @param sampleRate
     * @param startHz
     * @param subbandCount
     */
    public SubbandSplitter(int sampleRate, double startHz, double subbandRange, int subbandCount) {
        SAMPLE_RATE = sampleRate;
        NYQUIST_FREQ = SAMPLE_RATE / (double)2;
        startHz = Math.max(0, startHz);
        final double FREQ_RANGE = NYQUIST_FREQ - startHz;

        int i;
        if(startHz != 0) {
            i = 1;      // Already counting the first bin with startHz widrh
        }
        else {
            i = 0;      // No first bin, so we start from 0
        }
        double currSubbandRange = subbandRange;
        double coveredFrequencies = subbandRange;
        while(coveredFrequencies < FREQ_RANGE && i < subbandCount) {
            coveredFrequencies += currSubbandRange;
            i++;
            currSubbandRange *= 2;
        }

        SUBBAND_RANGE = subbandRange;
        SUBBAND_COUNT = Math.min(i, subbandCount);

        if(startHz > 0) {
            START_HZ = startHz;
        }
        else {
            START_HZ = SUBBAND_RANGE;
        }

        // TODO: Stary
//        SUBBAND_RANGE = (int)Math.ceil(FREQ_RANGE / Math.pow(2, SUBBAND_COUNT));
        // TODO: Stary
    }



    /**
     * The subbandCount will only used, if it will be smaller than then the maximum subband count which we is based on given
     * sample rate and startHz parameters.
     * @param sampleRate
     * @param startHz
     * @param subbandCount
     */
    public SubbandSplitter(int sampleRate, double startHz, int subbandCount) {
        SAMPLE_RATE = sampleRate;
        NYQUIST_FREQ = SAMPLE_RATE / (double)2;
        SUBBAND_COUNT = subbandCount;
        final double FREQ_RANGE;

        if(startHz != 0) {
            FREQ_RANGE = NYQUIST_FREQ - startHz;
            subbandCount--;
        }
        else {
            FREQ_RANGE = NYQUIST_FREQ;
        }
        // the divider is geometric series (sum of 2's, starting from 0 and ending at SUBBAND_COUNT - 1)
        double geometricSumFraction = (1 - Math.pow(2, subbandCount)) / -1;
        // +1 because we are going from 0
        SUBBAND_RANGE = FREQ_RANGE / (1 + 2 * geometricSumFraction);
        // TODO: Stary
//        SUBBAND_RANGE = FREQ_RANGE / (1 << SUBBAND_COUNT);
        // TODO: Stary

        if(startHz != 0) {
            START_HZ = startHz;
        }
        else {
            START_HZ = SUBBAND_RANGE;
        }
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
        binCount--;     // Because we don't use the 0-th bin
        Pair<Integer, Integer> retPair;
        int len;
        double subbandRangeInHz;

        // binCount - 1, because we are calculating the jump.
        double jumpHZ = NYQUIST_FREQ / binCount;

        if (subband == 0) {
            resetPreviousStartIndex();
            subbandRangeInHz = START_HZ;
        } else if (subband < SUBBAND_COUNT - 1) {
//            subbandRangeInHz = SUBBAND_RANGE * (1 << (subband - 1));
            subbandRangeInHz = SUBBAND_RANGE * Math.pow(2, subband - 1);
        } else if (subband == SUBBAND_COUNT - 1) {
//            return new Pair<>(previousStartIndex, binCount - 2 * previousStartIndex);     // TODO: To tu uz nema co delat asi kdyz uz nenasobim 2ma
            // TODO: DEBUG
//            ProgramTest.debugPrint("LAST BIN:", previousStartIndex, binCount - previousStartIndex);
//            ProgramTest.debugPrint("DELKAA-LAST:", binCount - previousStartIndex);
            // TODO: DEBUG
            return new Pair<>(previousStartIndex, binCount - previousStartIndex);
        } else {
            return null;
        }


        len = (int) Math.ceil(((subbandRangeInHz - previousHzOverflow) / jumpHZ));
        if (len <= 0) {
            // TODO: DEBUG
//            ProgramTest.debugPrint("LEN <= 0", len, subband,
//                    subbandRangeInHz - previousHzOverflow, ((subbandRangeInHz - previousHzOverflow) / jumpHZ),
//                    binCount);
            // TODO: DEBUG
            len = 1;
            previousHzOverflow = 0;
            artificallyEnlargedBins++;
        } else {
            // TODO: DEBUG
//            ProgramTest.debugPrint("DELKA:", len, "SUBBAND:", subband, subbandRangeInHz, artificallyEnlargedBins);
//            if (len == 880) {
//                int todo = 44444;
//            }
            // TODO: DEBUG
            if (deficitJump < 0) {
                if (len > 1 && subband != 0) {
                    int remainingSubbands = SUBBAND_COUNT - subband;
                    // For geometric series of p 2 * sum of 2^n when n goes from 0 to remainingSubbands
                    if(artificallyEnlargedBins == 0) {
                        deficitJump = 0;
                    }
                    else {
                        deficitJump = -(artificallyEnlargedBins) / (2 - Math.pow(2, remainingSubbands));
                        deficitJump++;      // because the sum goes from 0;
                    }
//                if(SUBBAND_COUNT < binCount / 2) {
//
//                }
//                else {
//
//                }
//                int n =
//                deficit = (SUBBAND_COUNT - subband) / (double)artificallyEnlargedBins;


                    // TODO: DEBUG
//                    ProgramTest.debugPrint("DEFICIT JUMP:", deficitJump);
                    // TODO: DEBUG
                    len -= (int) deficitJump;
                    deficitJump *= 2;
                }
            } else {
                // TODO: DEBUG
//                ProgramTest.debugPrint("DEFICIT JUMP:", deficitJump);
                // TODO: DEBUG
                len -= (int) deficitJump;
                deficitJump *= 2;
            }
            previousHzOverflow += len * jumpHZ;     // += because to get how much we overshot from the starting point
            if(previousHzOverflow < subbandRangeInHz) {
                previousHzOverflow -= subbandRangeInHz;
            }
            else {
                previousHzOverflow %= subbandRangeInHz;
            }
            // TODO: OLD - vymazat
//            previousHzOverflow %= subbandRangeInHz;
            // TODO: OLD - vymazat
        }

        retPair = new Pair<>(previousStartIndex, len);
        previousStartIndex += len;
        return retPair;
    }


    private double deficitJump;
    private int artificallyEnlargedBins;
    private void resetPreviousStartIndex() {
        deficitJump = -1;
        artificallyEnlargedBins = 0;
        previousStartIndex = 1;
        previousHzOverflow = 0;
    }
}