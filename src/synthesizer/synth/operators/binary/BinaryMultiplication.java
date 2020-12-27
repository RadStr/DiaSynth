package synthesizer.synth.operators.binary;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.shape.CircleShapedPanel;
import synthesizer.gui.diagram.panels.shape.internals.MultiplyInternals;
import synthesizer.gui.diagram.panels.shape.ShapedPanel;
import synthesizer.synth.Unit;

public class BinaryMultiplication extends BinaryOperator {
    public BinaryMultiplication(Unit u) {
        super(u);
    }

    public BinaryMultiplication(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    public double[] getNeutralValues() {
        return new double[]{1, 1};
    }


    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits, new MultiplyInternals(), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(relativeX, relativeY, w, h, panelWithUnits,
                                               new MultiplyInternals(), this);
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
