package synthesizer.synth.operators.unary;

import synthesizer.gui.MovablePanelsPackage.DiagramPanel;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.CircleShapedPanel;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.Internals.ConstantTextInternals;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import synthesizer.synth.Unit;

public class HalfWaveRectifier extends UnaryOperator {
    public HalfWaveRectifier(Unit u) {
        super(u);
    }

    public HalfWaveRectifier(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    public double unaryOperation(double val) {
        return val > 0 ? val : 0;
    }

    @Override
    public String getDefaultPanelName() {
        return "H_RECT";
    }

    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits,
                new ConstantTextInternals(getPanelName()), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(relativeX, relativeY, w, h, panelWithUnits,
                new ConstantTextInternals(getPanelName()), this);
        return sp;
    }

    @Override
    public void resetToDefaultState() {
        // EMPTY
    }

    @Override
    public String getTooltip() {
        // https://en.wikipedia.org/wiki/Rectifier
        return "Performs operation of half-wave rectification (Returns input if > 0, 0 otherwise)";
    }

    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}