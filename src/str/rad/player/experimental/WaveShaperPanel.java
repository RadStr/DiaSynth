package str.rad.player.experimental;

import javax.swing.*;
import java.awt.*;

public class WaveShaperPanel extends DrawWrapperBase {
    public static WaveShaperPanel createMaxSizeWaveShaper(Color backgroundColor,
                                                          double minValue, double maxValue,
                                                          boolean shouldDrawLabelsAtTop) {
        int windowSize = DrawWrapperBase.calculateMaxSizeBinCount(minValue, maxValue);
        int firstX = new WaveShaperPanel(windowSize, backgroundColor, minValue, maxValue, shouldDrawLabelsAtTop).
                drawnFunctionPanel.getFirstBinStartX();
        windowSize -= 2 * firstX;
        return new WaveShaperPanel(windowSize, backgroundColor, minValue, maxValue, shouldDrawLabelsAtTop);
    }

    public WaveShaperPanel(int windowSize,
                           Color backgroundColor,
                           double minValue, double maxValue,
                           boolean shouldDrawLabelsAtTop) {
        this(new FunctionWaveDrawPanel(windowSize, true, backgroundColor, shouldDrawLabelsAtTop),
             minValue, maxValue);
    }

    private WaveShaperPanel(FunctionWaveDrawPanel drawnFunctionPanel, double minValue, double maxValue) {
        super(drawnFunctionPanel, minValue, maxValue);
        this.drawnFunctionPanel = drawnFunctionPanel;
    }

    private final FunctionWaveDrawPanel drawnFunctionPanel;

    public double convertInputToOutput(double inputValue) {
        return drawnFunctionPanel.convertInputToOutput(inputValue);
    }

    public double[] getOutputValues() {
        return drawnFunctionPanel.getDrawnWave();
    }

    /**
     * Copies the given values in parameter to the internal array which represents the drawn wave. The arrays don't
     * have to be the same size, it tries to preserve the shape of the given argument.
     *
     * @param newValues
     */
    public void setOutputValues(double[] newValues) {
        drawnFunctionPanel.setDrawValues(newValues);
    }


    @Override
    public void addMenus(JMenuBar menuBar, WaveAdderIFace waveAdder) {
        addReset(menuBar);
    }
}