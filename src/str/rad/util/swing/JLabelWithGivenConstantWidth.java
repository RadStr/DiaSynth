package str.rad.util.swing;

import javax.swing.*;
import java.awt.*;

public class JLabelWithGivenConstantWidth extends JLabel {
    public JLabelWithGivenConstantWidth(int charCount, String text) {
        super(text);
        Font f = this.getFont();
        FontMetrics fm = this.getFontMetrics(f);
        CHAR_WIDTH = SwingUtils.calculateMaxWidthAlfanum(fm);
        SIZE = new Dimension(CHAR_WIDTH * charCount, fm.getHeight());
    }

    private final int CHAR_WIDTH;
    private final Dimension SIZE;

    @Override
    public Dimension getPreferredSize() {
        return SIZE;
    }

    @Override
    public Dimension getMinimumSize() {
        return SIZE;
    }

    @Override
    public Dimension getMaximumSize() {
        return SIZE;
    }
}
