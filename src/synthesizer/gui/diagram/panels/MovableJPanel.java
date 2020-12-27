package synthesizer.gui.diagram.panels;

import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.ifaces.MaxElevationGetterIFace;
import synthesizer.gui.diagram.panels.ifaces.LockUpdateIFace;
import synthesizer.gui.diagram.panels.ifaces.MovablePanelIFace;
import synthesizer.gui.diagram.panels.ifaces.MovablePanelSpecificGetMethodsIFace;
import synthesizer.gui.diagram.panels.ifaces.MovablePanelViewForPort;
import synthesizer.gui.diagram.panels.mouse.MovableJPanelMouseAdapter;
import synthesizer.gui.diagram.panels.util.color.ColorMover;
import synthesizer.gui.diagram.panels.util.color.HSB;
import synthesizer.gui.diagram.panels.port.util.PortChooser;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.gui.diagram.panels.port.OutputPort;
import synthesizer.gui.diagram.panels.port.Port;
import synthesizer.gui.diagram.panels.shape.internals.ShapedPanelInternals;
import synthesizer.UnitViewForGUIIFace;
import player.AudioPlayerPanel;
import synthesizer.gui.diagram.panels.util.Direction;
import util.logging.MyLogger;
import util.swing.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class MovableJPanel extends MovableJPanelBase implements MovablePanelIFace,
                                                                LockUpdateIFace, MovablePanelViewForPort {
    public boolean getIsCurrentlyConnecting() {
        return diagramPanel.getCurrentlyConnectingPanel() == this;
    }

    public boolean getIsAnyPanelCurrentlyConnecting() {
        return diagramPanel.getIsAnyPanelCurrentlyConnecting();
    }


    @Override
    public boolean getIsBeingMoved() {
        return diagramPanel.getCurrentlyMovingPanel() == this;
    }

    public boolean getIsAnyPanelInMotion() {
        return diagramPanel.getIsAnyPanelCurrentlyMoving();
    }

    private boolean getIsCurrentlyBeingConnectingTo() {
        return this == diagramPanel.getPanelCurrenlyConnectingTo();
    }

    public boolean getIsPanelCurrentlyUsed() {
        return getIsBeingMoved() || getIsCurrentlyConnecting() || getIsCurrentlyBeingConnectingTo();
    }

    public void changeMoveState() {
        if (getIsBeingMoved()) {      // Ended dragging
            diagramPanel.panelMovementEnded();
        }
        else {                      // Started dragging
            diagramPanel.panelMovementStarted(this);
        }
    }

    private boolean isInsideStaticPanel = false;
    private boolean isInCollision;
    private ColorMover collisionColorMover;
    private ColorMover noCollisionColorMover;

    /**
     * It says how many static rectangles it is from the reference panel.
     * For example (-1,0) if it is next to the reference on left.
     */
    private Point relativePosReferencePanel;

    @Override
    public Point getRelativePosToReferencePanel() {
        return relativePosReferencePanel;
    }

    public void setRelativePosToReferencePanel(int x, int y) {
        relativePosReferencePanel.x = x;
        relativePosReferencePanel.y = y;
    }

    private Point relativePosBeforeDragging = new Point();

    /**
     * This constructor should be called when trying to add unit from menu
     *
     * @param diagramPanel
     */
    public MovableJPanel(DiagramPanel diagramPanel, UnitViewForGUIIFace unit) {
        // I just put it somewhere far away, so it isn't seen when user starts dragging new unit from the menu
        this(-10000, -10000, diagramPanel.getReferencePanelWidth(),
             diagramPanel.getReferencePanelHeight(), diagramPanel, unit);
    }

    public MovableJPanel(int relativeX, int relativeY, int w, int h, DiagramPanel diagramPanel,
                         UnitViewForGUIIFace unit) {
        this(relativeX, relativeY, diagramPanel, unit);
        Dimension d = new Dimension(w, h);
        this.setSize(d);
        this.setPreferredSize(d);
    }

    public MovableJPanel(int relativeX, int relativeY, DiagramPanel diagramPanel,
                         UnitViewForGUIIFace unit) {
        super(diagramPanel);
        this.unit = unit;
        super.setSize(0, 0);

        float[] defaultHSBFloat = new float[3];
        Color color = getDefaultColor();
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), defaultHSBFloat);
        defaultHSB = new double[3];
        for (int i = 0; i < defaultHSB.length; i++) {
            defaultHSB[i] = defaultHSBFloat[i];
        }

        isInCollision = false;
        isInsideDiagramPanel = false;
        setInputPortsFontSizes();


        relativePosReferencePanel = new Point(relativeX, relativeY);
        correctPositionBasedOnRefPosition();

        panelMouseAdapter = new MovableJPanelMouseAdapter(this, this::panelMovementTimerEventCallback,
                                                          this::connectorOutputTimerEventCallback, this::connectorInputTimerEventCallback);
        this.addMouseListener(panelMouseAdapter);
        this.addMouseMotionListener(panelMouseAdapter);
        this.addMouseWheelListener(panelMouseAdapter);

        resetColor();
        // I set the WHITE_H as ALMOST_RED_H because hue doesn't change whiteness
        collisionColorMover = new ColorMover(hsb, ALMOST_RED_H, ALMOST_WHITE_S, ALMOST_WHITE_B,
                                             ALMOST_RED_H, ALMOST_RED_S, ALMOST_RED_B,
                                             MovableJPanelMouseAdapter.STEP_COUNT);
        // I set the WHITE_H as GREEN_H because hue doesn't change whiteness
        noCollisionColorMover = new ColorMover(hsb, GREEN_H, ALMOST_WHITE_S, ALMOST_WHITE_B,
                                               GREEN_H, GREEN_S, GREEN_B, MovableJPanelMouseAdapter.STEP_COUNT);

        inputPanelColorMoverCollision = new ColorMover(hsb, GREEN_H, ALMOST_WHITE_S, ALMOST_WHITE_B,
                                                       GREEN_H, GREEN_S, GREEN_B, MovableJPanelMouseAdapter.STEP_COUNT);
        inputPanelColorMoverNoCollision = new ColorMover(hsb, ALMOST_RED_H, ALMOST_WHITE_S, ALMOST_WHITE_B,
                                                         ALMOST_RED_H, ALMOST_RED_S, ALMOST_RED_B,
                                                         MovableJPanelMouseAdapter.STEP_COUNT);
        outputPanelColorMover = new ColorMover(hsb, BLUE_H, ALMOST_WHITE_S, ALMOST_WHITE_B,
                                               BLUE_H, BLUE_S, BLUE_B, MovableJPanelMouseAdapter.STEP_COUNT);

        this.setLayout(null);
    }

    private UnitViewForGUIIFace unit;

    public UnitViewForGUIIFace getUnit() {
        return unit;
    }

    private boolean isInsideDiagramPanel;

    @Override
    public void noteAdditionToDiagram() {
        isInsideDiagramPanel = true;
    }

    @Override
    public MaxElevationGetterIFace getClassWithMaxElevationInfo() {
        return diagramPanel;
    }

    @Override
    public int getIndexInPanelList() {
        return diagramPanel.getIndexInPanelList(unit);
    }

    @Override
    public void connectToPort(int targetPanelIndexInPanelList, int targetConnectorIndex) {
        diagramPanel.connectOutputPortToInputPort(targetPanelIndexInPanelList, targetConnectorIndex,
                                                  this.getOutputPort());
    }

    public Point getLocOnScreen() {
        Point loc = getLocation();
        loc = new Point(loc);
        SwingUtilities.convertPointToScreen(loc, getDiagramPanel());
        return loc;
    }


    @Override
    public InputPort[] getInputPorts() {
        return unit.getInputPorts();
    }

    @Override
    public int getInputPortsCount() {
        return getInputPorts().length;
    }

    @Override
    public InputPort getInputPort(int inputPortIndex) {
        return unit.getInputPort(inputPortIndex);
    }


    @Override
    public OutputPort getOutputPort() {
        return unit.getOutputPort();
    }


    private ColorMover outputPanelColorMover;
    private ColorMover inputPanelColorMoverCollision;
    private ColorMover inputPanelColorMoverNoCollision;
    private MovableJPanelMouseAdapter panelMouseAdapter;

    /**
     * Either from menu or by copying
     */
    public void startedAddingUnit() {
        panelMouseAdapter.startDragging();
    }

    public void endedDraggingFromOutsideMovablePanel() {
        panelMouseAdapter.stopDragging();
    }

    public void noConnectionCallback() {
        panelMouseAdapter.stopCurrentConnecting();
        diagramPanel.setCurrentlyConnectingPanel(null);
    }

    @Override
    public void stopCurrentInputConnecting() {
        panelMouseAdapter.stopCurrentInputConnecting();
    }

    public void stopCurrentConnecting() {
        panelMouseAdapter.stopCurrentConnecting();
    }

    public void startedConnecting() {
        diagramPanel.setCurrentlyConnectingPanel(this);
    }

    public void connectMovingPanelToThisPanel() {
        if (!diagramPanel.checkIfConnectionValid(this)) {
            diagramPanel.stopConnecting();
        }
        else {
            InputPort selectedInputPort = getPortUsingDialog(
                    () -> {
                        InputPort portChooserResult = PortChooser.choosePort(this,
                                                                             (ip) -> {
                                                                                 MovablePanelSpecificGetMethodsIFace panelContainingOutputPort;
                                                                                 panelContainingOutputPort = diagramPanel.getCurrentlyConnectingPanel();
                                                                                 OutputPort outputPort = panelContainingOutputPort.getOutputPort();
                                                                                 List<Port> connectedPorts = outputPort.getConnectedPorts();
                                                                                 return !connectedPorts.contains(ip);
                                                                             }
                        );

                        return portChooserResult;
                    }
            );

            if (selectedInputPort != null) {
                diagramPanel.connectCurrentlySelectedPanelToGivenPort(selectedInputPort);
            }
            else {
                diagramPanel.stopConnecting();
            }
        }
    }

    private interface DialogActionIFace {
        /**
         * @return Returns chosen port
         */
        InputPort dialogAction();
    }

    private InputPort getPortUsingDialog(DialogActionIFace actionIFace) {
        diagramPanel.focusLost();
        InputPort selectedInputPort = actionIFace.dialogAction();
        diagramPanel.focusGained();

        return selectedInputPort;
    }

    /**
     * Shows dialog where input ports connected to the output port can be chosen
     *
     * @param op
     * @return
     */
    public InputPort getPortUsingDialog(OutputPort op) {
        diagramPanel.focusLost();
        InputPort selectedInputPort = PortChooser.choosePort(this,
                                                             (ip) -> {
                                                                 List<Port> connectedPorts = op.getConnectedPorts();
                                                                 return connectedPorts.contains(ip);
                                                             });

        diagramPanel.focusGained();
        return selectedInputPort;
    }


    public void mouseExitedPanel() {
        diagramPanel.setPanelCurrentlyConnectingTo(null);
    }


    @Override
    public void recalculateCablesAbsolutePaths(Point referencePanelLoc, Dimension panelSize, int borderWidth,
                                               int borderHeight, int panelSizeWithBorderWidth,
                                               int panelSizeWithBorderHeight, int pixelsPerElevation) {
        unit.getOutputPort().setAbsolutePaths(referencePanelLoc, panelSize, borderWidth, borderHeight,
                                              panelSizeWithBorderWidth, panelSizeWithBorderHeight, pixelsPerElevation);
    }


    private HSB hsb;
    private Color drawColor;

    public Color getDrawColor() {
        return drawColor;
    }

    private static final double BLUE_H = 0.6;
    private static final double BLUE_S = 1;
    private static final double BLUE_B = 0.45;


    private static final double GREEN_H = 0.4;
    private static final double GREEN_S = 1;
    private static final double GREEN_B = 0.45;

    private static final double RED_H = 0;
    private static final double RED_S = 1;
    private static final double RED_B = 1;
    private static final double ALMOST_RED_H = 0;
    private static final double ALMOST_RED_S = 1;          // Can play with this parameter, but it seems ideal
    private static final double ALMOST_RED_B = 0.7;        // Can play with this parameter, but it seems ideal


    private static final double WHITE_H = 0;
    private static final double WHITE_S = 0;   //
    private static final double WHITE_B = 1;   // These 2 make the white
    private static final double ALMOST_WHITE_H = 0;
    private static final double ALMOST_WHITE_S = 0.2;      // Can play with this parameter, but it seems ideal
    private static final double ALMOST_WHITE_B = 0.8;      // Can play with this parameter, but it seems ideal

    private static final double BLACK_H = 0;
    private static final double BLACK_S = 0;
    private static final double BLACK_B = 0;   // 0 makes the black

    private static final Color BLACK_COLOR = Color.getHSBColor((float) BLACK_H, (float) BLACK_S, (float) BLACK_B);


    /**
     * Should be overridden if I don't wait it to be transparent.
     *
     * @return
     */
    public Color getDefaultColor() {
        return diagramPanel.DEFAULT_COLOR;
    }

    private double[] defaultHSB;

    private HSB getDefaultColorHSB() {
        return new HSB(defaultHSB[0], defaultHSB[1], defaultHSB[2]);
    }


    public void resetColor() {
        hsb = getDefaultColorHSB();
        drawColor = getDefaultColor();
    }

    private void setColorBasedOnHSB() {
        drawColor = Color.getHSBColor((float) hsb.h, (float) hsb.s, (float) hsb.b);
    }


    public void startedDragging() {
        changeMoveState();
        relativePosBeforeDragging.x = relativePosReferencePanel.x;
        relativePosBeforeDragging.y = relativePosReferencePanel.y;
        diagramPanel.repaint();
    }

    public void stoppedDragging() {
        changeMoveState();
        collisionColorMover.reset();
        noCollisionColorMover.reset();
        resetColor();
        if (isInCollision) {
            resetToStatePositionBeforeDragging();
            isInCollision = false;
        }
        else {
            isInsideStaticPanel = diagramPanel.lockMovablePanel(this, this.getX(), this.getY());
            if (!isInsideStaticPanel) {
                resetToStatePositionBeforeDragging();
            }
            else {
                diagramPanel.panelLocationChanged(this);
                if (!isInsideDiagramPanel) {
                    diagramPanel.makeTemporaryPanelPermanent(this);
                }
            }
        }
        isInsideStaticPanel = false;

        diagramPanel.recalculateAllCables();
        diagramPanel.repaint();
    }


    @Override
    public void updateSize(Dimension newSize) {
        setSize(newSize);
        if (getInputPorts().length > 0) {
            InputPort biggestPort = findBiggestPort();
            setInputPortFontSizeApproximation(biggestPort, newSize);
            setInputPortsFontSizes(biggestPort);
            setInputPortsLocs();
        }
    }


    @Override
    public void updateLocation(int updateXVal, int updateYVal) {
        Point loc = this.getLocation();
        super.setLocation(loc.x + updateXVal, loc.y + updateYVal);
        OutputPort outputPort = unit.getOutputPort();
        if (outputPort != null) {
            outputPort.moveCables(updateXVal, updateYVal);
        }
        moveInputPorts(updateXVal, updateYVal);
    }

    @Override
    public void updateXandYWhenZooming(int difX, int difY) {
        Point loc = this.getLocation();
        super.setLocation(loc.x + difX, loc.y + difY);
    }

    @Override
    public void updateX(int updateVal) {
        Point loc = this.getLocation();
        super.setLocation(loc.x + updateVal, loc.y);
        moveInputPorts(updateVal, 0);
        OutputPort op = unit.getOutputPort();
        op.moveCablesX(updateVal);
    }

    @Override
    public void updateY(int updateVal) {
        Point loc = this.getLocation();
        super.setLocation(loc.x, loc.y + updateVal);
        moveInputPorts(0, updateVal);
        OutputPort op = unit.getOutputPort();
        op.moveCablesY(updateVal);
    }

    // TODO: RML
    // I have to also update the cables
    // TODO: Possible optimization, when I call this method on zoom, I don't have to calculate the cable locations
    // since I have to recalculate them from scratch anyways
// TODO: RML
    @Override
    public void setLocation(int x, int y) {
        updateLocation(x - this.getX(), y - this.getY());
    }

    @Override
    public void correctPositionBasedOnRefPosition() {
        diagramPanel.moveToPosBasedOnRelativeToRefPanel(this);
    }

    @Override
    public void resetToStatePositionBeforeDragging() {
        if (isInsideDiagramPanel) {
            relativePosReferencePanel.x = relativePosBeforeDragging.x;
            relativePosReferencePanel.y = relativePosBeforeDragging.y;
            correctPositionBasedOnRefPosition();
        }
        else {
            diagramPanel.incorrectTemporaryPanelPosition(this);
        }
    }


    @Override
    public void mouseLocationChangedWithoutMouseMovement() {
        oldMouseLoc = null;
    }

    private Point oldMouseLoc = null;

    public void panelMovementTimerEventCallback() {
        if (getIsBeingMoved()) {
            // Move the panel
            Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(mouseLoc, diagramPanel);

            if (!mouseLoc.equals(oldMouseLoc)) {
                boolean collision = isValidPanelPlacement(mouseLoc.x, mouseLoc.y);
                if (!collision) {
                    setLocation(mouseLoc);
                }

                oldMouseLoc = mouseLoc;
            }


            // Change color
            if (isInsideStaticPanel) {
                if (isInCollision) {
                    hsb = collisionColorMover.moveOneStep();
                }
                else {
                    hsb = noCollisionColorMover.moveOneStep();
                }
                setColorBasedOnHSB();
            }
            else {
                resetColor();
            }
            diagramPanel.repaint();
        }
    }

    public void connectorOutputTimerEventCallback() {
        // Change color
        hsb = outputPanelColorMover.moveOneStep();
        setColorBasedOnHSB();
        diagramPanel.repaint();
    }

    public void connectorInputTimerEventCallback() {
        // Check where it is currently showing
        diagramPanel.setPanelCurrentlyConnectingTo(this);

        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouseLoc, diagramPanel);
        if (getInputPortsCount() > 0 && checkIfThisPanelIsValidConnectorInput()) {
            hsb = inputPanelColorMoverCollision.moveOneStep();
        }
        else {
            hsb = inputPanelColorMoverNoCollision.moveOneStep();
        }
        setColorBasedOnHSB();
        diagramPanel.repaint();
    }

    private boolean checkIfThisPanelIsValidConnectorInput() {
        Point p = this.getRelativePosToReferencePanel();
        int y = diagramPanel.getCurrentlyConnectingPanel().getRelativePosToReferencePanel().y;
        return y < p.y;
    }


    public boolean isValidPanelPlacement(int x, int y) {
        boolean isInsideStaticPanel = checkForCollisions(x, y);

        List<Port> connectedPorts;
        // Check if the output port of this panel is above all the panels to which it connects.
        if (!isInCollision) {
            OutputPort outputPort = unit.getOutputPort();
            connectedPorts = outputPort.getConnectedPorts();
            areOutputConnectionsOk(connectedPorts);
        }
        // Check if all the input port of this panel is below ale the output ports of panels connecting to this.
        InputPort[] inputPorts = unit.getInputPorts();
        int i = 0;
        while (!isInCollision && i < inputPorts.length) {
            isInputConnectionOk(inputPorts[i].getConnectedPort());
            i++;
        }

        return isInsideStaticPanel;
    }

    private void areOutputConnectionsOk(List<Port> connectedPorts) {
        for (Port p : connectedPorts) {
            Point relativePos = p.getPanelWhichContainsPort().getRelativePosToReferencePanel();
            if (relativePos.y <= getRelativePosToReferencePanel().y) {
                isInCollision = true;
            }
        }
    }

    private void isInputConnectionOk(Port connectedPort) {
        if (connectedPort != null) {
            Point relativePos = connectedPort.getPanelWhichContainsPort().getRelativePosToReferencePanel();
            if (relativePos.y >= getRelativePosToReferencePanel().y) {
                isInCollision = true;
            }
        }
    }

    // TODO: RML
    // TODO: Optimalizace, jakmile mam kolizi tak se mi jen staci divat jestli jsem furt v ni, kdyz ne tak zavolama tuhle metodu, kdyz jo tak nic
    // TODO: RML
    private boolean checkForCollisions(int x, int y) {
        isInsideStaticPanel = diagramPanel.lockMovablePanel(this, x, y);
        isInCollision = diagramPanel.checkForCollisions(this, relativePosReferencePanel.x, relativePosReferencePanel.y);
        return isInsideStaticPanel;
    }

    @Override
    public void removePanel() {
        diagramPanel.remove(this);
    }

    @Override
    public void copyPanel() {
        unit.copyPanel();
    }

    @Override
    public void removeInput() {
        InputPort selectedInputPort = getPortUsingDialog(

                () -> {
                    return PortChooser.choosePort(this, (ip) -> ip.getConnectedPort() != null);
                }

        );

        if (selectedInputPort != null) {
            Port op = selectedInputPort.getConnectedPort();
            op.removePort(selectedInputPort);
        }
        // else just exit - no removing will be done
    }

    @Override
    public void removeInputs() {
        for (InputPort ip : unit.getInputPorts()) {
            ip.removeAllPorts();
        }

        diagramPanel.revalidate();
        diagramPanel.repaint();
    }


    @Override
    public void removeOutputs() {
        unit.getOutputPort().removeAllPorts();

        diagramPanel.revalidate();
        diagramPanel.repaint();
    }

    @Override
    public final boolean getIsOutputPanel() {
        return unit.getIsOutputUnit();
    }

    @Override
    public void openPropertiesPanel() {
        if (unit.hasProperties()) {
            AudioPlayerPanel.loadPluginParameters(unit.getPropertiesPanel(), false);
            unit.updateAfterPropertiesCall();
        }
    }


    @Override
    public boolean hasPropertiesPanel() {
        return unit.hasProperties();
    }

    @Override
    public boolean hasInputPorts() {
        return unit.hasInputPorts();
    }


    public boolean getIsPointInsideShape(Point2D p) {
        return true;
    }

    /**
     * Called when resized. Don't forget to call method reshapeInternals to reshape the internals. Abstract in
     * ShapedPanel, needs to overridden in derived classes.
     */
    public void reshape(Dimension newSize) {
        // EMPTY
    }

    public ShapedPanelInternals getInternals() {
        return null;
    }

    /**
     * Should be overridden for different shapes.
     * Gets the vertical distance from the rectangle borders of the panel for given x.
     * (Used when having elevation at start - so I have to get the start of the cable so it touches the shape).
     *
     * @param x is distance from the mid of the panel.
     * @return
     */
    @Override
    public int getDistanceFromRectangleBorders(int x) {
        return 0;
    }


    @Override
    public void getNextToLastPoint(Point nextToLastPoint, int connectorIndex) {
        getNextToLastPoint(nextToLastPoint, connectorIndex, unit.getInputPortsLen());
    }

    @Override
    public void getNextToLastPoint(Point nextToLastPoint, InputPort inputPort) {
        getNextToLastPoint(nextToLastPoint, inputPort.CONNECTOR_INDEX);
    }


    @Override
    public void getNextToLastPoint(Point nextToLastPoint, int connectorIndex, int connectorCount) {
        getLastPoint(nextToLastPoint, connectorIndex, connectorCount);
        nextToLastPoint.y = 0;
    }
// TODO: RML
// It isn't wrong, but the variant above just produces better result for rectangle panels (used as generators)
//    @Override
//    public void getNextToLastPoint(Point nextToLastPoint, int connectorIndex, int connectorCount) {
//        getLastPoint(nextToLastPoint, connectorIndex, connectorCount);
//        Direction direction = getDirectionForInputPortLabel(connectorIndex, connectorCount);
//        switch (direction) {
//            case LEFT:
//                nextToLastPoint.x = nextToLastPoint.y;
//                nextToLastPoint.y = -1;
//                break;
//            case UP:
//                nextToLastPoint.y = 0;
//                break;
//            case RIGHT:
//                nextToLastPoint.x = nextToLastPoint.y;
//                nextToLastPoint.y = 1;
//                break;
//        }
//    }
// TODO: RML


    @Override
    public void getLastPoint(Point p, int connectorIndex, int connectorCount) {
        p.x = MovableJPanel.calculateXForHorizontalLineWithEdges(0, this.getWidth(), connectorIndex, connectorCount);
        p.y = 0;

        convertRelativePixelToAbsolute(p);
    }


    public static int calculateXForHorizontalLineWithEdges(int startX, int endX, int connectorIndex, int connectorCount) {
        int jump = calculateXJumpOnHorizontalLineWithEdges(startX, endX, connectorCount);
        int x = (connectorIndex + 1) * jump;
        return startX + x;
    }

    public static int calculateXJumpOnHorizontalLineWithEdges(int startX, int endX, int connectorCount) {
        int w = endX - startX;
        int jump = w / (connectorCount + 1);    // + 1 because I want to have edges
        return jump;
    }


    public static int calculateXForHorizontalLineWithoutEdges(int startX, int endX, int connectorIndex, int connectorCount) {
        int jump = calculateXJumpOnHorizontalLineWithoutEdges(startX, endX, connectorCount);
        int x = connectorIndex * jump;
        return startX + x;
    }

    public static int calculateXJumpOnHorizontalLineWithoutEdges(int startX, int endX, int connectorCount) {
        int w = endX - startX;
        int jump = w / connectorCount;
        return jump;
    }


    public void convertRelativePixelToAbsolute(Point p) {
        p.x += this.getX();
        p.y += this.getY();
    }


    @Override
    public Point getLastPoint(int connectorIndex) {
        return getLastPoint(connectorIndex, unit.getInputPortsLen());
    }

    @Override
    public void getLastPoint(Point lastPoint, int connectorIndex) {
        getLastPoint(lastPoint, connectorIndex, unit.getInputPortsLen());
    }


    @Override
    public Direction getDirectionForInputPortLabel(InputPort ip) {
        return getDirectionForInputPortLabel(ip.CONNECTOR_INDEX, unit.getInputPortsLen());
    }

    @Override
    public Direction getDirectionForInputPortLabel(int connectorIndex, int connectorCount) {
        if (connectorIndex < connectorCount / 2) {
            return Direction.LEFT;
        }
        else {
            return Direction.RIGHT;
        }
    }


    public void setInputPortsLocs() {
        for (InputPort ip : unit.getInputPorts()) {
            setInputPortLoc(ip);
        }
    }

    public void setInputPortLoc(InputPort ip) {
        Point p = getLastPoint(ip.CONNECTOR_INDEX);
        JLabel label = ip.getPortLabel();
        int cableThickness = (int) diagramPanel.calculateCableThickness();
        cableThickness++;       // So we have some reserve
        cableThickness /= 2;        // Because the last point is in the middle of the cable
        int lw = label.getWidth();
        int lh = label.getHeight();
        Direction lastLineDirection = getDirectionForInputPortLabel(ip);
        switch (lastLineDirection) {
            case LEFT:
                // Put it above the cable
                label.setLocation(p.x - lw - cableThickness, p.y - lh - cableThickness);
                break;
            case UP:
                label.setLocation(p.x - lw - 2 * cableThickness, p.y - lh - cableThickness);
                break;
            case RIGHT:
                label.setLocation(p.x + cableThickness, p.y - lh - cableThickness);
                break;
            case DOWN:
            default:
                MyLogger.logWithoutIndentation("Invalid direction inside method setInputPortLoc.");
                System.exit(4987);
        }
    }


    /**
     * Just approximate the font, so the real finding isn't that slow
     *
     * @param ip
     * @param newSize
     */
    private void setInputPortFontSizeApproximation(InputPort ip, Dimension newSize) {
        JLabel label = ip.getPortLabel();
        SwingUtils.setFontSize(label, diagramPanel.getReferencePanelOldWidth(), newSize.width);
    }


    public void setInputPortsFontSizes() {
        setInputPortsFontSizes(findBiggestPort());
    }

    public void setInputPortsFontSizes(InputPort biggestPort) {
        Dimension d = new Dimension();
        JLabel minLabel = null;
        if (biggestPort != null) {
            setInputPortFontSize(biggestPort, d);
            minLabel = biggestPort.getPortLabel();
        }

        for (InputPort ip : unit.getInputPorts()) {
            JLabel label = ip.getPortLabel();
            label.setFont(minLabel.getFont());
            label.setSize(label.getPreferredSize());
        }
    }


    private InputPort findBiggestPort() {
        int maxWidth = Integer.MIN_VALUE;
        InputPort biggestPort = null;
        InputPort[] inputPorts = getInputPorts();
        for (int i = 0; i < inputPorts.length; i++) {
            int w = inputPorts[i].getPortLabel().getPreferredSize().width;
            if (maxWidth < w) {
                maxWidth = w;
                biggestPort = inputPorts[i];
            }
        }

        return biggestPort;
    }


    /**
     * @param availableSize Just needs to be not null, it behaves as parameter which is used for calculation as temporary storage
     * @param ip
     * @return Returns the new label font size.
     */
    public int setInputPortFontSize(InputPort ip, Dimension availableSize) {
        JLabel label = ip.getPortLabel();
        calculateAvailableLabelSize(availableSize, ip.CONNECTOR_INDEX, unit.getInputPortsLen());
        SwingUtils.findBiggestFontToFitSize(label, availableSize.width, availableSize.height);
        label.setSize(label.getPreferredSize());
        return label.getFont().getSize();
    }


    @Override
    public void calculateAvailableLabelSize(Dimension availableSize, int connectorIndex, int connectorCount) {
        int w = diagramPanel.getReferencePanelWidth();
        int startDistFromCable = 3 * calculateXJumpOnHorizontalLineWithEdges(0, w, connectorCount) / 8;
        int endX = w - connectorCount * ((int) diagramPanel.calculateCableThickness() + startDistFromCable);
        availableSize.width = calculateXJumpOnHorizontalLineWithEdges(0, endX, connectorCount);
        availableSize.height = availableSize.width;
    }

    @Override
    public Dimension calculateAvailableLabelSize(InputPort ip) {
        Dimension d = new Dimension();
        calculateAvailableLabelSize(d, ip.CONNECTOR_INDEX, unit.getInputPortsLen());
        return d;
    }


    public void moveInputPorts(int xMovement, int yMovement) {
        for (InputPort ip : unit.getInputPorts()) {
            JLabel label = ip.getPortLabel();
            Point oldLoc = label.getLocation();
            label.setLocation(oldLoc.x + xMovement, oldLoc.y + yMovement);
        }
    }
}
