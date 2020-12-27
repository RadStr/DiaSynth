package player.popup;

import javax.swing.*;

public class PasteItem extends JMenuItem {
    public PasteItem(int copyCount) {
        super(copyCount + "x");
        this.copyCount = copyCount;
    }

    private int copyCount;

    public int getCopyCount() {
        return copyCount;
    }
}
