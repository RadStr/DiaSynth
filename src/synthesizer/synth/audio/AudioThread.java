package synthesizer.synth.audio;

import synthesizer.gui.PlayedWaveVisualizer;
import synthesizer.synth.CyclicQueueDouble;
import synthesizer.synth.OutputFormatGetterIFace;
import synthesizer.synth.Unit;
import util.Utilities;
import util.audio.AudioConverter;
import util.audio.AudioUtilities;
import util.audio.format.AudioFormatJPanel;
import util.audio.format.AudioFormatWithSign;
import player.control.AudioControlPanel;
import util.logging.MyLogger;
import test.ProgramTest;

import javax.sound.sampled.*;

/**
 * Just takes requests to put samples to queue and later replays the queue (Unless it is stopped).
 */
public class AudioThread extends Thread implements OutputFormatGetterIFace, AudioControlPanel.VolumeControlGetterIFace {
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
    public AudioThread(int maxPlayTimeInMs, int cyclicQueueSizeInMs, boolean shouldPause) {
        setShouldPause(shouldPause);
        maxPlayTimeDivFactor = convertTimeInMsToDivFactor(maxPlayTimeInMs);
        cyclicQueueSizeDivFactor = convertTimeInMsToDivFactor(cyclicQueueSizeInMs);
        // Set it to default audio format, instead of null
        AudioFormatWithSign af = new AudioFormatWithSign(44100, 16, 1,
                                                         true, false);
        setOutputAudioFormat(AudioFormatJPanel.getSupportedAudioFormat(af));
        lastPlayedSampleInChannel = 0;
    }

    /**
     * Just calls the other constructor with parameters, maxPlayTimeInMs=40, cyclicQueueSizeInMs=400, shouldPause
     *
     * @param shouldPause
     */
    public AudioThread(boolean shouldPause) {
        this(40, 400, shouldPause);
    }


    private PlayedWaveVisualizer waveVisualizer;

    public PlayedWaveVisualizer getWaveVisualizer() {
        return waveVisualizer;
    }

    public void setWaveVisualizer(PlayedWaveVisualizer waveVisualizer) {
        this.waveVisualizer = waveVisualizer;
    }

    private CyclicQueueDouble[] queuesDouble;
    private double maxPlayTimeDivFactor;
    private double cyclicQueueSizeDivFactor;

    public static double convertTimeInMsToDivFactor(int time) {
        return 1000 / (double) time;
    }

    /**
     * Puts samples to queue and returns number of written samples to queue
     *
     * @param samples
     * @return
     */
    public int pushSamplesToQueue(double[] samples, int channel) {
        return pushSamplesToQueue(samples, 0, samples.length, channel);
    }

    /**
     * Puts samples to queue and returns number of written samples to queue
     *
     * @param samples
     * @param startIndex
     * @param endIndex
     * @return
     */
    public int pushSamplesToQueue(double[] samples, int startIndex, int endIndex, int channel) {
        return queuesDouble[channel].push(samples, startIndex, endIndex);
    }

    /**
     * Returns how many samples can be put to queue corresponding to channel.
     *
     * @param channel
     * @return
     */
    public int getPushLen(int channel, int startIndex, int endIndex) {
        return queuesDouble[channel].getPushLength(startIndex, endIndex);
    }


    private AudioFormatWithSign outputAudioFormat;

    @Override
    public AudioFormatWithSign getOutputFormat() {
        return outputAudioFormat;
    }

    public void setOutputAudioFormat(AudioFormatWithSign newFormat) {
        try {
            masterGain = null;
            if (audioLine != null) {
                if (audioLine.isOpen()) {
                    audioLine.close();
                }
            }
        }
        catch (Exception e) {
            MyLogger.logException(e);
        }


        outputAudioFormat = newFormat;
        int channelCount = outputAudioFormat.getChannels();
        sampleSizeInBytes = outputAudioFormat.getSampleSizeInBits() / 8;
        maxAbsoluteValue = AudioUtilities.getMaxAbsoluteValueSigned(outputAudioFormat.getSampleSizeInBits());
        int sampleRate = (int) outputAudioFormat.getSampleRate();

        if (waveVisualizer != null) {
            waveVisualizer.setNumberOfChannels(channelCount);
            waveVisualizer.setSampleRate(sampleRate);
        }

        frameSize = channelCount * sampleSizeInBytes;
        int sizeInSamples = (int) (sampleRate / cyclicQueueSizeDivFactor);
        sizeInSamples = Math.max(Unit.BUFFER_LEN, sizeInSamples);
        int lenExponent = Utilities.getFirstPowerExponentOfNAfterNumber(sizeInSamples, 2) - 1;
        queuesDouble = new CyclicQueueDouble[channelCount];
        for (int i = 0; i < queuesDouble.length; i++) {
            // [x0.5, x1] result will be of len in ms
            queuesDouble[i] = new CyclicQueueDouble(lenExponent);
        }

        // Now it contains length of one second in bytes.
        int samplesToBePlayedByteLen = convertMsToByteLen(sampleRate, frameSize, maxPlayTimeDivFactor);


        DataLine.Info info = new DataLine.Info(SourceDataLine.class, outputAudioFormat);
        try {
            audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(outputAudioFormat);
            // NOTE: Type.VOLUME isn't available control
            masterGain = (FloatControl) audioLine.getControl(FloatControl.Type.MASTER_GAIN);
            muteControl = (BooleanControl) audioLine.getControl(BooleanControl.Type.MUTE);
            audioLine.start();
            audioLineMaxAvailableBytes = audioLine.available();
            samplesToBePlayedByteLen = Math.min(audioLineMaxAvailableBytes, samplesToBePlayedByteLen);
            samplesToBePlayedDouble = new double[channelCount][samplesToBePlayedByteLen / frameSize];
            samplesToBePlayed = new byte[samplesToBePlayedByteLen];

            minAllowedAvailableSize = audioLineMaxAvailableBytes - samplesToBePlayedByteLen;
        }
        catch (LineUnavailableException e) {
            MyLogger.logException(e);
        }
    }


    public static int convertMsToByteLen(int sampleRate, int frameSize, double msDivFactor) {
        int byteLen = AudioUtilities.calculateSizeOfOneSec(sampleRate, frameSize);
        byteLen *= (1 / msDivFactor);        // Number of bytes corresponding to the play time
        byteLen = Utilities.convertToMultipleUp(byteLen, frameSize);
        return byteLen;
    }


    private FloatControl masterGain;

    @Override
    public FloatControl getGain() {
        return masterGain;
    }

    private BooleanControl muteControl;

    @Override
    public BooleanControl getMuteControl() {
        return muteControl;
    }

    private int maxAbsoluteValue;
    private int sampleSizeInBytes;

    private int frameSize = 0;
    protected SourceDataLine audioLine;
    private int audioLineMaxAvailableBytes;
    private int minAllowedAvailableSize;

    private volatile boolean waiting = false;   // Modified only by audio thread

    public boolean isPaused() {
        return waiting;
    }

    private volatile boolean shouldPause;
    private Object pauseLock = new Object();

    private void setShouldPause(boolean value) {
        synchronized (pauseLock) {
            shouldPause = value;
        }
    }

    protected Object audioLock = new Object();
    private int lastPlayedSampleInChannel;
    private double[][] samplesToBePlayedDouble;
    protected byte[] samplesToBePlayed;

    @Override
    public void run() {
        playAudioLoop();
    }

    public void playAudioLoop() {
        synchronized (audioLock) {
            while (true) {
                int byteArrIndex = getAudioSamples();
                if (byteArrIndex > 0) {
                    audioLine.write(samplesToBePlayed, 0, byteArrIndex);
                }
            }
        }
    }


    /**
     * Is used internally in audio play loop, shouldn't be ever called from different place, then the audio loop.
     *
     * @return Returns -1 if the audio samples weren't set (for example not all queues are filled enough).
     * Otherwise returns number of valid samples in byte[] samplesToBePlayed
     */
    protected final int getAudioSamples() {
        // First I check if pause button was clicked and after that I play the current part
        if (shouldPause) {
            ProgramTest.debugPrint("AUDIO - WAITING");
            waiting = true;
            audioLine.drain();
            audioLine.stop();
            try {
                audioLock.wait();        // Passive waiting
            }
            catch (InterruptedException e) {
                MyLogger.logException(e);
                return -1;
            }

            audioLine.start();
            waiting = false;
            setShouldPause(false);
            ProgramTest.debugPrint("AUDIO - STOPPED WAITING");
            int minLen = queuesDouble[0].getTotalQueueCapacity();
            minLen = Math.min(minLen, samplesToBePlayedDouble[0].length * 8);
            while (queuesDouble[0].getLen() < minLen) {
                // Wait until there are at least some samples to be played
                if (shouldPause) {
                    break;
                }
            }

            if (shouldPause) {
                return -1;
            }
            ProgramTest.debugPrint("AUDIO - CONTINUE PLAYING");
        }


        int availableLen = Math.max(0, audioLine.available() - minAllowedAvailableSize);
        availableLen /= frameSize;
        availableLen = Math.min(availableLen, samplesToBePlayedDouble[0].length);

        int validSampleCount;
        boolean shouldSkip = false;
        validSampleCount = queuesDouble[0].getPopLength(0, availableLen);
        for (int i = 1; i < samplesToBePlayedDouble.length; i++) {
            if (validSampleCount > queuesDouble[i].getLen()) {
                shouldSkip = true;
                break;
            }
        }
        if (shouldSkip) {
            return -1;
        }

        for (int i = 0; i < samplesToBePlayedDouble.length; i++) {
            queuesDouble[i].pop(samplesToBePlayedDouble[i], 0, validSampleCount);
        }
        lastPlayedSampleInChannel += validSampleCount;

        int byteArrIndex = fillByteArrWithChannels(samplesToBePlayedDouble, validSampleCount, sampleSizeInBytes,
                                                   samplesToBePlayed, maxAbsoluteValue,
                                                   outputAudioFormat.isBigEndian(), outputAudioFormat.isSigned);

        if (waveVisualizer != null) {
            for (int ch = 0; ch < samplesToBePlayedDouble.length; ch++) {
                waveVisualizer.pushSamplesToQueue(samplesToBePlayedDouble[ch], 0, validSampleCount, ch);
            }
        }

        return byteArrIndex;
    }


    public static int fillByteArrWithChannels(double[][] channels, int validSampleCount,
                                              int sampleSizeInBytes, byte[] outArr,
                                              int maxAbsoluteValue, boolean isBigEndian, boolean isSigned) {
        int byteArrIndex = 0;
        for (int s = 0; s < validSampleCount; s++) {
            for (int i = 0; i < channels.length; i++, byteArrIndex += sampleSizeInBytes) {
                AudioConverter.convertDoubleToByteArr(channels[i][s], sampleSizeInBytes,
                                                      maxAbsoluteValue, isBigEndian, isSigned, byteArrIndex, outArr);
            }
        }

        return byteArrIndex;
    }


    public void pause() {
        setShouldPause(true);
    }

    public void play() {
        synchronized (audioLock) {
            audioLock.notifyAll();
        }
    }

    public void reset() {
        while (!isPaused()) {           // Active waiting
            pause();
        }
        lastPlayedSampleInChannel = 0;
        for (int i = 0; i < queuesDouble.length; i++) {
            queuesDouble[i].reset();
        }
        for (int i = 0; i < samplesToBePlayedDouble.length; i++) {
            for (int j = 0; j < samplesToBePlayedDouble[i].length; j++) {
                samplesToBePlayedDouble[i][j] = 0;
            }
        }
    }
}