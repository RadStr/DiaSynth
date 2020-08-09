package DiagramSynthPackage.Synth.Operators.UnaryOperations;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals.ConstantTextInternals;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.ParallelogramShapedPanel;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import DiagramSynthPackage.Synth.Unit;

public class NormalizeOperation extends UnaryOperator {
    public NormalizeOperation(Unit u) {
        super(u);
    }

    public NormalizeOperation(JPanelWithMovableJPanels panelWithUnits) {
        super(panelWithUnits);
    }

    private double maxAbsVal;
    @Override
    public void calculateSamples() {
        maxAbsVal = Math.abs(inputPorts[0].getMaxAbsValue());
        super.calculateSamples();
    }

    @Override
    public double unaryOperation(double val) {
        return val / maxAbsVal;
    }

    @Override
    public String getDefaultPanelName() {
        return "NORM";
    }

    @Override
    protected ShapedPanel createShapedPanel(JPanelWithMovableJPanels panelWithUnits) {
        ShapedPanel sp = new ParallelogramShapedPanel(panelWithUnits, 75,
                new ConstantTextInternals(getPanelName()), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            JPanelWithMovableJPanels panelWithUnits) {
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
        return 1;
    }
}
