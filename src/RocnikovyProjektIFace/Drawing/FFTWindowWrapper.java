package RocnikovyProjektIFace.Drawing;

import javax.swing.*;
import java.awt.*;

public class FFTWindowWrapper extends DrawWrapperBase {
    public FFTWindowWrapper(double[] audio,
                            int windowSize, int startIndex, double freqJump,
                            int numberOfChannels, boolean isEditable,
                            Color backgroundColor,
                            double minValue, double maxValue) {
        this(new FFTWindowPanel(audio, windowSize, startIndex, freqJump,
                        numberOfChannels, isEditable, backgroundColor), minValue, maxValue);
    }

    public FFTWindowWrapper(double[] audio,
                            int windowSize, int startIndex, int sampleRate,
                            int numberOfChannels, boolean isEditable,
                            Color backgroundColor,
                            double minValue, double maxValue) {
        this(new FFTWindowPanel(audio, windowSize, startIndex, sampleRate,
                        numberOfChannels, isEditable, backgroundColor), minValue, maxValue);
        }


    private FFTWindowWrapper(FFTWindowPanel fftPanel, double minValue, double maxValue) {
        super(fftPanel, minValue, maxValue);
        this.fftPanel = fftPanel;
    }

    private final FFTWindowPanel fftPanel;

    public double[] getIFFTResult(boolean setImagPartToZero) {
        return fftPanel.getIFFTResult(setImagPartToZero);
    }

    @Override
    public void addMenus(JMenuBar menuBar, AddWaveIFace waveAdder) {
        //        TODO: MENU
    }
}
