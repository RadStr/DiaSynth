package synthesizer.gui.diagram.panels.ifaces;

import synthesizer.gui.diagram.panels.port.OutputPort;

public interface PortsGetterIFace extends InputPortsGetterIFace {
    OutputPort getOutputPort();
}
