package util.audio.format;

import util.Pair;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


public class AudioFormatJPanel extends JPanel {
    public AudioFormatJPanel(AudioFormat oldFormat) {
        // channel count
        // sample rate
        // bit depth (sample size)
        // big/little endian
        format = new AudioFormatClass();

        this.setLayout(new GridLayout(0, 2));
        this.add(new JLabel("Current format:"));
        this.add(new JLabel(createString(oldFormat)));

        addFormatsComponent();
        addSampleRateComponent();
        formatsComboBox.setSelectedIndex(0);
    }

    private static void addUniqueFormat(AudioFormat format) {
        // Currently not supporting more channels than stereo, because I can't test it on my PC.
        if (format.getChannels() > 2) {
            return;
        }


        boolean isUnique = true;
        boolean shouldAdd = false;
        Pair formatPairToAdd = null;
        boolean supportsAllSampleRates = format.getSampleRate() == AudioSystem.NOT_SPECIFIED;
        // If added audioFormat supports more sample rates,
        // then remove all formats which matches the audioFormat, but support only 1 sample rate
        boolean shouldRemoveLessSpecificFormats = false;
        for (int i = availableFormats.size() - 1; i >= 0; i--) {
            Pair<String, AudioFormat> p = availableFormats.get(i);
            AudioFormat f = p.getValue();
            if (!shouldRemoveLessSpecificFormats) {
                boolean matchesAndSupportsAll = format.matches(f) && supportsAllSampleRates;
                shouldRemoveLessSpecificFormats = matchesAndSupportsAll &&
                                                  f.getSampleRate() != AudioSystem.NOT_SPECIFIED;
                if (matchesAndSupportsAll && f.getSampleRate() == AudioSystem.NOT_SPECIFIED) {
                    return;     // It is already there
                }
                shouldAdd = true;
                isUnique = isUnique && !format.matches(f);
            }
            if (shouldRemoveLessSpecificFormats) {
                if (format.matches(f)) {
                    availableFormats.remove(i);
                }
            }
        }
        shouldAdd = shouldAdd || isUnique;
        if (shouldAdd) {
            AudioFormat.Encoding e = format.getEncoding();
            // Can be changed in future to support more formats.
            if ((e == AudioFormat.Encoding.PCM_SIGNED || e == AudioFormat.Encoding.PCM_UNSIGNED) &&
                format.getChannels() <= 2) {
                String formatString = createString(format);
                formatPairToAdd = new Pair(formatString, format);
                availableFormats.add(formatPairToAdd);
            }
        }
    }

    public static String createString(AudioFormat format) {
        String formatString = format.toString().replaceAll("unknown sample rate", "Any sample rate");
        formatString = formatString.trim();
        int lastValidIndex = formatString.length() - 1;
        if (formatString.charAt(lastValidIndex) == ',') {
            formatString = formatString.substring(0, lastValidIndex);
        }

        return formatString;
    }

    private AudioFormatClass format;

    public AudioFormatClass getFormat() {
        return format;
    }

    private static List<Pair<String, AudioFormat>> availableFormats;

    static {
        availableFormats = new ArrayList();
        Mixer.Info[] mixersInfo = AudioSystem.getMixerInfo();
        for (Mixer.Info mixInfo : mixersInfo) {
            Mixer m = AudioSystem.getMixer(mixInfo);
            Line.Info[] sourceLineInfo = m.getSourceLineInfo();
            for (Line.Info lineInfo : sourceLineInfo) {
                if (lineInfo instanceof DataLine.Info) {
                    DataLine.Info dataLineInfo = (DataLine.Info) lineInfo;

                    AudioFormat[] formats = dataLineInfo.getFormats();
                    for (final AudioFormat format : formats) {
                        addUniqueFormat(format);
                    }
                }
            }
        }
    }


    // TODO: Can make it better by returning the closest one, for example in sample rate and number of channels.

    /**
     * @return Returns the audio format if it is supported or returns the first found.
     */
    public static AudioFormatWithSign getSupportedAudioFormat(AudioFormat af) {
        AudioFormatWithSign supportedFormat;
        try {
            SourceDataLine outputAudioFormatLine = AudioSystem.getSourceDataLine(af);
            if (!AudioSystem.isLineSupported(outputAudioFormatLine.getLineInfo())) {
                supportedFormat = AudioFormatJPanel.getFirstAvailableFormat();
            }
            else if (af.getFrameRate() == 0) {       // For example on my system it is 0 when having sample rate == 8000
                supportedFormat = AudioFormatJPanel.getFirstAvailableFormat();
            }
            else {
                supportedFormat = new AudioFormatWithSign(af);
            }
        }
        catch (Exception e) {
            supportedFormat = AudioFormatJPanel.getFirstAvailableFormat();
        }

        return supportedFormat;
    }

    // Copy pasted, but saves 1 allocation, it is micro micro optim

    /**
     * @return Returns the audio format if it is supported or returns the first found.
     */
    public static AudioFormatWithSign getSupportedAudioFormat(AudioFormatWithSign af) {
        AudioFormatWithSign supportedFormat = af;
        try {
            SourceDataLine outputAudioFormatLine = AudioSystem.getSourceDataLine(af);
            if (!AudioSystem.isLineSupported(outputAudioFormatLine.getLineInfo())) {
                supportedFormat = AudioFormatJPanel.getFirstAvailableFormat();
            }
            else if (af.getFrameRate() == 0) {
                // I was doing something wrong before, but I can keep this code here,
                // since such format is not valid anyways
                supportedFormat = AudioFormatJPanel.getFirstAvailableFormat();
            }
            else {
                // Try to open and close the line, it may fail sometimes. For example when sample rate == 1
                outputAudioFormatLine.open();
                outputAudioFormatLine.close();
            }
        }
        catch (Exception e) {
            supportedFormat = AudioFormatJPanel.getFirstAvailableFormat();
        }

        return supportedFormat;
    }


    public static AudioFormatWithSign getFirstAvailableFormat() {
        AudioFormat af = availableFormats.get(0).getValue();
        if (af.getSampleRate() == AudioSystem.NOT_SPECIFIED) {
            // This can end up in cycle and produce stack overflow,
            // but when that happens there is something wrong with the system
            // since the format should be supported.
            return new AudioFormatClass(af, 44100).createJavaAudioFormat(false);
        }
        else {
            return new AudioFormatWithSign(af);
        }
    }


    private JComboBox formatsComboBox;

    private void addFormatsComponent() {
        JLabel label = new JLabel("Format:");
        this.add(label);

        String[] availableFormatsNames = new String[availableFormats.size()];
        for (int i = 0; i < availableFormatsNames.length; i++) {
            availableFormatsNames[i] = availableFormats.get(i).getKey();
        }

        formatsComboBox = new JComboBox(availableFormatsNames);
        formatsComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                int selectedIndex = cb.getSelectedIndex();
                format.audioFormat = availableFormats.get(selectedIndex).getValue();
                changePanelBasedOnFormat(selectedIndex);
            }
        });

        this.add(formatsComboBox);
    }


    private void changePanelBasedOnFormat(int selectedFormatIndex) {
        Pair<String, AudioFormat> p = availableFormats.get(selectedFormatIndex);
        float sampleRate = p.getValue().getSampleRate();

        if (sampleRate == AudioSystem.NOT_SPECIFIED) {
            supportedSampleRatesComboBox.setEnabled(true);
            supportedSampleRatesComboBox.setModel(new DefaultComboBoxModel<>(allFormatsString));
        }
        else {
            supportedSampleRatesComboBox.setEnabled(false);
            supportedSampleRatesComboBox.setModel(new DefaultComboBoxModel<>(
                    new String[]{
                            String.format("%.3f", sampleRate / (double) 1000)
                    }));
            format.sampleRate = (int) sampleRate;
        }
    }

    private static final int[] ALL_FORMATS_INT = new int[]{
            8000, 11025, 12000, 16000, 22050, 24000, 32000, 44100, 48000, 88200, 96000, 192000
    };

    private static final String[] allFormatsString = createAllFormatsString();

    private static String[] createAllFormatsString() {
        String[] strings = new String[ALL_FORMATS_INT.length];
        for (int i = 0; i < ALL_FORMATS_INT.length; i++) {
            strings[i] = String.format("%.3f", ALL_FORMATS_INT[i] / (double) 1000);
        }

        return strings;
    }

    private JComboBox supportedSampleRatesComboBox;

    private void addSampleRateComponent() {
        this.add(new JLabel("Sample rate (in kHz):"));
        supportedSampleRatesComboBox = new JComboBox(allFormatsString);
        supportedSampleRatesComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                int selectedIndex = cb.getSelectedIndex();
                format.sampleRate = ALL_FORMATS_INT[selectedIndex];
            }
        });

        this.add(supportedSampleRatesComboBox);
        supportedSampleRatesComboBox.setSelectedIndex(0);
    }
}