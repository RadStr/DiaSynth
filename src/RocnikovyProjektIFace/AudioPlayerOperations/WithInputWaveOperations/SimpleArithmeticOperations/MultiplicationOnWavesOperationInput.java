package RocnikovyProjektIFace.AudioPlayerOperations.WithInputWaveOperations.SimpleArithmeticOperations;

import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginIFacesForUsers.WithInputWavePackage.AbstractPluginClass;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.MathOperationPackage.MathOperation;
import Rocnikovy_Projekt.Program;

public class MultiplicationOnWavesOperationInput extends AbstractPluginClass {
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
            outputStartIndex, outputEndIndex, MathOperation.MULTIPLY);
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
        return "Multiply waves";
    }

    @Override
    public String getPluginTooltip() {
        return "Multiply input wave (the first wave) with the output wave (the second one) and stores result to the output wave";
    }
}
