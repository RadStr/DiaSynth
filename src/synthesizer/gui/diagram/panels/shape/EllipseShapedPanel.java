package synthesizer.gui.diagram.panels.shape;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.MovableJPanel;
import synthesizer.gui.diagram.panels.shape.internals.ShapedPanelInternals;
import synthesizer.UnitViewForGUIIFace;
import synthesizer.gui.diagram.panels.util.Direction;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class EllipseShapedPanel extends ShapedPanel {
    private static final int THICKNESS_DIV_FACTOR = 16;

    public EllipseShapedPanel(DiagramPanel diagramPanel, ShapedPanelInternals internals,
                              UnitViewForGUIIFace unit) {
        super(diagramPanel, internals, unit);
        constructor();
    }

    public EllipseShapedPanel(int relativeX, int relativeY, int w, int h,
                              DiagramPanel diagramPanel, ShapedPanelInternals internals,
                              UnitViewForGUIIFace unit) {
        super(relativeX, relativeY, w, h, diagramPanel, internals, unit);
        constructor();
    }

    public EllipseShapedPanel(int relativeX, int relativeY, DiagramPanel diagramPanel,
                              ShapedPanelInternals internals, UnitViewForGUIIFace unit) {
        super(relativeX, relativeY, diagramPanel, internals, unit);
        constructor();
    }


    private void constructor() {
        panelShape = new Polygon();
        ring = new Polygon();        // Just dummy init, else there is exception because the reshape method is called after the first drawing
    }

    @Override
    public void reshape(Dimension newSize) {
        panelShape = new Ellipse2D.Double(0, 0, newSize.width, newSize.height);
        int thickness = ShapedPanel.calculateThickness(THICKNESS_DIV_FACTOR, newSize);
        ring = EllipseShapedPanel.createRingShapeEllipse((Ellipse2D) panelShape, thickness);
        reshapeInternals(newSize);
    }

    protected Shape ring;


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ShapedPanel.drawEdges(ring, g, Color.black);
    }


    // Code taken from https://stackoverflow.com/questions/35524394/draw-ring-with-given-thickness-position-and-radius-java2d
    // and modified
    /**
     * Creates ellipse which has sides of thickness thickness.
     * @param outerEllipse is the outer ellipse to create the ring shape from.
     * @param thickness is the thickness of the sides of ellipse.
     * @return Returns the shape area between inner and outer ellipse
     */
    public static Shape createRingShapeEllipse(Ellipse2D outerEllipse, double thickness) {
        Ellipse2D innerEllipse = new Ellipse2D.Double(
                outerEllipse.getCenterX() - outerEllipse.getWidth() / 2 + thickness,
                outerEllipse.getCenterY() - outerEllipse.getHeight() / 2 + thickness,
                outerEllipse.getWidth() - 2 * thickness,
                outerEllipse.getHeight() - 2 * thickness);
        Area area = new Area(outerEllipse);
        area.subtract(new Area(innerEllipse));
        return area;
    }


    @Override
    public int getDistanceFromRectangleBorders(int x) {
        // Same as the one in arc (since arc is just half of ellipse) but more specific
        // There is shift in y pos
        // I will do trick - just imagine the center is at 0,0 and the result y will be moved at the end
        Dimension size = this.getSize();
        int a = size.width / 2;
        int b = size.height / 2;
        a *= a;
        b *= b;
        x *= x;

        double y = Math.sqrt(b * (1 - (x / (double)a)));
        int yInt = (int)y;
        yInt += size.height / 2;
        return yInt - size.height;
    }


    @Override
    public void getLastPoint(Point p, int connectorIndex, int connectorCount) {
        int w = this.getWidth();

        p.x = MovableJPanel.calculateXForHorizontalLineWithEdges(0, w, connectorIndex, connectorCount);
        p.y = getDistanceFromRectangleBorders(p.x - w / 2);
        p.y = -p.y;		// I have to convert it to negative, because the distance is calculated for bot so it is inverted here
        convertRelativePixelToAbsolute(p);
    }


    @Override
    public Direction getDirectionForInputPortLabel(int connectorIndex, int connectorCount) {
        if(connectorCount % 2 == 0) {
            if(connectorIndex % 2 == 0) {
                return Direction.LEFT;
            }
            else {
                return Direction.RIGHT;
            }
        }
        else {
            int halfConnectors = connectorCount / 2;
            if (connectorIndex < halfConnectors) {
                return Direction.LEFT;
            } else if (connectorIndex > halfConnectors) {
                return Direction.RIGHT;
            } else {
                return Direction.UP;
            }
        }
    }

    @Override
    public void getNextToLastPoint(Point nextToLastPoint, int connectorIndex, int connectorCount) {
        getLastPoint(nextToLastPoint, connectorIndex, connectorCount);
        nextToLastPoint.y = 0;
    }
}
