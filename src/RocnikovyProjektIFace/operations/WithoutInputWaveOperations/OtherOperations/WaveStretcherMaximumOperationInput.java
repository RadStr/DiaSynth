package RocnikovyProjektIFace.operations.WithoutInputWaveOperations.OtherOperations;

import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginIFacesForUsers.WithoutInputWavePackage.WithoutInputWavePluginIFace;
import Rocnikovy_Projekt.DoubleWave;


public class WaveStretcherMaximumOperationInput implements WithoutInputWavePluginIFace {
    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        WaveStretcherOperationInput.stretchWave(audio, startIndex, endIndex, 1);
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
        return "Maximum wave stretcher";
    }

    @Override
    public String getPluginTooltip() {
        return "<html>" +
            "Vertically stretches the wave so that the max absolute value in the new wave reaches newAbsoluteMax height<br>" +
            "(if it is larger than the wave range [-1,1] then" +
            "It stretches it in such a way that there is no overflow so some sample reaches -1 or 1 but not anything out of range)<br>" +
            "</html>";
    }
}
