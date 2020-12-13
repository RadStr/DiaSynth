package player;

import javax.swing.*;
import java.awt.*;

// TODO: Vymazat - nepouzivany
@Deprecated
public class JSplitPaneForWaves extends JSplitPane {
    private boolean isSwap = false;
    public boolean getIsSwap() {
        return isSwap;
    }
    public void setIsSwap(boolean val) {
        isSwap = val;
    }

    Dimension minSize = super.getMinimumSize();
    Dimension prefSize = super.getPreferredSize();
    Dimension maxSize = super.getMaximumSize();


    @Override
    public Dimension getPreferredSize() {
        return prefSize;
    }
    @Override
    public void setPreferredSize(Dimension dim) {
        if(!isSwap) {
            this.prefSize = dim;
        }
    }
    @Override
    public Dimension getMinimumSize() {
        return minSize;
    }
    @Override
    public void setMinimumSize(Dimension dim) {
        if(!isSwap) {
            this.minSize = dim;
        }
    }
    @Override
    public Dimension getMaximumSize() {
        return maxSize;
    }
    @Override
    public void setMaximumSize(Dimension dim) {
        if(!isSwap) {
            this.maxSize = dim;
        }
    }

}
