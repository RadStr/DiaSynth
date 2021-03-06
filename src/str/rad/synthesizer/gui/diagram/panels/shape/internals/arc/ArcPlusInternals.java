package str.rad.synthesizer.gui.diagram.panels.shape.internals.arc;

import str.rad.synthesizer.gui.diagram.panels.shape.internals.CenteredRectanglesInternals;
import str.rad.synthesizer.gui.diagram.panels.shape.internals.PlusInternals;

public class ArcPlusInternals extends CenteredRectanglesInternals {
    public ArcPlusInternals() {
        super(PlusInternals.RECTANGLE_COUNT, PlusInternals.DISTANCE_FROM_CIRCLE_DIV_FACTOR,
              PlusInternals.RECTANGLE_THICKNESS_DIV_FACTOR, ArcMultiplyInternals.CIRCLE_DIAMETER_DECREASE_DIV_FACTOR);
    }
}
