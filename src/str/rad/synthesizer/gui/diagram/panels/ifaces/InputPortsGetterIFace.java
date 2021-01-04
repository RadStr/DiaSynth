package str.rad.synthesizer.gui.diagram.panels.ifaces;

import str.rad.synthesizer.gui.diagram.panels.port.InputPort;

public interface InputPortsGetterIFace {
    InputPort[] getInputPorts();

    InputPort getInputPort(int inputPortIndex);
}
