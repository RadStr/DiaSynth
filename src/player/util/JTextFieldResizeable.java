package player.util;

import Rocnikovy_Projekt.Program;

import java.awt.*;

public class JTextFieldResizeable extends javax.swing.JTextField {
    private Dimension prefSize;
    public static final int CHAR_COUNT = 3;

    public JTextFieldResizeable(String s) {
        super(s);
        LARGEST_WIDTH_DIGIT = calculateMaxWidthDigit();
        setPreferredSize(CHAR_COUNT);
    }

    public Dimension setPreferredSize(int charCount) {
        Font f = this.getFont();
        FontMetrics fm = this.getFontMetrics(f);
        charCount++;        // Needs a bit extra
        setInternalPreferredSize(new Dimension(LARGEST_WIDTH_DIGIT * charCount, fm.getHeight()));
        return prefSize;
    }

    public void setInternalPreferredSize(Dimension dim) {
        prefSize = dim;
    }

    @Override
    public Dimension getPreferredSize() {
        return prefSize;
    }

    private final int LARGEST_WIDTH_DIGIT;
    private int calculateMaxWidthDigit() {
        Font f = this.getFont();
        FontMetrics fm = this.getFontMetrics(f);
        return Program.calculateMaxWidthDigit(fm);
    }
}
