package util.audio;


import Rocnikovy_Projekt.Program;

import javax.sound.sampled.*;
import java.io.IOException;

public class AudioUtilities {
    private AudioUtilities() {}      // Allow only static access

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
            Program.reverseArr(song, audioFormat.getSampleSizeInBits() / 8);
        }
        // because number of frames needs to be integer, so if some last bytes doesn't fit in the last frame,
        // we don't play them
        int bytesToWrite = song.length - (song.length % frameSize);
        bytesWritten = line.write(song, 0, bytesToWrite);
        line.drain();
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
    public static void playSong(byte[] song, AudioFormat.Encoding encoding, int sampleRate, int sampleSizeInBits,
                                int numberOfChannels, int frameSize, float frameRate, boolean isBigEndian,
                                boolean playBackwards) throws LineUnavailableException {
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
                Program.reverseArr(songParts[i].songPart, audioFormat.getSampleSizeInBits() / 8);
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
    public static void playSongParts(SongPartWithAverageValueOfSamples[] songParts, AudioFormat.Encoding encoding,
                                     int sampleRate, int sampleSizeInBits, int numberOfChannels, int frameSize, float frameRate,
                                     boolean isBigEndian, boolean ascending, boolean playBackwards) throws LineUnavailableException {

        AudioFormat audioFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits, numberOfChannels, frameSize, frameRate, isBigEndian);
        playSongParts(songParts, audioFormat, ascending, playBackwards);
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
        for(int i = 0; i < binFreqs.length; i++, currFreqHz += freqJump * takeEveryNthFreq) {
            double currFreqKhz = currFreqHz / 1000;
            String freqString = String.format("%." + precision +"f", currFreqKhz);
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
     * @param frame
     * @param sizeOfOneSec
     * @return
     */
    public static int convertFrameToMillis(int frame, int sizeOfOneSec) {
        return (int)(1000 * (double)frame / sizeOfOneSec);
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
}
