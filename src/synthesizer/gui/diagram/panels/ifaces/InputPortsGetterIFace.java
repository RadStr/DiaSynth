package synthesizer.gui.diagram.panels.ifaces;

import synthesizer.gui.diagram.panels.port.InputPort;

public interface InputPortsGetterIFace {
    InputPort[] getInputPorts();

    InputPort getInputPort(int inputPortIndex);
}
