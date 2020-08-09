package DiagramSynthPackage.Synth.AudioThreads;

public interface AudioRecordingCallback {
    void recordingRealTimeCallback(byte[] playedAudio, int endIndex);
}
