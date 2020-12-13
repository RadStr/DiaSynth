package synthesizer.gui.MovablePanelsPackage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public abstract class MovableJPanelMouseAdapterBase extends MouseAdapter {
    public static final int DELAY_IN_SECS = 15;     // Parameter to play with ... Was 30
    public static final int STEP_COUNT = 40;       // Parameter to play with ... Was 20
    protected MovableJPanel movablePanel;
    protected Timer sourceTimer;

    protected MovablePanelPopUpMenu rightClickPopUpMenu;

    /**
     *
     * @param movablePanel is the panel of which takes this mouse adapter care of.
     * @param sourceCallback is the callback which is called on the source. (The output port, the panel moved, etc.)
     */
    public MovableJPanelMouseAdapterBase(MovableJPanel movablePanel, CallbackIFace sourceCallback) {
        this.movablePanel = movablePanel;
        sourceTimer = new Timer(DELAY_IN_SECS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sourceCallback.callback();
            }
        });

        rightClickPopUpMenu = new MovablePanelPopUpMenu(movablePanel);
        movablePanel.setComponentPopupMenu(rightClickPopUpMenu);
    }


    /**
     * Returns true if there is panel currently connecting but it is not the movable panel associated with this listener
     * @return
     */
    public boolean checkIfShouldChangeTimerState() {
        return movablePanel.getIsAnyPanelCurrentlyConnecting() && !movablePanel.getIsCurrentlyConnecting();
    }
}
