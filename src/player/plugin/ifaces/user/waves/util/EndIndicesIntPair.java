package player.plugin.ifaces.user.waves.util;

public class EndIndicesIntPair {
    public EndIndicesIntPair(int inputWaveEndIndex, int outputWaveEndIndex) {
        this.inputWaveEndIndex = inputWaveEndIndex;
        this.outputWaveEndIndex = outputWaveEndIndex;
    }

    public EndIndicesIntPair() {
        // EMPTY
    }

    public int inputWaveEndIndex;
    public int outputWaveEndIndex;
}
