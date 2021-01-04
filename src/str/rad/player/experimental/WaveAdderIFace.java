package str.rad.player.experimental;

import javax.swing.*;

public interface WaveAdderIFace {
    JSplitPane addWave(double[] wave);

    int getOutputSampleRate();
}
