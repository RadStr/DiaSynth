package synthesizer.synth.operators.unary;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.shape.internals.ConstantTextInternals;
import synthesizer.gui.diagram.panels.shape.ParallelogramShapedPanel;
import synthesizer.gui.diagram.panels.shape.ShapedPanel;
import synthesizer.synth.Unit;

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
        return 1;
    }

    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}
