package synthesizer.gui.diagram.panels.shape;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.shape.internals.ShapedPanelInternals;
import synthesizer.UnitViewForGUIIFace;

import java.awt.*;
import java.awt.geom.Area;

public class RectangleShapedPanel extends ShapedPanel {
    private static final int TOP_THICKNESS_DIV_FACTOR = 16;
    private static final int LEFT_THICKNESS_DIV_FACTOR = 16;


    public RectangleShapedPanel(DiagramPanel diagramPanel, ShapedPanelInternals internals,
                                UnitViewForGUIIFace unit) {
        super(diagramPanel, internals, unit);
        constructor();
    }

    public RectangleShapedPanel(int relativeX, int relativeY, int w, int h,
                                DiagramPanel diagramPanel, ShapedPanelInternals internals,
                                UnitViewForGUIIFace unit) {
        super(relativeX, relativeY, w, h, diagramPanel, internals, unit);
        constructor();
    }

    public RectangleShapedPanel(int relativeX, int relativeY, DiagramPanel diagramPanel,
                                ShapedPanelInternals internals, UnitViewForGUIIFace unit) {
        super(relativeX, relativeY, diagramPanel, internals, unit);
        constructor();
    }


    private void constructor() {
        rectangle = new Area();
        outerRectangle = new Polygon();
        panelShape = outerRectangle;

    }


    @Override
    public void reshape(Dimension newSize) {
        outerRectangle.reset();
        // Points are added clock-wise starting on top left
        outerRectangle.addPoint(0, 0);
        outerRectangle.addPoint(newSize.width, 0);
        outerRectangle.addPoint(newSize.width, newSize.height);
        outerRectangle.addPoint(0, newSize.height);

        int leftThickness = newSize.width / LEFT_THICKNESS_DIV_FACTOR;
        int topThickness = newSize.height / TOP_THICKNESS_DIV_FACTOR;
        createRectangle(topThickness, leftThickness);

        reshapeInternals(newSize);
    }

    /**
     * It just view on the panelShape, looking at it as polygon.
     */
    protected Polygon outerRectangle;
    private Area rectangle;


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ShapedPanel.drawEdges(rectangle, g, Color.black);
    }



    /**
     * Creates area of outerParallelogram and thicknesses.
     * @param outerRectangle is the outer rectangle
     * @param topThickness is the thickness of the top and bot side of rectangle.
     * @param leftThickness is the thickness of the left and right side of rectangle.
     * @return Returns the shape area between inner and outer rectangle
     */
    public static Area createRectangle(Polygon outerRectangle, int topThickness, int leftThickness) {
        int x,y;
        Polygon innerRectangle = new Polygon();

        x = outerRectangle.xpoints[0] + leftThickness;
        y = outerRectangle.ypoints[0] + topThickness;
        innerRectangle.addPoint(x, y);

        x = outerRectangle.xpoints[1] - leftThickness;
        y = outerRectangle.ypoints[1] + topThickness;
        innerRectangle.addPoint(x, y);

        x = outerRectangle.xpoints[2] - leftThickness;
        y = outerRectangle.ypoints[2] - topThickness;
        innerRectangle.addPoint(x, y);

        x = outerRectangle.xpoints[3] + leftThickness;
        y = outerRectangle.ypoints[3] - topThickness;
        innerRectangle.addPoint(x, y);


        Area area = new Area(outerRectangle);
        area.subtract(new Area(innerRectangle));
        return area;
    }

    public void createRectangle(int topThickness, int leftThickness) {
        rectangle = createRectangle(outerRectangle, topThickness, leftThickness);
    }
}
