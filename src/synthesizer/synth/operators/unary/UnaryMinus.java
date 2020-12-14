package synthesizer.synth.operators.unary;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.shape.CircleShapedPanel;
import synthesizer.gui.diagram.panels.shape.internals.SubtractionInternals;
import synthesizer.gui.diagram.panels.shape.ShapedPanel;
import synthesizer.synth.Unit;

public class UnaryMinus extends UnaryOperator {
    public UnaryMinus(Unit u) { super(u); }
    public UnaryMinus(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
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
        return "UnaryMinus";
    }

    @Override
    public void resetToDefaultState() {
        // EMPTY
    }


    @Override
    public double unaryOperation(double val) {
        return -val;
    }

    @Override
    public String getTooltip() {
        return "Multiplies the sample by -1";
    }

    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}
