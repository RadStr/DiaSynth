package DiagramSynthPackage.GUI.MovablePanelsPackage;

public interface MovablePanelControlMethodsIFace {
    void removePanel();
    void copyPanel();

    void removeInput();
    void removeInputs();

    void removeOutputs();

    boolean getIsOutputPanel();

    void openPropertiesPanel();
    boolean hasPropertiesPanel();

    boolean hasInputPorts();
    int getInputPortsCount();
}

