package synthesizer.GUI.PanelAroundMovablePanelsPackage;

import synthesizer.GUI.MovablePanelsPackage.Ports.InputPort;
import synthesizer.GUI.MovablePanelsPackage.Ports.OutputPort;
import synthesizer.Synth.Unit;
import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginDefaultIFace;

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
