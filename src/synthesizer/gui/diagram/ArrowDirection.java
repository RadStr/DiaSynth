package synthesizer.gui.diagram;

import java.awt.*;
import java.awt.geom.AffineTransform;

public enum ArrowDirection {
    NORTH {
        public void transformArrowPointingRight(Point[] arrowPointingRight, Point[] outputArrow) {
            double angle = Math.toRadians(-90);
            transform(angle, arrowPointingRight, outputArrow);
        }
    },
    NORTH_EAST {
        public void transformArrowPointingRight(Point[] arrowPointingRight, Point[] outputArrow) {
            double angle = Math.toRadians(-45);
            transform(angle, arrowPointingRight, outputArrow);
        }
    },
    EAST {
        public void transformArrowPointingRight(Point[] arrowPointingRight, Point[] outputArrow) {
            // Already facing right.
            for (int i = 0; i < arrowPointingRight.length; i++) {
                outputArrow[i].x = arrowPointingRight[i].x;
                outputArrow[i].y = arrowPointingRight[i].y;
            }
        }
    },
    SOUTH_EAST {
        public void transformArrowPointingRight(Point[] arrowPointingRight, Point[] outputArrow) {
            double angle = Math.toRadians(45);
            transform(angle, arrowPointingRight, outputArrow);
        }
    },
    SOUTH {
        public void transformArrowPointingRight(Point[] arrowPointingRight, Point[] outputArrow) {
            double angle = Math.toRadians(90);
            transform(angle, arrowPointingRight, outputArrow);
        }
    },
    SOUTH_WEST {
        public void transformArrowPointingRight(Point[] arrowPointingRight, Point[] outputArrow) {
            double angle = Math.toRadians(135);
            transform(angle, arrowPointingRight, outputArrow);
        }
    },
    WEST {
        public void transformArrowPointingRight(Point[] arrowPointingRight, Point[] outputArrow) {
            double angle = Math.toRadians(180);
            transform(angle, arrowPointingRight, outputArrow);
        }
    },
    NORTH_WEST {
        public void transformArrowPointingRight(Point[] arrowPointingRight, Point[] outputArrow) {
            double angle = Math.toRadians(225);
            transform(angle, arrowPointingRight, outputArrow);
        }
    };

    /**
     * Transforms arrow which is given as set of points to be facing the location given by the enum
     * @param arrowPointingRight are the points of the arrow. Starting at top left of the rectangle and going "clockwise" (so the first turn is to right)
     * @param outputArrow is the array to which will be stored the output arrow, should be at least the length of input array. (7+ for arrow). The order of elements is the same in the input array
     */
    abstract void transformArrowPointingRight(Point[] arrowPointingRight, Point[] outputArrow);

    private static void transform(double angle, Point[] arrowPointingRight, Point[] outputArrow) {
        Point mid = new Point(arrowPointingRight[3]);
        mid.x = (arrowPointingRight[0].x + mid.x) / 2;
        AffineTransform.getRotateInstance(angle, mid.x, mid.y).transform(arrowPointingRight,0, outputArrow,0, arrowPointingRight.length);
    }
}
