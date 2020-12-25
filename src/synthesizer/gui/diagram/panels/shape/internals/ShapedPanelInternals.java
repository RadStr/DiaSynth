package synthesizer.gui.diagram.panels.shape.internals;

import java.awt.*;

public interface ShapedPanelInternals {
    void reshape(Dimension newSize);
    void draw(Graphics g);

    /**
     * Should create exact copy of class and return it (the copy should not have any shared variables).
     * Currently not used, but it will be useful once we allow to change names of panels.
     * @return
     */
    ShapedPanelInternals createCopy();
}
