package synthesizer.synth.operators.binary;

import synthesizer.gui.MovablePanelsPackage.DiagramPanel;
import synthesizer.gui.MovablePanelsPackage.port.InputPort;
import synthesizer.gui.MovablePanelsPackage.port.OperatorInputPort;
import synthesizer.synth.operators.Operator;
import synthesizer.synth.Unit;

public abstract class BinaryOperator extends Operator {
    public BinaryOperator(Unit u) { super(u); }
    public BinaryOperator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        InputPort[] inputPorts = new InputPort[2];
        if(neutralValues != null && neutralValues.length >= inputPorts.length) {
            inputPorts[0] = new OperatorInputPort(this, shapedPanel, 0, panelWithUnits, neutralValues[0]);
            inputPorts[1] = new OperatorInputPort(this, shapedPanel, 1, panelWithUnits, neutralValues[1]);
        }
        else {
            inputPorts[0] = new OperatorInputPort(this, shapedPanel, 0, panelWithUnits);
            inputPorts[1] = new OperatorInputPort(this, shapedPanel, 1, panelWithUnits);
        }
        return inputPorts;
    }


    public abstract double binaryOperation(double a, double b);

    // Doesn't work for division. But I am not solving that, user just should be careful and divide in such a way
    // that the maximum after division is either always <= 1 or there is always larger number divided by smaller number
    @Override
    public double getMaxAbsValue() {
        double maxA = inputPorts[0].getMaxAbsValue();
        double maxB = inputPorts[1].getMaxAbsValue();
        return binaryOperation(maxA, maxB);
    }

    @Override
    public void calculateSamples() {
        double[] ops = inputPorts[0].getValues();
        double[] ops2 = inputPorts[1].getValues();
        for (int i = 0; i < results.length; i++) {
            results[i] = binaryOperation(ops[i], ops2[i]);
        }
    }
}
