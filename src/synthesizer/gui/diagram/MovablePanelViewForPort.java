package synthesizer.gui.diagram;

import synthesizer.gui.diagram.port.InputPort;

import java.awt.*;

public interface MovablePanelViewForPort extends MovablePanelSpecificGetMethodsIFace {
    GetMaxElevationIFace getClassWithMaxElevationInfo();

    int getIndexInPanelList();
    void connectToPort(int targetPanelIndexInPanelList, int targetConnectorIndex);

    /**
     * Doesn't need to be overridden. It is there just for convenience
     * @param p
     * @return
     */
    default Point getLastPoint(InputPort p) {
        return getLastPoint(p.CONNECTOR_INDEX);
    }

    /**
     * Doesn't need to be overridden. It is there just for convenience
     * @param lastPoint
     * @param p
     */
    default void getLastPoint(Point lastPoint, InputPort p) {
        getLastPoint(lastPoint, p.CONNECTOR_INDEX);
    }
}
