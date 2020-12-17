package util;

// TODO: ABS_MIN and ABS_MAX aren't in all performAggregation methods
//  (for obvious reasons - unsigned numbers, etc. - I have to fix that later, currently it is only in the double variant)

import Rocnikovy_Projekt.Program;
import util.audio.io.AudioReader;
import util.audio.NormalizedSongPartWithAverageValueOfSamples;
import util.audio.SongPartWithAverageValueOfSamples;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Enumeration representing the possible aggregation of n values.
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
     * @return Returns default value for given aggregation.
     */
    public abstract int defaultValueForMod();



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Static aggregation methods:
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: Nejak nebere k uvahu pocet kanalu ale tvari se ze jo protoze bere frameSize
    /**
     * Splits the input stream to parts of size numberOfFramesInOneSongPart * frameSize and calculates the aggregation agg for each part
     * The output is sorted by the int value (which depends on the agg argument), if the output is sorted depends on the value of variable returnSorted.
     * If the song is multi-channel (for example stereo), then the agg int is calculated as it was mono
     *
     * @param audioStream                 is the input stream with the audio samples.
     * @param numberOfFramesInOneSongPart is the total number of frames in 1 song part
     * @param frameSize                   is the size of 1 frame, which equals numberOfChannels * sampleSize, unless the method is used differently
     * @param isBigEndian                 is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned                    is boolean variable, which is true if the samples are signed, false if unsigned.
     * @param sampleSize                  is the size of 1 sample
     * @param returnSorted                if true, then the output is sorted by the int value, which is based on the agg, else the output is not sorted, that
     *                                    means if we connected all the song parts from the result in the order as they are in the array, then it would be
     *                                    same as the original input stream
     * @param agg                         represents the aggregation - what double will be added to each song part.
     * @return Returns the array of type SongPartWithAverageValueOfSamples which contains the song parts with the int based on the agg argument.
     * @throws IOException is thrown when error in reading the input stream occurred, or when the method is called with invalid agg.
     */
    @Deprecated
    public static SongPartWithAverageValueOfSamples[] takeSongPartsAndAddAggregation(InputStream audioStream,
                                                                                     int numberOfFramesInOneSongPart, int frameSize,
                                                                                     boolean isBigEndian, boolean isSigned, int sampleSize,
                                                                                     boolean returnSorted, Aggregation agg) throws IOException {
        ArrayList<SongPartWithAverageValueOfSamples> songParts = new ArrayList<>();
        int size = numberOfFramesInOneSongPart * frameSize;            // size of the song part
        byte[] songPart = new byte[size];
        int bytesRead = 0;

        int bytesReadSum = 0;
        while (bytesRead != -1) {
            bytesReadSum = AudioReader.readNSamples(audioStream, songPart);
            if (bytesReadSum < sampleSize) {
                break;
            }
            double specialValue;
            specialValue = performAggregation(songPart, sampleSize, isBigEndian, isSigned, agg);
            if (bytesReadSum != songPart.length) {// TODO: !!!!!!!!!!!!!!!!!!
                // TODO: Here I take the last window i nthe other cases I don't so I guess that I should just drop it
   /*
                byte[] arr = new byte[bytesReadSum];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = songPart[i];
                }
                songParts.add(new SongPartWithAverageValueOfSamples((int) specialValue, arr, false));
                bytesRead = -1;

    */
            } else {
                songParts.add(new SongPartWithAverageValueOfSamples((int) specialValue, songPart, true));
            }
        }

        SongPartWithAverageValueOfSamples[] arr = new SongPartWithAverageValueOfSamples[songParts.size()];
        arr = songParts.toArray(arr);
        if (returnSorted) {
            Arrays.sort(arr);
        }
        return arr;
    }

    /**
     * Splits the input stream to parts of size numberOfFramesInOneSongPart * frameSize and calculates the aggregation mod for each part.
     * The output is sorted by the int value (which depends on the mod argument).
     * If the output is sorted depends on the value of variable returnSorted.
     * The mod value is calculated of the original (non-normalized) values.
     * If the song is multi-channel (for example stereo), then the average value is calculated as it was mono
     *
     * @param audioStream                 is the input stream with the audio samples.
     * @param numberOfFramesInOneSongPart is the total number of frames in 1 song part
     * @param frameSize                   is the size of 1 frame, which equals numberOfChannels * sampleSize, unless the method is used differently
     * @param isBigEndian                 is boolean variable, which is true if the samples are big endian and false if little endian
     * @param sampleSize                  is the size of 1 sample
     * @param isSigned                    is boolean variable, which is true if the samples are signed and false if unsigned
     * @param returnSorted                if true, then the output is sorted by the int value, which depends on the mod, else the output is not sorted, that
     *                                    means if we connected all the song parts from the result in the order as they are in the array, then it would be
     *                                    same as the original input stream
     * @param mod                         represents the aggregation - what int will be added to each song part.
     * @return Returns the array of type NormalizedSongPartWithAverageValueOfSamples which contains the song parts in form of normalized samples, which are stored in 1D double array.
     * Together with the int based on the mod argument.
     * @throws IOException is thrown when error in reading the input stream occurred, or if the value in argument mod is invalid
     */
    @Deprecated
    public static NormalizedSongPartWithAverageValueOfSamples[] takeNormalizedSongPartsAndAddMod(InputStream audioStream,
                                                                                                 int numberOfFramesInOneSongPart,
                                                                                                 int frameSize,
                                                                                                 boolean isBigEndian,
                                                                                                 int sampleSize,
                                                                                                 boolean isSigned,
                                                                                                 boolean returnSorted,
                                                                                                 Aggregation mod) throws IOException {
        ArrayList<NormalizedSongPartWithAverageValueOfSamples> songParts = new ArrayList<>();
        int size = numberOfFramesInOneSongPart * frameSize;            // size of the song part
        byte[] songPart = new byte[size];
        double[] normalizedSongPart = new double[size / sampleSize];
        int bytesRead = 0;

        int bytesReadSum = 0;
        while (bytesRead != -1) {
            bytesReadSum = AudioReader.readNSamples(audioStream, songPart);
            if (bytesRead < sampleSize) {
                break;
            }

            int[] intArr;
            intArr = Program.convertBytesToSamples(songPart, sampleSize, isBigEndian, isSigned);

            double songPartValue = 0;
            switch (mod) {            // TODO: If the compiler doesn't optimize the cases outside the loop, then it is really inefficient
                case RMS:
                    for (int i = 0; i < intArr.length; i++) {
                        songPartValue = songPartValue + (double) (intArr[i] * intArr[i]) / intArr.length;
                    }
                    songPartValue = Math.sqrt(songPartValue);
                    break;
                case AVG:
                    for (int i = 0; i < intArr.length; i++) {
                        songPartValue = songPartValue + (double) intArr[i] / intArr.length;
                    }
                    break;
                case MIN:
                    int min = Integer.MAX_VALUE;
                    for (int i = 0; i < intArr.length; i++) {
                        if (intArr[i] < min) {
                            min = intArr[i];
                        }
                    }
                    songPartValue = min;
                    break;
                case MAX:
                    int max = Integer.MIN_VALUE;
                    for (int i = 0; i < intArr.length; i++) {
                        if (intArr[i] > max) {
                            max = intArr[i];
                        }
                    }
                    songPartValue = max;
                    break;
                default:
                    throw new IOException();
            }
            normalizedSongPart = Program.normalizeToDoubles(intArr, sampleSize * 8, isSigned);

            if (bytesReadSum != songPart.length) {
                double[] arr = new double[bytesReadSum / sampleSize];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = normalizedSongPart[i];
                }
                songParts.add(new NormalizedSongPartWithAverageValueOfSamples((int) songPartValue, arr, false));
                bytesRead = -1;
            } else {
                songParts.add(new NormalizedSongPartWithAverageValueOfSamples((int) songPartValue, normalizedSongPart, true));
            }
        }

        NormalizedSongPartWithAverageValueOfSamples[] arr = new NormalizedSongPartWithAverageValueOfSamples[songParts.size()];
        arr = songParts.toArray(arr);
        if (returnSorted) {
            Arrays.sort(arr);
        }
        return arr;
    }

    public static double performAggregation(double val1, double val2, Aggregation agg) {
        switch(agg) {
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
                return 0;
        }
    }

    public static double performAggregation(double[] arr, Aggregation agg) {
        return performAggregation(arr, 0, arr.length, agg);
    }

    /**
     * Compresses the audio:
     * (1) if agg = MIN then by taking the sample with the lowest amplitude of n given samples.
     * (2) if agg = MAX then by taking the sample with the highest amplitude of n given samples
     * (3) if agg = AVG then by taking the average value of samples
     * (4) if agg = RMS then by taking the RMS of samples
     * (5) if agg = SUM then by taking the sum of samples
     * Expects the samples to be from one channel (so if working with stereo, either convert stereo to mono
     * or call this method for each channel's samples respectively).
     *
     * @param samples     is double array with samples from one channel
     * @param startIndex
     * @param len
     * @param agg         represents the aggregation which will be performed on the len samples
     * @return Returns double which is result of the performed operation on len samples.
     */
    public static double performAggregation(double[] samples, int startIndex, int len, Aggregation agg) {
        double specialValue = agg.defaultValueForMod();

        int endIndex = startIndex + len;
        for (int i = startIndex; i < endIndex; i++) {
            switch(agg) {               // TODO: If the compiler doesn't optimize the if outside the loop, then it is really inefficient
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
            }
        }


        switch(agg) {
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
     * @param samples     is double array with samples from one channel
     * @param startIndex
     * @param endIndex
     * @param output puts min at index 0 and max at index 1
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
     * (2) if agg = MAX then by taking the sample with the highest amplitude of n given samples
     * (3) if agg = AVG then by taking the average value of samples
     * (4) if agg = RMS then by taking the RMS of samples
     * (5) if agg = SUM then by taking the sum of samples
     * Expects the samples to be from one channel (so if working with stereo, either convert stereo to mono
     * or call this method for each channel's samples respectively).
     *
     * @param samples     is byte array with samples from one channel
     * @param sampleSize  is the size of one sample
     * @param isBigEndian is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned    is boolean variable, which is true if samples are signed, false if unsigned
     * @param agg         represents the aggregation which will be performed on the n samples
     * @return Returns double which is result of the performed operation on n samples.
     * @throws IOException is thrown when the method calculateMask fails - invalid sample size
     */
    public static double performAggregation(byte[] samples, int sampleSize, boolean isBigEndian,
                                            boolean isSigned, Aggregation agg) throws IOException {
        int n = samples.length / sampleSize;

        int mask = Program.calculateMask(sampleSize);
        double specialValue = agg.defaultValueForMod();

        int sample;
        int index = 0;

        // TODO: Copy-pasted - probably to make it easier for compiler, but it should probably recognize it, the code is just too old
        if (isBigEndian) {
            for (int j = 0; j < n; j++) {
                sample = Program.convertBytesToIntBigEndian(samples, sampleSize, mask, index, isSigned);
                switch(agg) {           // TODO: If the compiler doesn't optimize the if outside the loop, then it is really inefficient
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
                        specialValue += sample * (double)sample;
                        break;
                    case SUM:
                    case AVG:
                        specialValue += sample;
                        break;
                }
                index = index + sampleSize;
            }
        } else {
            for (int j = 0; j < n; j++) {
                sample = Program.convertBytesToIntLittleEndian(samples, sampleSize, mask, index, isSigned);
                switch(agg) {           // TODO: If the compiler doesn't optimize the if outside the loop, then it is really inefficient
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
                        specialValue += sample * (double)sample;
                        break;
                    case SUM:
                    case AVG:
                        specialValue += sample;
                        break;
                }
                index = index + sampleSize;
            }
        }

        double maxAbsVal = (double) Program.getMaxAbsoluteValue(sampleSize * 8, isSigned);
        specialValue /= maxAbsVal;
        if (agg == RMS) {
            specialValue /= maxAbsVal;
            specialValue = Math.sqrt(specialValue / n);
        }
        else if(agg == AVG) {
            specialValue /= n;
        }

        return specialValue;
    }

    /**
     * Compresses the audio:
     * (1) if agg = MIN then by taking the sample with the lowest amplitude of n given samples.
     * (2) if agg = MAX then by taking the sample with the highest amplitude of n given samples
     * (3) if agg = AVG then by taking the average value of samples
     * (4) if agg = RMS then by taking the RMS of samples
     * (5) if agg = SUM then by taking the sum of samples
     * @param stream           is the input stream containing samples.
     * @param numberOfChannels represents number of channels.
     * @param sampleSize       is the size of one sample in bytes.
     * @param isBigEndian      is true if the samples are big endian, false otherwise.
     * @param isSigned         true if the samples are signed numbers, false otherwise.
     * @param byteLength       is the total length of the input stream. (The value is the same as onlyAudioSizeInBytes property in the class)
     * @return Returns double value which represents the result of the aggregation performed on samples given in input stream.
     * @throws IOException is thrown where error with input stream occurred, or the argument sampleSize is invalid.
     */
    public static double performAggregation(InputStream stream, int numberOfChannels, int sampleSize,
                                            boolean isBigEndian, boolean isSigned, int byteLength,
                                            Aggregation agg) throws IOException {
        int n = byteLength / sampleSize;
        double specialValue = agg.defaultValueForMod();
        int bytesRead = 0;
        int sample;

        int mask = Program.calculateMask(sampleSize);

        byte[] arr = new byte[sampleSize * numberOfChannels * 16];

        if (isBigEndian) {                // TODO: Again 2 same codes ... maybe can be done better ... currently for optimalization
            while (bytesRead != -1) {
                bytesRead = AudioReader.readNSamples(stream, arr);
                int arrIndex = 0;
                while (arrIndex < bytesRead) {
                    sample = Program.convertBytesToIntBigEndian(arr, sampleSize, mask, arrIndex, isSigned);
                    // TODO: Copy pasted
                    switch(agg) {           // TODO: If the compiler doesn't optimize the if outside the loop, then it is really inefficient
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
                            specialValue += sample * (double)sample;
                            break;
                        case AVG:
                        case SUM:
                            specialValue += (double) sample;
                            break;
                    }
                    arrIndex = arrIndex + sampleSize;
                }
            }
        } else {
            while (bytesRead != -1) {
                bytesRead = AudioReader.readNSamples(stream, arr);
                int arrIndex = 0;
                while (arrIndex < bytesRead) {
                    sample = Program.convertBytesToIntLittleEndian(arr, sampleSize, mask, arrIndex, isSigned);
                    switch(agg) {           // TODO: If the compiler doesn't optimize the if outside the loop, then it is really inefficient
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
                            specialValue += sample * (double)sample;
                            break;
                        case AVG:
                        case SUM:
                            specialValue += (double) sample;
                            break;
                    }
                    arrIndex = arrIndex + sampleSize;
                }
            }
        }


        double maxAbsVal = (double) Program.getMaxAbsoluteValue(sampleSize * 8, isSigned);
        specialValue /= maxAbsVal;
        if (agg == RMS) {
            specialValue /= maxAbsVal;
            specialValue = Math.sqrt(specialValue / n);
        }
        else if(agg == AVG) {
            specialValue /= n;
        }

        return specialValue;
    }


    /**
     * Returns all operations from Aggregation performed on given array.
     * @param samples is the given array with samples.
     * @param sampleSize is the size of 1 sample.
     * @param isBigEndian true if the given samples are in big endian, false if in little endian.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @return Returns array with mods in Aggregation order (given by calling Aggregation.values()).
     * @throws IOException is thrown when the sample size is <= 0 or > 4
     */
    public static double[] calculateAllAggregations(byte[] samples, int sampleSize, boolean isBigEndian, boolean isSigned) throws IOException {
        double[] arr = new double[values().length];
        int index = 0;
        for (Aggregation agg : values()) {
            arr[index] = performAggregation(samples, sampleSize, isBigEndian, isSigned, agg);
            index++;
        }
        return arr;
    }
}
