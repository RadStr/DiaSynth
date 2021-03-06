package str.rad.synthesizer.synth.operators.unary;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.gui.diagram.panels.port.ports.SingleInputPort;
import str.rad.synthesizer.synth.operators.Operator;
import str.rad.synthesizer.synth.Unit;

public abstract class UnaryOperator extends Operator {
    public UnaryOperator(Unit u) {
        super(u);
    }

    public UnaryOperator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        InputPort[] inputPorts = new InputPort[1];
        if (neutralValues != null && neutralValues.length >= inputPorts.length) {
            inputPorts[0] = new SingleInputPort(this, shapedPanel, panelWithUnits, neutralValues[0]);
        }
        else {
            inputPorts[0] = new SingleInputPort(this, shapedPanel, panelWithUnits);
        }
        return inputPorts;
    }

    @Override
    public double[] getNeutralValuesForPorts() {
        return null;
    }


    public abstract double unaryOperation(double val);


    @Override
    public void calculateSamples() {
        double[] ops = inputPorts[0].getValues();
        for (int i = 0; i < results.length; i++) {
            results[i] = unaryOperation(ops[i]);
        }
    }


    // Be careful, that this implementation doesn't work for some operations
    // (For example operations which producing huge numbers for small inputs.
    // For example 1 / n gets bigger with smaller n, so it behaves the opposite).
    // Take a look at the reciprocal to understand how to get around this issue.
    // It doesn't even work for unary minus - so careful

    @Override
    public double getMinValue() {
        return unaryOperation(inputPorts[0].getMinValue());
    }
    @Override
    public double getMaxValue() {
        return unaryOperation(inputPorts[0].getMaxValue());
    }
}