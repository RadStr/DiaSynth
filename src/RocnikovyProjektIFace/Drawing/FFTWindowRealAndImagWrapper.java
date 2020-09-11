package RocnikovyProjektIFace.Drawing;

import DiagramSynthPackage.Synth.Generators.ClassicGenerators.Phase.SineGeneratorWithPhase;
import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginDefaultIFace;
import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginParametersAnnotation;
import RocnikovyProjektIFace.AudioPlayerPlugins.PluginJPanelBasedOnAnnotations;
import Rocnikovy_Projekt.Program;
import Rocnikovy_Projekt.ProgramTest;
import org.jtransforms.fft.DoubleFFT_1D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;







public class FFTWindowRealAndImagWrapper extends JPanel implements DrawWrapperIFace {
    public FFTWindowRealAndImagWrapper(double[] song, int windowSize, int startIndex, int sampleRate,
                                       int numberOfChannels, boolean isEditable,
                                       Color backgroundColorRealPart, Color backgroundColorImagPart,
                                       boolean shouldDrawLabelsAtTop) {
        realPartPanel = new FFTWindowPartWrapper(this, song, windowSize, startIndex, sampleRate,
                numberOfChannels, isEditable, backgroundColorRealPart, shouldDrawLabelsAtTop);
        imagPartPanel = new FFTWindowPartWrapper(this, song, windowSize, startIndex, sampleRate,
                numberOfChannels, isEditable, backgroundColorImagPart, shouldDrawLabelsAtTop);

        int binCount = Program.getBinCountRealForward(windowSize);
        fftResult = new double[2 * windowSize]; // 2* because we will use complex FFT
        fft = new DoubleFFT_1D(windowSize);


        Program.calculateFFTRealForward(song, startIndex, windowSize, numberOfChannels, fft, fftResult);
//        TODO: nevim jestli je ta normalizace dobre
        for(int i = 0; i < fftResult.length; i++) {
            fftResult[i] /= binCount;
        }
        Program.separateRealAndImagPart(realPartPanel.fftWindowPartPanel.drawValues,
                imagPartPanel.fftWindowPartPanel.drawValues, fftResult, windowSize);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(realPartPanel);
        add(new JPanel() {
            private final Dimension prefSize = new Dimension(1, 5);
            @Override
            public Dimension getPreferredSize() {
                return prefSize;
            }
        });
        add(imagPartPanel);
    }

    private final DoubleFFT_1D fft;
    private final double[] fftResult;
    private final FFTWindowPartWrapper realPartPanel;
    private final FFTWindowPartWrapper imagPartPanel;



    private Dimension minSize = new Dimension();
    @Override
    public Dimension getMinimumSize() {
        minSize.width = realPartPanel.getMinimumSize().width;
        minSize.height = 2 * realPartPanel.getMinimumSize().height;
        return minSize;
    }



    public void setBinValues(FFTWindowPartPanel partPanel, int bin, double newValue) {
        FFTWindowPanelAbstract otherPartPanel = getTheOtherPartPanel(partPanel);
        double squareValue = newValue * newValue;
        double otherPanelValue = otherPartPanel.getDrawValue(bin);
        double otherPanelValueSquare = otherPanelValue * otherPanelValue;

        double squaresSum = otherPanelValueSquare + squareValue;
        if(squaresSum > 1) {
            double newOtherPanelValue = Math.sqrt(1 - squareValue);
            newOtherPanelValue *= Math.signum(otherPanelValue);
            otherPartPanel.setDrawValue(bin, newOtherPanelValue);
            otherPartPanel.repaint();
        }

        partPanel.setDrawValue(bin, newValue);
    }

    private FFTWindowPanelAbstract getTheOtherPartPanel(FFTWindowPanelAbstract partPanel) {
        FFTWindowPanelAbstract otherPartPanel;
        if(partPanel == imagPartPanel.fftWindowPartPanel) {
            otherPartPanel = realPartPanel.fftWindowPartPanel;
        }
        else {
            otherPartPanel = imagPartPanel.fftWindowPartPanel;
        }

        return otherPartPanel;
    }


    public double[] getIFFTResult(int periodCount) {
        double[] realPart = realPartPanel.fftWindowPartPanel.drawValues;
        double[] imagPart = imagPartPanel.fftWindowPartPanel.drawValues;
        Program.connectRealAndImagPart(realPart, imagPart, fftResult);
        getComplexIFFT(fftResult, fft);

        double[] ifftResult = Program.copyArr(fftResult, fftResult.length, periodCount);
        return ifftResult;
    }

    public static void getComplexIFFT(double[] arr, DoubleFFT_1D fft) {
        fft.complexInverse(arr, true);
    }

    protected void setTheOtherPartSelectedBin(FFTWindowPanelAbstract partPanel, int bin) {
        FFTWindowPanelAbstract otherPartPanel = getTheOtherPartPanel(partPanel);
        otherPartPanel.setSelectedBin(bin);
        otherPartPanel.repaint();
    }

    @Override
    public void addMenus(JMenuBar menuBar, AddWaveIFace waveAdder) {
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);
        JMenuItem optionsMenuItem = new JMenuItem("Set fft window parameters");
        menu.add(optionsMenuItem);
        optionsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FFTWindowOptionsDialogPanel classWithValues = new FFTWindowOptionsDialogPanel(realPartPanel.fftWindowPartPanel);
                PluginJPanelBasedOnAnnotations dialogPanel = new PluginJPanelBasedOnAnnotations(classWithValues,
                        classWithValues.getClass());

                int result = JOptionPane.showConfirmDialog(null, dialogPanel,
                        "Dialog: " + classWithValues.getPluginName(), JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if(result == JOptionPane.OK_OPTION) {
                    FFTWindowPartPanel part = (FFTWindowPartPanel) realPartPanel.fftWindowPartPanel.createNewFFTPanel(
                            classWithValues.getWindowSize(), classWithValues.getShouldChangeWindowSize(),
                            classWithValues.getSampleRate(), classWithValues.getShouldChangeSampleRate());
                    realPartPanel.setDrawPanel(part);

                    part = (FFTWindowPartPanel) imagPartPanel.fftWindowPartPanel.createNewFFTPanel(
                            classWithValues.getWindowSize(), classWithValues.getShouldChangeWindowSize(),
                            classWithValues.getSampleRate(), classWithValues.getShouldChangeSampleRate());
                    imagPartPanel.setDrawPanel(part);
                }
            }
        });

        menu = new JMenu("Action");
        menuBar.add(menu);
        JMenuItem actionMenuItem = new JMenuItem("Perform IFFT");
        menu.add(actionMenuItem);
        actionMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IFFTDialogPanel classWithValues = new IFFTDialogPanel();
                PluginJPanelBasedOnAnnotations performIFFTDialog = new PluginJPanelBasedOnAnnotations(classWithValues,
                        classWithValues.getClass());

                int result = JOptionPane.showConfirmDialog(null, performIFFTDialog,
                        "Dialog: " + classWithValues.getPluginName(), JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if(result == JOptionPane.OK_OPTION) {
                    double[] wave = getIFFTResult(classWithValues.getPeriodCount());
                    waveAdder.addWave(wave);
                }
            }
        });

        addFullReset(menu);
        addRealReset(menu);
        addImagReset(menu);
    }


    private void addFullReset(JMenu menu) {
        JMenuItem resetMenuItem = new JMenuItem("Reset both parts");
        resetMenuItem.setToolTipText("Resets both real and imaginary part to neutral values");

        menu.add(resetMenuItem);
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realPartPanel.drawPanel.resetValues();
                imagPartPanel.drawPanel.resetValues();
            }
        });
    }
    private void addRealReset(JMenu menu) {
        JMenuItem resetMenuItem = new JMenuItem("Reset real part");
        resetMenuItem.setToolTipText("Resets the real part to neutral value");

        menu.add(resetMenuItem);
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realPartPanel.drawPanel.resetValues();
            }
        });
    }
    private void addImagReset(JMenu menu) {
        JMenuItem resetMenuItem = new JMenuItem("Reset imaginary part");
        resetMenuItem.setToolTipText("Resets the imaginary part to neutral value");

        menu.add(resetMenuItem);
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagPartPanel.drawPanel.resetValues();
            }
        });
    }


    private static class FFTWindowOptionsDialogPanel implements PluginDefaultIFace {
        public FFTWindowOptionsDialogPanel(FFTWindowPanelAbstract fftPanel) {
            this.windowSize = fftPanel.WINDOW_SIZE;
            // 2* because otherwise it is Nyquist frequency
            this.sampleRate = 2 * (int)Math.round((Program.getBinCountRealForward(windowSize) - 1) * fftPanel.FREQ_JUMP);
        }


        @PluginParametersAnnotation(lowerBound = "1", parameterTooltip = "Controls number of size of the FFT window.")
        private int windowSize;
        public int getWindowSize() {
            return windowSize;
        }

        @PluginParametersAnnotation(defaultValue = "TRUE",
                parameterTooltip = "If set to true, the window size will be changed after ending the dialog with ok, otherwise it won't be changed")
        private boolean shouldChangeWindowSize;
        public boolean getShouldChangeWindowSize() {
            return shouldChangeWindowSize;
        }

        @PluginParametersAnnotation(lowerBound = "1", parameterTooltip = "Controls the sample rate of the input samples.")
        private int sampleRate;
        public int getSampleRate() {
            return sampleRate;
        }

        @PluginParametersAnnotation(defaultValue = "TRUE",
                parameterTooltip = "If set to true, the sample rate of the original wave for purposes of fft will be changed after ending the dialog with ok, otherwise it won't be changed")
        private boolean shouldChangeSampleRate;
        public boolean getShouldChangeSampleRate() {
            return shouldChangeSampleRate;
        }

        /**
         * @return Returns true if the operation needs parameters - so user needs to put them to the JPanel.
         * If it returns false, then it doesn't need parameters from user and the operation can start immediately
         */
        @Override
        public boolean shouldWaitForParametersFromUser() {
            return true;
        }

        /**
         * This parameter matters only when shouldWaitForParametersFromUser returns true
         *
         * @return
         */
        @Override
        public boolean isUsingDefaultJPane() {
            return true;
        }

        @Override
        public String getPluginName() {
            return "FFT window parameters panel";
        }
    }


    private static class IFFTDialogPanel implements PluginDefaultIFace {
        @PluginParametersAnnotation(lowerBound = "1", defaultValue = "1", parameterTooltip = "Controls the number of periods (repetitions) of IFFT result")
        private int periodCount;
        public int getPeriodCount() {
            return periodCount;
        }


        /**
         * @return Returns true if the operation needs parameters - so user needs to put them to the JPanel.
         * If it returns false, then it doesn't need parameters from user and the operation can start immediately
         */
        @Override
        public boolean shouldWaitForParametersFromUser() {
            return true;
        }

        /**
         * This parameter matters only when shouldWaitForParametersFromUser returns true
         *
         * @return
         */
        @Override
        public boolean isUsingDefaultJPane() {
            return true;
        }

        @Override
        public String getPluginName() {
            return "Perform IFFT";
        }
    }
}