package RocnikovyProjektIFace.Drawing;

import Rocnikovy_Projekt.Program;

import java.awt.*;

public abstract class WaveDrawPanel extends DrawPanel {
    /**
     * @param binCount
     * @param labelTypeToolTip for FFT window it is "Frequency" for wave drawing "Time"
     */
    public WaveDrawPanel(int binCount, String labelTypeToolTip, boolean isEditable, Color backgroundColor) {
        super(binCount, labelTypeToolTip, isEditable, true, false, backgroundColor);
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth();
        int hh = getHeight() / 2;
        g.setColor(Color.black);
        g.drawLine(0, hh, w, hh);
    }


    @Override
    protected double normalizeValue(double value) {
        return value;
    }

    @Override
    protected Color getBinColor(int bin) {
        return Color.BLUE;
    }


    public static double calculateBinValue(int y, int height) {
        int hh = height / 2;
        y -= hh;
        double binValue = -y / (double) hh; // -y because it is upside-down, value of 1 is at the top of window which is y = 0
        binValue = Math.min(binValue, 1);
        binValue = Math.max(binValue, -1);

        return binValue;
    }

    @Override
    protected void setBinValue(int bin, int y) {
        int h = this.getHeight();
        double binValue = calculateBinValue(y, h);
        setDrawValue(bin, binValue);
    }

    @Override
    protected void drawBin(Graphics g, double drawValue, int currX, int binWidth, int h) {
        drawBinValueBetweenMinusOneAndOne(g, drawValue, currX, binWidth, h, true);
    }

    /**
     *
     * @param g
     * @param drawValue
     * @param currX
     * @param binWidth
     * @param h
     * @param fillRect if set to true than draw bin as filled rectangle, otherwise draw it as rectangle with no filling.
     */
    public static void drawBinValueBetweenMinusOneAndOne(Graphics g, double drawValue, int currX,
                                                         int binWidth, int h, boolean fillRect) {
        int midY = h / 2;
        int y = midY - (int) (drawValue * midY);
        if(fillRect) {
            if (y < midY) {
                g.fillRect(currX, y, binWidth, midY - y);
            } else {
                g.fillRect(currX, midY, binWidth, y - midY);
            }
        }
        else {
            if (y < midY) {
                g.drawRect(currX, y, binWidth, midY - y);
            } else {
                g.drawRect(currX, midY, binWidth, y - midY);
            }
        }
    }


    public double[] getDrawnWave() {
        return drawValues;
    }
}