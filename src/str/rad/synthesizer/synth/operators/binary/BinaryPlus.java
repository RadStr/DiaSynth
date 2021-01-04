package str.rad.synthesizer.synth.operators.binary;


import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.diagram.panels.shape.CircleShapedPanel;
import str.rad.synthesizer.gui.diagram.panels.shape.internals.PlusInternals;
import str.rad.synthesizer.gui.diagram.panels.shape.ShapedPanel;
import str.rad.synthesizer.synth.Unit;

public class BinaryPlus extends BinaryOperator {
    public BinaryPlus(Unit u) {
        super(u);
    }

    public BinaryPlus(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    public double[] getNeutralValuesForPorts() {
        return new double[]{0, 0};
    }


    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits, new PlusInternals(), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(relativeX, relativeY, w, h, panelWithUnits,
                                               new PlusInternals(), this);
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
    public double getMinValue() {
        double minA = inputPorts[0].getMinValue();
        double minB = inputPorts[1].getMinValue();
        return binaryOperation(minA, minB);
    }
    @Override
    public double getMaxValue() {
        double maxA = inputPorts[0].getMaxValue();
        double maxB = inputPorts[1].getMaxValue();
        return binaryOperation(maxA, maxB);
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
