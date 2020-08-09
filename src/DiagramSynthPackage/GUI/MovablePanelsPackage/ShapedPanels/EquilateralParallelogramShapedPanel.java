package DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals.ShapedPanelInternals;
import DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage.UnitCommunicationWithGUI;

import java.awt.*;

public class EquilateralParallelogramShapedPanel extends ParallelogramShapedPanel {
    /**
     *
     * @param angle is in degrees. It is the left angle at the left bot point. (bot right -> bot left -> top left).
     * Works correctly for angle > 0 && angle < 180. If angle < 0 && angle > -180 then it also works, but probably not the way user intended.
     */
    public EquilateralParallelogramShapedPanel(JPanelWithMovableJPanels mainPanel, int angle,
                                               ShapedPanelInternals internals,
                                               UnitCommunicationWithGUI unit) {
        super(mainPanel, angle, internals, unit);
    }

    /**
     *
     * @param angle is in degrees. It is the left angle at the left bot point. (bot right -> bot left -> top left).
     * Works correctly for angle > 0 && angle < 180. If angle < 0 && angle > -180 then it also works, but probably not the way user intended.
     */
    public EquilateralParallelogramShapedPanel(int relativeX, int relativeY, int w, int h,
                                               JPanelWithMovableJPanels mainPanel, int angle,
                                               ShapedPanelInternals internals,
                                               UnitCommunicationWithGUI unit) {
        super(relativeX, relativeY, w, h, mainPanel, angle, internals, unit);
    }

    /**
     *
     * @param angle is in degrees. It is the left angle at the left bot point. (bot right -> bot left -> top left).
     * Works correctly for angle > 0 && angle < 180. If angle < 0 && angle > -180 then it also works, but probably not the way user intended.
     */
    public EquilateralParallelogramShapedPanel(int relativeX, int relativeY, JPanelWithMovableJPanels mainPanel,
                                               int angle, ShapedPanelInternals internals,
                                               UnitCommunicationWithGUI unit) {
        super(relativeX, relativeY, mainPanel, angle, internals, unit);
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
