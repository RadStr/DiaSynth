package deprecatedclasses;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

@Deprecated
public class JSplitPaneDragger implements MouseMotionListener {
    JSplitPane splitter;
    JSplitPane[] splitters;

    public JSplitPaneDragger(JSplitPane splitter, JSplitPane[] splitters) {
        this.splitter = splitter;
        this.splitters = splitters;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int y = e.getY();
//        JComponent bottomComponent = splitter.getBottomComponent();
//        if(y > bottomComponent.getY()) {
//            swapComponents();
//        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Empty
    }
}
