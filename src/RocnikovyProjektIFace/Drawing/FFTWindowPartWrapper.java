package RocnikovyProjektIFace.Drawing;

import javax.swing.*;
import java.awt.*;

public class FFTWindowPartWrapper extends DrawWrapperBase {
    public FFTWindowPartWrapper(FFTWindowRealAndImagWrapper controlPanel,
                                double[] audio,
                                int windowSize,
                                int startIndex,
                                int sampleRate,
                                int numberOfChannels,
                                boolean isEditable,
                                Color backgroundColor,
                                boolean shouldDrawLabelsAtTop) {
        this(new FFTWindowPartPanel(controlPanel, audio, windowSize, startIndex,
                        sampleRate, numberOfChannels, isEditable, backgroundColor, shouldDrawLabelsAtTop),
                -1, 1);
    }

    public FFTWindowPartWrapper(FFTWindowRealAndImagWrapper controlPanel,
                                double[] audio,
                                int windowSize,
                                int startIndex,
                                double freqJump,
                                int numberOfChannels,
                                boolean isEditable,
                                Color backgroundColor,
                                boolean shouldDrawLabelsAtTop) {
        this(new FFTWindowPartPanel(controlPanel, audio, windowSize, startIndex,
                freqJump, numberOfChannels, isEditable, backgroundColor, shouldDrawLabelsAtTop),
                -1, 1);
    }

    private FFTWindowPartWrapper(FFTWindowPartPanel fftWindowPartPanel, double minValue, double maxValue) {
        super(fftWindowPartPanel, minValue, maxValue);
        this.fftWindowPartPanel = fftWindowPartPanel;
    }



    protected FFTWindowPartPanel fftWindowPartPanel;

    @Override
    public void setDrawPanel(DrawPanel drawPanel) {
        super.setDrawPanel(drawPanel);
        fftWindowPartPanel = (FFTWindowPartPanel)drawPanel;
    }

    @Override
    public void addMenus(JMenuBar menuBar, AddWaveIFace waveAdder) {
//        JMenu menu = new JMenu("Options");
//        menuBar.add(menu);
//        JMenuItem optionsMenuItem = new JMenuItem("Set Time");
//        menu.add(optionsMenuItem);
//        optionsMenuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                TimeOptionsDialogPanel classWithValues = new TimeOptionsDialogPanel(timeWaveDrawPanel);
//                PluginJPanelBasedOnAnnotations dialogPanel = new PluginJPanelBasedOnAnnotations(classWithValues,
//                        classWithValues.getClass());
//
//                int result = JOptionPane.showConfirmDialog(null, dialogPanel,
//                        "Dialog: " + classWithValues.getPluginName(), JOptionPane.OK_CANCEL_OPTION,
//                        JOptionPane.PLAIN_MESSAGE);
//
//                if(result == JOptionPane.OK_OPTION) {
//                    timeWaveDrawPanel.setTimeInMs(classWithValues.timeInMs);
//                }
//            }
//        });
//
//        menu = new JMenu("Action");
//        menuBar.add(menu);
//        JMenuItem actionMenuItem = new JMenuItem("Perform action");
//        menu.add(actionMenuItem);
//        actionMenuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                TimeActionDialogPanel classWithValues = new TimeActionDialogPanel(timeWaveDrawPanel, waveAdder);
//                PluginJPanelBasedOnAnnotations dialogPanel = new PluginJPanelBasedOnAnnotations(classWithValues,
//                        classWithValues.getClass());
//
//                int result = JOptionPane.showConfirmDialog(null, dialogPanel,
//                        "Dialog: " + classWithValues.getPluginName(), JOptionPane.OK_CANCEL_OPTION,
//                        JOptionPane.PLAIN_MESSAGE);
//
//                if(result == JOptionPane.OK_OPTION) {
//                    timeWaveDrawPanel.setTimeInMs(classWithValues.getTimeInMs());
//                    double[] wave = getNPeriods(classWithValues.getSampleRate(), classWithValues.getPeriodCount());
//                    waveAdder.addWave(wave);
//                }
//            }
//        });
    }


//    private static class TimeActionDialogPanel extends TimeOptionsDialogPanel implements PluginDefaultIFace {
//        public TimeActionDialogPanel(TimeWaveDrawPanel timeWaveDrawPanel, AddWaveIFace waveAdder) {
//            super(timeWaveDrawPanel);
//            sampleRate = waveAdder.getOutputSampleRate();
//        }
//
//
//        @PluginParametersAnnotation(lowerBound = "0", parameterTooltip = "Controls the sample rate of the drawn wave")
//        private int sampleRate;
//
//        public int getSampleRate() {
//            return sampleRate;
//        }
//
//        @PluginParametersAnnotation(lowerBound = "1", defaultValue = "1", parameterTooltip = "Controls the number of periods (repetitions) of drawn wave")
//        private int periodCount;
//        public int getPeriodCount() {
//            return periodCount;
//        }
//
//        @Override
//        public String getPluginName() {
//            return "Generate drawn wave";
//        }
//    }
//
//
//    private static class TimeOptionsDialogPanel implements PluginDefaultIFace {
//        public TimeOptionsDialogPanel(TimeWaveDrawPanel timeWaveDrawPanel) {
//            timeInMs = timeWaveDrawPanel.getTimeInMs();
//        }
//
//        @PluginParametersAnnotation(lowerBound = "1", defaultValue = "500", parameterTooltip = "Controls the length of the drawn wave")
//        private int timeInMs;
//        public int getTimeInMs() {
//            return timeInMs;
//        }
//
//        /**
//         * @return Returns true if the operation needs parameters - so user needs to put them to the JPanel.
//         * If it returns false, then it doesn't need parameters from user and the operation can start immediately
//         */
//        @Override
//        public boolean shouldWaitForParametersFromUser() {
//            return true;
//        }
//
//        /**
//         * This parameter matters only when shouldWaitForParametersFromUser returns true
//         *
//         * @return
//         */
//        @Override
//        public boolean isUsingDefaultJPane() {
//            return true;
//        }
//
//        @Override
//        public String getPluginName() {
//            return "Wave drawing options";
//        }
//    }
}