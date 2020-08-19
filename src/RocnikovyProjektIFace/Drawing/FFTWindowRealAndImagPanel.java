package RocnikovyProjektIFace.Drawing;

import Rocnikovy_Projekt.Program;
import org.jtransforms.fft.DoubleFFT_1D;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class FFTWindowRealAndImagPanel extends JPanel {
    public FFTWindowRealAndImagPanel(double[] song, int windowSize, int startIndex, int sampleRate, int numberOfChannels) {
        realPartPanel = new FFTWindowPartPanel(this, song, windowSize, startIndex, sampleRate, numberOfChannels);
        imagPartPanel = new FFTWindowPartPanel(this, song, windowSize, startIndex, sampleRate, numberOfChannels);
        int binCount = Program.getBinCountRealForward(windowSize);
        fftResult = new double[binCount];
        fft = new DoubleFFT_1D(binCount);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 1;

        add(realPartPanel, c);

        c.gridy = 1;
        add(imagPartPanel, c);
    }

    private DoubleFFT_1D fft;
    private double[] fftResult;
    private FFTWindowPartPanel realPartPanel;
    private FFTWindowPartPanel imagPartPanel;


    public void setMeasures(FFTWindowPartPanel partPanel, int bin, double newValue) {
        FFTWindowPanel otherPartPanel;
        if(partPanel == imagPartPanel) {
            otherPartPanel = realPartPanel;
        }
        else {
            otherPartPanel = imagPartPanel;
        }

        double squareValue = newValue * newValue;
        double otherPanelValue = otherPartPanel.getDrawValue(bin);
        double otherPanelValueSquare = otherPanelValue * otherPanelValue;

        double squaresSum = otherPanelValueSquare + squareValue;
        if(squaresSum > 1) {
            double newOtherPanelValue = Math.sqrt(squaresSum - squareValue);
            otherPartPanel.setDrawValue(bin, newOtherPanelValue);
            otherPartPanel.repaint();
        }
    }


    public double[] getIFFTResult() {
        double[] realPart = realPartPanel.drawValues;
        double[] imagPart = imagPartPanel.drawValues;
        Program.convertFFTArrs(realPart, imagPart, fftResult);
        FFTWindowPanel.getIFFT(fftResult, fft);

        return Arrays.copyOf(fftResult, fftResult.length);
    }
}
