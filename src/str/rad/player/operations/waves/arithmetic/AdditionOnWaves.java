package str.rad.player.operations.waves.arithmetic;

import str.rad.player.plugin.ifaces.user.waves.OperationOnWavesPluginIFace;
import str.rad.util.audio.wave.DoubleWave;
import str.rad.util.math.ArithmeticOperation;

public class AdditionOnWaves implements OperationOnWavesPluginIFace {
    @Override
    public void performOperation(DoubleWave input, DoubleWave output,
                                 int inputStartIndex, int inputEndIndex,
                                 int outputStartIndex, int outputEndIndex) {
        double[] inputWave = input.getSong();
        double[] outputWave = output.getSong();
        ArithmeticOperation.performOperationOnSamples(inputWave, outputWave, inputStartIndex, inputEndIndex,
                                                      outputStartIndex, outputEndIndex, ArithmeticOperation.PLUS);
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
        return "Add waves";
    }

    @Override
    public String getPluginTooltip() {
        return "Adds input wave (the first wave) to the output wave (the second one) and stores result to the output wave";
    }
}
