package player.plugin.plugins;

import player.plugin.ifaces.user.wave.PluginWithInputWaveBase;
import player.plugin.ifaces.user.wave.WithInputWavePluginIFace;
import player.plugin.ifaces.PluginParametersAnnotation;
import Rocnikovy_Projekt.DoubleWave;


/**
 * Has implements WithInputWavePluginIFace in signature, else it wouldn't be found as plugin.
 */
public class TestPluginWithParametersWithInputWave extends PluginWithInputWaveBase implements WithInputWavePluginIFace {
    @PluginParametersAnnotation(lowerBound = "-0.1", upperBound = "0.1", parameterTooltip = "will be added to the output wave", defaultValue = "0")
    private double param;

    @Override
    public void performOperation(DoubleWave input, DoubleWave output,
                                 int inputStartIndex, int inputEndIndex,
                                 int outputStartIndex, int outputEndIndex) {
        super.performOperation(input, output, inputStartIndex, inputEndIndex, outputStartIndex, outputEndIndex);
        double[] inputWave = input.getSong();
        double[] outputWave = output.getSong();
        inputEndIndex = getInputEndIndex();
        outputEndIndex = getOutputEndIndex();
        for(int ii = inputStartIndex, oi = outputStartIndex; oi < outputEndIndex; ii++, oi++) {
            if(ii >= inputEndIndex) {
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
    public boolean isUsingDefaultJPanel() {
        return true;
    }

    @Override
    public String getPluginName() {
        return "TestPluginWithParametersWithInputWave";
    }

    @Override
    public String getPluginTooltip() {
        return "Adds the parameter give by user to the input wave and the result is added to the output wave";
    }
}
