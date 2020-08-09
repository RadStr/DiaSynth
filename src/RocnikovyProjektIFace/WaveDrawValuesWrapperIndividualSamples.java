package RocnikovyProjektIFace;

import RocnikovyProjektIFace.StaticDrawMethods.StaticDrawMethodsClass;
import Rocnikovy_Projekt.ProgramTest;

import java.awt.*;

public class WaveDrawValuesWrapperIndividualSamples extends WaveDrawValuesWrapperAbstract {
    /**
     *
     * @param leftPixel is the left visible pixel. but in sense of whole wave
     * @param newVisibleWidth
     * @param totalWaveWidthInPixels
     * @param startIndexInValues
     * @param valueCount
     * @param windowCountToTheRight
     * @param mainWaveClass
     */
    public WaveDrawValuesWrapperIndividualSamples(int leftPixel, int newVisibleWidth, int totalWaveWidthInPixels, int startIndexInValues, int valueCount,
                                                  int windowCountToTheRight, CommunicationWithWaveValuesPanelIFace mainWaveClass) {
        super(newVisibleWidth, totalWaveWidthInPixels, startIndexInValues, valueCount, windowCountToTheRight, mainWaveClass);
        pixelMovement = 0;
        this.leftPixel = leftPixel;
        firstSamplePixel = leftPixel;
        //setFirstSamplePixel();
    }

    @Override
    public void performZoom(int newStartIndex, int newTotalWidth, int newTotalValuesCount) {
        waveResize(visibleWidth, newTotalWidth, newStartIndex, newTotalValuesCount);
    }

    @Override
    public void waveResize(int newVisibleWidth, int totalWaveWidthInPixels, int startIndexInValues, int valueCount) {
        visibleWidth = newVisibleWidth;
        pixelDifferenceBetweenSamples = mainWaveClass.calculatePixelDifferenceBetweenSamples(totalWaveWidthInPixels);
        int w = (int) (visibleWidth / pixelDifferenceBetweenSamples);
        w++;           // + 1 because I have to add the 1 on the left for example 5 /5 == 1 but it contains 2 one on end and one start
        windowBufferDouble = new WindowBufferDouble(windowCountToTheRight, w, startIndexInValues, valueCount, this);

        leftPixel = mainWaveClass.getCurrentScroll();
        setFirstSamplePixel();
        fillWholeBuffer(startIndexInValues);

//        // Not the most effective way, but it works and is simple to understand, that's what matters I move it to 0 and the back to correct position
//        //updatePixelMovement(-leftPixel);
//        //updatePixelMovement(mainWaveClass.getCurrentScroll());
//        updatePixelMovement(-leftPixel + mainWaveClass.getCurrentScroll());
//        ProgramTest.debugPrint("wave resize", mainWaveClass.getCurrentScroll());



// TODO: Old - can delete
        //windowBufferDouble = new WindowBufferDouble(WINDOW_COUNT_TO_THE_RIGHT, newVisibleWidth, leftVisiblePixel, totalWaveWidthInPixels);
// TODO: newer - can delete
//            getDrawValues();        // TODO: Volam na tride co se stara o to cachovani
//            windowBufferDouble.fillBufferWithValuesToDraw();
    }

    @Override
    public int getStartIndex() {
        return windowBufferDouble.getStartIndex();
    }


    private double pixelDifferenceBetweenSamples;
    /**
     * Is the left visible pixel, but in sense of the whole wave.
     */
    private int leftPixel;

    /**
     * Is the pixel of the first sample in the sense of whole wave
     */
    private double firstSamplePixel;
    private void setFirstSamplePixel() {
//        firstSamplePixel = pixelDifferenceBetweenSamples * windowBufferDouble.getTotalIndex();
//        ProgramTest.debugPrint("setFirstSamplePixel", windowBufferDouble.getTotalIndex(), mainWaveClass.getCurrentScroll());

//        firstSamplePixel = windowBufferDouble.getTotalIndex();

        firstSamplePixel = leftPixel;
        double mod = leftPixel % pixelDifferenceBetweenSamples;
        if(mod != 0) {
            firstSamplePixel += (pixelDifferenceBetweenSamples - mod);
            //firstSamplePixel -= mod;
        }
//        firstSamplePixel = (int)(leftPixel / pixelDifferenceBetweenSamples);
////        ProgramTest.debugPrint("setFirstSamplePixel", mainWaveClass.getCurrentScroll() / pixelDifferenceBetweenSamples,
////            (mainWaveClass.getCurrentScroll() + 16) / pixelDifferenceBetweenSamples);
//        firstSamplePixel *= pixelDifferenceBetweenSamples;
    }

    private boolean isFirst = true;
    private int pixelMovement;
//    public void updatePixelMovement(int update) {
////        ProgramTest.debugPrint("updatePixelMovement", mainWaveClass.getCurrentScroll(), leftPixel, leftPixel + update);
//        pixelMovement += update;
//        leftPixel += update;
//        setFirstSamplePixel();
//        int sampleMovement = (int)(pixelMovement / pixelDifferenceBetweenSamples);
//        if(sampleMovement != 0) {
//            pixelMovement %= pixelDifferenceBetweenSamples;
//            windowBufferDouble.updateStartIndex(sampleMovement, mainWaveClass.getCurrentScroll(), mainWaveClass.getMaxScroll());
//        }
//    }


//    public void updatePixelMovement(int update) {
////        ProgramTest.debugPrint("updatePixelMovement", mainWaveClass.getCurrentScroll(), leftPixel, leftPixel + update);
//        leftPixel += update;
//
//        double oldFirstSamplePixel = firstSamplePixel;
//        setFirstSamplePixel();
//        int change = ((int)(firstSamplePixel - oldFirstSamplePixel) / (int)pixelDifferenceBetweenSamples);
//        if(change != 0) {
////            if(((firstSamplePixel - oldFirstSamplePixel) / pixelDifferenceBetweenSamples) != change) {
////                System.exit(1455);
////            }
//            ProgramTest.debugPrint("change", change);
//        }
//        if(change != 0) {
//            windowBufferDouble.updateStartIndex(change, mainWaveClass.getCurrentScroll(), mainWaveClass.getMaxScroll());
//        }
//        else if (leftPixel - update == 0 && leftPixel > 0) {
//            firstSamplePixel = pixelDifferenceBetweenSamples;
//            windowBufferDouble.updateStartIndex(1, mainWaveClass.getCurrentScroll(), mainWaveClass.getMaxScroll());
//        }
//    }

//        public void updatePixelMovement(int update) {
////        ProgramTest.debugPrint("updatePixelMovement", mainWaveClass.getCurrentScroll(), leftPixel, leftPixel + update);
//        pixelMovement += update;
//        leftPixel += update;
//        setFirstSamplePixel();
//        int sampleMovement = (int)(pixelMovement / pixelDifferenceBetweenSamples);
//        if(sampleMovement != 0) {
//            pixelMovement %= pixelDifferenceBetweenSamples;
//            windowBufferDouble.updateStartIndex(sampleMovement, mainWaveClass.getCurrentScroll(), mainWaveClass.getMaxScroll());
//        }
//    }


    private final double tolerance = 0.1;

    private int total = 0;

    public void updatePixelMovement(int update) {
        total += update;

//        ProgramTest.debugPrint("updatePixelMovement", mainWaveClass.getCurrentScroll(), leftPixel, leftPixel + update);
        leftPixel += update;

        double oldFirstSamplePixel = firstSamplePixel;
        setFirstSamplePixel();
        double change = (firstSamplePixel - oldFirstSamplePixel) / pixelDifferenceBetweenSamples;
        int changeInt = (int)Math.round(change);
//        int changeInt = (int)change;
        double dif = change - changeInt;
//
//        if(dif < tolerance && dif > 0) {
//            changeInt++;
//        }

        ProgramTest.debugPrint("change", change, (firstSamplePixel - oldFirstSamplePixel) / pixelDifferenceBetweenSamples, dif,
            total, leftPixel, oldFirstSamplePixel, firstSamplePixel);

//        if(change != 0) {
//            if(((firstSamplePixel - oldFirstSamplePixel) / pixelDifferenceBetweenSamples) != change) {
//                ProgramTest.debugPrint("change", change, (firstSamplePixel - oldFirstSamplePixel) / pixelDifferenceBetweenSamples);
//                System.exit(1455);
//            }
//        }
        if(changeInt != 0) {
            windowBufferDouble.updateStartIndex(changeInt);
        }
//        else if (leftPixel - update == 0 && leftPixel > 0) {
//            firstSamplePixel = pixelDifferenceBetweenSamples;
//            windowBufferDouble.updateStartIndex(1, mainWaveClass.getCurrentScroll(), mainWaveClass.getMaxScroll());
//        }
    }


    private static final int defaultR = 2;

    @Override
    public void drawSamples(Graphics g, int width, int height, int shiftY) {
        double currentVisiblePixel = firstSamplePixel - leftPixel;
//        while(currentVisiblePixel < 0) {
//            currentVisiblePixel += pixelDifferenceBetweenSamples;
//        }

//        if(width <= Math.ceil(currentVisiblePixel - pixelDifferenceBetweenSamples +
//            pixelDifferenceBetweenSamples * (windowBufferDouble.getEndIndex() - windowBufferDouble.getStartIndex()))) {
//            ProgramTest.debugPrint("Minus");
//            //currentVisiblePixel += pixelDifferenceBetweenSamples;
//            currentVisiblePixel -= pixelDifferenceBetweenSamples;
//        }
//        else {
//            currentVisiblePixel += pixelDifferenceBetweenSamples;
//            ProgramTest.debugPrint("Nothing");
//        }
//        ProgramTest.debugPrint("IF", width, currentVisiblePixel - pixelDifferenceBetweenSamples +
//            pixelDifferenceBetweenSamples * (windowBufferDouble.getEndIndex() - windowBufferDouble.getStartIndex()));


        ProgramTest.debugPrint("TOHLE MISTO TO JE", width, pixelDifferenceBetweenSamples, width - pixelDifferenceBetweenSamples,
            currentVisiblePixel - pixelDifferenceBetweenSamples +
                pixelDifferenceBetweenSamples * (windowBufferDouble.getEndIndex() - windowBufferDouble.getStartIndex()));
//        if(width - pixelDifferenceBetweenSamples >= Math.ceil(currentVisiblePixel - pixelDifferenceBetweenSamples +
//            pixelDifferenceBetweenSamples * (windowBufferDouble.getEndIndex() - windowBufferDouble.getStartIndex()))) {
//
//        }

        //double endPixel = currentVisiblePixel + pixelDifferenceBetweenSamples * (windowBufferDouble.getEndIndex() - windowBufferDouble.getStartIndex());
//        double endPixel = currentVisiblePixel - pixelDifferenceBetweenSamples +
//            pixelDifferenceBetweenSamples * (windowBufferDouble.getEndIndex() - windowBufferDouble.getStartIndex());
//        if(width - 1 <= endPixel && width >= endPixel) {
//            ProgramTest.debugPrint("if");
//        }
//        else {
//            ProgramTest.debugPrint("else");
//            currentVisiblePixel -= pixelDifferenceBetweenSamples;       // ANO z nejakyho duvodu
//        }

        //currentVisiblePixel -= pixelDifferenceBetweenSamples;       // ANO z nejakyho duvodu


        int halfHeight = height / 2;
        g.setColor(Color.black);
        g.drawLine(0, halfHeight + shiftY, width, halfHeight + shiftY);
        g.setColor(Color.blue);

        int index = windowBufferDouble.getStartIndex();
        ProgramTest.debugPrint("drawSamples", windowBufferDouble.getStartIndex(), windowBufferDouble.getEndIndex(),
            currentVisiblePixel, width, pixelDifferenceBetweenSamples, windowBufferDouble.getMaxRightIndex());

        int maxRightIndex = windowBufferDouble.getMaxRightIndex();
//        if(currentVisiblePixel >= width) {
//            return;
//        }
//
//        int iterationCount =  1 + (int)((width - currentVisiblePixel) / pixelDifferenceBetweenSamples);
//        iterationCount = Math.min(iterationCount, maxRightIndex - index);

        while(currentVisiblePixel < width && index < maxRightIndex) {
// TODO: DEBUG
//            if(index > windowBufferDouble.getBuffer().length) {
//                ProgramTest.debugPrint("Out of bounds", index, windowBufferDouble.getBuffer().length, currentVisiblePixel, width);
//            }
// TODO: DEBUG
            // minus because it the lowest value has to have to be at the highest pixel
            int sampleHeight = -(int)(windowBufferDouble.getIndex(index) * halfHeight);
            // Shift it so it starts in the middle
            sampleHeight += halfHeight + shiftY;
            int currentPixelInt = (int)currentVisiblePixel;
            g.drawLine(currentPixelInt, halfHeight, currentPixelInt, sampleHeight);
            if(pixelDifferenceBetweenSamples >= 4) {        // If there is enough space also draw circles at the end
                int r = defaultR;
                StaticDrawMethodsClass.drawCenteredCircle(g, currentPixelInt, sampleHeight, r);
            }


// TODO: Zbytecny, to jsem jakoby delal aby se ten kruh presne dotykal kde konci ten pixel, ale to nechci, ja chci aby mel stred presne tam kde konci cara
//            int absoluteSampleHeight = sampleHeight - halfHeight;
//            //int absoluteSampleHeight = Program.getAbsoluteValueGeneral()
//            int sign = Integer.signum(absoluteSampleHeight);
//            int y = sampleHeight + (sign * r);      // Doesn't need if branching
//            StaticDrawMethodsClass.drawCenteredCircle(g, currentVisiblePixel, y, r);

            currentVisiblePixel += pixelDifferenceBetweenSamples;
            index++;
        }

// TODO: DEBUG LINE
//        g.drawLine(0, -(int)(0.75 * halfHeight) + halfHeight,
//            1000, -(int)(0.75 * halfHeight) + halfHeight);
// TODO: DEBUG LINE


// TODO: DEBUG
        ProgramTest.debugPrint("Drawing individual", index, currentVisiblePixel, width, halfHeight, pixelDifferenceBetweenSamples, windowBufferDouble.getMaxRightIndex());

        // TODO: arr uz je pryc ale bylo to ekvivalentni se song z wavy
//        g.setColor(Color.GREEN);
//        int i = (int)(leftPixel / pixelDifferenceBetweenSamples);
//        double x = leftPixel % pixelDifferenceBetweenSamples;
////        double x = (int)(leftPixel % pixelDifferenceBetweenSamples);
//        for(; i < arr.length && x < width; i++, x += pixelDifferenceBetweenSamples) {
//            int sampleHeight = -(int)(arr[i] * halfHeight);
//            // Shift it so it starts in the middle
//            sampleHeight += halfHeight;
//            if(pixelDifferenceBetweenSamples < 4) {        // Too small spaces, just draw points
//                g.drawLine((int)x, sampleHeight, (int)x, sampleHeight);
//            }
//            else {
//                g.drawLine((int)x, halfHeight, (int)x, sampleHeight);
//                int r = defaultR;
//                StaticDrawMethodsClass.drawCenteredCircle(g, (int)x, sampleHeight, r);
//            }
//        }
// TODO: DEBUG
    }

    @Override
    public double convertFromBufferToPixel(int val) {
// TODO: TEDO
        return (pixelDifferenceBetweenSamples * val);
//        return val;
// TODO: TEDO
    }

    @Override
    public int convertFromPixelToBuffer(double val) {
// TODO: TEDO
//        double retVal = (val / pixelDifferenceBetweenSamples);
//        int retValInt = (int)retVal;
//        if(retVal - 0.5 > retValInt) {      // 0.9 is artificial value
//            return retValInt + 1;
//        }
//        else {
//            return retValInt;
//        }
        int retVal = (int)Math.round(val / pixelDifferenceBetweenSamples);
        ProgramTest.debugPrint("convertFromPixelToBuffer", val / pixelDifferenceBetweenSamples);
        retVal--;
        return retVal;
// TODO: TEDO
    }

    @Override
    public int convertFromPixelToIndexInAudio(double val) {
        int index = mainWaveClass.convertFromPixelToIndexInAudio(val);
        return index;
    }

    @Override
    public int convertFromBufferToIndexInAudio(int val) {
        int retVal = super.convertFromBufferToIndexInAudio(val);
        retVal--;
        return retVal;
    }

    @Override
    public int calculateMinLeftIndexForWindowBuffer() {
        int indexInAudio = mainWaveClass.convertFromPixelToIndexInAudio(mainWaveClass.getCurrentScroll());
        int bufferMidIndex = windowBufferDouble.getMiddleIndex();
        int minLeft = bufferMidIndex - indexInAudio;
        minLeft = Math.max(0, minLeft);
        return minLeft;
    }

    @Override
    public int calculateMaxRightIndexForWindowBuffer() {
        int currValIndex = mainWaveClass.convertFromPixelToIndexInAudio(mainWaveClass.getCurrentScroll());
        int newMaxRightIndex = mainWaveClass.getAudioLen() - currValIndex;
        newMaxRightIndex += windowBufferDouble.getMiddleIndex();
        return newMaxRightIndex;
    }
}
