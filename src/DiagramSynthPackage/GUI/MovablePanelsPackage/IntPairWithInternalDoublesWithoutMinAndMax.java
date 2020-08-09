package DiagramSynthPackage.GUI.MovablePanelsPackage;

import java.awt.*;

public class IntPairWithInternalDoublesWithoutMinAndMax {
    public IntPairWithInternalDoublesWithoutMinAndMax() {
        this(0, 0);
    }
    public IntPairWithInternalDoublesWithoutMinAndMax(double first, double second) {
        setFirst(first);
        setSecond(second);
    }


    protected double first;
    public double getFirstDouble() {
        return first;
    }
    protected void setFirst(double val) {
        if(!MathClass.isIntOverflow(val)) {
            first = val;
            firstInt = (int)Math.floor(first);
        }
    }

    protected double second;
    public double getSecondDouble() {
        return second;
    }
    protected void setSecond(double val) {
        if(!MathClass.isIntOverflow(val)) {
            second = val;
            secondInt = (int)Math.floor(second);
        }
    }

    public void set(double first, double second) {
        setFirst(first);
        setSecond(second);
    }

    protected int firstInt;
    public int getFirst() {
        return firstInt;
    }
    protected int secondInt;
    public int getSecond() {
        return secondInt;
    }

    public void addFirst(double first) {
        double sum = this.first + first;
        setFirst(sum);
    }
    public void substractFirst(double first) {
        addFirst(-first);
    }

    public void addSecond(double second) {
        double sum = this.second + second;
        setSecond(sum);
    }
    public void subtractSecond(double second) {
        addSecond(-second);
    }

    public void add(double first, double second) {
        addFirst(first);
        addSecond(second);
    }

    public void subtract(double first, double second) {
        add(-first, -second);
    }


    public void multiplyPairByN(double n) {
        setFirst(first * n);
        setSecond(second * n);
    }

    public void dividePairByN(double n) {
        setFirst(first / n);
        setSecond(second / n);
    }

    public void copy(IntPairWithInternalDoublesWithMinAndMax instanceCopied) {
        this.set(instanceCopied.getFirstDouble(), instanceCopied.getSecondDouble());
    }


    public Dimension getInternalsAsDimension() {
        return new Dimension(firstInt, secondInt);
    }


    public boolean equals(Dimension d) {
        return d.width == this.firstInt && d.height == this.secondInt;
    }

    @Override
    public boolean equals(Object o) {
        IntPairWithInternalDoublesWithoutMinAndMax pair = (IntPairWithInternalDoublesWithoutMinAndMax)o;
        return this.first == pair.first && this.second == pair.second;
    }

    @Override
    public String toString() {
        String s = "{Doubles: [" + first + ", " + second + "]\tIntegers: [" + firstInt + ", " + secondInt + "]}";
        return s;
    }
}
