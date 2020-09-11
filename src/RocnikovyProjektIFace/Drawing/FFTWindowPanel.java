package RocnikovyProjektIFace.Drawing;

import Rocnikovy_Projekt.Aggregations;
import Rocnikovy_Projekt.Program;
import org.jtransforms.fft.DoubleFFT_1D;

import java.awt.*;
import java.util.Arrays;

public class FFTWindowPanel extends FFTWindowPanelAbstract {
    public FFTWindowPanel(double[] song, int windowSize, int startIndex, int sampleRate,
                          int numberOfChannels, boolean isEditable,
                          Color backgroundColor, boolean shouldDrawLabelsAtTop) {
        this(song, windowSize, startIndex, Program.getFreqJump(sampleRate, windowSize),
                numberOfChannels, isEditable, backgroundColor, shouldDrawLabelsAtTop);
    }

    public FFTWindowPanel(double[] song, int windowSize, int startIndex, double freqJump,
                          int numberOfChannels, boolean isEditable,
                          Color backgroundColor, boolean shouldDrawLabelsAtTop) {
        super(song, windowSize, startIndex, freqJump, numberOfChannels,
                isEditable, false, backgroundColor, shouldDrawLabelsAtTop, false);

        if(song != null) {
            Program.calculateFFTRealForward(song, startIndex, fftResult.length, numberOfChannels, fft, fftResult);
        }
        else {
            Program.setOneDimArr(fftResult, 0, fftResult.length, 0);
        }
        Program.convertResultsOfFFTToRealRealForward(fftResult, drawValues);
        normalizeAndSetDrawValues();
        setLastPartOfTooltip();
    }


    @Override
    public FFTWindowPanelAbstract createNewFFTPanel(int windowSize, boolean shouldChangeWindowSize,
                                            int sampleRate, boolean shouldChangeSampleRate) {
        if(!shouldChangeWindowSize) {
            windowSize = this.WINDOW_SIZE;
        }

        double freqJump;
        if(!shouldChangeSampleRate) {
            freqJump = this.FREQ_JUMP;
        }
        else {
            freqJump = Program.getFreqJump(sampleRate, windowSize);
        }

        return new FFTWindowPanel(null, windowSize, -1, freqJump,
        1, getIsEditable(), getBackgroundColor(), getShouldDrawLabelsAtTop());
    }


    /**
     * Isn't called anywhere it is just marker, that the labels needs to be set in deriving class.
     */
    @Override
    protected void setLabels() {
        // EMPTY
    }

    @Override
    protected Color getBinColor(int bin) {
        return Color.red;
    }

    @Override
    protected void setBinValue(int bin, int y) {
        int h = this.getHeight();
        double binValue = 1 - (y / (double)h);
        if(binValue < 0) {
            binValue = 0;
        }
        else if(binValue > 1) {
            binValue = 1;
        }

        setDrawValue(bin, binValue);
    }

    public static void normalizeFFTResultsRealForward(double[] fftMeasures) {
        // Normalization and getting string representation
        for (int i = 0; i < fftMeasures.length; i++) {
            fftMeasures[i] *= 2;
            fftMeasures[i] /= (fftMeasures.length / 2);
        }
    }




    public static void getRealIFFT(double[] fftArr, DoubleFFT_1D fft) {
        fft.realInverse(fftArr, true);
    }


    public double[] getIFFTResult(boolean setImagPartToZero, int periodCount) {
        // TODO: DEBUG
//        for(int i = 0; i < fftMeasures.length; i++) {
//            ProgramTest.debugPrint("IFFT:", i, fftMeasures[i]);
//        }
        // TODO: DEBUG

// TODO: DEBUG
//        double[] todo = new double[fftResult.length];
//        double[] todo2 = new double[fftResult.length];
//        Program.convertFFTAmplitudesToClassicFFTArr(fftMeasures, todo);
//        Program.convertFFTAmplitudesToClassicFFTArrRandom(fftMeasures, todo2);
//
//        for(int i = 0; i < fftResult.length; i++) {
//            todo[i] *= fftMeasures.length;
//            todo2[i] *= fftMeasures.length;
//        }
//
//        getRealIFFT(todo, fft);
//        normalize(todo);
//        getRealIFFT(todo2, fft);
//        normalize(todo2);
//        if(Arrays.equals(todo, todo2)) {
//            // TODO: DEBUG
//            System.exit(15456);
//        }
        // TODO: DEBUG


        if(setImagPartToZero) {
            Program.convertFFTAmplitudesToClassicFFTArr(drawValues, fftResult);
        }
        else {
            Program.convertFFTAmplitudesToClassicFFTArrRandom(drawValues, fftResult);
        }


        for(int i = 0; i < fftResult.length; i++) {
            // TODO: DEBUG
            //ProgramTest.debugPrint("IFFT:", i, fftResult[i]);
            // TODO: DEBUG
            fftResult[i] *= drawValues.length;
        }
        getRealIFFT(fftResult, fft);
        normalize(fftResult);

        double[] ifftResult = Program.copyArr(fftResult, fftResult.length, periodCount);
        return ifftResult;
    }


    public static void normalize(double[] arr) {
        double max = Program.performAggregation(arr, Aggregations.ABS_MAX);

        for(int i = 0; i < arr.length; i++) {
            arr[i] /= max;
        }
    }



// TODO: VYMAZAT Vsechno pod timhle
//    private void normalizeAndSetMeasureStrings() {
//        // Normalization and getting string representation
//        for (int i = 0; i < fftMeasures.length; i++) {
//            // This 2 lines are from the book Computer music synthesis, composition and performance by Dodge Jerse,
//            // but the factor of 4 seems to be redundant, because when I remove them then the maximum possible value is 1.
////            fftMeasures[i] *= 2;
////            fftMeasures[i] /= (fftMeasures.length / 2);
//            fftMeasures[i] /= fftMeasures.length;
//            fftMeasuresString[i] = String.format("%.2f", fftMeasures[i]);
//        }
//    }
//
//    public static void normalizeFFTResultsRealForward(double[] fftMeasures) {
//        // Normalization and getting string representation
//        for (int i = 0; i < fftMeasures.length; i++) {
//            fftMeasures[i] *= 2;
//            fftMeasures[i] /= (fftMeasures.length / 2);
//        }
//    }
//
//
//    private void tryChangeBin(Point p, boolean isDragEvent) {
//        int bin = getBinAtPos(p);
//        System.out.println("BIN:\t" + bin + ":" + selectedBin);
//        if (!isDragEvent || (bin <= selectedBin + 1 && bin >= selectedBin - 1)) {       // If moved at max to next bin
//            setBinValue(bin, p.y);
//        } else {
//            //jumpOverMultipleBinsSimple(bin, p.y);
//            jumpOverMultipleBinsAdvanced(bin, p);
//        }
//
//        changeToolTip(bin);
//        selectedBin = bin;
//        this.repaint();
//    }
//
//
//    private void jumpOverMultipleBinsSimple(int bin, int y) {
//        if(bin < selectedBin) {
//            for (int i = selectedBin; i >= bin; i--) {
//                setBinValue(i, y);
//            }
//        }
//        else {
//            for (int i = selectedBin; i <= bin; i++) {
//                setBinValue(i, y);
//            }
//        }
//    }
//
//    private void jumpOverMultipleBinsAdvanced(int bin, Point p) {
//        double jump;
//        if(oldMouseLoc == null) {
//            oldMouseLoc = p;
//        }
//        double y = oldMouseLoc.y;
//
//        if(bin < selectedBin) {
//            jump = (p.y - oldMouseLoc.y) / (double)(selectedBin - bin);
//            System.out.println("Y!!!!!!!\t" + y);
//            for (int i = selectedBin; i >= bin; i--, y += jump) {
//                setBinValue(i, (int)y);
//            }
//        }
//        else {
//            jump = (p.y - oldMouseLoc.y) / (double)(bin - selectedBin);
//            System.out.println("Y!!!!!!!\t" + y);
//            for (int i = selectedBin; i <= bin; i++, y += jump) {
//                setBinValue(i, (int)y);
//            }
//        }
//    }
//
//    private void tryChangeBin(MouseEvent e, boolean isDragEvent) {
//        Point p = e.getPoint();
//        tryChangeBin(p, isDragEvent);
//    }
//
//    @Override
//    public void mouseDragged(MouseEvent e) {
//        if(isRightClick) {
//            setBinValue(selectedBin, e.getY());       // The selected bin was set at the mouse pressed event
//            changeToolTip(selectedBin);
//            this.repaint();
//        }
//        else {
//            tryChangeBin(e, true);
//            oldMouseLoc = e.getPoint();
//        }
//    }
//
//    @Override
//    public void mouseMoved(MouseEvent e) {
//        int bin = getBinAtPos(e.getPoint());
//        setSelectedBin(bin);
//        this.repaint();
//    }
//
//
//    private void changeToolTip(int bin) {
//        String binString = bins[bin];
//        setBinToolTip(binString);
//
//        String measure = fftMeasuresString[bin];
//        setMeasureToolTip(measure);
//
//        String frequency = binFreqs[bin];
//        setFrequencyToolTip(frequency);
//
//        this.setToolTipText(tooltip.toString());
//    }
//
//
//    private void setBinToolTip(String binString) {
//        for (int i = INDEX_IN_STRINGBUILDER_AFTER_BIN, j = 0; j < binString.length(); i++, j++) {
//            tooltip.setCharAt(i, binString.charAt(j));
//        }
//
//        for (int i = INDEX_IN_STRINGBUILDER_AFTER_BIN + binString.length(); i < MEASURE_TEXT_INDEX; i++) {
//            tooltip.setCharAt(i, ' ');
//        }
//    }
//
//    private void setMeasureToolTip(String measure) {
//        for (int i = MEASURE_VALUE_INDEX, j = 0; j < measure.length(); i++, j++) {
//            tooltip.setCharAt(i, measure.charAt(j));
//        }
//
//        for(int i = MEASURE_VALUE_INDEX + measure.length(); i < MEASURE_VALUE_INDEX + MAX_MEASURE; i++) {
//            tooltip.setCharAt(i, ' ');
//        }
//    }
//
//    private void setFrequencyToolTip(String frequency) {
//        for (int i = FREQUENCY_VALUE_INDEX, j = 0; j < frequency.length(); i++, j++) {
//            tooltip.setCharAt(i, frequency.charAt(j));
//        }
//
//        for(int i = FREQUENCY_VALUE_INDEX + frequency.length(); i < FREQUENCY_VALUE_INDEX + longestFreqLen; i++) {
//            tooltip.setCharAt(i, ' ');
//        }
//    }
//
//
//
//    @Override
//    public void mouseClicked(MouseEvent e) {
//        // EMPTY
//    }
//
//
//    private boolean isRightClick = false;
//
//    @Override
//    public void mousePressed(MouseEvent e) {
//        isRightClick = e.getButton() != MouseEvent.BUTTON1;
//        tryChangeBin(e, false);
//    }
//
//    @Override
//    public void mouseReleased(MouseEvent e) {
//        // EMPTY
//    }
//
//    @Override
//    public void mouseEntered(MouseEvent e) {
//        // EMPTY
//    }
//
//    @Override
//    public void mouseExited(MouseEvent e) {
//        // EMPTY
//    }
//
//
//    // TODO: Possible optimisation by redrawing only the chosen bin, or adjacent bins
//    @Override
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//
//        drawWindow(g);
//    }
//
//
//    private Dimension minSize;
//    @Override
//    public Dimension getMinimumSize() {
//        return minSize;
//    }
//
//    @Override
//    public Dimension getPreferredSize() {
//        Dimension prefSize = super.getPreferredSize();
//        if(prefSize.width < minSize.width || prefSize.height < minSize.height) {
//            return minSize;
//        }
//        return prefSize;
//    }
//
//
//    /**
//     * Checks inside what bin is the given position.
//     * @param pos is the position to be checked inside what bin it is.
//     * @return Returns the closest bin (Because when I returned -1 in spaces it wasn't behaving the correct way). (Starting at 0). Even if
//     */
//    public int getBinAtPos(Point pos) {
//        int w;
//        w = this.getWidth();
//        int x = Math.max(0, pos.x);
//        x = Math.min(x, w);
//
//
//        int binCount = fftMeasures.length;
//        int binWidth = w / binCount;
//        int freePixels = w % binCount;
//        int binsWhitespace = binWidth / 4;
//        binWidth -= binsWhitespace;
//
//        int indexToStartAddingPixels = binCount - freePixels;
//        int binWidthWithSpace = binWidth + binsWhitespace;
//
//
//
//// Slow variant of the code under this
////        int bin;
////        int currX;
////        for(bin = 0, currX = 0; currX < x; bin++, currX += binWidthWithSpace) {
////            if(bin > indexToStartAddingPixels) {
////                currX++;
////            }
////        }
////        if(bin <= 0) {
////            return 0;
////        }
////        else if(bin >= binCount) {
////            return binCount - 1;
////        }
////        return bin - 1;
//
//
//
//        int forgotPixels = 0;
//        int bin = x / binWidthWithSpace;
//        if(bin >= indexToStartAddingPixels) {
//            forgotPixels = bin - indexToStartAddingPixels;
//            int invalidBins = forgotPixels / (binWidthWithSpace + 1);
//            bin -= invalidBins;
//
//            int previousBinEnd = (bin - indexToStartAddingPixels - 1) * (binWidthWithSpace + 1) +
//                indexToStartAddingPixels * binWidthWithSpace + binWidth + 1;
//            if(forgotPixels % (binWidthWithSpace + 1) != 0 && x <= previousBinEnd) {
//
//                bin--;
//            }
//        }
//
//        if(bin >= binCount) {
//            bin = binCount - 1;
//        }
//        return bin;
//    }
//
//
//    private void setBinValue(int bin, int y) {
//        int h = this.getHeight();
//        fftMeasures[bin] = 1 - (y / (double)h);         // TODO: RELATIVE
//        if(fftMeasures[bin] < 0) {
//            fftMeasures[bin] = 0;
//        }
//        else if(fftMeasures[bin] > 1) {
//            fftMeasures[bin] = 1;
//        }
//
//        fftMeasuresString[bin] = String.format("%.2f", fftMeasures[bin]);
//    }
//
//
//    public void drawWindow(Graphics g) {
//        int w,h;
//        w = this.getWidth();
//        h = this.getHeight();
//
//        int binCount = fftMeasures.length;
//        int binWidth = w / binCount;
//        int freePixels = w % binCount;
//        int binsWhitespace = binWidth / 4;
//        binWidth -= binsWhitespace;
//
//        int indexToStartAddingPixels = binCount - freePixels;
//        int binWidthWithSpace = binWidth + binsWhitespace;
//
//        g.setColor(Color.white);
//        g.fillRect(0, 0, w, h);
//
//        // Find fitting font for frequency labels amd for energies
//        final int START_FONT_SIZE = 24;
//        final int MIN_FONT = 12;
//        boolean enoughSpaceForLabels = true;
//        int jumpIfTooSmall = 0;
//        int n = 1;
//        int textBinWidth = binWidth;
//        int fontSize = 0;
//        while(fontSize < MIN_FONT && n < binFreqs.length) {
//            fontSize = START_FONT_SIZE;
//            int textWhitespace = textBinWidth / 4;
//            fontSize = Program.getFont(fontSize, g, binFreqs, textBinWidth - textWhitespace, Integer.MAX_VALUE, n);
//            n *= 2;
//            textBinWidth *= 2;
//            System.out.println("FT:" + "\t" + fontSize);
//        }
//        n /= 2;
//        textBinWidth /= 2;
//
//        if(fontSize < MIN_FONT) {
//            enoughSpaceForLabels = false;
//        }
//
////        System.out.println("MAX:\t" + maxEnergy);
////        System.out.println(selectedBin);
//
//        boolean isFirstAdded = true;
//        for(int bin = 0, currX = 0; bin < fftMeasures.length; bin++, currX += binWidthWithSpace) {
//            int height = (int)(fftMeasures[bin] * h);
//
//            if(bin >= indexToStartAddingPixels && isFirstAdded) {
//                isFirstAdded = false;
//                binWidth++;
//                binWidthWithSpace++;
//            }
//
//
//            g.setColor(Color.red);
//            g.fillRect(currX, h - height, binWidth, height);
//            if(bin == selectedBin) {
//                g.setColor(new Color(0,0,255, 32));
//                g.fillRect(currX, 0, binWidth, h);
//            }
//        }
//
//
//
//        drawLabels(indexToStartAddingPixels, binWidthWithSpace, isFirstAdded,
//                binWidth, enoughSpaceForLabels, textBinWidth, h, g, n, binFreqs);
////        if(enoughSpaceForLabels) {
////            if (indexToStartAddingPixels < fftMeasures.length) {
////                isFirstAdded = true;
////                binWidth--;
////                binWidthWithSpace--;
////            }
////            for (int bin = 0, currX = 0; bin < fftMeasures.length; bin++, currX += binWidthWithSpace) {
////                if (bin >= indexToStartAddingPixels && isFirstAdded) {
////                    isFirstAdded = false;
////                    binWidth++;
////                    binWidthWithSpace++;
////                }
////
////
////                Color c = Color.black;
////                if (bin == fftMeasures.length - 1) {
////                    Program.drawStringWithSpace(g, c, binFreqs[bin], currX - 3 * textBinWidth / 4, textBinWidth, h);
////                } else if (bin == 0) {
////                    Program.drawStringWithSpace(g, c, binFreqs[bin], currX - textBinWidth / 4, textBinWidth, h);
////                } else if (bin % n == 0) {
////                    // Draw frequency
////                    Program.drawStringWithSpace(g, c, binFreqs[bin], currX - textBinWidth / 2, textBinWidth, h);
////                }
////            }
////        }
//    }
//
//
//    protected int calculateFontSize() {
//        sice pekny ale to nemuzu nebot tahle metoda ma vic vystupnich parametru
//    }
//
//    /**
//     * Draws every n-th label
//     * @param indexToStartAddingPixels because usually width % labels.length != 0 then, we need to make the spaces 1 pixel larger from some index, so it can fit.
//     * @param binWidthWithSpace is the binWidth but also containing space between the bins.
//     * @param isFirstAdded
//     * @param binWidth is the width of 1 bin. (In case of wave drawing it is 1 pixel, in case of FFT can be 1 or more, depends on size of window).
//     * @param shouldDrawLabels
//     * @param labelWidth
//     * @param h is the height of the panel
//     * @param g is the graphics
//     * @param n - every n-th label is drawed
//     * @param labels are the labels
//     */
//    public static void drawLabels(int indexToStartAddingPixels, int binWidthWithSpace, boolean isFirstAdded, int binWidth,
//                                  boolean shouldDrawLabels, int labelWidth, int h, Graphics g, int n, String[] labels) {
//        if(shouldDrawLabels) {
//            if (indexToStartAddingPixels < labels.length) {
//                isFirstAdded = true;
//                binWidth--;
//                binWidthWithSpace--;
//            }
//            for (int bin = 0, currX = 0; bin < labels.length; bin++, currX += binWidthWithSpace) {
//                if (bin >= indexToStartAddingPixels && isFirstAdded) {
//                    isFirstAdded = false;
//                    binWidth++;
//                    binWidthWithSpace++;
//                }
//
//
//                Color c = Color.black;
//                if (bin == labels.length - 1) {
//                    Program.drawStringWithSpace(g, c, labels[bin], currX - 3 * labelWidth / 4, labelWidth, h);
//                } else if (bin == 0) {
//                    Program.drawStringWithSpace(g, c, labels[bin], currX - labelWidth / 4, labelWidth, h);
//                } else if (bin % n == 0) {
//                    // Draw frequency
//                    Program.drawStringWithSpace(g, c, labels[bin], currX - labelWidth / 2, labelWidth, h);
//                }
//            }
//        }
//    }
//
//
//
//    private int getMinFontSize() {
//
//    }
//
//
//    public static void getRealIFFT(double[] fftArr, DoubleFFT_1D fft) {
//        fft.realInverse(fftArr, true);
//    }
//
//
//    public double[] getIFFTResult(boolean setImagPartToZero) {
//        // TODO: DEBUG
////        for(int i = 0; i < fftMeasures.length; i++) {
////            ProgramTest.debugPrint("IFFT:", i, fftMeasures[i]);
////        }
//        // TODO: DEBUG
//
//// TODO: DEBUG
////        double[] todo = new double[fftResult.length];
////        double[] todo2 = new double[fftResult.length];
////        Program.convertFFTAmplitudesToClassicFFTArr(fftMeasures, todo);
////        Program.convertFFTAmplitudesToClassicFFTArrRandom(fftMeasures, todo2);
////
////        for(int i = 0; i < fftResult.length; i++) {
////            todo[i] *= fftMeasures.length;
////            todo2[i] *= fftMeasures.length;
////        }
////
////        getRealIFFT(todo, fft);
////        normalize(todo);
////        getRealIFFT(todo2, fft);
////        normalize(todo2);
////        if(Arrays.equals(todo, todo2)) {
////            // TODO: DEBUG
////            System.exit(15456);
////        }
//        // TODO: DEBUG
//
//
//        if(setImagPartToZero) {
//            Program.convertFFTAmplitudesToClassicFFTArr(fftMeasures, fftResult);
//        }
//        else {
//            Program.convertFFTAmplitudesToClassicFFTArrRandom(fftMeasures, fftResult);
//        }
//
//
//        for(int i = 0; i < fftResult.length; i++) {
//            // TODO: DEBUG
//            //ProgramTest.debugPrint("IFFT:", i, fftResult[i]);
//            // TODO: DEBUG
//            fftResult[i] *= fftMeasures.length;
//        }
//        getRealIFFT(fftResult, fft);
//        normalize(fftResult);
//
//        return Arrays.copyOf(fftResult, fftResult.length);
//    }
//
//
//    public static void normalize(double[] arr) {
//        double max = Program.performAggregation(arr, Aggregations.ABS_MAX);
//
//        for(int i = 0; i < arr.length; i++) {
//            arr[i] /= max;
//        }
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    ////////////////////////////////////////////////////////////////////////////////////
//    ///////////////////// Create FFT Window image
//    ////////////////////////////////////////////////////////////////////////////////////
//    @Deprecated
//    public static BufferedImage createFFTWindowImage(double[] song, int numberOfChannels, int windowSize, double freqJump,
//                                                     int startIndex, int windowWidth, int windowHeight) {
//        double[] fftResult = new double[windowSize];
//        DoubleFFT_1D fft = new DoubleFFT_1D(windowSize);
//        int binCount = Program.getBinCountRealForward(windowSize);
//        double[] fftMeasures = new double[binCount];
//
//        return createFFTWindowImage(song, numberOfChannels, freqJump, startIndex,
//            fft, windowWidth, windowHeight, fftResult, fftMeasures);
//    }
//    // TODO: Udelat ze kdyz ukazu na ten obdelnik tak se mi ukaze frekvence a ta measure (treba nekde vedle) a zvyrazni se to, to dost pomuze
//    // TODO: Ve viditelnosti
//    @Deprecated
//    public static BufferedImage createFFTWindowImage(double[] song, int numberOfChannels, double freqJump,
//                                                     int startIndex, DoubleFFT_1D fft, int windowWidth, int windowHeight,
//                                                     double[] fftResult, double[] fftMeasures) {
//        int binCount = fftMeasures.length;
//        int binWidth = windowWidth / binCount;
//        int freePixels = windowWidth % binCount;
//        int binsWhitespace = binWidth / 4;
//        binWidth -= binsWhitespace;
//
//        int indexToStartAddingPixels = binCount - freePixels;
//        int binWidthWithSpace = binWidth + binsWhitespace;
//
//        Program.calculateFFTRealForward(song, startIndex, numberOfChannels, fft, fftResult);
//        Program.convertResultsOfFFTToRealRealForward(fftResult, fftMeasures);
//
//        BufferedImage image = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
//        Graphics g = image.getGraphics();
//        g.setColor(Color.white);
//        g.fillRect(0, 0, windowWidth, windowHeight);
//
//        // Find maxEnergy for normalization
//        double maxEnergy = 0;
//        for(int i = 0; i < fftMeasures.length; i++) {
//            if(fftMeasures[i] > maxEnergy) {
//                maxEnergy = fftMeasures[i];
//            }
//        }
//        if(maxEnergy == 0) {
//            return image;
//        }
//
//        // Normalization and getting string representation
//        String[] fftMeasuresString = new String[fftMeasures.length];
//        for(int i = 0; i < fftMeasures.length; i++) {
//            fftMeasures[i] /= maxEnergy;
//            fftMeasuresString[i] = String.format("%.2f", fftMeasures[i]);
//        }
//
//
//        // Set frequency labels for bins
//        String[] binFreqs = Program.getFreqs(binCount, freqJump, 0, 1);
//
//        // Find fitting font for frequency labels amd for energies
//        int fontSize = 24;
//        fontSize = Program.getFont(fontSize, g, binFreqs, binWidth, Integer.MAX_VALUE, 1);
//        fontSize = Program.getFont(fontSize, g, fftMeasuresString, binWidth, Integer.MAX_VALUE, 1);
//        FontMetrics fontMetrics = g.getFontMetrics();
//
//
//        boolean isFirstAdded = true;
//        for(int bin = 0, currX = 0; bin < fftMeasures.length; bin++, currX += binWidthWithSpace) {
//            System.out.println("MAX:\t" + maxEnergy);
//            int height = (int)(fftMeasures[bin] * windowHeight);
//
//            if(bin > indexToStartAddingPixels && isFirstAdded) {
//                isFirstAdded = false;
//                binWidth++;
//                binWidthWithSpace++;
//            }
//
//            g.setColor(Color.red);
//            g.fillRect(currX, windowHeight - height, binWidth, height);
//
//
//            Color c = Color.black;
//
//            // Draw frequency
//            Program.drawStringWithSpace(g, c, binFreqs[bin], currX, binWidth, windowHeight);
//            // Draw measures
//            Program.drawStringWithSpace(g, c, fftMeasuresString[bin], currX, binWidth, 16);
//        }
//
//        return image;
//    }
}
