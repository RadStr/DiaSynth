package util.audio;

import Rocnikovy_Projekt.Program;
import util.Aggregation;
import util.audio.io.AudioReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AudioProcessor {
    private AudioProcessor() {}          // Allow only static access

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Methods for getting certain part(s) of audio
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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


    // TODO: Called only from test
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
     * @param startSample is the number of sample to start at
     * @return Returns 1D array containing every nth sample of size sampleSize
     * @throws IOException is thrown when error with InputStream occurred or if the sampleSize is not multiple of the
     *                     original sampleSize or if the startSample is not multiple of the original sampleSize
     */
    public static byte[] getEveryNthSampleMono(InputStream samples, int sampleSize, int n, int startSample) throws IOException {
        int bytesRead = 0;
        byte[] arr = new byte[sampleSize * n];

        if (arr.length % sampleSize != 0 || startSample % sampleSize != 0) {        // limitation of java library
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


    // TODO: Called only from test
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
    public static byte[] getEveryNthSampleMono(byte[] samples, int sampleSize, int n, int startSample) {
        // Solved by calling more general method
        byte[][] newSamples = getEveryXthTimePeriodWithLength(samples, 1, n, sampleSize, startSample);

        return Program.convertTwoDimArrToOneDim(newSamples);
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
     * @return Returns 2D byte array, where each array represents the channels where only every n-th sample is taken
     * @throws IOException is thrown when the error in input stream occurred
     */
    public static byte[][] getEveryNthSampleMoreChannels(InputStream samples, int numberOfChannels, int sampleSize,
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
    public static double[][] getEveryNthSampleMoreChannelsDoubleOldAndSlow(InputStream samples, int numberOfChannels,
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
    public static double[][] getEveryNthSampleMoreChannelsDouble(InputStream samples, int numberOfChannels,
                                                                 int sampleSize, int n, int startSample,
                                                                 boolean isBigEndian, boolean isSigned,
                                                                 int totalAudioLength) throws IOException {
        int channelLen = getLengthOfOneChannelInSamplesForSampleSkipping(totalAudioLength, startSample, n,
            numberOfChannels, sampleSize);
        double[][] outputArr = new double[numberOfChannels][channelLen];

        int frameSize = sampleSize * numberOfChannels;
        int FRAME_COUNT = 2048;
        byte[] buffer = new byte[frameSize * FRAME_COUNT];

        int mask = AudioUtilities.calculateMask(sampleSize);
        int maxAbsoluteValue = AudioUtilities.getMaxAbsoluteValueSigned(8 * sampleSize);

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
                    outputArr[i][outputIndex] = Program.normalizeToDouble(sample, maxAbsoluteValue, isSigned);
                }

                outputIndex++;
                nextNByteIndex += n * frameSize;
            }
        }

        return outputArr;
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
    public static byte[][] getEveryNthSampleMoreChannels(byte[] samples, int numberOfChannels, int sampleSize, int n, int startSample) {
        byte[][] arr = new byte[numberOfChannels][];

        for (int i = 0; i < numberOfChannels; i++) {
            arr[i] = getEveryNthSampleMono(samples, sampleSize, n * numberOfChannels, i + startSample);
        }

        return arr;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Methods for getting certain part(s) of audio
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Audio modification methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
    public static byte[][] modifySamplesMoreChannels(byte[][] channels, int n, int sampleSize,
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

                byte[] arr = Program.convertIntToByteArr(sampleSize, newSample, isBigEndian);
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
    public static byte[] modifySamplesMono(byte[] mono, int n, int sampleSize,
                                           boolean isBigEndian, boolean isSigned,
                                           Aggregation agg) throws IOException {
        byte[][] channel = new byte[1][];
        channel[0] = mono;
        byte[][] result = modifySamplesMoreChannels(channel, n, sampleSize, isBigEndian, isSigned, agg);
        return result[0];
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Audio modification methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Other
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
     * Takes the int values representing some property (min or max or avg or rms)of each song part and returns them in 1D double array.
     *
     * @param songParts are the samples of the song part together with int, which represents some property of the song part.
     * @return Returns 1D double array containing the int values which are in the SongPartWithAverageValueOfSamples as int property.
     */
    public static double[] getValuesFromSongParts(SongPartWithAverageValueOfSamples[] songParts) {
        double[] values = new double[songParts.length];

        for (int i = 0; i < values.length; i++) {
            values[i] = songParts[i].averageAmplitude;
        }

        return values;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Other
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
