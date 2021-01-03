package synthesizer.synth.operators.unary;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.shape.CircleShapedPanel;
import synthesizer.gui.diagram.panels.shape.internals.ConstantTextInternals;
import synthesizer.gui.diagram.panels.shape.ShapedPanel;
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
    public double getMaxAbsValue() {
        return unaryOperation(inputPorts[0].getMaxAbsValue());
    }

    @Override
    public double getMinValue() {
        double min = inputPorts[0].getMinValue();
        double max = inputPorts[0].getMaxValue();
        if(min <= 0 && max >= 0) {      // It goes through 0
            return 0;
        }
        else {                          // Doesn't go through zero
            if(min >= 0) {              // Only positive
                return min;
            }
            else {                      // Only negative
                return -max;
            }
        }
    }

    @Override
    public double getMaxValue() {
        return unaryOperation(inputPorts[0].getMaxAbsValue());
    }



    @Override
    public String getDefaultPanelName() {
        return "RECT";
    }

    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits, new ConstantTextInternals(getPanelName()), this);
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
