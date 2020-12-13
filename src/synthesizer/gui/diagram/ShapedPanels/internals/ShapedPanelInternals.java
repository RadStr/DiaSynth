package synthesizer.gui.diagram.ShapedPanels.internals;

import java.awt.*;

public interface ShapedPanelInternals {
    void reshape(Dimension newSize);
    void draw(Graphics g);

    /**
     * Should create exact copy of class and return it (the copy should not have any shared variables)
     * @return
     */
    ShapedPanelInternals createCopy();
}
