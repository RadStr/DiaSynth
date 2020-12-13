package synthesizer.gui.diagram;

import synthesizer.gui.diagram.port.OutputPort;

public interface PortsGetterIFace extends InputPortsGetterIFace {
    OutputPort getOutputPort();
}
