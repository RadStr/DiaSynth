package player.plugin.ifaces;

/**
 * Wrapper interface for the enums. FieldName is there to identify the enum in class. (There may be more enums in class)
 */
public interface EnumWrapperForAnnotationPanelIFace {
    String[] getEnumsToStrings(String fieldName);
    void setEnumValue(String value, String fieldName);
    void setEnumValueToDefault(String fieldName);
    String getDefaultEnumString(String fieldName);
    String getToolTipForComboBox(String fieldName);
}
