package RocnikovyProjektIFace.AudioPlayerPlugins.Plugins;

import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.EnumWrapperIFaceForDefaultJPanel;
import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginIFacesForUsers.WithoutInputWavePackage.WithoutInputWavePluginIFace;
import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginParametersAnnotation;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.MathOperationPackage.MathOperation;
import Rocnikovy_Projekt.Program;

public class TestingEnumWithValue implements WithoutInputWavePluginIFace, EnumWrapperIFaceForDefaultJPanel {
    @PluginParametersAnnotation(parameterTooltip = "parameter for operations")
    private double value;
    @PluginParametersAnnotation
    private MathOperation mathOperation = MathOperation.PLUS;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] wave = audio.getSong();
        Program.performOperationOnSamples(wave, startIndex, endIndex, value, mathOperation);
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
        return "Perform operation on samples - plugin";
    }

    @Override
    public String getPluginTooltip() {
        return "User chooses the value and the operation to perform";
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////// EnumWrapperIFaceForDefaultJPanel
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String[] getEnumsToStrings(String fieldName) {
        if("mathOperation".equals(fieldName)) {
            return MathOperation.getEnumsToStrings();
        }
        return null;
    }

    @Override
    public void setEnumValue(String value, String fieldName) {
        if("mathOperation".equals(fieldName)) {
            mathOperation = MathOperation.convertStringToEnumValue(value);
        }
    }

    @Override
    public void setEnumValueToDefault(String fieldName) {
        setEnumValue(getDefaultEnumString(fieldName), fieldName);
    }

    @Override
    public String getDefaultEnumString(String fieldName) {
        if("mathOperation".equals(fieldName)) {
            return MathOperation.getEnumsToStrings()[getDefaultIndex(fieldName)];
        }
        return "";
    }

    private int getDefaultIndex(String fieldName) {
        if("mathOperation".equals(fieldName)) {
            return 1;
        }
        return -1;
    }

    @Override
    public String getToolTipForComboBox(String fieldName) {
        if("mathOperation".equals(fieldName)) {
            return "<html>" +
                "PLUS is addition<br>" +
                "MULTIPLY is multiplication<br>" +
                "LOG is logarithm<br>" +
                "POWER is power" +
                "</html>";
        }
        return "";
    }
}
