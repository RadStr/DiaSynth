package player;

import Rocnikovy_Projekt.Program;

import javax.swing.*;

@Deprecated
public class AudioWavePanel extends JPanel {

    int zoom;
    Program prog;

    public AudioWavePanel(Program prog) {
        zoom = 1;
        this.prog = prog;

    }

}
