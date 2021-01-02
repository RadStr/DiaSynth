package player;

import test.ProgramTest;
import player.wave.WavePanel;
import util.Time;
import util.swing.SwingUtils;

import javax.swing.*;
import java.awt.*;

public class TimestampsPanel extends JPanel {
    public TimestampsPanel(AudioPlayerPanel audioPlayerPanel) {
        this.audioPlayerPanel = audioPlayerPanel;
    }


    private AudioPlayerPanel audioPlayerPanel;
    /**
     * Says draw timestamp label every MARKS_PER_TIMESTAMP commas.
     * More in depth description, which says the same:
     * Says how many marks (commas) are there per one timestamp label, it means that there is comma with
     * the label and then there are 2 other commas and after that is another timestamp label.
     */
    public static final int MARKS_PER_TIMESTAMP = 3;


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTimestamps(g);
    }

    private void drawTimestamps(Graphics g) {
        boolean isTimeInSecs = true;
        double numOfSecs;
        int visibleWaveWidth;
        int waveWidth;
        int scrollX;
        int waveStartX;

        Color color = Color.black;
        g.setColor(color);

        int sampleRate = audioPlayerPanel.getOutputSampleRate();

        int sampleLen = audioPlayerPanel.getDoubleWaveLength();        // Range in doubles
        if (sampleLen == -1) {
            numOfSecs = 180;
            sampleLen = (int) (numOfSecs * sampleRate);
            scrollX = 0;
            waveStartX = 415;
            visibleWaveWidth = audioPlayerPanel.getWidth() - waveStartX;
            waveWidth = Math.max(visibleWaveWidth, WavePanel.START_DEFAULT_WAVE_WIDTH_IN_PIXELS);
        }
        else {
            numOfSecs = sampleLen / (double) sampleRate;
            visibleWaveWidth = audioPlayerPanel.getWavesVisibleWidth();
            waveWidth = audioPlayerPanel.getWaveWidth();
            scrollX = audioPlayerPanel.getCurrentHorizontalScroll();
            waveStartX = audioPlayerPanel.getWaveStartX();
        }

        int labelCount;
        try {
            labelCount = waveWidth / visibleWaveWidth;
            labelCount *= 4;
        }
        catch (ArithmeticException e) {
            return;
        }

        double timeJump = numOfSecs / labelCount;
        int timestampsMultiples = 5;
        int timeJumpInt;
        timeJumpInt = (int) timeJump;
        if (timeJumpInt >= timestampsMultiples) {
            // We will put it to be the closest multiple of timestampsMultiples (for example if timestampsMultiples == 5
            // and timeJumpInt = 7 then timeJumpInt = 10)
            if (timeJumpInt % timestampsMultiples != 0) {
                timeJumpInt += timestampsMultiples - (timeJumpInt % timestampsMultiples);
            }
        }
        else {
            if (timeJumpInt < 1) {   // Need to calculate time in milliseconds
                isTimeInSecs = false;
                timeJumpInt = (int) (1000 * timeJump);
                numOfSecs *= 1000;      // Convert seconds to milliseconds
                while (timeJumpInt == 0) {           // If it is too small then keep lowering the labelCount
                    labelCount /= 2;
                    timeJump = numOfSecs / labelCount;      // numOfSecs are already milliseconds
                    timeJumpInt = (int) timeJump;
                }
            }
        }


        double pixelJump = 0;
        int minimumPixelJump = 20;
        // Starts from index = 1
        int index = 1;
        int oldTimeJumpInt = timeJumpInt;
        // Now check if it fits the window, if it doesn't that take only every index-th label
        for (; index <= labelCount; index++) {
            timeJumpInt = index * oldTimeJumpInt;
            pixelJump = timeJumpInt / numOfSecs;        // If the song is short enough, then numOfSecs = numOfMillisecs
            pixelJump *= waveWidth;
            pixelJump /= MARKS_PER_TIMESTAMP;
            if (pixelJump >= minimumPixelJump) {
                break;
            }
        }

        drawTimestamps(g, waveStartX, scrollX, pixelJump, isTimeInSecs, color,
                       MARKS_PER_TIMESTAMP, timeJumpInt, visibleWaveWidth);
    }


    private void drawTimestamps(Graphics g, int waveStartX, int scrollX, double pixelJump, boolean isTimeInSecs,
                                Color color, int timestampCountBetweenTwoMainTimeStamps,
                                int timeJumpInt, int visibleWaveWidth) {
        int h = this.getHeight();
        int yForStampWithLabel = h - h / 3;
        int yForStampWithoutLabel = h - h / 5;
        String timeString;
        int timeInt = 0;
        int lineStartY;

        g.setColor(color);

        int startIndex = (int) (scrollX / pixelJump);
        int i = startIndex;
        // + timestampCountBetweenTwoMainTimeStamps - 1 because Every time I go over the label I have to add to the timeInt
        // Basically when when startIndex is 1 I am after the first label so the next label I draw will be the one after that
        timeInt = (startIndex + timestampCountBetweenTwoMainTimeStamps - 1) / timestampCountBetweenTwoMainTimeStamps *
                  timeJumpInt;
        double x = waveStartX - (scrollX % pixelJump);
        int endX = waveStartX + visibleWaveWidth;

        while (x < endX) {
            int xInt = (int) x;

            if (i % timestampCountBetweenTwoMainTimeStamps == 0) {
                lineStartY = yForStampWithLabel;

                if (isTimeInSecs) {
                    timeString = Time.convertSecondsToTime(timeInt, -1);
                }
                else {
                    timeString = Time.convertMillisecondsToTime(timeInt, -1);
                }

                // So I don't draw timelines for the static things such as mix part, etc. I draw it just for the wave
                if (xInt >= waveStartX) {
                    SwingUtils.drawStringWithDefinedMidLoc(g, color, timeString, xInt, lineStartY);
                }
                timeInt += timeJumpInt;
            }
            else {
                lineStartY = yForStampWithoutLabel;
            }

            // So I don't draw time lines for the static things such as mix part, etc. I draw it just for the wave
            if (xInt >= waveStartX) {
                g.drawLine(xInt, lineStartY, xInt, this.getHeight());
            }

            x += pixelJump;
            i++;
        }
    }
}