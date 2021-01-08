package str.rad.synthesizer.synth.audio;

public interface AudioRecordingCallback {
    void recordingRealTimeCallback(byte[] playedAudio, int endIndex);
}
