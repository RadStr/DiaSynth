package RocnikovyProjektIFace.AudioWavePanelOnlyWavePopupMenuPackage;

import javax.swing.*;

public class PasteItem extends JMenuItem {
    public PasteItem(int copyCount) {
        super(Integer.toString(copyCount) + "x");
        this.copyCount = copyCount;
    }

    private int copyCount;
    public int getCopyCount() {
        return copyCount;
    }
}
