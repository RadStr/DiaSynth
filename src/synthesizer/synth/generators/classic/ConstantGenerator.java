package synthesizer.synth.generators.classic;

import plugin.PluginBaseIFace;
import synthesizer.gui.diagram.DiagramPanel;
import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.gui.diagram.panels.shape.internals.DynamicTextInternals;
import synthesizer.gui.diagram.panels.shape.RhombusShapedPanel;
import synthesizer.gui.diagram.panels.shape.ShapedPanel;
import synthesizer.synth.Unit;
import plugin.PluginParameterAnnotation;
import util.logging.MyLogger;
import Rocnikovy_Projekt.Program;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ConstantGenerator extends Unit implements PluginBaseIFace {

    public ConstantGenerator(Unit u) {
        super(u);
        copyInternalState(u);
        updateAfterPropertiesCall();
    }

    public ConstantGenerator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
        getShapedPanel().setToolTipText(getTooltip());
    }

    @Override
    protected void setPropertiesPanel() {
        propertiesPanel = this;
    }

    @Override
    public void updateAfterPropertiesCall() {
        ShapedPanel sp = getShapedPanel();
        sp.reshape(sp.getSize());
        sp.repaint();
        sp.setToolTipText(getTooltip());
    }



    @Override
    public boolean hasInputPorts() {
        return false;
    }

    @Override
    public boolean getIsConst() {
        return true;
    }
    @Override
    public boolean getIsNoiseGen() {
        return false;
    }


    // Volatile because otherwise setting the variable isn't atomic.
    // Also take into consideration that it will be set using reflection, but taken from the page
    // https://docs.oracle.com/javase/tutorial/reflect/member/fieldValues.html it says at the bottom that
    // From the runtime's point of view, the effects are the same, and the operation is
    // as atomic as if the value was changed in the class code directly.
    @PluginParameterAnnotation(name = "Generated constant:",
            parameterTooltip = "Constant generated by this panel")
    private volatile double generatedConstant = 500;
    private JLabel labelShowingValue;


    @Override
    public double getMaxAbsValue() {
        return generatedConstant;
    }

    /**
     * Returns Double.MAX_VALUE if it doesn't have modulation frequency (if it is envelope or operation or noise generator).
     * Or returns its modulation frequency if it is generator.
     *
     * @return
     */
    @Override
    public double getModulationFrequency() {
        return Double.MAX_VALUE;
    }



    @Override
    public double[] getWaveAmps(int waveIndex) {
        return null;
    }

    @Override
    public double[] getWaveFreqs(int waveIndex) {
        return null;
    }


    @Override
    public String getDefaultPanelName() {
        return "CONST";
    }

    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        return new InputPort[0];
    }

    @Override
    public double[] getNeutralValues() {
        // Not used
        return null;
    }

    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        DynamicTextInternals textInternals = new DynamicTextInternals(() -> Double.toString(generatedConstant));
        labelShowingValue = textInternals.getLabel();
        ShapedPanel sp = new RhombusShapedPanel(panelWithUnits, textInternals, this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            DiagramPanel panelWithUnits) {
        DynamicTextInternals textInternals = new DynamicTextInternals(() -> Double.toString(generatedConstant));
        labelShowingValue = textInternals.getLabel();
        ShapedPanel sp = new RhombusShapedPanel(relativeX, relativeY, w, h, panelWithUnits,
                textInternals, this);
        return sp;
    }

    @Override
    public void resetToDefaultState() {
        // EMPTY
    }

    @Override
    public void calculateSamples() {
        if(results[0] != generatedConstant) {
            Program.setOneDimArr(results, 0, results.length, generatedConstant);
        }
    }

    @Override
    public String getTooltip() {
        return "GENERATES CONSTANT: " + labelShowingValue.getText();
    }



    /**
     * @return Returns true if the operation needs parameters - so user needs to put them in the JPanel.
     * If it returns false, then it doesn't need parameters from user and the operation can start immediately
     */
    @Override
    public boolean shouldWaitForParametersFromUser() {
        return true;
    }

    /**
     * This parameter matters only when shouldWaitForParametersFromUser returns true
     *
     * @return
     */
    @Override
    public boolean isUsingPanelCreatedFromAnnotations() {
        return true;
    }

    @Override
    public String getPluginName() {
        return "Constant";
    }


    @Override
    public void save(PrintWriter output) {
        super.save(output);
        output.println(generatedConstant);
    }

    @Override
    public void load(BufferedReader input) {
        super.load(input);
        try {
            String line = input.readLine();
            generatedConstant = Double.parseDouble(line);
            updateAfterPropertiesCall();
        }
        catch (IOException e) {
            MyLogger.logException(e);
        }
    }

    @Override
    public void copyInternalState(Unit copySource) {
        generatedConstant = ((ConstantGenerator)copySource).generatedConstant;
    }
}
