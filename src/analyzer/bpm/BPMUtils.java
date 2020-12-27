package analyzer.bpm;

public class BPMUtils {
    private BPMUtils() { }        // Allow only static access

    public static int convertBeatsToBPM(int beats, int sampleCount, int sampleSize,
                                        int numberOfChannels, int sampleRate) {
        int sizeOfOneSecond = sampleSize * numberOfChannels * sampleRate;
        int bpm = (int) (beats / ((double) sampleCount / (60 * sizeOfOneSecond)));
        return bpm;
    }
}
