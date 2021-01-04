package str.rad.synthesizer.synth.operators.unary;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.diagram.panels.shape.internals.ConstantTextInternals;
import str.rad.synthesizer.gui.diagram.panels.shape.ParallelogramShapedPanel;
import str.rad.synthesizer.gui.diagram.panels.shape.ShapedPanel;
import str.rad.synthesizer.synth.Unit;

public class NormalizeOperation extends UnaryOperator {
    public NormalizeOperation(Unit u) {
        super(u);
    }

    public NormalizeOperation(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    private double maxAbsVal;

    @Override
    public void calculateSamples() {
        maxAbsVal = inputPorts[0].getMaxAbsValue();
        super.calculateSamples();
    }

    @Override
    public double unaryOperation(double val) {
        return normalize(val, maxAbsVal);
    }

    @Override
    public String getDefaultPanelName() {
        return "NORM";
    }

    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new ParallelogramShapedPanel(panelWithUnits, 75,
                                                      new ConstantTextInternals(getPanelName()), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            DiagramPanel panelWithUnits) {
        ShapedPanel sp = new ParallelogramShapedPanel(relativeX, relativeY, w, h, panelWithUnits, 75,
                                                      new ConstantTextInternals(getPanelName()), this);
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
    public String getTooltip() {
        return "Normalizes the given input (Divides the input by the maximum absolute value)";
    }

    @Override
    public double getMaxAbsValue() {
        if(inputPorts[0].getMaxAbsValue() == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public double getMinValue() {
        return unaryOperation(inputPorts[0].getMinValue());
    }
    @Override
    public double getMaxValue() {
        return unaryOperation(inputPorts[0].getMaxValue());
    }

    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }


    /**
     * Normalizes the value in such way, that the maxAbsVal will be normalized to 1
     * @param value is the value to be normalized
     * @param maxAbsVal is the maximum absolute value in all of the values
     * @return
     */
    public static double normalize(double value, double maxAbsVal) {
        return value / maxAbsVal;
    }

    /**
     * Normalizes the value in such way, that the maxAbsVal will be normalized to normalizedMaxAbsVal.
     * @param value is the value to be normalized
     * @param maxAbsVal is the maximum absolute value in all of the values
     * @param normalizedMaxAbsVal is the value to which will be the maxAbsVal normalized.
     * @return
     */
    public static double normalize(double value, double maxAbsVal, double normalizedMaxAbsVal) {
        return value * (normalizedMaxAbsVal / maxAbsVal);
    }
}
