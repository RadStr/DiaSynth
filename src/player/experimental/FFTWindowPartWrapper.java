package player.experimental;

import javax.swing.*;
import java.awt.*;

public class FFTWindowPartWrapper extends DrawWrapperBase {
    public FFTWindowPartWrapper(FFTWindowRealAndImagWrapper controlPanel,
                                int windowSize,
                                int sampleRate,
                                boolean isEditable,
                                Color backgroundColor,
                                boolean shouldDrawLabelsAtTop) {
        this(new FFTWindowPartPanel(controlPanel, windowSize, sampleRate, isEditable,
                                    backgroundColor, shouldDrawLabelsAtTop), -1, 1);
    }

    public FFTWindowPartWrapper(FFTWindowRealAndImagWrapper controlPanel, int windowSize,
                                double freqJump, boolean isEditable, Color backgroundColor,
                                boolean shouldDrawLabelsAtTop) {
        this(new FFTWindowPartPanel(controlPanel, windowSize, freqJump, isEditable, backgroundColor,
                                    shouldDrawLabelsAtTop), -1, 1);
    }

    private FFTWindowPartWrapper(FFTWindowPartPanel fftWindowPartPanel, double minValue, double maxValue) {
        super(fftWindowPartPanel, minValue, maxValue);
        this.fftWindowPartPanel = fftWindowPartPanel;
    }


    protected FFTWindowPartPanel fftWindowPartPanel;

    @Override
    public void setDrawPanel(DrawPanel drawPanel) {
        super.setDrawPanel(drawPanel);
        fftWindowPartPanel = (FFTWindowPartPanel) drawPanel;
    }

    @Override
    public void addMenus(JMenuBar menuBar, WaveAdderIFace waveAdder) {
        // EMPTY
    }
}
