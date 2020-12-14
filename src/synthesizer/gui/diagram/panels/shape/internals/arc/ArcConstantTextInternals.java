package synthesizer.gui.diagram.panels.shape.internals.arc;

import synthesizer.gui.diagram.panels.shape.internals.ConstantTextInternals;

import javax.swing.*;

public class ArcConstantTextInternals extends ConstantTextInternals {
    public static final int DEFAULT_RECTANGLE_WIDTH_DECREASE_DIV_FACTOR = 2;
    public static final int DEFAULT_RECTANGLE_HEIGHT_DECREASE_DIV_FACTOR = 2;
    public static final int DEFAULT_RECTANGLE_START_X_DECREASE_DIV_FACTOR = -128;
    public static final int DEFAULT_RECTANGLE_START_Y_DECREASE_DIV_FACTOR = 8;

    public ArcConstantTextInternals(JLabel label, int rectangleWidthDecreaseDivFactor,
                                    int rectangleHeightDecreaseDivFactor, int rectangleStartXDecreaseDivFactor,
                                    int rectangleStartYDecreaseDivFactor) {
        super(label, rectangleWidthDecreaseDivFactor, rectangleHeightDecreaseDivFactor,
                rectangleStartXDecreaseDivFactor, rectangleStartYDecreaseDivFactor);
    }

    public ArcConstantTextInternals(String text, int rectangleWidthDecreaseDivFactor,
                                    int rectangleHeightDecreaseDivFactor, int rectangleStartXDecreaseDivFactor,
                                    int rectangleStartYDecreaseDivFactor) {
        super(text, rectangleWidthDecreaseDivFactor, rectangleHeightDecreaseDivFactor,
                rectangleStartXDecreaseDivFactor, rectangleStartYDecreaseDivFactor);
    }

    public ArcConstantTextInternals(String text) {
        super(text, DEFAULT_RECTANGLE_WIDTH_DECREASE_DIV_FACTOR, DEFAULT_RECTANGLE_HEIGHT_DECREASE_DIV_FACTOR,
                DEFAULT_RECTANGLE_START_X_DECREASE_DIV_FACTOR, DEFAULT_RECTANGLE_START_Y_DECREASE_DIV_FACTOR);
    }
}
