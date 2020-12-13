package player.plugin.plugins;

import player.plugin.ifaces.PluginIFacesForUsers.WithoutInputWavePackage.WithoutInputWavePluginIFace;
import player.plugin.ifaces.PluginParametersAnnotation;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.ProgramTest;

public class TestPluginWithParametersWithoutWaveWithDefaultJPaneInput implements WithoutInputWavePluginIFace {

    @PluginParametersAnnotation(lowerBound = "-1", upperBound = "1", defaultValue = "0.5", parameterTooltip = "double param")
    private double testParam1;
    @PluginParametersAnnotation
    private double testParam2;
    @PluginParametersAnnotation(lowerBound = "-0.5", upperBound = "0.5")
    private double testParam3;
    @PluginParametersAnnotation(lowerBound = "-20", upperBound = "20", defaultValue = "10", parameterTooltip = "int param")
    private int testParam4;
    @PluginParametersAnnotation
    private int testParam5;
    @PluginParametersAnnotation(lowerBound = "1", upperBound = "9.4")
    private double testParam6;



    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        ProgramTest.debugPrint("TestPluginWithParametersWithoutWaveWithCustomJPaneInput",
            testParam1, testParam2, testParam3, testParam4, testParam5);
        double[] song = audio.getSong();
        for(int i = startIndex; i < endIndex; i++) {
            switch(i % 6) {
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
                case 5 :
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
    public boolean isUsingDefaultJPanel() {
        return true;
    }

    @Override
    public String getPluginName() {
        return "TestPluginWithParametersWithoutWaveWithDefaultJPaneInput";
    }

    @Override
    public String getPluginTooltip() {
        return "This plugin uses the default pane and sets song to given parameters";
    }
}
