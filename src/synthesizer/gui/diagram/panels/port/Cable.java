package synthesizer.gui.diagram.panels.port;

import synthesizer.gui.diagram.ifaces.MaxElevationGetterIFace;
import synthesizer.gui.diagram.panels.ifaces.MovablePanelSpecificGetMethodsIFace;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

public class Cable {
    public Cable(MaxElevationGetterIFace maxElevation, MovablePanelSpecificGetMethodsIFace source,
                 InputPort targetPort) {
        MAX_ELEVATION = maxElevation;
        pathAroundTargetPanel = new Path2D.Double();
        absolutePath = new Path2D.Double();
        relativePath = new Path2D.Double();
        this.sourcePanel = source;
        this.targetPort = targetPort;
    }


    public enum CableType {
        STRAIGHT_LINE,
        AISLE_ALGORITHM,
        ADVANCED_ALGORITHM
    }


    private int pathAroundTargetPanelLen;

    public int getPathAroundTargetPanelLen() {
        return pathAroundTargetPanelLen;
    }

    private Path2D pathAroundTargetPanel;

    /**
     * Adds a point to the path by moving to the specified
     * coordinates specified in double precision.
     *
     * @param x the specified X coordinate
     * @param y the specified Y coordinate
     */
    private void pathAroundTargetPanelMoveTo(double x, double y) {
        pathAroundTargetPanel.moveTo(x, y);
        pathAroundTargetPanelLen++;
    }

    /**
     * Adds a point to the path by drawing a straight line from the
     * current coordinates to the new specified coordinates
     * specified in double precision.
     *
     * @param x the specified X coordinate
     * @param y the specified Y coordinate
     */
    private void pathAroundTargetPanelLineTo(double x, double y) {
        pathAroundTargetPanel.lineTo(x, y);
        pathAroundTargetPanelLen++;
    }

    public void resetPathAroundTargetPanel() {
        pathAroundTargetPanel.reset();
        pathAroundTargetPanelLen = 0;
    }

    public PathIterator getPathAroundTargetPanelIterator() {
        return pathAroundTargetPanel.getPathIterator(null);
    }

    private double sideConnectorLastYForCollision = -Integer.MIN_VALUE;

    public void setPathAroundTargetPanel() {
        resetPathAroundTargetPanel();

        MovablePanelSpecificGetMethodsIFace targetPanel = targetPort.getPanelWhichContainsPort();
        Point relativeLocEnd = targetPanel.getRelativePosToReferencePanel();

        pathAroundTargetPanelMoveTo(relativeLocEnd.x - 0.5, relativeLocEnd.y - 0.5);
        pathAroundTargetPanelLineTo(relativeLocEnd.x + 0.5, relativeLocEnd.y - 0.5);
        Point p = new Point();
        targetPanel.getNextToLastPoint(p, targetPort);
        if (p.y == -1) {
            sideConnectorLastYForCollision = relativeLocEnd.y;
            pathAroundTargetPanelMoveTo(relativeLocEnd.x - 0.5, relativeLocEnd.y - 0.5);
            pathAroundTargetPanelLineTo(relativeLocEnd.x - 0.5, sideConnectorLastYForCollision);
        }
        else if (p.y == 1) {
            sideConnectorLastYForCollision = relativeLocEnd.y;
            pathAroundTargetPanelMoveTo(relativeLocEnd.x + 0.5, relativeLocEnd.y - 0.5);
            pathAroundTargetPanelLineTo(relativeLocEnd.x + 0.5, sideConnectorLastYForCollision);
        }
        else {
            sideConnectorLastYForCollision = Integer.MIN_VALUE;
        }
    }

    public double getSideConnectorLastYForCollision() {
        return sideConnectorLastYForCollision;
    }


    private CableType cableType;

    public CableType getCableType() {
        return cableType;
    }

    public void setCableType(CableType val) {
        cableType = val;
    }

    private final MaxElevationGetterIFace MAX_ELEVATION;

    private MovablePanelSpecificGetMethodsIFace sourcePanel;

    public Point getSourcePanelRelativeLoc() {
        return sourcePanel.getRelativePosToReferencePanel();
    }

    private InputPort targetPort;

    public InputPort getTargetPort() {
        return targetPort;
    }

    public Point getTargetPanelRelativeLoc() {
        return targetPort.getPanelWhichContainsPort().getRelativePosToReferencePanel();
    }

    private Path2D absolutePath;

    public Path2D getAbsolutePath() {
        return absolutePath;
    }

    private Point lastPointBeforePort = new Point();

    public Point getLastPointBeforePort() {
        return lastPointBeforePort;
    }

    /**
     * Whole number is the middle of the panel. .5 is right in the middle between the end of the panel on left and start of the panel on right.
     */
    private Path2D relativePath;
    private int relativePathLen = 0;

    public int getRelativePathLen() {
        return relativePathLen;
    }

    /**
     * Adds a curved segment, defined by two new points, to the path by
     * drawing a Quadratic curve that intersects both the current
     * coordinates and the specified coordinates {@code (x2,y2)},
     * using the specified point {@code (x1,y1)} as a quadratic
     * parametric control point.
     * All coordinates are specified in double precision.
     *
     * @param x1 the X coordinate of the quadratic control point
     * @param y1 the Y coordinate of the quadratic control point
     * @param x2 the X coordinate of the final end point
     * @param y2 the Y coordinate of the final end point
     */
    public void relativePathQuadTo(double x1, double y1, double x2, double y2) {
        relativePath.quadTo(x1, y1, x2, y2);
        relativePathLen++;
    }

    /**
     * Adds a point to the path by moving to the specified
     * coordinates specified in double precision.
     *
     * @param x the specified X coordinate
     * @param y the specified Y coordinate
     */
    public void relativePathMoveTo(double x, double y) {
        relativePath.moveTo(x, y);
        relativePathLen++;
    }


    /**
     * Adds a point to the path by drawing a straight line from the
     * current coordinates to the new specified coordinates
     * specified in double precision.
     *
     * @param x the specified X coordinate
     * @param y the specified Y coordinate
     */
    public void relativePathLineTo(double x, double y) {
        relativePath.lineTo(x, y);
        relativePathLen++;
    }


    public PathIterator getRelativePathIterator() {
        return relativePath.getPathIterator(null);
    }


    private final static double[] tmpArr = new double[4];

    // I have to check if it is integer or not and based on that transform relative coordinates to absolute
    // At first the code was quite clear, but I probably made some implementation mistake and the code got much for worse
    // from there, but since it is working I won't be rewriting it
    public void setAbsolutePathBasedOnRelativePath(Point referencePanelLoc, Dimension panelSize, int borderWidth, int borderHeight,
                                                   int panelSizeWithBorderWidth, int panelSizeWithBorderHeight,
                                                   int pixelsPerElevation) {
        boolean endingCondition;
        Point tmpPoint = new Point();
        targetPort.getNextToLastPoint(tmpPoint);
        boolean isConnectorOnSides = tmpPoint.y == -1 || tmpPoint.y == 1;
        double[] line = new double[4];
        int totalElevation = pixelsPerElevation * elevation;


        int midBotReferencePanelX = referencePanelLoc.x + panelSize.width / 2;
        int referencePanelEndX = referencePanelLoc.x + panelSize.width;
        int referencePanelEndY = referencePanelLoc.y + panelSize.height;
        double x;
        double y;
        absolutePath.reset();

        double oldX = 0;
        boolean wasLastQuad = false;
        int index = 0;

        for (PathIterator iterator = relativePath.getPathIterator(null); !iterator.isDone(); index++) {
            if (index < 2) {
                x = midBotReferencePanelX;
            }
            else {
                x = referencePanelEndX;
            }
            y = referencePanelEndY;

            int type = iterator.currentSegment(line);
            iterator.next();
            if (index == 0) {
                oldX = line[0];
                x += (panelSizeWithBorderWidth * line[0]);
                y += (panelSizeWithBorderHeight * line[1]);

                // ELEVATING START
                x -= totalElevation;

                y += sourcePanel.getDistanceFromRectangleBorders(totalElevation);
                absolutePath.moveTo(x, y);

                if (cableType == CableType.STRAIGHT_LINE) {
                    targetPort.getNextToLastPoint(tmpPoint);
                    // If it is connector from top then I will solve it now, otherwise it will be solved in the code as other cable types
                    if (tmpPoint.y == 0) {
                        // When going through panel ... we go through the mid of panel .. these 2 lines of code are copy pasted from later
                        Point panelLoc = targetPort.getPanelWhichContainsPort().getLocation();
                        y = panelLoc.y + totalElevation - borderHeight / 2;
                        // x is the same
                        absolutePath.lineTo(x, y);
                        setLastTwoPoints(x, y, tmpPoint);
                        return;
                    }
                }
            }
            else {
                if (type == PathIterator.SEG_QUADTO) {
                    double arcX = referencePanelEndX;
                    double arcY = referencePanelEndY;

                    boolean isArcToLeft;
                    if (line[2] < oldX) {
                        isArcToLeft = true;
                    }
                    else {
                        isArcToLeft = false;
                    }


                    arcX += getDistanceFromReferencePanel(panelSizeWithBorderWidth, borderWidth, line[0]);

                    arcY += getDistanceFromReferencePanel(panelSizeWithBorderHeight, borderHeight, line[1]);
                    arcY += totalElevation;
                    if ((line[1] == Math.floor(line[1]))) {
                        arcY -= borderHeight / 2;
                    }

                    x += getDistanceFromReferencePanel(panelSizeWithBorderWidth, borderWidth, line[2]);
                    y += getDistanceFromReferencePanel(panelSizeWithBorderHeight, borderHeight, line[3]);
                    if ((line[2] == Math.floor(line[2]))) {
                        if (!isArcToLeft) {
                            x -= panelSize.width;
                        }
                    }


                    if (line[0] == Math.floor(line[0])) {
                        if (isArcToLeft) {
                            x += panelSize.width / 2;
                        }
                        else {
                            x -= panelSize.width / 2;
                        }
                    }
                    else {
                        if (isArcToLeft) {
                            x -= panelSize.width / 2 - borderWidth;
                        }
                        arcX += borderWidth;
                    }

                    if (line[1] == Math.floor(line[1])) {
                        // EMPTY
                    }
                    else {
                        y -= panelSize.height / 2;
                        arcY += borderHeight / 2;
                    }

                    arcX -= borderWidth;

                    y += totalElevation;
                    absolutePath.quadTo(arcX, arcY, x, y);

                    oldX = line[2];
                    wasLastQuad = true;
                }
                else if (type == PathIterator.SEG_LINETO) {
                    endingCondition =
                            (isConnectorOnSides && (
                                    (index >= relativePathLen - 2 && (cableType == CableType.ADVANCED_ALGORITHM) ||
                                     cableType == CableType.STRAIGHT_LINE) ||
                                    (index >= relativePathLen - 1 && cableType == CableType.AISLE_ALGORITHM)
                            )) ||
                            (!isConnectorOnSides && (
                                    (index >= relativePathLen - 3 && cableType == CableType.ADVANCED_ALGORITHM) ||
                                    (index >= relativePathLen - 2 && cableType == CableType.AISLE_ALGORITHM)
                            ));

                    if (cableType == CableType.ADVANCED_ALGORITHM &&
                        iterator.currentSegment(tmpArr) != PathIterator.SEG_QUADTO &&
                        index != relativePathLen - 2 && !isConnectorOnSides) {
                        x = midBotReferencePanelX;
                    }

                    // When going through panel ... we go through the mid of panel
                    if (line[0] == Math.floor(line[0])) {
                        x += line[0] * panelSizeWithBorderWidth;
                    }
                    else {          // When going through isle
                        x += getDistanceFromReferencePanel(panelSizeWithBorderWidth, borderWidth, line[0]);
                    }

                    // When going through panel ... we go through the mid of panel
                    if (line[1] == Math.floor(line[1])) {
                        y += line[1] * panelSizeWithBorderHeight;
                        y -= panelSize.height / 2;
                    }
                    else {          // When going through isle
                        y += getDistanceFromReferencePanel(panelSizeWithBorderHeight, borderHeight, line[1]);
                    }
                    y += totalElevation;

                    // Load the one line twice, it isn't ideal, but doesn't break the program flow
                    double lineX = line[0];
                    if (iterator.currentSegment(line) == PathIterator.SEG_QUADTO) {      // If the next segment is arc
                        boolean isArcToLeft = line[2] < line[0];
                        if (wasLastQuad && (isArcToLeft || (!isArcToLeft && lineX < oldX))) {
                            x = getNewXAfterFixForLastQuad(isArcToLeft, lineX, oldX, line[0], x, borderWidth, panelSize);
                        }
                        else {
                            if (line[0] == Math.floor(line[0])) {
                                if (isArcToLeft) {            // Is line to left
                                    x -= panelSize.width / 2;
                                }
                                else {
                                    x += borderWidth / 2 + panelSize.width / 4;
                                }
                            }
                            else {
                                if (isArcToLeft) {            // Is line to left
                                    if (wasLastQuad && oldX - line[0] < 1) {
                                        x -= panelSizeWithBorderWidth;
                                    }
                                    else {
                                        x -= panelSize.width;
                                    }
                                }
                                else {
                                    // EMPTY
                                }
                            }
                        }
                    }
                    else {
                        x -= totalElevation;
                    }
                    if (endingCondition) {                     // Just draw the last lines and exit
                        setLastFewPoints(x, y, tmpPoint, totalElevation, borderWidth, borderHeight);
                        return;
                    }
                    else {
                        absolutePath.lineTo(x, y);
                        oldX = line[0];
                        wasLastQuad = false;
                    }
                }
            }
        }
    }


    private void setLastFewPoints(double x, double y, Point tmpPoint, int totalElevation, int borderWidth, int borderHeight) {
        Point panelLoc = targetPort.getPanelWhichContainsPort().getLocation();

        targetPort.getNextToLastPoint(tmpPoint);
        if (tmpPoint.y == 0) {
            double newY = panelLoc.y + totalElevation - borderHeight / 2;
            if (y != newY) {
                absolutePath.lineTo(x, y);
                y = newY;
                absolutePath.lineTo(x, y);
            }

            x = tmpPoint.x;
        }
        else if (tmpPoint.y == -1) {
            double newX = panelLoc.x - totalElevation - borderWidth / 2;
            if (x != newX) {
                absolutePath.lineTo(x, y);
                x = newX;
                absolutePath.lineTo(x, y);
            }
            y = tmpPoint.x;
        }
        else {
            int targetPanelWidth = targetPort.getPanelWhichContainsPort().getSize().width;
            double newX = panelLoc.x - totalElevation + targetPanelWidth + borderWidth / 2;
            if (x != newX) {
                absolutePath.lineTo(x, y);
                x = newX;
                absolutePath.lineTo(x, y);
            }

            y = tmpPoint.x;
        }
        lastPointBeforePort.x = (int) x;
        lastPointBeforePort.y = (int) y;
        absolutePath.lineTo(x, y);

        targetPort.getLastPoint(tmpPoint);
        absolutePath.lineTo(tmpPoint.x, tmpPoint.y);
    }


    private void setLastTwoPoints(double x, double y, Point tmpPoint) {
        targetPort.getNextToLastPoint(tmpPoint);
        if (tmpPoint.y == 0) {
            x = tmpPoint.x;
        }
        else {
            y = tmpPoint.x;
        }
        lastPointBeforePort.x = (int) x;
        lastPointBeforePort.y = (int) y;
        absolutePath.lineTo(x, y);

        targetPort.getLastPoint(tmpPoint);
        absolutePath.lineTo(tmpPoint.x, tmpPoint.y);
    }


    private double getNewXAfterFixForLastQuad(boolean isArcToLeft, double lineX, double oldX, double arcX, double x, int borderWidth, Dimension panelSize) {
        if (isArcToLeft) {           // going left - end of arc is more on left than the arc
            if (lineX > oldX) {     // But the line is going to right
                if (arcX == Math.floor(arcX)) {
                    x -= borderWidth / 2 + panelSize.width / 4;
                }
                else {
                    x -= panelSize.width;
                }
            }
            else {
                if (arcX == Math.floor(arcX)) {
                    x -= borderWidth / 2 + panelSize.width / 4;
                }
                else {
                    x -= panelSize.width;
                }
            }
        }
        else {
            if (lineX < oldX) {
                if (arcX == Math.floor(arcX)) {
                    x += borderWidth / 2 + panelSize.width / 4;
                }
            }
        }

        return x;
    }


    private int getDistanceFromReferencePanel(int panelSizeWithBorder, int borderSize, double doubleVal) {
        int floorVal = (int) Math.floor(doubleVal);
        int dist = panelSizeWithBorder * floorVal;
        if ((int) doubleVal != doubleVal) {
            int modulo;
            if ((int) doubleVal == 0) {
                modulo = 1;
            }
            else {
                modulo = floorVal;
                if (modulo < 0 && modulo != 1) {
                    modulo++;
                }
            }
            double valToAdd = borderSize * (doubleVal % modulo);
            dist += Math.abs(valToAdd);
        }

        return dist;
    }


    private int elevation = 0;

    public int getElevation() {
        return elevation;
    }

    public void resetElevation() {
        isElevationSet = false;
        elevation = 0;
    }


    public void setElevationBasedOnMaxElevation(int currMaxElevation) {         // Goes like 0->1->-1->2->-2 ... etc.
        if (isBiggerThanCurrentlySetWithNext(currMaxElevation) || !isElevationSet) {
            if (currMaxElevation == 0) {
                if (MAX_ELEVATION.getMaxElevation() != 0)
                    elevation = 1;
                else
                    elevation = 0;
            }
            else if (currMaxElevation > 0) {
                elevation = -currMaxElevation;
            }
            else {
                elevation = currMaxElevation - 1;
                elevation = -elevation;
                if (elevation > MAX_ELEVATION.getMaxElevation()) {
                    elevation = 0;              // If too many cables, then just give up
                }
            }

            isElevationSet = true;
        }
    }

    /**
     * Works with setElevationBasedOnMaxElevation so it checks if the value after the val is bigger
     *
     * @param val
     * @return
     */
    public boolean isBiggerThanCurrentlySetWithNext(int val) {
        int absVal = Math.abs(val);
        int absElevation = Math.abs(elevation);
        return absVal >= absElevation;
    }

    public boolean isBiggerThanCurrentlySetWithGiven(int val) {
        int absVal = Math.abs(val);
        int absElevation = Math.abs(elevation);
        return absVal > absElevation || (absVal == absElevation && elevation > 0 && absVal < 0);
    }

    public void setElevationToGivenValue(int value) {
        elevation = value;
        isElevationSet = true;
    }

    private boolean isElevationSet;

    public boolean getIsElevationSet() {
        return isElevationSet;
    }


    public void resetPaths() {
        absolutePath.reset();
        relativePath.reset();
        relativePathLen = 0;
    }


    ////////////////////// Board change
    // Using transformations is easiest to program, but may create a lot of garbage
    public void moveX(int xMovement) {
        AffineTransform at = new AffineTransform();
        at.translate(xMovement, 0);
        absolutePath = new Path2D.Double(absolutePath, at);
        lastPointBeforePort.x += xMovement;
    }

    public void moveY(int yMovement) {
        AffineTransform at = new AffineTransform();
        at.translate(0, yMovement);
        absolutePath = new Path2D.Double(absolutePath, at);
        lastPointBeforePort.y += yMovement;
    }

    public void move(int xMovement, int yMovement) {
        AffineTransform at = new AffineTransform();
        at.translate(xMovement, yMovement);
        absolutePath = new Path2D.Double(absolutePath, at);
        lastPointBeforePort.x += xMovement;
        lastPointBeforePort.y += yMovement;
    }
}