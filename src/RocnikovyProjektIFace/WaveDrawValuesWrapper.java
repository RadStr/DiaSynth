package RocnikovyProjektIFace;

import Rocnikovy_Projekt.ProgramTest;

import java.awt.*;

public class WaveDrawValuesWrapper extends WaveDrawValuesWrapperAbstract {
    public WaveDrawValuesWrapper(int newVisibleWidth, int totalWaveWidthInPixels, int startIndexInValues, int valueCount,
                                 int windowCountToTheRight, CommunicationWithWaveValuesPanelIFace mainWaveClass) {
        super(newVisibleWidth, totalWaveWidthInPixels, startIndexInValues, valueCount, windowCountToTheRight, mainWaveClass);
        windowRange = new Range();
    }

//    private int WINDOW_COUNT_TO_THE_RIGHT;
//    public int getWindowCountToTheRight() {
//        return WINDOW_COUNT_TO_THE_RIGHT;
//    }
//
//    private int visibleWidth;
//    protected WindowBufferDouble windowBufferDouble;

    @Override
    public void waveResize(int newVisibleWidth, int totalWaveWidthInPixels, int startIndexInValues, int valueCount) {
        if(visibleWidth != newVisibleWidth || windowBufferDouble == null) {
            visibleWidth = newVisibleWidth;
            //windowBufferDouble = new WindowBufferDouble(WINDOW_COUNT_TO_THE_RIGHT, newWidth, leftVisiblePixel, totalWaveWidthInPixels);
            // TODO: EXACT BUFFER
            //int w = 2 * newVisibleWidth;        // 2 * because it contains min and max
            int w = 2 * newVisibleWidth;        // 2 * because it contains min and max  // TODO: ZOOM
            // TODO: EXACT BUFFER
            windowBufferDouble = new WindowBufferDouble(windowCountToTheRight, w, startIndexInValues, valueCount, this);      // TODO: ZOOM
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
        return windowBufferDouble.getStartIndex();
    }


//    /**
//     * Updates the internal buffer based on the current position of startIndex.
//     * @param startIndex is the current index in buffer. Must be < 0.
//     */
//    private void updateWaveToLeft(int startIndex) {
//        windowBufferDouble.updateInternalBufferScrollingToLeft(startIndex);
//        int len = windowBufferDouble.getBufferLength();
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
//        windowBufferDouble.updateInternalBufferScrollingToLeft(startIndex);
//        int len = windowBufferDouble.getBufferLength();
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
        windowBufferDouble.updateStartIndex(pixelShift);

        // TODO: TEST
//        if(!IS_FIRST) {
//            fillBufferWithValuesToDraw(mainWaveClass.getCurrentScroll(), 0, windowBufferDouble.VISIBLE_WIDTH);
//            windowBufferDouble.resetStartIndex();
//            nonFirstBuffer = windowBufferDouble;
//
//            ProgramTest.debugPrint("Visiblito", windowBufferDouble.VISIBLE_WIDTH);
//        }
//        else {
////            double[] buffer = windowBufferDouble.getBuffer();
////            for(int i = 0, j = getStartIndex(); i < windowBufferDouble.VISIBLE_WIDTH; i++, j++) {
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
        windowBufferDouble.setRange(windowRange);
// TODO: DEBUG
        ProgramTest.debugPrint("START_INDEX:", windowBufferDouble.getStartIndex(), windowBufferDouble.getEndIndex(),
                windowRange.start, windowRange.end, windowBufferDouble.getMaxRightIndex(), windowBufferDouble.getBufferLength(),
                windowBufferDouble.getEndIndex() - windowBufferDouble.getStartIndex(),
                (windowBufferDouble.getEndIndex() - windowBufferDouble.getStartIndex()) / 2);
// TODO: DEBUG
        for (int i = 0; i < /*windowBufferDouble.getBufferLength()*/windowBufferDouble.getEndIndex(); i++) {
            if (windowBufferDouble.getIndex(i) != 0) {
                ProgramTest.debugPrint("first non-zero index", i, windowBufferDouble.getIndex(i));
                break;
            }
        }

        // TODO: SQUARE WAVE
        int previousX = -1;
        int previousYMax = -1;
        // TODO: SQUARE WAVE
        int halfHeight = height / 2;              // TODO: BUG !!! windowRange.end je / 2 skutecne hodnote, protoze neberu k uvahu minima ... nekde tam musim dat *2
        for (int i = windowRange.start, x = 0; i < windowRange.end; i++, x++) {
            double minY = windowBufferDouble.getIndex(i);
            i++;
            double maxY = windowBufferDouble.getIndex(i);

            int sampleMinHeightSample = -(int) (minY * halfHeight);    // minus because it the lowest value has to have to be at the highest pixel
            int sampleMaxHeightSample = -(int) (maxY * halfHeight);
            // Shift it so it starts in the middle
            sampleMinHeightSample += halfHeight + shiftY;
            sampleMaxHeightSample += halfHeight + shiftY;
            if(x > 0) {
                // Because of square wave
                g.drawLine(previousX, previousYMax, x, sampleMinHeightSample);
            }
            g.drawLine(x, sampleMinHeightSample, x, sampleMaxHeightSample);
            previousX = x;
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
//        windowBufferDouble.setRange(windowRange);
//// TODO: DEBUG
////            ProgramTest.debugPrint("START_INDEX:", windowBufferDouble.getStartIndex(), windowBufferDouble.getEndIndex(), windowBufferDouble.getBufferLength());
//// TODO: DEBUG
//
//        int halfHeight = height / 2;
//        // TODO: BUG !!! windowRange.end je / 2 skutecne hodnote, protoze neberu k uvahu minima ... nekde tam musim dat *2
//        for (int i = windowRange.start / 2, x = 0; i < windowRange.end / 2; i++, x++) {
//            double avgY = windowBufferDouble.getIndex(i);
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
    public int calculateMinLeftIndexForWindowBuffer() {
        int currScroll = mainWaveClass.getCurrentScroll();
        int midIndex = windowBufferDouble.getMiddleIndex();

        int minLeft = midIndex - convertFromPixelToBuffer(currScroll);
        minLeft = Math.max(0, minLeft);
        return minLeft;
    }

    @Override
    public int calculateMaxRightIndexForWindowBuffer() {
        //int currValIndex = mainWaveClass.convertFromPixelToIndexInAudio(mainWaveClass.getCurrentScroll());
//        int index = mainWaveClass.getAudioLen() - currValIndex;
//        double newMaxRightIndexPixel = mainWaveClass.convertFromIndexInValuesToPixel(index);
//        int maxRightIndex = convertFromPixelToBuffer(newMaxRightIndexPixel);

//        int maxRightIndex = mainWaveClass.convertIndexInAudioTomainWaveClass.getAudioLen() - currValIndex;
//        int pixel =
//        maxRightIndex = convertFromPixelToBuffer(pixel)

        int pixelCount = mainWaveClass.getTotalWidth() - mainWaveClass.getCurrentScroll();
        int maxRightIndex = convertFromPixelToBuffer(pixelCount);
        maxRightIndex += windowBufferDouble.getMiddleIndex();
        ProgramTest.debugPrint("calculateMaxRightIndexForWindowBuffer", mainWaveClass.getMaxScroll(), mainWaveClass.getCurrentScroll());
        ProgramTest.debugPrint("calculateMaxRightIndexForWindowBuffer", maxRightIndex, pixelCount, convertFromPixelToBuffer(pixelCount),
            windowBufferDouble.getBuffer().length - convertFromPixelToBuffer(pixelCount) < windowBufferDouble.getMiddleIndex(),
            windowBufferDouble.getBuffer().length - convertFromPixelToBuffer(pixelCount));
        return maxRightIndex;
    }
}
