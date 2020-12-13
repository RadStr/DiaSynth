package synthesizer.gui.PanelAroundMovablePanelsPackage;

import synthesizer.gui.MovablePanelsPackage.port.InputPort;
import synthesizer.gui.MovablePanelsPackage.port.OutputPort;
import synthesizer.synth.Unit;
import RocnikovyProjektIFace.AudioPlayerPlugins.ifaces.PluginDefaultIFace;

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
