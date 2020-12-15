package util.wave.drawing.ifaces;
public interface WaveDrawValuesConverterIFace {
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
}