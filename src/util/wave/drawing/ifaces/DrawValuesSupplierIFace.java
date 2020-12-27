package util.wave.drawing.ifaces;

public interface DrawValuesSupplierIFace {

    void fillBufferWithValuesToDraw(double[] buffer, int bufferStartIndex, int bufferEndIndex, int startFillIndex);

    int getCurrentStartIndexInAudio();

    int getTotalWidth();

    /**
     * Should be called only for the ending conversion. (Addition of 2 these method calls probably gives wrong result)
     *
     * @param pixel
     * @return
     */
    int convertFromPixelToIndexInAudio(double pixel);

    int getCurrentScroll();

    int getMaxScroll();

    int getAudioLen();

    default double calculatePixelDifferenceBetweenSamples(int totalWaveWidthInPixels) {
        return totalWaveWidthInPixels / (double) getAudioLen();
    }
}