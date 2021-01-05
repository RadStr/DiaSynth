package str.rad.util.audio.wave;


import java.io.*;
import java.util.*;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat.Encoding;

import str.rad.analyzer.bpm.BPMSimple;
import str.rad.analyzer.bpm.BPMSimpleWithFreqBands;
import str.rad.util.Aggregation;
import str.rad.util.Utilities;
import str.rad.util.audio.*;
import str.rad.util.audio.io.AudioReader;
import str.rad.util.audio.io.AudioWriter;
import str.rad.analyzer.bpm.SubbandSplitterIFace;
import str.rad.util.Time;
import str.rad.util.audio.format.AudioFormatWithSign;
import org.jtransforms.fft.DoubleFFT_1D;
import str.rad.util.audio.format.AudioType;

/**
 * This class represents audio as series of bytes. Instance of this class can only be created through loadSong methods.
 */
public class ByteWave {
    private ByteWave() { }          // Instance can be created only through loadSong methods

    private byte[] song = null;

    public byte[] getSong() {
        return song;
    }

    /**
     * Returns the length of the underlying array or -1 if the array is set to null;
     *
     * @return
     */
    public int getSongLen() {
        if (song == null) {
            return -1;
        }
        return song.length;
    }

    private int mask;

    public int getMask() {
        return mask;
    }

    private File soundFile;
    private AudioInputStream decodedAudioStream;

    private int numberOfChannels;

    public int getNumberOfChannels() {
        return numberOfChannels;
    }

    private int sampleRate;

    public int getSampleRate() {
        return sampleRate;
    }

    private int sampleSizeInBits;

    public int getSampleSizeInBits() {
        return sampleSizeInBits;
    }

    private int sampleSizeInBytes;

    public int getSampleSizeInBytes() {
        return sampleSizeInBytes;
    }


    private int wholeFileSize;

    public int getWholeFileSize() {
        return wholeFileSize;
    }


    /**
     * This variable is set only after the loadSong method is called. It references the length of audio in bytes.
     * The variable is used only internally.
     */
    private int audioStreamSizeInBytes;

    private float frameRate;
    private int frameSize;

    public int getFrameSize() {
        return frameSize;
    }

    private boolean isBigEndian;

    public boolean getIsBigEndian() {
        return isBigEndian;
    }

    private Encoding encoding;

    public Encoding getEncoding() {
        return encoding;
    }

    private boolean isSigned;

    public boolean getIsSigned() {
        return isSigned;
    }

    private int headerSize;

    private int lengthOfAudioInSeconds;

    public int getLengthOfAudioInSeconds() {
        return lengthOfAudioInSeconds;
    }

    private String fileName;

    public String getFileName() {
        return fileName;
    }

    private String path;

    public String getPath() {
        return path;
    }

    private AudioFileFormat originalAudioFileFormat = null;
    private AudioFormat originalAudioFormat = null;
    private AudioFormat decodedAudioFormat = null;
    private Type type;
    private AudioType audioType;
    private AudioInputStream originalAudioStream;

    private int maxAbsoluteValue;

    public int calculateSizeOfOneSec() {
        return AudioUtilities.calculateSizeOfOneSec(this.sampleRate, this.frameSize);
    }


    /**
     * setVariables needs to be called before calling this method because
     * audioStreamSizeInBytes variable needs to be set to correct byte length of audio and
     * decodedAudioStream needs to be set to correct audio.
     */
    public byte[] convertStreamToByteArray() throws IOException {
        return AudioConverter.convertStreamToByteArray(decodedAudioStream, audioStreamSizeInBytes);
    }


    public void convertToMono() throws IOException {
        this.song = AudioConverter.convertToMono(this.song, this.frameSize, this.numberOfChannels,
                                                 this.sampleSizeInBytes, this.isBigEndian, this.isSigned);
        this.numberOfChannels = 1;
        this.frameSize = sampleSizeInBytes;
        this.decodedAudioFormat = new AudioFormat(decodedAudioFormat.getEncoding(),
                                                  decodedAudioFormat.getSampleRate(),
                                                  decodedAudioFormat.getSampleSizeInBits(), 1,
                                                  this.frameSize, decodedAudioFormat.getFrameRate(),
                                                  decodedAudioFormat.isBigEndian());
    }


    /**
     * Plays the loaded song. It is played in audioFormat given as parameter.
     *
     * @param audioFormat   is the audio audioFormat.
     * @param playBackwards if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * @throws LineUnavailableException is thrown when error with playing the song occurred.
     */
    public void playSong(AudioFormat audioFormat, boolean playBackwards) throws LineUnavailableException, IOException {
        if (playBackwards) {
            byte[] songArr = convertStreamToByteArray();
            AudioUtilities.playSong(songArr, audioFormat, playBackwards);
        }
        else {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);
            line.start();
            int bytesRead = 0;
            byte[] buffer = new byte[frameSize * 256];
            while (bytesRead != -1) {
                bytesRead = decodedAudioStream.read(buffer, 0, buffer.length);
                line.write(buffer, 0, bytesRead);
            }
            line.drain();
        }
    }


    /**
     * Plays the loaded song. Other parameters of this method describe the audioFormat in which will be the audio played.
     * Playing the audio backwards may be too slow, the input stream has to be transformed to byte array first.
     *
     * @param encoding         is the encoding of the audio data.
     * @param sampleRate       is the sample rate of the audio data.
     * @param sampleSizeInBits is the size of 1 sample in bits.
     * @param numberOfChannels represents the number of channels.
     * @param frameSize        is the size of one frame.
     * @param frameRate        is the frame rate of the audio.
     * @param isBigEndian      is true if the samples are in big endian, false if in little endian
     * @param playBackwards    if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * @throws LineUnavailableException is thrown when there is problem with feeding the data to the SourceDataLine.
     * @throws IOException              is thrown when error with the input stream occurred.
     */
    public void playSong(Encoding encoding, int sampleRate, int sampleSizeInBits, int numberOfChannels, int frameSize, float frameRate, boolean isBigEndian, boolean playBackwards) throws LineUnavailableException, IOException {
        AudioFormat audioFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits, numberOfChannels, frameSize, frameRate, isBigEndian);
        playSong(audioFormat, playBackwards);
    }


    /**
     * Creates instance of ByteWave based on given song and writes the audio properties to output together with some additional info.
     *
     * @param file    is the file with the song
     * @param setSong is true, if we want to write the whole song to 1D byte array song (It is property of this class).
     *                if it is false, then doesn't set the variable, which may save a lot of memory and time.
     * @return Returns instance of ByteWave if there was no problem with file, null if it couldn't be read (for example it wasn't audio).
     * @throws IOException is thrown if there was problem with reading the info from the file.
     */
    public static ByteWave loadSongAndPrintVariablesDEBUG(File file, boolean setSong) throws IOException {
        ByteWave byteWave;
        if ((byteWave = loadSong(file, setSong)) != null) {
            byteWave.writeVariablesDebug();
            return byteWave;
        }
        return null;
    }

    /**
     * Creates instance of ByteWave based on given song and writes the audio properties to output together with some additional info.
     *
     * @param path    is the path to the song
     * @param setSong is true, if we want to write the whole song to 1D byte array song (It is property of this class).
     *                if it is false, then doesn't set the variable, which may save a lot of memory and time.
     * @return Returns instance of ByteWave if there was no problem with file, null if it couldn't be read (for example it wasn't audio).
     * @throws IOException is thrown if there was problem with reading the info from the file.
     */
    public static ByteWave loadSongAndPrintVariablesDEBUG(String path, boolean setSong) throws IOException {
        ByteWave byteWave;
        if ((byteWave = loadSong(path, setSong)) != null) {
            byteWave.writeVariablesDebug();
            return byteWave;
        }
        return null;
    }

    /**
     * Creates instance of ByteWave based on given song.
     *
     * @param path    is the path to the song
     * @param setSong is true, if we want to write the whole song to 1D byte array song (It is property of this class).
     *                if it is false, then doesn't set the variable, which may save a lot of memory and time, but only limited number
     *                of methods works with stream
     * @return Returns instance of ByteWave if there was no problem with file, null if it couldn't be read (for example it wasn't audio).
     * @throws IOException is thrown if there was problem with reading the info from the file.
     */
    public static ByteWave loadSong(String path, boolean setSong) throws IOException {
        ByteWave bw = new ByteWave();
        if (loadSong(bw, path, setSong)) {
            return bw;
        }
        return null;
    }


    // Could be public, but the problem is that it destroys the ByteWave instance if it fails
    private static boolean loadSong(ByteWave byteWave, String path, boolean setSong) throws IOException {
        byteWave.setNameVariables(path);
        if (!byteWave.setFormatAndStream(path)) {
            return false;
        }
        byteWave.setVariables();


        if (setSong) {
            byteWave.setSong();
        }
        else {
            byteWave.setTotalAudioLength();
        }


        // If there was some problem, then something unexpected happened (Because the previous call succeeded)
        if (!byteWave.setFormatAndStream(path)) {
            return false;
        }
        return true;
    }


    /**
     * Creates instance of ByteWave based on given song.
     *
     * @param file    is the file with the song
     * @param setSong is true, if we want to write the whole song to 1D byte array song (It is property of this class).
     *                if it is false, then doesn't set the variable, which may save a lot of memory and time, but only limited number
     *                of methods works with stream
     * @return Returns instance of ByteWave if there was no problem with file, null if it couldn't be read (for example it wasn't audio).
     * @throws IOException is thrown if there was problem with reading the info from the file.
     */
    public static ByteWave loadSong(File file, boolean setSong) throws IOException {
        ByteWave byteWave = new ByteWave();
        if (loadSong(byteWave, file, setSong)) {
            return byteWave;
        }
        return null;
    }

    // Could be public, but the problem is that it destroys the ByteWave instance if it fails
    private static boolean loadSong(ByteWave byteWave, File file, boolean setSong) throws IOException {
        byteWave.setNameVariables(file);
        if (!byteWave.setFormatAndStream(file)) {
            return false;
        }
        byteWave.setVariables();

        if (setSong) {
            byteWave.setSong();
        }
        else {
            byteWave.setTotalAudioLength();
        }


        // If there was some problem, then something unexpected happened (Because the previous call succeeded)
        if (!byteWave.setFormatAndStream(file)) {
            return false;
        }
        return true;
    }

    private boolean setTotalAudioLength() throws IOException {
        audioStreamSizeInBytes = AudioReader.getLengthOfInputStream(decodedAudioStream);
        headerSize = wholeFileSize - audioStreamSizeInBytes;
        decodedAudioStream.close();
        return true;
    }

    private void setSong() throws IOException {
        setTotalAudioLength();
        setFormatAndStream(this.path);
        song = convertStreamToByteArray();
        audioStreamSizeInBytes = song.length;
        headerSize = wholeFileSize - audioStreamSizeInBytes;
        decodedAudioStream.close();
    }


    private void setNameVariables(File file) {
        this.fileName = file.getName();
        this.path = file.getPath();
    }

    private void setNameVariables(String path) {
        this.path = path;
        this.fileName = Utilities.getFilenameFromPath(path);
    }


    // Sets variables if there is already valid decodedAudioFormat
    private void setVariables() throws IOException {
        isBigEndian = decodedAudioFormat.isBigEndian();
        numberOfChannels = decodedAudioFormat.getChannels();
        encoding = decodedAudioFormat.getEncoding();
        frameRate = decodedAudioFormat.getFrameRate();
        sampleSizeInBits = decodedAudioFormat.getSampleSizeInBits();
        sampleSizeInBytes = sampleSizeInBits / 8;
        frameSize = sampleSizeInBytes * numberOfChannels;
        sampleRate = (int) decodedAudioFormat.getSampleRate();
        mask = AudioUtilities.calculateMask(sampleSizeInBytes);
        maxAbsoluteValue = AudioUtilities.getMaxAbsoluteValueSigned(sampleSizeInBits);

        wholeFileSize = originalAudioFileFormat.getByteLength();


        // That is the number of frames that means total number of samples is numberOfChannels * numberOfFrames
        if (this.audioType == AudioType.MP3) {
            //  This MP3 frame count - in mp3 frame is ~0.026 seconds
            int frameCount = Integer.parseInt(originalAudioFileFormat.properties().get("mp3.length.frames").toString());
            lengthOfAudioInSeconds = (int) (frameCount * 0.026);        // 0.026s is size of 1 frame
        }
        else {
            int totalNumberOfFrames = originalAudioFileFormat.getFrameLength();
            lengthOfAudioInSeconds = (totalNumberOfFrames / sampleRate);        // Works for wav
        }

        isSigned = AudioFormatWithSign.getIsSigned(encoding);

        if (frameSize != decodedAudioFormat.getFrameSize()) {
            throw new IOException();
        }
    }


    /**
     * Gets the audioFormat of the decoded audio and also gets the audio stream for the decoded audio and sets corresponding properties.
     * For the decoding of mp3 files is used library. If false is returnes, then there is some problem and song should
     * be invalidated.
     *
     * @param path is the path to the file with audio.
     * @return Returns true if all was set correctly, false if there was some problem.
     */
    private boolean setFormatAndStream(String path) {
        try {
            soundFile = new File(path);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return setFormatAndStream();
    }

    public boolean setFormatAndStream(File f) {
        soundFile = f;
        return setFormatAndStream();
    }

    private boolean setFormatAndStream() {
        try {
            if (originalAudioStream != null) {
                originalAudioStream.close();
            }
            originalAudioFileFormat = AudioSystem.getAudioFileFormat(soundFile);
            type = originalAudioFileFormat.getType();
            originalAudioStream = AudioSystem.getAudioInputStream(soundFile);
            originalAudioFormat = originalAudioStream.getFormat();

            if ("mp3".equals(type.getExtension())) {
                audioType = AudioType.MP3;
                decodedAudioFormat = new AudioFormat(Encoding.PCM_SIGNED,
                                                     originalAudioFormat.getSampleRate(),
                                                     16,
                                                     originalAudioFormat.getChannels(),
                                                     originalAudioFormat.getChannels() * 2,
                                                     originalAudioFormat.getSampleRate(),
                                                     false);
                decodedAudioStream = AudioSystem.getAudioInputStream(decodedAudioFormat, originalAudioStream);
            }
            else {
                audioType = AudioType.OTHER;
                decodedAudioFormat = originalAudioFormat;
                decodedAudioStream = originalAudioStream;
            }
        }
        catch (Exception e) {
            originalAudioStream = null;
            originalAudioFormat = null;
            decodedAudioFormat = null;
            decodedAudioStream = null;
            audioType = AudioType.NOT_SUPPORTED;
            return false;
        }

        return true;
    }


    public String getFileFormatType() {
        if (audioType == AudioType.MP3) {
            return "MP3 (.mp3)";
        }
        else {
            return type.toString() + " (." + type.getExtension() + ")";
        }
    }

    /**
     * Writes the contents of the properties together with some additional info.
     */
    private void writeVariablesDebug() {
        for (int i = 0; i < 5; i++) {
            System.out.println();
        }
        System.out.println("Audio info:");
        System.out.println("AudioFileFormat properties:");
        System.out.println("Number of properties:\t" + originalAudioFileFormat.properties().size());
        for (Map.Entry<String, Object> property : originalAudioFileFormat.properties().entrySet()) {
            System.out.println("Property name:\t" + property.getKey() + "\nValue of property:\t" + property.getValue());
        }
        System.out.println();

        // Mostly doesn't write anything
        System.out.println("AudioFormat properties:");
        System.out.println("Number of properties:\t" + decodedAudioFormat.properties().size());
        for (Map.Entry<String, Object> property : decodedAudioFormat.properties().entrySet()) {
            System.out.println("Property name:\t" + property.getKey() + "\nValue of property:\t" + property.getValue());
        }
        System.out.println();

        System.out.println("Extension:\t" + type.getExtension());
        System.out.println("Filetype (mostly WAVE):\t" + audioType);
        System.out.println(decodedAudioFormat);
        System.out.println("Number of channels:\t" + numberOfChannels);
        System.out.println("Type of encoding to waves (mostly PCM):\t" + encoding);
        System.out.println("Frame rate:\t" + frameRate);
        // For mp3 - it is mp3 frame - that means number of samples for time ~0.026 seconds
        System.out.println("Size of frame:\t" + frameSize); // Size of 1 frame
        // frameSize = numberOfChannels * sampleSize
        System.out.println("Sample(Sampling) rate (in Hz):\t" + sampleRate);
        System.out.println("Size of sample (in bits):\t" + sampleSizeInBits); // Size of 1 sample
        System.out.println("Is big endian: " + isBigEndian);
        System.out.println("Size of entire audio file (not just the audio data):\t" + wholeFileSize);

        System.out.println("Size of header:\t" + headerSize);

        // /1000 because it's kbit/s
        System.out.printf("kbit/s:\t%d\n", ((numberOfChannels * sampleRate * sampleSizeInBits) / 1000));
        if (song != null) {
            System.out.println("song length in bytes:\t" + song.length);    // size of song in bytes
        }

        System.out.println("audio length in seconds:\t" + lengthOfAudioInSeconds);
        System.out.println("Audio lengths (in audioFormat hours:mins:secs):\t" +
                           Time.convertSecondsToTime(lengthOfAudioInSeconds, -1));
    }


    /**
     * Puts all the samples together. For example if the audio is stereo, then result 1D array looks like this
     * Puts 1st sample from the 1st channel, then 1st sample from the 2nd channel, then 2nd sample from 1st channel then
     * 2nd sample from 2nd channel, etc. (do that for all the samples).
     *
     * @param channels is 2D byte array. Each byte array represents 1 channels.
     * @return Returns 1D byte array.
     */
    public byte[] createSongFromChannels(byte[][] channels) {
        byte[] song;
        ArrayList<Byte> songList = new ArrayList<>();
        int len;
        byte sample;
        if (channels.length == 1) {        // it is mono
            return channels[0];
        }
        else {
            // Putting channels together to make original song
            len = channels[0].length / sampleSizeInBytes;
            for (int i = 0; i < len; i++) {          // All have same size
                for (int j = 0; j < channels.length; j++) {
                    for (int k = 0; k < sampleSizeInBytes; k++) {
                        sample = channels[j][i * sampleSizeInBytes + k];
                        songList.add(sample);
                    }
                }
            }

            song = new byte[songList.size()];
            for (int i = 0; i < song.length; i++) {
                song[i] = songList.get(i);
            }
            return song;
        }
    }


    public void convertSampleRate(int newSampleRate) throws IOException {
        this.song = AudioConverter.convertSampleRate(this.song, this.sampleSizeInBytes, this.frameSize,
                                                     this.numberOfChannels, this.sampleRate, newSampleRate,
                                                     this.isBigEndian, this.isSigned, false);
        this.sampleRate = newSampleRate;
    }


    public boolean saveAudio(String path, Type type) {
        return AudioWriter.saveAudio(path, this.decodedAudioFormat, this.song, type);
    }


    /**
     * Calls AudioConverter.separateChannelsDouble with corresponding parameters on the internal decoded audio stream.
     *
     * @return
     * @throws IOException
     */
    public double[][] separateChannelsDouble() throws IOException {
        return AudioConverter.separateChannelsDouble(decodedAudioStream, numberOfChannels, sampleSizeInBytes,
                                                     isBigEndian, isSigned, audioStreamSizeInBytes);
    }

    /**
     * Creates new double[] array with normalized values in [-1, 1]. It uses the internal song array, not the stream.
     *
     * @return
     * @throws IOException
     */
    public double[] normalizeToDoubles() throws IOException {
        return AudioConverter.normalizeToDoubles(song, sampleSizeInBytes, sampleSizeInBits, isBigEndian, isSigned);
    }


    public double[] calculateAllAggregations() {
        return Aggregation.calculateAllAggregations(song, sampleSizeInBytes, isBigEndian, isSigned);
    }

    public int[] convertBytesToSamples() throws IOException {
        return AudioConverter.convertBytesToSamples(song, sampleSizeInBytes, isBigEndian, isSigned);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// BPM DETECTION ALGORITHMS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////
    // BPM Algorithm 1
    ////////////////////////////////////////////////////
    public int computeBPMSimple() {
        writeVariablesDebug();      // TODO: Vymazat

        int windowsLen = 43;    // Because 22050 / 43 == 512 == 1 << 9 ... 44100 / 43 == 1024 etc.
        int windowSize = sampleRate / windowsLen;
        windowSize = Utilities.convertToMultipleDown(windowSize, this.frameSize);
        double[] windows = new double[windowsLen];
        return BPMSimple.computeBPM(this.song, windowSize, windows, this.numberOfChannels, this.sampleSizeInBytes,
                                    this.frameSize, this.sampleRate, this.mask, this.isBigEndian, this.isSigned, 4);
    }


    ////////////////////////////////////////////////////
    // BPM Algorithm 2
    ////////////////////////////////////////////////////
    public int computeBPMSimpleWithFreqBands(int subbandCount, SubbandSplitterIFace splitter,
                                             double coef, int windowsBetweenBeats,
                                             double varianceLimit) {
        int historySubbandsCount = 43;    // Because 22050 / 43 == 512 == 1 << 9 ... 44100 / 43 == 1024 etc.
        int windowSize = this.sampleRate / historySubbandsCount;
        int powerOf2After = Utilities.getFirstPowerOfNAfterNumber(windowSize, 2);
        int powerOf2Before = powerOf2After / 2;
        int remainderBefore = windowSize - powerOf2Before;
        int remainderAfter = powerOf2After - windowSize;
        // Trying to get power of 2 closest to the number ... for fft efficiency
        if (remainderAfter > remainderBefore) {
            windowSize = powerOf2Before;
        }
        else {
            windowSize = powerOf2After;
        }

        // But not always is the power of 2 divisible by the frameSize, so we move it
        int mod = windowSize % this.frameSize;
        windowSize += mod;
        DoubleFFT_1D fft = new DoubleFFT_1D(windowSize);
        double[][] subbandEnergies = new double[historySubbandsCount][subbandCount];

        return BPMSimpleWithFreqBands.computeBPM(this.song, this.sampleSizeInBytes, this.sampleRate,
                                                 windowSize, this.isBigEndian, this.isSigned, this.mask,
                                                 this.maxAbsoluteValue, fft, splitter, subbandEnergies,
                                                 coef, windowsBetweenBeats, varianceLimit);
    }
}
