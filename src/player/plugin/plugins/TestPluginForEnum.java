package player.plugin.plugins;

import player.plugin.ifaces.EnumWrapperForAnnotationPanelIFace;
import player.plugin.ifaces.PluginParameterAnnotation;
import player.plugin.ifaces.user.wave.OperationOnWavePluginIFace;
import Rocnikovy_Projekt.DoubleWave;
import util.math.ArithmeticOperation;
import Rocnikovy_Projekt.Program;

public class TestPluginForEnum implements OperationOnWavePluginIFace, EnumWrapperForAnnotationPanelIFace {
    @PluginParameterAnnotation(parameterTooltip = "parameter for operations")
    private double value;
    @PluginParameterAnnotation
    private ArithmeticOperation arithmeticOperation = ArithmeticOperation.PLUS;

    @Override
    public void performOperation(DoubleWave audio, int startIndex, int endIndex) {
        double[] wave = audio.getSong();
        Program.performOperationOnSamples(wave, startIndex, endIndex, value, arithmeticOperation);
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
        return "Perform operation on samples - plugin";
    }

    @Override
    public String getPluginTooltip() {
        return "User chooses the value and the operation to perform";
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////// EnumWrapperForAnnotationPanelIFace
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String[] getEnumsToStrings(String fieldName) {
        if("arithmeticOperation".equals(fieldName)) {
            return ArithmeticOperation.getEnumsToStrings();
        }
        return null;
    }

    @Override
    public void setEnumValue(String value, String fieldName) {
        if("arithmeticOperation".equals(fieldName)) {
            arithmeticOperation = ArithmeticOperation.convertStringToEnumValue(value);
        }
    }

    @Override
    public void setEnumValueToDefault(String fieldName) {
        setEnumValue(getDefaultEnumString(fieldName), fieldName);
    }

    @Override
    public String getDefaultEnumString(String fieldName) {
        if("arithmeticOperation".equals(fieldName)) {
            return ArithmeticOperation.getEnumsToStrings()[getDefaultIndex(fieldName)];
        }
        return "";
    }

    private int getDefaultIndex(String fieldName) {
        if("arithmeticOperation".equals(fieldName)) {
            return 1;
        }
        return -1;
    }

    @Override
    public String getToolTipForComboBox(String fieldName) {
        if("arithmeticOperation".equals(fieldName)) {
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
