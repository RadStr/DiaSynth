package util.wave.drawing;

import util.swing.SwingUtils;
import util.wave.drawing.ifaces.DrawValuesSupplierIFace;
import test.ProgramTest;
import util.wave.ShiftBufferDouble;

import java.awt.*;

public class WaveDrawValuesIndividual extends WaveDrawValues {
    /**
     *
     * @param leftPixel is the left visible pixel. but in sense of whole wave
     * @param newVisibleWidth
     * @param totalWaveWidthInPixels
     * @param startIndexInValues
     * @param valueCount
     * @param windowCountToTheRight
     * @param mainWaveClass
     */
    public WaveDrawValuesIndividual(int leftPixel, int newVisibleWidth, int totalWaveWidthInPixels, int startIndexInValues, int valueCount,
                                    int windowCountToTheRight, DrawValuesSupplierIFace mainWaveClass) {
        super(newVisibleWidth, totalWaveWidthInPixels, startIndexInValues, valueCount, windowCountToTheRight, mainWaveClass);
        this.leftPixel = leftPixel;
        firstSamplePixel = leftPixel;
    }

    @Override
    public void performZoom(int newStartIndex, int newTotalWidth, int newTotalValuesCount) {
        waveResize(visibleWidth, newTotalWidth, newStartIndex, newTotalValuesCount);
    }

    @Override
    public void waveResize(int newVisibleWidth, int totalWaveWidthInPixels, int startIndexInValues, int valueCount) {
        visibleWidth = newVisibleWidth;
        pixelDifferenceBetweenSamples = mainWaveClass.calculatePixelDifferenceBetweenSamples(totalWaveWidthInPixels);
        int windowSize = (int) (visibleWidth / pixelDifferenceBetweenSamples);
        // + 1 because I have to add the 1 on the left for example 5 /5 == 1 but it contains 2 one on end and one start
        windowSize++;
        shiftBufferDouble = new ShiftBufferDouble(windowCountToTheRight, windowSize, this);

        leftPixel = mainWaveClass.getCurrentScroll();
        setFirstSamplePixel();
        fillWholeBuffer(startIndexInValues);
    }

    @Override
    public int getStartIndex() {
        return shiftBufferDouble.getStartIndex();
    }


    private double pixelDifferenceBetweenSamples;
    /**
     * Is the left visible pixel, but in sense of the whole wave.
     */
    private int leftPixel;

    /**
     * Is the pixel of the first sample in the sense of whole wave
     */
    private double firstSamplePixel;
    private void setFirstSamplePixel() {
        firstSamplePixel = leftPixel;
        double mod = leftPixel % pixelDifferenceBetweenSamples;
        if(mod != 0) {
            firstSamplePixel += (pixelDifferenceBetweenSamples - mod);
        }
    }


    public void shiftBuffer(int pixelShift) {
        leftPixel += pixelShift;

        // Because java does some resizing for the scroll by 1 pixel so sometimes the value of leftPixel goes to -1,
        // which makes the the samples moved 1 pixel to right - the 0th sample starts where should be the 1st one, which
        // would be fine if didn't have the tooltips, which are then 1 sample off.
        if(leftPixel < 0) {
            leftPixel = 0;
        }

        double oldFirstSamplePixel = firstSamplePixel;
        setFirstSamplePixel();
        double change = (firstSamplePixel - oldFirstSamplePixel) / pixelDifferenceBetweenSamples;
        int changeInt = (int)Math.round(change);
        if(changeInt != 0) {
            shiftBufferDouble.updateStartIndex(changeInt);
        }
    }


    private static final int DOT_RADIUS = 2;

    @Override
    public void drawSamples(Graphics g, int width, int height, int shiftY) {
        double currentVisiblePixel = firstSamplePixel - leftPixel;
        int halfHeight = height / 2;
        g.setColor(Color.black);
        g.drawLine(0, halfHeight + shiftY, width, halfHeight + shiftY);
        g.setColor(Color.blue);

        int index = shiftBufferDouble.getStartIndex();
        ProgramTest.debugPrint("drawSamples", shiftBufferDouble.getStartIndex(), shiftBufferDouble.getEndIndex(),
            currentVisiblePixel, width, pixelDifferenceBetweenSamples, shiftBufferDouble.getMaxRightIndex());

        int maxRightIndex = shiftBufferDouble.getMaxRightIndex();

        boolean hasDrawnPixelBefore = false;
        int previousVisiblePixelInt = -1;
        int previousHeight = -1;
        while(currentVisiblePixel < width && index < maxRightIndex) {
            // minus because it the lowest value has to have to be at the highest pixel
            int sampleHeight = -(int)(shiftBufferDouble.getValueAtIndex(index) * halfHeight);
            // Shift it so it starts in the middle
            sampleHeight += halfHeight + shiftY;
            int currentPixelInt = (int)currentVisiblePixel;

            if(pixelDifferenceBetweenSamples < 4) {
                if(hasDrawnPixelBefore) {
                    // TODO: Asi vymazat ty ostatni verze
                    // Version with rectangles
//                    if(previousHeight < halfHeight) {
//                        g.fillRect(previousVisiblePixelInt, previousHeight, currentPixelInt - previousVisiblePixelInt, halfHeight - previousHeight);
//                    }
//                    else {
//                        g.fillRect(previousVisiblePixelInt, halfHeight, currentPixelInt - previousVisiblePixelInt, previousHeight - halfHeight);
//                    }

                    // Version with connecting - Probably the best one - to doesn't show the individual samples but
                    // it is the best to look at
                    g.drawLine(previousVisiblePixelInt, previousHeight, currentPixelInt, sampleHeight);
                    // The next line is to show the sample locations, but it doesn't make sense since that is the old way of drawing
                    // and that was wrong because there isn't uniform space between the samples - so from time to time it is
                    // the pixels are a bit closer to itself and since there isn't much space between them, it is very noticeable
//                    g.drawLine(currentPixelInt, halfHeight, currentPixelInt, sampleHeight);

                    // Just dots
//                    g.drawLine(currentPixelInt, sampleHeight, currentPixelInt, sampleHeight); // Small dots
//                    StaticDrawMethodsClass.drawCenteredCircle(g, currentPixelInt, sampleHeight, 1); // Bigger dots
                }

                if(!hasDrawnPixelBefore) {
                    hasDrawnPixelBefore = true;
                }
                previousVisiblePixelInt = currentPixelInt;
                previousHeight = sampleHeight;
            }
            else {      // If there is enough space also draw circles at the end
                g.drawLine(currentPixelInt, halfHeight, currentPixelInt, sampleHeight);
                SwingUtils.drawCenteredCircle(g, currentPixelInt, sampleHeight, DOT_RADIUS);
            }

            currentVisiblePixel += pixelDifferenceBetweenSamples;
            index++;
        }
    }

    @Override
    public double convertFromBufferToPixel(int val) {
        return (pixelDifferenceBetweenSamples * val);
    }

    @Override
    public int convertFromPixelToBuffer(double val) {
        int retVal = (int)Math.round(val / pixelDifferenceBetweenSamples);
        retVal--;
        return retVal;
    }

    @Override
    public int convertFromPixelToIndexInAudio(double val) {
        int index = mainWaveClass.convertFromPixelToIndexInAudio(val);
        return index;
    }

    @Override
    public int convertFromBufferToIndexInAudio(int val) {
        int retVal = super.convertFromBufferToIndexInAudio(val);
        // 1 PIXEL BUG FIX
        // Added because when I start (when the first thing I draw are individuals) with drawing individual samples,
        // which means the number of samples is less than there are pixels in the wave visualiser,
        // then I had problem that when I started filling I started
        // from index 1 and it was because of this. I had 0 and by retVal-- I got -1 which I subtracted in the
        // convertToNonVisibleMostLeftIndexInAudio method, so I got +1 index than I really should have had.
        if(retVal > 0) {
            retVal--;
        }
        return retVal;
    }

    @Override
    public int calculateMinLeftIndexForShiftBuffer() {
        int indexInAudio = mainWaveClass.convertFromPixelToIndexInAudio(mainWaveClass.getCurrentScroll());
        int bufferMidIndex = shiftBufferDouble.getMiddleIndex();
        int minLeft = bufferMidIndex - indexInAudio;
        minLeft = Math.max(0, minLeft);
        return minLeft;
    }

    @Override
    public int calculateMaxRightIndexForShiftBuffer() {
        int currValIndex = mainWaveClass.convertFromPixelToIndexInAudio(mainWaveClass.getCurrentScroll());
        int newMaxRightIndex = mainWaveClass.getAudioLen() - currValIndex;
        newMaxRightIndex += shiftBufferDouble.getMiddleIndex();
        return newMaxRightIndex;
    }
}
