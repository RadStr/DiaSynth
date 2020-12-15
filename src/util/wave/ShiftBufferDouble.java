package util.wave;

import Rocnikovy_Projekt.ProgramTest;

/**
 * Immutable object, basically what it does, in constructor is created buffer, and we move in it by changing
 * setStartIndex method. We can fill the buffer, but can not change the length. So for resizing there is need to create new object.
 */
public class ShiftBufferDouble {

// TODO:    //public ShiftBufferDouble(int WINDOW_COUNT_TO_THE_RIGHT, int windowSize, int leftVisiblePixel, int totalWaveWidthInPixels, WaveDrawValuesConverterIFace boundsUpdater) {
    public ShiftBufferDouble(int windowCountToTheRight, int windowSize, int startIndexInValues, int valueCount, ShiftBufferBoundsIFace boundsUpdater) {
        this.boundsUpdater = boundsUpdater;
        windowCount = calculateWindowCount(windowCountToTheRight);
        this.windowSize = windowSize;
        int bufferSize = calculateBufferSize(windowSize, windowCount);

        // TODO: EXACT BUFFER
        System.out.println("Window_Size\t" + windowSize);
//        bufferSize = windowSize;
//        bufferSize = 19182;
        // TODO: EXACT BUFFER
        buffer = new double[bufferSize];

        //setStartIndex(leftVisiblePixel, totalWaveWidthInPixels, 0, 1);

        resetMinLeftIndexToZero();
        resetMaxRightIndexToBufferLen();

        setMidIndex();
        resetStartIndex();
    }

    private static int calculateWindowCount(int windowCountToTheRight) {
        return 2 * windowCountToTheRight + 1;
    }
    private static int calculateBufferSize(int windowSize, double windowCount) {
        return (int)Math.ceil(windowSize * windowCount);            // TODO: Not sure - possible bug
    }


    private ShiftBufferBoundsIFace boundsUpdater;

    private int minLeftIndex;
    private void resetMinLeftIndexToZero() {
        setMinLeftIndex(0);
    }
    public void setMinLeftIndex() {
        setMinLeftIndex(boundsUpdater.calculateMinLeftIndexForShiftBuffer());
    }
    private void setMinLeftIndex(int val) {
//        if(val < 0) {
//            minLeftIndex = 0;
//        }
//        else {
//            minLeftIndex = val;
//        }
// TODO: DELAM MID
        minLeftIndex = Math.max(val, 0);
// TODO: DELAM MID
    }
    private void updateMinLeftIndex(int update) {
        setMinLeftIndex(getMinLeftIndex() + update);
    }
    public int getMinLeftIndex() {
        return minLeftIndex;
    }

    private int maxRightIndex;
    private void resetMaxRightIndexToBufferLen() {
        setMaxRightIndex(buffer.length);
    }
    public void setMaxRightIndex() {
        setMaxRightIndex(boundsUpdater.calculateMaxRightIndexForShiftBuffer());
    }
    public void setMaxRightIndex(int val) {
//        if(val > buffer.length) {
//            maxRightIndex = buffer.length;
//        }
//        else {
//            //maxRightIndex = Math.max(VISIBLE_WIDTH, val);
//            maxRightIndex = val;
//        }
        maxRightIndex = Math.min(val, buffer.length);
        ProgramTest.debugPrint("setMaxRightIndex", maxRightIndex, val, buffer.length);
    }
// TODO: STARY - VYMAZAT
//    public void setMaxRightIndex(int totalIndex, int maxIndex) {
//        double windowSizeInPixels = boundsUpdater.convertFromBufferToPixel(VISIBLE_WIDTH);
//// TODO: TEDO
//        int newMaxRightIndex = boundsUpdater.convertFromPixelToBuffer(maxIndex - totalIndex + windowSizeInPixels);
//        ProgramTest.debugPrint("setMaxRightIndexCandidate", newMaxRightIndex, maxIndex, totalIndex,
//            windowSizeInPixels, VISIBLE_WIDTH);
////        newMaxRightIndex = boundsUpdater.convertFromPixelToBuffer(windowSizeInPixels);
//
//
////        int newMaxRightIndex = boundsUpdater.convertFromPixelToIndexInAudio(maxIndex - totalIndex + windowSizeInPixels);
//// TODO: TEDO
//        setMaxRightIndex(newMaxRightIndex);
//    }

    private void updateMaxRightIndex(int update) {
        setMaxRightIndex(getMaxRightIndex() + update);
    }
    public int getMaxRightIndex() {
        return maxRightIndex;
    }

    private int middleIndex;
    private void setMidIndex() {
        int middleWindow = (int)(windowCount / 2);
        middleIndex = middleWindow * windowSize;
    }
    public int getMiddleIndex() {
        return middleIndex;
    }

    public void setBounds() {
        setMinLeftIndex();
        setMaxRightIndex();
    }


    public int calculateNumberOfIndicesInBufferBeforeMidIndex() {
        int indexCountBeforeMidIndex = getMiddleIndex() - getMinLeftIndex();
        return indexCountBeforeMidIndex;
    }

    private double[] buffer;
    public int getBufferLength() {
        return maxRightIndex - minLeftIndex;
    }
    public double[] getBuffer() {
        return buffer;
    }

    public int getEndIndexOverflow() {
        return endIndex - buffer.length;
    }

    // TODO:
//    public void setIndex(int index, double val) {
//        buffer[index] = val;
//    }
    public double getIndex(int index) {
        return buffer[index];
    }

    /**
     * Splits the values by splitter.
     * Fills the content of the internal buffer starting from 0 with values from the parsed string starting at index waveSamplesStartIndex.
     * And resets the startIndex to the middle index.
     * @param bufferStartIndex is the first index in the buffer which should be filled with new value. (inclusive)
     * @param bufferEndIndex is the last index index in the buffer which should be filled with new value. (exclusive)
     * @param waveSamplesStartIndex is the index of the number from values string, from which we should start taking numbers.
     * @param values are the values divided by splitter.
     * @param separator is the regex by which are the values split.
     */
    public void fillBuffer(int bufferStartIndex, int bufferEndIndex, int waveSamplesStartIndex, String values, String separator) {
        String[] valuesParsed = values.split(separator);
        for(int i = bufferStartIndex; i < bufferEndIndex; i++, waveSamplesStartIndex++) {
            buffer[i] = Double.parseDouble(valuesParsed[waveSamplesStartIndex]);
        }
        resetStartIndex();
    }

    /**
     * Fills the content of the internal buffer starting from 0 with values from values array starting at index waveSamplesStartIndex.
     * And resets the startIndex to the middle index.
     * @param bufferStartIndex is the first index in the buffer which should be filled with new value. (inclusive)
     * @param bufferEndIndex is the last index index in the buffer which should be filled with new value. (exclusive)
     * @param waveSamplesStartIndex is the start index where to take the values from the given array.
     * @param values is the double array which values will be used to fill the internal buffer.
     */
    public void fillBuffer(int bufferStartIndex, int bufferEndIndex, int waveSamplesStartIndex, double[] values) {
        for(int i = bufferStartIndex; i < bufferEndIndex; i++, waveSamplesStartIndex++) {
            buffer[i] = values[waveSamplesStartIndex];
        }

//        resetStartIndex();      // TODO: EXACT BUFFER
    }




    private double windowCount;     // TODO: Possible bug, it was int before
    public double getWindowCount() {
        return windowCount;
    }

    private int windowSize;
    public int getWindowSize() {
        return windowSize;
    }
    public void setWindowSize(int val) {
        windowSize = val;
        // TODO: DELAM MID
        endIndex = startIndex + windowSize;
        // TODO: DELAM MID
        windowCount = buffer.length / windowSize;
    }
// TODO:
//    public void setWindowSize(int val) {
//        windowSize = val;
//        upgradeEndIndex = true;
//    }

    private int startIndex;
    public int getStartIndex() {
        return startIndex;
    }


    public void updateStartIndex(int update) {
        setStartIndex(startIndex + update);
        boolean result = updateIfOutOfBounds(update);

//        if(update < 0 && !result) {     // !result because else it was already updated
//            //updateMaxRightIndex(-update);
//            setMaxRightIndex((maxScroll - currentScroll + VISIBLE_WIDTH / 2) * 2);
//        }

        //setMaxRightIndex(currentScroll, maxScroll);

//        setMinLeftIndex();
//        setMaxRightIndex();
    }
    private void setStartIndex(int val) {
// TODO: DEBUG
//        if(val < 0) {
//            System.out.println(val);
//        }
// TODO: DEBUG

        startIndex = val;
// TODO: DELAM MID
        endIndex = startIndex + windowSize;
// TODO: DELAM MID
// TODO: DEBUG
//        ProgramTest.debugPrint("setStartIndexRealSetter", getStartIndex());
// TODO: DEBUG
    }
    public void resetStartIndex() {
        this.setStartIndex(middleIndex);
    }


    private int endIndex;       // The variable is set inside the updateStartIndex method
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * Checks if either startIndex < 0 or endIndex is outside the buffer. and if so, then reads new values.
     */
    public boolean updateIfOutOfBounds(int update) {
        boolean result = false;

        // TODO: LEVA MEZ
        result = result || updateIfOutOfBoundsStartIndex();
        // TODO: LEVA MEZ
        result = result || updateIfOutOfBoundsEndIndex();

        ProgramTest.debugPrint("updateIfOutOfBounds min and max", minLeftIndex, maxRightIndex);
        return result;
    }

    /**
     * Checks if startIndex < 0 and if so, then reads new values.
     */
    public boolean updateIfOutOfBoundsStartIndex() {
        if(startIndex < 0) {
            setMaxRightIndex();
            int copiedValCount = updateInternalBufferScrollingToLeft();
            ProgramTest.debugPrint("updateIfOutOfBoundsStartIndex", copiedValCount, getMaxRightIndex());
            // TODO: SET MIN
            setMinLeftIndex();
            //setMaxRightIndex();
            // TODO: SET MIN
            //updateMaxRightIndex(-update);
            boundsUpdater.updateBufferWithNewValuesOnLeft(copiedValCount);
            resetStartIndex();

            return true;
        }

        return false;
    }

    /**
     * Checks if endIndex is outside the buffer and if so, then reads new values.
     */
    public boolean updateIfOutOfBoundsEndIndex() {
        int endIndex = getEndIndex();

        if(endIndex > maxRightIndex) {
            setMinLeftIndex();
            int totalCopiedValCount = updateInternalBufferScrollingToRight();

            //int dif = maxIndex - totalIndex - (endIndex - maxRightIndex) - 100000000;
            int newValsCount = boundsUpdater.getNewValCountOnRight(totalCopiedValCount);
            //int dif = maxIndex - totalIndex - (endIndex - maxRightIndex) / 2;
//            int dif = maxIndex - totalIndex - (newValsCount) / 2;       // How far it is from maxIndex
//            if(dif < 0) {
//                ProgramTest.debugPrint("Dif:", dif);
//                ProgramTest.debugPrint("newMaxRightIndex", dif, getMaxRightIndex());
//                updateMaxRightIndex(2 * dif + 1);
//                ProgramTest.debugPrint("newMaxRightIndex", getMaxRightIndex());
//            }
            int oldMaxRightIndex = getMaxRightIndex();
            // TODO: SET MIN
            setMaxRightIndex();
            // TODO: SET MIN
            ProgramTest.debugPrint("are setMaxRightIndex equal", getMaxRightIndex(), oldMaxRightIndex, oldMaxRightIndex == getMaxRightIndex());
            //setMaxRightIndex(maxIndex - totalIndex);


//            else {
//                if(getMaxRightIndex() < 730) {
//                    setMaxRightIndex(730);
//                }
//                //setMaxRightIndex(buffer.length);
//            }

            boundsUpdater.updateBufferWithNewValuesOnRight(totalCopiedValCount);
            resetStartIndex();
            return true;
        }

        ProgramTest.debugPrint("maxRightIndex", getMaxRightIndex());
        return false;
    }

// TODO: !!!!!!!!!! Jen zaloha toho puvodniho - ale muzu to pak klidne smazat podle me
//    TODO: Tohle nen idobre to by se mel;o kontrolovat asi uvnitr tydle tridy, jestli jsem moc vlevo
//    nebo moc pravo a kdyz jsem tak si zazadat od WaveDrawValues o hodnoty z cache/nove vypocitany
//    public int updateInternalBufferScrollingToLeft() {
//        int dif = getIndexToCopyToWhenScrollingLeft(startIndex);
//        // In this case we can reuse some old values from buffer, so we don't read it all from HDD cache (or calculate again when caching is disabled)
//        // Basically it shifts by the middleIndex - startIndex to the left
//        if(dif > 0) {
//            updateBufferReuseDifVals(dif);
//            shiftBufferToRight(dif);    // To the left, because we take the old values in buffer on start and put them on the end of the buffer
//            return dif;
//        }
//        else { // We moved too far away, can't reuse values from buffer, have to read again from HDD (or calculate)
//            updateBufferDontReuseAnything();
//        }
//
//        return 0;
//    }
//
//    public int updateInternalBufferScrollingToRight() {
//        int dif = getIndexToCopyFromWhenScrollingRight(startIndex);      TODO: Nejsem si jisty
//        // In this case we can reuse some old values from buffer, so we don't read it all from HDD cache (or calculate again when caching is disabled)
//        // Basically it shifts by the middleIndex - startIndex to the left
//        if(dif > 0) {
//            updateBufferReuseDifVals(dif);
//            shiftBufferToLeft(dif);     // To the left, because we take the old values in buffer on end and put them on the start of the buffer
//            return dif;
//        }
//        TODO: Else vetev dat pryc neni potreba ted uz
//        else { // We moved too far away, can't reuse values from buffer, have to read again from HDD (or calculate)
//            updateBufferDontReuseAnything();
//        }
//
//        return 0;
//    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // When I scroll to right I put right values to left
    // When I scroll to left I put left values to right
    // So it is the opposite direction
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public int getIndexToCopyToWhenScrollingLeft() {
        int indexToCopyTo;
// TODO: MIDINDEX
//        indexToCopyTo = middleIndex + startIndex;
        indexToCopyTo = getMinLeftIndex() - startIndex;     // How far is the start index from the min left index
        indexToCopyTo = getMiddleIndex() + indexToCopyTo;   // and by that much it is from the mid index on right (everything on left from that needs to be taken from cache)
// TODO: MIDINDEX
        return indexToCopyTo;
    }

    public int getIndexToCopyFromWhenScrollingRight() {
        int indexToCopyFrom;                  // TODO: not sure - possible bug
// TODO: MIDINDEX
//        indexToCopyFrom = middleIndex + buffer.length - endIndex;    // middleIndex - how many indexes am I after the buffer
        //indexToCopyFrom = buffer.length - endIndex;    // middleIndex - how many indexes am I after the buffer
        // TODO: LEVA MEZ
        indexToCopyFrom = startIndex;       // startIndex is currently the left visible pixel, so everything from that to end needs to be put in to mid index
        // So that will be put in the middle index. And everything before that until the new min index needs to be put before the min index
        indexToCopyFrom -= (middleIndex - minLeftIndex);
        ProgramTest.debugPrint("getIndexToCopyFromWhenScrollingRight", startIndex, endIndex,
            middleIndex - minLeftIndex, middleIndex, minLeftIndex, maxRightIndex, startIndex - (middleIndex - minLeftIndex));
        // TODO: LEVA MEZ
// TODO: MIDINDEX
        return indexToCopyFrom;
    }

    public int getCopiedValCountAfterMidIndex() {
        int copiedValCountAfterMidIndex = getMaxRightIndex() - startIndex;
// TODO: DEBUG
//        if(copiedValCountAfterMidIndex < 0) {
//            int a = 5;
//        }
// TODO: DEBUG
        return copiedValCountAfterMidIndex;
    }


    public int updateInternalBufferScrollingToLeft() {
        int startCopyToIndex = getIndexToCopyToWhenScrollingLeft();
        int totalCopiedValCount = getMaxRightIndex() - startCopyToIndex;
        if(totalCopiedValCount <= 0) {
            return 0;
        }

        // In this case we can reuse some old values from buffer, so we don't read it all from HDD cache (or calculate again when caching is disabled)
        // Basically it shifts by the middleIndex - startIndex to the left
        if(startCopyToIndex < maxRightIndex) {
            shiftBufferToRight(startCopyToIndex);    // To the right, because we take the old in buffer on start and put them on the end of the buffer
//            updateMaxRightIndex(startCopyToIndex);
//            return startCopyToIndex;
        }

//        updateMaxRightIndex(startCopyToIndex);
//        return 0;

//        ProgramTest.debugPrint("startCopyToIndex is", startCopyToIndex, getMaxRightIndex());
//        int oldMaxRightIndex = getMaxRightIndex();
//        updateMaxRightIndex(startCopyToIndex);
        return totalCopiedValCount;
    }

    public int updateInternalBufferScrollingToRight() {
        int startCopyFromIndex = getIndexToCopyFromWhenScrollingRight();
        int totalCopiedValCount = getMaxRightIndex() - startCopyFromIndex;
        if(totalCopiedValCount <= 0) {
            return 0;
        }

        // In this case we can reuse some old values from buffer, so we don't read it all from HDD cache (or calculate again when caching is disabled)
        // Basically it shifts by the middleIndex - startIndex to the left
        if(startCopyFromIndex < maxRightIndex) {
            shiftBufferToLeft(startCopyFromIndex);     // To the left, because we take the values in buffer on end and put them on the start of the buffer
            return totalCopiedValCount;
        }

        return 0;
    }



//    private void updateBufferReuseDifVals(int dif) {
//        shiftBufferToRight(dif);
//        fillTheRestOfArray(dif, buffer.length);   TODO: private void fillBufferWithValues(int bufferStartIndex, int bufferEndIndex, int valuesStartIndex, double[]/String values);
//    }


    private void shiftBufferToLeft(int startCopyFromIndex) {
//        int startCopyFromIndex = getOldIndexWhenShiftingToLeft(shift);
        ProgramTest.debugPrint("shiftBufferToLeft", startCopyFromIndex, getMinLeftIndex(),
            buffer.length, buffer.length - startCopyFromIndex, maxRightIndex, maxRightIndex - startCopyFromIndex);

        for(int copyToIndex = getMinLeftIndex(), copyFromIndex = startCopyFromIndex;
                copyFromIndex < maxRightIndex;
                copyFromIndex++, copyToIndex++) {
            buffer[copyToIndex] = buffer[copyFromIndex];
            //System.out.println("shifting left before:\t" + copyToIndex + "\t" + buffer[copyToIndex]);
            //buffer[copyToIndex] = 0.75;
            //System.out.println("shifting left after:\t" + copyToIndex + "\t" + buffer[copyToIndex]);
        }

// TODO: DEBUG
//        for(int i = buffer.length - 1 - startCopyFromIndex; i < buffer.length; i++) {
//            buffer[i] = i / (double)buffer.length;
//        }
// TODO: DEBUG
    }

    private int getOldIndexWhenShiftingToLeft(int shift) {
        return buffer.length - shift;
    }


    private void shiftBufferToRight(int startCopyToIndex) {
        ProgramTest.debugPrint("shiftBufferToRight", startCopyToIndex, buffer.length,
            buffer.length - startCopyToIndex, maxRightIndex, maxRightIndex - startCopyToIndex);

// TODO: DEBUG
//        ProgramTest.debugPrint("BEFORE:", buffer[buffer.length - 1]);
// TODO: DEBUG

//        int startCopyToIndex = getNewStartIndexWhenShiftingToRight(shift);
//        int startCopyToIndex = shift;
        // Have to go from end because otherwise I will rewrite
        for(int copyToIndex = maxRightIndex - 1, copyFromIndex = (maxRightIndex - 1) - startCopyToIndex;
                copyToIndex >= startCopyToIndex;
                copyFromIndex--, copyToIndex--) {
            buffer[copyToIndex] = buffer[copyFromIndex];
        }


// TODO: DEBUG
//        ProgramTest.debugPrint("MID:", buffer[buffer.length - 1]);
////        for(int i = 0; i < startCopyToIndex; i++) {
////            System.out.println(i);
////            buffer[i] = -0.75;
////        }
//        if(buffer[buffer.length - 1] == -0.75) {
//            ProgramTest.debugPrint("BROKEN");
//        }
//        ProgramTest.debugPrint("AFTER:", buffer[buffer.length - 1]);
// TODO: DEBUG
    }

    public int getNewStartIndexWhenShiftingToRight(int shift) {
        return buffer.length - shift;
    }

    public void setRange(Range range) {
        range.start = getStartIndex();
        range.end = getEndIndex();
    }
}
