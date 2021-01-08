package str.rad.synthesizer.gui.diagram.ifaces;

import str.rad.synthesizer.gui.diagram.panels.MovableJPanel;

public interface MovingPanelSetterIFace {
    /**
     * Sets the panel which is currently being moved. So this method should be called when move state of any movable panel changed.
     *
     * @param movedPanel is the panel currently in movement. So if started dragging, we set it to the dragged panel, if stopped we set it to null.
     */
    void setCurrentlyMovingPanel(MovableJPanel movedPanel);
}
