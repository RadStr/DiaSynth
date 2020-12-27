package synthesizer.gui;

import java.awt.*;

public class GridBagConstraintsSetter {
    public static void setConstraint(GridBagConstraints c,
                                     int gridx, int gridy, int gridWidth, int gridHeight,
                                     int ipadx, int ipady, double weigthx, double weigthy, int fill) {
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = gridWidth;
        c.gridheight = gridHeight;
        c.ipadx = ipadx;
        c.ipady = ipady;
        c.weightx = weigthx;
        c.weighty = weigthy;
        c.fill = fill;
    }
}
