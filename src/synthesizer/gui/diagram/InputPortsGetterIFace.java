package synthesizer.gui.diagram;

import synthesizer.gui.diagram.port.InputPort;

public interface InputPortsGetterIFace {
    InputPort[] getInputPorts();
    InputPort getInputPort(int inputPortIndex);
}
