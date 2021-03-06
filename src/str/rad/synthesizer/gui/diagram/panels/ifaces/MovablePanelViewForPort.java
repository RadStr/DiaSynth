package str.rad.synthesizer.gui.diagram.panels.ifaces;

import str.rad.synthesizer.gui.diagram.ifaces.MaxElevationGetterIFace;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;

import java.awt.*;

public interface MovablePanelViewForPort extends MovablePanelSpecificGetMethodsIFace {
    MaxElevationGetterIFace getClassWithMaxElevationInfo();

    int getIndexInPanelList();

    void connectToPort(int targetPanelIndexInPanelList, int targetConnectorIndex);

    /**
     * Doesn't need to be overridden. It is there just for convenience
     *
     * @param p
     * @return
     */
    default Point getLastPoint(InputPort p) {
        return getLastPoint(p.CONNECTOR_INDEX);
    }

    /**
     * Doesn't need to be overridden. It is there just for convenience
     *
     * @param lastPoint
     * @param p
     */
    default void getLastPoint(Point lastPoint, InputPort p) {
        getLastPoint(lastPoint, p.CONNECTOR_INDEX);
    }
}
