package synthesizer.gui.diagram.ShapedPanels;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.MovableJPanel;
import synthesizer.gui.diagram.ShapedPanels.internals.ShapedPanelInternals;
import synthesizer.gui.PanelAroundMovablePanelsPackage.UnitCommunicationWithGUI;
import Rocnikovy_Projekt.Direction;

import java.awt.*;
import java.awt.geom.Area;

// Contains the easiest way to calculate the area
public class TrapeziumShapedPanel extends ShapedPanel {
    public static final int THICKNESS_DIV_FACTOR = 16;

    public TrapeziumShapedPanel(DiagramPanel diagramPanel, ShapedPanelInternals internals,
                                UnitCommunicationWithGUI unit) {
        super(diagramPanel, internals, unit);
        constructor();
    }

    public TrapeziumShapedPanel(int relativeX, int relativeY, int w, int h,
                                DiagramPanel diagramPanel, ShapedPanelInternals internals,
                                UnitCommunicationWithGUI unit) {
        super(relativeX, relativeY, w, h, diagramPanel, internals, unit);
        constructor();
    }

    public TrapeziumShapedPanel(int relativeX, int relativeY, DiagramPanel diagramPanel,
                                ShapedPanelInternals internals, UnitCommunicationWithGUI unit) {
        super(relativeX, relativeY, diagramPanel, internals, unit);
        constructor();
    }


    private void constructor() {
        outerTrapezium = new Polygon();
        panelShape = outerTrapezium;
    }

    /**
     * It just view on the panelShape, looking at it as polygon.
     */
    private Polygon outerTrapezium;


    private Area trapeziumEdge = new Area();
    private void setTrapeziumEdge(Dimension newSize) {
        int thickness = ShapedPanel.calculateThickness(THICKNESS_DIV_FACTOR, newSize);
        trapeziumEdge = TrapeziumShapedPanel.getTrapeziumEdge(outerTrapezium, newSize, thickness);
    }


    public static Area getTrapeziumEdge(Polygon outerTrapezium, Dimension size, int thickness) {
        Polygon innerTrapezium = new Polygon();
        Dimension sizeCopied = new Dimension(size);
        sizeCopied.width -= 2 * thickness;
        sizeCopied.height -= 2 * thickness;
        setTrapezium(innerTrapezium, sizeCopied);
        for (int i = 0; i < outerTrapezium.npoints; i++) {
            innerTrapezium.xpoints[i] += thickness;
            innerTrapezium.ypoints[i] += 3 / 4.0 * thickness;
        }


        Area area = new Area(outerTrapezium);
        area.subtract(new Area(innerTrapezium));
        return area;
    }


    @Override
    public void reshape(Dimension newSize) {
        setTrapezium(outerTrapezium, newSize);
        setTrapeziumEdge(newSize);
        reshapeInternals(newSize);
    }

    public static void setTrapezium(Polygon trapezium, Dimension newSize) {
        trapezium.reset();

        int botY = newSize.height;
// TODO: PARAMETERS TO PLAY WITH
        trapezium.addPoint(newSize.width / 4, 0);
        trapezium.addPoint(3 * newSize.width / 4, 0);
        trapezium.addPoint(newSize.width, botY);
        trapezium.addPoint(0, botY);
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ShapedPanel.drawEdges(trapeziumEdge, g, Color.black);
    }


    @Override
    public void getLastPoint(Point p, int connectorIndex, int connectorCount) {
        int[] xpoints = outerTrapezium.xpoints;
        int[] ypoints = outerTrapezium.ypoints;
        int leftLineConnectorsCount = connectorCount / 3;
        int midLineConnectorsCount = leftLineConnectorsCount + connectorCount % 3;
        int rightLineStartIndex = leftLineConnectorsCount + midLineConnectorsCount;
        if(connectorIndex < leftLineConnectorsCount) {                                      // left line
            p.x = MovableJPanel.calculateXForHorizontalLineWithEdges(xpoints[3], xpoints[0], connectorIndex, leftLineConnectorsCount);
            p.y = ParallelogramShapedPanel.getPointOnLine(p.x, xpoints[3], ypoints[3], xpoints[0], ypoints[0]);
        }
        else if(connectorIndex < rightLineStartIndex) {        // mid line
            connectorIndex -= leftLineConnectorsCount;
            p.x = MovableJPanel.calculateXForHorizontalLineWithEdges(xpoints[0], xpoints[1], connectorIndex, midLineConnectorsCount);
            p.y = ypoints[0];
        }
        else {                                                                              // right line
            connectorIndex -= rightLineStartIndex;
            p.x = MovableJPanel.calculateXForHorizontalLineWithEdges(xpoints[1], xpoints[2], connectorIndex, leftLineConnectorsCount);
            p.y = ParallelogramShapedPanel.getPointOnLine(p.x, xpoints[1], ypoints[1], xpoints[2], ypoints[2]);
        }

        convertRelativePixelToAbsolute(p);
    }


    @Override
    public void getNextToLastPoint(Point nextToLastPoint, int connectorIndex, int connectorCount) {
        getLastPoint(nextToLastPoint, connectorIndex, connectorCount);

        int leftLineConnectorsCount = connectorCount / 3;
        int midLineConnectorsCount = leftLineConnectorsCount + connectorCount % 3;
        int rightLineStartIndex = leftLineConnectorsCount + midLineConnectorsCount;
        if(connectorIndex < leftLineConnectorsCount) {    // left line
            nextToLastPoint.x = nextToLastPoint.y;
            nextToLastPoint.y = -1;
        }
        else if(connectorIndex < rightLineStartIndex) {   // mid line
            nextToLastPoint.y = 0;
        }
        else {                                            // right line
            nextToLastPoint.x = nextToLastPoint.y;
            nextToLastPoint.y = 1;
        }
    }

    @Override
    public Direction getDirectionForInputPortLabel(int connectorIndex, int connectorCount) {
        if(connectorIndex < connectorCount / 2) {         // left half
            return Direction.LEFT;
        }
        else {                                            // right half
            return Direction.RIGHT;
        }
// TODO: RML
//        int leftLineConnectorsCount = connectorCount / 3;
//        int midLineConnectorsCount = leftLineConnectorsCount + connectorCount % 3;
//        int rightLineStartIndex = leftLineConnectorsCount + midLineConnectorsCount;
//        if(connectorIndex < leftLineConnectorsCount) {    // left line
//            return Direction.LEFT;
//        }
//        else if(connectorIndex < rightLineStartIndex) {   // mid line
//            return Direction.UP;
//        }
//        else {                                            // right line
//            return Direction.RIGHT;
//        }
// TODO: RML
    }
}
