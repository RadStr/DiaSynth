package str.rad.player.wave;

import str.rad.util.Aggregation;
import str.rad.util.audio.AudioUtilities;
import str.rad.util.swing.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class VerticalReferencesPanel extends JPanel {
    private int valuesLongestWidth;
    private double pixelJump;
    private int labelCount;
    private int minLineLen = 5;
    private double minValue;
    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
        setMidValue();
    }

    private double maxValue;
    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        setMidValue();
    }


    private double midValue;

    public double getMidValue() {
        return midValue;
    }

    private void setMidValue() {
        midValue = Aggregation.performAggregation(minValue, maxValue, Aggregation.AVG);
    }

    public VerticalReferencesPanel(double minValue, double maxValue) {
        setMinValue(minValue);
        setMaxValue(maxValue);
        FontMetrics fm = this.getFontMetrics(this.getFont());

        String widestDoubleVal = getStringDouble(-1.00);
        valuesLongestWidth = fm.stringWidth(widestDoubleVal);
        widestDoubleVal = getStringDouble(minValue);
        valuesLongestWidth = Math.max(valuesLongestWidth, fm.stringWidth(widestDoubleVal));
        widestDoubleVal = getStringDouble(maxValue);
        valuesLongestWidth = Math.max(valuesLongestWidth, fm.stringWidth(widestDoubleVal));


        JPanel thisPanel = this;
        ComponentListener resizeListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int height = e.getComponent().getHeight();
                int spaceSizeBetweenLabelsInPixels = 50;
                int halfHeight = height / 2;
                labelCount = halfHeight / spaceSizeBetweenLabelsInPixels;
                if (labelCount > 100) {
                    labelCount = 100;
                }
                // +1 because we need to take into consideration the middle value.
                pixelJump = halfHeight / (double) (labelCount + 1);
                thisPanel.repaint();
            }
        };
        this.addComponentListener(resizeListener);

        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(layout);
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawSamplesValueRangeDouble(g);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
/////////////////// METHODS FOR DRAWING THE DOUBLE REFERENCES
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void drawSamplesValueRangeDouble(Graphics g) {
        drawSamplesValueRangeDouble(g, minValue, maxValue, midValue);
    }

    // midVal is usually 0
    private void drawSamplesValueRangeDouble(Graphics g, double minVal, double maxVal, double midVal) {
        int waveStartX = 30;
        int waveStartY = 0;

        double valRange;
        double valJump;

        valRange = Math.abs(maxVal - midVal);
        // +1 because we need to take into consideration the middle value.
        valJump = valRange / (labelCount + 1);

        Color color = Color.black;
        g.setColor(color);
        int x = waveStartX;

        double val = maxVal;
        double y = waveStartY;


        int textHeight = g.getFontMetrics().getHeight();
        drawFirstValueDouble(x, (int) (y), maxVal, color, g, textHeight);
        y += pixelJump;
        val -= valJump;
        for (int i = 0; i < labelCount; y += pixelJump, val -= valJump, i++) {
            drawInternalValueDouble(x, (int) y, val, color, g, textHeight);
        }
        drawInternalValueDouble(x, (int) y, midVal, color, g, textHeight);


        valRange = Math.abs(minVal - midVal);
        // +1 because we need to take into consideration the middle value.
        valJump = valRange / (labelCount + 1);

        y += pixelJump;
        val = midVal - valJump;
        for (int i = 0; i < labelCount; y += pixelJump, val -= valJump, i++) {
            drawInternalValueDouble(x, (int) y, val, color, g, textHeight);
        }

        drawLastValueDouble(x, getHeight() - 1, minVal, color, g, textHeight);
    }


    private void drawFirstValueDouble(int x, int y, double valToDraw, Color color, Graphics g, int textHeight) {
        int shiftForStringY = textHeight - textHeight / 4;
        drawValueDouble(x, y, valToDraw, color, g, shiftForStringY);
    }

    private void drawInternalValueDouble(int x, int y, double valToDraw, Color color, Graphics g, int textHeight) {
        int shiftForStringY = textHeight / 4;
        drawValueDouble(x, y, valToDraw, color, g, shiftForStringY);
    }

    private void drawLastValueDouble(int x, int y, double valToDraw, Color color, Graphics g, int textHeight) {
        int shiftForStringY = -textHeight / 4;
        drawValueDouble(x, y, valToDraw, color, g, shiftForStringY);
    }

    private void drawValueDouble(int x, int y, double valToDraw, Color color, Graphics g, int shiftForStringY) {
        String valString = getStringDouble(valToDraw);
        drawValue(valString, x, y, color, g, shiftForStringY);
    }


    private void drawValue(String valString, int x, int y, Color color, Graphics g, int shiftForStringY) {
        int startX = SwingUtils.drawStringWithSpace(g, color, valString, 0, this.getWidth(), y + shiftForStringY);
        int w = g.getFontMetrics().stringWidth(valString);
        if (w > valuesLongestWidth) {
            valuesLongestWidth = w;
        }

        startX += w;
        g.drawLine(startX, y, this.getWidth(), y);
    }


    public static String getStringDouble(double valToDraw) {
        String valString = String.format("%.2f", valToDraw);
        return valString;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
/////////////////// METHODS FOR DRAWING THE DOUBLE REFERENCES
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(valuesLongestWidth + minLineLen, super.getPreferredSize().height);
    }




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
/////////////////// METHODS FOR DRAWING INT REFERENCES (SAME AS THE DOUBLE VARIANT, BUT WITH INTS) - CUT FEATURE
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Another cut feature, wanted to make it more general and also have possibility to reference ints,
    // but to be honest the doubles just look better.

    private void drawSamplesValueRangeInt(Graphics g, int sampleSizeInBits) {
        int valMax = AudioUtilities.getMaxAbsoluteValueSigned(sampleSizeInBits);
        int valMin = -valMax;
        valMax--;
        drawSamplesValueRangeInt(g, valMin, valMax, 0);
    }


    // midVal is usually 0
    private void drawSamplesValueRangeInt(Graphics g, int minVal, int maxVal, int midVal) {
        int waveStartX = 30;
        int waveStartY = 0;

        int valRange;
        int valJump;

        valRange = Math.abs(maxVal - midVal);
        // +1 because we need to take into consideration the middle value.
        valJump = valRange / (labelCount + 1);

        Color color = Color.black;
        g.setColor(color);
        int x = waveStartX;


        int val = maxVal;
        double y = waveStartY;


        int textHeight = g.getFontMetrics().getHeight();
        drawFirstValueInt(x, (int) (y), maxVal, color, g, textHeight);
        y += pixelJump;
        val -= valJump;
        for (int i = 0; i < labelCount; y += pixelJump, val -= valJump, i++) {
            drawInternalValueInt(x, (int) y, val, color, g, textHeight);
        }
        drawInternalValueInt(x, (int) y, midVal, color, g, textHeight);


        valRange = Math.abs(minVal - midVal);
        // +1 because we need to take into consideration the middle value.
        valJump = valRange / (labelCount + 1);

        y += pixelJump;
        val = midVal - valJump;
        for (int i = 0; i < labelCount; y += pixelJump, val -= valJump, i++) {
            drawInternalValueInt(x, (int) y, val, color, g, textHeight);
        }


        drawLastValueInt(x, (int) (y), minVal, color, g, textHeight);
    }


    private void drawFirstValueInt(int x, int y, int valToDraw, Color color, Graphics g, int textHeight) {
        int shiftForStringY = textHeight - textHeight / 4;
        drawValueInt(x, y, valToDraw, color, g, shiftForStringY);
    }

    private void drawInternalValueInt(int x, int y, int valToDraw, Color color, Graphics g, int textHeight) {
        int shiftForStringY = textHeight / 4;
        drawValueInt(x, y, valToDraw, color, g, shiftForStringY);
    }

    private void drawLastValueInt(int x, int y, int valToDraw, Color color, Graphics g, int textHeight) {
        int shiftForStringY = -textHeight / 4;
        drawValueInt(x, y, valToDraw, color, g, shiftForStringY);
    }

    private void drawValueInt(int x, int y, int valToDraw, Color color, Graphics g, int shiftForStringY) {
        String valString = getStringInt(valToDraw);
        drawValue(valString, x, y, color, g, shiftForStringY);
    }


    public static String getStringInt(int valToDraw) {
        String valString = Integer.toString(valToDraw);
        return valString;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
/////////////////// METHODS FOR DRAWING INTS (SAME AS THE DOUBLE VARIANT, BUT WITH INTS) - CUT FEATURE
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
