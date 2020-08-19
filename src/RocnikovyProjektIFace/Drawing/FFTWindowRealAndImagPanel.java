package RocnikovyProjektIFace.Drawing;

import javax.swing.*;
import java.awt.*;

public class FFTWindowRealAndImagPanel extends JPanel {
    public FFTWindowRealAndImagPanel(double[] song, int windowSize, int startIndex, int sampleRate, int numberOfChannels) {
        realPartPanel = new FFTWindowPartPanel(this, song, windowSize, startIndex, sampleRate, numberOfChannels);
        imagPartPanel = new FFTWindowPartPanel(this, song, windowSize, startIndex, sampleRate, numberOfChannels);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 1;

        add(realPartPanel, c);

        c.gridy = 1;
        add(imagPartPanel, c);
    }

    private FFTWindowPartPanel realPartPanel;
    private FFTWindowPartPanel imagPartPanel;


    public void setMeasures(FFTWindowPartPanel partPanel, int bin, double newValue) {
        FFTWindowPanel otherPartPanel;
        if(partPanel == imagPartPanel) {
            otherPartPanel = realPartPanel;
        }
        else {
            otherPartPanel = imagPartPanel;
        }

        double squareValue = newValue * newValue;
        double otherPanelValue = otherPartPanel.getDrawValue(bin);
        double otherPanelValueSquare = otherPanelValue * otherPanelValue;

        double squaresSum = otherPanelValueSquare + squareValue;
        if(squaresSum > 1) {
            double newOtherPanelValue = Math.sqrt(squaresSum - squareValue);
            otherPartPanel.setDrawValue(bin, newOtherPanelValue);
            otherPartPanel.repaint();
        }
    }
}
