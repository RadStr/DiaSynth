package DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage.UnitChooser;

import java.util.List;

public interface JTreeCellClickedCallbackIFace {
    void clickCallback();

    /**
     * Returns null on unit and children otherwise (when called on some subbranch of JTree)
     * @return
     */
    List<JTreeCellClickedCallbackIFace> getChildren();
}