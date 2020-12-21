package player.operations.waves.arithmetic;

import player.plugin.ifaces.user.waves.OperationOnWavesPlugin;
import util.audio.wave.DoubleWave;
import util.math.ArithmeticOperation;

public class AdditionOnWaves extends OperationOnWavesPlugin {
    @Override
    public void performOperation(DoubleWave input, DoubleWave output,
                                 int inputStartIndex, int inputEndIndex,
                                 int outputStartIndex, int outputEndIndex) {
        super.performOperation(input, output, inputStartIndex, inputEndIndex, outputStartIndex, outputEndIndex);
        double[] inputWave = input.getSong();
        double[] outputWave = output.getSong();
        inputEndIndex = getInputEndIndex();
        outputEndIndex = getOutputEndIndex();
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
