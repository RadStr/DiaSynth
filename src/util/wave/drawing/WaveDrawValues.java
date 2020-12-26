package util.wave.drawing;

import test.ProgramTest;
import util.wave.drawing.ifaces.DrawValuesSupplierIFace;
import util.wave.ShiftBufferBoundsIFace;
import util.wave.ShiftBufferDouble;
import util.wave.drawing.ifaces.WaveDrawValuesConverterIFace;

import java.awt.*;

public abstract class WaveDrawValues implements WaveDrawValuesConverterIFace, ShiftBufferBoundsIFace {
    public WaveDrawValues(int newVisibleWidth, int totalWaveWidthInPixels, int startIndexInValues, int valueCount,
                          int windowCountToTheRight, DrawValuesSupplierIFace mainWaveClass) {
        this.mainWaveClass = mainWaveClass;
        this.windowCountToTheRight = windowCountToTheRight;
        waveResize(newVisibleWidth, totalWaveWidthInPixels, startIndexInValues, valueCount);
    }

    protected DrawValuesSupplierIFace mainWaveClass;

    protected int windowCountToTheRight;

    protected int visibleWidth;
    protected ShiftBufferDouble shiftBufferDouble;

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

    public abstract void shiftBuffer(int pixelShift);

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
        mainWaveClass.fillBufferWithValuesToDraw(shiftBufferDouble.getBuffer(), bufferStartIndex, bufferEndIndex, startFillIndex);
    }


    public void updateBufferWithNewValuesOnLeft(int totalCopiedValCount) {
        int bufferStartIndex = shiftBufferDouble.getMinLeftIndex();
        int bufferEndIndex = shiftBufferDouble.getMaxRightIndex() - totalCopiedValCount;
        int beforeMidIndexCount = shiftBufferDouble.calculateNumberOfIndicesInBufferBeforeMidIndex();
        double startFillPixel = mainWaveClass.getCurrentScroll() - convertFromBufferToPixel(beforeMidIndexCount);
        int startFillIndex = mainWaveClass.convertFromPixelToIndexInAudio(startFillPixel);

        fillBuffer(startFillIndex, bufferStartIndex, bufferEndIndex);
    }


    public void updateBufferWithNewValuesOnRight(int totalCopiedValCount) {
        int bufferStartIndex = getBufferStartIndexOnRight(totalCopiedValCount);
        int bufferEndIndex = getBufferEndIndexOnRight();
        int distanceFromMidIndex = shiftBufferDouble.getMiddleIndex() - totalCopiedValCount;
        double startFillPixel = mainWaveClass.getCurrentScroll() - convertFromBufferToPixel(distanceFromMidIndex);
        int startFillIndex = mainWaveClass.convertFromPixelToIndexInAudio(startFillPixel);

        fillBuffer(startFillIndex, bufferStartIndex, bufferEndIndex);
    }

    private int getBufferStartIndexOnRight(int copiedValCount) {
        return shiftBufferDouble.getMinLeftIndex() + copiedValCount;
    }

    private int getBufferEndIndexOnRight() {
        return shiftBufferDouble.getMaxRightIndex();
    }

    @Override
    public int getNewValCountOnRight(int copiedValCount) {
        return getBufferEndIndexOnRight() - getBufferStartIndexOnRight(copiedValCount);
    }


    /**
     *
     * @param startIndexInAudio is the start index in audio - the index of the most left currently visible sample.
     */
    protected void fillWholeBuffer(int startIndexInAudio) {
        shiftBufferDouble.resetStartIndex();
        ProgramTest.debugPrint("fill whole buffer", startIndexInAudio, mainWaveClass.getCurrentScroll(), mainWaveClass.getMaxScroll());
        shiftBufferDouble.setBounds();
        int minLeftIndex = shiftBufferDouble.getMinLeftIndex();
        startIndexInAudio = convertToNonVisibleMostLeftIndexInAudio(startIndexInAudio);

        ProgramTest.debugPrint("fill whole buffer2", startIndexInAudio, minLeftIndex, shiftBufferDouble.getMaxRightIndex());
        fillBuffer(startIndexInAudio, minLeftIndex, shiftBufferDouble.getMaxRightIndex());
    }

    /**
     * Shift it to the left because the first visible value will be at middle index
     * @param startIndexInAudio is the start index in audio - the index of the most left currently visible sample.
     * @return Returns the index in audio corresponding to the most left index in shift buffer.
     */
    private int convertToNonVisibleMostLeftIndexInAudio(int startIndexInAudio) {
        int indexCountBeforeMidIndex = shiftBufferDouble.calculateNumberOfIndicesInBufferBeforeMidIndex();
        startIndexInAudio -= convertFromBufferToIndexInAudio(indexCountBeforeMidIndex);
        return startIndexInAudio;
    }
}