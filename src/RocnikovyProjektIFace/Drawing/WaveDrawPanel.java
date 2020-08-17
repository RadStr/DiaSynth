package RocnikovyProjektIFace.Drawing;


import Rocnikovy_Projekt.Program;

import java.awt.*;

public class WaveDrawPanel extends DrawPanel {
    /**
     * @param binCount
     * @param labelTypeToolTip for FFT window it is "Frequency" for wave drawing "Time"
     */
    public WaveDrawPanel(int sampleRate, int timeInMs, int binCount, String labelTypeToolTip) {
        super(binCount, labelTypeToolTip);
        setTimeInMs(timeInMs);
        setLabels();
        normalizeAndSetDrawValues();
        setLastPartOfTooltip();
    }


    private int timeInMs;
    private String timeInMsString;
    public void setTimeInMs(int timeInMs) {
        this.timeInMs = timeInMs;
        timeInMsString = Double.toString(timeInMs);
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

    /**
     * Isn't called anywhere it is just marker, that the labels needs to be set in deriving class.
     */
    @Override
    protected void setLabels() {
        labels = createLabels(timeInMs, drawValues.length);
    }

    public static String[] createLabels(int timeInMs, int binCount) {
        String[] labels = new String[binCount];

        for(int i = 0; i < labels.length; i++) {
            labels[i] = Program.convertMillisecondsToTime((int)(timeInMs * i / (double)labels.length));
        }

        return labels;
    }

    @Override
    protected void setBinMeasure(int bin, int y) {
        int w = this.getWidth();
        int h = this.getHeight();
        int hh = h / 2;
        double jump;

        y -= hh;
        double value = -y / (double) hh; // -y because it is upside-down, value of 1 is at the top of window which is y = 0
        value = Math.min(value, 1);
        value = Math.max(value, -1);
        setDrawValue(bin, value);

// TODO: VYMAZAT
//        currMouseLoc.y = Math.max(0, currMouseLoc.y);
//        currMouseLoc.y = Math.min(h - 1, currMouseLoc.y);
//        currMouseLoc.x = Math.min(w-1, currMouseLoc.x);
//        currMouseLoc.x = Math.max(0, currMouseLoc.x);
//        if(oldMouseLoc == null) {
//            oldMouseLoc = currMouseLoc;
//        }
//
//
//        double y = oldMouseLoc.y;
//        if(currMouseLoc.x <= oldMouseLoc.x) {
//            jump = (currMouseLoc.y - oldMouseLoc.y) / (double)(oldMouseLoc.x - currMouseLoc.x);
//            for (int i = oldMouseLoc.x; i >= currMouseLoc.x; i--, y += jump) {
//                wave[i] = y / h;
//// TODO: DEBUG            	System.out.println(i + "\t" + y);
//            }
//        }
//        else if(currMouseLoc.x > oldMouseLoc.x) {
//            jump = (currMouseLoc.y - oldMouseLoc.y) / (double)(currMouseLoc.x - oldMouseLoc.x);
//            for (int i = oldMouseLoc.x; i <= currMouseLoc.x; i++, y += jump) {
//                wave[i] = y / h;
//// TODO: DEBUG            	System.out.println(i + "\t" + y);
//            }
//        }
//        //wave[currMouseLoc.x] = currMouseLoc.y / (double)h;
    }

    @Override
    protected void drawBin(Graphics g, double drawValue, int currX, int binWidth, int h) {
        int midY = h / 2;
        int y = midY - (int)(drawValue * midY);
        if(y < midY) {
            g.drawRect(currX, y, binWidth, midY - y);
        }
        else {
            g.drawRect(currX, midY, binWidth, y - midY);
        }
    }

    public double[] getDrawnWave() {
        return drawValues;
    }
}