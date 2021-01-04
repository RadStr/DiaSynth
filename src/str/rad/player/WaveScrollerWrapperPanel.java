package str.rad.player;

import str.rad.util.swing.EmptyPanelWithSetMethod;
import str.rad.player.wave.WavePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * A bit of hack class, I have empty panel connected to this and based on the scroll I scroll the moves.
 * I have to do it myself. Can't let java do it.
 */
public class WaveScrollerWrapperPanel extends JPanel {
    public WaveScrollerWrapperPanel(WaveScrollEventCallbackIFace waveScrollCallback) {
        super();
        waveScroller = new JScrollPane();
        constructorDefaultSet(waveScrollCallback);
    }

    public WaveScrollerWrapperPanel(int vsbPolicy, int hsbPolicy, WaveScrollEventCallbackIFace waveScrollCallback) {
        super();
        waveScroller = new JScrollPane(vsbPolicy, hsbPolicy);
        constructorDefaultSet(waveScrollCallback);
    }

    private void constructorDefaultSet(WaveScrollEventCallbackIFace waveScrollCallback) {
        oldScrollbarValue = 0;
        horizontalBarAdjustmentListener = new HorizontalBarAdjustmentListener(this);
        this.waveScrollCallback = waveScrollCallback;
        waveScroller.getHorizontalScrollBar().addAdjustmentListener(horizontalBarAdjustmentListener);

        waveScroller.getHorizontalScrollBar().setUnitIncrement(AudioPlayerPanel.HORIZONTAL_SCROLL_UNIT_INCREMENT);
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


    private boolean scrollReceivedResizeEvent = false;

    public boolean getScrollReceivedResizeEvent() {
        return scrollReceivedResizeEvent;
    }

    public void processScrollReceivedResizeEvent() {
        scrollReceivedResizeEvent = false;
    }

    private ComponentListener listener = new ComponentListener() {
        private int oldWidth = Integer.MIN_VALUE;
        private int oldScrollVal = Integer.MIN_VALUE;
        private boolean isFirst = true;

        @Override
        public void componentResized(ComponentEvent e) {
            scrollReceivedResizeEvent = true;
            int newWidth = e.getComponent().getWidth();
            JScrollBar scrollBar = waveScroller.getHorizontalScrollBar();
            int scrollBarVal = scrollBar.getValue();
            if (isFirst) {
                oldScrollVal = scrollBarVal;
                isFirst = false;
            }

            int visibleWidthOfWave = emptyPanelForHorizontalScroll.getVisibleRect().width;
            int newVal = WavePanel.getLeftPixelAfterZoom(oldWidth, newWidth, visibleWidthOfWave,
                                                         getOldScrollbarValue(), waveScrollCallback.getShouldZoomToMid(), waveScrollCallback.getShouldZoomToEnd());
            scrollBar.setValue(newVal);
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

    public JScrollPane getWaveScroller() {
        return waveScroller;
    }

    public void scrollToStart() {
        JScrollBar scrollBar = waveScroller.getHorizontalScrollBar();
        scrollBar.setValue(0);
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
    // Simulates the vertical scrollbar
    private final EmptyPanelWithSetMethod emptyPanelAfterHorizontalScroll = new EmptyPanelWithSetMethod();

    /**
     * Sets the width of the panel representing scrollbar, we set it separately,
     * since the width of vertical scrollbar shouldn't
     * change therefore it is set only once at start and then we call it again after some resizing, just in case.
     *
     * @param width
     */
    public void setLastEmptyPanelWidth(int width) {
        emptyPanelAfterHorizontalScroll.setSizeInternal(new Dimension(width, 0));
    }

    public Dimension getEmptyPanelSizeDebug() {
        return new Dimension(emptyPanelForHorizontalScroll.getSize());
    }

    public void resetEmptyPanelSize() {
        setEmptyPanelsSizes(0, 0, 0);
    }

    /**
     * Sets the sizes of the empty panels representing space before the wave and space of the wave. To set the size
     * of the last panel, which is representing the vertical scrollbar width you must call setLastEmptyPanelWidth separately.
     */
    public void setEmptyPanelsSizes(int widthBeforeWavePanel, int wavePanelWidth, int h) {
        Dimension oldVisibleSize = new Dimension(waveScroller.getViewport().getVisibleRect().width, waveScroller.getViewport().getVisibleRect().height);
        emptyPanelBeforeHorizontalScroll.setSizeInternal(new Dimension(widthBeforeWavePanel, h));
        emptyPanelForHorizontalScroll.setSizeInternal(new Dimension(wavePanelWidth, h));

        // I have to this because it just randomly resizes otherwise for no reason - probably java bug in layout manager
        // https://stackoverflow.com/questions/9632936/jscrollpanes-inside-gridbaglayout-gets-resized-randomly
        waveScroller.getViewport().setPreferredSize(oldVisibleSize);
        waveScroller.getViewport().setSize(oldVisibleSize);
    }

    public void updateWhenZooming() {
        ComponentEvent event = new ComponentEvent((Component) waveScrollCallback, ComponentEvent.COMPONENT_RESIZED);
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


    public class HorizontalBarAdjustmentListener implements AdjustmentListener {
        public HorizontalBarAdjustmentListener(WaveScrollerWrapperPanel waveScrollerWrapper) {
            this.waveScrollerWrapper = waveScrollerWrapper;
        }

        private WaveScrollerWrapperPanel waveScrollerWrapper;
        private int oldValue = 0;


        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            JScrollBar scrollBar = waveScrollerWrapper.getWaveScroller().getHorizontalScrollBar();
            int value = e.getValue();
            int extent = scrollBar.getModel().getExtent();
            int max = scrollBar.getMaximum() - extent;

            if (oldValue != value) {
                if (waveScrollerWrapper.waveScrollCallback.getCanZoom()) {
                    if (oldValue <= max) {
                        waveScrollerWrapper.waveScrollCallback.scrollChangeCallback(oldValue, value);
                    }
                }

                oldValue = value;
            }
        }
    }
}