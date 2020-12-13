package player.format;

import javax.sound.sampled.AudioFormat;
import java.util.Map;

public class AudioFormatWithSign extends AudioFormat {

    /**
     * Constructs an <code>AudioFormat</code> with the given parameters.
     * The encoding specifies the convention used to represent the data.
     * The other parameters are further explained in the {@link AudioFormat
     * class description}.
     *
     * @param encoding         the audio encoding technique
     * @param sampleRate       the number of samples per second
     * @param sampleSizeInBits the number of bits in each sample
     * @param channels         the number of channels (1 for mono, 2 for stereo, and so on)
     * @param frameSize        the number of bytes in each frame
     * @param frameRate        the number of frames per second
     * @param bigEndian        indicates whether the data for a single sample
     *                         is stored in big-endian byte order (<code>false</code>
     */
    public AudioFormatWithSign(Encoding encoding, float sampleRate, int sampleSizeInBits,
                               int channels, int frameSize, float frameRate, boolean bigEndian) {
        super(encoding, sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
        isSigned = getIsSigned(encoding);
    }

    /**
     * Constructs an <code>AudioFormat</code> with the given parameters.
     * The encoding specifies the convention used to represent the data.
     * The other parameters are further explained in the {@link AudioFormat
     * class description}.
     *
     * @param encoding         the audio encoding technique
     * @param sampleRate       the number of samples per second
     * @param sampleSizeInBits the number of bits in each sample
     * @param channels         the number of channels (1 for mono, 2 for
     *                         stereo, and so on)
     * @param frameSize        the number of bytes in each frame
     * @param frameRate        the number of frames per second
     * @param bigEndian        indicates whether the data for a single sample
     *                         is stored in big-endian byte order
     *                         (<code>false</code> means little-endian)
     * @param properties       a <code>Map&lt;String,Object&gt;</code> object
     *                         containing format properties
     * @since 1.5
     */
    public AudioFormatWithSign(Encoding encoding, float sampleRate, int sampleSizeInBits, int channels, int frameSize,
                               float frameRate, boolean bigEndian, Map<String, Object> properties) {
        super(encoding, sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian, properties);
        isSigned = getIsSigned(encoding);
    }

    /**
     * Constructs an <code>AudioFormat</code> with a linear PCM encoding and
     * the given parameters.  The frame size is set to the number of bytes
     * required to contain one sample from each channel, and the frame rate
     * is set to the sample rate.
     *
     * @param sampleRate       the number of samples per second
     * @param sampleSizeInBits the number of bits in each sample
     * @param channels         the number of channels (1 for mono, 2 for stereo, and so on)
     * @param signed           indicates whether the data is signed or unsigned
     * @param bigEndian        indicates whether the data for a single sample
     *                         is stored in big-endian byte order (<code>false</code>
     */
    public AudioFormatWithSign(float sampleRate, int sampleSizeInBits,
                               int channels, boolean signed, boolean bigEndian) {
        super(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        isSigned = signed;
    }

    public AudioFormatWithSign(AudioFormat audioFormat) {
        super(audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(),
                audioFormat.getChannels(), getIsSigned(audioFormat), audioFormat.isBigEndian());
        isSigned = getIsSigned(audioFormat);
    }

    public static boolean getIsSigned(AudioFormat af) {
        return getIsSigned(af.getEncoding());
    }

    public static boolean getIsSigned(Encoding encoding) {
        return Encoding.PCM_UNSIGNED != encoding;
    }

    public final boolean isSigned;
}
