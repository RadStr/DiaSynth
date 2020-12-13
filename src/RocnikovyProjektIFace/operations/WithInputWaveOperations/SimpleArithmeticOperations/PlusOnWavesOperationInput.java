package RocnikovyProjektIFace.operations.WithInputWaveOperations.SimpleArithmeticOperations;

import RocnikovyProjektIFace.plugin.ifaces.PluginIFacesForUsers.WithInputWavePackage.AbstractPluginClass;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.math.ArithmeticOperation;
import Rocnikovy_Projekt.Program;

public class PlusOnWavesOperationInput extends AbstractPluginClass {
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
            outputStartIndex, outputEndIndex, ArithmeticOperation.PLUS);
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
        return "Add waves";
    }

    @Override
    public String getPluginTooltip() {
        return "Adds input wave (the first wave) to the output wave (the second one) and stores result to the output wave";
    }
}
