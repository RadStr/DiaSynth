package RocnikovyProjektIFace;

import RocnikovyProjektIFace.SpecialSwingClasses.EmptyPanelWithSetMethod;
import Rocnikovy_Projekt.ProgramTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * A bit of hack class, I have empty panel connected to this and based on the scroll I scroll the moves. I have to do it myself
 * Can't let java do it.
 */
public class WaveScroller extends JPanel {
    public WaveScroller(WaveScrollEventCallbackIFace waveScrollCallback) {
        super();
        waveScroller = new JScrollPane();
        constructorDefaultSet(waveScrollCallback);
    }

    public WaveScroller(int vsbPolicy, int hsbPolicy, WaveScrollEventCallbackIFace waveScrollCallback) {
        super();
        waveScroller = new JScrollPane(vsbPolicy, hsbPolicy);
        constructorDefaultSet(waveScrollCallback);
    }

    private void constructorDefaultSet(WaveScrollEventCallbackIFace waveScrollCallback) {
        oldScrollbarValue = 0;
        horizontalBarAdjustmentListener = new HorizontalBarAdjustmentListener(this);
        this.waveScrollCallback = waveScrollCallback;
        waveScroller.getHorizontalScrollBar().addAdjustmentListener(horizontalBarAdjustmentListener);

        waveScroller.getHorizontalScrollBar().setUnitIncrement(AudioPlayerPanelIFaceImplementation.HORIZONTAL_SCROLL_UNIT_INCREMENT);
        waveScroller.setViewportView(emptyPanelForHorizontalScroll);
        emptyPanelForHorizontalScroll.addComponentListener(listener);


        waveScroller.getHorizontalScrollBar().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // EMPTY
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isScrollbarBeingUsed = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isScrollbarBeingUsed = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // EMPTY
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // EMPTY
            }
        });


        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(emptyPanelBeforeHorizontalScroll);
        this.add(waveScroller);
        this.add(emptyPanelAfterHorizontalScroll);
    }


    private ComponentListener listener = new ComponentListener() {
        private int oldWidth = Integer.MIN_VALUE;
        private int oldScrollVal = Integer.MIN_VALUE;
        private boolean isFirst = true;

        @Override
        public void componentResized(ComponentEvent e) {
//                waveScroller.getHorizontalScrollBar().setValue(waveScroller.getHorizontalScrollBar().getMaximum());
            int newWidth = e.getComponent().getWidth();
            JScrollBar scrollBar = waveScroller.getHorizontalScrollBar();
            int scrollBarVal = scrollBar.getValue();
            if(isFirst) {
                oldScrollVal = scrollBarVal;
                isFirst = false;
            }


            int visibleWidthOfWave = emptyPanelForHorizontalScroll.getVisibleRect().width;
            // TODO: DEBUG:
//                int extent = scrollBar.getModel().getExtent();
//                ProgramTest.debugPrint("RESIZING SCROLLBAR", oldWidth, newWidth, getOldScrollbarValue(),
//                    scrollBarVal, scrollBar.getMaximum() - extent);
//                ProgramTest.debugPrint(((AudioPlayerPanelIFaceImplementation)waveScrollCallback).waves.get(0).getPreferredSize());
            // TODO: DEBUG:

            int newVal = AudioWavePanelOnlyWave.getLeftPixelAfterZoom(oldWidth, newWidth, visibleWidthOfWave,
                getOldScrollbarValue(), waveScrollCallback.getShouldZoomToMid(), waveScrollCallback.getShouldZoomToEnd());
            horizontalBarAdjustmentListener.setShouldNotifyWaves(false);
            scrollBar.setValue(newVal);
            horizontalBarAdjustmentListener.setShouldNotifyWaves(true);
            //setOldScrollbarValue(newVal);

            ProgramTest.debugPrint("sadKEK", newVal, oldWidth, newWidth);
            oldWidth = newWidth;

            waveScrollCallback.enableZooming();
            waveScrollCallback.revalidateTimestamps();
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
    };

    // This is so I can't zoom and scroll at the same time - there would be inconsistency
    private boolean isScrollbarBeingUsed = false;
    public boolean getIsScrollbarBeingUsed() {
        return isScrollbarBeingUsed;
    }
    public void setIsScrollbarBeingUsed(boolean val) {
        isScrollbarBeingUsed = val;
    }

    // This is the value before zoom
    private int oldScrollbarValue;
    public int getOldScrollbarValue() {
        return oldScrollbarValue;
    }
    public void setOldScrollbarValue(int val) {
        oldScrollbarValue = val;
    }


    private final JScrollPane waveScroller;
    public JScrollPane getTheRealWaveScroller() {
        return waveScroller;
    }

    public int getCurrentHorizontalScroll() {
        return waveScroller.getHorizontalScrollBar().getValue();
    }
    public int getMaxHorizontalScroll() {
        JScrollBar scrollBar = waveScroller.getHorizontalScrollBar();
        int max = scrollBar.getMaximum();
        max -= scrollBar.getModel().getExtent();
        return max;
    }

    private final EmptyPanelWithSetMethod emptyPanelBeforeHorizontalScroll = new EmptyPanelWithSetMethod(0, 0);
    private final EmptyPanelWithSetMethod emptyPanelForHorizontalScroll = new EmptyPanelWithSetMethod(0, 0);
    private final EmptyPanelWithSetMethod emptyPanelAfterHorizontalScroll = new EmptyPanelWithSetMethod();        // Simulates the scrollbar
    public void setEmptyPanelAfterHorizontalScroll(int width) {
        emptyPanelAfterHorizontalScroll.setSizeInternal(new Dimension(width, 0));
    }

    public Dimension getEmptyPanelSizeDebug() {
        return new Dimension(emptyPanelForHorizontalScroll.getSize());
    }
    public void resetEmptyPanelSize() {
        setEmptyPanelSizes(0, 0, 0);
    }
    public void setEmptyPanelSizes(int leftPanelWidth, int rightPanelWidth, int h) {
        Dimension oldVisibleSize = new Dimension(waveScroller.getViewport().getVisibleRect().width, waveScroller.getViewport().getVisibleRect().height);
        emptyPanelBeforeHorizontalScroll.setSizeInternal(new Dimension(leftPanelWidth, h));
        emptyPanelForHorizontalScroll.setSizeInternal(new Dimension(rightPanelWidth, h));
        ProgramTest.debugPrint("Empty panel size inside wave scroller:", emptyPanelBeforeHorizontalScroll.getSize(),
            emptyPanelForHorizontalScroll.getSize(), emptyPanelAfterHorizontalScroll.getSize(), waveScroller.getHorizontalScrollBar().getSize(),
            waveScroller.getViewport().getSize(), waveScroller.getViewport().getViewRect());

// TODO: JAVA :)
        // I have to this because it just randomly resizes otherwise for no reason - probably java bug in layout manager
        // https://stackoverflow.com/questions/9632936/jscrollpanes-inside-gridbaglayout-gets-resized-randomly
        waveScroller.getViewport().setPreferredSize(oldVisibleSize);
        waveScroller.getViewport().setSize(oldVisibleSize);
// TODO: JAVA :)
    }

    public void updateWhenZooming() {
        ComponentEvent event = new ComponentEvent((Component)waveScrollCallback, ComponentEvent.COMPONENT_RESIZED);
        listener.componentResized(event);
    }

    public void revalidateEmptyPanel() {
        emptyPanelForHorizontalScroll.revalidate();
    }
    public void repaintEmptyPanel() {
        emptyPanelForHorizontalScroll.repaint();
    }


    private WaveScrollEventCallbackIFace waveScrollCallback;
    private HorizontalBarAdjustmentListener horizontalBarAdjustmentListener;

// TODO: ASI VYMAZAT
//    private boolean isResizeEvent = false;
//    public void setIsResizeEvent(boolean val) {
//        isResizeEvent = val;
//    }




    public class HorizontalBarAdjustmentListener implements AdjustmentListener {
        public HorizontalBarAdjustmentListener(WaveScroller scrollPane) {
            this.scrollPane = scrollPane;
        }

        private WaveScroller scrollPane;
        private int oldValue = 0;
        private boolean shouldNotifyWaves = true;

        public void setShouldNotifyWaves(boolean val) {
            shouldNotifyWaves = val;
        }


        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            JScrollBar scrollBar = scrollPane.getTheRealWaveScroller().getHorizontalScrollBar();
//            if(!e.getValueIsAdjusting()) {
            int value = e.getValue();
            int extent = scrollBar.getModel().getExtent();
            int max = scrollBar.getMaximum() - extent;

// TODO: DEBUG            System.out.println("Value:\t" + value);
            if (oldValue != value) {
                if (scrollPane.waveScrollCallback.getCanZoom()) {
                    if (oldValue <= max) {       // TODO: UNZOOM
                        scrollPane.waveScrollCallback.scrollChangeCallback(oldValue, value);
                    }                           // TODO: UNZOOM
// TODO: DEBUG
//                    else {
//                        System.out.println("NOTIFY ELSE");
//                    }
// TODO: DEBUG
                }
// TODO: DEBUG
//                else {
//                    System.out.println("ELSE");
//                }
// TODO: DEBUG

                if (oldValue <= max) {
// TODO: DEBUG
//                    int extent = scrollBar.getModel().getExtent();
//                    ProgramTest.debugPrint("SCROLLBAR ADJUSTMENT", oldValue, value, scrollBar.getMaximum() - extent);
// TODO: DEBUG
                    //scrollPane.setOldScrollbarValue(value);
                }

                oldValue = value;
            }
//            }
        }
    }
}
