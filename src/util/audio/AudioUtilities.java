package util.audio;


import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AudioUtilities {
    private AudioUtilities() { }      // Allow only static access

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Constants
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String LOG_MESSAGE_WHEN_SET_VARIABLES_RETURN_FALSE =
            "Probably invalid audioFormat or the file wasn't audio or the path was invalid";

    /**
     * Mask for top 8 bits in int
     */
    private static final int TOP_8_BITS_MASK = 0xFF_00_00_00;
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Constants
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Mask calculation methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates mask used for converting the byte array to number of size sampleSize bytes, which must fit to int.
     * The mask has the top sampleSize * 8 bits set to 1, the rest is set to 0
     *
     * @param sampleSize is the size of 1 sample in bytes
     * @return returns the mask which is used for converting the byte array to int
     * @throws IOException is thrown when the sample size > 4, because then the samples can't fit to int, or when it is <= 0
     */
    public static int calculateMask(int sampleSize) {
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
    public static int calculateInverseMask(int sampleSize) {
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Mask calculation methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Play audio methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Plays the audio given in the 1D array song in audio audioFormat given as parameter.
     *
     * @param song          is the audio with the samples to be played.
     * @param audioFormat   is the audio audioFormat.
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
        if (playBackwards) {
            AudioProcessor.reverseArr(song, audioFormat.getSampleSizeInBits() / 8);
        }
        // because number of frames needs to be integer, so if some last bytes doesn't fit in the last frame,
        // we don't play them
        int bytesToWrite = song.length - (song.length % frameSize);
        bytesWritten = line.write(song, 0, bytesToWrite);
        line.drain();
    }

    /**
     * Plays the audio given in the 1D array song, other parameters of this method describe the audioFormat in which will be the audio played.
     *
     * @param song             is 1D byte array which contains the samples, which will be played.
     * @param encoding         is the encoding of the audio data.
     * @param sampleRate       is the sample rate of the audio data.
     * @param sampleSizeInBits is the size of 1 sample in bits.
     * @param numberOfChannels represents the number of channels.
     * @param frameSize        is the size of one frame.
     * @param frameRate        is the frame rate of the audio.
     * @param isBigEndian      is true if the samples are in big endian, false if in little endian
     * @param playBackwards    if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * @throws LineUnavailableException is thrown when there is problem with feeding the data to the SourceDataLine.
     */
    public static void playSong(byte[] song, AudioFormat.Encoding encoding, int sampleRate, int sampleSizeInBits,
                                int numberOfChannels, int frameSize, float frameRate, boolean isBigEndian,
                                boolean playBackwards) throws LineUnavailableException {
        AudioFormat audioFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits, numberOfChannels, frameSize, frameRate, isBigEndian);
        playSong(song, audioFormat, playBackwards);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Play audio methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Max absolute value methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int getMaxAbsoluteValue(int sampleSizeInBits, boolean isSigned) {
        if (isSigned) {
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Max absolute value methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Frequencies help methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static double computeFreqJump(int sampleRate, double windowSize) {
        double result = sampleRate / (double) windowSize;
        return result;
    }

    // Get frequencies in khz
    public static String[] computeFreqs(int binCount, double freqJump, double startFreq, int takeEveryNthFreq, int precision) {
        int len = 1 + (binCount - 1) / takeEveryNthFreq;  // -1 Because for example for binCount = takeEveryNthFreq = 4 I'd have 2 without the -1
        String[] binFreqs = new String[len];
        double currFreqHz = startFreq;
        for (int i = 0; i < binFreqs.length; i++, currFreqHz += freqJump * takeEveryNthFreq) {
            double currFreqKhz = currFreqHz / 1000;
            String freqString = String.format("%." + precision + "f", currFreqKhz);
            binFreqs[i] = freqString;
        }

        return binFreqs;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Frequencies help methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert frames to time methods.
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int convertFrameToSecs(int frame, int sizeOfOneSec) {
        return frame / sizeOfOneSec;
    }

    /**
     * Doesn't work for long audio files - 596 hours+ (more exactly 2 147 483secs / 60 / 60)
     *
     * @param frame
     * @param sizeOfOneSec
     * @return
     */
    public static int convertFrameToMillis(int frame, int sizeOfOneSec) {
        return (int) (1000 * (double) frame / sizeOfOneSec);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert frames to time methods.
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Other help methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int calculateSizeOfOneSec(int sampleRate, int frameSize) {
        return sampleRate * frameSize;
    }

    public static int calculateFrameSize(AudioFormat format) {
        return format.getChannels() * format.getSampleSizeInBits() / 8;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Other help methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    ///////////////////                        Not used Methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// White noise methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Fills given array with random numbers from range lowestRandom to highestRandom. Usually from -1 to 1.
     * Where 1 random generated number is used to set next repeatedNumbersCount samples.
     * Changes the values in array.
     *
     * @param arr                  is the array to be filled.
     * @param lowestRandom         is the lowest possible number to be generated. Usually -1.
     * @param highestRandom        is the highest possible number to be generated. Usually 1.
     * @param repeatedNumbersCount is number of samples to be set with 1 random number.
     */
    public static void generateWhiteNoiseWithRepeatByRef(double[] arr, int repeatedNumbersCount, double lowestRandom, double highestRandom) {
        double random;
        for (int i = 0; i < arr.length; ) {
            random = ThreadLocalRandom.current().nextDouble(lowestRandom, highestRandom);
            for (int j = 0; j < repeatedNumbersCount; j++, i++) {
                arr[i] = random;
            }
        }
    }


    /**
     * Fills given array with random numbers from range lowestRandom to highestRandom. Usually from -1 to 1.
     * Where 1 random generated number is used to set next repeatedNumbersCount samples.
     *
     * @param len                  is the length of the array to be filled with random noise.
     * @param repeatedNumbersCount is number of samples to be set with 1 random number.
     * @param lowestRandom         is the lowest possible number to be generated. Usually -1.
     * @param highestRandom        is the highest possible number to be generated. Usually 1.
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
     *
     * @param arr           is the array to be filled.
     * @param n             is number of samples after which will be next random number generated.
     * @param lowestRandom  is the lowest possible number to be generated. Usually -1.
     * @param highestRandom is the highest possible number to be generated. Usually 1.
     *                      That means: Generate random number every nth sample.
     */
    public static void generateWhiteNoiseWithLinearInterpolationByRef(double[] arr, int n, double lowestRandom, double highestRandom) {
        double random1;
        double random2 = ThreadLocalRandom.current().nextDouble(lowestRandom, highestRandom);
        double jump;


        for (int i = 0; i < arr.length; ) {
            random1 = random2;
            random2 = ThreadLocalRandom.current().nextDouble(lowestRandom, highestRandom);
            jump = (random2 - random1) / n;

            arr[i] = random1;
            i++;
            for (int j = 0; j < n; j++, i++) {
                arr[i] = random1;
                random1 += jump;
            }
            arr[i] = random2;
            i++;
        }
    }


    /**
     * Same as generateWhiteNoiseWithLinearInterpolationByRef but the array is returned and created internally.
     *
     * @param len           is the length of the array to be filled with random noise.
     * @param n             is number of samples after which will be next random number generated.
     * @param lowestRandom  is the lowest possible number to be generated. Usually -1.
     * @param highestRandom is the highest possible number to be generated. Usually 1.
     * @return Returns white noise with linear interpolation.
     */
    public static double[] generateWhiteNoiseWithLinearInterpolationByCopy(int len, int n, double lowestRandom, double highestRandom) {
        double[] retArr = new double[len];
        generateWhiteNoiseWithLinearInterpolationByRef(retArr, n, lowestRandom, highestRandom);
        return retArr;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// White noise methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Set samples to random values methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Probability 1 - probabilityToContinue is the probability that the method will end.
     * Until the method ends, it chooses random sample in each iteration which will be set with parameter number.
     * Changes the input array.
     *
     * @param samples               is the input array.
     * @param number                is the number with which will be set randomly chosen sample.
     * @param probabilityToContinue is the probability that we will set some next sample with parameter number.
     */
    public static void setRandomSamplesToNumberByRef(double[] samples, double number, double probabilityToContinue) {
        Random rand = new Random();
        int index;

        while (rand.nextDouble() < probabilityToContinue) {
            index = rand.nextInt(samples.length);
            samples[index] = number;
        }
    }

    /**
     * Same as setRandomSamplesToNumberByRef but doesn't change the input array.
     *
     * @param samples               is the input array.
     * @param number                is the number with which will be set randomly chosen sample.
     * @param probabilityToContinue is the probability that we will set some next sample with parameter number.
     * @return Returns copy of the samples array where random samples are set to number.
     */
    public static double[] setRandomSamplesToNumberByCopy(double[] samples, double number, double probabilityToContinue) {
        double[] retArr = new double[samples.length];
        for (int i = 0; i < samples.length; i++) {
            retArr[i] = samples[i];
        }
        setRandomSamplesToNumberByRef(retArr, number, probabilityToContinue);

        return retArr;
    }


    /**
     * Probability 1 - probabilityToContinue is the probability that the method will end.
     * Until the method ends, it chooses random sample in each iteration which will be set to random double number between
     * lowestRandom and highestRandom.
     * Changes the input array.
     *
     * @param samples               is the input array.
     * @param probabilityToContinue is the probability that we will set some next sample with parameter number.
     * @param lowestRandom          is the lowest possible number to be generated. Usually -1.
     * @param highestRandom         is the highest possible number to be generated. Usually 1.
     */
    public static void setRandomSamplesToRandomNumberByRef(double[] samples, double probabilityToContinue, double lowestRandom, double highestRandom) {
        Random rand = new Random();
        int index;

        while (rand.nextDouble() < probabilityToContinue) {
            index = rand.nextInt(samples.length);
            samples[index] = ThreadLocalRandom.current().nextDouble(lowestRandom, highestRandom);
            ;
        }
    }

    /**
     * Same as setRandomSamplesToNumberByRef but doesn't change the input array.
     *
     * @param samples               is the input array.
     * @param probabilityToContinue is the probability that we will set some next sample with parameter number.
     * @param lowestRandom          is the lowest possible number to be generated. Usually -1.
     * @param highestRandom         is the highest possible number to be generated. Usually 1.
     * @return Returns copy of the samples array where random samples are set to number.
     */
    public static double[] setRandomSamplesToRandomNumberByCopy(double[] samples, double probabilityToContinue, double lowestRandom, double highestRandom) {
        double[] retArr = new double[samples.length];
        for (int i = 0; i < samples.length; i++) {
            retArr[i] = samples[i];
        }
        setRandomSamplesToRandomNumberByRef(retArr, probabilityToContinue, lowestRandom, highestRandom);

        return retArr;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Set samples to random values methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    ///////////////////                        Not used Methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
