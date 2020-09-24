package RocnikovyProjektIFace.Drawing;

import DiagramSynthPackage.Synth.WaveTables.WaveTable;

import java.awt.*;

public class FunctionWaveDrawPanel extends WaveDrawPanel {
    public FunctionWaveDrawPanel(int binCount, boolean isEditable, Color backgroundColor,
                                 boolean shouldDrawLabelsAtTop) {
        super(binCount, "Input Value", isEditable, backgroundColor, shouldDrawLabelsAtTop);
        setLabels();
        normalizeAndSetDrawValues();
        setLastPartOfTooltip();
    }


    /**
     * Isn't called anywhere it is just marker, that the labels needs to be set in deriving class.
     */
    @Override
    protected void setLabels() {
        createLabels(DRAW_VALUES.length);
    }

    private void createLabels(int len) {
        labels = new String[len];
        double val = -1;
        double valJump = 2.0d / (len - 1);           // 2 Because I have range -1 to 1 I need to split it to the labels and -1 because the first doesn't count
        for(int i = 0; i < labels.length; i++, val += valJump) {
            labels[i] = String.format("%.2f", val);
        }
    }

    /**
     * Returns double, because the result will be interpolated.
     * @param inputValue
     * @return
     */
    public double getBinIndex(double inputValue) {
        inputValue += 1;           // Shift it so it is between 0-2 instead of -1 and 1
        double ratio = 2.0 / inputValue;
        double binIndex;

        if(inputValue != 0) {
            binIndex = ratio * (DRAW_VALUES.length - 1);
        }
        else {
            binIndex = 0;
        }
        return binIndex;
    }



    public double convertInputToOutput(double inputValue) {
        double index = getBinIndex(inputValue);
        double output = WaveTable.interpolate(DRAW_VALUES, index);
        return output;
    }
}
