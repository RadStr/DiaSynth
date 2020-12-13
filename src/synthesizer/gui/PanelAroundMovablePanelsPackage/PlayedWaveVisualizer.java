package synthesizer.gui.PanelAroundMovablePanelsPackage;

import synthesizer.synth.CyclicQueueDouble;
import RocnikovyProjektIFace.WaveDrawValuesAggregated;
import RocnikovyProjektIFace.WavePanel;
import RocnikovyProjektIFace.DrawValuesSupplierIFace;
import RocnikovyProjektIFace.WaveDrawValues;
import Rocnikovy_Projekt.Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class PlayedWaveVisualizer extends JPanel implements DrawValuesSupplierIFace {
    public PlayedWaveVisualizer() {
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = e.getComponent().getWidth();
                arrToCopyQueueToLen = 0;
                for(WaveDrawValues wrapper : drawValuesWrappers) {
                    wrapper.waveResize(w, w, 0, w);
                }
                setArrToCopyQueueTo(w);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                // EMPTY
            }

            @Override
            public void componentShown(ComponentEvent e) {
                // EMPTY
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // EMPTY
            }
        });

        this.setPreferredSize(getPreferredSize());
        timer = new Timer(100, (e) -> repaint());

        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                zoom(-e.getWheelRotation());
            }
        });
        zoom(0);
    }

    private void zoom(int zoom) {
        double newSamplesPerPixel = samplesPerPixel - 2 * zoom;
        if(newSamplesPerPixel != samplesPerPixel) {
            arrToCopyQueueToLen = 0;
            int w = getWidth();
            if (newSamplesPerPixel < 1) {
                newSamplesPerPixel = 1;
            }

            samplesPerPixel = newSamplesPerPixel;
            setArrToCopyQueueTo(w);
            tooltip.setLength(TOOLTIP_START_TEXT.length());
            tooltip.append(samplesPerPixel + "<br>");
            double lenInSamples = w * samplesPerPixel;
            tooltip.append("Length of visible wave in samples " + lenInSamples + "<br>");
            double lenInSecs = lenInSamples / sampleRate;
            tooltip.append("Length of visible wave in seconds " + String.format("%.2f", lenInSecs) + "</html>");
            this.setToolTipText(tooltip.toString());
        }
    }

    private StringBuilder tooltip = new StringBuilder(TOOLTIP_START_TEXT);
    private static final String TOOLTIP_START_TEXT = "<html>Samples per pixel = ";


    private Dimension prefSize = new Dimension(0, 80);
    @Override
    public Dimension getPreferredSize() {
        Dimension superPrefSize = super.getPreferredSize();
        prefSize.width = superPrefSize.width;
        prefSize.height = Math.max(superPrefSize.height, 80);
        return prefSize;
    }


    private boolean shouldViewWave = true;
    public void setShouldViewWave(boolean shouldViewWave) {
        this.shouldViewWave = shouldViewWave;
        this.repaint();
    }


    // volatile because audio thread checks it
    private volatile boolean isPaused = true;
    private Timer timer;
    public void pause() {
        isPaused = true;
        timer.stop();
        Timer t = new Timer(20, (e) -> { this.repaint(); ((Timer)e.getSource()).stop(); } );
        t.start();
    }
    public void start() {
// TODO: I have to do this else, it will overflow later, but this part of code somehow lags everything
//        if(lastPushedSample != null && lastDrawnSample != null) {
//            for(int i = 0; i < lastPushedSample.length; i++) {
//                lastPushedSample[i] = 0;
//                lastDrawnSample[i] = 0;
//            }
//        }
        isPaused = false;
        timer.start();
    }

    private double samplesPerPixel = 3;


    private volatile int[] lastPushedSample;
    private volatile int[] lastDrawnSample;


    public void pushSamplesToQueue(double[] samples, int startIndex, int endIndex, int channel) {
        if(!isPaused) {
            int pushLen = sampleQueues[channel].push(samples, startIndex, endIndex);
            lastPushedSample[channel] += pushLen;
        }
    }


    private int sampleRate = 44100;
    /**
     * Used so we know how many seconds does the window show
     * @param sampleRate
     */
    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
        zoom(0);
    }


    private CyclicQueueDouble[] sampleQueues;
    public void setNumberOfChannels(int newChannelCount) {
        int w = getWidth();
        lastPushedSample = new int[newChannelCount];
        lastDrawnSample = new int[newChannelCount];
        setArrToCopyQueueTo(w);
        drawValuesWrappers = new WaveDrawValues[newChannelCount];

        sampleQueues = new CyclicQueueDouble[newChannelCount];
        for(int i = 0; i < sampleQueues.length; i++) {
            sampleQueues[i] = new CyclicQueueDouble(16);
            drawValuesWrappers[i] = new WaveDrawValuesAggregated(w, w, 0, w, 0, this);
        }
    }

    private WaveDrawValues[] drawValuesWrappers;
    private double[] arrToCopyQueueTo = new double[2048 * 4];
    private int arrToCopyQueueToLen;
    private void setArrToCopyQueueTo(int w) {
        int newLen = (int)(2 * w * samplesPerPixel);
        if(newLen > arrToCopyQueueTo.length) {
            int newArrLen = Program.getFirstPowerOfNAfterNumber(newLen, 2);
            arrToCopyQueueTo = new double[newArrLen];
        }

        arrToCopyQueueToLen = newLen;
    }
    private int currentChannel;

    public static final int VERTICAL_SPACE = 5;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(shouldViewWave) {
            int w = this.getWidth();
            int h = this.getHeight();
            g.drawRect(0, 0, w - 1, h - 1);
            h /= drawValuesWrappers.length;
            h -= VERTICAL_SPACE;
            currentChannel = 0;
            for (int currY = 0; currentChannel < drawValuesWrappers.length; currentChannel++) {
                int sampleShift = lastPushedSample[currentChannel] - lastDrawnSample[currentChannel];
                drawValuesWrappers[currentChannel].shiftBuffer((int) (sampleShift / samplesPerPixel));

                if(currentChannel % 2 == 0) {
                    g.setColor(Color.LIGHT_GRAY);
                }
                else {
                    g.setColor(Color.gray);
                }

                if (currentChannel != drawValuesWrappers.length - 1) {
                    g.fillRect(0, currY, w, h);
                    drawValuesWrappers[currentChannel].drawSamples(g, w, h, currY);
                    currY += h;
                    g.setColor(Color.PINK);
                    g.fillRect(0, currY, w, VERTICAL_SPACE);
                    currY += VERTICAL_SPACE;
                } else {
                    g.fillRect(0, currY, w, h + VERTICAL_SPACE);
                    drawValuesWrappers[currentChannel].drawSamples(g, w, h + VERTICAL_SPACE, currY);
                }
            }
        }
    }

    @Override
    public void fillBufferWithValuesToDraw(double[] buffer, int bufferStartIndex, int bufferEndIndex, int startFillIndex) {
        double[] arrToCopyQueueToConc = arrToCopyQueueTo;
        if(arrToCopyQueueToLen > 0) {
            int numberOfSamplesToDraw;
            numberOfSamplesToDraw = (int) ((bufferEndIndex - bufferStartIndex) / 2 * samplesPerPixel);      // / 2 since it is min and max
            if (isPaused) {
                Program.setOneDimArr(buffer, 0, buffer.length, 0);
            }
            else {
                int currentQueueLen = sampleQueues[currentChannel].getLen();
                int bufferLenInSamples = (int) (buffer.length / 2 * samplesPerPixel);
                // If there are much more newer samples, so the whole buffer will be thrown out and replaced by new values
                if (currentQueueLen >= bufferLenInSamples) {
                    bufferStartIndex = 0;
                    bufferEndIndex = buffer.length;
                    numberOfSamplesToDraw = bufferLenInSamples;
                    int popLen = currentQueueLen - bufferLenInSamples;
                    sampleQueues[currentChannel].pop(popLen);
                    lastDrawnSample[currentChannel] += popLen;

                    sampleQueues[currentChannel].pop(arrToCopyQueueToConc, 0, numberOfSamplesToDraw);
                    lastDrawnSample[currentChannel] += numberOfSamplesToDraw;
                }
                else {
                    int remainingLen = numberOfSamplesToDraw;
                    int startIndex = 0;
                    while (remainingLen > 0) {
                        if (isPaused) {
                            Program.setOneDimArr(buffer, 0, buffer.length, 0);
                            return;
                        }

                        int popCount = sampleQueues[currentChannel].pop(arrToCopyQueueToConc, startIndex,startIndex + remainingLen);
                        startIndex += popCount;
                        remainingLen -= popCount;
                        lastDrawnSample[currentChannel] += popCount;
                    }
                }

                WavePanel.findExtremesInValues(arrToCopyQueueToConc, buffer,
                        0, bufferStartIndex, numberOfSamplesToDraw, samplesPerPixel);
            }
        }
    }

    @Override
    public int getPrefixLenInBytes() {
        return 0;
    }

    @Override
    public int fillBufferWithCachedValues(double[] buffer, int bufferStartIndex, int bufferEndIndex,
                                          int startFillIndex, int inputLen, int outputLen) {
        return 0;
    }

    @Override
    public boolean getIsCached() {
        return false;
    }

    @Override
    public int getCurrentStartIndexInAudio() {
        return 0;
    }

    @Override
    public int getTotalWidth() {
        return getWidth();
    }

    @Override
    public int convertFromPixelToIndexInAudio(double pixel) {
        return (int)pixel;
    }

    @Override
    public int getCurrentScroll() {
        return 0;
    }

    @Override
    public int getMaxScroll() {
            return 0;
    }

    @Override
    public int getAudioLen() {
        return Integer.MAX_VALUE;
    }
}