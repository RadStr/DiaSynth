package DiagramSynthPackage.Synth.Operators.UnaryOperations;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.CircleShapedPanel;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals.ConstantTextInternals;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import DiagramSynthPackage.Synth.Unit;

import java.awt.*;

public class WaveShaper extends UnaryOperator {
    public WaveShaper(Unit u) {
        super(u);
        waveShaperPanel = RocnikovyProjektIFace.Drawing.WaveShaper.createMaxSizeWaveShaper(Color.LIGHT_GRAY,
                -1, 1, true);
    }
    public WaveShaper(JPanelWithMovableJPanels panelWithUnits) {
        super(panelWithUnits);
        waveShaperPanel = RocnikovyProjektIFace.Drawing.WaveShaper.createMaxSizeWaveShaper(Color.LIGHT_GRAY,
                -1, 1, true);
    }


    RocnikovyProjektIFace.Drawing.WaveShaper waveShaperPanel;


    @Override
    public double unaryOperation(double val) {
        return waveShaperPanel.convertInputToOutput(val);
    }

    @Override
    public String getDefaultPanelName() {
        return "WS";
    }

    @Override
    protected ShapedPanel createShapedPanel(JPanelWithMovableJPanels panelWithUnits) {
        ShapedPanel sp = new CircleShapedPanel(panelWithUnits,
                new ConstantTextInternals(getPanelName()), this);
        return sp;
    }

    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h,
                                            JPanelWithMovableJPanels panelWithUnits) {
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
    public String getTooltip() {
        return "<html>" +
                "The waveshaper operator. Uses function f: [-1, 1] -> [-1, 1] to transform input values to output values." +
                "The function is set using GUI component" +
                "</html>";
    }
}
