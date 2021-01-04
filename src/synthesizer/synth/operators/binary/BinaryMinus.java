package synthesizer.synth.operators.binary;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.shape.CircleShapedPanel;
import synthesizer.gui.diagram.panels.shape.internals.SubtractionInternals;
import synthesizer.gui.diagram.panels.shape.ShapedPanel;
import synthesizer.synth.Unit;

public class BinaryMinus extends BinaryOperator {
    public BinaryMinus(Unit u) {
        super(u);
    }

    public BinaryMinus(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    public double[] getNeutralValuesForPorts() {
        return new double[]{0, 0};
    }


    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits, new SubtractionInternals(), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(relativeX, relativeY, w, h, panelWithUnits,
                                               new SubtractionInternals(), this);
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
    public double getMinValue() {
        double minA = inputPorts[0].getMinValue();
        double maxB = inputPorts[1].getMaxValue();
        return binaryOperation(minA, maxB);
    }
    @Override
    public double getMaxValue() {
        double maxA = inputPorts[0].getMaxValue();
        double minB = inputPorts[1].getMinValue();
        return binaryOperation(maxA, minB);
    }


    @Override
    public String getTooltip() {
        return "Subtracts right input from the left.";
    }

    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}
