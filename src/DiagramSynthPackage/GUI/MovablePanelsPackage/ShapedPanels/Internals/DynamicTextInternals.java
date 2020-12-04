package DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals;

import DiagramSynthPackage.Synth.GetStringCallback;
import Rocnikovy_Projekt.ProgramTest;

import javax.swing.*;
import java.awt.*;

public class DynamicTextInternals extends ConstantTextInternals {
    public DynamicTextInternals(GetStringCallback callback, int rectangleWidthDecreaseDivFactor,
                                int rectangleHeightDecreaseDivFactor, int rectangleStartXDecreaseDivFactor,
                                int rectangleStartYDecreaseDivFactor) {
        super(createLabelWithCallback(callback), rectangleWidthDecreaseDivFactor, rectangleHeightDecreaseDivFactor,
              rectangleStartXDecreaseDivFactor, rectangleStartYDecreaseDivFactor);
    }


    public DynamicTextInternals(GetStringCallback callback) {
        this(callback,
             DEFAULT_RECTANGLE_WIDTH_DECREASE_DIV_FACTOR,
             DEFAULT_RECTANGLE_HEIGHT_DECREASE_DIV_FACTOR,
             DEFAULT_RECTANGLE_START_X_DECREASE_DIV_FACTOR,
             DEFAULT_RECTANGLE_START_Y_DECREASE_DIV_FACTOR);
    }

    private static JLabel createLabelWithCallback(GetStringCallback callback) {
        JLabel label = new JLabel() {
            @Override
            public String getText() {
                return callback.getText();
            }
        };

        return label;
    }
}
