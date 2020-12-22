package player.wave;

import player.AudioPlayerPanel;
import player.popup.WavePanelPopupMenu;
import test.ProgramTest;
import util.Utilities;
import util.audio.AudioConverter;
import util.audio.AudioUtilities;
import util.audio.wave.DoubleWave;
import util.logging.MyLogger;
import util.swing.SwingUtils;
import util.wave.drawing.WaveDrawValues;
import util.wave.drawing.WaveDrawValuesAggregated;
import util.wave.drawing.WaveDrawValuesIndividual;
import util.wave.drawing.ifaces.DrawValuesSupplierIFace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Arrays;

public class WavePanel extends JPanel {
    public final static int ZOOM_VALUE = 2;

    public final int WINDOW_COUNT_TO_THE_RIGHT = 2;


    public static final int START_DEFAULT_WAVE_WIDTH_IN_PIXELS = 1024;
//    public static final int START_DEFAULT_WAVE_WIDTH_IN_PIXELS = 350;     // for testing
    private int defaultWaveWidthInPixels = START_DEFAULT_WAVE_WIDTH_IN_PIXELS;
    public int getDefaultWaveWidthInPixels() {
        return defaultWaveWidthInPixels;
    }


    private DoubleWave doubleWave;
    public DoubleWave getDoubleWave() {
        return doubleWave;
    }
    public int getSongLen() {
        return doubleWave.getSong().length;
    }
    public double getNthSample(int n) {
        return doubleWave.getSong()[n];
    }

    private boolean isCached = false;
    public boolean getIsCached() {
        return isCached;
    }

    private WaveMainPanel wholeWavePanel;

    private int waveWidth = 0;
    public int getWaveWidth() {
        return waveWidth;
    }

    private WavePanelPopupMenu waveRightClickPopUpMenu;
    public void setEnabledWithWavePopUpItems(boolean enabled) {
        waveRightClickPopUpMenu.setEnabledWithWavePopUpItems(enabled);
    }

    public WavePanel(DoubleWave doubleWave, WaveMainPanel wholeWavePanel) {
        this.doubleWave = doubleWave;
        waveRightClickPopUpMenu = new WavePanelPopupMenu(wholeWavePanel);
        this.setComponentPopupMenu(waveRightClickPopUpMenu);
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        this.wholeWavePanel = wholeWavePanel;

        drawValuesSupplierIndividual = new DrawValuesSupplierIndividual();
        drawValuesSupplierAggregated = new DrawValuesSupplierAggregated();

        ComponentListener resizeListener = new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e) {
                wholeWavePanel.revalidate();
                wholeWavePanel.repaint();
                revalidate();
                repaint();
            }
        };
        this.addComponentListener(resizeListener);
    }


    public void setVariablesWhichNeededSize() {
        oldVisibleWaveWidth = wholeWavePanel.getWaveVisibleWidth();
        defaultWaveWidthInPixels = wholeWavePanel.getDefaultWaveWidthFromMainPanel();
        waveWidth = wholeWavePanel.getWaveWidthFromMainPanel();
        int newPreferredWidth;
        newPreferredWidth = Math.max(wholeWavePanel.getWaveVisibleWidth(), this.getVisibleRect().width);
        int newPreferredHeight;
        newPreferredHeight = wholeWavePanel.calculateWavePreferredHeight();
        setPreferredSize(new Dimension(newPreferredWidth, newPreferredHeight));

        int currZoom = wholeWavePanel.getCurrentZoom();
        int maxCacheZoom = calculateMaxCacheZoom();
        zoomVariables = new ZoomVariablesOneWave(currZoom, maxCacheZoom);
// TODO: TESTING WITHOUT CACHE
//        isCached = cacheToHDD();
// TODO: TESTING WITHOUT CACHE
        setCurrentDrawValuesBasedOnZoom();
    }


    private boolean doubleWaveLenChanged = false;
    private void resetLengthChangedMarker() {
        doubleWaveLenChanged = false;
    }
    private void setLengthChangedMarker() {
        doubleWaveLenChanged = true;
    }
    public void reloadDrawValues() {
        setLengthChangedMarker();
    }

    public void setNewDoubleWave(int newLen, int startPasteIndex, int copyLen) {
        double[] oldSong = getDoubleWave().getSong();
        double[] newSong = new double[newLen];
        System.arraycopy(oldSong, 0, newSong, 0, startPasteIndex);
        int startCopyIndex = startPasteIndex;
        startPasteIndex += copyLen;
        int len = newLen - startPasteIndex;
        System.arraycopy(oldSong, startCopyIndex, newSong, startPasteIndex, len);
        setNewDoubleWave(newSong);
        setMaxCacheZoom();
    }
    private void setNewDoubleWave(double[] newSong) {
        doubleWave = new DoubleWave(newSong, doubleWave, false);
        setLengthChangedMarker();
        setMaxCacheZoom();
    }
    private void setNewDoubleWave(double[] newSong, int newSampleRate) {
        doubleWave = new DoubleWave(newSong, newSampleRate, 1,
                doubleWave.getFilenameWithoutExtension(), false);
        setLengthChangedMarker();
        setMaxCacheZoom();
    }

    public void setNewDoubleWave(int newLen) {
        double[] oldSong = getDoubleWave().getSong();
        if(newLen != oldSong.length) {
            double[] newSong = new double[newLen];
            int len = Math.min(oldSong.length, newSong.length);
            System.arraycopy(oldSong, 0, newSong, 0, len);
            setNewDoubleWave(newSong);
        }
        else {
            setLengthChangedMarker();
        }
        setMaxCacheZoom();
    }

    public void setWaveToNewSampleRate(int newSampleRate) {
        double[] newWave;
        try {
            newWave = AudioConverter.convertSampleRate(doubleWave.getSong(), 1,
                    doubleWave.getSampleRate(), newSampleRate, true);
            setNewDoubleWave(newWave, newSampleRate);
        }
        catch(IOException e) {
            // EMPTY
        }
    }


    private int oldVisibleWaveWidth = -1;

    public void visibleWidthChangedCallback() {
        firstWaveDrawingAction();
        fixIndividualSampleBug();

        int visibleWaveWidth = wholeWavePanel.getWaveVisibleWidth();
        if(visibleWaveWidth != oldVisibleWaveWidth || doubleWaveLenChanged || (visibleWaveWidth > waveWidth)) {
            resetLengthChangedMarker();
            ProgramTest.debugPrint("visibleWidthChangedCallback()", visibleWaveWidth, getPreferredSize());
            currScroll = wholeWavePanel.getCurrentHorizontalScroll();
            ProgramTest.debugPrint("WWW:", visibleWaveWidth, waveWidth);
            int newVisibleWidth = repairPreferredWidthToVisibleWidth(visibleWaveWidth);
            currentDrawValues.waveResize(newVisibleWidth, waveWidth, mainWaveClass.getCurrentStartIndexInAudio(), getSongLen());

            ProgramTest.debugPrint("WWW2:", waveWidth, getVisibleRect().width, visibleWaveWidth);
            this.revalidate();
            this.repaint();
        }
    }

    /**
     *
     * @param visibleWaveWidth
     * @return Returns new visible wave width
     */
    private int repairPreferredWidthToVisibleWidth(int visibleWaveWidth) {
        double zoomMultiplication = Math.pow(ZOOM_VALUE, zoomVariables.currentZoom);       // Because I can be larger even when zooming (for example for zoom == 1 I need to have the visible widrth larger than 2048 which is possible)
        boolean shouldCacheAgain = false;
        ProgramTest.debugPrint("WAVE_WIDTH_2", waveWidth, visibleWaveWidth);
        if(visibleWaveWidth > waveWidth) {
            int possibleDefaultWaveWidth = (int)(visibleWaveWidth / zoomMultiplication);
            defaultWaveWidthInPixels = possibleDefaultWaveWidth;
            waveWidth = visibleWaveWidth;
            shouldCacheAgain = true;
        }
        else if (defaultWaveWidthInPixels != START_DEFAULT_WAVE_WIDTH_IN_PIXELS &&
                visibleWaveWidth < waveWidth && waveWidth == defaultWaveWidthInPixels &&
                visibleWaveWidth < START_DEFAULT_WAVE_WIDTH_IN_PIXELS) {
            defaultWaveWidthInPixels = START_DEFAULT_WAVE_WIDTH_IN_PIXELS;
            //defaultWaveWidthInPixels = Math.max(START_DEFAULT_WAVE_WIDTH_IN_PIXELS, visibleWaveWidth);    // TODO:
            waveWidth = (int)(defaultWaveWidthInPixels * zoomMultiplication);
            shouldCacheAgain = true;
        }

        oldVisibleWaveWidth = visibleWaveWidth;
        setMaxCacheZoom();
        int newPrefHeight = wholeWavePanel.calculateWavePreferredHeight();
        this.setPreferredSize(new Dimension(visibleWaveWidth, newPrefHeight));
// TODO: TESTING WITHOUT CACHE
//        if(shouldCacheAgain) {
//            isCached = cacheToHDD();
//        }
// TODO: TESTING WITHOUT CACHE
        return visibleWaveWidth;
    }


    private void setCurrentDrawValuesBasedOnZoom() {
        ProgramTest.debugPrint("setCurrentDrawValuesBasedOnZoom", zoomVariables.currentZoom, zoomVariables.maxCacheZoom);
        if(zoomVariables.currentZoom > zoomVariables.maxCacheZoom) {
            setCurrentDrawValuesToNewIndividual();
        }
        else {
            setCurrentDrawValuesToNewAggregated();
        }
    }

    private void setCurrentDrawValuesToNewIndividual() {
        int leftPixel;
        int visibleWaveWidth = wholeWavePanel.getWaveVisibleWidth();
        int totalWaveWidth = waveWidth;

        drawValuesSupplierIndividual = new DrawValuesSupplierIndividual();
        mainWaveClass = drawValuesSupplierIndividual;

        leftPixel = wholeWavePanel.getCurrentHorizontalScroll();
        totalWaveWidth = waveWidth;
        int startIndex = mainWaveClass.getCurrentStartIndexInAudio();
        int valueCount = getSongLen();

        drawValuesIndividual = new WaveDrawValuesIndividual(leftPixel, visibleWaveWidth,
            totalWaveWidth, startIndex, valueCount, WINDOW_COUNT_TO_THE_RIGHT, drawValuesSupplierIndividual);
        currentDrawValues = drawValuesIndividual;
    }
    private void setCurrentDrawValuesToNewAggregated() {
        drawValuesSupplierAggregated = new DrawValuesSupplierAggregated();
        mainWaveClass = drawValuesSupplierAggregated;
        int visibleWaveWidth = wholeWavePanel.getWaveVisibleWidth();
        int totalWaveWidth = waveWidth;
        int startIndex = mainWaveClass.getCurrentStartIndexInAudio();
        int valueCount = getSongLen();

        drawValuesAggregated = new WaveDrawValuesAggregated(visibleWaveWidth, totalWaveWidth, startIndex, valueCount,
            WINDOW_COUNT_TO_THE_RIGHT, drawValuesSupplierAggregated);
        currentDrawValues = drawValuesAggregated;
    }



    private boolean isFirstWaveDrawing = true;
    private void firstWaveDrawingAction() {
        ProgramTest.debugPrint("wave paintComponent before:", doubleWave.getFilenameWithExtension(),
                               this.getVisibleRect(), getPreferredSize(), getMaxPossibleZoom());
        if(isFirstWaveDrawing) {
            isFirstWaveDrawing = false;
            setVariablesWhichNeededSize();
        }
        ProgramTest.debugPrint("wave paintComponent after:", doubleWave.getFilenameWithExtension(),
                               this.getVisibleRect(), getPreferredSize(), getMaxPossibleZoom());
    }


    /**
     * This image is used, because when we are zooming, we first make the scroll size larger (or smaller if unzooming)
     * and then we shift the scroll to correct positions, but these steps are done separately due to events fired by java.
     * Which is problem, because for example when we zoom, we double the size, but we will have the scroll at the same position,
     * we will shift it in the next step, so now it shows incorrect wave. (That wasn't the main problem, the main
     * problem was that the marking of wave flickered because of that and that was very disturbing for user.)
     * We had to fix that, we had fix the bridge between the steps,
     * so we choose this solution, which is when zooming save the image of the wave and show it until the zooming is done,
     * then we will draw the wave as we usually do (drawing the samples based on the underlying array).
     * The other solution would be to block the first event of resizing scroll and then just do the shifting and after
     * that we will show the correct wave, but that may introduce unexpected problems, which I don't currently have time to
     * solve. I may do it later though. (TODO).
     *
     */
    private Image zoomBridgeImg = null;





    /**
     * The bug is that when we have short wave, which draws individual samples at zoom 0 and we add a large one,
     * then the old wave is enlarged, but the currentDrawValues is still set to individuals and
     * the performance hit is quite big, because we are drawing a lot of individual samples.
     */
    // This is the best solution I came up with probably not the best in general, but it works,
    // zoomVariables is always non-null
    // so that is fine and the maxCacheZoom is updated to correct values in the setNewDouble method.
    private void fixIndividualSampleBug() {
        if(zoomVariables.currentZoom < zoomVariables.maxCacheZoom && currentDrawValues == drawValuesIndividual) {
            int visibleWidth = wholeWavePanel.getWaveVisibleWidth();
            setDrawValuesInZoom(currScroll, visibleWidth, waveWidth);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (wholeWavePanel.isInProcessOfZooming()) {
            super.paintComponent(g);
            if (zoomBridgeImg != null) {
                g.drawImage(zoomBridgeImg, 0, 0, this);
            }

            if (wholeWavePanel.getScrollReceivedResizeEvent()) {
                wholeWavePanel.processScrollReceivedResizeEvent();
            }
            return;
        }
        else {
            zoomBridgeImg = null;
        }

        paintComponentInternal(g);
    }

    public void paintComponentInternal(Graphics g) {
        super.paintComponent(g);
        visibleWidthChangedCallback();

        // Maybe it would be better to show the marking always
        if (wholeWavePanel.getShouldIncludeInOperations()) {
            markPart(g, Color.red);
        }

        AudioPlayerPanel.ClipboardDrawView clipboard = wholeWavePanel.getClipboardDrawView();
        if (clipboard.isEqualToClipboardWavePanel(wholeWavePanel)) {
            Color color;
            if (clipboard.isCut()) {
                color = Color.black;
            } else {
                color = Color.gray;
            }

            markPartGeneralFull(g, clipboard.getClipboardMarkStartPixel(), clipboard.getClipboardMarkEndPixel(),
                                color, this.getHeight());

        }

        drawAudioWave(g);

        if (wholeWavePanel.getShouldIncludeInMixing()) {
            drawTimeLine(g);
        }
    }


    private void drawAudioWave(Graphics g) {
        int visibleWaveWidth = wholeWavePanel.getWaveVisibleWidth();
        ProgramTest.debugPrint("drawAudioWave:", visibleWaveWidth, wholeWavePanel.getWaveVisibleWidth(),
                               this.getHeight(), doubleWave.getFilenameWithoutExtension());
        currentDrawValues.drawSamples(g, visibleWaveWidth, this.getHeight(), 0);
    }


    private void drawTimeLine(Graphics g) {
        double x = wholeWavePanel.getTimeLineX();
        x -= wholeWavePanel.getCurrentHorizontalScroll();
        drawTimeLine(g, (int)(x), 0, this.getHeight());
    }

    private static void drawTimeLine(Graphics g, int x, int startY, int endY) {
        g.setColor(Color.GREEN);
        g.drawLine(x, startY, x, endY);
    }


    public void markPart(Graphics g, Color color) {
        if(wholeWavePanel.getShouldMarkPart()) {
            int markStartX = wholeWavePanel.getMarkStartXPixel();
            int markEndX = wholeWavePanel.getMarkEndXPixel();
            markPartGeneralFull(g, markStartX, markEndX, color, this.getHeight());
// TODO: DEBUG
//            ProgramTest.debugPrint(markStartX, markEndX, markStartXShifted, markEndXShifted, this.getVisibleRect());
//            ProgramTest.debugPrint("MARK_PART:", markStartX, markEndX, this.getVisibleRect());
// TODO: DEBUG
        }
    }


    private void markPartGeneralOnlyEndings(Graphics g, int markStartX, int markEndX, Color color, int height) {
        int horizontalScroll = wholeWavePanel.getCurrentHorizontalScroll();
        int markStartXShifted = markStartX - horizontalScroll;
        int markEndXShifted = markEndX - horizontalScroll;
        g.setColor(color);
        int dif;
        if(markStartX > markEndX) {
            dif = markStartX - markEndX;
            dif = Math.min(dif, 15);
            g.fillRect(markEndXShifted,0, dif, height);
            g.fillRect(markStartXShifted - dif, 0, dif, height);
        }
        else {
            dif = markEndX - markStartX;
            dif = Math.min(dif, 15);
            g.fillRect(markStartXShifted, 0, dif, height);
            g.fillRect(markEndXShifted - dif, 0, dif, height);
        }
    }


    private void markPartGeneralFull(Graphics g, int markStartX, int markEndX, Color color, int height) {
        int horizontalScroll = wholeWavePanel.getCurrentHorizontalScroll();
        int markStartXShifted = markStartX - horizontalScroll;
        int markEndXShifted = markEndX - horizontalScroll;
        g.setColor(color);

        if(markStartX > markEndX) {
            g.fillRect(markEndXShifted, 0,  markStartX - markEndX, height);
        }
        else {
            g.fillRect(markStartXShifted, 0,  markEndX - markStartX, height);
        }


// TODO: DEBUG
        ProgramTest.debugPrint("MARK_PART_GENERAL:", horizontalScroll, markStartX, markEndX,
                markStartXShifted, markEndXShifted, this.getVisibleRect());
// TODO: DEBUG
    }





    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////// Taking care of drawing
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean performCaching = false;

    private ZoomVariablesOneWave zoomVariables;


    // TODO: Vymazat WAVE_DRAW_NAME_SUFFIX
    //final String WAVE_DRAW_NAME_SUFFIX = "_Draw.draww";
    public static final String WAVE_DRAW_NAME_SUFFIX = "_Draw";
    public static final String WAVE_DRAW_EXTENSION = ".draww";
    public static final String WAVE_DRAW_VALS_SEPARATOR = ";";
    public static final char SUFFIX_END_CHAR = '|';


    // Taken from https://stackoverflow.com/questions/7240519/delete-files-with-same-prefix-string-using-java
    // Only needed to find regex
    /**
     * If all old cache files for wave draw values with same name were correctly removed
     * @return
     */
    private boolean removeOldCaches() {
//        try {
//            Thread.sleep(10000);
//        }
//        catch(Exception e) {
//
//        }

        String cacheFilename = getCacheFilename(0);
        cacheFilename = new File(cacheFilename).getName();

        if(!cacheFilename.equals(doubleWave.getFullPath())) {
            String prefix = cacheFilename.substring(0, cacheFilename.length() - WAVE_DRAW_EXTENSION.length() - 1);   // - 1 because I need to remove the 0
            String prefixWithValidBackslashes = prefix.replace("\\", "\\\\");
            String regexWithInvalidDots = prefixWithValidBackslashes + "\\d+" + WAVE_DRAW_EXTENSION;
            String regex = regexWithInvalidDots.replace(".", "\\.");

            // + "_" + wantedZoom + WAVE_DRAW_EXTENSION;

            final File folder = new File(DoubleWave.addDirectoryToFilename(""));

            final File[] files = folder.listFiles( new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String name) {
                    return name.matches(regex);
                }
            } );
// TODO: DEBUG
//            ProgramTest.debugPrint("Thread:", Thread.currentThread());
// TODO: DEBUG
//            SwingUtilities.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    for ( final File file : files ) {
//                        try {
//                            Files.delete(file.toPath());
//                        }
//                        catch(Exception e) {
//                            MyLogger.log(e.toString());
//                            System.exit(11154);
//                        }
////                if ( !file.delete() ) {
////                    System.err.println( "Can't remove " + file.getAbsolutePath() );
////                    return false;
////                }
//                    }
//                }
//            });

            for (final File file : files) {
                try {
                    Files.delete(file.toPath());
                }
                catch(Exception e) {
                    MyLogger.logException(e);
                    System.exit(11154);
                    return false;
                }
//                if (!file.delete()) {
//                    MyLogger.log("Can't remove " + file.getAbsolutePath());
//                    System.exit(11154);
//                    return false;
//                }
            }
        }

        return true;
    }

    /**
     * Creates files with data used for drawing of wave with different zooms. For each zoom 1 file.
     * Read description of parameter filename
     * @return Returns true if file was created successfully
     */
    public boolean cacheToHDD() {
        if(!removeOldCaches()) {
            System.exit(4568);
            return false;
        }

        int maxWidth = (int) (defaultWaveWidthInPixels * Math.pow(WavePanel.ZOOM_VALUE, zoomVariables.maxCacheZoom));
        double samplesPerPixel = calculateInputValsPerOutputValsPure(getSongLen(), maxWidth);
        // * 2 because it is min/max
        double[] values = new double[2 * maxWidth];     // TODO: NE TOHLE MUSIM DELAT PO CASTECH, CO KDYZ TO JE PROSTE MOC VELKY
        double[] song = doubleWave.getSong();       // TODO: Full Song loaded (use variable from doubleWave)
        int currZoom = zoomVariables.maxCacheZoom;

        String cacheFilename = getCacheFilename(currZoom);

        WavePanel.findExtremesInValues(song, values, 0, 0, song.length, maxWidth);
        //findExtremesInValues(song, values, 0, 0, song.length, samplesPerPixel);
        int[] prefix = new int[] { values.length };
        DoubleWave.storeDoubleArray(values, 0, values.length, cacheFilename, prefix);
        currZoom--;

//            w = maxWidth;
//            for (int i = maxZoom - 1; i >= 0; i--, samplesPerPixel /= 2, currZoom--) {
//                int tmp = findExtremesInValues(values, 0, w, 2);
//                writeDoubles(dataOutput, values, w, 2, suffix, WAVE_DRAW_VALS_SEPARATOR, currZoom);
//
//                w = tmp;
//            }
        return cacheToHDD(values.length, currZoom, values);
    }

    // This one takes the values of the next zoom and creates new zoom of it
    public boolean cacheToHDD(int w, int currZoom, double[] values) {
        for (; currZoom >= 0; currZoom--) {
            // TODO: CHYBA - tady nema byt w protoze tam mam predat delku toho values pole - resp. pocet platnych indexu a ten neodpovida w
            // ten odpovida te puvodni velikosti pole, kterou postupne delim 2ma - pozn. kdyz beru min a max tak v ty prvni iteraci
            // je to pole stejne velky vlastne az v tech dalsich se zmensuje na polovinu
//            w = findExtremesInValues(values, 0, 0, w, samplesPerPixel);            // TODO: startIndex
            w = findExtremesInExtremes(values, 0, 0, w);
            String cacheFilename = getCacheFilename(currZoom);
            int[] prefix = new int[] { values.length };
            if(DoubleWave.storeDoubleArray(values, 0, w, cacheFilename, prefix) < 0) {
                return false;
            }
        }

        return true;
    }

    private static int PREFIX_BEFORE_CACHED_DATA = 1;       // It is just the length of the array

    // Test method of the caching
    public static double[][] cacheToHDDTest(int maxCacheZoom, double samplesPerPixel, double[] input,
                                            double[] output) {
        double[][] result = new double[maxCacheZoom + 1][];     // +1 Because I also need the zoom at 0
        int w = findExtremesInValues(input, output, 0, 0, input.length, samplesPerPixel);
        //int w = findExtremesInValues(input, output,0, input.length, output.length);
        if(w != output.length) {
            ProgramTest.debugPrint("cacheToHDDTest", maxCacheZoom, w, input.length, output.length,
                samplesPerPixel, output.length * samplesPerPixel);
            System.exit(-11111);
        }
        result[0] = Arrays.copyOf(output, w);

        for (int i = maxCacheZoom - 1, twoDimArrIndex = 1; i >= 0; i--, samplesPerPixel /= 2, twoDimArrIndex++) {
//            w /= 2;
            //w = findExtremesInValues(output, 0, w, (double)4);
            w = findExtremesInExtremes(output, 0,0, w);
            result[twoDimArrIndex] = Arrays.copyOf(output, w);
        }
        return result;
    }




    /**
     *
     * @param values
     * @param startIndex
     * @param inputLen
     * @param outputLen is the number of the wanted pixels in output, in classic case it is == width,
     *                  so the values array has to be at least twice the outputLen since it needs to contain min and max for each pixel
     * @return
     */
    public static int findExtremesInValues(double[] values, int startIndex, int outputStartIndex, int inputLen, int outputLen) {
        double inputValsPerOutputVals = calculateInputValsPerOutputValsPure(inputLen, outputLen);
        return findExtremesInValues(values, values, startIndex, outputStartIndex, inputLen, inputValsPerOutputVals, outputLen);
    }

    public static int findExtremesInValues(double[] values, int startIndex, int outputStartIndex, int inputLen, double inputValsPerOutputVals) {
        int outputLen = calculateOutputLen(inputLen, inputValsPerOutputVals);
        return findExtremesInValues(values, values, startIndex, outputStartIndex, inputLen, inputValsPerOutputVals, outputLen);
    }

    public static int findExtremesInValues(double[] values, double[] extremes, int startIndex, int outputStartIndex,
                                           int inputLen, double inputValsPerOutputVals) {
        int outputLen = calculateOutputLen(inputLen, inputValsPerOutputVals);
        return findExtremesInValues(values, extremes, startIndex, outputStartIndex, inputLen, inputValsPerOutputVals, outputLen);
    }


    /**
     *
     * @param values
     * @param extremes
     * @param startIndex
     * @param inputLen
     * @param outputLen is the number of the wanted pixels in output, in classic case it is == width,
     *                  so the extremes array has to be at least twice the outputLen since it needs to contain min and max for each pixel
     * @return
     */
    public static int findExtremesInValues(double[] values, double[] extremes, int startIndex,
                                           int outputStartIndex, int inputLen, int outputLen) {
        double inputValsPerOutputVals = calculateInputValsPerOutputValsPure(inputLen, outputLen);
        return findExtremesInValues(values, extremes, startIndex, outputStartIndex, inputLen, inputValsPerOutputVals, outputLen);
    }


    /**
     * Finds extremes in given values. on extremes[2*i] = min, extremes[2*i + 1] = max
     * @param values are the values to take extremes from.
     * @param extremes is the array which will contain the extremes
     * @param startIndex is the start index in the values array to take extremes from
     * @param inputLen is the number of valid indexes in the values array.
     * @param inputValsPerOutputVals is the number of input values per two output values (min and max)
     * @param outputLen is the number of the wanted pixels in output, in classic case it is == width,
     *                  so the extremes array has to be at least twice the outputLen since it needs to contain min and max for each pixel
     * @return Returns the first non-filled output index.
     */
    public static int findExtremesInValues(double[] values, double[] extremes, int startIndex, int outputStartIndex,
                                           int inputLen, double inputValsPerOutputVals, int outputLen) {
        // In case if outputLen == w this is the same as samples per pixel
        final int inputValsPerOutputValsInt = (int)inputValsPerOutputVals;
        final int power = Utilities.testIfNumberIsPowerOfN(inputValsPerOutputVals, 2);

        double modulo;
        double currentInputValsPerOutputVals = inputValsPerOutputVals;
        if(inputValsPerOutputValsInt == 0) {
            modulo = inputValsPerOutputVals;
        }
        else {
            modulo = inputValsPerOutputVals % inputValsPerOutputValsInt;
        }

        boolean isFirstDebug = true;

        double min = Double.MAX_VALUE;
        double max = Double.NEGATIVE_INFINITY;
        int outInd = outputStartIndex;
        final int endIndex = startIndex + inputLen;
        for (int i = startIndex, imod = 0; i < endIndex; i++) {
            if(values[i] != 0 && isFirstDebug && modulo != 0) {
                int moduloCountNeededFor1 = 0;
                for(double sum = 0; sum < 1; moduloCountNeededFor1++, sum += modulo);
                ProgramTest.debugPrint("First non-zero in extreme method", i, values[i], i / inputValsPerOutputVals,
                    moduloCountNeededFor1, inputValsPerOutputVals);

                isFirstDebug = false;
            }
            if (power == -1 || power == -2) {       // the number is final, so compiler should optimize the branching out
                if (imod >= (int)currentInputValsPerOutputVals) {
                    imod = 0;
                    extremes[outInd] = min;
                    min = Double.MAX_VALUE;
                    outInd++;

                    extremes[outInd] = max;
                    max = Double.NEGATIVE_INFINITY;
                    outInd++;


                    if(currentInputValsPerOutputVals >= (inputValsPerOutputValsInt + 1)) {
                        currentInputValsPerOutputVals--;
                    }
                    currentInputValsPerOutputVals += modulo;

// TODO: DEBUG
//                    ProgramTest.debugPrint("setting min, max", outInd - 1, outInd - 2, extremes[outInd - 1], extremes[outInd - 2]);
//                    ProgramTest.debugPrint("EXTREMES1:", i, currentInputValsPerOutputVals, modulo, outInd, extremes.length, outputLen, inputLen);
// TODO: DEBUG
                }

                imod++;
            }
            else {      // It is integer and power of 2
                if(i != startIndex && i % ((int)currentInputValsPerOutputVals) == 0) {
                    extremes[outInd] = min;
                    min = Double.MAX_VALUE;
                    outInd++;

                    extremes[outInd] = max;
                    max = Double.NEGATIVE_INFINITY;
                    outInd++;

// TODO: DEBUG
//                    ProgramTest.debugPrint("EXTREMES2:", i, currentInputValsPerOutputVals, modulo, outInd, extremes.length, outputLen, inputLen);
// TODO: DEBUG
                }
            }

            min = Double.min(min, values[i]);
            max = Double.max(max, values[i]);
        }
        if(outInd < outputStartIndex + 2*outputLen) {
            extremes[outInd] = min;
            outInd++;
            extremes[outInd] = max;
            outInd++;
        }

// TODO: DEBUG
//        if(outInd - outputStartIndex != (int)(2*inputLen / inputValsPerOutputVals)) {
//            System.exit(696969);
//            int a = 5;
//        }
//        System.out.println(inputValsPerOutputVals * (extremes.length / 2) + "\t" + values.length + "\t" +
//            extremes.length + "\t" + calculateInputValsPerOutputValsPure(values.length, 1 + (extremes.length / 2)) + "\t" +
//            inputValsPerOutputVals);
//        System.out.println((inputValsPerOutputVals * 19636) / 2);
//        System.out.println((inputValsPerOutputVals * 19636) / 2 < values.length);
// TODO: DEBUG
// TODO: DEBUG
//        if(outInd != extremes.length) {
//            ProgramTest.debugPrint(outInd, extremes.length);
//            System.exit(123487);
//        }
// TODO: DEBUG
        return outInd;
    }


    /**
     * @param startIndex is the start index in the input array.
     * @param inputLen is the length of the input array
     */
    public static int findExtremesInExtremes(double[] inputExtremes, int startIndex, int outputStartIndex, int inputLen) {
        return findExtremesInExtremes(inputExtremes, inputExtremes, startIndex, outputStartIndex, inputLen);
    }


    // Micro optim. I can make either by if or by using mod (mod is better if inputValsPerOutputVals == 2^n)
    // TODO; For further testing check Testing_Branching_Optimization
    /**
     * Finds extremes in given extremes, where on index [2*i] = min, [2*i + 1] = max for input and output array.
     * The reason why this has separate implementation is because in the findExtremesInValues method we take
     * maximums for both minimums and maximums, and same for minimums, so this method saves half the time
     * @param inputExtremes are the extremes to take extremes from.
     * @param outputExtremes is the array which will contain the output extremes
     * @param startIndex is the start index in the inputExtremes array to take extremes from
     * @param inputLen is the number of valid indexes in the values array.
     * @return Returns the first non-filled output index.
     */
    public static int findExtremesInExtremes(double[] inputExtremes, double[] outputExtremes, int startIndex,
                                             int outputStartIndex, int inputLen) {
        // In case if outputLen == w this is the same as samples per pixel
        final int inputValsPerOutputVals = 2;
        int outputLen = inputLen / inputValsPerOutputVals;

        double min = Double.MAX_VALUE;
        double max = Double.NEGATIVE_INFINITY;
        int outInd = outputStartIndex;
        final int endIndex = startIndex + inputLen;
        for (int i = startIndex, halfI = 0, iHalfMod = 0; i < endIndex;) {
            // Since it is power of 2 this is the effective variant
            if (halfI != 0 && halfI % inputValsPerOutputVals == 0) {
                outputExtremes[outInd] = min;
                min = Double.MAX_VALUE;
                outInd++;

                outputExtremes[outInd] = max;
                max = Double.NEGATIVE_INFINITY;
                outInd++;

// TODO: DEBUG
//                    ProgramTest.debugPrint("EXTREMES2:", i, currentInputValsPerOutputVals, modulo, outInd, extremes.length, outputLen, inputLen);
// TODO: DEBUG
            }

            min = Double.min(min, inputExtremes[i]);
            i++;
            max = Double.max(max, inputExtremes[i]);
            i++;
            halfI++;
        }
        if(outInd < outputStartIndex + outputLen) {
            outputExtremes[outInd] = min;
            outInd++;
            outputExtremes[outInd] = max;
            outInd++;
        }


// TODO: DEBUG - HNED
        if(outInd != outputStartIndex + outputLen) {
            System.exit(12456);
        }
// TODO: DEBUG - HNED
        return outInd;
    }


    public static int findAveragesInValues(double[] values, double[] averages, int startIndex, int outputStartIndex,
                                           int inputLen, int outputLen) {
        double inputValsPerOutputVals = calculateInputValsPerOutputValsPure(inputLen, outputLen);
        return findAveragesInValues(values, averages, startIndex, outputStartIndex, inputLen, inputValsPerOutputVals, outputLen);
    }


    /**
     * Finds averages in given values. and puts the to averages array
     * @param values are the values to take averages from.
     * @param averages is the array which will contain the averages
     * @param startIndex is the start index in the values array to take averages from
     * @param inputLen is the number of valid indexes in the values array.
     * @param inputValsPerOutputVals is the number of input values per one output value
     * @param outputLen is the number of the wanted pixels in output, in classic case it is == width,
     *                  so the averages array has to be at least the outputLen long
     * @return Returns the first non-filled output index.
     */
    private static int findAveragesInValues(double[] values, double[] averages, int startIndex, int outputStartIndex,
                                            int inputLen, double inputValsPerOutputVals, int outputLen) {
        // In case if outputLen == w this is the same as samples per pixel
        final int inputValsPerOutputValsInt = (int)inputValsPerOutputVals;
        final int power = Utilities.testIfNumberIsPowerOfN(inputValsPerOutputVals, 2);

        double modulo;
        double currentInputValsPerOutputVals = inputValsPerOutputVals;
        if(inputValsPerOutputValsInt == 0) {
            modulo = inputValsPerOutputVals;
        }
        else {
            modulo = inputValsPerOutputVals % inputValsPerOutputValsInt;
        }

        // TODO: DEBUG
        boolean isFirstDebug = true;
        // TODO: DEBUG

        double avg = 0;
        int outInd = outputStartIndex;
        final int endIndex = startIndex + inputLen;
        for (int i = startIndex, imod = 0; i < endIndex; i++) {
            // TODO: DEBUG
            if(values[i] != 0 && isFirstDebug) {
                int moduloCountNeededFor1 = 0;
                for(double sum = 0; sum < 1; moduloCountNeededFor1++, sum += modulo);
                ProgramTest.debugPrint("First non-zero in extreme method", i, values[i], i / inputValsPerOutputVals,
                    moduloCountNeededFor1, inputValsPerOutputVals);

                isFirstDebug = false;
            }
            // TODO: DEBUG

            if (power == -1 || power == -2) {       // the number is final, so compiler should optimize the branching out
                if (imod >= (int)currentInputValsPerOutputVals) {
                    imod = 0;
                    averages[outInd] = calculateAvg(avg, (int)currentInputValsPerOutputVals);
                    outInd++;
                    avg = 0;


                    if(currentInputValsPerOutputVals >= (inputValsPerOutputValsInt + 1)) {
                        currentInputValsPerOutputVals--;
                    }
                    currentInputValsPerOutputVals += modulo;
// TODO: DEBUG
//                    ProgramTest.debugPrint("EXTREMES1:", i, currentInputValsPerOutputVals, modulo, outInd, extremes.length, outputLen, inputLen);
// TODO: DEBUG
                }

                imod++;
            }
            else {      // It is integer and power of 2
                if(i != startIndex && i % ((int)currentInputValsPerOutputVals) == 0) {
                    averages[outInd] = calculateAvg(avg, (int)currentInputValsPerOutputVals);
                    outInd++;
                    avg = 0;
// TODO: DEBUG
//                    ProgramTest.debugPrint("EXTREMES2:", i, currentInputValsPerOutputVals, modulo, outInd, extremes.length, outputLen, inputLen);
// TODO: DEBUG
                }
            }

            avg += values[i];
        }
        if(outInd < outputStartIndex + outputLen) {
            averages[outInd] = calculateAvg(avg, (int)currentInputValsPerOutputVals);
            outInd++;
        }


        return outInd;
    }

    public static double calculateAvg(double number, int count) {
        return number / count;
    }



    public static int calculateOutputLen(int inputLen, double inputValsPerOutputVals) {
        int outputLen = (int)(inputLen / inputValsPerOutputVals);
        return outputLen;
    }

    public static double calculateInputValsPerOutputValsPure(int inputLen, int outputLen) {
        double samplesPerPixel = inputLen / (double)outputLen;
        return samplesPerPixel;
    }


    /**
     *
     * @param filename is the whole name of the audio (without extension)
     * @param wantedZoom
     * @return
     */
    public static String getCacheFilename(String filename, int wantedZoom) {
        String cachedFileName = filename + WAVE_DRAW_NAME_SUFFIX + "_" + wantedZoom + WAVE_DRAW_EXTENSION;
        cachedFileName = DoubleWave.addDirectoryToFilename(cachedFileName);
        return cachedFileName;
    }


    public String getCacheFilename(int wantedZoom) {
        if(wantedZoom > zoomVariables.maxCacheZoom) {
            return doubleWave.getFullPath();
        }
        else {
            return WavePanel.getCacheFilename(doubleWave.getFilenameWithoutExtension(), wantedZoom);
        }
    }

    public String getCacheFilename() {
        int currZoom = zoomVariables.currentZoom;
        return getCacheFilename(currZoom);
    }


    public void saveZoomBridgeImg() {
        Rectangle visibleRect = this.getVisibleRect();
        // When there is no bridge img already, we aren't initializing and when the wave is visible (we don't need to scroll to find it),
        // then save bridge picture
        if(zoomBridgeImg == null && zoomVariables != null && visibleRect.width > 0 && visibleRect.height > 0) {
            // https://stackoverflow.com/questions/1349220/convert-jpanel-to-image ...
            // Just changed paint method to our internal paint method
            zoomBridgeImg = this.createImage(visibleRect.width, visibleRect.height);
            int w = this.getWidth();
            int h = this.getHeight();
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bi.createGraphics();
            this.paintComponentInternal(g);
            g.dispose();
            zoomBridgeImg = bi;
        }
    }


    public static int calculateWidth(int zoom, int defaultWaveWidth) {
        return (int)(Math.pow(ZOOM_VALUE, zoom) * defaultWaveWidth);
    }

    public void updateZoom(int newZoom, int scrollBeforeZoom, boolean shouldZoomToMid, boolean shouldZoomToEnd) {
        visibleWidthChangedCallback();
        int oldZoom = zoomVariables.currentZoom;
        zoomVariables.currentZoom = newZoom;

        int newWidth = calculateWidth(newZoom, defaultWaveWidthInPixels);
        System.out.println("New width:\t" + newWidth);
        int oldWidth = waveWidth;
        int newVisibleWidth = wholeWavePanel.getWaveVisibleWidth();

        ProgramTest.debugPrint("Updating zoom before", waveWidth, getPreferredSize().width, getVisibleRect().width,
            wholeWavePanel.getPreferredSize().width, wholeWavePanel.getSize());
//            this.setPreferredSize(new Dimension(this.getPreferredSize().width, this.getPreferredSize().height));
        waveWidth = newWidth;


        ProgramTest.debugPrint("Updating zoom after", waveWidth, getPreferredSize().width, getVisibleRect().width,
            wholeWavePanel.getPreferredSize().width, wholeWavePanel.getSize());

        // TODO: RIKA INDEX DOVNITR TOHO BUFFERU, KDE SE MA ZACIT - TO BY ASI MEL BYT TEN MIDDLE INDEX
        int startIndexInValues = getLeftPixelAfterZoom(oldWidth, newWidth, newVisibleWidth, scrollBeforeZoom, shouldZoomToMid,
            shouldZoomToEnd);
        ProgramTest.debugPrint("currScroll before", currScroll);
        currScroll = startIndexInValues;
        ProgramTest.debugPrint("currScroll after", currScroll);
        // TODO: INDIV
        // TODO: NEW INDIV
        // TODO: NEW INDIV
//        if(currentDrawValues == drawValuesIndividual) {
//            startIndexInValues = (int)((currentDrawValues.getStartIndex() + newVisibleWidth) * Math.pow(ZOOM_VALUE, zoomDif));
//        }
//        else if(currentDrawValues == drawValuesAggregated) {
//            startIndexInValues = (int)((currentDrawValues.getStartIndex() + 2 * newVisibleWidth) * Math.pow(ZOOM_VALUE, zoomDif));
//        }
        // TODO: INDIV
        ProgramTest.debugPrint("ZOOMING BEFORE", currentDrawValues.getStartIndex(), startIndexInValues,
            oldWidth, newWidth, newVisibleWidth);
        ProgramTest.debugPrint(oldZoom, newZoom);

//        currentDrawValues = null;          // TODO: !!! TED

        setDrawValuesInZoom(startIndexInValues, newVisibleWidth, newWidth);
        ProgramTest.debugPrint("ZOOMING AFTER", startIndexInValues, startIndexInValues + 2 * newVisibleWidth);
    }


    private void setDrawValuesInZoom(int leftPixel, int newVisibleWidth, int newWidth) {
        int valueCount = getSongLen();
        if(zoomVariables.currentZoom > zoomVariables.maxCacheZoom || zoomVariables.maxCacheZoom == 0) {
            //currScroll = convertScrollValueToIndividualIndexInAudio(currScroll);
            mainWaveClass = drawValuesSupplierIndividual;
            if(drawValuesIndividual != currentDrawValues) {
                drawValuesAggregated = null;
                drawValuesIndividual = new WaveDrawValuesIndividual(leftPixel, newVisibleWidth,
                        newWidth, mainWaveClass.getCurrentStartIndexInAudio(), valueCount, WINDOW_COUNT_TO_THE_RIGHT, mainWaveClass);
                currentDrawValues = drawValuesIndividual;
            }
            else {
                currentDrawValues.performZoom(mainWaveClass.getCurrentStartIndexInAudio(), newWidth, valueCount);
            }
        }
        else {
            mainWaveClass = drawValuesSupplierAggregated;
            if(drawValuesAggregated != currentDrawValues) {
                drawValuesIndividual = null;
                drawValuesAggregated = new WaveDrawValuesAggregated(newVisibleWidth,
                        newWidth, mainWaveClass.getCurrentStartIndexInAudio(), valueCount, WINDOW_COUNT_TO_THE_RIGHT, mainWaveClass);
                currentDrawValues = drawValuesAggregated;
            }
            else {
                ProgramTest.debugPrint("STARTIND", leftPixel, waveWidth);
                currentDrawValues.performZoom(mainWaveClass.getCurrentStartIndexInAudio(), newWidth, valueCount);
            }
        }
    }


    private DrawValuesSupplierIFace mainWaveClass = null;
    private int currScroll = 0;

    public int convertScrollValueToIndividualIndexInAudio(double scrollValue) {
        double result = scrollValue * WavePanel.calculateInputValsPerOutputValsPure(getSongLen(), waveWidth);
        ProgramTest.debugPrint("convertScrollValueToIndividualIndexInAudio start", scrollValue, result, (int)(result + 1));
        if((int)result == 0) {
            if(scrollValue == 0) {
                return 0;
            }
            else {
                return 1;
            }
        }
// TODO: DEBUG
//        if((int)(result + 1) < getSongLen()) {
//            double[] song = doubleWave.getSong();
//            ProgramTest.debugPrint("convertScrollValueToIndividualIndexInAudio", result, result + 1,
//                song[(int) result - 1], song[(int) result], song[(int) result + 1], song[(int) Math.round(result)]);
//        }
// TODO: DEBUG

//        return (int)(result + 1);      // +1 because I want the index after the scroll not before it

//        return (int)Math.round(result + 1);

//        if(scrollValue == mainWaveClass.getMaxScroll()) {
//            return (int)result;
//        }
//        else {
//            return (int)(result + 1);
//        }


        double tolerance = 0.00001;
        if(result > ((int)result + 1) - tolerance) {    // Basically take into account precision error for doubles, I think this tolerance should be more than ok.
            return (int)result + 2;
        }
        return (int)result + 1;
    }



    public static double getRatio(int oldWidth, int newWidth, int visibleWaveWidth) {
        boolean isZooming = newWidth > oldWidth;
        double ratio;
        int oldMaxScroll = oldWidth - visibleWaveWidth;
        if (isZooming) {
            ratio = WavePanel.ZOOM_VALUE;
        } else {
            ratio = 1 / (double) WavePanel.ZOOM_VALUE;
        }

        ProgramTest.debugPrint("Ratio:", ratio, (newWidth - visibleWaveWidth), oldMaxScroll);
        return ratio;
    }

    public static int getLeftCornerAfterZoom(int oldWidth, int newWidth, int visibleWaveWidth, int scrollBeforeZoom) {
        double ratio = getRatio(oldWidth, newWidth, visibleWaveWidth);
        return (int)(scrollBeforeZoom * ratio);
    }

    public static int getLeftPixelAfterZoom(int oldWidth, int newWidth, int newVisibleWidth, int scrollBeforeZoom, boolean shouldZoomToMid,
                                            boolean shouldZoomToEnd) {
        ProgramTest.debugPrint("getLeftPixelAfterZoom", oldWidth, newWidth, newVisibleWidth, scrollBeforeZoom, shouldZoomToMid, shouldZoomToEnd);
        int scrollAfterZoom;
        if(shouldZoomToMid) {
            int oldScroll = scrollBeforeZoom;
            int oldMid = oldScroll + newVisibleWidth / 2;       // Find old mid
            boolean isZoom = newWidth > oldWidth;
            int oldMaxScroll = oldWidth - newVisibleWidth;
            if(!isZoom && oldMid >= oldMaxScroll) {
                scrollAfterZoom = newWidth - newVisibleWidth;        // Just point the scroll to end
            }
            else {
                scrollAfterZoom = getLeftCornerAfterZoom(oldWidth, newWidth, newVisibleWidth, oldMid);
                scrollAfterZoom -= newVisibleWidth / 2;                      // Push it to left so it is pointing to mid
//                if(scrollAfterZoom < 0) {
//                    scrollAfterZoom = 0;
//                }
            }
        }
        else if(shouldZoomToEnd) {
            scrollAfterZoom = newWidth - newVisibleWidth;        // Just point the scroll to end
        }
        else {
            scrollAfterZoom = getLeftCornerAfterZoom(oldWidth, newWidth, newVisibleWidth, scrollBeforeZoom);
        }

        ProgramTest.debugPrint("getLeftPixelAfterZoom", scrollAfterZoom);
        if(scrollAfterZoom < 0) {
            scrollAfterZoom = 0;
        }
        return scrollAfterZoom;
    }


    /**
     * Called when scrolling
     * @param oldLeftPixel is the old leftest pixel
     * @param newLeftPixel is the new leftest pixel
     */
    public void updateWaveDrawValues(int oldLeftPixel, int newLeftPixel) {
        visibleWidthChangedCallback();

        int pixelChange = newLeftPixel - oldLeftPixel;
        currScroll = newLeftPixel;

        ProgramTest.debugPrint("updateWaveDrawValues",
            getVisibleRect().width, getVisibleRect().height, waveWidth,
            oldLeftPixel, newLeftPixel, pixelChange, doubleWave.getFilenameWithoutExtension());
        currentDrawValues.shiftBuffer(pixelChange);
    }


    private void setMaxCacheZoom() {
        // The if is not needed I think, but just in case
        if(zoomVariables != null) {
            zoomVariables.maxCacheZoom = calculateMaxCacheZoom();
        }
    }
    private int calculateMaxCacheZoom() {
        return calculateMaxCacheZoom(getSongLen(), defaultWaveWidthInPixels);
    }
    public static int calculateMaxCacheZoom(int length, int defaultWaveWidth) {
        int maxCacheZoom = 1;
        int zoomedWidth = defaultWaveWidth * WavePanel.ZOOM_VALUE;
        while(zoomedWidth < length) {
            maxCacheZoom++;
            zoomedWidth *= WavePanel.ZOOM_VALUE;
        }

        maxCacheZoom--;
        return maxCacheZoom;
    }


    private Dimension preferredSize = new Dimension(super.getPreferredSize());

    @Override
    public Dimension getPreferredSize() {
        return preferredSize;
    }
// TODO: IMPORTANT
//ten problem je ze kdyz to roztahnu tak ze se zobrazi vertical scrollbar tak wavy dostanou 50 preferred height mmisto 48
// TODO: IMPORTANT

    public void setPreferredHeight(int h) {
        preferredSize.height = h;
    }

    @Override
    public void setPreferredSize(Dimension newSize) {
        setPreferredSizeWithoutUpdatingHorizontalScrollSize(newSize);
        wholeWavePanel.updateHorizontalScrollSize();
    }


    private void setPreferredSizeWithoutUpdatingHorizontalScrollSize(Dimension newSize) {
        preferredSize = newSize;
        wholeWavePanel.updatePreferredSize();
    }


    public int getMaxPossibleZoom() {
        int maxPossibleZoom = calculateMaxZoomBeforeOverflow(defaultWaveWidthInPixels, ZOOM_VALUE);
        return maxPossibleZoom;
    }

    // Probably can be calculated faster by using powers but currently can't think of it
    public static int calculateMaxZoomBeforeOverflow(int defaultWidth, int zoom) {
        int maxZoom = 0;
        int lastMax = Integer.MAX_VALUE / zoom;
        int width = defaultWidth;
        while(width <= lastMax) {
            width *= zoom;
            maxZoom++;
        }
        return maxZoom;
    }






    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////// Audio player operations on waves - ModOperations
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Pastes by inserting - doesn't delete any old value.
     * @param arrToCopy
     * @param startCopyIndex
     * @param startPasteIndex
     * @param len
     * @param copyCount
     * @return Returns new length of wave
     * */
    public int paste(double[] arrToCopy, int startCopyIndex, int startPasteIndex, int len, int copyCount, boolean isCut) {
        int copyLen = len * copyCount;
        double[] oldSong = getDoubleWave().getSong();
        double[] newSong = new double[this.getSongLen() + copyLen];

        int outIndex = pasteStartAndThePaste(oldSong, arrToCopy, newSong, startCopyIndex, startPasteIndex, len, copyCount);
        // Copy the rest of the old array
        System.arraycopy(oldSong, startPasteIndex, newSong, outIndex, oldSong.length - startPasteIndex);

        if(isCut) {
            int endCopyIndex = startCopyIndex + len;
            if(oldSong == arrToCopy) {
                int endPasteIndex = startCopyIndex + copyLen;
                if (startPasteIndex >= startCopyIndex && startPasteIndex < endCopyIndex) {
                    int remainingLen = endCopyIndex - startPasteIndex;
                    endCopyIndex = startPasteIndex;
                    Utilities.setOneDimArrWithCheck(newSong, startCopyIndex, endCopyIndex, 0);

                    startCopyIndex = endPasteIndex;
                    endCopyIndex = endPasteIndex + remainingLen;
                    Utilities.setOneDimArrWithCheck(newSong, startCopyIndex, endCopyIndex, 0);
                }
                else if (startCopyIndex > startPasteIndex) {
                    startCopyIndex += copyLen;
                    endCopyIndex += copyLen;
                    Utilities.setOneDimArr(newSong, startCopyIndex, endCopyIndex, 0);
                }
                else {
                    Utilities.setOneDimArr(newSong, startCopyIndex, endCopyIndex, 0);
                }
            }
            else {
                Utilities.setOneDimArr(arrToCopy, startCopyIndex, endCopyIndex, 0);
            }
        }

        setNewDoubleWave(newSong);
        return newSong.length;
    }

    /**
     * Copies by deleting (overwriting) - Puts new values instead of the old values
     * @param arrToCopy
     * @param startCopyIndex
     * @param startPasteIndex
     * @param len
     * @param copyCount
     */
    public int pasteWithOverwriting(double[] arrToCopy, int startCopyIndex, int startPasteIndex,
                                  int len, int copyCount, boolean isCut) {
        int copyLen = len * copyCount;
        double[] oldSong = getDoubleWave().getSong();
        // Either I make the array larger when the last position where I will put copy is after the old array
        // Or I just delete (overwrite) some part inside the array so the length doesn't change.
        int newLen = Math.max(startPasteIndex + copyLen, oldSong.length);
        double[] newSong = new double[newLen];
        int outIndex = pasteStartAndThePaste(oldSong, arrToCopy, newSong, startCopyIndex, startPasteIndex, len, copyCount);
        // Still need to copy the end of the old array
        if(outIndex < oldSong.length) {
            // Copy the rest of the old array
            System.arraycopy(oldSong, outIndex, newSong, outIndex, oldSong.length - outIndex);
        }
        // Else the array was made larger, no need to copy anything


        if(isCut) {
            if(oldSong == arrToCopy) {
                int endCopyIndex = startCopyIndex + len;
                int endPasteIndex = startPasteIndex + copyLen;
                if(startPasteIndex > startCopyIndex && startPasteIndex < endCopyIndex) {
                    Utilities.setOneDimArrWithCheck(newSong, startCopyIndex, startPasteIndex, 0);
                }
                else if(endPasteIndex < startCopyIndex || startPasteIndex > endCopyIndex) {
                    Utilities.setOneDimArrWithCheck(newSong, startCopyIndex, endCopyIndex, 0);
                }
                else if(endPasteIndex > startCopyIndex && endPasteIndex < endCopyIndex) {
                    Utilities.setOneDimArrWithCheck(newSong, endPasteIndex, endCopyIndex, 0);
                }
                // Else the the values were already overwritten so no need to set anything to 0
            }
            else {
                int endCopyIndex = Math.min(arrToCopy.length, startCopyIndex + len);
                Utilities.setOneDimArr(arrToCopy, startCopyIndex, endCopyIndex, 0);
            }
        }

        setNewDoubleWave(newSong);
        return newSong.length;
    }




    public int moveWave(int oldStartIndex, int newStartIndex, int len) {
        double[] oldSong = getDoubleWave().getSong();
        return pasteWithOverwriting(oldSong, oldStartIndex, newStartIndex, len, 1, true);
    }

    /**
     * Internal copying method - copies the start of old array and the arrToCopy. Returns the first free index in the newSong (first free output index)
     * @param oldSong
     * @param arrToCopy
     * @param newSong
     * @param startCopyIndex
     * @param startPasteIndex
     * @param len
     * @param copyCount
     * @return
     */
    private int pasteStartAndThePaste(double[] oldSong, double[] arrToCopy, double[] newSong,
                                      int startCopyIndex, int startPasteIndex, int len, int copyCount) {
        // Copy the start of old array
        System.arraycopy(oldSong, 0, newSong, 0, startPasteIndex);

        // The copying itself
        int pasteIndex = startPasteIndex;
        for(int i = 0; i < copyCount; i++, pasteIndex += len) {
            System.arraycopy(arrToCopy, startCopyIndex, newSong, pasteIndex, len);
        }

        return pasteIndex;
    }


    public void remove(int startIndex, int endIndex) {
        double[] oldSong = getDoubleWave().getSong();
        double[] newSong = new double[doubleWave.getSongLength() - (endIndex - startIndex)];
        System.arraycopy(oldSong, 0, newSong, 0, startIndex);

        int outIndex = startIndex;
        int len = oldSong.length - endIndex;
        System.arraycopy(oldSong, endIndex, newSong, outIndex, len);

        doubleWave = new DoubleWave(newSong, doubleWave, false);
        setNewDoubleWave(newSong);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////// Audio player operations on waves
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////









    private WaveDrawValuesIndividual drawValuesIndividual;
    private WaveDrawValuesAggregated drawValuesAggregated;
    private WaveDrawValues currentDrawValues;
    private DrawValuesSupplierIndividual drawValuesSupplierIndividual;
    private DrawValuesSupplierAggregated drawValuesSupplierAggregated;









//        abstract class DrawValuesSupplier implements DrawValuesSupplierIFace {
//        @Override
//        public boolean getIsCached() {
//            return isCached;
//        }
//
//
//        @Override
//        public int getMaxScroll() {
//// TODO: DEBUG
////            ProgramTest.debugPrint("getMaxScroll", wholeWavePanel.getMaxHorizontalScroll(), getPreferredSize().width - wholeWavePanel.getWaveVisibleWidth());
////            if(wholeWavePanel.getMaxHorizontalScroll() != getPreferredSize().width - wholeWavePanel.getWaveVisibleWidth() && wholeWavePanel.getMaxHorizontalScroll() > 10000) {
////                System.exit(456789);
////            }
//// TODO: DEBUG
//            return getPreferredSize().width - wholeWavePanel.getWaveVisibleWidth();
//            //return getPreferredSize().width;
//        }
//
//        @Override
//        public int getCurrentScroll() {
//// TODO: DEBUG
////            if(wholeWavePanel.getCurrentHorizontalScroll() > wholeWavePanel.getMaxHorizontalScroll()) {
////                System.exit(4567890);
////            }
////            if(currScroll > getMaxScroll()) {
////                ProgramTest.debugPrint(currScroll, wholeWavePanel.getCurrentHorizontalScroll(),
////                    wholeWavePanel.getMaxHorizontalScroll(), getMaxScroll());
////                System.exit(45678900);
////            }
//// TODO: DEBUG
//// TODO: POSUVNY BUFFER
//            ProgramTest.debugPrint("curr scroll", currScroll, wholeWavePanel.getCurrentHorizontalScroll());
//            return currScroll;
//            //return wholeWavePanel.getCurrentHorizontalScroll();
////            return zoomValuesInfo.currentZoomValueIndex;
//// TODO: POSUVNY BUFFER
//        }
//
//        public int getCurrentStartIndexInAudio() {
//            return currScroll;
//        }
//
//
//
//        @Deprecated
//        public double convertFromIndexInValuesToPixel(int indexInValues) {
//            double pixelsPerIndex = calculateInputValsPerOutputValsPure(getPreferredSize().width, getSongLen());
//            return indexInValues * pixelsPerIndex;
//        }
//
//        @Override
//        public int getTotalWidth() {
//            return getPreferredSize().width;
//        }
//    }
//
//
//
//    class DrawValuesSupplierIndividual extends DrawValuesSupplier {
//        @Override
//        public void fillBufferWithValuesToDraw(double[] buffer, int bufferStartIndex, int bufferEndIndex, int startFillIndex) {
//
//            // TODO: IS_FIRST - s tim souvisi i ten currentDrawValues to tam vubec nemusi byt
//            if((doubleWave.getIsFullSongLoaded() && currentDrawValues != null) && currentDrawValues.IS_FIRST) {
//                double[] song = doubleWave.getSong();               // TODO: Full Song loaded (use variable from doubleWave)
//                for (int i = bufferStartIndex, fillInd = startFillIndex; i < bufferEndIndex; i++, fillInd++) {
//// TODO: DEBUG
//                    ProgramTest.debugPrint("Individual fill", i, fillInd, getSongLen(), bufferStartIndex, bufferEndIndex);
//// TODO: DEBUG
//                    buffer[i] = song[fillInd];
//                }
//            }
//            else {
//                ProgramTest.debugPrint("Cache");
//                int len = bufferEndIndex - bufferStartIndex;
//                fillBufferWithCachedValues(buffer, bufferStartIndex, bufferEndIndex, startFillIndex, len, len);
//            }
//        }
//
//        @Override
//        public int getAudioLen() {
//            return WavePanel.this.getSongLen();     // TODO: HNED - 3. radek v cache souboru, pripadne si to muzu vzit odjinud tu informaci
//        }
//
//        @Override
//        public int getCurrentStartIndexInAudio() {
//            return convertScrollValueToIndividualIndexInAudio(currScroll);
//        }
//
//        @Override
//        public int convertFromPixelToIndexInAudio(double pixel) {
//            int retVal = convertScrollValueToIndividualIndexInAudio(pixel);
//            return retVal;
//        }
//
//        public int fillBufferWithCachedValues(double[] buffer, int bufferStartIndex, int bufferEndIndex,
//                                              int startFillIndex, int inputLen, int outputLen) {
//            int outIndex = -1;
//
//            String cacheFilename = getCacheFilename();
//            try {
//                RandomAccessFile cacheFile = new RandomAccessFile(cacheFilename, "r");
//                FileChannel cache = cacheFile.getChannel();
//                fillBufferWithCachedValues(cache, buffer, bufferStartIndex, bufferEndIndex, startFillIndex);
//                DoubleWave.safeClose(cacheFile);
//            }
//            catch (FileNotFoundException e) {
//                for(int i = 0; i < buffer.length; i++) {
//                    buffer[i] = 0;
//                }
//                System.exit(-555);          // TODO: For now just end it, but it makes sense, since that means that I don't have the audio track so I can't work with this
//
//            }
//
//            return outIndex;
//        }
//
//        @Override
//        public int getPrefixLenInBytes() {
//            return DoubleWave.SAMPLES_POS * Integer.BYTES;
//        }
//
//        @Deprecated
//        public int convertToOutputIndex(double inputIndex) {
//            return (int)inputIndex;
//        }
//    }
//
//
//
//
//    class DrawValuesSupplierAggregated extends DrawValuesSupplier {
//        @Override
//        public void fillBufferWithValuesToDraw(double[] buffer, int bufferStartIndex, int bufferEndIndex, int startFillIndex) {
//            int outputLen = (bufferEndIndex - bufferStartIndex) / 2;        // /2 because there are min and max
//            double samplesPerPixel = calculateInputValsPerOutputValsPure(getSongLen(), getPreferredSize().width);
//            int inputLen = (int)(samplesPerPixel * outputLen);
//
//            // TODO: IS_FIRST - s tim souvisi i ten currentDrawValues to tam vubec nemusi byt
//            if((getIsCached() && currentDrawValues != null) && !currentDrawValues.IS_FIRST) {
//                ProgramTest.debugPrint("Cache");
//                startFillIndex = convertToOutputIndex(samplesPerPixel * startFillIndex);
//                fillBufferWithCachedValues(buffer, bufferStartIndex, bufferEndIndex, startFillIndex, inputLen, outputLen);
//            }
//            else {
//                double[] song = doubleWave.getSong();           // TODO: Full Song loaded (use variable from doubleWave)
//                startFillIndex = (int)(samplesPerPixel * startFillIndex);
//                int outIndex = WavePanel.findExtremesInValues(song, buffer, startFillIndex, bufferStartIndex, inputLen, outputLen);
//                if (outIndex != bufferEndIndex) {
//                    ProgramTest.debugPrint("output index is not right", outIndex, bufferEndIndex);
//                    //System.exit(1548);
//                }
//            }
//
//            // TODO: POSUVNY BUFFER
////            startFillIndex *= 2;
//            ProgramTest.debugPrint("Filling buffer", getCurrentScroll(), startFillIndex, startFillIndex - getCurrentScroll());
//// TODO: POSUVNY BUFFER
//
//            ProgramTest.debugPrint("FillBuffer", bufferStartIndex, bufferEndIndex, startFillIndex, inputLen,
//                startFillIndex + inputLen, getSongLen(), samplesPerPixel, outputLen, samplesPerPixel * outputLen);
//            ProgramTest.debugPrint("FillBuffer2", startFillIndex, inputLen, getPreferredSize().width);
//            ProgramTest.debugPrint("FillBuffer3", getPreferredSize().width, wholeWavePanel.getCurrentHorizontalScroll(),
//                currScroll, currScroll == wholeWavePanel.getCurrentHorizontalScroll());
//        }
//
//
//        public int fillBufferWithCachedValues(double[] buffer, int bufferStartIndex, int bufferEndIndex,
//                                              int startFillIndex, int inputLen, int outputLen) {
//            int outIndex = -1;
//
//            String cacheFilename = getCacheFilename();
//            try {
////                InputStream cacheFile = new FileInputStream(cacheFilename);
////                DataInput cache = new DataInputStream(cacheFile);
//                RandomAccessFile cacheFile = new RandomAccessFile(cacheFilename, "r");
//                FileChannel cache = cacheFile.getChannel();
//                fillBufferWithCachedValues(cache, buffer, bufferStartIndex, bufferEndIndex, startFillIndex);
//                DoubleWave.safeClose(cacheFile);
//            }
//            catch (Exception e) {
//                double[] song = doubleWave.getSong();           // TODO: Full Song loaded (use variable from doubleWave)
//                outIndex = WavePanel.findExtremesInValues(song, buffer, startFillIndex, bufferStartIndex, inputLen, outputLen);
//                if (outIndex != bufferEndIndex) {
//                    ProgramTest.debugPrint("output index is not right", outIndex, bufferEndIndex);
//                    //System.exit(1548);
//                }
//
//                isCached = false;       // Because when 1 file is missing probably all other are missing as well.
//            }
//
//            return outIndex;
//        }
//
//
//        @Override
//        public int getPrefixLenInBytes() {
//            return WavePanel.PREFIX_BEFORE_CACHED_DATA * Integer.BYTES;
//        }
//
//
//        @Override
//        public int getAudioLen() {
//            return WavePanel.this.getSongLen();     // TODO: HNED - 3. radek v cache souboru, pripadne si to muzu vzit odjinud tu informaci
//        }
//
//        @Override
//        public int convertFromPixelToIndexInAudio(double pixel) {
//            return (int)pixel;
//        }
//
//        @Deprecated
//        public int convertToOutputIndex(double inputIndex) {
//            double samplesPerPixel = calculateInputValsPerOutputValsPure(getSongLen(), getPreferredSize().width);
//            int outputIndex = (int)(2*inputIndex / samplesPerPixel);
//            return outputIndex;
//        }
//    }











// TODO: VYMAZAT = OBOJE TAKOVY TO MICHANI
//    abstract class DrawValuesSupplier implements DrawValuesSupplierIFace {
//        @Override
//        public boolean getIsCached() {
//            return isCached;
//        }
//
//
//        @Override
//        public int getMaxScroll() {
//// TODO: DEBUG
////            ProgramTest.debugPrint("getMaxScroll", wholeWavePanel.getMaxHorizontalScroll(), getPreferredSize().width - wholeWavePanel.getWaveVisibleWidth());
////            if(wholeWavePanel.getMaxHorizontalScroll() != getPreferredSize().width - wholeWavePanel.getWaveVisibleWidth() && wholeWavePanel.getMaxHorizontalScroll() > 10000) {
////                System.exit(456789);
////            }
//// TODO: DEBUG
//            return waveWidth - wholeWavePanel.getWaveVisibleWidth();
//            //return getPreferredSize().width;
//        }
//
//        @Override
//        public int getCurrentScroll() {
//// TODO: DEBUG
////            if(wholeWavePanel.getCurrentHorizontalScroll() > wholeWavePanel.getMaxHorizontalScroll()) {
////                System.exit(4567890);
////            }
////            if(currScroll > getMaxScroll()) {
////                ProgramTest.debugPrint(currScroll, wholeWavePanel.getCurrentHorizontalScroll(),
////                    wholeWavePanel.getMaxHorizontalScroll(), getMaxScroll());
////                System.exit(45678900);
////            }
//// TODO: DEBUG
//// TODO: POSUVNY BUFFER
//            ProgramTest.debugPrint("curr scroll", currScroll, wholeWavePanel.getCurrentHorizontalScroll());
//            return currScroll;
//            //return wholeWavePanel.getCurrentHorizontalScroll();
////            return zoomValuesInfo.currentZoomValueIndex;
//// TODO: POSUVNY BUFFER
//        }
//
//        public int getCurrentStartIndexInAudio() {
//            return currScroll;
//        }
//
//
//
//        @Deprecated
//        public double convertFromIndexInValuesToPixel(int indexInValues) {
//            double pixelsPerIndex = calculateInputValsPerOutputValsPure(waveWidth, getSongLen());
//            return indexInValues * pixelsPerIndex;
//        }
//
//        @Override
//        public int getTotalWidth() {
//            return waveWidth;
//        }
//    }
//
//
//
//    class DrawValuesSupplierIndividual extends DrawValuesSupplier {
//        @Override
//        public void fillBufferWithValuesToDraw(double[] buffer, int bufferStartIndex, int bufferEndIndex, int startFillIndex) {
//
//            // TODO: IS_FIRST - s tim souvisi i ten currentDrawValues to tam vubec nemusi byt
//            if((doubleWave.getIsFullSongLoaded() && currentDrawValues != null) && currentDrawValues.IS_FIRST) {
//                double[] song = doubleWave.getSong();               // TODO: Full Song loaded (use variable from doubleWave)
//                for (int i = bufferStartIndex, fillInd = startFillIndex; i < bufferEndIndex; i++, fillInd++) {
//// TODO: DEBUG
////                    ProgramTest.debugPrint("Individual fill", i, fillInd, getSongLen(), bufferStartIndex, bufferEndIndex);
//// TODO: DEBUG
//                    buffer[i] = song[fillInd];
//                }
//            }
//            else {
//                ProgramTest.debugPrint("Cache");
//                int len = bufferEndIndex - bufferStartIndex;
//                fillBufferWithCachedValues(buffer, bufferStartIndex, bufferEndIndex, startFillIndex, len, len);
//            }
//        }
//
//        @Override
//        public int getAudioLen() {
//            return WavePanel.this.getSongLen();     // TODO: HNED - 3. radek v cache souboru, pripadne si to muzu vzit odjinud tu informaci
//        }
//
//        @Override
//        public int getCurrentStartIndexInAudio() {
//            return convertScrollValueToIndividualIndexInAudio(currScroll);
//        }
//
//        @Override
//        public int convertFromPixelToIndexInAudio(double pixel) {
//            int retVal = convertScrollValueToIndividualIndexInAudio(pixel);
//            return retVal;
//        }
//
//        public int fillBufferWithCachedValues(double[] buffer, int bufferStartIndex, int bufferEndIndex,
//                                              int startFillIndex, int inputLen, int outputLen) {
//            int outIndex = -1;
//
//            String cacheFilename = getCacheFilename();
//            try {
//                // Close to this block so references can be garbage collected (setting to null would have the same effect)
//                {
//                    RandomAccessFile cacheFile = new RandomAccessFile(cacheFilename, "r");
//                    FileChannel cache = cacheFile.getChannel();
//                    fillBufferWithCachedValues(cache, buffer, bufferStartIndex, bufferEndIndex, startFillIndex);
//                    DoubleWave.safeClose(cacheFile);
//                }
//
//// From comment in https://stackoverflow.com/questions/11316289/cannot-delete-file-even-after-closing-audioinputstream
//// https://stackoverflow.com/questions/991489/file-delete-returns-false-even-though-file-exists-file-canread-file-canw
//                System.gc();
//            }
//            catch (FileNotFoundException e) {
//                for(int i = 0; i < buffer.length; i++) {
//                    buffer[i] = 0;
//                }
//                System.exit(-555);          // TODO: For now just end it, but it makes sense, since that means that I don't have the audio track so I can't work with this
//
//            }
//
//            return outIndex;
//        }
//
//        @Override
//        public int getPrefixLenInBytes() {
//            return DoubleWave.SAMPLES_POS * Integer.BYTES;
//        }
//
//        @Deprecated
//        public int convertToOutputIndex(double inputIndex) {
//            return (int)inputIndex;
//        }
//    }
//
//
//
//
//    class DrawValuesSupplierAggregated extends DrawValuesSupplier {
//        @Override
//        public void fillBufferWithValuesToDraw(double[] buffer, int bufferStartIndex, int bufferEndIndex, int startFillIndex) {
//            int outputLen = (bufferEndIndex - bufferStartIndex) / 2;        // /2 because there are min and max
//            double samplesPerPixel = calculateInputValsPerOutputValsPure(getSongLen(), waveWidth);
//            int inputLen = (int)(samplesPerPixel * outputLen);
//
//            // TODO: IS_FIRST - s tim souvisi i ten currentDrawValues to tam vubec nemusi byt
//            if((getIsCached() && currentDrawValues != null) && !currentDrawValues.IS_FIRST) {
//                ProgramTest.debugPrint("Cache");
//                startFillIndex = convertToOutputIndex(samplesPerPixel * startFillIndex);
//                fillBufferWithCachedValues(buffer, bufferStartIndex, bufferEndIndex, startFillIndex, inputLen, outputLen);
//            }
//            else {
//                double[] song = doubleWave.getSong();           // TODO: Full Song loaded (use variable from doubleWave)
//                startFillIndex = (int)(samplesPerPixel * startFillIndex);
//                int outIndex = WavePanel.findExtremesInValues(song, buffer, startFillIndex, bufferStartIndex, inputLen, outputLen);
//                if (outIndex != bufferEndIndex) {
//                    ProgramTest.debugPrint("output index is not right", outIndex, bufferEndIndex);
//                    //System.exit(1548);
//                }
//            }
//
//            // TODO: POSUVNY BUFFER
////            startFillIndex *= 2;
//            ProgramTest.debugPrint("Filling buffer", getCurrentScroll(), startFillIndex, startFillIndex - getCurrentScroll());
//// TODO: POSUVNY BUFFER
//
//            ProgramTest.debugPrint("FillBuffer", bufferStartIndex, bufferEndIndex, startFillIndex, inputLen,
//                startFillIndex + inputLen, getSongLen(), samplesPerPixel, outputLen, samplesPerPixel * outputLen);
//            ProgramTest.debugPrint("FillBuffer2", startFillIndex, inputLen, getPreferredSize().width);
//            ProgramTest.debugPrint("FillBuffer3", getPreferredSize().width, waveWidth, wholeWavePanel.getCurrentHorizontalScroll(),
//                currScroll, currScroll == wholeWavePanel.getCurrentHorizontalScroll());
//        }
//
//
//        public int fillBufferWithCachedValues(double[] buffer, int bufferStartIndex, int bufferEndIndex,
//                                              int startFillIndex, int inputLen, int outputLen) {
//            int outIndex = -1;
//
//            String cacheFilename = getCacheFilename();
//            try {
////                InputStream cacheFile = new FileInputStream(cacheFilename);
////                DataInput cache = new DataInputStream(cacheFile);
//
//                // Close to this block so references can be garbage collected (setting to null would have the same effect)
//                {
//                    RandomAccessFile cacheFile = new RandomAccessFile(cacheFilename, "r");
//                    FileChannel cache = cacheFile.getChannel();
//                    fillBufferWithCachedValues(cache, buffer, bufferStartIndex, bufferEndIndex, startFillIndex);
//                    DoubleWave.safeClose(cacheFile);
//                }
//
//// From comment in https://stackoverflow.com/questions/11316289/cannot-delete-file-even-after-closing-audioinputstream
//// https://stackoverflow.com/questions/991489/file-delete-returns-false-even-though-file-exists-file-canread-file-canw
//                System.gc();
//            }
//            catch (Exception e) {
//                double[] song = doubleWave.getSong();           // TODO: Full Song loaded (use variable from doubleWave)
//                outIndex = WavePanel.findExtremesInValues(song, buffer, startFillIndex, bufferStartIndex, inputLen, outputLen);
//                if (outIndex != bufferEndIndex) {
//                    ProgramTest.debugPrint("output index is not right", outIndex, bufferEndIndex);
//                    //System.exit(1548);
//                }
//
//                isCached = false;       // Because when 1 file is missing probably all other are missing as well.
//            }
//
//            return outIndex;
//        }
//
//
//        @Override
//        public int getPrefixLenInBytes() {
//            return WavePanel.PREFIX_BEFORE_CACHED_DATA * Integer.BYTES;
//        }
//
//
//        @Override
//        public int getAudioLen() {
//            return WavePanel.this.getSongLen();     // TODO: HNED - 3. radek v cache souboru, pripadne si to muzu vzit odjinud tu informaci
//        }
//
//        @Override
//        public int convertFromPixelToIndexInAudio(double pixel) {
//            return (int)pixel;
//        }
//
//        @Deprecated
//        public int convertToOutputIndex(double inputIndex) {
//            double samplesPerPixel = calculateInputValsPerOutputValsPure(getSongLen(), waveWidth);
//            int outputIndex = (int)(2*inputIndex / samplesPerPixel);
//            return outputIndex;
//        }
//    }
// TODO: VYMAZAT







    // TODO: VYMAZAT = ONLY CACHING
//    abstract class DrawValuesSupplier implements DrawValuesSupplierIFace {
//        @Override
//        public boolean getIsCached() {
//            return isCached;
//        }
//
//
//        @Override
//        public int getMaxScroll() {
//// TODO: DEBUG
////            ProgramTest.debugPrint("getMaxScroll", wholeWavePanel.getMaxHorizontalScroll(), getPreferredSize().width - wholeWavePanel.getWaveVisibleWidth());
////            if(wholeWavePanel.getMaxHorizontalScroll() != getPreferredSize().width - wholeWavePanel.getWaveVisibleWidth() && wholeWavePanel.getMaxHorizontalScroll() > 10000) {
////                System.exit(456789);
////            }
//// TODO: DEBUG
//            return getPreferredSize().width - wholeWavePanel.getWaveVisibleWidth();
//            //return getPreferredSize().width;
//        }
//
//        @Override
//        public int getCurrentScroll() {
//// TODO: DEBUG
////            if(wholeWavePanel.getCurrentHorizontalScroll() > wholeWavePanel.getMaxHorizontalScroll()) {
////                System.exit(4567890);
////            }
////            if(currScroll > getMaxScroll()) {
////                ProgramTest.debugPrint(currScroll, wholeWavePanel.getCurrentHorizontalScroll(),
////                    wholeWavePanel.getMaxHorizontalScroll(), getMaxScroll());
////                System.exit(45678900);
////            }
//// TODO: DEBUG
//// TODO: POSUVNY BUFFER
//            ProgramTest.debugPrint("curr scroll", currScroll, wholeWavePanel.getCurrentHorizontalScroll());
//            return currScroll;
//            //return wholeWavePanel.getCurrentHorizontalScroll();
////            return zoomValuesInfo.currentZoomValueIndex;
//// TODO: POSUVNY BUFFER
//        }
//
//        public int getCurrentStartIndexInAudio() {
//            return currScroll;
//        }
//
//
//
//        @Deprecated
//        public double convertFromIndexInValuesToPixel(int indexInValues) {
//            double pixelsPerIndex = calculateInputValsPerOutputValsPure(getPreferredSize().width, getSongLen());
//            return indexInValues * pixelsPerIndex;
//        }
//
//        @Override
//        public int getTotalWidth() {
//            return getPreferredSize().width;
//        }
//    }
//
//
//
//    class DrawValuesSupplierIndividual extends DrawValuesSupplier {
//        @Override
//        public void fillBufferWithValuesToDraw(double[] buffer, int bufferStartIndex, int bufferEndIndex, int startFillIndex) {
//
//            // TODO: IS_FIRST - s tim souvisi i ten currentDrawValues to tam vubec nemusi byt
////            if((doubleWave.getIsFullSongLoaded() && currentDrawValues != null) && currentDrawValues.IS_FIRST) {
////                double[] song = doubleWave.getSong();               // TODO: Full Song loaded (use variable from doubleWave)
////                for (int i = bufferStartIndex, fillInd = startFillIndex; i < bufferEndIndex; i++, fillInd++) {
////// TODO: DEBUG
////                    ProgramTest.debugPrint("Individual fill", i, fillInd, getSongLen(), bufferStartIndex, bufferEndIndex);
////// TODO: DEBUG
////                    buffer[i] = song[fillInd];
////                }
////            }
////            else {
//                ProgramTest.debugPrint("Cache");
//                int len = bufferEndIndex - bufferStartIndex;
//                fillBufferWithCachedValues(buffer, bufferStartIndex, bufferEndIndex, startFillIndex, len, len);
////            }
//        }
//
//        @Override
//        public int getAudioLen() {
//            return WavePanel.this.getSongLen();     // TODO: HNED - 3. radek v cache souboru, pripadne si to muzu vzit odjinud tu informaci
//        }
//
//        @Override
//        public int getCurrentStartIndexInAudio() {
//            return convertScrollValueToIndividualIndexInAudio(currScroll);
//        }
//
//        @Override
//        public int convertFromPixelToIndexInAudio(double pixel) {
//            int retVal = convertScrollValueToIndividualIndexInAudio(pixel);
//            return retVal;
//        }
//
//        public int fillBufferWithCachedValues(double[] buffer, int bufferStartIndex, int bufferEndIndex,
//                                              int startFillIndex, int inputLen, int outputLen) {
//            int outIndex = -1;
//
//            String cacheFilename = getCacheFilename();
//            try {
//                RandomAccessFile cacheFile = new RandomAccessFile(cacheFilename, "r");
//                FileChannel cache = cacheFile.getChannel();
//                fillBufferWithCachedValues(cache, buffer, bufferStartIndex, bufferEndIndex, startFillIndex);
//                DoubleWave.safeClose(cacheFile);
//            }
//            catch (FileNotFoundException e) {
//                for(int i = 0; i < buffer.length; i++) {
//                    buffer[i] = 0;
//                }
//                System.exit(-555);          // TODO: For now just end it, but it makes sense, since that means that I don't have the audio track so I can't work with this
//
//            }
//
//            return outIndex;
//        }
//
//        @Override
//        public int getPrefixLenInBytes() {
//            return DoubleWave.SAMPLES_POS * Integer.BYTES;
//        }
//
//        @Deprecated
//        public int convertToOutputIndex(double inputIndex) {
//            return (int)inputIndex;
//        }
//    }
//
//
//
//
//    class DrawValuesSupplierAggregated extends DrawValuesSupplier {
//        @Override
//        public void fillBufferWithValuesToDraw(double[] buffer, int bufferStartIndex, int bufferEndIndex, int startFillIndex) {
//            int outputLen = (bufferEndIndex - bufferStartIndex) / 2;        // /2 because there are min and max
//            double samplesPerPixel = calculateInputValsPerOutputValsPure(getSongLen(), getPreferredSize().width);
//            int inputLen = (int)(samplesPerPixel * outputLen);
//
//            // TODO: IS_FIRST - s tim souvisi i ten currentDrawValues to tam vubec nemusi byt
////            if((getIsCached() && currentDrawValues != null) && !currentDrawValues.IS_FIRST) {
//                ProgramTest.debugPrint("Cache");
//                startFillIndex = convertToOutputIndex(samplesPerPixel * startFillIndex);
//                fillBufferWithCachedValues(buffer, bufferStartIndex, bufferEndIndex, startFillIndex, inputLen, outputLen);
////            }
////            else {
////                double[] song = doubleWave.getSong();           // TODO: Full Song loaded (use variable from doubleWave)
////                startFillIndex = (int)(samplesPerPixel * startFillIndex);
////                int outIndex = WavePanel.findExtremesInValues(song, buffer, startFillIndex, bufferStartIndex, inputLen, outputLen);
////                if (outIndex != bufferEndIndex) {
////                    ProgramTest.debugPrint("output index is not right", outIndex, bufferEndIndex);
////                    //System.exit(1548);
////                }
////            }
//
//            // TODO: POSUVNY BUFFER
////            startFillIndex *= 2;
//            ProgramTest.debugPrint("Filling buffer", getCurrentScroll(), startFillIndex, startFillIndex - getCurrentScroll());
//// TODO: POSUVNY BUFFER
//
//            ProgramTest.debugPrint("FillBuffer", bufferStartIndex, bufferEndIndex, startFillIndex, inputLen,
//                startFillIndex + inputLen, getSongLen(), samplesPerPixel, outputLen, samplesPerPixel * outputLen);
//            ProgramTest.debugPrint("FillBuffer2", startFillIndex, inputLen, getPreferredSize().width);
//            ProgramTest.debugPrint("FillBuffer3", getPreferredSize().width, wholeWavePanel.getCurrentHorizontalScroll(),
//                currScroll, currScroll == wholeWavePanel.getCurrentHorizontalScroll());
//        }
//
//
//        public int fillBufferWithCachedValues(double[] buffer, int bufferStartIndex, int bufferEndIndex,
//                                              int startFillIndex, int inputLen, int outputLen) {
//            int outIndex = -1;
//
//            String cacheFilename = getCacheFilename();
//            try {
////                InputStream cacheFile = new FileInputStream(cacheFilename);
////                DataInput cache = new DataInputStream(cacheFile);
//                RandomAccessFile cacheFile = new RandomAccessFile(cacheFilename, "r");
//                FileChannel cache = cacheFile.getChannel();
//                fillBufferWithCachedValues(cache, buffer, bufferStartIndex, bufferEndIndex, startFillIndex);
//                DoubleWave.safeClose(cacheFile);
//            }
//            catch (Exception e) {
//                double[] song = doubleWave.getSong();           // TODO: Full Song loaded (use variable from doubleWave)
//                outIndex = WavePanel.findExtremesInValues(song, buffer, startFillIndex, bufferStartIndex, inputLen, outputLen);
//                if (outIndex != bufferEndIndex) {
//                    ProgramTest.debugPrint("output index is not right", outIndex, bufferEndIndex);
//                    //System.exit(1548);
//                }
//
//                isCached = false;       // Because when 1 file is missing probably all other are missing as well.
//            }
//
//            return outIndex;
//        }
//
//
//        @Override
//        public int getPrefixLenInBytes() {
//            return WavePanel.PREFIX_BEFORE_CACHED_DATA * Integer.BYTES;
//        }
//
//
//        @Override
//        public int getAudioLen() {
//            return WavePanel.this.getSongLen();     // TODO: HNED - 3. radek v cache souboru, pripadne si to muzu vzit odjinud tu informaci
//        }
//
//        @Override
//        public int convertFromPixelToIndexInAudio(double pixel) {
//            return (int)pixel;
//        }
//
//        @Deprecated
//        public int convertToOutputIndex(double inputIndex) {
//            double samplesPerPixel = calculateInputValsPerOutputValsPure(getSongLen(), getPreferredSize().width);
//            int outputIndex = (int)(2*inputIndex / samplesPerPixel);
//            return outputIndex;
//        }
//    }
    // TODO: VYMAZAT











// TODO: VYMAZAT = NO CACHING
    abstract class DrawValuesSupplier implements DrawValuesSupplierIFace {
        public DrawValuesSupplier() {
            currScroll = wholeWavePanel.getCurrentHorizontalScroll();
        }

        @Override
        public boolean getIsCached() {
            return isCached;
        }


        @Override
        public int getMaxScroll() {
// TODO: DEBUG
//            ProgramTest.debugPrint("getMaxScroll", wholeWavePanel.getMaxHorizontalScroll(), getPreferredSize().width - wholeWavePanel.getWaveVisibleWidth());
//            if(wholeWavePanel.getMaxHorizontalScroll() != getPreferredSize().width - wholeWavePanel.getWaveVisibleWidth() && wholeWavePanel.getMaxHorizontalScroll() > 10000) {
//                System.exit(456789);
//            }
// TODO: DEBUG
            return waveWidth - wholeWavePanel.getWaveVisibleWidth();
            //return getPreferredSize().width;
        }

        @Override
        public int getCurrentScroll() {
// TODO: DEBUG
//            if(wholeWavePanel.getCurrentHorizontalScroll() > wholeWavePanel.getMaxHorizontalScroll()) {
//                System.exit(4567890);
//            }
//            if(currScroll > getMaxScroll()) {
//                ProgramTest.debugPrint(currScroll, wholeWavePanel.getCurrentHorizontalScroll(),
//                    wholeWavePanel.getMaxHorizontalScroll(), getMaxScroll());
//                System.exit(45678900);
//            }
// TODO: DEBUG
// TODO: POSUVNY BUFFER
            ProgramTest.debugPrint("curr scroll", currScroll, wholeWavePanel.getCurrentHorizontalScroll());
            return currScroll;
            //return wholeWavePanel.getCurrentHorizontalScroll();
//            return zoomValuesInfo.currentZoomValueIndex;
// TODO: POSUVNY BUFFER
        }

        public int getCurrentStartIndexInAudio() {
            return currScroll;
        }

        @Deprecated
        public double convertFromIndexInValuesToPixel(int indexInValues) {
            double pixelsPerIndex = calculateInputValsPerOutputValsPure(waveWidth, getSongLen());
            return indexInValues * pixelsPerIndex;
        }

        @Override
        public int getTotalWidth() {
            return waveWidth;
        }
    }



    class DrawValuesSupplierIndividual extends DrawValuesSupplier {
        @Override
        public void fillBufferWithValuesToDraw(double[] buffer, int bufferStartIndex, int bufferEndIndex, int startFillIndex) {

            // TODO: IS_FIRST - s tim souvisi i ten currentDrawValues to tam vubec nemusi byt
// TODO: WEIRD CACHE
//            if((doubleWave.getIsFullSongLoaded() && currentDrawValues != null) && currentDrawValues.IS_FIRST) {
            double[] song = doubleWave.getSong();               // TODO: Full Song loaded (use variable from doubleWave)
            for (int i = bufferStartIndex, fillInd = startFillIndex; i < bufferEndIndex; i++, fillInd++) {
// TODO: DEBUG
                ProgramTest.debugPrint("Individual fill", i, fillInd, getSongLen(), bufferStartIndex, bufferEndIndex);
// TODO: DEBUG
                buffer[i] = song[fillInd];
            }
//            }
//            else {
//                ProgramTest.debugPrint("Cache");
//                int len = bufferEndIndex - bufferStartIndex;
//                fillBufferWithCachedValues(buffer, bufferStartIndex, bufferEndIndex, startFillIndex, len, len);
//            }
// TODO: WEIRD CACHE
        }

        @Override
        public int getAudioLen() {
            return WavePanel.this.getSongLen();     // TODO: HNED - 3. radek v cache souboru, pripadne si to muzu vzit odjinud tu informaci
        }

        @Override
        public int getCurrentStartIndexInAudio() {
            return convertScrollValueToIndividualIndexInAudio(currScroll);
        }

        @Override
        public int convertFromPixelToIndexInAudio(double pixel) {
            int retVal = convertScrollValueToIndividualIndexInAudio(pixel);
            return retVal;
        }

        public int fillBufferWithCachedValues(double[] buffer, int bufferStartIndex, int bufferEndIndex,
                                              int startFillIndex, int inputLen, int outputLen) {
            int outIndex = -1;

            String cacheFilename = getCacheFilename();
            try {
                RandomAccessFile cacheFile = new RandomAccessFile(cacheFilename, "r");
                FileChannel cache = cacheFile.getChannel();
                fillBufferWithCachedValues(cache, buffer, bufferStartIndex, bufferEndIndex, startFillIndex);
            }
            catch (FileNotFoundException e) {
                for(int i = 0; i < buffer.length; i++) {
                    buffer[i] = 0;
                }
                System.exit(-555);          // TODO: For now just end it, but it makes sense, since that means that I don't have the audio track so I can't work with this

            }

            return outIndex;
        }

        @Override
        public int getPrefixLenInBytes() {
            return DoubleWave.SAMPLES_POS * Integer.BYTES;
        }

        @Deprecated
        public int convertToOutputIndex(double inputIndex) {
            return (int)inputIndex;
        }
    }




    class DrawValuesSupplierAggregated extends DrawValuesSupplier {
        @Override
        public void fillBufferWithValuesToDraw(double[] buffer, int bufferStartIndex, int bufferEndIndex, int startFillIndex) {
            int outputLen = (bufferEndIndex - bufferStartIndex) / 2;        // /2 because there are min and max
            double samplesPerPixel = calculateInputValsPerOutputValsPure(getSongLen(), waveWidth);
            int inputLen = (int)(samplesPerPixel * outputLen);

            // TODO: IS_FIRST - s tim souvisi i ten currentDrawValues to tam vubec nemusi byt
// TODO: WEIRD CACHE
//            if((getIsCached() && currentDrawValues != null) && !currentDrawValues.IS_FIRST) {
//                ProgramTest.debugPrint("Cache");
//                startFillIndex = convertToOutputIndex(samplesPerPixel * startFillIndex);
//                fillBufferWithCachedValues(buffer, bufferStartIndex, bufferEndIndex, startFillIndex, inputLen, outputLen);
//            }
//            else {
            double[] song = doubleWave.getSong();           // TODO: Full Song loaded (use variable from doubleWave)
            startFillIndex = (int)(samplesPerPixel * startFillIndex);
            int outIndex = WavePanel.findExtremesInValues(song, buffer, startFillIndex, bufferStartIndex, inputLen, outputLen);
            if (outIndex != bufferEndIndex) {
                ProgramTest.debugPrint("output index is not right", outIndex, bufferEndIndex);
                //System.exit(1548);
//                }
// TODO: WEIRD CACHE
            }

            // TODO: POSUVNY BUFFER
//            startFillIndex *= 2;
            ProgramTest.debugPrint("Filling buffer", getCurrentScroll(), startFillIndex, startFillIndex - getCurrentScroll());
// TODO: POSUVNY BUFFER

            ProgramTest.debugPrint("FillBuffer", bufferStartIndex, bufferEndIndex, startFillIndex, inputLen,
                startFillIndex + inputLen, getSongLen(), samplesPerPixel, outputLen, samplesPerPixel * outputLen,
                buffer.length);
            ProgramTest.debugPrint("FillBuffer2", startFillIndex, inputLen, waveWidth);
            ProgramTest.debugPrint("FillBuffer3", getPreferredSize().width, waveWidth, wholeWavePanel.getCurrentHorizontalScroll(),
                currScroll, currScroll == wholeWavePanel.getCurrentHorizontalScroll());
        }


        public int fillBufferWithCachedValues(double[] buffer, int bufferStartIndex, int bufferEndIndex,
                                              int startFillIndex, int inputLen, int outputLen) {
            int outIndex = -1;

            String cacheFilename = getCacheFilename();
            try {
//                InputStream cacheFile = new FileInputStream(cacheFilename);
//                DataInput cache = new DataInputStream(cacheFile);
                RandomAccessFile cacheFile = new RandomAccessFile(cacheFilename, "r");
                FileChannel cache = cacheFile.getChannel();
                fillBufferWithCachedValues(cache, buffer, bufferStartIndex, bufferEndIndex, startFillIndex);
            }
            catch (Exception e) {
                double[] song = doubleWave.getSong();           // TODO: Full Song loaded (use variable from doubleWave)
                outIndex = WavePanel.findExtremesInValues(song, buffer, startFillIndex, bufferStartIndex, inputLen, outputLen);
                if (outIndex != bufferEndIndex) {
                    ProgramTest.debugPrint("output index is not right", outIndex, bufferEndIndex);
                    //System.exit(1548);
                }

                isCached = false;       // Because when 1 file is missing probably all other are missing as well.
            }

            return outIndex;
        }


        @Override
        public int getPrefixLenInBytes() {
            return WavePanel.PREFIX_BEFORE_CACHED_DATA * Integer.BYTES;
        }


        @Override
        public int getAudioLen() {
            return WavePanel.this.getSongLen();     // TODO: HNED - 3. radek v cache souboru, pripadne si to muzu vzit odjinud tu informaci
        }


        @Override
        public int convertFromPixelToIndexInAudio(double pixel) {
            return (int)pixel;
        }

        @Deprecated
        public int convertToOutputIndex(double inputIndex) {
            double samplesPerPixel = calculateInputValsPerOutputValsPure(getSongLen(), waveWidth);
            int outputIndex = (int)(2*inputIndex / samplesPerPixel);
            return outputIndex;
        }
    }
// TODO: VYMAZAT

}