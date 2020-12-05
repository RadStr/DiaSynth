package DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage;

import DiagramSynthPackage.GUI.MovablePanelsPackage.DiagramPanel;
import DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage.UnitChooser.DiagramUnitsJTree;

import javax.swing.*;
import java.awt.*;

public class DiagramItemsMenu extends JScrollPane {

    public DiagramItemsMenu(DiagramPanel diagramPanel) {
        super(new DiagramUnitsJTree(diagramPanel), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    }

    private final Dimension minSize = new Dimension(0,0);
    @Override
    public Dimension getMinimumSize() {
        return minSize;
    }
}
