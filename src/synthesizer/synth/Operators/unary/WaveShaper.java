package synthesizer.synth.Operators.unary;

import synthesizer.gui.MovablePanelsPackage.DiagramPanel;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.CircleShapedPanel;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.Internals.ConstantTextInternals;
import synthesizer.gui.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import synthesizer.synth.Unit;
import RocnikovyProjektIFace.AudioPlayerPanel;
import RocnikovyProjektIFace.Drawing.DrawJFrame;
import RocnikovyProjektIFace.Drawing.FunctionWaveDrawPanel;
import Rocnikovy_Projekt.Aggregations;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.MyLogger;
import Rocnikovy_Projekt.Program;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class WaveShaper extends UnaryOperator {
    public WaveShaper(Unit u) {
        super(u);
        copyInternalState(u);
    }
    public WaveShaper(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
    }


    public static class FunctionWithMaxAbsVal {
        public FunctionWithMaxAbsVal(double[] function) {
            setFunction(function);
        }

        private double[] function;
        private void setFunction(double[] function) {
            this.function = function;
            setFunctionOutputMaxAbsVal();
        }
        private double functionOutputMaxAbsVal;
        private void setFunctionOutputMaxAbsVal() {
            functionOutputMaxAbsVal = Program.performAggregation(function, Aggregations.ABS_MAX);
        }
    }


    // I have separate variable, which uses its own copy of the function array. Because otherwise when the function
    // is being changed and the samples are generated at the same time, then there could be invalid values (half-written doubles)
    private volatile FunctionWithMaxAbsVal functionWrapper;
    private void setFunctionWrapper(double[] function) {
        functionWrapper = new FunctionWithMaxAbsVal(function);
    }

    private void setFunction() {
        DrawJFrame f = (DrawJFrame)propertiesPanel;
        RocnikovyProjektIFace.Drawing.WaveShaper waveShaperPanel = (RocnikovyProjektIFace.Drawing.WaveShaper)f.getDrawPanel();
        double[] waveShaperFunction = waveShaperPanel.getOutputValues();
        double[] newFunction = new double[waveShaperFunction.length];
        System.arraycopy(waveShaperFunction, 0, newFunction, 0, newFunction.length);
        setFunctionWrapper(newFunction);
    }

    @Override
    protected void setPropertiesPanel() {
        // Doesn't have properties
        propertiesPanel = AudioPlayerPanel.createDrawFrame(AudioPlayerPanel.DRAW_PANEL_TYPES.WAVESHAPER, -1,
                                                           null, null, -1, -1);
        setFunction();
        ((JFrame)propertiesPanel).addWindowListener(new WindowAdapter() {
           @Override
           public void windowClosing(WindowEvent e)
           {
               setFunction();
           }
       });
        // TODO: Pres panel to moc nejde protoze to pak dam do dialogu
//        waveShaperPanel = RocnikovyProjektIFace.Drawing.WaveShaper.createMaxSizeWaveShaper(Color.LIGHT_GRAY,
//                -1, 1, true);
        // TODO: Pres panel to moc nejde protoze to pak dam do dialogu
    }


    @Override
    public double unaryOperation(double val) {
        return FunctionWaveDrawPanel.convertInputToOutput(functionWrapper.function, val);
    }

    @Override
    public String getDefaultPanelName() {
        return "WS";
    }

    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits,
                new ConstantTextInternals(getPanelName()), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            DiagramPanel panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(relativeX, relativeY, w, h, panelWithUnits,
                new ConstantTextInternals(getPanelName()), this);
        return sp;
    }

    /**
     * Resets to the default state (as if no sample was ever before played)
     */
    @Override
    public void resetToDefaultState() {
        // EMPTY
    }


    @Override
    public void calculateSamples() {
        double[] ops = inputPorts[0].getValues();

        // Have to normalize, because the waveshaper expects the input to be on [-1, 1] interval
        double amplitude = inputPorts[0].getMaxAbsValue();
        if(amplitude != 0) {
            for(int i = 0; i < results.length; i++) {
                results[i] = unaryOperation(ops[i] / amplitude);
            }
        }
        else {
            for (int i = 0; i < results.length; i++) {
                results[i] = unaryOperation(ops[i]);
            }
        }
    }

    @Override
    public double getMaxAbsValue() {
        return functionWrapper.functionOutputMaxAbsVal;
    }



    @Override
    public String getTooltip() {
        return "<html>" +
                "The waveshaper operator. Uses function f: [-1, 1] -> [-1, 1] to transform input values to output values." +
                "The function is set using GUI component" +
                "</html>";
    }



    @Override
    public void save(PrintWriter output) {
        super.save(output);
        double[] wave = functionWrapper.function;
        String PREFIX_PATH = "resources/WaveShaper/WS_";
        int index = 0;
        String path = PREFIX_PATH + index;
        while (new File(path).exists()) {
            index++;
            path = PREFIX_PATH + index;
        }
        DoubleWave.storeDoubleArray(wave, 0, wave.length, path);
        output.println(path);
    }

    @Override
    public void load(BufferedReader input) {
        super.load(input);
        try {
            String line = input.readLine();
            loadWaveShaperFunction(line);
        }
        catch (IOException e) {
            MyLogger.logException(e);
        }
    }


    private void loadWaveShaperFunction(String path) {
        try {
            RandomAccessFile file = new RandomAccessFile(path, "r");
            double[] function = DoubleWave.getStoredDoubleArray(file.getChannel());
            setWaveShaperPanelDrawValues(function);
        } catch (IOException e) {
            MyLogger.logException(e);
        }
    }

    private void setWaveShaperPanelDrawValues(double[] function) {
        if (function != null) {
            DrawJFrame f = (DrawJFrame)propertiesPanel;
            RocnikovyProjektIFace.Drawing.WaveShaper waveShaperPanel = (RocnikovyProjektIFace.Drawing.WaveShaper)f.getDrawPanel();
            waveShaperPanel.setOutputValues(function);
            setFunction();
        }
    }

    @Override
    public void copyInternalState(Unit copySource) {
        setWaveShaperPanelDrawValues(((WaveShaper)copySource).functionWrapper.function);
    }
}