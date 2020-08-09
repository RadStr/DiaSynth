package DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage.UnitChooser.JTreeUnitChooser;

import javax.swing.*;
import java.awt.*;

public class MenuWithItems extends JScrollPane {

    public MenuWithItems(JPanelWithMovableJPanels panelWithMovablePanels) {
        super(new JTreeUnitChooser(panelWithMovablePanels), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    }

    private final Dimension minSize = new Dimension(0,0);
    @Override
    public Dimension getMinimumSize() {
        return minSize;
    }
}
