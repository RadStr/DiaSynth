package Rocnikovy_Projekt;

import RocnikovyProjektIFace.Pair;

public class SubbandSplitterConstant implements SubbandSplitterIFace {
    static int subbandCountValid = 6;
    private int previousStartIndex = 0;
    private double previousHzOverFlow;
    private int sampleRate;

    public SubbandSplitterConstant(int sampleRate) {
        this.sampleRate = sampleRate;
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
            if (subband != subbandCountValid - 1) {
                currentSubbandSize *= 2;
            }
            startIndex = 2 * startIndex;        // TODO: Shouldn't currentSubbandSize - 2 or -1, probably not since it isn't in the Logarithmic as well
            if (subband == subbandCountValid - 1) {
                result[1] = fftResult[1];
            }
        }

// TODO: DEBUG
/*
        double jumpHzTODO = (double)sampleRate / fftResult.length;
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
        // It is divided by arrayLen because, we can analyze only up to nyquist frequency which is sampleRate / 2 and
        // every complex number is made of 2 numbers so we will get up to nyquist frequency from the arrayLen / 2 complex numbers
        // When arrayLen == windowSize == number of samples put to FFT
        double jumpHZ = (double)sampleRate / windowSize;

        switch (subband) {
            case 0:
            case 1:
                subbandRangeInHz = 200;
                break;
            case 2:
                subbandRangeInHz = 400;
                break;
            case 3:
                subbandRangeInHz = 800;
                break;
            case 4:
                subbandRangeInHz = 1600;
                break;
            case 5:
// TODO: DEBUG                System.out.println("::::::" + windowSize + "\t" + previousStartIndex + "\t" + (windowSize - 2*previousStartIndex));
                return new Pair<>(previousStartIndex, windowSize - 2*previousStartIndex);
            default:
                return null;
        }


        len = (int)Math.ceil((subbandRangeInHz - previousHzOverFlow) / jumpHZ);
        previousHzOverFlow += len * jumpHZ;     // += because to get how much I overshot from the starting point
        previousHzOverFlow %= subbandRangeInHz;

        retPair = new Pair<>(previousStartIndex, len);
        previousStartIndex += len;

        return retPair;
    }

    private void setPreviousStartIndex(int subband) {
        if(subband == 0) {
            previousStartIndex = 0;
            previousHzOverFlow = 0;
        }
    }
}
