package RocnikovyProjektIFace;

import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.Program;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class AudioWavePanelOnlyWaveMouseListener implements MouseListener, MouseMotionListener {
    private WaveMainPanel waveMainPanel;

    // TODO: I will do it this way - user may choose the audioFormat to which will be all the audio waves converted - I could
    // make it like the waves will be in some different formats and in the mixing they will be converted to the
    // output audioFormat but that is too difficult I think
// TODO: Pri nacteni nove vlny nastavim output audioFormat na ten audioFormat te vlny a pri pridani nove k teto ji pretransformuju na tehle audioFormat
// TODO: Idealne nactu ve vstupnim formatu a az pri samotnym mixovani provedu prevedeni na novy audioFormat

    // TODO: Budu muset brat k uvahu ze muzu scrollovat doleva, doprava a jeste zoomovat
    private int leftestVisibleSampleInWindow;       // If scrolled or zoomed, it changes, it is the literal sample, not the pixel
    private int widthOfVisibleAudioWindowInSamples; // If zoomed, changes


    // frame variants (used for the normalized double array)
    private int leftestVisibleFrameInWindow;        // If scrolled or zoomed, it changes, it is the literal sample, not the pixel
    private int widthOfVisibleAudioWindowInFrames;  // If zoomed, changes




    public AudioWavePanelOnlyWaveMouseListener(WaveMainPanel waveMainPanel) {
        this.waveMainPanel = waveMainPanel;
// TODO: PROGRAMO
//        // TODO: Budu muset brat k uvahu ze muzu scrollovat doleva, doprava a jeste zoomovat
//        leftestVisibleSampleInWindow = 0;       // If scrolled or zoomed, it changes, it is the literal sample, not the pixel
//        int frameSize = waveMainPanel.getNumberOfChannelsInOutputFormat();
//        leftestVisibleFrameInWindow = leftestVisibleSampleInWindow / frameSize;
//        widthOfVisibleAudioWindowInSamples = waveMainPanel.getDoubleWaveLength();
//        widthOfVisibleAudioWindowInFrames = widthOfVisibleAudioWindowInSamples / frameSize;
// TODO: PROGRAMO
    }

    //////////////// MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {    // mouseClicked is when the mouse button has been pressed and released.
        if(!SwingUtilities.isRightMouseButton(e)) {     // If not right click (not popup)
            int waveWidth = waveMainPanel.getWaveWidth();
            int songLenInFrames = waveMainPanel.getDoubleWaveLength();

            int x = e.getX();
            if (x < 0) {
                x = 0;          // TODO: Nevim jestli je tohle uplne chovani co chci, tj. ze kdyz kliknu pred zacatek vlny tak se to spusti od zacatku
            }
            x += waveMainPanel.getCurrentHorizontalScroll();
            double time = x / (double) waveWidth;

// TODO: DEBUG            System.out.println(currSampleUserSelected + "\t" + currSampleRaw + "\t" + (program.frameSize - (currSampleRaw % program.frameSize)) + "\t" + x);
// TODO: PROGRAMO
//        int currSampleUserSelected = Program.convertToMultipleDown(currSampleRaw, waveMainPanel.getNumberOfChannelsInOutputFormat());
            //int currSampleRaw = (int)(time * waveMainPanel.getDoubleWaveLength());
            int currSampleUserSelected = (int) (time * songLenInFrames);
// TODO: PROGRAMO
            waveMainPanel.setCurrSampleUserSelected(currSampleUserSelected);
// TODO: DEBUG            System.out.println(currSampleUserSelected + "\t" + currSampleRaw + "\t" + (program.frameSize - (currSampleRaw % program.frameSize)) + "\t" + x);

            // TODO: just for debugging test, if the timestamps are drawed correctly
            // TODO:            currSampleUserSelected = 80 * program.getSizeOfOneSecInBytes();
            // TODO:

            double timeLineX = currSampleUserSelected / (double) songLenInFrames;
            timeLineX *= waveWidth;
            waveMainPanel.setTimeLineXUserSelected(timeLineX);

            int currPlayTimeInMillis = DoubleWave.convertSampleToMillis(currSampleUserSelected, waveMainPanel.getOutputSampleRate());
            waveMainPanel.setCurrPlayTimeInMillis(currPlayTimeInMillis);
            waveMainPanel.setUserClickedWave(true);
            waveMainPanel.performUserClickedWaveVariableSetPaused();


//        shouldMarkPart = false;
//
//        this.setSampleToBePlayedToClickedLocation(e.getX());
//        this.repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {    // mousePressed is when the mouse button has been pressed (doesn't need release).
        if(!SwingUtilities.isRightMouseButton(e)) {     // If not right click (not popup)
            waveMainPanel.setShouldMarkPart(false);
            waveMainPanel.setMarkStartXVariablesBasedOnPixel(e.getX());
//        this.setSampleToBePlayedToClickedLocation(startX);        // TODO: Zatim to zpusobi jen exception
            waveMainPanel.repaintPanelWithMultipleWaves();

            waveMainPanel.mouseButtonPressed();
        }
        else {
            waveMainPanel.setLastRightPressMouseEvent(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        waveMainPanel.stoppedDraggingWave();
        waveMainPanel.mouseButtonUnpressed();
    }


    //////////////// MouseMotionListener
    @Override
    public void mouseEntered(MouseEvent e) {
        // EMPTY
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // EMPTY
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(!SwingUtilities.isRightMouseButton(e)) {     // If not right click (not popup)
            waveMainPanel.draggingWave();
            waveMainPanel.setShouldMarkPart(true);
            waveMainPanel.setMarkEndXVariablesBasedOnPixel(e.getX());
            waveMainPanel.repaintPanelWithMultipleWaves();
        }
    }


    private StringBuilder tooltip = new StringBuilder(200);        // Set to huge value, so that we don't have to keep creating new underlying array
    private static final char[][] TOOLTIP_STRINGS  = new char[][] {
        "<html>Sample index: ".toCharArray(), "<br>Value: ".toCharArray(), "<br>Sample time: ".toCharArray(),
                "<br>Wave size in pixels: ".toCharArray(), "</html>".toCharArray()
    };

    private int insertToTooltip(char[] chars, int index) {
        tooltip.insert(index, chars);
        return index + chars.length;
    }

    private int insertToTooltip(String s, int index) {
        tooltip.insert(index, s);
        return index + s.length();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO: Asi neni nutny - uz to je pokryty v tech predchozich tohle tu bylo kvuli tomu vykreslovani hodnot samplu ale to budu stejne delat pres toolbox
        int sampleIndex = convertXToSampleIndex(e.getX());
        if(sampleIndex < waveMainPanel.getSongLen()) {
            double value = waveMainPanel.getNthSample(sampleIndex);
            String sampleIndexString = VerticalReferencesPanel.getStringInt(sampleIndex);
            String valueString = VerticalReferencesPanel.getStringDouble(value);
            int sampleTimeInMillis = waveMainPanel.convertSampleToMillis(sampleIndex);
            String timeInMilis = Program.convertMillisecondsToTime(sampleTimeInMillis, -1);

//            int currIndex = 0;
//            currIndex = insertToTooltip(TOOLTIP_STRINGS[0], currIndex);
//            currIndex = insertToTooltip(sampleIndexString, currIndex);
//            currIndex = insertToTooltip(TOOLTIP_STRINGS[1], currIndex);
//            currIndex = insertToTooltip(valueString, currIndex);
//            currIndex = insertToTooltip(TOOLTIP_STRINGS[2], currIndex);
//            currIndex = insertToTooltip(String.audioFormat("%.3f", sampleTimeInMillis / (double)1000), currIndex);
//            currIndex = insertToTooltip(TOOLTIP_STRINGS[3], currIndex);
//            currIndex = insertToTooltip(waveMainPanel.getWave().getSize().toString(), currIndex);
//            currIndex = insertToTooltip(TOOLTIP_STRINGS[4], currIndex);
//            tooltip.setLength(currIndex);
            tooltip.setLength(0);
            tooltip.append(TOOLTIP_STRINGS[0]);
            tooltip.append(sampleIndexString);
            tooltip.append(TOOLTIP_STRINGS[1]);
            tooltip.append(valueString);
            tooltip.append(TOOLTIP_STRINGS[2]);
            tooltip.append(timeInMilis);
            tooltip.append(TOOLTIP_STRINGS[3]);
            tooltip.append(waveMainPanel.getWave().getSize().toString());
            tooltip.append(TOOLTIP_STRINGS[4]);
            waveMainPanel.setWaveTooltipText(tooltip.toString());

// TODO: DEBUG
//            waveMainPanel.setWaveTooltipText("<html>Index: " + sampleIndexString + "<br>Value: " + valueString +
//                "<br>Wave total X: " + waveMainPanel.getWave().getX() +
//                "<br>Wave total Y: " + waveMainPanel.getY() +
//                "<br>Pixel: " + e.getX() +
//                "<br>MouseLoc: " + e.getPoint() +
//                "<br><br>PrefSize everything: " + waveMainPanel.getPreferredSize() +
//                "<br>Size everything: " + waveMainPanel.getSize() +
//                "<br>MinSize everything: " + waveMainPanel.getMinimumSize() +
//                "<br><br>PrefSize wave: " + waveMainPanel.getWave().getPreferredSize() +
//                "<br>Size wave: " + waveMainPanel.getWave().getSize() +
//                "<br>MinSize wave: " + waveMainPanel.getWave().getMinimumSize() +
//                "<br>bot divider: " + waveMainPanel.getBotDividerLoc() +
//                "</html>");
// TODO: DEBUG
            waveMainPanel.repaintPanelWithMultipleWaves();
        }
    }



    private int convertXToSampleIndex(int x) {
        x += waveMainPanel.getCurrentHorizontalScroll();
// TODO: DEBUG
//        ProgramTest.debugPrint("Mouse x:", x);
// TODO: DEBUG
        //double numberOfSamplesPerPixel = widthOfVisibleAudioWindowInFrames / (double)waveMainPanel.getWaveWidth();
        double numberOfSamplesPerPixel = waveMainPanel.getAudioLenInFrames() / (double)waveMainPanel.getWaveWidth();
        int sampleIndex = (int)(numberOfSamplesPerPixel * x);
//        sampleIndex += leftestVisibleFrameInWindow;
        return sampleIndex;
    }
}
