package synthesizer.Synth.Operators.UnaryOperations;

import synthesizer.GUI.MovablePanelsPackage.DiagramPanel;
import synthesizer.GUI.MovablePanelsPackage.ShapedPanels.CircleShapedPanel;
import synthesizer.GUI.MovablePanelsPackage.ShapedPanels.Internals.ConstantTextInternals;
import synthesizer.GUI.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import synthesizer.Synth.Unit;

public class Reciprocical extends UnaryOperator {
    public Reciprocical(Unit u) {
        super(u);
    }

    public Reciprocical(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    public String getDefaultPanelName() {
        return "1/x ";
    }

    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits,
                new ConstantTextInternals(getDefaultPanelName()), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(relativeX, relativeY, w, h, panelWithUnits,
                new ConstantTextInternals(getDefaultPanelName()), this);
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
        return "Returns 1 / input";
    }


    /**
     * For amplitude = 1 this is the minimum allowed absolute value.
     */
    public static final double MIN_ALLOWED_VAL_FOR_ONE = 0.05;
    private double minAllowedVal = MIN_ALLOWED_VAL_FOR_ONE;
    @Override
    public void calculateSamples() {
        minAllowedVal = MIN_ALLOWED_VAL_FOR_ONE * inputPorts[0].getMaxAbsValue();
        super.calculateSamples();
    }

    @Override
    public double unaryOperation(double val) {
        if(val > -minAllowedVal && val < minAllowedVal) {
            if(val < 0) {
                val = -minAllowedVal;
            }
            else {
                val = minAllowedVal;
            }
        }

        // Alternative variant, but I think that the other one is generally faster,
        // since usually very small number of elements passes the first condition
//        if(val > -minAllowedVal) {
//            if(val < 0) {
//                val = -minAllowedVal;
//            }
//            else if(val < minAllowedVal) {
//                val = minAllowedVal;
//            }
//        }

        // TODO: DEBUG
//        ProgramTest.debugPrint("Generated value:", val, 1 / val);
        // TODO: DEBUG
        return 1 / val;
    }

    @Override
    public double getMaxAbsValue() {
        // TODO: DEBUG
//        ProgramTest.debugPrint("Max:", inputPorts[0].getMaxAbsValue(),
//                minAllowedVal, inputPorts[0].getMaxAbsValue() / minAllowedVal, 1 / minAllowedVal);
        // TODO: DEBUG
        return 1 / minAllowedVal;
    }

    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}
