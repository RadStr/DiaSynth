package synthesizer.gui.diagram.panels.mouse;


import synthesizer.gui.diagram.panels.MovableJPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class MovableJPanelMouseAdapter extends MovableJPanelMouseAdapterBase {
    /**
     * @param movablePanel is the panel of which takes this mouse adapter care of.
     */
    public MovableJPanelMouseAdapter(MovableJPanel movablePanel, CallbackIFace sourceCallbackMoving,
                                     CallbackIFace sourceConnectingCallback, CallbackIFace targetConnectingCallback) {
        super(movablePanel, sourceCallbackMoving);

        sourceConnectingTimer = new Timer(DELAY_IN_SECS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sourceConnectingCallback.callback();
            }
        });

        targetConnectingTimer = new Timer(DELAY_IN_SECS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                targetConnectingCallback.callback();
            }
        });
    }


    private Timer sourceConnectingTimer;
    private Timer targetConnectingTimer;

    public void stopCurrentInputConnecting() {
        stopConnection(targetConnectingTimer);
    }

    public void stopCurrentConnecting() {
        stopConnection(sourceConnectingTimer);
    }

    private void startConnecting() {
        movablePanel.startedConnecting();
        sourceConnectingTimer.start();
    }

    private void stopConnection(Timer t) {
        if (t.isRunning()) {
            t.stop();
        }
        movablePanel.resetColor();
        movablePanel.repaint();
    }


    public void stopDragging() {
        sourceTimer.stop();
        movablePanel.stoppedDragging();
    }

    /**
     * Starts the panel dragging
     */
    public void startDragging() {
        movablePanel.startedDragging();
        sourceTimer.start();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (checkIfShouldChangeTimerState()) {
            targetConnectingTimer.start();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        isAlreadyOneClick = false;
        if (checkIfShouldChangeTimerState()) {
            stopCurrentInputConnecting();
        }
        movablePanel.mouseExitedPanel();
    }

    // Modified code from
    // https://stackoverflow.com/questions/4051659/identifying-double-click-in-java/18990721
    private boolean isAlreadyOneClick = false;

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            if (movablePanel.getIsBeingMoved()) {
                stopDragging();
            }
        }
        else {
            if (isAlreadyOneClick && !movablePanel.getUnit().getIsOutputUnit()) {
                isAlreadyOneClick = false;
                stopDragging();
                startConnecting();
            }
            else {
                if (movablePanel.getIsAnyPanelCurrentlyConnecting()) {
                    if (movablePanel.getIsCurrentlyConnecting()) {       // Clicked on the panel which was the output one
                        movablePanel.noConnectionCallback();
                    }
                    else {
                        movablePanel.connectMovingPanelToThisPanel();
                    }
                }
                else if (movablePanel.getIsBeingMoved()) {       // If panel moving, stop moving
                    stopDragging();
                }
                else if (movablePanel.getIsAnyPanelInMotion()) {
                    movablePanel.endedDraggingFromOutsideMovablePanel();
                }
                else if (movablePanel.getIsPointInsideShape(e.getPoint())) {
                    if(!movablePanel.getUnit().getIsOutputUnit()) {
                        isAlreadyOneClick = true;
                    }
                    startDragging();
                }
                else {
                    isDraggingOutsideShape = true;
                }
            }
        }
    }

    private boolean isDraggingOutsideShape = false;

    @Override
    public void mouseReleased(MouseEvent e) {
        if(isDraggingOutsideShape) {
            isDraggingOutsideShape = false;
            MouseEvent convertMouseEvent = SwingUtilities.convertMouseEvent(e.getComponent(), e, movablePanel.getParent());
            movablePanel.getParent().dispatchEvent(convertMouseEvent);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(isDraggingOutsideShape) {
            MouseEvent convertMouseEvent = SwingUtilities.convertMouseEvent(e.getComponent(), e, movablePanel.getParent());
            movablePanel.getParent().dispatchEvent(convertMouseEvent);
        }
    }
}