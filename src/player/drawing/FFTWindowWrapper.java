package player.drawing;

import player.plugin.ifaces.PluginDefaultIFace;
import player.plugin.ifaces.PluginParametersAnnotation;
import player.plugin.PluginJPanelBasedOnAnnotations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class FFTWindowWrapper extends DrawWrapperBase {
    public FFTWindowWrapper(double[] audio,
                            int windowSize, int startIndex, double freqJump,
                            boolean isEditable, Color backgroundColor,
                            double minValue, double maxValue,
                            boolean shouldDrawLabelsAtTop) {
        this(new FFTWindowPanel(audio, windowSize, startIndex, freqJump,
                        isEditable, backgroundColor, shouldDrawLabelsAtTop),
                minValue, maxValue);
    }

    public FFTWindowWrapper(double[] audio,
                            int windowSize, int startIndex,
                            int sampleRate, boolean isEditable,
                            Color backgroundColor,
                            double minValue, double maxValue,
                            boolean shouldDrawLabelsAtTop) {
        this(new FFTWindowPanel(audio, windowSize, startIndex, sampleRate,
                        isEditable, backgroundColor, shouldDrawLabelsAtTop),
                minValue, maxValue);
    }


    private FFTWindowWrapper(FFTWindowPanel fftPanel, double minValue, double maxValue) {
        super(fftPanel, minValue, maxValue);
        this.fftPanel = fftPanel;
    }

    // Note this is one of the few reasons why should I just use casting on the member of derived class, now that I have
    // this view variable also have to manipulate it, so it really isn't worth it, also it can get very confusing because of that
    private FFTWindowPanel fftPanel;
    @Override
    public void setDrawPanel(DrawPanel drawPanel) {
        super.setDrawPanel(drawPanel);
        fftPanel = (FFTWindowPanel)drawPanel;
    }

    public double[] getIFFTResult(boolean setImagPartToZero, int periodCount) {
        return fftPanel.getIFFTResult(setImagPartToZero, periodCount);
    }


    @Override
    public void addMenus(JMenuBar menuBar, AddWaveIFace waveAdder) {
        if(!fftPanel.getIsEditable()) {
            JMenu menu = new JMenu("Options");
            menuBar.add(menu);
            JCheckBoxMenuItem showRelativeCheckbox = new JCheckBoxMenuItem("Show relative");
            showRelativeCheckbox.setSelected(false);
            menu.add(showRelativeCheckbox);
            showRelativeCheckbox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {
                        fftPanel.makeRelativeValues();
                    }
                    else {
                        fftPanel.makeAbsoluteValues();
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
                    FFTWindowOptionsDialogPanel classWithValues = new FFTWindowOptionsDialogPanel(fftPanel);
                    PluginJPanelBasedOnAnnotations dialogPanel = new PluginJPanelBasedOnAnnotations(classWithValues,
                            classWithValues.getClass());

                    int result = JOptionPane.showConfirmDialog(null, dialogPanel,
                            "Dialog: " + classWithValues.getPluginName(), JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                        FFTWindowPanel newFFTPanel = (FFTWindowPanel) fftPanel.createNewFFTPanel(
                                classWithValues.getWindowSize(), classWithValues.getShouldChangeWindowSize(),
                                classWithValues.getSampleRate(), classWithValues.getShouldChangeSampleRate());
                        setDrawPanel(newFFTPanel);
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

                    if (result == JOptionPane.OK_OPTION) {
                        double[] wave = fftPanel.getIFFTResult(classWithValues.getShouldSetImagPartToZero(),
                                classWithValues.getPeriodCount());
                        waveAdder.addWave(wave);
                    }
                }
            });

            addReset(menu);
        }
    }


    private static class FFTWindowOptionsDialogPanel implements PluginDefaultIFace {
        public FFTWindowOptionsDialogPanel(FFTWindowPanelAbstract fftPanel) {
            this.windowSize = fftPanel.WINDOW_SIZE;
            this.sampleRate = (int)Math.round(windowSize * fftPanel.FREQ_JUMP);
        }


        @PluginParametersAnnotation(name = "Window size:",
                lowerBound = FFTWindowPanel.MIN_WINDOW_SIZE_STRING,
                upperBound = FFTWindowPanel.MAX_WINDOW_SIZE_STRING,
                parameterTooltip = "Controls number of size of the FFT window.")
        private int windowSize;
        public int getWindowSize() {
            return windowSize;
        }

        @PluginParametersAnnotation(name = "Change window size:", defaultValue = "TRUE",
                parameterTooltip = "If set to true, the window size will be changed after ending the dialog with ok, " +
                        "otherwise it won't be changed")
        private boolean shouldChangeWindowSize;
        public boolean getShouldChangeWindowSize() {
            return shouldChangeWindowSize;
        }

        @PluginParametersAnnotation(name = "Sample rate:", lowerBound = "1",
                parameterTooltip = "Controls the sample rate of the input samples.")
        private int sampleRate;
        public int getSampleRate() {
            return sampleRate;
        }

        @PluginParametersAnnotation(name = "Change sample rate:", defaultValue = "TRUE",
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
        public boolean isUsingDefaultJPanel() {
            return true;
        }

        @Override
        public String getPluginName() {
            return "FFT window parameters panel";
        }
    }


    private static class IFFTDialogPanel implements PluginDefaultIFace {
        @PluginParametersAnnotation(name = "Set imaginary part to zero:", defaultValue = "FALSE",
                parameterTooltip = "If set to true, the imaginary part of FFT result will be set to 0. Otherwise it will be set to random number in" +
                        "such way that the measures are correct.")
        private boolean shouldSetImagPartToZero;
        public boolean getShouldSetImagPartToZero() {
            return shouldSetImagPartToZero;
        }


        @PluginParametersAnnotation(name = "Period count:", lowerBound = "1", defaultValue = "1",
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
        public boolean isUsingDefaultJPanel() {
            return true;
        }

        @Override
        public String getPluginName() {
            return "Perform IFFT";
        }
    }
}
