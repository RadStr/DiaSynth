package player.drawing;

import player.plugin.PanelFromAnnotations;
import player.plugin.ifaces.PluginDefaultIFace;
import player.plugin.ifaces.PluginParametersAnnotation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;




public class TimeWaveDrawWrapper extends DrawWrapperBase {
    public static TimeWaveDrawWrapper createMaxSizeTimeWaveDrawWrapper(int timeInMs, boolean isEditable,
                                                                       Color backgroundColor,
                                                                       boolean shouldDrawLabelsAtTop) {
        int binCount = DrawWrapperBase.calculateMaxSizeBinCount(-1, 1);
        int firstX = new TimeWaveDrawWrapper(timeInMs, binCount,
                isEditable, backgroundColor, shouldDrawLabelsAtTop).timeWaveDrawPanel.getFirstBinStartX();
        binCount -= 2 * firstX;

        return new TimeWaveDrawWrapper(timeInMs, binCount, isEditable,
                backgroundColor, shouldDrawLabelsAtTop);
    }

    public TimeWaveDrawWrapper(int timeInMs, int binCount, boolean isEditable,
                               Color backgroundColor, boolean shouldDrawLabelsAtTop) {
        this(new TimeWaveDrawPanel(timeInMs, binCount, isEditable, backgroundColor, shouldDrawLabelsAtTop),
                -1, 1);
    }

    private TimeWaveDrawWrapper(TimeWaveDrawPanel timeWaveDrawPanel, double minValue, double maxValue) {
        super(timeWaveDrawPanel, minValue, maxValue);
        this.timeWaveDrawPanel = timeWaveDrawPanel;
    }

    private TimeWaveDrawPanel timeWaveDrawPanel;
    public double[] getOneSecondWave(int sampleRate) {
        return timeWaveDrawPanel.getOneSecondWave(sampleRate);
    }

    public double[] getNPeriods(int sampleRate, int periodCount) {
        return timeWaveDrawPanel.getNPeriods(sampleRate, periodCount);
    }


    @Override
    public void addMenus(JMenuBar menuBar, AddWaveIFace waveAdder) {
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);
        JMenuItem optionsMenuItem = new JMenuItem("Set Time");
        menu.add(optionsMenuItem);
        optionsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TimeOptionsDialogPanel classWithValues = new TimeOptionsDialogPanel(timeWaveDrawPanel);
                PanelFromAnnotations dialogPanel = new PanelFromAnnotations(classWithValues,
                        classWithValues.getClass());

                int result = JOptionPane.showConfirmDialog(null, dialogPanel,
                        "Dialog: " + classWithValues.getPluginName(), JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if(result == JOptionPane.OK_OPTION) {
                    timeWaveDrawPanel.setTimeInMs(classWithValues.timeInMs);
                }
            }
        });

        menu = new JMenu("Action");
        menuBar.add(menu);
        JMenuItem actionMenuItem = new JMenuItem("Perform action");
        menu.add(actionMenuItem);
        TimeWaveDrawWrapper thisWrapper = this;
        actionMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TimeActionDialogPanel classWithValues = new TimeActionDialogPanel(thisWrapper, waveAdder);
                PanelFromAnnotations dialogPanel = new PanelFromAnnotations(classWithValues,
                        classWithValues.getClass());

                int result = JOptionPane.showConfirmDialog(null, dialogPanel,
                        "Dialog: " + classWithValues.getPluginName(), JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if(result == JOptionPane.OK_OPTION) {
                    timeWaveDrawPanel.setTimeInMs(classWithValues.getTimeInMs());
                    periodCount = classWithValues.getPeriodCount();
                    double[] wave = getNPeriods(classWithValues.getSampleRate(), periodCount);
                    waveAdder.addWave(wave);
                }
            }
        });

        addReset(menu);
    }

    private int periodCount = 1;



    private static class TimeActionDialogPanel extends TimeOptionsDialogPanel implements PluginDefaultIFace {
        public TimeActionDialogPanel(TimeWaveDrawWrapper timeWaveDrawWrapper, AddWaveIFace waveAdder) {
            super(timeWaveDrawWrapper.timeWaveDrawPanel);
            periodCount = timeWaveDrawWrapper.periodCount;
            sampleRate = waveAdder.getOutputSampleRate();
        }


        @PluginParametersAnnotation(name = "Sample rate:", lowerBound = "0",
                parameterTooltip = "Controls the sample rate of the drawn wave")
        private int sampleRate;

        public int getSampleRate() {
            return sampleRate;
        }

        @PluginParametersAnnotation(name = "Period count:", lowerBound = "1",
                parameterTooltip = "Controls the number of periods (repetitions) of drawn wave")
        private int periodCount;
        public int getPeriodCount() {
            return periodCount;
        }

        @Override
        public String getPluginName() {
            return "Generate drawn wave";
        }
    }


    private static class TimeOptionsDialogPanel implements PluginDefaultIFace {
        public TimeOptionsDialogPanel(TimeWaveDrawPanel timeWaveDrawPanel) {
            timeInMs = timeWaveDrawPanel.getTimeInMs();
        }

        @PluginParametersAnnotation(name = "Time (in Ms):", lowerBound = "1",
                parameterTooltip = "Controls the length of the drawn wave. The time is in milliseconds.")
        private int timeInMs;
        public int getTimeInMs() {
            return timeInMs;
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
            return "Wave drawing options";
        }
    }
}
