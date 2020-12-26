package util.audio.io;

import util.logging.MyLogger;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class AudioWriter {
    private AudioWriter() {}        // Allow only static access


    // Note: If I want to write stream (in which we don't know the length), I would have to do some workaround -
    // Like write the header and after that write the samples, and then fix the header to correct size.
    public static boolean saveAudio(String path, float sampleRate,
                                    int sampleSizeInBits,
                                    int numberOfChannels, boolean isSigned,
                                    boolean isBigEndian, byte[] input, AudioFileFormat.Type type) {
        AudioFormat af = new AudioFormat(sampleRate, sampleSizeInBits, numberOfChannels, isSigned, isBigEndian);
        return saveAudio(path, af, input, type);
    }
    public static boolean saveAudio(String path, AudioFormat format, byte[] input, AudioFileFormat.Type type) {
        InputStream is = new ByteArrayInputStream(input);
        long frameLen = input.length / format.getFrameSize();
        return saveAudio(path, format, is, frameLen, type);
    }

    public static boolean saveAudio(String path, AudioFormat format, byte[] input,
                                    int startIndex, int endIndex, AudioFileFormat.Type type) {
        InputStream is = new ByteArrayInputStream(input, startIndex, endIndex);
        long frameLen = (endIndex - startIndex) / format.getFrameSize();
        return saveAudio(path, format, is, frameLen, type);
    }


    public static boolean saveAudio(String path, float sampleRate, int sampleSizeInBits, int numberOfChannels,
                                    boolean isSigned, boolean isBigEndian,
                                    InputStream input, long len, AudioFileFormat.Type type) {
        AudioFormat af = new AudioFormat(sampleRate, sampleSizeInBits, numberOfChannels, isSigned, isBigEndian);
        return saveAudio(path, af, input, len, type);
    }
    public static boolean saveAudio(String path, AudioFormat format, InputStream input,
                                    long len, AudioFileFormat.Type type) {
        AudioInputStream ais = new AudioInputStream(input, format, len);
        return saveAudio(path, ais, type);
    }


    public static boolean saveAudio(String path, AudioInputStream audioInputStream, AudioFileFormat.Type type) {
        File f = new File(path + "." + type.getExtension());
        try {
            AudioSystem.write(audioInputStream, type, f);
            return true;
        }
        catch(Exception e) {
            MyLogger.logException(e);
            return false;
        }
    }
}
