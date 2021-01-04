package str.rad.synthesizer.synth.operators.binary;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.gui.diagram.panels.port.ports.OperatorInputPort;
import str.rad.synthesizer.synth.operators.Operator;
import str.rad.synthesizer.synth.Unit;

public abstract class BinaryOperator extends Operator {
    public BinaryOperator(Unit u) {
        super(u);
    }

    public BinaryOperator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        InputPort[] inputPorts = new InputPort[2];
        if (neutralValues != null && neutralValues.length >= inputPorts.length) {
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

    @Override
    public void calculateSamples() {
        double[] ops = inputPorts[0].getValues();
        double[] ops2 = inputPorts[1].getValues();
        for (int i = 0; i < results.length; i++) {
            results[i] = binaryOperation(ops[i], ops2[i]);
        }
    }
}
