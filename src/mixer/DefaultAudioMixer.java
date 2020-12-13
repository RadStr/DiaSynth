package mixer;

import Rocnikovy_Projekt.Program;

/**
 * Default implementation. Just implement AudioMixerIFace if overriding, the int variants and byte variants aren't used anyways.
 * Performs the simplest mixing, the result may overflow the max/min supported value of sample
 *
 */
// The best mix methods are mix(double[][] vals, double[][] multFactors, double[] finalMultFactors, int index, double[] outputArr)
// and the int variant of that, and the variant which doesn't use finalMultFactors.
// Other methods are either byte methods, which aren't the fastest since we need to convert them to int first.
// And others are help methods which can be used, but it isn't necessary
public class DefaultAudioMixer implements AudioMixerIFace, AudioMixerDoubleIFace, AudioMixerIntIFace, AudioMixerByteIFace {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Methods when the output audioFormat has more than 1 channel
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int mix(byte[][] vals, byte[] outputArr, int outputArrIndex, double[][] multFactors, int sampleSize,
                   int mask, boolean isBigEndian, boolean isSigned, int index) {
        for(int ch = 0; ch < multFactors[0].length; ch++, index += sampleSize, outputArrIndex += sampleSize) {
            int result = mix(vals, multFactors, sampleSize, mask, isBigEndian, isSigned, index, ch);
            Program.convertIntToByteArr(outputArr, result, sampleSize, outputArrIndex, isBigEndian);
        }
        return outputArrIndex;
    }

    /**
     * Mixes vals.length values to 1 sample.
     * @param vals is the 2D array with samples. vals.length is number of mixed values. And we take values from vals[][index] to vals[][index + sampleSize] and mix them
     * @param multFactors is the 2D array with factors to multiply the samples in vals with.
     * @param sampleSize is the sample size
     * @param mask is the mask used for calculation
     * @param isBigEndian true if the samples are big endian
     * @param isSigned true if the samples are signed
     * @param index is the index in the 2nd dimension of vals array. (The index says which sample it is)
     * @param channel is the channel from which should be taken multFactors (multFactors[][channel])
     */
    protected int mix(byte[][] vals, double[][] multFactors, int sampleSize, int mask, boolean isBigEndian, boolean isSigned,
                   int index, int channel) {
        int sample = Program.convertBytesToInt(vals[0], sampleSize, mask, index, isBigEndian, isSigned);
        sample = mixOneVal(sample, multFactors[0][channel]);
        int result = sample;
        for (int i = 1; i < vals.length; i++) {
            sample = Program.convertBytesToInt(vals[i], sampleSize, mask, index, isBigEndian, isSigned);
            sample = mixOneVal(sample, multFactors[i][channel]);
            result = mix(result, sample);
        }

        return result;
    }


    @Override
    public void mix(int[][] vals, double[][] multFactors, int index, int[] outputArr) {
        for(int ch = 0; ch < multFactors[0].length; ch++) {
            outputArr[ch] = mix(vals, multFactors, index, ch);
        }
    }
    // Same as int but with double[] vals and outputArr
    @Override
    public void mix(double[][] vals, double[][] multFactors, int index, double[] outputArr) {
        for(int ch = 0; ch < multFactors[0].length; ch++) {
            outputArr[ch] = mix(vals, multFactors, index, ch);
        }
    }


    // Advanced variants with finalMultFactors
    @Override
    public void mix(int[][] vals, double[][] multFactors, double[] finalMultFactors, int index, int[] outputArr) {
        if(finalMultFactors == null) {
            mix(vals, multFactors, index, outputArr);
        }
        else {
            for (int ch = 0; ch < multFactors[0].length; ch++) {
                outputArr[ch] = mix(vals, multFactors, index, ch);
                outputArr[ch] = (int) (outputArr[ch] * finalMultFactors[ch]);
            }
        }
    }
    // Same as int but with double[] vals and outputArr
    @Override
    public void mix(double[][] vals, double[][] multFactors, double[] finalMultFactors, int index, double[] outputArr) {
        if(finalMultFactors == null) {
            mix(vals, multFactors, index, outputArr);
        }
        else {
            for (int ch = 0; ch < multFactors[0].length; ch++) {
                outputArr[ch] = mix(vals, multFactors, index, ch);
                outputArr[ch] = outputArr[ch] * finalMultFactors[ch];
            }
        }
    }

    // Help methods
    public int mix(int[][] vals, double[][] multFactors, int channel, int index) {
        int result = mixOneVal(vals[0][index], multFactors[0][channel]);
        for(int i = 1; i < multFactors.length; i++) {
            int val = mixOneVal(vals[i][index], multFactors[i][channel]);
            result = mix(result, val);
        }
        return result;
    }

    public double mix(double[][] vals, double[][] multFactors, int channel, int index) {
        double result = mixOneVal(vals[0][index], multFactors[0][channel]);
        for(int i = 1; i < multFactors.length; i++) {
            double val = mixOneVal(vals[i][index], multFactors[i][channel]);
            result = mix(result, val);
        }
        return result;
    }



    @Override
    public int mix(byte[][] vals, double[][] multFactors, int mask, boolean isBigEndian, boolean isSigned) {
        return mix(vals, multFactors, vals[0].length, mask, isBigEndian, isSigned, 0, 0);
    }


    @Override
    public void mix(int[] vals, double[][] multFactors, int[] outputArr) {
        for(int ch = 0; ch < multFactors[0].length; ch++) {
            outputArr[ch] = mixOneVal(vals[0], multFactors[0][ch]);
            for (int i = 1; i < vals.length; i++) {
                int val = mixOneVal(vals[i], multFactors[i][ch]);
                outputArr[ch] = mix(outputArr[ch], val);
            }
        }
    }
    // Same as int but with double[] vals and outputArr
    @Override
    public void mix(double[] vals, double[][] multFactors, double[] outputArr) {
        for(int ch = 0; ch < multFactors[0].length; ch++) {
            outputArr[ch] = mixOneVal(vals[0], multFactors[0][ch]);
            for (int i = 1; i < vals.length; i++) {
                double val = mixOneVal(vals[i], multFactors[i][ch]);
                outputArr[ch] = mix(outputArr[ch], val);
            }
        }
    }








    @Override
    public int mix(double[][] vals, byte[] outputArr, int outputArrIndex, double[][] multFactors, int sampleSize,
                   boolean isBigEndian, boolean isSigned, int maxAbsoluteValue, int index) {
        for(int ch = 0; ch < multFactors[0].length; ch++, outputArrIndex += sampleSize) {
            double sample = mix(vals, multFactors, ch, index);
            Program.convertDoubleToByteArr(sample, sampleSize, maxAbsoluteValue, isBigEndian, isSigned, outputArrIndex, outputArr);
        }

        return outputArrIndex;
    }

    @Override
    public int mix(double[][] vals, double[] outputArr, int outputArrIndex, double[][] multFactors, int index) {
        for(int ch = 0; ch < multFactors[0].length; ch++, outputArrIndex++) {
            outputArr[outputArrIndex] = mix(vals, multFactors, ch, index);
        }

        return outputArrIndex;
    }

    /**
     * Mixes input tracks to outputArr channels at index and number of mixed frames is outputLen
     *
     * @param vals
     * @param outputArr
     * @param outputArrIndex
     * @param multFactors
     * @param index
     * @param outputLen
     * @return
     */
    @Override
    public int mix(double[][] vals, double[][] outputArr, int outputArrIndex, int outputLen,
                   double[][] multFactors, int index) {
        for(int i = 0; i < outputLen; i++, index++, outputArrIndex++) {
            for (int ch = 0; ch < multFactors[0].length; ch++) {
                outputArr[ch][outputArrIndex] = mix(vals, multFactors, ch, index);
            }
        }

        return outputArrIndex;
    }








    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Mono methods - not used, but I program them and they may be useful - they perform the same operations as the
    // multi-channel variants but with different interface - we need only 1D array for the multFactors arrays
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    /**
//     * Mixes 1 sample. Mixes vals.length values and puts the result in to outputArr at outputArrIndex.
//     * @param vals is the 2D array with samples. vals.length is number of mixed values. And we take values from vals[][index] to vals[][index + sampleSize] and mix them
//     * @param outputArr is the array to which is put the resulting mix sample.
//     * @param outputArrIndex is the index in outputArr where is put first byte of the resulting sample.
//     * @param multFactors is the array with factors to multiply the vals with.
//     * @param sampleSize is the sample size
//     * @param mask is the mask used for calculation
//     * @param isBigEndian true if the samples are big endian
//     * @param isSigned true if the samples are signed
//     * @param index is the index in the 2nd dimension of vals array. (The index says which sample it is)
//     * @return Returns the first index after the  in outputArr
//     */
//    public int mix(byte[][] vals, byte[] outputArr, int outputArrIndex, double[] multFactors, int sampleSize, int mask,
//                   boolean isBigEndian, boolean isSigned, int index) {
//        int result = mix(vals, multFactors, sampleSize, mask, isBigEndian, isSigned, index);
//        Program.convertIntToByteArr(outputArr, result, sampleSize, outputArrIndex, isBigEndian);
//        return outputArrIndex + sampleSize;
//    }
//
//    /**
//     * Mixes vals.length values to 1 sample.
//     * @param vals is the 2D array with samples. vals.length is number of mixed values. And we take values from vals[][index] to vals[][index + sampleSize] and mix them
//     * @param multFactors is the array with factors to multiply the vals with.
//     * @param sampleSize is the sample size
//     * @param mask is the mask used for calculation
//     * @param isBigEndian true if the samples are big endian
//     * @param isSigned true if the samples are signed
//     * @param index is the index in the 2nd dimension of vals array. (The index says which sample it is)
//     * @return
//     */
//    public int mix(byte[][] vals, double[] multFactors, int sampleSize, int mask, boolean isBigEndian, boolean isSigned,
//                   int index) {
//        int result = 0;
//        int sample;
//        for(int i = 0; i < vals.length; i++) {
//            sample = Program.convertBytesToInt(vals[i], sampleSize, mask, index, isBigEndian, isSigned);
//            result += mixOneVal(sample, multFactors[i]);
//        }
//
//        return result;
//    }
//
//    /**
//     * Mixes vals.length samples at vals[][index] to 1 sample.
//     * @param vals is the 2D array with samples.
//     * @param multFactors is the array with factors to multiply the vals with.
//     * @param index is the index in the 2nd dimension of vals array. (The index says which sample it is)
//     * @return
//     */
//    public int mix(int[][] vals, double[] multFactors, int index) {
//        int result = 0;
//        for(int i = 0; i < vals.length; i++) {
//            result += mixOneVal(vals[i][index], multFactors[i]);
//        }
//
//        index++;
//        return result;
//    }
//
//
//    /**
//     * Mixes vals.length values to 1 sample.
//     * @param vals is the 2D array with samples, where vals[i].length == sampleSize and vals.length == number of samples to mix
//     * @param multFactors is the array with factors to multiply the vals with.
//     * @param mask is the mask used for calculation
//     * @param isBigEndian true if the samples are big endian
//     * @param isSigned true if the samples are signed
//     * @return
//     */
//    public int mix(byte[][] vals, double[] multFactors, int mask, boolean isBigEndian, boolean isSigned) {
//        return mix(vals, multFactors, vals[0].length, mask, isBigEndian, isSigned, 0);
//    }
//
//    /**
//     * Mixes the input array together, treats every value as value to be mixed. Array parameters should be both of same length.
//     * @param vals is the input array, 1 index = 1 value to mix
//     * @param multFactors is the array with factors to multiply the vals with.
//     * @return Returns the mix.
//     */
//    public int mix(int[] vals, double[] multFactors) {
//        int result = 0;
//        for(int i = 0; i < vals.length; i++) {
//            result += mixOneVal(vals[i], multFactors[i]);
//        }
//
//        return result;
//    }



    /**
     * Mix 2 values together. This method is used for internal calculations, so it may not always represent the expected mixing output.
     * That is the reason why it is protected method. Default implementation is just add to the 2 values together.
     * @param val1
     * @param val2
     * @return Returns the mixed value
     */
    protected int mix(int val1, int val2) {
        return val1 + val2;
    }
    /**
     * Mix 2 values together. This method is used for internal calculations, so it may not always represent the expected mixing output.
     * That is the reason why it is protected method. Default implementation is just add to the 2 values together.
     * @param val1
     * @param val2
     * @return Returns the mixed value
     */
    protected double mix(double val1, double val2) {
        return val1 + val2;
    }


    // This method is only used for internal calculations, but can be overriden to fit the mixing we want to perform.
    // That is the reason why it is not in AudioMixerIFace
    /**
     * Multiplies the value by multiply factor.
     * @param val
     * @param multiplyFactor
     * @return
     */
    public int mixOneVal(int val, double multiplyFactor) {
        return (int)mixOneVal((double)val, multiplyFactor);
    }
    /**
     * Multiplies the value by multiply factor.
     * @param val
     * @param multiplyFactor
     * @return
     */
    public double mixOneVal(double val, double multiplyFactor) {
        return val * multiplyFactor;
    }

    // Also not included in AudioMixerIFace, for same reason as mixOneVal, but this method usually don't make sense be overriden.
    /**
     * Mixes 2 value together, each multiplied by multiply factor
     * @param val1
     * @param val2
     * @param multFactor1
     * @param multFactor2
     * @return
     */
    public int mix(int val1, int val2, double multFactor1, double multFactor2) {
        val1 = mixOneVal(val1, multFactor1);
        val2 = mixOneVal(val2, multFactor2);
        return mix(val1, val2);
    }
    /**
     * Mixes 2 value together, each multiplied by multiply factor
     * @param val1
     * @param val2
     * @param multFactor1
     * @param multFactor2
     * @return
     */
    public double mix(double val1, double val2, double multFactor1, double multFactor2) {
        val1 = mixOneVal(val1, multFactor1);
        val2 = mixOneVal(val2, multFactor2);
        return mix(val1, val2);
    }

    @Override
    public void update(double[][] multFactors) {

    }
}
