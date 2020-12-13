package synthesizer.GUI.MovablePanelsPackage.ShapedPanels.Internals;

public final class PlusInternals extends CenteredRectanglesInternals {
	public static final int CIRCLE_DIAMETER_DECREASE = Integer.MAX_VALUE;
	public static final int DISTANCE_FROM_CIRCLE_DIV_FACTOR = 4;
	public static final int RECTANGLE_THICKNESS_DIV_FACTOR = 8;
	public static final int RECTANGLE_COUNT = 2;


	public PlusInternals() {
		super(RECTANGLE_COUNT, DISTANCE_FROM_CIRCLE_DIV_FACTOR, RECTANGLE_THICKNESS_DIV_FACTOR, CIRCLE_DIAMETER_DECREASE);
	}
}


