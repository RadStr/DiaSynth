package synthesizer.gui.diagram;

import synthesizer.gui.diagram.panels.port.OutputPort;

public interface PortsGetterIFace extends InputPortsGetterIFace {
    OutputPort getOutputPort();
}
