package DiagramSynthPackage.Synth;

import DiagramSynthPackage.GUI.MovablePanelsPackage.DiagramPanel;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.InputPort;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.SingleInputPort;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals.DynamicTextInternals;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.RectangleShapedPanel;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import DiagramSynthPackage.Synth.AudioThreads.AudioThread;
import RocnikovyProjektIFace.AudioFormatChooserPackage.ChannelCount;
import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginDefaultIFace;
import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginParametersAnnotation;

import java.io.PrintWriter;

public final class OutputUnit extends Unit implements PluginDefaultIFace {
    public OutputUnit(DiagramPanel panelWithUnits, int channel, ChannelCount channelCount,
                      AudioThread audioThread) {
        super(panelWithUnits);
        this.channel = channel;
        this.audioThread = audioThread;
        TOOLTIP = "<html>";
        TOOLTIP += "This is the output unit, which results are put to the audio buffer and played.<br>";

        switch (channelCount) {
            case MONO:     // Mono
                DEFAULT_NAME = "OUT";
                TOOLTIP += "MONO" + "<br>";
                break;
            case STEREO:     // Stereo
                TOOLTIP += "STEREO" + "<br>";
                if(channel == 0) {
                    DEFAULT_NAME = "OUT-L";
                    TOOLTIP += "LEFT CHANNEL" + "<br>";
                }
                else {
                    DEFAULT_NAME = "OUT-R";
                    TOOLTIP += "RIGHT CHANNEL" + "<br>";
                }
                break;
// TODO: Currently not supported
//            case QUADRO:
//                TOOLTIP += "QUADRO (4 channels)" + "<br>";
//                switch(channel) {
//                    case 0:
//                        DEFAULT_NAME = "OUT-FL";
//                        TOOLTIP += "FRONT LEFT" + "<br>";
//                        break;
//                    case 1:
//                        DEFAULT_NAME = "OUT-FR";
//                        TOOLTIP += "FRONT RIGHT" + "<br>";
//                        break;
//                    case 2:
//                        DEFAULT_NAME = "OUT-SL";
//                        TOOLTIP += "SURROUND LEFT" + "<br>";
//                        break;
//                    case 3:
//                        DEFAULT_NAME = "OUT-SR";
//                        TOOLTIP += "SURROUND RIGHT" + "<br>";
//                        break;
//                    default:
//                        DEFAULT_NAME = "";
//                        MyLogger.log("Invalid channel");
//                        break;
//                }
//                break;
//            case FIVE_POINT_ONE:
//                // https://en.wikipedia.org/wiki/5.1_surround_sound
//                TOOLTIP += "5.1 channels" + "<br>";
//                switch(channel) {
//                    case 0:
//                        DEFAULT_NAME = "OUT-FL";
//                        TOOLTIP += "FRONT LEFT" + "<br>";
//                        break;
//                    case 1:
//                        DEFAULT_NAME = "OUT-FR";
//                        TOOLTIP += "FRONT RIGHT" + "<br>";
//                        break;
//                    case 2:
//                        DEFAULT_NAME = "OUT-C";
//                        TOOLTIP += "CENTER" + "<br>";
//                        break;
//                    case 3:
//                        DEFAULT_NAME = "OUT-LFE";
//                        TOOLTIP += "LOW-FREQUENCY EFFECTS" + "<br>";
//                        break;
//                    case 4:
//                        DEFAULT_NAME = "OUT-SL";
//                        TOOLTIP += "SURROUND LEFT" + "<br>";
//                        break;
//                    case 5:
//                        DEFAULT_NAME = "OUT-SR";
//                        TOOLTIP += "SURROUND RIGHT" + "<br>";
//                        break;
//                    default:
//                        DEFAULT_NAME = "";
//                        MyLogger.log("Invalid channel");
//                        break;
//                }
//                break;
//            case SEVEN_POINT_ONE:
//                // https://www.gearslutz.com/board/post-production-forum/659911-7-1-channels-order-naming-conventions.html
//                TOOLTIP += "7.1 channels" + "<br>";
//                switch(channel) {
//                    case 0:
//                        DEFAULT_NAME = "OUT-FL";
//                        TOOLTIP += "FRONT LEFT" + "<br>";
//                        break;
//                    case 1:
//                        DEFAULT_NAME = "OUT-FR";
//                        TOOLTIP += "FRONT RIGHT" + "<br>";
//                        break;
//                    case 2:
//                        DEFAULT_NAME = "OUT-C";
//                        TOOLTIP += "CENTER" + "<br>";
//                        break;
//                    case 3:
//                        DEFAULT_NAME = "OUT-LFE";
//                        TOOLTIP += "LOW-FREQUENCY EFFECTS" + "<br>";
//                        break;
//                    case 4:
//                        DEFAULT_NAME = "OUT-LSS";
//                        TOOLTIP += "LEFT SIDE SURROUND" + "<br>";
//                        break;
//                    case 5:
//                        DEFAULT_NAME = "OUT-RSS";
//                        TOOLTIP += "RIGHT SIDE SURROUND" + "<br>";
//                        break;
//                    case 6:
//                        DEFAULT_NAME = "OUT-LSR";
//                        TOOLTIP += "LEFT SIDE REAR" + "<br>";
//                        break;
//                    case 7:
//                        DEFAULT_NAME = "OUT-RSR";
//                        TOOLTIP += "RIGHT SIDE REAR" + "<br>";
//                        break;
//                    default:
//                        DEFAULT_NAME = "";
//                        MyLogger.log("Invalid channel");
//                        break;
//                }
//                break;
// TODO: Currently not supported
            default:
                DEFAULT_NAME = "OUT" + channel;
                TOOLTIP += channelCount.CHANNEL_COUNT + " CHANNELS" + "<br>";
                TOOLTIP += channel + "-TH CHANNEL" + "<br>";
                break;
        }

        TOOLTIP += "</html>";
        setPanelName(DEFAULT_NAME);
        shapedPanel.setToolTipText(getTooltip());
    }


    private final AudioThread audioThread;
    private final int channel;
    private int writtenSamplesCount = -1;
    public final int getWrittenSamplesCount() {
        return writtenSamplesCount;
    }

    @Override
    public void unmarkAsCalculated() {
        super.unmarkAsCalculated();
        resetWrittenSamplesCount();
    }

    @Override
    public void resetToDefaultState() {
        unmarkAsCalculated();
    }

    private void resetWrittenSamplesCount() {
        writtenSamplesCount = -1;
    }


    private String TOOLTIP;

    private final String DEFAULT_NAME;
    @Override
    public String getDefaultPanelName() {
        return DEFAULT_NAME == null ? "" : DEFAULT_NAME;
    }

    @Override
    protected InputPort[] createInputPorts(DiagramPanel panelWithUnits, double[] neutralValues) {
        InputPort[] inputPorts = new InputPort[1];
        // The class is final, doesn't have to solve case with neutral values == null
        inputPorts[0] = new SingleInputPort(this, shapedPanel, panelWithUnits, neutralValues[0]);
        return inputPorts;
    }

    @Override
    public double[] getNeutralValues() {
        return new double[] { 0 };
    }

    @Override
    protected ShapedPanel createShapedPanel(DiagramPanel panelWithUnits) {
        ShapedPanel sp = new RectangleShapedPanel(panelWithUnits, new DynamicTextInternals(() -> getPanelName()), this);
        return sp;
    }


    @Override
    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h, DiagramPanel panelWithUnits) {
        // RETURNS NULL, since output panel can't be copied.
        return null;
    }


    @PluginParametersAnnotation(name = "Amplitude:", lowerBound = "0", upperBound = "1",
            parameterTooltip = "Maximum absolute value allowed to output")
    private double maxAbsoluteValue = 1;
    @PluginParametersAnnotation(name = "Always scale:",
            parameterTooltip = "<html>If set to true, then every wave will be scaled to the max absolute value from first parameter.<br>" +
            "If set to false, then the wave will be scaled to the max absolute value only if its max absolute value is larger than the first parameter</html>")
    private boolean shouldAlwaysSetToMaxAbs = false;


    @Override
    public void calculateSamplesInstantRecord(double[][] channelRecords, int index, int remainingLen) {
        // Copy pasted code from calculate samples, but with removed waiting and putting items to queue. It will be put to buffer instead
        final double maxAbsVal = Math.abs(getMaxAbsValue());
        double[] ops = inputPorts[0].getValues();
        boolean didNomalize = tryNormalize(ops, maxAbsVal);

        writtenSamplesCount = Math.min(remainingLen, results.length);
        if(didNomalize) {
            System.arraycopy(results, 0, channelRecords[channel], index, writtenSamplesCount);
        }
        else {
            System.arraycopy(ops, 0, channelRecords[channel], index, writtenSamplesCount);
        }
    }

    /**
     * Returns true if normalization took place.
     * @param input
     * @param maxAbsVal
     * @return
     */
    private boolean tryNormalize(double[] input, double maxAbsVal) {
        if(shouldAlwaysSetToMaxAbs) {
            for (int i = 0; i < input.length; i++) {
                results[i] = input[i] * (maxAbsoluteValue / maxAbsVal);
            }
            return true;
        }
        else {
            if (maxAbsVal > maxAbsoluteValue) {
                if (maxAbsoluteValue == 1) {
                    for (int i = 0; i < input.length; i++) {
                        results[i] = input[i] / maxAbsVal;
                    }
                } else {
                    for (int i = 0; i < input.length; i++) {
                        results[i] = input[i] * (maxAbsoluteValue / maxAbsVal);
                    }
                }

                return true;
            }
            return false;
        }
    }

    @Override
    public void calculateSamples() {
        final double maxAbsVal = Math.abs(getMaxAbsValue());
        double[] ops = inputPorts[0].getValues();
        boolean didNomalize = tryNormalize(ops, maxAbsVal);
        while (audioThread.getPushLen(channel, 0, ops.length) < ops.length) {
            // Active waiting, while the audio thread freed enough bytes from queue
        }

        if (didNomalize) {
            writtenSamplesCount = audioThread.pushSamplesToQueue(results, channel);
        }
        else {
            writtenSamplesCount = audioThread.pushSamplesToQueue(ops, channel);
        }
    }



    /**
     * @return Returns true if the operation needs parameters - so user needs to put them in the JPanel.
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
        return "Maximum absolute value";
    }

    @Override
    protected void setPropertiesPanel() {
        propertiesPanel = this;
    }
    @Override
    public void updateAfterPropertiesCall() {
        // EMPTY
    }


    @Override
    public String getPanelName() {
        return getDefaultPanelName();
    }

    @Override
    public String getTooltip() {
        return TOOLTIP;
    }

    @Override
    public boolean getIsConst() {
        // Not used
        return inputPorts[0].getIsConst();
    }
    @Override
    public boolean getIsNoiseGen() {
        return inputPorts[0].getIsNoiseGen();
    }


    @Override
    public double getMaxAbsValue() {
        return inputPorts[0].getMaxAbsValue();
    }


    /**
     * Returns the amplitudes of the modulating wave
     *
     * @return
     */
    @Override
    public double[] getWaveAmps(int waveIndex) {
        return inputPorts[0].getWaveAmps(waveIndex);
    }

    /**
     * Returns the frequencies of the modulating wave.
     *
     * @return
     */
    @Override
    public double[] getWaveFreqs(int waveIndex) {
        return inputPorts[0].getWaveFreqs(waveIndex);
    }


    @Override
    /**
     * Prints "OUTPUT-UNIT" and on next line channel of output and then calls super method. The channel has to be read in the main class and call load
     * on corresponding panel.
     */
    public void save(PrintWriter output) {
        output.println("OUTPUT-UNIT");
        output.println(channel);
        super.save(output);
    }


    @Override
    public void copyInternalState(Unit copySource) {
        // EMPTY
    }
}