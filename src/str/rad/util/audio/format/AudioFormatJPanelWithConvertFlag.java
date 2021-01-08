package str.rad.util.audio.format;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;

public class AudioFormatJPanelWithConvertFlag extends AudioFormatJPanel {
    public AudioFormatJPanelWithConvertFlag(AudioFormat af) {
        super(af);
        addShouldConvertFlag();
    }


    private JCheckBox shouldConvertCheckbox;

    private void addShouldConvertFlag() {
        shouldConvertCheckbox = new JCheckBox("Should convert audio", true);
        shouldConvertCheckbox.setToolTipText("<html>If set to true, " +
                                             "then the currently loaded audio will be converted to the new sample rate.<br>" +
                                             "Otherwise the audio will be kept the same, which will make it play slower/faster</html>");
        this.add(shouldConvertCheckbox);
        this.add(new JPanel());
    }

    public boolean getShouldConvert() {
        return shouldConvertCheckbox.isSelected();
    }
}
