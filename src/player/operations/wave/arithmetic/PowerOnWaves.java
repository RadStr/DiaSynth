package player.operations.wave.arithmetic;

import player.plugin.ifaces.PluginIFacesForUsers.wave.AbstractPluginClass;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.math.ArithmeticOperation;
import Rocnikovy_Projekt.Program;

public class PowerOnWaves extends AbstractPluginClass {
    @Override
    public void performOperation(DoubleWave input, DoubleWave output,
                                 int inputStartIndex, int inputEndIndex,
                                 int outputStartIndex, int outputEndIndex) {
        super.performOperation(input, output, inputStartIndex, inputEndIndex, outputStartIndex, outputEndIndex);
        double[] inputWave = input.getSong();
        double[] outputWave = output.getSong();
        inputEndIndex = getInputEndIndex();
        outputEndIndex = getOutputEndIndex();
        Program.performOperationOnSamples(inputWave, outputWave, inputStartIndex, inputEndIndex,
            outputStartIndex, outputEndIndex, ArithmeticOperation.POWER);
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
        return "Power waves";
    }

    @Override
    public String getPluginTooltip() {
        return "<html>" +
            "Raises samples of the input wave (the first wave) to the power of output wave samples (the second one) and stores result to the output wave<br>" +
            "inputWave[i] ^ outputWave[i]" +
            "</html>";
    }
}
