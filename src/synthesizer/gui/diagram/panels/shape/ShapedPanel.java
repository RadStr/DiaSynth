package synthesizer.gui.diagram.panels.shape;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.MovableJPanel;
import synthesizer.gui.diagram.panels.shape.internals.ShapedPanelInternals;
import synthesizer.UnitViewForGUIIFace;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * This is the base class for panel shapes.
 * NOTE: reshape(new Dimension(diagramPanel.getReferencePanelWidth(), diagramPanel.getReferencePanelHeight()));
 * or reshape with different argument based on the wanted size
 * should be called every time instance of Shaped panel is created,
 * else the panel won't be drawn correctly since the shape won't be calculated correctly
 */
public abstract class ShapedPanel extends MovableJPanel {
    /**
     * @param diagramPanel
     * @param internals    - null if we want just the shape without any internals
     */
    public ShapedPanel(DiagramPanel diagramPanel, ShapedPanelInternals internals,
                       UnitViewForGUIIFace unit) {
        super(diagramPanel, unit);
        constructor(internals);
    }

    /**
     * @param relativeX
     * @param relativeY
     * @param w
     * @param h
     * @param diagramPanel
     * @param internals    - null if we want just the shape without any internals
     */
    public ShapedPanel(int relativeX, int relativeY, int w, int h,
                       DiagramPanel diagramPanel, ShapedPanelInternals internals,
                       UnitViewForGUIIFace unit) {
        super(relativeX, relativeY, w, h, diagramPanel, unit);
        constructor(internals);
    }

    /**
     * @param relativeX
     * @param relativeY
     * @param diagramPanel
     * @param internals    - null if we want just the shape without any internals
     */
    public ShapedPanel(int relativeX, int relativeY, DiagramPanel diagramPanel,
                       ShapedPanelInternals internals, UnitViewForGUIIFace unit) {
        super(relativeX, relativeY, diagramPanel, unit);
        constructor(internals);
    }


    private void constructor(ShapedPanelInternals internals) {
        this.setLayout(null);
        this.internals = internals;
    }

    /**
     * The panelShape variable should be changed only in the panel which implements concrete shape, next derived classes
     * shouldn't change the shape. (So basically the class implementing Shaped Panel should be final)
     */
    protected Shape panelShape;

    public Shape getPanelShape() {
        return panelShape;
    }

    private ShapedPanelInternals internals;

    @Override
    public ShapedPanelInternals getInternals() {
        return internals;
    }

    private void setInternals(ShapedPanelInternals internals) {
        this.internals = internals;
    }

    /**
     * Called when resized. Don't forget to call method reshapeInternals to reshape the internals.
     */
    @Override
    abstract public void reshape(Dimension newSize);

    public void reshapeInternals(Dimension newSize) {
        if (internals != null) {
            internals.reshape(newSize);
        }
    }

    @Override
    public void updateSize(Dimension newSize) {
        reshape(newSize);
        super.updateSize(newSize);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;


        g.setColor(getDrawColor());
        g2.fill(panelShape);
        g.setColor(Color.black);
        g2.draw(panelShape);

        if (internals != null) {
            internals.draw(g);
        }

// TODO: RML
        // TODO: LAST POINT - DEBUG
//		g.setColor(Color.RED);
//		g2.setStroke(new BasicStroke(10));
//		InputPort[] inputPorts = getInputPorts();
//		for(int i = 0; i < inputPorts.length; i++) {
//			Point p = getLastPoint(i, inputPorts.length);
//			g.drawLine(p.x, p.y, p.x, p.y);
//		}
        // TODO: LAST POINT - DEBUG


// TODO: DEBUG
//		g.setColor(Color.RED);
//		g2.setStroke(new BasicStroke(10));
//		for(int x = -this.getSize().width / 2; x < this.getSize().width / 2; x++) {
//			int y = getDistanceFromRectangleBorders(x);
//			g2.draw(new Line2D.Float(
//					x + this.getSize().width / 2,
//					this.getSize().height + y,
//					x + this.getSize().width / 2,
//					this.getSize().height + y));
//		}
// TODO: DEBUG
// TODO: RML
    }


    public static void drawEdges(Shape area, Graphics g, Color color) {
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(color);
        g2.fill(area);
        g2.draw(area);
    }

    public static int calculateThickness(int divFactor, Dimension newSize) {
        return Math.min(newSize.width, newSize.height) / divFactor;
    }


    @Override
    public boolean getIsPointInsideShape(Point2D p) {
        return getPanelShape().contains(p);
    }
}
