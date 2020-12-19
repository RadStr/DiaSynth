package util.audio.wave;
// When I talk about compiler I mean JVM
// TODO: Remove the next 3 lines after clean up
// When I didn't have much knowledge I did copy-pasting to make the code faster, but now after 2 years I see that
// it was very bad decision and also to compiler optimizes it anyways
// Oznacuju je typicky // TODO: OPTIM-OLD ... nebo jak rikam dole TODO: Copy pasted



// Sometimes in code we can see code duplication where only the parameter referencing to endianness and sign of samples is changing
// that is pretty old code and the reason for that was to minimize branching since I didn't know if compiler will
// look inside the methods and optimize the branching. So we are using convertBytesToIntLittleEndian and
// convertBytesToIntBigEndian and we branch based on endianness instead of just calling convertBytesToInt.
// It should be a bit faster, but it involves code duplication. Since the code is working and won't be changed, I will
// keep it as it is written.



// TODO: Copy pasted - REMOVE ALL THESE, sometimes can be found under:
// TODO: If the compiler doesn't optimize the if outside the loop, then it is really inefficient

// TODO: !!!!!!!!!!!!!!!!!!! Prepsat veskery kod kde se kopiruje na System.arraycopy


///// This is example of tagging part of code for better code clarity

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Audio format conversion methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// public void convertAudio1() {}
// public void convertAudio2() {}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Audio format conversion methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///// This is end of example


// TEMPLATE TO COPY:

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    ///////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    ///////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// END OF TEMPLATE TO COPY


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* -------------------------------------------- [START] -------------------------------------------- */
/////////////////// FFT NOTES
/* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Full FFT on n complex values produces FFT result with n complex values

// FFT EXAMPLE: Let's say we have window size of 4 real numbers. When we perform FFT on 4 complex numbers,
// with imaginary part being 0 and the real part being the real numbers of the window. And with sample rate == 100Hz
// Then we get 4 bins [0] == 0Hz, [1] == 25Hz, [2] == 50Hz, [3] == 75Hz, where [0,1,2] are unique values and [3] is [1] mirrored,
// which means the real_part[1] == real_part[3] and imag_part[1] == -imag_part[3].
// So that is WINDOW_SIZE / 2 + 1 are unique values

// If we have only 3 real numbers then it is [0] == 0Hz, [1] == 33.33Hz, [2] == 66.66Hz
// Here values [0,1] are unique, and [2] is [1] mirrored.
// So now we have again WINDOW_SIZE / 2 + 1 unique values.

// If input is even, then there is the middle value which isn't mirrored.
// If input is odd,  then there isn't the middle value.


// Result of real forward FFT by library method:
// From documentation:
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* --------------------------------------------- [END] --------------------------------------------- */
/////////////////// FFT NOTES
/* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// The length of the window is in number of complex numbers not total length of array



import java.io.*;
import java.util.*;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat.Encoding;

import analyzer.bpm.BPMSimple;
import analyzer.bpm.BPMSimpleWithFreqBands;
import util.Utilities;
import util.audio.*;
import util.audio.io.AudioReader;
import util.audio.io.AudioWriter;
import analyzer.bpm.SubbandSplitterIFace;
import util.Time;
import util.audio.format.AudioFormatWithSign;
import org.jtransforms.fft.DoubleFFT_1D;
import util.audio.format.AudioType;

// TODO: Vsude mit gettery a settery

// TODO: running average filter se chova jinak nez nerekurzivni filtr pro prvnich n samplu, kde n je velikost okenka.


// TODO: not enough time - must remove some configs and try them later - such as weight inits etc. - best solution try it for some small parts and choose the best performing on these small samples
// TODO: Nekde se skore nemeni kdyz je tohle (TODO: nemeni) nekde tak to vymazat to je urceni na to u kterych se to nemeni

// TODO: przc - vymazat je to ted jen na zkouseni - jestli to hledani vhodne konfigurace funguje bez chyby

// TODO: Dropout will try different values later - for that uncomment everything where is TODO: Dropout

// TODO: Napsat metodu co zkontroluje jestli ma pole spravnou delku
//  (Tj. ze tam jsou vsechny framy cely, tedy jestli je tam ten posledni frame cely (Tj. delka pole % frameSize == 0))
public class ByteWave {
    public byte[] song;         // TODO: Bylo static

    private int mask;                   // TODO: At to zbytecne nepocitam pro kazdou metodu zvlast (i kdyz to je lehkej vypocet)
    public int getMask() {
        return mask;
    }

    private File soundFile;
    public AudioInputStream decodedAudioStream;
    private SourceDataLine sourceLine;

    public int numberOfChannels;
    public int sampleRate;
    public int sampleSizeInBits;
    public int sampleSizeInBytes;

    public int wholeFileSize;
    private int onlyAudioSizeInBytes;
    public int getOnlyAudioSizeInBytes() {
        return onlyAudioSizeInBytes;
    }
    private float frameRate;
    public int frameSize;
    public boolean isBigEndian;
    private int kbits;

    public Encoding encoding;
    public boolean isSigned;

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

    private int sizeOfOneSecInFrames;
    public int getSizeOfOneSecInFrames() {
        return sizeOfOneSecInFrames;
    }

    private int sizeOfOneSecBytes;
    public int getSizeOfOneSecInBytes() {
        return sizeOfOneSecBytes;
    }
    private void setSizeOfOneSec() {
        sizeOfOneSecBytes = calculateSizeOfOneSec();
        sizeOfOneSecInFrames = sampleRate;
    }
    public int calculateSizeOfOneSec() { return AudioUtilities.calculateSizeOfOneSec(this.sampleRate, this.frameSize); }


    /**
     * setVariables needs to be called before calling this method because
     * onlyAudioSizeInBytes variable needs to be set to correct byte length of audio and
     * decodedAudioStream needs to be set to correct audio.
     */
    public byte[] convertStreamToByteArray() throws IOException {
        return AudioConverter.convertStreamToByteArray(decodedAudioStream, onlyAudioSizeInBytes);
    }


    // TODO: PROGRAMO
//    /**
//     * This method basically splits the array to channels and from each channel takes the n-th sample.
//     * Internally it is performed a bit different, but the result is the same.
//     *
//     * @param samples          is the input stream containing samples
//     * @param numberOfChannels represents number of channels
//     * @param sampleSize       is the size of 1 sample in a channel
//     * @param n                - Every n-th sample is taken from all channels separately
//     * @param startSample      - The first sample to be taken from each channel
//     * @return Returns 2D byte array, where each array represents the channels where only every n-th sample is taken
//     * @throws IOException is thrown when the error in input stream occurred
//     */
//    @Deprecated // Slow variant - Was creating too large objects on heap which were immediately deleted
//    public static byte[][] takeEveryNthSampleMoreChannels(InputStream samples, int numberOfChannels, int sampleSize, int n, int startSample) throws IOException {
//        byte[][] arr = new byte[numberOfChannels][];
//        int frameSize = sampleSize * numberOfChannels;
//        byte[] oneFrame = new byte[frameSize];
//
//        ArrayList<ArrayList<Byte>> listList = new ArrayList<>();
//        for (int i = 0; i < numberOfChannels; i++) {
//            listList.add(new ArrayList<>());
//        }
//
//        int bytesRead = 0;
//        int arrIndex;
//        int count = 0;
//        int bytesReadSum = 0;
//
//        bytesRead = readNotNeededSamples(samples, sampleSize * numberOfChannels, startSample);
//        while (bytesRead != -1) {
//            arrIndex = 0;
//            bytesReadSum = readNSamples(samples, oneFrame);
//            if (bytesReadSum < oneFrame.length) {
//                break;
//            }
//            if (count % n == 0) {
//                arrIndex = 0;
//                for (int i = 0; i < numberOfChannels; i++) {
//                    for (int j = 0; j < sampleSize; j++) {
//                        listList.get(i).add(oneFrame[arrIndex]);
//                        arrIndex++;
//                    }
//                }
//            }
//            count++;
//        }
//
//        for (int i = 0; i < numberOfChannels; i++) {
//            arr[i] = new byte[listList.get(i).size()];
//            for (int j = 0; j < arr[i].length; j++) {
//                arr[i][j] = listList.get(i).get(j);
//            }
//        }
//
//        return arr;
//    }


    // TODO: PROGRAMO


    public void convertToMono() throws IOException {
        this.song = AudioConverter.convertToMono(this.song, this.frameSize, this.numberOfChannels, this.sampleSizeInBytes,
            this.isBigEndian, this.isSigned);
        this.numberOfChannels = 1;
        this.frameSize = sampleSizeInBytes;
        this.decodedAudioFormat = new AudioFormat(decodedAudioFormat.getEncoding(),
                                                  decodedAudioFormat.getSampleRate(),
                                                  decodedAudioFormat.getSampleSizeInBits(), 1,
                                                  this.frameSize, decodedAudioFormat.getFrameRate(),
                                                  decodedAudioFormat.isBigEndian());
        setSizeOfOneSec();
    }


    // TODO: Nahrazeno volanim pres referenci
//    /**
//     * Converts the audio from samples to mono signal by averaging the samples in 1 frame.
//     *
//     * @param samples          is the input array with samples
//     * @param frameSize        is the size of 1 frame
//     * @param numberOfChannels represents the number of channels
//     * @param sampleSize       is the size of one sample
//     * @param isBigEndian      true if the samples are in big endian, false otherwise.
//     * @param isSigned         true if the samples are signed numbers, false otherwise.
//     * @return Returns 1D byte array which is represents the mono audio gotten from the input array by averaging
//     * the samples in frame
//     * @throws IOException is thrown when method calculateMask failed - fails if the sampleSize is invalid.
//     */
//    public static byte[] convertToMono(byte[] samples, int frameSize, int numberOfChannels,
//                                                   int sampleSize, boolean isBigEndian, boolean isSigned) throws IOException {
//        int sample = 0;
//        int monoSample = 0;
//
//        int mask = calculateMask(sampleSize);
//
//        byte[] monoSong = new byte[samples.length / numberOfChannels];
//        byte[] monoSampleInBytes = new byte[sampleSize];
//
//        for (int index = 0, monoSongIndex = 0; index < samples.length;) {
//            // We take the bytes from end, but it doesn't matter, since we take just the average value
//            monoSample = 0;
//            for (int i = 0; i < numberOfChannels; i++) {
//// TODO: Tenhle for tu podle me nema byt                       for(int j = 0 ; j < sampleSize; j++) {
//                sample = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
//// TODO:                        }
//                monoSample = monoSample + sample;
//                index = index + sampleSize;
//            }
//
//            monoSample = monoSample / numberOfChannels;
//            convertIntToByteArr(monoSampleInBytes, monoSample, isBigEndian);
//            for (int i = 0; i < monoSampleInBytes.length; i++, monoSongIndex++) {
//                monoSong[monoSongIndex] = monoSampleInBytes[i];
//            }
//        }
//
//
//        return monoSong;
//    }

    //    /**
//     * Converts the audio from audioStream to mono signal by averaging the samples in 1 frame.
//     * @param audioStream is the InputStream with samples
//     * @param frameSize is the size of 1 frame
//     * @param frameRate is the frame rate, which is the same as sample rate
//     * @param numberOfChannels represents the number of channels
//     * @param sampleSize is the size of one sample
//     * @return Returns 1D byte array which is represents the mono audio gotten from the input stream by averaging
//     * the samples in frame
//     * @throws IOException is thrown when method calculateMask failed - fails if the sampleSize is invalid.
//     */
//    public static byte[] convertToMono(InputStream audioStream,
//                                                   int frameSize, int frameRate, int numberOfChannels, int sampleSize, boolean isBigEndian) throws IOException {
//
//        int sample = 0;
//        int monoSample = 0;
//
//        int mask = calculateMask(sampleSize);
//        int inverseMask = calculateInverseMaskFromMask(mask);
//
//        ArrayList<Byte> monoSong = new ArrayList<>();
//        int bytesRead = 0;
//        byte[] frame = new byte[frameSize];
//        byte[] monoSampleInBytes = new byte[sampleSize];
//        if(isBigEndian) {				// TODO: Here i have 2 same codes, maybe it can be done better, but right now it is for optimalization
//            while (bytesRead != -1) {
//                try {
//                    bytesRead = readNSamples(audioStream, frame);
//                    int index = 0;
//                    // We take the bytes from end, but it doesn't matter, since we take just the average value
//                    for(int i = 0; i < numberOfChannels; i++) {
//                        sample = 0;
//                        monoSample = 0;
//                        for(int j = 0 ; j < sampleSize; j++) {
//                            sample = convertBytesToIntBigEndian(frame, sampleSize, mask, inverseMask, index);
//                        }
//                        monoSample = monoSample + sample;
//                        index = index + sampleSize;
//                    }
//                    monoSample = monoSample / numberOfChannels;
//                    for(int j = 0; j < sampleSize; j++) {
//                        monoSampleInBytes[j] = (byte) (monoSample >> (j * 8));
//                    }
//
//
//                    for(int i = 0; i < monoSampleInBytes.length; i++) {
//                		monoSong.add(monoSampleInBytes[i]);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            while (bytesRead != -1) {
//                try {
//                    bytesRead = readNSamples(audioStream, frame);
//                    int index = 0;
//                    // We take the bytes from end, but it doesn't matter, since we take just the average value
//                    for(int i = 0; i < numberOfChannels; i++) {
//                        sample = 0;
//                        monoSample = 0;
//                        for(int j = 0 ; j < sampleSize; j++) {
//                            sample = convertBytesToIntLittleEndian(frame, sampleSize, mask, inverseMask, index);
//                        }
//                        monoSample = monoSample + sample;
//                        index = index + sampleSize;
//                    }
//                    monoSample = monoSample / numberOfChannels;
//                    for(int j = 0; j < sampleSize; j++) {
//                        monoSampleInBytes[j] = (byte) (monoSample >> (j * 8));
//                    }
//
//                    for(int i = 0; i < monoSampleInBytes.length; i++) {
//                        monoSong.add(monoSampleInBytes[i]);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        byte[] arr = new byte[monoSong.size()];
//        for(int i = 0; i < arr.length; i++) {
//            arr[i] = monoSong.get(i);
//        }
//
//        return arr;
//    }


    /**
     * Plays the loaded song. It is played in audioFormat given as parameter.
     * @param audioFormat is the audio audioFormat.
     * @param playBackwards if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * @throws LineUnavailableException is thrown when error with playing the song occurred.
     */
    public void playSong(AudioFormat audioFormat, boolean playBackwards) throws LineUnavailableException, IOException {
        if(playBackwards) {
            byte[] songArr = convertStreamToByteArray();
            AudioUtilities.playSong(songArr, audioFormat, playBackwards);
        } else {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);
            line.start();
            int bytesRead = 0;
            byte[] buffer = new byte[frameSize * 256];
            while(bytesRead != -1) {
                bytesRead = decodedAudioStream.read(buffer, 0, buffer.length);
                line.write(buffer, 0, bytesRead);
            }
            line.drain();
        }
    }


    /**
     * Plays the loaded song. Other parameters of this method describe the audioFormat in which will be the audio played.
     * Playing the audio backwards may be too slow, the input stream has to be transformed to byte array first.
     * @param encoding is the encoding of the audio data.
     * @param sampleRate is the sample rate of the audio data.
     * @param sampleSizeInBits is the size of 1 sample in bits.
     * @param numberOfChannels represents the number of channels.
     * @param frameSize is the size of one frame.
     * @param frameRate is the frame rate of the audio.
     * @param isBigEndian is true if the samples are in big endian, false if in little endian
     * @param playBackwards if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * @throws LineUnavailableException is thrown when there is problem with feeding the data to the SourceDataLine.
     * @throws IOException is thrown when error with the input stream occurred.
     */
    public void playSong(Encoding encoding, int sampleRate, int sampleSizeInBits, int numberOfChannels, int frameSize, float frameRate, boolean isBigEndian, boolean playBackwards) throws LineUnavailableException, IOException {
        AudioFormat audioFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits, numberOfChannels, frameSize, frameRate, isBigEndian);
        playSong(audioFormat, playBackwards);
    }


    /**
     * Sets the properties of this class and writes them to output together with some additional info.
     * @param file is the file with the song
     * @param setSong is true, if we want to write the whole song to 1D byte array song (It is property of this class).
     * if it is false, then doesn't set the variable, which may save a lot of memory and time.
     * @throws IOException is thrown if there was problem with reading the info from the file.
     * @return Returns boolean based on the output of setVariables
     */
    public boolean setVariablesAndWriteValues(File file, boolean setSong) throws IOException {
        if(setVariables(file, setSong)) {
            writeVariables();
            return true;
        }
        return false;
    }

    /**
     * Sets the properties of this class and writes them to output together with some additional info.
     * @param path is the path to the song
     * @param setSong is true, if we want to write the whole song to 1D byte array song (It is property of this class).
     * if it is false, then doesn't set the variable, which may save a lot of memory and time.
     * @throws IOException is thrown if there was problem with reading the info from the file.
     * @return Returns boolean based on the output of setVariables
     */
    public boolean setVariablesAndWriteValues(String path, boolean setSong) throws IOException {
        if(setVariables(path, setSong)) {
            writeVariables();
            return true;
        }
        return false;
    }


    /**
     * Sets the properties of this class.
     * @param path is the path to the song
     * @param setSong is true, if we want to write the whole song to 1D byte array song (It is property of this class).
     * if it is false, then doesn't set the variable, which may save a lot of memory and time.
     * @throws IOException is thrown if there was problem with reading the info from the file.
     * @return Returns true if there was no problem with file, false if it couldn't be read (for example it wasn't audio).
     */
    public boolean setVariables(String path, boolean setSong) throws IOException {
        setNameVariables(path);
        if(!setFormatAndStream(path)) {
            return false;
        }
        setVariables();


        if(setSong) {
            setSong();
        }
        else {
            setTotalAudioLength();
        }


        if(!setFormatAndStream(path)) {     // If there was some problem, then something unexpected happened
            return false;                   // Because the previous call succeeded
        }
        return true;
    }

    /**
     * Sets the properties of this class.
     * @param file is the file with the song
     * @param setSong is true, if we want to write the whole song to 1D byte array song (It is property of this class).
     * if it is false, then doesn't set the variable, which may save a lot of memory and time.
     * @throws IOException is thrown if there was problem with reading the info from the file.
     * @return Returns true if there was no problem with file, false if it couldn't be read (for example it wasn't audio).
     */
    public boolean setVariables(File file, boolean setSong) throws IOException {
        setNameVariables(file);
        if(!setFormatAndStream(file)) {
            return false;
        }
        setVariables();

        if(setSong) {
            setSong();
        }
        else {
            setTotalAudioLength();
        }


        if(!setFormatAndStream(file)) {     // If there was some problem, then something unexpected happened
            return false;                   // Because the previous call succeeded
        }
        return true;
    }

    private boolean setTotalAudioLength() throws IOException {
        // TODO: PROGRAMO
        onlyAudioSizeInBytes = AudioReader.getLengthOfInputStream(decodedAudioStream);
        headerSize = wholeFileSize - onlyAudioSizeInBytes;
        decodedAudioStream.close();
        // TODO: PROGRAMO
        return true;
    }

    private void setSong() throws IOException  {
        setTotalAudioLength();
        setFormatAndStream(this.path);
        song = convertStreamToByteArray();
        onlyAudioSizeInBytes = song.length;
        headerSize = wholeFileSize - onlyAudioSizeInBytes;
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
        sampleRate = (int)decodedAudioFormat.getSampleRate();
        setSizeOfOneSec();
        mask = AudioUtilities.calculateMask(sampleSizeInBytes);
        maxAbsoluteValue = AudioUtilities.getMaxAbsoluteValueSigned(sampleSizeInBits);

        wholeFileSize = originalAudioFileFormat.getByteLength();
        kbits = ((numberOfChannels * sampleRate * sampleSizeInBits) / 1000);

        // That is the number of frames that means total number of samples is numberOfChannels * numberOfFrames
        if(this.audioType == AudioType.MP3) {
            // TODO: This MP3 framecount - since here we call frames some different thing
            int frameCount = Integer.parseInt(originalAudioFileFormat.properties().get("mp3.length.frames").toString());
            lengthOfAudioInSeconds = (int)(frameCount * 0.026);        // 0.026s is size of 1 frame
        }
        else {
            int totalNumberOfFrames = originalAudioFileFormat.getFrameLength();
            lengthOfAudioInSeconds = (totalNumberOfFrames / sampleRate);        // Works for wav
        }

        isSigned = AudioFormatWithSign.getIsSigned(encoding);

        if(frameSize != decodedAudioFormat.getFrameSize()) {
            throw new IOException();
        }
    }


    /**
     * Gets the audioFormat of the decoded audio and also gets the audio stream for the decoded audio and sets corresponding properties.
     * For the decoding of mp3 files is used library. If false is returnes, then there is some problem and song should
     * be invalidated.
     * @param path is the path to the file with audio.
     * @return Returns true if all was set correctly, false if there was some problem.
     */
    private boolean setFormatAndStream(String path) {
        try {
            soundFile = new File(path);
        } catch (Exception e) {
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
            if(originalAudioStream != null) {
                originalAudioStream.close();
            }
            originalAudioFileFormat = AudioSystem.getAudioFileFormat(soundFile);
            type = originalAudioFileFormat.getType();
            originalAudioStream = AudioSystem.getAudioInputStream(soundFile);
            originalAudioFormat = originalAudioStream.getFormat();

            if("mp3".equals(type.getExtension())) {
                audioType = AudioType.MP3;
                decodedAudioFormat = new AudioFormat(Encoding.PCM_SIGNED,
                    originalAudioFormat.getSampleRate(),
                    16,
                    originalAudioFormat.getChannels(),
                    originalAudioFormat.getChannels() * 2,
                    originalAudioFormat.getSampleRate(),
                    false);
                // TODO: I should probably later close the original inputStream
                decodedAudioStream = AudioSystem.getAudioInputStream(decodedAudioFormat, originalAudioStream);
            }
            else {
                audioType = AudioType.OTHER;
                decodedAudioFormat = originalAudioFormat;
                decodedAudioStream = originalAudioStream;
            }
        } catch (Exception e) {
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
        if(audioType == AudioType.MP3) {
            return "MP3 (.mp3)";
        }
        else {
            return type.toString() + " (." + type.getExtension() + ")";
        }
    }

    /**
     * Writes the contents of the properties together with some additional info.
     */
    private void writeVariables() {
        // TODO: at mp3 files writes some good properties
        for (int i = 0; i < 5; i++) {
            System.out.println();
        }
        System.out.println("Audio info:");
        System.out.println("AudioFileFormat properties:");
        System.out.println("Number of properties:\t" + originalAudioFileFormat.properties().size());
        for(Map.Entry<String, Object> property : originalAudioFileFormat.properties().entrySet()) {
            System.out.println("Property name:\t" + property.getKey() + "\nValue of property:\t" + property.getValue());
        }
        System.out.println();

        // TODO: mostly doesn't write anything
        System.out.println("AudioFormat properties:");
        System.out.println("Number of properties:\t" + decodedAudioFormat.properties().size());
        for(Map.Entry<String, Object> property : decodedAudioFormat.properties().entrySet()) {
            System.out.println("Property name:\t" + property.getKey() + "\nValue of property:\t" + property.getValue());
        }
        System.out.println();

        System.out.println("Extension:\t" + type.getExtension());
        System.out.println("Filetype (mostly WAVE):\t" + audioType);
        System.out.println(decodedAudioFormat);
        System.out.println("Number of channels:\t" + numberOfChannels);
        System.out.println("Type of encoding to waves (mostly PCM):\t" + encoding);
        System.out.println("Frame rate:\t" + frameRate);
        System.out.println("Size of frame:\t" + frameSize); // Size of 1 frame
        // frameSize = numberOfChannels * sampleSize
        // TODO: Zase nefunguje u mp3 - tam je frame ten mp3 frame to jsou samply co majĂ­ 0.23 sekund
        System.out.println("Sample(Sampling) rate (in Hz):\t" + sampleRate);
        System.out.println("Size of sample (in bits):\t" + sampleSizeInBits); // Size of 1 sample
        System.out.println("Is big endian: " + isBigEndian);
        System.out.println("Size of entire audio file (not just the audio data):\t" + wholeFileSize);

        System.out.println("Size of header:\t" + headerSize);

        System.out.printf("kbit/s:\t%d\n", ((numberOfChannels * sampleRate * sampleSizeInBits) / 1000));	// /1000 because it's kbit/s
        if(song != null) {
            System.out.println("song length in bytes:\t" + song.length);	// size of song in bytes
        }

        System.out.println("audio length in seconds:\t" + lengthOfAudioInSeconds);
        System.out.println("Audio lengths (in audioFormat hours:mins:secs):\t" + Time.convertSecondsToTime(lengthOfAudioInSeconds, -1));
    }


    /**
     * Puts all the samples together. For example if the audio is stereo, then result 1D array looks like this
     * Puts 1st sample from the 1st channel, then 1st sample from the 2nd channel, then 2nd sample from 1st channel then
     * 2nd sample from 2nd channel, etc. (do that for all the samples).
     * @param channels is 2D byte array. Each byte array represents 1 channels.
     * @return Returns 1D byte array.
     */
    public byte[] createSongFromChannels(byte[][] channels) {
        byte[] song;
        ArrayList<Byte> songList = new ArrayList<>();
        int len;
        byte sample;
        if(channels.length == 1) {		// it is mono
            return channels[0];
        } else {
            // Putting channels together to make original song
            len = channels[0].length / sampleSizeInBytes;
            for(int i = 0; i < len; i++) {		// All have same size
                for(int j = 0; j < channels.length; j++) {
                    for(int k = 0; k < sampleSizeInBytes; k++) {
                        sample = channels[j][i * sampleSizeInBytes + k];
                        songList.add(sample);
                    }
                }
            }

            song = new byte[songList.size()];
            for(int i = 0; i < song.length; i++) {
                song[i] = songList.get(i);
            }
            return song;
        }
    }


    //    // TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Neresim Signed, Unsigned
//    /**
//     * Converts byte array to int samples of size sampleSize.
//     * @param byteSamples are the samples in 1D byte array.
//     * @param sampleSize is the size of one sample in bytes.
//     * @param isBigEndian is true if the samples are in big endian, false otherwise.
//     * @return Returns the samples as 1D array of ints.
//     * @throws IOException is thrown when the sample size is invalid.
//     */
//    public static int[] convertBytesToSamples(byte[] byteSamples, int sampleSize, boolean isBigEndian) throws IOException {
//        int[] result = new int[byteSamples.length / sampleSize];
//
//        int arrIndex;
//        int mask = calculateMask(sampleSize);
//        int inverseMask = calculateInverseMaskFromMask(mask);
//        if(isBigEndian) {
//            arrIndex = 0;
//            for(int i = 0; i < result.length; i++) {
//                result[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, inverseMask, arrIndex);
//                arrIndex = arrIndex + sampleSize;
//            }
//        } else {
//            arrIndex = 0;
//            for(int i = 0; i < result.length; i++) {
//                result[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, inverseMask, arrIndex);
//                arrIndex = arrIndex + sampleSize;
//            }
//        }
//
//        return result;
//    }


    public void convertSampleRate(int newSampleRate) throws IOException {
        this.song = AudioConverter.convertSampleRate(this.song, this.sampleSizeInBytes, this.frameSize,
                this.numberOfChannels, this.sampleRate, newSampleRate,
                this.isBigEndian, this.isSigned, false);
        this.sampleRate = newSampleRate;
    }


    //    @Deprecated
//    public static double[] performOperationOnSamples(double[] samples, double[] changeValues,
//                                              int startSamplesIndex, int startChangeValuesIndex, int outputStartIndex,
//                                              int len, ArithmeticOperation op) {
//        double[] retArr = new double[samples.length];
//        performOperationOnSamples(samples, changeValues, retArr, startSamplesIndex, startChangeValuesIndex, outputStartIndex, len, op);
//        return retArr;
//    }


//    @Deprecated
//    public static void performOperationOnSamples(double[] samples, double[] changeValues, double[] outputArr,
//                                          int startSamplesIndex, int startChangeValuesIndex, int outputStartIndex,
//                                          int len, ArithmeticOperation op) {
//        int changeValuesEndIndex = startChangeValuesIndex + len;
//        for(int indexInChangeValues = startChangeValuesIndex, samplesIndex = startSamplesIndex, outputIndex = outputStartIndex;
//                indexInChangeValues < changeValuesEndIndex;
//                indexInChangeValues++, samplesIndex++, outputIndex++) {
//            outputArr[outputIndex] = ByteWave.performOperation(samples[samplesIndex], changeValues[indexInChangeValues], op);
//        }
//    }



    public boolean saveAudio(String path, Type type) {
        return AudioWriter.saveAudio(path, this.decodedAudioFormat, this.song, type);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// BPM DETECTION ALGORITHMS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////
    // BPM Algorithm 1
    ////////////////////////////////////////////////////
    public int computeBPMSimple() {
        writeVariables();

        int windowsLen = 43;    // Because 22050 / 43 == 512 == 1 << 9 ... 44100 / 43 == 1024 etc.
        int windowSize = sampleRate / windowsLen;
        windowSize = Utilities.convertToMultipleDown(windowSize, this.frameSize);
        double[] windows = new double[windowsLen];                       // TODO: Taky bych mel mit jen jednou asi ... i kdyz tohle je vlastne sampleRate specific
        return BPMSimple.computeBPM(this.song, windowSize, windows, this.numberOfChannels, this.sampleSizeInBytes, this.frameSize,
                                  this.sampleRate, this.mask, this.isBigEndian, this.isSigned, 4);
    }


    ////////////////////////////////////////////////////
    // BPM Algorithm 2
    ////////////////////////////////////////////////////
    public int computeBPMSimpleWithFreqBands(int subbandCount, SubbandSplitterIFace splitter,
                                             double coef, int windowsBetweenBeats,
                                             double varianceLimit) {  // TODO: Bud predavat ty referenci nebo ne ... ono to nedava uplne smysl to predavat referenci
        // TODO: Dava smysl ze to vytvorim tady ... protoze to vyrabim v zavislosti na sample rate a tak


         int historySubbandsCount = 43;    // Because 22050 / 43 == 512 == 1 << 9 ... 44100 / 43 == 1024 etc.
         int windowSize = this.sampleRate / historySubbandsCount;
         int powerOf2After = Utilities.getFirstPowerOfNAfterNumber(windowSize, 2);
         int powerOf2Before = powerOf2After / 2;
         int remainderBefore = windowSize - powerOf2Before;
         int remainderAfter = powerOf2After - windowSize;
         if(remainderAfter > remainderBefore) {       // Trying to get power of 2 closest to the number ... for fft efficiency
             windowSize = powerOf2Before;
         }
         else {
             windowSize = powerOf2After;
         }

         int mod = windowSize % this.frameSize;     // But not always is the power of 2 divisible by the frameSize
         // TODO: DEBUG
//         ProgramTest.debugPrint("window size (2nd bpm alg):", windowSize);        // TODO: remove
         // TODO: DEBUG
         windowSize += mod;
         DoubleFFT_1D fft = new DoubleFFT_1D(windowSize);
         double[][] subbandEnergies = new double[historySubbandsCount][subbandCount];

         try {
             return BPMSimpleWithFreqBands.computeBPM(this.song, this.sampleSizeInBytes, this.sampleRate,
                 windowSize, this.isBigEndian, this.isSigned, this.mask, this.maxAbsoluteValue, fft, splitter,
                     subbandEnergies, coef, windowsBetweenBeats, varianceLimit);
         }
         catch (IOException e) {
             return -1;             // TODO:
         }
     }





    // TODO: Dont create new array in FFT only measures
    // TODO: Verze s tim ze se to bude delat po 2jicich ta FFT - s realnou i komplexni casti
    // TODO: THIS IS VERSION FOR MONO SIGNAL
    // TODO: double[][][] subbandEnergies in multiple channel case
//    public static int getBPMSimpleWithFreqDomains(byte[] samples, int sampleSize, int sampleSizeInBits,
//                                                  int windowSize, boolean isBigEndian, boolean isSigned,
//                                                  int mask, int maxAbsoluteValue, DoubleFFT_1D fft, SubbandSplitterIFace splitter,
//                                                  double[][] subbandEnergies // TODO: 1D are the past values, 2D are the subbands
//                                                  ) throws IOException { // TODO: Predpokladam ,ze subbandEnergies uz je alokovany pole o spravny velikosti
//
///*
//        int bpm = 0;
//        double fft;
//        int windowSizeInBytes = sampleSize * windowSize;        // TODO: * frameSize
//        for(i = 0; i < windows.length; i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
//            if(nextSampleIndex < samples.length) {
//                windows[i] = getEnergy(samples, windowSize, numberOfChannels, sampleSize, sampleIndex, mask,
//                    isBigEndian, isSigned);
//                avg += windows[i];
//            }
//        }
//
//        for (int index = 0; index < samples.length; index += jumpInBytes) {
//
//            // TODO: Tahle metoda vypocita jen cast FFT o dane velikosti (tedy vraci pole doublu)
//            // TODO: V obecnem pripade tahle metoda bude bud taky vracet double[] s tim ze proste vezme hodnoty
//            // TODO: kanalu (pres preskakovani tj numberOfChannels * sampleSize je dalsi index ktery mam vzit)
//            // TODO: nebo proste tu metodu udelat tak aby vratila double[][] kde to bude double[numberOfChannels][windowSize]
//            // TODO: takze tam musim dat index
////            double[] fft = calculateFFTOnlyMeasuresGetOnlyOnePart(samples, sampleSize, sampleSizeInBits, windowSize, isBigEndian, isSigned);
//            // TODO: !!!!!!!!! Tak jeste jinak ... rovnou spocitam energie tech subbandu ... zase jen v ty jedny casti
//            // TODO: !!!!!!!!! Pro vic kanalu zase musim pres double[][]
//            double[] subbandEnergies = getSubbandEnergiesUsingFFT(...); // TODO: !!!! Zase to delat spis jen pres referenci
//
//        }
//        return bpm;
// */
//
//
//// TODO:
//        int numberOfChannels = 1;
//        int frameSize = sampleSize;
//// TODO:
//
//        int subbandCount = subbandEnergies[0].length;
//        int historySubbandsCount = subbandEnergies.length;
//
//        double[] fftArr = new double[windowSize];
//
//        // TODO: Zbytecny staci aby to pole melo polovicni velikost (viz kod pod tim)
////double[] measuresArr = new double[windowSize];        // TODO: Muzu pouzit fftArr jako measuresArr, ale takhle to je prehlednesji a navic pak chci ty vysledky fft ulozit do souboru abych to uz nemusel pocitat
//        double[] measuresArr;
//        if(windowSize % 2 == 0) {			// It's even
//            measuresArr = new double[windowSize / 2 + 1];
//        } else {
//            measuresArr = new double[(windowSize + 1) / 2];
//        }
//
//
//        int bpm = 0;
//        int sampleIndex = 0;
//        int i;
//        int windowSizeInBytes = windowSize * sampleSize;     // TODO: frameSize v vice multichannel variante
//        int nextSampleIndex = windowSizeInBytes;
//        //TODO: Asi zase predat jako argument ... tady je to pole protoze kazdy subband ma vlastni average
//        double[] avgs = new double[subbandCount];  // TODO: Pro vice kanalove to bude double[][]
//        double[] currEnergies = new double[subbandCount];
//        for(i = 0; i < subbandEnergies.length; // TODO: U multi-channel varianty to bude subbandEnergies[0].length
//            i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
//            if(nextSampleIndex < samples.length) {
//                getSubbandEnergiesUsingFFT(samples, subbandEnergies[i], sampleIndex,//int startIndex,
//                    numberOfChannels, sampleSize, frameSize, mask, fft, fftArr, measuresArr,
//                    maxAbsoluteValue, isBigEndian, isSigned, splitter);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy
//// TODO:                subbandEnergies[i] = currEnergies;
//                for(int j = 0; j < subbandEnergies[i].length; j++) {
//                    avgs[j] += subbandEnergies[i][j];
//                }
//            }
//        }
//
//        double coef = 20;
//        double avgAfterDiv;
//
//        int oldestIndexInSubbands = 0;
//        while(nextSampleIndex < samples.length) {
//            getSubbandEnergiesUsingFFT(samples, currEnergies, sampleIndex,//int startIndex,
//                numberOfChannels, sampleSize, frameSize, mask, fft, fftArr, measuresArr,
//                maxAbsoluteValue, isBigEndian, isSigned, splitter);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy
//            //            currEnergies = getSubbandEnergiesUsingFFT(...);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy
//
//            int j = 0;
//            for(; j < currEnergies.length; j++) {
//                avgAfterDiv = avgs[j] / historySubbandsCount; // TODO:
//                System.out.println(currEnergies[j] + ":\t" + avgAfterDiv + ":\t" + (coef * avgAfterDiv));
//                if (currEnergies[j] > coef * avgAfterDiv) {        // TODO: Tady beru ze kdyz je beat na libovolnym mistem - pak typicky budu chtit brat beaty jen z urcitych frekvencnich pasem
//                    bpm++;
//                    break;
//                }
//                updateEnergySumsAndSubbands(j, oldestIndexInSubbands, avgs, currEnergies[j], subbandEnergies);
//            }
//
//            // TODO: I do this because of the break, I found beat but I still have to update the values
//            // TODO: Ideally I want to do this in the previous for cycle,
//            for(; j < currEnergies.length; j++) {
//                updateEnergySumsAndSubbands(j, oldestIndexInSubbands, avgs, currEnergies[j], subbandEnergies);
//            }
//
//            oldestIndexInSubbands++;
//            sampleIndex = nextSampleIndex;
//            nextSampleIndex += windowSizeInBytes;
//
//
//            // Again optimize the case when windows.length is power of 2
//            if (historySubbandsCount % 2 == 0) {       // TODO: U multi-channel verze chci subbandEnegies[i].length
//                oldestIndexInSubbands %= historySubbandsCount; // TODO: U multi-channel verze chci subbandEnegies[i].length
//            } else {
//                if (oldestIndexInSubbands >= historySubbandsCount) { // TODO: U multi-channel verze chci subbandEnegies[i].length
//                    oldestIndexInSubbands = 0;
//                }
//            }
//        }
//
//        return bpm;
//    }


    //    // From documentation:
////	if n is even then
////	 a[2*k] = Re[k], 0<=k<n/2
////	 a[2*k+1] = Im[k], 0<k<n/2
////	 a[1] = Re[n/2]
////
////
////	if n is odd then
////	 a[2*k] = Re[k], 0<=k<(n+1)/2
////	 a[2*k+1] = Im[k], 0<k<(n-1)/2
////	 a[1] = Im[(n-1)/2]
//    // TODO: Tohle je skoro konvoluce, akorat vysledky neukladame do pole ktere bude obsahovat vysledek konvoluce ale pocitame rovnou energii
//    // TODO: A energii pocitame tak ze bereme vysledky konvoluce na druhou (realnou a imaginarni slozku zvlast) (protoze pocitame absolutni hodnotu)
//    public static double getCombFilterEnergyRealForward(double[] fftResult, double[] bpmArray) {      // TODO: Monoverze
//        double energy;              // TODO: mozna takhle prepsat i ten prevod na realny ... je to prehlednejsi
//        double real;                // TODO: Ten prevod na realny mozna ani nebude dobre
//        double imag;
//        if(fftResult.length % 2 == 0) {			// It's even
//            real = fftResult[0] * bpmArray[0];
//            energy = calculateComplexNumMeasure(real, 0);
//            real = fftResult[1] * bpmArray[1];      // TODO: Prehozeny poradi bylo to zatim for cyklem ... v te convertImagToReal to delat nemusim protoze tam to prevadim do pole polovicni velikosti
//            energy += calculateComplexNumMeasure(real, 0);
//            for(int i = 2; i < fftResult.length; i = i + 2) {
//                real = fftResult[i] * bpmArray[i];
//                imag = fftResult[i+1] * bpmArray[i+1];
//                energy += calculateComplexNumMeasure(real, imag);
//            }
//        } else {
//            real = fftResult[0] * bpmArray[0];
//            energy = calculateComplexNumMeasure(real, 0);
//            for(int i = 2; i < fftResult.length - 1; i = i + 2) {
//                real = fftResult[i] * bpmArray[i];
//                imag = fftResult[i+1] * bpmArray[i+1];
//                energy += calculateComplexNumMeasure(real, imag);
//            }
//
//            real =  fftResult[fftResult.length - 1] * bpmArray[fftResult.length - 1];
//            imag = fftResult[1] * bpmArray[1];
//            energy += calculateComplexNumMeasure(real, imag);
//        }
//
//        return energy;
//    }


    // TODO: To je p[odle me jen napsana ta jednoducha verze ... muzu to pak vymazat
//        int bpm = 0;
//        int[] maxBPMIndexes = 0;
//        double[] maxEnergies = 0;
//        double[] energies = new double[subbandCount];
//        for(int i = 0; i < bpmArrays.length; i++) {
//            double[][] fftResults = calculateFFTRealForwardOnlyMeasures(samples, sampleSize, sampleSizeInBits, // TODO: Tahle metoda se casto pouziva se stejnym FFT oknem ... nema smysl vytvaret porad ten samy
//                windowSize, isBigEndian, isSigned);     // TODO: tohle vraci measury ... nikoliv imag a real cast ... prizpusobit k tomu tu metodu
//            // TODO: A jeste ten nechci volat na cely song ... vypocetne narocny ... melo by se to delat na nejakou 5ti sekundovou cast
//            // TODO: A funguje na mono
//            // TODO: !!!!!!!!!!!!!!
//            getCombFilterEnergyRealForward(fftResults, bpmArrays[i], energies);
//            for(int j = 0; j < energies.length; j++) {
//                if (energies[j] > maxEnergies[j]) {
//                    maxEnergies[j] = energies[j];
//                    maxBPMIndexes[j] = i;
//                }
//            }
//        }
//
//        return maxEnergy;
//    }


    ////////////////////////////////////////////////////////////////////////////////////
    ///////////////////// Audio visualization
    ////////////////////////////////////////////////////////////////////////////////////
    // Very important information - The maximum energy of FFT bin is equal to window size and it is in case, when
    // there are only ones in window. !!! But this only applies to case where the input double values are normalized between -1 and 1.


// TODO: Sice pekny, ale nemam cas si to implementovat, jen pouziju uz to naprogramovany convertovani
//    public static void convertFormat(byte[] audio, int oldSampleRate, boolean oldIsBigEndian, boolean oldIsSigned,
//                                     int oldSampleSize, int oldNumberOfChannels,
//                                     int newSampleRate, boolean newIsBigEndian, boolean newIsSigned,
//                                     int newSampleSize, int newNumberOfChannels) {
//        int oldFrameSize = oldSampleSize * oldNumberOfChannels;
//        int newFrameSize = newSampleSize * newNumberOfChannels;
//        if(oldSampleRate != newSampleRate) {
//            audio = convertSampleRate(audio, oldSampleSize, oldFrameSize, oldNumberOfChannels,
//                oldSampleRate, newSampleRate, oldIsBigEndian, oldIsSigned);
//        }
//        if(oldIsBigEndian != newIsBigEndian) {
//            convertEndianity(oldIsBigEndian, newIsBigEndian);
//        }
//        if(oldIsSigned != newIsSigned) {
//            convertSign(oldIsSigned, newIsSigned);
//        }
//        if(oldNumberOfChannels != newNumberOfChannels) {
//            convertNumberOfChannels(oldNumberOfChannels, newNumberOfChannels);
//        }
//    }


}


//Math.ceil(1) = 1
