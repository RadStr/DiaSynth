package DiagramSynthPackage.Synth.Operators.BinaryOperations;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.CircleShapedPanel;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals.MultiplyInternals;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import DiagramSynthPackage.Synth.Unit;

public class BinaryMultiplication extends BinaryOperator {
    public BinaryMultiplication(Unit u) { super(u); }
    public BinaryMultiplication(JPanelWithMovableJPanels panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    public double[] getNeutralValues() {
        return new double[] { 1, 1 };
    }


    @Override
    protected ShapedPanel createShapedPanel(JPanelWithMovableJPanels panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits, new MultiplyInternals(), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            JPanelWithMovableJPanels panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(relativeX, relativeY, w, h, panelWithUnits, new MultiplyInternals(), this);
        return sp;
    }

    @Override
    public String getDefaultPanelName() {
        return "BinaryMultiplication";
    }

    @Override
    public void resetToDefaultState() {
        // EMPTY
    }

    @Override
    public double binaryOperation(double a, double b) {
        return a * b;
    }

    @Override
    public String getTooltip() {
        return "Multiplies 2 samples together";
    }
}
