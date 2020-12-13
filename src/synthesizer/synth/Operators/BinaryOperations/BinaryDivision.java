package synthesizer.synth.Operators.BinaryOperations;

import synthesizer.gui.MovablePanelsPackage.DiagramPanel;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.CircleShapedPanel;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.Internals.DivisionInternals;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import synthesizer.synth.Operators.UnaryOperations.Reciprocical;
import synthesizer.synth.Unit;

public class BinaryDivision extends BinaryOperator {
    public BinaryDivision(Unit u) {
        super(u);
    }

    public BinaryDivision(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    public double[] getNeutralValues() {
        return new double[]{0, 1};
    }

    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits, new DivisionInternals(), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            DiagramPanel panelWithUnits) {
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

    private double minAllowedVal = Reciprocical.MIN_ALLOWED_VAL_FOR_ONE;
    @Override
    public void calculateSamples() {
        minAllowedVal = Reciprocical.MIN_ALLOWED_VAL_FOR_ONE * inputPorts[1].getMaxAbsValue();
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

    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}