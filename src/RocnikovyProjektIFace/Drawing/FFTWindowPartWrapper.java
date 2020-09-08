package RocnikovyProjektIFace.Drawing;

import javax.swing.*;
import java.awt.*;

public class FFTWindowPartWrapper extends DrawWrapperBase {
    public FFTWindowPartWrapper(FFTWindowRealAndImagPanel controlPanel,
                                double[] audio,
                                int windowSize,
                                int startIndex,
                                int sampleRate,
                                int numberOfChannels,
                                boolean isEditable,
                                Color backgroundColor) {
        this(new FFTWindowPartPanel(controlPanel, audio, windowSize, startIndex,
                        sampleRate, numberOfChannels, isEditable, backgroundColor), -1, 1);
    }

    public FFTWindowPartWrapper(FFTWindowRealAndImagPanel controlPanel,
                                double[] audio,
                                int windowSize,
                                int startIndex,
                                double freqJump,
                                int numberOfChannels,
                                boolean isEditable,
                                Color backgroundColor) {
        this(new FFTWindowPartPanel(controlPanel, audio, windowSize, startIndex,
                freqJump, numberOfChannels, isEditable, backgroundColor), -1, 1);
    }

    private FFTWindowPartWrapper(FFTWindowPartPanel fftWindowPartPanel, double minValue, double maxValue) {
        super(fftWindowPartPanel, minValue, maxValue);
        this.fftWindowPartPanel = fftWindowPartPanel;
    }



    protected final FFTWindowPartPanel fftWindowPartPanel;

    @Override
    public void addMenus(JMenuBar menuBar, AddWaveIFace waveAdder) {
//        TODO: MENU
    }
}
