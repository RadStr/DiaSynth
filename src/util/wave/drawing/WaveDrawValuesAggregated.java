package util.wave.drawing;

import test.ProgramTest;
import util.wave.drawing.ifaces.DrawValuesSupplierIFace;
import util.wave.Range;
import util.wave.ShiftBufferDouble;

import java.awt.*;

public class WaveDrawValuesAggregated extends WaveDrawValues {
    public WaveDrawValuesAggregated(int newVisibleWidth, int totalWaveWidthInPixels, int startIndexInValues, int valueCount,
                                    int windowCountToTheRight, DrawValuesSupplierIFace mainWaveClass) {
        super(newVisibleWidth, totalWaveWidthInPixels, startIndexInValues, valueCount, windowCountToTheRight, mainWaveClass);
        windowRange = new Range();
    }

    @Override
    public void waveResize(int newVisibleWidth, int totalWaveWidthInPixels, int startIndexInValues, int valueCount) {
        if (visibleWidth != newVisibleWidth || shiftBufferDouble == null) {
            visibleWidth = newVisibleWidth;
            int windowSize = 2 * newVisibleWidth;        // 2 * because it contains min and max
            shiftBufferDouble = new ShiftBufferDouble(windowCountToTheRight, windowSize, this);
            fillWholeBuffer(startIndexInValues);
        }
        else {
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
        return shiftBufferDouble.getStartIndex();
    }


    public void shiftBuffer(int pixelShift) {
        pixelShift *= 2;        // Because min and max
        shiftBufferDouble.updateStartIndex(pixelShift);
    }


    @Override
    public void drawSamples(Graphics g, int width, int height, int shiftY) {
        g.setColor(Color.blue);

        shiftBufferDouble.setRange(windowRange);
        int previousX = -1;
        int previousYMin = -1;
        int previousYMax = -1;
        int halfHeight = height / 2;
        for (int i = windowRange.start, x = 0; i < windowRange.end; i++, x++) {
            double minY = shiftBufferDouble.getValueAtIndex(i);
            i++;
            double maxY = shiftBufferDouble.getValueAtIndex(i);

            // minus because it the lowest value has to have to be at the highest pixel
            int sampleMinHeightSample = -(int) (minY * halfHeight);
            int sampleMaxHeightSample = -(int) (maxY * halfHeight);
            // Shift it so it starts in the middle
            sampleMinHeightSample += halfHeight + shiftY;
            sampleMaxHeightSample += halfHeight + shiftY;


            g.drawLine(x, sampleMinHeightSample, x, sampleMaxHeightSample);
            if (x > 0) {
                // Now check if we need to connect the the previous sample (sample aggregation) with next one
                if (previousYMax < sampleMinHeightSample) {             // If the previous line is below the current
                    g.drawLine(previousX, previousYMax, x, sampleMinHeightSample);
                }
                else if (previousYMin > sampleMaxHeightSample) {        // If the previous line is above the current
                    g.drawLine(previousX, previousYMin, x, sampleMaxHeightSample);
                }
            }

            previousX = x;
            previousYMin = sampleMinHeightSample;
            previousYMax = sampleMaxHeightSample;
        }


        g.setColor(Color.black);
        halfHeight += shiftY;
        g.drawLine(0, halfHeight, width, halfHeight);
    }

    @Override
    public double convertFromBufferToPixel(int val) {
        return val / (double) 2;
    }

    @Override
    public int convertFromPixelToBuffer(double val) {
        return (int) (2 * val);
    }

    @Override
    public int convertFromPixelToIndexInAudio(double val) {
        return (int) val;
    }


    @Override
    public int calculateMinLeftIndexForShiftBuffer() {
        int currScroll = mainWaveClass.getCurrentScroll();
        int midIndex = shiftBufferDouble.getMiddleIndex();

        int minLeft = midIndex - convertFromPixelToBuffer(currScroll);
        minLeft = Math.max(0, minLeft);
        return minLeft;
    }

    @Override
    public int calculateMaxRightIndexForShiftBuffer() {
        int pixelCount = mainWaveClass.getTotalWidth() - mainWaveClass.getCurrentScroll();
        int maxRightIndex = convertFromPixelToBuffer(pixelCount);
        maxRightIndex += shiftBufferDouble.getMiddleIndex();
        return maxRightIndex;
    }
}
