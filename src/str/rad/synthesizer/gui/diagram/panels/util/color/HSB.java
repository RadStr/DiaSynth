package str.rad.synthesizer.gui.diagram.panels.util.color;

public class HSB {
    public HSB() {
        // EMPTY
    }

    public HSB(double h, double s, double b) {
        this.h = h;
        this.s = s;
        this.b = b;
    }

    public double h, s, b;

    public void add(double ha, double sa, double ba) {
        h += ha;
        s += sa;
        b += ba;
    }

    public void add(HSB hsb) {
        add(hsb.h, hsb.s, hsb.b);
    }


    public void negate() {
        h *= -1;
        s *= -1;
        b *= -1;
    }

    /**
     * Calculates size of one step between the given instance and the target parameter for number of steps specified in
     * stepCount. The result is stored in jumps.
     *
     * @param target    is the target location to which we want to move in stepCount steps.
     * @param stepCount is number of steps.
     * @param jumps     is the output parameter.
     */
    public void getJumps(HSB target, int stepCount, HSB jumps) {
        jumps.h = getJumpH(h, target.h, stepCount);
        jumps.s = getJumpSorB(s, target.s, stepCount);
        jumps.b = getJumpSorB(b, target.b, stepCount);
    }


    private static double getJumpH(double h, double targetH, double stepCount) {
        // Contains clockwise path from h to zero or targetH to zero (depending on which is closer)
        double clockwisePathToZero;
        double clockwisePath;               // Clockwise from h to targetH
        double counterClockwisePath;
        // Find the shortest path between h and targetH and from that calculate jump per 1 step.
        if (h > targetH) {
            clockwisePathToZero = 1 - h;
            clockwisePath = clockwisePathToZero + targetH;
            counterClockwisePath = h - targetH;
        }
        else {
            clockwisePath = targetH - h;
            clockwisePathToZero = 1 - targetH;
            counterClockwisePath = clockwisePathToZero + h;
        }


        double jump;
        if (clockwisePath < counterClockwisePath) {
            jump = clockwisePath / stepCount;
        }
        else {
            jump = -counterClockwisePath / stepCount;
        }
        return jump;
    }

    private static double getJumpSorB(double val, double targetVal, double stepCount) {
        double jump = (targetVal - val) / stepCount;
        return jump;
    }
}
