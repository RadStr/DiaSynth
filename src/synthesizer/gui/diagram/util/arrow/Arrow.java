package synthesizer.gui.diagram.util.arrow;


import java.awt.*;

public class Arrow {
    public static final int ARROW_POINT_COUNT = 7;

    public Arrow() {
        arrowPoints = new Point[ARROW_POINT_COUNT];
        for (int i = 0; i < arrowPoints.length; i++) {
            arrowPoints[i] = new Point();
        }
        arrowPolygon = new Polygon(new int[ARROW_POINT_COUNT], new int[ARROW_POINT_COUNT], ARROW_POINT_COUNT);
    }

    /**
     * Represents the points of polygon. It is final so only the values can be changed.
     * Also after changing arrow points, setArrowPolygon method should be called.
     */
    public final Point[] arrowPoints;
    /**
     * Represents the polygon. It is final so only the values can be changed.
     */
    public final Polygon arrowPolygon;

    public void setArrowPolygon() {
        for (int i = 0; i < arrowPoints.length; i++) {
            arrowPolygon.xpoints[i] = arrowPoints[i].x;
            arrowPolygon.ypoints[i] = arrowPoints[i].y;
        }
    }
}
