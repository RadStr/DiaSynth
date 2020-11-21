package RocnikovyProjektIFace.AudioPlayerOperations.WithoutInputWaveOperations.SimpleOperationWithSingleValue;

import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginIFacesForUsers.WithoutInputWavePackage.WithoutInputWavePluginIFace;
import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginParametersAnnotation;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.MathOperationPackage.MathOperation;
import Rocnikovy_Projekt.Program;

public class MultiplyWithValueOperationInput implements WithoutInputWavePluginIFace {
    @PluginParametersAnnotation(name = "Multiplication constant:", lowerBound = "-1", upperBound = "1", defaultValue = "0",
        parameterTooltip = "is the constant to multiply the samples with")
    private double value;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] wave = audio.getSong();
        Program.operationOnSamples(wave, startIndex, endIndex, value, MathOperation.MULTIPLY);
    }

    @Override
    public boolean shouldWaitForParametersFromUser() {
        return true;
    }

    @Override
    public boolean isUsingDefaultJPane() {
        return true;
    }

    @Override
    public String getPluginName() {
        return "Multiply samples with value";
    }

    @Override
    public String getPluginTooltip() {
        return "Multiplies all samples with given parameter";
    }
}
