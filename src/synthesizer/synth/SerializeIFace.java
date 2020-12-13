package synthesizer.synth;

import java.io.BufferedReader;
import java.io.PrintWriter;

public interface SerializeIFace {
    void save(PrintWriter output);
    void load(BufferedReader input);
}
