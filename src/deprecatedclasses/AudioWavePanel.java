package deprecatedclasses;

import util.audio.wave.ByteWave;

import javax.swing.*;

@Deprecated
public class AudioWavePanel extends JPanel {

    int zoom;
    ByteWave byteWave;

    public AudioWavePanel(ByteWave byteWave) {
        zoom = 1;
        this.byteWave = byteWave;
    }

}
