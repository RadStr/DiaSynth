package str.rad.player.plugin.plugins;

import str.rad.player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import str.rad.plugin.PluginParameterAnnotation;
import str.rad.util.audio.wave.DoubleWave;

public class TestPluginWithParametersWithSingleInputWaveWithPanelFromAnnotations implements OperationOnWavePluginIFace {

    @PluginParameterAnnotation(lowerBound = "-1", upperBound = "1", defaultValue = "0.5", parameterTooltip = "double param")
    private double testParam1;
    @PluginParameterAnnotation
    private double testParam2;
    @PluginParameterAnnotation(lowerBound = "-0.5", upperBound = "0.5")
    private double testParam3;
    @PluginParameterAnnotation(lowerBound = "-20", upperBound = "20", defaultValue = "10", parameterTooltip = "int param")
    private int testParam4;
    @PluginParameterAnnotation
    private int testParam5;
    @PluginParameterAnnotation(lowerBound = "1", upperBound = "9.4")
    private double testParam6;


    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] song = audio.getSong();
        for (int i = startIndex; i < endIndex; i++) {
            switch (i % 6) {
                case 0:
                    song[i] = testParam1;
                    break;
                case 1:
                    song[i] = testParam2;
                    break;
                case 2:
                    song[i] = testParam3;
                    break;
                case 3:
//                    song[i] = testParam4 / (double)20;
                    song[i] = testParam4;
                    break;
                case 4:
                    song[i] = testParam5;
                    break;
                case 5:
                    song[i] = testParam6;
                    break;
                default:
                    System.exit(489);
            }
        }
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
        return "TestPluginWithParametersWithSingleInputWaveWithPanelFromAnnotations";
    }

    @Override
    public String getPluginTooltip() {
        return "This plugin uses the default pane and sets song to given parameters";
    }
}
