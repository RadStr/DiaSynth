package AudioMixers;

/**
 * Performs the mixing like (x_1 / n) + (x_2 / n) + ... + (x_n / n)
 * For less computationally demanding and more accurate variant check AverageMixelWithPossibleClipping, which can mix up to 2^(32-sampleSizeInBits) without clipping.
 */
public class AverageMixerWithoutClipping extends DefaultAudioMixer {
    private int waveCount = 1;

    /**
     * Multiplies the value by multiply factor.
     * @param val
     * @param multiplyFactor
     * @return
     */
    @Override
    public int mixOneVal(int val, double multiplyFactor) {
        return (int)mixOneVal((double)val, multiplyFactor);
    }
    /**
     * Multiplies the value by multiply factor.
     * @param val
     * @param multiplyFactor
     * @return
     */
    @Override
    public double mixOneVal(double val, double multiplyFactor) {
        return (val * multiplyFactor) / waveCount;
    }

    @Override
    public void update(double[][] multFactors) {
        if(multFactors == null || multFactors.length == 0) {
            waveCount = 1;
        }
        else {
            waveCount = multFactors.length;
        }
    }
}
