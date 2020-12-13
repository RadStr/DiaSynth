package synthesizer.synth.AudioThreads;

public interface AudioRecordingCallback {
    void recordingRealTimeCallback(byte[] playedAudio, int endIndex);
}
