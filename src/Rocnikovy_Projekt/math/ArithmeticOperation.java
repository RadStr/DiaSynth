package Rocnikovy_Projekt.math;

public enum ArithmeticOperation {
    PLUS,
    MULTIPLY,
    LOG,
    POWER;

    public static String[] getEnumsToStrings() {
        ArithmeticOperation[] values = ArithmeticOperation.values();
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = values[i].toString();
        }

        return strings;
    }


    public static ArithmeticOperation convertStringToEnumValue(String s) {
        ArithmeticOperation[] values = ArithmeticOperation.values();
        for (ArithmeticOperation v : values) {
            if(v.toString().equals(s)) {
                return v;
            }
        }

        return null;
    }
}
