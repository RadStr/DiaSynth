package RocnikovyProjektIFace;

import Rocnikovy_Projekt.Program;
import Rocnikovy_Projekt.ProgramTest;

import javax.swing.*;
import java.awt.*;

public class TimestampsPanel extends JPanel {

    private AudioPlayerPanelIFaceImplementation audioPlayerPanel;
    public static final int TIMESTAMP_COUNT_BETWEEN_TWO_MAIN_TIMESTAMPS = 3;

    public TimestampsPanel(AudioPlayerPanelIFaceImplementation audioPlayerPanel) {
        this.audioPlayerPanel = audioPlayerPanel;
    }



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
        if(sampleLen == -1) {
            numOfSecs = 180;
            sampleLen = (int)(numOfSecs * sampleRate);
            scrollX = 0;
            waveStartX = 415;
            visibleWaveWidth = audioPlayerPanel.getWidth() - waveStartX;
            waveWidth = AudioWavePanelOnlyWave.START_DEFAULT_WAVE_WIDTH_IN_PIXELS;
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
            labelCount = waveWidth / visibleWaveWidth;        // TODO: Maybe tune this parameter - the multiply factor (Was constant 20, then 60)
            labelCount *= 4;
        } catch (ArithmeticException e) {
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
                while(timeJumpInt == 0) {           // If it is too small then keep lowering the labelCount
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
        for (; index <= labelCount; index++) {       // Now check if it fits the window, if it doesn't that take only every index-th label
            timeJumpInt = index * oldTimeJumpInt;
            pixelJump = timeJumpInt / numOfSecs;        // If the song is short enough, then numOfSecs = numOfMillisecs
            pixelJump *= waveWidth;
            labelCount = (int) (waveWidth / pixelJump);
            labelCount *= TIMESTAMP_COUNT_BETWEEN_TWO_MAIN_TIMESTAMPS;
            pixelJump /= TIMESTAMP_COUNT_BETWEEN_TWO_MAIN_TIMESTAMPS;
            if (pixelJump >= minimumPixelJump) {
                break;
            }

            ProgramTest.debugPrint("Timestamp finding pixel jump", index, labelCount, pixelJump, timeJumpInt, numOfSecs);
        }

        labelCount++;


        drawTimestamps(g, waveStartX, scrollX, pixelJump, isTimeInSecs, color,
            TIMESTAMP_COUNT_BETWEEN_TWO_MAIN_TIMESTAMPS, timeJumpInt, visibleWaveWidth);


//        labelCount += 2;
//        String timeString;
//        // TODO: Rozhodne nechci x = 30 takhle defaultne ... i kdyz mozna chci
//        for (int i = 0, nextX = x + spaceSizeBetweenTimestampsInPixels; i < labelCount; x = nextX, nextX += spaceSizeBetweenTimestampsInPixels, time += timeJump, i++) {
//            g.drawLine(x, 0, x, y);
//            timeString = Program.convertSecondsToTime((int) time);
//// TODO: DEBUG            System.out.println(time + "\t" + timeString + "\t" + timeJump + "\t" + numOfSecs);
//            //Program.drawStringWithSpace(g, color, timeString, x / 2, nextX / 2, y, fontMetrics);
//            g.setColor(color);
//            g.drawString(timeString, x, y);
//
////
////            g.setColor(Color.black);
////            int textLen = fontMetrics.stringWidth(binFreqs[bin]);
////            int textStart = (currBinWidth - textLen) / 2;
////            g.drawString(binFreqs[bin], currX + textStart, windowHeight);
//        }
    }


    private void drawTimestamps(Graphics g, int waveStartX, int scrollX, double pixelJump, boolean isTimeInSecs,
                                Color color, int timestampCountBetweenTwoMainTimeStamps, int timeJumpInt, int visibleWaveWidth) {
        int h = this.getHeight();
        int yForStampWithLabel = h - h / 3;
        int yForStampWithoutLabel = h - h / 5;
        String timeString;
        int timeInt = 0;
        int lineStartY;

        g.setColor(color);

        int startIndex = (int)(scrollX / pixelJump);
        int i = startIndex;
        // + timestampCountBetweenTwoMainTimeStamps - 1 because Every time I go over the label I have to add to the timeInt
        // Basically when when startIndex is 1 I am after the first label so the next label I draw will be the one after that
        timeInt = (startIndex + timestampCountBetweenTwoMainTimeStamps - 1) / timestampCountBetweenTwoMainTimeStamps * timeJumpInt;
        double x = waveStartX - (scrollX % pixelJump);
        int endX = waveStartX + visibleWaveWidth;

        while (x < endX) {
            int xInt = (int) x;

            if (i % timestampCountBetweenTwoMainTimeStamps == 0) {
                lineStartY = yForStampWithLabel;

                if (isTimeInSecs) {
                    timeString = Program.convertSecondsToTime(timeInt, -1);
                } else {
                    timeString = Program.convertMillisecondsToTime(timeInt, -1);
                }
                if (xInt >= waveStartX) {        // So I don't draw timelines for the static things such as mix part, etc. I draw it just for the wave
                    Program.drawStringWithDefinedMidLoc(g, color, timeString, xInt, lineStartY);
                }
                timeInt += timeJumpInt;
            } else {
                lineStartY = yForStampWithoutLabel;
            }

            if (xInt >= waveStartX) {            // So I don't draw timelines for the static things such as mix part, etc. I draw it just for the wave
                g.drawLine(xInt, lineStartY, xInt, this.getHeight());
            }

// TODO: Debug print
//            System.out.println("------------------------------------------------");
//            System.out.println(x + "\t" + pixelJump + "\t" + waveWidth);
// TODO: Debug print
            x += pixelJump;
            i++;
        }


// TODO: Debug - just testing if the timestamps are correct, that means if I add to the last timestamp the remaining time I will end up at end of wave panel
//        x -= pixelJump;
//        if ((labelCount - 1) % timestampCountBetweenTwoMainTimeStamps == 0) {
//            timeInt -= timeJumpInt;
//        }
//        double remainingTime = numOfSecs - timeInt;
//        double ratioOfRemainingTimeToJump = remainingTime / timeJumpInt;
//        ratioOfRemainingTimeToJump *= timestampCountBetweenTwoMainTimeStamps;       // because one pixelJump is == timeJumpInt / timestampCountBetweenTwoMainTimeStamps
//        double lastX = ratioOfRemainingTimeToJump * pixelJump + x;
// TODO: DEBUG
//        System.out.println(x + "\t" + waveWidth + "\t" + pixelJump + "\t" + timeInt + "\t" + remainingTime + "\t" + timeJumpInt + "\t" +
//            ratioOfRemainingTimeToJump + "\t" + lastX + "\t" + audioPlayerPanel.getWaveEndX());
// TODO: DEBUG
    }


    // We have to take in consideration the size of the scroll
    @Override
    public Dimension getPreferredSize() {
        Dimension prefSize = super.getPreferredSize();
        // I have to do this. Because when resizing width of the player, the waves suddenly move up and so the timestamps
        // suddenly don't have enough space and aren't correctly drawn. I guess that it is because the buttons don't have enough space
        // So they ignore this preferred size and when they have enough width to satisfy the preferred sizes of buttons,
        // The panel takes into consideration this preferred size and moves the waves up. Before that it ignores this preferred size.
        prefSize.height += audioPlayerPanel.getPanelWithWavesHorizontalScrollSize() + 8;
        // TODO: PROGRAMO
        // Another problem when I move the volume there is crackling
        // Also make some simple sample smoothing - if there is some operation - then there may be sample skip at the start and end of the changed part
        // TODO: PROGRAMO
        return prefSize;
    }
}
