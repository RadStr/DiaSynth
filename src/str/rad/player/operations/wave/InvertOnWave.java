package str.rad.player.operations.wave;

import str.rad.player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import str.rad.util.audio.wave.DoubleWave;
import str.rad.util.math.ArithmeticOperation;

public class InvertOnWave implements OperationOnWavePluginIFace {
    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] wave = audio.getSong();
        int len = endIndex - startIndex;
        ArithmeticOperation.performOperationOnSamples(wave, wave, startIndex, startIndex,
                                                      len, (double) -1, ArithmeticOperation.MULTIPLY);
    }

    @Override
    public boolean shouldWaitForParametersFromUser() {
        return false;
    }

    @Override
    public boolean isUsingPanelCreatedFromAnnotations() {
        return false;
    }

    @Override
    public String getPluginName() {
        return "Invert wave";
    }

    @Override
    public String getPluginTooltip() {
        return "Inverts the given part of wave";
    }
}
