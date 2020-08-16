package RocnikovyProjektIFace.Drawing;

import RocnikovyProjektIFace.FFTWindowPanel;

public abstract class DrawWrapper {
    public abstract double[] getResult(int sampleRate, int copyCount);

    protected DoubleDrawPanel drawClass;
}
