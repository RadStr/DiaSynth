package str.rad.synthesizer;

import str.rad.plugin.PluginBaseIFace;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.gui.diagram.panels.port.OutputPort;
import str.rad.synthesizer.synth.Unit;

public interface UnitViewForGUIIFace {
    /**
     * @return Returns the copy of panel
     */
    Unit copyPanel();

    InputPort[] getInputPorts();

    int getInputPortsLen();

    InputPort getInputPort(int inputPortIndex);

    boolean hasInputPorts();

    OutputPort getOutputPort();

    boolean getIsOutputUnit();

    PluginBaseIFace getPropertiesPanel();

    default boolean hasProperties() {
        return getPropertiesPanel() != null;
    }

    void updateAfterPropertiesCall();
}
