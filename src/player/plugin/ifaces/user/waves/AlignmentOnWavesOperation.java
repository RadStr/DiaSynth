package player.plugin.ifaces.user.waves;

import player.plugin.ifaces.user.waves.util.EndIndicesIntPair;
import plugin.EnumWrapperForAnnotationPanelIFace;
import plugin.PluginParameterAnnotation;
import util.audio.wave.DoubleWave;

/**
 * This class is used internally in program to set end indices based on alignment.
 */
public class AlignmentOnWavesOperation implements OperationOnWavesPluginIFace,
                                                  EnumWrapperForAnnotationPanelIFace {
    @PluginParameterAnnotation(name = "Length alignment:", defaultValue = "TRUE",
                               parameterTooltip = "The enum which value tells what alignment should be done. " +
                                                  "Only changes the end indices not the start indices")
    private AlignmentEnum lengthAlignment = AlignmentEnum.NO_ALIGNMENT;

    private EndIndicesIntPair endIndicesIntPair = new EndIndicesIntPair();


    @Override
    public void performOperation(DoubleWave input, DoubleWave output,
                                 int inputStartIndex, int inputEndIndex,
                                 int outputStartIndex, int outputEndIndex) {
        lengthAlignment.updateEndIndicesBasedOnEnumValue(endIndicesIntPair, inputStartIndex, inputEndIndex,
                                                         outputStartIndex, outputEndIndex, input.getSongLength(),
                                                         output.getSongLength());
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////// EnumWrapperForAnnotationPanelIFace
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String[] getEnumsToStrings(String fieldName) {
        if ("lengthAlignment".equals(fieldName)) {
            return AlignmentEnum.getEnumsToStrings();
        }
        return null;
    }

    @Override
    public void setEnumValue(String value, String fieldName) {
        if ("lengthAlignment".equals(fieldName)) {
            lengthAlignment = AlignmentEnum.convertStringToEnumValue(value);
        }
    }

    @Override
    public void setEnumValueToDefault(String fieldName) {
        setEnumValue(getDefaultEnumString(fieldName), fieldName);
    }

    @Override
    public String getDefaultEnumString(String fieldName) {
        if ("lengthAlignment".equals(fieldName)) {
            return AlignmentEnum.getEnumsToStrings()[getDefaultIndex(fieldName)];
        }
        return "";
    }

    private int getDefaultIndex(String fieldName) {
        if ("lengthAlignment".equals(fieldName)) {
            return 0;
        }
        return -1;
    }

    @Override
    public String getToolTipForComboBox(String fieldName) {
        if ("lengthAlignment".equals(fieldName)) {
            return "<html>" +
                   "NO_ALIGNMENT means that if the output is longer then <br>" +
                   "the input will be used more times to fill the output wave." +
                   "<br>Other options are self-explaining." +
                   "</html>";
        }
        return "";
    }

    /**
     * @return Returns tooltip which will be shown when hovering over the button which will perform the operation.
     */
    @Override
    public String getPluginTooltip() {
        return "Set alignment";
    }

    /**
     * @return Returns true if the operation needs parameters - so user needs to put them to the JPanel.
     * If it returns false, then it doesn't need parameters from user and the operation can start immediately
     */
    @Override
    public boolean shouldWaitForParametersFromUser() {
        return true;
    }

    /**
     * This parameter matters only when shouldWaitForParametersFromUser returns true
     *
     * @return
     */
    @Override
    public boolean isUsingPanelCreatedFromAnnotations() {
        return true;
    }

    @Override
    public String getPluginName() {
        return "Alignment";
    }
}
