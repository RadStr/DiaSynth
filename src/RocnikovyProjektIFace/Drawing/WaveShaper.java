package RocnikovyProjektIFace.Drawing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class WaveShaper extends DrawWrapperBase {
    public WaveShaper(int windowSize,
                      Color backgroundColor,
                      double minValue, double maxValue,
                      boolean shouldDrawLabelsAtTop) {
        this(new FunctionWaveDrawPanel(windowSize, true, backgroundColor, shouldDrawLabelsAtTop),
                minValue, maxValue);
    }

    private WaveShaper(FunctionWaveDrawPanel drawnFunctionPanel, double minValue, double maxValue) {
        super(drawnFunctionPanel, minValue, maxValue);
        this.drawnFunctionPanel = drawnFunctionPanel;
    }

    private final FunctionWaveDrawPanel drawnFunctionPanel;

    public double[] getOutputValues() {
        return drawnFunctionPanel.getDrawnWave();
    }

    @Override
    public void addMenus(JMenuBar menuBar, AddWaveIFace waveAdder) {
        addReset(menuBar);
    }
}