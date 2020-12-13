package RocnikovyProjektIFace;

import RocnikovyProjektIFace.plugin.SetFieldIFace;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import java.lang.reflect.Field;

// Uses copy pasted from the LimitDocumentFilterInt - I can't effectively reuse the code since, it calls replace on the parent internally
// And the doubles will need that too
public class LimitDocumentFilterIntAndDouble extends DocumentFilter {
    public LimitDocumentFilterIntAndDouble(double lowerBound, double upperBound, boolean isFloatOrDouble,
                                           Field field, SetFieldIFace setFieldIFace) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.isFloatOrDouble = isFloatOrDouble;
        this.field = field;
        this.setFieldIFace = setFieldIFace;
        hasDecimalPoint = false;
    }


    private double lowerBound;
    private double upperBound;
    private boolean isFloatOrDouble;
    private boolean hasDecimalPoint;
    private Field field;
    private SetFieldIFace setFieldIFace;


    // text is the added text, and it is starting at offset in the old string
    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        String oldNumString = internalRemove(fb, offset, length);

        String prefix = oldNumString.substring(0, offset);
        String suffix = oldNumString.substring(offset);
        boolean alreadyContainsMinus = prefix.contains("-") || suffix.contains("-");
        boolean isJustMinus = false;

        if(text.indexOf('-') == 0) {
            if(alreadyContainsMinus) {
                return;
            }
            // It is only '-' and neither the prefix and suffix doesn't contain '-'
            else if(text.length() == 1) {
                if(prefix.length() == 0 && suffix.length() == 0 && lowerBound < 0) {
                    internalReplace(fb, offset, length, text, attrs, text);
                    return;
                }
                isJustMinus = true;
            }
        }

        // The new given number is either just minus or int or double there is no other possibility
        String newNumString = prefix + text + suffix;
        if(LimitDocumentFilterInt.isInteger(text, 10) || isJustMinus) {       // Check if the new text are only digits
            double newNum;

            try {
                if(isFloatOrDouble) {
                    newNum = Double.parseDouble(newNumString);
                }
                else {
                    newNum = Integer.parseInt(newNumString);
                }
            }
            catch(NumberFormatException e) {
                return;
            }

            ifInBoundsReplace(fb, offset, length, text, attrs, newNum, newNumString);
        }
        else if(isFloatOrDouble && text.indexOf('.') != -1 && !hasDecimalPoint &&
                !(text.indexOf('.') == 0 && prefix.length() == 0) || isJustMinus) { // I don't allow the . to be at start
            hasDecimalPoint = true;

            if(text.indexOf('.') == text.length() - 1 && "".equals(suffix)) {
                newNumString += "0";
            }
            double newNum;
            try {
                newNum = Double.parseDouble(newNumString);
            }
            catch(NumberFormatException e) {
                return;
            }

            ifInBoundsReplace(fb, offset, length, text, attrs, newNum, newNumString);
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        internalRemove(fb, offset, length);
        super.remove(fb, offset, length);
    }

    private String internalRemove(FilterBypass fb, int offset, int length) throws BadLocationException {
        Document doc = fb.getDocument();
        String oldNumString = doc.getText(0, doc.getLength());  // The number I had before
        if(length > 0) {
            String removedSubString = oldNumString.substring(offset, offset + length);
            if(removedSubString.contains(".")) {
                hasDecimalPoint = false;
            }
            oldNumString = oldNumString.substring(0, offset) + oldNumString.substring(offset + length);
        }

        setFieldIFace.setField(field, oldNumString);
        return oldNumString;
    }


    private void ifInBoundsReplace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs,
                                   double newNum, String newNumString) throws BadLocationException {
        if (newNum >= lowerBound && newNum <= upperBound) {
            internalReplace(fb, offset, length, text, attrs, newNumString);
        }
        // Else the number is too big or too small, so the new text won't be added
    }

    private void internalReplace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs,
                                 String newNumString) throws BadLocationException {
        setFieldIFace.setField(field, newNumString);
        super.replace(fb, offset, length, text, attrs);
    }
}
