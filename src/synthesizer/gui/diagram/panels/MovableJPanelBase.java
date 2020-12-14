package synthesizer.gui.diagram.panels;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.ifaces.GetTopLeftIFace;
import synthesizer.gui.diagram.panels.ifaces.UpdateIFace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public abstract class MovableJPanelBase extends JPanel implements UpdateIFace, GetTopLeftIFace {
    public MovableJPanelBase(int absoluteX, int absoluteY, int w, int h, DiagramPanel diagramPanel) {
        this(absoluteX, absoluteY, diagramPanel);
        Dimension d = new Dimension(w, h);
        this.setSize(d);
        this.setPreferredSize(d);
    }

    public MovableJPanelBase(int absoluteX, int absoluteY, DiagramPanel diagramPanel) {
        this(diagramPanel);
        super.setLocation(absoluteX, absoluteY);
    }

    public MovableJPanelBase(DiagramPanel diagramPanel) {
        this.diagramPanel = diagramPanel;
        addZoomListener();
    }

    private void addZoomListener() {
        // Take care of Zooming
        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int wheelMovement = e.getWheelRotation();
                diagramPanel.zoom(wheelMovement);
            }
        });
    }


    protected DiagramPanel diagramPanel;
    public DiagramPanel getDiagramPanel() {
        return diagramPanel;
    }


    @Override
    public void updateSize(Dimension newSize) {
        setSize(newSize);
    }


    @Override
    public abstract void updateX(int updateVal);
    @Override
    public abstract void updateY(int updateVal);


    private Dimension minSize;
    @Override
    public Dimension getMinimumSize() {
        return minSize;
    }
    @Override
    public void setMinimumSize(Dimension val) {
        minSize = val;
    }

    private Dimension prefSize;
    @Override
    public Dimension getPreferredSize() {
        return prefSize;
    }
    @Override
    public void setPreferredSize(Dimension val) {
        prefSize = val;
    }

    private Dimension maxSize;
    @Override
    public Dimension getMaximumSize() {
        return maxSize;
    }
    @Override
    public void setMaximumSize(Dimension val) {
        maxSize = val;
    }

    @Override
    public int getLeftX() {
        return getX();
    }

    @Override
    public int getTopY() {
        return getY();
    }
}
