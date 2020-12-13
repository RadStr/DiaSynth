package player.SpecialSwingClasses;

import javax.swing.*;
import java.awt.*;

public class JLabelWithLineInMid extends JLabel {
    private int lineLen;

    public JLabelWithLineInMid(int lineLen, String label) {
        super(label);
        this.lineLen = lineLen;
    }



    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.blue);
        int x1 = this.getX() + this.getWidth() - 2;
        //x1 = 0;
        int x2 = x1 + lineLen;
        int y = this.getHeight() / 2;
        g.drawLine(x1, y, x2, y);

        g.drawLine(0, y, this.getWidth(), y);


        g.setColor(Color.black);
        g.drawLine(x1, 0, x1, this.getHeight());


    }
}
