package synthesizer.Synth.WaveTables;

public class WaveTableFast extends WaveTable {
    /**
     * @param wave            is the wave used in wave table synthesis. The length has to be power of 2, that is the reason
     *                        why this wave table is faster. To be honest I am not sure how much faster it is.
     * @param wavePath is the path to the wave or null if it wasn't from file
     */
    public WaveTableFast(double[] wave, String wavePath) {
        super(wave, wavePath);
        MOD_VAL = wave.length - 1;
    }

    private final int MOD_VAL;

    @Override
    public void goToNthWaveIndex(double generatedWaveFreq, int playFreq, int n) {
        waveIndex += n * (generatedWaveFreq * wave.length) / playFreq;
        // Have to modulo, otherwise I would have the same problem as for Cyclic queue and that is overflow
        waveIndex = getIndexMod((int)waveIndex) + getDoubleRemainder(waveIndex);
    }

    @Override
    protected double convertWaveIndexToRange(double waveIndex) {
        waveIndex = getIndexMod((int)waveIndex) + getDoubleRemainder(waveIndex);
        if(waveIndex < 0) {
            waveIndex = wave.length + waveIndex;
        }
        return waveIndex;
    }

    @Override
    public int getIndexMod(int index) {
        return index & MOD_VAL;
    }
}