package synthesizer.GUI.MovablePanelsPackage;

import synthesizer.GUI.MovablePanelsPackage.Ports.InputPort;
import Rocnikovy_Projekt.Direction;

import java.awt.*;

public interface ShapeSpecificGetMethodsIFace {
    int getDistanceFromRectangleBorders(int x);


    /**
     * Doesn't need to be overridden. It is there just for convenience
     * @param connectorIndex
     * @param connectorCount
     * @return
     */
    default Point getLastPoint(int connectorIndex, int connectorCount) {
        Point p = new Point();
        getLastPoint(p, connectorIndex, connectorCount);
        return p;
    }

    void getLastPoint(Point lastPoint, int connectorIndex, int connectorCount);

    // These 2 methods are overridden in MovablePanel and doesn't need to be overridden anymore.
    /**
     * This method should be overridden only once. It is same as getLastPoint(int connectorIndex, int connectorCount),
     * but the overriding already knows the connector count.
     * @param connectorIndex
     * @return
     */
    Point getLastPoint(int connectorIndex);

    /**
     * This method should be overridden only once. It is same as getLastPoint(Point lastPoint, int connectorIndex, int connectorCount),
     *      * but the overriding already knows the connector count.
     * @param lastPoint
     * @param connectorIndex
     */
    void getLastPoint(Point lastPoint, int connectorIndex);


    /**
     * Overridden once in movable Panel, no need to overwrite anymore.
     * @return
     */
    void getNextToLastPoint(Point nextToLastPoint, int connectorIndex);

    /**
     * Overridden once in movable Panel, no need to overwrite anymore.
     * @return
     */
    void getNextToLastPoint(Point nextToLastPoint, InputPort inputPort);

    /**
     * Returns the next to last point. In the.x is the relative location (at the rectangle borders of movable panels)
     * And the .y which isle it is. y == -1 it is on left, and the line to last point is horizontal.
     * y == 0 it is on top and the line is vertical. y == 1 it is on the right and the line is horizontal.
     * @param nextToLastPoint
     * @param connectorIndex
     * @param connectorCount
     */
    void getNextToLastPoint(Point nextToLastPoint, int connectorIndex, int connectorCount);



    /**
     * Used to find the correct position for the input port labels.
     * @param connectorIndex
     * @param connectorCount
     * @return
     */
    Direction getDirectionForInputPortLabel(int connectorIndex, int connectorCount);

    /**
     * Overridden once in movable Panel, no need to overwrite anymore.
     * @return
     */
    Direction getDirectionForInputPortLabel(InputPort ip);


    /**
     * Again overridden in movable panel. No need to override anymore.
     * @param ip
     * @return
     */
    Dimension calculateAvailableLabelSize(InputPort ip);

    /**
     *
     * @param availableSize will contain the result
     * @param connectorIndex
     * @param connectorCount
     */
    void calculateAvailableLabelSize(Dimension availableSize, int connectorIndex, int connectorCount);
}
