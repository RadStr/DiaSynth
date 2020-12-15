package player.experimental;

import player.wave.VerticalReferencesPanel;
import player.wave.VerticalReferencesPanelWithHeightCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class DrawWrapperBase extends JPanel implements DrawWrapperIFace {
    public DrawWrapperBase(DrawPanel drawPanel, double minValue, double maxValue) {
        this.setLayout(new GridBagLayout());
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;

        // I have to override the preferred size here because the height == 0 and for that reason it isn't drawn.
        // Which is kind of interesting, since for the audio player it works correctly, even when the height is 0.
        outputReferenceValues = new VerticalReferencesPanelWithHeightCallback(minValue, maxValue,
                () -> this.drawPanel.getPreferredSize().height);

        add(outputReferenceValues, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        setDrawPanel(drawPanel);
    }


    private final GridBagConstraints constraints;
    protected final VerticalReferencesPanelWithHeightCallback outputReferenceValues;
    protected DrawPanel drawPanel;
    public void setDrawPanel(DrawPanel drawPanel) {
        if(this.drawPanel != null) {
            remove(this.drawPanel);
        }
        this.drawPanel = drawPanel;
        add(drawPanel, constraints);
        // TODO: VYMAZAT
        //outputReferenceValues.setPreferredSize(new Dimension(20, 150));
        //outputReferenceValues.setPreferredSize(new Dimension(getPreferredSize().width, drawnFunctionPanel.getPreferredSize().height));
        // TODO: VYMAZAT

        if(drawPanel instanceof FFTWindowPartPanel) {
            drawPanel.setDrawValuesStrings();
            drawPanel.setLastPartOfTooltip();
        }

        minSize.width = outputReferenceValues.getPreferredWidth() + drawPanel.getMinimumSize().width;
        minSize.height = drawPanel.getMinimumSize().height;

        revalidate();
        repaint();
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if(topFrame != null) {
            topFrame.setMinimumSize(null);
            topFrame.pack();
            topFrame.revalidate();
            topFrame.repaint();
        }
    }


    private Dimension minSize = new Dimension();
    @Override
    public Dimension getMinimumSize() {
        return minSize;
    }


    public void addReset(JMenuBar menuBar) {
        JMenuItem resetMenuItem = new JMenuItem("Reset");
        resetMenuItem.setToolTipText("Resets the draw values to neutral value");

        menuBar.add(resetMenuItem);
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.resetValues();
            }
        });
    }

    public void addReset(JMenu menu) {
        JMenuItem resetMenuItem = new JMenuItem("Reset");
        resetMenuItem.setToolTipText("Resets the draw values to neutral value");

        menu.add(resetMenuItem);
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.resetValues();
            }
        });
    }

    @Override
    public abstract void addMenus(JMenuBar menuBar, AddWaveIFace waveAdder);

    public static int calculateMaxSizeBinCount(double minValue, double maxValue) {
        int referenceValuesWidth = new VerticalReferencesPanel(minValue, maxValue).getPreferredSize().width;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int binCount = screenSize.width - referenceValuesWidth;
        return binCount;
    }
}
