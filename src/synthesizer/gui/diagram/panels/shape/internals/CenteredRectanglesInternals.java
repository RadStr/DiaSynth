package synthesizer.gui.diagram.panels.shape.internals;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public class CenteredRectanglesInternals implements ShapedPanelInternals {
    private final int DISTANCE_FROM_CIRCLE_DIV_FACTOR;
    private final int RECTANGLE_THICKNESS_DIV_FACTOR;
    private final double START_ANGLE;

    /**
     *
     * @param rectangleCount
     * @param distanceFromCircleDivFactor
     * @param rectangleThicknessDivFactor
     * @param circleDiameterDecreaseDivFactor Divides the diameter of the circle
     *                                        I am putting the internal to and by that value it moves it up (down if given number is < 0).
     *                                        If the value is Integer.MAX_VALUE then it doesn't move it.
     */
    public CenteredRectanglesInternals(int startAngle, int rectangleCount, int distanceFromCircleDivFactor,
                                       int rectangleThicknessDivFactor, int circleDiameterDecreaseDivFactor) {
        DISTANCE_FROM_CIRCLE_DIV_FACTOR = distanceFromCircleDivFactor;
        RECTANGLE_THICKNESS_DIV_FACTOR = rectangleThicknessDivFactor;
        this.CIRCLE_DIAMETER_DECREASE_DIV_FACTOR = circleDiameterDecreaseDivFactor;
        START_ANGLE = Math.toRadians(startAngle);
        constructor(rectangleCount);
    }


    /**
     *
     * @param rectangleCount
     * @param distanceFromCircleDivFactor
     * @param rectangleThicknessDivFactor
     * @param circleDiameterDecreaseDivFactor Divides the diameter of the circle
     *                                        I am putting the internal to and by that value it moves it up (down if given number is < 0).
     *                                        If the value is Integer.MAX_VALUE then it doesn't move it.
     */
    public CenteredRectanglesInternals(int rectangleCount, int distanceFromCircleDivFactor,
                                       int rectangleThicknessDivFactor, int circleDiameterDecreaseDivFactor) {
        this(0, rectangleCount, distanceFromCircleDivFactor,
                rectangleThicknessDivFactor, circleDiameterDecreaseDivFactor);
    }



    private void constructor(int rectangleCount) {
        verticalRectangle = new Rectangle2D.Double();
        rectangles = new Shape[rectangleCount];
        for (int i = 0; i < rectangles.length; i++) {
            rectangles[i] = new Path2D.Double();
        }
    }

    private Rectangle2D verticalRectangle;
    private Shape[] rectangles;
    /**
     * Divides the diameter of the circle
     * I am putting the internal to and by that value it moves it up (down if given number is < 0).
     * If the value is Integer.MAX_VALUE then it doesn't move it.
     */
    private final int CIRCLE_DIAMETER_DECREASE_DIV_FACTOR;


    @Override
    public void reshape(Dimension newSize) {
        int minD = Math.min(newSize.width, newSize.height);
        int widthMinDif = newSize.width - minD;
        int heightMinDif = newSize.height - minD;


        int w = minD / RECTANGLE_THICKNESS_DIV_FACTOR;
        int x = widthMinDif / 2 + minD / 2 - w / 2;

        int relY = minD / DISTANCE_FROM_CIRCLE_DIV_FACTOR;        // relative to circle
        int absY = relY + heightMinDif / 2;
        int h = minD - 2 * relY;

        if(CIRCLE_DIAMETER_DECREASE_DIV_FACTOR != Integer.MAX_VALUE) {
            absY -= minD / CIRCLE_DIAMETER_DECREASE_DIV_FACTOR;
        }
        verticalRectangle.setRect(x, absY, w, h);
        rectangles[0] = verticalRectangle;
        if(START_ANGLE != 0) {
            AffineTransform at = AffineTransform.getRotateInstance(START_ANGLE,
                    x + w / 2, absY + h / 2);
            Shape rectangle = at.createTransformedShape(rectangles[0]);
            rectangles[0] = rectangle;
        }
        for (int i = 1; i < rectangles.length; i++) {
            AffineTransform at = AffineTransform.getRotateInstance(i * Math.PI / rectangles.length,
                    x + w / 2, absY + h / 2);
            Shape rectangle = at.createTransformedShape(rectangles[0]);
            rectangles[i] = rectangle;
        }
    }


    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        Graphics2D g2 = (Graphics2D)g;
        for(int i = 0; i < rectangles.length; i++) {
            g2.fill(rectangles[i]);
        }
    }

    @Override
    public ShapedPanelInternals createCopy() {
        return new CenteredRectanglesInternals(rectangles.length, DISTANCE_FROM_CIRCLE_DIV_FACTOR,
                                               RECTANGLE_THICKNESS_DIV_FACTOR, CIRCLE_DIAMETER_DECREASE_DIV_FACTOR);
    }
}
