package RocnikovyProjektIFace.Drawing;

import RocnikovyProjektIFace.AudioWavePanelReferenceValues;

import javax.swing.*;
import java.awt.*;

public class WaveShaper extends JPanel {
    public WaveShaper() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 1;
        c.gridheight = 1;


        //drawnFunctionPanel = new FunctionWaveDrawPanel(getWidth());
        drawnFunctionPanel = new FunctionWaveDrawPanel(1024, true);
        outputReferenceValues = new AudioWavePanelReferenceValues();
        add(outputReferenceValues, c);

        c.gridx = 1;
        c.weightx = 1;
        add(drawnFunctionPanel, c);
        // TODO: VYMAZAT
        //outputReferenceValues.setPreferredSize(new Dimension(20, 150));
        //outputReferenceValues.setPreferredSize(new Dimension(getPreferredSize().width, drawnFunctionPanel.getPreferredSize().height));
        // TODO: VYMAZAT
    }

    private AudioWavePanelReferenceValues outputReferenceValues;
    private FunctionWaveDrawPanel drawnFunctionPanel;

    public double[] getOutputFunction() {
        return drawnFunctionPanel.getDrawnWave();
    }
}