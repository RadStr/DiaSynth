package DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals.ArcInternals;

import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals.CenteredRectanglesInternals;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals.PlusInternals;

public class ArcPlusInternals extends CenteredRectanglesInternals {
    public ArcPlusInternals() {
        super(PlusInternals.RECTANGLE_COUNT, PlusInternals.DISTANCE_FROM_CIRCLE_DIV_FACTOR,
                PlusInternals.RECTANGLE_THICKNESS_DIV_FACTOR, ArcMultiplyInternals.CIRCLE_DIAMETER_DECREASE_DIV_FACTOR);
    }
}
