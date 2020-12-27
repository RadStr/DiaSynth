package synthesizer.synth.audio;

public class AudioThreadWithRecordingSupport extends AudioThread {
    /**
     * @param maxPlayTimeInMs     There is upper bound which will be known at run time (usually 500ms)
     *                            after adding more than that, the audio playing line blocks and there will
     *                            occur clicking in sound
     *                            (so to remove that it is set to that maximum value if it is bigger than that).
     *                            So set it to smaller numbers,
     *                            rather than higher, since it may be capped to the maximum value
     * @param cyclicQueueSizeInMs this won't be accurate since of optimisation purposes the size of queue has to be
     *                            power of 2.
     *                            So it will be in the end between [cyclicQueueSizeInMs / 2, cyclicQueueSizeInMs].
     *                            Minimum is 512 samples.
     * @param shouldPause
     */
    public AudioThreadWithRecordingSupport(AudioRecordingCallback recordingCallback,
                                           int maxPlayTimeInMs, int cyclicQueueSizeInMs, boolean shouldPause) {
        super(maxPlayTimeInMs, cyclicQueueSizeInMs, shouldPause);
        this.recordingCallback = recordingCallback;
    }

    /**
     * Just calls the other constructor with parameters, recordingCallback,
     * maxPlayTimeInMs=40, cyclicQueueSizeInMs=400, shouldPause
     *
     * @param shouldPause
     */
    public AudioThreadWithRecordingSupport(AudioRecordingCallback recordingCallback, boolean shouldPause) {
        this(recordingCallback, 80, 600, shouldPause);
    }


    private AudioRecordingCallback recordingCallback;


    @Override
    public void playAudioLoop() {
        synchronized (audioLock) {
            while (true) {
                int byteArrIndex = getAudioSamples();
                if (byteArrIndex > 0) {
                    recordingCallback.recordingRealTimeCallback(samplesToBePlayed, byteArrIndex);
                    audioLine.write(samplesToBePlayed, 0, byteArrIndex);
                }
            }
        }
    }
}