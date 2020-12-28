package util.wave;

public interface ShiftBufferBoundsIFace {
    int calculateMaxRightIndexForShiftBuffer();

    int calculateMinLeftIndexForShiftBuffer();

    /**
     * Putting new values to the start of the buffer, this is done when scrolling to left.
     * Updates the buffer with values from cache (or by calculating when there is no caching).
     *
     * @param bufferEndIndex is the index where to stop adding new values.
     */
    void updateBufferWithNewValuesOnLeft(int bufferEndIndex);

    /**
     * Putting new values to the end of the buffer, this is done when scrolling to right.
     * Updates the buffer with values from cache (or by calculating when there is no caching).
     *
     * @param totalCopiedValCount is the total number of copied indices from old buffer
     */
    void updateBufferWithNewValuesOnRight(int totalCopiedValCount);
}
