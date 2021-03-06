package str.rad.synthesizer.gui.diagram.panels.mouse;

import str.rad.synthesizer.gui.diagram.panels.ifaces.MovablePanelControlMethodsIFace;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MovablePanelPopUpMenu extends JPopupMenu {
    private JMenuItem copyItem;
    private JMenuItem propertiesItem;
    private JMenuItem removeItem;

    private JMenuItem removeInputItem;
    private JMenuItem removeInputsItem;

    private JMenuItem removeOutputsItem;

    public MovablePanelPopUpMenu(MovablePanelControlMethodsIFace movablePanel) {
        if (!movablePanel.getIsOutputPanel()) {
            copyItem = new JMenuItem("Copy");
            copyItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    movablePanel.copyPanel();
                }
            });
            this.add(copyItem);


            removeItem = new JMenuItem("Remove");
            removeItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    movablePanel.removePanel();

                }
            });
            this.add(removeItem);
        }


        if (movablePanel.hasPropertiesPanel()) {
            propertiesItem = new JMenuItem("Properties");
            propertiesItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    movablePanel.openPropertiesPanel();

                }
            });
            this.add(propertiesItem);
        }

        if (movablePanel.hasInputPorts()) {
            removeInputItem = new JMenuItem("Remove input");
            removeInputItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    movablePanel.removeInput();

                }
            });
            this.add(removeInputItem);

            removeInputsItem = new JMenuItem("Remove inputs");
            removeInputsItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    movablePanel.removeInputs();

                }
            });
            this.add(removeInputsItem);
        }


        if (!movablePanel.getIsOutputPanel()) {
            removeOutputsItem = new JMenuItem("Remove outputs");
            removeOutputsItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    movablePanel.removeOutputs();

                }
            });
            this.add(removeOutputsItem);
        }
    }
}
