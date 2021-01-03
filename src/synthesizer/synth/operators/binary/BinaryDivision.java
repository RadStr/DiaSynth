package synthesizer.synth.operators.binary;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.shape.CircleShapedPanel;
import synthesizer.gui.diagram.panels.shape.internals.DivisionInternals;
import synthesizer.gui.diagram.panels.shape.ShapedPanel;
import synthesizer.synth.operators.unary.Reciprocal;
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
        ShapedPanel sp = new CircleShapedPanel(relativeX, relativeY, w, h, panelWithUnits,
                                               new DivisionInternals(), this);
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

    private double minAllowedVal = Reciprocal.MIN_ALLOWED_VAL_FOR_ONE;

    @Override
    public void calculateSamples() {
        minAllowedVal = Reciprocal.MIN_ALLOWED_VAL_FOR_ONE * inputPorts[1].getMaxAbsValue();
        super.calculateSamples();
    }


    @Override
    public double binaryOperation(double a, double b) {
        if (b > -minAllowedVal && b < minAllowedVal) {
            if (b < 0) {
                b = -minAllowedVal;
            }
            else {
                b = minAllowedVal;
            }
        }

        return a / b;
    }

//    @Override
//    public double getMaxAbsValue() {
//        return binaryOperation(inputPorts[0].getMaxAbsValue(), minAllowedVal);
//    }

    @Override
    public double getMaxAbsValue() {
        double min = getMinValue();
        double max = getMaxValue();
        return Math.max(Math.abs(min), Math.abs(max));
    }

    @Override
    public double getMinValue() {
        double min = inputPorts[1].getMinValue();
        double max = inputPorts[1].getMaxValue();
        double pd = Reciprocal.getSmallestPositiveDenominator(min, max, minAllowedVal);
        double nd = Reciprocal.getSmallestNegativeDenominator(min, max, minAllowedVal);

        double numeratorMin = inputPorts[0].getMinValue();
        double numeratorMax = inputPorts[0].getMaxValue();

        // 4 cases for the minimum
        // numeratorMin >= 0 and min >= 0 --- Finding the smallest positive number ... divide numMin by max
        // numeratorMin < 0 and pd >= 0   --- Finding the smallest negative number ... divide numMin by pd

        // Now 2 mirror cases for the numeratorMax and nd
        // numeratorMax < 0 and max < 0   --- Finding the smallest positive number ... divide numMax by min
        // numeratorMax >= 0 and nd < 0   --- Finding the smallest negative number ... divide numMax by nd
        double result = Math.min(binaryOperation(numeratorMin, pd), binaryOperation(numeratorMax, nd));
        if(result >= 0) {
            result = Math.min(binaryOperation(numeratorMin, max), binaryOperation(numeratorMax, min));

        }
        return result;
    }


    @Override
    public double getMaxValue() {
        double min = inputPorts[1].getMinValue();
        double max = inputPorts[1].getMaxValue();
        double pd = Reciprocal.getSmallestPositiveDenominator(min, max, minAllowedVal);
        double nd = Reciprocal.getSmallestNegativeDenominator(min, max, minAllowedVal);

        double numeratorMin = inputPorts[0].getMinValue();
        double numeratorMax = inputPorts[0].getMaxValue();


        // 4 cases for the maximum
        // Same as minimum just switch "signs", now when we get positive it is good (for maximum) -
        // swap numeratorMin with numeratorMax (and numMax with numMin) and change the first compare operator
        // numeratorMax < 0 and min >= 0   --- Finding the smallest negative number ... divide numMax by max
        // numeratorMax >= 0 and pd >= 0   --- Finding the biggest positive number  ... divide numMax by pd

        // Now 2 mirror cases for the numeratorMin and nd
        // numeratorMin >= 0 and max < 0   --- Finding the smallest negative number ... divide numMin by min
        // numeratorMin < 0 and nd < 0     --- Finding the biggest positive number  ... divide numMin by nd
        double result = Math.max(binaryOperation(numeratorMax, pd), binaryOperation(numeratorMin, nd));
        if(result < 0) {
            result = Math.max(binaryOperation(numeratorMax, max), binaryOperation(numeratorMin, min));

        }
        return result;
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