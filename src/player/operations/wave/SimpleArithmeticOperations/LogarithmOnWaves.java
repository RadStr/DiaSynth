package player.operations.wave.SimpleArithmeticOperations;

import player.plugin.ifaces.PluginIFacesForUsers.WithInputWavePackage.AbstractPluginClass;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.math.ArithmeticOperation;
import Rocnikovy_Projekt.Program;

public class LogarithmOnWaves extends AbstractPluginClass {
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
            outputStartIndex, outputEndIndex, ArithmeticOperation.LOG);
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
        return "Logarithm waves";
    }

    @Override
    public String getPluginTooltip() {
        return "<html>" +
            "Logarithms samples of the input wave (the first wave) with base of output wave samples (the second one) and stores result to the output wave<br>" +
            "log_outputWave[i](inputWave[i])" +
            "</html>";
    }
}
