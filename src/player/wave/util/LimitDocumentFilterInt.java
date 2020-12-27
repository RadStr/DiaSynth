package player.wave.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

// Inspired by https://stackoverflow.com/questions/10136794/limiting-the-number-of-characters-in-a-jtextfield

/**
 * Used for limiting number written in document. It cannot exceed certain number threshold (which may change dynamically).
 * Used in WaveMainPanel.
 */
public class LimitDocumentFilterInt extends DocumentFilter {
    private LimitGetterIFace getter;

    public LimitDocumentFilterInt(LimitGetterIFace getter) {
        this.getter = getter;
    }


    // text is the added text, and it is starting at offset in the old string
    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        Document doc = fb.getDocument();
        String oldNumString = doc.getText(0, doc.getLength());  // The number I had before
        if (length > 0) {
            oldNumString = oldNumString.substring(0, offset) + oldNumString.substring(offset + length);
        }

        // Check if the new text are only digits
        if (!isZeroFirst(text, offset) && isPositiveInteger(text, 10)) {
            String a = oldNumString.substring(0, offset);
            String b = oldNumString.substring(offset);
            String newNumString = a + text + b;

            int newNum;
            try {
                // If the added string has always length 1, then the exception won't happen
                // (There would have to be around 2^29 audio tracks)
                newNum = Integer.parseInt(newNumString);
            }
            catch (NumberFormatException e) {
                return;
            }
            if (newNum <= getter.getLimit()) {
                super.replace(fb, offset, length, text, attrs);
                getter.revalidateMethod();
            }
            // Else it is number which is too big, so the new text won't be added
        }
    }


    /**
     * Return true if either the string is empty or when offset == 0 and there is zero as first character in string
     *
     * @param s is the string to check
     * @return Return true if either the string is empty or there is zero as first character in string
     */
    public static boolean isZeroFirst(String s, int offset) {
        if (s.isEmpty()) {
            return true;
        }
        if (offset == 0) {
            return s.charAt(0) == '0';
        }
        else {
            return false;
        }
    }

    // Taken from: https://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
    // Removed the minus sign, since it is not valid in our case.
    // Doesn't check if string is empty, that is already checked by method isZeroFirst
    public static boolean isPositiveInteger(String s, int radix) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.digit(s.charAt(i), radix) < 0) {
                return false;
            }
        }
        return true;
    }


    // Taken from: https://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }
}
