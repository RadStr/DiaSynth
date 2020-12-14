package synthesizer.synth.operators.unary;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.gui.diagram.panels.port.ports.SingleInputPort;
import synthesizer.synth.operators.Operator;
import synthesizer.synth.Unit;

public abstract class UnaryOperator extends Operator {
    public UnaryOperator(Unit u) { super(u); }
    public UnaryOperator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        InputPort[] inputPorts = new InputPort[1];
        if(neutralValues != null && neutralValues.length >= inputPorts.length) {
            inputPorts[0] = new SingleInputPort(this, shapedPanel, panelWithUnits, neutralValues[0]);
        }
        else {
            inputPorts[0] = new SingleInputPort(this, shapedPanel, panelWithUnits);
        }
        return inputPorts;
    }

    @Override
    public double[] getNeutralValues() {
        return null;
    }


    public abstract double unaryOperation(double val);


    @Override
    public void calculateSamples() {
        double[] ops = inputPorts[0].getValues();
        for(int i = 0; i < results.length; i++) {
            results[i] = unaryOperation(ops[i]);
        }
    }


    // Be careful, that this implementation doesn't work for some operations
    // (For example operations which producing huge numbers for small inputs.
    // For example 1 / n gets bigger with smaller n, so it behaves the opposite).
    // And getting the min absolute value is quite hard to predict, it isn't impossible but takes a lot of time
    // I don't have and I am not even sure if it is possible to get exact numbers, since we are mostly working
    // with doubles
    @Override
    public double getMaxAbsValue() {
        return unaryOperation(inputPorts[0].getMaxAbsValue());
    }
}