package RocnikovyProjektIFace.AudioFormatChooserPackage;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;


public class AudioFormatClass {
    public AudioFormatClass() {
        // EMPTY
    }
    public AudioFormatClass(AudioFormat audioFormat, int sampleRate) {
        this.audioFormat = audioFormat;
        this.sampleRate = sampleRate;
    }

    public int sampleRate;
    public AudioFormat audioFormat;

    private AudioFormatWithSign chosenFormat;
    public AudioFormatWithSign getChosenFormat() {
        return chosenFormat;
    }
    private boolean isChosenFormatSupported;
    /**
     * Says if the created java audio format is the one chosen
     * @return
     */
    public boolean getIsChosenFormatSupported() {
        return isChosenFormatSupported;
    }

    /**
     * If the created format isn't supported, returns some default one.
     * @return
     */
    public AudioFormatWithSign createJavaAudioFormat(boolean shouldCreateConversionFailedDialog) {
        isChosenFormatSupported = true;
        AudioFormatWithSign chosenFormat;
        boolean isSigned = AudioFormatWithSign.getIsSigned(audioFormat);
        chosenFormat = new AudioFormatWithSign(sampleRate, audioFormat.getSampleSizeInBits(),
                audioFormat.getChannels(), isSigned, audioFormat.isBigEndian());
        AudioFormatWithSign supportedAF = AudioFormatJPanel.getSupportedAudioFormat(chosenFormat);
        if(!chosenFormat.equals(supportedAF)) {
            isChosenFormatSupported = false;
        }

        if(shouldCreateConversionFailedDialog && !isChosenFormatSupported) {
            JOptionPane.showMessageDialog(null,
                    "Not supported format: " + AudioFormatJPanel.createString(chosenFormat) + "\n" +
                            "Chosen format instead: " + AudioFormatJPanel.createString(supportedAF),
                    "Format not supported",
                    JOptionPane.ERROR_MESSAGE);
        }

        return supportedAF;
    }
}
