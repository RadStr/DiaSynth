package util.audio;

import util.Utilities;
import util.audio.filter.NonRecursiveFilter;
import util.audio.io.AudioReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;



// Sometimes we can see code duplication where only the parameter referencing to endianness and sign of samples is changing.
// That is pretty old code and the reason for that was to minimize branching since I didn't know if compiler will
// look inside the methods and optimize the branching. So we are using convertBytesToIntLittleEndian and
// convertBytesToIntBigEndian and we branch based on endianness instead of just calling convertBytesToInt.
// It should be a bit faster, but it involves code duplication. Since the code is working and won't be changed, I will
// keep it as it is written.


public class AudioConverter {
    private AudioConverter() {}         // Allow only static access



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert stream methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Converts given stream to byte array of length streamLen
     * (streamLen should be at least the same size as the stream)
     * @param stream is the stream to convert
     * @return returns the converted stream
     * @throws IOException if error with stream occurred
     */
    public static byte[] convertStreamToByteArray(InputStream stream, int streamLen) throws IOException {
        byte[] converted = new byte[streamLen];
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert stream methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Separate channels
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
        return AudioProcessor.getEveryNthSampleMoreChannels(samples, numberOfChannels, sampleSize,
                                                           1, 0, totalAudioLength);
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
                                                    boolean isBigEndian, boolean isSigned,
                                                    int totalAudioLength) throws IOException {
        return AudioProcessor.getEveryNthSampleMoreChannelsDouble(samples, numberOfChannels, sampleSize, 1,
            0, isBigEndian, isSigned, totalAudioLength);
    }

    /**
     * Takes the byte array with samples and returns the samples of channels in byte labelReferenceArrs
     * (1 array = 1 channel).
     *
     * @param samples    is the byte array containing samples
     * @param sampleSize is the size of 1 sample in a channel
     * @return Returns 2D byte array, where each byte array corresponds to 1 channel.
     * @throws IOException is thrown when error with input
     */
    public static byte[][] separateChannels(byte[] samples, int numberOfChannels, int sampleSize) throws IOException {
        return AudioProcessor.getEveryNthSampleMoreChannels(samples, numberOfChannels, sampleSize, 1, 0);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Separate channels
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert to mono methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
                                     boolean isBigEndian, boolean isSigned, byte[] monoSong) {
        int sample = 0;
        int monoSample = 0;

        int mask = AudioUtilities.calculateMask(sampleSize);

        byte[] monoSampleInBytes = new byte[sampleSize];

        for (int index = 0, monoSongIndex = 0; index < samples.length;) {
            // We take the bytes from end, but it doesn't matter, since we take just the average value
            monoSample = 0;
            for (int i = 0; i < numberOfChannels; i++) {
                sample = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
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
        return monoSong;
    }

    /**
     * Converts the audio from audioStream to mono signal by averaging the samples in 1 frame. Pretty memory expensive.
     * Also not tested, but it should be fine. Still it is preferred to use the byte variant.
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
                    sample = convertBytesToInt(frame, sampleSize, mask, index, isBigEndian, isSigned);
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert to mono methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert int to bytes (from sample to sample methods)
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Converts the sizeInBytes least significant bytes of int given in parameter numberToConvert to
     * byte array of size sizeInBytes.
     * @param sizeInBytes is the size of the number in bytes.
     * @param numberToConvert is the number to be converted.
     * @param convertToBigEndian is boolean variable, if true, then the first byte in array contains the most
     *                           significant byte of the number, if false, then it contains the least significant
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

    /**
     * Fills given array with int given in parameter numberToConvert.
     * @param arr is the array to be filled with bytes of numberToConvert in given endianity.
     * @param numberToConvert is the number to be converted.
     * @param convertToBigEndian is boolean variable, if true, then the first byte in array contains the most
     *                           significant byte of the number, if false, then it contains the least significant
     */
    public static void convertIntToByteArr(byte[] arr, int numberToConvert, boolean convertToBigEndian) {
        // The implementation could be solved by calling the general variant of this method,
        // but this should be a bit faster.
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
                                           int startIndex, boolean convertToBigEndian) {
        int endIndex = startIndex + sampleSize;
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert int to bytes (from sample to sample methods)
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert sample in bytes to sample in int methods (from sample to sample methods)
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
    public static int convertBytesToInt(byte[] bytes, int sampleSize, int mask, int arrIndex,
                                        boolean isBigEndian, boolean isSigned) {
        if(isBigEndian) {
            return convertBytesToIntBigEndian(bytes, sampleSize, mask, arrIndex, isSigned);
        }
        else {
            return convertBytesToIntLittleEndian(bytes, sampleSize, mask, arrIndex, isSigned);
        }
    }

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
    public static int convertBytesToIntBigEndian(byte[] bytes, int sampleSize,
                                                 int mask, int arrIndex, boolean isSigned) {
        int result = 0;
        arrIndex = arrIndex + sampleSize - 1;

        // This part is general, but we may get better performance by having switch for sampleSize on [1,4].
        // That would need some performance tests though.
        for (int i = 0; i < sampleSize; i++) {
            result = result | (((int) bytes[arrIndex] & 0x00_00_00_FF) << (i * 8));
            arrIndex--;
        }

        // Old variant with if
//        if(isSigned) {
//            if (((result >> (((sampleSize - 1) * 8) + 7)) & 1) == 1) {  //If true, then copy sign bit
//                result = result | mask;
//            }
//        }

        // New variant without if
        if (isSigned) {
            //If == 1 then there is sign bit, if == 0 then no sign bit
            int sign = (result >> (((sampleSize - 1) * 8) + 7)) & 1;
            mask *= sign;       // mask will be 0 if the number >= 0 (no sign bit); mask == mask if sign == 1
            result = result | mask;
        }

        return result;
    }

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
    public static int convertBytesToIntLittleEndian(byte[] bytes, int sampleSize, int mask,
                                                    int arrIndex, boolean isSigned) {
        int result = 0;

        // This part is general, but we may get better performance by having switch for sampleSize on [1,4].
        // That would need some performance tests though.
        for (int i = 0; i < sampleSize; i++) {
            result = result | (((int) bytes[arrIndex] & 0x00_00_00_FF) << (i * 8));
            arrIndex++;
        }
          // Old variant with if
//        if (isSigned) {
//            if (((result >> (((sampleSize - 1) * 8) + 7)) & 1) == 1) {  //If true, then copy sign bit
//                result = result | mask;
//            }
//        }

        // New variant without if
        if (isSigned) {
            //If == 1 then there is sign bit, if == 0 then no sign bit
            int sign = (result >> (((sampleSize - 1) * 8) + 7)) & 1;
            mask *= sign;       // mask will be 0 if the number >= 0 (no sign bit); mask == mask if sign == 1
            result = result | mask;
        }

        return result;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert sample in bytes to sample in int methods (from sample to sample methods)
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert to double sample methods (from sample to sample methods)
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Converts the given int sample to normalized double, normalized means that it is on interval [-1, 1]. Only
     * if the sample is within the [-maxAbsoluteValue, maxAbsoluteValue] of course.
     * @param sample
     * @param maxAbsoluteValue
     * @param isSigned
     * @return
     */
    public static double normalizeToDouble(int sample, int maxAbsoluteValue, boolean isSigned) {
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert to double sample methods (from sample to sample methods)
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert double sample to other sample types (from sample to sample methods)
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Expects the double to be between -1 and 1
     * @param sampleDouble
     * @param maxAbsoluteValue
     * @param isSigned
     * @return
     */
    public static int convertDoubleToInt(double sampleDouble, int maxAbsoluteValue, boolean isSigned) {
        int sampleInt = (int)(sampleDouble * maxAbsoluteValue);
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert double sample to other sample types (from sample to sample methods)
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert bytes to samples (from array to array methods)
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Converts byte array to int samples of size sampleSize.
     * @param byteSamples are the samples in 1D byte array.
     * @param sampleSize is the size of one sample in bytes.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned tells if the converted samples are signed or unsigned
     * @return Returns the samples as 1D array of ints.
     * @throws IOException is thrown when the sample size is invalid.
     */
    public static int[] convertBytesToSamples(byte[] byteSamples, int sampleSize,
                                              boolean isBigEndian, boolean isSigned) throws IOException {
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert bytes to samples (from array to array methods)
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert to normalized double samples methods (from array to array methods)
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
     * Converts byte array to int samples, which are then normalized to double number between -1 and 1 and
     * put in outputArr. Ends when outputArr is full
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
     * Converts byte array to int samples, which are then normalized to double number between -1 and 1 and
     * put in outputArr. Ends when outputArr is full
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
     * Converts byte array to int samples, which are then normalized to double number between -1 and 1 and
     * put in outputArr. Ends when outputArr is full
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
        return outputArr;
    }



    /**
     Takes int[] which represents samples converts them to double[] which are normalized samples
     (values between -1 and 1).
     * @param samples is 1D int array with samples.
     * @param sampleSizeInBits is the size of 1 sample in bits in the samples array.
     * @param isSigned is true if the samples are signed, is false otherwise.
     * @return Returns normalized samples in form of double[]
     */
    public static double[] normalizeToDoubles(int[] samples, int sampleSizeInBits, boolean isSigned) {
        double[] result = new double[samples.length];
        int maxAbsoluteValue = AudioUtilities.getMaxAbsoluteValueSigned(sampleSizeInBits);
        if(isSigned) {
            for (int i = 0; i < result.length; i++) {
                result[i] = samples[i] / (double)maxAbsoluteValue;
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

    /**
     * Takes double[] which represents samples (or for example average value).
     * First converts them to int (since the double values are in these case expected to be ints).
     * Performs normalization on these samples and returns them.
     * This class exists for optimization (saving copying and creating array).
     * @param samples is 1D int array with samples.
     * @param sampleSizeInBits is the size of 1 sample in bits in the samples array.
     * @param isSigned is true if the samples are signed, is false otherwise.
     */
    public static void normalizeToDoubles(double[] samples, int sampleSizeInBits, boolean isSigned) {
        int maxAbsoluteValue = AudioUtilities.getMaxAbsoluteValueSigned(sampleSizeInBits);
        if(isSigned) {
            for (int i = 0; i < samples.length; i++) {
                samples[i] = (int)samples[i];
                samples[i] = samples[i] / maxAbsoluteValue;
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert to normalized double samples methods (from array to array methods)
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert double samples to other types (from array to array methods)
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int[] convertDoubleArrToIntArr(double[] doubleArr, int maxAbsoluteValue, boolean isSigned) {
        int[] intArr = new int[doubleArr.length];

        for(int i = 0; i < doubleArr.length; i++) {
            intArr[i] = convertDoubleToInt(doubleArr[i], maxAbsoluteValue, isSigned);
        }

        return intArr;
    }

    public static void convertDoubleArrToIntArr(double[] doubleArr, int[] intArr,
                                                int maxAbsoluteValue, boolean isSigned) {
        convertDoubleArrToIntArr(doubleArr, intArr, 0, 0, intArr.length,
                maxAbsoluteValue, isSigned);
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
            convertDoubleToByteArr(doubleArr[doubleStartInd], sampleSize, maxAbsoluteValue,
                    isBigEndian, isSigned, byteStartInd, byteArr);
        }
    }

    public static byte[] convertDoubleArrToByteArr(double[] doubleArr, int doubleStartInd, int len, int sampleSize,
                                                   int maxAbsoluteValue, boolean isBigEndian, boolean isSigned) {
        byte[] arr = new byte[len * sampleSize];
        convertDoubleArrToByteArr(doubleArr, arr, doubleStartInd, 0, len, sampleSize,
        maxAbsoluteValue, isBigEndian, isSigned);
        return arr;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert double samples to other types (from array to array methods)
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert sample rates methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
     * @param isBigEndian      is boolean variable, which is true if the samples are big endian and
     *                         false if little endian
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
            retArr = convertToLowerSampleRateByUpSampling(samples, sampleSize, frameSize, numberOfChannels,
                    oldSampleRate, newSampleRate, isBigEndian, isSigned, canChangeInputArr);
        }
        else if (oldSampleRate < newSampleRate) {
            retArr = convertToHigherSampleRate(samples, sampleSize, numberOfChannels, oldSampleRate,
                    newSampleRate, isBigEndian, isSigned);
        }
        else {
            retArr = samples;        // The sampling rates are the same
        }
        return retArr;
    }

    /**
     * Input array isn't changed.
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate",
     * where oldSampleRate < newSampleRate
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
    private static byte[] convertToHigherSampleRate(byte[] samples, int sampleSize, int numberOfChannels,
                                                    int oldSampleRate, int newSampleRate, boolean isBigEndian,
                                                    boolean isSigned) throws IOException {
        return convertSampleRateImmediateVersion(samples, sampleSize, numberOfChannels, oldSampleRate,
                newSampleRate, isBigEndian, isSigned);
    }

    public static double[] convertSampleRate(double[] samples, int numberOfChannels, int oldSampleRate,
                                             int newSampleRate, boolean canChangeInputArr) throws IOException {
        double[] retArr;
        if (oldSampleRate > newSampleRate) {
            retArr = convertToLowerSampleRateByImmediate(samples, numberOfChannels, oldSampleRate,
                    newSampleRate, canChangeInputArr);
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
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate",
     * where oldSampleRate < newSampleRate
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
                                                            boolean isBigEndian, boolean isSigned) {
        int frameSize = numberOfChannels * sampleSize;
        if (samples == null || samples.length <= frameSize) {
            return samples;
        }
        double ratio = ((double) oldSampleRate) / newSampleRate;
        ArrayList<Byte> retList = new ArrayList<>();
        int mask = AudioUtilities.calculateMask(sampleSize);

        double currRatio = 0;
        int[][] currentSamples = new int[numberOfChannels][2];  // for each channel we will have left and right sample
        int bytesNeededToInitArr = currentSamples.length * currentSamples[0].length * sampleSize;
        int index = 0;
        index = setLeftAndRightSamples(currentSamples, samples, sampleSize, numberOfChannels, mask,
                index, isBigEndian, isSigned);
        int val = 0;
        byte[] valByte = new byte[sampleSize];
        // The second part of or is for case when we are working with the last samples
        while(index < samples.length || currRatio+ratio <= 1) {
            for(int j = 0; j < currentSamples.length; j++) {
                val = (int) (currentSamples[j][0] * (1 - currRatio) + currentSamples[j][1] * currRatio);
                convertIntToByteArr(valByte, val, isBigEndian);
                for(int ind = 0; ind < valByte.length; ind++) {
                    retList.add(valByte[ind]);
                }
            }

            currRatio += ratio;
            if(currRatio > 1) {
                if(ratio <= 1) {         // Should be optimized by compiler ... perform the if branching only once
                    for (int j = 0; j < currentSamples.length; j++) {
                        currentSamples[j][0] = currentSamples[j][1];
                        currentSamples[j][1] = convertBytesToInt(samples, sampleSize, mask, index,
                                isBigEndian, isSigned);
                        index += sampleSize;
                    }
                }
                else {
                    if(currRatio >= 3) {
                        index += ((int)currRatio - 2) * frameSize;
                    }

                    if(index > samples.length - bytesNeededToInitArr) {           // We skipped too much
                        break;
                    }
                    index = setLeftAndRightSamples(currentSamples, samples, sampleSize, numberOfChannels, mask,
                            index, isBigEndian, isSigned);
                }

                currRatio %= 1;
            }
        }

        // Not sure if I always want to add the last frame, but it is just one last frame so it doesn't matter that much
        for(int i = 0; i < currentSamples.length; i++) {
            convertIntToByteArr(valByte, currentSamples[i][1], isBigEndian);
            for(int ind = 0; ind < valByte.length; ind++) {
                retList.add(valByte[ind]);
            }
        }


        byte[] retArr = new byte[retList.size()];
        int i = 0;
        for(byte b : retList) {
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
     * @param isBigEndian      is boolean variable, which is true,
     *                         if the samples are big endian and false if little endian
     * @param isSigned         is boolean variable, which is true,
     *                         if the samples are signed, false if unsigned.
     * @return Returns the index of the sample behind the last touched sample
     * (so the returned index = index + sampleSize * 2 * numberOfChannels).
     */
    private static int setLeftAndRightSamples(int[][] currentSamples, byte[] samples,
                                              int sampleSize, int numberOfChannels, int mask, int index,
                                              boolean isBigEndian, boolean isSigned) {
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
    private static double[] convertSampleRateImmediateVersion(double[] samples, int numberOfChannels,
                                                              int oldSampleRate, int newSampleRate) {
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
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate",
     * where oldSampleRate > newSampleRate.
     * <br>
     * The conversion isn't immediate, we convert to some factor "n" of the newSampleRate,
     * which is bigger than the oldSampleRate.
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
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate",
     * where oldSampleRate > newSampleRate
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert sample rates methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Help methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Help methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Non-used methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Non-used methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Deprecated methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate",
     * where oldSampleRate > newSampleRate.
     * <br>
     * The conversion isn't immediate, we convert to some factor "n" of the newSampleRate,
     * which is bigger than the oldSampleRate.
     * And the we take every "n"-th sample of the upsampled array.
     * @param samples          is input array with samples
     * @param sampleSize       is the size of one sample in bytes
     * @param frameSize        is the size of one frame in bytes
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @param isBigEndian      is boolean variable, which is true if the samples are big endian and
     *                         false if little endian
     * @param isSigned         is boolean variable, which is true if the samples are signed, false if unsigned.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalid (<0 or >4)
     */
    @Deprecated
    private static byte[] convertToLowerSampleRateByUpSampling(byte[] samples, int sampleSize, int frameSize,
                                                               int numberOfChannels, int oldSampleRate,
                                                               int newSampleRate, boolean isBigEndian, boolean isSigned,
                                                               boolean canChangeInputArr) throws IOException {
        int upSampleRate = newSampleRate;
        int upSampleRateRatio = 1;
        while(upSampleRate < oldSampleRate) {
            upSampleRateRatio++;
            upSampleRate += newSampleRate;
        }
        int skipSize = (upSampleRateRatio - 1) * frameSize;       // Skip all the frames to downsample
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
            upSampledArr = convertToHigherSampleRate(samples, sampleSize, numberOfChannels,
                    oldSampleRate, upSampleRate, isBigEndian, isSigned);
        }

        // Low pass filter for the nyquist frequency of the new frequency
        upSampledArr = NonRecursiveFilter.runLowPassFilter(upSampledArr, newSampleRate / 2,
                64, oldSampleRate, numberOfChannels, sampleSize, frameSize, isBigEndian, isSigned);
        int len = frameSize;        // Get frame count
        int frameCount = upSampledArr.length / frameSize;
        if(frameCount % upSampleRateRatio == 0) {
            len = 0;
        }
        len += (frameCount / upSampleRateRatio) * frameSize;
        byte[] retArr = new byte[len];

        for(int retInd = 0, upSampleInd = 0; retInd < retArr.length; upSampleInd += skipSize) {
            for (int fs = 0; fs < frameSize; fs++, retInd++, upSampleInd++) {
                retArr[retInd] = upSampledArr[upSampleInd];
            }
        }

        return retArr;
    }



    /**
     * Input array isn't changed.
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate",
     * where oldSampleRate > newSampleRate
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
        byte[] filtered = NonRecursiveFilter.runLowPassFilter(samples, newSampleRate / 2, 64,
                oldSampleRate, numberOfChannels, sampleSize, frameSize, isBigEndian, isSigned);
        return convertSampleRateImmediateVersion(filtered, sampleSize, numberOfChannels, oldSampleRate,
                newSampleRate, isBigEndian, isSigned);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Deprecated methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
