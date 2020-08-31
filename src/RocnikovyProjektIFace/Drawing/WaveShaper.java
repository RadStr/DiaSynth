package RocnikovyProjektIFace.Drawing;

import RocnikovyProjektIFace.AudioWavePanelReferenceValues;
import RocnikovyProjektIFace.AudioWavePanelReferenceValuesWithHeightCallback;
import Rocnikovy_Projekt.ProgramTest;

import javax.swing.*;
import java.awt.*;


public class WaveShaper extends DrawWrapperClass {
    public WaveShaper(int windowSize, Color backgroundColor, double minValue, double maxValue) {
        super(new FunctionWaveDrawPanel(windowSize, true, backgroundColor), minValue, maxValue);
        this.drawnFunctionPanel = drawnFunctionPanel;
    }

    private FunctionWaveDrawPanel drawnFunctionPanel;

    @Override
    public double[] getOutputValues() {
        return drawnFunctionPanel.getDrawnWave();
    }
}


//public class WaveShaper extends JPanel {
//    public WaveShaper(Color backgroundColor) {
//        this.setLayout(new GridBagLayout());
//        GridBagConstraints c = new GridBagConstraints();
//        c.gridx = 0;
//        c.gridy = 0;
//        c.weightx = 1;
//        c.weighty = 1;
//        c.gridwidth = 1;
//        c.gridheight = 1;
//
//
//        //drawnFunctionPanel = new FunctionWaveDrawPanel(getWidth());
//        drawnFunctionPanel = new FunctionWaveDrawPanel(200, true, backgroundColor);
//        //outputReferenceValues = new AudioWavePanelReferenceValues();
//        // I have to override the preferred size here because the height == 0 and for that reason it isn't drawn.
//        // Which is kind of interesting, since for the audio player it works correctly, even when the height is 0.
//        outputReferenceValues = new AudioWavePanelReferenceValuesWithHeightCallback( -1, 1,
//                () -> drawnFunctionPanel.getPreferredSize().height);
//
//
//        add(outputReferenceValues, c);
//
//        c.gridx = 1;
//        c.weightx = 1;
//        add(drawnFunctionPanel, c);
//        // TODO: VYMAZAT
//        //outputReferenceValues.setPreferredSize(new Dimension(20, 150));
//        //outputReferenceValues.setPreferredSize(new Dimension(getPreferredSize().width, drawnFunctionPanel.getPreferredSize().height));
//        // TODO: VYMAZAT
//    }
//
//    private AudioWavePanelReferenceValues outputReferenceValues;
//    private FunctionWaveDrawPanel drawnFunctionPanel;
//
//    public double[] getOutputFunction() {
//        return drawnFunctionPanel.getDrawnWave();
//    }
//}