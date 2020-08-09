package DiagramSynthPackage.Synth.Operators.UnaryOperations;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.InputPort;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.OperatorInputPort;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.SingleInputPort;
import DiagramSynthPackage.Synth.Operators.Operator;
import DiagramSynthPackage.Synth.Unit;

public abstract class UnaryOperator extends Operator {
    public UnaryOperator(Unit u) { super(u); }
    public UnaryOperator(JPanelWithMovableJPanels panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    protected InputPort[] createInputPorts(JPanelWithMovableJPanels panelWithUnits, double[] neutralValues) {
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
