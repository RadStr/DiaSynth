package str.rad.main;

import str.rad.util.audio.format.AudioFormatWithSign;

public interface AddToAudioPlayerIFace {
    void addToAudioPlayer(String path);

    /**
     * Works in such way that it is saved but added to the player after the audio player is shown
     */
    void addToAudioPlayer(byte[] audio, int len, AudioFormatWithSign format,
                          boolean shouldConvertToPlayerOutputFormat);
}
