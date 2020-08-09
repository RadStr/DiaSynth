package RocnikovyProjektIFace.SpecialSwingClasses;

import javax.swing.*;
import java.awt.*;

public class EmptyPanelWithSetMethod extends JPanel {
    public EmptyPanelWithSetMethod() {
        size = new Dimension(0,0);
    }

    public EmptyPanelWithSetMethod(Dimension dim) {
        this(dim.width, dim.height);
    }

    public EmptyPanelWithSetMethod(int w, int h) {
        size = new Dimension(w, h);
    }

    private Dimension size;

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

    public void setSizeInternal(Dimension d) {
        size = d;
    }
}
