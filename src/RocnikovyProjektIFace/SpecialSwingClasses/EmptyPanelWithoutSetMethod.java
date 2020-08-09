package RocnikovyProjektIFace.SpecialSwingClasses;

import javax.swing.*;
import java.awt.*;

/**
 * Panel which has size 0,0. or value set in constructor. And can't be changed
 */
public class EmptyPanelWithoutSetMethod extends JPanel {
    public EmptyPanelWithoutSetMethod() {
        size = new Dimension(0,0);
    }

    public EmptyPanelWithoutSetMethod(Dimension dim) {
        this(dim.width, dim.height);
    }

    public EmptyPanelWithoutSetMethod(int w, int h) {
        size = new Dimension(w, h);
    }

    private final Dimension size;

    @Override
    public Dimension getPreferredSize() {
        return size;
    }
    @Override
    public Dimension getMinimumSize() {
        return size;
    }
    @Override
    public Dimension getMaximumSize() {
        return size;
    }
    @Override
    public Dimension getSize() {
        return size;
    }
    @Override
    public Dimension getSize(Dimension d) {
        if(d == null) {
            d = new Dimension(0, 0);
        }
        else {
            d.width = 0;
            d.height = 0;
        }
        return d;
    }


    @Override
    public void setPreferredSize(Dimension d) { }
    @Override
    public void setMinimumSize(Dimension d) { }
    @Override
    public void setMaximumSize(Dimension d) { }
    @Override
    public void setSize(Dimension d) { }
}
