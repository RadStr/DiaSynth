package synthesizer.gui.diagram.panels.util;

public class MathClass {
    private MathClass() {}  // To make this class accessible only in static way

    /**
     * Taken from the java source codes, java.lang.Math.addExact. Return true if the sum of the arguments overflows.
     * @param x is the first operand of sum.
     * @param y is the second operand of sum.
     * @return
     */
    public static boolean isAddOverflow(int x, int y) {
        int r = x + y;
        // HD 2-12 Overflow iff both arguments have the opposite sign of the result
        // Comment from diasynth creator: I guess that it makes sense, overflow (underflow) can occur only if we add 2 operands of the same sign.
        return ((x ^ r) & (y ^ r)) < 0;
    }

    public static boolean isAddIntOverflow(double x, double y) {
        double s = x + y;
        return isIntOverflow(s);
    }

    public static boolean isIntOverflow(double s) {
        return s > Integer.MAX_VALUE || s < Integer.MIN_VALUE;
    }
}
