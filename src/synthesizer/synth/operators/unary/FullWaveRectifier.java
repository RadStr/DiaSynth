package synthesizer.synth.operators.unary;

import synthesizer.gui.MovablePanelsPackage.DiagramPanel;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.CircleShapedPanel;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.internals.ConstantTextInternals;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import synthesizer.synth.Unit;

public class FullWaveRectifier extends UnaryOperator {
    public FullWaveRectifier(Unit u) {
        super(u);
    }

    public FullWaveRectifier(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    public double unaryOperation(double val) {
        return Math.abs(val);
    }

    @Override
    public String getDefaultPanelName() {
        return "RECT";
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
        return "Performs operation of full-wave rectification (Returns absolute value of given inputs)";
    }

    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}
