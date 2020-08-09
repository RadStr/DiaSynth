package DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals.ShapedPanelInternals;
import DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage.UnitCommunicationWithGUI;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class CircleShapedPanel extends EllipseShapedPanel {
	private static final int THICKNESS_DIV_FACTOR = 16;

	public CircleShapedPanel(JPanelWithMovableJPanels mainPanel, ShapedPanelInternals internals,
							 UnitCommunicationWithGUI unit) {
		super(mainPanel, internals, unit);
	}

	public CircleShapedPanel(int relativeX, int relativeY, int w, int h,
							 JPanelWithMovableJPanels mainPanel, ShapedPanelInternals internals,
							 UnitCommunicationWithGUI unit) {
		super(relativeX, relativeY, w, h, mainPanel, internals, unit);
	}

	public CircleShapedPanel(int relativeX, int relativeY, JPanelWithMovableJPanels mainPanel,
							 ShapedPanelInternals internals, UnitCommunicationWithGUI unit) {
		super(relativeX, relativeY, mainPanel, internals, unit);
	}

	@Override
	public void reshape(Dimension newSize) {
		int centerX = newSize.width / 2;
		int centerY = newSize.height / 2;

		int thickness = Math.min(newSize.width, newSize.height);		// Is the diameter of circle
		outerRadius = thickness / 2;
		thickness /= THICKNESS_DIV_FACTOR;
		createRingShapeCircle(centerX, centerY, outerRadius, thickness);

		reshapeInternals(newSize);
	}

	private int outerRadius;


	// Code taken from https://stackoverflow.com/questions/35524394/draw-ring-with-given-thickness-position-and-radius-java2d
	// and modified
	/**
	 * Creates ring with radius outerRadius and sets panel shape to the outer ellipse
	 * @param centerX is the center x of the ring.
	 * @param centerY is the center y of the ring.
	 * @param outerRadius is the radius of the whole ring
	 * @param thickness is the thickness between inner and outer side of circle.
	 */
	private void createRingShapeCircle(double centerX, double centerY, double outerRadius, double thickness) {
		Ellipse2D outer = new Ellipse2D.Double(
				centerX - outerRadius,
				centerY - outerRadius,
				outerRadius + outerRadius,
				outerRadius + outerRadius);
		Ellipse2D inner = new Ellipse2D.Double(
				centerX - outerRadius + thickness,
				centerY - outerRadius + thickness,
				outerRadius + outerRadius - thickness - thickness,
				outerRadius + outerRadius - thickness - thickness);

		Area area = new Area(outer);
		area.subtract(new Area(inner));

		panelShape = outer;
		ring = area;
	}


	@Override
	public int getDistanceFromRectangleBorders(int x) {
		// Pythagorean theorem
		int c = outerRadius * outerRadius;
		int a = x * x;
		double y = Math.sqrt(c - a);
		int yInt = (int)y;
		int ph = this.getSize().height;		// panel Height
		return yInt - ph / 2;
	}
}
