package RocnikovyProjektIFace.Drawing;

import DiagramSynthPackage.Synth.Generators.ClassicGenerators.Phase.SineGeneratorWithPhase;
import Rocnikovy_Projekt.Program;
import Rocnikovy_Projekt.ProgramTest;
import org.jtransforms.fft.DoubleFFT_1D;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;






public class FFTWindowRealAndImagPanel extends JPanel {
    public FFTWindowRealAndImagPanel(double[] song, int windowSize, int startIndex, int sampleRate, int numberOfChannels) {
        realPartPanel = new FFTWindowPartPanel(this, song, windowSize, startIndex, sampleRate, numberOfChannels);
        imagPartPanel = new FFTWindowPartPanel(this, song, windowSize, startIndex, sampleRate, numberOfChannels);
        int binCount = Program.getBinCountRealForward(windowSize);
        fftResult = new double[2 * windowSize]; // 2* because we will use complex FFT
        fft = new DoubleFFT_1D(windowSize);


        Program.calculateFFTRealForward(song, startIndex, windowSize, numberOfChannels, fft, fftResult);
        for(int i = 0; i < fftResult.length; i++) {      // TODO: nevim jestli je ta normalizace dobre
            fftResult[i] /= binCount;
        }
        Program.separateRealAndImagPart(realPartPanel.drawValues, imagPartPanel.drawValues, fftResult, windowSize);

        realPartPanel.setDrawValuesStrings();
        realPartPanel.setLastPartOfTooltip();
        imagPartPanel.setDrawValuesStrings();
        imagPartPanel.setLastPartOfTooltip();


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

    private final DoubleFFT_1D fft;
    private final double[] fftResult;
    private final FFTWindowPartPanel realPartPanel;
    private final FFTWindowPartPanel imagPartPanel;


    public void setMeasures(FFTWindowPartPanel partPanel, int bin, double newValue) {
        FFTWindowPanelAbstract otherPartPanel = getTheOtherPartPanel(partPanel);
        double squareValue = newValue * newValue;
        double otherPanelValue = otherPartPanel.getDrawValue(bin);
        double otherPanelValueSquare = otherPanelValue * otherPanelValue;

        double squaresSum = otherPanelValueSquare + squareValue;
        if(squaresSum > 1) {
            double newOtherPanelValue = Math.sqrt(1 - squareValue);
            otherPartPanel.setDrawValue(bin, newOtherPanelValue);
            otherPartPanel.repaint();
        }

        partPanel.setDrawValue(bin, newValue);
    }

    private FFTWindowPanelAbstract getTheOtherPartPanel(FFTWindowPanelAbstract partPanel) {
        FFTWindowPanelAbstract otherPartPanel;
        if(partPanel == imagPartPanel) {
            otherPartPanel = realPartPanel;
        }
        else {
            otherPartPanel = imagPartPanel;
        }

        return otherPartPanel;
    }


    public double[] getIFFTResult() {
        double[] realPart = realPartPanel.drawValues;
        double[] imagPart = imagPartPanel.drawValues;
        Program.connectRealAndImagPart(realPart, imagPart, fftResult);
        getComplexIFFT(fftResult, fft);

        return Arrays.copyOf(fftResult, fftResult.length);
    }

    public static void getComplexIFFT(double[] arr, DoubleFFT_1D fft) {
        fft.complexInverse(arr, true);
    }

    protected void setTheOtherPartSelectedBin(FFTWindowPanelAbstract partPanel, int bin) {
        FFTWindowPanelAbstract otherPartPanel = getTheOtherPartPanel(partPanel);
        otherPartPanel.setSelectedBin(bin);
        otherPartPanel.repaint();
    }
}