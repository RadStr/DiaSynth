package deprecatedclasses;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Deprecated
public class DebugMenuItem extends JPanel {
    private static int labelNumber = 0;
    private static DebugMenuItem itemWithFocus;

    public static DebugMenuItem getSelectedItem() {
        return itemWithFocus;
    }

    public static void itemDeletedFromList(Object item) {
        if (item == itemWithFocus) {
            itemWithFocus = null;
        }
    }

    public final int panelNumber;
    private final JLabel panelText;

    public DebugMenuItem() {
        panelNumber = labelNumber;
        panelText = new JLabel(Integer.toString(panelNumber));
        labelNumber++;
        this.add(panelText);

        final DebugMenuItem thisPanel = this;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (itemWithFocus == thisPanel) {
                    itemWithFocus = null;
                }
                else {
                    itemWithFocus = thisPanel;
                }
                thisPanel.getParent().repaint();
            }
        });

        this.setBorder(new LineBorder(Color.black, 5));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (itemWithFocus == this) {
            g.setColor(Color.red);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }

//        System.out.println("Component number " + panelNumber + ":\t" + "\t" + this.getPreferredSize() + this.getSize());
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}