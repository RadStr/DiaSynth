package DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage;

import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.InputPort;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.OutputPort;
import DiagramSynthPackage.Synth.Unit;
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
