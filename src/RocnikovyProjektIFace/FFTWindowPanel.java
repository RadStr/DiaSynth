package RocnikovyProjektIFace;

import Rocnikovy_Projekt.Aggregations;
import Rocnikovy_Projekt.Program;
import Rocnikovy_Projekt.ProgramTest;
import org.jtransforms.fft.DoubleFFT_1D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class FFTWindowPanel extends JPanel implements MouseMotionListener, MouseListener {
    public FFTWindowPanel(double[] song, int windowSize, int startIndex, int sampleRate, int numberOfChannels) {
        this(song, windowSize, startIndex, Program.getFreqJump(sampleRate, windowSize), numberOfChannels);
    }

    public FFTWindowPanel(double[] song, int windowSize, int startIndex, double freqJump, int numberOfChannels) {
        this.freqJump = freqJump;

        fftResult = new double[windowSize];
        fft = new DoubleFFT_1D(windowSize);
        int binCount = Program.getBinCountRealForward(windowSize);
        fftMeasures = new double[binCount];

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double w = screenSize.getWidth();
        double h = screenSize.getHeight();

        minSize = new Dimension();
        minSize.width = Math.min(2*binCount, (int)w);
        minSize.height = 100;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        Program.calculateFFTRealForward(song, startIndex, numberOfChannels, fft, fftResult);
        Program.convertResultsOfFFTToRealRealForward(fftResult, fftMeasures);

        BIN_COUNT = Integer.toString(binCount).length();
        MAX_MEASURE = BIN_COUNT + 3;      // BIN_COUNT + .xx

        tooltip = new StringBuilder("<html>Bin: ");
        INDEX_IN_STRINGBUILDER_AFTER_BIN = tooltip.length();
        int index = INDEX_IN_STRINGBUILDER_AFTER_BIN;
        for(int i = 0; i < BIN_COUNT; i++, index++) {
            tooltip.insert(index, ' ');
        }

        final String newLine = "<br>";
        tooltip.insert(index, newLine);
        index += newLine.length();
        MEASURE_TEXT_INDEX = index;
        String measureString = newLine + "Measure: ";
        tooltip.insert(index, measureString);
        index += measureString.length();
        MEASURE_VALUE_INDEX = index;
        for(int i = 0; i < MAX_MEASURE; i++, index++) {
            tooltip.insert(index, ' ');
        }

        String tmp = newLine + "Frequency: ";
        tooltip.insert(index, tmp);
        index += tmp.length();
        FREQUENCY_VALUE_INDEX = index;


        fftMeasuresString = new String[fftMeasures.length];
        normalizeAndSetMeasureStrings();

        bins = new String[binCount];
        for (int i = 0; i < bins.length; i++) {
            bins[i] = Integer.toString(i);
        }

        // Set frequency labels for bins
        binFreqs = Program.getFreqs(binCount, freqJump, 0, 1);
        longestFreqLen = 0;
        for (String s : binFreqs) {
            if(s.length() > longestFreqLen) {
                longestFreqLen = s.length();
            }
        }

        for(int i = 0; i < longestFreqLen; i++, index++) {
            tooltip.insert(index, ' ');
        }

        tooltip.insert(index, newLine);
        index += newLine.length();
    }

    private final int BIN_COUNT;
    private final int MAX_MEASURE;      // BIN_COUNT + .xx

    private final int INDEX_IN_STRINGBUILDER_AFTER_BIN;
    private final int MEASURE_TEXT_INDEX;
    private final int MEASURE_VALUE_INDEX;
    private final int FREQUENCY_VALUE_INDEX;

    private final double[] fftResult;
    private final double[] fftMeasures;
    public double[] getFftMeasures() {
        return fftMeasures;
    }
    private final DoubleFFT_1D fft;
    private final double freqJump;

    private final String[] bins;
    private final String[] fftMeasuresString;
    private final String[] binFreqs;
    private int longestFreqLen;


    private int selectedBin = -1;
    private void setSelectedBin(int bin) {
        if(bin != selectedBin) {
            changeToolTip(bin);
            selectedBin = bin;
        }
    }

    private StringBuilder tooltip;

    private Point oldMouseLoc;

    private void normalizeAndSetMeasureStrings() {
        // Normalization and getting string representation
        for (int i = 0; i < fftMeasures.length; i++) {
            // This 2 lines are from the book Computer music synthesis, composition and performance by Dodge Jerse,
            // but the factor of 4 seems to be redundant, because when I remove them then the maximum possible value is 1.
//            fftMeasures[i] *= 2;
//            fftMeasures[i] /= (fftMeasures.length / 2);
            fftMeasures[i] /= fftMeasures.length;
            fftMeasuresString[i] = String.format("%.2f", fftMeasures[i]);
        }
    }

    public static void normalizeFFTResultsRealForward(double[] fftMeasures) {
        // Normalization and getting string representation
        for (int i = 0; i < fftMeasures.length; i++) {
            fftMeasures[i] *= 2;
            fftMeasures[i] /= (fftMeasures.length / 2);
        }
    }


    private void tryChangeBin(Point p) {
        int bin = getBinAtPos(p);
        System.out.println("BIN:\t" + bin + ":" + selectedBin);
        if ((bin <= selectedBin + 1 && bin >= selectedBin - 1)) {       // If moved at max to next bin
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

    private void tryChangeBin(MouseEvent e) {
        Point p = e.getPoint();
        tryChangeBin(p);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(isRightClick) {
            setBinMeasure(selectedBin, e.getY());       // The selected bin was set at the mouse pressed event
            changeToolTip(selectedBin);
            this.repaint();
        }
        else {
            tryChangeBin(e);
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
        String binString = bins[bin];
        setBinToolTip(binString);

        String measure = fftMeasuresString[bin];
        setMeasureToolTip(measure);

        String frequency = binFreqs[bin];
        setFrequencyToolTip(frequency);

        this.setToolTipText(tooltip.toString());
    }


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

        for(int i = MEASURE_VALUE_INDEX + measure.length(); i < MEASURE_VALUE_INDEX + MAX_MEASURE; i++) {
            tooltip.setCharAt(i, ' ');
        }
    }

    private void setFrequencyToolTip(String frequency) {
        for (int i = FREQUENCY_VALUE_INDEX, j = 0; j < frequency.length(); i++, j++) {
            tooltip.setCharAt(i, frequency.charAt(j));
        }

        for(int i = FREQUENCY_VALUE_INDEX + frequency.length(); i < FREQUENCY_VALUE_INDEX + longestFreqLen; i++) {
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
        tryChangeBin(e);
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


    // TODO: Possible optimisation by redrawing only the chosen bin, or adjacent bins
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


        int binCount = fftMeasures.length;
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


    private void setBinMeasure(int bin, int y) {
        int h = this.getHeight();
        fftMeasures[bin] = 1 - (y / (double)h);         // TODO: RELATIVE
        if(fftMeasures[bin] < 0) {
            fftMeasures[bin] = 0;
        }
        else if(fftMeasures[bin] > 1) {
            fftMeasures[bin] = 1;
        }

        fftMeasuresString[bin] = String.format("%.2f", fftMeasures[bin]);
    }


    public void drawFFTWindow(Graphics g) {
        int w,h;
        w = this.getWidth();
        h = this.getHeight();

        int binCount = fftMeasures.length;
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
        boolean isWayTooSmall = false;
        int jumpIfTooSmall = 0;
        int n = 1;
        int textBinWidth = binWidth;
        int fontSize = 0;
        while(fontSize < MIN_FONT && n < binFreqs.length) {
            fontSize = START_FONT_SIZE;
            int textWhitespace = textBinWidth / 4;
            fontSize = Program.getFont(fontSize, g, binFreqs, textBinWidth - textWhitespace, Integer.MAX_VALUE, n);
            n *= 2;
            textBinWidth *= 2;
            System.out.println("FT:" + "\t" + fontSize);
        }
        n /= 2;
        textBinWidth /= 2;

        if(fontSize < MIN_FONT) {
            isWayTooSmall = true;
        }

//        System.out.println("MAX:\t" + maxEnergy);
//        System.out.println(selectedBin);

        boolean isFirstAdded = true;
        for(int bin = 0, currX = 0; bin < fftMeasures.length; bin++, currX += binWidthWithSpace) {
            int height = (int)(fftMeasures[bin] * h);

            if(bin >= indexToStartAddingPixels && isFirstAdded) {
                isFirstAdded = false;
                binWidth++;
                binWidthWithSpace++;
            }


            g.setColor(Color.red);
            g.fillRect(currX, h - height, binWidth, height);
            if(bin == selectedBin) {
                g.setColor(new Color(0,0,255, 32));
                g.fillRect(currX, 0, binWidth, h);
            }
        }



        if(indexToStartAddingPixels < fftMeasures.length) {
            isFirstAdded = true;
            binWidth--;
            binWidthWithSpace--;
        }
        for(int bin = 0, currX = 0; bin < fftMeasures.length; bin++, currX += binWidthWithSpace) {
            if(bin >= indexToStartAddingPixels && isFirstAdded) {
                isFirstAdded = false;
                binWidth++;
                binWidthWithSpace++;
            }


            Color c = Color.black;
            if(!isWayTooSmall) {
                if (bin == fftMeasures.length - 1) {
                    Program.drawStringWithSpace(g, c, binFreqs[bin], currX - 3 * textBinWidth / 4, textBinWidth, h);
                } else if (bin == 0) {
                    Program.drawStringWithSpace(g, c, binFreqs[bin], currX - textBinWidth / 4, textBinWidth, h);
                } else if (bin % n == 0) {
                    // Draw frequency
                    Program.drawStringWithSpace(g, c, binFreqs[bin], currX - textBinWidth / 2, textBinWidth, h);
                }
            }
        }
    }


    public static void getIFFT(double[] fftArr, DoubleFFT_1D fft) {
        fft.realInverse(fftArr, true);
    }


    public double[] getIFFTResult() {
        Program.convertFFTAmplitudesToClassicFFTArr(fftMeasures, fftResult);

        for(int i = 0; i < fftResult.length; i++) {
            fftResult[i] = fftResult[i] * (fftMeasures.length);
        }
        getIFFT(fftResult, fft);
        normalize(fftResult);

        return fftResult;
    }


    public static void normalize(double[] arr) {
        double max = Program.performAggregation(arr, Aggregations.ABS_MAX);

        for(int i = 0; i < arr.length; i++) {
            arr[i] /= max;
        }
    }





















    ////////////////////////////////////////////////////////////////////////////////////
    ///////////////////// Create FFT Window image
    ////////////////////////////////////////////////////////////////////////////////////
    @Deprecated
    public static BufferedImage createFFTWindowImage(double[] song, int numberOfChannels, int windowSize, double freqJump,
                                                     int startIndex, int windowWidth, int windowHeight) {
        double[] fftResult = new double[windowSize];
        DoubleFFT_1D fft = new DoubleFFT_1D(windowSize);
        int binCount = Program.getBinCountRealForward(windowSize);
        double[] fftMeasures = new double[binCount];

        return createFFTWindowImage(song, numberOfChannels, freqJump, startIndex,
            fft, windowWidth, windowHeight, fftResult, fftMeasures);
    }
    // TODO: Udelat ze kdyz ukazu na ten obdelnik tak se mi ukaze frekvence a ta measure (treba nekde vedle) a zvyrazni se to, to dost pomuze
    // TODO: Ve viditelnosti
    @Deprecated
    public static BufferedImage createFFTWindowImage(double[] song, int numberOfChannels, double freqJump,
                                                     int startIndex, DoubleFFT_1D fft, int windowWidth, int windowHeight,
                                                     double[] fftResult, double[] fftMeasures) {
        int binCount = fftMeasures.length;
        int binWidth = windowWidth / binCount;
        int freePixels = windowWidth % binCount;
        int binsWhitespace = binWidth / 4;
        binWidth -= binsWhitespace;

        int indexToStartAddingPixels = binCount - freePixels;
        int binWidthWithSpace = binWidth + binsWhitespace;

        Program.calculateFFTRealForward(song, startIndex, numberOfChannels, fft, fftResult);
        Program.convertResultsOfFFTToRealRealForward(fftResult, fftMeasures);

        BufferedImage image = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, windowWidth, windowHeight);

        // Find maxEnergy for normalization
        double maxEnergy = 0;
        for(int i = 0; i < fftMeasures.length; i++) {
            if(fftMeasures[i] > maxEnergy) {
                maxEnergy = fftMeasures[i];
            }
        }
        if(maxEnergy == 0) {
            return image;
        }

        // Normalization and getting string representation
        String[] fftMeasuresString = new String[fftMeasures.length];
        for(int i = 0; i < fftMeasures.length; i++) {
            fftMeasures[i] /= maxEnergy;
            fftMeasuresString[i] = String.format("%.2f", fftMeasures[i]);
        }


        // Set frequency labels for bins
        String[] binFreqs = Program.getFreqs(binCount, freqJump, 0, 1);

        // Find fitting font for frequency labels amd for energies
        int fontSize = 24;
        fontSize = Program.getFont(fontSize, g, binFreqs, binWidth, Integer.MAX_VALUE, 1);
        fontSize = Program.getFont(fontSize, g, fftMeasuresString, binWidth, Integer.MAX_VALUE, 1);
        FontMetrics fontMetrics = g.getFontMetrics();


        boolean isFirstAdded = true;
        for(int bin = 0, currX = 0; bin < fftMeasures.length; bin++, currX += binWidthWithSpace) {
            System.out.println("MAX:\t" + maxEnergy);
            int height = (int)(fftMeasures[bin] * windowHeight);

            if(bin > indexToStartAddingPixels && isFirstAdded) {
                isFirstAdded = false;
                binWidth++;
                binWidthWithSpace++;
            }

            g.setColor(Color.red);
            g.fillRect(currX, windowHeight - height, binWidth, height);


            Color c = Color.black;

            // Draw frequency
            Program.drawStringWithSpace(g, c, binFreqs[bin], currX, binWidth, windowHeight);
            // Draw measures
            Program.drawStringWithSpace(g, c, fftMeasuresString[bin], currX, binWidth, 16);
        }

        return image;
    }
}
