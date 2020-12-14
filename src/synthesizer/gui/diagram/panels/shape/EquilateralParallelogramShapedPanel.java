package synthesizer.gui.diagram.panels.shape;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.shape.internals.ShapedPanelInternals;
import synthesizer.gui.UnitCommunicationWithGUI;

import java.awt.*;

public class EquilateralParallelogramShapedPanel extends ParallelogramShapedPanel {
    /**
     *
     * @param angle is in degrees. It is the left angle at the left bot point. (bot right -> bot left -> top left).
     * Works correctly for angle > 0 && angle < 180. If angle < 0 && angle > -180 then it also works, but probably not the way user intended.
     */
    public EquilateralParallelogramShapedPanel(DiagramPanel diagramPanel, int angle,
                                               ShapedPanelInternals internals,
                                               UnitCommunicationWithGUI unit) {
        super(diagramPanel, angle, internals, unit);
    }

    /**
     *
     * @param angle is in degrees. It is the left angle at the left bot point. (bot right -> bot left -> top left).
     * Works correctly for angle > 0 && angle < 180. If angle < 0 && angle > -180 then it also works, but probably not the way user intended.
     */
    public EquilateralParallelogramShapedPanel(int relativeX, int relativeY, int w, int h,
                                               DiagramPanel diagramPanel, int angle,
                                               ShapedPanelInternals internals,
                                               UnitCommunicationWithGUI unit) {
        super(relativeX, relativeY, w, h, diagramPanel, angle, internals, unit);
    }

    /**
     *
     * @param angle is in degrees. It is the left angle at the left bot point. (bot right -> bot left -> top left).
     * Works correctly for angle > 0 && angle < 180. If angle < 0 && angle > -180 then it also works, but probably not the way user intended.
     */
    public EquilateralParallelogramShapedPanel(int relativeX, int relativeY, DiagramPanel diagramPanel,
                                               int angle, ShapedPanelInternals internals,
                                               UnitCommunicationWithGUI unit) {
        super(relativeX, relativeY, diagramPanel, angle, internals, unit);
    }

    @Override
    public void reshape(Dimension newSize) {
        if(newSize.width < newSize.height) {
            newSize = new Dimension(newSize.width, newSize.width);
        }
        else {
            newSize = new Dimension(newSize.height, newSize.height);
        }
        super.reshape(newSize);
    }

}
