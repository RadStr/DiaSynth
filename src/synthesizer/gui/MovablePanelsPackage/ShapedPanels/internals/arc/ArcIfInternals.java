package synthesizer.gui.MovablePanelsPackage.ShapedPanels.internals.arc;

import synthesizer.gui.MovablePanelsPackage.ShapedPanels.internals.ConstantTextInternals;

public class ArcIfInternals extends ConstantTextInternals {
    public static final int RECTANGLE_START_Y_DECREASE_DIV_FACTOR = 16;

    public ArcIfInternals() {
        super("IF",
                ConstantTextInternals.DEFAULT_RECTANGLE_WIDTH_DECREASE_DIV_FACTOR,
                ConstantTextInternals.DEFAULT_RECTANGLE_HEIGHT_DECREASE_DIV_FACTOR,
                ConstantTextInternals.DEFAULT_RECTANGLE_START_X_DECREASE_DIV_FACTOR,
                RECTANGLE_START_Y_DECREASE_DIV_FACTOR);
    }
}
