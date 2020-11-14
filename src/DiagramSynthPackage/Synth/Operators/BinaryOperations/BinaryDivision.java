package DiagramSynthPackage.Synth.Operators.BinaryOperations;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.CircleShapedPanel;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals.DivisionInternals;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import DiagramSynthPackage.Synth.Operators.UnaryOperations.Reciprocical;
import DiagramSynthPackage.Synth.Unit;

public class BinaryDivision extends BinaryOperator {
    public BinaryDivision(Unit u) {
        super(u);
    }

    public BinaryDivision(JPanelWithMovableJPanels panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    public double[] getNeutralValues() {
        return new double[]{0, 1};
    }

    @Override
    protected ShapedPanel createShapedPanel(JPanelWithMovableJPanels panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits, new DivisionInternals(), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            JPanelWithMovableJPanels panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(relativeX, relativeY, w, h, panelWithUnits, new DivisionInternals(), this);
        return sp;
    }

    /**
     * Resets to the default state (as if no sample was ever before played)
     */
    @Override
    public void resetToDefaultState() {
        // EMPTY
    }

    @Override
    public String getDefaultPanelName() {
        return "BinaryDivision";
    }

    private double minAllowedVal = Reciprocical.MIN_ALLOWED_VAL_FOR_POINT_TWO;
    @Override
    public void calculateSamples() {
        minAllowedVal = Reciprocical.MIN_ALLOWED_VAL_FOR_POINT_TWO * inputPorts[1].getMaxAbsValue() / 0.2;
        super.calculateSamples();
    }


    @Override
    public double binaryOperation(double a, double b) {
        if(b > -minAllowedVal && b < minAllowedVal) {
            if(b < 0) {
                b = -minAllowedVal;
            }
            else {
                b = minAllowedVal;
            }
        }

        return a / b;
    }

    @Override
    public double getMaxAbsValue() {
        return binaryOperation(inputPorts[0].getMaxAbsValue(), minAllowedVal);
    }


    @Override
    public String getTooltip() {
        return "The right input divides the left one";
    }
}