package synthesizer.gui.diagram.util.pairs;

import util.math.MathClass;

/**
 * If the size to be set is smaller than the minimum, then old value is kept.
 */
public class IntPairWithInternalDoublesWithMinAndMax extends IntPairWithInternalDoubles {
    public IntPairWithInternalDoublesWithMinAndMax() {
        this(0, 0, 1, 1, 65536, 65536);
    }
    public IntPairWithInternalDoublesWithMinAndMax(double first, double second, int firstMin, int secondMin, int firstMax, int secondMax) {
        this.firstMin = firstMin;
        this.secondMin = secondMin;
        this.firstMax = firstMax;
        this.secondMax = secondMax;
        setFirst(first);
        setSecond(second);
    }


    private final int firstMin;
    private final int secondMin;
    private final int firstMax;
    private final int secondMax;

    @Override
    protected void setFirst(double val) {
        if(firstMax < Integer.MAX_VALUE && firstMin > Integer.MIN_VALUE) {
            setFirstWithoutOverflow(val);
        }
        else {
            setFirstWithOverflow(val);
        }
    }

    @Override
    protected void setSecond(double val) {
        if(secondMax < Integer.MAX_VALUE && secondMin > Integer.MIN_VALUE) {
            setSecondWithoutOverflow(val);
        }
        else {
            setSecondWithOverflow(val);
        }
    }

    private void setFirstWithoutOverflow(double val) {
        if (val >= firstMin && val <= firstMax) {
            first = val;
            setFirstInt();
        }
    }
    private void setFirstWithOverflow(double val) {
        if (val >= firstMin && val <= firstMax && !MathClass.isIntOverflow(val)) {
            first = val;
            setFirstInt();
        }
    }


    private void setSecondWithoutOverflow(double val) {
        if (val >= secondMin && val <= secondMax) {
            second = val;
            secondInt = (int)Math.floor(second);
        }
    }
    private void setSecondWithOverflow(double val) {
        if (val >= secondMin && val <= secondMax && !MathClass.isIntOverflow(val)) {
            second = val;
            secondInt = (int)Math.floor(second);
        }
    }

    @Override
    public String toString() {
        String s = "{Doubles: [" + first + ", " + second + "]\tIntegers: [" + firstInt + ", " + secondInt + "]\tMinimums: [" + firstMin + ", " + secondMin + "]}";
        return s;
    }
}
