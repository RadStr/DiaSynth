package RocnikovyProjektIFace.plugin;

import RocnikovyProjektIFace.LimitDocumentFilterIntAndDouble;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.lang.reflect.Field;

public class JTextFieldWithBounds extends JTextField {
    /**
     *
     * @param isFloatOrDouble
     * @param lowerBoundDouble
     * @param upperBoundDouble
     * @param tooltip
     * @param field
     * @param objectContainingField
     * @param setFieldIFace
     * @throws IllegalAccessException is thrown when the field couldn't be accessed
     */
    public JTextFieldWithBounds(boolean isFloatOrDouble, double lowerBoundDouble, double upperBoundDouble,
                                String tooltip, Field field, Object objectContainingField, SetFieldIFace setFieldIFace) throws IllegalAccessException {
        super(field.get(objectContainingField).toString());         // Set it to the default value
        this.setToolTipText(tooltip);
        ((AbstractDocument) this.getDocument()).
            setDocumentFilter(new LimitDocumentFilterIntAndDouble(lowerBoundDouble, upperBoundDouble, isFloatOrDouble,
                field, setFieldIFace));
    }
}
