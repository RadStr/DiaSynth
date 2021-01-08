package str.rad.synthesizer.gui.diagram.panels.ifaces;

import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.gui.diagram.panels.util.Direction;

import java.awt.*;

public interface ShapeSpecificGetMethodsIFace {
    int getDistanceFromRectangleBorders(int x);


    /**
     * Doesn't need to be overridden. It is there just for convenience.
     *
     * @param connectorIndex
     * @param connectorCount
     * @return Returns the absolute coordinates last point of connection
     */
    default Point getLastPoint(int connectorIndex, int connectorCount) {
        Point p = new Point();
        getLastPoint(p, connectorIndex, connectorCount);
        return p;
    }

    /**
     * Gets the absolute coordinates last point of connection
     * @param lastPoint
     * @param connectorIndex
     * @param connectorCount
     */
    void getLastPoint(Point lastPoint, int connectorIndex, int connectorCount);

    // These 2 methods are overridden in MovablePanel and there is no need to be overridden anymore.

    /**
     * This method should be overridden only once. It is same as getLastPoint(int connectorIndex, int connectorCount),
     * but the overriding class already knows the connector count.
     *
     * @param connectorIndex
     * @return Returns the absolute coordinates last point of connection
     */
    Point getLastPoint(int connectorIndex);

    /**
     * This method should be overridden only once. It is same as getLastPoint(Point lastPoint, int connectorIndex, int connectorCount),
     * but the overriding class already knows the connector count. Gets the absolute coordinates last point of connection
     *
     * @param lastPoint
     * @param connectorIndex
     */
    void getLastPoint(Point lastPoint, int connectorIndex);


    /**
     * Overridden once in movable Panel, no need to overwrite anymore.
     *
     * @return
     */
    void getNextToLastPoint(Point nextToLastPoint, int connectorIndex);

    /**
     * Overridden once in movable Panel, no need to overwrite anymore.
     *
     * @return
     */
    void getNextToLastPoint(Point nextToLastPoint, InputPort inputPort);

    /**
     * Returns the next to last point. In the.x is the relative location (at the rectangle borders of movable panels)
     * And the .y which isle it is. y == -1 it is on left, and the line to last point is horizontal.
     * y == 0 it is on top and the line is vertical. y == 1 it is on the right and the line is horizontal.
     *
     * @param nextToLastPoint
     * @param connectorIndex
     * @param connectorCount
     */
    void getNextToLastPoint(Point nextToLastPoint, int connectorIndex, int connectorCount);


    /**
     * Used to find the correct position for the input port labels.
     *
     * @param connectorIndex
     * @param connectorCount
     * @return
     */
    Direction getDirectionForInputPortLabel(int connectorIndex, int connectorCount);

    /**
     * Overridden once in movable Panel, no need to overwrite anymore.
     *
     * @return
     */
    Direction getDirectionForInputPortLabel(InputPort ip);


    /**
     * Again overridden in movable panel. No need to override anymore.
     *
     * @param ip
     * @return
     */
    Dimension calculateAvailableLabelSize(InputPort ip);

    /**
     * @param availableSize  will contain the result
     * @param connectorIndex
     * @param connectorCount
     */
    void calculateAvailableLabelSize(Dimension availableSize, int connectorIndex, int connectorCount);
}
