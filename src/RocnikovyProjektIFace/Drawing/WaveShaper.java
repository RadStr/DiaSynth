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


        TODO: tady bych mel mit co nevjic to jde, resp. v te WaveShaper to bude pres celou obrazovku a ta function Draw vezme vsechno co nevezmou ty reference
        //drawnFunctionPanel = new FunctionWaveDrawPanel(getWidth());
        drawnFunctionPanel = new FunctionWaveDrawPanel(200);
        outputReferenceValues = new AudioWavePanelReferenceValues();
        add(outputReferenceValues, c);

        c.gridx = 1;
        c.weightx = 1;
        add(drawnFunctionPanel, c);
        // TODO: VYMAZAT
        //outputReferenceValues.setPreferredSize(new Dimension(20, 150));

        Nevim Proc je tohle potreba, melo by to snad jit i bez toho - layout by mel spravne nastavit tu vysku takze nevim co se deje
        outputReferenceValues.setPreferredSize(new Dimension(getPreferredSize().width, drawnFunctionPanel.getPreferredSize().height));
        // TODO: VYMAZAT
    }

    private AudioWavePanelReferenceValues outputReferenceValues;
    private FunctionWaveDrawPanel drawnFunctionPanel;

    public double[] getOutputFunction() {
        return drawnFunctionPanel.getDrawnWave();
    }
}

    Uz to je skoro hotovy, uz jen musim udelat ze to nejde roztahnout nebo tak neco
        pridat tam ty reference
        a asi udelat ze labelu muze byt i min nez je pocet tech binu





        ---------


        Asi budu muset udelat tridu od ktery bude tahle dedit a ta bude mit jen getDrawnValues a nebude mit cas - tu pouziju na vykreslovani grafu pro waveshaper


        Muzu mit 2 FFT windows na realnou a imaginarni slozku

// TODO: AAA - setFrequencyToolTip


        Pozor na + a - u tech FFT to nevadi ale u tech draw values to vadi tam je bezne - cislo


        Takze co vlastne udelam je ze naimplementuju ty metody, pak zmenim paint protoze tam se to kresli pres prostredek
        pak tam pridam tu metodu co mi vrati tu vlnu uz v doublech
        nezapomenout zavolat ty labely no a ted envim to uz vypada ze je vsechno