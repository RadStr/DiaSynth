package str.rad.synthesizer.gui.diagram.panels.ifaces;

import str.rad.synthesizer.gui.diagram.panels.port.OutputPort;

public interface PortsGetterIFace extends InputPortsGetterIFace {
    OutputPort getOutputPort();
}
