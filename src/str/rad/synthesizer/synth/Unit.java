package str.rad.synthesizer.synth;

import str.rad.plugin.PluginBaseIFace;
import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.gui.diagram.panels.port.InputPort;
import str.rad.synthesizer.gui.diagram.panels.port.OutputPort;
import str.rad.synthesizer.gui.diagram.panels.shape.ShapedPanel;
import str.rad.synthesizer.gui.tree.JTreeCellClickedCallbackIFace;
import str.rad.synthesizer.UnitViewForGUIIFace;
import str.rad.util.logging.DiasynthLogger;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * This is just template class, when overriding the user should override the Operator or Generator or Envelope classes
 * and also implement both constructors calling the super() variants with corresponding arguments.
 * I can't force user to implement constructors, the user has to have this in mind.
 * <p>
 * Note: When overriding this class directly, you shouldn't change the samples that the unit gets on inputs,
 * since it may also change the other calculations, which don't have anything to do with the Unit you have written.
 */
public abstract class Unit implements SerializeIFace, JTreeCellClickedCallbackIFace,
                                      UnitViewForGUIIFace, UnitGeneratedValuesInfo {
    public static final int BUFFER_LEN = 512;


    /**
     * Copy constructor. Has to be implemented in all deriving classes.
     *
     * @param u
     */
    public Unit(Unit u) {
        this();
        setIsOutputUnit();
        ShapedPanel sp = u.getShapedPanel();
        this.panelWithUnits = sp.getDiagramPanel();
        setPanelName(findFirstNonUsedName(panelWithUnits, getDefaultPanelName()));
        setPropertiesPanel();
        Point relativePos = sp.getRelativePosToReferencePanel();
        this.shapedPanel = createShapedPanel(relativePos.x, relativePos.y, panelWithUnits.getReferencePanelWidth(),
                                             panelWithUnits.getReferencePanelHeight(), panelWithUnits);
        inputPorts = createInputPorts(panelWithUnits, getNeutralValuesForPorts());
        outputPort = new OutputPort(this, shapedPanel);
        shapedPanel.setToolTipText(getTooltip());
    }


    /**
     * Constructor used when not copying.  Has to be implemented in all deriving classes.
     *
     * @param panelWithUnits
     */
    public Unit(DiagramPanel panelWithUnits) {
        this();
        setIsOutputUnit();
        setPanelName(findFirstNonUsedName(panelWithUnits, getDefaultPanelName()));
        setPropertiesPanel();
        this.shapedPanel = createShapedPanel(panelWithUnits);
        inputPorts = createInputPorts(panelWithUnits, getNeutralValuesForPorts());
        outputPort = new OutputPort(this, shapedPanel);
        shapedPanel.setToolTipText(getTooltip());
        this.panelWithUnits = panelWithUnits;
    }

    private Unit() {
        setResultsLen(BUFFER_LEN);
    }


    // JTreeCellClickedCallbackIFace
    @Override
    public void clickCallback() {
        panelWithUnits.tryAdd(this);
    }

    @Override
    public List<JTreeCellClickedCallbackIFace> getChildren() {
        return null;
    }
    // JTreeCellClickedCallbackIFace


    protected PluginBaseIFace propertiesPanel;

    @Override
    public PluginBaseIFace getPropertiesPanel() {
        return propertiesPanel;
    }

    protected abstract void setPropertiesPanel();


    public Unit createInstanceFromThis() {
        return panelWithUnits.createUnit(this);
    }


    // Probably could be final, since to me it makes more sense to do some additional actions in the
    // copy constructor instead of overriding this method, but maybe I am wrong, so I keep it non-final
    @Override    // UnitCommunicationWithGUI
    public Unit copyPanel() {
        Unit u = createInstanceFromThis();
        ShapedPanel sp = u.getShapedPanel();

        for (int i = 0; i < this.inputPorts.length; i++) {
            u.inputPorts[i].copyFields(this.inputPorts[i]);
        }

        panelWithUnits.addPanelTemporary(u);
        sp.startedAddingUnit();
        panelWithUnits.repaint();

        return u;
    }


    public boolean isBinaryPlus() {
        return false;
    }

    protected DiagramPanel panelWithUnits;
    private String panelName;

    public String getPanelName() {
        return panelName;
    }

    public void setPanelName(String newName) {
        panelName = newName;
    }

    public abstract String getDefaultPanelName();

    protected double[] results = new double[0];

    @Override
    public double[] getValues() {
        return results;
    }

    @Override
    public double getValue(int index) {
        return results[index];
    }

    private void setResultsLen(int len) {
        results = new double[len];
    }

    protected InputPort[] inputPorts = new InputPort[0];        // Default value is InputPort[0] not null

    @Override       // UnitCommunicationWithGUI
    public InputPort[] getInputPorts() {
        return inputPorts;
    }

    public int getInputPortsLen() {
        return inputPorts.length;
    }

    public InputPort getInputPort(int inputPortIndex) {
        return inputPorts[inputPortIndex];
    }

    @Override
    public boolean hasInputPorts() {
        return true;
    }

    /**
     * Needs to be changed to true at the end of calculateSamples method
     */
    private boolean performedCalculation = false;
    public boolean getPerformedCalculation() {
        return performedCalculation;
    }

    public void markAsCalculated() {
        performedCalculation = true;
    }

    public void unmarkAsCalculated() {
        performedCalculation = false;
    }


    /**
     * Should return InputPort[] which will be set as unit's input ports. It is called inside the constructors.
     * If there are no input ports then set it to new InputPort[0].
     */
    protected abstract InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues);

    /**
     * It is used as parameter to the createInputPorts method.
     * It is up to the person who implements the plugin, if
     * he takes these values into consideration. He may ignore them and
     * just set the input ports to hard-coded default values in createInputPorts method.
     * @return Returns neutral values for ports.
     *         If null or if the array is shorter than number of ports
     *         then these values should be ignored.
     */
    public abstract double[] getNeutralValuesForPorts();

    protected OutputPort outputPort;

    @Override       // UnitCommunicationWithGUI
    public OutputPort getOutputPort() {
        return outputPort;
    }

    protected ShapedPanel shapedPanel;

    public ShapedPanel getShapedPanel() {
        return shapedPanel;
    }

    /**
     * Creates new shaped panel
     *
     * @param panelWithUnits
     * @return
     */
    protected abstract ShapedPanel createShapedPanel(DiagramPanel panelWithUnits);

    /**
     * Creates new shaped panel called with corresponding constructor of same signature as this method
     * (+ the internals of course)
     *
     * @param panelWithUnits
     * @return
     */
    protected abstract ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                                     DiagramPanel panelWithUnits);


    /**
     * Resets to the default state (as if no sample was ever before played)
     */
    public abstract void resetToDefaultState();

    public abstract void calculateSamples();


    // Don't touch this method when writing plugins

    /**
     * Don't touch this method when writing plugins
     *
     * @param channelRecords are the channel to put the recorded samples to
     * @param index          is the index in the buffer to put the recorded samples to
     * @param remainingLen   is the remaining length in the channel
     */
    public void calculateSamplesInstantRecord(double[][] channelRecords, int index, int remainingLen) {
        calculateSamples();
    }

    public abstract String getTooltip();

    @Override
    public double getMaxAbsValue() {
        double min = getMinValue();
        double max = getMaxValue();
        return Math.max(Math.abs(min), Math.abs(max));
    }

    @Override
    public abstract double getMinValue();
    @Override
    public abstract double getMaxValue();


    private boolean isOutputUnit;

    private final void setIsOutputUnit() {
        if (this instanceof OutputUnit) {
            isOutputUnit = true;
        }
        else {
            isOutputUnit = false;
        }
    }

    public final boolean getIsOutputUnit() {
        return isOutputUnit;
    }


    // SerializeIFace
    @Override
    /**
     * Same as for load, where the save on output port needs to be called after save was called on all panels.
     * So it can be easily loaded in load, instead of going through the text twice.
     * NOTE: I just need to save the output port because the connections are symmetric
     */
    public void save(PrintWriter output) {
        String className = this.getClass().getName();
        output.println(className);

        output.println(getPanelName());

        Point relativePos = shapedPanel.getRelativePosToReferencePanel();
        output.println(relativePos.x + " " + relativePos.y);
    }

    /**
     * It is important to note that it doesn't load input port, since in my case when calling it, the other panels
     * to which the output ports connects doesn't have to exit yet. Also note:
     * I just need to load the output port because the connections are symmetric - that means that by connecting
     * the output port to input port the input port will be set to the output port
     */
    @Override
    public void load(BufferedReader input) {
        try {
            String line;
            String[] lineParts;

            line = input.readLine();
            setPanelName(line);

            // Just hot-fix, because I noticed that when loading saved diagram, it doesn't contain the name with number.
            if (!(this instanceof OutputUnit)) {
                Point relativePos = shapedPanel.getRelativePosToReferencePanel();
                int rw = panelWithUnits.getReferencePanelWidth();
                int rh = panelWithUnits.getReferencePanelHeight();
                panelWithUnits.removeInputPortLabels(shapedPanel);
                shapedPanel = createShapedPanel(relativePos.x, relativePos.y, rw, rh, panelWithUnits);
                inputPorts = createInputPorts(panelWithUnits, getNeutralValuesForPorts());
                outputPort = new OutputPort(this, shapedPanel);
                shapedPanel.setToolTipText(getTooltip());
                shapedPanel.updateSize(new Dimension(rw, rh));
            }

            // Read relative position
            line = input.readLine();
            lineParts = line.split(" ");
            int relX = Integer.parseInt(lineParts[0]);
            int relY = Integer.parseInt(lineParts[1]);
            shapedPanel.setRelativePosToReferencePanel(relX, relY);
        }
        catch (IOException e) {
            DiasynthLogger.logException(e);
        }
    }
    // SerializeIFace


    private static String findFirstNonUsedName(DiagramPanel panelWithUnits, String name) {
        int number = 1;
        String panelName = name;
        List<Unit> panels = panelWithUnits.getPanels();
        boolean isUnique = false;

        // Pretty slow, but it is called only on addition so it doesn't matter that much
        while (!isUnique) {
            isUnique = true;
            for (Unit p : panels) {
                if (panelName.equals(p.getPanelName())) {
                    panelName = name + number;
                    isUnique = false;
                    break;
                }
            }
            number++;
        }

        return panelName;
    }


    /**
     * Returns the first constant in input ports or Double.MAX_VALUE if there are no constants on input ports
     *
     * @return
     */
    @Override
    public double getConstant() {
        for (int i = 0; i < inputPorts.length; i++) {
            if (inputPorts[i].getIsConst()) {
                return inputPorts[i].getValue(0);
            }
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
        int index = -1;

        for (int i = 0; i < inputPorts.length; i++) {
            if (!inputPorts[i].getIsConst()) {
                index++;
                if (index == n) {
                    return inputPorts[i].getValues();
                }
            }
        }

        return null;
    }

    public static double freqToRad(double freq) {
        return 2 * Math.PI * freq;
    }


    // Thought: Maybe I could call copyInternalState at the end of the copyPanel method,
    // but it could a bit problematic with some additional units - for example when you copy filter -
    // you copy the values which are currently there - and the values may be
    // in inconsistent state, because we may be currently in process of generating samples.
    // Because of that it is better to just have
    // all the values set to default for the target of copy - like no sample was played before.

    // Added for future proofing, because of the possible computation inconsistencies for units with internal state when
    // panel connection or panel deletion happens

    /**
     * The method copies the state of the given parameter to the instance on which the method was called. State is
     * everything which is needed for calculation except the fields already defined in the Unit.
     *
     * @param copySource contains the content that we should copy.
     */
    public abstract void copyInternalState(Unit copySource);
}