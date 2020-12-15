package deprecatedclasses;

import player.experimental.DoubleDrawPanel;

@Deprecated
public abstract class DrawWrapper {
    public abstract double[] getResult(int sampleRate, int copyCount);

    protected DoubleDrawPanel drawClass;
}
