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



import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.swing.*;

import test.ProgramTest;
import util.Aggregation;
import util.Utilities;
import util.audio.*;
import util.audio.io.AudioReader;
import util.audio.io.AudioWriter;
import analyzer.bpm.SubbandSplitterIFace;
import main.DiasynthTabbedPanel;
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
    public int calculateSizeOfOneSec() { return calculateSizeOfOneSec(this.sampleRate, this.frameSize); }
    public static int calculateSizeOfOneSec(int sampleRate, int frameSize) {
        return sampleRate * frameSize;
    }

    public static int calculateFrameSize(AudioFormat format) {
        return format.getChannels() * format.getSampleSizeInBits() / 8;
    }



    /**
     * Takes the int values representing some property (min or max or avg or rms)of each song part and returns them in 1D double array.
     *
     * @param songParts are the samples of the song part together with int, which represents some property of the song part.
     * @return Returns 1D double array containing the int values which are in the SongPartWithAverageValueOfSamples as int property.
     */
    public static double[] takeValuesFromSongParts(SongPartWithAverageValueOfSamples[] songParts) {
        double[] values = new double[songParts.length];

        for (int i = 0; i < values.length; i++) {
            values[i] = songParts[i].averageAmplitude;
        }

        return values;
    }

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
     * This method takes every x-th read part of length length from input stream
     * Starts at startFame
     * Example: if length = 3, x = 2, frameSize = 4 (bytes), startFrame = 1
     * then the first byte is discarded, then the next 3 * 4 bytes are taken, then 3 * 4 bytes are skipped and then
     * the next 3 * 4 bytes are taken, this continues until the end of the stream is reached
     * Only the parts of size length are returned (that means if the last part isn't long enough, then it won't be added to the output)
     *
     * @param audioStream is the stream containing the audio
     * @param length      is the length of one part (in frames)
     * @param x           - Every x-th part of length length is taken, if x = MAX_VALUE, then only the first part of length length is taken
     * @param frameSize   is the size of one frame
     * @param startFrame  is the first frame to be processed, that means frames from 0 to startFrame - 1 will be discarded
     * @return Returns 2 dimensional array, containing all the x-th parts of size length
     * @throws IOException is thrown when error with the input stream occurred.
     */
    public static byte[][] getEveryXthTimePeriodWithLength(InputStream audioStream, int length, int x, int frameSize, int startFrame) throws IOException {
        int currentTime = 0;
        int bytesRead = 0;
        byte[] songPart = new byte[length * frameSize];
        ArrayList<byte[]> list = new ArrayList<>();

        // Skip first startFrame * frameSize bytes ... the library skip method doesn't skip exactly the number of bytes it should
        bytesRead = AudioReader.readNotNeededSamples(audioStream, frameSize, startFrame);

        int bytesReadSum = 0;

        // The algorithm
        while (bytesRead != -1) {
            bytesReadSum = AudioReader.readNSamples(audioStream, songPart);
            if (bytesReadSum != songPart.length) {
                break;
            }
            // it's x-th part
            if (currentTime % x == 0) {
                // It has exactly length size
                if (bytesReadSum == songPart.length) {        // > 0 if we want to also get the last part, even if it isn't full
                    byte[] arr = new byte[songPart.length];
                    for (int k = 0; k < arr.length; k++) {
                        arr[k] = songPart[k];
                    }
                    list.add(arr);
                }
            }
            currentTime++;

            // take only the first part
            if (x == Integer.MAX_VALUE) {
                break;
            }
        }


        byte[][] bArr = new byte[list.size()][];
        for (int i = 0; i < bArr.length; i++) {
            bArr[i] = list.get(i);
        }

        return bArr;
    }


    /**
     * This method takes every x-th read part of length length from byte array
     * Starts at startFame
     * Example: if length = 3, x = 2, frameSize = 4 (bytes), startFrame = 1
     * then the first byte is discarded, then the next 3 * 4 bytes are taken, then 3 * 4 bytes are skipped and then
     * the next 3 * 4 bytes are taken, this continues until the end of the array is reached
     * Only the parts of size length are returned (that means if the last part isn't long enough, then it won't be added to the output)
     *
     * @param audio      is byte array containing audio
     * @param length     is the length of one part (in frames)
     * @param x          - Every x-th part of length length is taken, if x = MAX_VALUE, then only the first part of length length is taken
     * @param frameSize  is the size of one frame
     * @param startFrame is the first frame to be processed, that means frames from 0 to startFrame - 1 will be discarded
     * @return Returns 2 dimensional array, containing all the x-th parts of size length
     */
    public static byte[][] getEveryXthTimePeriodWithLength(byte[] audio, int length, int x, int frameSize, int startFrame) {

        int frameCount = audio.length / frameSize;
        int x1 = x - 1;
        int arrLen = 0;
        int remainingFrames = frameCount - startFrame;
        // Calculate how
        if (x == Integer.MAX_VALUE && remainingFrames >= length) {
            arrLen = 1;
        } else if (x == Integer.MAX_VALUE && remainingFrames < length) {
            arrLen = 0;
        } else {
            while (remainingFrames >= length) {
                arrLen++;
                remainingFrames = remainingFrames - length;
                remainingFrames = remainingFrames - length * x1;
            }
        }
        byte[][] result = new byte[arrLen][];
        int secondDimIndex = 0;
        int firstDimIndex = 0;
        int oldArrIndex = startFrame * frameSize;
        arrLen = length * frameSize;

        while (firstDimIndex < result.length) {
            secondDimIndex = 0;
            if (secondDimIndex + arrLen <= audio.length) {
                byte[] newSamples = new byte[arrLen];
                for (int i = 0; i < length; i++) {
                    for (int j = 0; j < frameSize; j++) {
                        newSamples[secondDimIndex] = audio[oldArrIndex];
                        oldArrIndex++;
                        secondDimIndex++;
                    }
                }
                result[firstDimIndex] = newSamples;
                oldArrIndex = oldArrIndex + x1 * frameSize * length;
            }
            firstDimIndex++;
        }

        return result;
    }


    /**
     * Returns 1D array containing every nth sample of size sampleSize.
     * If the result of this method wants to be played in some audio player, then it is important to notice, that to
     * play the song in original tempo we need to divide the sample rate and frame rate by n given in argument.
     * Important info: sampleSize and startSample needs to be equal to c * (original frame rate), where c > 0
     * That's limitation of java.
     *
     * @param samples     is the input stream with samples
     * @param sampleSize  is the size of one sample
     * @param n           - Every nth sample is taken
     * @param frameSize   is the size of one frame (= sampleSize * number of channels)
     * @param startSample is the number of sample to start at
     * @return Returns 1D array containing every nth sample of size sampleSize
     * @throws IOException is thrown when error with InputStream occurred or if the sampleSize is not multiple of the
     *                     original sampleSize or if the startSample is not multiple of the original sampleSize
     */
    public static byte[] takeEveryNthSampleOneChannel(InputStream samples, int sampleSize, int n, int frameSize, int startSample) throws IOException {
        int bytesRead = 0;
        byte[] arr = new byte[sampleSize * n];

        if (arr.length % frameSize != 0 || startSample % frameSize != 0) {        // limitation of java library
            throw new IOException("Not supported yet");                        // it's not possible to read smaller
        }                                                                    // chunks than size of frame
        ArrayList<Byte> sampleList = new ArrayList<>();
        byte[] newSamples;
        int bytesReadSum = 0;

        // skip samples until the startSample is reached
        bytesRead = AudioReader.readNotNeededSamples(samples, sampleSize, startSample);

        while (bytesRead != -1) {
            bytesReadSum = AudioReader.readNSamples(samples, arr);
            if (bytesReadSum >= sampleSize) {
                for (int i = 0; i < sampleSize; i++) {
                    sampleList.add(arr[i]);
                }
            } else {
                break;
            }
            if (bytesReadSum != arr.length) {
                bytesRead = -1;
            }
        }

        newSamples = new byte[sampleList.size()];
        for (int i = 0; i < newSamples.length; i++) {
            newSamples[i] = sampleList.get(i);
        }
        return newSamples;
    }


    /**
     * Returns 1D array containing every nth sample of size sampleSize.
     * If the result of this method wants to be played in some audio player, then it is important to notice, that to
     * play the song in original tempo we need to divide the sample rate and frame rate by n given in argument
     *
     * @param samples     is the byte array containing the samples
     * @param sampleSize  is the size of one sample
     * @param n           - Every nth sample is taken
     * @param startSample is the number of sample to start at
     * @return Returns 1D array containing every nth sample of size sampleSize
     */
    public static byte[] takeEveryNthSampleOneChannel(byte[] samples, int sampleSize, int n, int startSample) {
        // Solved by calling more general method
        byte[][] newSamples = getEveryXthTimePeriodWithLength(samples, 1, n, sampleSize, startSample);

        return convertTwoDimArrToOneDim(newSamples);
    }


    /**
     * Converts the 2D array to 1D array by stacking the labelReferenceArrs
     *
     * @param arr is the 2D array to be converted to 1D array
     * @return Returns 1D array
     */
    private static byte[] convertTwoDimArrToOneDim(byte[][] arr) {
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
        return takeEveryNthSampleMoreChannels(samples, numberOfChannels, sampleSize, 1, 0, totalAudioLength);
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
        return takeEveryNthSampleMoreChannelsDouble(samples, numberOfChannels, sampleSize, 1,
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






    /**
     * This method basically splits the array to channels and from each channel takes the n-th sample.
     * Internally it is performed a bit different, but the result is the same.
     * Doesn't work correctly if startSample isn't multiple of frameSize
     *
     * @param samples          is the input stream containing samples
     * @param numberOfChannels represents number of channels
     * @param sampleSize       is the size of 1 sample in a channel
     * @param n                - Every n-th sample is taken from all channels separately
     * @param startSample      - The first sample to be taken from each channel
     * @param totalAudioLength is the total length of the samples input in bytes.
     * @return Returns 2D byte array, where each array represents the channels where only every n-th sample is taken
     * @throws IOException is thrown when the error in input stream occurred
     */
    public static byte[][] takeEveryNthSampleMoreChannels(InputStream samples, int numberOfChannels, int sampleSize,
                                                          int n, int startSample, int totalAudioLength) throws IOException {
        int channelLen = getLengthOfOneChannelInSamplesForSampleSkipping(totalAudioLength, startSample, n,
            numberOfChannels, sampleSize);
        channelLen *= sampleSize;
        byte[][] arr = new byte[numberOfChannels][channelLen];
        int frameSize = sampleSize * numberOfChannels;
        byte[] oneFrame = new byte[frameSize];

        int bytesRead = 0;
        int arrIndex;
        int count = 0;
        int bytesReadSum = 0;
        int outputIndex = 0;

        bytesRead = AudioReader.readNotNeededSamples(samples, sampleSize, startSample);
        while (bytesRead != -1) {
            arrIndex = 0;
            bytesReadSum = AudioReader.readNSamples(samples, oneFrame);
            if (bytesReadSum < oneFrame.length) {
                break;
            }
            if (count % n == 0) {
                arrIndex = 0;
                for (int i = 0; i < numberOfChannels; i++) {
                    int channelOutputIndex = outputIndex;
                    for (int j = 0; j < sampleSize; j++, channelOutputIndex++, arrIndex++) {
                        arr[i][channelOutputIndex] = oneFrame[arrIndex];
                    }
                }

                outputIndex += sampleSize;
            }
            count++;
        }

        return arr;
    }

// TODO: PROGRAMO



    /**
     * This method basically splits the array to channels and from each channel takes the n-th sample.
     * Internally it is performed a bit different, but the result is the same.
     * Doesn't work correctly if startSample isn't multiple of frameSize
     *
     * @param samples          is the input stream containing samples
     * @param numberOfChannels represents number of channels
     * @param sampleSize       is the size of 1 sample in a channel
     * @param n                - Every n-th sample is taken from all channels separately
     * @param startSample      - The first sample to be taken from each channel
     * @param totalAudioLength is the total length of the samples input in bytes.
     * @return Returns 2D double array, where each array represents the channels where only every n-th sample is taken
     * @throws IOException is thrown when the error in input stream occurred
     */
    @Deprecated     // The buffer it uses is too small - only of frameSize
    public static double[][] takeEveryNthSampleMoreChannelsDoubleOldAndSlow(InputStream samples, int numberOfChannels,
                                                                            int sampleSize, int n, int startSample,
                                                                            boolean isBigEndian, boolean isSigned,
                                                                            int totalAudioLength) throws IOException {
        int channelLen = getLengthOfOneChannelInSamplesForSampleSkipping(totalAudioLength, startSample, n,
            numberOfChannels, sampleSize);
        double[][] outputArr = new double[numberOfChannels][channelLen];

        int frameSize = sampleSize * numberOfChannels;
        byte[] oneFrame = new byte[frameSize];

        int bytesRead = 0;
        int arrIndex;
        int count = 0;
        int bytesReadSum = 0;
        int outputIndex = 0;

        bytesRead = AudioReader.readNotNeededSamples(samples, sampleSize, startSample);
        while (bytesRead != -1) {
            arrIndex = 0;
            bytesReadSum = AudioReader.readNSamples(samples, oneFrame);
            if (bytesReadSum < oneFrame.length) {
                break;
            }
            if (count % n == 0) {
                arrIndex = 0;
                for (int i = 0; i < numberOfChannels; i++, arrIndex += sampleSize) {
                    Program.normalizeToDoubles(oneFrame, outputArr[i], sampleSize, sampleSize * 8,
                                               arrIndex, outputIndex, 1, isBigEndian, isSigned);
                }

                outputIndex++;
            }
            count++;
        }

        return outputArr;
    }

    /**
     * This method basically splits the array to channels and from each channel takes the n-th sample.
     * Internally it is performed a bit different, but the result is the same.
     * Doesn't work correctly if startSample isn't multiple of frameSize
     *
     * @param samples          is the input stream containing samples
     * @param numberOfChannels represents number of channels
     * @param sampleSize       is the size of 1 sample in a channel
     * @param n                - Every n-th sample is taken from all channels separately
     * @param startSample      - The first sample to be taken from each channel
     * @param totalAudioLength is the total length of the samples input in bytes.
     * @return Returns 2D double array, where each array represents the channels where only every n-th sample is taken
     * @throws IOException is thrown when the error in input stream occurred
     */
    public static double[][] takeEveryNthSampleMoreChannelsDouble(InputStream samples, int numberOfChannels,
                                                                  int sampleSize, int n, int startSample,
                                                                  boolean isBigEndian, boolean isSigned,
                                                                  int totalAudioLength) throws IOException {
        int channelLen = getLengthOfOneChannelInSamplesForSampleSkipping(totalAudioLength, startSample, n,
            numberOfChannels, sampleSize);
        double[][] outputArr = new double[numberOfChannels][channelLen];

        int frameSize = sampleSize * numberOfChannels;
        int FRAME_COUNT = 2048;
        byte[] buffer = new byte[frameSize * FRAME_COUNT];

        int mask = calculateMask(sampleSize);
        int maxAbsoluteValue = Program.getMaxAbsoluteValueSigned(8 * sampleSize);

        int bytesRead = 0;
        int bytesReadSum = 0;
        int outputIndex = 0;

        int nextNByteIndex = 0;
        int nextTotalIndex = 0;

        bytesRead = AudioReader.readNotNeededSamples(samples, sampleSize, startSample);
        while (bytesRead != -1) {
            bytesReadSum = AudioReader.readNSamples(samples, buffer);
            if (bytesReadSum == -1) {
                break;
            }

            nextTotalIndex += bytesReadSum;
            while(nextNByteIndex < nextTotalIndex && outputIndex < outputArr[0].length) {
                for (int i = 0, arrIndex = nextNByteIndex % buffer.length; i < numberOfChannels; i++, arrIndex += sampleSize) {
                    int sample = Program.convertBytesToInt(buffer, sampleSize, mask, arrIndex, isBigEndian, isSigned);
                    outputArr[i][outputIndex] = Program.normalizeToDoubleBetweenMinusOneAndOne(sample, maxAbsoluteValue, isSigned);
                }

                outputIndex++;
                nextNByteIndex += n * frameSize;
            }
        }

        return outputArr;
    }


    /**
     * Returns int which is the length of channel 0 (if the startSample wasn't multiple of frameSize then some next channels may have length of the 0th channel - 1)
     * @param totalAudioLength
     * @param startSample
     * @param skip
     * @param numberOfChannels
     * @param sampleSize
     * @return
     */
    private static int getLengthOfOneChannelInSamplesForSampleSkipping(int totalAudioLength, int startSample,
                                                                       int skip, int numberOfChannels, int sampleSize) {
        int channelLen;
        int totalByteSize = totalAudioLength - (startSample * sampleSize);
        int frameSize = numberOfChannels * sampleSize;
        int samplesPerChannel = totalByteSize / frameSize;
        // Again the thing I wrote to TODO - solving the problem that I want to count the channel if samplesPerChannel == 1 as 1
        // It is in todo under the tag: PROBLEM_KTEREJ_JSEM_UZ_RESIL_V_NEKOLIKA_PROGRAMECH
        channelLen = (skip - 1 + samplesPerChannel) / skip;


        return channelLen;
    }



    /**
     * Takes the byte array with samples and returns the samples of channels in byte labelReferenceArrs (1 array = 1 channel).
     *
     * @param samples    is the byte array containing samples
     * @param sampleSize is the size of 1 sample in a channel
     * @return Returns 2D byte array, where each byte array corresponds to 1 channel.
     * @throws IOException is thrown when error with input
     */
    public static byte[][] separateChannels(byte[] samples, int numberOfChannels, int sampleSize) throws IOException {
        return takeEveryNthSampleMoreChannels(samples, numberOfChannels, sampleSize, 1, 0);
    }


    /**
     * Takes nth sample from each channel.
     * This method basically splits the array to channels and from each channel takes the n-th sample.
     * Internally it is performed a bit different, but the result is the same.
     *
     * @param samples          is the byte array containing samples
     * @param numberOfChannels represents number of channels
     * @param sampleSize       is the size of 1 sample in a channel
     * @param n                - Every n-th sample is taken from all channels separately
     * @param startSample      - The first sample to be taken from each channel
     * @return Returns 2D byte array, where each array represents the channels where only every n-th sample is taken
     */
    public static byte[][] takeEveryNthSampleMoreChannels(byte[] samples, int numberOfChannels, int sampleSize, int n, int startSample) {
        byte[][] arr = new byte[numberOfChannels][];

        for (int i = 0; i < numberOfChannels; i++) {
            arr[i] = takeEveryNthSampleOneChannel(samples, sampleSize, n * numberOfChannels, i + startSample);
        }

        return arr;
    }





    /**
     * Mask for top 8 bits in int
     */
    private static final int TOP_8_BITS_MASK = 0xFF_00_00_00;

    /**
     * Creates mask used for converting the byte array to number of size sampleSize bytes, which must fit to int.
     * The mask has the top sampleSize * 8 bits set to 1, the rest is set to 0
     *
     * @param sampleSize is the size of 1 sample in bytes
     * @return returns the mask which is used for converting the byte array to int
     * @throws IOException is thrown when the sample size > 4, because then the samples can't fit to int, or when it is <= 0
     */
    public static int calculateMask(int sampleSize) throws IOException {
        // TODO: Tyhle kontroly asi můžu dát pryč
        if (sampleSize <= 0) {
            throw new IOException("Sample size is <= 0 bytes");
        }
        else  if (sampleSize > 4) {
            throw new IOException("SampleSize is > 4 bytes");
        }

        if (sampleSize == 4) {
            return 0x00000000;
        }
        int mask = TOP_8_BITS_MASK;
        for (int k = 0; k < Integer.BYTES - sampleSize - 1; k++) {
            mask = mask >> 8;
            mask = mask | TOP_8_BITS_MASK;
        }

        return mask;
    }


    /**
     * Creates mask used for converting the byte array to to number of size sampleSize bytes, which must fit to int.
     * The mask has the top sampleSize * 8 bits set to 0, the rest is set to 1. So the result is binary negation of the
     * result of method calculateMask, if it was called with the same parameter.
     *
     * @param sampleSize is the size of 1 sample in bytes
     * @return returns the mask which is used for converting the byte array to int
     * @throws IOException is thrown when the sample size > 4, because then the samples can't fit to int, or when it is <= 0
     */
    public static int calculateInverseMask(int sampleSize) throws IOException {
        if (sampleSize > 4 || sampleSize <= 0) {
            throw new IOException();
        }
        if (sampleSize == 4) {
            return 0xFFFFFFFF;
        }
        int inverseMaskTop8Bits = ~TOP_8_BITS_MASK;
        int inverseMask = inverseMaskTop8Bits;
        for (int k = 0; k < Integer.BYTES - sampleSize - 1; k++) {
            inverseMask = inverseMask >> 8;
            inverseMask = inverseMask & inverseMaskTop8Bits;
        }

        return inverseMask;
    }


    /**
     * Binary negates the argument mask.
     *
     * @return Returns binary negation of argument mask.
     */
    public static int calculateInverseMaskFromMask(int mask) {
        return (~mask);
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

        int mask = calculateMask(sampleSize);

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

        int mask = calculateMask(sampleSize);

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
     * Reverses the samples. First sample is last, last is first etc.
     * @param arr samples to be reversed.
     * @param sampleSize is the size of 1 sample.
     */
    public static void reverseArr(byte[] arr, int sampleSize) {
        int sampleCount = arr.length / sampleSize;
        int index = 0;
        int index2 = 0;
        for(int i = 0; i < sampleCount / 2; i++)
        {
            index2 = index2 + sampleSize;
            for(int j = 0; j < sampleSize; j++) {
                byte temp = arr[index];
                arr[index] = arr[arr.length - index2 + j];
                arr[arr.length - index2 + j] = temp;
                index++;
            }
        }
    }


    /**
     * Reverses the samples. First sample is last, last is first etc. Isn't tested
     * @param arr samples to be reversed.
     * @param numberOfChannels is the number of channels.
     */
    public static void reverseArr(double[] arr, int numberOfChannels) {
        for(int index = 0, index2 = arr.length - 1; index < arr.length / 2; index2 -= numberOfChannels) {
            int upperBound = index + numberOfChannels;
            for(; index < upperBound; index++, index2++) {
                double tmp = arr[index];
                arr[index] = arr[index2];
                arr[index2] = tmp;
            }
        }
    }



    /**
     * Plays the audio given in the 1D array song in audio audioFormat given as parameter.
     * @param song is the audio with the samples to be played.
     * @param audioFormat is the audio audioFormat.
     * @param playBackwards if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * @throws LineUnavailableException is thrown when error with playing the song occurred.
     */
    public static void playSong(byte[] song, AudioFormat audioFormat, boolean playBackwards) throws LineUnavailableException {
        int frameSize = (audioFormat.getSampleSizeInBits() / 8) * audioFormat.getChannels();
        int bytesWritten;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(audioFormat);
        line.start();
        if(playBackwards) {
            reverseArr(song, audioFormat.getSampleSizeInBits() / 8);
        }
        // because number of frames needs to be integer, so if some last bytes doesn't fit in the last frame,
        // we don't play them
        int bytesToWrite = song.length - (song.length % frameSize);
        bytesWritten = line.write(song, 0, bytesToWrite);
        line.drain();
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
            playSong(songArr, audioFormat, playBackwards);
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
     * Plays the audio given in the 1D array song, other parameters of this method describe the audioFormat in which will be the audio played.
     * @param song is 1D byte array which contains the samples, which will be played.
     * @param encoding is the encoding of the audio data.
     * @param sampleRate is the sample rate of the audio data.
     * @param sampleSizeInBits is the size of 1 sample in bits.
     * @param numberOfChannels represents the number of channels.
     * @param frameSize is the size of one frame.
     * @param frameRate is the frame rate of the audio.
     * @param isBigEndian is true if the samples are in big endian, false if in little endian
     * @param playBackwards if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * @throws LineUnavailableException is thrown when there is problem with feeding the data to the SourceDataLine.
     */
    public static void playSong(byte[] song, Encoding encoding, int sampleRate, int sampleSizeInBits,
                                int numberOfChannels, int frameSize, float frameRate, boolean isBigEndian,
                                boolean playBackwards) throws LineUnavailableException {
        AudioFormat audioFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits, numberOfChannels, frameSize, frameRate, isBigEndian);
        playSong(song, audioFormat, playBackwards);
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
     * Plays song parts given in the songParts parameter.
     * @param songParts contains the song parts together with the average value of the song part.
     * @param audioFormat is the audio audioFormat to play the song parts in.
     * @param ascending is true if we want to play the song parts in ascending order (first play part at the 0th index, then 1st, etc.)
     * if it is set to false, then play in descending order (last index, last - 1 index, etc.)
     * This is important if the songParts array is sorted, if so then the ascending order (when ascending = true)
     * plays the songParts with lowest
     * average amplitude first. Otherwise first the ones with the highest and then continue in descending order.
     * @param playBackwards if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * Also if we want to play the song backwards (from finish to start), then we should call it with
     * specific values: ascending = false and the song parts should't be sorted* @throws LineUnavailableException
     * @throws LineUnavailableException is thrown when there is problem with feeding the data to the SourceDataLine.
     */
    public static void playSongParts(SongPartWithAverageValueOfSamples[] songParts, AudioFormat audioFormat, boolean ascending, boolean playBackwards) throws LineUnavailableException {
        int bytesWritten;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(audioFormat);
        line.start();

        if(playBackwards) {
            for(int i = 0; i < songParts.length; i++) {
                reverseArr(songParts[i].songPart, audioFormat.getSampleSizeInBits() / 8);
            }
            if(ascending) {
                for(int i = 0; i < songParts.length; i++) {
                    // Number of frames needs to be an integer,
                    // so we don't play that part (if the number of frames in that part is not an integer)
                    // because the reverse method couldn't produce correct output, so the output is probably noise
                    if(songParts[i].songPart.length % audioFormat.getFrameSize() == 0) {
                        bytesWritten = line.write(songParts[i].songPart, 0, songParts[i].songPart.length);
                    }
                }
            } else {
                for(int i = songParts.length - 1; i >= 0; i--) {
                    // Number of frames needs to be an integer,
                    // so we don't play that part (if the number of frames in that part is not an integer)
                    // because the reverse method couldn't produce correct output, so the output is probably noise
                    if(songParts[i].songPart.length % audioFormat.getFrameSize() == 0) {
                        bytesWritten = line.write(songParts[i].songPart, 0, songParts[i].songPart.length);
                    }
                }
            }
        } else {
            if(ascending) {
                for(int i = 0; i < songParts.length; i++) {
                    // Because number of frames needs to be integer
                    int bytesToWrite = songParts[i].songPart.length - (songParts[i].songPart.length % audioFormat.getFrameSize());
                    bytesWritten = line.write(songParts[i].songPart, 0, bytesToWrite);
                }
            } else {
                for(int i = songParts.length - 1; i >= 0; i--) {
                    // Because number of frames needs to be integer
                    int bytesToWrite = songParts[i].songPart.length - (songParts[i].songPart.length % audioFormat.getFrameSize());
                    bytesWritten = line.write(songParts[i].songPart, 0, bytesToWrite);
                }
            }
        }
        line.drain();
    }


    /**
     * Plays song parts given in the songParts parameter.
     * @param songParts contains the song parts together with the average value of the song part.
     * @param encoding is the encoding of the audio data.
     * @param sampleRate is the sample rate of the audio data.
     * @param sampleSizeInBits is the size of 1 sample in bits.
     * @param numberOfChannels represents the number of channels.
     * @param frameSize is the size of one frame.
     * @param frameRate is the frame rate of the audio.
     * @param isBigEndian is true if the samples are in big endian, false if in little endian
     * @param ascending is true if we want to play the song parts in ascending order (first play part at the 0th index, then 1st, etc.)
     * if it is set to false, then play in descending order (last index, last - 1 index, etc.)
     * This is important if the songParts array is sorted, if so then the ascending order play the songParts with lowest
     * average amplitude first. Otherwise first the ones with the highest and then continue in descending order.
     * @param playBackwards if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * Also if we want to play the song backwards (from finish to start), then we should call it with
     * specific values: ascending = false and the song parts should't be sorted
     * @throws LineUnavailableException is thrown when there is problem with feeding the data to the SourceDataLine.
     */
    public static void playSongParts(SongPartWithAverageValueOfSamples[] songParts, Encoding encoding,
                              int sampleRate, int sampleSizeInBits, int numberOfChannels, int frameSize, float frameRate,
                              boolean isBigEndian, boolean ascending, boolean playBackwards) throws LineUnavailableException {

        AudioFormat audioFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits, numberOfChannels, frameSize, frameRate, isBigEndian);
        playSongParts(songParts, audioFormat, ascending, playBackwards);
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
        mask = calculateMask(sampleSizeInBytes);
        maxAbsoluteValue = getMaxAbsoluteValueSigned(sampleSizeInBits);

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


    public static int getMaxAbsoluteValue(int sampleSizeInBits, boolean isSigned) {
        if(isSigned) {
            return getMaxAbsoluteValueSigned(sampleSizeInBits);
        }
        else {
            return getMaxAbsoluteValueUnsigned(sampleSizeInBits);
        }
    }


    public static int getMaxAbsoluteValueSigned(int sampleSizeInBits) {
        return (1 << (sampleSizeInBits - 1)) - 1;
    }
    public static int getMaxAbsoluteValueUnsigned(int sampleSizeInBits) {
        return (1 << sampleSizeInBits) - 1;
    }



    public static final String LOG_MESSAGE_WHEN_SET_VARIABLES_RETURN_FALSE =
            "Probably invalid audioFormat or the file wasn't audio or the path was invalid";

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
     * Performs aggregation (compression) agg to all channels. For all channels do respectively, take n samples
     * perform the agg action on them, add the given number to the result, continue until the end of the channel is reached.
     * @param channels is 2D byte array, where 1 array corresponds to 1 channel.
     * @param n is the number of samples to perform 1 aggregation to.
     * @param sampleSize is the size of 1 sample in bytes.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @param agg is the type of aggregation to be performed.
     * @return Returns 2D array which corresponds to the modified channels.
     * @throws IOException is thrown when the given agg is not supported.
     */
    @Deprecated
    public static byte[][] forEachChannelModifySamplesMoreChannels(byte[][] channels, int n, int sampleSize,
                                                                   boolean isBigEndian, boolean isSigned,
                                                                   Aggregation agg) throws IOException {
        byte[][] modChannels = new byte[channels.length][];
        ArrayList<Byte> moddedChannel = new ArrayList<>();
        byte[] samples = new byte[n * sampleSize];
        for(int i = 0; i < channels.length; i++) {
            int index = 0;
            while(index + samples.length <= channels[i].length) {
                for(int j = 0; j < samples.length; j++) {
                    samples[j] =  channels[i][index];
                    index++;
                }
                int newSample = (int) Aggregation.performAggregation(samples, sampleSize, isBigEndian, isSigned, agg);

                byte[] arr = convertIntToByteArr(sampleSize, newSample, isBigEndian);
                for(int k = 0; k < arr.length; k++) {
                    moddedChannel.add(arr[k]);
                }
            }
            byte[] arr = new byte[moddedChannel.size()];
            for(int k = 0; k < arr.length; k++) {
                arr[k] = moddedChannel.get(k);
            }
            modChannels[i] = arr;
        }

        return modChannels;
    }


    /**
     * Performs aggregation (compression) agg to channel. Take n samples
     * perform the agg action on them, add the given number to the result, continue until the end of the channel is reached.
     * @param mono is 1D byte array with samples of the mono audio.
     * @param n is the number of samples to perform 1 aggregation to.
     * @param sampleSize is the size of 1 sample in bytes.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @param agg is the type of aggregation to be performed.
     * @return Returns the modified 1D byte array.
     * @throws IOException
     */
    @Deprecated
    public static byte[] forEachChannelModifySamplesOneChannel(byte[] mono, int n, int sampleSize,
                                                               boolean isBigEndian, boolean isSigned,
                                                               Aggregation agg) throws IOException {
        byte[][] channel = new byte[1][];
        channel[0] = mono;
        byte[][] result = forEachChannelModifySamplesMoreChannels(channel, n, sampleSize, isBigEndian, isSigned, agg);
        return result[0];
    }


    /**
     * Takes the 1D byte array (parameter samples) and split it to parts of size n * frameSize.
     * @param samples is the 1D byte array with the samples.
     * @param n is the size of the 1 song part.
     * @param frameSize is the size of 1 frame (= numberOfChannels * sampleSize).
     * @return Returns the 2D array where 1 byte array represents the part of size n * frameSize.
     */
    public byte[][] convertSongPartToMultipleSongPartsOfSizeNFrames(byte[] samples, int n, int frameSize) {
        byte[][] result = getEveryXthTimePeriodWithLength(samples, n, 1, frameSize, 0);
        return result;
    }


    /**
     * Takes the input stream (parameter samples) and split it to parts of size n * frameSize.
     * @param samples is the input stream with the samples.
     * @param n is the size of the 1 song part.
     * @param frameSize is the size of 1 frame (= numberOfChannels * sampleSize).
     * @return Returns the 2D array where 1 byte array represents the part of size n * frameSize.
     */
    public byte[][] convertWholeSongToMultipleSongPartsOfSizeNFrames(InputStream samples, int n, int frameSize) {
        byte[][] result = getEveryXthTimePeriodWithLength(song, n, 1, frameSize, 0);

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
        int mask = calculateMask(sampleSize);
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
        int maxAbsoluteValue = getMaxAbsoluteValueSigned(sampleSizeInBits);
        int arrIndex = 0;
        int mask = calculateMask(sampleSize);

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
        int maxAbsoluteValue = getMaxAbsoluteValueSigned(sampleSizeInBits);
        int mask = calculateMask(sampleSize);
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
        int maxAbsoluteValue = getMaxAbsoluteValueSigned(sampleSizeInBits);
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
        int maxAbsoluteValue = getMaxAbsoluteValueSigned(sampleSizeInBits);
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
        int mask = calculateMask(sampleSize);
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
        upSampledArr = runLowPassFilter(upSampledArr, newSampleRate / 2, 64, oldSampleRate,
            numberOfChannels, sampleSize, frameSize, isBigEndian, isSigned);			// Low pass filter for the nyquist frequency of the new frequency
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
        runLowPassFilter(upSampledArr, 0, numberOfChannels, oldSampleRate,
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
        byte[] filtered = runLowPassFilter(samples, newSampleRate / 2, 64, oldSampleRate,
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
        runLowPassFilter(samples, 0, numberOfChannels, oldSampleRate,
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


    /**
     * Fills given array with random numbers from range lowestRandom to highestRandom. Usually from -1 to 1.
     * Where 1 random generated number is used to set next repeatedNumbersCount samples.
     * Changes the values in array.
     * @param arr is the array to be filled.
     * @param lowestRandom is the lowest possible number to be generated. Usually -1.
     * @param highestRandom is the highest possible number to be generated. Usually 1.
     * @param repeatedNumbersCount is number of samples to be set with 1 random number.
     */
    public static void generateWhiteNoiseWithRepeatByRef(double[] arr, int repeatedNumbersCount, double lowestRandom, double highestRandom) {
        double random;
        for(int i = 0; i < arr.length;) {
            random = ThreadLocalRandom.current().nextDouble(lowestRandom, highestRandom);
            for(int j = 0; j < repeatedNumbersCount; j++, i++) {
                arr[i] = random;
            }
        }
    }



    /**
     * Fills given array with random numbers from range lowestRandom to highestRandom. Usually from -1 to 1.
     * Where 1 random generated number is used to set next repeatedNumbersCount samples.
     * @param len is the length of the array to be filled with random noise.
     * @param repeatedNumbersCount is number of samples to be set with 1 random number.
     * @param lowestRandom is the lowest possible number to be generated. Usually -1.
     * @param highestRandom is the highest possible number to be generated. Usually 1.
     * @return returns the array with white noise with repeat.
     */
    public static double[] generateWhiteNoiseWithRepeatByCopy(int len, int repeatedNumbersCount, double lowestRandom, double highestRandom) {
        double[] retArr = new double[len];
        generateWhiteNoiseWithRepeatByRef(retArr, repeatedNumbersCount, lowestRandom, highestRandom);
        return retArr;
    }


    /**
     * Fills given array with random numbers from range lowestRandom to highestRandom. Usually from -1 to 1.
     * 2 random numbers are generated. First sample gets the first random number, then n-1 samples
     * are linearly interpolated to the second random number (exclusive) and then follows the second random number.
     * Then the second random is taken as first and new second random number is generated, then we interpolate this, etc.
     * @param arr is the array to be filled.
     * @param n is number of samples after which will be next random number generated.
     * @param lowestRandom is the lowest possible number to be generated. Usually -1.
     * @param highestRandom is the highest possible number to be generated. Usually 1.
     * That means: Generate random number every nth sample.
     */
    public static void generateWhiteNoiseWithLinearInterpolationByRef(double[] arr, int n, double lowestRandom, double highestRandom) {
        double random1;
        double random2 = ThreadLocalRandom.current().nextDouble(lowestRandom, highestRandom);
        double jump;


        for(int i = 0; i < arr.length;) {
            random1 = random2;
            random2 = ThreadLocalRandom.current().nextDouble(lowestRandom, highestRandom);
            jump = (random2 - random1) / n;

            arr[i] = random1;
            i++;
            for(int j = 0; j < n; j++, i++) {
                arr[i] = random1;
                random1 += jump;
            }
            arr[i] = random2;
            i++;
        }
    }


    /**
     * Same as generateWhiteNoiseWithLinearInterpolationByRef but the array is returned and created internally.
     * @param len is the length of the array to be filled with random noise.
     * @param n is number of samples after which will be next random number generated.
     * @param lowestRandom is the lowest possible number to be generated. Usually -1.
     * @param highestRandom is the highest possible number to be generated. Usually 1.
     * @return Returns white noise with linear interpolation.
     */
    public static double[] generateWhiteNoiseWithLinearInterpolationByCopy(int len, int n, double lowestRandom, double highestRandom) {
        double[] retArr = new double[len];
        generateWhiteNoiseWithLinearInterpolationByRef(retArr, n, lowestRandom, highestRandom);
        return retArr;
    }


    // TODO: Jine interpolace podle me nemaji smysl


    /**
     * Probability 1 - probabilityToContinue is the probability that the method will end.
     * Until the method ends, it chooses random sample in each iteration which will be set with parameter number.
     * Changes the input array.
     * @param samples is the input array.
     * @param number is the number with which will be set randomly chosen sample.
     * @param probabilityToContinue is the probability that we will set some next sample with parameter number.
     */
    public static void setRandomSamplesToNumberByRef(double[] samples, double number, double probabilityToContinue) {
        Random rand = new Random();
        int index;

        while(rand.nextDouble() < probabilityToContinue) {
            index = rand.nextInt(samples.length);
            samples[index] = number;
        }
    }

    /**
     * Same as setRandomSamplesToNumberByRef but doesn't change the input array.
     * @param samples is the input array.
     * @param number is the number with which will be set randomly chosen sample.
     * @param probabilityToContinue is the probability that we will set some next sample with parameter number.
     * @return Returns copy of the samples array where random samples are set to number.
     */
    public static double[] setRandomSamplesToNumberByCopy(double[] samples, double number, double probabilityToContinue) {
        double[] retArr = new double[samples.length];
        for(int i = 0; i < samples.length; i++) {
            retArr[i] = samples[i];
        }
        setRandomSamplesToNumberByRef(retArr, number, probabilityToContinue);

        return retArr;
    }

    // TODO: Teoreticky lze pridat verzi co vybere random index a da na neho random cislo
    /**
     * Probability 1 - probabilityToContinue is the probability that the method will end.
     * Until the method ends, it chooses random sample in each iteration which will be set to random double number between
     * lowestRandom and highestRandom.
     * Changes the input array.
     * @param samples is the input array.
     * @param probabilityToContinue is the probability that we will set some next sample with parameter number.
     * @param lowestRandom is the lowest possible number to be generated. Usually -1.
     * @param highestRandom is the highest possible number to be generated. Usually 1.
     */
    public static void setRandomSamplesToRandomNumberByRef(double[] samples, double probabilityToContinue, double lowestRandom, double highestRandom) {
        Random rand = new Random();
        int index;

        while(rand.nextDouble() < probabilityToContinue) {
            index = rand.nextInt(samples.length);
            samples[index] = ThreadLocalRandom.current().nextDouble(lowestRandom, highestRandom);;
        }
    }

    /**
     * Same as setRandomSamplesToNumberByRef but doesn't change the input array.
     * @param samples is the input array.
     * @param probabilityToContinue is the probability that we will set some next sample with parameter number.
     * @param lowestRandom is the lowest possible number to be generated. Usually -1.
     * @param highestRandom is the highest possible number to be generated. Usually 1.
     * @return Returns copy of the samples array where random samples are set to number.
     */
    public static double[] setRandomSamplesToRandomNumberByCopy(double[] samples, double probabilityToContinue, double lowestRandom, double highestRandom) {
        double[] retArr = new double[samples.length];
        for(int i = 0; i < samples.length; i++) {
            retArr[i] = samples[i];
        }
        setRandomSamplesToRandomNumberByRef(retArr, probabilityToContinue, lowestRandom, highestRandom);

        return retArr;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// TODO: Filtry

    // TODO: Not sure about this, maybe moving average is calculated from the already averaged values,
    // TODO: but this way it makes more sense.
    // TODO: !!!!!!!!!!!!! Running average is filter, so it should be implemented by using the filter method !!!!
    // TODO: !!!!!!!!!!!!! But it is specific filter, which can be implemented more efficiently than general filter
    /**
     * Performs moving window average on windows of size windowSize. Moving window average averages last
     * windowSize samples and the average is stored in the last sample of window.
     * Changes the input array.
     * @param samples is the input array to perform the window average on.
     * @param windowSize is the size of window on which the averaging will be performed.
     *                   For example if == 1 then we average just 1 sample in each channel, so the output won't change.
     *
     * @deprecated Old method works only for mono
     */
    public static void performMovingWindowAverageByRef(double[] samples, int windowSize) {
//		double oldSampleValue;
//		double windowSum = 0;
//
//		int i = 0;
//		int firstIndexInWindow = 0;
//		for(; i < windowSize; i++) {		// Sum of first window
//			windowSum += samples[i];
//		}
//
//
//		// Now we will just move the window (subtract first element of that window and add the last one)
//		for(; i < samples.length; i++, firstIndexInWindow++) {
//			oldSampleValue = samples[i];
//			samples[i] = windowSum / windowSize;
//			windowSum = windowSum - samples[firstIndexInWindow] + oldSampleValue;
//		}

        // Zmenena verze
        double[] oldSampleValues = new double[windowSize];
        double windowSum = 0;
        int i = 0;
        for(; i < windowSize; i++) {		// Sum of first window
            windowSum += samples[i];
            oldSampleValues[i] = samples[i];
        }
        i--;			// TODO: Zmena oproti minulemu, protoze mi prijde ze se ten prumer ma pocitat i z te soucasne hodnoty


        // Now we will just move the window (subtract first element of that window and add the last one)
        int firstIndexInWindow = 0;
        // 2 Variants because of optimization
        if(windowSize % 2 == 0)
        {
            for(; i < samples.length - 1; i++, firstIndexInWindow++) {
                samples[i] = windowSum / windowSize;
                windowSum = windowSum - oldSampleValues[firstIndexInWindow % windowSize] + samples[i+1];
                oldSampleValues[firstIndexInWindow % windowSize] = samples[i+1];
            }
        }
        else {
            for(; i < samples.length - 1; i++, firstIndexInWindow++) {
                samples[i] = windowSum / windowSize;
                if(firstIndexInWindow == windowSize) {
                    firstIndexInWindow = 0;
                }
                windowSum = windowSum - oldSampleValues[firstIndexInWindow] + samples[i+1];
                oldSampleValues[firstIndexInWindow] = samples[i+1];
            }
        }
        samples[samples.length - 1] = windowSum / windowSize;
    }

    /**
     * Performs moving window average on windows of size windowSize. Moving window average averages last
     * windowSize samples and the average is stored in the last sample of window.
     * Changes the input array.
     * @param samples is the input array to perform the window average on.
     * @param windowSize is the size of window on which the averaging will be performed.
     *                   For example if == 1 then we average just 1 sample in each channel, so the output won't change.
     * @param numberOfChannels is the number of channels.
     */
    public static void performMovingWindowAverageByRef(double[] samples, int windowSize, int numberOfChannels) {
//		double oldSampleValue;
//		double windowSum = 0;
//
//		int i = 0;
//		int firstIndexInWindow = 0;
//		for(; i < windowSize; i++) {		// Sum of first window
//			windowSum += samples[i];
//		}
//
//
//		// Now we will just move the window (subtract first element of that window and add the last one)
//		for(; i < samples.length; i++, firstIndexInWindow++) {
//			oldSampleValue = samples[i];
//			samples[i] = windowSum / windowSize;
//			windowSum = windowSum - samples[firstIndexInWindow] + oldSampleValue;
//		}

        // Zmenena verze
        double[][] oldSampleValues = new double[numberOfChannels][windowSize];
        double[] windowSum = new double[numberOfChannels];
        int sampleInd = 0;
        int indexCheck = windowSize * windowSum.length;
        if(indexCheck > samples.length) {
            return;
        }
        for(int i = 0; i < windowSize; i++) {		// Sum of first window
            for(int ch = 0; ch < windowSum.length; ch++, sampleInd++) {
                windowSum[ch] += samples[sampleInd];
                oldSampleValues[ch][i] = samples[sampleInd];
            }
        }
        sampleInd -= numberOfChannels;			// TODO: Zmena oproti minulemu, protoze mi prijde ze se ten prumer ma pocitat i z te soucasne hodnoty


        // Now we will just move the window (subtract first element of that window and add the last one)
        int firstIndexInWindow = 0;
        double oldVal;
        // 2 Variants because of optimization
        if(windowSize % 2 == 0)
        {
            for(; sampleInd < samples.length - numberOfChannels; firstIndexInWindow++) {
                for(int ch = 0; ch < numberOfChannels; ch++, sampleInd++) {
                    samples[sampleInd] = windowSum[ch] / windowSize;
                    windowSum[ch] = windowSum[ch] - oldSampleValues[ch][firstIndexInWindow % windowSize] + samples[sampleInd + numberOfChannels];
                    oldSampleValues[ch][firstIndexInWindow % windowSize] = samples[sampleInd + numberOfChannels];
                }
            }
        }
        else {
            for(; sampleInd < samples.length - numberOfChannels; firstIndexInWindow++) {
                for(int ch = 0; ch < numberOfChannels; ch++, sampleInd++) {
                    samples[sampleInd] = windowSum[ch] / windowSize;
                    if (firstIndexInWindow == windowSize) {
                        firstIndexInWindow = 0;
                    }
                    windowSum[ch] = windowSum[ch] - oldSampleValues[ch][firstIndexInWindow] + samples[sampleInd + numberOfChannels];
                    oldSampleValues[ch][firstIndexInWindow] = samples[sampleInd + numberOfChannels];
                }
            }
        }
        for(int i = 0; i < windowSum.length; i++, sampleInd++) {
            samples[sampleInd] = windowSum[i] / windowSize;
        }
    }



    // TODO: !!! Not sure if I should work with ints or doubles when multiplying with coeffecients - for example
    // for example in doubles 2/3 + 2/3 = 4/3 which will be converted to 1. But when working with ints it is 0+0=0
    // TODO: Udelat reference variantu
    /**
     * Performs non-recursive filter, result is returned in new array. Non-recursive filter is this (y[n] is n-th output sample and x[n] is n-th input sample):
     * <br>
     * y[n] = coef[0] * x[n - coef.length + 1] + ... + coef[coef.length] x[n]
     * @param samples is the input array. It isn't changed.
     * @param coef are the coefficients for the input samples. The last index contains index for the currently computed output. The first index is the (coef.length+1)-th before the current sample.
     * @param numberOfChannels represents the number of channels
     * @param sampleSize is the size of 1 sample
     * @param frameSize is the size of 1 frame
     * @param isBigEndian true if the samples are in big endian, false otherwise.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @return Returns new array gotten from input samples array by non-recursive filter.
     * @throws IOException is thrown by method calculateMask if the sampleSize is invalid.
     */
    @Deprecated
    public static byte[] performNonRecursiveFilter(byte[] samples, double[] coef, int numberOfChannels,
                                                   int sampleSize, int frameSize, boolean isBigEndian, boolean isSigned) throws IOException {
        byte[] retArr = new byte[samples.length];
        int[] vals = new int[numberOfChannels];
        int index;
        int startingCoefInd;
        int sample;
        byte[] sampleBytes = new byte[sampleSize];
        int mask = calculateMask(sampleSize);

        // Filter for the first indexes is a bit different, since they dont have all the preceding samples for the filtering.
        // It's for optimization because we need to check if there are the preceding samples.
        startingCoefInd = -coef.length * frameSize + frameSize;		// +1 because the current sample can be used (Simple check of correctness is if we had just 1 coef)
        int resInd;
        int coefInd;
        for(resInd = 0, coefInd = 0; coefInd < coef.length - 1; startingCoefInd += frameSize, coefInd++) {
            // Covers the case when there is more coefficients than frames,
            // but sample.length is expected to be containing only full frames,
            // that is samples.length % frameSize == 0
            if(resInd >= retArr.length) {
                return retArr;
            }
            for(int ch = 0; ch < vals.length; ch++) {
                vals[ch] = 0;
            }
            index = startingCoefInd;
// TODO:            System.out.println(resInd + ":" + index + "\t:\t" + retArr.length + ":" + samples.length + ":\t" + numberOfChannels + ":\t" + sampleSize);
            for (int j = 0; j < coef.length; j++) {
                if (index >= 0) {
                    for (int ch = 0; ch < vals.length; ch++, index += sampleSize) {
                        sample = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
                        vals[ch] += coef[j] * sample;
                        // TODO:                      System.out.println("SAMPLE:\t" + sample + "\t:\tMULTSAMPLE:\t" + vals[ch]);
// TODO:                        System.out.println(index);
                    }
                }
                else {
// TODO:                    System.out.println(":::::::::::" + index);
                    index += frameSize;
                }
            }
// TODO:            System.out.println("IND:\t" + index);

            for (int ch = 0; ch < vals.length; ch++) {
// TODO:                System.out.println("VAL:\t" + vals[ch]);
                convertIntToByteArr(sampleBytes, vals[ch], isBigEndian);
                for(int j = 0; j < sampleBytes.length; j++, resInd++) {
// TODO:                   System.out.println("VALBYTE:\t" + sampleBytes[j]);
                    retArr[resInd] = sampleBytes[j];
                }
            }
        }

        // Now we just perform do filtering for the rest, we don't need to check for the preceding elements anymore.
        // TODO:        System.out.println("------------------------------------------");

        for(; resInd < retArr.length; startingCoefInd += frameSize) {
            for(int ch = 0; ch < vals.length; ch++) {
                vals[ch] = 0;
            }
            index = startingCoefInd;
// TODO:            System.out.println(resInd + ":" + index + "\t:\t" + retArr.length + ":" + samples.length + ":\t" + numberOfChannels + ":\t" + sampleSize);
            for(int j = 0; j < coef.length; j++) {
                for (int ch = 0; ch < vals.length; ch++, index += sampleSize) {
                    sample = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
                    vals[ch] += coef[j] * sample;
//TODO:                    System.out.println("SAMPLE:\t" + sample + "\t:\tMULTSAMPLE:\t" + vals[ch]);
                }
            }

            // TODO: the same for cycle as above (30 lines above)
            for (int ch = 0; ch < vals.length; ch++) {
                convertIntToByteArr(sampleBytes, vals[ch], isBigEndian);
//TODO:                System.out.println("VAL:\t" + vals[ch]);
                for(int j = 0; j < sampleBytes.length; j++, resInd++) {
//TODO:                    System.out.println("VALBYTE:\t" + sampleBytes[j]);
                    retArr[resInd] = sampleBytes[j];
                }
            }
        }

        return retArr;

//        byte[] retArr = new byte[samples.length];
//        byte val;
//        int index;
//        int startingCoefInd;
//
//        // Filter for the first indexes is a bit different, since they dont have all the preceding samples for the filtering.
//        // It's for optimization because we need to check if there are the preceding samples.
//        startingCoefInd = -coef.length + 1;		// +1 because the current sample can be used (Simple check of correctness is if we had just 1 coef)
//        int i;
//        for(i = 0; i < coef.length; i++, startingCoefInd++) {
//            val = 0;
//            index = startingCoefInd;
//            for(int j = 0; j < coef.length; j++, index++) {
//                if(index >= 0) {
//                    val += coef[j] * samples[index];
//                }
//            }
//
//            retArr[i] = val;
//        }
//
//        // Now we just perform do filtering for the rest, we don't need to check for the preceding elements anymore.
//        startingCoefInd = 0;
//        for(; i < samples.length; i++, startingCoefInd++) {
//            val = 0;
//            index = startingCoefInd;
//            for(int j = 0; j < coef.length; j++, index++) {
//                val += coef[j] * samples[index];
//            }
//
//            retArr[i] = val;
//        }
//
//        return retArr;
    }



//// TODO: !!!!!!!!!!!!!!!!!!!! Jen ted na rychlo double varianta bez testu
//    // TODO: !!! Not sure if I should work with ints or doubles when multiplying with coeffecients - for example
//    // for example in doubles 2/3 + 2/3 = 4/3 which will be converted to 1. But when working with ints it is 0+0=0
//    // TODO: Udelat reference variantu
//    /**
//     * Performs non-recursive filter, result is returned in new array. Non-recursive filter is this (y[n] is n-th output sample and x[n] is n-th input sample):
//     * <br>
//     * y[n] = coef[0] * x[n - coef.length + 1] + ... + coef[coef.length] x[n]
//     * @param samples is the input array. It isn't changed.
//     * @param coef are the coefficients for the input samples. The last index contains index for the currently computed output. The first index is the (coef.length+1)-th before the current sample.
//     * @param numberOfChannels represents the number of channels
//     * @param retArr is he array which will contain the result of filter.
//     * @return Returns -1 if the output array was shorter than length of coefs array else returns 1. In both cases the result of filter is in retArr.
//     */
//    // TODO: Returns -1 if the input array was too short ... the function doesn't do anything
//    public static int performNonRecursiveFilter(double[] samples, double[] coef, int numberOfChannels, double[] retArr) {
//        double[] vals = new double[numberOfChannels];
//        int index;
//        int startingCoefInd;
//
//        // Filter for the first indexes is a bit different, since they dont have all the preceding samples for the filtering.
//        // It's for optimization because we need to check if there are the preceding samples.
//        startingCoefInd = -coef.length * numberOfChannels + numberOfChannels;		// +1 because the current sample can be used (Simple check of correctness is if we had just 1 coef)
//        int resInd;
//        int coefInd;
//
//        if(numberOfChannels * coef.length >= retArr.length) {
//            return -1;
//        }
//
//        for(resInd = 0, coefInd = 0; coefInd < coef.length - 1; startingCoefInd += numberOfChannels, coefInd++) {
//            // Covers the case when there is more coefficients than frames,
//            // but sample.length is expected to be containing only full frames,
//            // that is samples.length % frameSize == 0
//            for(int ch = 0; ch < vals.length; ch++) {
//                vals[ch] = 0;
//            }
//            index = startingCoefInd;
//// TODO:            System.out.println(resInd + ":" + index + "\t:\t" + retArr.length + ":" + samples.length + ":\t" + numberOfChannels + ":\t" + sampleSize);
//            for (int j = 0; j < coef.length; j++) {
//                if (index >= 0) {
//                    for (int ch = 0; ch < vals.length; ch++, index++) {
//                        vals[ch] += coef[j] * samples[index];
//                        // TODO:                      System.out.println("SAMPLE:\t" + sample + "\t:\tMULTSAMPLE:\t" + vals[ch]);
//// TODO:                        System.out.println(index);
//                    }
//                }
//                else {
//// TODO:                    System.out.println(":::::::::::" + index);
//                    index += numberOfChannels;
//                }
//            }
//// TODO:            System.out.println("IND:\t" + index);
//
//            for (int ch = 0; ch < vals.length; ch++, resInd++) {
//// TODO:                System.out.println("VAL:\t" + vals[ch]);
//                retArr[resInd] = vals[ch];
//            }
//        }
//
//        // Now we just perform do filtering for the rest, we don't need to check for the preceding elements anymore.
//        // TODO:        System.out.println("------------------------------------------");
//
//        for(; resInd < retArr.length; startingCoefInd += numberOfChannels) {
//            for(int ch = 0; ch < vals.length; ch++) {
//                vals[ch] = 0;
//            }
//            index = startingCoefInd;
//// TODO:            System.out.println(resInd + ":" + index + "\t:\t" + retArr.length + ":" + samples.length + ":\t" + numberOfChannels + ":\t" + sampleSize);
//            for(int j = 0; j < coef.length; j++) {
//                for (int ch = 0; ch < vals.length; ch++, index++) {
//                    vals[ch] += coef[j] * samples[index];
//                    // TODO: PROGRAMO
////                    ProgramTest.debugPrint("low-pass filter:", j, coef[j], samples[index], coef[j] * samples[index], vals[ch]);
////                    // https://stackoverflow.com/questions/16098046/how-do-i-print-a-double-value-without-scientific-notation-using-java
////                    System.out.printf("v1: %f\n", coef[j]);
////                    System.out.printf("v2: %f\n", samples[index]);
////                    System.out.printf("v3: %f\n", coef[j] * samples[index]);
////                    System.out.printf("v4: %f\n", vals[ch]);
////                    Tady totiz jsou 2 problemy - 1) je to celkem pomaly protoze to delam jakoby po 1 prvku
////                    to ale az tak nevadi
////                        2) co ale vadi je ze kdyz retArr == samples which is the input arr, then it rewrites underlying
////                        samples so the values become invalid, so I will have to have so buffer and then when I am with
////                        the index far enough I will copy it - the buffer can be the vals[ch] just make it [][] and I am set
////                        so I will solve both problems at once
////                        3) the result has samples larger than 1 - This is fine I can just call set to max after that, or just user let do what he wants -
////                    to se da vyresit tak ze znormalizuju ty coeficienty
//                    // TODO: PROGRAMO
//                }
//            }
//
//            // TODO: the same for cycle as above (30 lines above)
//            for (int ch = 0; ch < vals.length; ch++, resInd++) {
////TODO:                System.out.println("VAL:\t" + vals[ch]);
//                retArr[resInd] = vals[ch];
//            }
//        }
//
//        return 1;
//    }


    // TODO: !!!!!!!!!!!!!!!!!!!! Jen ted na rychlo double varianta bez testu
    // TODO: !!! Not sure if I should work with ints or doubles when multiplying with coeffecients - for example
    // for example in doubles 2/3 + 2/3 = 4/3 which will be converted to 1. But when working with ints it is 0+0=0
    /**
     * Performs non-recursive filter on input array, result is returned in output array (Input and output array can be the same).
     * Non-recursive filter is this (y[n] is n-th output sample and x[n] is n-th input sample):
     * <br>
     * y[n] = coef[0] * x[n - coef.length + 1] + ... + coef[coef.length] x[n]
     * @param samples is the input array. It isn't changed.
     * @param coef are the coefficients for the input samples. The last index contains index for the currently computed output. The first index is the (coef.length+1)-th before the current sample.
     * @param numberOfChannels represents the number of channels
     * @param retArr is he array which will contain the result of filter.
     * @param retArrStartIndex is the start index in the output array (retArr) - inclusive
     * @param retArrEndIndex is the end index in the output array (retArr) - exclusive
     * @return Returns -1 if the output array was shorter than length of coefs array else returns 1.
     * Returns -2 if the input array isn't long enough. If 1 is returned the result of filter is in retArr. Else the retArr isn't changed in any way.
     */
    // Implementation note: since retArrEndIndex is exclusive,
    // we need to use retArrEndIndex - 1 when we are referring to valid indices
    public static int performNonRecursiveFilter(double[] samples, int samplesStartIndex,
                                                double[] coef, int numberOfChannels,
                                                double[] retArr, int retArrStartIndex, final int retArrEndIndex) {
        int bufferLen = 4096;
        bufferLen = Math.max(bufferLen, Utilities.getFirstPowerOfNAfterNumber(coef.length, 2));
        int indexToStopCopyFrom = bufferLen;
        int indexCountToWaitWithForNextIteration = coef.length - 1;
        bufferLen += indexCountToWaitWithForNextIteration;

        double[][] vals = new double[numberOfChannels][bufferLen];
        int index;
        int startingCoefInd;

        // Filter for the first indexes is a bit different, since they dont have all the preceding samples for the filtering.
        // It's for optimization because we need to check if there are the preceding samples.
        startingCoefInd = samplesStartIndex + -indexCountToWaitWithForNextIteration * numberOfChannels;        // +1 because the current sample can be used (Simple check of correctness is if we had just 1 coef)
        int resInd = retArrStartIndex;

        if (retArrStartIndex + numberOfChannels * coef.length >= retArrEndIndex) {
            return -1;
        }
        if (samplesStartIndex + numberOfChannels * coef.length >= samples.length ||
            retArrEndIndex - retArrStartIndex > samples.length - samplesStartIndex) {
            return -2;
        }

        Utilities.resetTwoDimArr(vals, 0, vals[0].length);
        for (int i = 0, coefInd = 0; i < vals[0].length;
             i++, startingCoefInd += numberOfChannels, coefInd++) {
            index = startingCoefInd;
            if (coefInd >= indexCountToWaitWithForNextIteration) {
                break;
            }
            for (int j = 0; j < coef.length; j++) {
                if (index >= 0) {
                    for (int ch = 0; ch < vals.length; ch++, index++) {
                        vals[ch][i] += coef[j] * samples[index];
                    }
                } else {
                    index += numberOfChannels;
                }
            }
        }
        // Now we just perform do filtering for the rest, we don't need to check for the preceding elements anymore.
        int firstInvalidIndexInChannel = -1;
        for ( ; resInd < retArrEndIndex - 1; ) {
            // This represents the current result index (where we are in the out array currently)
            int virtualResInd = resInd + indexCountToWaitWithForNextIteration * vals.length;

            for (int i = indexCountToWaitWithForNextIteration; i < vals[0].length;
                 i++, startingCoefInd += numberOfChannels, virtualResInd += numberOfChannels) {
                if (virtualResInd >= retArrEndIndex - 1) {
                    firstInvalidIndexInChannel = i;
                    break;
                }
                index = startingCoefInd;
                for (int j = 0; j < coef.length; j++) {
                    for (int ch = 0; ch < vals.length; ch++, index++) {
                        vals[ch][i] += coef[j] * samples[index];
                    }
                }
            }

            resInd = setRetArrInLowPassFilter(resInd, firstInvalidIndexInChannel, vals, indexToStopCopyFrom, retArr);
            for (int ch = 0; ch < vals.length; ch++) {
                System.arraycopy(vals[ch], indexToStopCopyFrom, vals[ch], 0, indexCountToWaitWithForNextIteration);
            }
            Utilities.resetTwoDimArr(vals, indexCountToWaitWithForNextIteration, vals[0].length);
        }

        return 1;
    }


    private static int setRetArrInLowPassFilter(int resInd, int firstInvalidIndexInChannel, double[][] vals,
                                                int endIndex, double[] retArr) {
        int startResInd = resInd;
        final int resIndAndMethodStart = resInd;
        int len;
        if(firstInvalidIndexInChannel < 0) {
            len = endIndex;
        }
        else {
            len = firstInvalidIndexInChannel;
        }
        for (int ch = 0; ch < vals.length; ch++, startResInd++, resInd = startResInd) {
            for (int i = 0; i < len; i++, resInd += vals.length) {
// TODO:                System.out.println("VAL:\t" + vals[ch]);
                retArr[resInd] = vals[ch][i];
            }
        }

        return resIndAndMethodStart + len * vals.length;
    }




    /**
     * Performs recursive filter, result is returned in new array. Recursive filter is this (y[n] is n-th output sample and x[n] is n-th input sample):
     * <br>
     * y[n] = coef[0] * x[n - coef.length + 1] + ... + coef[coef.length] x[n] + coefOutput[0] * y[n - coefOutput.length] + coefOutput[coefOutput.length] * y[n-1]
     * @param samples is the input array. It isn't changed.
     * @param coef are the coefficients for the input samples. The last index contains index for the currently computed output. The first index is the (coef.length+1)-th before the current sample.
     * @param coefOutput are the coefficients for the output samples. The last index contains index for the currently for the output before the currently calculated one. The first index is the coef.length -th before the current sample.
     * @return Returns new array gotten from input samples array by recursive filter.
     */
    public static byte[] performRecursiveFilter(byte[] samples, double[] coef, double[] coefOutput) {
        byte[] retArr = new byte[samples.length];
        byte val;
        int index;
        int startingCoefInd;
        int startingCoefOutputInd;
        int len = Math.max(coef.length, coefOutput.length);

        // Filter for the first indexes is a bit different, since they dont have all the preceding samples for the filtering.
        // It's for optimalization because we need to check if there are the preceding samples.
        startingCoefInd = -coef.length + 1;		// +1 because the current sample can be used (Simple check of correctness is if we had just 1 coef)
        startingCoefOutputInd = -coefOutput.length;
        int i;
        for(i = 0; i < len; i++, startingCoefInd++, startingCoefOutputInd++) {
            val = 0;
            index = startingCoefInd;
            for(int j = 0; j < coef.length; j++, index++) {
                if(index >= 0) {
                    val += coef[j] * samples[index];
                }
            }

            index = startingCoefOutputInd;
            for(int j = 0; j < coefOutput.length; j++, index++) {
                if(index >= 0) {
                    val += coefOutput[j] * retArr[index];
                }
            }

            retArr[i] = val;
        }

        // Now we just perform do filtering for the rest, we don't need to check for the preceding elements anymore.
        startingCoefInd = 0;
        for(; i < samples.length; i++, startingCoefInd++, startingCoefOutputInd++) {
            val = 0;
            index = startingCoefInd;
            for(int j = 0; j < coef.length; j++, index++) {
                val += coef[j] * samples[index];
            }

            index = startingCoefOutputInd;
            for(int j = 0; j < coefOutput.length; j++, index++) {
                if(index >= 0) {
                    val += coefOutput[j] * retArr[index];
                }
            }

            retArr[i] = val;
        }

        return retArr;
    }



/* TODO: // ono to je asi celkem zbytecny protoze samotnej filtr je by copy
	private static byte[] runLowPassFilterByCopy(byte[] samples, int cutoffFreq) {
		byte[] retArr = Arrays.copyOf(samples, samples.length);
		runLowPassFilterByRef(retArr, cutoffFreq);
		return retArr;
	}
*/

    /**
     * Performs low pass filtering with cutoffFreq on given samples, which are supposed to be sampled at sampleRate.
     * @param samples are the samples to perform the low pass filter on.
     * @param cutoffFreq is the cut-off frequency of the filter.
     * @param coefCount is the number of the coefficients used for filtering (How many last samples should be used for calculating the current one in the filter). Usually the more the better filter.
     * @param sampleRate is the sampling rate of the given samples
     * @param numberOfChannels represents the number of channels
     * @param sampleSize is the size of 1 sample
     * @param frameSize is the size of 1 frame
     * @param isBigEndian true if the samples are in big endian, false otherwise.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @return Returns copy of the samples array on which was performed low pass filter.
     * @throws IOException is thrown by method calculateMask if the sampleSize is invalid.
     */
    @Deprecated
    public static byte[] runLowPassFilter(byte[] samples, double cutoffFreq, int coefCount, int sampleRate,
                                          int numberOfChannels, int sampleSize, int frameSize,
                                          boolean isBigEndian, boolean isSigned) throws IOException {
        double[] coef = calculateCoefForLowPass(cutoffFreq, coefCount, sampleRate);
        return performNonRecursiveFilter(samples, coef, numberOfChannels, sampleSize, frameSize, isBigEndian, isSigned);
    }


    public static int runLowPassFilter(double[] samples, int samplesStartIndex,
                                       int numberOfChannels, int sampleRate,
                                       double cutoffFreq, int coefCount,
                                       double[] retArr, int retArrStartIndex, int retArrEndIndex) {
        double[] coef = calculateCoefForLowPass(cutoffFreq, coefCount, sampleRate);
        int retVal = performNonRecursiveFilter(samples, samplesStartIndex,
            coef, numberOfChannels, retArr, retArrStartIndex, retArrEndIndex);
        return retVal;
    }


    /**
     * Caculates coefficients for low pass filter and returns them in array.
     * @param cutOffFreq is the cut-off frequency of the filter
     * @param coefCount is the number of the coefficients which will be returned
     * @param sampleRate is the sampling rate of the samples on which will be used filtering.
     * @return Returns double array containing the coefficients for non-recursive low pass filtering.
     */
    public static double[] calculateCoefForLowPass(double cutOffFreq, int coefCount, int sampleRate) {
        double[] coefForCalc = new double[coefCount];
        double[] coef = new double[coefCount];
        int jump = sampleRate / coefCount;
        int currFreq = 0;

        // Calculate values which are used for calculating the coefficients for filters
        int index = 0;
        while(currFreq < cutOffFreq) {
            coefForCalc[index] = 1;
            currFreq += jump;
            index++;
        }
        if(index < coefForCalc.length) {   // So the jump in filter isn't abrupt (Page 208 Dodge - Computer music)
            coefForCalc[index] = 1 / (double)2;
            index++;
        }
        for(; index < coefForCalc.length; index++) {
            coefForCalc[index] = 0;
        }

        // Calculate coefs for filter
        for(int k = 0; k < (coefCount - 1) / (double)2; k++) {      // From Page 206 Dodge
            double currCoef = 0;
            for(int i = 1; i < (coefCount - 1) / (double)2; i++) {
                double tmp = (2 * Math.PI * i / coefCount) * (k - (coefCount - 1) / (double)2);
                currCoef += Math.abs(coefForCalc[i]) * Math.cos(tmp);
            }
            coef[k] =  coefForCalc[0] + 2 * currCoef;
            coef[k] /= coefCount;
        }

        // The rest is symmetric
        for(int k = 0; k < (coefCount - 1) / (double)2; k++) {
            coef[coefCount - k - 1] = coef[k];
        }

        // Normalize the coefficients
        double sum = 0;
        for(int i = 0; i < coef.length; i++) {
            sum += coef[i];
        }
        if(sum > 1) {
            for (int i = 0; i < coef.length; i++) {
                coef[i] /= sum;
            }
        }
        // Now we reverse the array
        Utilities.reverseArr(coef);
        return coef;
    }


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

        final int maxAbsValSigned = getMaxAbsoluteValueSigned(8 * sampleSize);     // TODO: Signed and unsigned variant

        int beatCount = 0;
        int sampleIndex = 0;
        int i;
        int windowSizeInBytes = windowSize * frameSize;
        int nextSampleIndex = windowSizeInBytes;
        double energySum = 0;
        double energyAvg;
        for(i = 0; i < windows.length; i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
            if(nextSampleIndex < samples.length) {
                windows[i] = getEnergy(samples, windowSize, numberOfChannels, sampleSize, sampleIndex, mask,
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
            currEnergy = getEnergy(samples, windowSize, numberOfChannels, sampleSize, sampleIndex, mask,
                    isBigEndian, isSigned, maxAbsValSigned);
            variance = getVariance(energyAvg, windows);
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

         int bpm = convertBPM(beatCount, samples.length, sampleSize, numberOfChannels, sampleRate);

        // TODO: DEBUG
//         MyLogger.log("END OF BPM SIMPLE:\t" + minCoef + "\t" + maxCoef + "\t" + maxEnergy + "\t" + maxVariance, 0);
        ProgramTest.debugPrint("END OF BPM SIMPLE:", minCoef, maxCoef, maxEnergy, maxVariance);
         // TODO: DEBUG
         return bpm;
     }

     public static int convertBPM(int beats, int sampleCount, int sampleSize, int numberOfChannels, int sampleRate) {
         int sizeOfOneSecond = sampleSize * numberOfChannels * sampleRate;
         int bpm = (int) (beats / ((double)sampleCount / (60 * sizeOfOneSecond)));
         return bpm;
     }




    private static double getEnergy(byte[] samples, int windowSize, int numberOfChannels, int sampleSize,
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
     private static double getVariance(double average, double[] values) {
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
                getSubbandEnergiesUsingFFT(samples, subbandEnergies[i], sampleIndex,//int startIndex,
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
            getSubbandEnergiesUsingFFT(samples, currEnergies, sampleIndex,//int startIndex,
                numberOfChannels, sampleSize, frameSize, mask, fft, fftArr, measuresArr,
                maxAbsoluteValue, isBigEndian, isSigned, splitter);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy
            //            currEnergies = getSubbandEnergiesUsingFFT(...);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy

            // This is version for Constant splitter The commented coef = 2.5 ... is for logaritmic, but the version with constant seems to work very good
            int j = 0;
            for(; j < currEnergies.length; j++) {
                todoMaxEnergy = Math.max(currEnergies[j], todoMaxEnergy);       // TODO: Finding the difference in coefs

                avg = energySums[j] / historySubbandsCount; // TODO:
                double variance = getVariance(avg, subbandEnergies, j);
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
        int bpm = convertBPM(beatCount, samples.length, sampleSize, numberOfChannels, sampleRate);
        return bpm;
    }

    private static double getVariance(double average, double[][] values, int subbandIndex) {
        double variance = 0;
        double val;
        for(int i = 0; i < values.length; i++) {
            val = values[i][subbandIndex] - average;
            variance += val*val;
        }

        return variance / values.length;
    }


    public static void getSubbandEnergiesUsingFFT(byte[] samples, double[] currEnergies,
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



    ////////////////////////////////////////////////////
    // BPM Algorithms 3 - are implementing getBPMUsingCombFilterIFace
    ////////////////////////////////////////////////////


    // TODO: Remove ... nepotrebuju measury
    // TODO: Tyhle pole si chci urcite uchovat abych je nemusel pro ten comb filter delat pokazdy znova
    // TODO: We can do small trick for better memory managment, 1 sample == 1 byte, and the non-zero value would be
    // TODO: max value of size sampleSize
    // TODO: Returns THE FFT RESULTS!!!
    public static double[][][] getBPMArraysFFTMeasures(int lowerBoundBPM, int upperBoundBPM, int jumpBPM, int sampleRate,
                                                       double numberOfSeconds, int fftWindowSize, int numberOfBeats) {
        if(upperBoundBPM < lowerBoundBPM) {
            return null;
        }
        int arrayCount = 1 + (upperBoundBPM - lowerBoundBPM) / jumpBPM;
        int arrayLen = (int)(sampleRate * numberOfSeconds);
        DoubleFFT_1D fft = new DoubleFFT_1D(fftWindowSize);
        int fftWindowsCount = arrayLen / fftWindowSize;     // TODO: Maybe solve special case when fftWindowsCount == 0
        double[][][] bpmFFTArrays = new double[arrayCount][fftWindowsCount][];
        double[] fftArr = new double[fftWindowSize];

        int impulsePeriod;
        for(int i = 0, currBPM = lowerBoundBPM; i < bpmFFTArrays.length; i++, currBPM += jumpBPM) {
            int totalIndexInBpm = 0;
            impulsePeriod = (60 * sampleRate) / currBPM;
            int beatCount = 0;
            for(int j = 0; j < fftWindowsCount; j++) {
                for (int k = 0; k < fftArr.length; k++, totalIndexInBpm++) {
                    if(beatCount < numberOfBeats) {
                        if ((totalIndexInBpm % impulsePeriod) == 0) {
                            fftArr[k] = 1;
                            beatCount++;
                        }
                        else {
                            fftArr[k] = 0;
                        }
                    }
                    else {
                        fftArr[k] = 0;
                    }
                }

// TODO:                System.out.println(currBPM + "\tImpulsePeriod:\t" + impulsePeriod);
                fft.realForward(fftArr);
                bpmFFTArrays[i][j] = FFT.convertResultsOfFFTToRealRealForward(fftArr);
            }
        }

        return bpmFFTArrays;
    }

    // TODO: Tyhle pole si chci urcite uchovat abych je nemusel pro ten comb filter delat pokazdy znova
    // TODO: We can do small trick for better memory managment, 1 sample == 1 byte, and the non-zero value would be
    // TODO: max value of size sampleSize
    // TODO: Returns THE FFT RESULTS!!!
    public static double[][][] getBPMArraysFFT(int lowerBoundBPM, int upperBoundBPM, int jumpBPM, int sampleRate,
                                               double numberOfSeconds, int fftWindowSize, int numberOfBeats) {      // TODO: Maybe later pass the fft with the length, so it doesn't have to allocated over and over again ... but it's only smal optimazation
        if(upperBoundBPM < lowerBoundBPM) {
            return null;
        }


        int arrayCount = 1 + (upperBoundBPM - lowerBoundBPM) / jumpBPM;
        int arrayLen = (int)(sampleRate * numberOfSeconds);
        DoubleFFT_1D fft = new DoubleFFT_1D(fftWindowSize);
        int fftWindowsCount = arrayLen / fftWindowSize;     // TODO: Maybe solve special case when fftWindowsCount == 0
        double[][][] bpmFFTArrays = new double[arrayCount][fftWindowsCount][];

        int impulsePeriod;
        for(int i = 0, currBPM = lowerBoundBPM; i < bpmFFTArrays.length; i++, currBPM += jumpBPM) {
            int totalIndexInBpm = 0;
            impulsePeriod = (60 * sampleRate) / currBPM;
            int beatCount = 0;
            for(int j = 0; j < fftWindowsCount; j++) {
                double[] fftArr = new double[fftWindowSize];
                // TODO: Koment po dlouhy dobe - nechapu proc to nenastavuju na 1 pres modulo
                for (int k = 0; k < fftArr.length; k++, totalIndexInBpm++) {
                    int mod = totalIndexInBpm % impulsePeriod;

                    if(beatCount < numberOfBeats) {
                        if (mod == 0) { // TODO: !!!!!!!!! Kdyz ted vim ze se nedela FFT z tech kousku ale z celyho tak muzu nastavit proste kazdej sample na nasobku impulsePeriod na 1
                            fftArr[k] = 1;
                            beatCount++;
// TODO:                            System.out.println("bpmArrs:\t" + beatCount + "\t" + totalIndexInBpm + "\t" + currBPM);
                        }
                    }
                    else {
// TODO:                        System.out.println("break");
                        break;
                    }

/*

                    // TODO: !!!!!!!!!!!!!!!!!!!!!!!!!

                    int sizeOfPeak = 0;      // TODO: !!!!!!!!!!!!!!!!!!!!!!!!!
                    if (mod <= sizeOfPeak || mod > impulsePeriod - sizeOfPeak) {
                        fftArr[k] = 1;
                    }
*/
                }

//                System.out.println(currBPM + "\tImpulsePeriod:\t" + impulsePeriod);
                fft.realForward(fftArr);

//                fftArr = convertResultsOfFFTToRealRealForward(fftArr);  // TODO:

                bpmFFTArrays[i][j] = fftArr;
// TODO: Vymazat
/*
if(currBPM == 60) {
    for (int l = 0; l < fftArr.length; l++) {
        System.out.println(fftArr[l]);
    }
}
*/
            }
        }

        return bpmFFTArrays;
    }




    // TODO: Melo by to byt double[][] u obou? u toho bpmArray to zavisi na velikosti okna ... kdyz je to mocnina 2ky tak pak jsou vsechny ty pole stejny
    // TODO: ... ale tak to furt muzu mit double[][] akorat si budou odkazovat na stejny pole ... takze skoro zadarmo jen par referenci me to bude stat
    public static double getCombFilterEnergyRealForward(double[][] fftResults, double[][] bpmArray) {
        double energy = 0;

// TODO: Jen debug        if (fftResults.length != 1 || bpmArray.length != 1) System.exit(-10);       // TODO:


        for(int i = 0; i < fftResults.length; i++) {
            energy += getCombFilterEnergyRealForward(fftResults[i], bpmArray[i]);
        }

        return energy;
    }


    // TODO:
    public static void getCombFilterEnergies(double[] fftResult, double[][][] bpmArray, double[] energies) {
        for (int i = 0; i < bpmArray.length; i++) {
            for(int j = 0; j < bpmArray[i].length; j++) {
               energies[i] += getCombFilterEnergyRealForward(fftResult, bpmArray[i][j]);
            }
        }
    }


    // TODO: Ted nevim jestli to ma byt stejne jako u getCombFilterEnergyRealForward ... kde pocitam measury misto toho co delam tady (tj nejdriv to vynasobim a pak z vysledku vezmu measury)
    public static double getCombFilterEnergyRealForwardFull(double[] fftResult, double[] bpmArray) {      // TODO: "Stereo" verze
        double real;
        double imag;
        double energy = 0;

        for(int i = 0; i < fftResult.length; i = i + 2) {
            real = fftResult[i] * bpmArray[i] - fftResult[i+1] * bpmArray[i+1];
            real *= real;
            imag = fftResult[i] * bpmArray[i+1] + fftResult[i+1] * bpmArray[i];
            imag *= imag;
            energy += real + imag;
        }

        return energy;
    }

//     From documentation:
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
    // TODO: Tohle je skoro konvoluce, akorat vysledky neukladame do pole ktere bude obsahovat vysledek konvoluce ale pocitame rovnou energii
    // TODO: A energii pocitame tak ze bereme vysledky konvoluce na druhou (realnou a imaginarni slozku zvlast) (protoze pocitame absolutni hodnotu)
    public static double getCombFilterEnergyRealForward(double[] fftResult, double[] bpmArray) {      // TODO: Monoverze
        double energy;              // TODO: mozna takhle prepsat i ten prevod na realny ... je to prehlednejsi
        double real;                // TODO: Ten prevod na realny mozna ani nebude dobre
        double imag;
        if(fftResult.length % 2 == 0) {			// It's even
            real = fftResult[0] * bpmArray[0];
            energy = FFT.calculateComplexNumMeasure(real, 0);
            real = fftResult[1] * bpmArray[1];      // TODO: Prehozeny poradi bylo to zatim for cyklem ... v te convertImagToReal to delat nemusim protoze tam to prevadim do pole polovicni velikosti
            energy += FFT.calculateComplexNumMeasure(real, 0);
            for(int i = 2; i < fftResult.length; i = i + 2) {
                real = fftResult[i] * bpmArray[i] - fftResult[i+1] * bpmArray[i+1];
                imag = fftResult[i] * bpmArray[i+1] + fftResult[i+1] * bpmArray[i];
                energy += FFT.calculateComplexNumMeasure(real, imag);
            }
        } else {
            real = fftResult[0] * bpmArray[0];
            energy = FFT.calculateComplexNumMeasure(real, 0);
            for(int i = 2; i < fftResult.length - 1; i = i + 2) {
                real = fftResult[i] * bpmArray[i] - fftResult[i+1] * bpmArray[i+1];
                imag = fftResult[i] * bpmArray[i+1] + fftResult[i+1] * bpmArray[i];
                energy += FFT.calculateComplexNumMeasure(real, imag);
            }

            real =  fftResult[fftResult.length - 1] * bpmArray[fftResult.length - 1] - fftResult[1] * bpmArray[1];
            imag = fftResult[fftResult.length - 1] * bpmArray[1] + fftResult[1] * bpmArray[fftResult.length - 1];
            energy += FFT.calculateComplexNumMeasure(real, imag);
        }

        return energy;
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






    public static double[][] getIFFTBasedOnSubbands(double[] fftResult, int subbandCount, DoubleFFT_1D fft,
                                                    SubbandSplitterIFace splitter) {
        double[][] result = new double[subbandCount][fftResult.length];
        getIFFTBasedOnSubbands(fftResult, subbandCount, fft, splitter, result);
        return result;
    }
    public static void getIFFTBasedOnSubbands(double[] fftResult, int subbandCount, DoubleFFT_1D fft,
                                             SubbandSplitterIFace splitter, double[][] result) {
        for(int subband = 0; subband < subbandCount; subband++) {
            splitter.getSubband(fftResult, subbandCount, subband, result[subband]);

//            // TODO:
//            System.out.println("\n\n\n\n" + subband);
//            for(int i = 0; i < result[subband].length; i++) {
//                System.out.println(i + "\t" + result[subband][i]);
//            }

            FFT.calculateIFFTRealForward(result[subband], fft, true);      // TODO: To skalovani nevim
            // TODO: Tady bych mel volat tu metodu podtim asi
        }
    }

    public static void getIFFTBasedOnSubband(double[] fftResult, int subbandCount, int subband, DoubleFFT_1D fft,
                                             SubbandSplitterIFace splitter, double[] result) {
        splitter.getSubband(fftResult, subbandCount, subband, result);
        FFT.calculateIFFTRealForward(result, fft, true);     // TODO: To skalovani ... asi se ma davat true, ale nevim proc ... no vzdycky to muze prevadet jako parametr
    }

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




    public static double getFreqJump(int sampleRate, double windowSize) {
        double result = sampleRate / (double) windowSize;
        return result;
    }



    ////////////////////////////////////////////////////////////////////////////////////
    ///////////////////// Audio visualization
    ////////////////////////////////////////////////////////////////////////////////////
    // Very important information - The maximum energy of FFT bin is equal to window size and it is in case, when
    // there are only ones in window. !!! But this only applies to case where the input double values are normalized between -1 and 1.


    public static int drawStringWithSpace(Graphics g, Color color, String s, int currX, int binWidth, int y) {
        FontMetrics fontMetrics = g.getFontMetrics();
        g.setColor(color);
        int textLen = fontMetrics.stringWidth(s);
        int textStart = (binWidth - textLen) / 2;
        int stringStartX = currX + textStart;
        g.drawString(s, stringStartX, y);

        return stringStartX;

// TODO: Vymazat, jen DEBUG testovani neceho
//        int x = stringStartX + textLen / 2;
//        g.drawLine(x, 0, x, 400);
//
//        g.setColor(Color.red);
//        x = currX + binWidth / 2;
//        g.drawLine(x, 0, x, 400);
    }

    public static void setLabelLocWithSpace(JLabel label, int startX, int binWidth, int y) {
        int textLen = label.getWidth();
        int textStart = (binWidth - textLen) / 2;
        int labetStartX = startX + textStart;
        label.setLocation(labetStartX, y);
    }

    public static void setLabelLocWithSpace(JLabel label, int startX, int startY, int binWidth, int binHeight) {
        FontMetrics fm = label.getFontMetrics(label.getFont());
        int textLen =  fm.stringWidth(label.getText());
        int textStartX = (binWidth - textLen) / 2;
        int x = startX + textStartX;

        int textHeight = fm.getHeight();
        int textStartY = (binHeight - textHeight) / 2;
        int y = startY + textStartY;

        label.setLocation(x, y);
    }


    public static void findBiggestFontToFitSize(JLabel label, int maxWidth, int maxHeight) {
        Font oldFont = label.getFont();
        int currFontSize = oldFont.getSize();
        FontMetrics fm = label.getFontMetrics(oldFont);

        Font newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
        int textWidth = fm.stringWidth(label.getText());
        if(textWidth < maxWidth && textWidth >= 0) {
            while(currFontSize < DiasynthTabbedPanel.MAX_LABEL_FONT_SIZE) {
                // TODO: DEBUG
                //ProgramTest.debugPrint("Font:", newFont, "w and h", maxWidth, maxHeight);
                // TODO: DEBUG
                currFontSize++;
                newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
                fm = label.getFontMetrics(newFont);
                textWidth = fm.stringWidth(label.getText());
                if(textWidth > maxWidth) {
                    currFontSize = Math.max(1, currFontSize - 1);
                    newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
                    fm = label.getFontMetrics(newFont);
                    label.setFont(newFont);
                    if(fm.getHeight() > maxHeight) {
                        findBiggestFontToFitMaxHeight(label, maxHeight);
                    }
                    return;
                }
                else if(textWidth < 0) {
                    break;
                }
            }

            // If we get here then the maximum label size was reached
            newFont = new Font(oldFont.getName(), oldFont.getStyle(), DiasynthTabbedPanel.MAX_LABEL_FONT_SIZE);
            label.setFont(newFont);
            findBiggestFontToFitMaxHeight(label, maxHeight);
        }
        else if (textWidth > maxWidth) {
            while(currFontSize > 1) {
                // TODO: DEBUG
                //ProgramTest.debugPrint("Font:", newFont, "w and h", maxWidth, maxHeight);
                // TODO: DEBUG
                currFontSize--;
                newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
                fm = label.getFontMetrics(newFont);
                if(fm.stringWidth(label.getText()) < maxWidth) {
                    break;
                }
            }

            label.setFont(newFont);
            if(fm.getHeight() > maxHeight) {
                findBiggestFontToFitMaxHeight(label, maxHeight);
            }
            return;
        }
    }

    public static int findMaxFontSize(JLabel label) {
        // On My system the result is java.awt.Font[family=Dialog,name=Dialog,style=bold,size=26822]
        Font oldFont = label.getFont();
        int currFontSize = 0;
        FontMetrics fm ;
        Font newFont;
        while(true) {
            currFontSize++;
            newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
            fm = label.getFontMetrics(newFont);
            // TODO: DEBUG
            //ProgramTest.debugPrint("Font:", newFont, "stringWidth", fm.stringWidth(label.getText()));
            // TODO: DEBUG
            if(fm.stringWidth(label.getText()) < 0) {
                return currFontSize - 1;
            }
        }
    }


    public static void findBiggestFontToFitMaxHeight(JLabel label, int maxHeight) {
        Font newFont = label.getFont();
        int currFontSize = newFont.getSize();
        FontMetrics fm = label.getFontMetrics(newFont);
        while(fm.getHeight() >= maxHeight) {
            currFontSize--;
            newFont = new Font(newFont.getName(), newFont.getStyle(), currFontSize);
            fm = label.getFontMetrics(newFont);
        }

        label.setFont(newFont);
    }




    public static void drawStringWithDefinedMidLoc(Graphics g, Color color, String s, int mid, int y) {
        FontMetrics fontMetrics = g.getFontMetrics();
        g.setColor(color);
        int textLen = fontMetrics.stringWidth(s) - 1;       // -1 because it pushes more to the middle
        int stringStartX = mid - textLen / 2;
        g.drawString(s, stringStartX, y);
    }

    public static void drawLabelWithDefinedMidLoc(Graphics g, JLabel label, int mid, int y) {

        int textLen = label.getWidth() - 1;       // -1 because it pushes more to the middle
        int labetStartX = mid - textLen / 2;
        label.setLocation(labetStartX, y);
    }


    public static void setFontSize(JLabel label, int oldWidth, int newWidth) {
        float ratio = newWidth / (float)oldWidth;
        // TODO: DEBUG
        //ProgramTest.debugPrint("Old font:", label.getFont());
        // TODO: DEBUG
        Font oldFont = label.getFont();
        Font newFont = oldFont.deriveFont(ratio * oldFont.getSize2D());
        label.setFont(newFont);
        // TODO: DEBUG
        //ProgramTest.debugPrint("New font:", label.getFont());
        // TODO: DEBUG
    }





    // Get frequencies in khz
    public static String[] getFreqs(int binCount, double freqJump, double startFreq, int takeEveryNthFreq, int precision) {
        int len = 1 + (binCount - 1) / takeEveryNthFreq;  // -1 Because for example for binCount = takeEveryNthFreq = 4 I'd have 2 without the -1
        String[] binFreqs = new String[len];
        double currFreqHz = startFreq;
        for(int i = 0; i < binFreqs.length; i++, currFreqHz += freqJump * takeEveryNthFreq) {
            double currFreqKhz = currFreqHz / 1000;
            String freqString = String.format("%." + precision +"f", currFreqKhz);
            binFreqs[i] = freqString;
        }

        return binFreqs;
    }


    public static int findMaxFontSize(int startFontSize, Graphics g, String[] texts, int maxWidth, int maxHeight, int checkNthIndexes) {
        int fontSize = startFontSize;
        FontMetrics fontMetrics;
        for(int i = 0; fontSize > 0; fontSize--) {
            g.setFont(new Font("Serif", Font.BOLD, fontSize));
            fontMetrics = g.getFontMetrics();
            for(; i < texts.length; i++) {
                if(i % checkNthIndexes == 0) {
                    if (fontMetrics.stringWidth(texts[i]) > maxWidth || fontMetrics.getHeight() > maxHeight) {
                        break;
                    }
                }
            }

            if(i >= texts.length) {
                break;
            }
        }

        return fontSize;
    }


    public static int convertFrameToSecs(int frame, int sizeOfOneSec) {
        return frame / sizeOfOneSec;
    }

    // TODO: Doesn't work for long audio files - 596 hours+ (more exactly 2 147 483secs / 60 / 60)
    public static int convertFrameToMillis(int frame, int sizeOfOneSec) {
        return (int)(1000 * (double)frame / sizeOfOneSec);
    }




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



    public static int calculateMaxWidth(char startChar, char endChar, FontMetrics fm) {
        int maxWidth = -1;
        char c = startChar;
        while(c <= endChar) {
            maxWidth = Math.max(maxWidth, fm.charWidth(c));
            c++;
        }

        return maxWidth;
    }

    public static int calculateMaxWidthDigit(FontMetrics fm) {
        return calculateMaxWidth('0', '9', fm);
    }

    public static int calculateMaxWidthAlphabetLowerCase(FontMetrics fm) {
        return calculateMaxWidth('a', 'z', fm);
    }
    public static int calculateMaxWidthAlphabetUpperCase(FontMetrics fm) {
        return calculateMaxWidth('A', 'Z', fm);
    }

    public static int calculateMaxWidthAlfanum(FontMetrics fm) {
        int maxWidth = -1;
        maxWidth = Math.max(maxWidth, calculateMaxWidthDigit(fm));
        maxWidth = Math.max(maxWidth, calculateMaxWidthAlphabetLowerCase(fm));
        maxWidth = Math.max(maxWidth, calculateMaxWidthAlphabetUpperCase(fm));
        return maxWidth;
    }


    public static Dimension calculateMaximizedFrameSize() {
        JFrame f = new JFrame();
        f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        f.setVisible(true);
        Dimension size = f.getSize();
        f.setVisible(false);
        f.dispose();
        return size;
    }
}


//Math.ceil(1) = 1
