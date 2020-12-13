package RocnikovyProjektIFace.operations.WithoutInputWaveOperations.OtherOperations;

import RocnikovyProjektIFace.AudioPlayerPlugins.ifaces.PluginIFacesForUsers.WithoutInputWavePackage.WithoutInputWavePluginIFace;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.math.ArithmeticOperation;
import Rocnikovy_Projekt.Program;

public class InvertOperationInput implements WithoutInputWavePluginIFace {
    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] wave = audio.getSong();
        int len = endIndex - startIndex;
        Program.performOperationOnSamples(wave, wave, startIndex, startIndex, len, (double)-1, ArithmeticOperation.MULTIPLY);
    }

    @Override
    public boolean shouldWaitForParametersFromUser() {
        return false;
    }

    @Override
    public boolean isUsingDefaultJPanel() {
        return false;
    }

    @Override
    public String getPluginName() {
        return "Invert wave";
    }

    @Override
    public String getPluginTooltip() {
        return "Inverts the given part of wave";
    }
}
