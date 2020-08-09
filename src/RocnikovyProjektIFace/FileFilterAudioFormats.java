package RocnikovyProjektIFace;

import javax.sound.sampled.AudioFileFormat;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class FileFilterAudioFormats extends FileFilter {
    public final AudioFileFormat.Type audioType;
    public final String EXTENSION;

    public FileFilterAudioFormats(AudioFileFormat.Type at) {
        this.audioType = at;
        EXTENSION = "." + audioType.getExtension();
    }


    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getAbsolutePath().endsWith(EXTENSION);
    }

    @Override
    public String getDescription() {
        return audioType.toString() + " (" + EXTENSION + ")";
    }
}
