package synthesizer.synth.Operators.BinaryOperations;


import synthesizer.gui.MovablePanelsPackage.DiagramPanel;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.CircleShapedPanel;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.Internals.PlusInternals;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import synthesizer.synth.Unit;

public class BinaryPlus extends BinaryOperator {
    public BinaryPlus(Unit u) { super(u); }
    public BinaryPlus(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    public double[] getNeutralValues() {
        return new double[] { 0, 0 };
    }


    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits, new PlusInternals(), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                         DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(relativeX, relativeY, w, h, panelWithUnits, new PlusInternals(), this);
        return sp;
    }

    @Override
    public String getDefaultPanelName() {
        return "BinaryPlus";
    }

    @Override
    public void resetToDefaultState() {
        // EMPTY
    }

    @Override
    public boolean isBinaryPlus() {
        return true;
    }

    @Override
    public double binaryOperation(double a, double b) {
        return a + b;
    }

    @Override
    public String getTooltip() {
        return "Adds 2 samples together";
    }

    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}
