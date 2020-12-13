package synthesizer.gui;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class DiagramFileFilter extends FileFilter {
    public static final String DIAGRAM_EXTENSION = ".dia";

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getAbsolutePath().endsWith(DIAGRAM_EXTENSION);
    }

    @Override
    public String getDescription() {
        return "Diagram file audioFormat" + " (" + DIAGRAM_EXTENSION + ")";
    }
}
