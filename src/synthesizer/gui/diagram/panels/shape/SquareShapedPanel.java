package synthesizer.gui.diagram.panels.shape;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.shape.internals.ShapedPanelInternals;
import synthesizer.UnitViewForGUIIFace;

import java.awt.*;
import java.awt.geom.Area;

public class SquareShapedPanel extends RectangleShapedPanel {
    private static final  int THICKNESS_DIV_FACTOR = 8;


    public SquareShapedPanel(DiagramPanel diagramPanel, ShapedPanelInternals internals,
                             UnitViewForGUIIFace unit) {
        super(diagramPanel, internals, unit);
    }

    public SquareShapedPanel(int relativeX, int relativeY, int w, int h,
                             DiagramPanel diagramPanel, ShapedPanelInternals internals,
                             UnitViewForGUIIFace unit) {
        super(relativeX, relativeY, w, h, diagramPanel, internals, unit);
    }

    public SquareShapedPanel(int relativeX, int relativeY, DiagramPanel diagramPanel,
                             ShapedPanelInternals internals, UnitViewForGUIIFace unit) {
        super(relativeX, relativeY, diagramPanel, internals, unit);
    }

    @Override
    public void reshape(Dimension newSize) {
        outerRectangle.reset();

        int w = Math.min(newSize.width / 2, newSize.height / 2);   // square width
        // So it is in the middle
        int xStart = newSize.width / 4 + (newSize.width / 2 - w) / 2;
        int yStart = newSize.height / 4 + (newSize.height / 2 - w) / 2;

        // Points are added clock-wise starting on top left
        outerRectangle.addPoint(xStart, yStart);
        outerRectangle.addPoint(xStart + w, yStart);
        outerRectangle.addPoint(xStart + w, yStart + w);
        outerRectangle.addPoint(xStart, yStart + w);

        int thickness = w / THICKNESS_DIV_FACTOR;
        createRectangle(thickness);

        reshapeInternals(newSize);
    }

    public static Area createRectangle(Polygon outerRectangle, int thickness) {
        return RectangleShapedPanel.createRectangle(outerRectangle, thickness, thickness);
    }

    public void createRectangle(int thickness) {
        super.createRectangle(thickness, thickness);
    }
}
