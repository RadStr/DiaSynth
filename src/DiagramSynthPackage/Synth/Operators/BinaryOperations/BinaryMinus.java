package DiagramSynthPackage.Synth.Operators.BinaryOperations;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.CircleShapedPanel;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals.SubtractionInternals;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import DiagramSynthPackage.Synth.Unit;

public class BinaryMinus extends BinaryOperator {
    public BinaryMinus(Unit u) { super(u); }
    public BinaryMinus(JPanelWithMovableJPanels panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    public double[] getNeutralValues() {
        return new double[] { 0, 0 };
    }


    @Override
    protected ShapedPanel createShapedPanel(JPanelWithMovableJPanels panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits, new SubtractionInternals(), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            JPanelWithMovableJPanels panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(relativeX, relativeY, w, h, panelWithUnits, new SubtractionInternals(), this);
        return sp;
    }

    @Override
    public String getDefaultPanelName() {
        return "Subtraction";
    }


    @Override
    public void resetToDefaultState() {
        // EMPTY
    }


    public double binaryOperation(double a, double b) {
        return a - b;
    }

    @Override
    public String getTooltip() {
        return "Subtracts right input from the left.";
    }
}
