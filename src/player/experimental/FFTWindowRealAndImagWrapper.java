package player.experimental;

import plugin.util.AnnotationPanel;
import plugin.PluginBaseIFace;
import plugin.PluginParameterAnnotation;
import org.jtransforms.fft.DoubleFFT_1D;
import util.Utilities;
import util.audio.FFT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;




public class FFTWindowRealAndImagWrapper extends JPanel implements DrawWrapperIFace {
    public FFTWindowRealAndImagWrapper(double[] song, int windowSize, int startIndex,
                                       int sampleRate, boolean isEditable,
                                       Color backgroundColorRealPart, Color backgroundColorImagPart,
                                       boolean shouldDrawLabelsAtTop) {
        // I just take the bins before nyquist for real and imaginary part respectively.
        realPartPanel = new FFTWindowPartWrapper(this, windowSize, sampleRate, isEditable,
                                                 backgroundColorRealPart, shouldDrawLabelsAtTop);
        imagPartPanel = new FFTWindowPartWrapper(this, windowSize, sampleRate, isEditable,
                                                 backgroundColorImagPart, shouldDrawLabelsAtTop);

        int binCount = FFT.getBinCountRealForward(windowSize);
        fftResult = new double[2 * windowSize];     // 2* because we will use complex FFT
        fft = new DoubleFFT_1D(windowSize);


        if(song != null) {
            FFT.calculateFFTRealForward(song, startIndex, windowSize, 1, fft, fftResult);
        }

        for(int i = 0; i < fftResult.length; i++) {
            fftResult[i] /= (2 * binCount);         // TODO: NORM
        }
        FFT.separateRealAndImagPart(realPartPanel.fftWindowPartPanel.DRAW_VALUES,
                imagPartPanel.fftWindowPartPanel.DRAW_VALUES, fftResult, windowSize);
        realPartPanel.setDrawPanel(realPartPanel.fftWindowPartPanel);
        imagPartPanel.setDrawPanel(imagPartPanel.fftWindowPartPanel);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(realPartPanel);
        add(new JPanel() {
            private final Dimension prefSize = new Dimension(1, SPACE_BETWEEN_PARTS);
            @Override
            public Dimension getPreferredSize() {
                return prefSize;
            }
        });
        add(imagPartPanel);
        add(new JPanel() {
            private final Dimension prefSize = new Dimension(1, SPACE_BETWEEN_PARTS);
            @Override
            public Dimension getPreferredSize() {
                return prefSize;
            }
        });
    }

    public static final int SPACE_BETWEEN_PARTS = 4;

    private final DoubleFFT_1D fft;
    private final double[] fftResult;
    private final FFTWindowPartWrapper realPartPanel;
    private final FFTWindowPartWrapper imagPartPanel;



    private Dimension minSize = new Dimension();
    @Override
    public Dimension getMinimumSize() {
        minSize.width = realPartPanel.getMinimumSize().width;
        minSize.height = 2 * realPartPanel.getMinimumSize().height + 2 * SPACE_BETWEEN_PARTS;
        return minSize;
    }

    private Dimension prefSize = new Dimension();
    @Override
    public Dimension getPreferredSize() {
        prefSize.width = super.getPreferredSize().width;

        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JPanel contentPane = (JPanel) topFrame.getContentPane();
        Insets frameInsets = topFrame.getInsets();
        // For some reason have to make it smaller. I choose to make it smaller by frameInsets.bottom, but could be anything > 5
        prefSize.height = contentPane.getHeight() - frameInsets.bottom - frameInsets.bottom;
        return prefSize;
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
        double[] realPart = realPartPanel.fftWindowPartPanel.DRAW_VALUES;
        double[] imagPart = imagPartPanel.fftWindowPartPanel.DRAW_VALUES;
        FFT.connectRealAndImagPart(realPart, imagPart, fftResult);
        for(int i = 0; i < fftResult.length; i++) {
            fftResult[i] *= 2 * realPart.length;            // TODO: NORM
        }
        getComplexIFFT(fftResult, fft);

        // Only the real part is valid, the imaginary is equal to zero.
        double[] realFFTPart = new double[fftResult.length / 2];
        FFT.separateOnlyRealPart(realFFTPart, fftResult, fftResult.length);
        FFTWindowPanel.normalize(realFFTPart);
        double[] ifftResult = Utilities.copyArr(realFFTPart, realFFTPart.length, periodCount);
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
    public void addMenus(JMenuBar menuBar, WaveAdderIFace waveAdder) {
        if(!realPartPanel.drawPanel.getIsEditable()) {
            JMenu menu = new JMenu("Options");
            menuBar.add(menu);
            JCheckBoxMenuItem showRelativeCheckbox = new JCheckBoxMenuItem("Show relative");
            showRelativeCheckbox.setSelected(false);
            menu.add(showRelativeCheckbox);
            showRelativeCheckbox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {
                        realPartPanel.fftWindowPartPanel.makeRelativeValues();
                        imagPartPanel.fftWindowPartPanel.makeRelativeValues();
                    }
                    else {
                        realPartPanel.fftWindowPartPanel.makeAbsoluteValues();
                        imagPartPanel.fftWindowPartPanel.makeAbsoluteValues();
                    }
                }
            });
        }
        else {
            JMenu menu = new JMenu("Options");
            menuBar.add(menu);
            JMenuItem optionsMenuItem = new JMenuItem("Set fft window parameters");
            menu.add(optionsMenuItem);
            optionsMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FFTWindowOptionsDialogPanel classWithValues = new FFTWindowOptionsDialogPanel(realPartPanel.fftWindowPartPanel);
                    AnnotationPanel dialogPanel = new AnnotationPanel(classWithValues, classWithValues.getClass());

                    int result = JOptionPane.showConfirmDialog(null, dialogPanel,
                            "Dialog: " + classWithValues.getPluginName(), JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
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
                    AnnotationPanel performIFFTDialog = new AnnotationPanel(classWithValues,
                            classWithValues.getClass());

                    int result = JOptionPane.showConfirmDialog(null, performIFFTDialog,
                            "Dialog: " + classWithValues.getPluginName(), JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                        double[] wave = getIFFTResult(classWithValues.getPeriodCount());
                        waveAdder.addWave(wave);
                    }
                }
            });

            addFullReset(menu);
            addRealReset(menu);
            addImagReset(menu);
        }
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


    private static class FFTWindowOptionsDialogPanel implements PluginBaseIFace {
        public FFTWindowOptionsDialogPanel(FFTWindowPanelAbstract fftPanel) {
            this.windowSize = fftPanel.WINDOW_SIZE;
            this.sampleRate = (int)Math.round(windowSize * fftPanel.FREQ_JUMP);
        }


        @PluginParameterAnnotation(name = "Window size:",
                lowerBound = FFTWindowPanel.MIN_WINDOW_SIZE_STRING,
                upperBound = FFTWindowPanel.MAX_WINDOW_SIZE_STRING,
                parameterTooltip = "Controls number of size of the FFT window.")
        private int windowSize;
        public int getWindowSize() {
            return windowSize;
        }

        @PluginParameterAnnotation(name = "Change window size:", defaultValue = "TRUE",
                parameterTooltip = "If set to true, the window size will be changed after ending the dialog with ok, otherwise it won't be changed")
        private boolean shouldChangeWindowSize;
        public boolean getShouldChangeWindowSize() {
            return shouldChangeWindowSize;
        }

        @PluginParameterAnnotation(name = "Sample rate:", lowerBound = "1",
                parameterTooltip = "Controls the sample rate of the input samples.")
        private int sampleRate;
        public int getSampleRate() {
            return sampleRate;
        }

        @PluginParameterAnnotation(name = "Change sample rate:", defaultValue = "TRUE",
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
        public boolean isUsingPanelCreatedFromAnnotations() {
            return true;
        }

        @Override
        public String getPluginName() {
            return "FFT window parameters panel";
        }
    }


    private static class IFFTDialogPanel implements PluginBaseIFace {
        @PluginParameterAnnotation(name = "Period count:", lowerBound = "1", defaultValue = "1",
                parameterTooltip = "Controls the number of periods (repetitions) of IFFT result")
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
        public boolean isUsingPanelCreatedFromAnnotations() {
            return true;
        }

        @Override
        public String getPluginName() {
            return "Perform IFFT";
        }
    }
}