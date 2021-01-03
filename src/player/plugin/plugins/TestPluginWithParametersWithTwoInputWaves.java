package player.plugin.plugins;

import plugin.PluginParameterAnnotation;
import player.plugin.ifaces.user.waves.OperationOnWavesPluginIFace;
import util.audio.wave.DoubleWave;



public class TestPluginWithParametersWithTwoInputWaves implements OperationOnWavesPluginIFace {
    @PluginParameterAnnotation(lowerBound = "-0.1", upperBound = "0.1",
                               parameterTooltip = "will be added to the output wave", defaultValue = "0")
    private double param;

    @Override
    public void performOperation(DoubleWave input, DoubleWave output,
                                 int inputStartIndex, int inputEndIndex,
                                 int outputStartIndex, int outputEndIndex) {
        double[] inputWave = input.getSong();
        double[] outputWave = output.getSong();
        for (int ii = inputStartIndex, oi = outputStartIndex; oi < outputEndIndex; ii++, oi++) {
            if (ii >= inputEndIndex) {
                ii = inputStartIndex;
            }
            outputWave[oi] += inputWave[ii] + param;
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
        return "TestPluginWithParametersWithTwoInputWaves";
    }

    @Override
    public String getPluginTooltip() {
        return "Adds the parameter give by user to the input wave and the result is added to the output wave";
    }
}
