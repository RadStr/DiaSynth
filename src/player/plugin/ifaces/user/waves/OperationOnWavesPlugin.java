package player.plugin.ifaces.user.waves;

import player.plugin.ifaces.EnumWrapperForAnnotationPanelIFace;
import player.plugin.ifaces.PluginParameterAnnotation;
import Rocnikovy_Projekt.DoubleWave;

/**
 * This class should be used as base class for user defined plugin, because the method performOperation, performs alignment
 * of waves. So this class has internal input and output end indices which are changed for alignment.
 * The plugin inheriting from this has also has to have implements OperationOnWavesPluginIFace in signature, else it won't
 * be found as plugin.
 */
abstract public class OperationOnWavesPlugin implements OperationOnWavesPluginIFace, EnumWrapperForAnnotationPanelIFace {
    @PluginParameterAnnotation(name = "Length alignment:", defaultValue = "TRUE",
        parameterTooltip = "The enum which value tells what alignment should be done. Only changes the end indices not the start indices")
    private AlignmentEnum lengthAlignment = AlignmentEnum.NO_ALIGNMENT;
    public AlignmentEnum getLengthAlignment() {
        return lengthAlignment;
    }
    public void setLengthAlignment(AlignmentEnum val) {
        this.lengthAlignment = val;
    }

    private int inputEndIndex;
    public int getInputEndIndex() {
        return inputEndIndex;
    }
    public void setInputEndIndex(int val) {
        this.inputEndIndex = val;
    }

    private int outputEndIndex;
    public int getOutputEndIndex() {
        return outputEndIndex;
    }
    public void setOutputEndIndex(int val) {
        this.outputEndIndex = val;
    }

    @Override
    public void performOperation(DoubleWave input, DoubleWave output,
                                 int inputStartIndex, int inputEndIndex,
                                 int outputStartIndex, int outputEndIndex) {
        lengthAlignment.updateDataBasedOnEnumValue(this, inputStartIndex, inputEndIndex,
            outputStartIndex, outputEndIndex, input.getSongLength(), output.getSongLength());
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////// EnumWrapperForAnnotationPanelIFace
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String[] getEnumsToStrings(String fieldName) {
        if("lengthAlignment".equals(fieldName)) {
            return AlignmentEnum.getEnumsToStrings();
        }
        return null;
    }

    @Override
    public void setEnumValue(String value, String fieldName) {
        if("lengthAlignment".equals(fieldName)) {
            lengthAlignment = AlignmentEnum.convertStringToEnumValue(value);
        }
    }

    @Override
    public void setEnumValueToDefault(String fieldName) {
        setEnumValue(getDefaultEnumString(fieldName), fieldName);
    }

    @Override
    public String getDefaultEnumString(String fieldName) {
        if("lengthAlignment".equals(fieldName)) {
            return AlignmentEnum.getEnumsToStrings()[getDefaultIndex(fieldName)];
        }
        return "";
    }

    private int getDefaultIndex(String fieldName) {
        if("lengthAlignment".equals(fieldName)) {
            return 0;
        }
        return -1;
    }

    @Override
    public String getToolTipForComboBox(String fieldName) {
        if("lengthAlignment".equals(fieldName)) {
            return "<html>" +
                "NO_ALIGNMENT means that if the output is longer then <br>" +
                    "the input will be used more times to fill the output wave." +
                    "<br>Other options are self-explaining." +
                "</html>";
        }
        return "";
    }
}
