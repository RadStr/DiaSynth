package RocnikovyProjektIFace.Drawing;

import RocnikovyProjektIFace.AudioWavePanelReferenceValues;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class WaveShaper extends DrawWrapperBase {
    public static WaveShaper createMaxSizeWaveShaper(Color backgroundColor,
                                                     double minValue, double maxValue,
                                                     boolean shouldDrawLabelsAtTop) {
        int windowSize = DrawWrapperBase.calculateMaxSizeBinCount(minValue, maxValue);
        int firstX = new WaveShaper(windowSize, backgroundColor,
                minValue, maxValue, shouldDrawLabelsAtTop).drawnFunctionPanel.getFirstBinStartX();
        windowSize -= 2 * firstX;
        return new WaveShaper(windowSize, backgroundColor, minValue, maxValue, shouldDrawLabelsAtTop);
    }

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