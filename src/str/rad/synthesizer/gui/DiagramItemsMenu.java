package str.rad.synthesizer.gui;

import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.tree.DiagramUnitsJTree;

import javax.swing.*;
import java.awt.*;

public class DiagramItemsMenu extends JScrollPane {

    public DiagramItemsMenu(DiagramPanel diagramPanel) {
        super(new DiagramUnitsJTree(diagramPanel), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
              JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    }

    private final Dimension minSize = new Dimension(0, 0);

    @Override
    public Dimension getMinimumSize() {
        return minSize;
    }
}
