package str.rad.player.wave;

import str.rad.util.audio.wave.DoubleWave;
import str.rad.util.Time;

import javax.swing.*;
import java.awt.event.*;

public class WavePanelMouseListener implements MouseListener, MouseMotionListener {
    public WavePanelMouseListener(WaveMainPanel waveMainPanel) {
        this.waveMainPanel = waveMainPanel;
    }


    private static boolean canChangeTooltip = true;
    private static final Timer TOOLTIP_TIMER;
    public static void startTooltipTimer() {
        TOOLTIP_TIMER.start();
    }
    public static void stopTooltipTimer() {
        TOOLTIP_TIMER.stop();
    }
    static {
        TOOLTIP_TIMER = new Timer(100,
                                  new ActionListener() {
                                      @Override
                                      public void actionPerformed(ActionEvent e) {
                                          canChangeTooltip = true;
                                      }
                                  });
    }

    private WaveMainPanel waveMainPanel;


    //////////////// MouseListener
    // mouseClicked is when the mouse button has been pressed and released.
    @Override
    public void mouseClicked(MouseEvent e) {
        if (!SwingUtilities.isRightMouseButton(e)) {     // If not right click (not popup)
            int waveWidth = waveMainPanel.getWaveWidth();
            int songLenInFrames = waveMainPanel.getDoubleWaveLength();

            int x = e.getX();
            if (x < 0) {
                x = 0;
            }
            x += waveMainPanel.getCurrentHorizontalScroll();
            double time = x / (double) waveWidth;

            int userSelectedSample = (int) (time * songLenInFrames);
            double timeLineX = userSelectedSample / (double) songLenInFrames;
            timeLineX *= waveWidth;
            int currPlayTimeInMillis = DoubleWave.convertSampleToMillis(userSelectedSample, waveMainPanel.getOutputSampleRate());
            waveMainPanel.processUserClickedWaveEvent(timeLineX, userSelectedSample, currPlayTimeInMillis);
        }
    }

    // mousePressed is when the mouse button has been pressed (doesn't need release).
    @Override
    public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isRightMouseButton(e)) {     // If not right click (not popup)
            waveMainPanel.setShouldMarkPart(false);
            waveMainPanel.setMarkStartXVariablesBasedOnPixel(e.getX());
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
        if (!SwingUtilities.isRightMouseButton(e)) {     // If not right click (not popup)
            waveMainPanel.draggingWave();
            waveMainPanel.setShouldMarkPart(true);
            waveMainPanel.setMarkEndXVariablesBasedOnPixel(e.getX());
            waveMainPanel.repaintPanelWithMultipleWaves();
        }
    }


    // Set to huge value, so that we don't have to keep creating new underlying array
    private StringBuilder tooltip = new StringBuilder(200);
    private static final char[][] TOOLTIP_STRINGS = new char[][]{
            "<html>Index: ".toCharArray(), "<br>Value: ".toCharArray(),
            "<br>Time: ".toCharArray(), "</html>".toCharArray()
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
        if(canChangeTooltip) {
            canChangeTooltip = false;

            int sampleIndex = convertXToSampleIndex(e.getX());
            if (sampleIndex < waveMainPanel.getSongLen()) {
                double value = waveMainPanel.getNthSample(sampleIndex);
                String sampleIndexString = VerticalReferencesPanel.getStringInt(sampleIndex);
                String valueString = VerticalReferencesPanel.getStringDouble(value);
                int sampleTimeInMillis = waveMainPanel.convertSampleToMillis(sampleIndex);
                String timeInMillis = Time.convertMillisecondsToTime(sampleTimeInMillis, -1);

                tooltip.setLength(0);
                tooltip.append(TOOLTIP_STRINGS[0]);
                tooltip.append(sampleIndexString);
                tooltip.append(TOOLTIP_STRINGS[1]);
                tooltip.append(valueString);
                tooltip.append(TOOLTIP_STRINGS[2]);
                tooltip.append(timeInMillis);
                tooltip.append(TOOLTIP_STRINGS[3]);
                waveMainPanel.setWaveTooltipText(tooltip.toString());

                waveMainPanel.repaintPanelWithMultipleWaves();
            }
        }
    }


    private int convertXToSampleIndex(int x) {
        x += waveMainPanel.getCurrentHorizontalScroll();
        double numberOfSamplesPerPixel = waveMainPanel.getAudioLenInFrames() / (double) waveMainPanel.getWaveWidth();
        int sampleIndex = (int) (numberOfSamplesPerPixel * x);
        return sampleIndex;
    }
}
