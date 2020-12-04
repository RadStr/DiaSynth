package AudioMixers;

import Rocnikovy_Projekt.Program;

abstract public class MixerWithPostProcessing extends DefaultAudioMixer {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Methods when the output audioFormat has more than 1 channel
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
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

        result = postProcessing(result, channel);
        return result;
    }

    @Override
    public void mix(int[][] vals, double[][] multFactors, int index, int[] outputArr) {
        for(int ch = 0; ch < multFactors[0].length; ch++) {
            outputArr[ch] = mixOneVal(vals[0][index], multFactors[0][ch]);
            for (int i = 1; i < vals.length; i++) {
                int val = mixOneVal(vals[i][index], multFactors[i][ch]);
                outputArr[ch] = mix(outputArr[ch], val);
            }

            outputArr[ch] = postProcessing(outputArr[ch], ch);
        }
    }
    // Same as int but with double[] vals and outputArr
    @Override
    public void mix(double[][] vals, double[][] multFactors, int index, double[] outputArr) {
        for(int ch = 0; ch < multFactors[0].length; ch++) {
            outputArr[ch] = mixOneVal(vals[0][index], multFactors[0][ch]);
            for (int i = 1; i < vals.length; i++) {
                double val = mixOneVal(vals[i][index], multFactors[i][ch]);
                outputArr[ch] = mix(outputArr[ch], val);
            }

            outputArr[ch] = postProcessing(outputArr[ch], ch);
        }
    }


    @Override
    public int mix(byte[][] vals, double[][] multFactors, int mask, boolean isBigEndian, boolean isSigned) {
        return mix(vals, multFactors, vals[0].length, mask, isBigEndian, isSigned, 0, 0);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Methods when the output audioFormat has 1 channel
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void mix(int[] vals, double[][] multFactors, int[] outputArr) {
        for(int ch = 0; ch < multFactors[0].length; ch++) {
            outputArr[ch] = mixOneVal(vals[0], multFactors[0][ch]);
            for (int i = 1; i < vals.length; i++) {
                int val = mixOneVal(vals[i], multFactors[i][ch]);
                outputArr[ch] = mix(outputArr[ch], val);
            }

            outputArr[ch] = postProcessing(outputArr[ch], ch);
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

            outputArr[ch] = postProcessing(outputArr[ch], ch);
        }
    }


    // Help methods
    @Override
    public int mix(int[][] vals, double[][] multFactors, int channel, int index) {
        int result = mixOneVal(vals[0][index], multFactors[0][channel]);
        for(int i = 1; i < multFactors.length; i++) {
            int val = mixOneVal(vals[i][index], multFactors[i][channel]);
            result = mix(result, val);
        }

        result = postProcessing(result, channel);
        return result;
    }
    @Override
    public double mix(double[][] vals, double[][] multFactors, int channel, int index) {
        double result = mixOneVal(vals[0][index], multFactors[0][channel]);
        for (int i = 1; i < multFactors.length; i++) {
            double val = mixOneVal(vals[i][index], multFactors[i][channel]);
            result = mix(result, val);
        }

        result = postProcessing(result, channel);
        return result;
    }




    /**
     *
     * @param sample is the sample before post processing
     * @return Returns sample after postprocessing
     */
    abstract public int postProcessing(int sample, int channel);

    /**
     *
     * @param sample is the sample before post processing
     * @return Returns sample after postprocessing
     */
    abstract public double postProcessing(double sample, int channel);
}
