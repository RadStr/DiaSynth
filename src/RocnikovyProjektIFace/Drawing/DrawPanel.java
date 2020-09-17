package RocnikovyProjektIFace.Drawing;

import Rocnikovy_Projekt.Program;
import Rocnikovy_Projekt.ProgramTest;

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
     * @param labelTypeToolTip for FFT window it is "Frequency" for wave drawing "Time"
     * @param shouldDrawLabelsAtTop if set to true, draws the labels at the top of the panel, otherwise at the bottom.
     */
    public DrawPanel(int binCount, String labelTypeToolTip, boolean isEditable,
                     boolean areValuesSigned, boolean allowDifferentWidthBins,
                     Color backgroundColor, boolean shouldDrawLabelsAtTop,
                     boolean shouldDrawLineInMiddle) {
        this.DRAW_LINE_IN_MIDDLE = shouldDrawLineInMiddle;
        this.shouldDrawLabelsAtTop = shouldDrawLabelsAtTop;
        setBackgroundColor(backgroundColor);
        this.ALLOW_DIFFERENT_WIDTH_BINS = allowDifferentWidthBins;
        setIsEditable(isEditable);
        labels = new String[binCount];
        drawValues = new double[binCount];

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double w = screenSize.getWidth();
        double h = screenSize.getHeight();

        minSize = new Dimension();
        int binWidth = 1;
        for(double currWidth = binCount; currWidth < w; currWidth += binCount, binWidth++) {
            // EMPTY
        }
        if(binWidth != 1) {
            binWidth--;
        }

        // TODO: DRAW PANEL THINGS
//        DrawPanel - TODO: Sem asi pridat + neco abych to mohl spravne udelat pro ten text k te minSize.Width
        // TODO: DRAW PANEL THINGS
        minSize.width = binWidth * binCount;
        minSize.height = 100;
        prefSize = new Dimension(minSize);

        this.addMouseListener(this);
        this.addMouseMotionListener(this);











        BIN_COUNT = Integer.toString(binCount).length();
        if(areValuesSigned) {
            MAX_VALUE = 3 + VALUE_PRECISION;      // [-][01] + .xxx;
        }
        else {
            MAX_VALUE = 2 + VALUE_PRECISION;      // [01] + .xxx;
        }

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


    private boolean shouldDrawLabelsAtTop;
    public boolean getShouldDrawLabelsAtTop() {
        return shouldDrawLabelsAtTop;
    }
    public void setShouldDrawLabelsAtTop(boolean shouldDrawLabelsAtTop) {
        this.shouldDrawLabelsAtTop = shouldDrawLabelsAtTop;
    }


    private Color backgroundColor = Color.WHITE;
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }


    public final boolean ALLOW_DIFFERENT_WIDTH_BINS;

    private final String NEW_LINE = "<br>";
    private final int BIN_COUNT;
    private final int VALUE_PRECISION = 3;
    private final int MAX_VALUE;

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


    private boolean isEditable;
    protected void setIsEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }
    public boolean getIsEditable() {
        return isEditable;
    }

    protected double[] drawValues;
    protected String[] drawValuesStrings;
    protected void setDrawValue(int index, double value) {
        if(isEditable) {
            drawValues[index] = value;
            setDrawValueString(index, drawValues[index]);
        }
    }
    protected double getDrawValue(int bin) {
        return drawValues[bin];
    }
    protected void setDrawValueString(int bin, double value) {
        drawValuesStrings[bin] = String.format("%." + VALUE_PRECISION + "f", value);
    }

    protected void resetValues() {
        for(int i = 0; i < drawValues.length; i++) {
            setDrawValue(i, 0);
        }

        repaint();
    }


    /**
     * Sets all the drawValuesStrings based on current drawValues
     */
    protected void setDrawValuesStrings() {
        for(int i = 0; i < drawValues.length; i++) {
            setDrawValueString(i, drawValues[i]);
        }
    }

    protected String[] binIndices;

    protected int selectedBin = -1;
    protected void setSelectedBin(int bin) {
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
            setBinValue(bin, p.y);
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
                setBinValue(i, y);
            }
        }
        else {
            for (int i = selectedBin; i <= bin; i++) {
                setBinValue(i, y);
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
                setBinValue(i, (int)y);
            }
        }
        else {
            jump = (p.y - oldMouseLoc.y) / (double)(bin - selectedBin);
            System.out.println("Y!!!!!!!\t" + y);
            for (int i = selectedBin; i <= bin; i++, y += jump) {
                setBinValue(i, (int)y);
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
            setBinValue(selectedBin, e.getY());       // The selected bin was set at the mouse pressed event
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


    public final boolean DRAW_LINE_IN_MIDDLE;

    // TODO: Possible optimisation by redrawing only the chosen bin, or adjacent binIndices
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();

        g.setColor(backgroundColor);
        g.fillRect(0, 0, w, h);

        drawWindow(g);

        g.setColor(Color.black);
        g.drawRect(0, 0, w, h);
        g.drawLine(w / 2, 0, w / 2, h);

        if(DRAW_LINE_IN_MIDDLE) {
            int hh = getHeight() / 2;
            g.drawLine(0, hh, w, hh);
        }
    }


    private Dimension minSize;
    @Override
    public Dimension getMinimumSize() {
        return minSize;
    }


    private Dimension prefSize;
    @Override
    public Dimension getPreferredSize() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JPanel contentPane = (JPanel) topFrame.getContentPane();
        ProgramTest.debugPrint("INSETS:", contentPane.getInsets(), getLocation(), contentPane.getLocation(),
                getInsets(), topFrame.getInsets());
        Insets frameInsets = topFrame.getInsets();
        // Same size
        ProgramTest.debugPrint("Insets:", topFrame.getHeight() - frameInsets.top - frameInsets.bottom, contentPane.getHeight());
        // For some reason have to make it smaller. I choose to make it smaller by frameInsets.bottom, but could be anything > 5
        prefSize.height = contentPane.getHeight() - frameInsets.bottom;


//        Container parent = getParent();
//        Container grandParent = parent.getParent();
//        Container grandGrandParent = grandParent.getParent();
//        if(parent == SwingUtilities.getWindowAncestor(this) || grandParent == SwingUtilities.getWindowAncestor(this) ||
//                grandGrandParent == SwingUtilities.getWindowAncestor(this)) {
//            System.exit(4578);
//        }
//        if(parent == null) {
//
//        }
//        else {
//
//        }
//
//        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
//        JPanel content = (JPanel)topFrame.getContentPane();
//
////        ProgramTest.debugPrint("LOCATION:", getLocation());
////        prefSize.width = topFrame.getWidth() / 2;
////        prefSize.height = topFrame.getHeight() / 2;
////        prefSize =
////
////        Dimension prefSize = super.getPreferredSize();
////        if(prefSize.width < minSize.width || prefSize.height < minSize.height) {
////            return minSize;
////        }
////
//////        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
//////        prefSize.width = topFrame.getWidth() / 2;
//////        prefSize.height = topFrame.getHeight() / 2;
////////        prefSize.width = Math.max(prefSize.width, 1);
////////        prefSize.height = Math.max(prefSize.height, 1);
////////        prefSize.width = Math.max(1, prefSize.width);
////////        prefSize.height = Math.max(1, prefSize.height);
//////        ProgramTest.debugPrint("Testing pref size drawing", prefSize);
//////
////////        prefSize.width += 100;
////////        prefSize.height += 100;
//////
////////        prefSize.width = 1200;
////////        prefSize.height = 1200;

        return prefSize;


        //return new Dimension(1200, 1200);
        //return new Dimension(getWidth(), getHeight() / 2);
        //return new Dimension(100, 100);
        //return new Dimension(0, 0);
    }


    /**
     * Checks inside what bin is at the given position.
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

        int binWidthWithSpace = binWidth + binsWhitespace;
        int bin = x / binWidthWithSpace;


        if (ALLOW_DIFFERENT_WIDTH_BINS) {
            int indexToStartAddingPixels = binCount - freePixels;
            int extraPixels = 0;
            if (bin >= indexToStartAddingPixels) {
                extraPixels = bin - indexToStartAddingPixels;
                int invalidBins = extraPixels / (binWidthWithSpace + 1);
                bin -= invalidBins;

                int previousBinEndInPixels = (bin - indexToStartAddingPixels - 1) * (binWidthWithSpace + 1) +
                        indexToStartAddingPixels * binWidthWithSpace + binWidth + 1;
                if (extraPixels % (binWidthWithSpace + 1) != 0 && x <= previousBinEndInPixels) {
                    bin--;
                }
            }
// Slow variant of the code above this
//            int bin;
//            int currX;
//            for (bin = 0, currX = 0; currX < x; bin++, currX += binWidthWithSpace) {
//                if (bin > indexToStartAddingPixels) {
//                    currX++;
//                }
//            }
//            if (bin <= 0) {
//                return 0;
//            } else if (bin >= binCount) {
//                return binCount - 1;
//            }
//            return bin - 1;
        }

        if (bin >= binCount) {
            bin = binCount - 1;
        }
        return bin;
    }


    protected abstract void setBinValue(int bin, int y);

    public void drawWindow(Graphics g) {
        int w, h;
        w = this.getWidth();
        h = this.getHeight();

        int binCount = drawValues.length;
        int binWidth = w / binCount;
        int freePixels = w % binCount;
        int binsWhitespace = binWidth / 4;
        binWidth -= binsWhitespace;

        // Find fitting font for frequency labels amd for energies
        final int START_FONT_SIZE = 24;
        final int MIN_FONT = 12;
        boolean enoughSpaceForLabels = true;
        int n = 1;
        int textBinWidth = binWidth;
        int fontSize = 0;
        while (fontSize < MIN_FONT && n < labels.length) {
            fontSize = START_FONT_SIZE;
            int textWhitespace = textBinWidth / 4;
            // TODO: DRAW PANEL THINGS
//            Draw panel - drawWindow() - 581 - Muzu napsat rychleji staci se mi podivat jen na delku toho nejvetsiho a celkove to muzu napsat trochu lip to hledani fontu aby mi to i produkovalo spravny vysledky
            // TODO: DRAW PANEL THINGS
            fontSize = Rocnikovy_Projekt.Program.getFont(fontSize, g, labels, textBinWidth - textWhitespace, Integer.MAX_VALUE, n);
            n *= 2;
            textBinWidth *= 2;
            System.out.println("FT:" + "\t" + fontSize);
        }
        n /= 2;
        textBinWidth /= 2;

        if (fontSize < MIN_FONT) {
            enoughSpaceForLabels = false;
        }

//        System.out.println("MAX:\t" + maxEnergy);
//        System.out.println(selectedBin);


        int binWidthWithSpace = binWidth + binsWhitespace;
        int indexToStartAddingPixels = binCount - freePixels;

        boolean isFirstAdded = true;
        for (int bin = 0, currX = 0; bin < drawValues.length; bin++, currX += binWidthWithSpace) {
            if (ALLOW_DIFFERENT_WIDTH_BINS) {
                if (bin >= indexToStartAddingPixels && isFirstAdded) {
                    isFirstAdded = false;
                    binWidth++;
                    binWidthWithSpace++;
                }
            }

            drawBinMain(g, bin, currX, binWidth, h);
        }


        drawLabels(indexToStartAddingPixels, binWidthWithSpace, binWidth, enoughSpaceForLabels,
                textBinWidth, h, g, n, labels, drawValues.length, ALLOW_DIFFERENT_WIDTH_BINS, shouldDrawLabelsAtTop);
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

    private void drawBinMain(Graphics g, int bin, int currX, int binWidth, int h) {
        g.setColor(getBinColor(bin));
        drawBin(g, drawValues[bin], currX, binWidth, h);
        if(bin == selectedBin) {
            g.setColor(new Color(0, 0, 255, 32));
            g.fillRect(currX, 0, binWidth, h);
        }
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
                                  int binCount, boolean areDifferentSizeBinsAllowed, boolean shouldDrawUp) {
        int labelHeight = 1 + g.getFontMetrics().getHeight() / 2;
        int lastLabelIndex = Program.convertToMultipleDown(binCount - 1, n);
        if(lastLabelIndex ==  binCount - 1) {
            lastLabelIndex -= n;
        }
        // TODO: DRAW PANEL THINGS
//        DrawPanel - drawLabels()
//        Za 1) Je to pomaly muzu skakat po tech n misto abych moduloval,
//        za 2) musim nejak vyresit ten rpvni a posledni label.
//        a) bud muzu prvni a posledni label vynechat (resp. asi staci jen ten posledni) ale pak to vypada divne a hlavne neni videt maximalni hodnota
//        b) vymazat ten 1. a pred pred posledni ale pak tam je velka mezera takze to taky nechci
//        c) Muzu ty labely zmensit natolik resp. udelat taky velky n aby ty mezery byly dostatecne velky - to je podle me absolutne nejlepsi reseni
        // TODO: DRAW PANEL THINGS
        boolean isFirstAdded = false;
        if(shouldDrawLabels) {
            if (areDifferentSizeBinsAllowed && indexToStartAddingPixels < binCount) {
                isFirstAdded = true;
                binWidth--;
                binWidthWithSpace--;
            }

            for (int bin = 0, currX = 0; bin < binCount; bin++, currX += binWidthWithSpace) {
                if (areDifferentSizeBinsAllowed && bin >= indexToStartAddingPixels && isFirstAdded) {
                    isFirstAdded = false;
                    binWidth++;
                    binWidthWithSpace++;
                }

                int y;
                if(shouldDrawUp) {
                    y = labelHeight;
                }
                else {
                    y = h;
                }
                Color c = Color.black;
                if (bin == 0) {
                    // When the first number has minus sign then minus sign isn't visible, but this the only way to make
                    // all the other strings fit without overlapping.
//                    Rocnikovy_Projekt.Program.drawStringWithSpace(g, c, labels[bin], currX - labelWidth / 4, labelWidth, y);
                    Rocnikovy_Projekt.Program.drawStringWithSpace(g, c, labels[bin], currX - labelWidth / 8, labelWidth, y);
                }
                else if (bin == binCount - 1) {
//                    Rocnikovy_Projekt.Program.drawStringWithSpace(g, c, labels[bin], currX - 3 * labelWidth / 4, labelWidth, y);
                    Rocnikovy_Projekt.Program.drawStringWithSpace(g, c, labels[bin], currX - 7 * labelWidth / 8, labelWidth, y);
                }
                else if(bin == n || bin == lastLabelIndex ) {
                    continue;
                }
                else if (bin % n == 0) {
                    Rocnikovy_Projekt.Program.drawStringWithSpace(g, c, labels[bin], currX - labelWidth / 2, labelWidth, y);
                }
            }
        }
    }
}
