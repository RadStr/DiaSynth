package deprecatedclasses;

@Deprecated
public class FFTDrawWrapper extends DrawWrapper {
    @Override
    public double[] getResult(int sampleRate, int copyCount) {
        return new double[0];
    }
}
