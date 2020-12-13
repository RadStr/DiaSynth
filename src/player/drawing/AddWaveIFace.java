package player.drawing;

import javax.swing.*;

public interface AddWaveIFace {
    JSplitPane addWave(double[] wave);
    int getOutputSampleRate();
}
