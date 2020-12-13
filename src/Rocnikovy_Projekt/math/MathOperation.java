package Rocnikovy_Projekt.math;

public enum MathOperation {
    PLUS,
    MULTIPLY,
    LOG,
    POWER;

    public static String[] getEnumsToStrings() {
        MathOperation[] values = MathOperation.values();
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = values[i].toString();
        }

        return strings;
    }


    public static MathOperation convertStringToEnumValue(String s) {
        MathOperation[] values = MathOperation.values();
        for (MathOperation v : values) {
            if(v.toString().equals(s)) {
                return v;
            }
        }

        return null;
    }
}
