package synthesizer.GUI.MovablePanelsPackage.ShapedPanels.Internals;

import Rocnikovy_Projekt.Program;

import javax.swing.*;
import java.awt.*;

public class ConstantTextInternals implements ShapedPanelInternals {
    public static final int DEFAULT_RECTANGLE_WIDTH_DECREASE_DIV_FACTOR = 2;
    public static final int DEFAULT_RECTANGLE_HEIGHT_DECREASE_DIV_FACTOR = 2;
    public static final int DEFAULT_RECTANGLE_START_X_DECREASE_DIV_FACTOR = -32;
    public static final int DEFAULT_RECTANGLE_START_Y_DECREASE_DIV_FACTOR = Integer.MAX_VALUE;


    // Used by Dynamic text internals
    private void setRectangleParameters(int rectangleWidthDecreaseDivFactor,
                                    int rectangleHeightDecreaseDivFactor, int rectangleStartXDecreaseDivFactor,
                                    int rectangleStartYDecreaseDivFactor) {
        RECTANGLE_WIDTH_DECREASE_DIV_FACTOR = rectangleWidthDecreaseDivFactor;
        RECTANGLE_HEIGHT_DECREASE_DIV_FACTOR = rectangleHeightDecreaseDivFactor;
        RECTANGLE_START_X_DECREASE_DIV_FACTOR = rectangleStartXDecreaseDivFactor;
        RECTANGLE_START_Y_DECREASE_DIV_FACTOR = rectangleStartYDecreaseDivFactor;
    }

    public ConstantTextInternals(JLabel label, int rectangleWidthDecreaseDivFactor,
                                 int rectangleHeightDecreaseDivFactor, int rectangleStartXDecreaseDivFactor,
                                 int rectangleStartYDecreaseDivFactor) {
        setRectangleParameters(rectangleWidthDecreaseDivFactor, rectangleHeightDecreaseDivFactor,
                rectangleStartXDecreaseDivFactor, rectangleStartYDecreaseDivFactor);
        textLabel = label;
    }

    public ConstantTextInternals(String text, int rectangleWidthDecreaseDivFactor,
                                 int rectangleHeightDecreaseDivFactor, int rectangleStartXDecreaseDivFactor,
                                 int rectangleStartYDecreaseDivFactor) {
        setRectangleParameters(rectangleWidthDecreaseDivFactor, rectangleHeightDecreaseDivFactor,
                rectangleStartXDecreaseDivFactor, rectangleStartYDecreaseDivFactor);
        textLabel = new JLabel(text);
    }


    public ConstantTextInternals(String text) {
        this(text,
             DEFAULT_RECTANGLE_WIDTH_DECREASE_DIV_FACTOR,
             DEFAULT_RECTANGLE_HEIGHT_DECREASE_DIV_FACTOR,
             DEFAULT_RECTANGLE_START_X_DECREASE_DIV_FACTOR,
             DEFAULT_RECTANGLE_START_Y_DECREASE_DIV_FACTOR);
    }

    private int RECTANGLE_WIDTH_DECREASE_DIV_FACTOR;
    private int RECTANGLE_HEIGHT_DECREASE_DIV_FACTOR;
    private int RECTANGLE_START_X_DECREASE_DIV_FACTOR;
    private int RECTANGLE_START_Y_DECREASE_DIV_FACTOR;

    // The label isn't used, but it is here to have fontmetrics which will be used for drawing
    // It isn't used because then the blinking wouldn't be drawn correctly
    protected final JLabel textLabel;
    public JLabel getLabel() {
        return textLabel;
    }

    // For optimization - to approximate new font size
    private int oldWidth = -1;

    @Override
    public void reshape(Dimension newSize) {
        // Basically same as CenteredRectanglesInternals but there it was for circle/rectangle where width = height
        int startX = 0;
        int startY = 0;

        int width = newSize.width;
        int height = newSize.height;
        if(RECTANGLE_WIDTH_DECREASE_DIV_FACTOR != Integer.MAX_VALUE) {
            startX = newSize.width / RECTANGLE_WIDTH_DECREASE_DIV_FACTOR;
            width -= startX;
            startX /= 2;
        }
        if(RECTANGLE_HEIGHT_DECREASE_DIV_FACTOR != Integer.MAX_VALUE) {
            startY = newSize.height / RECTANGLE_HEIGHT_DECREASE_DIV_FACTOR;
            height -= startY;
            startY /= 2;
        }

        if(RECTANGLE_START_X_DECREASE_DIV_FACTOR != Integer.MAX_VALUE) {
            startX -= newSize.width / RECTANGLE_START_X_DECREASE_DIV_FACTOR;
        }
        if(RECTANGLE_START_Y_DECREASE_DIV_FACTOR != Integer.MAX_VALUE) {
            startY -= newSize.height / RECTANGLE_START_Y_DECREASE_DIV_FACTOR;
        }


        // Approximation
        if(oldWidth == -1) {
            oldWidth = width;
        }
        else {
            Program.setFontSize(textLabel, oldWidth, width);
            oldWidth = width;
        }
        // End of Approximation

        Program.getBiggestFontToFitSize(textLabel, width, height);
        Program.setLabelLocWithSpace(textLabel, startX, startY, width, height);
// TODO: RML
        // TODO: DEBUG
//        ProgramTest.debugPrint("Text:", textLabel.getText(), textLabel.getX(), textLabel.getY(),
//                textLabel.getWidth(), textLabel.getHeight(), textLabel.getFont(), textLabel.getPreferredSize(), newSize);
        // TODO: DEBUG
// TODO: RML
    }

    @Override
    public void draw(Graphics g) {
        g.setFont(textLabel.getFont());
        FontMetrics fm = g.getFontMetrics();
// TODO: RML
// TODO: DEBUG
//        ProgramTest.debugPrint("Draw:", fm.getHeight(), fm.getAscent(), fm.getDescent());
// TODO: DEBUG
// TODO: RML
        g.drawString(textLabel.getText(), textLabel.getX(), textLabel.getY() + fm.getAscent());
    }

    @Override
    public ShapedPanelInternals createCopy() {
        return new ConstantTextInternals(textLabel.getText(),
                RECTANGLE_WIDTH_DECREASE_DIV_FACTOR, RECTANGLE_HEIGHT_DECREASE_DIV_FACTOR,
                RECTANGLE_START_X_DECREASE_DIV_FACTOR, RECTANGLE_START_Y_DECREASE_DIV_FACTOR);
    }
}