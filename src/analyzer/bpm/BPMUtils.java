package analyzer.bpm;

public class BPMUtils {
    public static int convertBPM(int beats, int sampleCount, int sampleSize, int numberOfChannels, int sampleRate) {
        int sizeOfOneSecond = sampleSize * numberOfChannels * sampleRate;
        int bpm = (int) (beats / ((double)sampleCount / (60 * sizeOfOneSecond)));
        return bpm;
    }
}
