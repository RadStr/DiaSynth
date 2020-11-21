package RocnikovyProjektIFace.AudioPlayerOperations.WithoutInputWaveOperations.SimpleOperationWithSingleValue;

import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginIFacesForUsers.WithoutInputWavePackage.WithoutInputWavePluginIFace;
import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginParametersAnnotation;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.MathOperationPackage.MathOperation;
import Rocnikovy_Projekt.Program;

public class PlusWithValueOperationInput implements WithoutInputWavePluginIFace {
    @PluginParametersAnnotation(name = "Addition constant:", lowerBound = "-1", upperBound = "1", defaultValue = "0",
        parameterTooltip = "is the constant to be added to the samples")
    private double value;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] wave = audio.getSong();
        Program.operationOnSamples(wave, startIndex, endIndex, value, MathOperation.PLUS);
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
        return "Add value to samples";
    }

    @Override
    public String getPluginTooltip() {
        return "Adds given parameter to all samples";
    }
}
