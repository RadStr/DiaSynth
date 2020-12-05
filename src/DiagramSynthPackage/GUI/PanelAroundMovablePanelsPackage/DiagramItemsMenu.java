package DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage.UnitChooser.DiagramUnitsJTree;

import javax.swing.*;
import java.awt.*;

public class DiagramItemsMenu extends JScrollPane {

    public DiagramItemsMenu(JPanelWithMovableJPanels panelWithMovablePanels) {
        super(new DiagramUnitsJTree(panelWithMovablePanels), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    }

    private final Dimension minSize = new Dimension(0,0);
    @Override
    public Dimension getMinimumSize() {
        return minSize;
    }
}
