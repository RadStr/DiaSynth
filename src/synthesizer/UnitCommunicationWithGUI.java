package synthesizer;

import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.gui.diagram.panels.port.OutputPort;
import synthesizer.synth.Unit;
import player.plugin.ifaces.PluginDefaultIFace;

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
