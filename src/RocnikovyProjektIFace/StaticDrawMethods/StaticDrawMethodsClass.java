package RocnikovyProjektIFace.StaticDrawMethods;


import java.awt.*;

public class StaticDrawMethodsClass {
    // https://stackoverflow.com/questions/19386951/how-to-draw-a-circle-with-given-x-and-y-coordinates-as-the-middle-spot-of-the-ci
    public static void drawCenteredCircle(Graphics g, int x, int y, int r) {
        x = x - r;
        y = y - r;
        int d = 2 * r;
        g.fillOval(x, y, d, d);
    }

}
