package RocnikovyProjektIFace.AudioFormatChooserPackage;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class AudioFormatJPanelWithShouldConvertFlag extends AudioFormatJPanel {
    public AudioFormatJPanelWithShouldConvertFlag(AudioFormat af) {
        super(af);
        addShouldConvertFlag();
    }


    private JCheckBox shouldConvertCheckbox;

    private void addShouldConvertFlag() {
        shouldConvertCheckbox = new JCheckBox("Should convert audio", true);
        shouldConvertCheckbox.setToolTipText("<html>If set to true, then the currently loaded audio will be converted to the new sample rate.<br>" +
                "Otherwise the audio will be kept the same, which will make it play slower/faster</html>");
        this.add(shouldConvertCheckbox);
        this.add(new JPanel());
    }

    public boolean getShouldConvert() {
        return shouldConvertCheckbox.isSelected();
    }
}
