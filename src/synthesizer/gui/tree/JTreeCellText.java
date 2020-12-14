package synthesizer.gui.tree;

import javax.swing.*;
import java.util.List;

abstract public class JTreeCellText extends JLabel implements JTreeCellClickedCallbackIFace {
    public JTreeCellText(String text) {
        super(text);
    }

    @Override
    public void clickCallback() {
        // EMPTY
    }

    @Override
    abstract public List<JTreeCellClickedCallbackIFace> getChildren();
}
