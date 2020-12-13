package player;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public interface DrawValuesSupplierIFace {

    void fillBufferWithValuesToDraw(double[] buffer, int bufferStartIndex, int bufferEndIndex, int startFillIndex);


    int getPrefixLenInBytes();
    int fillBufferWithCachedValues(double[] buffer, int bufferStartIndex, int bufferEndIndex,
                                   int startFillIndex, int inputLen, int outputLen);

    default int fillBufferWithCachedValues(FileChannel cache, double[] buffer,
                                           int bufferStartIndex, int bufferEndIndex,
                                           int startFillIndex) {
        int outIndex;
        try {
            //cache.position(startFillIndex * Double.BYTES + getPrefixLenInBytes());
            int startPosInFile = startFillIndex * Double.BYTES + getPrefixLenInBytes();
            int len = bufferEndIndex - bufferStartIndex;
            ByteBuffer byteBuffer = cache.map(FileChannel.MapMode.READ_ONLY, startPosInFile, Double.BYTES * len);
            for (int i = bufferStartIndex; i < bufferEndIndex; i++) {
                buffer[i] = byteBuffer.getDouble();
            }
            outIndex = startFillIndex + (bufferEndIndex - bufferStartIndex);
        } catch (EOFException e) {
            outIndex = -1;
        } catch (IOException e) {
            outIndex = -2;
        }

        return outIndex;
    }

    boolean getIsCached();

    int getCurrentStartIndexInAudio();

    int getTotalWidth();

    /**
     * Should be called only for the ending conversion. (Addition of 2 these method calls probably gives wrong result)
     * @param pixel
     * @return
     */
    int convertFromPixelToIndexInAudio(double pixel);

    int getCurrentScroll();
    int getMaxScroll();

    int getAudioLen();

    default double calculatePixelDifferenceBetweenSamples(int totalWaveWidthInPixels) { return totalWaveWidthInPixels / (double) getAudioLen(); }
}