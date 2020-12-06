package AudioMixers;

/**
 * Performs averaging, so the the result will fit into the sample range
 */
public class SimpleAverageMixer extends DefaultAudioMixer {
    @Override
    protected int mix(int val1, int val2) {
        return (val1 + val2) / 2;
    }
    @Override
    protected double mix(double val1, double val2) {
        return (val1 + val2) / 2;
    }
}
