package synthesizer.GUI.PanelAroundMovablePanelsPackage;

import synthesizer.GUI.MovablePanelsPackage.DiagramPanel;
import Rocnikovy_Projekt.MyLogger;

import javax.swing.*;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.*;

public class DiagramJSplitPane extends JSplitPane {
    private static final double THIS_SPLIT_DIVIDER_WEIGHT = 0.8;        // Parameter to play with

    public DiagramJSplitPane(SynthesizerMainPanelIFace synthesizerMainPanel, PlayedWaveVisualizer waveVisualizer) {
        super(HORIZONTAL_SPLIT);
        this.synthesizerMainPanel = synthesizerMainPanel;
        setResizeWeight(THIS_SPLIT_DIVIDER_WEIGHT);
        setOneTouchExpandable(true);
        MyLogger.log("Adding DiagramPanel", 1);
        leftPanel = new DiagramPanel(getBackground(), synthesizerMainPanel, waveVisualizer);
        this.setLeftComponent(leftPanel);
        MyLogger.log("Added DiagramPanel", -1);

        MyLogger.log("Adding menu", 1);
        // Set the menu
        DiagramItemsMenu menu = new DiagramItemsMenu(leftPanel);
        this.setRightComponent(menu);
        MyLogger.log("Adding menu", -1);

        addResizeListener();

        SplitPaneUI spui = this.getUI();
        JSplitPane thisPanel = this;
        if (spui instanceof BasicSplitPaneUI) {
            MouseAdapter splitterMouseAdapter = new MouseAdapter() {
                private boolean drag = false;
                @Override
                public void mouseReleased(MouseEvent e) {
                    if(drag) {
                        drag = false;
                        setWidthBetweenDivLocAndEnd(thisPanel.getDividerLocation(), thisPanel.getWidth());
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    drag = true;
                }
            };
            // Setting a mouse listener directly on split pane does not work, because no events are being received.
            ((BasicSplitPaneUI) spui).getDivider().addMouseMotionListener(splitterMouseAdapter);
            ((BasicSplitPaneUI) spui).getDivider().addMouseListener(splitterMouseAdapter);
        }
    }

    private void addResizeListener() {
        // Resize listener
        ComponentListener resizeListener = new ComponentAdapter(){
            private boolean isFirstCall = true;
            @Override
            public void componentResized(ComponentEvent e) {
                JSplitPane sp = (JSplitPane)e.getComponent();
                if(isFirstCall) {
                    isFirstCall = false;
                    sp.setDividerLocation(THIS_SPLIT_DIVIDER_WEIGHT);
                    int divLoc = sp.getDividerLocation();
                    leftPanel.resizeCallback(divLoc);
                    setWidthBetweenDivLocAndEnd(getDividerLocation(), getWidth());
                }
                else {
                    int w = sp.getWidth();
                    int newDivLoc = w - widthBetweenDivLocAndEnd;
                    int newMaxSplit = (int)(w * THIS_SPLIT_DIVIDER_WEIGHT);
                    if(newDivLoc < 0) {
                        newDivLoc = 0;
                        leftPanel.resizeCallback(newDivLoc);
                    }
                    else if(newMaxSplit > newDivLoc) {
                        leftPanel.resizeCallback(newDivLoc);
                    }
                    else {
                        leftPanel.resizeCallback(newMaxSplit);
                    }
                    setDividerLocation(newDivLoc);
                }

                System.out.println("Debug component listener bottom Panel:\t" + sp.getDividerLocation() + "\t" + sp.getDividerLocation() + "\t" + sp.getLeftComponent().getWidth() + "\t" + sp.getRightComponent().getWidth());
            }
        };
        this.addComponentListener(resizeListener);
        prefSize = new Dimension();
    }


    private SynthesizerMainPanelIFace synthesizerMainPanel;

    // Because java is doing java things, and when there are a lot of panel directories opened in the JTree (the menu with panels)
    // It resizes it up and breaks the layout of the top panels because they don't have enough space as result of that
    // So I have to set the preferred size manually so it takes up all the space except the preferred height of the buttons
    private Dimension prefSize;
    @Override
    public Dimension getPreferredSize() {
        int buttonsHeight = synthesizerMainPanel.getTopButtonsPreferredHeight();
        int mainPanelHeight = synthesizerMainPanel.getSize().height;
        Dimension superPrefSize = super.getPreferredSize();
        prefSize.width = superPrefSize.width;
        prefSize.height = mainPanelHeight - buttonsHeight;

        if(prefSize.height <= 0) {
            prefSize.height = superPrefSize.height;
        }
        return prefSize;
    }


    private DiagramPanel leftPanel;
    public DiagramPanel getDiagramPanel() {
        return leftPanel;
    }

    private int widthBetweenDivLocAndEnd;
    private void setWidthBetweenDivLocAndEnd(int divLoc, int w) {
        widthBetweenDivLocAndEnd = calculateWidthBetweenDivLocAndEnd(divLoc, w);
    }
    public static int calculateWidthBetweenDivLocAndEnd(int divLoc, int w) {
        return w - divLoc;
    }
}