package synthesizer.gui.diagram.ShapedPanels.internals;

public final class MultiplyInternals extends CenteredRectanglesInternals {
    public static final int CIRCLE_DIAMETER_DECREASE_DIV_FACTOR = Integer.MAX_VALUE;
    public static final int DISTANCE_FROM_CIRCLE_DIV_FACTOR = 4;
    public static final int RECTANGLE_THICKNESS_DIV_FACTOR = 16;
    public static final int RECTANGLE_COUNT = 4;

    public MultiplyInternals() {
        super(RECTANGLE_COUNT, DISTANCE_FROM_CIRCLE_DIV_FACTOR, RECTANGLE_THICKNESS_DIV_FACTOR, CIRCLE_DIAMETER_DECREASE_DIV_FACTOR);
    }
}