package deprecatedclasses;

import Rocnikovy_Projekt.ByteWave;

import javax.swing.*;

@Deprecated
public class AudioWavePanel extends JPanel {

    int zoom;
    ByteWave prog;

    public AudioWavePanel(ByteWave prog) {
        zoom = 1;
        this.prog = prog;

    }

}
