package player.operations.waves.arithmetic;

import player.plugin.ifaces.user.waves.OperationOnWavesPluginIFace;
import util.audio.wave.DoubleWave;
import util.math.ArithmeticOperation;

public class MultiplicationOnWaves implements OperationOnWavesPluginIFace {
    @Override
    public void performOperation(DoubleWave input, DoubleWave output,
                                 int inputStartIndex, int inputEndIndex,
                                 int outputStartIndex, int outputEndIndex) {
        double[] inputWave = input.getSong();
        double[] outputWave = output.getSong();
        ArithmeticOperation.performOperationOnSamples(inputWave, outputWave, inputStartIndex, inputEndIndex,
                                                      outputStartIndex, outputEndIndex, ArithmeticOperation.MULTIPLY);
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
        return "Multiply waves";
    }

    @Override
    public String getPluginTooltip() {
        return "Multiply input wave (the first wave) with the output wave (the second one) and " +
               "stores result to the output wave";
    }
}
