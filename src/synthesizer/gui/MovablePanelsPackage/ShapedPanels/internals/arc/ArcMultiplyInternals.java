package synthesizer.gui.MovablePanelsPackage.ShapedPanels.internals.arc;

import synthesizer.gui.MovablePanelsPackage.ShapedPanels.internals.CenteredRectanglesInternals;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.internals.MultiplyInternals;

public class ArcMultiplyInternals extends CenteredRectanglesInternals {
    public static final int CIRCLE_DIAMETER_DECREASE_DIV_FACTOR = 16;

    public ArcMultiplyInternals() {
        super(MultiplyInternals.RECTANGLE_COUNT, MultiplyInternals.DISTANCE_FROM_CIRCLE_DIV_FACTOR,
                MultiplyInternals.RECTANGLE_THICKNESS_DIV_FACTOR, CIRCLE_DIAMETER_DECREASE_DIV_FACTOR);
    }
}
