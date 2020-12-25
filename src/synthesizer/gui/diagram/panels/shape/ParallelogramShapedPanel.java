package synthesizer.gui.diagram.panels.shape;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.MovableJPanel;
import synthesizer.gui.diagram.panels.shape.internals.ShapedPanelInternals;
import synthesizer.UnitViewForGUIIFace;

import java.awt.*;
import java.awt.geom.Area;

public class ParallelogramShapedPanel extends ShapedPanel {
    private static final int TOP_THICKNESS_DIV_FACTOR = 16;
    private static final int LEFT_THICKNESS_DIV_FACTOR = 16;


    /**
     *
     * @param angle is in degrees. It is the left angle at the left bot point. (bot right -> bot left -> top left).
     * Works correctly for angle > 0 && angle < 180. If angle < 0 && angle > -180 then it also works, but probably not the way user intended.
     */
    public ParallelogramShapedPanel(DiagramPanel diagramPanel, int angle,
                                    ShapedPanelInternals internals, UnitViewForGUIIFace unit) {
        super(diagramPanel, internals, unit);
        constructor(angle);
    }

    /**
     *
     * @param angle is in degrees. It is the left angle at the left bot point. (bot right -> bot left -> top left).
     * Works correctly for angle > 0 && angle < 180. If angle < 0 && angle > -180 then it also works, but probably not the way user intended.
     */
    public ParallelogramShapedPanel(int relativeX, int relativeY, int w, int h,
                                    DiagramPanel diagramPanel, int angle,
                                    ShapedPanelInternals internals, UnitViewForGUIIFace unit) {
        super(relativeX, relativeY, w, h, diagramPanel, internals, unit);
        constructor(angle);
    }

    /**
     *
     * @param angle is in degrees. It is the left angle at the left bot point. (bot right -> bot left -> top left).
     * Works correctly for angle > 0 && angle < 180. If angle < 0 && angle > -180 then it also works, but probably not the way user intended.
     */
    public ParallelogramShapedPanel(int relativeX, int relativeY, DiagramPanel diagramPanel,
                                    int angle, ShapedPanelInternals internals, UnitViewForGUIIFace unit) {
        super(relativeX, relativeY, diagramPanel, internals, unit);
        constructor(angle);
    }


    /**
     *
     * @param angle is in degrees. It is the left angle at the left bot point. (bot right -> bot left -> top left).
     * Works correctly for angle > 0 && angle < 180. If angle < 0 && angle > -180 then it also works, but probably not the way user intended.
     */
    private void constructor(int angle) {
        parallelogram = new Area();
        outerParallelogram = new Polygon();
        panelShape = outerParallelogram;
        this.angle = angle;
    }

    /**
     * It just view on the panelShape, looking at it as polygon.
     */
    protected Polygon outerParallelogram;
    private Area parallelogram;
    private int angle;
    public int getAngle() {
        return angle;
    }
    public void setAngle(int newAngle) {
        angle = newAngle;
        reshape(this.getSize());
    }


    @Override
    public void reshape(Dimension newSize) {
        outerParallelogram.reset();
        int w = newSize.width;
        int h = newSize.height;
        // Non-Equilateral variant
        createOuterParallelogram(outerParallelogram, w, h, angle);
        createParallelogramInternalMethod(w, h);

        reshapeInternals(newSize);
    }

    protected void createParallelogramInternalMethod(int w, int h) {
        int leftThickness = w / LEFT_THICKNESS_DIV_FACTOR;          // PARAMETER TO PLAY WITH
        int topThickness = h / TOP_THICKNESS_DIV_FACTOR;            // PARAMETER TO PLAY WITH
        createParallelogram(topThickness, leftThickness);
    }


    /**
     * Stores parallelogram to the parameter result.
     * @param result is the polygon to store the parallelogram to.
     * @param width is the width to which should the parallelogram fit
     * @param height is the height to which should the parallelogram fit
     * @param angle is in degrees. It is the left angle at the left bot point. (bot right -> bot left -> top left).
     * Works correctly for angle > 0 && angle < 180. If angle < 0 && angle > -180 then it also works, but probably not the way user intended.
     */
    public static void createOuterParallelogram(Polygon result, int width, int height, int angle) {
        int supplementAngle;
        double supplementAngleRads;
        int leftSideLen;
        int botDif;

        if(angle > 90) {
            supplementAngle = 180 - angle;
            supplementAngleRads = Math.toRadians(supplementAngle);
            leftSideLen = (int) (height / Math.sin(supplementAngleRads));
            botDif = (int) (Math.cos(supplementAngleRads) * leftSideLen);
        }
        else {
            double angleRads = Math.toRadians(angle);
            leftSideLen = (int) (height / Math.sin(angleRads));
            botDif = (int) (Math.cos(angleRads) * leftSideLen);
        }
        int parallelogramBotLen = width - botDif;

        int startX;
        int startY = 0;
        if(angle < 90) {
            startX = botDif;
        }
        else {
            startX = 0;
        }

        result.reset();
        int x = startX;
        int y = startY;
        result.addPoint(x, y);
        x += parallelogramBotLen;
        result.addPoint(x, y);
        y += height;
        if(angle < 90) {
            x = width - botDif;
        }
        else {
            x = width;
        }


        result.addPoint(x, y);
        x -= parallelogramBotLen;
        result.addPoint(x, y);
    }

    /**
     * Creates parallelogram
     * @param width is the width to which should the parallelogram fit
     * @param height is the height to which should the parallelogram fit
     * @param angle is in degrees. It is the left angle at the left bot point. (bot right -> bot left -> top left).
     * Works correctly for angle > 0 && angle < 180. If angle < 0 && angle > -180 then it also works, but probably not the way user intended.
     * @return Returns instance of polygon representing the parallelogram.
     */
    public static Polygon createOuterParallelogram(int width, int height, int angle) {
        Polygon p = new Polygon();
        createOuterParallelogram(p, width, height, angle);
        return p;
    }


    /**
     * Creates area of outerParallelogram and thicknesses.
     * @param outerParallelogram is the outer parallelogram
     * @param angle is in degrees. It is the left angle at the left bot point. (bot right -> bot left -> top left).
     * Works correctly for angle > 0 && angle < 180. If angle < 0 && angle > -180 then it also works, but probably not the way user intended.
     * @param topThickness is the thickness of the top and bot side of parallelogram.
     * @param leftThickness is the thickness of the left and right side of parallelogram.
     * @return Returns the shape area between inner and outer parallelogram
     */
    public static Area createParallelogram(Polygon outerParallelogram, int angle, int topThickness, int leftThickness) {
        int x,y;
        double ratio, complementRatio;
        boolean isAcute = true;         // < 90
        if(angle > 90) {
            angle = 180 - angle;        // Complement angle
            isAcute = false;
        }
        ratio = angle / 90.0;
        complementRatio = 1 - ratio;
        Polygon innerParallelogram = new Polygon();

        int topSideLen = (int)(topThickness * (1 / ratio));


        x = outerParallelogram.xpoints[0] + leftThickness;
        x = addToXBasedOnAngleAndThickness(x, topSideLen, isAcute, complementRatio);
        y = outerParallelogram.ypoints[0] + topThickness;
        innerParallelogram.addPoint(x, y);

        x = outerParallelogram.xpoints[1] - leftThickness;
        x = addToXBasedOnAngleAndThickness(x, topSideLen, isAcute, complementRatio);
        y = outerParallelogram.ypoints[1] + topThickness;
        innerParallelogram.addPoint(x, y);

        // Now we pass !isAcute to the function not because the angle is not acute, but because at bottom the side is coming from different side then at the top
        // (For example for acute angle at the left bot, the side is going to right, but when looking from the left top point it is coming from left)
        x = outerParallelogram.xpoints[2] - leftThickness;
        x = addToXBasedOnAngleAndThickness(x, topSideLen, !isAcute, complementRatio);
        y = outerParallelogram.ypoints[2] - topThickness;
        innerParallelogram.addPoint(x, y);

        x = outerParallelogram.xpoints[3] + leftThickness;
        x = addToXBasedOnAngleAndThickness(x, topSideLen, !isAcute, complementRatio);
        y = outerParallelogram.ypoints[3] - topThickness;
        innerParallelogram.addPoint(x, y);


        Area area = new Area(outerParallelogram);
        area.subtract(new Area(innerParallelogram));
        return area;
    }


    private static int addToXBasedOnAngleAndThickness(int x, int sideLen, boolean isAcute, double ratio) {
        int outerXOnCertainHeight = (int)(ratio * sideLen);
        if(isAcute) {
            x -= outerXOnCertainHeight;
        }
        else {
            x += outerXOnCertainHeight;
        }

        return x;
    }

    public void createParallelogram(int topThickness, int leftThickness) {
        parallelogram = ParallelogramShapedPanel.createParallelogram(outerParallelogram, angle, topThickness, leftThickness);
    }




    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ShapedPanel.drawEdges(parallelogram, g, Color.black);
    }


    /**
     * Gets the y on line from sp to ep for given x
     * @param x
     * @param sp start point of line
     * @param ep end point of line
     * @return
     */
    public static int getPointOnLine(int x, Point sp, Point ep) {
        return getPointOnLine(x, sp.x, sp.y, ep.x , ep.y);
    }

    /**
     * Gets the y on line from sp to ep for given x.
     * sp stands for start point of line, ep for end point.
     * @param x
     * @param spx
     * @param spy
     * @param epy
     * @param epx
     * @return
     */
    public static int getPointOnLine(int x, int spx, int spy, int epx, int epy) {
        // https://stackoverflow.com/questions/9343105/point-on-line-by-distance-to-first-point
        // Just use parametric equation for line
        // I know x so I can calculate the parameter and from that calculate the y
        // x = p * x1 + (1-p) * x2;
        // y = p * y1 + (1-p) * y2;

        double p = (x - epx) / (double)(spx - epx);
        int y = (int)(p * spy + (1 - p) * epy);

        return y;
    }




    @Override
    public int getDistanceFromRectangleBorders(int x) {
        int[] xpoints = outerParallelogram.xpoints;
        int[] ypoints = outerParallelogram.ypoints;
        int y;

        int midX = this.getSize().width / 2;
        x += midX;
        boolean isOnLeftFromBotLine = x < xpoints[3];
        boolean isOnRightFromBotLine = x > xpoints[2];
        if(isOnLeftFromBotLine) {
            y = ParallelogramShapedPanel.getPointOnLine(x, xpoints[3], ypoints[3], xpoints[0], ypoints[0]);
        }
        else if(isOnRightFromBotLine) {
            y = ParallelogramShapedPanel.getPointOnLine(x, xpoints[1], ypoints[1], xpoints[2], ypoints[2]);
        }
        else {
            y = ypoints[2];
        }

        y -= this.getSize().height;
        return y;
    }





    // I will put the inputs just on the horizontal line
    @Override
    public void getLastPoint(Point p, int connectorIndex, int connectorCount) {
        int[] xpoints = outerParallelogram.xpoints;
        p.x = MovableJPanel.calculateXForHorizontalLineWithEdges(xpoints[0], xpoints[1], connectorIndex, connectorCount);
        p.y = 0;
        convertRelativePixelToAbsolute(p);
    }
}
