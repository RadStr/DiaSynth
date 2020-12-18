package Rocnikovy_Projekt;
// When I talk about compiler I mean JVM
// TODO: Remove the next 2 lines after clean up
// When I didn't have much knowledge I did copy-pasting to make the code faster, but now after 2 years I see that
// it was very bad decision and also to compiler optimizes it anyways



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

import analyzer.bpm.BPMUtils;
import test.ProgramTest;
import util.Utilities;
import util.audio.*;
import util.audio.filter.NonRecursiveFilter;
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
public class Program {
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

    public int lengthOfAudioInSeconds;

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

    public static String fileWithModel = "fileWithTheModel";

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
     * Converts given stream to byte array,
     * setVariables needs to be called before calling this method because
     * onlyAudioSizeInBytes variable needs to be set to correct byte length of audio
     * @param stream is the stream to convert
     * @return returns the converted stream
     * @throws IOException if error with stream occurred
     */
    public byte[] convertStreamToByteArray(InputStream stream) throws IOException {
        byte[] converted = new byte[onlyAudioSizeInBytes];
        int readCount = 0;
        int totalLen = 0;
        int readLen = stream.available();
        if(readLen <= 0) {
            readLen = 4096;
        }
        else {
            readLen = Math.min(readLen, 4096);
        }
        while(readCount != -1) {
            readCount = stream.read(converted, totalLen, readLen);
            totalLen += readCount;
        }

        return converted;
    }


    /**
     * Converts the 2D array to 1D array by stacking the labelReferenceArrs
     *
     * @param arr is the 2D array to be converted to 1D array
     * @return Returns 1D array
     */
    public static byte[] convertTwoDimArrToOneDim(byte[][] arr) {
        int length = 0;
        for (int i = 0; i < arr.length; i++) {
            length = length + arr[i].length;
        }

        byte[] result = new byte[length];
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                result[index] = arr[i][j];
                index++;
            }
        }

        return result;
    }


    /**
     * Takes the input stream and returns the samples of channels in byte labelReferenceArrs (1 array = 1 channel).
     *
     * @param samples    is the input stream containing samples
     * @param sampleSize is the size of 1 sample in a channel
     * @param totalAudioLength is the total length of the samples input in bytes.
     * @return Returns 2D byte array, where each byte array corresponds to 1 channel.
     * @throws IOException is thrown when error with input
     */
    public static byte[][] separateChannels(InputStream samples, int numberOfChannels, int sampleSize,
                                            int totalAudioLength) throws IOException {
        // TODO: PROGRAMO
        //return takeEveryNthSampleMoreChannels(samples, numberOfChannels, sampleSize, 1, 0);
        return AudioProcessor.getEveryNthSampleMoreChannels(samples, numberOfChannels, sampleSize, 1, 0, totalAudioLength);
        // TODO: PROGRAMO
    }


    /**
     * Takes the input stream and returns the samples of channels in double[][] (1 array = 1 channel).
     *
     * @param samples    is the input stream containing samples
     * @param sampleSize is the size of 1 sample in a channel
     * @return Returns 2D byte array, where each double array corresponds to 1 channel.
     * @throws IOException is thrown when error with input
     */
    public static double[][] separateChannelsDouble(InputStream samples, int numberOfChannels, int sampleSize,
                                                    boolean isBigEndian, boolean isSigned, int totalAudioLength) throws IOException {
        return AudioProcessor.getEveryNthSampleMoreChannelsDouble(samples, numberOfChannels, sampleSize, 1,
            0, isBigEndian, isSigned, totalAudioLength);
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


    /**
     * Takes the byte array with samples and returns the samples of channels in byte labelReferenceArrs (1 array = 1 channel).
     *
     * @param samples    is the byte array containing samples
     * @param sampleSize is the size of 1 sample in a channel
     * @return Returns 2D byte array, where each byte array corresponds to 1 channel.
     * @throws IOException is thrown when error with input
     */
    public static byte[][] separateChannels(byte[] samples, int numberOfChannels, int sampleSize) throws IOException {
        return AudioProcessor.getEveryNthSampleMoreChannels(samples, numberOfChannels, sampleSize, 1, 0);
    }


    public void convertToMono() throws IOException {
        this.song = convertToMono(this.song, this.frameSize, this.numberOfChannels, this.sampleSizeInBytes,
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

    /**
     * Converts the audio from samples to mono signal by averaging the samples in 1 frame.
     *
     * @param samples          is the input array with samples
     * @param frameSize        is the size of 1 frame
     * @param numberOfChannels represents the number of channels
     * @param sampleSize       is the size of one sample
     * @param isBigEndian      true if the samples are in big endian, false otherwise.
     * @param isSigned         true if the samples are signed numbers, false otherwise.
     * @param monoSong         is the arraz in which will be stored the resulting mono song.
     * @throws IOException is thrown when method calculateMask failed - fails if the sampleSize is invalid.
     */
    public static void convertToMono(byte[] samples, int frameSize, int numberOfChannels, int sampleSize,
                                     boolean isBigEndian, boolean isSigned, byte[] monoSong) throws IOException {
        int sample = 0;
        int monoSample = 0;

        int mask = AudioUtilities.calculateMask(sampleSize);

        byte[] monoSampleInBytes = new byte[sampleSize];

        for (int index = 0, monoSongIndex = 0; index < samples.length;) {
            // We take the bytes from end, but it doesn't matter, since we take just the average value
            monoSample = 0;
            for (int i = 0; i < numberOfChannels; i++) {
// TODO: Tenhle for tu podle me nema byt                       for(int j = 0 ; j < sampleSize; j++) {
                sample = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
// TODO:                        }
                monoSample = monoSample + sample;
                index += sampleSize;
            }

            monoSample = monoSample / numberOfChannels;
            convertIntToByteArr(monoSampleInBytes, monoSample, isBigEndian);
            for (int i = 0; i < monoSampleInBytes.length; i++, monoSongIndex++) {
                monoSong[monoSongIndex] = monoSampleInBytes[i];
            }
        }

    }


    /**
     * Converts the audio from samples to mono signal by averaging the samples in 1 frame.
     *
     * @param samples          is the input array with samples
     * @param frameSize        is the size of 1 frame
     * @param numberOfChannels represents the number of channels
     * @param sampleSize       is the size of one sample
     * @param isBigEndian      true if the samples are in big endian, false otherwise.
     * @param isSigned         true if the samples are signed numbers, false otherwise.
     * @return Returns 1D byte array which is represents the mono audio gotten from the input array by averaging
     * the samples in frame
     * @throws IOException is thrown when method calculateMask failed - fails if the sampleSize is invalid.
     */
    public static byte[] convertToMono(byte[] samples, int frameSize, int numberOfChannels,
                                       int sampleSize, boolean isBigEndian, boolean isSigned) throws IOException {
        byte[] monoSong = new byte[samples.length / numberOfChannels];
        convertToMono(samples, frameSize, numberOfChannels, sampleSize, isBigEndian, isSigned, monoSong);

// TODO: 60 BPM stereo - not both channels are the same
/*
// TODO:
// TODO: Tohle je dobrej test, kdyz jsou oba kanaly stejny
        for(int i = 0, monoIndex = 0; i < samples.length; i += (numberOfChannels - 1) * sampleSize) {
            for(int j = 0; j < sampleSize; j++, i++, monoIndex++) {
                if(monoSong[monoIndex] != samples[i]) {
                    System.out.println(monoSong[monoIndex] + "\t" + samples[i]);
                    System.exit(1);
                }
            }
        }
// TODO:
*/
        return monoSong;
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

    // TODO: Tohle je nova verze konverze do mona
    /**
     * Converts the audio from audioStream to mono signal by averaging the samples in 1 frame.
     * @param audioStream is the InputStream with samples
     * @param frameSize is the size of 1 frame
     * @param numberOfChannels represents the number of channels
     * @param sampleSize is the size of one sample
     * @param isBigEndian true if the samples are in big endian, false otherwise.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @return Returns 1D byte array which is represents the mono audio gotten from the input stream by averaging
     * the samples in frame
     * @throws IOException is thrown when method calculateMask failed - fails if the sampleSize is invalid.
     */
    public static byte[] convertToMono(InputStream audioStream, int frameSize, int numberOfChannels,
                                       int sampleSize, boolean isBigEndian, boolean isSigned) throws IOException {

        int sample = 0;
        int monoSample = 0;

        int mask = AudioUtilities.calculateMask(sampleSize);

        ArrayList<Byte> monoSong = new ArrayList<>();
        int bytesRead = 0;
        byte[] frame = new byte[frameSize];
        byte[] monoSampleInBytes = new byte[sampleSize];

        while (bytesRead != -1) {
            try {
                bytesRead = AudioReader.readNSamples(audioStream, frame);
                int index = 0;
                // We take the bytes from end, but it doesn't matter, since we take just the average value
                monoSample = 0;
                for(int i = 0; i < numberOfChannels; i++) {
// TODO: Tenhle for tu podle me nema byt                       for(int j = 0 ; j < sampleSize; j++) {
                    sample = convertBytesToInt(frame, sampleSize, mask, index, isBigEndian, isSigned);
// TODO:                        }
                    monoSample = monoSample + sample;
                    index = index + sampleSize;
                }

                monoSample = monoSample / numberOfChannels;
                convertIntToByteArr(monoSampleInBytes, monoSample, isBigEndian);
                for(int i = 0; i < monoSampleInBytes.length; i++) {
                    monoSong.add(monoSampleInBytes[i]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] arr = new byte[monoSong.size()];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = monoSong.get(i);
        }

        return arr;
    }
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
     * Converts the sizeInBytes least significant bytes of int given in parameter numberToConvert to byte array of size sizeInBytes.
     * @param sizeInBytes is the size of the number in bytes.
     * @param numberToConvert is the number to be converted.
     * @param convertToBigEndian is boolean variable, if true, then the first byte in array contains the most significant
     * byte of the number, if false, then it contains the least significant
     * @return Returns byte array of size sizeInBytes, which contains the converted number.
     */
    public static byte[] convertIntToByteArr(int sizeInBytes, int numberToConvert, boolean convertToBigEndian) {
        byte[] converted = new byte[sizeInBytes];

        if(convertToBigEndian) {
            for (int i = sizeInBytes - 1; i >= 0; i--) {
                converted[i] = (byte) numberToConvert;
                numberToConvert = numberToConvert >> 8;
            }
        } else {
            for (int i = 0; i < sizeInBytes; i++) {
                converted[i] = (byte) numberToConvert;
                numberToConvert = numberToConvert >> 8;
            }
        }

        return converted;
    }

    // TODO: Can be solved by calling the general convertIntToByteArr method, this should be a bit faster, but it doesn't matter
    /**
     * Fills given array with int given in parameter numberToConvert.
     * @param arr is the array to be filled with bytes of numberToConvert in given endianity.
     * @param numberToConvert is the number to be converted.
     * @param convertToBigEndian is boolean variable, if true, then the first byte in array contains the most significant
     * byte of the number, if false, then it contains the least significant
     */
    public static void convertIntToByteArr(byte[] arr, int numberToConvert, boolean convertToBigEndian) {
        if(convertToBigEndian) {
            for (int i = arr.length - 1; i >= 0; i--) {
                arr[i] = (byte) numberToConvert;
                numberToConvert = numberToConvert >> 8;
            }
        } else {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = (byte) numberToConvert;
                numberToConvert = numberToConvert >> 8;
            }
        }
    }


    /**
     * Converts given number to bytes and put those bytes in the byte array starting at startIndex.
     * @param arr is the byte array.
     * @param numberToConvert is the array to be converted.
     * @param sampleSize is the number of bytes to be converted.
     * @param startIndex is the starting index, where should be put the first byte.
     * @param convertToBigEndian tells if we should convert to big endian or not.
     */
    public static void convertIntToByteArr(byte[] arr, int numberToConvert, int sampleSize,
                                           int startIndex, boolean convertToBigEndian) {   // TODO: Nova metoda
        int endIndex = startIndex + sampleSize;                                         // TODO: Predchozi metodu lze prepsat touto
        if(convertToBigEndian) {
            endIndex--;
            for (; endIndex >= startIndex; endIndex--) {
                arr[endIndex] = (byte) numberToConvert;
                numberToConvert = numberToConvert >> 8;
            }
        } else {
            for (; startIndex < endIndex; startIndex++) {
                arr[startIndex] = (byte) numberToConvert;
                numberToConvert = numberToConvert >> 8;
            }
        }
    }


    /**
     * Plays the audio given in the input stream in audio audioFormat given as parameter.
     * @param song is the input stream with the samples to be played.
     * @param audioFormat is the audio audioFormat.
     * @param playBackwards if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * @throws LineUnavailableException is thrown when error with playing the song occurred.
     */
    public void playSong(InputStream song, AudioFormat audioFormat, boolean playBackwards) throws LineUnavailableException, IOException {
        if(playBackwards) {
            byte[] songArr = convertStreamToByteArray(song);
            AudioUtilities.playSong(songArr, audioFormat, playBackwards);
        } else {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);
            line.start();
            int bytesRead = 0;
            byte[] buffer = new byte[frameSize * 256];
            while(bytesRead != -1) {
                bytesRead = song.read(buffer, 0, buffer.length);
                line.write(buffer, 0, bytesRead);
            }
            line.drain();
        }
    }


    /**
     * Plays the song given in the input stream song. Other parameters of this method describe the audioFormat in which will be the audio played.
     * Playing the audio backwards may be too slow, the input stream has to be transformed to byte array first.
     * @param song is the input stream containing samples, which will be played.
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
    public void playSong(InputStream song, Encoding encoding, int sampleRate, int sampleSizeInBits, int numberOfChannels, int frameSize, float frameRate, boolean isBigEndian, boolean playBackwards) throws LineUnavailableException, IOException {
        AudioFormat audioFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits, numberOfChannels, frameSize, frameRate, isBigEndian);
        playSong(song, audioFormat, playBackwards);
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
        song = convertStreamToByteArray(decodedAudioStream);
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


    /**
     * Takes the 1D byte array (parameter samples) and split it to parts of size n * frameSize.
     * @param samples is the 1D byte array with the samples.
     * @param n is the size of the 1 song part.
     * @param frameSize is the size of 1 frame (= numberOfChannels * sampleSize).
     * @return Returns the 2D array where 1 byte array represents the part of size n * frameSize.
     */
    public static byte[][] splitSongToPartsOfSizeNFrames(byte[] samples, int n, int frameSize) {
        byte[][] result = AudioProcessor.getEveryXthTimePeriodWithLength(samples, n, 1, frameSize, 0);
        return result;
    }


    /**
     * Takes the input stream (parameter samples) and split it to parts of size n * frameSize.
     * @param samples is the input stream with the samples.
     * @param n is the size of the 1 song part.
     * @param frameSize is the size of 1 frame (= numberOfChannels * sampleSize).
     * @return Returns the 2D array where 1 byte array represents the part of size n * frameSize.
     */
    public static byte[][] splitSongToPartsOfSizeNFrames(InputStream samples, int n, int frameSize) throws IOException {
        byte[][] result = AudioProcessor.getEveryXthTimePeriodWithLength(samples, n, 1, frameSize, 0);

        return result;
    }





    // TODO: verze se signed/unsigned
    // TODO: Taky zbytecne 2 vetve - ale zase kvuli optimalizaci pro ted necham, nevim jestli se to dobre prelozi ... TODO: Zkontrolovat
    /**
     * Converts byte array to int samples of size sampleSize.
     * @param byteSamples are the samples in 1D byte array.
     * @param sampleSize is the size of one sample in bytes.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned tells if the converted samples are signed or unsigned
     * @return Returns the samples as 1D array of ints.
     * @throws IOException is thrown when the sample size is invalid.
     */
    public static int[] convertBytesToSamples(byte[] byteSamples, int sampleSize, boolean isBigEndian, boolean isSigned) throws IOException {
        int[] result = new int[byteSamples.length / sampleSize];

        int arrIndex;
        int mask = AudioUtilities.calculateMask(sampleSize);
        if(isBigEndian) {
            arrIndex = 0;
            for(int i = 0; i < result.length; i++) {
                result[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, arrIndex, isSigned);
                arrIndex = arrIndex + sampleSize;
            }
        } else {
            arrIndex = 0;
            for(int i = 0; i < result.length; i++) {
                result[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, arrIndex, isSigned);
                arrIndex = arrIndex + sampleSize;
            }
        }

        return result;
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


    /**
     * Converts byte array to int samples, which are then normalized to double number between -1 and 1 which are returned.
     * @param byteSamples are the samples in 1D byte array.
     * @param sampleSize is the size of one sample.
     * @param sampleSizeInBits is the size of one sample in ibts.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned is true if the samples are signed, is false otherwise.
     * @return Returns normalized samples in form of double[]
     * @throws IOException is thrown when the sample size is invalid.
     */
    public static double[] normalizeToDoubles(byte[] byteSamples, int sampleSize, int sampleSizeInBits,
                                              boolean isBigEndian, boolean isSigned) throws IOException {
        double[] result = new double[byteSamples.length / sampleSize];
        int maxAbsoluteValue = AudioUtilities.getMaxAbsoluteValueSigned(sampleSizeInBits);
        int arrIndex = 0;
        int mask = AudioUtilities.calculateMask(sampleSize);

        if(isSigned) {
            if(isBigEndian) {
                for(int i = 0; i < result.length; i++) {
                    result[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, arrIndex, true);
                    result[i] = result[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            } else {
                for(int i = 0; i < result.length; i++) {
                    result[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, arrIndex, true);
                    result[i] = result[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            }
        } else {
            int convertUnsignedToSigned = maxAbsoluteValue;
            if(isBigEndian) {
                for(int i = 0; i < result.length; i++) {
                    result[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, arrIndex, false);
                    result[i] = result[i] - convertUnsignedToSigned;
                    result[i] = result[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            } else {
                for(int i = 0; i < result.length; i++) {
                    result[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, arrIndex, false);
                    result[i] = result[i] - convertUnsignedToSigned;
                    result[i] = result[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            }
        }

        return result;
    }


    /**
     * Converts byte array to int samples, which are then normalized to double number between -1 and 1 and put in outputArr.
     * Ends when outputArr is full
     * @param byteSamples are the samples in 1D byte array.
     * @param outputArr is the array which will contain the normalized samples
     * @param sampleSize is the size of one sample.
     * @param sampleSizeInBits is the size of one sample in bits.
     * @param arrIndex is index in byteSamples array where is the first sample to be normalized.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned is true if the samples are signed, is false otherwise.
     * @return Returns the current index in byteSamples after performing normalization
     * @throws IOException is thrown when the sample size is invalid.
     */
    public static int normalizeToDoubles(byte[] byteSamples, double[] outputArr, int sampleSize,
                                         int sampleSizeInBits, int arrIndex,
                                         boolean isBigEndian, boolean isSigned) throws IOException {
        return normalizeToDoubles(byteSamples, outputArr, sampleSize, sampleSizeInBits,
                                  arrIndex, 0, outputArr.length, isBigEndian, isSigned);
    }


    /**
     * Converts byte array to int samples, which are then normalized to double number between -1 and 1 and put in outputArr.
     * Ends when outputArr is full
     * @param byteSamples are the samples in 1D byte array.
     * @param outputArr is the array which will contain the normalized samples
     * @param sampleSize is the size of one sample.
     * @param sampleSizeInBits is the size of one sample in bits.
     * @param arrIndex is index in byteSamples array where is the first sample to be normalized.
     * @param outputStartIndex is the index to which we should start give output values
     * @param outputLen is the length of the output - how many samples should be taken
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned is true if the samples are signed, is false otherwise.
     * @return Returns the current index in byteSamples after performing normalization
     * @throws IOException is thrown when the sample size is invalid.
     */
    public static int normalizeToDoubles(byte[] byteSamples, double[] outputArr, int sampleSize,
                                         int sampleSizeInBits, int arrIndex, int outputStartIndex, int outputLen,
                                         boolean isBigEndian, boolean isSigned) throws IOException {
        int maxAbsoluteValue = AudioUtilities.getMaxAbsoluteValueSigned(sampleSizeInBits);
        int mask = AudioUtilities.calculateMask(sampleSize);
        int outputEndIndex = outputStartIndex + outputLen;

        if(isSigned) {
            if(isBigEndian) {
                for(int i = outputStartIndex; i < outputEndIndex; i++) {
                    outputArr[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, arrIndex, true);
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            } else {
                for(int i = outputStartIndex; i < outputEndIndex; i++) {
                    outputArr[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, arrIndex, true);
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            }
        } else {
            int convertUnsignedToSigned = maxAbsoluteValue;
            if(isBigEndian) {
                for(int i = outputStartIndex; i < outputEndIndex; i++) {
                    outputArr[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, arrIndex, false);
                    outputArr[i] = outputArr[i] - convertUnsignedToSigned;
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            } else {
                for(int i = outputStartIndex; i < outputEndIndex; i++) {
                    outputArr[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, arrIndex, false);
                    outputArr[i] = outputArr[i] - convertUnsignedToSigned;
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            }
        }

        return arrIndex;
    }


    /**
     * Converts byte array to int samples, which are then normalized to double number between -1 and 1 and put in outputArr.
     * Ends when outputArr is full
     * @param byteSamples are the samples in 1D byte array.
     * @param sampleSize is the size of one sample.
     * @param sampleSizeInBits is the size of one sample in bits.
     * @param arrIndex is index in byteSamples array where is the first sample to be normalized.
     * @param windowSizeInSamples is the size of the double array.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned is true if the samples are signed, is false otherwise.
     * @return Returns normalized samples in form of double[]
     * @throws IOException is thrown when the sample size is invalid.
     */
    public static double[] normalizeToDoubles(byte[] byteSamples, int sampleSize,
                                              int sampleSizeInBits, int arrIndex, int windowSizeInSamples,
                                              boolean isBigEndian, boolean isSigned) throws IOException {
        double[] outputArr = new double[windowSizeInSamples];
        normalizeToDoubles(byteSamples, outputArr, sampleSize, sampleSizeInBits, arrIndex, isBigEndian, isSigned);

        // TODO: Zakomentovano protoze to bylo nahrazeno volanim referencni varianty
/*
        int maxAbsoluteValue = getMaxAbsoluteValueSigned(sampleSizeInBits);
        int mask = calculateMask(sampleSize);

        if(isSigned) {
            if(isBigEndian) {
                for(int i = 0; i < outputArr.length; i++) {
                    outputArr[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, arrIndex, true);
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            } else {
                for(int i = 0; i < outputArr.length; i++) {
                    outputArr[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, arrIndex, true);
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            }
        } else {
            int convertUnsignedToSigned = maxAbsoluteValue;
            if(isBigEndian) {
                for(int i = 0; i < outputArr.length; i++) {
                    outputArr[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, arrIndex, false);
                    outputArr[i] = outputArr[i] - convertUnsignedToSigned;
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            } else {
                for(int i = 0; i < outputArr.length; i++) {
                    outputArr[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, arrIndex, false);
                    outputArr[i] = outputArr[i] - convertUnsignedToSigned;
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            }
        }
*/

        return outputArr;
    }


    public static double normalizeToDoubleBetweenMinusOneAndOne(int sample, int maxAbsoluteValue, boolean isSigned) {
        double result;

        if (isSigned) {
            result = sample / (double) maxAbsoluteValue;
        } else {
            int convertUnsignedToSigned = maxAbsoluteValue;
            result = sample - convertUnsignedToSigned;
            result = result / (double) maxAbsoluteValue;
        }

        return result;
    }

     /**
     Takes int[] which represents samples converts them to double[] which are normalized samples (values between -1 and 1).
     * @param samples is 1D int array with samples.
     * @param sampleSizeInBits is the size of 1 sample in bits in the samples array.
     * @param isSigned is true if the samples are signed, is false otherwise.
     * @return Returns normalized samples in form of double[]
     */
    public static double[] normalizeToDoubles(int[] samples, int sampleSizeInBits, boolean isSigned) {
        double[] result = new double[samples.length];
//        System.out.println("sample size in bits:\t" + sampleSizeInBits);          // TODO: remove debug prints
        int maxAbsoluteValue = AudioUtilities.getMaxAbsoluteValueSigned(sampleSizeInBits);
//        System.out.println("Max absolute value:\t" + maxAbsoluteValue);
        if(isSigned) {
            for (int i = 0; i < result.length; i++) {
                result[i] = samples[i] / (double)maxAbsoluteValue;
//                System.out.println("Original sample:\t" + samples[i] + "\tnormalized sample:\t" + result[i]);
            }
        } else {
            int convertUnsignedToSigned = maxAbsoluteValue;
            for (int i = 0; i < result.length; i++) {
                result[i] = samples[i] - convertUnsignedToSigned;
                result[i] = result[i] / (double)maxAbsoluteValue;
            }
        }

        return result;
    }



    // TODO: i think that converting to int before dividing may help for receiving better results
    /**
     * Takes double[] which represents samples (or for example average value) - First converts them to int (since the double values are in these case expected to be ints). Performs normalization on these samples and returns them.
     * This class exists for optimalization (saving copying and creating array).
     * @param samples is 1D int array with samples.
     * @param sampleSizeInBits is the size of 1 sample in bits in the samples array.
     * @param isSigned is true if the samples are signed, is false otherwise.
     */
    public static void normalizeToDoubles(double[] samples, int sampleSizeInBits, boolean isSigned) {
//        System.out.println("sample size in bits:\t" + sampleSizeInBits);          // TODO: remove debug prints
        int maxAbsoluteValue = AudioUtilities.getMaxAbsoluteValueSigned(sampleSizeInBits);
//        System.out.println("Max absolute value:\t" + maxAbsoluteValue);
        if(isSigned) {
            for (int i = 0; i < samples.length; i++) {
                samples[i] = (int)samples[i];                               // TODO: maybe remove
                samples[i] = samples[i] / maxAbsoluteValue;
//                System.out.println("Original sample:\t" + samples[i] + "\tnormalized sample:\t" + result[i]);
            }
        } else {
            int convertUnsignedToSigned = maxAbsoluteValue;
            for (int i = 0; i < samples.length; i++) {
                samples[i] = (int)samples[i];
                samples[i] = samples[i] - convertUnsignedToSigned;
                samples[i] = samples[i] / maxAbsoluteValue;
            }
        }
    }


    /**
     * Converts sample in byte array of size sampleSize to int. With given endianity.
     * @param bytes is byte array with sample(s).
     * @param mask is mask used to mask certain bytes, so that the value in int is same as the value in the sample.
     * @param isBigEndian says if the given array is in big endian.
     * @param isSigned says if the given array has signed samples.
     * @return Returns the sample starting at index arrIndex.
     */
    public static int convertBytesToInt(byte[] bytes, int mask, boolean isBigEndian, boolean isSigned) {
        return convertBytesToInt(bytes, bytes.length, mask, 0, isBigEndian, isSigned);
    }
        // TODO: tahle metoda je nove pridana
    // TODO: Ted jsem tam dopsal isSigned, ale to by melo byt ... i v tom prevodu mona by to melo byt
    /**
     * Converts sample starting at index arrIndex in byte array bytes of size sampleSize to int. With given endianity.
     * @param bytes is byte array with sample(s).
     * @param sampleSize is the size of 1 sample.
     * @param mask is mask used to mask certain bytes, so that the value in int is same as the value in the sample.
     * @param arrIndex is index in the array, where starts the sample to be converted
     * @param isBigEndian says if the given array is in big endian.
     * @param isSigned says if the given array has signed samples.
     * @return Returns the sample starting at index arrIndex.
     */
    public static int convertBytesToInt(byte[] bytes, int sampleSize, int mask, int arrIndex, boolean isBigEndian, boolean isSigned) {
        if(isBigEndian) {
            return convertBytesToIntBigEndian(bytes, sampleSize, mask, arrIndex, isSigned);
        }
        else {
            return convertBytesToIntLittleEndian(bytes, sampleSize, mask, arrIndex, isSigned);
        }
    }

    // TODO: maybe it is better performace wise to write it all explicitly
    // TODO: in switch for each sample size (1..4) then having it in general if
    /**
     * The sample is expected to be in big endian audioFormat.
     * Converts sample starting at index arrIndex in byte array bytes of size sampleSize to int.
     * @param bytes is byte array with sample(s).
     * @param sampleSize is the size of 1 sample.
     * @param mask is mask used to mask certain bytes, so that the value in int is same as the value in the sample.
     * @param arrIndex is index in the array, where starts the sample to be converted
     * @param isSigned tells if the converted sample is signed or unsigned
     * @return Returns the sample starting at index arrIndex.
     */
    public static int convertBytesToIntBigEndian(byte[] bytes, int sampleSize, int mask, int arrIndex, boolean isSigned) {
        int result = 0;
        arrIndex = arrIndex + sampleSize - 1;
        for (int i = 0; i < sampleSize; i++) {
            result = result | (((int) bytes[arrIndex] & 0x00_00_00_FF) << (i * 8));
            arrIndex--;
        }

        // TODO: old variant with if
//        if(isSigned) {
//            if (((result >> (((sampleSize - 1) * 8) + 7)) & 1) == 1) {  //If true, then copy sign bit
//                result = result | mask;
//            }
//        }

        // TODO: New variant without if
        if (isSigned) {
            int sign = (result >> (((sampleSize - 1) * 8) + 7)) & 1;  //If == 1 then there is sign bit, if == 0 then no sign bit
            mask *= sign;       // mask will be 0 if the number >= 0 (no sign bit); mask == mask if sign == 1
            result = result | mask;
        }

        return result;
    }



    // TODO: maybe it is better performace wise to write it all explicitly
    // TODO: in switch for each sample size (1..4) then having it in general if
    /**
     * The sample is expected to be in little endian audioFormat.
     * Converts sample starting at index arrIndex in byte array bytes of size sampleSize to int.
     * @param bytes is byte array with sample(s).
     * @param sampleSize is the size of 1 sample.
     * @param mask is mask used to mask certain bytes, so that the value in int is same as the value in the sample.
     * @param arrIndex is index in the array, where starts the sample to be converted
     * @param isSigned tells if the converted sample is signed or unsigned
     * @return Returns the sample starting at index arrIndex.
     */
    public static int convertBytesToIntLittleEndian(byte[] bytes, int sampleSize, int mask, int arrIndex, boolean isSigned) {
        int result = 0;
        for (int i = 0; i < sampleSize; i++) {
            result = result | (((int) bytes[arrIndex] & 0x00_00_00_FF) << (i * 8));
            arrIndex++;
        }
// TODO: old variant with if
//        if (isSigned) {
//            if (((result >> (((sampleSize - 1) * 8) + 7)) & 1) == 1) {  //If true, then copy sign bit
//                result = result | mask;
//            }
//        }

        // TODO: New variant without if
        if (isSigned) {
            int sign = (result >> (((sampleSize - 1) * 8) + 7)) & 1;  //If == 1 then there is sign bit, if == 0 then no sign bit
            mask *= sign;       // mask will be 0 if the number >= 0 (no sign bit); mask == mask if sign == 1
            result = result | mask;
        }

        return result;
    }


    /**
     * Expects the double to be between -1 and 1
     * @param sampleDouble
     * @param maxAbsoluteValue
     * @param isSigned
     * @return
     */
    public static int convertDoubleToInt(double sampleDouble, int maxAbsoluteValue, boolean isSigned) {
        int sampleInt = (int)(sampleDouble * maxAbsoluteValue); // TODO: Maybe Math.ceil or something more advanced will have better result
        if(!isSigned) {
            sampleInt += maxAbsoluteValue;
        }

        return sampleInt;
    }

    public static byte[] convertDoubleToByteArr(double sampleDouble, int sampleSize, int maxAbsoluteValue,
                                                boolean isBigEndian, boolean isSigned) {
        byte[] resultArr = new byte[sampleSize];
        convertDoubleToByteArr(sampleDouble, sampleSize, maxAbsoluteValue, isBigEndian,  isSigned,0, resultArr);
        return resultArr;
    }

    public static void convertDoubleToByteArr(double sampleDouble, int sampleSize, int maxAbsoluteValue,
                                              boolean isBigEndian, boolean isSigned, int startIndex, byte[] resultArr) {
        int sampleInt = convertDoubleToInt(sampleDouble, maxAbsoluteValue, isSigned);
        convertIntToByteArr(resultArr, sampleInt, sampleSize, startIndex, isBigEndian);
    }

///////////

    public static int[] convertDoubleArrToIntArr(double[] doubleArr, int maxAbsoluteValue, boolean isSigned) {
        int[] intArr = new int[doubleArr.length];

        for(int i = 0; i < doubleArr.length; i++) {
            intArr[i] = convertDoubleToInt(doubleArr[i], maxAbsoluteValue, isSigned);
        }

        return intArr;
    }

    // TODO: Not sure about effectivity, maybe I could I just all the more advanced variant.
    public static void convertDoubleArrToIntArr(double[] doubleArr, int[] intArr, int maxAbsoluteValue, boolean isSigned) {
        for(int i = 0; i < doubleArr.length; i++) {
            intArr[i] = convertDoubleToInt(doubleArr[i], maxAbsoluteValue, isSigned);
        }
    }


    public static void convertDoubleArrToIntArr(double[] doubleArr, int[] intArr, int doubleStartInd, int intStartInd,
                                                int len, int maxAbsoluteValue, boolean isSigned) {
        for(int i = 0; i < len; i++, doubleStartInd++, intStartInd++) {
            intArr[intStartInd] = convertDoubleToInt(doubleArr[doubleStartInd], maxAbsoluteValue, isSigned);
        }
    }


    public static void convertDoubleArrToByteArr(double[] doubleArr, byte[] byteArr, int doubleStartInd,
                                                 int byteStartInd, int len, int sampleSize,
                                                 int maxAbsoluteValue, boolean isBigEndian, boolean isSigned) {
        for(int i = 0; i < len; i++, doubleStartInd++, byteStartInd += sampleSize) {
            convertDoubleToByteArr(doubleArr[doubleStartInd], sampleSize, maxAbsoluteValue, isBigEndian, isSigned, byteStartInd, byteArr);
        }
    }

    public static byte[] convertDoubleArrToByteArr(double[] doubleArr, int doubleStartInd, int len, int sampleSize,
                                                 int maxAbsoluteValue, boolean isBigEndian, boolean isSigned) {
        byte[] arr = new byte[len * sampleSize];
        convertDoubleArrToByteArr(doubleArr, arr, doubleStartInd, 0, len, sampleSize,
        maxAbsoluteValue, isBigEndian, isSigned);
        return arr;
    }


    public void convertSampleRate(int newSampleRate) throws IOException {
        this.song = convertSampleRate(this.song, this.sampleSizeInBytes, this.frameSize,
                this.numberOfChannels, this.sampleRate, newSampleRate,
                this.isBigEndian, this.isSigned, false);
        this.sampleRate = newSampleRate;
    }

    /**
     * Input array isn't changed.
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate"
     * @param samples          is input array with samples
     * @param sampleSize       is the size of one sample in bytes
     * @param frameSize        is the size of one frame in bytes
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @param isBigEndian      is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned         is boolean variable, which is true if the samples are signed, false if unsigned.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalit (<0 or >4)
     */
    public static byte[] convertSampleRate(byte[] samples, int sampleSize, int frameSize,
                                           int numberOfChannels, int oldSampleRate, int newSampleRate,
                                           boolean isBigEndian, boolean isSigned,
                                           boolean canChangeInputArr) throws IOException {
        byte[] retArr = null;
        if (oldSampleRate > newSampleRate) {
//            retArr = convertToLowerSampleRate(samples, sampleSize, numberOfChannels, oldSampleRate, newSampleRate, isBigEndian, isSigned);
            retArr = convertToLowerSampleRateByUpSampling(samples, sampleSize, frameSize, numberOfChannels,
                    oldSampleRate, newSampleRate, isBigEndian, isSigned, canChangeInputArr);
        }
        else if (oldSampleRate < newSampleRate) {
            retArr = convertToHigherSampleRate(samples, sampleSize, numberOfChannels, oldSampleRate, newSampleRate, isBigEndian, isSigned);
        }
        else {
            retArr = samples;        // The sampling rates are the same
        }
        return retArr;
    }


    /**
     * Input array isn't changed.
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate", where oldSampleRate < newSampleRate
     * @param samples          is input array with samples
     * @param sampleSize       is the size of one sample in bytes
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @param isBigEndian      is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned         is boolean variable, which is true if the samples are signed, false if unsigned.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalid (<0 or >4)
     */
    private static byte[] convertToHigherSampleRate(byte[] samples, int sampleSize, int numberOfChannels, int oldSampleRate,
                                                    int newSampleRate, boolean isBigEndian, boolean isSigned) throws IOException {
        return convertSampleRateImmediateVersion(samples, sampleSize, numberOfChannels, oldSampleRate, newSampleRate, isBigEndian, isSigned);
    }


    public static double[] convertSampleRate(double[] samples, int numberOfChannels, int oldSampleRate,
                                             int newSampleRate, boolean canChangeInputArr) throws IOException {
        double[] retArr;
        if (oldSampleRate > newSampleRate) {
            //retArr = convertToLowerSampleRateByUpSampling(samples, numberOfChannels, oldSampleRate, newSampleRate, canChangeInputArr);
            retArr = convertToLowerSampleRateByImmediate(samples, numberOfChannels, oldSampleRate, newSampleRate, canChangeInputArr);
        }
        else if (oldSampleRate < newSampleRate) {
            retArr = convertToHigherSampleRate(samples, numberOfChannels, oldSampleRate, newSampleRate);
        }
        else {
            retArr = samples;        // The sampling rates are the same
        }
        return retArr;
    }

    /**
     * Input array isn't changed.
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate", where oldSampleRate < newSampleRate
     * @param samples          is input array with samples
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalid (<0 or >4)
     */
    private static double[] convertToHigherSampleRate(double[] samples, int numberOfChannels, int oldSampleRate,
                                                    int newSampleRate) throws IOException {
        return convertSampleRateImmediateVersion(samples, numberOfChannels, oldSampleRate, newSampleRate);
    }


    /**
     * Expects the samples to be filtered if converting to lower sample rate
     * @param samples
     * @param sampleSize
     * @param numberOfChannels
     * @param oldSampleRate
     * @param newSampleRate
     * @param isBigEndian
     * @param isSigned
     * @return
     * @throws IOException
     */
    private static byte[] convertSampleRateImmediateVersion(byte[] samples, int sampleSize, int numberOfChannels,
                                                            int oldSampleRate, int newSampleRate,
                                                            boolean isBigEndian, boolean isSigned) throws IOException {
        int frameSize = numberOfChannels * sampleSize;
        if (samples == null || samples.length <= frameSize) {
            return samples;
        }
        double ratio = ((double) oldSampleRate) / newSampleRate;
        ArrayList<Byte> retList = new ArrayList<>();
        int mask = AudioUtilities.calculateMask(sampleSize);
/*
		int secs = samples.length / oldSampleRate;
		if(samples.length % oldSampleRate != 0) {	// If the last chunk of data doesn't represent whole second
			secs++;									// We just add 1 more second to the new data
		}

		byte[] retArr = new byte[(secs) * newSampleRate];
		for(int i = 0; i < samples.length; i++) {
			for(int j = 0; j < )
		}
*/

        double currRatio = 0;
        int[][] currentSamples = new int[numberOfChannels][2];  // for each channel we will have left and right sample
        int bytesNeededToInitArr = currentSamples.length * currentSamples[0].length * sampleSize;
        int index = 0;
//        for (int j = 0; j < 2; j++) {
//            for (int i = 0; i < numberOfChannels; i++) {
//                currentSamples[i][j] = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
//                index += sampleSize;
//            }
//        }
        index = setLeftAndRightSamples(currentSamples, samples, sampleSize, numberOfChannels, mask, index, isBigEndian, isSigned);
        int val = 0;
        byte[] valByte = new byte[sampleSize];
        while(index < samples.length || currRatio+ratio <= 1) {      // The second part of or is for case when we are working with the last samples
            for(int j = 0; j < currentSamples.length; j++) {
                val = (int) (currentSamples[j][0] * (1 - currRatio) + currentSamples[j][1] * currRatio);
                convertIntToByteArr(valByte, val, isBigEndian);
// TODO:                System.out.println("val: " + val);
// TODO:                System.out.println("Index:\t" + index + ":" + j + ":" + currentSamples[j][0] + ":" +
// TODO:                    currentSamples[j][1] + ":" + (val == currentSamples[j][0]) + ":" + currRatio);
                for(int ind = 0; ind < valByte.length; ind++) {
                    retList.add(valByte[ind]);					// TODO: tohle uz chci delat pro ty intovy (pripadne doublovy) hodnoty, rozhodne to nechci delat pro byte hodnoty
                }
            }

// TODO:            System.out.println(TODO++ + ":\t" + index + "\t:\t" + currRatio);
            currRatio += ratio;
//            System.out.println("A" + ":\t" + ratio + ":" + currRatio);
            if(currRatio > 1) {
                if(ratio <= 1) {         // Should be optimized by compiler ... perform the if branching only once
                    for (int j = 0; j < currentSamples.length; j++) {
                        currentSamples[j][0] = currentSamples[j][1];
                        currentSamples[j][1] = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
                        index += sampleSize;
                    }
                }
                else {
                    if(currRatio >= 3) {
                        index += ((int)currRatio - 2) * frameSize;
                    }

                    if(index > samples.length - bytesNeededToInitArr) {           // We skipped too much // TODO: not sure if in this case I should add the last right samples
                        break;
                    }
                    index = setLeftAndRightSamples(currentSamples, samples, sampleSize, numberOfChannels, mask, index, isBigEndian, isSigned);
                    // TODO: tohle je v te metode
//                    for (int j = 0; j < currentSamples.length; j++) {
//                        currentSamples[j][0] = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
//                        index += sampleSize;
//                        currentSamples[j][1] = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
//                        index += sampleSize;
//                    }

                }

                currRatio %= 1;
            }
        }									// TODO: nemel by byt problem, staci jen udelat metodu co prevadi byty na normalni hodnoty

        for(int i = 0; i < currentSamples.length; i++) {        // Not sure if I always want to add the last frame, but it is just one last frame so it doesn't matter that much
            convertIntToByteArr(valByte, currentSamples[i][1], isBigEndian);        // currentSamples[i][0] if we want to pass the tests
            for(int ind = 0; ind < valByte.length; ind++) {
                retList.add(valByte[ind]);					// TODO: tohle uz chci delat pro ty intovy (pripadne doublovy) hodnoty, rozhodne to nechci delat pro byte hodnoty
            }
        }


        byte[] retArr = new byte[retList.size()];
        int i = 0;
        for(byte b : retList) {			// TODO: nevim jestli funguje
            retArr[i] = b;
            i++;
        }
        return retArr;
    }

    /**
     * Fills currentSamples array where for each channel we will fill 2 successive samples from the input samples array
     * starting at index index.
     * @param currentSamples   is double int array to be filled
     * @param samples          is input array with samples
     * @param sampleSize       is the size of one sample in bytes
     * @param numberOfChannels is number of channels in samples array.
     * @param mask             is the mask from calculateMask method
     * @param index            is the current index in samples array
     * @param isBigEndian      is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned         is boolean variable, which is true if the samples are signed, false if unsigned.
     * @return Returns the index of the sample behind the last touched sample (so the returned index = index + sampleSize * 2 * numberOfChannels).
     */
    private static int setLeftAndRightSamples(int[][] currentSamples, byte[] samples, int sampleSize, int numberOfChannels,
                                               int mask, int index, boolean isBigEndian, boolean isSigned) {
        // j == 0 means set the left value, j == 1 set the right value ... we first set all the left then all the right
        // - it makes since since this is how the audio data are stored in the array ...
        // in frames (samples 1 for all channels then samples 2 for all channels)
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < numberOfChannels; i++) {
                currentSamples[i][j] = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
                index += sampleSize;
            }
        }

        return index;
    }


    /**
     * Expects the samples to be filtered if converting to lower sample rate
     * @param samples
     * @param numberOfChannels
     * @param oldSampleRate
     * @param newSampleRate
     *
     * @return
     * @throws IOException
     */
    private static double[] convertSampleRateImmediateVersion(double[] samples, int numberOfChannels, int oldSampleRate,
                                                              int newSampleRate) {
        double ratio = ((double)newSampleRate) / oldSampleRate;
        int newLen = Utilities.convertToMultipleUp((int)(samples.length * ratio), numberOfChannels);
        double[] convertedArr = new double[newLen];

        int i = 0;
        double currRatio = 0;
        int convertedArrIndex = 0;
        double indexJump = 1 / ratio * numberOfChannels;
        while(i < samples.length - 1) {
            for(int ch = 0; ch < numberOfChannels; ch++, convertedArrIndex++) {
                convertedArr[convertedArrIndex] = (samples[i] * (1 - currRatio) + samples[i + 1] * currRatio);
            }
//            ProgramTest.debugPrint("CONV", convertedArr[convertedArrIndex], convertedArrIndex, i,
//                    currRatio, samples[i], samples[i + 1]);
            currRatio += indexJump;
            if(currRatio >= 1) {
                i += (int) currRatio;
                currRatio %= 1;
            }
        }

        return convertedArr;
    }



    /**
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate", where oldSampleRate > newSampleRate.
     * <br>
     * The conversion isn't immediate, we convert to some factor "n" of the newSampleRate which is bigger than the oldSampleRate.
     * And the we take every "n"-th sample of the upsampled array.
     * @param samples          is input array with samples
     * @param sampleSize       is the size of one sample in bytes
     * @param frameSize        is the size of one frame in bytes
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @param isBigEndian      is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned         is boolean variable, which is true if the samples are signed, false if unsigned.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalid (<0 or >4)
     */
    @Deprecated
    private static byte[] convertToLowerSampleRateByUpSampling(byte[] samples, int sampleSize, int frameSize,
                                                               int numberOfChannels, int oldSampleRate, int newSampleRate,
                                                               boolean isBigEndian, boolean isSigned,
                                                               boolean canChangeInputArr) throws IOException {
        int upSampleRate = newSampleRate;
        int upSampleRateRatio = 1;
        while(upSampleRate < oldSampleRate) {
            upSampleRateRatio++;
            upSampleRate += newSampleRate;
        }
        int skipSize = (upSampleRateRatio - 1) * frameSize;       // Skip all the frames to downsample
// If I want the tests to return true        samples = runLowPassFilter(samples, newSampleRate / 2, 64, oldSampleRate);			// Low pass filter for the nyquist frequency of the new frequency
        byte[] upSampledArr = null;
        if(oldSampleRate % newSampleRate == 0) {
            if(canChangeInputArr) {
                upSampledArr = new byte[samples.length];
                System.arraycopy(samples, 0, upSampledArr, 0, upSampledArr.length);
            }
            else {
                upSampledArr = samples;
            }
        }
        else {
            upSampledArr = convertToHigherSampleRate(samples, sampleSize, numberOfChannels, oldSampleRate, upSampleRate, isBigEndian, isSigned);
        }                                                                        // TODO: nemelo by tu byt upSampleRate

        // Low pass filter for the nyquist frequency of the new frequency
        upSampledArr = NonRecursiveFilter.runLowPassFilter(upSampledArr, newSampleRate / 2,
                64, oldSampleRate, numberOfChannels, sampleSize, frameSize, isBigEndian, isSigned);
        int len = frameSize;        // Get frame count
        //int frameCount = upSampledArr.length / (upSampleRateRatio * frameSize);
        int frameCount = upSampledArr.length / frameSize;
        if(frameCount % upSampleRateRatio == 0) {
            len = 0;
        }
        len += (frameCount / upSampleRateRatio) * frameSize;
        byte[] retArr = new byte[len];           // TODO: Ted nevim jestli tu nema byt jen upSampleRateRatio

        for(int retInd = 0, upSampleInd = 0; retInd < retArr.length; upSampleInd += skipSize) {
            for (int fs = 0; fs < frameSize; fs++, retInd++, upSampleInd++) {
                retArr[retInd] = upSampledArr[upSampleInd];
            }
        }

        return retArr;
    }


    /**
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate", where oldSampleRate > newSampleRate.
     * <br>
     * The conversion isn't immediate, we convert to some factor "n" of the newSampleRate which is bigger than the oldSampleRate.
     * And the we take every "n"-th sample of the upsampled array.
     * @param samples          is input array with samples
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalid (<0 or >4)
     */
    private static double[] convertToLowerSampleRateByUpSampling(double[] samples, int numberOfChannels,
                                                                 int oldSampleRate, int newSampleRate,
                                                                 boolean canChangeInputArr) throws IOException {
        // First find the first multiple bigger than the old sample rate
        int upSampleRate = newSampleRate;
        int upSampleRateRatio = 1;
        while(upSampleRate < oldSampleRate) {
            upSampleRateRatio++;
            upSampleRate += newSampleRate;
        }
        int skipSize = (upSampleRateRatio - 1);       // Skip all the frames to downsample
        double[] upSampledArr = null;
        if(oldSampleRate % newSampleRate == 0) {      // Then the upSampleRate = oldSampleRate
            if(canChangeInputArr) {
                upSampledArr = samples;
            }
            else {
                upSampledArr = new double[samples.length];
                System.arraycopy(samples, 0, upSampledArr, 0, upSampledArr.length);
            }
        }
        else {
            upSampledArr = convertToHigherSampleRate(samples, numberOfChannels, oldSampleRate, upSampleRate);
        }
        // Low pass filter for the nyquist frequency of the new frequency
        NonRecursiveFilter.runLowPassFilter(upSampledArr, 0, numberOfChannels, oldSampleRate,
                newSampleRate / 2,64, upSampledArr, 0, upSampledArr.length);
        int convertArrLen;
        convertArrLen = (upSampledArr.length / upSampleRateRatio);
        double[] retArr = new double[convertArrLen];

        for(int retInd = 0, upSampleInd = 0; retInd < retArr.length; upSampleInd += skipSize) {
            for (int ch = 0; ch < numberOfChannels; ch++, retInd++, upSampleInd++) {
                retArr[retInd] = upSampledArr[upSampleInd];
            }
        }

        return retArr;
    }

    /**
     * Input array isn't changed.
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate", where oldSampleRate > newSampleRate
     * <br>
     * Performs the conversion immediately.
     * @param samples          is input array with samples
     * @param sampleSize       is the size of one sample in bytes
     * @param frameSize        is the size of one frame in bytes.
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @param isBigEndian      is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned         is boolean variable, which is true if the samples are signed, false if unsigned.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalit (<0 or >4)
     */
    @Deprecated
    private static byte[] convertToLowerSampleRate(byte[] samples, int sampleSize, int frameSize,
                                                   int numberOfChannels, int oldSampleRate, int newSampleRate,
                                                   boolean isBigEndian, boolean isSigned) throws IOException {
        byte[] filtered = NonRecursiveFilter.runLowPassFilter(samples, newSampleRate / 2, 64, oldSampleRate,
            numberOfChannels, sampleSize, frameSize, isBigEndian, isSigned);
        return convertSampleRateImmediateVersion(filtered, sampleSize, numberOfChannels, oldSampleRate, newSampleRate, isBigEndian, isSigned);
    }


    /**
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate", where oldSampleRate > newSampleRate
     * <br>
     * Performs the conversion immediately.
     * @param samples          is input array with samples
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalit (<0 or >4)
     */
    private static double[] convertToLowerSampleRateByImmediate(double[] samples, int numberOfChannels,
                                                                int oldSampleRate, int newSampleRate,
                                                                boolean canChangeInputArr) throws IOException {
        double[] filtered;
        if(canChangeInputArr) {
            filtered = samples;
        }
        else {
            filtered = new double[samples.length];
            System.arraycopy(samples, 0, filtered, 0, filtered.length);
        }
        // Low pass filter for the nyquist frequency of the new frequency
        NonRecursiveFilter.runLowPassFilter(samples, 0, numberOfChannels, oldSampleRate,
                                            newSampleRate / 2, 64, filtered, 0, filtered.length);
        return convertSampleRateImmediateVersion(filtered, numberOfChannels, oldSampleRate, newSampleRate);
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
//            outputArr[outputIndex] = Program.performOperation(samples[samplesIndex], changeValues[indexInChangeValues], op);
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
    public int calculateBPMSimple() {
        writeVariables();

        int windowsLen = 43;    // Because 22050 / 43 == 512 == 1 << 9 ... 44100 / 43 == 1024 etc.
        int windowSize = sampleRate / windowsLen;
        windowSize = Utilities.convertToMultipleDown(windowSize, this.frameSize);
        double[] windows = new double[windowsLen];                       // TODO: Taky bych mel mit jen jednou asi ... i kdyz tohle je vlastne sampleRate specific
        return calculateBPMSimple(this.song, windowSize, windows, this.numberOfChannels, this.sampleSizeInBytes, this.frameSize,
                                  this.sampleRate, this.mask, this.isBigEndian, this.isSigned, 4);
    }


// TODO: Podle me kdyz vezmu jen mono signal, tak ty energie mi budou vychazet stejne
// TODO: A bude to rychlejsi... hlavne u tech comb filtru - to je tak vypocetne narocny, ze se bere jen par sekund
// TODO: http://archive.gamedev.net/archive/reference/programming/features/beatdetection/page2.html
// TODO: Ten derivation filter se mi nejak nezda - proc to nasobi fs(samplovaci frekvenci) a bere ten nasledujici sample
// TODO: filtry vetsinou berou jen ty predchozi - ale tak asi proc ne - podle me podobnyho vysledku dosahnu
// TODO: Tim ze vezmu ten soucasny a ten predchozi a ty zprumeruju
// TODO: A proc u toho sterea bere z leveho kanalu realny hodnoty a z praveho imaginarni - co kdybych mel 5 kanalu
// TODO: On to dela, protoze vezme ten levej kanal jako realny koeficienty a pravej jako imaginarni a na to posle FFT.

// The subbands in the algorithm means that we take some frequency bandwidth (we take the frequency bins in that
// bandwidth and work with them as it is 1. We just have 2D array instead of 1D and treat bandwidths separately // TODO:

// TODO: V tom R22 to s znaci soucasny subband a ws znaci jeho sirku - kolik je v nem binu
// TODO: Ten barycenter - je prostě že vezmu průměr z těch subbandů vážený tím bpm
// TODO: Tj. vydeleny souctem energii a to co delim je suma kde spolu nasobim tu BPM a tu energii v tom BPM ...
// TODO: beru z tech BPM nejakou funcki g (asi aby to vyslo lip ... ale neni zmineno jaka je ta funkce g)

    // sampleRate / windowSize == windows.length
     public static int calculateBPMSimple(byte[] samples, int windowSize, double[] windows, int numberOfChannels,
                                          int sampleSize, int frameSize,
                                          int sampleRate, int mask, boolean isBigEndian, boolean isSigned,
                                          int windowsBetweenBeats) {
        // TODO: DEBUG
        double maxEnergy = Double.MIN_VALUE;
        double minCoef = Double.MAX_VALUE;
        double maxCoef = Double.MIN_VALUE;
        double maxVariance = Double.MIN_VALUE;
        // TODO: DEBUG

        final int maxAbsValSigned = AudioUtilities.getMaxAbsoluteValueSigned(8 * sampleSize);     // TODO: Signed and unsigned variant

        int beatCount = 0;
        int sampleIndex = 0;
        int i;
        int windowSizeInBytes = windowSize * frameSize;
        int nextSampleIndex = windowSizeInBytes;
        double energySum = 0;
        double energyAvg;
        for(i = 0; i < windows.length; i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
            if(nextSampleIndex < samples.length) {
                windows[i] = computeEnergy(samples, windowSize, numberOfChannels, sampleSize, sampleIndex, mask,
                    isBigEndian, isSigned, maxAbsValSigned);
                energySum += windows[i];
            }
        }



         double maxValueInEnergy = ((double)windowSize) * maxAbsValSigned * maxAbsValSigned;     // max energy
         double maxValueInVariance = 2 * maxValueInEnergy;           // the val - avg (since avg = -val then it is 2*)
         // TODO: It is way to strict (The max variance can be much lower), but I don't see how could I make it more accurate
         maxValueInVariance *= maxValueInVariance;                   // Finally the variance of 1 window (we don't divide by the windows.length since we calculated for just 1 window as I said)
         // Just took 10000 because it worked quite nicely, but not for every sample rate,
         // so we have to multiply it with some value based on that
         double varianceMultFactor = 10000 * Math.pow(3.75, 44100d / sampleRate - 1);

        int windowsFromLastBeat = windowsBetweenBeats;
        int oldestIndexInWindows = 0;
        double currEnergy;
        double variance;
        double coef;
        while(nextSampleIndex < samples.length) {
            energyAvg = energySum / windows.length;
            currEnergy = computeEnergy(samples, windowSize, numberOfChannels, sampleSize, sampleIndex, mask,
                    isBigEndian, isSigned, maxAbsValSigned);
            variance = computeVariance(energyAvg, windows);
            variance /= maxValueInVariance;

            variance *= varianceMultFactor;

            coef = -variance / maxValueInVariance + 1.4;        // TODO: pryc
            coef = -0.0025714 * variance + 1.5142857;
//            coef = -0.0025714 * maxValueInVariance * variance + 1.5142857;            // TODO: NE
//            coef = -2.5714 * variance + 1.5142857;                                      // TODO: NE
//            coef = -2.5714 * variance * 128 + 1.5142857;                              // TODO: NE - zmeni to - ale smerem nahoru, takze vlastne kdyz nad tim preymslim tak tohle naopak zvetsuje BPM a ne snizuje
                                                                                        // TODO: Musel bych jeste posunout tu konstantu smerem vys
//            coef = -2.5714 * variance * 1024 + 1.5142857;             // TODO: Poskoci o 10
//            coef = -variance + 1.4;               // Gives a bit bigger results then the results should be
            coef = -0.0025714 * variance + 1.5142857;
            coef = -0.0025714 * variance + 1.8;

//            energyAvg = energyAvg / (windowSize * (1 << (sampleSize * 8)));
            // TODO: DEBUG
//            System.out.println("!!!!!!!!!!!!!!!!");
//            System.out.println(maxValueInEnergy);
//            System.out.println(":" + coef + ":\t" + maxValueInEnergy + ":\t" + (variance / (maxValueInEnergy * maxValueInEnergy)));
//            System.out.println(currEnergy + ":\t" + coef * energyAvg + ":\t" + variance);
//            System.out.println("!!!!!!!!!!!!!!!!");
            // TODO: DEBUG
// TODO:
            if(currEnergy > coef * energyAvg) {
                if(windowsFromLastBeat >= windowsBetweenBeats) {
                    beatCount++;
                    windowsFromLastBeat = -1;
                }

                // TODO: DEBUG
//                ProgramTest.debugPrint("TODO: TEST", currEnergy, coef, energyAvg, coef * energyAvg);
                // TODO: DEBUG
            }

            // TODO: DEBUG
            minCoef = Math.min(coef, minCoef);
            maxCoef = Math.max(coef, maxCoef);
            maxEnergy = Math.max(energySum, maxEnergy);
            maxVariance = Math.max(variance, maxVariance);
// TODO: DEBUG

            // Again optimize the case when windows.length is power of 2
            if(windows.length % 2 == 0) {
                energySum = energySum - windows[oldestIndexInWindows % windows.length] + currEnergy;
                windows[oldestIndexInWindows % windows.length] = currEnergy;
            }
            else {
                if(oldestIndexInWindows >= windows.length) {
                    oldestIndexInWindows = 0;
                }
                energySum = energySum - windows[oldestIndexInWindows] + currEnergy;
                windows[oldestIndexInWindows] = currEnergy;
            }
            // TODO: DEBUG
//            ProgramTest.debugPrint("Window in simple BPM:", windows[oldestIndexInWindows]);          // TODO: DEBUG
            // TODO: DEBUG
            oldestIndexInWindows++;
            sampleIndex = nextSampleIndex;
            nextSampleIndex += windowSizeInBytes;
            windowsFromLastBeat++;
        }

         int bpm = BPMUtils.convertBeatsToBPM(beatCount, samples.length, sampleSize, numberOfChannels, sampleRate);

        // TODO: DEBUG
//         MyLogger.log("END OF BPM SIMPLE:\t" + minCoef + "\t" + maxCoef + "\t" + maxEnergy + "\t" + maxVariance, 0);
        ProgramTest.debugPrint("END OF BPM SIMPLE:", minCoef, maxCoef, maxEnergy, maxVariance);
         // TODO: DEBUG
         return bpm;
     }


    private static double computeEnergy(byte[] samples, int windowSize, int numberOfChannels, int sampleSize,
                                        int index, int mask, boolean isBigEndian, boolean isSigned,
                                        int maxAbsoluteValueSigned) {
        double energy = 0;

        for(int i = 0; i < windowSize; i++) {
            for(int j = 0; j < numberOfChannels; j++, index += sampleSize) {
                int val = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
                if(!isSigned) {     // Convert unsigned sample to signed
                    val -= maxAbsoluteValueSigned;
                }
                energy += val*(double)val;
            }
        }

        return energy;
     }

    // Currently is expected to run only on small labelReferenceArrs, so there is no need to parallelize this method.
     private static double computeVariance(double average, double[] values) {
        double variance = 0;
        double val;
        for(int i = 0; i < values.length; i++) {
            val = values[i] - average;
            variance += val*val;
        }

        return variance / values.length;
     }






    ////////////////////////////////////////////////////
    // BPM Algorithm 2
    ////////////////////////////////////////////////////
    // TODO: Mozna vymazat tyhle 2 metody a volat to primo - nebo aspon zmenit jmeno
    public int calculateBPMSimpleWithFreqBands(int subbandCount, SubbandSplitterIFace splitter,
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
             return calculateBPMSimpleWithFreqBands(this.song, this.sampleSizeInBytes, this.sampleRate,
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


    // TODO: Dont create new array in FFT only measures
    // TODO: Verze s tim ze se to bude delat po 2jicich ta FFT - s realnou i komplexni casti
    // TODO: THIS IS VERSION FOR MONO SIGNAL
    // TODO: double[][][] subbandEnergies in multiple channel case
    public static int calculateBPMSimpleWithFreqBands(byte[] samples, int sampleSize, int sampleRate,
                                                      int windowSize, boolean isBigEndian, boolean isSigned,
                                                      int mask, int maxAbsoluteValue, DoubleFFT_1D fft, SubbandSplitterIFace splitter,
                                                      double[][] subbandEnergies, // TODO: 1D are the past values, 2D are the subbands
                                                      double coef, int windowsBetweenBeats, double varianceLimit
    ) throws IOException { // TODO: Predpokladam ,ze subbandEnergies uz je alokovany pole o spravny velikosti
        // TODO: REMOVE
        final double oldCoef = coef;      // TODO: OLD COEF
        double todoMaxEnergy = -1;
        // TODO: REMOVE



        //                double coefBasedOnSampleRate = coef / 1.3;//Math.pow(1., 44100 / (double)sampleRate - 1);

        double divFactor = 1;
        if(sampleRate < 44100) {
            // TODO: REMOVE
//                    divFactor = 1 + 0.3 * ((44100 / (double) sampleRate) - 1);
//                    divFactor = 1.825;
            // TODO: REMOVE

            double log = Utilities.logarithm((44100 / (double) sampleRate) - 1, 2.36);
            divFactor = 1 + 0.3 * (log + 1);

// TODO: REMOVE
//                    double log = Program.logGeneral((44100 / (double) sampleRate) - 1, 1.5);
//                    divFactor = 2;
//                    divFactor = 1.5;
//                    divFactor = 1 + 0.49 * (log + 1);
// TODO: REMOVE
        }
        double coefBasedOnSampleRate = coef / divFactor;        // Has to be done because, the lower the sample rate, the lower needs to be the coefficient

/*
        int beatCount = 0;
        double fft;
        int windowSizeInBytes = sampleSize * windowSize;        // TODO: * frameSize
        for(i = 0; i < windows.length; i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
            if(nextSampleIndex < samples.length) {
                windows[i] = getEnergy(samples, windowSize, numberOfChannels, sampleSize, sampleIndex, mask,
                    isBigEndian, isSigned);
                avg += windows[i];
            }
        }

        for (int index = 0; index < samples.length; index += jumpInBytes) {

            // TODO: Tahle metoda vypocita jen cast FFT o dane velikosti (tedy vraci pole doublu)
            // TODO: V obecnem pripade tahle metoda bude bud taky vracet double[] s tim ze proste vezme hodnoty
            // TODO: kanalu (pres preskakovani tj numberOfChannels * sampleSize je dalsi index ktery mam vzit)
            // TODO: nebo proste tu metodu udelat tak aby vratila double[][] kde to bude double[numberOfChannels][windowSize]
            // TODO: takze tam musim dat index
//            double[] fft = calculateFFTOnlyMeasuresGetOnlyOnePart(samples, sampleSize, sampleSizeInBits, windowSize, isBigEndian, isSigned);
            // TODO: !!!!!!!!! Tak jeste jinak ... rovnou spocitam energie tech subbandu ... zase jen v ty jedny casti
            // TODO: !!!!!!!!! Pro vic kanalu zase musim pres double[][]
            double[] subbandEnergies = getSubbandEnergiesUsingFFT(...); // TODO: !!!! Zase to delat spis jen pres referenci

        }
        return beatCount;
 */


// TODO:
        int numberOfChannels = 1;
        int frameSize = sampleSize;
// TODO:
// TODO:        double varianceLimit = 0;     // TODO:
        int windowsFromLastBeat = windowsBetweenBeats;
        int subbandCount = subbandEnergies[0].length;
        int historySubbandsCount = subbandEnergies.length;
        double[] fftArr = new double[windowSize];

        // TODO: Zbytecny staci aby to pole melo polovicni velikost (viz kod pod tim)
//double[] measuresArr = new double[windowSize];        // TODO: Muzu pouzit fftArr jako measuresArr, ale takhle to je prehlednesji a navic pak chci ty vysledky fft ulozit do souboru abych to uz nemusel pocitat
        double[] measuresArr = new double[FFT.getBinCountRealForward(windowSize)];


        int beatCount = 0;
        int sampleIndex = 0;
        int i;
        int windowSizeInBytes = windowSize * sampleSize;     // TODO: frameSize v vice multichannel variante
        int nextSampleIndex = windowSizeInBytes;
        //TODO: Asi zase predat jako argument ... tady je to pole protoze kazdy subband ma vlastni average
        double[] energySums = new double[subbandCount];  // TODO: Pro vice kanalove to bude double[][]
        double[] currEnergies = new double[subbandCount];
        for(i = 0; i < subbandEnergies.length; // TODO: U multi-channel varianty to bude subbandEnergies[0].length
            i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
            if(nextSampleIndex < samples.length) {
                // TODO: Vymazat ten startIndex
                computeSubbandEnergiesUsingFFT(samples, subbandEnergies[i], sampleIndex,//int startIndex,
                    numberOfChannels, sampleSize, frameSize, mask, fft, fftArr, measuresArr,
                    maxAbsoluteValue, isBigEndian, isSigned, splitter);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy
// TODO:                subbandEnergies[i] = currEnergies;
                for(int j = 0; j < subbandEnergies[i].length; j++) {
                    energySums[j] += subbandEnergies[i][j];
                }
            }
        }

        double avg;


        int oldestIndexInSubbands = 0;
        while(nextSampleIndex < samples.length) {
            // TODO: BPM NOVY
            boolean hasBeat = false;
            // TODO: BPM NOVY
            // TODO: Ten startIndex pod timhle dat pryc
            computeSubbandEnergiesUsingFFT(samples, currEnergies, sampleIndex,//int startIndex,
                numberOfChannels, sampleSize, frameSize, mask, fft, fftArr, measuresArr,
                maxAbsoluteValue, isBigEndian, isSigned, splitter);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy
            //            currEnergies = getSubbandEnergiesUsingFFT(...);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy

            // This is version for Constant splitter The commented coef = 2.5 ... is for logaritmic, but the version with constant seems to work very good
            int j = 0;
            for(; j < currEnergies.length; j++) {
                todoMaxEnergy = Math.max(currEnergies[j], todoMaxEnergy);       // TODO: Finding the difference in coefs

                avg = energySums[j] / historySubbandsCount; // TODO:
                double variance = computeVariance(avg, subbandEnergies, j);
                // TODO: OLD - REMOVE
//                coef = 3;
//                    coef = 6;
                // TODO: OLD - REMOVE

         //       coef = 2.5 + 10000 * variance; For logarithmic with subbandCount == 32 and that version doesn't contain the if with varianceLimit
//                System.out.println(currEnergies[j] + ":\t" + avg + ":\t" + (coef * avg));

                // TODO: DEBUG
//                if(variance > 150) {
//                    ProgramTest.debugPrint("Variance >150:", variance);
//                }
//                if(energySums[j] > 50) {
//                    ProgramTest.debugPrint("energy >50:", energySums[j]);
//                }
//
//                ProgramTest.debugPrint("Variance:", variance);
//                ProgramTest.debugPrint("energy:", energySums[j]);
                // TODO: DEBUG


                // TODO: ENERGIE TED
//                variance *= 5000;
//                coef = oldCoef - variance * (0.0025714 / 2);

//                coef = oldCoef - variance;

                // Code from BPM Simple
//                variance *= 10000;
//                coef = -0.0025714 * variance + 1.8;
                // Code from BPM Simple

                // Modified Code from BPM Simple
//                variance *= 5000;
//                coef = -0.0025714 * variance + 3.6;
//                coef = 10;

//                coef = 3;
                // Modified Code from BPM Simple
                // TODO: ENERGIE TED

                // TODO: DEBUG
                // TODO: Tady beru ze kdyz je beat na libovolnym mistem - pak typicky budu chtit brat beaty jen z urcitych frekvencnich pasem
                if (currEnergies[j] > coefBasedOnSampleRate * avg) {
//                if (currEnergies[j] > coef / Math.max(1, (((44100 / (double)sampleRate) - 1)) * 1) * avg) {
                    // TODO: DEBUG
//                    System.out.println("---------------" + variance);
                    // TODO: DEBUG
                    // TODO: not used anymore - the variance just doesn't seem to work.
//                    double varianceLimit = 0.0000001;
//                    varianceLimit = 1;
//                    varianceLimit = 20;
//                    varianceLimit = 40;
//                    varianceLimit = 75;
//                    varianceLimit = 150;
//                    varianceLimit = 250;
//                    varianceLimit = 300;
                    // TODO: not used anymore - the variance just doesn't seem to work.

/*// TODO: K nicemu, lepsi je mit varianci zahrnutou v tom coef                   */ if(variance > varianceLimit) {
    // TODO: BPM NOVY
//    if(!hasBeat) {
//        beatCount++;
//        hasBeat = true;
//    }
    ////////////
                        if(windowsFromLastBeat >= windowsBetweenBeats) {
//                            System.out.println(sampleIndex + ":\t" + j + ":\t" + samples.length);
                            beatCount++;
                            windowsFromLastBeat = -1;
                            hasBeat = true;
                            break;
/*// TODO:                        */}
    // TODO: BPM NOVY
                    }
                }
                updateEnergySumsAndSubbands(j, oldestIndexInSubbands, energySums, currEnergies[j], subbandEnergies);
            }

            if(hasBeat) {
                for (; j < currEnergies.length; j++) {
                    updateEnergySumsAndSubbands(j, oldestIndexInSubbands, energySums, currEnergies[j], subbandEnergies);
                }
            }

            oldestIndexInSubbands++;
            sampleIndex = nextSampleIndex;
            nextSampleIndex += windowSizeInBytes;
            windowsFromLastBeat++;


            // Again optimize the case when windows.length is power of 2
            if (historySubbandsCount % 2 == 0) {       // TODO: U multi-channel verze chci subbandEnegies[i].length
                oldestIndexInSubbands %= historySubbandsCount; // TODO: U multi-channel verze chci subbandEnegies[i].length
            } else {
                if (oldestIndexInSubbands >= historySubbandsCount) { // TODO: U multi-channel verze chci subbandEnegies[i].length
                    oldestIndexInSubbands = 0;
                }
            }
        }

        ProgramTest.debugPrint("MAX_ENERGY:", todoMaxEnergy);
        int bpm = BPMUtils.convertBeatsToBPM(beatCount, samples.length, sampleSize, numberOfChannels, sampleRate);
        return bpm;
    }

    private static double computeVariance(double average, double[][] values, int subbandIndex) {
        double variance = 0;
        double val;
        for(int i = 0; i < values.length; i++) {
            val = values[i][subbandIndex] - average;
            variance += val*val;
        }

        return variance / values.length;
    }


    public static void computeSubbandEnergiesUsingFFT(byte[] samples, double[] currEnergies,
                                                      int startIndex,
                                                      int numberOfChannels,
                                                      int sampleSize,
                                                      int frameSize,
                                                      int mask,
                                                      DoubleFFT_1D fft,
                                                      double[] fftArray, double[] fftArrayMeasures,
                                                      int maxAbsoluteValue,
                                                      boolean isBigEndian,
                                                      boolean isSigned,
                                                      SubbandSplitterIFace splitter) {
        FFT.calculateFFTRealForward(samples, startIndex, numberOfChannels, sampleSize,
                frameSize, mask, fft, fftArray, maxAbsoluteValue, isBigEndian, isSigned);


        // TODO: NORMALIZACE
//        for(int i = 0; i < fftArray.length; i++) {
//            fftArray[i] /= (fftArray.length / 2);
//        }
        // TODO: NORMALIZACE



        FFT.convertResultsOfFFTToRealRealForward(fftArray, fftArrayMeasures);
        for(int subband = 0; subband < currEnergies.length; subband++) {
            currEnergies[subband] = splitter.getSubbandEnergy(fftArrayMeasures, currEnergies.length, subband);
        }
    }

    // The oldestIndexInSubbands should already be in range from 0 to energySums.length (== subbandCount)
    private static void updateEnergySumsAndSubbands(int subbandInd, int oldestIndexInSubbands, double[] energySums,
                                                    double currEnergy, double[][] subbandEnergies) {
        energySums[subbandInd] += -subbandEnergies[oldestIndexInSubbands][subbandInd] + currEnergy;
        subbandEnergies[oldestIndexInSubbands][subbandInd] = currEnergy;
    }


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
