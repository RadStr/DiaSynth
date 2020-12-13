package main;

import RocnikovyProjektIFace.AudioFormatChooserPackage.AudioFormatWithSign;

public interface AddToAudioPlayerIFace {
    void addToAudioPlayer(String path);

    /**
     * Works in such way that it is saved but added to the player after the audio player is shown
     */
    void addToAudioPlayer(byte[] audio, int len, AudioFormatWithSign format,
                          boolean shouldConvertToPlayerOutputFormat);
}
