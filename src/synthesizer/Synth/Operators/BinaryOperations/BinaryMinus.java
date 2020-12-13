package synthesizer.Synth.Operators.BinaryOperations;

import synthesizer.GUI.MovablePanelsPackage.DiagramPanel;
import synthesizer.GUI.MovablePanelsPackage.ShapedPanels.CircleShapedPanel;
import synthesizer.GUI.MovablePanelsPackage.ShapedPanels.Internals.SubtractionInternals;
import synthesizer.GUI.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import synthesizer.Synth.Unit;

public class BinaryMinus extends BinaryOperator {
    public BinaryMinus(Unit u) { super(u); }
    public BinaryMinus(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    public double[] getNeutralValues() {
        return new double[] { 0, 0 };
    }


    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits, new SubtractionInternals(), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            DiagramPanel panelWithUnits) {
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

    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}
