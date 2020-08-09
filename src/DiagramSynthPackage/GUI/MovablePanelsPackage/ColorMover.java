package DiagramSynthPackage.GUI.MovablePanelsPackage;// TODO: Prepsat - zmenil jsem zpusob
// TODO: Mozna jen staci to mit static - protoze to bude asi pro vsechny stejnym takze mi staci kdyz budu mit instanci toho ColorMoveru mit static
/**
 * This class lets you set 2 points - start and end. and allows you to change currPos.
 * And also let's you change boolean variable isUniversalMove.
 * Basically what this class does it moves on 2 sides of triangle. 1 side is moving between start and end positions.
 * That is if isUniversalMove == false.
 * Otherwise it is move from currPos to the either currPos to start or end position based on isMovingToStartPos variable.
 * currPos variable is being changed while moving.
 */
public class ColorMover {
    public ColorMover(HSB currPos, double startH, double startS, double startB, double endH, double endS, double endB, int stepCount) {
        this(currPos.h, currPos.s, currPos.b, startH, startS, startB, endH, endS, endB, stepCount);
    }

    public ColorMover(double currH, double currS, double currB, double startH, double startS, double startB, double endH, double endS, double endB, int stepCount) {
        defaultPos = new HSB(currH, currS, currB);
        startPos = new HSB(startH, startS, startB);
        endPos = new HSB(endH, endS, endB);
        startToEndJump = new HSB();
        defaultPosJump = new HSB();
        resetMoveVarsAndCurrPos();
        setStepCount(stepCount);
    }


    private final HSB defaultPos;
    private HSB currPos;
    private HSB startPos;
    private HSB endPos;
    private HSB defaultPosJump;
    private HSB startToEndJump;
    private boolean isMovingToStartPos;
    private boolean isUniversalMove;
    private int currStep;
    private int stepCount;

    public void setStepCount(int val) {
        stepCount = val;
        resetSteps();
    }

    private void resetSteps() {
        currStep = 0;
        setOneStepBetweenStartAndEnd();
        setOneStepUniversal();
    }

    private void resetMoveVarsAndCurrPos() {
        currPos = new HSB(defaultPos.h, defaultPos.s, defaultPos.b);
        isUniversalMove = true;
        isMovingToStartPos = true;
    }

    public void reset() {
        resetMoveVarsAndCurrPos();
        resetSteps();
    }

    private void setOneStepBetweenStartAndEnd() {
        if (isMovingToStartPos) {
            endPos.getJumps(startPos, stepCount, startToEndJump);
        } else {
            startPos.getJumps(endPos, stepCount, startToEndJump);
        }
    }

    private void setOneStepUniversal() {
        if (isMovingToStartPos) {
            currPos.getJumps(startPos, stepCount, defaultPosJump);
        } else {
            currPos.getJumps(endPos, stepCount, defaultPosJump);
        }
    }

    private void moveOneStepBetweenStartAndEnd() {
        currPos.add(startToEndJump);
    }

    private void moveOneStepUniversal() {
        currPos.add(defaultPosJump);
    }

    public HSB moveOneStep() {
        if (currStep < stepCount) {
            if (isUniversalMove) {
                moveOneStepUniversal();
            } else {
                moveOneStepBetweenStartAndEnd();
            }
            currStep++;
        } else {
            currStep = 0;
            if (isUniversalMove) {
                isUniversalMove = false;
            }
            changeStartToEndDirection();
            return moveOneStep();
        }

        return currPos;
    }

    private void changeStartToEndDirection() {
        isMovingToStartPos = !isMovingToStartPos;
        startToEndJump.negate();
    }
}

