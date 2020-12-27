package player.popup;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WavePanelPopupMenu extends JPopupMenu {
    public WavePanelPopupMenu(WavePanelPopupMenuActionsIFace actions) {
        copyItem = new JMenuItem("Copy wave");
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actions.copyWave();
            }
        });
        this.add(copyItem);


        pasteWithOverwritingMenu = new JMenu("Paste wave with overwriting");
        pasteWithOverwritingItems = new PasteItem[7];
        setPasteItems(pasteWithOverwritingMenu, pasteWithOverwritingItems, actions::pasteWaveWithOverwriting);
        this.add(pasteWithOverwritingMenu);

        pasteMenu = new JMenu("Paste wave");
        pasteItems = new PasteItem[7];
        setPasteItems(pasteMenu, pasteItems, actions::pasteWave);
        this.add(pasteMenu);


        cutItem = new JMenuItem("Cut wave");
        cutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actions.cutWave();
            }
        });
        this.add(cutItem);


        moveItem = new JMenuItem("Move wave");
        moveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actions.moveWave();

            }
        });
        this.add(moveItem);


        cleanItem = new JMenuItem("Clean wave");
        cleanItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actions.cleanWave();

            }
        });
        this.add(cleanItem);


        removeItem = new JMenuItem("Remove wave");
        removeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actions.removeWave();

            }
        });
        this.add(removeItem);
    }


    private JMenuItem copyItem;
    private JMenuItem cutItem;


    private JMenu pasteWithOverwritingMenu;
    private PasteItem[] pasteWithOverwritingItems;

    private JMenu pasteMenu;
    private PasteItem[] pasteItems;

    private JMenuItem moveItem;

    private JMenuItem cleanItem;

    private JMenuItem removeItem;


    public void setEnabledWithWavePopUpItems(boolean enabled) {
        pasteWithOverwritingMenu.setEnabled(enabled);
        pasteMenu.setEnabled(enabled);
    }


    public static void setPasteItems(JMenu pasteMenu, PasteItem[] pasteItems, PasteItemActionIFace pasteAction) {
        pasteItems[0] = new PasteItem(1);
        pasteItems[1] = new PasteItem(2);
        pasteItems[2] = new PasteItem(5);
        pasteItems[3] = new PasteItem(10);
        pasteItems[4] = new PasteItem(20);
        pasteItems[5] = new PasteItem(50);
        pasteItems[6] = new PasteItem(100);
        for (PasteItem pasteItem : pasteItems) {
            pasteItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pasteAction.paste(pasteItem.getCopyCount());
                }
            });
            pasteMenu.add(pasteItem);
        }
    }
}
