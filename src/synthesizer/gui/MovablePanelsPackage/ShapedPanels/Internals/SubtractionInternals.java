package synthesizer.gui.MovablePanelsPackage.ShapedPanels.Internals;

public class SubtractionInternals extends CenteredRectanglesInternals {
    public static final int CIRCLE_DIAMETER_DECREASE = Integer.MAX_VALUE;
    public static final int DISTANCE_FROM_CIRCLE_DIV_FACTOR = 4;
    public static final int RECTANGLE_THICKNESS_DIV_FACTOR = 8;
    public static final int RECTANGLE_COUNT = 1;


    public SubtractionInternals() {
        super(90, RECTANGLE_COUNT, DISTANCE_FROM_CIRCLE_DIV_FACTOR,
                RECTANGLE_THICKNESS_DIV_FACTOR, CIRCLE_DIAMETER_DECREASE);
    }
}
