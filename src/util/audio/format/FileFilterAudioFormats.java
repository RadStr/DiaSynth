package util.audio.format;

import javax.sound.sampled.AudioFileFormat;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class FileFilterAudioFormats extends FileFilter {
    public final AudioFileFormat.Type AUDIO_TYPE;
    public final String EXTENSION;

    public FileFilterAudioFormats(AudioFileFormat.Type at) {
        this.AUDIO_TYPE = at;
        EXTENSION = "." + AUDIO_TYPE.getExtension();
    }


    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getAbsolutePath().endsWith(EXTENSION);
    }

    @Override
    public String getDescription() {
        return AUDIO_TYPE.toString() + " (" + EXTENSION + ")";
    }
}
