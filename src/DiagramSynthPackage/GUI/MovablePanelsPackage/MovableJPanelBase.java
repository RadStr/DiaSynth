package DiagramSynthPackage.GUI.MovablePanelsPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public abstract class MovableJPanelBase extends JPanel implements UpdateIFace, GetTopLeftIFace {
    public MovableJPanelBase(int absoluteX, int absoluteY, int w, int h, JPanelWithMovableJPanels mainPanel) {
        this(absoluteX, absoluteY, mainPanel);
        Dimension d = new Dimension(w, h);
        this.setSize(d);
        this.setPreferredSize(d);
    }

    public MovableJPanelBase(int absoluteX, int absoluteY, JPanelWithMovableJPanels mainPanel) {
        this(mainPanel);
        super.setLocation(absoluteX, absoluteY);
    }

    public MovableJPanelBase(JPanelWithMovableJPanels mainPanel) {
        this.mainPanel = mainPanel;
        addZoomListener();
    }

    private void addZoomListener() {
        // Take care of Zooming
        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int wheelMovement = e.getWheelRotation();
                mainPanel.zoom(wheelMovement);
            }
        });
    }


    protected JPanelWithMovableJPanels mainPanel;
    public JPanelWithMovableJPanels getMainPanel() {
        return mainPanel;
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
