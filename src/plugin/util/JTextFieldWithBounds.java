package plugin.util;

import player.wave.util.LimitDocumentFilterIntAndDouble;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.lang.reflect.Field;

public class JTextFieldWithBounds extends JTextField {
    /**
     * @throws IllegalAccessException is thrown when the field couldn't be accessed
     */
    public JTextFieldWithBounds(boolean isFloatOrDouble, double lowerBoundDouble, double upperBoundDouble,
                                String tooltip, Field field, Object objectContainingField,
                                FieldSetterIFace fieldSetter) throws IllegalAccessException {
        super(field.get(objectContainingField).toString());         // Set it to the default value
        this.setToolTipText(tooltip);
        ((AbstractDocument) this.getDocument()).
                setDocumentFilter(new LimitDocumentFilterIntAndDouble(lowerBoundDouble, upperBoundDouble,
                                                                      isFloatOrDouble, field, fieldSetter));
    }
}
