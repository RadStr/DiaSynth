package str.rad.util;

import str.rad.util.audio.AudioConverter;
import str.rad.util.audio.AudioUtilities;
import str.rad.util.audio.io.AudioReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Enumeration representing the possible aggregation of n values. ABS_MIN and ABS_MAX currently work only for doubles.
 * The not supported aggregations return Double.MIN_VALUE.
 */
public enum Aggregation {
    MIN {
        public int defaultValueForMod() {
            return Integer.MAX_VALUE;
        }
    },
    ABS_MIN {
        public int defaultValueForMod() {
            return Integer.MAX_VALUE;
        }
    },
    MAX {
        public int defaultValueForMod() {
            return Integer.MIN_VALUE;
        }
    },
    ABS_MAX {
        public int defaultValueForMod() {
            return 0;
        }
    },
    AVG {
        public int defaultValueForMod() {
            return 0;
        }
    },
    RMS {
        public int defaultValueForMod() {
            return 0;
        }
    },
    SUM {
        public int defaultValueForMod() {
            return 0;
        }
    };


    /**
     * Returns default value for given aggregation.
     *
     * @return Returns default value for given aggregation.
     */
    public abstract int defaultValueForMod();


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Static aggregation methods:
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static double performAggregation(double val1, double val2, Aggregation agg) {
        switch (agg) {
            case ABS_MAX:
                return Math.max(Math.abs(val1), Math.abs(val2));
            case ABS_MIN:
                return Math.min(Math.abs(val1), Math.abs(val2));
            case MAX:
                return Math.max(val1, val2);
            case MIN:
                return Math.min(val1, val2);
            case RMS:
                return Math.sqrt((val1 * val1 + val2 * val2) / 2);
            case AVG:
                return (val1 + val2) / 2;
            case SUM:
                return val1 + val2;
            default:
                return Double.MIN_VALUE;
        }
    }

    public static double performAggregation(double[] arr, Aggregation agg) {
        return performAggregation(arr, 0, arr.length, agg);
    }

    /**
     * Compresses the audio:
     * (1) if agg = MIN then by taking the sample with the lowest amplitude of n given samples.
     * (2) if agg = ABS_MIN then by taking the minimum in absolute value
     * (3) if agg = MAX then by taking the sample with the highest amplitude of n given samples
     * (4) if agg = ABS_MAX then by taking the maximum in absolute value
     * (5) if agg = AVG then by taking the average value of samples
     * (6) if agg = RMS then by taking the RMS of samples
     * (7) if agg = SUM then by taking the sum of samples
     * Expects the samples to be from one channel (so if working with stereo, either convert stereo to mono
     * or call this method for each channel's samples respectively).
     *
     * @param samples    is double array with samples from one channel
     * @param startIndex
     * @param len
     * @param agg        represents the aggregation which will be performed on the len samples
     * @return Returns double which is result of the performed operation on len samples.
     * Returns Double.MIN_VALUE if the aggregation isn't supported
     */
    public static double performAggregation(double[] samples, int startIndex, int len, Aggregation agg) {
        double specialValue = agg.defaultValueForMod();

        int endIndex = startIndex + len;
        for (int i = startIndex; i < endIndex; i++) {
            switch (agg) {
                case ABS_MAX: {
                    double abs = Math.abs(samples[i]);
                    if (specialValue < abs) {
                        specialValue = abs;
                    }
                    break;
                }
                case ABS_MIN: {
                    double abs = Math.abs(samples[i]);
                    if (specialValue > abs) {
                        specialValue = abs;
                    }
                    break;
                }

                case MAX:
                    if (specialValue < samples[i]) {
                        specialValue = samples[i];
                    }
                    break;
                case MIN:
                    if (specialValue > samples[i]) {
                        specialValue = samples[i];
                    }
                    break;
                case RMS:
                    specialValue += (samples[i] * samples[i]);
                    break;
                case AVG:
                case SUM:
                    specialValue += samples[i];
                    break;
                default:
                    return Double.MIN_VALUE;
            }
        }


        switch (agg) {
            case RMS:
                specialValue /= len;
                specialValue = Math.sqrt(specialValue);
                break;
            case AVG:
                specialValue /= len;
                break;
        }

        return specialValue;
    }


    // Note - can be implemented using class from WavePanel with the extremes but,
    //  it is way too general and slower, and it is simple enough to implement it again here

    /**
     * Finds the min and max in the samples array at range [startIndex, endIndex]
     * Expects the samples to be from one channel (so if working with stereo, either convert stereo to mono
     * or call this method for each channel's samples respectively).
     * If the startIndex is out of bounds crashes. If len == 0 puts the samples[startIndex] into output[0] and output[1].
     *
     * @param samples    is double array with samples from one channel
     * @param startIndex
     * @param endIndex
     * @param output     puts min at index 0 and max at index 1
     */
    public static void convertNSamplesToMinAndMax(double[] samples, int startIndex, int endIndex, double[] output) {
        double min = samples[startIndex];
        double max = samples[startIndex];

        for (int i = startIndex + 1; i < endIndex; i++) {
            if (max < samples[i]) {
                max = samples[i];
            }
            else if (min > samples[i]) {
                min = samples[i];
            }
        }

        output[0] = min;
        output[1] = max;
    }

    /**
     * Compresses the audio:
     * (1) if agg = MIN then by taking the sample with the lowest amplitude of n given samples.
     * (2) if agg = ABS_MIN then by taking the minimum in absolute value
     * (3) if agg = MAX then by taking the sample with the highest amplitude of n given samples
     * (4) if agg = ABS_MAX then by taking the maximum in absolute value
     * (5) if agg = AVG then by taking the average value of samples
     * (6) if agg = RMS then by taking the RMS of samples
     * (7) if agg = SUM then by taking the sum of samples
     * Expects the samples to be from one channel (so if working with stereo, either convert stereo to mono
     * or call this method for each channel's samples respectively).
     *
     * @param samples     is byte array with samples from one channel
     * @param sampleSize  is the size of one sample
     * @param isBigEndian is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned    is boolean variable, which is true if samples are signed, false if unsigned
     * @param agg         represents the aggregation which will be performed on the n samples
     * @return Returns double which is result of the performed operation on n samples.
     * Returns Double.MIN_VALUE if the aggregation isn't supported.
     * @throws IOException is thrown when the method calculateMask fails - invalid sample size
     */
    public static double performAggregation(byte[] samples, int sampleSize, boolean isBigEndian,
                                            boolean isSigned, Aggregation agg) {
        int n = samples.length / sampleSize;

        int mask = AudioUtilities.calculateMask(sampleSize);
        double specialValue = agg.defaultValueForMod();

        int sample;
        int index = 0;

        // Copy-pasted - probably to make it easier for compiler,
        // but it should probably recognize it, the code is just too old
        if (isBigEndian) {
            for (int j = 0; j < n; j++) {
                sample = AudioConverter.convertBytesToIntBigEndian(samples, sampleSize, mask, index, isSigned);
                switch (agg) {
                    case MAX:
                        if (specialValue < sample) {
                            specialValue = sample;
                        }
                        break;
                    case MIN:
                        if (specialValue > sample) {
                            specialValue = sample;
                        }
                        break;
                    case RMS:
                        specialValue += sample * (double) sample;
                        break;
                    case SUM:
                    case AVG:
                        specialValue += sample;
                        break;
                    default:
                        return Double.MIN_VALUE;
                }
                index = index + sampleSize;
            }
        }
        else {
            for (int j = 0; j < n; j++) {
                sample = AudioConverter.convertBytesToIntLittleEndian(samples, sampleSize, mask, index, isSigned);
                switch (agg) {
                    case MAX:
                        if (specialValue < sample) {
                            specialValue = sample;
                        }
                        break;
                    case MIN:
                        if (specialValue > sample) {
                            specialValue = sample;
                        }
                        break;
                    case RMS:
                        specialValue += sample * (double) sample;
                        break;
                    case SUM:
                    case AVG:
                        specialValue += sample;
                        break;
                    default:
                        return Double.MIN_VALUE;
                }
                index = index + sampleSize;
            }
        }

        double maxAbsVal = (double) AudioUtilities.getMaxAbsoluteValue(sampleSize * 8, isSigned);
        specialValue /= maxAbsVal;
        if (agg == RMS) {
            specialValue /= maxAbsVal;
            specialValue = Math.sqrt(specialValue / n);
        }
        else if (agg == AVG) {
            specialValue /= n;
        }

        return specialValue;
    }

    /**
     * Compresses the audio:
     * (1) if agg = MIN then by taking the sample with the lowest amplitude of n given samples.
     * (2) if agg = ABS_MIN then by taking the minimum in absolute value
     * (3) if agg = MAX then by taking the sample with the highest amplitude of n given samples
     * (4) if agg = ABS_MAX then by taking the maximum in absolute value
     * (5) if agg = AVG then by taking the average value of samples
     * (6) if agg = RMS then by taking the RMS of samples
     * (7) if agg = SUM then by taking the sum of samples
     *
     * @param stream           is the input stream containing samples.
     * @param numberOfChannels represents number of channels.
     * @param sampleSize       is the size of one sample in bytes.
     * @param isBigEndian      is true if the samples are big endian, false otherwise.
     * @param isSigned         true if the samples are signed numbers, false otherwise.
     * @param byteLength       is the total length of the input stream. (The value is the same as onlyAudioSizeInBytes property in the class)
     * @return Returns double value which represents the result of the aggregation performed on samples given in input stream.
     * Returns Double.MIN_VALUE if the aggregation isn't supported.
     * @throws IOException is thrown where error with input stream occurred, or the argument sampleSize is invalid.
     */
    public static double performAggregation(InputStream stream, int numberOfChannels, int sampleSize,
                                            boolean isBigEndian, boolean isSigned, int byteLength,
                                            Aggregation agg) throws IOException {
        int n = byteLength / sampleSize;
        double specialValue = agg.defaultValueForMod();
        int bytesRead = 0;
        int sample;

        int mask = AudioUtilities.calculateMask(sampleSize);

        byte[] arr = new byte[sampleSize * numberOfChannels * 16];

        // Copy-pasted - probably to make it easier for compiler,
        // but it should probably recognize it, the code is just too old
        if (isBigEndian) {
            while (bytesRead != -1) {
                bytesRead = AudioReader.readNSamples(stream, arr);
                int arrIndex = 0;
                while (arrIndex < bytesRead) {
                    sample = AudioConverter.convertBytesToIntBigEndian(arr, sampleSize, mask, arrIndex, isSigned);
                    switch (agg) {
                        case MAX:
                            if (specialValue < sample) {
                                specialValue = sample;
                            }
                            break;
                        case MIN:
                            if (specialValue > sample) {
                                specialValue = sample;
                            }
                            break;
                        case RMS:
                            specialValue += sample * (double) sample;
                            break;
                        case AVG:
                        case SUM:
                            specialValue += (double) sample;
                            break;
                        default:
                            return Double.MIN_VALUE;
                    }
                    arrIndex = arrIndex + sampleSize;
                }
            }
        }
        else {
            while (bytesRead != -1) {
                bytesRead = AudioReader.readNSamples(stream, arr);
                int arrIndex = 0;
                while (arrIndex < bytesRead) {
                    sample = AudioConverter.convertBytesToIntLittleEndian(arr, sampleSize, mask, arrIndex, isSigned);
                    switch (agg) {
                        case MAX:
                            if (specialValue < sample) {
                                specialValue = sample;
                            }
                            break;
                        case MIN:
                            if (specialValue > sample) {
                                specialValue = sample;
                            }
                            break;
                        case RMS:
                            specialValue += sample * (double) sample;
                            break;
                        case AVG:
                        case SUM:
                            specialValue += (double) sample;
                            break;
                        default:
                            return Double.MIN_VALUE;
                    }
                    arrIndex = arrIndex + sampleSize;
                }
            }
        }


        double maxAbsVal = (double) AudioUtilities.getMaxAbsoluteValue(sampleSize * 8, isSigned);
        specialValue /= maxAbsVal;
        if (agg == RMS) {
            specialValue /= maxAbsVal;
            specialValue = Math.sqrt(specialValue / n);
        }
        else if (agg == AVG) {
            specialValue /= n;
        }

        return specialValue;
    }


    /**
     * Returns all operations from Aggregation performed on given array.
     *
     * @param samples     is the given array with samples.
     * @param sampleSize  is the size of 1 sample.
     * @param isBigEndian true if the given samples are in big endian, false if in little endian.
     * @param isSigned    true if the samples are signed numbers, false otherwise.
     * @return Returns array with mods in Aggregation order (given by calling Aggregation.values()).
     * Some values may be equal to Double.MIN_VALUE that means they are not supported
     * @throws IOException is thrown when the sample size is <= 0 or > 4
     */
    public static double[] calculateAllAggregations(byte[] samples, int sampleSize,
                                                    boolean isBigEndian, boolean isSigned) {
        double[] arr = new double[values().length];
        int index = 0;
        for (Aggregation agg : values()) {
            arr[index] = performAggregation(samples, sampleSize, isBigEndian, isSigned, agg);
            index++;
        }
        return arr;
    }
}
