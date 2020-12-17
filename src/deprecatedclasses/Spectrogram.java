package deprecatedclasses;

import Rocnikovy_Projekt.Program;
import org.jtransforms.fft.DoubleFFT_1D;
import util.audio.FFT;
import util.math.ArithmeticOperation;
import util.swing.SwingUtilities;

import java.awt.*;
import java.awt.image.BufferedImage;

@Deprecated
public class Spectrogram {
    ////////////////////////////////////////////////////////////////////////////////////
    ///////////////////// Spectrogram
    ////////////////////////////////////////////////////////////////////////////////////
    // TODO: VYMAZAT TEN SPECTROGRAM
    // TODO: VYMAZAT TEN SPECTROGRAM
    // TODO: VYMAZAT TEN SPECTROGRAM
    // TODO: VYMAZAT TEN SPECTROGRAM

    public static BufferedImage createSpectrogram(double[] song, int numberOfChannels, int windowSize, int windowShift,
                                                  int startIndex, int endIndex, double freqJump,
                                                  int pixelWidthForWindow, int pixelHeightForBin) {
        DoubleFFT_1D fft = new DoubleFFT_1D(windowSize);
        return createSpectrogram(song, numberOfChannels, windowSize, windowShift,
                startIndex, endIndex, freqJump, fft, pixelWidthForWindow, pixelHeightForBin);
    }


//    public static BufferedImage createSpectrogram(double[] song, int numberOfChannels, int windowSize, int windowShift,
//                                                  int startIndex, int endIndex, double freqJump, DoubleFFT_1D fft,
//                                                  int pixelWidthForWindow, int pixelHeightForBin) {
//        double[] fftResult = new double[windowSize];
//
//        int spectrogramWidth = (endIndex - startIndex) / windowSize;
//        int spectrogramWidthInPixels = spectrogramWidth * pixelWidthForWindow;
//        int binCount = getBinCountRealForward(windowSize);
//        int spectrogramHeightInPixels = binCount * pixelHeightForBin;
//
//
//        int spectrogramWidthInPixelsWithReference = spectrogramWidthInPixels + 100;    // TODO:
//        int spectrogramStart = 100;
//        int spectrogramWidthInPixelsWithReferenceAndHzLabels = spectrogramWidthInPixelsWithReference + spectrogramStart;
//
//        BufferedImage spectrogram = new BufferedImage(spectrogramWidthInPixelsWithReferenceAndHzLabels, spectrogramHeightInPixels, BufferedImage.TYPE_INT_RGB);
//        Graphics g = spectrogram.getGraphics();
//        g.setColor(Color.black);
//        g.fillRect(0, 0, spectrogram.getWidth(), spectrogram.getHeight());
//        double windowOverlapCountForOneWindow = windowSize / (double)windowShift;
//        int windowOverlapCountForOneWindowInt = (int)windowOverlapCountForOneWindow;    // TODO: Asi pres Math.ceil
//        int pixelWidthForWindowPart;
//        double[][] currentlyCalculatedMeasures;
////        if(windowOverlapCountForOneWindowInt == 0) {
////            // TODO:
////            currentlyCalculatedMeasures = new double[binCount][1];
////            pixelWidthForWindowPart = (int)(pixelWidthForWindow / windowOverlapCountForOneWindow);
////            pixelWidthForWindow = pixelWidthForWindowPart;
////            // TODO:
////        }
////        else {
//            currentlyCalculatedMeasures = new double[binCount][windowOverlapCountForOneWindowInt];
//            // TODO:
//            pixelWidthForWindowPart = pixelWidthForWindow / windowOverlapCountForOneWindowInt;
//            System.out.println("aa:\t" + pixelWidthForWindow + "\t" + pixelWidthForWindowPart);
//            pixelWidthForWindow = pixelWidthForWindowPart;
//            // TODO:
////        }
//
//        int logarithmBase = 2;
//        double[] fftMeasures = new double[binCount];
//        int currWindowCalculatedInd = 0;       // Is the index for which we are no calculating the values - l - Lies between 0 and windowOverlapCountForOneWindowInt
//        for(int i = startIndex, currX = spectrogramStart, currWindow = 0; i < endIndex; i += windowShift, currX += pixelWidthForWindow, currWindow++) {
//            calculateFFTRealForward(song, i, numberOfChannels, fft, fftResult);
//            convertResultsOfFFTToRealRealForward(fftResult, fftMeasures);
//            for(int j = 0; j < fftMeasures.length; j++) {
//                fftMeasures[j] += 1;      // Not the minimum energy is 1, so the minimum log == 0
//            }
//
//            Program.performOperationOnSamples(fftMeasures, logarithmBase, ArithmeticOperation.LOG);
//// TODO: Asi nemusim a jestli musim, tak to je vypocet navic, protoze tam posilam sqrt stejne
//            for(int j = 0; j < fftMeasures.length; j++) {
//// TODO: Debug print
////                if(fftMeasures[j] > 0) {
////                    System.out.println(j + "\t" + fftMeasures[j]);
////                }
//                // TODO: jen jse mto delal abych prisel na to jak to udelat obecne
////                if(currWindow == 0) {
////                    // Print 0 index
////                    for(int k = 0; k < currentlyCalculatedMeasures[j].length; k++) {
////                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / (k+1);
////                    }
////                }
////                else if(currWindow == 1) {
////                    currentlyCalculatedMeasures[j][0] = fftMeasures[j] / currentlyCalculatedMeasures[j].length;
////                    // Print 1 index
////                    for(int k = 1; k < currentlyCalculatedMeasures[j].length; k++) {
////                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / (k+1);
////                    }
////                }
////                else if(currWindow == 2) {
////                    currentlyCalculatedMeasures[j][0] += fftMeasures[j] / currentlyCalculatedMeasures[j].length;
////                    currentlyCalculatedMeasures[j][1] = fftMeasures[j] / currentlyCalculatedMeasures[j].length;
////                    // Print 2 index
////                    for(int k = 2; k < currentlyCalculatedMeasures[j].length; k++) {
////                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / (k+1);
////                    }
////                }
//                if(currWindow < currentlyCalculatedMeasures[j].length) {
//                    int k;
//                    for(k = 0; k < currWindow - 1; k++) {
//                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / currentlyCalculatedMeasures[j].length;
//                    }
//// TODO: Vynuluju az potom
////                    if(k != 0) {
////                        currentlyCalculatedMeasures[j][k] = fftMeasures[j] / currentlyCalculatedMeasures[j].length;
////                        k++;
////                    }
//
//                    for(; k < currentlyCalculatedMeasures[j].length; k++) {
//                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / (k+1);
//                    }
//                }
//                else {
//                    for(int k = 0; k < currentlyCalculatedMeasures[j].length; k++) {
//                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / currentlyCalculatedMeasures[j].length;
//                    }
//                }
//
//
//
//
//// TODO:
////                if(fftMeasures[j] > 0x00ffffff) {       // TODO: To neni dobre asi
////                    fftMeasures[j] = 0x00ffffff;
////                }
////                else {
////                    fftMeasures[j] %= 0x00ffffff;
////                }
////                fftMeasures[j] *= fftMeasures[j];
//            }
//
//            // TODO: nextY useless
//            // TODO:
//            // If we want to have spectrogram with lower frequencies at top and higher at bottom then use this
//            // for(int bin = 0, currY = 0, nextY = pixelHeightForBin; bin < binCount; bin++, currY = nextY, nextY += pixelHeightForBin) {
//            for(int bin = 0, currY = spectrogramHeightInPixels, nextY = currY - pixelHeightForBin; bin < binCount; bin++, currY = nextY, nextY -= pixelHeightForBin) {
//                // So the max is 1, but I am not really sure if I shouldn't do something further, like multiply it by some factor so the maximum is == 1.
//                // If I don't do anything then the maximum is reached only if the wave is constant wave at 1.
//                double val;
//                val = currentlyCalculatedMeasures[bin][currWindow % currentlyCalculatedMeasures[bin].length];
//                // TODO: 2*windowSize because of the +1 for each fftMeasures
//                double windowSizeLog = Math.log(2 * windowSize) / Math.log(logarithmBase);
//                val /= windowSizeLog;
//                //val /= (windowSize / 2);        // TODO:
//                //val *= 30;
//                Color c;
//                c = Color.getHSBColor(1-(float)val, (float)val, (float)val);
////                c = Color.getHSBColor(1,(float)val,(float)val);
////                c = Color.getHSBColor(-1+(float)val, (float)-val, (float)-val);
//                //val = Double.NEGATIVE_INFINITY;
//                //c = Color.getHSBColor((float)(1-val),(float)val,(float)val);
////                System.out.println(val + "\t" + currentlyCalculatedMeasures[bin].length);
//                g.setColor(c);
//                g.fillRect(currX, currY, pixelWidthForWindow, pixelHeightForBin);
//
////
////                System.out.println(currX + "\t" + currY + "\t" + i + "\t" + endIndex);
////                System.out.println(spectrogramWidthInPixels + "\t" + spectrogramHeightInPixels);
////                System.out.println();
////
////                int rgb = spectrogram.getRGB(currX, currY);
////                rgb *= Math.ceil(fftMeasures[bin]);
////                g.fillRect(currX, currY, pixelWidthForWindow, pixelHeightForBin);
////
////                int red = (rgb & 0x00ff0000) >> 16;
////                int green = (rgb & 0x0000ff00) >> 8;
////                int blue =  rgb & 0x000000ff;
////
////                int redBin;
////                int greenBin;
////                int blueBin;
////
////                if(fftMeasures[bin] < 0x000000ff) {
////                    blueBin = (int)Math.ceil(fftMeasures[bin] / windowOverlapCountForOneWindow);
////                    blue += blueBin;
////                }
////                else if((greenBin = ((int)Math.ceil(fftMeasures[bin]) & 0x0000ff00) >> 8) > 0) {
////                    greenBin /= windowOverlapCountForOneWindow;
////                    green += greenBin;
////
////                    blueBin = (int)Math.ceil((0x000000ff / windowOverlapCountForOneWindow));
////                    blue += blueBin;
////                }
////                else {
////                    redBin = ((int)Math.ceil(fftMeasures[bin]) & 0x00ff0000) >> 16;
////                    redBin /= windowOverlapCountForOneWindow;
////                    red += redBin;
////
////
////                    blueBin = (int)Math.ceil((0x000000ff / windowOverlapCountForOneWindow));
////                    blue += blueBin;
////                    greenBin = blueBin;
////                    green += greenBin;
////                }
//
//                currentlyCalculatedMeasures[bin][currWindow % currentlyCalculatedMeasures[bin].length] = 0;  // It was used and is now the latest unused, so it is set to 0 and will be set to 0 again after passing windowOverlapCountForOneWindowInt windows
//            }
//        }
//
//
//
//         // TODO: Timhle se dela to spektrum na prostor, tj. ten prouzek
////        int pixelSkip = 0x00ffffff / spectrogramWidthInPixels;
////        double pixelSkipDouble = 1.0 / spectrogramWidthInPixels;
////        System.out.println(pixelSkip + "\t" + 0x00ffffff + "\t" + pixelSkipDouble);
////
////        double currHue = 0;
////        for(int rgb = 0, x = 0; rgb < 0x00ffffff; rgb += pixelSkip, x++, currHue += pixelSkipDouble) {
/////*
////            int red = (rgb & 0x00ff0000) >> 16;
////            int green = (rgb & 0x0000ff00) >> 8;
////            int blue =  rgb & 0x000000ff;
////            System.out.println(red + "\t" + green + "\t" + blue);
////            System.out.println(x);
////            g.setColor(new Color(red, green, blue));
////*/
////            Color c;
////
/////*            // B should be 1 or currHue
////            c = Color.getHSBColor((float)currHue,1-(float)currHue, (float)currHue); // Black to white
////
////            c = Color.getHSBColor((float)currHue,1-(float)currHue, 1);          // Red to white
////
////            c = Color.getHSBColor((float)currHue, (float)currHue, 1);               // White to red
////
////            c = Color.getHSBColor((float)currHue, (float)currHue, (float)currHue);      // Black to red
////
////
////
////
////            c = Color.getHSBColor(1-(float)currHue,1-(float)currHue, (float)currHue); // Black to white
////
////            c = Color.getHSBColor(1-(float)currHue,1-(float)currHue, 1);           // Red to white
////
////            c = Color.getHSBColor(1-(float)currHue, (float)currHue, 1);               // White to red
////*/
////            c = Color.getHSBColor(1-(float)currHue, (float)currHue, (float)currHue);        // Good, from black to red
/////*
////
////
////
////// S = 1
////            c = Color.getHSBColor((float)currHue,1, (float)currHue); // Ok - Black to red
////
////            c = Color.getHSBColor((float)currHue, 1, 1);               // NO - Red to red
////
////            c = Color.getHSBColor((float)currHue, 1, (float)currHue);      // Black to red
////
////
////
////            c = Color.getHSBColor(1-(float)currHue,1, (float)currHue); // Black to red
////
////            c = Color.getHSBColor(1-(float)currHue, 1, 1);               // NO - Red to red
////
////            c = Color.getHSBColor(1-(float)currHue, 1, (float)currHue);        // Good, from black to red
////*/
////
////            g.setColor(c);
////            g.fillRect(x, 0, 1, spectrogramHeightInPixels);
////           // g.fillRect(50,0, spectrogramWidthInPixels, spectrogramHeightInPixels);
//////            g.setColor(Color.red);
//////            g.fillRect(0,0, spectrogramWidthInPixels / 8, spectrogramHeightInPixels);
////
////        }
//
//        // Draw the measure reference
//        g.setColor(Color.white);
//        int startXReference = spectrogramStart + spectrogramWidthInPixels;     // TODO: V tehle jmenech promennych je bordel
//
//        // TODO: bud to mit oddeleny jen bilou carou ... to je to zakomentovany nebo tam mit proste bilej obdelnik to je to nezakomentovany ... jedna se jen o tyhle 3 nasledujici radky
//        // TODO: +20 kdyz chci tu carku
//        // TODO: Ten ctverec je lepsi ... podle me
//        //int startXReference = spectrogramStart + spectrogramWidthInPixels + 20;
////        g.drawLine(startXReference, 0, startXReference, spectrogramHeightInPixels);
//        g.fillRect(startXReference, 0, spectrogramWidthInPixelsWithReferenceAndHzLabels, spectrogramHeightInPixels);
//
//        double pixelSkipDouble = 1.0 / spectrogramHeightInPixels;
//        int x = startXReference + 20;
//        int visibleWidth = 60;
//        int height = 1;
//        double colorVal = 0;
//        for(int y = spectrogramHeightInPixels; y >= 0; y--, colorVal += pixelSkipDouble) {
//            Color c = Color.getHSBColor(1 - (float) colorVal, (float) colorVal, (float) colorVal);
//            g.setColor(c);
//            g.fillRect(x, y, visibleWidth, height);
//            System.out.println("test\t" + x + "\t" + y + "\t" + pixelSkipDouble + "\t" + colorVal + "\t" + (colorVal + pixelSkipDouble));
//        }
//
//
//        // Draw frequency labels
//        int n = 8;      // take every nth bin
//        int maxTextHeight = n * pixelHeightForBin;
//        String[] binFreqs = getFreqs(binCount, freqJump, 0, n);
//        getFont(24, g, binFreqs, spectrogramStart - 15, maxTextHeight);
//        FontMetrics fontMetrics = g.getFontMetrics();
//
//        g.setColor(Color.white);
//        g.fillRect(0, 0, spectrogramStart, spectrogramHeightInPixels);
//        int lineXSmall = spectrogramStart - 5;
//        int lineXBig = lineXSmall - 5;
//        g.drawLine(lineXSmall, 0, lineXSmall, spectrogramHeightInPixels);
//        g.setColor(Color.black);
//
//        // Draw the small lines
//        for(int i = 0, currY = 0, midY = pixelHeightForBin / 2;
//            i < binCount;
//            i++, currY += pixelHeightForBin, midY += pixelHeightForBin)
//        {
//            g.drawLine(lineXSmall, midY, spectrogramStart, midY);
//        }
//
//        int textHeight = fontMetrics.getHeight();
//        int freeSpace = maxTextHeight - textHeight;
//        freeSpace /= 2;
//        // Draw the big lines + text
//        for(int bin = binFreqs.length - 1, currY = 0, midY = pixelHeightForBin / 2;
//            bin >= 0;
//            bin--, currY += maxTextHeight, midY += maxTextHeight)
//        {
//            System.out.println(bin + "\t"  + currY);
//            g.drawLine(lineXBig, midY, spectrogramStart, midY);
//            int textLen = fontMetrics.stringWidth(binFreqs[bin]);
//            if(bin == binFreqs.length - 1) {        // Special cases at top and at bottom (0Hz and nyquist freq)
//                g.drawString(binFreqs[bin], spectrogramStart - textLen - 15, 15);   // TODO: y == 15 because of the top ledge (the ledge with cross)
//            }
//            else if(bin == 0) {
//                g.drawString(binFreqs[bin], spectrogramStart - textLen - 15, spectrogramHeightInPixels);
//            }
//            else {
//                g.drawString(binFreqs[bin], spectrogramStart - textLen - 15, currY + freeSpace);
//            }
//        }
//
//
//        // Draw KHz to the top left
//        g.drawString("KHz", 0, spectrogramHeightInPixels - maxTextHeight / 2);      // TODO: Bude tam obcas prekryv
//
//        return spectrogram;
//    }


    public static BufferedImage createSpectrogram(double[] song, int numberOfChannels, int windowSize, int windowShift,
                                                  int startIndex, int endIndex, double freqJump, DoubleFFT_1D fft,
                                                  int spectrogramWidthInPixels, int spectrogramHeightInPixels) {
        double[] fftResult = new double[windowSize];

        int windowCount = (endIndex - startIndex) / windowSize;        // TODO: Tady vzit Math.ceil abych nabral i to posledni okno
        double pixelWidthForWindow = spectrogramWidthInPixels / (double)windowCount;
        int binCount = FFT.getBinCountRealForward(windowSize);
        double pixelHeightForBin = spectrogramHeightInPixels / (double)binCount;


        int spectrogramWidthInPixelsWithReference = spectrogramWidthInPixels + 100;    // TODO:
        int spectrogramStart = 100;
        int spectrogramWidthInPixelsWithReferenceAndHzLabels = spectrogramWidthInPixelsWithReference + spectrogramStart;

        BufferedImage spectrogram = new BufferedImage(spectrogramWidthInPixelsWithReferenceAndHzLabels, spectrogramHeightInPixels, BufferedImage.TYPE_INT_RGB);
        Graphics g = spectrogram.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, spectrogram.getWidth(), spectrogram.getHeight());
        double windowOverlapCountForOneWindow = windowSize / (double)windowShift;
//        int windowOverlapCountForOneWindowInt = (int)windowOverlapCountForOneWindow;    // TODO: Asi pres Math.ceil
        double pixelWidthForWindowPart;
        double[][] currentlyCalculatedMeasures;
        double lastWindowReminder = windowOverlapCountForOneWindow % 1;
        double pixelWidthForOneWindowInCaseItIsSmallerThanOneIThenHaveAddThisValueSomehowToThecurrentlyCalculatedMeasuresSize;



        double[] windowsOverlaps = null;
        double[] lastWindowParts = null;
        double[] counters = null;
        if(lastWindowReminder == 0) {
            currentlyCalculatedMeasures = new double[binCount][(int) windowOverlapCountForOneWindow];
        }
        else {
            currentlyCalculatedMeasures = new double[binCount][(int)Math.ceil(windowOverlapCountForOneWindow)];

            windowsOverlaps = new double[(int)Math.ceil(windowOverlapCountForOneWindow)];
            lastWindowParts = new double[windowsOverlaps.length];   // TODO: Not needed but it is easier to understand if I have separater array for it
            counters = new double[lastWindowParts.length];

/*
            // TODO:
            windowsOverlaps = new double[6];
            lastWindowParts = new double[windowsOverlaps.length];   // TODO: Not needed but it is easier to understand if I have separater array for it
            // TODO:
*/
            windowsOverlaps[0] = windowOverlapCountForOneWindow;
            lastWindowParts[0] = lastWindowReminder;
            System.out.println(0 + "\t" + windowsOverlaps[0]);
            for (int i = 1; i < windowsOverlaps.length; i++) {
                windowsOverlaps[i] = (int)Math.ceil(windowsOverlaps[i - 1]) - windowsOverlaps[i - 1];
                windowsOverlaps[i] = windowsOverlaps[0] - windowsOverlaps[i];
                lastWindowParts[i] = windowsOverlaps[i] % 1; // TODO: Not sure maybe not even needed

                counters[i] = (1 - lastWindowParts[i - 1]);
                System.out.println(i + "\t" + windowsOverlaps[i] + "\t" + ((int)Math.ceil(windowsOverlaps[i - 1]) - windowsOverlaps[i - 1]));
            }
// TODO:            System.exit((int)Math.ceil(1.00));
        }
        // TODO:
        pixelWidthForWindowPart = pixelWidthForWindow / windowOverlapCountForOneWindow;
        System.out.println("aa:\t" + pixelWidthForWindow + "\t" + pixelWidthForWindowPart);
        pixelWidthForWindow = pixelWidthForWindowPart;         //TODO: Taky be melo byt double
        // TODO:

        double windowsPerPixel = 1;
        if(pixelWidthForWindowPart < 1) {
            windowsPerPixel = 1 / pixelWidthForWindowPart;
        }



//        if(windowOverlapCountForOneWindowInt == 0) {
//            // TODO:
//            currentlyCalculatedMeasures = new double[binCount][1];
//            pixelWidthForWindowPart = (int)(pixelWidthForWindow / windowOverlapCountForOneWindow);
//            pixelWidthForWindow = pixelWidthForWindowPart;
//            // TODO:
//        }
//        else {
//            currentlyCalculatedMeasures = new double[binCount][windowOverlapCountForOneWindowInt];
//        // TODO:
//            pixelWidthForWindowPart = pixelWidthForWindow / windowOverlapCountForOneWindowInt;
//            System.out.println("aa:\t" + pixelWidthForWindow + "\t" + pixelWidthForWindowPart);
//            pixelWidthForWindow = pixelWidthForWindowPart;
//        // TODO:
//        }


        // TODO:
        double[][][] tmpArrs = new double[windowsOverlaps.length][2][binCount];
//        double[][][] tmpArrs = null;

        // TODO:
        int oldestWindow = 0;
        int newOldestWindow = 0;
        boolean oldestWindowSet = false;

        int newestWindow = 0;
        // TODO:

        int logarithmBase = 2;
        double[] fftMeasures = new double[binCount];
        double[] tmpArr = new double[fftMeasures.length];
        double[] tmpArr2 = new double[fftMeasures.length];
        int currWindowCalculatedInd = 0;       // Is the index for which we are no calculating the values - l - Lies between 0 and windowOverlapCountForOneWindowInt
        double currX = spectrogramStart;
        int nextPixel = (int)(currX + 1);    // Used only if (pixelWidthForWindowPart < 1)

        double multiplyFactor = lastWindowReminder;
        int indexWhereToPutNotFullValue = currentlyCalculatedMeasures[0].length - 1;
        double[] arrWithNotFullMeasures = new double[fftMeasures.length];
        TODO: // TODO: Just debug label

        for(int i = startIndex, currWindow = 0; i < endIndex; i += windowShift, currWindow++, indexWhereToPutNotFullValue++) {
            indexWhereToPutNotFullValue %= currentlyCalculatedMeasures[0].length;
            oldestWindow = newOldestWindow;
            oldestWindowSet = false;

            double windowPartsCount = windowOverlapCountForOneWindow;       // TODO:
            if (pixelWidthForWindowPart < 1) {
                currWindow = 0; // Because currWindow says which window is currently drawn, and we want to always draw the first in this case
                System.out.println("DEBUG:\t" + currX + "\t" + nextPixel);


// TODO:                double windowPartsCount = windowOverlapCountForOneWindow;
                double lastWindowPart;
                if(lastWindowReminder == 0) {
// TODO:                    windowPartsCount = currentlyCalculatedMeasures[0].length;
                    lastWindowPart = 0;
                }
                else {
// TODO:                    windowPartsCount = (int)Math.ceil(windowsOverlaps[0]);
                    lastWindowPart = lastWindowParts[0];
                }

                if (currX < nextPixel) {
                    System.out.println("NEWMEASURES:\t" + currentlyCalculatedMeasures[0][0]);
                    addCurrentMeasures(song, i, numberOfChannels, fft, fftResult, fftMeasures, logarithmBase,
                            currWindow, currentlyCalculatedMeasures, tmpArr, tmpArr2, multiplyFactor,
                            windowPartsCount, true, counters, windowsOverlaps, lastWindowParts, tmpArrs,
                            indexWhereToPutNotFullValue, arrWithNotFullMeasures);
                    continue;           // Just keep calculating the values until we can draw the pixel value
                }

                nextPixel++;
            }
            else {
                if(lastWindowReminder == 0) {
//                    int windowPartsCount = currentlyCalculatedMeasures[0].length;
                    addCurrentMeasures(song, i, numberOfChannels, fft, fftResult, fftMeasures, logarithmBase,
                            currWindow, currentlyCalculatedMeasures, tmpArr, tmpArr2, 0,
                            windowPartsCount, false, counters, windowsOverlaps, lastWindowParts, tmpArrs,
                            indexWhereToPutNotFullValue, arrWithNotFullMeasures);
                }
                else {
                    int mod = currWindow % currentlyCalculatedMeasures[0].length;
//                    int windowPartsCount = (int)Math.ceil(windowsOverlaps[mod]);
                    addCurrentMeasures(song, i, numberOfChannels, fft, fftResult, fftMeasures, logarithmBase,
                            currWindow, currentlyCalculatedMeasures, tmpArr, tmpArr2, multiplyFactor,
                            windowPartsCount, false, counters, windowsOverlaps, lastWindowParts, tmpArrs,
                            indexWhereToPutNotFullValue, arrWithNotFullMeasures);
                }
            }
            // TODO: Nahrazeno funkci
//            calculateFFTRealForward(song, i, numberOfChannels, fft, fftResult);
//            convertResultsOfFFTToRealRealForward(fftResult, fftMeasures);
//            for(int j = 0; j < fftMeasures.length; j++) {
//                fftMeasures[j] += 1;      // Not the minimum energy is 1, so the minimum log == 0
//            }
//
//            Program.performOperationOnSamples(fftMeasures, logarithmBase, ArithmeticOperation.LOG);
//// TODO: Asi nemusim a jestli musim, tak to je vypocet navic, protoze tam posilam sqrt stejne
//            for(int j = 0; j < fftMeasures.length; j++) {
//// TODO: Debug print
////                if(fftMeasures[j] > 0) {
////                    System.out.println(j + "\t" + fftMeasures[j]);
////                }
//
//                if(currWindow < currentlyCalculatedMeasures[j].length) {
//                    int k;
//                    for(k = 0; k < currWindow - 1; k++) {
//                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / currentlyCalculatedMeasures[j].length;
//                    }
//                    for(; k < currentlyCalculatedMeasures[j].length; k++) {
//                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / (k+1);
//                    }
//                }
//                else {
//                    for(int k = 0; k < currentlyCalculatedMeasures[j].length; k++) {
//                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / currentlyCalculatedMeasures[j].length;
//                    }
//                }




// TODO:
//                if(fftMeasures[j] > 0x00ffffff) {       // TODO: To neni dobre asi
//                    fftMeasures[j] = 0x00ffffff;
//                }
//                else {
//                    fftMeasures[j] %= 0x00ffffff;
//                }
//                fftMeasures[j] *= fftMeasures[j];
//            }

            System.out.println("counter0\t" + counters[0] + "\t" + lastWindowParts[0] + "\t" + windowsOverlaps[0]);
            System.out.println("counter1\t" + counters[1] + "\t" + lastWindowParts[1] + "\t" + windowsOverlaps[1]);
            // TODO: nextY useless
            // TODO: window = oldestWindow ??? ... asi newest ale u neho je zase problem s -Infinity
//            for(int window = oldestWindow; window < windowsOverlaps.length; window++) {
//                double currY = spectrogramHeightInPixels - pixelHeightForBin;
//                if (counters[window] >= windowOverlapCountForOneWindow) {
//                    newestWindow = window;
//                    if (!oldestWindowSet) {
//                        oldestWindowSet = true;
//                        newOldestWindow = window;
//                    }
////TODO:System.exit(currWindow);
//                    currX = Program.drawOneWindowInSpectrogram(currY, counters, windowsOverlaps, lastWindowParts, currentlyCalculatedMeasures,
//                        window, oldestWindowSet, newOldestWindow, currWindow, pixelHeightForBin, binCount,
//                        windowSize, logarithmBase, g, currX, pixelWidthForWindow, lastWindowReminder,
//                        tmpArrs, windowOverlapCountForOneWindow);
//                }
//            }
//
//
//            for(int window = 0; window < oldestWindow; window++) {
            double currY = spectrogramHeightInPixels - pixelHeightForBin;
//                if (counters[window] >= windowOverlapCountForOneWindow) {
//                    newestWindow = window;
//                    if (!oldestWindowSet) {
//                        oldestWindowSet = true;
//                        newOldestWindow = window;
//                    }
////TODO:System.exit(currWindow);
            currX = drawOneWindowInSpectrogram(currY, counters, windowsOverlaps, lastWindowParts, currentlyCalculatedMeasures,
                    oldestWindowSet, newOldestWindow, currWindow, pixelHeightForBin, binCount,
                    windowSize, logarithmBase, g, currX, pixelWidthForWindow, lastWindowReminder,
                    tmpArrs, windowOverlapCountForOneWindow, windowsPerPixel);
//                }
//            }

            System.out.println("counter0\t" + counters[0] + "\t" + lastWindowParts[0] + "\t" + windowsOverlaps[0]);
            System.out.println("counter1\t" + counters[1] + "\t" + lastWindowParts[1] + "\t" + windowsOverlaps[1]);
            System.out.println();



//                if(counters[window] >= windowsOverlaps[window]) {
//                    if(!oldestWindowSet) {
//                        oldestWindowSet = true;
//                        newOldestWindow = window;
//                    }
//                    System.out.println("COUNTER:\t" + currWindow + "\t" + window);
//                    System.out.println(counters[1] + "\t" + windowsOverlaps[1] + "\t" +  lastWindowParts[1]);
//                    for (int bin = 0; bin < binCount; bin++, currY -= pixelHeightForBin) {
//                        // So the max is 1, but I am not really sure if I shouldn't do something further, like multiply it by some factor so the maximum is == 1.
//                        // If I don't do anything then the maximum is reached only if the wave is constant wave at 1.
//                        double val;
//                        val = currentlyCalculatedMeasures[bin][currWindow % currentlyCalculatedMeasures[bin].length];
//                        val = currentlyCalculatedMeasures[bin][window];     // TODO:
//                        // windowSize + 1 because the max value is raised by +1 for each fftMeasures, it is because of the log,
//                        // so now bin with min energy has value of log(1) == 0
//                        //TODO:DEBUGSystem.out.println(windowSize * 2); System.exit(1);
//                        double windowSizeLog = Math.log(windowSize + 1) / Math.log(logarithmBase);
//
//                        System.out.println(val);
//                        System.out.println(windowSizeLog);
//                        val /= windowSizeLog;
//                        //val /= 2;
//                        //val /= (windowSize / 2);        // TODO:
//                        //val *= 300000;
//                        //if(val > 1) { System.out.println("Bigger than 1:\t" + val + "\t" + currWindow + "\t" + bin); System.exit(555); }
//// TODO:                if(currX > spectrogramStart) break TODO;
//                        Color c;
//                        c = Color.getHSBColor(1 - (float) val, (float) val, (float) val);
////                c = Color.getHSBColor(1,(float)val,(float)val);
////                c = Color.getHSBColor(-1+(float)val, (float)-val, (float)-val);
//                        //val = Double.NEGATIVE_INFINITY;
//                        //c = Color.getHSBColor((float)(1-val),(float)val,(float)val);
////                System.out.println(val + "\t" + currentlyCalculatedMeasures[bin].length);
//                        g.setColor(c);
//                        //g.setColor(Color.red);// currY = spectrogramHeightInPixels - pixelHeightForBin;
//                        // TODO: Mozna Math.ceil
//                        g.fillRect((int) currX, (int) currY, (int) Math.ceil(pixelWidthForWindow), (int) Math.ceil(pixelHeightForBin));        // TODO: asi jeste udelat i pro tu y souradnici (tj ty biny) to spojovani do 1 pixelu, kdyz to je moc velky
//                        //TODO: DEBUGSystem.out.println("Drawn rectangle:\t" + currX + "\t" + currY + "\t" + pixelWidthForWindow + "\t" + pixelHeightForBin);
////
////                System.out.println(currX + "\t" + currY + "\t" + i + "\t" + endIndex);
////                System.out.println(spectrogramWidthInPixels + "\t" + spectrogramHeightInPixels);
////                System.out.println();
///
//
//                        // It was used and is now the latest unused, now there are 2 cases:
//                        // a) lastWindowReminder == 0 then it is set to 0 and will be set to 0 again after passing windowOverlapCountForOneWindowInt windows
//                        // b) lastWindowReminder != 0 then it is set to the reminder of the window which wasn't used before, which is original fftMeasures[bin] * (1-reminder) to the result add 1 and take logarithm
//                        if (lastWindowReminder == 0) {
//                            currentlyCalculatedMeasures[bin][window] = 0;
//                        } else {
//                            if (lastWindowParts[currWindow % windowsOverlaps.length] != 0) {
//                                currentlyCalculatedMeasures[bin][window] = tmpArrs[window][1][bin] / windowOverlapCountForOneWindow;
////                        currentlyCalculatedMeasures[bin][currWindow % currentlyCalculatedMeasures[bin].length] = 0;
//                            } else {
//                                currentlyCalculatedMeasures[bin][window] = 0;
//                            }
//                        }
//                    }
//
//                    if (lastWindowReminder != 0) {
//                        int mod = window % windowsOverlaps.length;          // TODO:
//                        if (windowsOverlaps[0] == 0) {
//                            windowsOverlaps[0] = windowOverlapCountForOneWindow;
//                            lastWindowParts[0] = lastWindowReminder;
//                        } else {
//                            if (mod == 0) {
//                                windowsOverlaps[0] = (int) Math.ceil(windowsOverlaps[windowsOverlaps.length - 1]) - windowsOverlaps[windowsOverlaps.length - 1];
//                                windowsOverlaps[0] = windowOverlapCountForOneWindow - windowsOverlaps[0];
//                            } else {
//                                System.out.println("OVERLAP:\t" + windowsOverlaps[mod] + "\t" + windowsOverlaps[mod - 1] + "\t" + windowOverlapCountForOneWindow);
//                                windowsOverlaps[mod] = (int) Math.ceil(windowsOverlaps[mod - 1]) - windowsOverlaps[mod - 1];
//                                windowsOverlaps[mod] = windowOverlapCountForOneWindow - windowsOverlaps[mod];
//                                System.out.println("OVERLAP:\t" + windowsOverlaps[mod]);
//                            }
//                            lastWindowParts[mod] = windowsOverlaps[mod] % 1;
//                        }
//                    }
//
//
//
//                    if (lastWindowReminder == 0) {
//                        counters[window] = 0;
//                    } else {
//                        if (lastWindowParts[currWindow % windowsOverlaps.length] != 0) {
//                            counters[window] = lastWindowParts[window];
//                            //counters[window] = 0;
//                        } else {
//                            counters[window] = 0;
//                        }
//                    }
//
//
//
//                    currX += pixelWidthForWindow;
//                }
//            }
        }



        // Draw the measure reference
        g.setColor(Color.white);
        int startXReference = spectrogramStart + spectrogramWidthInPixels;     // TODO: V tehle jmenech promennych je bordel

        // TODO: bud to mit oddeleny jen bilou carou ... to je to zakomentovany nebo tam mit proste bilej obdelnik to je to nezakomentovany ... jedna se jen o tyhle 3 nasledujici radky
        // TODO: +20 kdyz chci tu carku
        // TODO: Ten ctverec je lepsi ... podle me
        //int startXReference = spectrogramStart + spectrogramWidthInPixels + 20;
//        g.drawLine(startXReference, 0, startXReference, spectrogramHeightInPixels);
        g.fillRect(startXReference, 0, spectrogramWidthInPixelsWithReferenceAndHzLabels, spectrogramHeightInPixels);

        double pixelSkipDouble = 1.0 / spectrogramHeightInPixels;
        int x = startXReference + 20;
        int width = 60;
        int height = 1;
        double colorVal = 0;
        for(int y = spectrogramHeightInPixels; y >= 0; y--, colorVal += pixelSkipDouble) {
            Color c = Color.getHSBColor(1 - (float) colorVal, (float) colorVal, (float) colorVal);
            g.setColor(c);
            g.fillRect(x, y, width, height);
            System.out.println("test\t" + x + "\t" + y + "\t" + pixelSkipDouble + "\t" + colorVal + "\t" + (colorVal + pixelSkipDouble));
        }


        // Draw frequency labels
        int n = 8;      // take every nth bin
        double maxTextHeight = n * pixelHeightForBin;
        String[] binFreqs = Program.getFreqs(binCount, freqJump, 0, n, 2);
        SwingUtilities.findMaxFontSize(24, g, binFreqs, spectrogramStart - 15, (int)maxTextHeight, 1);
        FontMetrics fontMetrics = g.getFontMetrics();

        g.setColor(Color.white);
        g.fillRect(0, 0, spectrogramStart, spectrogramHeightInPixels);
        int lineXSmall = spectrogramStart - 5;
        int lineXBig = lineXSmall - 5;
        g.drawLine(lineXSmall, 0, lineXSmall, spectrogramHeightInPixels);
        g.setColor(Color.black);

        // Draw the small lines
        double currY = 0;
        double midY = pixelHeightForBin / 2;
        for(int i = 0; i < binCount; i++, currY += pixelHeightForBin, midY += pixelHeightForBin) {
            if(i % n!= 0) {
                g.drawLine(lineXSmall, (int) midY, spectrogramStart, (int) midY);   // TODO: Zase mozna Math.ceil
            }
        }

        int textHeight = fontMetrics.getHeight();
        double freeSpace = maxTextHeight - textHeight;
        freeSpace /= 2;
        // Draw the big lines + text
        currY = 0;
        midY = pixelHeightForBin / 2;
        for(int bin = binFreqs.length - 1; bin >= 0; bin--, currY += maxTextHeight, midY += maxTextHeight)
        {
            System.out.println(bin + "\t"  + currY);
            g.drawLine(lineXBig, (int)midY, spectrogramStart, (int)midY);   // TODO: Zase mozna Math.ceil
            int textLen = fontMetrics.stringWidth(binFreqs[bin]);
            if(bin == binFreqs.length - 1) {        // Special cases at top and at bottom (0Hz and nyquist freq)
                g.drawString(binFreqs[bin], spectrogramStart - textLen - 15, 15);   // TODO: y == 15 because of the top ledge (the ledge with cross)
            }
            else if(bin == 0) {
                g.drawString(binFreqs[bin], spectrogramStart - textLen - 15, spectrogramHeightInPixels);
            }
            else {
                g.drawString(binFreqs[bin], spectrogramStart - textLen - 15, (int)(currY + freeSpace));    // TODO: Zase mozna Math.ceil
            }
        }


        // Draw KHz to the top left
        g.drawString("KHz", 0, (int)(spectrogramHeightInPixels - maxTextHeight / 2));      // TODO: Bude tam obcas prekryv

        return spectrogram;
    }


    private static void resetDoubleArr(double[][] arr) {
        for(int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                arr[i][j] = 0;
            }
        }
    }



    private static double drawOneWindowInSpectrogram(double currY,
                                                     double[] counters, double[] windowsOverlaps, double[] lastWindowParts,
                                                     double[][] currentlyCalculatedMeasures,
                                                     boolean oldestWindowSet, int newOldestWindow,
                                                     int currWindow, double pixelHeightForBin, int binCount,
                                                     int windowSize, int logarithmBase, Graphics g,
                                                     double currX, double pixelWidthForWindow, double lastWindowReminder,
                                                     double[][][] tmpArrs, double windowOverlapCountForOneWindow, double windowsPerPixel) {
        System.out.println(counters[1] + "\t" + windowsOverlaps[1] + "\t" + lastWindowParts[1]);
        for (int bin = 0; bin < binCount; bin++, currY -= pixelHeightForBin) {
            // So the max is 1, but I am not really sure if I shouldn't do something further, like multiply it by some factor so the maximum is == 1.
            // If I don't do anything then the maximum is reached only if the wave is constant wave at 1.
            double val;
            val = currentlyCalculatedMeasures[bin][currWindow % currentlyCalculatedMeasures[bin].length];
            // windowSize + 1 because the max value is raised by +1 for each fftMeasures, it is because of the log,
            // so now bin with min energy has value of log(1) == 0
            //TODO:DEBUGSystem.out.println(windowSize * 2); System.exit(1);
            double windowSizeLog = Math.log(windowSize + 1) / Math.log(logarithmBase);

            System.out.println("VAL:\t" + currWindow + "\t" + bin + "\t" + val);
            System.out.println(windowSizeLog);
            val /= windowSizeLog;
            //        val /= windowsPerPixel;
            //val /= 2;
            //val /= (windowSize / 2);        // TODO:
            //val *= 300000;
            //if(val > 1) { System.out.println("Bigger than 1:\t" + val + "\t" + currWindow + "\t" + bin); System.exit(555); }
// TODO:                if(currX > spectrogramStart) break TODO;
            Color c;
            c = Color.getHSBColor(1 - (float) val, (float) val, (float) val);
//                c = Color.getHSBColor(1,(float)val,(float)val);
//                c = Color.getHSBColor(-1+(float)val, (float)-val, (float)-val);
            //val = Double.NEGATIVE_INFINITY;
            //c = Color.getHSBColor((float)(1-val),(float)val,(float)val);
//                System.out.println(val + "\t" + currentlyCalculatedMeasures[bin].length);
            g.setColor(c);
            //g.setColor(Color.red);// currY = spectrogramHeightInPixels - pixelHeightForBin;
            // TODO: Mozna Math.ceil
            g.fillRect((int) currX, (int) currY, (int) Math.ceil(pixelWidthForWindow), (int) Math.ceil(pixelHeightForBin));        // TODO: asi jeste udelat i pro tu y souradnici (tj ty biny) to spojovani do 1 pixelu, kdyz to je moc velky
            //TODO: DEBUGSystem.out.println("Drawn rectangle:\t" + currX + "\t" + currY + "\t" + pixelWidthForWindow + "\t" + pixelHeightForBin);
//
//                System.out.println(currX + "\t" + currY + "\t" + i + "\t" + endIndex);
//                System.out.println(spectrogramWidthInPixels + "\t" + spectrogramHeightInPixels);
//                System.out.println();


            // It was used and is now the latest unused, now there are 2 cases:
            // a) lastWindowReminder == 0 then it is set to 0 and will be set to 0 again after passing windowOverlapCountForOneWindowInt windows
            // b) lastWindowReminder != 0 then it is set to the reminder of the window which wasn't used before, which is original fftMeasures[bin] * (1-reminder) to the result add 1 and take logarithm
//            if (lastWindowReminder == 0) {
//                currentlyCalculatedMeasures[bin][window] = 0;
//            } else {
//                if (lastWindowParts[window] != 0) {
//                    System.out.println(":))))\t" + bin + "\t" + window + "\t" + currentlyCalculatedMeasures[bin][window]);
//                    currentlyCalculatedMeasures[bin][window] = tmpArrs[window][1][bin] / windowOverlapCountForOneWindow;
//                    System.out.println(":))))\t" + bin + "\t" + window + "\t" + currentlyCalculatedMeasures[bin][window]);
////                        currentlyCalculatedMeasures[bin][currWindow % currentlyCalculatedMeasures[bin].length] = 0;
//                } else {
//                    currentlyCalculatedMeasures[bin][window] = 0;
//                }
//            }
//        }
//
//
//
//
//        int mod = window % windowsOverlaps.length;          // TODO: neni nutny ted uz, driv pro currWindow bylo, ale ted to delam primo pres window bez mod
//        System.out.println("OVERLAP1:\t" + mod + "\t" + windowsOverlaps[mod] + "\t" + lastWindowParts[mod] + "\t" + counters[mod] + "\t" + ((counters[mod] - 1) + lastWindowParts[mod]));
//        if (lastWindowReminder == 0) {
//            counters[window] = 0;
//        } else {
//            if (lastWindowParts[mod] != 0) {
//                counters[window] = 1 - lastWindowParts[window];
//                //counters[window] = 0;
//            } else {
//                counters[window] = 0;
//            }
//        }
//        System.out.println(mod + "\t" + counters[mod]);
//
//        if (lastWindowReminder != 0) {
//            if (windowsOverlaps[0] == 0) {
//                windowsOverlaps[0] = windowOverlapCountForOneWindow;
//                lastWindowParts[0] = lastWindowReminder;
//            } else {
//                if (mod == 0) {
//                    windowsOverlaps[0] = (int) Math.ceil(windowsOverlaps[windowsOverlaps.length - 1]) - windowsOverlaps[windowsOverlaps.length - 1];
//                    windowsOverlaps[0] = windowOverlapCountForOneWindow - windowsOverlaps[0];
//                } else {
////                    System.out.println("OVERLAP:\t" + windowsOverlaps[mod] + "\t" + windowsOverlaps[mod - 1] + "\t" + windowOverlapCountForOneWindow);
//                    windowsOverlaps[mod] = (int) Math.ceil(windowsOverlaps[mod - 1]) - windowsOverlaps[mod - 1];
//                    windowsOverlaps[mod] = windowOverlapCountForOneWindow - windowsOverlaps[mod];
////                    System.out.println("OVERLAP:\t" + windowsOverlaps[mod]);
//                }
//                lastWindowParts[mod] = windowsOverlaps[mod] % 1;
//            }
//
//            System.out.println("OVERLAP2:\t" + windowsOverlaps[mod] + "\t" + lastWindowParts[mod] + "\t" + ((counters[mod] - 1) + lastWindowParts[mod]));
        }


        currX += pixelWidthForWindow;
        return currX;
    }


    // TODO: Asi je nutny so nasobit jeste predtim logaritmovanim - pred tim to fungovalo protoze jsem bral jakoby prumery, ted uz je neberu, takze dostavam hodnoty co jsou mimo range
    // TODO: To cim to mam delit je vlastne windowsPerPixel * multiplyFactor ... ale kdybych to chtel delit uz predtim tak ty addCurrentMeasures metody musim jakoby presunout pred to logaritmovani a v tamtech metodach jen prictu uz bez deleni
    private static void addCurrentMeasures(double[] song, int currSongIndex, int numberOfChannels, DoubleFFT_1D fft,
                                           double[] fftResult, double[] fftMeasures, double logarithmBase, int currWindow,
                                           double[][] currentlyCalculatedMeasures,
                                           double[] tmpArr, double[] tmpArr2, double multiplyFactor,
                                           double windowOverlapCountForOneWindow, boolean moreWindowsPerPixel,
                                           double[] counters, double[] windowsOverlaps, double[] multiplyFactors,
                                           double[][][] tmpArrs, int indexWhereToPutNotFullValue, double[] arrWithNotFullMeasures) {
        int mod = currWindow % currentlyCalculatedMeasures[0].length;
        // TODO: int mod = currWindow % currentlyCalculatedMeasures[bin].length; takhle to bylo predtim, ale vzhledem k tomu ze to ma stejny rozmery tak to nevadi
//        for(int i = currSongIndex; i <= currSongIndex + fftResult.length; i++) {
//            song[i] = 1;
//        }
        FFT.calculateFFTRealForward(song, currSongIndex, fftResult.length, numberOfChannels, fft, fftResult);         // TODO: Tahle vicekanalova verze se mi vubec nelibi
        FFT.convertResultsOfFFTToRealRealForward(fftResult, fftMeasures);
        System.out.println("TODO:\t" + currentlyCalculatedMeasures[1][1]);
        System.out.println("--------------------------");
        for(int i = 0; i < windowsOverlaps.length; i++) {
            if(currWindow >= windowsOverlaps.length) {      // TODO:
                counters[i]++;
                System.out.println("COUNTERS:\t" + i + "\t" + counters[i] + "\t" + windowsOverlaps[i] + "\t" + multiplyFactors[i]);
            }
            if((multiplyFactors[i] != 0 && currWindow >= windowsOverlaps.length) || moreWindowsPerPixel) {
                System.out.println(i + "\t" + multiplyFactors[i] + "\t" + (multiplyFactors[i] != 0) + "\t" + (currWindow >= windowsOverlaps.length));
                if (counters[i] >= windowOverlapCountForOneWindow) {
                    for(int bin = 0; bin < fftMeasures.length; bin++) {
                        tmpArrs[i][0][bin] = fftMeasures[bin] * multiplyFactors[i];
                        tmpArrs[i][1][bin] = fftMeasures[bin] * (1 - multiplyFactors[i]);
                        tmpArrs[i][0][bin]++;
                        tmpArrs[i][1][bin]++;
                    }

//TODO:
//                    if(currWindow == 9 && i == 1) {
//                        System.out.println("TmpArrs1:\t" + i + "\t" + tmpArrs[i][0][1]);
//                        System.exit(11111);
//                    }
                }
            }
//TODO:
//            System.out.println("TmpArrs0:\t" + i + "\t" + tmpArrs[i][0][1]);
//            if(currWindow == 9 && i == 1) {
//                System.out.println(counters[i] + "\t" + windowOverlapCountForOneWindow + "\t" + (multiplyFactors[i]) +
//                    "\t" + fftMeasures[0] + "\t" + (fftMeasures[0] / windowOverlapCountForOneWindow) +
//                    "\t" + fftMeasures[1] + "\t" + (fftMeasures[1] / windowOverlapCountForOneWindow));
//                System.exit(86666);
//            }
        }


        for(int bin = 0; bin < fftMeasures.length; bin++) {
            if(multiplyFactor != 0 || moreWindowsPerPixel) {
                arrWithNotFullMeasures[bin] = fftMeasures[bin] * multiplyFactor;
                arrWithNotFullMeasures[bin]++;
            }
            if((multiplyFactor != 0 && currWindow >= currentlyCalculatedMeasures[bin].length) || moreWindowsPerPixel) {
                tmpArr[bin] = fftMeasures[bin] * multiplyFactor;
                tmpArr2[bin] = fftMeasures[bin] * (1 - multiplyFactor);
                tmpArr[bin]++;
                tmpArr2[bin]++;
            }

            fftMeasures[bin]++;      // Now the minimum energy is 1, so the minimum log == 0
        }

        double TODOsum = 0;
        for(int i = 0; i < fftMeasures.length; i++) {
            if(fftMeasures[i] > 0) System.out.println("MEASURES:\t" + i +"\t" + fftMeasures[i]);
            TODOsum += fftMeasures[i];
        }
        System.out.println(TODOsum);
        System.out.println(fftMeasures[0]);
        //TODO:System.exit(-666);


        // TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!! Vymazat tmpArrs a vsechny tyhle veci
        System.out.println("TODO:\t" + currentlyCalculatedMeasures[1][1]);
//            for (int window = 0; window < tmpArrs.length; window++) {
//                if((multiplyFactors[window] != 0 && currWindow >= windowsOverlaps.length) || moreWindowsPerPixel) {
//                    if (counters[window] >= windowOverlapCountForOneWindow) {
//                        Program.performOperationOnSamples(tmpArrs[window][0], logarithmBase, ArithmeticOperation.LOG);
//                        Program.performOperationOnSamples(tmpArrs[window][1], logarithmBase, ArithmeticOperation.LOG);
//                    }
//                }
//            }
//TODO:        System.out.println("TmpArrs1:\t" + tmpArrs[1][0][1]);

// TODO: PROGRAMO - REDOING operations
        ArithmeticOperation.performOperationOnSamples(fftMeasures, fftMeasures, 0, 0,
                fftMeasures.length, logarithmBase, ArithmeticOperation.LOG);
        //Program.operationOnSamplesByReference(fftMeasures, logarithmBase, ArithmeticOperation.LOG);
        if((multiplyFactor != 0 && currWindow >= currentlyCalculatedMeasures[0].length) || moreWindowsPerPixel) {
            ArithmeticOperation.performOperationOnSamples(tmpArr, tmpArr, 0, 0,
                    tmpArr.length, logarithmBase, ArithmeticOperation.LOG);
            //Program.operationOnSamplesByReference(tmpArr, logarithmBase, ArithmeticOperation.LOG);
            ArithmeticOperation.performOperationOnSamples(tmpArr2, tmpArr2, 0, 0,
                    tmpArr2.length, logarithmBase, ArithmeticOperation.LOG);
            //Program.operationOnSamplesByReference(tmpArr2, logarithmBase, ArithmeticOperation.LOG);
        }

        if(multiplyFactor != 0 || moreWindowsPerPixel) {
            ArithmeticOperation.performOperationOnSamples(arrWithNotFullMeasures, arrWithNotFullMeasures, 0, 0,
                    arrWithNotFullMeasures.length, logarithmBase, ArithmeticOperation.LOG);
            //Program.operationOnSamplesByReference(arrWithNotFullMeasures, logarithmBase, ArithmeticOperation.LOG);
        }
// TODO: PROGRAMO - REDOING operations

        for(int i = 0; i < fftMeasures.length; i++) {
            if(fftMeasures[i] < 0) {
                System.exit(45679);
            }
        }

        System.out.println("....:\t" + currentlyCalculatedMeasures[0][1]);
// TODO: Asi nemusim a jestli musim, tak to je vypocet navic, protoze tam posilam sqrt stejne
        for(int bin = 0; bin < fftMeasures.length; bin++) {
// TODO: Debug print
            if(fftMeasures[bin] > 0) {
                System.out.println(bin + "\t" + fftMeasures[bin]);
            }
            if(moreWindowsPerPixel) {
                addCurrentMeasures(mod, currentlyCalculatedMeasures[bin], fftMeasures[bin], bin, tmpArr, multiplyFactor,
                        windowOverlapCountForOneWindow, counters, windowsOverlaps, tmpArrs, multiplyFactors,
                        indexWhereToPutNotFullValue, arrWithNotFullMeasures[bin]);
            }
            else {
                if (currWindow < currentlyCalculatedMeasures[bin].length) {
                    addCurrentMeasures(currWindow, currentlyCalculatedMeasures[bin], fftMeasures[bin], windowOverlapCountForOneWindow,
                            indexWhereToPutNotFullValue, arrWithNotFullMeasures[bin], multiplyFactor);

                } else {
                    addCurrentMeasures(mod, currentlyCalculatedMeasures[bin], fftMeasures[bin], bin, tmpArr, multiplyFactor,
                            windowOverlapCountForOneWindow, counters, windowsOverlaps, tmpArrs, multiplyFactors,
                            indexWhereToPutNotFullValue, arrWithNotFullMeasures[bin]);
                }
            }


// TODO:
//            double res = currentlyCalculatedMeasures[bin][1];
//            if(res > 10) {
//                System.out.println(".....\t" + bin + "\t" + arrWithNotFullMeasures[bin] +
//                    "\t" + windowOverlapCountForOneWindow + "\t" + (arrWithNotFullMeasures[bin] / windowOverlapCountForOneWindow));
//                System.exit(-123456);
//            }


            System.out.println("bin :]\t" + bin);
//            if(currWindow == 9) {
//                System.out.println(counters[1] + "\t" + windowOverlapCountForOneWindow + "\t" + (multiplyFactors[1]) +
//                    "\t" + fftMeasures[0] + "\t" + (fftMeasures[0] / windowOverlapCountForOneWindow) + "\t" +
//                    Double.isNaN(currentlyCalculatedMeasures[bin][1]));
//                if(Double.isNaN(currentlyCalculatedMeasures[bin][1]))                System.exit(8743100);
//            }
// TODO: DEBUG
//            for(int i = 0; i < currentlyCalculatedMeasures[bin].length; i++) {
//                System.out.println("currMeasures:\t" + i + ":\t" + bin + "\t" + currentlyCalculatedMeasures[bin][i] +
//                    "\t" + tmpArrs[i][0][bin] + "\t" + fftMeasures[bin] + "\t" + multiplyFactors[1] + "\t" +
//                    (fftMeasures[bin] * multiplyFactors[1]) + "\t" + windowOverlapCountForOneWindow + "\t" +
//                    (tmpArrs[i][0][bin] / windowOverlapCountForOneWindow));
//
//                System.out.println(currWindow);
//                if(currentlyCalculatedMeasures[bin][i] < 0) {
//                    System.exit(123456);
//                }
//                if(Double.isNaN(currentlyCalculatedMeasures[bin][i]) || Double.isInfinite(currentlyCalculatedMeasures[bin][i])) System.exit(98989);
//
//               // if(Double.isNaN(tmpArrs[i][0][bin])) System.exit(989890);
//            }
        }
// TODO: DEBUG        System.out.println("....\t" + currentlyCalculatedMeasures[0][1]);

    }



    private static void addCurrentMeasures(int currWindow, double[] currentlyCalculatedMeasures,
                                           double fftMeasure, double numberOfWindowParts,
                                           int indexWhereToPutNotFullValue, double notFullMeasure, double multiplyFactor) {
        int i;
        for(i = 0; i < currWindow; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / numberOfWindowParts;
        }

        for(; i < currentlyCalculatedMeasures.length - 1; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / (i+1);        // TODO: To se mi nezda
        }
        currentlyCalculatedMeasures[i] += fftMeasure / numberOfWindowParts;
        if(multiplyFactor != 0) {
            currentlyCalculatedMeasures[indexWhereToPutNotFullValue] = notFullMeasure / numberOfWindowParts;
            currentlyCalculatedMeasures[indexWhereToPutNotFullValue] = multiplyFactor * fftMeasure / numberOfWindowParts;
        }

// TODO:
//        System.out.println("LOL:\t" + currentlyCalculatedMeasures[1]);
//        if(currWindow == 1) System.exit(indexWhereToPutNotFullValue);

//        if(currWindow == 0 && multiplyFactor != 0) {
//            currentlyCalculatedMeasures[currentlyCalculatedMeasures.length - 1] = notFullMeasure / numberOfWindowParts;
//        }
    }

    private static void addCurrentMeasures(int currWindowMod, double[] currentlyCalculatedMeasures,
                                           double fftMeasure, int bin, double[] tmpArr,
                                           double multiplyFactor, double windowOverlapCountForOneWindow,
                                           double[] counters, double[] numberOfWindowParts,
                                           double[][][]tmpArrs, double[] multiplyFactors,
                                           int indexWhereToPutNotFullValue, double notFullMeasure) {
        if(bin == 0) {
            System.out.println(bin + "\t" + fftMeasure + "\t:::::\t" + notFullMeasure);
            for(int i = 0; i < currentlyCalculatedMeasures.length; i++) {
                if (currentlyCalculatedMeasures[i] > 11) {
                    System.out.println("OVER:\t" + i + "\t" + currentlyCalculatedMeasures[i]);
                }
            }
        }


        int i = 0;
        for(; i < indexWhereToPutNotFullValue; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / windowOverlapCountForOneWindow;
        }

        if(multiplyFactor != 0) {
            currentlyCalculatedMeasures[i] = notFullMeasure / windowOverlapCountForOneWindow;
            currentlyCalculatedMeasures[i] = multiplyFactor * fftMeasure / windowOverlapCountForOneWindow;
            i++;
        }

        for(; i < currentlyCalculatedMeasures.length; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / windowOverlapCountForOneWindow;
        }

//System.out.println("IN1:\t" + multiplyFactors[1]);
//        for(; i < currentlyCalculatedMeasures.length; i++) {
//            System.out.println("IN2:\t" + i + "\t" + bin + "\t" + currentlyCalculatedMeasures[i]);
//            if(multiplyFactors[i] == 0) {
//                currentlyCalculatedMeasures[i] += fftMeasure / windowOverlapCountForOneWindow;
//                System.out.println("IN:\t" + i + "\t" + bin + "\t" + currentlyCalculatedMeasures[i] + "\t" + fftMeasure);
//            }
//            else {
//                if (counters[i] >= windowOverlapCountForOneWindow) {
//                    currentlyCalculatedMeasures[i] += tmpArrs[i][0][bin] / windowOverlapCountForOneWindow;
//                } else {
//                    currentlyCalculatedMeasures[i] += fftMeasure / windowOverlapCountForOneWindow;
//                }
//            }
//        }
    }







    private static void addCurrentMeasuresMoreWindowsPerPixel(double[] song, int currSongIndex, int numberOfChannels, DoubleFFT_1D fft,
                                                              double[] fftResult, double[] fftMeasures, double logarithmBase, int currWindow,
                                                              double[][] currentlyCalculatedMeasures,
                                                              double[] tmpArr, double[] tmpArr2, double multiplyFactor,
                                                              double windowOverlapCountForOneWindow, boolean moreWindowsPerPixel,
                                                              double[] counters, double[] windowsOverlaps, double[] multiplyFactors,
                                                              double[][][] tmpArrs, int indexWhereToPutNotFullValue, double[] arrWithNotFullMeasures) {
        int mod = currWindow % currentlyCalculatedMeasures[0].length;
        // TODO: int mod = currWindow % currentlyCalculatedMeasures[bin].length; takhle to bylo predtim, ale vzhledem k tomu ze to ma stejny rozmery tak to nevadi
        for(int i = currSongIndex; i <= currSongIndex + fftResult.length; i++) {
            song[i] = 1;
        }
        FFT.calculateFFTRealForward(song, currSongIndex, fftResult.length, numberOfChannels, fft, fftResult);         // TODO: Tahle vicekanalova verze se mi vubec nelibi
        FFT.convertResultsOfFFTToRealRealForward(fftResult, fftMeasures);
        System.out.println("TODO:\t" + currentlyCalculatedMeasures[1][1]);
        System.out.println("--------------------------");
        for(int i = 0; i < windowsOverlaps.length; i++) {
            if(currWindow >= windowsOverlaps.length) {      // TODO:
                counters[i]++;
                System.out.println("COUNTERS:\t" + i + "\t" + counters[i] + "\t" + windowsOverlaps[i] + "\t" + multiplyFactors[i]);
            }
            if((multiplyFactors[i] != 0 && currWindow >= windowsOverlaps.length) || moreWindowsPerPixel) {
                System.out.println(i + "\t" + multiplyFactors[i] + "\t" + (multiplyFactors[i] != 0) + "\t" + (currWindow >= windowsOverlaps.length));
                if (counters[i] >= windowOverlapCountForOneWindow) {
                    for(int bin = 0; bin < fftMeasures.length; bin++) {
                        tmpArrs[i][0][bin] = fftMeasures[bin] * multiplyFactors[i];
                        tmpArrs[i][1][bin] = fftMeasures[bin] * (1 - multiplyFactors[i]);
                        tmpArrs[i][0][bin]++;
                        tmpArrs[i][1][bin]++;
                    }
                }
            }
        }


        for(int bin = 0; bin < fftMeasures.length; bin++) {
            if(multiplyFactor != 0 || moreWindowsPerPixel) {
                arrWithNotFullMeasures[bin] = fftMeasures[bin] * multiplyFactor;
                arrWithNotFullMeasures[bin]++;
            }
            if((multiplyFactor != 0 && currWindow >= currentlyCalculatedMeasures[bin].length) || moreWindowsPerPixel) {
                tmpArr[bin] = fftMeasures[bin] * multiplyFactor;
                tmpArr2[bin] = fftMeasures[bin] * (1 - multiplyFactor);
                tmpArr[bin]++;
                tmpArr2[bin]++;
            }

            fftMeasures[bin]++;      // Now the minimum energy is 1, so the minimum log == 0
        }

        double TODOsum = 0;
        for(int i = 0; i < fftMeasures.length; i++) {
            if(fftMeasures[i] > 0) System.out.println("MEASURES:\t" + i +"\t" + fftMeasures[i]);
            TODOsum += fftMeasures[i];
        }
        System.out.println(TODOsum);
        System.out.println(fftMeasures[0]);
        //TODO:System.exit(-666);

// TODO: PROGRAMO - REDOING operations
        System.out.println("TODO:\t" + currentlyCalculatedMeasures[1][1]);
        for (int window = 0; window < tmpArrs.length; window++) {
            if((multiplyFactors[window] != 0 && currWindow >= windowsOverlaps.length) || moreWindowsPerPixel) {
                if (counters[window] >= windowOverlapCountForOneWindow) {
                    ArithmeticOperation.performOperationOnSamples(tmpArrs[window][0], tmpArrs[window][0], 0, 0,
                            tmpArrs[window][0].length, logarithmBase, ArithmeticOperation.LOG);
                    //Program.operationOnSamplesByReference(tmpArrs[window][0], logarithmBase, ArithmeticOperation.LOG);
                    ArithmeticOperation.performOperationOnSamples(tmpArrs[window][1], tmpArrs[window][1], 0, 0,
                            tmpArrs[window][1].length, logarithmBase, ArithmeticOperation.LOG);
                    //Program.operationOnSamplesByReference(tmpArrs[window][1], logarithmBase, ArithmeticOperation.LOG);
                }
            }
        }
//TODO:        System.out.println("TmpArrs1:\t" + tmpArrs[1][0][1]);

        ArithmeticOperation.performOperationOnSamples(fftMeasures, fftMeasures, 0, 0,
                fftMeasures.length, logarithmBase, ArithmeticOperation.LOG);
        //Program.operationOnSamplesByReference(fftMeasures, logarithmBase, ArithmeticOperation.LOG);
        if((multiplyFactor != 0 && currWindow >= currentlyCalculatedMeasures[0].length) || moreWindowsPerPixel) {
            ArithmeticOperation.performOperationOnSamples(tmpArr, tmpArr, 0, 0,
                    tmpArr.length, logarithmBase, ArithmeticOperation.LOG);
            //Program.operationOnSamplesByReference(tmpArr, logarithmBase, ArithmeticOperation.LOG);
            ArithmeticOperation.performOperationOnSamples(tmpArr2, tmpArr2, 0, 0,
                    tmpArr2.length, logarithmBase, ArithmeticOperation.LOG);
            //Program.operationOnSamplesByReference(tmpArr2, logarithmBase, ArithmeticOperation.LOG);
        }

        if(multiplyFactor != 0 || moreWindowsPerPixel) {
            ArithmeticOperation.performOperationOnSamples(arrWithNotFullMeasures, arrWithNotFullMeasures, 0, 0,
                    arrWithNotFullMeasures.length, logarithmBase, ArithmeticOperation.LOG);
            //Program.operationOnSamplesByReference(arrWithNotFullMeasures, logarithmBase, ArithmeticOperation.LOG);
        }
// TODO: PROGRAMO - REDOING operations

        for(int i = 0; i < fftMeasures.length; i++) {
            if(fftMeasures[i] < 0) {
                System.exit(45679);
            }
        }

        System.out.println("....:\t" + currentlyCalculatedMeasures[0][1]);
// TODO: Asi nemusim a jestli musim, tak to je vypocet navic, protoze tam posilam sqrt stejne
        for(int bin = 0; bin < fftMeasures.length; bin++) {
// TODO: Debug print
            if(fftMeasures[bin] > 0) {
                System.out.println(bin + "\t" + fftMeasures[bin]);
            }
            if(moreWindowsPerPixel) {
                addCurrentMeasures(mod, currentlyCalculatedMeasures[bin], fftMeasures[bin], bin, tmpArr, multiplyFactor,
                        windowOverlapCountForOneWindow, counters, windowsOverlaps, tmpArrs, multiplyFactors,
                        indexWhereToPutNotFullValue, arrWithNotFullMeasures[bin]);
            }
            else {
                if (currWindow < currentlyCalculatedMeasures[bin].length) {
                    addCurrentMeasures(currWindow, currentlyCalculatedMeasures[bin], fftMeasures[bin], windowOverlapCountForOneWindow,
                            indexWhereToPutNotFullValue, arrWithNotFullMeasures[bin], multiplyFactor);

                } else {
                    addCurrentMeasures(mod, currentlyCalculatedMeasures[bin], fftMeasures[bin], bin, tmpArr, multiplyFactor,
                            windowOverlapCountForOneWindow, counters, windowsOverlaps, tmpArrs, multiplyFactors,
                            indexWhereToPutNotFullValue, arrWithNotFullMeasures[bin]);
                }
            }




            System.out.println("bin :]\t" + bin);
            for(int i = 0; i < currentlyCalculatedMeasures[bin].length; i++) {
                System.out.println("currMeasures:\t" + i + ":\t" + bin + "\t" + currentlyCalculatedMeasures[bin][i] +
                        "\t" + tmpArrs[i][0][bin] + "\t" + fftMeasures[bin] + "\t" + multiplyFactors[1] + "\t" +
                        (fftMeasures[bin] * multiplyFactors[1]) + "\t" + windowOverlapCountForOneWindow + "\t" +
                        (tmpArrs[i][0][bin] / windowOverlapCountForOneWindow));

                System.out.println(currWindow);
                if(currentlyCalculatedMeasures[bin][i] < 0) {
                    System.exit(123456);
                }
                if(Double.isNaN(currentlyCalculatedMeasures[bin][i]) || Double.isInfinite(currentlyCalculatedMeasures[bin][i])) System.exit(98989);

                // if(Double.isNaN(tmpArrs[i][0][bin])) System.exit(989890);
            }
        }
        System.out.println("....\t" + currentlyCalculatedMeasures[0][1]);
    }




    private static void addCurrentMeasuresMoreWindowsPerPixel(double[] currentlyCalculatedMeasures,
                                                              double fftMeasure, int bin,
                                                              double multiplyFactor, double windowOverlapCountForOneWindow, double[] multiplyFactors,
                                                              int indexWhereToPutNotFullValue, double notFullMeasure) {
        if(bin == 0) {
            System.out.println(bin + "\t" + fftMeasure + "\t:::::\t" + notFullMeasure);
            for(int i = 0; i < currentlyCalculatedMeasures.length; i++) {
                if (currentlyCalculatedMeasures[i] > 11) {
                    System.out.println("OVER:\t" + i + "\t" + currentlyCalculatedMeasures[i]);
                }
            }
        }


        int i = 0;
        for(; i < indexWhereToPutNotFullValue; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / windowOverlapCountForOneWindow;
        }

        if(multiplyFactor != 0) {
            currentlyCalculatedMeasures[i] = notFullMeasure / windowOverlapCountForOneWindow;
            currentlyCalculatedMeasures[i] = multiplyFactor * fftMeasure / windowOverlapCountForOneWindow;
            i++;
        }

        for(; i < currentlyCalculatedMeasures.length; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / windowOverlapCountForOneWindow;
        }
    }


    private static void addCurrentMeasuresMoreWindowsPerPixel(int currWindow,
                                                              double[] currentlyCalculatedMeasures,
                                                              double[] currentlyCalculatedMeasuresWithOtherParts,
                                                              double fftMeasure, double numberOfWindowParts,
                                                              int indexWhereToPutNotFullValue, double notFullMeasure,
                                                              double multiplyFactor) {
        int i;
        for(i = 0; i < currWindow; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / numberOfWindowParts;
        }

        for(; i < currentlyCalculatedMeasures.length - 1; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / (i+1);        // TODO: To se mi nezda
        }
        currentlyCalculatedMeasures[i] += fftMeasure / numberOfWindowParts;
        if(multiplyFactor != 0) {
            currentlyCalculatedMeasures[indexWhereToPutNotFullValue] = notFullMeasure / numberOfWindowParts;
            currentlyCalculatedMeasures[indexWhereToPutNotFullValue] = multiplyFactor * fftMeasure / numberOfWindowParts;
        }
    }
}
