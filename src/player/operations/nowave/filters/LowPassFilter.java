package player.operations.nowave.filters;

import player.plugin.ifaces.user.nowave.WithoutInputWavePluginIFace;
import player.plugin.ifaces.PluginParametersAnnotation;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.Program;

public class LowPassFilter implements WithoutInputWavePluginIFace {
    @PluginParametersAnnotation(name = "Cutoff frequency:",
            lowerBound = "0", defaultValue = "400", parameterTooltip = "Cut-off frequency")
    private double cutoffFreq;
    @PluginParametersAnnotation(name = "Coefficient count:",
            lowerBound = "2", defaultValue = "32", parameterTooltip = "Represents the number of " +
            "the coefficients used for filtering " +
            "(How many last samples should be used for calculating the current one in the filter)." +
            "Usually the more the better filter. Just use some low powers of 2 - like 32, 64 , ...")
    private int coefCount;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] samples = audio.getSong();
        Program.runLowPassFilter(samples, startIndex, 1, audio.getSampleRate(),
            cutoffFreq, coefCount, samples, startIndex, endIndex);
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
        return "Low-pass filter";
    }

    @Override
    public String getPluginTooltip() {
        return "Performs low-pass filter with user given cut-off frequency";
    }
}
