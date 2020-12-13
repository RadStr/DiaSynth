package synthesizer.gui.PanelAroundMovablePanelsPackage;

import synthesizer.gui.diagram.port.InputPort;
import synthesizer.gui.diagram.port.OutputPort;
import synthesizer.synth.Unit;
import RocnikovyProjektIFace.plugin.ifaces.PluginDefaultIFace;

public interface UnitCommunicationWithGUI {
    /**
     *
     * @return Returns the copy of panel
     */
    Unit copyPanel();
    InputPort[] getInputPorts();
    int getInputPortsLen();
    InputPort getInputPort(int inputPortIndex);
    boolean hasInputPorts();
    OutputPort getOutputPort();
    boolean getIsOutputUnit();

    PluginDefaultIFace getPropertiesPanel();
    default boolean hasProperties() {
        return getPropertiesPanel() != null;
    }
    void updateAfterPropertiesCall();
}
