package synthesizer.Synth.Operators.BinaryOperations;

import synthesizer.GUI.MovablePanelsPackage.DiagramPanel;
import synthesizer.GUI.MovablePanelsPackage.ShapedPanels.CircleShapedPanel;
import synthesizer.GUI.MovablePanelsPackage.ShapedPanels.Internals.MultiplyInternals;
import synthesizer.GUI.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import synthesizer.Synth.Unit;

public class BinaryMultiplication extends BinaryOperator {
    public BinaryMultiplication(Unit u) { super(u); }
    public BinaryMultiplication(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    public double[] getNeutralValues() {
        return new double[] { 1, 1 };
    }


    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits, new MultiplyInternals(), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            DiagramPanel panelWithUnits) {
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

    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}
