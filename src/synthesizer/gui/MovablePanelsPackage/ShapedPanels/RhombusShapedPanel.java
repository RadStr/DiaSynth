package synthesizer.gui.MovablePanelsPackage.ShapedPanels;

import synthesizer.gui.MovablePanelsPackage.DiagramPanel;
import synthesizer.gui.MovablePanelsPackage.MovableJPanel;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.Internals.ShapedPanelInternals;
import synthesizer.gui.PanelAroundMovablePanelsPackage.UnitCommunicationWithGUI;
import Rocnikovy_Projekt.Direction;
import Rocnikovy_Projekt.MyLogger;

import java.awt.*;
import java.awt.geom.Area;

public class RhombusShapedPanel extends ShapedPanel {
    private static final int TOP_THICKNESS_DIV_FACTOR = 16;
    private static final int LEFT_THICKNESS_DIV_FACTOR = 16;


    public RhombusShapedPanel(DiagramPanel diagramPanel, ShapedPanelInternals internals,
                              UnitCommunicationWithGUI unit) {
        super(diagramPanel, internals, unit);
        constructor();
    }

    public RhombusShapedPanel(int relativeX, int relativeY, int w, int h,
                              DiagramPanel diagramPanel, ShapedPanelInternals internals,
                              UnitCommunicationWithGUI unit) {
        super(relativeX, relativeY, w, h, diagramPanel, internals, unit);
        constructor();
    }

    public RhombusShapedPanel(int relativeX, int relativeY, DiagramPanel diagramPanel,
                              ShapedPanelInternals internals, UnitCommunicationWithGUI unit) {
        super(relativeX, relativeY, diagramPanel, internals, unit);
        constructor();
    }


    private void constructor() {
        rhombus = new Area();
        outerRhombus = new Polygon();
        panelShape = outerRhombus;
    }


    @Override
    public void reshape(Dimension newSize) {
        outerRhombus.reset();
        // Points are added clock-wise starting on top
        outerRhombus.addPoint(newSize.width / 2, 0);
        outerRhombus.addPoint(newSize.width, newSize.height / 2);
        outerRhombus.addPoint(newSize.width / 2, newSize.height);
        outerRhombus.addPoint(0, newSize.height / 2);

        int leftThickness = newSize.width / LEFT_THICKNESS_DIV_FACTOR;
        int topThickness = newSize.height / TOP_THICKNESS_DIV_FACTOR;
        createRhombus(topThickness, leftThickness);

        reshapeInternals(newSize);
    }

    /**
     * It just view on the panelShape, looking at it as polygon.
     */
    protected Polygon outerRhombus;
    private Area rhombus;


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ShapedPanel.drawEdges(rhombus, g, Color.black);
    }



    /**
     * Creates area of outerParallelogram and thicknesses.
     * @param outerRhombus is the outer rhombus
     * @param topThickness is the thickness of the top and bot side of rhombus.
     * @param leftThickness is the thickness of the left and right side of rhombus.
     * @return Returns the shape area between inner and outer rhombus
     */
    public static Area createRhombus(Polygon outerRhombus, int topThickness, int leftThickness) {
        int x,y;
        Polygon innerRhombus = new Polygon();

        x = outerRhombus.xpoints[0];
        y = outerRhombus.ypoints[0] + topThickness;
        innerRhombus.addPoint(x, y);

        x = outerRhombus.xpoints[1] - leftThickness;
        y = outerRhombus.ypoints[1];
        innerRhombus.addPoint(x, y);

        x = outerRhombus.xpoints[2];
        y = outerRhombus.ypoints[2] - topThickness;
        innerRhombus.addPoint(x, y);

        x = outerRhombus.xpoints[3] + leftThickness;
        y = outerRhombus.ypoints[3];
        innerRhombus.addPoint(x, y);


        Area area = new Area(outerRhombus);
        area.subtract(new Area(innerRhombus));
        return area;
    }

    public void createRhombus(int topThickness, int leftThickness) {
        rhombus = createRhombus(outerRhombus, topThickness, leftThickness);
    }




    @Override
    public int getDistanceFromRectangleBorders(int x) {
        int[] xpoints = outerRhombus.xpoints;
        int[] ypoints = outerRhombus.ypoints;
        int y;

        boolean isOnRight = x > 0;
        int midX = this.getSize().width / 2;
        x += midX;
        if(isOnRight) {
            y = ParallelogramShapedPanel.getPointOnLine(x, xpoints[1], ypoints[1], xpoints[2], ypoints[2]);
        }
        else {
            y = ParallelogramShapedPanel.getPointOnLine(x, xpoints[2], ypoints[2], xpoints[3], ypoints[3]);
        }


        y -= this.getSize().height;
        return y;
    }


    @Override
    public void getLastPoint(Point p, int connectorIndex, int connectorCount) {
        int[] xpoints = outerRhombus.xpoints;
        int[] ypoints = outerRhombus.ypoints;

        if(connectorIndex == 0) {
            p.x = xpoints[0];
            p.y = ypoints[0];
        }
        else if(connectorIndex == 1) {      // Left
            p.x = xpoints[3];
            p.y = ypoints[3];
        }
        else if(connectorIndex == 2) {      // Right
            p.x = xpoints[1];
            p.y = ypoints[1];
        }
        else {
            connectorCount -= 3;
            int connectorIndexOnLine = calculateIndexOnLine(connectorIndex);
            if(connectorIndex % 2 == 1) {       // Put it on left line
                // The modulo is here because if there are more inputs on left (% 2 == 1)
                // then there is 1 more connector than on the right
                connectorCount = (connectorCount / 2) + (connectorCount % 2);
                p.x = MovableJPanel.calculateXForHorizontalLineWithEdges(xpoints[3], xpoints[0], connectorIndexOnLine, connectorCount);
                p.y = ParallelogramShapedPanel.getPointOnLine(p.x, xpoints[3], ypoints[3], xpoints[0], ypoints[0]);
            }
            else {                              // Put it on right line
                connectorCount /= 2;
                p.x = MovableJPanel.calculateXForHorizontalLineWithEdges(xpoints[0], xpoints[1], connectorIndexOnLine, connectorCount);
                p.y = ParallelogramShapedPanel.getPointOnLine(p.x, xpoints[0], ypoints[0], xpoints[1], ypoints[1]);
            }
        }

        convertRelativePixelToAbsolute(p);
    }

    private static int getConnectorCountOnSide(int connectorIndex, int connectorCount) {
        connectorCount -= 3;
        if(connectorIndex % 2 == 1) {       // Put it on left line
            // The modulo is here because if there are more inputs on left (% 2 == 1)
            // then there is 1 more connector than on the right
            connectorCount = (connectorCount / 2) + (connectorCount % 2);
        }
        else {                              // Put it on right line
            connectorCount /= 2;
        }

        return connectorCount;
    }

    private static int calculateIndexOnLine(int connectorIndex) {
        return (connectorIndex - 3) / 2;
    }



    @Override
    public Direction getDirectionForInputPortLabel(int connectorIndex, int connectorCount) {
        if(connectorIndex == 0) {
            return Direction.UP;
        }
        else if(connectorIndex % 2 == 1) {
            return Direction.LEFT;
        }
        else {
            return Direction.RIGHT;
        }
    }


    @Override
    public void getNextToLastPoint(Point nextToLastPoint, int connectorIndex, int connectorCount) {
        getLastPoint(nextToLastPoint, connectorIndex, connectorCount);
        if(connectorIndex == 0) {
            nextToLastPoint.y = 0;
        }
        else if(connectorIndex == 1) {      // Left
            nextToLastPoint.x = nextToLastPoint.y;
            nextToLastPoint.y = -1;
        }
        else if(connectorIndex == 2) {      // Right
            nextToLastPoint.x = nextToLastPoint.y;
            nextToLastPoint.y = 1;
        }
        else {
            int indexOnLine = calculateIndexOnLine(connectorIndex);
            int connectorCountOnSide = getConnectorCountOnSide(connectorIndex, connectorCount);
            int half = connectorCountOnSide / 2;

            Direction direction = getDirectionForInputPortLabel(connectorIndex, connectorCount);
            boolean isInFirstHalf = (indexOnLine > half && direction == Direction.LEFT) ||
                                    (indexOnLine < half && direction == Direction.RIGHT);
            if(isInFirstHalf) {
                nextToLastPoint.y = 0;
            }
            else {
                int h = this.getHeight() / 2;

                // in the /* */  is just experiment with having "diagonal lines".
                // It is possible but I have to take into consideration the elevations,
                // and also sometimes the line should go from the top and when from left, etc. I won't be doing that
                // else the result isn't just really that good and also there some problems with labels because of that
                // (The cables overshadow the labels)
                switch(direction) {
                    case LEFT:
                        nextToLastPoint.x = nextToLastPoint.y/* - (h / connectorCountOnSide)*/;
                        nextToLastPoint.y = -1;
                        break;
                    case RIGHT:
                        nextToLastPoint.x = nextToLastPoint.y/* - (h / connectorCountOnSide)*/;
                        nextToLastPoint.y = 1;
                        break;
                    default:
                        MyLogger.logWithoutIndentation("Invalid direction inside rhombus next to last method:\t" + direction);
                        break;
                }
            }
        }
    }

// When I want everything except the side ones go vertically
//    @Override
//    public void getNextToLastPoint(Point nextToLastPoint, int connectorIndex, int connectorCount) {
//        getLastPoint(nextToLastPoint, connectorIndex, connectorCount);
//        if(connectorIndex == 0 || connectorIndex > 2) {
//            nextToLastPoint.y = 0;
//        }
//        else if(connectorIndex == 1) {      // Left
//            nextToLastPoint.x = nextToLastPoint.y;
//            nextToLastPoint.y = -1;
//        }
//        else if(connectorIndex == 2) {      // Right
//            nextToLastPoint.x = nextToLastPoint.y;
//            nextToLastPoint.y = 1;
//        }
//    }
}
