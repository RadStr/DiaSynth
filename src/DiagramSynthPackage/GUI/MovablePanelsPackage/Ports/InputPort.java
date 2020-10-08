package DiagramSynthPackage.GUI.MovablePanelsPackage.Ports;

import DiagramSynthPackage.GUI.MovablePanelsPackage.AddInputPortToGUIIFace;
import DiagramSynthPackage.GUI.MovablePanelsPackage.MovablePanelViewForPort;
import DiagramSynthPackage.Synth.Unit;
import DiagramSynthPackage.Synth.UnitGeneratedValuesInfo;
import Rocnikovy_Projekt.Program;

import javax.swing.*;
import java.awt.*;

public class InputPort extends Port {
    private final double[] ARR_WITH_DEFAULT_VALUES;
    public static final double[] ZERO_ARR = new double[Unit.BUFFER_LEN];

    /**
     *
     * @param panelWhichContainsPort
     * @param name is saved to the portLabel label as the label name. (Shouldn't contain ")< br >" ... without the spaces).
     * @param fullName is saved to the portLabel under public field FULL_NAME
     * @param connectorIndex
     * @param addInputPortToGUIIFace
     */
    public InputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort,
                     String name, String fullName, int connectorIndex,
                     AddInputPortToGUIIFace addInputPortToGUIIFace, String labelTooltip,
                     double neutralValue) {
        super(u, panelWhichContainsPort, connectorIndex);
        if(neutralValue == 0) {
            ARR_WITH_DEFAULT_VALUES = ZERO_ARR;
        }
        else {
            ARR_WITH_DEFAULT_VALUES = new double[Unit.BUFFER_LEN];
            Program.setOneDimArr(ARR_WITH_DEFAULT_VALUES, 0, ARR_WITH_DEFAULT_VALUES.length, neutralValue);
        }
        portLabel = new InputPortLabel(name, fullName, this, labelTooltip);
        addInputPortToGUIIFace.addInputPortLabel(portLabel);
    }


    @Override
    public boolean getIsConst() {
        if(connectedPort != null) {
            return connectedPort.getIsConst();
        }
        return true;
    }
    @Override
    public boolean getIsNoiseGen() {
        if(connectedPort != null) {
            return connectedPort.getIsNoiseGen();
        }
        return false;
    }

    @Override
    public double getMaxAbsValue() {
        if(connectedPort != null) {
            return connectedPort.getMaxAbsValue();
        }
        return ARR_WITH_DEFAULT_VALUES[0];
    }
    @Override
    public double getValue(int index) {
        if(connectedPort != null) {
            return connectedPort.getValue(index);
        }
        return ARR_WITH_DEFAULT_VALUES[0];
    }
    @Override
    public double[] getValues() {
        if(connectedPort != null) {
            return connectedPort.getValues();
        }
        return ARR_WITH_DEFAULT_VALUES;
    }

    /**
     * Returns Double.MAX_VALUE if it doesn't have modulation frequency (if it is envelope or operation or noise generator).
     * Or returns its modulation frequency if it is generator.
     *
     * @return
     */
    @Override
    public double getModulationFrequency() {
        if(connectedPort != null) {
            return connectedPort.getModulationFrequency();
        }
        return Double.MAX_VALUE;
    }

    @Override
    /**
     * Returns if the predecessor is binary plus.
     */
    public boolean isBinaryPlus() {
        if(connectedPort != null) {
            return connectedPort.isBinaryPlus();
        }
        return false;
    }

    /**
     * Returns the first constant in input ports or Double.MAX_VALUE if there are no constants on input ports
     *
     * @return
     */
    @Override
    public double getConstant() {
        if(connectedPort != null) {
            return connectedPort.getConstant();
        }
        return Double.MAX_VALUE;
    }

    /**
     * Returns the n-th non-constant in input ports or null if there are no non-constants on input ports.
     * Doesn't work recursively.
     *
     * @return
     */
    @Override
    public double[] getNonConstant(int n) {
        if(connectedPort != null) {
            return connectedPort.getNonConstant(n);
        }
        return null;
    }


    @Override
    public double[] getWaveAmps(int waveIndex) {
        if(connectedPort != null) {
            return connectedPort.getWaveAmps(waveIndex);
        }
        return null;
    }

    @Override
    public double[] getWaveFreqs(int waveIndex) {
        if(connectedPort != null) {
            return connectedPort.getWaveFreqs(waveIndex);
        }
        return null;
    }


    private Port connectedPort = null;
    public Port getConnectedPort() {
        return connectedPort;
    }

    private InputPortLabel portLabel;
    public InputPortLabel getPortLabel() {
        return portLabel;
    }


    @Override
    public void connectToPort(Port port, boolean connectAlsoOnTheOtherPort) {
        if(connectedPort != port) {
            Point p = port.getPanelWhichContainsPort().getRelativePosToReferencePanel();
            int y = panelWhichContainsPort.getRelativePosToReferencePanel().y;
            if (y > p.y) {
                if(connectedPort != null) {
                    removePort(connectedPort);
                }
                connectedPort = port;
                if(connectAlsoOnTheOtherPort) {
                    port.connectToPort(this, false);
                }
            }
        }
    }


    @Override
    public int removePort(Port port, boolean removeTheOtherPort) {
        if(connectedPort == port) {
            connectedPort = null;
            if(removeTheOtherPort) {
                port.removePort(this, false);
            }
            return 0;
        }
        else {
            return -1;
        }
    }


    @Override
    public void removeAllPorts() {
        if(connectedPort != null) {
            removePort(connectedPort);
        }
    }



    /**
     * Copies the fields from the given parameter to the instance on which was the method called.
     * @param copySourcePort is the port from which we will copy.
     */
    public void copyFields(InputPort copySourcePort) {
        this.connectToPort(copySourcePort.connectedPort);
    }

    @Override
    public InputPort copy(UnitGeneratedValuesInfo unitToContainNewPort,
                          MovablePanelViewForPort panelToContainNewPort) {
        InputPort port = new InputPort(unitToContainNewPort, panelToContainNewPort, portLabel.getName(), portLabel.FULL_NAME,
                CONNECTOR_INDEX, panelToContainNewPort.getMainPanel(), portLabel.getAdditionalTooltip(), ARR_WITH_DEFAULT_VALUES[0]);
        port.copyFields(this);
        return port;
    }


    public Point getLastPoint() {
        Point p = new Point();
        getLastPoint(p);
        return p;
    }

    public void getLastPoint(Point lastPoint) {
        panelWhichContainsPort.getLastPoint(lastPoint, CONNECTOR_INDEX);
    }

    public Point getNextToLastPoint() {
        Point nextToLastPoint = new Point();
        panelWhichContainsPort.getNextToLastPoint(nextToLastPoint, CONNECTOR_INDEX);
        return nextToLastPoint;
    }

    public void getNextToLastPoint(Point nextToLastPoint) {
        panelWhichContainsPort.getNextToLastPoint(nextToLastPoint, CONNECTOR_INDEX);
    }




    public static class InputPortLabel extends JLabel {
        public InputPortLabel(String name, String fullName, InputPort inputPort, String additionalToolTip) {
            super(name);
            this.FULL_NAME = fullName;
            TOOL_TIP_PREFIX = "<html>" + FULL_NAME + " (" + name + ")<br>" + additionalToolTip;
            setToolTipText(TOOL_TIP_PREFIX + "</html>");
// TODO: RML
            // TODO: MORE ADVANCED TOOLTIP
//            this.inputPort = inputPort;
//            // Based on https://stackoverflow.com/questions/1660967/why-gettooltiptext-is-never-called
//            ToolTipManager.sharedInstance().registerComponent(this);
            // TODO: MORE ADVANCED TOOLTIP
// TODO: RML
        }

        public final String FULL_NAME;
        public final String TOOL_TIP_PREFIX;

// TODO: RML
// TODO: MORE ADVANCED TOOLTIP
//private final InputPort inputPort;
//        @Override
//        public String getToolTipText() {
//            String tooltip = null;
//            if(inputPort.connectedPort != null) {
//                tooltip = TOOL_TIP_PREFIX + "<br>" +
//                        "<br>" +
//                        inputPort.connectedPort.getPanelWhichContainsPort().getPanelName() + " - " +
//                        inputPort.getValue(0) + "</html>";
//            }
//            else {
//                tooltip = TOOL_TIP_PREFIX + "</html>";
//            }
//            return tooltip;
//        }
// TODO: MORE ADVANCED TOOLTIP
// TODO: RML

        /**
         * Doesn't work if the name contains ")< br >" ... without the spaces.
         * @return
         */
        public String getAdditionalTooltip() {
            String tooltip = TOOL_TIP_PREFIX.substring(FULL_NAME.length());
            int endLineIndex = tooltip.indexOf(")<br>");
            tooltip = tooltip.substring(endLineIndex +   ")<br>".length());
            return tooltip;
        }
    }
}
