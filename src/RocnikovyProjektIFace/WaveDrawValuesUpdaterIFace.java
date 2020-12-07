package RocnikovyProjektIFace;
public interface WaveDrawValuesUpdaterIFace {
    /**
     * Putting new values (usually from cache file) to the start of the buffer, this is done when scrolling to left.
     * Updates the buffer from the cache (or by calculating when there is no caching)
     * @param bufferEndIndex is the index where to stop adding new values.
     */
    void updateBufferWithNewValuesOnLeft(int bufferEndIndex);
    /**
     * Putting new values (usually from cache file) to the end of the buffer, this is done when scrolling to right.
     * Updates the buffer from the cache (or by calculating when there is no caching), copiedValCount values were already copied from the old one.
     * @param totalCopiedValCount is the total number of copied indices from old buffer
     */
    void updateBufferWithNewValuesOnRight(int totalCopiedValCount);

    int getNewValCountOnRight(int copiedValCount);

    /**
     * For example when working with min and max there are 2 values in buffer per 1 pixel
     * @param val
     * @return
     */
    double convertFromBufferToPixel(int val);
    /**
     * For example when working with min and max there are 2 values in buffer per 1 pixel
     * @param val
     * @return
     */
    int convertFromPixelToBuffer(double val);

    int convertFromPixelToIndexInAudio(double val);

    default int convertFromBufferToIndexInAudio(int val) {
        double pixel = convertFromBufferToPixel(val);
        int index = convertFromPixelToIndexInAudio(pixel);
        return index;
    }

    int calculateMaxRightIndexForShiftBuffer();
    int calculateMinLeftIndexForShiftBuffer();
}