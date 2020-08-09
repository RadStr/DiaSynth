package RocnikovyProjektIFace.AudioPlayerOperations.WithoutInputWaveOperations.SimpleOperationWithSingleValue;

import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginIFacesForUsers.WithoutInputWavePackage.WithoutInputWavePluginIFace;
import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginParametersAnnotation;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.Program;

public class SetSamplesToValueOperation implements WithoutInputWavePluginIFace {
    @PluginParametersAnnotation(lowerBound = "-1", upperBound = "1",
        defaultValue = "0", parameterTooltip = "Represents the value to set the samples to.")
    private double value;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        setSamples(audio, startIndex, endIndex, value);
    }

    public static void setSamples(DoubleWave audio, int startIndex, int endIndex, double value) {
        double[] wave = audio.getSong();
        Program.setOneDimArr(wave, startIndex, endIndex, value);
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
        return "Set to value";
    }

    @Override
    public String getPluginTooltip() {
        return "Sets samples to value given by user";
    }
}
