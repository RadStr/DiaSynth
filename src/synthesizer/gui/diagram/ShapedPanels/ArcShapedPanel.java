package synthesizer.gui.diagram.ShapedPanels;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.ShapedPanels.internals.ShapedPanelInternals;
import synthesizer.gui.PanelAroundMovablePanelsPackage.UnitCommunicationWithGUI;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;

public class ArcShapedPanel extends ShapedPanel {
    private final int THICKNESS_DIV_FACTOR = 8;

    public ArcShapedPanel(DiagramPanel diagramPanel, ShapedPanelInternals internals,
                          UnitCommunicationWithGUI unit) {
        super(diagramPanel, internals, unit);
        constructor();
    }

    public ArcShapedPanel(int relativeX, int relativeY, int w, int h,
                          DiagramPanel diagramPanel, ShapedPanelInternals internals,
                          UnitCommunicationWithGUI unit) {
        super(relativeX, relativeY, w, h, diagramPanel, internals, unit);
        constructor();
    }

    public ArcShapedPanel(int relativeX, int relativeY, DiagramPanel diagramPanel,
                          ShapedPanelInternals internals, UnitCommunicationWithGUI unit) {
        super(relativeX, relativeY, diagramPanel, internals, unit);
        constructor();
    }


    private void constructor() {
        arc = new Area();        // Just dummy init, else there is exception because the reshape method is called after the first drawing
        panelShapeView = new Arc2D.Double();
        panelShape = panelShapeView;
    }

    @Override
    public void reshape(Dimension newSize) {
        // To be honest I don't understand why it is -newSize.height, probably mistake in documentation???
        // Because -newSize.height is the top y of the whole ecllipse
        panelShapeView.setArc(0, -newSize.height, newSize.width, 2 * newSize.height, 0, -180, Arc2D.CHORD);

        createArc(newSize);
        reshapeInternals(newSize);
    }

    /**
     * View on panel shape
     */
    private Arc2D panelShapeView;

    protected Area arc;


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ShapedPanel.drawEdges(arc, g, Color.black);
    }



    /**
     * Creates area of outerArc and thickness.
     * @param outerArc is the outer arc
     * @param thickness is the thickness
     * @return Returns the shape area between inner and outer arc
     */
    public static Area createArc(Arc2D outerArc, int thickness) {
        Arc2D innerArc = new Arc2D.Double(outerArc.getX() + thickness / 2, outerArc.getY() + thickness,
                outerArc.getWidth() - thickness, outerArc.getHeight() - thickness - thickness / 2,
                outerArc.getAngleStart(), outerArc.getAngleExtent(), outerArc.getArcType());

        Area area = new Area(outerArc);
        area.subtract(new Area(innerArc));
        return area;
    }

    public void createArc(Dimension newSize) {
        int thickness = Math.min(newSize.width, newSize.height) / THICKNESS_DIV_FACTOR;
        arc = ArcShapedPanel.createArc(panelShapeView, thickness);
    }


    @Override
    public int getDistanceFromRectangleBorders(int x) {
        Dimension size = this.getSize();
        // I get the y from the equation (x-m)^2 / b^2 + y^2 / a^2 = 1 ... where a == panel width / 2
        // b == panel height ; x-m = x from parameter
        // a is the x axis, b is the y axis
        int a = size.width / 2;
        int b = size.height;
        a *= a;
        b *= b;
        x *= x;

        double y = Math.sqrt(b * (1 - (x / (double)a)));
        int yInt = (int)y;
        return yInt - size.height;
    }
}
