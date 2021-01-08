package str.rad.player.operations.wave;

import str.rad.player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import str.rad.util.audio.wave.DoubleWave;


public class WaveStretcherMaximumOnWave implements OperationOnWavePluginIFace {
    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        WaveStretcherOnWave.stretchWave(audio, startIndex, endIndex, 1);
    }

    @Override
    public boolean shouldWaitForParametersFromUser() {
        return false;
    }

    @Override
    public boolean isUsingPanelCreatedFromAnnotations() {
        return false;
    }

    @Override
    public String getPluginName() {
        return "Maximum wave stretcher";
    }

    @Override
    public String getPluginTooltip() {
        return "<html>" +
               "Vertically stretches (or shrinks) the wave so that the max absolute value in the new wave is equal to 1" +
               "</html>";
    }
}
