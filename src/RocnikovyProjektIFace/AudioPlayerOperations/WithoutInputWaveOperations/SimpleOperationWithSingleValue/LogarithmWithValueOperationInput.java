package RocnikovyProjektIFace.AudioPlayerOperations.WithoutInputWaveOperations.SimpleOperationWithSingleValue;

import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginIFacesForUsers.WithoutInputWavePackage.WithoutInputWavePluginIFace;
import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginParametersAnnotation;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.MathOperationPackage.MathOperation;
import Rocnikovy_Projekt.Program;

public class LogarithmWithValueOperationInput implements WithoutInputWavePluginIFace {
    // "" + Double.MIN_NORMAL is workaround (it has to be constant, so I can't use toString)
    @PluginParametersAnnotation(lowerBound = "" + Double.MIN_NORMAL, defaultValue = "0",
        parameterTooltip = "is the base of the logarithm")
    private double value;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] wave = audio.getSong();
        Program.operationOnSamples(wave, startIndex, endIndex, value, MathOperation.LOG);
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
        return "Logarithm samples";
    }

    @Override
    public String getPluginTooltip() {
        return "Logarithm the samples with base of user given value";
    }
}
