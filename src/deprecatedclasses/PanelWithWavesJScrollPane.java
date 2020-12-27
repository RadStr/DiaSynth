package deprecatedclasses;

import javax.swing.*;
import java.awt.*;


@Deprecated
public class PanelWithWavesJScrollPane extends JScrollPane implements Scrollable {
    public PanelWithWavesJScrollPane(int vsbPolicy, int hsbPolicy) {
        super(vsbPolicy, hsbPolicy);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(200, 200);
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 128;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 128;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
