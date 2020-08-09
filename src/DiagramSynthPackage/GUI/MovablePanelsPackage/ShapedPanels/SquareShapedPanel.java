package DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals.ShapedPanelInternals;
import DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage.UnitCommunicationWithGUI;

import java.awt.*;
import java.awt.geom.Area;

public class SquareShapedPanel extends RectangleShapedPanel {
    private static final  int THICKNESS_DIV_FACTOR = 8;


    public SquareShapedPanel(JPanelWithMovableJPanels mainPanel, ShapedPanelInternals internals,
                             UnitCommunicationWithGUI unit) {
        super(mainPanel, internals, unit);
    }

    public SquareShapedPanel(int relativeX, int relativeY, int w, int h,
                             JPanelWithMovableJPanels mainPanel, ShapedPanelInternals internals,
                             UnitCommunicationWithGUI unit) {
        super(relativeX, relativeY, w, h, mainPanel, internals, unit);
    }

    public SquareShapedPanel(int relativeX, int relativeY, JPanelWithMovableJPanels mainPanel,
                             ShapedPanelInternals internals, UnitCommunicationWithGUI unit) {
        super(relativeX, relativeY, mainPanel, internals, unit);
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
