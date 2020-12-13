package synthesizer.gui.diagram;

import synthesizer.gui.diagram.port.InputPort;
import synthesizer.gui.diagram.port.OutputPort;
import synthesizer.gui.diagram.port.Port;
import synthesizer.gui.diagram.ShapedPanels.*;
import synthesizer.gui.PanelAroundMovablePanelsPackage.*;
import synthesizer.gui.PanelAroundMovablePanelsPackage.tree.UnitAdditionIFace;
import synthesizer.gui.diagram.util.ListSortedByY;
import synthesizer.synth.*;
import synthesizer.synth.audio.AudioRecordingCallback;
import synthesizer.synth.audio.AudioThreadWithRecordingSupport;
import player.format.AudioFormatJPanel;
import player.format.AudioFormatWithSign;
import player.format.ChannelCount;
import player.AudioControlPanel;
import player.util.BooleanButton;
import Rocnikovy_Projekt.MyLogger;
import Rocnikovy_Projekt.Program;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DiagramPanel extends JLayeredPane implements ZoomIFace, SetMovingPanelIFace,
                                                          MouseListener, MouseMotionListener,
                                                          GetMaxElevationIFace, ResizeSplitpaneCallbackIFace,
                                                          UnitAdditionIFace, AddInputPortToGUIIFace,
                                                          SerializeIFace, OutputUnitGetter, AudioRecordingCallback {
    public static final int START_PIXELS_PER_ELEVATION = 4;
    public static final int START_CIRCLE_CONNECTION_SIZE = 4;

    public static final int STATIC_PANEL_START_WIDTH = 128;
    public static final int STATIC_PANEL_START_HEIGHT = 128;

    public static final int STATIC_PANEL_MIN_WIDTH = 16;            // Parameter to play with
    public static final int STATIC_PANEL_MIN_HEIGHT = 16;           // Parameter to play with
    public static final int STATIC_PANEL_MAX_WIDTH = 65536;         // Parameter to play with
    public static final int STATIC_PANEL_MAX_HEIGHT = 65536;        // Parameter to play with


    public static final int ZOOM_COUNT_FROM_START_TO_MIN = (int)Math.round(Program.logGeneral(STATIC_PANEL_START_WIDTH, 2)) -
            (int)Math.round(Program.logGeneral(STATIC_PANEL_MIN_WIDTH, 2));


    public static final int SPACE_BETWEEN_STATIC_PANELS_X = 64;       // Parameter to play with
    public static final int SPACE_BETWEEN_STATIC_PANELS_Y = 64;       // Parameter to play with
    public static final int MIN_SPACE_BETWEEN_STATIC_PANELS_X = (int)(STATIC_PANEL_MIN_WIDTH *
            (SPACE_BETWEEN_STATIC_PANELS_X / (double)STATIC_PANEL_START_WIDTH));
    public static final int MIN_SPACE_BETWEEN_STATIC_PANELS_Y = (int)(STATIC_PANEL_MIN_HEIGHT *
            (SPACE_BETWEEN_STATIC_PANELS_Y / (double)STATIC_PANEL_START_HEIGHT));

    public static final int DEFAULT_SIZE_DIVIDER = 20;
    public static final int ZOOM_PER_SCROLL = 2;


    public static final int TIME_BETWEEN_MOUSE_POS_CHECKS = 50;        // Parameter to play with
    public static final int PIXELS_MOVED_PER_TICK = 20;                // Parameter to play with

    public static final int SCROLL_BORDER_SIZE_X = 50;
    public static final int SCROLL_BORDER_SIZE_Y = 50;

    public static final int ARROW_SIZE_X = SCROLL_BORDER_SIZE_X - 10;  // Parameter to play with
    public static final int ARROW_SIZE_Y = SCROLL_BORDER_SIZE_Y - 10;  // Parameter to play with

    public static final int BORDER_ARROW_HALF_SPACE_X = (SCROLL_BORDER_SIZE_X - ARROW_SIZE_X) / 2;
    public static final int BORDER_ARROW_HALF_SPACE_Y = (SCROLL_BORDER_SIZE_Y - ARROW_SIZE_Y) / 2;


    public static final Color BORDER_COLOR = new Color(Color.BLUE.getRed(),
            Color.BLUE.getGreen(), Color.BLUE.getBlue(), 32);        // Parameter to play with
    public static final Color ARROW_COLOR = Color.BLUE;                 // Parameter to play with

    public static final int WHEEL_TIME_INTERVAL_BETWEEN_RESET_MILLIS = 250;
    public boolean isWheelMovementZoom(int wheelMovement) {
        return wheelMovement < 0;
    }

    public final Color DEFAULT_COLOR;

    /**
     * The default color has to be passed since JLayeredPane doesn't have any background color until it is added to some component.
     * @param defaultColor is the color of the component to which is this class added.
     */
    public DiagramPanel(Color defaultColor, SynthesizerMainPanelIFace synthesizerMainPanel,
                        PlayedWaveVisualizer waveVisualizer) {
        this.DEFAULT_COLOR = defaultColor;
        this.synthesizerMainPanel = synthesizerMainPanel;

        this.setLayout(null);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        ComponentListener resizeListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component c = e.getComponent();
                int newW, newH;
                newW = c.getWidth();
                newH = c.getHeight();

                screenMidPoint.x = newW / 2;
                screenMidPoint.y = newH / 2;

                for (Unit u : panels) {
                    u.getShapedPanel().updateSize(referencePanel.getSize());
                }
            }
        };
        this.addComponentListener(resizeListener);

        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int wheelMovement = e.getWheelRotation();
                zoom(wheelMovement);
            }
        });


        mousePosTimer = new Timer(TIME_BETWEEN_MOUSE_POS_CHECKS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
                observeMouseLoc(mouseLoc);
            }
        });


        currentPanelSize = new IntPairWithInternalDoublesWithMinAndMax(STATIC_PANEL_START_WIDTH, STATIC_PANEL_START_HEIGHT,
                STATIC_PANEL_MIN_WIDTH, STATIC_PANEL_MIN_HEIGHT, STATIC_PANEL_MAX_WIDTH, STATIC_PANEL_MAX_HEIGHT);
        int first = currentPanelSize.getFirst();
        int second = currentPanelSize.getSecond();

        referencePanel = new ReferenceMovableJPanel(0, 0, first, second, this);
        sizeChangeCallback();

        panels = new ListSortedByY();
        recalculateAllCables();

        screenMidPoint = new Point();
        oldPanelSize = new Dimension();

        focusGained();
        setArrowVariables();

        MyLogger.log("Adding audio thread to synth part", 1);
        audioThread = new AudioThreadWithRecordingSupport(this, true);
        audioThread.setWaveVisualizer(waveVisualizer);
        setOutputAudioFormat(new AudioFormatWithSign(44100, 16, 1,
                true, false));
        MyLogger.log("Added audio thread to synth part", -1);

        MyLogger.log("Starting audio thread to synth part", 1);
        audioThread.start();
        MyLogger.log("Started audio thread to synth part", -1);
        MyLogger.log("Adding synth diagram to synth part", 1);
        synthDiagram = new SynthDiagram(panels, this, audioThread, true);
        MyLogger.log("Added synth diagram to synth part", -1);
        MyLogger.log("Starting synth diagram in synth part", 1);
        synthDiagram.start();
        MyLogger.log("Started synth diagram in synth part", -1);
    }


    private File recordFile = new File("audio");
    private AudioFileFormat.Type recordAudioType = AudioFileFormat.Type.WAVE;
    public void setRecordPathRelatedValues(File chosenFile, AudioFileFormat.Type audioType) {
        recordFile = chosenFile;
        this.recordAudioType = audioType;
    }

    private double recordAudioLen = 3;
    public void setRecordTimeInSeconds(double recordAudioLen) {
        this.recordAudioLen = recordAudioLen;
    }


    private boolean shouldConvertToPlayerOutputFormat = false;
    public void setShouldConvertToPlayerFormat(boolean shouldConvert) {
        shouldConvertToPlayerOutputFormat = shouldConvert;
    }


    public void recordInstantly() {
        AudioControlPanel audioControlPanel = synthesizerMainPanel.getAudioControlPanel();
        audioControlPanel.setEnabled(false);
        stopAudioUsingClick();
        byte[] instantRecord = synthDiagram.recordInstantlyBytes(recordAudioLen);
        if(isRecordingToPlayer) {
            synthesizerMainPanel.putRecordedWaveToPlayer(instantRecord, instantRecord.length,
                    audioThread.getOutputFormat(), shouldConvertToPlayerOutputFormat);
        }
        if(isRecordingToFile) {
            saveRecordedAudio(instantRecord, 0, instantRecord.length);
        }
        audioControlPanel.setEnabled(true);
    }

    private volatile boolean isRecordingRealTime = false;       // volatile because it is read by the audio thread
    private Object recordLock = new Object();
    private void setIsRecordingRealTime(boolean value) {
        synchronized (recordLock) {
            isRecordingRealTime = value;
        }
    }
    private volatile boolean isAudioThreadInsideCallback = false;
    private volatile boolean shouldWriteRecordToFile = false;
    private int realTimeRecordingCurrIndex;
    private byte[] realTimeRecord;

    private boolean isRecordingToPlayer = false;
    public void setIsRecordingToPlayer() {
        isRecordingToPlayer = !isRecordingToPlayer;
    }
    private boolean isRecordingToFile = false;
    public void setIsRecordingToFile() {
        isRecordingToFile = !isRecordingToFile;
    }

    public void recordRealTime() {
        if(isRecordingRealTime) {
            stopRealTimeRecording();
        }
        else {
            startRealTimeRecording();
        }
    }


    private void startRealTimeRecording() {
        AudioFormat af = audioThread.getOutputFormat();
        int frameSize = af.getChannels() * af.getSampleSizeInBits() / 8;
        double sampleRate = af.getSampleRate();
        int audioLenInBytes = (int)(recordAudioLen * frameSize * sampleRate);
        audioLenInBytes = Program.convertToMultipleUp(audioLenInBytes, frameSize);
        realTimeRecord = new byte[audioLenInBytes];
        setIsRecordingRealTime(true);
    }

    private void stopRealTimeRecording() {
        setIsRecordingRealTime(false);
        while(isAudioThreadInsideCallback) {
            // Active waiting
        }
        if(realTimeRecordingCurrIndex > 0) {
            if (isRecordingToPlayer) {
                synthesizerMainPanel.putRecordedWaveToPlayer(realTimeRecord, realTimeRecordingCurrIndex,
                        audioThread.getOutputFormat(), shouldConvertToPlayerOutputFormat);
            }
            if (isRecordingToFile) {
                saveRecordedAudio(realTimeRecord, 0, realTimeRecordingCurrIndex);
            }
        }

        shouldWriteRecordToFile = false;
        realTimeRecord = null;
        realTimeRecordingCurrIndex = 0;
    }


    /**
     * Copies the given buffer to the internal buffer which represents the recording.
     * @param playedAudio
     * @param endIndex
     */
    @Override
    public void recordingRealTimeCallback(byte[] playedAudio, int endIndex) {
        if(isRecordingRealTime && !shouldWriteRecordToFile) {
            isAudioThreadInsideCallback = true;
            int copyLen = endIndex;
            if (realTimeRecordingCurrIndex + copyLen > realTimeRecord.length) {
                copyLen = realTimeRecord.length - realTimeRecordingCurrIndex;
                shouldWriteRecordToFile = true;
            }
            System.arraycopy(playedAudio, 0, realTimeRecord, realTimeRecordingCurrIndex, copyLen);

            realTimeRecordingCurrIndex += copyLen;

            if(shouldWriteRecordToFile) {
                synthesizerMainPanel.clickRealTimeRecordingCheckbox();
            }
            isAudioThreadInsideCallback = false;
        }
    }


    /**
     * Returns true if audio was correctly written to output.
     * @return
     */
    private boolean saveRecordedAudio(byte[] recordedAudio, int startIndex, int endIndex) {
        return saveRecordedAudio(recordFile.getPath(), recordedAudio, startIndex, endIndex,
                audioThread.getOutputFormat(), recordAudioType);
    }

    public static boolean saveRecordedAudio(String path, byte[] audioToSave, int startIndex, int endIndex,
                                            AudioFormat outputFormat, AudioFileFormat.Type recordAudioType) {
        boolean savedAudioCorrectly = false;
        try {
            Program.saveAudio(path, outputFormat, audioToSave, startIndex, endIndex, recordAudioType);
            savedAudioCorrectly = true;
        } catch (IOException e) {
            MyLogger.logException(e);
        }

        return savedAudioCorrectly;
    }




    private SynthDiagram synthDiagram;
    public SynthDiagram getSynthDiagram() {
        return synthDiagram;
    }
    private AudioThreadWithRecordingSupport audioThread;
    public AudioControlPanel.VolumeControlGetterIFace getAudioThread() {
        return audioThread;
    }
    public AudioFormatWithSign getOutputAudioFormat() {
        return audioThread.getOutputFormat();
    }

    public void startAudio() {
        synthDiagram.play();
        audioThread.play();
    }
    public void pauseAudio() {
        synthDiagram.pause();
        audioThread.pause();
    }
    public void resetAudio() {
        synthDiagram.reset();
        audioThread.reset();
    }

    private void stopAudioUsingClick() {
        BooleanButton playButton = synthesizerMainPanel.getAudioControlPanel().getPlayButton();
        if(!playButton.getBoolVar()) {
            playButton.doClick();
        }
    }

    private ChannelCount channelCount;

    private long timeOfLastWheelEvent = 0;
    private Timer mousePosTimer;

    private OutputUnit[] outputPanels;
    private void setOutputPanels(ChannelCount channelCount) {
        if(outputPanels != null) {
            for(OutputUnit out : outputPanels) {
                remove(out.getShapedPanel());
            }
        }

        this.channelCount = channelCount;
        outputPanels = new OutputUnit[channelCount.CHANNEL_COUNT];
        Point maxYPoint = panels.getMaxY();
        for(int i = 0; i < outputPanels.length; i++) {
            outputPanels[i] = new OutputUnit(this, i, channelCount, audioThread);
            ShapedPanel sp = outputPanels[i].getShapedPanel();
            if(maxYPoint == null) {
                sp.setRelativePosToReferencePanel(i + 1, 1);
            }
            else {
                sp.setRelativePosToReferencePanel(maxYPoint.x + i, maxYPoint.y + 1);
            }
            moveToPosBasedOnRelativeToRefPanel(sp);
            this.addPanelPermanently(outputPanels[i]);
            Dimension size = new Dimension(getReferencePanelWidth(), getReferencePanelHeight());
            outputPanels[i].getShapedPanel().updateSize(size);
        }
    }


    @Override
    public OutputUnit[] getOutputUnits() {
        return outputPanels;
    }
    @Override
    public int getOutputUnitWrittenSamples() {
        int maxWrittenSamples = 0;
        for(int i = 0; i < outputPanels.length; i++) {
            maxWrittenSamples = Math.max(maxWrittenSamples, outputPanels[i].getWrittenSamplesCount());
        }

        return maxWrittenSamples;
    }

    private SynthesizerMainPanelIFace synthesizerMainPanel;

    public void setOutputAudioFormat(AudioFormatWithSign audioFormat) {
        audioFormat = AudioFormatJPanel.getSupportedAudioFormat(audioFormat);
        AudioControlPanel audioControlPanel = synthesizerMainPanel.getAudioControlPanel();
        if(synthDiagram != null) {     // When setting the first audio audioFormat
            BooleanButton playButton = audioControlPanel.getPlayButton();
            if(!playButton.getBoolVar()) {
                playButton.doClick();
            }
            audioControlPanel.setEnabled(false);
            activeWaitingUntilPaused();
        }
        setOutputPanels(ChannelCount.convertNumberToEnum(audioFormat.getChannels()));
        audioThread.setOutputAudioFormat(audioFormat);
        if(synthDiagram != null) {     // When setting the first audio audioFormat
            audioControlPanel.setMasterGainToCurrentSlideValue();
            audioControlPanel.setEnabled(true);
        }
    }

    private void activeWaitingUntilPaused() {
        while (!audioThread.isPaused() || !synthDiagram.isPaused()) {
            // Active waiting
        }
    }


    private ListSortedByY panels;
    public List<Unit> getPanels() {
        return panels;
    }
    private IntPairWithInternalDoublesWithMinAndMax currentPanelSize;
    private ReferenceMovableJPanel referencePanel;       // Give us the info where is currently the top left STATIC panel
    public int getReferencePanelWidth() {
        return referencePanel.getWidth();
    }
    public int getReferencePanelHeight() {
        return referencePanel.getHeight();
    }

    private Point screenMidPoint;

    ///////// Variables for arrows
    private Arrow arrow;
    private Point[] arrowPointingRight;

    private void setArrowPointingRight(int index, int x, int y) {
        arrowPointingRight[index].x = x;
        arrowPointingRight[index].y = y;
    }

    private void setArrowVariables() {
        arrowPointingRight = new Point[Arrow.ARROW_POINT_COUNT];
        for (int i = 0; i < arrowPointingRight.length; i++) {
            arrowPointingRight[i] = new Point();
        }

        arrow = new Arrow();
    }


    private int getPixelsPerElevation() {
        return (int)(START_PIXELS_PER_ELEVATION * realZoom);
    }
    // div by 2 because we take + and - numbers and -1 to have some space between panels and cables
    private final double maxElevation = (SPACE_BETWEEN_STATIC_PANELS_Y / START_PIXELS_PER_ELEVATION) / 2 - 1;
    @Override
    public int getMaxElevation() {
        return (int)maxElevation;
    }


    public class Arrow {
        public static final int ARROW_POINT_COUNT = 7;

        public Arrow() {
            arrowPoints = new Point[ARROW_POINT_COUNT];
            for (int i = 0; i < arrowPoints.length; i++) {
                arrowPoints[i] = new Point();
            }
            arrowPolygon = new Polygon(new int[ARROW_POINT_COUNT], new int[ARROW_POINT_COUNT], ARROW_POINT_COUNT);
        }

        // TODO: It can be done better by making the polygon private, adding draw method, removing the setArrowPolygon and instead having set method on the arrow points,
        // TODO: which also changes the arrow polygon. But it is to complicated
        /**
         * Represents the points of polygon. It is final so only the values can be changed. Also after changing arrow points, setArrowPolygon method should be called.
         */
        public final Point[] arrowPoints;
        /**
         * Represents the polygon. It is final so only the values can be changed.
         */
        public final Polygon arrowPolygon;

        public void setArrowPolygon() {
            for (int i = 0; i < arrowPoints.length; i++) {
                arrowPolygon.xpoints[i] = arrowPoints[i].x;
                arrowPolygon.ypoints[i] = arrowPoints[i].y;
            }
        }
    }
    /////////

    private MovableJPanel currentlyMovingPanel;
    @Override
    public void setCurrentlyMovingPanel(MovableJPanel movedPanel) {
        currentlyMovingPanel = movedPanel;
    }
    public MovablePanelSpecificGetMethodsIFace getCurrentlyMovingPanel() {
        return currentlyMovingPanel;
    }
    public boolean getIsAnyPanelCurrentlyMoving() {
        return getCurrentlyMovingPanel() != null;
    }


    private MovableJPanel currentlyConnectingPanel;
    public void setCurrentlyConnectingPanel(MovableJPanel connectingPanel) {
        if (getCurrentlyConnectingPanel() != null) {
            for (Unit u : panels) {
                u.getShapedPanel().stopCurrentInputConnecting();
            }
        }
        currentlyConnectingPanel = connectingPanel;

        this.repaint();
    }
    public MovablePanelSpecificGetMethodsIFace getCurrentlyConnectingPanel() {
        return currentlyConnectingPanel;
    }
    public boolean getIsAnyPanelCurrentlyConnecting() {
        return getCurrentlyConnectingPanel() != null;
    }

    /**
     * Should be called when the panel failed to connect (When calling from outside). For example when no port was chosen.
     */
    public void stopConnecting() {
        currentlyConnectingPanel.stopCurrentConnecting();
        setCurrentlyConnectingPanel(null);
        this.repaint();
    }


    public boolean getShouldDrawEdges() {
        return (getIsAnyPanelCurrentlyConnecting() || getIsAnyPanelCurrentlyMoving()) && hasFocus;
    }

    public void connectCurrentlySelectedPanelToGivenPort(InputPort targetPort) {
        currentlyConnectingPanel.getOutputPort().connectToPort(targetPort);
        stopConnecting();
        recalculateAllCables();
    }

    public void connectOutputPortToInputPort(int targetPanelIndexInPanelList, int targetConnectorIndex, OutputPort outputPort) {
        Unit u = panels.get(targetPanelIndexInPanelList);
        InputPort targetPort = u.getInputPort(targetConnectorIndex);
        outputPort.connectToPort(targetPort);
    }

    public boolean checkIfConnectionValid(MovablePanelIFace inputPanel) {
        return checkIfConnectionValid(inputPanel, currentlyConnectingPanel);
    }

    public static boolean checkIfConnectionValid(MovablePanelIFace inputPanel, MovablePanelIFace outputPanel) {
        Point inputLoc = inputPanel.getRelativePosToReferencePanel();
        Point outputLoc = outputPanel.getRelativePosToReferencePanel();
        return inputPanel.getInputPortsCount() > 0 && inputLoc.y > outputLoc.y;
    }


    private MovableJPanel panelCurrenlyConnectingTo = null;
    public MovableJPanel getPanelCurrenlyConnectingTo() {
        return panelCurrenlyConnectingTo;
    }
    public void setPanelCurrentlyConnectingTo(MovableJPanel panel) {
        panelCurrenlyConnectingTo = panel;
    }


    private boolean hasFocus;
    /**
     * Should be called when the windows is visible again.
     */
    public void focusGained() {
        hasFocus = true;
        mousePosTimer.start();
        this.repaint();
    }
    /**
     * Should be called when the window either isn't visible (it is minimized for example), or when we look at other window no (switched tabs).
     */
    public void focusLost() {
        hasFocus = false;
        mousePosTimer.stop();
        this.repaint();
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////    Zooming     ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private int currentZoom = 0;
    private double realZoom = 0;

    private Dimension oldPanelSize;


    private void zoomToMiddleWithWheelMovement(int wheelMovement) {
        Point mouseLoc = new Point(screenMidPoint.x, screenMidPoint.y);
        zoom(wheelMovement, mouseLoc);
    }
    public void zoomToMiddle(int zoom) {
        zoomToMiddleWithWheelMovement(-zoom);
    }

    @Override
    public void zoom(int wheelMovement) {
        if (isWheelMovementZoom(wheelMovement)) {
            Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(mouseLoc, this);
            long currTime = System.currentTimeMillis();
            if (currTime - timeOfLastWheelEvent < WHEEL_TIME_INTERVAL_BETWEEN_RESET_MILLIS) {
                mouseLoc.x = screenMidPoint.x;
                mouseLoc.y = screenMidPoint.y;
            }
            timeOfLastWheelEvent = currTime;
            zoom(wheelMovement, mouseLoc);
        }
        else {
            zoomToMiddleWithWheelMovement(wheelMovement);
        }
    }


    private double getZoomMultiplyFactor(int zoom) {
        return Math.pow(ZOOM_PER_SCROLL, zoom);
    }

    private void zoom(int wheelMovement, Point mouseLoc) {
        int zoomChange = -wheelMovement;
        int zoomSign = Integer.signum(zoomChange);
        double multiplyFactor = getZoomMultiplyFactor(zoomSign);
        int zoomCount = Math.abs(wheelMovement);
        moveXandYWhenZooming(mouseLoc.x - screenMidPoint.x, mouseLoc.y - screenMidPoint.y);       // Move it to the point so middle of the visible screen is where is the cursor
        for(int i = 0; i < zoomCount; i++) {
            oldPanelSize.width = currentPanelSize.getFirst();
            oldPanelSize.height = currentPanelSize.getSecond();

            currentPanelSize.multiplyPairByN(multiplyFactor);
            if(currentPanelSize.equals(oldPanelSize)) {
                // If we reached the minimum or maximum then we will just redraw (because else the cables aren't drawn correctly) and leave
                break;
            }
            currentZoom += zoomSign;

            updateAllPanelsInsideZoomBased();
        }

        synthesizerMainPanel.getAudioControlPanel().getZoomPanel().setNewZoom(ZOOM_COUNT_FROM_START_TO_MIN +
                currentZoom, getReferencePanelWidth() == STATIC_PANEL_MAX_WIDTH);
        this.repaint();
    }

    private void updateAllPanelsInsideZoomBased() {
        Dimension panelSize = new Dimension(currentPanelSize.getFirst(), currentPanelSize.getSecond());
        updateReferencePanel(panelSize);

        int i = 0;
        for(Unit u : panels) {
            MovablePanelIFace panel = u.getShapedPanel();
            moveToPosBasedOnRelativeToRefPanel(panel);
            panel.updateSize(panelSize);
            i++;
        }

        referencePanelOldWidth = getReferencePanelWidth();
        setAbsoluteCables();
    }

    private int referencePanelOldWidth = STATIC_PANEL_START_WIDTH;
    public int getReferencePanelOldWidth() {
        return referencePanelOldWidth;
    }

    private void updateReferencePanel(Dimension panelSize) {
        referencePanel.updateSize(panelSize);     // TODO: Maybe I should create new instance of dimension - but it makes performance a bit worse, and the sizes shouldn't be changed inside the panels
        referencePanel.updateLocation(oldPanelSize, currentPanelSize, screenMidPoint);
        sizeChangeCallback();
    }

    private Point firstPanel = new Point();
    private int borderBasedOnZoomX;
    private int borderBasedOnZoomY;
    private int frameWidth;
    private int frameHeight;
    private int panelSizeWithBorderWidth;
    private int panelSizeWithBorderHeight;

    /**
     * updates internal variables, should be called when zooming
     */
    public void sizeChangeCallback() {
        setStaticPanelsVariables();
        findFirstPanel();
    }

    private void updateFirstPanel(int scrollX, int scrollY) {
        updateFirstPanelX(scrollX);
        updateFirstPanelY(scrollY);
    }

    private void updateFirstPanelX(int scrollX) {
        firstPanel.x += scrollX;
        if (firstPanel.x > borderBasedOnZoomX) {
            firstPanel.x %= panelSizeWithBorderWidth;
            firstPanel.x -= panelSizeWithBorderWidth;
        }
        else if (firstPanel.x <= -panelSizeWithBorderWidth) {
            firstPanel.x %= panelSizeWithBorderWidth;
        }
        if (firstPanel.x <= -(panelSizeWithBorderWidth - borderBasedOnZoomX)) {
            firstPanel.x += panelSizeWithBorderWidth;
        }
    }
    private void updateFirstPanelY(int scrollY) {
        firstPanel.y += scrollY;
        if (firstPanel.y > borderBasedOnZoomY) {
            firstPanel.y %= panelSizeWithBorderHeight;
            firstPanel.y -= panelSizeWithBorderHeight;
        }
        else if (firstPanel.y <= -panelSizeWithBorderHeight) {
            firstPanel.y %= panelSizeWithBorderHeight;
        }
        if (firstPanel.y <= -(panelSizeWithBorderHeight - borderBasedOnZoomY)) {
            firstPanel.y += panelSizeWithBorderHeight;
        }
    }

    private void setStaticPanelsVariables() {
        realZoom = Math.pow(ZOOM_PER_SCROLL, currentZoom);
        borderBasedOnZoomX = (int) (SPACE_BETWEEN_STATIC_PANELS_X * realZoom);
        borderBasedOnZoomY = (int) (SPACE_BETWEEN_STATIC_PANELS_Y * realZoom);
        int w = currentPanelSize.getFirst();
        int h = currentPanelSize.getSecond();
        frameWidth = w / 10;        // Parameter to play with
        frameHeight = h / 10;       // Parameter to play with
        panelSizeWithBorderWidth = w + borderBasedOnZoomX;
        panelSizeWithBorderHeight = h + borderBasedOnZoomY;
    }


    private void findFirstPanel() {
        // Now find the top left panel
        findFirstPanelX();
        findFirstPanelY();
    }

    private void findFirstPanelX() {
        firstPanel.x = referencePanel.getLeftX();
        firstPanel.x %= panelSizeWithBorderWidth;
        // Now moveXandYWhenZooming 1 panel to left, if the panel is too far from start
        if (firstPanel.x > borderBasedOnZoomX) {
            firstPanel.x -= panelSizeWithBorderWidth;
        }

        // The first panel is the panel on the right next to it
        if (firstPanel.x <= -(panelSizeWithBorderWidth - borderBasedOnZoomX)) {
            firstPanel.x += panelSizeWithBorderWidth;
        }
    }

    private void findFirstPanelY() {
        firstPanel.y = referencePanel.getTopY();
        firstPanel.y %= panelSizeWithBorderHeight;
        // Now moveXandYWhenZooming 1 panel up, if the panel is too far from start
        if (firstPanel.y > borderBasedOnZoomY) {
            firstPanel.y -= panelSizeWithBorderHeight;
        }

        // The first panel is the panel under it
        if (firstPanel.y <= -(panelSizeWithBorderHeight - borderBasedOnZoomY)) {
            firstPanel.y += panelSizeWithBorderHeight;
        }
    }


    private static void multiplyDimensionByN(Dimension dim, double n) {
        dim.width *= n;
        dim.height *= n;
    }

    private void multiplyCurrPanelSizeByNWithZeroRepair(Dimension dim, double n) {
        multiplyDimensionByN(dim, n);
        repairZeroSize(dim);
    }


    private static void divideDimensionByN(Dimension dim, double n) {
        dim.width /= n;
        dim.height /= n;
    }

    private void divideCurrPanelSizeByNWithZeroRepair(Dimension dim, double n) {
        divideDimensionByN(dim, n);
        repairZeroSize(dim);
    }

    private void repairZeroSize(Dimension dim) {
        if(dim.width < 1) {
            dim.width = 1;
        }
        if(dim.height < 1) {
            dim.height = 1;
        }
    }

// TODO: REMOVE
    private boolean TODOswap = true;
    private static final boolean isOnNTB = false;
// TODO: REMOVE

    public void observeMouseLoc(Point screenMouseLoc) {
        SwingUtilities.convertPointFromScreen(screenMouseLoc, this);
        tryMoveBoardUsingPolling(screenMouseLoc);
    }


    private void tryMoveBoardUsingPolling(Point mouseLocRelativeToThisPanel) {
        if (getShouldDrawEdges()) {
            int w = this.getWidth();
            int h = this.getHeight();
            boolean shouldRepaint = false;

            if (mouseLocRelativeToThisPanel.x < w && mouseLocRelativeToThisPanel.x > 0 &&  // It is in bounds of panel
                    mouseLocRelativeToThisPanel.y < h && mouseLocRelativeToThisPanel.y > 0) {

                if (mouseLocRelativeToThisPanel.x < SCROLL_BORDER_SIZE_X) {
                    moveLeft(PIXELS_MOVED_PER_TICK);
                    shouldRepaint = true;
                }
                else if (mouseLocRelativeToThisPanel.x > w - SCROLL_BORDER_SIZE_X) {
                    moveRight(PIXELS_MOVED_PER_TICK);
                    shouldRepaint = true;
                }

                if (mouseLocRelativeToThisPanel.y < SCROLL_BORDER_SIZE_Y) {      // If to allow diagonal movement
                    moveUp(PIXELS_MOVED_PER_TICK);
                    shouldRepaint = true;
                }
                else if (mouseLocRelativeToThisPanel.y > h - SCROLL_BORDER_SIZE_Y) {
                    moveDown(PIXELS_MOVED_PER_TICK);
                    shouldRepaint = true;
                }

                if (shouldRepaint) {
                    this.repaint();
                }
            }
        }
    }


    private void moveLeft(int dif) {
        moveX(-dif);
    }
    private void moveRight(int dif) {
        moveX(dif);
    }
    private void moveUp(int dif) {
        moveY(-dif);
    }
    private void moveDown(int dif) {
        moveY(dif);
    }

    private void moveXandYWhenZooming(Point p) {
        moveXandYWhenZooming(p.x, p.y);
    }

    private void moveXandYWhenZooming(int difX, int difY) {
        updateReferencePanelsX(-difX);
        updateReferencePanelsY(-difY);

        for(Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            if(p.getIsBeingMoved()) {
                p.mouseLocationChangedWithoutMouseMovement();
            }
            else {
                p.updateXandYWhenZooming(-difX, -difY);
            }
        }
    }

    private void moveX(int dif) {
        updateReferencePanelsX(-dif);

        for(Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            if(p.getIsBeingMoved()) {
                p.mouseLocationChangedWithoutMouseMovement();
            }
            else {
                p.updateX(-dif);
            }
        }
    }
    private void moveY(int dif) {
        updateReferencePanelsY(-dif);

        for(Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            if(p.getIsBeingMoved()) {
                p.mouseLocationChangedWithoutMouseMovement();
            }
            else {
                p.updateY(-dif);
            }
        }
    }

    private void updateReferencePanelsX(int dif) {
        referencePanel.updateX(dif);
        updateFirstPanelX(dif);
    }

    private void updateReferencePanelsY(int dif) {
        referencePanel.updateY(dif);
        updateFirstPanelY(dif);
    }

    /**
     * Returns true there is some panel on the relative position.
     * @param panel is the panel which we are checking collision against.
     * @param x is the relative position to reference panel of the panel in x coordinate.
     * @param y is the relative position to reference panel of the panel in y coordinate.
     * @return Returns true there is some panel on the relative position.
     */
    public boolean checkForCollisions(Object panel, int x, int y) {
        for(Unit u : panels) {
            MovablePanelSpecificMethodsIFace p = u.getShapedPanel();
            if(p != panel) {
                Point relativePos = p.getRelativePosToReferencePanel();
                if(relativePos.x == x && relativePos.y == y) {
                    return true;
                }
            }
        }

        return false;
    }


    private Dimension minSize = new Dimension(4 * SCROLL_BORDER_SIZE_X, 4 * SCROLL_BORDER_SIZE_Y);
    @Override
    public Dimension getMinimumSize() {
        return minSize;
    }
    @Override
    public void resizeCallback(int newDivLoc) {
        // Uncomment this code if I want to restrict the maximum size in the splitpane
//        System.out.println("resizeCallback:\t" + minSize + "\t" + newDivLoc);
//        minSize = new Dimension(newDivLoc, super.getMinimumSize().height);
//        System.out.println("resizeCallback:\t" + minSize + "\t" + newDivLoc);
    }

    /**
     * Returns true if the x,y is inside any panel and sets the relative position of the movablePanel to that panel.
     * Else returns false and doesn't do anything
     * @param movablePanel is the panel which will have changes relative position if (x,y) is inside panel.
     * @param x is the x coordinate against which will be checked if is inside any panel.
     * @param y is the y coordinate against which will be checked if is inside any panel.
     * @return Returns true if it is inside any panel, false otherwise.
     */
    public boolean lockMovablePanel(MovableJPanel movablePanel, int x, int y) {
        Point newPos = getStaticPanelLocation(x, y);
        if (newPos != null) {
            int difX = newPos.x - referencePanel.getLeftX();
            int difY = newPos.y - referencePanel.getTopY();
            movablePanel.setRelativePosToReferencePanel(difX / panelSizeWithBorderWidth, difY / panelSizeWithBorderHeight);
            movablePanel.correctPositionBasedOnRefPosition();
            return true;
        }

        return false;
    }


    public Point getMovablePanelOnTheLocation(Object panelWhichAsked, int x, int y) {
        Point staticPanelLoc = getStaticPanelLocation(x, y);
        if (staticPanelLoc != null) {
            if(checkForCollisions(panelWhichAsked, staticPanelLoc.x, staticPanelLoc.y)) {
                return staticPanelLoc;
            }
        }

        return null;
    }



    private Point selectedPanel;      // Micro-optim don't have to create array on every call
    /**
     * Finds the static panel which contains point at location (x, y) and returns the top left location of that panel.
     * Returns null if (x, y) is not contained in any panel.
     * @param x
     * @param y
     * @return
     */
    private Point getStaticPanelLocation(int x, int y) {
        int width = currentPanelSize.getFirst();
        int height = currentPanelSize.getSecond();


        // Using modulo instead of while cycle
        int distanceX = x - firstPanel.x;
        int distanceFromClosestPanelX = distanceX % panelSizeWithBorderWidth;
        int distanceY = y - firstPanel.y;
        int distanceFromClosestPanelY = distanceY % panelSizeWithBorderHeight;
        if (isOutsideTest(distanceFromClosestPanelX, width, panelSizeWithBorderWidth - width) ||
                isOutsideTest(distanceFromClosestPanelY, height, panelSizeWithBorderHeight - height)) {
            return null;
        }
        else {
            // Example referencePanel.x = 0; x = -12; border = 10, panelWidth = 10
            // Then result should be = -20
            int refDistanceX = x - referencePanel.getLeftX();
            int returnPanelX = x;
            if (refDistanceX < 0) {
                if(refDistanceX % panelSizeWithBorderWidth != 0) {      // Else we are at the start of the panel
                    returnPanelX -= panelSizeWithBorderWidth + (refDistanceX % panelSizeWithBorderWidth);
                }
            }
            else {
                returnPanelX -= refDistanceX % panelSizeWithBorderWidth;
            }


            int refDistanceY = y - referencePanel.getTopY();
            int returnPanelY = y;
            if (refDistanceY < 0) {
                if(refDistanceY % panelSizeWithBorderHeight != 0) {      // Else we are at the start of the panel
                    returnPanelY -= panelSizeWithBorderHeight + (refDistanceY % panelSizeWithBorderHeight);
                }
            }
            else {
                returnPanelY -= refDistanceY % panelSizeWithBorderHeight;
            }


            selectedPanel = new Point(returnPanelX, returnPanelY);
            return selectedPanel;
        }
    }

    private boolean isOutsideTest(int location, int panelWidth, int borderWidth) {
        return (location >= 0 && location > panelWidth) || (location < 0 && location > -borderWidth);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int startX = firstPanel.x;
        int startY = firstPanel.y;
        int panelW = currentPanelSize.getFirst();
        int panelH = currentPanelSize.getSecond();


        int w = this.getWidth();
        int h = this.getHeight();
        int x = startX;
        int y;

        while (x < w) {
            y = startY;
            while (y < h) {
                g.setColor(Color.GRAY);
                g.fillRect(x, y, panelW, panelH);
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x + frameWidth, y + frameHeight, panelW - 2 * frameWidth, panelH - 2 * frameHeight);
                y += panelSizeWithBorderHeight;
            }

            x += panelSizeWithBorderWidth;
        }
    }


    public float calculateCableThickness() {
        return (float)Math.pow(ZOOM_PER_SCROLL, currentZoom);
    }


    // Have to override paint method because, we want to draw the frame after the drawing of child component
    // Also I have to call paint again, else the cables won't paint over the shaped panels
    private boolean shouldRepaintAgain = false;
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if(shouldRepaintAgain) {
            this.repaint();
        }
        shouldRepaintAgain = !shouldRepaintAgain;

        if(getShouldDrawEdges()) {
            drawScrollEdges(g);
            if(!mousePosTimer.isRunning()) {
                mousePosTimer.start();
            }
        }
        else {
            if(mousePosTimer.isRunning()) {
                mousePosTimer.stop();
            }
        }

//        paintConnectionsAsStraightLines(g);

        g.setColor(Color.BLACK);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(calculateCableThickness()));

        MovablePanelSpecificGetMethodsIFace movingPanel = getCurrentlyMovingPanel();
        Point movingPanelPos = null;
        if(movingPanel != null) {
            movingPanelPos = movingPanel.getLocation();
            movingPanelPos.x += currentPanelSize.firstInt / 2;
            movingPanelPos.y += currentPanelSize.secondInt;
        }

        int endCircleWidth = (int)(realZoom * START_CIRCLE_CONNECTION_SIZE);
        int endCircleHeight = (int)(realZoom * START_CIRCLE_CONNECTION_SIZE);
        // Draw cables/connections
        for (Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            List<Cable> cables = p.getOutputPort().getCables();
            for (Cable cable : cables) {
                g.setColor(Color.black);
                if(p == movingPanel) {
                    Point end = cable.getTargetPort().getLastPoint();
                    g.drawLine(movingPanelPos.x, movingPanelPos.y, end.x, end.y);
                }
                else {
                    MovablePanelSpecificGetMethodsIFace targetPanel = cable.getTargetPort().getPanelWhichContainsPort();
                    if(targetPanel == movingPanel) {
                        Point end = cable.getTargetPort().getLastPoint();
                        g.drawLine(p.getLeftX() + currentPanelSize.firstInt / 2,
                                p.getTopY() + currentPanelSize.secondInt,
                                end.x, end.y);
                    }
                    else {
                        int elevation = cable.getElevation();
                        if (ColorPalet.isValidIndex(elevation)) {
                            g.setColor(ColorPalet.getColor(elevation + ZERO_ELEVATION_INDEX));
                        }
                        g2.draw(cable.getAbsolutePath());
                        Point pointBeforeConnection = cable.getLastPointBeforePort();
                        g.fillArc(pointBeforeConnection.x - endCircleWidth / 2, pointBeforeConnection.y - endCircleHeight / 2,
                                endCircleWidth, endCircleHeight, 0, 360);
                    }
                }
            }
        }
        g.setColor(Color.black);


        if(getIsAnyPanelCurrentlyConnecting()) {
            int botX = currentlyConnectingPanel.getLeftX() + currentPanelSize.firstInt / 2;
            int botY = currentlyConnectingPanel.getTopY() + currentPanelSize.secondInt;
            if(panelCurrenlyConnectingTo != null) {
                g.drawLine(botX, botY,panelCurrenlyConnectingTo.getLeftX() + currentPanelSize.firstInt / 2,
                        panelCurrenlyConnectingTo.getTopY());
            }
            else {
                Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(mouseLoc, this);
                g.drawLine(botX, botY, mouseLoc.x, mouseLoc.y);
            }
        }
    }

    /**
     * Recalculates, relative paths, collisions, and sets absolute coordinates of cables etc.
     */
    public void recalculateAllCables()  {
        for(Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            p.getOutputPort().resetCables();
        }

        setRelativeCableConnections();
        recalculateElevations();
        setAbsoluteCables();

        this.repaint();
    }


    public void setRelativeCableConnections() {
        for(Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            for(Cable cable : p.getOutputPort().getCables()) {
                setRelativeCableConnections(p, cable.getTargetPort(), cable, true);
            }
        }

        makeFromOnlyVerticalCablesCompleteCables();
    }


    public enum VerticalLineCount {
        STRAIGHT_LINE,
        TWO_VERTICAL_LINES,
        THREE_VERTICAL_LINES
    }

    private void makeFromOnlyVerticalCablesCompleteCables() {
        VerticalLineCount verticalLineCount;
        double[][] lines = new double[5][4];
        double arcW = getRelativeArcWidth();
        double arcH = getRelativeArcHeight();


        for(Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            for(Cable cable : p.getOutputPort().getCables()) {
                if(cable.getCableType() == Cable.CableType.STRAIGHT_LINE) {
                    continue;
                }

                PathIterator iterator = cable.getRelativePathIterator();
                iterator.currentSegment(lines[0]);
                iterator.next();
                iterator.currentSegment(lines[1]);
                iterator.next();
                if(!iterator.isDone()) {        // Just straight line
                    iterator.currentSegment(lines[2]);
                    iterator.next();
                    if(!iterator.isDone()) {
                        iterator.currentSegment(lines[3]);
                        iterator.next();
                        iterator.currentSegment(lines[4]);
                        verticalLineCount = VerticalLineCount.THREE_VERTICAL_LINES;
                    }
                    else {
                        verticalLineCount = VerticalLineCount.TWO_VERTICAL_LINES;
                    }
                }
                else {
                    verticalLineCount = VerticalLineCount.STRAIGHT_LINE;
                }


                cable.resetPaths();
                cable.relativePathMoveTo(lines[0][0], lines[0][1]);
                cable.relativePathLineTo(lines[1][0], lines[1][1]);


                switch (verticalLineCount) {
                    // Straight line already solved by if() continue;
                    case TWO_VERTICAL_LINES:
                        setCablesLastParts(cable, arcW, arcH, lines[1][0], lines[1][1], lines[2][0], lines[2][1]);
                        break;
                    case THREE_VERTICAL_LINES:
                        drawHorizontalLine(cable, lines[1][0], lines[1][1], lines[2][0], arcW, arcH);
                        cable.relativePathLineTo(lines[3][0], lines[3][1]);

                        setCablesLastParts(cable, arcW, arcH, lines[3][0], lines[3][1], lines[4][0], lines[4][1]);
                        break;
                    default:
                        System.exit(12346);
                }
            }
        }
    }


    private void setCablesLastParts(Cable cable, double arcW, double arcH,
                                    double startX, double startY, double endX, double endY) {
        drawHorizontalLine(cable, startX, startY, endX, arcW, arcH);
        cable.relativePathLineTo(endX, endY);
    }


    public void setAbsoluteCables() {
        for (Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            p.recalculateCablesAbsolutePaths(referencePanel.getLocation(), currentPanelSize.getInternalsAsDimension(),
                    borderBasedOnZoomX, borderBasedOnZoomY, panelSizeWithBorderWidth,
                    panelSizeWithBorderHeight, getPixelsPerElevation());
        }
    }


    public void recalculateElevations() {
        resetElevations();
        for (Unit u : panels) {
            MovablePanelIFace panel = u.getShapedPanel();
            findElevationsForPort(panel);
        }
    }

    public void resetElevations() {
        for(Unit u : panels) {
            MovablePanelIFace panel = u.getShapedPanel();
            panel.getOutputPort().resetElevations();
        }
    }

    public void findElevationsForPort(MovablePanelIFace panel) {
        OutputPort op = panel.getOutputPort();
        for (Cable c : op.getCables()) {
            findElevation(panel, c);
        }
    }


    // It is better than the boolean version if there is too many cables, then it warrants that the overlap will be uniform.
    private final int[] ELEVATION_ARR = new int[2 * getMaxElevation() + 1];
    private final int ZERO_ELEVATION_INDEX = getMaxElevation();
    // TODO: Micro-Optimization - Currently using nicer but slower version. Maybe later just generate the random numbers instead
    //private Random randomGenerator = new Random();
    public void findElevation(Object panelWhichOutputsTheCable, Cable cable) {
        // The new version uses boolean array, which solves the problem of partial elevation - for example when there is
        // collision with some line which was already on maximum, then it would be set to default value, for all lines
        // colliding with that
        if (!cable.getIsElevationSet()) {        // TODO: Just check for me, I don't think that it is needed, if everything is correct
            // TODO: Micro-Optimization Later I can rewrite it using the System.arrayCopy
            for (int i = 0; i < ELEVATION_ARR.length; i++) {
                ELEVATION_ARR[i] = 0;
            }

            // There are 2 ways to do this, 1st way that I am using - check against all cables with already set elevations
            // This is very effective if there are very little collisions - O(n) where n is number of cables.
            // Since I just set the elevations of all cables to 0 without checking the collisions
            // 2nd way to this is to remove the flag if the elevation is set and I check the cable against all cables after that
            // So the first cable is checked against n -1 cables the 2nd n - 2, etc.
            // which is O((n^2) / 2) always.
            // I think the first one is better since the collision checking isn't computationally trivial at all it is O(n^2)
            // where n here is the number of the cable lines in the longer cable
            // This part of code can be optimized for sure, using better collision detecting, not checking collisions at all
            // for cables which are far away, ...
            for (Unit u : panels) {
                MovablePanelIFace mp = u.getShapedPanel();
                List<Cable> cables = mp.getOutputPort().getCables();
                for (Cable c : cables) {
                    if (cable != c) {
                        if (c.getIsElevationSet()) {
                            if (shouldElevate(cable, c)) {
                                ELEVATION_ARR[ZERO_ELEVATION_INDEX + c.getElevation()]++;
                            }
                        }
                    }
                }
            }


            // Set 1 above, 1 below, etc.
            int addVal = 0;
            for (int i = ZERO_ELEVATION_INDEX; i < ELEVATION_ARR.length; ) {
                if (ELEVATION_ARR[i] == 0) {
                    cable.setElevationToGivenValue(i - ZERO_ELEVATION_INDEX);
                    return;
                }

                if (addVal < 0) {
                    addVal--;
                    addVal = -addVal;
                } else if (addVal > 0) {
                    addVal = -addVal;
                } else {
                    addVal = 1;
                }

                i = ZERO_ELEVATION_INDEX + addVal;
            }
// TODO: RML
// Fast version but not quite visually ok - we first set all the elevations under then the above
//            for(int i = ZERO_ELEVATION_INDEX; i < ELEVATION_ARR.length; i++) {
//                if(!ELEVATION_ARR[i]) {
//                    cable.setElevationToGivenValue(i - ZERO_ELEVATION_INDEX);
//                    return;
//                }
//            }
//            for(int i = 0; i < ZERO_ELEVATION_INDEX; i++) {
//                if(!ELEVATION_ARR[i]) {
//                    cable.setElevationToGivenValue(i - ZERO_ELEVATION_INDEX);
//                    return;
//                }
//            }
// TODO: RML

            int indexWithMinElevation = ZERO_ELEVATION_INDEX;
            int currMin = Integer.MAX_VALUE;
            for (int i = 0; i < ELEVATION_ARR.length; i++) {
                if (ELEVATION_ARR[i] < currMin) {
                    currMin = ELEVATION_ARR[i];
                    indexWithMinElevation = i;
                }
            }

            cable.setElevationToGivenValue(indexWithMinElevation - ZERO_ELEVATION_INDEX);
// TODO: RML
// Faster version using random numbers
//            cable.setElevationToGivenValue(randomGenerator.
//                    nextInt(ELEVATION_ARR.length - 1) - ZERO_ELEVATION_INDEX);
// TODO: RML
        }
    }


    /**
     * prefix h is the horizontal line, v is the vertical line (which is checked if is really vertical)
     * @param h1x
     * @param hy
     * @param h2x
     * @param vx
     * @param v1y
     * @param v2y
     * @return
     */
    public boolean isLineCollision(double h1x, double hy, double h2x,
                                   double vx, double v1y,  double v2y) {
        if(isLineHorizontal(v1y, v2y)) {
            return false;
        }
        else {
            // Sort x
            if(h2x < h1x) {
                double tmp = h1x;
                h1x = h2x;
                h2x = tmp;
            }
            // TODO: FEEDBACK - If I want to allow feedback then I should also sort the v1y and v2y
            return h1x < vx && h2x > vx && hy > v1y && hy < v2y;
        }
    }

    private Point tmpPoint = new Point();
    // For isVertical there are 2 or 3 or 5 points - start and end of the first n/2 (where n == 2,3,5) vertical lines
    // and then the location of end of the last vertical line
    public void setRelativeCableConnections(MovablePanelIFace outputPanel,
                                            InputPort connectedPort,
                                            Cable cable, boolean onlyVertical) {
        cable.setPathAroundTargetPanel();
        MovablePanelSpecificGetMethodsIFace connectedPanel = connectedPort.getPanelWhichContainsPort();
        Point relativeLocStart = outputPanel.getRelativePosToReferencePanel();
        Point relativeLocEnd = connectedPanel.getRelativePosToReferencePanel();
        double distance = (relativeLocEnd.y - relativeLocStart.y) / 2.0d;
        double halfY = relativeLocStart.y + distance;
        boolean isStraightUnder = relativeLocEnd.x == relativeLocStart.x;
        if (isStraightUnder) {
            boolean straightLineCol = hasVerticalLineCollisionWithOtherPanel(outputPanel, connectedPanel, relativeLocStart.x, relativeLocStart.y, relativeLocEnd.y);
            if(straightLineCol) {
                setRelativeCableConnection(outputPanel, connectedPort, cable, onlyVertical);
            }
            else {
                cable.relativePathMoveTo(relativeLocStart.x, relativeLocStart.y);
                connectedPanel.getNextToLastPoint(tmpPoint, connectedPort);
                if(tmpPoint.y == -1) {
                    cable.relativePathLineTo(relativeLocEnd.x, relativeLocEnd.y - 0.5);
                }
                else if(tmpPoint.y == 1) {
                    cable.relativePathLineTo(relativeLocEnd.x, relativeLocEnd.y - 0.5);
                }
                else {
                    cable.relativePathLineTo(relativeLocEnd.x, relativeLocEnd.y);
                }

                cable.setCableType(Cable.CableType.STRAIGHT_LINE);
            }
        }
        else {
            boolean isCollision;
            boolean hasVerticalCollision = true;
            while (distance >= 0.5) {
                isCollision = hasVerticalLineCollisionWithOtherPanel(outputPanel, null, relativeLocStart.x, relativeLocStart.y, halfY);
                if (isCollision) {
                    distance /= 2;
                    halfY -= distance;
                }
                else {
                    hasVerticalCollision = false;
                    break;
                }
            }
            if (hasVerticalCollision) {
                setRelativeCableConnection(outputPanel, connectedPort, cable, onlyVertical);
            }
            else {
                boolean hasCollisionMovingHorizontally;
                if (halfY != Math.floor(halfY)) {
                    hasCollisionMovingHorizontally = false;
                }
                else {
                    hasCollisionMovingHorizontally = hasHorizontalLineCollisionWithOtherPanel(relativeLocStart.x, relativeLocEnd.x, halfY);
                }

                boolean hasCollisionGoingStraightToEnd = hasVerticalLineCollisionWithOtherPanel(null, connectedPanel, relativeLocEnd.x, halfY, relativeLocEnd.y);

                // There is collision going down to end, just make it the old way - take first aisle
                if (hasCollisionGoingStraightToEnd || hasCollisionMovingHorizontally) {
                    setRelativeCableConnection(outputPanel, connectedPort, cable, onlyVertical);
                }
                else {      // Else can go through panels since they are empty
                    if (distance >= 1) {
                        if (distance != Math.floor(distance)) {
                            distance = Math.floor(distance) + 0.5;
                        }
                    }
                    else {
                        distance = 0.5;
                    }
                    halfY = relativeLocStart.y + distance;

                    cable.relativePathMoveTo(relativeLocStart.x, relativeLocStart.y);
                    double x = relativeLocStart.x;
                    double y = halfY;
                    cable.relativePathLineTo(x, y);

                    x = relativeLocEnd.x;
                    connectedPanel.getNextToLastPoint(tmpPoint, connectedPort);
                    x += Math.signum(tmpPoint.y) * 0.5;

                    double arcW = getRelativeArcWidth();
                    double arcH = getRelativeArcHeight();
                    if (!onlyVertical) {
                        drawHorizontalLine(cable, relativeLocStart.x, y, x, arcW, arcH);
                    }
                    y = relativeLocEnd.y;

                    if(tmpPoint.y != 0) {
                        y -= 0.5;
                        cable.relativePathLineTo(x, y);
                        y += 0.5;
                        cable.relativePathLineTo(x, y);
                        cable.relativePathLineTo(x, y);
                    }
                    else {
                        y -= 0.5;
                        cable.relativePathLineTo(x, y);
                        y += 0.5;
                        cable.relativePathLineTo(x, y);
                        cable.relativePathLineTo(x, y);
                    }

                    cable.setCableType(Cable.CableType.ADVANCED_ALGORITHM);
                }
            }
        }
    }



    private boolean hasHorizontalLineCollisionWithOtherPanel(int startX, int endX, double startY) {
        // if line going to left
        if(endX < startX) {
            int tmp = endX;
            endX = startX;
            startX = tmp;
        }
        for (Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            Point pLoc = p.getRelativePosToReferencePanel();
            if (pLoc.y == startY && startX <= pLoc.x && pLoc.x <= endX) {        // There is panel p in way
                return true;
            }
        }

        return false;
    }


    // For case when it is coming from some panel and going to some panel, so we don't want to include those in collision
    private boolean hasVerticalLineCollisionWithOtherPanel(Object outputPanel, Object inputPanel, int startX, double startY, double endY) {
        for (Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            if (outputPanel != p && inputPanel != p) {
                Point pLoc = p.getRelativePosToReferencePanel();
                if (pLoc.x == startX && startY <= pLoc.y && pLoc.y <= endY) {        // There is panel p in way
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasVerticalLineCollisionWithOtherPanel(int startX, double startY, int endY) {
        for (Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            Point pLoc = p.getRelativePosToReferencePanel();
            if (pLoc.x == startX && startY <= pLoc.y && pLoc.y <= endY) {        // There is panel p in way
                return true;
            }
        }

        return false;
    }


    private final double[] pointsFirstCable = new double[4];
    private final double[] pointsSecondCable = new double[4];
    private double[] tmpArrFirstCable = new double[4];
    private double[] tmpArrSecondCable = new double[4];

    /**
     * Checks for collisions between the given cables. Returns true if there was collision, that means elevation is needed.
     * @param c1
     * @param c2
     * @return
     */
    private boolean shouldElevate(Cable c1, Cable c2) {
// TODO: RML
// A bit slower variant I think - micro-optimization (but since can run many times it can add to noticeable difference)
// Because of the spatial locality (here we go through the iterators of c1 twice instead of once)
//        PathIteratorGetterIFace iteratorGetter = new RelativePathIteratorGetter(c1);
//        PathIteratorGetterIFace iterator2Getter = new RelativePathIteratorGetter(c2);
//        if(shouldElevate(iteratorGetter, iterator2Getter)) {
//            return true;
//        }
//        else {
//            iteratorGetter = new PathAroundIteratorGetter(c1);
//            return shouldElevate(iteratorGetter, iterator2Getter);
//        }

//        PathIteratorGetterIFace iteratorGetter = new RelativePathIteratorGetter(c1);
//        PathIteratorGetterIFace iterator2Getter = new RelativePathIteratorGetter(c2);
//        if(shouldElevate(iteratorGetter, iterator2Getter)) {
//            return true;
//        }
//        else {
//            iterator2Getter = new PathAroundIteratorGetter(c2);
//            if(shouldElevate(iteratorGetter, iterator2Getter)) {
//                return true;
//            }
//            else {
//                iteratorGetter = new PathAroundIteratorGetter(c1);
//                if(shouldElevate(iteratorGetter, iterator2Getter)) {
//                    return true;
//                }
//                else {
//                    iterator2Getter = new RelativePathIteratorGetter(c2);
//                    return shouldElevate(iteratorGetter, iterator2Getter);
//                }
//            }
//        }
// TODO: RML


// A bit faster variant
        PathIteratorGetterIFace iteratorGetter = new RelativePathIteratorGetter(c1);
        RelativePathIteratorGetter iterator2Getter = new RelativePathIteratorGetter(c2);
        PathAroundIteratorGetter iterator2PathAroundGetter = new PathAroundIteratorGetter(c2);
        if(shouldElevate(iteratorGetter, iterator2Getter, iterator2PathAroundGetter)) {
            return true;
        }
        else {
            iteratorGetter = new PathAroundIteratorGetter(c1);
            return shouldElevate(iteratorGetter, iterator2Getter, iterator2PathAroundGetter);
        }
    }


    private interface PathIteratorGetterIFace {
        PathIterator getPathIterator();
        int getPathIteratorLen();
    }

    private class RelativePathIteratorGetter implements PathIteratorGetterIFace {
        public RelativePathIteratorGetter(Cable cable) {
            this.cable = cable;
        }

        private Cable cable;

        @Override
        public PathIterator getPathIterator() {
            return cable.getRelativePathIterator();
        }

        @Override
        public int getPathIteratorLen() {
            return cable.getRelativePathLen();
        }
    }


    private class PathAroundIteratorGetter implements PathIteratorGetterIFace {
        public PathAroundIteratorGetter(Cable cable) {
            this.cable = cable;
        }

        private Cable cable;

        @Override
        public PathIterator getPathIterator() {
            return cable.getPathAroundTargetPanelIterator();
        }

        @Override
        public int getPathIteratorLen() {
            return cable.getPathAroundTargetPanelLen();
        }
    }


    /**
     * It checks for collisions between the iterator given by the first iterator getter and the iterator given by the 2nd iterator getter.
     * @param iteratorGetter
     * @param iterator2Getter
     * @return
     */
    private boolean shouldElevate(PathIteratorGetterIFace iteratorGetter,
                                  PathIteratorGetterIFace iterator2Getter) {
        int firstCablePartIndex;

        PathIterator iterator = iteratorGetter.getPathIterator();
        int firstIteratorLen = iteratorGetter.getPathIteratorLen();
        setPointsForOverlapCheck(iterator, pointsFirstCable);
        iterator.next();
        firstCablePartIndex = 1;
        int secondIteratorLen = iterator2Getter.getPathIteratorLen();


        for(; firstCablePartIndex < firstIteratorLen; firstCablePartIndex++, iterator.next()) {
            int firstCableType = setPointsForOverlapCheck(iterator, pointsFirstCable);
            if (firstCableType == PathIterator.SEG_MOVETO || firstCableType == PathIterator.SEG_QUADTO) {
                continue;
            }

            PathIterator iterator2 = iterator2Getter.getPathIterator();
            shouldElevateInternal(iterator2, secondIteratorLen);
        }

        return false;
    }


    /**
     * A bit faster variant. It checks collision of the iterator from the iteratorGetter in first argument.
     * Against the other 2 arguments.
     * @param iteratorGetter
     * @param iterator2RelativePathGetter
     * @param iterator2PathAroundGetter
     * @return
     */
    private boolean shouldElevate(PathIteratorGetterIFace iteratorGetter,
                                  RelativePathIteratorGetter iterator2RelativePathGetter,
                                  PathAroundIteratorGetter iterator2PathAroundGetter) {
        int firstCablePartIndex;

        PathIterator iterator = iteratorGetter.getPathIterator();
        int firstIteratorLen = iteratorGetter.getPathIteratorLen();
        setPointsForOverlapCheck(iterator, pointsFirstCable);
        iterator.next();
        firstCablePartIndex = 1;


        for(; firstCablePartIndex < firstIteratorLen; firstCablePartIndex++, iterator.next()) {
            int firstCableType = setPointsForOverlapCheck(iterator, pointsFirstCable);
            if (firstCableType == PathIterator.SEG_MOVETO || firstCableType == PathIterator.SEG_QUADTO) {
                continue;
            }

            if(shouldElevateInternal(iterator2RelativePathGetter, iterator2PathAroundGetter)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Checks collision of the 2 given iterators against the content of pointsFirstCable.
     * @param relativePathGetter
     * @param pathAroundGetter
     * @return
     */
    private boolean shouldElevateInternal(RelativePathIteratorGetter relativePathGetter,
                                          PathAroundIteratorGetter pathAroundGetter) {
        PathIterator iterator = relativePathGetter.getPathIterator();
        int iteratorLen = relativePathGetter.getPathIteratorLen();
        if(shouldElevateInternal(iterator, iteratorLen)) {
            return true;
        }
        else {
            iterator = pathAroundGetter.getPathIterator();
            iteratorLen = pathAroundGetter.getPathIteratorLen();
            return shouldElevateInternal(iterator, iteratorLen);
        }

    }

    /**
     * Checks collision of the iterator against the content of pointsFirstCable.
     * @param iterator
     * @param iteratorLen
     * @return
     */
    private boolean shouldElevateInternal(PathIterator iterator, int iteratorLen) {
        setPointsForOverlapCheck(iterator, pointsSecondCable);
        iterator.next();

        for (int secondCablePartIndex = 1; secondCablePartIndex < iteratorLen; secondCablePartIndex++, iterator.next()) {
            int secondCableType = setPointsForOverlapCheck(iterator, pointsSecondCable);
            if (secondCableType == PathIterator.SEG_LINETO && isLineOverlap()) {
                return true;
            }
        }

        return false;
    }


    private static void skipTwoInIterator(PathIterator iterator, double[] arrForPoints) {
        setPointsForOverlapCheck(iterator, arrForPoints);
        iterator.next();
        setPointsForOverlapCheck(iterator, arrForPoints);
        iterator.next();
    }

    private static int setPointsForOverlapCheck(PathIterator iterator, double[] arrForPoints) {
        double val1 = arrForPoints[2];
        double val2 = arrForPoints[3];
        int type = iterator.currentSegment(arrForPoints);
        // I have to this because the currentSegment method only returns 1 point - the end point for line to and move to
        if(type == PathIterator.SEG_MOVETO) {
            arrForPoints[2] = arrForPoints[0];
            arrForPoints[3] = arrForPoints[1];
        }
        else if(type != PathIterator.SEG_QUADTO) {      // If == then don't do anything it is already set as it should be
            arrForPoints[2] = arrForPoints[0];
            arrForPoints[3] = arrForPoints[1];
            arrForPoints[0] = val1;
            arrForPoints[1] = val2;
        }

        return type;
    }

    public boolean isLineOverlap() {
        for(int i = 0; i < pointsFirstCable.length; i++) {
            tmpArrFirstCable[i] = pointsFirstCable[i];
            tmpArrSecondCable[i] = pointsSecondCable[i];
        }

        boolean isLineOneHorizontal = isLineHorizontal(tmpArrFirstCable[1], tmpArrFirstCable[3]);
        boolean isLineTwoHorizontal = isLineHorizontal(tmpArrSecondCable[1], tmpArrSecondCable[3]);

        if(isLineOneHorizontal == isLineTwoHorizontal) {
            double leftEnd, rightStart; // Left is top, right is bot, if the line is vertical
            if(isLineOneHorizontal) {
                if(tmpArrFirstCable[1] != tmpArrSecondCable[1]) {
                    return false;           // It is now in the same row
                }
                // Sort it by x
                sortHorizontal(tmpArrFirstCable);
                sortHorizontal(tmpArrSecondCable);
                // Swaps the lines, so in the tmpArrFirstCable is the line starting more on left.
                // (Both arrays are expected to be sorted and both lines to be horizontal)
                if(tmpArrSecondCable[0] < tmpArrFirstCable[0]) {
                    double[] tmp = tmpArrSecondCable;
                    tmpArrSecondCable = tmpArrFirstCable;
                    tmpArrFirstCable = tmp;
                }


                leftEnd = tmpArrFirstCable[2];
                rightStart = tmpArrSecondCable[0];
            }
            else {
                if(tmpArrFirstCable[0] != tmpArrSecondCable[0]) {
                    return false;           // It is now in the same column
                }

                // Sort it by y
                sortVertical(tmpArrFirstCable);
                sortVertical(tmpArrSecondCable);
                // Swaps the lines, so in the tmpArrFirstCable is the line starting more on top.
                // (Both arrays are expected to be sorted and both lines to be vertical)
                if(tmpArrSecondCable[1] < tmpArrFirstCable[1]) {
                    double[] tmp = tmpArrSecondCable;
                    tmpArrSecondCable = tmpArrFirstCable;
                    tmpArrFirstCable = tmp;
                }

                leftEnd = tmpArrFirstCable[3];
                rightStart = tmpArrSecondCable[1];
            }

            // The line on left completely contains the line on right
            // Or the line more on left ends somewhere between the start and end of second line
            return leftEnd >= rightStart;       // It is [-]- or [--] ... This covers it all when the lines are sorted
        }
        else {
            return false;
        }
    }

    // Sorts output for GeneralPath iterator when called currentSegment
    // Also the line is expected to be horizontal (so the y coordinate is same for both)
    private static void sortHorizontal(double[] arr) {
        if(arr[2] < arr[0]) {
            double tmp = arr[0];
            arr[0] = arr[2];
            arr[2] = tmp;
        }
    }

    // Also the line is expected to be vertical (so the x coordinate is same for both)
    private static void sortVertical(double[] arr) {
        if(arr[3] < arr[1]) {
            double tmp = arr[1];
            arr[1] = arr[3];
            arr[3] = tmp;
        }
    }

    private static boolean isLineHorizontal(double startY, double endY) {
        return startY == endY;
    }


    private void paintConnectionsAsStraightLines(Graphics g) {
        for (Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            int x = p.getLeftX() + currentPanelSize.firstInt / 2;
            int y = p.getTopY() + currentPanelSize.secondInt;
            List<Port> connectedPorts = p.getOutputPort().getConnectedPorts();
            for (Port port : connectedPorts) {
                MovablePanelViewForPort c = port.getPanelWhichContainsPort();
                Point absolutePos = c.getLocation();
                putInMid(absolutePos, currentPanelSize.firstInt);
                g.setColor(Color.red);
                g.drawLine(x,y, absolutePos.x, absolutePos.y);
            }
        }
    }



    public void setRelativeCableConnection(MovablePanelIFace outputPanel, InputPort connectedPort,
                                           Cable cable, boolean onlyVertical) {
        MovablePanelSpecificGetMethodsIFace connectedPanel = connectedPort.getPanelWhichContainsPort();
        cable.setCableType(Cable.CableType.AISLE_ALGORITHM);        // may be set to straight line

        Point relativePosOutput = outputPanel.getRelativePosToReferencePanel();
        double startX = outputPanel.getRelativePosToReferencePanel().x;
        double startY = outputPanel.getRelativePosToReferencePanel().y;

        cable.resetPaths();
        cable.relativePathMoveTo(startX, startY);

        Point endPosInts = connectedPanel.getRelativePosToReferencePanel();
        Point2D.Double endPos = new Point2D.Double(endPosInts.x, endPosInts.y);
        connectedPanel.getNextToLastPoint(tmpPoint, connectedPort);
        boolean isConnectionOnSide = true;
        if(tmpPoint.y == 0) {
            isConnectionOnSide = false;
        }
        else if (tmpPoint.y == -1) {
            endPos.x -= 0.5;
        }
        else {
            endPos.x += 0.5;
        }

        if (checkIfMovingAndIsNotInStaticPanel(outputPanel) || checkIfMovingAndIsNotInStaticPanel(connectedPanel)) {
            cable.relativePathLineTo((double) endPos.x, (double) endPos.y);
            cable.setCableType(Cable.CableType.STRAIGHT_LINE);
        }
        else {
            if (relativePosOutput.y + 1 == endPos.y) {     // If it is in line below
                connectPanelsInAdjacentRows(cable, endPosInts.x, relativePosOutput.x, startX, startY, endPos, outputPanel, onlyVertical);
            }
            else {
                connectPanelsInNotAdjacentRows(cable, startX, startY, endPos, outputPanel, onlyVertical, isConnectionOnSide);
            }
        }
    }


    private boolean checkIfMovingAndIsNotInStaticPanel(MovablePanelSpecificGetMethodsIFace panel) {
        return panel.getIsBeingMoved() &&
                getStaticPanelLocation(panel.getLocation().x, panel.getLocation().y) == null;
    }


    private void connectPanelsInAdjacentRows(Cable cable, int relativePosInputX, int relativePosOutputX,
                                             double startX, double startY, Point2D.Double endPos, Object outputPanel, boolean onlyVertical) {

        if(relativePosInputX == relativePosOutputX) {     // If it is also in the same column
            cable.relativePathLineTo(endPos.x, endPos.y);
            cable.setCableType(Cable.CableType.STRAIGHT_LINE);
        }
        else {
            double midY = startY + 0.5;
            cable.relativePathLineTo(startX, midY);

            final double arcW = getRelativeArcWidth();
            final double arcH = getRelativeArcHeight();
            if(!onlyVertical) {
                drawHorizontalLine(cable, startX, midY, endPos.x, arcW, arcH, 0.5, outputPanel);
            }

            cable.relativePathLineTo(endPos.x, endPos.y);
        }
    }

    public static double getRelativeArcWidth() {
        return 1;
    }
    public static double getRelativeArcHeight() {
        return 0.5;
    }


    private void drawHorizontalLine(Cable cable, double x1, double y, double x2, double arcW, double arcH) {
        if(x1 == x2) {
            return;
        }
        if (checkIfTheTargetOfOutputPanelIsOnLeft(x1, x2)) {
            drawHorizontalLineToLeft(cable, x1, y, x2, arcW, arcH);
        }
        else {
            drawHorizontalLineToRight(cable, x1, y, x2, arcW, arcH);
        }
    }


    private void drawHorizontalLineToLeft(Cable cable, double x1, double y, double x2, double arcW, double arcH) {
        double colX = x1 - 0.5;
        double arcEndX = colX - arcW / 2;
        if(arcEndX < x2) {
            // Just draw line, the next possible arc is after the x2
            cable.relativePathLineTo(x2, y);
        }
        else {  // There are possibilities for collisions, check them and draw arcs if some is found
            List<Double> arcLocations = new ArrayList<>();
            setCollisionsOnHorizontalLine(cable, x1, y, x2, true, arcLocations);
            double x = x1;
            double lastArcLoc = x;
            for(double d : arcLocations) {
                if(lastArcLoc != d) {
                    lastArcLoc = d;
                    x = d + arcW / 2;
                    cable.relativePathLineTo(x, y);
                    cable.relativePathQuadTo(x - arcW / 2, y - arcH, x - arcW, y);
                }
            }
            if(x != x2 || (!arcLocations.isEmpty() && x - arcW != x2)) {
                cable.relativePathLineTo(x2, y);
            }
        }
    }


    private void drawHorizontalLineToRight(Cable cable, double x1, double y, double x2, double arcW, double arcH) {
        double colX = x1 + 0.5;
        double arcEndX = colX + arcW / 2;
        if(arcEndX > x2) {
            // Just draw line, the next possible arc is after the x2
            cable.relativePathLineTo(x2, y);
        }
        else {  // There are possibilities for collisions, check them and draw arcs if some is found
            List<Double> arcLocations = new ArrayList<>();
            setCollisionsOnHorizontalLine(cable, x1, y, x2, false, arcLocations);
            double x = x1;
            double lastArcLoc = x;
            for(double d : arcLocations) {
                if(lastArcLoc != d) {
                    lastArcLoc = d;
                    x = d - arcW / 2;
                    cable.relativePathLineTo(x, y);
                    cable.relativePathQuadTo(x + arcW / 2, y - arcH, x + arcW, y);
                }
            }
            if(x != x2 || (!arcLocations.isEmpty() && x + arcW != x2)) {
                cable.relativePathLineTo(x2, y);
            }
        }
    }



    private void setCollisionsOnHorizontalLine(Cable cable, double x1, double y, double x2,
                                               boolean isTargetOnLeft, List<Double> arcLocations) {
        // For all panels check the outputs and check of collision - that means the target of output panel in panel p
        // has to be be lower that the y and also the output panel has to be above the line, and also the x must be equal
        for (Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            List<Cable> cables = p.getOutputPort().getCables();
            for (Cable c : cables) {
                if(c != cable) {            // TODO: Probably not even needed
                    setCollisionsOnHorizontalLine(c, x1, x2, y, arcLocations);
                }
            }
        }

        if (isTargetOnLeft) {
            // sort from highest to lowest
            arcLocations.sort(new Comparator<Double>() {
                @Override
                public int compare(Double o1, Double o2) {
                    if(o2 > o1) {
                        return 1;
                    }
                    else if(o2 < o1) {
                        return -1;
                    }
                    return 0;
                }
            });
        } else {
            arcLocations.sort(null);
        }
    }


    @Deprecated // Not needed anymore since the algorithm for finding arcs prolongs automatically which is how it should be
    private boolean checkIfShouldProlongLastVerticalLine(Cable cable) {
        boolean shouldProlongLastVerticalLine = false;
        Point nextToLastPoint = cable.getTargetPort().getNextToLastPoint();

        boolean isSideConnection = nextToLastPoint.y == 1 || nextToLastPoint.y == -1;

        if (isSideConnection &&
                (cable.getCableType() == Cable.CableType.ADVANCED_ALGORITHM ||
                        cable.getCableType() == Cable.CableType.AISLE_ALGORITHM)) {
            shouldProlongLastVerticalLine = true;
        }

        return shouldProlongLastVerticalLine;
    }


    private final double[] tmpLineArr = new double[4];
    // Sets the arcLocations with x coordinates of the collisions
    private void setCollisionsOnHorizontalLine(Cable cableToCheckColAgainst, double startX,
                                               double endX, double y, List<Double> arcLocations) {
        PathIterator iterator = cableToCheckColAgainst.getRelativePathIterator();
        setCollisionsOnHorizontalLine(iterator, startX, endX, y, arcLocations);
    }

    private void setCollisionsOnHorizontalLine(PathIterator iterator, double startX,
                                               double endX, double y, List<Double> arcLocations) {
        double highestYForCurrentX = Integer.MIN_VALUE;

        for(double oldX = 0, oldY = 0; !iterator.isDone();) {
            int type = iterator.currentSegment(tmpLineArr);
            iterator.next();
            if(type == PathIterator.SEG_MOVETO) {
                oldX = tmpLineArr[0];
                oldY = tmpLineArr[1];
                highestYForCurrentX = oldY;
            }
            else if(type == PathIterator.SEG_LINETO) {
                // If it is "diagonal" line - it happens when we are checking collision against cable which doesn't have set vertical lines yet
                if(oldX != tmpLineArr[0] && oldY != tmpLineArr[1]) {
                    highestYForCurrentX = oldY;
                }
                else if(isLineHorizontal(oldY, tmpLineArr[1])) {
                    highestYForCurrentX = tmpLineArr[1];
                }

                if(isLineCollision(startX, y, endX, tmpLineArr[0], highestYForCurrentX, tmpLineArr[1])) {
                    arcLocations.add(tmpLineArr[0]);
                }

                oldY = tmpLineArr[1];
                oldX = tmpLineArr[0];
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    /**
     * Draws horizontal line with solving collisions. Drawing left to right, that means x1 < x2 else this method doesn't work correctly
     * @param cable is the class containing path to be drew.
     * @param x1 must be smaller than x2
     * @param y is the y where to draw the line
     * @param x2 is the end of line
     * @param arcW is the width of arc
     * @param arcH is the height of arc
     * @param distToFirstCrossroad is the distance from x1 to first crossroad, it is always > 0 even when x2 < x1 is on right
     * @param outputPanel is the panel from which is going the line
     */
    private void drawHorizontalLine(Cable cable, double x1, double y, double x2, double arcW, double arcH,
                                    double distToFirstCrossroad, Object outputPanel) {
        if (checkIfTheTargetOfOutputPanelIsOnLeft(x1, x2)) {
            drawHorizontalLineToLeft(cable, x1, y, x2, distToFirstCrossroad, arcW, arcH, outputPanel);
        }
        else {
            drawHorizontalLineToRight(cable, x1, y, x2, distToFirstCrossroad, arcW, arcH, outputPanel);
        }
    }


    private void drawHorizontalLineToLeft(Cable cable, double x1, double y, double x2, double distToFirstCrossroad,
                                          double arcW, double arcH, Object outputPanel) {
        double colX = x1 - distToFirstCrossroad;
        double arcEndX = colX - arcW / 2;
        Debug.debugPrint(x1, arcEndX, colX, x2);
        if(arcEndX < x2 || distToFirstCrossroad < 0) {
            // Just draw line, the next possible arc is after the x2
            cable.relativePathLineTo(x2, y);
        }
        else {  // There are possibilities for collisions, check them and draw arcs if some is found
            drawHorizontalLinePart(cable, x1, y, arcEndX, arcW, arcH, colX, outputPanel, true);
            double nextArcEndX = arcEndX - 1;
            while (nextArcEndX >= x2) {
                colX -= 1;
                drawHorizontalLinePart(cable, arcEndX, y, nextArcEndX, arcW, arcH, colX, outputPanel, true);
                arcEndX = nextArcEndX;
                nextArcEndX -= 1;
            }

            cable.relativePathLineTo(x2, y);
        }
    }

    private void drawHorizontalLineToRight(Cable cable, double x1, double y, double x2, double distToFirstCrossroad,
                                           double arcW, double arcH, Object outputPanel) {
        double colX = x1 + distToFirstCrossroad;
        double arcEndX = colX + arcW / 2;
        Debug.debugPrint(x1, arcEndX, colX, x2);
        if(arcEndX > x2 || distToFirstCrossroad < 0) {
            // Just draw line, the next possible arc is after the x2
            cable.relativePathLineTo(x2, y);
        }
        else {   // There are possibilities for collisions, check them and draw arcs if some is found
            drawHorizontalLinePart(cable, x1, y, arcEndX, arcW, arcH, colX, outputPanel, false);
            double nextArcEndX = arcEndX + 1;
            while (nextArcEndX <= x2) {
                colX += 1;
                drawHorizontalLinePart(cable, arcEndX, y, nextArcEndX, arcW, arcH, colX, outputPanel, false);
                arcEndX = nextArcEndX;
                nextArcEndX += 1;
            }

            cable.relativePathLineTo(x2, y);
        }
    }


    /**
     *
     * @param cable
     * @param x1
     * @param y
     * @param x2
     * @param arcW
     * @param arcH
     * @param colX
     * @param outputPanel
     * @param isTargetOnLeft
     * @return Returns true if arc was drawn, else returns false and doesn't draw anything
     */
    private boolean drawHorizontalLinePart(Cable cable, double x1, double y, double x2, double arcW, double arcH, double colX,
                                           Object outputPanel, boolean isTargetOnLeft) {
        // For all panels check the outputs and check of collision - that means the target of output panel in panel p
        // has to be be lower that the y and also the output panel has to be above the line, and also the x must be equal
        for(Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            if(outputPanel != p) {
                List<Port> connectedPorts = p.getOutputPort().getConnectedPorts();
                Point relativeLocOutput = p.getRelativePosToReferencePanel();
                for (Port port : connectedPorts) {
                    MovablePanelSpecificGetMethodsIFace mp = port.getPanelWhichContainsPort();
                    if(isTargetOnLeft) {
                        if(drawHorizontalLinePartCheckAgainstOnePanel(mp, cable,
                                relativeLocOutput, x2, y, colX, arcW, arcH, true)) {
                            return true;
                        }
                    }
                    else {
                        if(drawHorizontalLinePartCheckAgainstOnePanel(mp, cable,
                                relativeLocOutput, x2, y, colX, arcW, arcH, false)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


    private boolean drawHorizontalLinePartCheckAgainstOnePanel(MovablePanelSpecificGetMethodsIFace outputPanelOfThePossibleCollisionCable,
                                                               Cable cableBeingBuild,
                                                               Point relativeLocOutput,
                                                               double x2, double y, double colX, double arcW,
                                                               double arcH, boolean isHorizontalLineToLeft) {
        double outputAisleX = relativeLocOutput.x;
        Point relativeLocInput = outputPanelOfThePossibleCollisionCable.getRelativePosToReferencePanel();
        // If it is line which ends below the current y and starts above current y
        // Second line is to disable collisions when the horizontal line goes through line which just changed from horizontal to vertical and it is at least 2 rows
        // Third line is when the second line changes from vertical to horizontal and it is also at least 2 rows - TODO: SAME CONDITION FOR NUBMER OF ROWS - BUT IT IS MICRO-OPTIM don't do it
        if (relativeLocInput.y > y && relativeLocOutput.y < y
                && !(relativeLocOutput.y + 1 - 0.5 == y && relativeLocInput.y - relativeLocOutput.y >= 2)
                && !(relativeLocInput.y - 0.5 == y/* && relativeLocInput.y - relativeLocOutput.y >= 2*/)) {

            if (checkIfTheTargetOfOutputPanelIsOnLeft(relativeLocOutput.x, relativeLocInput.x)) {
                outputAisleX -= 0.5;
            } else {
                outputAisleX += 0.5;
            }
            if (outputAisleX == colX) {       // Collision
                if (isHorizontalLineToLeft) {
                    double curveStartX = colX + arcW / 2;
                    cableBeingBuild.relativePathLineTo(curveStartX, y);
                    cableBeingBuild.relativePathQuadTo(curveStartX - arcW / (double)2, (y - arcH), (curveStartX - arcW), y);
                    return true;
                } else {
                    double curveStartX = colX - arcW / 2;
                    cableBeingBuild.relativePathLineTo(curveStartX, y);
                    cableBeingBuild.relativePathQuadTo(curveStartX + arcW / (double)2, (y - arcH), (curveStartX + arcW), y);
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean checkIfTheTargetOfOutputPanelIsOnLeft(Cable c) {
        return checkIfTheTargetOfOutputPanelIsOnLeft(c.getSourcePanelRelativeLoc().x, c.getTargetPanelRelativeLoc().x);

    }

    private static boolean checkIfTheTargetOfOutputPanelIsOnLeft(double x, double endPosX) {
        return endPosX < x;
    }


    private void connectPanelsInNotAdjacentRows(Cable cable, double startX, double startY, Point2D.Double endPos,
                                                Object outputPanel, boolean onlyVertical, boolean isConnectionOnSide) {
        final double arcW = getRelativeArcWidth();
        final double arcH = getRelativeArcHeight();

        double drawX = startX;
        double drawY = startY + 0.5;                     // Down
        cable.relativePathLineTo(drawX, drawY);
        boolean isTargetOnLeft = checkIfTheTargetOfOutputPanelIsOnLeft(startX, endPos.x);

        if (isTargetOnLeft) {
            drawX -= 0.5;              // Left
        }
        else {
            drawX += 0.5;              // Right
        }
        cable.relativePathLineTo(drawX, drawY);
        double oldDrawX = drawX;

        drawY = endPos.y - 0.5;         // Down
        cable.relativePathLineTo(drawX, drawY);
        oldDrawX = drawX;

        drawX = endPos.x;                                  // above the middle of the panel
        if(!onlyVertical) {
            drawHorizontalLine(cable, oldDrawX, drawY, drawX, arcW, arcH, 1, outputPanel);
        }

        if(isConnectionOnSide) {
            cable.relativePathLineTo(drawX, (double) endPos.y);
        }
        else {
            cable.relativePathLineTo(drawX, (double) drawY);
        }
    }



    private void putInMid(Point p, int w) {
        p.x += w / 2;
    }

    public void add(Point p, int x, int y) {
        p.x += x;
        p.y += y;
    }

    private void drawScrollEdges(Graphics g) {
        Color oldColor = g.getColor();
        double widthProportionTriangle = 0.2;           // Parameter to play with
        double heightProportionRect = 0.4;              // Parameter to play with
        int x, y, w, h;
        w = this.getWidth();
        h = this.getHeight();

        x = 0;
        y = 0;
        drawScrollEdge(g, x, y, w, SCROLL_BORDER_SIZE_X, BORDER_COLOR);   // top
        drawEdgeArrow(g, w, h, widthProportionTriangle, heightProportionRect, ARROW_COLOR, ArrowDirection.NORTH);

        x = w - SCROLL_BORDER_SIZE_X;
        y = 0;
        drawScrollEdge(g, x, y, SCROLL_BORDER_SIZE_X, h, BORDER_COLOR);   // right
        drawEdgeArrow(g, w, h, widthProportionTriangle, heightProportionRect, ARROW_COLOR, ArrowDirection.EAST);

        x = 0;
        y = h - SCROLL_BORDER_SIZE_Y;
        drawScrollEdge(g, x, y, w, SCROLL_BORDER_SIZE_Y, BORDER_COLOR);   // bot
        drawEdgeArrow(g, w, h, widthProportionTriangle, heightProportionRect, ARROW_COLOR, ArrowDirection.SOUTH);

        x = 0;
        y = 0;
        drawScrollEdge(g, x, y, SCROLL_BORDER_SIZE_X, h, BORDER_COLOR);   // left
        drawEdgeArrow(g, w, h, widthProportionTriangle, heightProportionRect, ARROW_COLOR, ArrowDirection.WEST);

        drawEdgeArrow(g, w, h, widthProportionTriangle, heightProportionRect, ARROW_COLOR, ArrowDirection.NORTH_EAST);
        drawEdgeArrow(g, w, h, widthProportionTriangle, heightProportionRect, ARROW_COLOR, ArrowDirection.SOUTH_EAST);
        drawEdgeArrow(g, w, h, widthProportionTriangle, heightProportionRect, ARROW_COLOR, ArrowDirection.SOUTH_WEST);
        drawEdgeArrow(g, w, h, widthProportionTriangle, heightProportionRect, ARROW_COLOR, ArrowDirection.NORTH_WEST);

        g.setColor(oldColor);
    }

    private void drawScrollEdge(Graphics g, int x, int y, int w, int h, Color edgeColor) {
        g.setColor(edgeColor);
        g.fillRect(x, y, w, h);
    }


    /**
     *
     * @param g is the graphics to draw to
     * @param w is the width of the edge.
     * @param h is the height of the edge.
     * @param widthProportionTriangle lies in interval [0,1]. 0 means that the triangle takes 0 pixel, 1 the triangle takes the whole width (the w parameter)
     * @param heightProportionRect lies in interval [0,1]. 0 means that the rectangle takes 0 pixel, 1 the rectangle takes the whole height (the h parameter)
     * @param c is the color of the arrow
     * @param direction is the direction of the arrow.
     */
    private void drawEdgeArrow(Graphics g, int w, int h, double widthProportionTriangle, double heightProportionRect,
                               Color c, ArrowDirection direction) {
        int x = 0, y = 0;       // Default init because compiler doesn't see that it will be init in switch
        int triangleW = (int)(ARROW_SIZE_X * widthProportionTriangle);
        int rectW = ARROW_SIZE_X - triangleW;
        int rectH = (int)(ARROW_SIZE_Y * heightProportionRect);
        int rectangleTriangleDistanceY = (ARROW_SIZE_Y - rectH) / 2;


        // The directions are quite simple, if it is classic (NORTH, ...) then put it in the middle and make equal space on both sides.
        // If it is combined (NORTH-EAST, ...) then the x coordinate is the same as for east and the y same as for north.
        switch (direction) {
            case NORTH:
                x = w / 2 - ARROW_SIZE_Y / 2;                      // In the middle.

                y = rectangleTriangleDistanceY;     // So the arrow touches the top.
                y += BORDER_ARROW_HALF_SPACE_Y;     // So the space is equal and both sides.
                break;


            case NORTH_EAST:
                x = w - SCROLL_BORDER_SIZE_X;
                x += BORDER_ARROW_HALF_SPACE_X;

                y = rectangleTriangleDistanceY;
                y += BORDER_ARROW_HALF_SPACE_Y;
                break;


            case EAST:
                x = w - SCROLL_BORDER_SIZE_X;
                x += BORDER_ARROW_HALF_SPACE_X;

                y = h / 2 - rectH / 2;
                break;


            case SOUTH_EAST:
                x = w - SCROLL_BORDER_SIZE_X;
                x += BORDER_ARROW_HALF_SPACE_X;

                y = h - SCROLL_BORDER_SIZE_Y + rectangleTriangleDistanceY;
                y += BORDER_ARROW_HALF_SPACE_Y;
                break;


            case SOUTH:
                x = w / 2 - ARROW_SIZE_Y / 2;

                y = h - SCROLL_BORDER_SIZE_Y + rectangleTriangleDistanceY;
                y += BORDER_ARROW_HALF_SPACE_Y;
                break;


            case SOUTH_WEST:
                x = BORDER_ARROW_HALF_SPACE_X;

                y = h - SCROLL_BORDER_SIZE_Y + rectangleTriangleDistanceY;
                y += BORDER_ARROW_HALF_SPACE_Y;
                break;


            case WEST:
                x = BORDER_ARROW_HALF_SPACE_X;
                y = h / 2 - rectH / 2;
                break;


            case NORTH_WEST:
                x = BORDER_ARROW_HALF_SPACE_X;

                y = rectangleTriangleDistanceY;
                y += BORDER_ARROW_HALF_SPACE_Y;
                break;
// TODO: Zalogovat asi nebo nevim
            default:
                MyLogger.logWithoutIndentation("Unknown enum of type ArrowDirection in switch in method drawEdgeArrow");
                System.exit(0);
        }

        drawArrow(g, x, y, rectW, rectH, triangleW, rectangleTriangleDistanceY, c, direction);
    }


    /**
     * Draws arrow starting at x,y (the top left coordinates of the rectangle).
     * @param g is the graphics to draw the arrow to.
     * @param x is the top left x coordinate of the rectangle in arrow.
     * @param y is the top left y coordinate of the rectangle in arrow.
     * @param rectW is the width of the rectangle in arrow.
     * @param rectH is the height of the rectangle in arrow.
     * @param triangleW is the width of the triangle in arrow.
     * @param rectangleTriangleDistanceY is the vertical distance between the top right vertex of rectangle and top left vertex of triangle.
     * @param c is the color of the arrow.
     * @param direction is the direction of the arrow.
     */
    private void drawArrow(Graphics g, int x, int y, int rectW, int rectH, int triangleW, int rectangleTriangleDistanceY, Color c, ArrowDirection direction) {
        calculateArrowPolygon(x, y, rectW, rectH, triangleW, rectangleTriangleDistanceY);

        direction.transformArrowPointingRight(arrowPointingRight, arrow.arrowPoints);
        arrow.setArrowPolygon();
        g.setColor(c);
        g.drawPolygon(arrow.arrowPolygon);
    }

    private void calculateArrowPolygon(int x, int y, int rectW, int rectH, int triangleW, int rectangleTriangleDistanceY) {
        int xLoc = x;
        int yLoc = y;

        setArrowPointingRight(0, xLoc, yLoc);       // Top left rectangle vertex

        xLoc += rectW;
        setArrowPointingRight(1, xLoc, yLoc);     // Top right rectangle vertex

        yLoc -= rectangleTriangleDistanceY;
        setArrowPointingRight(2, xLoc, yLoc);     // Top left triangle vertex

        xLoc = x + rectW + triangleW;
        yLoc = y + rectH / 2;
        setArrowPointingRight(3, xLoc, yLoc);     // Mid right triangle vertex

        xLoc -= triangleW;
        yLoc = y + rectH + rectangleTriangleDistanceY;
        setArrowPointingRight(4, xLoc, yLoc);     // Bot left triangle vertex

        yLoc -= rectangleTriangleDistanceY;
        setArrowPointingRight(5, xLoc, yLoc);     // Bot right rectangle vertex

        xLoc -= rectW;
        setArrowPointingRight(6, xLoc, yLoc);     // Bot left rectangle vertex
    }


    public void moveToPosBasedOnRelativeToRefPanel(MovablePanelSpecificMethodsIFace p) {
        Point relativePos = p.getRelativePosToReferencePanel();
        int x = getRealLocationBasedOnRelativePosToReferencePanel(relativePos.x, referencePanel.getLeftX(), panelSizeWithBorderWidth);
        int y = getRealLocationBasedOnRelativePosToReferencePanel(relativePos.y, referencePanel.getTopY(), panelSizeWithBorderHeight);
        p.setLocation(x, y);        // Setting without creating new Point is faster
    }

    private Point getRealLocationBasedOnRelativePosToReferencePanel(Point relativePos) {
        int x = getRealLocationBasedOnRelativePosToReferencePanel(relativePos.x, referencePanel.getLeftX(), panelSizeWithBorderWidth);
        int y = getRealLocationBasedOnRelativePosToReferencePanel(relativePos.y, referencePanel.getTopY(), panelSizeWithBorderHeight);

        return new Point(x, y);
    }

    private int getRealLocationBasedOnRelativePosToReferencePanel(int relativePos, int startLoc, int skipSize) {
        return startLoc + relativePos * skipSize;
    }

    private Point getRealLocationBasedOnRelativePosToReferencePanel(Point relativePos, Point startLoc, Point skipSize) {
        int x = getRealLocationBasedOnRelativePosToReferencePanel(relativePos.x, startLoc.x, skipSize.x);
        int y = getRealLocationBasedOnRelativePosToReferencePanel(relativePos.y, startLoc.y, skipSize.y);
        Point p = new Point(x, y);
        return p;
    }

    private Point getLocationOfRelativePos(MovablePanelSpecificMethodsIFace panel, Point panelSizeWithBorders) {
        Point relative = panel.getRelativePosToReferencePanel();
        return getRealLocationBasedOnRelativePosToReferencePanel(relative, referencePanel.getLocation(), panelSizeWithBorders);
    }


    public void panelMovementStarted(MovableJPanel p) {
        setLayer(p, JLayeredPane.DRAG_LAYER);
        setCurrentlyMovingPanel(p);

    }
    public void panelMovementEnded() {
        setLayer(currentlyMovingPanel, JLayeredPane.DEFAULT_LAYER);
        setCurrentlyMovingPanel(null);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        // Empty
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (getIsAnyPanelCurrentlyConnecting()) {
            currentlyConnectingPanel.noConnectionCallback();
        }
        else if (getIsAnyPanelCurrentlyMoving()) { // Solves special case when clicking on the edge of the static panel
            // while moving panel, the clicked is registered here and not on the moving panel
            currentlyMovingPanel.endedDraggingFromOutsideMovablePanel();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        stoppedDragging();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Empty
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Empty
    }


    public void remove(MovableJPanel mp) {
        // First remove the connections
        mp.removeInputs();
        removeInputPortLabels(mp);
        mp.removeOutputs();

        // Remove from array and component
        Object unit = mp.getUnit();
        panels.remove(unit);
        super.remove(mp);
        this.repaint();
    }

    private void clearPanels() {
        while(panels.size() > 0) {
            remove(panels.get(0).getShapedPanel());
        }
    }

    public void clearPanelsExceptOutputs() {
        int index = 0;
        while(index < panels.size()) {
            Unit u = panels.get(index);
            if(u instanceof OutputUnit) {
                index++;
            }
            else {
                remove(u.getShapedPanel());
            }
        }
    }

    /**
     * Removes all the input cables - when deleted panel is input to some other component
     * @param mp is the deleted panel
     */
    @Deprecated
    public void removeFromAllInputs(MovablePanelIFace mp) {
        OutputPort outputPort = mp.getOutputPort();
        List<Port> deletedPanelOutputs = mp.getOutputPort().getConnectedPorts();
        for (Port port : deletedPanelOutputs) {
            port.removePort(outputPort);
        }
    }


    /**
     * Removes all the output cables - when some panel has in output the deleted panel
     * @param mp is the deleted panel
     */
    @Deprecated
    public void removeFromAllOutputs(MovablePanelIFace mp) {
        InputPort[] inputPorts = mp.getInputPorts();
        for (Unit u : panels) {
            MovablePanelIFace p = u.getShapedPanel();
            for(InputPort inputPort : inputPorts) {
                p.getOutputPort().removePort(inputPort);
            }
        }
    }


    @Override
    public void tryAdd(Unit unit) {
        addPanelTemporary(unit.getClass());
    }

    public void addPanelTemporary(Class<?> unitClass) {
        if(!getIsAnyPanelCurrentlyMoving()) {
            Unit u = createUnit(unitClass);
            ShapedPanel sp = u.getShapedPanel();

            if(sp != null) {
                addPanelTemporary(u);
                sp.startedAddingUnit();
            }
        }
    }





    /**
     * Used when creating from menu (JTree) - doesn't copy the relative location
     * @param unitClass is the type of unit which should be created
     * @return
     */
    public Unit createUnit(Class<?> unitClass) {
        Unit u = null;
        try {
// Using reflection - so it is a bit slower but much simpler and requires less work - else I would have to have
// enum or something and based on that create instance in switch and that would be quite complicated to use with plugin operators
// I would have to create the enum dynamically at launch of application - would read some string from the plugins and create enum based on that
// And also this is called only when adding, so the performance hit is pretty small
            Constructor<?> ctor = unitClass.getConstructor(DiagramPanel.class);
            u = (Unit) ctor.newInstance(new Object[] { this });
            ShapedPanel sp = u.getShapedPanel();
            sp.updateSize(new Dimension(this.getReferencePanelWidth(), this.getReferencePanelHeight()));
        }
        catch(Exception e) {
            MyLogger.logException(e);
            return null;
        }

        return u;
    }


    // Copy paste but using different constructor for shaped panel (this method is used when copying)

    /**
     * Used when copying - does copy the relative location
     * @param templateUnit
     * @return
     */
    public Unit createUnit(Unit templateUnit) {
        Class<?> templateUnitClass = templateUnit.getClass();
        Unit createdUnit = null;
        try {
// Using reflection - so it is a bit slower but much simpler and requires less work - else I would have to have
// enum or something and based on that create instance in switch and that would be quite complicated to use with plugin operators
// I would have to create the enum dynamically at launch of application - would read some string from the plugins and create enum based on that
// And also this is called only when adding, so the performance hit is pretty small
            Constructor<?> ctor = templateUnitClass.getConstructor(Unit.class);
            createdUnit = (Unit) ctor.newInstance(new Object[] { templateUnit });
            ShapedPanel sp = createdUnit.getShapedPanel();
            sp.updateSize(new Dimension(this.getReferencePanelWidth(), this.getReferencePanelHeight()));
        }
        catch(Exception e) {
            MyLogger.logException(e);
            return null;
        }

        return createdUnit;
    }


    /**
     * Called when adding new unit to class. Adds new unit to the class. But the unit doesn't have placement, it is currently
     * being moved by user who is finding where to place panel. Later he may place it to incorrect position, then it is removed.
     * Or when the placement is valid then it is added to this panel permanently
     * @param u is the unit to be added
     */
    public void addPanelTemporary(Unit u) {
        panels.add(u);
        this.add(u.getShapedPanel(), JLayeredPane.DEFAULT_LAYER);
    }


    public void makeTemporaryPanelPermanent(MovableJPanel mp) {
        mp.noteAdditionToDiagram();
    }

    public void addPanelPermanently(Unit u) {
        addPanelTemporary(u);
        makeTemporaryPanelPermanent(u.getShapedPanel());
    }

    public void incorrectTemporaryPanelPosition(MovableJPanel mp) {
        mp.removeInputs();
        mp.removeOutputs();
        this.remove(mp);
        for(InputPort ip : mp.getInputPorts()) {
            this.remove(ip.getPortLabel());
        }
        panels.remove(mp);
    }



    //  Deprecated since I started using symmetrical connection
    @Deprecated
    public void mapThisOutputPortToInputPorts(OutputPort outputPort) {
        List<Port> portsToConnectTo = outputPort.getConnectedPorts();
        for (Port inputPort : portsToConnectTo) {
            inputPort.connectToPort(outputPort);
        }
    }

    @Deprecated
    public void mapThisInputPortToOutputPort(InputPort inputPort) {
        inputPort.getConnectedPort().connectToPort(inputPort);
    }





    private Point oldDragPos = null;
    private void stoppedDragging() {
        oldDragPos = null;
    }
    private boolean isDragging() {
        return oldDragPos != null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point currPos = e.getPoint();
        if(oldDragPos == null) {
            oldDragPos = currPos;
        }
        else {
            int difX = currPos.x - oldDragPos.x;
            int difY = currPos.y - oldDragPos.y;
            moveLeft(difX);
            moveUp(difY);

            oldDragPos = currPos;
            this.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // EMPTY
    }



    @Override
    public void addInputPortLabel(JLabel inputPortLabel) {
        this.add(inputPortLabel, JLayeredPane.DRAG_LAYER);
    }

    private void removeInputPortLabel(JLabel inputPortLabel) {
        this.remove(inputPortLabel);
    }

    private void removeInputPortLabel(InputPort ip) {
        removeInputPortLabel(ip.getPortLabel());
    }

    private void removeInputPortLabels(PortsGetterIFace panel) {
        for(InputPort ip : panel.getInputPorts()) {
            removeInputPortLabel(ip);
        }
    }



    public int getIndexInPanelList(Object o) {
        return panels.indexOf(o);
    }


    /**
     * This will get updated as the saving algorithms will change, this is here just for backwards compatibility, since
     * when I will add changing of default values of units to properties, I will need to save that to the .dia file and
     * therefore it won't be possible to load old files in the new version.
     */
    public static final String CURRENT_SAVE_VERSION = "1.0";

    @Override
    public void save(PrintWriter output) {
        output.println(CURRENT_SAVE_VERSION);
        output.println(channelCount.CHANNEL_COUNT);
        output.println(panels.size());

        for(Unit u : panels) {
            u.save(output);
        }
        for(Unit u : panels) {
            u.getOutputPort().save(output);
        }
    }

    @Override
    public void load(BufferedReader input) {
        stopAudioUsingClick();
        activeWaitingUntilPaused();
        clearPanels();
        try {
            String line;
            line = input.readLine();        // Reads version, for now just skip it, this is for future proofing
            line = input.readLine();
            int channelCount = Integer.parseInt(line);
            AudioFormatWithSign af = audioThread.getOutputFormat();
            setOutputAudioFormat(new AudioFormatWithSign(af.getSampleRate(), af.getSampleSizeInBits(),
                    channelCount, af.isSigned, af.isBigEndian()));

            line = input.readLine();
            int panelsLen = Integer.parseInt(line);
            for(int i = 0; i < panelsLen; i++) {
                line = input.readLine();
                if("OUTPUT-UNIT".equals(line)) {
                    line = input.readLine();
                    int channel = Integer.parseInt(line);
                    input.readLine();       // Skip the output panel java name
                    OutputUnit outputUnit = outputPanels[channel];
                    outputUnit.load(input);
                    moveToPosBasedOnRelativeToRefPanel(outputUnit.getShapedPanel());
                    panels.repairUnitPosition(outputUnit);
                    Dimension size = new Dimension(getReferencePanelWidth(), getReferencePanelHeight());
                    outputUnit.getShapedPanel().updateSize(size);
                }
                else {
                    Class<?> clazz = Class.forName(line);
                    Unit u = createUnit(clazz);
                    u.load(input);
                    moveToPosBasedOnRelativeToRefPanel(u.getShapedPanel());
                    this.addPanelPermanently(u);
                }
            }
            for(int i = 0; i < panelsLen; i++) {
                panels.get(i).getOutputPort().load(input);
            }
        }
        catch(IOException e) {
            MyLogger.logException(e);
        }
        catch (ClassNotFoundException e) {
            MyLogger.logException(e);
        }

        recalculateAllCables();
        this.repaint();
    }


    public void panelLocationChanged(MovableJPanel panel) {
        Unit u = panels.get(panel.getIndexInPanelList());
        panels.repairUnitPosition(u);
    }


    public void zoomToPanel(int index) {
        Unit panel = panels.get(index);
        zoomToPanel(panel);
    }

    public void zoomToPanel(Unit u) {
        Point loc = u.getShapedPanel().getLocation();
        Dimension size = this.getSize();
        moveLeft(-loc.x + size.width / 2 - getReferencePanelWidth() / 2);
        moveUp(-loc.y + size.height / 2 - 2 * getReferencePanelHeight());
        this.repaint();
    }
}