package RocnikovyProjektIFace;

import RocnikovyProjektIFace.StaticDrawMethods.StaticDrawMethodsClass;
import Rocnikovy_Projekt.ProgramTest;

import java.awt.*;

public class WaveDrawValuesIndividual extends WaveDrawValues {
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
    public WaveDrawValuesIndividual(int leftPixel, int newVisibleWidth, int totalWaveWidthInPixels, int startIndexInValues, int valueCount,
                                    int windowCountToTheRight, DrawValuesSupplierIFace mainWaveClass) {
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
        shiftBufferDouble = new ShiftBufferDouble(windowCountToTheRight, w, startIndexInValues, valueCount, this);

        leftPixel = mainWaveClass.getCurrentScroll();
        setFirstSamplePixel();
        fillWholeBuffer(startIndexInValues);

//        // Not the most effective way, but it works and is simple to understand, that's what matters I move it to 0 and the back to correct position
//        //shiftBuffer(-leftPixel);
//        //shiftBuffer(mainWaveClass.getCurrentScroll());
//        shiftBuffer(-leftPixel + mainWaveClass.getCurrentScroll());
//        ProgramTest.debugPrint("wave resize", mainWaveClass.getCurrentScroll());



// TODO: Old - can delete
        //shiftBufferDouble = new ShiftBufferDouble(WINDOW_COUNT_TO_THE_RIGHT, newVisibleWidth, leftVisiblePixel, totalWaveWidthInPixels);
// TODO: newer - can delete
//            getDrawValues();        // TODO: Volam na tride co se stara o to cachovani
//            shiftBufferDouble.fillBufferWithValuesToDraw();
    }

    @Override
    public int getStartIndex() {
        return shiftBufferDouble.getStartIndex();
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
//        firstSamplePixel = pixelDifferenceBetweenSamples * shiftBufferDouble.getTotalIndex();
//        ProgramTest.debugPrint("setFirstSamplePixel", shiftBufferDouble.getTotalIndex(), mainWaveClass.getCurrentScroll());

//        firstSamplePixel = shiftBufferDouble.getTotalIndex();

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
//    public void shiftBuffer(int update) {
////        ProgramTest.debugPrint("shiftBuffer", mainWaveClass.getCurrentScroll(), leftPixel, leftPixel + update);
//        pixelMovement += update;
//        leftPixel += update;
//        setFirstSamplePixel();
//        int sampleMovement = (int)(pixelMovement / pixelDifferenceBetweenSamples);
//        if(sampleMovement != 0) {
//            pixelMovement %= pixelDifferenceBetweenSamples;
//            shiftBufferDouble.updateStartIndex(sampleMovement, mainWaveClass.getCurrentScroll(), mainWaveClass.getMaxScroll());
//        }
//    }


//    public void shiftBuffer(int update) {
////        ProgramTest.debugPrint("shiftBuffer", mainWaveClass.getCurrentScroll(), leftPixel, leftPixel + update);
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
//            shiftBufferDouble.updateStartIndex(change, mainWaveClass.getCurrentScroll(), mainWaveClass.getMaxScroll());
//        }
//        else if (leftPixel - update == 0 && leftPixel > 0) {
//            firstSamplePixel = pixelDifferenceBetweenSamples;
//            shiftBufferDouble.updateStartIndex(1, mainWaveClass.getCurrentScroll(), mainWaveClass.getMaxScroll());
//        }
//    }

//        public void shiftBuffer(int update) {
////        ProgramTest.debugPrint("shiftBuffer", mainWaveClass.getCurrentScroll(), leftPixel, leftPixel + update);
//        pixelMovement += update;
//        leftPixel += update;
//        setFirstSamplePixel();
//        int sampleMovement = (int)(pixelMovement / pixelDifferenceBetweenSamples);
//        if(sampleMovement != 0) {
//            pixelMovement %= pixelDifferenceBetweenSamples;
//            shiftBufferDouble.updateStartIndex(sampleMovement, mainWaveClass.getCurrentScroll(), mainWaveClass.getMaxScroll());
//        }
//    }


    // TODO: DEBUG
    private final double tolerance = 0.1;

    private int total = 0;
    // TODO: DEBUG

    public void shiftBuffer(int pixelShift) {
        total += pixelShift;

//        ProgramTest.debugPrint("shiftBuffer", mainWaveClass.getCurrentScroll(), leftPixel, leftPixel + update);

        leftPixel += pixelShift;
        // Because java does some resizing for the scroll by 1 pixel so sometimes the value of leftPixel goes to -1,
        // which makes the the samples moved 1 pixel to right - the 0th sample starts where should be the 1st one, which
        // would be fine if didn't have the tooltips, which are then 1 sample off.
        if(leftPixel < 0) {
            leftPixel = 0;
        }

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
            shiftBufferDouble.updateStartIndex(changeInt);
        }
//        else if (leftPixel - update == 0 && leftPixel > 0) {
//            firstSamplePixel = pixelDifferenceBetweenSamples;
//            shiftBufferDouble.updateStartIndex(1, mainWaveClass.getCurrentScroll(), mainWaveClass.getMaxScroll());
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
//            pixelDifferenceBetweenSamples * (shiftBufferDouble.getEndIndex() - shiftBufferDouble.getStartIndex()))) {
//            ProgramTest.debugPrint("Minus");
//            //currentVisiblePixel += pixelDifferenceBetweenSamples;
//            currentVisiblePixel -= pixelDifferenceBetweenSamples;
//        }
//        else {
//            currentVisiblePixel += pixelDifferenceBetweenSamples;
//            ProgramTest.debugPrint("Nothing");
//        }
//        ProgramTest.debugPrint("IF", width, currentVisiblePixel - pixelDifferenceBetweenSamples +
//            pixelDifferenceBetweenSamples * (shiftBufferDouble.getEndIndex() - shiftBufferDouble.getStartIndex()));


        ProgramTest.debugPrint("TOHLE MISTO TO JE", width, pixelDifferenceBetweenSamples, width - pixelDifferenceBetweenSamples,
            currentVisiblePixel - pixelDifferenceBetweenSamples +
                pixelDifferenceBetweenSamples * (shiftBufferDouble.getEndIndex() - shiftBufferDouble.getStartIndex()));
//        if(width - pixelDifferenceBetweenSamples >= Math.ceil(currentVisiblePixel - pixelDifferenceBetweenSamples +
//            pixelDifferenceBetweenSamples * (shiftBufferDouble.getEndIndex() - shiftBufferDouble.getStartIndex()))) {
//
//        }

        //double endPixel = currentVisiblePixel + pixelDifferenceBetweenSamples * (shiftBufferDouble.getEndIndex() - shiftBufferDouble.getStartIndex());
//        double endPixel = currentVisiblePixel - pixelDifferenceBetweenSamples +
//            pixelDifferenceBetweenSamples * (shiftBufferDouble.getEndIndex() - shiftBufferDouble.getStartIndex());
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

        int index = shiftBufferDouble.getStartIndex();
        ProgramTest.debugPrint("drawSamples", shiftBufferDouble.getStartIndex(), shiftBufferDouble.getEndIndex(),
            currentVisiblePixel, width, pixelDifferenceBetweenSamples, shiftBufferDouble.getMaxRightIndex());

        int maxRightIndex = shiftBufferDouble.getMaxRightIndex();
//        if(currentVisiblePixel >= width) {
//            return;
//        }
//
//        int iterationCount =  1 + (int)((width - currentVisiblePixel) / pixelDifferenceBetweenSamples);
//        iterationCount = Math.min(iterationCount, maxRightIndex - index);

        while(currentVisiblePixel < width && index < maxRightIndex) {
// TODO: DEBUG
//            if(index > shiftBufferDouble.getBuffer().length) {
//                ProgramTest.debugPrint("Out of bounds", index, shiftBufferDouble.getBuffer().length, currentVisiblePixel, width);
//            }
// TODO: DEBUG
            // minus because it the lowest value has to have to be at the highest pixel
            int sampleHeight = -(int)(shiftBufferDouble.getIndex(index) * halfHeight);
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
        ProgramTest.debugPrint("Drawing individual", index, currentVisiblePixel, width, halfHeight, pixelDifferenceBetweenSamples, shiftBufferDouble.getMaxRightIndex());

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
        // TODO: 1 PIXEL BUG FIX
        // Added because when I start (when the first thing I draw are individuals) with drawing individual samples,
        // which means the number of samples is less than there are pixels in the wave visualiser,
        // then I had problem that when I started filling I started
        // from index 1 and it was because of this. I had 0 and by retVal-- I got -1 which I subtracted in the
        // convertToNonVisibleMostLeftIndexInAudio method, so I got +1 index than I really should have had.
        if(retVal > 0) {
            retVal--;
        }
        // TODO: 1 PIXEL BUG FIX
        return retVal;
    }

    @Override
    public int calculateMinLeftIndexForShiftBuffer() {
        int indexInAudio = mainWaveClass.convertFromPixelToIndexInAudio(mainWaveClass.getCurrentScroll());
        int bufferMidIndex = shiftBufferDouble.getMiddleIndex();
        int minLeft = bufferMidIndex - indexInAudio;
        minLeft = Math.max(0, minLeft);
        return minLeft;
    }

    @Override
    public int calculateMaxRightIndexForShiftBuffer() {
        int currValIndex = mainWaveClass.convertFromPixelToIndexInAudio(mainWaveClass.getCurrentScroll());
        int newMaxRightIndex = mainWaveClass.getAudioLen() - currValIndex;
        newMaxRightIndex += shiftBufferDouble.getMiddleIndex();
        return newMaxRightIndex;
    }
}
