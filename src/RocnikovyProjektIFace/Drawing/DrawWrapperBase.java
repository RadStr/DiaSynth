package RocnikovyProjektIFace.Drawing;

import RocnikovyProjektIFace.AudioWavePanelReferenceValues;
import RocnikovyProjektIFace.AudioWavePanelReferenceValuesWithHeightCallback;

import javax.swing.*;
import java.awt.*;

public abstract class DrawWrapperBase extends JPanel {
    public DrawWrapperBase(DrawPanel drawPanel, double minValue, double maxValue) {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 1;
        c.gridheight = 1;

        // I have to override the preferred size here because the height == 0 and for that reason it isn't drawn.
        // Which is kind of interesting, since for the audio player it works correctly, even when the height is 0.
        outputReferenceValues = new AudioWavePanelReferenceValuesWithHeightCallback(minValue, maxValue,
                () -> drawPanel.getPreferredSize().height);

        add(outputReferenceValues, c);

        c.gridx = 1;
        c.weightx = 1;
        this.drawPanel = drawPanel;
        add(drawPanel, c);
        // TODO: VYMAZAT
        //outputReferenceValues.setPreferredSize(new Dimension(20, 150));
        //outputReferenceValues.setPreferredSize(new Dimension(getPreferredSize().width, drawnFunctionPanel.getPreferredSize().height));
        // TODO: VYMAZAT

        minSize.width = outputReferenceValues.getPreferredWidth() + drawPanel.getMinimumSize().width;
        minSize.height = drawPanel.getMinimumSize().height;
    }

    protected final AudioWavePanelReferenceValuesWithHeightCallback outputReferenceValues;
    protected final DrawPanel drawPanel;


    private Dimension minSize = new Dimension();
    @Override
    public Dimension getMinimumSize() {
        return minSize;
    }
}
