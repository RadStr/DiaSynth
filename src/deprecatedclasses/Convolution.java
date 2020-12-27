package deprecatedclasses;

import player.plugin.ifaces.user.waves.OperationOnWavesPlugin;
import util.audio.wave.DoubleWave;

@Deprecated
public class Convolution extends OperationOnWavesPlugin {
    @Override
    public void performOperation(DoubleWave input, DoubleWave output,
                                 int inputStartIndex, int inputEndIndex,
                                 int outputStartIndex, int outputEndIndex) {
        super.performOperation(input, output, inputStartIndex, inputEndIndex, outputStartIndex, outputEndIndex);
        double[] inputWave = input.getSong();
        double[] outputWave = output.getSong();
        inputEndIndex = getInputEndIndex();
        outputEndIndex = getOutputEndIndex();
        // TODO: Konvoluce je napsana dobre, ale musim udelat ty fft pole stejne dlouhy a doplnit je 0ma aby se mohla
        // TODO: ta konvoluce provest, pokud chci aby to fungovalo jako echo efekt, tak musim povolit zvetseni te vlny a mit parametr na delku echa
        //ByteWave.convolutionInFreqDomainRealForward();
    }


    @Override
    public boolean shouldWaitForParametersFromUser() {
        return true;
    }

    @Override
    public boolean isUsingPanelCreatedFromAnnotations() {
        return true;
    }

    @Override
    public String getPluginName() {
        return "Convolution";
    }

    @Override
    public String getPluginTooltip() {
        return "<html>" +
               "Puts result of convolution between first and second wave to the second wave" +
               "</html>";
    }
}