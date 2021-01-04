package str.rad.player.mixer;

/**
 * for mixing of n values does (x_1+x_2+x_3+...+x_n) / n
 * That means it can mix up to 2^(32-sampleSizeInBits) waves without clipping.
 * If I want to mix more channels I have to use AverageMixerWithoutClipping
 */
public class AverageMixerWithPossibleClipping extends MixerWithPostProcessing {
    public AverageMixerWithPossibleClipping(double[][] multFactors) {
        update(multFactors);
    }

    private int waveCount = 1;

    @Override
    public int postProcessing(int sample, int channel) {
        return sample / waveCount;
    }

    @Override
    public double postProcessing(double sample, int channel) {
        return sample / waveCount;
    }

    @Override
    public void update(double[][] multFactors) {
        if (multFactors == null || multFactors.length == 0) {
            waveCount = 1;
        }
        else {
            waveCount = multFactors.length;
        }
    }
}
