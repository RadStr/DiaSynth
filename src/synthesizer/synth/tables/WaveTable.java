package synthesizer.synth.tables;

public class WaveTable {
    /**
     *
     * @param wave is the wave used in wave table synthesis.
     * @param wavePath is the path to the wave or null if it wasn't from file
     */
    public WaveTable(double[] wave, String wavePath) {
        this.wave = wave;
        waveIndex = 0;
        this.WAVE_PATH = wavePath;
    }

    public final String WAVE_PATH;
    protected final double[] wave;
    public double[] getWave() {
        return wave;
    }
    public double peek() {
        return wave[getWaveIndex()];
    }
    public double peek(double phase) {
        return wave[(int) getWaveIndexDouble(phase)];
    }


    public double peekInterpolatedPhase(double phase) {
        double index = getWaveIndexDouble(phase);
        return peekInterpolated(index);
    }

    public double peekInterpolated() {
        return peekInterpolated(getWaveIndex());
    }

    /**
     * The index is the wave index shifted by phase
     * @param index
     * @return
     */
    public double peekInterpolated(double index) {
        int leftValIndex = (int)index;
        int rightValIndex = getNextWaveIndex(index);
        double factor = getWaveIndexRemainder();
        double rightVal = factor * wave[rightValIndex];
        factor = 1 - factor;
        double leftVal = factor * wave[leftValIndex];
        return leftVal + rightVal;
    }

    /**
     * index has to be [0, wave.length - 1]
     * @param wave
     * @param index
     * @return
     */
    public static double interpolate(double[] wave, double index) {
        int leftValIndex = (int)index;
        int rightValIndex = leftValIndex + 1;
        if(rightValIndex > wave.length - 1) {       // When the index == wave.length - 1
            rightValIndex = wave.length - 1;
        }
        double factor = index - leftValIndex;
        double rightVal = factor * wave[rightValIndex];
        factor = 1 - factor;
        double leftVal = factor * wave[leftValIndex];
        return leftVal + rightVal;
    }

    public double pop(double generatedWaveFreq, int playFreq, double phase) {
        double retVal = peek(phase);
        goToNextWaveIndex(generatedWaveFreq, playFreq);
        return retVal;
    }

    public double pop(double generatedWaveFreq, int playFreq) {
        double retVal = peek();
        goToNextWaveIndex(generatedWaveFreq, playFreq);
        return retVal;
    }


    public double popInterpolated(double generatedWaveFreq, int playFreq, double phase) {
        double retVal = peekInterpolatedPhase(phase);
        goToNextWaveIndex(generatedWaveFreq, playFreq);
        return retVal;
    }

    public double popInterpolated(double generatedWaveFreq, int playFreq) {
        double retVal = peekInterpolated();
        goToNextWaveIndex(generatedWaveFreq, playFreq);
        return retVal;
    }


    protected double waveIndex;
    public double getWaveIndexDouble() {
        return waveIndex;
    }
    private int getWaveIndex() {
        return (int)waveIndex;
    }
    private double getWaveIndexDouble(double phase) {
        double phasedWaveIndex = waveIndex + getPhaseShift(phase);
        phasedWaveIndex = convertWaveIndexToRange(phasedWaveIndex);
        return phasedWaveIndex;
    }
    private double getWaveIndex(double phase) {
        return (int)getWaveIndexDouble(phase);
    }

    public void setWaveIndex(double newWaveIndex) {
        this.waveIndex = newWaveIndex;
    }
    public void goToNextWaveIndex(double generatedWaveFreq, int playFreq) {
        goToNthWaveIndex(generatedWaveFreq, playFreq, 1);
    }

    public void goToNthWaveIndex(double generatedWaveFreq, int playFreq, int n) {
        double indexJump = n * (generatedWaveFreq * wave.length) / playFreq;
        if(indexJump < 0) {
            indexJump = -indexJump;
        }
        waveIndex += indexJump;
        if(waveIndex >= wave.length) {
            waveIndex %= wave.length;
        }
    }

    /**
     * Converts the given range back to array range (waveIndex can be < 0 and >= wave.length)
     * @param waveIndex
     * @return
     */
    protected double convertWaveIndexToRange(double waveIndex) {
        if(waveIndex >= wave.length) {
            waveIndex %= wave.length;
        }
        else if(waveIndex < 0) {
            waveIndex %= wave.length;
            waveIndex = wave.length + waveIndex;
        }
        return waveIndex;
    }


    private double getPhaseShift(double phase) {
        double indexJump = wave.length * phase / (2 * Math.PI);
        return indexJump;
    }

    private double savedWaveIndex;
    public void saveWaveIndex() {
        savedWaveIndex = waveIndex;
    }
    public void resetToSavedWaveIndex() {
        waveIndex = savedWaveIndex;
    }

    private int getNextWaveIndex() {
        return getNextWaveIndex(waveIndex);
    }

    private int getNextWaveIndex(double index) {
        return getIndexMod(((int)index) + 1);
    }


    // Is overridden for power of 2 (In WaveTableFast).
    public int getIndexMod(int index) {
        return index % wave.length;
    }

    public double getWaveIndexRemainder() {
        return getDoubleRemainder(waveIndex);
    }

    public static double getDoubleRemainder(double value) {
        return value - (int)value;
    }
}
