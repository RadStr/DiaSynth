package test;

import util.audio.wave.ByteWave;
import deprecatedclasses.Spectrogram;
import util.Aggregation;
import util.Utilities;
import util.audio.*;
import analyzer.AnalyzerPanel;
import player.experimental.FFTWindowPanel;
import util.Pair;
import analyzer.bpm.SubbandSplitter;
import analyzer.bpm.SubbandSplitterIFace;
import analyzer.bpm.SubbandSplitterLinear;
import deprecatedclasses.SubbandSplitterLogarithmic;
import org.jtransforms.fft.DoubleFFT_1D;
import util.Time;
import util.audio.filter.NonRecursiveFilter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This class is used for testing, will be later transformed to unit tests
 */
public class ProgramTest {
    public static void main(String[] args) throws Exception {
        ProgramTest test = new ProgramTest();
        test.testAll();
    }


    // Modified code from stackoverflow, I can't find the source currently. Forgot to cite it like 1.5 years ago.
    // It was wrong anyways, the notes were off.
    public class Tone {
        public void main(String[] args) throws LineUnavailableException {
            Tone t = new Tone();
            final AudioFormat af =
                    new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, true);
            SourceDataLine line = AudioSystem.getSourceDataLine(af);
            line.open(af, Note.SAMPLE_RATE);
            line.start();
            for (Note n : Note.values()) {
                t.play(line, n, 500);
                t.play(line, Note.REST, 10);
            }
            line.drain();
            line.close();
        }

        private void play(SourceDataLine line, Note note, int ms) {
            ms = Math.min(ms, Note.SECONDS * 1000);
            // I added this to make the tones last longer by changing the SECONDS property
            ms = Note.SECONDS * 1000;
            int length = Note.SAMPLE_RATE * ms / 1000;
            byte[] audioData = note.data();
            for (int i = 0; i < audioData.length; i++) {
                System.out.println(audioData[i]);
            }
            int count = line.write(note.data(), 0, length);
        }
    }


    enum Note {
        REST, A4, A4$, B4, C4, C4$, D4, D4$, E4, F4, F4$, G4, G4$, A5;
        public static final int SAMPLE_RATE = 44100;
        public static final int SECONDS = 5;
        private byte[] sin = new byte[SECONDS * SAMPLE_RATE];

        Note() {
            int n = this.ordinal();
            if (n > 0) {
                double exp = ((double) n - 1) / 12d;
                double f = 440d * Math.pow(2d, exp);
                //f = 261;
                for (int i = 0; i < sin.length; i++) {
                    double period = (double) SAMPLE_RATE / f;
                    double angle = 2.0 * Math.PI * i / period;
                    sin[i] = (byte) (Math.sin(angle) * 127f);
                }
            }
        }

        public byte[] data() {
            return sin;
        }


        /**
         * Mono variant
         *
         * @param freq
         * @param sampleRate
         * @param lengthInSeconds
         * @return
         */
        public static byte[] generateFrequencyFreq(double freq, int sampleRate, int lengthInSeconds) {
            byte[] samples = new byte[lengthInSeconds * sampleRate];
            for (int i = 0; i < samples.length; i++) {
                double period = (double) sampleRate / freq;
                double angle = 2.0 * Math.PI * i / period;
                samples[i] = (byte) (Math.sin(angle) * 127f);
            }
            return samples;
        }

        /**
         * General variant
         *
         * @param freq
         * @param sampleRate
         * @param lengthInSeconds
         * @return
         */
        public static byte[] generateFrequencyFreq(double freq, int sampleRate, int lengthInSeconds,
                                                   int sampleSize, boolean isBigEndian, boolean isSigned) {
            int sampleSizeInBits = sampleSize * 8;
            int maxValue = (1 << (sampleSizeInBits - 1)) - 1;
            int sample;
            byte[] sampleBytes = new byte[sampleSize];
            int sampleCount = lengthInSeconds * sampleRate;
            byte[] samples = new byte[sampleCount * sampleSize];
            for (int i = 0, byteIndex = 0; i < sampleCount; i++) {
                double period = (double) sampleRate / freq;
                double angle = 2.0 * Math.PI * i / period;
                sample = (int) (Math.sin(angle) * maxValue);
                if (!isSigned) {
                    sample += maxValue;
                }

                AudioConverter.convertIntToByteArr(sampleBytes, sample, isBigEndian);
                for (int j = 0; j < sampleBytes.length; j++, byteIndex++) {
                    samples[byteIndex] = sampleBytes[j];
                }
            }
            return samples;
        }
    }


    /**
     * Runs all tests and writes results. Could have done it better by using JUnit or at least write the method names
     * using reflection, but I won't be rewriting it anymore since these tests are pretty much final. I don't think
     * I will need to do some extensive testing in future, because the things that will be done in future are pretty
     * hard to test.
     *
     * @throws Exception can be thrown
     */
    public void testAll() throws Exception {
        System.out.println("generateFrequencyFreqTest(): " + generateFrequencyFreqTest());
        System.out.println("testFFT(): " + testFFT(17000, 1, true, true));
        System.out.println("testFFT(): " + testFFT(17000, 3, true, true));
        System.out.println("testFFT(): " + testFFT(17000, 3, true, false));
        System.out.println("testFFT(): " + testFFT(17000, 2, true, true));
        System.out.println("testFFT(): " + testFFT(17000, 3, false, false));

        System.out.println("convertBytesToSamplesTest():\t" + convertBytesToSamplesTest());
        System.out.println("reverseArrTest():\t" + reverseArrTest());
        System.out.println("performAggregationTestAvg():\t" + performAggregationTestAvg());
        System.out.println("performAggregationTestRms():\t" + performAggregationTestRms());
        System.out.println("performAggregationTestMin():\t" + performAggregationTestMin());
        System.out.println("performAggregationTestMax():\t" + performAggregationTestMax());


        Random random = new Random();
        boolean isSigned = false;
        byte[] song = new byte[2000];
        for (int i = 0; i < song.length; i++) {
            song[i] = (byte) random.nextInt(256);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(song);
        ByteArrayInputStream bais2;
        int numberOfChannels = 2;
        int sampleSizeInBits = 16;
        float frameRate = 44100;

        System.out.println("separateChannelsOfSongTestBoth:\t" +
                           separateChannelsOfSongTestBoth(bais, song, numberOfChannels,
                                                          sampleSizeInBits / 8));

        bais = new ByteArrayInputStream(song);
        int length = (int) 77;
        int x = 33;
        int startFrame = 67;
        System.out.println("Testing getEveryXthTimePeriodWithLength:\t" +
                           getEveryXthTimePeriodWithLengthTestBoth(bais, song, length, x,
                                                                   sampleSizeInBits / 8 * numberOfChannels, startFrame));

        bais = new ByteArrayInputStream(song);
        int n1 = 32;
        int startSample = 68;
        System.out.println("takeEveryNthSampleOneChannel:\t" +
                           takeEveryNthSampleOneChannelTestBoth(bais, song, sampleSizeInBits / 8,
                                                                n1, startSample));

        bais = new ByteArrayInputStream(song);
        int n2 = 18;
        System.out.println("song length:\t" + song.length);
        int frameSize = numberOfChannels * (sampleSizeInBits / 8);
        System.out.println("takeEveryNthSampleMoreChannels:\t" +
                           takeEveryNthSampleMoreChannelsTestBoth(bais, song, numberOfChannels,
                                                                  sampleSizeInBits / 8, n2,
                                                                  Utilities.convertToMultipleDown(song.length / (2 * frameSize), frameSize)));

        bais = new ByteArrayInputStream(song);
        bais2 = new ByteArrayInputStream(song);
        boolean isBigEndian = false;
        ProgramTest.debugPrint("takeEveryNthSampleMoreChannelsDoubleTestCorrectnessOfFastVariant:",
                               takeEveryNthSampleMoreChannelsDoubleTestCorrectnessOfFastVariant(bais, bais2, song, numberOfChannels,
                                                                                                sampleSizeInBits / 8, n2,
                                                                                                7 * frameSize, isBigEndian,
                                                                                                isSigned, song.length));

        ProgramTest.debugPrint("takeEveryNthSampleMoreChannelsDoubleTestCorrectnessOfFastVariantTest2():",
                               takeEveryNthSampleMoreChannelsDoubleTestCorrectnessOfFastVariantTest2());

        bais = new ByteArrayInputStream(song);
        isBigEndian = false;
        System.out.println("performAggregationRMStestBoth with little endian:\t" +
                           performAggregationRMStestBoth(bais, song, numberOfChannels, sampleSizeInBits / 8,
                                                         isBigEndian, isSigned, song.length));

        bais = new ByteArrayInputStream(song);
        isBigEndian = true;
        System.out.println("performAggregationRMStestBoth with big endian:\t" +
                           performAggregationRMStestBoth(bais, song, numberOfChannels, sampleSizeInBits / 8,
                                                         isBigEndian, isSigned, song.length));


        convertBytesToNormalizedSamplesTests();
        convertToMonoTests();
        System.out.println("convertBytesToSamplesTest2():\t" + convertBytesToSamplesTest2());
        System.out.println("convertToMono1ByteSamples2ChannelsTest():\t" +
                           convertToMono1ByteSamples2ChannelsTest());
        convertBytesToSamplesTest3();
        System.out.println("performMovingWindowAverageByRefTest()\t" + performMovingWindowAverageByRefTest());
        convertSampleRateTests();
        System.out.println("performNonRecursiveFilterTest():\t" + performNonRecursiveFilterTest());
        System.out.println("performOneNonRecursiveFilterSmallTestDiffCoefs():\t" +
                           performOneNonRecursiveFilterSmallTestDiffCoefs());
        for (int k = 0; k < 10; k++) {
            System.out.println();
        }

        int arrLen = 256;
        int subbandCount = 32;
        testSubbandSplitterLinear(subbandCount, arrLen);
        for (int k = 0; k < 10; k++) {
            System.out.println();
        }
        testSubbandSplitterLogarithimic(subbandCount, arrLen);
        for (int k = 0; k < 10; k++) {
            System.out.println();
        }

        int sampleRate = 22050;
        arrLen = 256;
        subbandCount = 6;
        testSubbandSplitterConstant(sampleRate, subbandCount, arrLen);
        for (int k = 0; k < 10; k++) {
            System.out.println();
        }

// TODO: RML
        // Test FFT window visualisation 1
        sampleRate = 22050;
        int windowSize = 512;
        double freq = sampleRate / (double) windowSize;
        // Chooses the harmonics of fft if whole number, if not then the fft gets confused
        freq *= windowSize / 2 - 1.5;
        int sampleSize = 2;
        numberOfChannels = 1;
        isBigEndian = false;
        isSigned = true;
        int startIndex = 0;
        int windowWidth = 1600;
        int windowHeight = 900;
        boolean result;
        result = createFFTWindowTest(freq, sampleRate, sampleSize, numberOfChannels, isBigEndian, isSigned,
                                     startIndex, windowSize, windowWidth, windowHeight);
        System.out.println("createFFTWindowTest:\t" + result);
        for (int k = 0; k < 10; k++) {
            System.out.println();
        }
        // Test FFT window visualisation 2
        freq = sampleRate / (double) windowSize;

        // Chooses the harmonics of fft if whole number, if not then the fft gets confused
        freq *= windowSize / 2 - 1;

        result = createFFTWindowTest(freq, sampleRate, sampleSize, numberOfChannels, isBigEndian, isSigned,
                                     startIndex, windowSize, windowWidth, windowHeight);
        System.out.println("createFFTWindowTest:\t" + result);
        for (int k = 0; k < 10; k++) {
            System.out.println();
        }
// TODO: RML

// TODO: RML
//        // Spectrogram test 1
//        int lenInSecs = 50;
//        windowSize = 1024;windowSize = 200;                 // TODO:
//        int windowShift = windowSize;windowShift = 160;     // TODO:
//        freq = sampleRate / (double)windowSize;
//        freq *= windowSize / 4 - 1.5;       // TODO: pekne barevny
//        freq *= windowSize / 4;
//        startIndex = 0;
//        int endIndex = startIndex + windowSize * 100;// TODO: tohle musim jeste vyladit, kdyz neni ten endIndex startIndex + nasobek windowSize tak outoufbounds + sampleRate * lenInSecs;
//        int spectrogramWidth = 1400;
//        int spectrogramHeight = 900;
//      result =  createSpectrogramTest(lenInSecs, freq, sampleRate, numberOfChannels, sampleSize,
//        isBigEndian, isSigned, windowSize, windowShift,
//        startIndex, endIndex, spectrogramWidth, spectrogramHeight);
//        System.out.println("createSpectrogramTest:\t" + result);
//        for (int k = 0; k < 10; k++) {
//            System.out.println();
//        }
//
//
//        // Spectrogram test 2
//        String songPath = "C:\\Users\\Radek\\source\\SDL\\CppKlavesyZapProgram\\ruzneklavesy.wav";
//        windowSize = 256;
//        windowShift = windowSize;
//        windowShift = (int)(windowSize * 0.7);
//
//        windowSize = 200;         // TODO:
//        windowShift = 160;
//
//// TODO:        System.exit(windowShift);
//        startIndex = 44100 * 60;
//        endIndex = startIndex + windowSize * 700; // TODO: Nefunguje protoze to nedelim, staci to jen vydelit jednou na konci a mam hotovo a je to i efektivnejsi a presnejsi
//        spectrogramWidth = 1400;
////        spectrogramWidth = 3200;
//        spectrogramHeight = 900;
//        result = createSpectrogramTest(songPath, windowSize, windowShift, startIndex, endIndex, spectrogramWidth, spectrogramHeight);
//        System.out.println("createSpectrogramTest:\t" + result);
//        for (int k = 0; k < 10; k++) {
//            System.out.println();
//            System.out.println(spectrogramWidth + "\t" + spectrogramHeight);
//        }
//
//        // Long test
//        arrLen = 1 << 12;
//        System.out.println("windowsAverageVsNonRecursiveDoubleFilter:\t" + windowsAverageVsNonRecursiveDoubleFilter(arrLen));
//
//
//        testGetAbsoluteValueGeneral();
// TODO: RML
    }


    private static boolean generateFrequencyFreqTest() {
        int freq = 16352;
        int sampleRate = 20000;
        int lengthInSeconds = 10;
        byte[] samples1 = Note.generateFrequencyFreq(freq, sampleRate, lengthInSeconds);
        byte[] samples2 = Note.generateFrequencyFreq(freq, sampleRate, lengthInSeconds, 1, true, true);
        if (samples1.length != samples2.length) {
            return false;
        }

        for (int i = 0; i < samples1.length; i++) {
            if (samples1[i] != samples2[i]) {
                return false;
            }
        }
        return true;
    }


    public static boolean testFFT(int freq, int sampleSize, boolean isBigEndian, boolean isSigned) {
        // Test fft frequency
        int sampleSizeInBits = sampleSize * 8;
        int frameSize = sampleSize;
        int sampleRate = 44000;
        // however it is fixed by changing the size of one song part
        int tolerance;
        int sizeOfOneSongPart;
        for (int d = 0; d < 10; d++) {
            sizeOfOneSongPart = sampleRate;
            tolerance = sampleRate / sizeOfOneSongPart + 1;
            System.out.println("-:\t" + (freq - tolerance) + "\t+:\t" + (freq + tolerance));
            byte[] audioWithFreqfreq = Note.generateFrequencyFreq(freq, sampleRate, 5,
                                                                  sampleSize, isBigEndian, isSigned);
            InputStream is = new ByteArrayInputStream(audioWithFreqfreq);
            SongPartWithAverageValueOfSamples[] spwavos = new SongPartWithAverageValueOfSamples[0];
            try {
                spwavos = Aggregation.takeSongPartsAndAddAggregation(is, sizeOfOneSongPart, frameSize, isBigEndian,
                                                                     isSigned, sampleSize, false, Aggregation.AVG);
            }
            catch (IOException e) {
                System.out.println("FALSE: EXCEPTION\t");
                return false;
            }

            FrequencyWithMeasure[][] freqs = new FrequencyWithMeasure[0][];
            try {
                freqs = FFT.calculateFFTRealForward(spwavos, sampleSize, sampleRate, isBigEndian, isSigned);
            }
            catch (IOException e) {
                System.out.println("FALSE: EXCEPTION\t");
                return false;
            }


            FrequencyWithMeasure[][] freqsCalculatedStraigthFromBytes;
            try {
                freqsCalculatedStraigthFromBytes = FFT.calculateFFTRealForward(audioWithFreqfreq, sampleSize,
                                                                               sampleSizeInBits, sampleRate,
                                                                               sizeOfOneSongPart, isBigEndian,
                                                                               isSigned);
            }
            catch (IOException e) {
                System.out.println("FALSE: EXCEPTION");
                return false;
            }

            if (freqs.length != freqsCalculatedStraigthFromBytes.length) {
                return false;
            }
            for (int i = 0; i < freqs.length; i++) {
                for (int j = 0; j < freqs[i].length; j++) {
                    if (freqsCalculatedStraigthFromBytes[i].length != freqs[i].length) {
                        return false;
                    }
                    if (!freqsCalculatedStraigthFromBytes[i][j].equals(freqs[i][j])) {
                        System.out.println("Not same values: ");
                        return false;
                    }
                }
            }

            double[][] onlyMeasures;
            try {
                onlyMeasures = FFT.calculateFFTRealForwardOnlyMeasures(audioWithFreqfreq, sampleSize, sampleSizeInBits,
                                                                       sizeOfOneSongPart, 0,
                                                                       audioWithFreqfreq.length, isBigEndian, isSigned);
            }
            catch (IOException e) {
                System.out.println("FALSE: EXCEPTION");
                return false;
            }
            int previousFreq = 0;
            for (int i = 0; i < freqs.length; i++) {
                int topNMeasures = 3;

                FrequencyWithMeasure[] highestFrequencies = new FrequencyWithMeasure[0];
                FrequencyWithMeasure[] highestFrequencies2 = new FrequencyWithMeasure[0];
                double[] highestMeasures = new double[0];
                try {
                    highestMeasures = getNHighestMeasures(onlyMeasures[i], topNMeasures);
                    highestFrequencies = FFT.takeNFreqsWithHighestMeasure(freqs[i], topNMeasures, false);
                    highestFrequencies2 = FFT.takeNFreqsWithHighestMeasure(freqsCalculatedStraigthFromBytes[i],
                                                                           topNMeasures, false);
                }
                catch (IOException e) {
                    System.out.println("FALSE: EXCEPTION\t");
                    return false;
                }
                if (i != 0) {
                    if (previousFreq != highestFrequencies[0].frequency) {
                        System.out.println("FALSE: Frequncies aren't the same: old freq: " + previousFreq +
                                           ", new freq:" + highestFrequencies[0].frequency + "breaking");
                        return false;
                    }
                }
                previousFreq = highestFrequencies[0].frequency;
                if (highestFrequencies[0].frequency >= freq - tolerance &&
                    highestFrequencies[0].frequency <= freq + tolerance) {
                    if (i == 0) {
                        System.out.print("Frequencies are in tolerance:\t");
                        System.out.println("true");
                    }
                }
                else {
                    System.out.print("Frequencies are in tolerance:\t");
                    System.out.println("false");
                    System.out.println("original frequency: " + freq +
                                       "\tfrequency from fft: " + highestFrequencies[0].frequency);
                    return false;
                }


                for (int index = 0; index < highestFrequencies.length; index++) {
                    if (highestFrequencies[index].frequency != highestFrequencies2[index].frequency &&
                        highestFrequencies[index].measure != highestFrequencies2[index].measure) {
                        System.out.println("FALSE: HIGHEST FREQUENCIES AREN'T THE SAME: frequencies: " +
                                           highestFrequencies[index].frequency + ":" +
                                           highestFrequencies2[index].frequency +
                                           "\tmeasures: " + highestFrequencies[index].measure + ":" +
                                           highestFrequencies2[index].measure);
                        return false;
                    }

                    if (highestFrequencies[index].measure != highestMeasures[index]) {
                        System.out.println("FALSE: MEASURES IN ONLY MEASURES AND MEASURES WITH FREQS AREN'T THE SAME: " +
                                           highestFrequencies[index].measure + ":" + onlyMeasures[index]);
                        return false;
                    }
                }
            }
            freq = freq / 2;
            sampleRate = sampleRate / 2;
        }

        return true;
    }


    private static double[] getNHighestMeasures(double[] arr, int n) {
        if (n > arr.length) {
            return null;
        }
        double[] result = new double[n];
        Arrays.sort(arr);
        int index = arr.length - 1;
        for (int i = 0; i < result.length; i++, index--) {
            result[i] = arr[index];
        }

        return result;
    }


    /**
     * Method for testing getEveryXthTimePeriodWithLength by comparing the results of the 2 different implementations
     * (1 for input stream and 1 for byte array).
     *
     * @param audioStream is the input stream, which contains the same content as the byte array audio.
     * @param audio       is the byte array which contains the same content as the audioStream.
     * @param length      is the length for calling the methods.
     * @param x           is the x for calling the methods.
     * @param frameSize   is the frame size for calling the methods.
     * @param startFrame  is the starting frame for calling the methods.
     * @return Returns true if both methods gave the same output, else returns false.
     * @throws IOException is thrown when error with input stream occurred.
     */
    public boolean getEveryXthTimePeriodWithLengthTestBoth(InputStream audioStream, byte[] audio, int length, int x,
                                                           int frameSize, int startFrame) throws IOException {
        byte[][] result1 = AudioProcessor.getEveryXthTimePeriodWithLength(audio, length, x, frameSize, startFrame);
        byte[][] result2 = AudioProcessor.getEveryXthTimePeriodWithLength(audioStream, length, x, frameSize, startFrame);

        return checkEqualityOfArraysTwoDim(result1, result2);
    }


    /**
     * Method for testing takeEveryNthSampleOneChannel by comparing the results of the 2 different implementations
     * (1 for input stream and 1 for byte array).
     *
     * @param stream            is the input stream. Contains the same content as the samplesFromStream.
     * @param samplesFromStream is 1D byte array. Contains the same content as the stream.
     * @param sampleSize        is the sampleSize parameter for calling the methods.
     * @param n                 is the n parameter for calling the methods.
     * @param startSample       is the startSample parameter for calling the methods.
     * @return Returns true if both methods gave the same output, else returns false.
     * @throws IOException is thrown when error with input stream occurred.
     */
    public boolean takeEveryNthSampleOneChannelTestBoth(InputStream stream, byte[] samplesFromStream, int sampleSize,
                                                        int n, int startSample) throws IOException {
        byte[] samples1 = AudioProcessor.getEveryNthSampleMono(stream, sampleSize, n, startSample);
        byte[] samples2 = AudioProcessor.getEveryNthSampleMono(samplesFromStream, sampleSize, n, startSample);

        return checkEqualityOfArraysOneDim(samples1, samples2, 0);
    }


    public boolean takeEveryNthSampleMoreChannelsTestBoth(InputStream stream, byte[] samplesFromStream,
                                                          int numberOfChannels, int sampleSize,
                                                          int n, int startSample) throws IOException {
        // Passing length of array just for testing measures - else I would have to take the real length from the file
        byte[][] samples1 = AudioProcessor.getEveryNthSampleMoreChannels(stream, numberOfChannels, sampleSize,
                                                                         n, startSample, samplesFromStream.length);
        byte[][] samples2 = AudioProcessor.getEveryNthSampleMoreChannels(samplesFromStream, numberOfChannels,
                                                                         sampleSize, n, startSample);
        return checkEqualityOfArraysTwoDim(samples1, samples2);
    }


    public boolean takeEveryNthSampleMoreChannelsDoubleTestCorrectnessOfFastVariant(InputStream stream1,
                                                                                    InputStream stream2,
                                                                                    byte[] samplesFromStream,
                                                                                    int numberOfChannels,
                                                                                    int sampleSize, int n,
                                                                                    int startSample,
                                                                                    boolean isBigEndian,
                                                                                    boolean isSigned,
                                                                                    int totalAudioLen) throws IOException {
        double[][] samples1 = AudioProcessor.getEveryNthSampleMoreChannelsDoubleOldAndSlow(stream1, numberOfChannels,
                                                                                           sampleSize, n, startSample,
                                                                                           isBigEndian, isSigned,
                                                                                           totalAudioLen);
        double[][] samples2 = AudioProcessor.getEveryNthSampleMoreChannelsDouble(stream2, numberOfChannels, sampleSize,
                                                                                 n, startSample, isBigEndian, isSigned,
                                                                                 totalAudioLen);

        byte[][] samples3 = AudioProcessor.getEveryNthSampleMoreChannels(samplesFromStream, numberOfChannels,
                                                                         sampleSize, n, startSample);
        double[][] samples3Double = new double[samples3.length][];
        for (int i = 0; i < samples3Double.length; i++) {
            samples3Double[i] = AudioConverter.normalizeToDoubles(samples3[i], sampleSize, sampleSize * 8,
                                                                  isBigEndian, isSigned);
        }

        boolean result1 = checkEqualityOfArraysTwoDim(samples1, samples3Double);
        boolean result2 = checkEqualityOfArraysTwoDim(samples2, samples3Double);
        ProgramTest.debugPrint("Checking against byte variant:", result1, result2);
        return checkEqualityOfArraysTwoDim(samples1, samples2);
    }

    public boolean takeEveryNthSampleMoreChannelsDoubleTestCorrectnessOfFastVariantTest2() throws IOException {
        Random rand = new Random();
        int LEN = 4447632;
        byte[] samplesFromStream = new byte[LEN];
        for (int i = 0; i < samplesFromStream.length; i++) {
            byte val = (byte) (rand.nextInt(255) - 128);
            samplesFromStream[i] = val;
        }
        InputStream stream1 = new ByteArrayInputStream(samplesFromStream);
        InputStream stream2 = new ByteArrayInputStream(samplesFromStream);
        boolean isSigned = true;
        boolean isBigEndian = false;
        int startSample = 0;
        int numberOfChannels = 2;
        int sampleSize = 2;
        int n = 1;


        double[][] samples1 = AudioProcessor.getEveryNthSampleMoreChannelsDoubleOldAndSlow(stream1, numberOfChannels,
                                                                                           sampleSize, n, startSample,
                                                                                           isBigEndian, isSigned, LEN);
        double[][] samples2 = AudioProcessor.getEveryNthSampleMoreChannelsDouble(stream2, numberOfChannels, sampleSize,
                                                                                 n, startSample, isBigEndian, isSigned, LEN);

        byte[][] samples3 = AudioProcessor.getEveryNthSampleMoreChannels(samplesFromStream, numberOfChannels,
                                                                         sampleSize, n, startSample);
        double[][] samples3Double = new double[samples3.length][];
        for (int i = 0; i < samples3Double.length; i++) {
            samples3Double[i] = AudioConverter.normalizeToDoubles(samples3[i], sampleSize, sampleSize * 8,
                                                                  isBigEndian, isSigned);
        }

        boolean result1 = checkEqualityOfArraysTwoDim(samples1, samples3Double);
        boolean result2 = checkEqualityOfArraysTwoDim(samples2, samples3Double);
        ProgramTest.debugPrint("Checking against byte variant:", result1, result2);
        return checkEqualityOfArraysTwoDim(samples1, samples2);
    }


    /**
     * Method for testing calculateRMS by comparing the results of the 2 different implementations
     * (1 for input stream and 1 for byte array). Theoretically tests also other convertNSamplesToOneByTakingThe...
     * methods because they have almost identical implementation.
     *
     * @param stream            is the input stream, which contains the same content as the samplesFromStream parameter.
     * @param samplesFromStream is the 1D byte array which contains the same content as the stream parameter.
     * @param byteLength        is the total length of the stream, so it equals the samplesFromStream.length and
     *                          usually is in the property onlyAudioSize
     * @return Returns true if the outputs of the methods are equal, otherwise returns false.
     * @throws IOException is thrown when error with input stream occurred
     */
    public boolean performAggregationRMStestBoth(InputStream stream, byte[] samplesFromStream, int numberOfChannels,
                                                 int sampleSize, boolean isBigEndian, boolean isSigned,
                                                 int byteLength) throws IOException {
        if (Aggregation.performAggregation(stream, numberOfChannels, sampleSize, isBigEndian, isSigned, byteLength, Aggregation.RMS) ==
            Aggregation.performAggregation(samplesFromStream, sampleSize, isBigEndian, isSigned, Aggregation.RMS)) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Tests reverseArr method by calling it twice and checking if it is the same as before calling.
     * And by checking if the reverse of the array with sample size is the array reversed.
     *
     * @return Returns true if test succeeded, false if not.
     */
    public boolean reverseArrTest() {
        byte[] arr = new byte[400];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (byte) i;
        }

        AudioProcessor.reverseArr(arr, 2);
        AudioProcessor.reverseArr(arr, 2);

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != (byte) i) {
                return false;
            }
        }

        AudioProcessor.reverseArr(arr, 1);
        for (int i = 0; i < arr.length; i++) {
            if (arr[arr.length - 1 - i] != (byte) i) {
                return false;
            }
        }
        return true;
    }


    /**
     * Method for testing separateChannels by comparing the results of the 2 different implementations.
     * (1 for input stream and 1 for byte array).
     *
     * @param stream            is the input stream, which contains the same content as the byte array samplesFromStream.
     * @param samplesFromStream is the byte array which contains the same content as the stream.
     * @param numberOfChannels  represents the number of channels
     * @param sampleSize        is the size of 1 sample.
     * @return Returns true if both methods gave the same output, else returns false.
     * @throws IOException is thrown if error with input stream occurred.
     */
    public boolean separateChannelsOfSongTestBoth(InputStream stream, byte[] samplesFromStream,
                                                  int numberOfChannels, int sampleSize) throws IOException {
        byte[][] channels1 = AudioConverter.separateChannels(samplesFromStream, numberOfChannels, sampleSize);
        // Passing length of array just for testing measures - else I would have to take the real length from the file
        byte[][] channels2 = AudioConverter.separateChannels(stream, numberOfChannels, sampleSize,
                                                             samplesFromStream.length);

        return checkEqualityOfArraysTwoDim(channels1, channels2);
    }


    /**
     * Tests the performAggregation method with mod = MAX
     *
     * @return Returns true if the output equals the expected output
     * @throws IOException can be thrown only if the sample size is invalid
     */
    public boolean performAggregationTestMax() throws IOException {
        boolean isSigned = false;
        double correctResult;
        boolean isBigEndian = true;
        byte[] samples = new byte[8];
        samples[0] = 0;
        samples[1] = 1;
        samples[2] = 0;
        samples[3] = 5;
        samples[4] = 0;
        samples[5] = 3;
        samples[6] = 0;
        samples[7] = 4;

        correctResult = 5 / (double) AudioUtilities.getMaxAbsoluteValue(16, isSigned);
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.MAX) != correctResult) {
            return false;
        }

        isSigned = true;
        correctResult = 5 / (double) AudioUtilities.getMaxAbsoluteValue(16, isSigned);
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.MAX) != correctResult) {
            return false;
        }


        isBigEndian = false;
        isSigned = false;
        samples[0] = 1;
        samples[1] = 0;
        samples[2] = 5;
        samples[3] = 0;
        samples[4] = 3;
        samples[5] = 0;
        samples[6] = 4;
        samples[7] = 0;
        correctResult = 5 / (double) AudioUtilities.getMaxAbsoluteValue(16, isSigned);
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.MAX) != correctResult) {
            return false;
        }

        isSigned = true;
        correctResult = 5 / (double) AudioUtilities.getMaxAbsoluteValue(16, isSigned);
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.MAX) != correctResult) {
            return false;
        }

        return true;
    }


    /**
     * Tests the performAggregation method with mod = MIN
     *
     * @return Returns true if the output equals the expected output
     * @throws IOException can be thrown only if the sample size is invalid
     */
    public boolean performAggregationTestMin() throws IOException {
        double correctResult;
        boolean isSigned = false;
        boolean isBigEndian = true;
        byte[] samples = new byte[8];
        samples[0] = 0;
        samples[1] = 3;
        samples[2] = 0;
        samples[3] = 1;
        samples[4] = 0;
        samples[5] = 3;
        samples[6] = 0;
        samples[7] = 4;

        correctResult = 1 / (double) AudioUtilities.getMaxAbsoluteValue(16, isSigned);
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.MIN) != correctResult) {
            return false;
        }

        isSigned = true;
        correctResult = 1 / (double) AudioUtilities.getMaxAbsoluteValue(16, isSigned);
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.MIN) != correctResult) {
            return false;
        }

        isSigned = false;
        isBigEndian = false;
        samples[0] = 3;
        samples[1] = 0;
        samples[2] = 1;
        samples[3] = 0;
        samples[4] = 3;
        samples[5] = 0;
        samples[6] = 4;
        samples[7] = 0;
        correctResult = 1 / (double) AudioUtilities.getMaxAbsoluteValue(16, isSigned);
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.MIN) != correctResult) {
            return false;
        }

        isSigned = true;
        correctResult = 1 / (double) AudioUtilities.getMaxAbsoluteValue(16, isSigned);
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.MIN) != correctResult) {
            return false;
        }

        return true;
    }


    /**
     * Tests the performAggregation method with mod = AVG
     *
     * @return Returns true if the output equals the expected output
     * @throws IOException can be thrown only if the sample size is invalid
     */
    public boolean performAggregationTestAvg() throws IOException {
        double correctResult;
        boolean isSigned = false;
        boolean isBigEndian = true;
        byte[] samples = new byte[8];
        samples[0] = 0;
        samples[1] = 1;
        samples[2] = 0;
        samples[3] = 2;
        samples[4] = 0;
        samples[5] = 3;
        samples[6] = 0;
        samples[7] = 4;

        correctResult = ((1 + 2 + 3 + 4) / 4.0) / AudioUtilities.getMaxAbsoluteValue(16, isSigned);
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.AVG) != correctResult) {
            return false;
        }

        isSigned = true;
        correctResult = ((1 + 2 + 3 + 4) / 4.0) / AudioUtilities.getMaxAbsoluteValue(16, isSigned);
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.AVG) != correctResult) {
            return false;
        }


        isSigned = false;
        isBigEndian = false;
        samples[0] = 1;
        samples[1] = 0;
        samples[2] = 2;
        samples[3] = 0;
        samples[4] = 3;
        samples[5] = 0;
        samples[6] = 4;
        samples[7] = 0;

        correctResult = ((1 + 2 + 3 + 4) / 4.0) / AudioUtilities.getMaxAbsoluteValue(16, isSigned);
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.AVG) != correctResult) {
            return false;
        }

        isSigned = true;
        correctResult = ((1 + 2 + 3 + 4) / 4.0) / AudioUtilities.getMaxAbsoluteValue(16, isSigned);
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.AVG) != correctResult) {
            return false;
        }

        return true;
    }

    /**
     * Tests the performAggregation method with mod = RMS
     *
     * @return Returns true if the output equals the expected output
     * @throws IOException can be thrown only if the sample size is invalid
     */
    public boolean performAggregationTestRms() throws IOException {
        double correctResult;
        boolean isSigned = false;
        boolean isBigEndian = true;
        byte[] samples = new byte[8];
        samples[0] = 0;
        samples[1] = 1;
        samples[2] = 0;
        samples[3] = 2;
        samples[4] = 0;
        samples[5] = 3;
        samples[6] = 0;
        samples[7] = 4;

        correctResult = Math.sqrt(((1 * 1 + 2 * 2 + 3 * 3 + 4 * 4) / 4.0) /
                                  Math.pow(AudioUtilities.getMaxAbsoluteValue(16, isSigned), 2));
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.RMS) != correctResult) {
            return false;
        }

        isSigned = true;
        correctResult = Math.sqrt(((1 * 1 + 2 * 2 + 3 * 3 + 4 * 4) / 4.0) /
                                  Math.pow(AudioUtilities.getMaxAbsoluteValue(16, isSigned), 2));
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.RMS) != correctResult) {
            return false;
        }

        isSigned = false;
        isBigEndian = false;
        samples[0] = 1;
        samples[1] = 0;
        samples[2] = 2;
        samples[3] = 0;
        samples[4] = 3;
        samples[5] = 0;
        samples[6] = 4;
        samples[7] = 0;

        correctResult = Math.sqrt(((1 * 1 + 2 * 2 + 3 * 3 + 4 * 4) / 4.0) /
                                  Math.pow(AudioUtilities.getMaxAbsoluteValue(16, isSigned), 2));
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.RMS) != correctResult) {
            return false;
        }

        isSigned = true;
        correctResult = Math.sqrt(((1 * 1 + 2 * 2 + 3 * 3 + 4 * 4) / 4.0) /
                                  Math.pow(AudioUtilities.getMaxAbsoluteValue(16, isSigned), 2));
        if (Aggregation.performAggregation(samples, 2, isBigEndian, isSigned, Aggregation.RMS) != correctResult) {
            return false;
        }

        return true;
    }


    /**
     * Checks equality of two 1D labelReferenceArrs.
     *
     * @param arr1 is the first array.
     * @param arr2 is the second array.
     * @return Returns true, if the labelReferenceArrs have the same content, else returns false.
     */
    public static boolean checkEqualityOfArraysOneDim(double[] arr1, double[] arr2, int startIndex, double tolerance) {
        if (arr1.length != arr2.length) {
            return false;
        }
        else {
            return checkEqualityOfArraysOneDim(arr1, arr2, startIndex, arr1.length, tolerance);
        }
    }

    /**
     * Checks equality of two 1D labelReferenceArrs.
     *
     * @param arr1 is the first array.
     * @param arr2 is the second array.
     * @return Returns true, if the labelReferenceArrs have the same content, else returns false.
     */
    public static boolean checkEqualityOfArraysOneDim(double[] arr1, double[] arr2,
                                                      int startIndex, int len, double tolerance) {
        for (int i = startIndex; i < len; i++) {
            if (arr2[i] < arr1[i] - tolerance || arr2[i] > arr1[i] + tolerance) {
                System.out.println(i + "\t" + arr1[i] + "\t" + arr2[i]);
                if (i > 0) {
                    System.out.println((i - 1) + "\t" + arr1[i - 1] + "\t" + arr2[i - 1]);
                }
                if (i < len - 1) {
                    System.out.println((i + 1) + "\t" + arr1[i + 1] + "\t" + arr2[i + 1]);
                }
                return false;
            }
        }

        return true;
    }


    /**
     * Checks equality of two 1D labelReferenceArrs.
     *
     * @param arr1 is the first array.
     * @param arr2 is the second array.
     * @return Returns true, if the labelReferenceArrs have the same content, else returns false.
     */
    public static boolean checkEqualityOfArraysOneDim(byte[] arr1, byte[] arr2, int startIndex) {
        if (arr1.length != arr2.length) {
            return false;
        }
        else {
            for (int i = startIndex; i < arr1.length; i++) {
                if (arr1[i] != arr2[i]) {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Checks equality of two 2D labelReferenceArrs.
     *
     * @param arr1 is the first array.
     * @param arr2 is the second array.
     * @return Returns true, if the labelReferenceArrs have the same content, else returns false.
     */
    public static boolean checkEqualityOfArraysTwoDim(byte[][] arr1, byte[][] arr2) {

        if (arr1.length != arr2.length) {
            return false;
        }
        else {
            for (int i = 0; i < arr1.length; i++) {
                if (arr1[i].length != arr2[i].length) {
                    return false;
                }
                else {
                    for (int j = 0; j < arr1[i].length; j++) {
                        if (arr1[i][j] != arr2[i][j]) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }


    // Same as byte variant - I had to copy paste, since java generics are bad

    /**
     * Checks equality of two 2D labelReferenceArrs.
     *
     * @param arr1 is the first array.
     * @param arr2 is the second array.
     * @return Returns true, if the labelReferenceArrs have the same content, else returns false.
     */
    public static boolean checkEqualityOfArraysTwoDim(double[][] arr1, double[][] arr2) {

        if (arr1.length != arr2.length) {
            return false;
        }
        else {
            for (int i = 0; i < arr1.length; i++) {
                if (arr1[i].length != arr2[i].length) {
                    return false;
                }
                else {
                    for (int j = 0; j < arr1[i].length; j++) {
                        if (arr1[i][j] != arr2[i][j]) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }


    /**
     * tests the convertBytesToSamples method.
     *
     * @return Returns true if test was successful, false otherwise.
     */
    public boolean convertBytesToSamplesTest() {
        try {
            boolean isBigEndian = true;
            byte[] samples = new byte[8];
            samples[0] = 0;
            samples[1] = 1;
            samples[2] = 0;
            samples[3] = 2;
            samples[4] = 0;
            samples[5] = 3;
            samples[6] = 0;
            samples[7] = 4;
            int[] result = AudioConverter.convertBytesToSamples(samples, 2, isBigEndian, false);
            for (int i = 0; i < result.length; i++) {
                if (result[i] != (i + 1)) {
                    return false;
                }
            }
            isBigEndian = false;
            samples[0] = 1;
            samples[1] = 0;
            samples[2] = 2;
            samples[3] = 0;
            samples[4] = 3;
            samples[5] = 0;
            samples[6] = 4;
            samples[7] = 0;
            result = AudioConverter.convertBytesToSamples(samples, 2, isBigEndian, false);
            for (int i = 0; i < result.length; i++) {
                if (result[i] != (i + 1)) {
                    return false;
                }
            }

            isBigEndian = false;
            samples[0] = -1;
            samples[1] = 0;
            samples[2] = 0;
            samples[3] = 0;
            samples[4] = 0;
            samples[5] = 0;
            samples[6] = 0;
            samples[7] = 0;
            result = AudioConverter.convertBytesToSamples(samples, 1, isBigEndian, false);
            if (result[0] != 255) {
                return false;
            }
            for (int i = 1; i < result.length; i++) {
                if (result[i] != 0) {
                    return false;
                }
            }

            result = AudioConverter.convertBytesToSamples(samples, 1, isBigEndian, true);
            if (result[0] != -1) {
                return false;
            }
            for (int i = 1; i < result.length; i++) {
                if (result[i] != 0) {
                    return false;
                }
            }


            return true;
        }
        catch (Exception e) {
            return false;
        }
    }


    public static void convertBytesToSamplesTest3() {
        System.out.println("convertBytesToSamplesTest3(): --- Start of test");
        System.out.println(convertBytesToSamplesTestUniform(2, false, false));
        System.out.println(convertBytesToSamplesTestUniform(2, true, false));
        System.out.println(convertBytesToSamplesTestUniform(2, false, true));
        System.out.println(convertBytesToSamplesTestUniform(2, true, true));
        System.out.println(convertBytesToSamplesTestUniform(3, false, false));
        System.out.println("convertBytesToSamplesTest3(): --- End of test");
    }

    /**
     * tests the convertBytesToSamples method.
     *
     * @return Returns true if test was successful, false otherwise.
     */
    public static boolean convertBytesToSamplesTestUniform(int sampleSize, boolean isBigEndian, boolean isSigned) {
        int[] intArr = new int[128];
        byte[] byteArr = fillIntArrAndReturnByteArr(intArr, sampleSize, isBigEndian, isSigned);

        try {
            int[] result = AudioConverter.convertBytesToSamples(byteArr, sampleSize, isBigEndian, isSigned);
            if (result.length != intArr.length) {
                System.out.print("Size of original array: " + intArr.length +
                                 "\t\tSize of generated array: " + result.length + "\t\t\t");
                return false;
            }
            for (int i = 0; i < result.length; i++) {
                if (result[i] != intArr[i]) {
                    System.out.println("Calculated Value: " + result[i] + "\tOriginal value: " + intArr[i]);
                    return false;
                }
            }
        }
        catch (IOException e) {
            return false;
        }

        return true;
    }

    public static boolean convertBytesToSamplesTest2() {
        try {
            boolean isSigned = false;
            boolean isBigEndian = true;
            byte[] samples = new byte[8];
            samples[0] = 0;
            samples[1] = 1;
            samples[2] = 0;
            samples[3] = 2;
            samples[4] = 0;
            samples[5] = 3;
            samples[6] = 0;
            samples[7] = 4;
            int[] result = AudioConverter.convertBytesToSamples(samples, 2, isBigEndian, isSigned);
            for (int i = 0; i < result.length; i++) {
                if (result[i] != (i + 1)) {
                    return false;
                }
            }
            isSigned = false;
            isBigEndian = false;
            samples[0] = 1;
            samples[1] = 0;
            samples[2] = 2;
            samples[3] = 0;
            samples[4] = 3;
            samples[5] = 0;
            samples[6] = 4;
            samples[7] = 0;
            result = AudioConverter.convertBytesToSamples(samples, 2, isBigEndian, isSigned);
            for (int i = 0; i < result.length; i++) {
                if (result[i] != (i + 1)) {
                    return false;
                }
            }

            isSigned = false;
            isBigEndian = false;
            samples[0] = -128;
            String s1 = String.format("%8s", Integer.toBinaryString(samples[0] & 0xFF)).replace(' ', '0');
            System.out.println(s1); // 10000001
            samples[1] = 0;
            samples[2] = 0;
            samples[3] = 0;
            samples[4] = 0;
            samples[5] = 0;
            samples[6] = 0;
            samples[7] = 0;
            result = AudioConverter.convertBytesToSamples(samples, 1, isBigEndian, isSigned);
            if (result[0] != 128) {
                return false;
            }
            for (int i = 1; i < result.length; i++) {
                if (result[i] != 0) {
                    return false;
                }
            }


            isSigned = true;
            isBigEndian = false;
            samples[0] = -128;
            s1 = String.format("%8s", Integer.toBinaryString(samples[0] & 0xFF)).replace(' ', '0');
            System.out.println(s1); // 10000001
            samples[1] = 0;
            samples[2] = 0;
            samples[3] = 0;
            samples[4] = 0;
            samples[5] = 0;
            samples[6] = 0;
            samples[7] = 0;
            result = AudioConverter.convertBytesToSamples(samples, 1, isBigEndian, isSigned);
            if (result[0] != -128) {
                return false;
            }
            for (int i = 1; i < result.length; i++) {
                if (result[i] != 0) {
                    return false;
                }
            }


            isSigned = true;
            isBigEndian = false;
            samples[0] = -128;
            s1 = String.format("%8s", Integer.toBinaryString(samples[0] & 0xFF)).replace(' ', '0');
            System.out.println(s1); // 10000001
            samples[1] = 0;
            samples[2] = 0;
            samples[3] = 0;
            samples[4] = 0;
            samples[5] = 0;
            samples[6] = 0;
            samples[7] = 0;
            result = AudioConverter.convertBytesToSamples(samples, 2, isBigEndian, isSigned);
            if (result[0] != 128) {            // Should be 128 since the sing bit == 0
                System.out.println(result[0]);
                return false;
            }
            for (int i = 1; i < result.length; i++) {
                if (result[i] != 0) {
                    return false;
                }
            }


            isSigned = true;
            isBigEndian = true;
            samples[0] = 0;
            s1 = String.format("%8s", Integer.toBinaryString(samples[0] & 0xFF)).replace(' ', '0');
            System.out.println(s1); // 10000001
            samples[1] = -128;
            samples[2] = 0;
            samples[3] = 0;
            samples[4] = 0;
            samples[5] = 0;
            samples[6] = 0;
            samples[7] = 0;
            result = AudioConverter.convertBytesToSamples(samples, 2, isBigEndian, isSigned);
            if (result[0] != 128) {            // Should be 128 since the sing bit == 0
                System.out.println(result[0]);
                return false;
            }
            for (int i = 1; i < result.length; i++) {
                if (result[i] != 0) {
                    return false;
                }
            }


            isSigned = true;
            isBigEndian = false;
            samples[0] = -128;
            s1 = String.format("%8s", Integer.toBinaryString(samples[0] & 0xFF)).replace(' ', '0');
            System.out.println(s1); // 10000001
            samples[1] = -1;
            samples[2] = 0;
            samples[3] = 0;
            samples[4] = 0;
            samples[5] = 0;
            samples[6] = 0;
            samples[7] = 0;
            result = AudioConverter.convertBytesToSamples(samples, 2, isBigEndian, isSigned);
            if (result[0] != -128) {
                System.out.println(result[0]);
                return false;
            }
            for (int i = 1; i < result.length; i++) {
                if (result[i] != 0) {
                    return false;
                }
            }


            isSigned = true;
            isBigEndian = true;
            samples[0] = -1;
            s1 = String.format("%8s", Integer.toBinaryString(samples[0] & 0xFF)).replace(' ', '0');
            System.out.println(s1); // 10000001
            samples[1] = -128;
            samples[2] = 0;
            samples[3] = 0;
            samples[4] = 0;
            samples[5] = 0;
            samples[6] = 0;
            samples[7] = 0;
            result = AudioConverter.convertBytesToSamples(samples, 2, isBigEndian, isSigned);
            if (result[0] != -128) {
                System.out.println(result[0]);
                return false;
            }
            for (int i = 1; i < result.length; i++) {
                if (result[i] != 0) {
                    return false;
                }
            }

            isSigned = false;
            isBigEndian = false;
            samples[0] = -128;
            s1 = String.format("%8s", Integer.toBinaryString(samples[0] & 0xFF)).replace(' ', '0');
            System.out.println(s1); // 10000001
            samples[1] = -1;
            samples[2] = 0;
            samples[3] = 0;
            samples[4] = 0;
            samples[5] = 0;
            samples[6] = 0;
            samples[7] = 0;
            result = AudioConverter.convertBytesToSamples(samples, 2, isBigEndian, isSigned);
            if (result[0] != 0x0000FF80) {
                System.out.println(result[0]);
                return false;
            }
            for (int i = 1; i < result.length; i++) {
                if (result[i] != 0) {
                    return false;
                }
            }


            isBigEndian = false;
            isSigned = false;
            int sampleSize = 3;
            samples = new byte[sampleSize];
            int number = 45678;
            AudioConverter.convertIntToByteArr(samples, number, isBigEndian);
            result = AudioConverter.convertBytesToSamples(samples, sampleSize, isBigEndian, isSigned);
            if (result[0] != number) {
                System.out.println(result[0]);
                return false;
            }

            isBigEndian = false;
            isSigned = true;
            sampleSize = 3;
            samples = new byte[sampleSize];
            number = -500;
            AudioConverter.convertIntToByteArr(samples, number, isBigEndian);
            result = AudioConverter.convertBytesToSamples(samples, sampleSize, isBigEndian, isSigned);
            if (result[0] != number) {
                System.out.println(result[0]);
                return false;
            }


            isBigEndian = true;
            isSigned = false;
            sampleSize = 3;
            samples = new byte[sampleSize];
            number = 65559;
            AudioConverter.convertIntToByteArr(samples, number, isBigEndian);
            result = AudioConverter.convertBytesToSamples(samples, sampleSize, isBigEndian, isSigned);
            if (result[0] != number) {
                System.out.println(result[0]);
                return false;
            }

            isBigEndian = true;
            isSigned = true;
            sampleSize = 3;
            samples = new byte[sampleSize];
            number = -65559;
            AudioConverter.convertIntToByteArr(samples, number, isBigEndian);
            result = AudioConverter.convertBytesToSamples(samples, sampleSize, isBigEndian, isSigned);
            if (result[0] != number) {
                System.out.println(result[0]);
                return false;
            }


            isBigEndian = true;
            isSigned = true;
            sampleSize = 1;
            samples = new byte[]{110};
            result = AudioConverter.convertBytesToSamples(samples, sampleSize, isBigEndian, isSigned);
            if (result[0] != samples[0]) {
                System.out.println(result[0]);
                return false;
            }

            isBigEndian = true;
            isSigned = true;
            sampleSize = 1;
            samples = new byte[]{-110};
            result = AudioConverter.convertBytesToSamples(samples, sampleSize, isBigEndian, isSigned);
            if (result[0] != samples[0]) {
                System.out.println(result[0]);
                return false;
            }


            return true;
        }
        catch (Exception e) {
            return false;
        }
    }


    public static boolean convertToMono1ByteSamples2ChannelsTest() {
        byte[] testArr = new byte[100];
        for (int i = 0; i < testArr.length; i++) {
            if (i % 2 == 0) {
                testArr[i] = (byte) (i + 2);
            }
            else {
                testArr[i] = (byte) (i + 3);
            }
        }

        byte[] result = null;
        try {
            result = AudioConverter.convertToMono(testArr, 2, 2, 1, true, false);
        }
        catch (IOException e) {
            System.out.print("FALSE1");
            return false;
        }
        int correctLen = testArr.length / 2;
        if (result.length != correctLen) {
            System.out.print("FALSE2:\tCorrect length: " + correctLen + "\tCalculated length: " + result.length + "\t");
            return false;
        }
        for (int i = 0; i < result.length; i++) {
            if (result[i] != 2 * i + 3) {
                System.out.print("FALSE3:\tCorrect value: " + (i + 3) +
                                 "\tCalculated value: " + result[i] + "\tIndex:" + i + "\t");
                return false;
            }
        }

        return true;
    }


    public static boolean convertToMonoUniformTest(int sampleSize, boolean isBigEndian,
                                                   boolean isSigned, int numberOfChannels) {
        int[] testArrInt = new int[128];
        byte[] testArrByte = fillIntArrAndReturnByteArr(testArrInt, sampleSize, isBigEndian, isSigned);

        byte[] result = new byte[0];
        try {
            result = AudioConverter.convertToMono(testArrByte, sampleSize * numberOfChannels, numberOfChannels,
                                                  sampleSize, isBigEndian, isSigned);
        }
        catch (IOException e) {
            System.out.print("FALSE1\t");
            return false;
        }
        int correctArrLen = sampleSize * testArrInt.length / numberOfChannels;
        if (result.length != correctArrLen) {
            System.out.print("FALSE2\tCorrect array length:" +
                             correctArrLen + "\tCalculated array length:" + result.length + "\t");
            return false;
        }

        int mask = AudioUtilities.calculateMask(sampleSize);
        for (int i = 0, testIndex = 0; i < result.length; i += sampleSize) {
            int val = AudioConverter.convertBytesToInt(result, sampleSize, mask, i, isBigEndian, isSigned);
            int average = 0;
            for (int k = 0; k < numberOfChannels; k++, testIndex++) {
                average += testArrInt[testIndex];
            }
            average /= numberOfChannels;

            if (average != val) {
                System.out.print("FALSE4\tCorrect result: " + average + "\tCalculated result: " + val + "\t");
                return false;
            }
        }

        return true;
    }


    public static void convertToMonoTests() {
        System.out.println("convertToMonoTest2ByteSamplesUnsigned2ChannelsBigEndian:\t" +
                           convertToMonoTest2ByteSamplesUnsigned2ChannelsBigEndian());
        System.out.println("convertToMonoTest2ByteSamplesSigned2ChannelsBigEndian:\t" +
                           convertToMonoTest2ByteSamplesSigned2ChannelsBigEndian());
        System.out.println("convertToMonoTest2ByteSamplesUnsigned2ChannelsLittleEndian:\t" +
                           convertToMonoTest2ByteSamplesUnsigned2ChannelsLittleEndian());
        System.out.println("convertToMonoTest2ByteSamplesSigned2ChannelsLittleEndian:\t" +
                           convertToMonoTest2ByteSamplesSigned2ChannelsLittleEndian());
        System.out.println("convertToMonoTest3ByteSamplesSigned4ChannelsLittleEndian:\t" +
                           convertToMonoTest3ByteSamplesSigned4ChannelsLittleEndian());
    }

    public static boolean convertToMonoTest2ByteSamplesUnsigned2ChannelsBigEndian() {
        int sampleSize = 2;
        boolean isBigEndian = true;
        int numberOfChannels = 2;
        boolean isSigned = false;
        return convertToMonoUniformTest(sampleSize, isBigEndian, isSigned, numberOfChannels);
    }

    public static boolean convertToMonoTest2ByteSamplesSigned2ChannelsBigEndian() {
        int sampleSize = 2;
        boolean isBigEndian = true;
        int numberOfChannels = 2;
        boolean isSigned = true;
        return convertToMonoUniformTest(sampleSize, isBigEndian, isSigned, numberOfChannels);
    }

    public static boolean convertToMonoTest2ByteSamplesUnsigned2ChannelsLittleEndian() {
        int sampleSize = 2;
        boolean isBigEndian = false;
        int numberOfChannels = 2;
        boolean isSigned = false;
        return convertToMonoUniformTest(sampleSize, isBigEndian, isSigned, numberOfChannels);
    }

    public static boolean convertToMonoTest2ByteSamplesSigned2ChannelsLittleEndian() {
        int sampleSize = 2;
        boolean isBigEndian = false;
        int numberOfChannels = 2;
        boolean isSigned = true;
        return convertToMonoUniformTest(sampleSize, isBigEndian, isSigned, numberOfChannels);
    }

    public static boolean convertToMonoTest3ByteSamplesSigned4ChannelsLittleEndian() {
        int sampleSize = 3;
        boolean isBigEndian = false;
        int numberOfChannels = 4;
        boolean isSigned = true;
        return convertToMonoUniformTest(sampleSize, isBigEndian, isSigned, numberOfChannels);
    }


    public static boolean convertBytesToNormalizedSamplesUniformTest(int sampleSize,
                                                                     boolean isBigEndian, boolean isSigned) {
        int[] testArrInt = new int[128];
        byte[] byteSamples = fillIntArrAndReturnByteArr(testArrInt, sampleSize, isBigEndian, isSigned);
        if (byteSamples.length != testArrInt.length * sampleSize) {
            System.out.print("FALSE1\t");
            return false;
        }

        int sampleSizeInBits = sampleSize * 8;
        double[] result = new double[0];
        try {
            result = AudioConverter.normalizeToDoubles(byteSamples, sampleSize, sampleSizeInBits, isBigEndian, isSigned);
        }
        catch (IOException e) {
            System.out.print("FALSE2\t");
            return false;
        }
        if (result.length != testArrInt.length) {
            System.out.print("FALSE3\t");
            return false;
        }

        int maxAbsoluteValue = AudioUtilities.getMaxAbsoluteValueSigned(sampleSizeInBits);
        for (int i = 0; i < result.length; i++) {
            int val = testArrInt[i];
            if (!isSigned) {
                val -= maxAbsoluteValue;
            }
            double dv = ((double) val) / maxAbsoluteValue;
            if (dv != result[i]) {
                System.out.print("FALSE4 ------ Index:\tWrong result:\tRight result:\t" +
                                 "Value before normalization:\tMax absolute value --- ");
                System.out.println(i + ":\t" + result[i] + ":\t" + dv + ":\t" + val + ":\t" + maxAbsoluteValue);
                return false;
            }
        }
        return true;
    }

    public static void convertBytesToNormalizedSamplesTests() {
        System.out.println("ConvertBytesToNormalizedSamplesTest1:\t" + convertBytesToNormalizedSamplesTest1());
        System.out.println("ConvertBytesToNormalizedSamplesTest2:\t" + convertBytesToNormalizedSamplesTest2());
        System.out.println("ConvertBytesToNormalizedSamplesTest3:\t" + convertBytesToNormalizedSamplesTest3());
        System.out.println("ConvertBytesToNormalizedSamplesTest4:\t" + convertBytesToNormalizedSamplesTest4());
        System.out.println("ConvertBytesToNormalizedSamplesTest5:\t" + convertBytesToNormalizedSamplesTest5());
    }

    public static boolean convertBytesToNormalizedSamplesTest1() {
        int sampleSize = 2;
        boolean isBigEndian = false;
        boolean isSigned = false;
        return convertBytesToNormalizedSamplesUniformTest(sampleSize, isBigEndian, isSigned);
    }

    public static boolean convertBytesToNormalizedSamplesTest2() {
        int sampleSize = 2;
        boolean isBigEndian = true;
        boolean isSigned = false;
        return convertBytesToNormalizedSamplesUniformTest(sampleSize, isBigEndian, isSigned);
    }

    public static boolean convertBytesToNormalizedSamplesTest3() {
        int sampleSize = 2;
        boolean isBigEndian = false;
        boolean isSigned = true;
        return convertBytesToNormalizedSamplesUniformTest(sampleSize, isBigEndian, isSigned);
    }

    public static boolean convertBytesToNormalizedSamplesTest4() {
        int sampleSize = 2;
        boolean isBigEndian = true;
        boolean isSigned = true;
        return convertBytesToNormalizedSamplesUniformTest(sampleSize, isBigEndian, isSigned);
    }

    public static boolean convertBytesToNormalizedSamplesTest5() {
        int sampleSize = 3;
        boolean isBigEndian = true;
        boolean isSigned = true;
        return convertBytesToNormalizedSamplesUniformTest(sampleSize, isBigEndian, isSigned);
    }

    // Method used in testing
    private static byte[] fillIntArrAndReturnByteArr(int[] testArrInt, int sampleSize, boolean isBigEndian, boolean isSigned) {
        Random random = new Random(1337);

        int maxSample = (1 << sampleSize) - 1;
        if (isSigned) {
            for (int i = 0; i < testArrInt.length; i++) {
                testArrInt[i] = random.nextInt(maxSample) - (maxSample / 2);
            }
        }
        else {
            for (int i = 0; i < testArrInt.length; i++) {
                testArrInt[i] = random.nextInt(maxSample);
            }
        }

        byte[] sample = new byte[sampleSize];
        byte[] testArrByte = new byte[testArrInt.length * sampleSize];
        for (int i = 0, j = 0; i < testArrInt.length; i++) {
            AudioConverter.convertIntToByteArr(sample, testArrInt[i], isBigEndian);
            for (int sampleInd = 0; sampleInd < sampleSize; j++, sampleInd++) {
                testArrByte[j] = sample[sampleInd];
            }
        }

        return testArrByte;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Things for audio modfication which aren't needed ... filters, sum, etc.
    public static boolean performMovingWindowAverageByRefTest() {
        // 1 channel
        int numberOfChannels = 1;
        double[] samples = new double[]{1.5, 2.5, 3.5, 4.5, 5.5};
        int windowSize = 1;
        double[] result = new double[]{1.5, 2.5, 3.5, 4.5, 5.5};
        NonRecursiveFilter.performMovingWindowAverageByRef(samples, windowSize, numberOfChannels);
        for (int i = 0; i < samples.length; i++) {
            if (samples[i] != result[i]) {
                System.out.print("FALSE1:\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + samples[i] + "\tIndex:" + i + "\t");
                return false;
            }
        }

        windowSize = 2;
        samples = new double[]{1.5, 2.5, 3.5, 4.5, 5.5};
        result = new double[]{1.5, 2, 3, 4, 5};
        NonRecursiveFilter.performMovingWindowAverageByRef(samples, windowSize, numberOfChannels);
        for (int i = 0; i < samples.length; i++) {
            if (samples[i] != result[i]) {
                System.out.print("FALSE2:\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + samples[i] + "\tIndex:" + i + "\t");
                return false;
            }
        }

        windowSize = 3;
        samples = new double[]{1.5, 2.5, 3.5, 4.5, 5.5};
        result = new double[]{1.5, 2.5, 2.5, 3.5, 4.5};
        NonRecursiveFilter.performMovingWindowAverageByRef(samples, windowSize, numberOfChannels);
        for (int i = 0; i < samples.length; i++) {
            if (samples[i] != result[i]) {
                System.out.print("FALSE3:\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + samples[i] + "\tIndex:" + i + "\t");
                return false;
            }
        }

        windowSize = 5;
        samples = new double[]{1.5, 2.5, 3.5, 4.5, 5.5};
        result = new double[]{1.5, 2.5, 3.5, 4.5, ((1.5 + 2.5 + 3.5 + 4.5 + 5.5) / 5)};
        NonRecursiveFilter.performMovingWindowAverageByRef(samples, windowSize, numberOfChannels);
        for (int i = 0; i < samples.length; i++) {
            if (samples[i] != result[i]) {
                System.out.print("FALSE4:\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + samples[i] + "\tIndex:" + i + "\t");
                return false;
            }
        }

        // 2 channels
        numberOfChannels = 2;
        samples = new double[]{1.5, 1.5, 2.5, 2.5, 3.5, 3.5, 4.5, 4.5, 5.5, 5.5};
        windowSize = 1;
        result = new double[]{1.5, 1.5, 2.5, 2.5, 3.5, 3.5, 4.5, 4.5, 5.5, 5.5};
        NonRecursiveFilter.performMovingWindowAverageByRef(samples, windowSize, numberOfChannels);
        for (int i = 0; i < samples.length; i++) {
            if (samples[i] != result[i]) {
                System.out.print("FALSE5:\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + samples[i] + "\tIndex:" + i + "\t");
                return false;
            }
        }

        windowSize = 2;
        samples = new double[]{1.5, 1.5, 2.5, 2.5, 3.5, 3.5, 4.5, 4.5, 5.5, 5.5};
        result = new double[]{1.5, 1.5, 2, 2, 3, 3, 4, 4, 5, 5};
        NonRecursiveFilter.performMovingWindowAverageByRef(samples, windowSize, numberOfChannels);
        for (int i = 0; i < samples.length; i++) {
            if (samples[i] != result[i]) {
                System.out.print("FALSE6:\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + samples[i] + "\tIndex:" + i + "\t");
                return false;
            }
        }

        windowSize = 3;
        samples = new double[]{1.5, 1.5, 2.5, 2.5, 3.5, 3.5, 4.5, 4.5, 5.5, 5.5};
        result = new double[]{1.5, 1.5, 2.5, 2.5, 2.5, 2.5, 3.5, 3.5, 4.5, 4.5};
        NonRecursiveFilter.performMovingWindowAverageByRef(samples, windowSize, numberOfChannels);
        for (int i = 0; i < samples.length; i++) {
            if (samples[i] != result[i]) {
                System.out.print("FALSE7:\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + samples[i] + "\tIndex:" + i + "\t");
                return false;
            }
        }

        windowSize = 5;
        samples = new double[]{1.5, 1.5, 2.5, 2.5, 3.5, 3.5, 4.5, 4.5, 5.5, 5.5};
        double lastResultval = (1.5 + 2.5 + 3.5 + 4.5 + 5.5) / 5;
        result = new double[]{1.5, 1.5, 2.5, 2.5, 3.5, 3.5, 4.5, 4.5, lastResultval, lastResultval};
        NonRecursiveFilter.performMovingWindowAverageByRef(samples, windowSize, numberOfChannels);
        for (int i = 0; i < samples.length; i++) {
            if (samples[i] != result[i]) {
                System.out.print("FALSE8:\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + samples[i] + "\tIndex:" + i + "\t");
                return false;
            }
        }


        // Random test - basically implements window average again
        numberOfChannels = 1;
        double resultSample;
        int len = 5000;
        double[] oldSamples = new double[len];
        Random rand = new Random();
        for (int i = 0; i < oldSamples.length; i++) {
            oldSamples[i] = rand.nextDouble();
        }


        // Test for windowSizes from 1 to 10
        for (windowSize = 1; windowSize <= 10; windowSize++) {
            samples = Arrays.copyOf(oldSamples, oldSamples.length);
            NonRecursiveFilter.performMovingWindowAverageByRef(samples, windowSize, numberOfChannels);
            for (int i = 0; i < windowSize - 1; i++) {
                if (oldSamples[i] != samples[i]) {
                    System.out.print("FALSE9:\tStart samples doesn't equal:\tCorrect value: " +
                                     oldSamples[i] + "\tCalculated value: " + samples[i] + "\t");
                    return false;
                }
            }


            // Since we are calculating the value in 2 different ways in doubles, the result will be slightly different
            double epsilon = 0.00000001;
            int firstSample = 0;
            for (int i = windowSize - 1; i < samples.length; i++, firstSample++) {
                int index = firstSample;
                resultSample = 0;
                for (int j = 0; j < windowSize; j++, index++) {
                    resultSample += oldSamples[index];
                }
                resultSample /= windowSize;
                if (samples[i] > resultSample + epsilon || samples[i] < resultSample - epsilon) {
                    System.out.print("FALSE10:\tCorrect value: " + resultSample + "\tCalculated value: " + samples[i] +
                                     "\tIndex: " + i + "\tWindow size: " + windowSize + "\t");
                    return false;
                }
            }
        }

        return true;
    }


    public static boolean windowsAverageVsNonRecursiveDoubleFilter(int len) {
        int channelTestCount = 6;
        for (int ch = 2; ch < channelTestCount; ch++) {
            if (len % ch != 0) {
                len *= ch;
            }
        }
        double[] arr1 = new double[len];
        double[] result1 = new double[len];
        double[] result2 = new double[len];
        Random rand = new Random();
        for (int i = 0; i < arr1.length; i++) {
            arr1[i] = rand.nextDouble();
        }
        for (int i = 0; i < 10; i++) {
            double[] coef = new double[rand.nextInt(len)];
            for (int j = 0; j < coef.length; j++) {
                coef[j] = (double) 1 / coef.length;
            }

            for (int ch = 1; ch < channelTestCount; ch++) {
                NonRecursiveFilter.performNonRecursiveFilter(arr1, 0, coef, ch,
                                                             result1, 0, result1.length);
                System.arraycopy(arr1, 0, result2, 0, arr1.length);
                NonRecursiveFilter.performMovingWindowAverageByRef(result2, coef.length, ch);

                if (!ProgramTest.checkEqualityOfArraysOneDim(result1, result2, coef.length * ch, 0.000000000001)) {
                    System.out.println("Channel that failed:\t" + ch);
                    return false;
                }
            }
        }

        return true;
    }


    private static double[] getAVGCoefs(int windowSize) {
        double[] coef = new double[windowSize];
        for (int i = 0; i < coef.length; i++) {
            coef[i] = 1 / (double) windowSize;
        }

        return coef;
    }

    private static byte[] getSamples(int[] intArr, int sampleSize, boolean convertToBigEndian) {
        byte[] sample;
        byte[] outputArr = new byte[intArr.length * sampleSize];
        int j = 0;
        for (int i = 0; i < intArr.length; i++) {
            sample = AudioConverter.convertIntToByteArr(sampleSize, intArr[i], convertToBigEndian);
            for (int k = 0; k < sample.length; k++, j++) {
                outputArr[j] = sample[k];
            }
        }

        return outputArr;
    }

    private static int[] performNonRecursiveFilterTestIntsInput(int numberOfChannels) {
        int[] ints = new int[6 * numberOfChannels];
        for (int i = 0, index = 0; i <= 10; i += 2) {
            for (int ch = 0; ch < numberOfChannels; ch++, index++) {
                ints[index] = i;
            }
        }

        return ints;
    }

    private static byte[] performNonRecursiveFilterTestGetResult(int sampleSize, int numberOfChannels,
                                                                 int windowSize, boolean convertToBigEndian) {
        byte[] result = new byte[6 * numberOfChannels * sampleSize];
        int[] resultFor1Channel = null;
        switch (windowSize) {
            case 1:
                resultFor1Channel = new int[]{0, 2, 4, 6, 8, 10};
                break;
            case 2:
                resultFor1Channel = new int[]{0, 1, 3, 5, 7, 9};
                break;
            case 3:
                resultFor1Channel = new int[]{0, 0, 1, 3, 5, 7};
                break;
            case 4:
                resultFor1Channel = new int[]{0, 0, 1, 2, 4, 6};
                break;
            case 5:
                resultFor1Channel = new int[]{0, 0, 0, 1, 2, 4};
                break;
            case 6:
                resultFor1Channel = new int[]{0, 0, 0, 1, 2, 3};
                break;
            case 7:
                resultFor1Channel = new int[]{0, 0, 0, 0, 1, 2};
                break;
            case 8:
                resultFor1Channel = new int[]{0, 0, 0, 0, 1, 2};
                break;
            case 9:
                resultFor1Channel = new int[]{0, 0, 0, 0, 0, 1};
                break;
            case 10:
                resultFor1Channel = new int[]{0, 0, 0, 0, 0, 1};
                break;
            case 11:
                resultFor1Channel = new int[]{0, 0, 0, 0, 0, 0};
                break;
            default:
                return null;
        }

        for (int i = 0, intIndex = 0; intIndex < resultFor1Channel.length; intIndex++) {
            byte[] sample = AudioConverter.convertIntToByteArr(sampleSize, resultFor1Channel[intIndex],
                                                               convertToBigEndian);
            for (int ch = 0; ch < numberOfChannels; ch++) {
                for (int j = 0; j < sample.length; j++, i++) {
                    result[i] = sample[j];
                }
            }
        }

        return result;
    }


    private static boolean performOneNonRecursiveFilterTest(int sampleSize, int numberOfChannels, int windowSize,
                                                            boolean isBigEndian, boolean isSigned) {
        int frameSize = sampleSize * numberOfChannels;
        double[] coef = getAVGCoefs(windowSize);
        int[] intSamples = performNonRecursiveFilterTestIntsInput(numberOfChannels);
        byte[] samples = getSamples(intSamples, sampleSize, isBigEndian);
        byte[] result = performNonRecursiveFilterTestGetResult(sampleSize, numberOfChannels, windowSize, isBigEndian);
        try {
            samples = NonRecursiveFilter.performNonRecursiveFilter(samples, coef, numberOfChannels, sampleSize,
                                                                   frameSize, isBigEndian, isSigned);
        }
        catch (IOException e) {
            System.out.print("Exception:\t");
            return false;
        }

        if (samples.length != result.length) {
            System.out.print("Not same length:\tCorrect length:\t" + result.length +
                             "\tCalculated length:\t" + samples.length);
            return false;
        }

        for (int i = 0; i < samples.length; i++) {
            if (samples[i] != result[i]) {
                System.out.print("\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + samples[i] + "\tIndex:" + i + "\t");
                return false;
            }
        }

        return true;
    }

    public static boolean performNonRecursiveFilterTest() {
        boolean isBigEndian;
        boolean isSigned;
        int sampleSize;
        int numberOfChannels;
        int windowSize;
        boolean result;

        isBigEndian = false;
        isSigned = false;
        sampleSize = 2;
        numberOfChannels = 1;
        windowSize = 1;
        result = performOneNonRecursiveFilterTest(sampleSize, numberOfChannels, windowSize, isBigEndian, isSigned);
        if (!result) {
            System.out.print("FALSE1:");
            return false;
        }


        numberOfChannels = 1;
        windowSize = 2;
        sampleSize = 1;
        isSigned = true;
        isBigEndian = true;
        result = performOneNonRecursiveFilterTest(sampleSize, numberOfChannels, windowSize, isBigEndian, isSigned);
        if (!result) {
            System.out.print("FALSE2:");
            return false;
        }

        numberOfChannels = 3;
        windowSize = 3;
        sampleSize = 3;
        isSigned = true;
        isBigEndian = true;
        result = performOneNonRecursiveFilterTest(sampleSize, numberOfChannels, windowSize, isBigEndian, isSigned);
        if (!result) {
            System.out.print("FALSE3:");
            return false;
        }

        numberOfChannels = 4;
        windowSize = 5;
        sampleSize = 3;
        isSigned = false;
        isBigEndian = false;
        result = performOneNonRecursiveFilterTest(sampleSize, numberOfChannels, windowSize, isBigEndian, isSigned);
        if (!result) {
            System.out.print("FALSE4:");
            return false;
        }


        numberOfChannels = 7;
        windowSize = 5;
        sampleSize = 3;
        isSigned = true;
        isBigEndian = false;
        result = performOneNonRecursiveFilterTest(sampleSize, numberOfChannels, windowSize, isBigEndian, isSigned);
        if (!result) {
            System.out.print("FALSE5:");
            return false;
        }


        // Random test
        numberOfChannels = 13;
        sampleSize = 3;
        isBigEndian = true;
        isSigned = false;

        // Test for windowSizes from 1 to 10
        for (windowSize = 1; windowSize <= 11; windowSize++) {
            result = performOneNonRecursiveFilterTest(sampleSize, numberOfChannels, windowSize, isBigEndian, isSigned);
            if (!result) {
                System.out.print("FALSE6:");
                return false;
            }
        }

        // Now simple test with different coefficients

        return true;
    }

    private boolean performOneNonRecursiveFilterSmallTestDiffCoefs() {
        double[] coefs = new double[]{1, 2};
        byte[] samples = new byte[]{1, 2, 3, 4, 5, 1};
        byte[] result = new byte[]{1 * 2, 2 * 2, 1 * 1 + 2 * 3, 1 * 2 + 2 * 4, 1 * 3 + 2 * 5, 1 * 4 + 2 * 1};
        try {
            samples = NonRecursiveFilter.performNonRecursiveFilter(samples, coefs, 2, 1, 2, true, false);
        }
        catch (IOException e) {
            System.out.println("EXCEPTION" + "\t");
            return false;
        }

        if (samples.length != result.length) {
            System.out.print("Not same length:\tCorrect length:\t" + result.length +
                             "\tCalculated length:\t" + samples.length + "\t");
            return false;
        }

        for (int i = 0; i < samples.length; i++) {
            if (samples[i] != result[i]) {
                System.out.print("\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + samples[i] + "\tIndex:" + i + "\t");
                return false;
            }
        }

        return true;
    }

    /**
     * Tests whole multiples of the rates. Because for example when downsampling with not whole ratio,
     * there is just no simple way to test without implementing the method in the test itself.
     * This method returns false for not whole ratios.
     * If the downsampling method is the immediate variant, then this method returns false (because of the last frame)
     * In the last frame we always add the final frame to the result, but it doesn't correspond to this method
     * So in short, it works correctly, but the last frame is sometimes incorrect (It doesn't matter that much)
     * But sometimes might be problem. So for this reason it is better to use the downsampling by upsampling first
     * and the dropping frames.
     *
     * @param sampleSize
     * @param numberOfChannels
     * @param oldSampleRate
     * @param newSampleRate
     * @param isBigEndian
     * @param isSigned
     * @return Returns true if all tests passed, else returns false
     */
    public boolean convertSampleRateDownSampleTestUniform(int sampleSize, int numberOfChannels, int oldSampleRate,
                                                          int newSampleRate, boolean isBigEndian, boolean isSigned) {
        int frameSize = sampleSize * numberOfChannels;
        int ratio = 0;
        if (oldSampleRate > newSampleRate) {
            if (oldSampleRate % newSampleRate != 0) {
                System.out.print("Incorrect sample rates on input of test:\t");
                return false;
            }
            ratio = oldSampleRate / newSampleRate;
        }
        else {
            // Can't be tested unless I implement the method, so just return false
            return false;
        }
        int[] arrInt = new int[3 * 7 * 4];
        byte[] byteArr = fillIntArrAndReturnByteArr(arrInt, sampleSize, isBigEndian, isSigned);
        System.out.println(byteArr.length);
        byte[] calculatedArr = new byte[0];
        try {
            calculatedArr = AudioConverter.convertSampleRate(byteArr, sampleSize, frameSize, numberOfChannels,
                                                             oldSampleRate, newSampleRate, isBigEndian, isSigned, false);
        }
        catch (IOException e) {
            System.out.print("FALSE3:\t");
            return false;
        }

        int correctOutputSize = (byteArr.length / frameSize);
        if (correctOutputSize % ratio < 1) {
            correctOutputSize /= ratio;
            correctOutputSize *= frameSize;
        }
        else {
            correctOutputSize /= ratio;
            correctOutputSize = (correctOutputSize + 1) * frameSize;
        }
        if (calculatedArr.length != correctOutputSize) {
            System.out.print("FALSE4:\t" + "Correct length: " + correctOutputSize +
                             "\tCalculated length: " + calculatedArr.length + "\t");
            return false;
        }
        try {
            byteArr = NonRecursiveFilter.runLowPassFilter(byteArr, newSampleRate / 2, 64,
                                                          oldSampleRate, numberOfChannels, sampleSize, frameSize,
                                                          isBigEndian, isSigned);
        }
        catch (IOException e) {
            System.out.print("FALSE5:\t Invalid sampleSize: " + sampleSize);
            return false;
        }
        try {
            arrInt = AudioConverter.convertBytesToSamples(byteArr, sampleSize, isBigEndian, isSigned);
        }
        catch (IOException e) {
            System.out.print("FALSE1");
            return false;
        }
        if (byteArr.length != arrInt.length * sampleSize) {
            System.out.print("FALSE2:\t" + "Correct length: " + byteArr.length +
                             "\tCalculated length: " + arrInt.length * sampleSize + "\t");
            return false;
        }

        int mask = AudioUtilities.calculateMask(sampleSize);
        int skipCount = (ratio - 1) * numberOfChannels;
        for (int i = 0, intInd = 0; i < calculatedArr.length; intInd += skipCount) {
            for (int j = 0; j < numberOfChannels; j++, intInd++) {
                int sample = AudioConverter.convertBytesToInt(calculatedArr, sampleSize, mask, i, isBigEndian, isSigned);
                i += sampleSize;
                if (sample != arrInt[intInd]) {
                    System.out.print("FALSE6:\t" + "Index: " + intInd + "\tCorrect value: " + arrInt[intInd] +
                                     "\tCalculated value: " + sample + "\t");
                    return false;
                }
            }

        }

        return true;
    }


    public boolean convertSampleRateUpSampleTest1Mono() {
        int sampleSize = 1;
        boolean isBigEndian = true;
        boolean isSigned = false;
        int oldSampleRate = 22050;
        int newSampleRate = 44100;
        byte[] samples = new byte[16];
        int numberOfChannels = 1;
        int frameSize = numberOfChannels * sampleSize;
        for (int index = 0; index < samples.length; index++) {
            samples[index] = (byte) (2 * index);
        }
        byte[] result = new byte[samples.length * newSampleRate / oldSampleRate - 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (i);
        }

        byte[] calculatedArr = new byte[0];
        try {
            calculatedArr = AudioConverter.convertSampleRate(samples, sampleSize, frameSize, numberOfChannels,
                                                             oldSampleRate, newSampleRate, isBigEndian, isSigned, false);
        }
        catch (IOException e) {
            System.out.print("FALSE1");
            return false;
        }
        if (calculatedArr.length != result.length) {
            System.out.print("FALSE2:\t" + "Correct length: " + result.length +
                             "\tCalculated length: " + calculatedArr.length + "\t");
            return false;
        }
        for (int i = 0; i < calculatedArr.length; i++) {
            if (calculatedArr[i] != result[i]) {
                System.out.print("FALSE3:\t" + "Index: " + i + "\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + calculatedArr[i] + "\t");
                return false;
            }
        }


        isSigned = true;
        try {
            calculatedArr = AudioConverter.convertSampleRate(samples, sampleSize, frameSize, 1,
                                                             oldSampleRate, newSampleRate, isBigEndian, isSigned, false);
        }
        catch (IOException e) {
            System.out.print("FALSE4");
            return false;
        }
        if (calculatedArr.length != result.length) {
            System.out.print("FALSE5:\t" + "Correct length: " + result.length +
                             "\tCalculated length: " + calculatedArr.length + "\t");
            return false;
        }
        for (int i = 0; i < calculatedArr.length; i++) {
            if (calculatedArr[i] != result[i]) {
                System.out.print("FALSE6:\t" + "Index: " + i + "\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + calculatedArr[i] + "\t");
                return false;
            }
        }

        return true;
    }

    public boolean convertSampleRateUpSampleTest1Stereo() {
        int numberOfChannels = 2;
        int sampleSize = 1;
        int frameSize = numberOfChannels * sampleSize;
        boolean isBigEndian = true;
        boolean isSigned = false;
        int oldSampleRate = 22050;
        int newSampleRate = 44100;
        byte[] samples = new byte[16];
        for (int index = 0, halfIndex = 0; index < samples.length; index++, halfIndex++) {
            samples[index++] = (byte) (2 * halfIndex);
            samples[index] = (byte) (2 * halfIndex);
        }

        byte[] result = new byte[samples.length * newSampleRate / oldSampleRate - numberOfChannels];
        for (int index = 0, halfIndex = 0; index < result.length; index++, halfIndex++) {
            result[index++] = (byte) halfIndex;
            result[index] = (byte) halfIndex;
        }

        byte[] calculatedArr = new byte[0];
        try {
            calculatedArr = AudioConverter.convertSampleRate(samples, sampleSize, frameSize, numberOfChannels,
                                                             oldSampleRate, newSampleRate, isBigEndian, isSigned, false);
        }
        catch (IOException e) {
            System.out.print("FALSE1:\t");
            return false;
        }
        if (calculatedArr.length != result.length) {
            System.out.print("FALSE2:\t" + "Correct length: " + result.length +
                             "\tCalculated length: " + calculatedArr.length + "\t");
            return false;
        }
        for (int i = 0; i < calculatedArr.length; i++) {
            if (calculatedArr[i] != result[i]) {
                System.out.print("FALSE3:\t" + "Index: " + i + "\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + calculatedArr[i] + "\t");
                return false;
            }
        }

        isSigned = true;
        try {
            calculatedArr = AudioConverter.convertSampleRate(samples, sampleSize, frameSize, numberOfChannels,
                                                             oldSampleRate, newSampleRate, isBigEndian, isSigned, true);
        }
        catch (IOException e) {
            System.out.print("FALSE4");
            return false;
        }
        if (calculatedArr.length != result.length) {
            System.out.print("FALSE5:\t" + "Correct length: " + result.length +
                             "\tCalculated length: " + calculatedArr.length + "\t");
            return false;
        }
        for (int i = 0; i < calculatedArr.length; i++) {
            if (calculatedArr[i] != result[i]) {
                System.out.print("FALSE6:\t" + "Index: " + i + "\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + calculatedArr[i] + "\t");
                return false;
            }
        }

        return true;
    }

    public boolean convertSampleRateUpSampleTestUniform(int numberOfChannels) {
        int sampleSize = 1;
        int frameSize = sampleSize * numberOfChannels;
        boolean isBigEndian = true;
        boolean isSigned = false;
        int oldSampleRate = 22050;
        int newSampleRate = 44100;
        byte[] samples = new byte[8 * numberOfChannels];
        for (int index = 0, i = 0; i < samples.length; index++) {
            for (int j = 0; j < numberOfChannels; j++, i++) {
                samples[i] = (byte) (2 * index);
            }
        }
        byte[] result = new byte[samples.length * newSampleRate / oldSampleRate - numberOfChannels];
        for (int index = 0, i = 0; i < result.length; index++) {
            for (int j = 0; j < numberOfChannels; j++, i++) {
                result[i] = (byte) index;
            }
        }

        byte[] calculatedArr = new byte[0];
        try {
            calculatedArr = AudioConverter.convertSampleRate(samples, sampleSize, frameSize, numberOfChannels,
                                                             oldSampleRate, newSampleRate, isBigEndian, isSigned, true);
        }
        catch (IOException e) {
            System.out.print("FALSE1");
            return false;
        }
        if (calculatedArr.length != result.length) {
            System.out.print("FALSE2:\t" + "Correct length: " + result.length +
                             "\tCalculated length: " + calculatedArr.length + "\t");
            return false;
        }
        for (int i = 0; i < calculatedArr.length; i++) {
            if (calculatedArr[i] != result[i]) {
                System.out.print("FALSE3:\t" + "Index: " + i + "\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + calculatedArr[i] + "\t");
                return false;
            }
        }

        return true;
    }


    // Test works "only" up to 16 channels
    public boolean convertSampleRateUpSampleTestUniform(int numberOfChannels, int sampleSize,
                                                        boolean isBigEndian, boolean isSigned) {
        if (numberOfChannels > 16) {
            return false;
        }
        int frameSize = numberOfChannels * sampleSize;
        int oldSampleRate = 22050;
        int newSampleRate = 44100;
        byte[] samples = new byte[8 * numberOfChannels * sampleSize];
        for (int index = 0, i = 0; i < samples.length; index++) {
            for (int j = 0; j < numberOfChannels; j++) {
                if (isBigEndian) {
                    samples[i] = (byte) (2 * index);
                    i++;
                    for (int k = 0; k < sampleSize - 1; k++, i++) {
                        samples[i] = 0;
                    }
                }
                else {
                    for (int k = 0; k < sampleSize - 1; k++, i++) {
                        samples[i] = 0;
                    }
                    samples[i] = (byte) (2 * index);
                    i++;
                }
            }
        }
        byte[] result = new byte[samples.length * newSampleRate / oldSampleRate - (numberOfChannels * sampleSize)];
        for (int index = 0, i = 0; i < result.length; index++) {
            for (int j = 0; j < numberOfChannels; j++) {
                if (isBigEndian) {
                    result[i] = (byte) index;
                    i++;
                    for (int k = 0; k < sampleSize - 1; k++, i++) {
                        result[i] = 0;
                    }
                }
                else {
                    for (int k = 0; k < sampleSize - 1; k++, i++) {
                        result[i] = 0;
                    }
                    result[i] = (byte) index;
                    i++;
                }
            }
        }

        byte[] calculatedArr = new byte[0];
        try {
            calculatedArr = AudioConverter.convertSampleRate(samples, sampleSize, frameSize, numberOfChannels,
                                                             oldSampleRate, newSampleRate, isBigEndian, isSigned, true);
        }
        catch (IOException e) {
            System.out.print("FALSE1");
            return false;
        }
        if (calculatedArr.length != result.length) {
            System.out.print("FALSE2:\t" + "Correct length: " + result.length +
                             "\tCalculated length: " + calculatedArr.length + "\t");
            return false;
        }
        for (int i = 0; i < calculatedArr.length; i++) {
            if (calculatedArr[i] != result[i]) {
                System.out.print("FALSE3:\t" + "Index: " + i + "\tCorrect value: " + result[i] +
                                 "\tCalculated value: " + calculatedArr[i] + "\t");
                return false;
            }
        }

        return true;
    }


    public void convertSampleRateTests() {
        System.out.println("convertSampleRateUpSampleTest1Mono():\t" + convertSampleRateUpSampleTest1Mono());
        System.out.println("convertSampleRateUpSampleTest1Stereo():\t" + convertSampleRateUpSampleTest1Stereo());

        int sampleSize = 2;
        int numberOfChannels = 2;
        int newSampleRate = 22050;
        int oldSampleRate = newSampleRate * 2;
        boolean isBigEndian = false;
        boolean isSigned = false;
        boolean result;

        result = convertSampleRateDownSampleTestUniform(sampleSize, numberOfChannels, oldSampleRate,
                                                        newSampleRate, isBigEndian, isSigned);
        System.out.println("convertSampleRateDownSampleTestUniform1:\t" + result);

        isBigEndian = true;
        isSigned = false;
        result = convertSampleRateDownSampleTestUniform(sampleSize, numberOfChannels, oldSampleRate,
                                                        newSampleRate, isBigEndian, isSigned);
        System.out.println("convertSampleRateDownSampleTestUniform2:\t" + result);

        isBigEndian = false;
        isSigned = true;
        result = convertSampleRateDownSampleTestUniform(sampleSize, numberOfChannels, oldSampleRate,
                                                        newSampleRate, isBigEndian, isSigned);
        System.out.println("convertSampleRateDownSampleTestUniform3:\t" + result);

        isBigEndian = true;
        isSigned = true;
        result = convertSampleRateDownSampleTestUniform(sampleSize, numberOfChannels, oldSampleRate,
                                                        newSampleRate, isBigEndian, isSigned);
        System.out.println("convertSampleRateDownSampleTestUniform4:\t" + result);

        sampleSize = 3;
        numberOfChannels = 7;
        result = convertSampleRateDownSampleTestUniform(sampleSize, numberOfChannels, oldSampleRate,
                                                        newSampleRate, isBigEndian, isSigned);
        System.out.println("convertSampleRateDownSampleTestUniform5:\t" + result);

        newSampleRate = 22050;
        oldSampleRate = newSampleRate * 4;
        sampleSize = 3;
        numberOfChannels = 7;
        result = convertSampleRateDownSampleTestUniform(sampleSize, numberOfChannels, oldSampleRate,
                                                        newSampleRate, isBigEndian, isSigned);
        System.out.println("convertSampleRateDownSampleTestUniform6\t" + result);


        newSampleRate = 22050;
        oldSampleRate = newSampleRate * 5;
        sampleSize = 3;
        numberOfChannels = 3;
        result = convertSampleRateDownSampleTestUniform(sampleSize, numberOfChannels, oldSampleRate,
                                                        newSampleRate, isBigEndian, isSigned);
        System.out.println("convertSampleRateDownSampleTestUniform7\t" + result);


        numberOfChannels = 1;
        result = convertSampleRateUpSampleTestUniform(numberOfChannels);
        System.out.println("convertSampleRateUpSampleTestUniformChannel1:\t" + result);
        numberOfChannels = 2;
        result = convertSampleRateUpSampleTestUniform(numberOfChannels);
        System.out.println("convertSampleRateUpSampleTestUniformChannel2:\t" + result);
        numberOfChannels = 3;
        result = convertSampleRateUpSampleTestUniform(numberOfChannels);
        System.out.println("convertSampleRateUpSampleTestUniformChannel3:\t" + result);
        numberOfChannels = 4;
        result = convertSampleRateUpSampleTestUniform(numberOfChannels);
        System.out.println("convertSampleRateUpSampleTestUniformChannel4:\t" + result);

        numberOfChannels = 5;
        sampleSize = 3;
        isBigEndian = false;
        isSigned = false;
        result = convertSampleRateUpSampleTestUniform(numberOfChannels, sampleSize, isBigEndian, isSigned);
        System.out.println("convertSampleRateUpSampleTestUniform1:\t" + result);

        numberOfChannels = 5;
        sampleSize = 3;
        isBigEndian = true;
        isSigned = true;
        result = convertSampleRateUpSampleTestUniform(numberOfChannels, sampleSize, isBigEndian, isSigned);
        System.out.println("convertSampleRateUpSampleTestUniform2:\t" + result);

        numberOfChannels = 7;
        sampleSize = 2;
        isBigEndian = false;
        isSigned = false;
        result = convertSampleRateUpSampleTestUniform(numberOfChannels, sampleSize, isBigEndian, isSigned);
        System.out.println("convertSampleRateUpSampleTestUniform3:\t" + result);

        numberOfChannels = 9;
        sampleSize = 4;
        isBigEndian = true;
        isSigned = false;
        result = convertSampleRateUpSampleTestUniform(numberOfChannels, sampleSize, isBigEndian, isSigned);
        System.out.println("convertSampleRateUpSampleTestUniform4:\t" + result);

        numberOfChannels = 9;
        sampleSize = 4;
        isBigEndian = true;
        isSigned = true;
        result = convertSampleRateUpSampleTestUniform(numberOfChannels, sampleSize, isBigEndian, isSigned);
        System.out.println("convertSampleRateUpSampleTestUniform4:\t" + result);
    }


    // Tests for subband splitters are very difficult to be made, so i just check it by debug print
    public static boolean testSubbandSplitterLinear(int subbandCount, int arrLen) {
        SubbandSplitterIFace s = new SubbandSplitterLinear(32);
        return testGetSubbandRealForward(s, subbandCount, arrLen);
    }

    public static boolean testSubbandSplitterLogarithimic(int subbandCount, int arrLen) {
        SubbandSplitterIFace s = new SubbandSplitterLogarithmic();
        return testGetSubbandRealForward(s, subbandCount, arrLen);
    }

    public static boolean testSubbandSplitterConstant(int sampleRate, int subbandCount, int arrLen) {
        SubbandSplitterIFace s = new SubbandSplitter(sampleRate, 200, subbandCount);
        return testGetSubbandRealForward(s, subbandCount, arrLen);
    }

    public static boolean testGetSubbandRealForward(SubbandSplitterIFace splitter, int subbandCount, int arrLen) {
        double[] arr = new double[arrLen];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i + 1;
        }
        for (int subband = 0; subband < subbandCount; subband++) {
            double[] result = new double[arr.length];
            int startIndex = -1;
            int counter = 0;
            int endIndex = -1;
            splitter.getSubband(arr, subbandCount, subband, result);
            for (int i = 0; i < result.length; i++) {
                if (result[i] != 0) {
                    System.out.println("Test: " + i);
                    counter++;
                    endIndex = i;
                    if (startIndex == -1) {
                        startIndex = i;
                    }
                }
            }
            System.out.println(subband + "\t" + startIndex + "\t" + endIndex + "\t" + counter);
        }

        return true;
    }


    public boolean createFFTWindowTest(double freq, int sampleRate, int sampleSize, int numberOfChannels,
                                       boolean isBigEndian, boolean isSigned, int startIndex,
                                       int windowSize, int windowWidth, int windowHeight) {
        int neededLenInSecs = (int) Math.ceil((windowSize + startIndex) / (double) sampleRate);
        byte[] song = Note.generateFrequencyFreq(freq, sampleRate, neededLenInSecs, sampleSize, isBigEndian, isSigned);
        double[] songDouble;
        try {
            songDouble = AudioConverter.normalizeToDoubles(song, sampleSize, sampleSize * 8,
                                                           isBigEndian, isSigned);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }


        double freqJump = AudioUtilities.computeFreqJump(sampleRate, windowSize);
        createFFTWindow(songDouble, windowSize, startIndex, freqJump);

        return true;
    }

    private void createFFTWindow(double[] song, int windowSize, int startIndex, double freqJump) {
        JFrame frame = new JFrame();
        frame.setContentPane(new FFTWindowPanel(song, windowSize, startIndex, freqJump,
                                                true, Color.lightGray, false));

        // Instead of pack
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double w = screenSize.getWidth();
        double h = screenSize.getHeight();
        frame.setSize((int) w, (int) h / 2);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // if you want the X button to close the app
    }


    private static void createTestWindow(BufferedImage img) {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // if you want the X button to close the app
    }


    public static void testGetAbsoluteValueGeneral() {
        System.out.println("testIfGetAbsoluteValueGeneralPositive():\t" + testIfGetAbsoluteValueGeneralPositive());
        System.out.println("testIfGetAbsoluteValueGeneralNegative():\t" + testIfGetAbsoluteValueGeneralNegative());
    }


    public static boolean testIfGetAbsoluteValueGeneralPositive() {
        Random rand = new Random();

        for (int i = 0; i < 10000; i++) {
            int value = rand.nextInt();
            int zero = rand.nextInt();
            if (getAbsoluteValueGeneralPositiveBranching(value, zero) != getAbsoluteValueGeneralPositiveNoBranching(value, zero)) {
                return false;
            }
        }

        return true;
    }

    private static boolean testIfGetAbsoluteValueGeneralNegative() {
        Random rand = new Random();

        for (int i = 0; i < 10000; i++) {
            int value = rand.nextInt();
            int zero = rand.nextInt();
            if (getAbsoluteValueGeneralNegativeBranching(value, zero) != getAbsoluteValueGeneralNegativeNoBranching(value, zero)) {
                return false;
            }
        }

        return true;
    }


    private static int getAbsoluteValueGeneralPositiveNoBranching(int value, int zero) {
        // Version without branching
        int dif = value - zero;
        int sign = Integer.signum(dif);
        int returnVal = zero + (sign * dif);      // Doesn't need if branching
        return returnVal;
    }

    private static int getAbsoluteValueGeneralPositiveBranching(int value, int zero) {
        // Version with branching
        if (value > zero) {
            return value;
        }
        else {
            // zero - value tells how much it is under zero, so we just add that number to zero, to get the positive one
            return zero + (zero - value);
        }
    }

    private static int getAbsoluteValueGeneralNegativeNoBranching(int value, int zero) {
        // Version without branching
        int dif = value - zero;
        int sign = -Integer.signum(dif);
        int returnVal = zero + (sign * dif);      // Doesn't need if branching
        return returnVal;
    }


    private static int getAbsoluteValueGeneralNegativeBranching(int value, int zero) {
        // Version with branching
        if (value > zero) {
            // value - zero tells how much it is above zero, so we subtract that number from zero
            return zero - (value - zero);
        }
        else {
            return value;
        }
    }


    public static boolean testSetOneDimArr() {
        double[] array = new double[100];
        double[] correctResult = new double[array.length];
        Random rand = new Random();
        for (int i = 0; i < array.length; i++) {
            for (int j = array.length; j > i; j--) {
                Arrays.fill(array, 0);
                Arrays.fill(correctResult, 0);
                double val = rand.nextDouble();
                Utilities.setOneDimArr(array, i, j, val);
                for (int k = i; k < j; k++) {
                    correctResult[k] = val;
                }
                if (!checkEqualityOfArraysOneDim(array, correctResult, 0, array.length, 0)) {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Method to try if 2 bins are equal, I made it because the FFT was giving weird results, but it was because I
     * misunderstood the results of FFT.
     *
     * @param windowSize
     * @param testCount
     * @return
     */
    @Deprecated
    public static void testFFTBinCount(int windowSize, int testCount) {
        double[] arr = new double[windowSize];
        DoubleFFT_1D fft = new DoubleFFT_1D(windowSize);
        int differentBinsCount = 0;

        for (int i = 0; i < testCount; i++) {
            Utilities.fillArrWithRandomValues(arr, 1);
            fft.realForward(arr);


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
            if (arr.length % 2 == 0) {
                //if(!(arr[1] == arr[arr.length - 2] && arr[arr.length -1] == 0)) {
                if (arr[1] != arr[arr.length - 2]) {
                    differentBinsCount++;
                }
            }
            else {
                if (arr[1] != arr[arr.length - 1]) {
                    differentBinsCount++;
                }
            }
        }


        ProgramTest.debugPrint("testFFTBinCount (dif/total):", differentBinsCount + "/" + testCount,
                               "windowSize:", windowSize);
    }


    public static void printRealFFT(int len, double amp, double freq, double phase,
                                    int sampleRate, Utilities.CURVE_TYPE curve) {
        DoubleFFT_1D fft = new DoubleFFT_1D(len);
        double[] sine;
        sine = curve.createCurve(len, amp, freq, sampleRate, phase);
// TODO: Nevim ted
// Doesn't even help, the results are just wrong because it is the precision error with double values.
//        double sum = ByteWave.performAggregation(sine, Aggregation.SUM);
//        for(int i = 0; i < sine.length; i++) {
//            sine[i] -= sum;
//        }

        fft.realForward(sine);

        //ProgramTest.debugPrint(sine);
        printFFTValuesOverThreshold(len, sine, amp);
    }

    public static void printComplexFFT(int len, double realAmp, double realFreq, double realPhase,
                                       double imagAmp, double imagFreq, double imagPhase,
                                       int sampleRate, Utilities.CURVE_TYPE curve) {
        int complexLen = 2 * len;
        DoubleFFT_1D fft = new DoubleFFT_1D(len);
        double[] sine;
        double[] arr = new double[complexLen];
        sine = curve.createCurve(len, realAmp, realFreq, sampleRate, realPhase);
        FFT.realToComplexRealOnly(sine, arr, false);
        sine = curve.createCurve(len, imagAmp, imagFreq, sampleRate, imagPhase);
        FFT.realToComplexImagOnly(sine, arr, false);
        fft.complexForward(arr);

        //ProgramTest.debugPrint(arr);
        printFFTValuesOverThreshold(len, arr, realAmp, imagAmp);
    }


    public static void printComplexFFTRealOnly(int len,
                                               double realAmp, double realFreq, double realPhase,
                                               int sampleRate, Utilities.CURVE_TYPE curve) {
        int complexLen = 2 * len;
        // The length of the window is in number of complex numbers not total length of array
        DoubleFFT_1D fft = new DoubleFFT_1D(len);
        double[] sine;
        double[] arr = new double[complexLen];
        sine = curve.createCurve(len, realAmp, realFreq, sampleRate, realPhase);
        FFT.realToComplexRealOnly(sine, arr, true);
        fft.complexForward(arr);

        //ProgramTest.debugPrint(arr);
        printFFTValuesOverThreshold(len, arr, realAmp);
    }

    public static void printComplexFFTImagOnly(int len,
                                               double imagAmp, double imagFreq, double imagPhase,
                                               int sampleRate, Utilities.CURVE_TYPE curve) {
        int complexLen = 2 * len;
        DoubleFFT_1D fft = new DoubleFFT_1D(len);
        double[] sine = new double[len];
        double[] arr = new double[complexLen];
        sine = curve.createCurve(len, imagAmp, imagFreq, sampleRate, imagPhase);
        FFT.realToComplexImagOnly(sine, arr, true);
        fft.complexForward(arr);

        //ProgramTest.debugPrint(arr);
        printFFTValuesOverThreshold(len, arr, imagAmp);
    }


    public static void printComplexIFFT(int len,
                                        int realIndex, double realAmp,
                                        int imagIndex, double imagAmp,
                                        boolean shouldIFFTScale) {
        int complexLen = 2 * len;
        DoubleFFT_1D fft = new DoubleFFT_1D(len);

        double[] arr = new double[complexLen];

        arr[2 * realIndex] = realAmp;
        arr[2 * len + 2 * imagIndex + 1] = imagAmp;

        fft.complexInverse(arr, shouldIFFTScale);
        ProgramTest.debugPrint(arr);
    }

    public static void printComplexIFFT(int len,
                                        int[] realIndices, double[] realAmps,
                                        int[] imagIndices, double[] imagAmps,
                                        boolean shouldIFFTScale) {
        int complexLen = 2 * len;
        DoubleFFT_1D fft = new DoubleFFT_1D(len);

        double[] arr = new double[complexLen];
        for (int i = 0; i < realIndices.length; i++) {
            arr[2 * realIndices[i]] = realAmps[i];
            arr[2 * len + 2 * imagIndices[i] + 1] = imagAmps[i];
        }

        fft.complexInverse(arr, shouldIFFTScale);

        //ProgramTest.debugPrint(arr);
        printFFTValuesOverThreshold(len, arr, realAmps, imagAmps);
    }


    private static double EPSILON = 0.001;

    /**
     * @param arr
     * @param threshold is between -1 and 1.
     */
    private static void printFFTValuesOverThreshold(int windowSize, double[] arr, double threshold) {
        threshold = Math.abs(threshold);
        threshold *= (windowSize / 2);
        ProgramTest.debugPrint("\n\n\nPRINTING VALUES OVER THRESHOLD:", threshold, "ARR LEN:", arr.length);
        threshold -= EPSILON;
        for (int i = 0; i < arr.length; i++) {
            if (Math.abs(arr[i]) > threshold) {
                ProgramTest.debugPrint(i, ":", arr[i]);
            }
        }
        ProgramTest.debugPrint("END OF PRINTING VALUES OVER THRESHOLD");
    }


    /**
     * @param arr
     * @param thresholds are between -1 and 1.
     */
    private static void printFFTValuesOverThreshold(int windowSize, double[] arr, double[]... thresholds) {
        double max = Integer.MIN_VALUE;
        for (int i = 0; i < thresholds.length; i++) {
            for (int j = 0; j < thresholds[i].length; j++) {
                double val = Math.abs(thresholds[i][j]);
                if (val > max) {
                    max = val;
                }
            }
        }
        printFFTValuesOverThreshold(windowSize, arr, max);
    }


    private static void printFFTValuesOverThreshold(int windowSize, double[] arr, double... thresholds) {
        double max = Integer.MIN_VALUE;
        for (int i = 0; i < thresholds.length; i++) {
            double threshold = Math.abs(thresholds[i]);
            if (threshold > max) {
                max = threshold;
            }
        }
        printFFTValuesOverThreshold(windowSize, arr, max);
    }


    public static void tryFFTSums(int iterationCount) {
        Random r = new Random();
        for (int i = 0; i < iterationCount; i++) {
            int exponent = r.nextInt(9) + 4;
            //int exponent = 3;
            double[] audio = Utilities.CURVE_TYPE.RANDOM.createCurve(1 << exponent, 1,
                                                                     0, 0, 0);
            //double[] audio =  ByteWave.CURVE_TYPE.LINE.createCurve(1 << exponent, 1, 0, 0, 0);
            DoubleFFT_1D fft = new DoubleFFT_1D(audio.length);
            FFT.calculateFFTRealForward(audio, 0, audio.length, 1, fft, audio);
            for (int j = 0; j < audio.length; j++) {
                //audio[j] /= (audio.length / 2);
                audio[j] /= audio.length;
            }
            double[] measures = new double[FFT.getBinCountRealForward(audio.length)];
            FFT.convertResultsOfFFTToRealRealForward(audio, measures);

            for (int j = 0; j < audio.length; j++) {
                audio[j] = Math.abs(audio[j]);
            }

            double sum1 = Aggregation.performAggregation(audio, Aggregation.SUM);
            double sum2 = Aggregation.performAggregation(measures, Aggregation.SUM);

            sum2 -= measures[0];
            sum2 -= measures[measures.length - 1];
            if (sum1 >= 1 || sum2 >= 1) {
                ProgramTest.debugPrint("SUM IS BIGGER THAN ONE:", sum1, sum2, "Size:", audio.length);
                ProgramTest.debugPrint(audio);
                ProgramTest.debugPrint(measures);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Debug methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void printArray(byte[] arr) {
        System.out.println("!!!Printing array!!!");
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }


    public static void printNTimes(String stringToPrint, int n) {
        for (int i = 0; i < n; i++) {
            System.out.println(stringToPrint);
        }
    }


    public static void printCharKTimesOnNLines(char c) {
        printCharKTimesOnNLines(c, 1, 1);
    }

    public static void printCharKTimesOnNLines(char c, int k, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < k; i++) {
            sb.append(c);
        }
        String s = sb.toString();
        for (int i = 0; i < n; i++) {
            printNTimes(s, n);
        }
    }


    /**
     * Specific variant of debugPrintWithSep. Uses tabulator (\t) as separator.
     *
     * @param texts are the strings, which will be written to output separated by \t.
     */
    public static void debugPrint(String... texts) {
        debugPrintWithSep("\t", texts);
    }

    /**
     * Writes the strings separated by separator. Puts new line at the end.
     *
     * @param separator is the separator which will be put between the strings.
     * @param texts     are the strings, which will be written to output separated by separator.
     */
    public static void debugPrintWithSep(String separator, String... texts) {
        // Classic implementation
//        int i;
//        for(i = 0; i < texts.length - 1; i++) {
//            System.out.print(texts[i] + separator);
//        }
//        System.out.println(texts[i]);

        //https://stackoverflow.com/questions/9633991/how-to-check-if-processing-the-last-item-in-an-iterator
        String previousSeparator = "";
        for (String text : texts) {
            System.out.print(previousSeparator + text);
            previousSeparator = separator;
        }
        System.out.println();
    }

    /**
     * Uses refraction - so it is slow. Specific variant of debugPrintWithSep. Uses tabulator (\t) as separator.
     *
     * @param objects are the objects, on which will be called .toString() and
     *                will be written to output separated by \t.
     */
    public static void debugPrint(Object... objects) {
        debugPrintWithSep("\t", objects);
    }

    /**
     * Uses refraction - so it is slow. Writes the objects separated by separator. Puts new line at the end.
     *
     * @param separator is the separator which will be put between the objects.
     * @param objects   are the objects, on which will be called .toString() and
     *                  will be written to output separated by separator.
     */
    public static void debugPrintWithSep(String separator, Object... objects) {
        //https://stackoverflow.com/questions/9633991/how-to-check-if-processing-the-last-item-in-an-iterator
        String previousSeparator = "";
        for (Object o : objects) {
            System.out.print(previousSeparator + debugPrintObject(o));
            previousSeparator = separator;
        }
        System.out.println();
    }

    /**
     * Uses refraction to check if the given object is array, if so,
     * then print is it in audioFormat index:\t value [space]. Else returns o.toString()
     *
     * @param o
     * @return
     */
    public static String debugPrintObject(Object o) {
        Class<? extends Object> c = o.getClass();
        if (c.isArray()) {
            StringBuilder ret = new StringBuilder();
            int arrLen;
            ret.append("NOW PRINTING ARRAY:\n");
            // Check if it is array of primitives, because it needs to be iterated differently
            if (c.getComponentType().isPrimitive()) {
                arrLen = Array.getLength(o);
                for (int i = 0; i < arrLen; i++) {
                    Object obj = Array.get(o, i);
                    ret.append(i);
                    ret.append(':');
                    ret.append('\t');
                    ret.append(obj);
                    appendSeparatorInArray(ret, i);
                }
            }
            else {
                Object[] arr = (Object[]) o;
                arrLen = arr.length;
                for (int i = 0; i < arr.length; i++) {
                    ret.append(i);
                    ret.append(':');
                    ret.append('\t');
                    ret.append(arr[i]);
                    appendSeparatorInArray(ret, i);
                }
            }

            if (!shouldPutNewLineToArrayPrint(arrLen - 1)) {     // If the last separator wasn't \n
                ret.append('\n');
            }
            ret.append("STOPPED PRINTING ARRAY:\n");
            return ret.toString();
        }
        else {
            return o.toString();
        }
    }


    private static final int ARR_INDICES_ON_LINE = 2;

    private static void appendSeparatorInArray(StringBuilder sb, int index) {
        if (shouldPutNewLineToArrayPrint(index)) {
            sb.append('\n');       // Separator
        }
        else {
            sb.append(' ');        // Separator
        }
    }

    private static boolean shouldPutNewLineToArrayPrint(int index) {
        return index % ARR_INDICES_ON_LINE == 0;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Debug methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
