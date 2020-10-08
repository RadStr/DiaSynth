package DiagramSynthPackage.GUI.MovablePanelsPackage.Ports;

import DiagramSynthPackage.GUI.MovablePanelsPackage.Cable;
import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.MovablePanelViewForPort;
import DiagramSynthPackage.Synth.SerializeIFace;
import DiagramSynthPackage.Synth.UnitGeneratedValuesInfo;
import Rocnikovy_Projekt.MyLogger;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class OutputPort extends Port implements SerializeIFace {
    public OutputPort(UnitGeneratedValuesInfo u, MovablePanelViewForPort panelWhichContainsPort) {
        super(u, panelWhichContainsPort, Integer.MAX_VALUE);
        connectedPorts = new ArrayList<>();
    }


    @Override
    public boolean getIsConst() {
        return unitValsInfo.getIsConst();
    }
    @Override
    public boolean getIsNoiseGen() {
        return unitValsInfo.getIsNoiseGen();
    }

    @Override
    public double getMaxAbsValue() {
        return unitValsInfo.getMaxAbsValue();
    }
    @Override
    public double getValue(int index) {
        return unitValsInfo.getValue(index);
    }
    @Override
    public double[] getValues() {
        return unitValsInfo.getValues();
    }

    /**
     * Returns Double.MAX_VALUE if it doesn't have modulation index (if it is envelope or operation or noise generator).
     * Or returns its modulation frequency if it is generator.
     *
     * @return
     */
    @Override
    public double getModulationFrequency() {
        return unitValsInfo.getModulationFrequency();
    }

    @Override
    public boolean isBinaryPlus() {
        return unitValsInfo.isBinaryPlus();
    }

    /**
     * Returns the first constant in input ports or Double.MAX_VALUE if there are no constants on input ports
     *
     * @return
     */
    @Override
    public double getConstant() {
        return unitValsInfo.getConstant();
    }

    /**
     * Returns the n-th non-constant in input ports or null if there are no non-constants on input ports.
     * Doesn't work recursively.
     *
     * @return
     */
    @Override
    public double[] getNonConstant(int n) {
        return unitValsInfo.getNonConstant(n);
    }


    @Override
    public double[] getWaveAmps(int waveIndex) {
        return unitValsInfo.getWaveAmps(waveIndex);
    }


    @Override
    public double[] getWaveFreqs(int waveIndex) {
        return unitValsInfo.getWaveFreqs(waveIndex);
    }


    private List<Port> connectedPorts;
    public List<Port> getConnectedPorts() {
        return connectedPorts;
    }
    private final List<Cable> cables = new ArrayList<Cable>();
    public List<Cable> getCables() {
        return cables;
    }
    public void setAbsolutePaths(Point referencePanelLoc, Dimension panelSize, int borderWidth, int borderHeight,
                                 int panelSizeWithBorderWidth, int panelSizeWithBorderHeight, int pixelsPerElevation) {
        for (Cable c : cables) {
            c.setAbsolutePathBasedOnRelativePath(referencePanelLoc, panelSize, borderWidth, borderHeight,
                    panelSizeWithBorderWidth, panelSizeWithBorderHeight, pixelsPerElevation);
        }
    }

    public void moveCables(int xMovement, int yMovement) {
        for (Cable c : cables) {
            c.move(xMovement, yMovement);
        }
    }
    public void moveCablesX(int xMovement) {
        for (Cable c : cables) {
            c.moveX(xMovement);
        }
    }
    public void moveCablesY(int yMovement) {
        for (Cable c : cables) {
            c.moveY(yMovement);
        }
    }

    public void resetCablesAndElevations() {
        for(Cable c : getCables()) {
            c.resetPaths();
            c.resetElevation();
        }
    }

    public void resetCables() {
        for(Cable c : getCables()) {
            c.resetPaths();
        }
    }


    public void resetElevations() {
        for(Cable c : getCables()) {
            c.resetElevation();
        }
    }



    @Override
    public void connectToPort(Port port, boolean connectAlsoOnTheOtherPort) {
        if(!connectedPorts.contains(port)) {
            Point p = port.getPanelWhichContainsPort().getRelativePosToReferencePanel();
            int y = panelWhichContainsPort.getRelativePosToReferencePanel().y;
            if (y < p.y) {
                connectedPorts.add(port);
                Cable cable = new Cable(panelWhichContainsPort.getClassWithMaxElevationInfo(),
                        this.panelWhichContainsPort, (InputPort) port);     // If it isn't Input port then something is very wrong
                cables.add(cable);

                if(connectAlsoOnTheOtherPort) {
                    port.connectToPort(this, false);
                }
            }
        }
    }



    private int removeOnlyPort(Port port, boolean removeTheOtherPort) {
        for(int i = 0; i < connectedPorts.size(); i++) {
            Port p = connectedPorts.get(i);
            if(p == port) {
                connectedPorts.remove(i);
                if(removeTheOtherPort) {
                    p.removePort(this, false);
                }
                return i;
            }
        }

        return -1;
    }

    @Override
    public int removePort(Port port, boolean removeTheOtherPort) {
        int index = removeOnlyPort(port, removeTheOtherPort);
        if(index >= 0) {
            cables.remove(index);
        }

        return index;
    }

    @Override
    public void removeAllPorts() {
        int len = connectedPorts.size();
        for(int i = 0; i < len; i++) {
            removePort(connectedPorts.get(0));
        }
        cables.clear();
    }


    /**
     * Copies the fields from the given parameter to the instance on which was the method called.
     * @param copySourcePort is the port from which we will copy.
     */
    private void copyFields(OutputPort copySourcePort) {
        for(Port p : copySourcePort.connectedPorts) {
            this.connectToPort(p);
        }
    }

    @Override
    public OutputPort copy(UnitGeneratedValuesInfo unitToContainNewPort,
                           MovablePanelViewForPort panelToContainNewPort) {
        OutputPort port = new OutputPort(unitToContainNewPort, panelToContainNewPort);
        port.copyFields(this);
        return port;
    }

    // SerializeIFace
    @Override
    public void save(PrintWriter output) {
        output.println(connectedPorts.size());
        // I will save the index of the panel followed by the index of input port for each connected port
        for(Port p : connectedPorts) {
            output.println(p.panelWhichContainsPort.getIndexInPanelList() + " " + p.CONNECTOR_INDEX);
        }
    }


    @Override
    public void load(BufferedReader input) {
        try {
            String line;
            String[] lineParts;
            line = input.readLine();
            int outputCount = Integer.parseInt(line);
            for (int i = 0; i < outputCount; i++) {
                line = input.readLine();
                lineParts = line.split(" ");
                int indexInPanelList = Integer.parseInt(lineParts[0]);
                int connectorIndex = Integer.parseInt(lineParts[1]);
                panelWhichContainsPort.connectToPort(indexInPanelList, connectorIndex);
            }
        }
        catch (IOException e) {
            MyLogger.logException(e);
        }
    }
}
