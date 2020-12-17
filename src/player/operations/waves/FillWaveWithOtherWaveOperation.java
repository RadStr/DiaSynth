package player.operations.waves;

import player.plugin.ifaces.user.waves.OperationOnWavesPlugin;
import util.Utilities;
import util.audio.wave.DoubleWave;
import Rocnikovy_Projekt.Program;

public class FillWaveWithOtherWaveOperation extends OperationOnWavesPlugin {
    @Override
    public void performOperation(DoubleWave input, DoubleWave output,
                                 int inputStartIndex, int inputEndIndex,
                                 int outputStartIndex, int outputEndIndex) {
        super.performOperation(input, output, inputStartIndex, inputEndIndex, outputStartIndex, outputEndIndex);
        double[] inputWave = input.getSong();
        double[] outputWave = output.getSong();
        inputEndIndex = getInputEndIndex();
        outputEndIndex = getOutputEndIndex();
        FillWaveWithOtherWaveOperation.fillArrayWithValues(inputWave, outputWave, inputStartIndex, inputEndIndex, outputStartIndex, outputEndIndex);
    }




    /**
     * Keeps filling the song array with the values (if end of the values array is reached move to input start index)
     */
    public static void fillArrayWithValues(double[] input, double[] output,
                                           int inputStartIndex, int inputEndIndex,
                                           int outputStartIndex, int outputEndIndex) {
        int inputLen = inputEndIndex - inputStartIndex;
        boolean isPowerOf2 = Utilities.testIfNumberIsPowerOfN(inputLen, 2) >= 0;

        if (isPowerOf2) {
            for (int oi = outputStartIndex, ii = 0; oi < outputEndIndex; oi++, ii++) {
                output[oi] = input[inputStartIndex + (ii % inputLen)];
            }
        }
        else {
            for (int oi = outputStartIndex, ii = inputStartIndex; oi < outputEndIndex; oi++, ii++) {
                if (ii >= inputEndIndex) {
                    ii = inputStartIndex;
                }
                output[oi] = input[ii];
            }
        }
    }

    /**
     * Not used anymore - version with input and output start and end indices is preferred.
     * Keeps filling the song array with the values (if end of the values array is reached move to 0)
     * @param arr
     * @param values
     * @param startIndex
     * @param endIndex
     */
    public static void fillArrayWithValues(double[] arr, double[] values, int startIndex, int endIndex) {
        boolean isPowerOf2 = Utilities.testIfNumberIsPowerOfN(values.length, 2) >= 0;

        for(int i = startIndex, valuesInd = 0; i < endIndex; i++, valuesInd++) {
            if(isPowerOf2) {
                arr[i] = values[valuesInd % values.length];
            }
            else {
                if (valuesInd >= values.length) {
                    valuesInd = 0;
                }
                arr[i] = values[valuesInd];
            }
        }
    }




    @Override
    public boolean shouldWaitForParametersFromUser() {
        return true;
    }

    @Override
    public boolean isUsingPanelCreatedFromAnnotations() {
        return true;
    }

    @Override
    public String getPluginName() {
        return "Fill wave";
    }

    @Override
    public String getPluginTooltip() {
        return "<html>" +
            "Fill output wave (second wave) with samples of input wave (first wave)<br>" +
            "If the output wave is longer than input wave, then input wave behaves as circular buffer<br>" +
            "So when end of input wave is reached, it moves to [0] and continues.<br>" +
            "Unless shouldAlignLengths is set to true" +
            "</html>";
    }
}
