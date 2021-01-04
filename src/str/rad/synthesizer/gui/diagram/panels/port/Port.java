package str.rad.synthesizer.gui.diagram.panels.port;

import str.rad.synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import str.rad.synthesizer.synth.UnitGeneratedValuesInfo;

public abstract class Port implements UnitGeneratedValuesInfo {
    protected UnitGeneratedValuesInfo unitValsInfo;
    protected MovablePanelViewForPort panelWhichContainsPort;

    public MovablePanelViewForPort getPanelWhichContainsPort() {
        return panelWhichContainsPort;
    }

    public Port(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort, int connectorIndex) {
        this.CONNECTOR_INDEX = connectorIndex;
        this.panelWhichContainsPort = panelWhichContainsPort;
        this.unitValsInfo = u;
    }

    public final int CONNECTOR_INDEX;


    public final void connectToPort(Port port) {
        connectToPort(port, true);
    }

    public abstract void connectToPort(Port port, boolean connectAlsoOnTheOtherPort);


    /**
     * Removes the given port. Returns index >= 0 of the removed port in the connected ports array.
     * If the port wasn't there then returns -1.
     * Also removes the connection on the other panel (so in case of input port it removes itself from the output port)
     *
     * @param port is the port to be removed
     * @return
     */
    public final int removePort(Port port) {
        return removePort(port, true);
    }

    /**
     * Should be overridden in input and output port.
     *
     * @param port               is the port to be removed
     * @param removeTheOtherPort If true removes the cable completely, if false removes only the port from this instance
     *                           (so on the other side it will still think it is connected to this port)
     * @return
     */
    public abstract int removePort(Port port, boolean removeTheOtherPort);

    public abstract void removeAllPorts();


    /**
     * Abstract method, the implementation usually returns the type of the port where it is created. (InputPort returns InputPort, ...).
     * This method doesn't make that much sense for OutputPort. From logical standpoint.
     *
     * @param unitToContainNewPort  is the unit to which is the port put
     * @param panelToContainNewPort is the panel to which is the port put
     * @return Returns copy of the given port which is associated with the given panel.
     */
    public abstract Port copy(UnitGeneratedValuesInfo unitToContainNewPort,
                              MovablePanelViewForPort panelToContainNewPort);
}
