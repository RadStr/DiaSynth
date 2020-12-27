package util.wave;

import test.ProgramTest;

/**
 * Immutable object, basically what it does, in constructor is created buffer, and we move in it by changing
 * setStartIndex method. We can fill the buffer, but can not change the length. So for resizing there is need to create new object.
 */
public class ShiftBufferDouble {
    public ShiftBufferDouble(int windowCountToTheRight, int windowSize, ShiftBufferBoundsIFace boundsUpdater) {
        this.boundsUpdater = boundsUpdater;
        windowCount = calculateWindowCount(windowCountToTheRight);
        this.windowSize = windowSize;
        int bufferSize = calculateBufferSize(windowSize, windowCount);
        buffer = new double[bufferSize];

        resetMinLeftIndexToZero();
        resetMaxRightIndexToBufferLen();
        setMidIndex();
        resetStartIndex();
    }

    private static int calculateWindowCount(int windowCountToTheRight) {
        return 2 * windowCountToTheRight + 1;
    }

    private static int calculateBufferSize(int windowSize, double windowCount) {
        return (int) Math.ceil(windowSize * windowCount);
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
        minLeftIndex = Math.max(val, 0);
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
        maxRightIndex = Math.min(val, buffer.length);
    }

    private void updateMaxRightIndex(int update) {
        setMaxRightIndex(getMaxRightIndex() + update);
    }

    public int getMaxRightIndex() {
        return maxRightIndex;
    }

    private int middleIndex;

    private void setMidIndex() {
        int middleWindow = (int) (windowCount / 2);
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

    public double getValueAtIndex(int index) {
        return buffer[index];
    }


    private double windowCount;

    public double getWindowCount() {
        return windowCount;
    }

    private int windowSize;

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int val) {
        windowSize = val;
        endIndex = startIndex + windowSize;
        windowCount = buffer.length / windowSize;
    }

    private int startIndex;

    public int getStartIndex() {
        return startIndex;
    }


    public void updateStartIndex(int update) {
        setStartIndex(startIndex + update);
        boolean result = updateIfOutOfBounds();
    }

    private void setStartIndex(int val) {
        startIndex = val;
        endIndex = startIndex + windowSize;
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
    private boolean updateIfOutOfBounds() {
        boolean result = false;
        result = result || updateIfOutOfBoundsStartIndex();
        result = result || updateIfOutOfBoundsEndIndex();

        // TODO: DEBUG
        ProgramTest.debugPrint("updateIfOutOfBounds min and max", minLeftIndex, maxRightIndex);
        // TODO: DEBUG
        return result;
    }

    /**
     * Checks if startIndex < 0 and if so, then reads new values.
     */
    private boolean updateIfOutOfBoundsStartIndex() {
        if (startIndex < 0) {
            setMaxRightIndex();
            int copiedValCount = updateInternalBufferScrollingToLeft();
            ProgramTest.debugPrint("updateIfOutOfBoundsStartIndex", copiedValCount, getMaxRightIndex());
            setMinLeftIndex();
            boundsUpdater.updateBufferWithNewValuesOnLeft(copiedValCount);
            resetStartIndex();

            return true;
        }

        return false;
    }

    /**
     * Checks if endIndex is outside the buffer and if so, then reads new values.
     */
    private boolean updateIfOutOfBoundsEndIndex() {
        int endIndex = getEndIndex();

        if (endIndex > maxRightIndex) {
            setMinLeftIndex();
            int totalCopiedValCount = updateInternalBufferScrollingToRight();
            int newValsCount = boundsUpdater.getNewValCountOnRight(totalCopiedValCount);
            int oldMaxRightIndex = getMaxRightIndex();
            setMaxRightIndex();
            ProgramTest.debugPrint("are setMaxRightIndex equal", getMaxRightIndex(), oldMaxRightIndex, oldMaxRightIndex == getMaxRightIndex());
            boundsUpdater.updateBufferWithNewValuesOnRight(totalCopiedValCount);
            resetStartIndex();
            return true;
        }

        ProgramTest.debugPrint("maxRightIndex", getMaxRightIndex());
        return false;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // When I scroll to right I put right values to left
    // When I scroll to left I put left values to right
    // So it is the opposite direction
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public int getIndexToCopyToWhenScrollingLeft() {
        int indexToCopyTo;
        indexToCopyTo = getMinLeftIndex() - startIndex;     // How far is the start index from the min left index
        // and by that much it is from the mid index on right
        // (everything on left from that needs to be taken from cache)
        indexToCopyTo = getMiddleIndex() + indexToCopyTo;
        return indexToCopyTo;
    }

    public int getIndexToCopyFromWhenScrollingRight() {
        int indexToCopyFrom;
        // startIndex is currently the left visible pixel,
        // so everything from that to end needs to be put in to mid index
        indexToCopyFrom = startIndex;
        // So that will be put in the middle index.
        // And everything before that until the new min index needs to be put before the min index
        indexToCopyFrom -= (middleIndex - minLeftIndex);
        return indexToCopyFrom;
    }


    public int updateInternalBufferScrollingToLeft() {
        int startCopyToIndex = getIndexToCopyToWhenScrollingLeft();
        int totalCopiedValCount = getMaxRightIndex() - startCopyToIndex;
        if (totalCopiedValCount <= 0) {
            return 0;
        }

        // In this case we can reuse some old values from buffer, so we don't read it all from HDD cache
        // (or calculate again when caching is disabled)
        // Basically it shifts by the middleIndex - startIndex to the left
        if (startCopyToIndex < maxRightIndex) {
            // To the right, because we take the old in buffer on start and put them on the end of the buffer
            shiftBufferToRight(startCopyToIndex);
        }

        return totalCopiedValCount;
    }

    public int updateInternalBufferScrollingToRight() {
        int startCopyFromIndex = getIndexToCopyFromWhenScrollingRight();
        int totalCopiedValCount = getMaxRightIndex() - startCopyFromIndex;
        if (totalCopiedValCount <= 0) {
            return 0;
        }

        // In this case we can reuse some old values from buffer, so we don't read it all from HDD cache
        // (or calculate again when caching is disabled)
        // Basically it shifts by the middleIndex - startIndex to the left
        if (startCopyFromIndex < maxRightIndex) {
            // To the left, because we take the values in buffer on end and put them on the start of the buffer
            shiftBufferToLeft(startCopyFromIndex);
            return totalCopiedValCount;
        }

        return 0;
    }


    private void shiftBufferToLeft(int startCopyFromIndex) {
        ProgramTest.debugPrint("shiftBufferToLeft", startCopyFromIndex, getMinLeftIndex(),
                               buffer.length, buffer.length - startCopyFromIndex, maxRightIndex, maxRightIndex - startCopyFromIndex);

        for (int copyToIndex = getMinLeftIndex(), copyFromIndex = startCopyFromIndex;
             copyFromIndex < maxRightIndex;
             copyFromIndex++, copyToIndex++) {
            buffer[copyToIndex] = buffer[copyFromIndex];
        }
    }


    private void shiftBufferToRight(int startCopyToIndex) {
        // TODO: DEBUG
        ProgramTest.debugPrint("shiftBufferToRight", startCopyToIndex, buffer.length,
                               buffer.length - startCopyToIndex, maxRightIndex, maxRightIndex - startCopyToIndex);
        // TODO: DEBUG
        // Have to go from end because otherwise I will rewrite the values before I copy them
        for (int copyToIndex = maxRightIndex - 1, copyFromIndex = (maxRightIndex - 1) - startCopyToIndex;
             copyToIndex >= startCopyToIndex;
             copyFromIndex--, copyToIndex--) {
            buffer[copyToIndex] = buffer[copyFromIndex];
        }
    }

    public void setRange(Range range) {
        range.start = getStartIndex();
        range.end = getEndIndex();
    }
}
