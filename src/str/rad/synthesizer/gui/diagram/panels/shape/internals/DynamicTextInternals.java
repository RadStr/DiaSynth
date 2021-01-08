package str.rad.synthesizer.gui.diagram.panels.shape.internals;

import str.rad.synthesizer.synth.StringGetterIFace;

import javax.swing.*;

public class DynamicTextInternals extends ConstantTextInternals {
    public DynamicTextInternals(StringGetterIFace callback, int rectangleWidthDecreaseDivFactor,
                                int rectangleHeightDecreaseDivFactor, int rectangleStartXDecreaseDivFactor,
                                int rectangleStartYDecreaseDivFactor) {
        super(createLabelWithCallback(callback), rectangleWidthDecreaseDivFactor, rectangleHeightDecreaseDivFactor,
              rectangleStartXDecreaseDivFactor, rectangleStartYDecreaseDivFactor);
    }


    public DynamicTextInternals(StringGetterIFace callback) {
        this(callback,
             DEFAULT_RECTANGLE_WIDTH_DECREASE_DIV_FACTOR,
             DEFAULT_RECTANGLE_HEIGHT_DECREASE_DIV_FACTOR,
             DEFAULT_RECTANGLE_START_X_DECREASE_DIV_FACTOR,
             DEFAULT_RECTANGLE_START_Y_DECREASE_DIV_FACTOR);
    }

    private static JLabel createLabelWithCallback(StringGetterIFace callback) {
        JLabel label = new JLabel() {
            @Override
            public String getText() {
                return callback.getText();
            }
        };

        return label;
    }
}
