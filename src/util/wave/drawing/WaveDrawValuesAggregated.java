package util.wave.drawing;

import test.ProgramTest;
import util.wave.drawing.ifaces.DrawValuesSupplierIFace;
import util.wave.Range;
import util.wave.ShiftBufferDouble;

import java.awt.*;

public class WaveDrawValuesAggregated extends WaveDrawValues {
    public WaveDrawValuesAggregated(int newVisibleWidth, int totalWaveWidthInPixels, int startIndexInValues, int valueCount,
                                    int windowCountToTheRight, DrawValuesSupplierIFace mainWaveClass) {
        super(newVisibleWidth, totalWaveWidthInPixels, startIndexInValues, valueCount, windowCountToTheRight, mainWaveClass);
        windowRange = new Range();
    }

//    private int WINDOW_COUNT_TO_THE_RIGHT;
//    public int getWindowCountToTheRight() {
//        return WINDOW_COUNT_TO_THE_RIGHT;
//    }
//
//    private int visibleWidth;
//    protected ShiftBufferDouble shiftBufferDouble;

    @Override
    public void waveResize(int newVisibleWidth, int totalWaveWidthInPixels, int startIndexInValues, int valueCount) {
        if(visibleWidth != newVisibleWidth || shiftBufferDouble == null) {
            visibleWidth = newVisibleWidth;
            //shiftBufferDouble = new ShiftBufferDouble(WINDOW_COUNT_TO_THE_RIGHT, newWidth, leftVisiblePixel, totalWaveWidthInPixels);
            // TODO: EXACT BUFFER
            //int w = 2 * newVisibleWidth;        // 2 * because it contains min and max
            int w = 2 * newVisibleWidth;        // 2 * because it contains min and max  // TODO: ZOOM
            // TODO: EXACT BUFFER
            shiftBufferDouble = new ShiftBufferDouble(windowCountToTheRight, w, startIndexInValues, valueCount, this);      // TODO: ZOOM
            ProgramTest.debugPrint("visible width * 2", 2 * newVisibleWidth);
            fillWholeBuffer(startIndexInValues);
        }
        else {
            // TODO: HOR
            totalUpdate = startIndexInValues;
            // TODO: HOR
            ProgramTest.debugPrint("STARTIND2", startIndexInValues);
            fillWholeBuffer(startIndexInValues);
        }
    }

    /**
     * Represents the start and end index, he values are most of the time invalid, they are updated
     * in getter.
     */
    private Range windowRange;


    @Override
    public int getStartIndex() {
        return shiftBufferDouble.getStartIndex();
    }


//    /**
//     * Updates the internal buffer based on the current position of startIndex.
//     * @param startIndex is the current index in buffer. Must be < 0.
//     */
//    private void updateWaveToLeft(int startIndex) {
//        shiftBufferDouble.updateInternalBufferScrollingToLeft(startIndex);
//        int len = shiftBufferDouble.getBufferLength();
//        int dif = len + startIndex;
//        // In this case we can reuse some old values from buffer, so we don't read it all from HDD cache (or calculate again when caching is disabled)
//        // Basically it shifts by the middleIndex - startIndex to the left
//        if(dif > 0) {
//            updateBufferReuseDifVals(dif);
//        }
//        else { // We moved too far away, can't reuse values from buffer, have to read again from HDD (or calculate)
//            updateBufferDontReuseAnything();
//        }
//    }

// TODO: Stara verze - muzu asi vymazat
//    public void shiftBuffer(int update) {
//
//        shiftBufferDouble.updateInternalBufferScrollingToLeft(startIndex);
//        int len = shiftBufferDouble.getBufferLength();
//        int dif = len + startIndex;
//        // In this case we can reuse some old values from buffer, so we don't read it all from HDD cache (or calculate again when caching is disabled)
//        // Basically it shifts by the middleIndex - startIndex to the left
//        if(dif > 0) {
//            updateBufferReuseDifVals(dif);
//        }
//        else { // We moved too far away, can't reuse values from buffer, have to read again from HDD (or calculate)
//            updateBufferDontReuseAnything();
//        }
//    }


    // TODO: DEBUG
    // TODO: HOR
    private int totalUpdate = 0;
    // TODO: HOR

    public void shiftBuffer(int pixelShift) {
        // TODO: HOR
//        totalUpdate += update;
//        ProgramTest.debugPrint("total Horizontal update:", totalUpdate, mainWaveClass.getCurrentScroll());
//        if(totalUpdate != mainWaveClass.getCurrentScroll()) {
//            System.exit(11111111);
//        }
        // TODO: HOR

        //TODO: HORIZONTAL UPDATE
        // TOOD: AVG - vymazat ten update *= 2 pro avg
        pixelShift *= 2;        // Because min and max
        shiftBufferDouble.updateStartIndex(pixelShift);

        // TODO: TEST
//        if(!IS_FIRST) {
//            fillBufferWithValuesToDraw(mainWaveClass.getCurrentScroll(), 0, shiftBufferDouble.VISIBLE_WIDTH);
//            shiftBufferDouble.resetStartIndex();
//            nonFirstBuffer = shiftBufferDouble;
//
//            ProgramTest.debugPrint("Visiblito", shiftBufferDouble.VISIBLE_WIDTH);
//        }
//        else {
////            double[] buffer = shiftBufferDouble.getBuffer();
////            for(int i = 0, j = getStartIndex(); i < shiftBufferDouble.VISIBLE_WIDTH; i++, j++) {
////                if(buffer[j] != nonFirstBuffer.getIndex(i)) {
////                    ProgramTest.debugPrint("Non equal", getStartIndex(), i, j, buffer[j], nonFirstBuffer.getIndex(i));
//////                    if(Math.abs(buffer[j] - nonFirstBuffer.getIndex(i)) > 0.1) {
//////                        System.exit(1236);
//////                    }
//////                    System.exit(11235);
////                }
////            }
//        }
        // TODO: TEST
    }



    @Override
    public void drawSamples(Graphics g, int width, int height, int shiftY) {
        g.setColor(Color.blue);

//        try {     // TODO: DEBUG
        shiftBufferDouble.setRange(windowRange);
// TODO: DEBUG
        ProgramTest.debugPrint("START_INDEX:", shiftBufferDouble.getStartIndex(), shiftBufferDouble.getEndIndex(),
                windowRange.start, windowRange.end, shiftBufferDouble.getMaxRightIndex(), shiftBufferDouble.getBufferLength(),
                shiftBufferDouble.getEndIndex() - shiftBufferDouble.getStartIndex(),
                (shiftBufferDouble.getEndIndex() - shiftBufferDouble.getStartIndex()) / 2);
// TODO: DEBUG
        for (int i = 0; i < /*shiftBufferDouble.getBufferLength()*/shiftBufferDouble.getEndIndex(); i++) {
            if (shiftBufferDouble.getIndex(i) != 0) {
                ProgramTest.debugPrint("first non-zero index", i, shiftBufferDouble.getIndex(i));
                break;
            }
        }

        // TODO: SQUARE WAVE
        int previousX = -1;
        int previousYMin = -1;
        int previousYMax = -1;
        // TODO: SQUARE WAVE
        int halfHeight = height / 2;              // TODO: BUG !!! windowRange.end je / 2 skutecne hodnote, protoze neberu k uvahu minima ... nekde tam musim dat *2
        for (int i = windowRange.start, x = 0; i < windowRange.end; i++, x++) {
            double minY = shiftBufferDouble.getIndex(i);
            i++;
            double maxY = shiftBufferDouble.getIndex(i);

            int sampleMinHeightSample = -(int) (minY * halfHeight);    // minus because it the lowest value has to have to be at the highest pixel
            int sampleMaxHeightSample = -(int) (maxY * halfHeight);
            // Shift it so it starts in the middle
            sampleMinHeightSample += halfHeight + shiftY;
            sampleMaxHeightSample += halfHeight + shiftY;


            g.drawLine(x, sampleMinHeightSample, x, sampleMaxHeightSample);
            if(x > 0) {
                // Now check if we need to connect the the previous sample (sample aggregation) with next one
                if (previousYMax < sampleMinHeightSample) {         // If the previous line is below the current
                    g.drawLine(previousX, previousYMax, x, sampleMinHeightSample);
                } else if (previousYMin > sampleMaxHeightSample) {  // If the previous line is above the current
                    g.drawLine(previousX, previousYMin, x, sampleMaxHeightSample);
                }
            }

            previousX = x;
            previousYMin = sampleMinHeightSample;
            previousYMax = sampleMaxHeightSample;
            // TODO: SQUARE WAVE
// TODO: DEBUG
//                ProgramTest.debugPrint("Drawing min, max", i, minY, maxY, windowRange.start, windowRange.end);
// TODO: DEBUG
        }

//        }
//        catch(ArrayIndexOutOfBoundsException e) {
//            System.out.println(windowRange);
//        }

        g.setColor(Color.black);
        halfHeight += shiftY;
        g.drawLine(0, halfHeight, width, halfHeight);
    }

    @Override
    public double convertFromBufferToPixel(int val) {
        return val / (double)2;
    }

    @Override
    public int convertFromPixelToBuffer(double val) {
        return (int)(2 * val);
    }

    @Override
    public int convertFromPixelToIndexInAudio(double val) {
        return (int)val;
    }


//    @Override
//    public void drawSamples(Graphics g, int width, int height) {
//        g.setColor(Color.blue);
//
////        try {     // TODO: DEBUG
//        shiftBufferDouble.setRange(windowRange);
//// TODO: DEBUG
////            ProgramTest.debugPrint("START_INDEX:", shiftBufferDouble.getStartIndex(), shiftBufferDouble.getEndIndex(), shiftBufferDouble.getBufferLength());
//// TODO: DEBUG
//
//        int halfHeight = height / 2;
//        // TODO: BUG !!! windowRange.end je / 2 skutecne hodnote, protoze neberu k uvahu minima ... nekde tam musim dat *2
//        for (int i = windowRange.start / 2, x = 0; i < windowRange.end / 2; i++, x++) {
//            double avgY = shiftBufferDouble.getIndex(i);
//            int sampleAvgHeightSample = -(int) (avgY * halfHeight);    // minus because it the lowest value has to have to be at the highest pixel
//            // Shift it so it starts in the middle
//            sampleAvgHeightSample += halfHeight;
//            g.drawLine(x, sampleAvgHeightSample, x, sampleAvgHeightSample);
//        }
//
////        }
////        catch(ArrayIndexOutOfBoundsException e) {
////            System.out.println(windowRange);
////        }
//    }

    @Override
    public int calculateMinLeftIndexForShiftBuffer() {
        int currScroll = mainWaveClass.getCurrentScroll();
        int midIndex = shiftBufferDouble.getMiddleIndex();

        int minLeft = midIndex - convertFromPixelToBuffer(currScroll);
        minLeft = Math.max(0, minLeft);
        return minLeft;
    }

    @Override
    public int calculateMaxRightIndexForShiftBuffer() {
        //int currValIndex = mainWaveClass.convertFromPixelToIndexInAudio(mainWaveClass.getCurrentScroll());
//        int index = mainWaveClass.getAudioLen() - currValIndex;
//        double newMaxRightIndexPixel = mainWaveClass.convertFromIndexInValuesToPixel(index);
//        int maxRightIndex = convertFromPixelToBuffer(newMaxRightIndexPixel);

//        int maxRightIndex = mainWaveClass.convertIndexInAudioTomainWaveClass.getAudioLen() - currValIndex;
//        int pixel =
//        maxRightIndex = convertFromPixelToBuffer(pixel)

        int pixelCount = mainWaveClass.getTotalWidth() - mainWaveClass.getCurrentScroll();
        int maxRightIndex = convertFromPixelToBuffer(pixelCount);
        maxRightIndex += shiftBufferDouble.getMiddleIndex();
        ProgramTest.debugPrint("calculateMaxRightIndexForShiftBuffer", mainWaveClass.getMaxScroll(), mainWaveClass.getCurrentScroll());
        ProgramTest.debugPrint("calculateMaxRightIndexForShiftBuffer", maxRightIndex, pixelCount, convertFromPixelToBuffer(pixelCount),
            shiftBufferDouble.getBuffer().length - convertFromPixelToBuffer(pixelCount) < shiftBufferDouble.getMiddleIndex(),
            shiftBufferDouble.getBuffer().length - convertFromPixelToBuffer(pixelCount));
        return maxRightIndex;
    }
}
