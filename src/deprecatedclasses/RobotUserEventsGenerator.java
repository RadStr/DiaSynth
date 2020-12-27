package deprecatedclasses;

import java.awt.*;
import java.awt.event.InputEvent;

@Deprecated // This was one of the first tries to fix some issues with the infinite dragging of last splitter
public class RobotUserEventsGenerator {
    private Robot bot;

    public RobotUserEventsGenerator() {
        setBot();
    }

    private void setBot() {
        if (bot == null) {
            try {
                bot = new Robot();
            }
            catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    public void moveTo(Point p, Point pStartX, Point pEndX) {
//        //https://stackoverflow.com/questions/48837741/java-robot-mousemovex-y-not-producing-correct-results
//        int maxTimes = 10;
//        double currX;
//        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
//        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
//        for(int count = 0; ((currX = MouseInfo.getPointerInfo().getLocation().getX()) < pStartX.x || currX > pEndX.x ||
//            MouseInfo.getPointerInfo().getLocation().getY() != p.y) &&
//            count < maxTimes; count++) {
//            bot.mouseMove(p.x, p.y);
//        }

        moveTo(p.x, p.y, pStartX.x, pEndX.x);
    }

    public void moveTo(int x, int y, int startX, int endX) {
        //https://stackoverflow.com/questions/48837741/java-robot-mousemovex-y-not-producing-correct-results
        int maxTimes = 10;
        Point currPos;
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        for (int count = 0; ((currPos = MouseInfo.getPointerInfo().getLocation()).x < startX
                             || currPos.x > endX || currPos.y != y) && count < maxTimes; count++) {
            bot.mouseMove(x, y);
        }
    }

    public void moveTo(Point target) {
        moveTo(target.x, target.y);
    }

    public void moveTo(int x, int y) {
        int maxTimes = 10;
        for (int count = 0; (MouseInfo.getPointerInfo().getLocation().getX() != x ||
                             MouseInfo.getPointerInfo().getLocation().getY() != y) &&
                            count < maxTimes; count++) {
            bot.mouseMove(x, y);
        }
    }

    public void moveToWithClick(int x, int y, int startX, int endX) {
        moveTo(x, y, startX, endX);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void splitterMove(Point p, Point startX, Point endX) {
        splitterMove(p.x, p.y, startX.x, endX.x);
    }

    public void splitterMove(int x, int y, int startX, int endX) {
        Point currPos = MouseInfo.getPointerInfo().getLocation();

        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        moveTo(x, y, startX, endX);
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);


        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        moveTo(x, y - 20, startX, endX);
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
//        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);


        //moveTo(currPos);
//        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
//        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void moveToWithClickWithReturn(int x, int y) {
        Point currPos = MouseInfo.getPointerInfo().getLocation();
        moveTo(x, y);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        moveTo(currPos);
    }

    public void click(int buttons) {
        bot.mousePress(buttons);
    }

    public void release(int buttons) {
        bot.mouseRelease(buttons);
    }


    // Taken from https://stackoverflow.com/questions/19185162/how-to-simulate-a-real-mouse-click-using-java
//    public static Robot moveTo(int x, int y) throws AWTException {
//        Robot bot = new Robot();
//        bot.mouseMove(x, y);
//        return bot;
//    }
//
//    public static Robot click(int x, int y) throws AWTException {
//        Robot bot = moveTo(x, y);
//        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
//        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
//        return bot;
//    }
//
//    public static void clickWithReturn(int x, int y) throws AWTException {
//        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
//        Robot bot = click(x, y);
//        bot.mouseMove(mouseLoc.x, mouseLoc.y);
//    }
}
