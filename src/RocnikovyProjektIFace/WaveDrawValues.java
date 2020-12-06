package RocnikovyProjektIFace;

import Rocnikovy_Projekt.ProgramTest;

import java.awt.*;

public abstract class WaveDrawValues implements WaveDrawValuesUpdaterIFace {
    public WaveDrawValues(int newVisibleWidth, int totalWaveWidthInPixels, int startIndexInValues, int valueCount,
                          int windowCountToTheRight, CommunicationWithWaveValuesPanelIFace mainWaveClass) {
        this.mainWaveClass = mainWaveClass;
        this.windowCountToTheRight = windowCountToTheRight;
        waveResize(newVisibleWidth, totalWaveWidthInPixels, startIndexInValues, valueCount);
    }

    protected CommunicationWithWaveValuesPanelIFace mainWaveClass;

    protected int windowCountToTheRight;
    public int getWindowCountToTheRight() {
        return windowCountToTheRight;
    }

    protected int visibleWidth;
    protected WindowBufferDouble windowBufferDouble;

    abstract public int getStartIndex();


    public void performZoom(int newStartIndex, int newTotalWidth, int newTotalValuesCount) {
        waveResize(visibleWidth, newTotalWidth, newStartIndex, newTotalValuesCount);
    }


    /**
     * Resizes the internal buffer class based on the resize of the window which paints the wave.
     * @param newVisibleWidth represents the new visible wave visibleWidth.
     * @param totalWaveWidthInPixels is the total visibleWidth of the wave.
     * @param startIndexInValues is the current index in the wave values, which corresponds to the left visible pixel.
     * @param valueCount is the number of the draw values in the whole wave.
     */
    public abstract void waveResize(int newVisibleWidth, int totalWaveWidthInPixels, int startIndexInValues, int valueCount);

  // TODO:
//    /**
//     * Resizes the internal buffer class based on the resize of the window which paints the wave.
//     * @param newWidth represents the new window visibleWidth parameter.
//     * @param leftVisiblePixel is the left pixel where is the wave starting.
//     * @param totalWaveWidthInPixels is the total visibleWidth of wave in pixels.
//     */
//    public abstract void waveResize(int newWidth, int leftVisiblePixel, int totalWaveWidthInPixels);
    public abstract void shiftBuffer(int pixelShift);
//    public abstract void updateWaveToLeft(int startIndex);

    /**
     *
     * @param g
     * @param width
     * @param height
     * @param shiftY used when drawing more waves. So we just move their start location in y coordinates by using this parameter
     */
    public abstract void drawSamples(Graphics g, int width, int height, int shiftY);


    /**
     *
     * @param startFillIndex is the pixel in the cached buffer file, to start taking the values from. So in the case min/max it is 1/2 the real index
     * @param bufferStartIndex
     * @param bufferEndIndex
     */
    public void fillBuffer(int startFillIndex, int bufferStartIndex, int bufferEndIndex) {
        mainWaveClass.fillBufferWithValuesToDraw(windowBufferDouble.getBuffer(), bufferStartIndex, bufferEndIndex, startFillIndex);

        // TODO: START
        //windowBufferDouble.setStartIndex(getStartIndex() % windowBufferDouble.getBufferLength());
        //windowBufferDouble.resetStartIndex();
        // TODO: START
    }


    public void updateBufferWithNewValuesOnLeft(int totalCopiedValCount) {
        // TODO: not sure about these indexes - Possible bug
//        int bufferStartIndex = 0;
//        int bufferEndIndex = windowBufferDouble.getNewStartIndexWhenShiftingToRight(totalCopiedValCount);
//        int startFillIndex = mainWaveClass.getCurrentScroll() - totalCopiedValCount; // TODO: Not sure about this - possible bug
        //startFillIndex = mainWaveClass.getNewTotalIndex();              // TODO: Not sure about this - possible bug


// TODO: POSUVNY BUFFER
        int bufferStartIndex = windowBufferDouble.getMinLeftIndex();
        //int bufferEndIndex = windowBufferDouble.getBufferLength() - totalCopiedValCount;
        int bufferEndIndex = windowBufferDouble.getMaxRightIndex() - totalCopiedValCount;
//        int startFillIndex = mainWaveClass.getCurrentStartIndexInAudio();     // /2 because it is again min and max // TODO: IND
//        startFillIndex = convertToNonVisibleMostLeftIndexInAudio(startFillIndex);

//        int distanceFromMidIndex = windowBufferDouble.getMiddleIndex() - totalCopiedValCount;
//        double startFillPixel = mainWaveClass.getCurrentScroll() - convertFromBufferToPixel(distanceFromMidIndex);
//        int startFillIndex = mainWaveClass.convertFromPixelToIndexInAudio(startFillPixel);
        int beforeMidIndexCount = windowBufferDouble.calculateNumberOfIndicesInBufferBeforeMidIndex();
        double startFillPixel = mainWaveClass.getCurrentScroll() - convertFromBufferToPixel(beforeMidIndexCount);
        int startFillIndex = mainWaveClass.convertFromPixelToIndexInAudio(startFillPixel);

// TODO: POSUVNY BUFFER

        // TODO: EXACT BUFFER
//        bufferStartIndex = 0;
//        bufferEndIndex = windowBufferDouble.getBufferLength();
//        startFillIndex = mainWaveClass.getCurrentScroll();
        // TODO: EXACT BUFFER


        ProgramTest.debugPrint("updateBufferWithNewValuesOnLeft", bufferStartIndex, bufferEndIndex, startFillIndex,
            windowBufferDouble.getMinLeftIndex(), windowBufferDouble.getMiddleIndex(), windowBufferDouble.getMaxRightIndex());
//        if(bufferEndIndex < 700) {
//            bufferEndIndex = 700;
//        }


        // TODO: TESTING WAVES
        // TODO: Version without IS_FIRST
        fillBuffer(startFillIndex, bufferStartIndex, bufferEndIndex);
        // TODO: Version without IS_FIRST
//        if(IS_FIRST) {
////            bufferEndIndex += windowBufferDouble.VISIBLE_WIDTH / 2;
////            bufferEndIndex = Math.min(bufferEndIndex, windowBufferDouble.getBuffer().length);
//            fillBuffer(startFillIndex, bufferStartIndex, bufferEndIndex);
//            ProgramTest.debugPrint("IS_FIRST");
//            ProgramTest.debugPrint(windowBufferDouble.getBufferLength(), windowBufferDouble.getBuffer().length,
//                windowBufferDouble.getStartIndex(), windowBufferDouble.getEndIndex(),
//                windowBufferDouble.getMaxRightIndex(), bufferEndIndex);
////            for(; bufferStartIndex < bufferEndIndex; bufferStartIndex++) {
////                ProgramTest.debugPrint("pixel:", bufferStartIndex, windowBufferDouble.getIndex(bufferStartIndex));
////                windowBufferDouble.getBuffer()[bufferStartIndex] = 0.75;
////            }
////            windowBufferDouble.getBuffer()[bufferStartIndex] = -0.75;
//            //windowBufferDouble.getBuffer()[++bufferStartIndex] = -0.75;
//        }
//        else {
//////            //int w = windowBufferDouble.VISIBLE_WIDTH;
////            int w = windowBufferDouble.getMaxRightIndex();
////            fillBuffer(startFillIndex, windowBufferDouble.getMinLeftIndex(), w);
////////            fillBufferWithValuesToDraw(mainWaveClass.getCurrentScroll(), 0, windowBufferDouble.getBuffer().length);
//////            ProgramTest.debugPrint("IS_NOT_FIRST");
////
//////            for(int i = 0; i < windowBufferDouble.getBuffer().length; i++) {
//////                windowBufferDouble.getBuffer()[i] = 0;
//////            }
//
//        }
        // TODO: TESTING WAVES
// TODO: MIDINDEX
//        windowBufferDouble.resetStartIndex();
// TODO: MIDINDEX
    }


    public void updateBufferWithNewValuesOnRight(int totalCopiedValCount) {
        // TODO: not sure about these indexes - Possible bug
//        int bufferStartIndex = totalCopiedValCount;
//        int bufferEndIndex = windowBufferDouble.getBufferLength();
//        int startFillIndex = mainWaveClass.getCurrentScroll() + totalCopiedValCount; // TODO: Not sure about this - possible bug
        //startFillIndex = mainWaveClass.getNewTotalIndex();                  // TODO: Not sure about this - possible bug


// TODO: POSUVNY BUFFER
        int bufferStartIndex = getBufferStartIndexOnRight(totalCopiedValCount);
        int bufferEndIndex = getBufferEndIndexOnRight();


        int distanceFromMidIndex = windowBufferDouble.getMiddleIndex() - totalCopiedValCount;
        double startFillPixel = mainWaveClass.getCurrentScroll() - convertFromBufferToPixel(distanceFromMidIndex);
        int startFillIndex = mainWaveClass.convertFromPixelToIndexInAudio(startFillPixel);


        ProgramTest.debugPrint("updateBufferWithNewValuesOnRight", bufferStartIndex, bufferEndIndex, startFillIndex,
            mainWaveClass.getCurrentScroll(), totalCopiedValCount,
            windowBufferDouble.getMinLeftIndex(), windowBufferDouble.getMiddleIndex(), windowBufferDouble.getMaxRightIndex());

//        if(startFillIndex != mainWaveClass.convertFromPixelToIndexInAudio(
//            mainWaveClass.getCurrentScroll() +
//                convertFromBufferToPixel(totalCopiedValCount)))
//        {
//            ProgramTest.debugPrint(startFillIndex, mainWaveClass.convertFromPixelToIndexInAudio(
//                mainWaveClass.getCurrentScroll() +
//                    convertFromBufferToPixel(totalCopiedValCount)));
//            System.exit(1111);
//        }
// TODO: POSUVNY BUFFER

        // TODO: EXACT BUFFER
//        bufferStartIndex = 0;
//        bufferEndIndex = windowBufferDouble.getBufferLength();
//        startFillIndex = mainWaveClass.getCurrentScroll();
        // TODO: EXACT BUFFER


// TODO: NEVIM
//        if(isFirst) {
//            for (int i = bufferStartIndex; i < bufferEndIndex; i++) {
//                windowBufferDouble.getBuffer()[i] = i / (double) windowBufferDouble.getBufferLength();
////            windowBufferDouble.getBuffer()[i] = 0.75;
//            }
//            isFirst = false;
//        }
// TODO: NEVIM
        ProgramTest.debugPrint("Before Filling buffer", startFillIndex, totalCopiedValCount, visibleWidth);
//        if(isFirst) {
        // TODO: TESTING WAVES
        // TODO: Version without IS_FIRST
        fillBuffer(startFillIndex, bufferStartIndex, bufferEndIndex);
        ProgramTest.debugPrint("IS_BOTH");
        ProgramTest.debugPrint("specs", bufferStartIndex, bufferEndIndex, windowBufferDouble.getMaxRightIndex());
        // TODO: Version without IS_FIRST
//        if(IS_FIRST) {
//            fillBuffer(startFillIndex, bufferStartIndex, bufferEndIndex);
//            ProgramTest.debugPrint("IS_FIRST");
//            ProgramTest.debugPrint("First specs", bufferStartIndex, bufferEndIndex, windowBufferDouble.getMaxRightIndex());
//        }
//        else {
////            int w;
////            w = windowBufferDouble.getMaxRightIndex() + 2;          // TODO: Crash - so it is correct
////            w = Math.min(windowBufferDouble.getBuffer().length, w);
////            if(mainWaveClass.getCurrentScroll() == mainWaveClass.getMaxScroll()) {
////                w = mainWaveClass.getAudioLen() - startFillIndex;
////            }
////
////            w = windowBufferDouble.getMaxRightIndex();
////            int beforeMidIndexCount = windowBufferDouble.calculateNumberOfIndicesInBufferBeforeMidIndex();
////            double startFillPixelWhenFillingEveything = mainWaveClass.getCurrentScroll() - convertFromBufferToPixel(beforeMidIndexCount);
////            int startFillIndexWhenFillingEveything = mainWaveClass.convertFromPixelToIndexInAudio(startFillPixelWhenFillingEveything);
////            //fillBuffer(mainWaveClass.getCurrentStartIndexInAudio(), windowBufferDouble.getMinLeftIndex(), w);
////            fillBuffer(startFillIndexWhenFillingEveything, windowBufferDouble.getMinLeftIndex(), w);
////
//////////            fillBufferWithValuesToDraw(mainWaveClass.getCurrentScroll(), 0, w);
////////
//////////            if(startFillIndex == 0) {
////////                fillBufferWithValuesToDraw(mainWaveClass.getCurrentStartIndexInAudio(), 0, w);
////////                windowBufferDouble.resetStartIndex();
//////////            }
////////
////
//////            for(int i = 0; i < windowBufferDouble.getBuffer().length; i++) {
//////                windowBufferDouble.getBuffer()[i] = 0;
//////            }
////
//////
//////            ProgramTest.debugPrint("visible width * 2 going right", w);
////////            fillBufferWithValuesToDraw(mainWaveClass.getCurrentScroll(), 0, windowBufferDouble.getBuffer().length);
//////            ProgramTest.debugPrint(mainWaveClass.getCurrentScroll(), windowBufferDouble.getBufferLength());
//////            ProgramTest.debugPrint("IS_NOT_FIRST");
//        }
        // TODO: TESTING WAVES
//            isFirst = false;
//        }
// TODO: NEVIM
// TODO: MIDINDEX
//        windowBufferDouble.resetStartIndex();
// TODO: MIDINDEX
    }


    // TODO: NEVIM
    private boolean isFirst = true;
    // TODO: NEVIM

    private int getBufferStartIndexOnRight(int copiedValCount) {
        return windowBufferDouble.getMinLeftIndex() + copiedValCount;
    }

    private int getBufferEndIndexOnRight() {
        return windowBufferDouble.getMaxRightIndex();
    }

    @Override
    public int getNewValCountOnRight(int copiedValCount) {
        return getBufferEndIndexOnRight() - getBufferStartIndexOnRight(copiedValCount);
    }




    private int getNewTotalIndex() {
        // I add to the old index how much I moved in the start index and from that I substract the middle index, because I take the middle index values from the left
        return mainWaveClass.getCurrentScroll() + windowBufferDouble.getStartIndex() - windowBufferDouble.getMiddleIndex();
    }

    /**
     *
     * @param startIndexInAudio is the start index in audio - the index of the most left currently visible sample.
     */
    protected void fillWholeBuffer(int startIndexInAudio) {        // TODO: Possible bug
        windowBufferDouble.resetStartIndex();
        ProgramTest.debugPrint("fill whole buffer", startIndexInAudio, mainWaveClass.getCurrentScroll(), mainWaveClass.getMaxScroll());
        windowBufferDouble.setBounds();
        //windowBufferDouble.setMaxRightIndex();
        int minLeftIndex = windowBufferDouble.getMinLeftIndex();
        startIndexInAudio = convertToNonVisibleMostLeftIndexInAudio(startIndexInAudio);

        ProgramTest.debugPrint("fill whole buffer2", startIndexInAudio, minLeftIndex, windowBufferDouble.getMaxRightIndex());
        fillBuffer(startIndexInAudio, minLeftIndex, windowBufferDouble.getMaxRightIndex());
    }

    /**
     * Shift it to the left because the first visible value will be at middle index
     * @param startIndexInAudio is the start index in audio - the index of the most left currently visible sample.
     * @return Returns the index in audio corresponding to the most left index in window buffer.
     */
    private int convertToNonVisibleMostLeftIndexInAudio(int startIndexInAudio) {
        int indexCountBeforeMidIndex = windowBufferDouble.calculateNumberOfIndicesInBufferBeforeMidIndex();
        startIndexInAudio -= convertFromBufferToIndexInAudio(indexCountBeforeMidIndex);
        return startIndexInAudio;
    }
}