package RocnikovyProjektIFace.Drawing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Note: labels variable needs to be set in deriving constructor and also setLastPartOfTooltip() and normalizeAndSetDrawValues(double value)
 * needs to be called at the end of deriving constructor.
 */
public abstract class DrawPanel extends JPanel implements MouseMotionListener, MouseListener {
    /**
     *
     * @param binCount
     * @param labelTypeToolTip for FFT window it is "Frequency" for wave drawing "Time"
     */
    public DrawPanel(int binCount, String labelTypeToolTip) {
        labels = new String[binCount];
        drawValues = new double[binCount];

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double w = screenSize.getWidth();
        double h = screenSize.getHeight();

        minSize = new Dimension();
        minSize.width = Math.min(2*binCount, (int)w);
        minSize.height = 100;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);











        BIN_COUNT = Integer.toString(binCount).length();
        MAX_VALUE = 1 + 3;      // [01] + .xx

        tooltip = new StringBuilder("<html>Bin: ");
        INDEX_IN_STRINGBUILDER_AFTER_BIN = tooltip.length();
        int index = INDEX_IN_STRINGBUILDER_AFTER_BIN;
        for(int i = 0; i < BIN_COUNT; i++, index++) {
            tooltip.insert(index, ' ');
        }

        tooltip.insert(index, NEW_LINE);
        index += NEW_LINE.length();
        MEASURE_TEXT_INDEX = index;
        String valueString = NEW_LINE + "Value: ";
        tooltip.insert(index, valueString);
        index += valueString.length();
        MEASURE_VALUE_INDEX = index;
        for(int i = 0; i < MAX_VALUE; i++, index++) {
            tooltip.insert(index, ' ');
        }

        String tmp = NEW_LINE + labelTypeToolTip + ": ";
        tooltip.insert(index, tmp);
        index += tmp.length();
        LABEL_VALUE_INDEX = index;


        drawValuesStrings = new String[drawValues.length];

        binIndices = new String[binCount];
        for (int i = 0; i < binIndices.length; i++) {
            binIndices[i] = Integer.toString(i);
        }
    }


    private final String NEW_LINE = "<br>";
    private final int BIN_COUNT;
    private final int MAX_VALUE;      // BIN_COUNT + .xx

    private final int INDEX_IN_STRINGBUILDER_AFTER_BIN;
    private final int MEASURE_TEXT_INDEX;
    private final int MEASURE_VALUE_INDEX;
    private final int LABEL_VALUE_INDEX;

    protected abstract Color getBinColor(int bin);


    private Point oldMouseLoc;

    private int longestLabelLen;
    /**
     * Needs to be called after the labels are set.
     */
    protected void setLongestLabelLen() {
        longestLabelLen = 0;
        for (String s : labels) {
            if(s.length() > longestLabelLen) {
                longestLabelLen = s.length();
            }
        }
    }
    /**
     * Needs to be set in deriving class
     */
    protected String[] labels;
    /**
     * Isn't called anywhere it is just marker, that the labels needs to be set in deriving class.
     */
    protected abstract void setLabels();


    protected double[] drawValues;
    protected String[] drawValuesStrings;
    protected void setDrawValue(int index, double value) {
        drawValues[index] = value;
        drawValuesStrings[index] = String.format("%.2f", drawValues[index]);
    }
    protected double getDrawValue(int bin) {
        return drawValues[bin];
    }
    protected String[] binIndices;

    protected int selectedBin = -1;
    private void setSelectedBin(int bin) {
        if(bin != selectedBin) {
            changeToolTip(bin);
            selectedBin = bin;
        }
    }

    private StringBuilder tooltip;
    protected final void setLastPartOfTooltip() {
        setLongestLabelLen();
        for(int i = 0; i < longestLabelLen; i++) {
            tooltip.append(' ');
        }

        tooltip.append(NEW_LINE);
    }

    protected abstract double normalizeValue(double value);
    protected void normalizeAndSetDrawValues() {
        // Normalization and getting string representation
        for (int i = 0; i < drawValues.length; i++) {
            // This 2 lines are from the book Computer music synthesis, composition and performance by Dodge Jerse,
            // but the factor of 4 seems to be redundant, because when I remove them then the maximum possible value is 1.
//            fftMeasures[i] *= 2;
//            fftMeasures[i] /= (fftMeasures.length / 2);
            setDrawValue(i, normalizeValue(drawValues[i]));
        }
    }



    private void tryChangeBin(Point p, boolean isDragEvent) {
        int bin = getBinAtPos(p);
        System.out.println("BIN:\t" + bin + ":" + selectedBin);
        if (!isDragEvent || (bin <= selectedBin + 1 && bin >= selectedBin - 1)) {       // If moved at max to next bin
            setBinMeasure(bin, p.y);
        } else {
            //jumpOverMultipleBinsSimple(bin, p.y);
            jumpOverMultipleBinsAdvanced(bin, p);
        }

        changeToolTip(bin);
        selectedBin = bin;
        this.repaint();
    }


    private void jumpOverMultipleBinsSimple(int bin, int y) {
        if(bin < selectedBin) {
            for (int i = selectedBin; i >= bin; i--) {
                setBinMeasure(i, y);
            }
        }
        else {
            for (int i = selectedBin; i <= bin; i++) {
                setBinMeasure(i, y);
            }
        }
    }

    private void jumpOverMultipleBinsAdvanced(int bin, Point p) {
        double jump;
        if(oldMouseLoc == null) {
            oldMouseLoc = p;
        }
        double y = oldMouseLoc.y;

        if(bin < selectedBin) {
            jump = (p.y - oldMouseLoc.y) / (double)(selectedBin - bin);
            System.out.println("Y!!!!!!!\t" + y);
            for (int i = selectedBin; i >= bin; i--, y += jump) {
                setBinMeasure(i, (int)y);
            }
        }
        else {
            jump = (p.y - oldMouseLoc.y) / (double)(bin - selectedBin);
            System.out.println("Y!!!!!!!\t" + y);
            for (int i = selectedBin; i <= bin; i++, y += jump) {
                setBinMeasure(i, (int)y);
            }
        }
    }

    private void tryChangeBin(MouseEvent e, boolean isDragEvent) {
        Point p = e.getPoint();
        tryChangeBin(p, isDragEvent);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(isRightClick) {
            setBinMeasure(selectedBin, e.getY());       // The selected bin was set at the mouse pressed event
            changeToolTip(selectedBin);
            this.repaint();
        }
        else {
            tryChangeBin(e, true);
            oldMouseLoc = e.getPoint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int bin = getBinAtPos(e.getPoint());
        setSelectedBin(bin);
        this.repaint();
    }


    private void changeToolTip(int bin) {
        String binString = binIndices[bin];
        setBinToolTip(binString);

        String measure = drawValuesStrings[bin];
        setMeasureToolTip(measure);

        String label = labels[bin];
        setBinInfoToolTip(label);

        this.setToolTipText(tooltip.toString());
    }


    // TODO: Asi vymazat
//    protected abstract void setBinToolTip(String binString);
//
//    protected abstract void setMeasureToolTip(String measure);
//
//    // TODO: AAA - setFrequencyToolTip
//    protected abstract void setBinInfoToolTip(String info);


    private void setBinToolTip(String binString) {
        for (int i = INDEX_IN_STRINGBUILDER_AFTER_BIN, j = 0; j < binString.length(); i++, j++) {
            tooltip.setCharAt(i, binString.charAt(j));
        }

        for (int i = INDEX_IN_STRINGBUILDER_AFTER_BIN + binString.length(); i < MEASURE_TEXT_INDEX; i++) {
            tooltip.setCharAt(i, ' ');
        }
    }

    private void setMeasureToolTip(String measure) {
        for (int i = MEASURE_VALUE_INDEX, j = 0; j < measure.length(); i++, j++) {
            tooltip.setCharAt(i, measure.charAt(j));
        }

        for(int i = MEASURE_VALUE_INDEX + measure.length(); i < MEASURE_VALUE_INDEX + MAX_VALUE; i++) {
            tooltip.setCharAt(i, ' ');
        }
    }

    private void setBinInfoToolTip(String info) {
        for (int i = LABEL_VALUE_INDEX, j = 0; j < info.length(); i++, j++) {
            tooltip.setCharAt(i, info.charAt(j));
        }

        for(int i = LABEL_VALUE_INDEX + info.length(); i < LABEL_VALUE_INDEX + longestLabelLen; i++) {
            tooltip.setCharAt(i, ' ');
        }
    }



    @Override
    public void mouseClicked(MouseEvent e) {
        // EMPTY
    }


    private boolean isRightClick = false;

    @Override
    public void mousePressed(MouseEvent e) {
        isRightClick = e.getButton() != MouseEvent.BUTTON1;
        tryChangeBin(e, false);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // EMPTY
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // EMPTY
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // EMPTY
    }


    // TODO: Possible optimisation by redrawing only the chosen bin, or adjacent binIndices
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawFFTWindow(g);
    }


    private Dimension minSize;
    @Override
    public Dimension getMinimumSize() {
        return minSize;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension prefSize = super.getPreferredSize();
        if(prefSize.width < minSize.width || prefSize.height < minSize.height) {
            return minSize;
        }
        return prefSize;
    }


    /**
     * Checks inside what bin is the given position.
     * @param pos is the position to be checked inside what bin it is.
     * @return Returns the closest bin (Because when I returned -1 in spaces it wasn't behaving the correct way). (Starting at 0). Even if
     */
    public int getBinAtPos(Point pos) {
        int w;
        w = this.getWidth();
        int x = Math.max(0, pos.x);
        x = Math.min(x, w);


        int binCount = drawValues.length;
        int binWidth = w / binCount;
        int freePixels = w % binCount;
        int binsWhitespace = binWidth / 4;
        binWidth -= binsWhitespace;

        int indexToStartAddingPixels = binCount - freePixels;
        int binWidthWithSpace = binWidth + binsWhitespace;



// Slow variant of the code under this
//        int bin;
//        int currX;
//        for(bin = 0, currX = 0; currX < x; bin++, currX += binWidthWithSpace) {
//            if(bin > indexToStartAddingPixels) {
//                currX++;
//            }
//        }
//        if(bin <= 0) {
//            return 0;
//        }
//        else if(bin >= binCount) {
//            return binCount - 1;
//        }
//        return bin - 1;



        int forgotPixels = 0;
        int bin = x / binWidthWithSpace;
        if(bin >= indexToStartAddingPixels) {
            forgotPixels = bin - indexToStartAddingPixels;
            int invalidBins = forgotPixels / (binWidthWithSpace + 1);
            bin -= invalidBins;

            int previousBinEnd = (bin - indexToStartAddingPixels - 1) * (binWidthWithSpace + 1) +
                    indexToStartAddingPixels * binWidthWithSpace + binWidth + 1;
            if(forgotPixels % (binWidthWithSpace + 1) != 0 && x <= previousBinEnd) {

                bin--;
            }
        }

        if(bin >= binCount) {
            bin = binCount - 1;
        }
        return bin;
    }


    protected abstract void setBinMeasure(int bin, int y);

    public void drawFFTWindow(Graphics g) {
        int w,h;
        w = this.getWidth();
        h = this.getHeight();

        int binCount = drawValues.length;
        int binWidth = w / binCount;
        int freePixels = w % binCount;
        int binsWhitespace = binWidth / 4;
        binWidth -= binsWhitespace;

        int indexToStartAddingPixels = binCount - freePixels;
        int binWidthWithSpace = binWidth + binsWhitespace;

        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);

        // Find fitting font for frequency labels amd for energies
        final int START_FONT_SIZE = 24;
        final int MIN_FONT = 12;
        boolean enoughSpaceForLabels = true;
        int jumpIfTooSmall = 0;
        int n = 1;
        int textBinWidth = binWidth;
        int fontSize = 0;
        while(fontSize < MIN_FONT && n < labels.length) {
            fontSize = START_FONT_SIZE;
            int textWhitespace = textBinWidth / 4;
            fontSize = Rocnikovy_Projekt.Program.getFont(fontSize, g, labels, textBinWidth - textWhitespace, Integer.MAX_VALUE, n);
            n *= 2;
            textBinWidth *= 2;
            System.out.println("FT:" + "\t" + fontSize);
        }
        n /= 2;
        textBinWidth /= 2;

        if(fontSize < MIN_FONT) {
            enoughSpaceForLabels = false;
        }

//        System.out.println("MAX:\t" + maxEnergy);
//        System.out.println(selectedBin);

        boolean isFirstAdded = true;
        for(int bin = 0, currX = 0; bin < drawValues.length; bin++, currX += binWidthWithSpace) {
            if(bin >= indexToStartAddingPixels && isFirstAdded) {
                isFirstAdded = false;
                binWidth++;
                binWidthWithSpace++;
            }

            g.setColor(getBinColor(bin));
            drawBin(g, drawValues[bin], currX, binWidth, h);
            if(bin == selectedBin) {
                g.setColor(new Color(0,0,255, 32));
                g.fillRect(currX, 0, binWidth, h);
            }
        }



        drawLabels(indexToStartAddingPixels, binWidthWithSpace, binWidth,
                enoughSpaceForLabels, textBinWidth, h, g, n, labels, drawValues.length);
//        if(enoughSpaceForLabels) {
//            if (indexToStartAddingPixels < fftMeasures.length) {
//                isFirstAdded = true;
//                binWidth--;
//                binWidthWithSpace--;
//            }
//            for (int bin = 0, currX = 0; bin < fftMeasures.length; bin++, currX += binWidthWithSpace) {
//                if (bin >= indexToStartAddingPixels && isFirstAdded) {
//                    isFirstAdded = false;
//                    binWidth++;
//                    binWidthWithSpace++;
//                }
//
//
//                Color c = Color.black;
//                if (bin == fftMeasures.length - 1) {
//                    Program.drawStringWithSpace(g, c, binFreqs[bin], currX - 3 * textBinWidth / 4, textBinWidth, h);
//                } else if (bin == 0) {
//                    Program.drawStringWithSpace(g, c, binFreqs[bin], currX - textBinWidth / 4, textBinWidth, h);
//                } else if (bin % n == 0) {
//                    // Draw frequency
//                    Program.drawStringWithSpace(g, c, binFreqs[bin], currX - textBinWidth / 2, textBinWidth, h);
//                }
//            }
//        }
    }

    protected abstract void drawBin(Graphics g, double drawValue, int currX, int binWidth, int h);

    /**
     * Draws every n-th label
     * @param indexToStartAddingPixels because usually width % labels.length != 0 then, we need to make the spaces 1 pixel larger from some index, so it can fit.
     * @param binWidthWithSpace is the binWidth but also containing space between the binIndices.
     * @param binWidth is the width of 1 bin. (In case of wave drawing it is 1 pixel, in case of FFT can be 1 or more, depends on size of window).
     * @param shouldDrawLabels
     * @param labelWidth
     * @param h is the height of the panel
     * @param g is the graphics
     * @param n - every n-th label is drawn
     * @param labels are the labels
     */
    public static void drawLabels(int indexToStartAddingPixels, int binWidthWithSpace, int binWidth,
                                  boolean shouldDrawLabels, int labelWidth, int h, Graphics g, int n, String[] labels,
                                  int binCount) {
        boolean isFirstAdded = false;
        if(shouldDrawLabels) {
            if (indexToStartAddingPixels < binCount) {
                isFirstAdded = true;
                binWidth--;
                binWidthWithSpace--;
            }
            for (int bin = 0, currX = 0; bin < binCount; bin++, currX += binWidthWithSpace) {
                if (bin >= indexToStartAddingPixels && isFirstAdded) {
                    isFirstAdded = false;
                    binWidth++;
                    binWidthWithSpace++;
                }


                Color c = Color.black;
                if (bin == binCount - 1) {
                    Rocnikovy_Projekt.Program.drawStringWithSpace(g, c, labels[bin], currX - 3 * labelWidth / 4, labelWidth, h);
                } else if (bin == 0) {
                    Rocnikovy_Projekt.Program.drawStringWithSpace(g, c, labels[bin], currX - labelWidth / 4, labelWidth, h);
                } else if (bin % n == 0) {
                    // Draw frequency
                    Rocnikovy_Projekt.Program.drawStringWithSpace(g, c, labels[bin], currX - labelWidth / 2, labelWidth, h);
                }
            }
        }
    }
}
