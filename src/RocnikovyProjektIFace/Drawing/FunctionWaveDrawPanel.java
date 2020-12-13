package RocnikovyProjektIFace.Drawing;

import synthesizer.synth.WaveTables.WaveTable;

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
    private double getBinIndex(double inputValue) {
        return getBinIndex(DRAW_VALUES.length, inputValue);
    }


    private static double getBinIndex(double[] function, double inputValue) {
        return getBinIndex(function.length, inputValue);
    }

    /**
     * Returns double, because the result will be interpolated.
     * @param inputValue
     * @return
     */
    private static double getBinIndex(int functionArrLen, double inputValue) {
        inputValue += 1;           // Shift it so it is between 0-2 instead of -1 and 1
        double ratio = inputValue / 2.0;
        double binIndex;

        if(inputValue != 0) {
            binIndex = ratio * (functionArrLen - 1);
        }
        else {
            binIndex = 0;
        }

        return binIndex;
    }


    public double convertInputToOutput(double inputValue) {
        return convertInputToOutput(DRAW_VALUES, inputValue);
    }


    public static double convertInputToOutput(double[] function, double inputValue) {
        double index = getBinIndex(function.length, inputValue);
        double output = WaveTable.interpolate(function, index);
        return output;
    }
}
