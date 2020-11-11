package RocnikovyProjektIFace.Drawing;

import DiagramSynthPackage.Synth.WaveTables.WaveTable;
import RocnikovyProjektIFace.AudioWavePanelOnlyWave;
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

    public double convertInputToOutput(double inputValue) {
        return drawnFunctionPanel.convertInputToOutput(inputValue);
    }
    public double[] getOutputValues() {
        return drawnFunctionPanel.getDrawnWave();
    }
    /**
     * Copies the given values in parameter to the internal array which represents the drawn wave. The arrays don't
     * have to be the same size, it tries to preserve the shape of the given argument.
     * @param newValues
     */
    public void setOutputValues(double[] newValues) {
        double[] outArr = getOutputValues();
        if(newValues.length < outArr.length) {
            double newValuesJump = newValues.length / (double)outArr.length;
            double newValuesIndex = 0;
            for(int i = 0; i < outArr.length; i++, newValuesIndex += newValuesJump) {
                outArr[i] = WaveTable.interpolate(outArr, newValuesIndex);
            }
        }
        else if(newValues.length > outArr.length) {
            AudioWavePanelOnlyWave.findAveragesInValues(newValues, outArr, 0, 0,
                    newValues.length, outArr.length);
        }
        else {
            System.arraycopy(newValues, 0, outArr, 0, outArr.length);
        }
    }


    @Override
    public void addMenus(JMenuBar menuBar, AddWaveIFace waveAdder) {
        addReset(menuBar);
    }
}