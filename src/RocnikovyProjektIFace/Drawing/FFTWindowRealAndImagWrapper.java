package RocnikovyProjektIFace.Drawing;

import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginDefaultIFace;
import RocnikovyProjektIFace.AudioPlayerPlugins.IFaces.PluginParametersAnnotation;
import RocnikovyProjektIFace.AudioPlayerPlugins.PluginJPanelBasedOnAnnotations;
import Rocnikovy_Projekt.Program;
import org.jtransforms.fft.DoubleFFT_1D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


// TODO: DRAW PANEL THINGS
//Je nyquist validní výsledek FFT já myslim, že ne,
//
//        jestli ne tak to je lehký tak pak prostě udělám ty 2 FFT okna a vezmu výsledky a ty dám do pole a z toho udělám IFFT.
//
//        Akorát musim ten 0.tej bin z komplexni casti ignorovat.
//
//        Pokud tam je nyquist validní, tak ho tam hold přidám no a pokud má validní jen reálnou šást tak tam přidám jen jeho reálnou část
//        https://mathworld.wolfram.com/NyquistFrequency.html - Nyquist
//
//        Takže je to jednoduchý nyquist dává smysl, ale stejně je lepší ho vyfiltrovat, protože to už je skutečně na hraci (pro představu mám fs = 4, takže mám 4 body na reprezentaci 2Hz sinu Takže mám [0, 0, 0, 0])
//        --------------------------------------------------------------------------------------------
//        Amplitude values are assumed to be between 0 and 1, and notice that they’re quite small because they all must sum to 1 (and there are a lot of bins!). - Nezdá se mi, že by to byla pravda, z testování mi vyšly součty vyšší než 1.
//        --------------------------------------------------------------------------------------------
//
//        --------------------------------------------------------------------------------------------
//        --------------------------------------------------------------------------------------------
//        --------------------------------------------------------------------------------------------
//        --------------------------------------------------------------------------------------------
//        --------------------------------------------------------------------------------------------
//        --------------------------------------------------------------------------------------------
//        Co bude dobrý bude, že když to window posunu do nuly tak pak mi stačí dělit velikost okna / 2 jinak musim dělit velikostí okna kvůli 0Hz
//
//        ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//
//        Tak je to takhle:
//        Lichý vstup nemá prostředek
//        Sudý má.
//        První bin odpovídá frekvenci 0 Hz, která odpovídá sum(vstup)
//        Poslední bin odpovídá frekvenci f_s - freq_jump
//
//        --------------------------------------------------------------------------------------------
//
//        proste hadam ze ten [513] uz je fakt stejny s [512] - otestovat ale dává to smysl
//        Jen když je lichej počet binů tak to má protředek co se nekopíruje
//
//        Takže se teď musim zamyslet jestli když je windowsize lichá tak jestli je i počet binů lichej. Já si myslim, že jo a že sudej má sudej. Dává to konec konců smysl z té knížky !!!!!!!!!!
//
//        Poznámka: Sudý počet binů nemá prostředek, lichý má, viz. 12 3 45 vs 123 456
//
//        --------------------------------------------------------------------------------------------
//
//        Uz to je skoro hotovy, uz jen musim udelat ze to nejde roztahnout nebo tak neco
//        pridat tam ty reference
//        a asi udelat ze labelu muze byt i min nez je pocet tech binu
//
//
//
//
//
//        ----------------------------------------------------------------------------------------------------------------
//
//
//        Asi budu muset udelat tridu od ktery bude tahle dedit a ta bude mit jen getDrawnValues a nebude mit cas - tu pouziju na vykreslovani grafu pro waveshaper
//
//
//        Muzu mit 2 FFT windows na realnou a imaginarni slozku
//
//// TODO: AAA - setFrequencyToolTip
//
//
//        Pozor na + a - u tech FFT to nevadi ale u tech draw values to vadi tam je bezne - cislo
//
//
//        Takze co vlastne udelam je ze naimplementuju ty metody, pak zmenim paint protoze tam se to kresli pres prostredek
//        pak tam pridam tu metodu co mi vrati tu vlnu uz v doublech
//        nezapomenout zavolat ty labely no a ted envim to uz vypada ze je vsechno
//
//        -----------------------------------------------------------------------------------------------------------------
//        Nevim Proc je tohle potreba, melo by to snad jit i bez toho - layout by mel spravne nastavit tu vysku takze nevim co se deje
//        outputReferenceValues.setPreferredSize(new Dimension(getPreferredSize().width, drawnFunctionPanel.getPreferredSize().height));
//
//        -----------------------------------------------------------------------------------------------------------------
//
//        TODO: tady bych mel mit co nevjic to jde, resp. v te WaveShaper to bude pres celou obrazovku a ta function Draw vezme vsechno co nevezmou ty reference
//        ------------------------------------------------------------------------------------------------------------------------
//        Takze mam fft window 1024 - to ma nejakych asi 512 kosiku nebo 513 ted nevim, 513 asi
//        No a kdyz dam real forward tak se mi to vejde do tech 1024 prvku protoze nejaky ty realny, resp. imaginarni casti jsou 0 takze tam byt nemusi
//
//        Kdybych ale dal full complex forward tak mam tech 1024 prvku ale pak jeste dalsich 1024 protoze to je prevraceny (protoze jsem dal realnej vstup tak to je prevraceny, kdybych dal komplexni vstup tak by realne casti odpovidala ta prvni pulka a te komplexni ta druha) - takze mam 1024 real cisel a 1024 imag cisel
//
//        A tech prvnich 513 * 2 indexu da stejny vysledky jako ta fft real forward ta 513 tam je protoze se to podle nej dal zrcadli - je tam jen jednou takze proto 513
//
//        proto mam biny 0-512
//
//
//        ?????? - nevim co jsem timhle myslel
//        Jenze kdyz mam tech 513 u toho delka % 2 == 0 tak tam mam ten prvek na index len / 2 no a ten odpovida te 0, takze mam i tak jen 512 binu
//        ??????
//        ------------------------------------------------------------------------------------------------------------------------
//        aha uz vim v cem je problem a[1] a a[0] jsou stejny cisla totiz to je frekvence 0 a nyquist frekvence
//        v tom pripade bych ale mel pracovat jen s 512.
//        zkontrolovat to jestli se to skutecne rovna
//        !!!!!! Jenze to neni pravda nyquistova frekvence se prekryva s nyquistovou frekvenci
//        ------------------------------------------------------------------------------------------------------------------------
//        Dalsi vec vzhledem k tomu ze vysledky fft jsou i zaporny, tak to znamena ze asi dava vetsi smysl mit ty measury mezi -1 a 1 misto 0 a 1
//        Ale to jen v případě, když mám tu imaginární a reálnou část, jinak mi stačí jen ty measury, protože tu imaginární část stejně nastavim na 0.
// TODO: DRAW PANEL THINGS



public class FFTWindowRealAndImagWrapper extends JPanel implements DrawWrapperIFace {
    public FFTWindowRealAndImagWrapper(double[] song, int windowSize, int startIndex,
                                       int sampleRate, boolean isEditable,
                                       Color backgroundColorRealPart, Color backgroundColorImagPart,
                                       boolean shouldDrawLabelsAtTop) {
        realPartPanel = new FFTWindowPartWrapper(this, song, windowSize, startIndex, sampleRate,
                isEditable, backgroundColorRealPart, shouldDrawLabelsAtTop);
        imagPartPanel = new FFTWindowPartWrapper(this, song, windowSize, startIndex, sampleRate,
                isEditable, backgroundColorImagPart, shouldDrawLabelsAtTop);

        int binCount = Program.getBinCountRealForward(windowSize);
        fftResult = new double[2 * windowSize]; // 2* because we will use complex FFT
        fft = new DoubleFFT_1D(windowSize);


        if(song != null) {
            Program.calculateFFTRealForward(song, startIndex, windowSize, 1, fft, fftResult);
        }
        // TODO: DRAW PANEL THINGS
//        TODO: nevim jestli je ta normalizace dobre
        // TODO: DRAW PANEL THINGS
        for(int i = 0; i < fftResult.length; i++) {
            fftResult[i] /= binCount;
        }
        Program.separateRealAndImagPart(realPartPanel.fftWindowPartPanel.DRAW_VALUES,
                imagPartPanel.fftWindowPartPanel.DRAW_VALUES, fftResult, windowSize);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(realPartPanel);
        add(new JPanel() {
            private final Dimension prefSize = new Dimension(1, SPACE_BETWEEN_PARTS);
            @Override
            public Dimension getPreferredSize() {
                return prefSize;
            }
        });
        add(imagPartPanel);
        add(new JPanel() {
            private final Dimension prefSize = new Dimension(1, SPACE_BETWEEN_PARTS);
            @Override
            public Dimension getPreferredSize() {
                return prefSize;
            }
        });
    }

    public static final int SPACE_BETWEEN_PARTS = 4;

    private final DoubleFFT_1D fft;
    private final double[] fftResult;
    private final FFTWindowPartWrapper realPartPanel;
    private final FFTWindowPartWrapper imagPartPanel;



    private Dimension minSize = new Dimension();
    @Override
    public Dimension getMinimumSize() {
        minSize.width = realPartPanel.getMinimumSize().width;
        minSize.height = 2 * realPartPanel.getMinimumSize().height + 2 * SPACE_BETWEEN_PARTS;
        return minSize;
    }

    private Dimension prefSize = new Dimension();
    @Override
    public Dimension getPreferredSize() {
        prefSize.width = super.getPreferredSize().width;

        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JPanel contentPane = (JPanel) topFrame.getContentPane();
        Insets frameInsets = topFrame.getInsets();
        // For some reason have to make it smaller. I choose to make it smaller by frameInsets.bottom, but could be anything > 5
        prefSize.height = contentPane.getHeight() - frameInsets.bottom - frameInsets.bottom;
        return prefSize;
    }



    public void setBinValues(FFTWindowPartPanel partPanel, int bin, double newValue) {
        FFTWindowPanelAbstract otherPartPanel = getTheOtherPartPanel(partPanel);
        double squareValue = newValue * newValue;
        double otherPanelValue = otherPartPanel.getDrawValue(bin);
        double otherPanelValueSquare = otherPanelValue * otherPanelValue;

        double squaresSum = otherPanelValueSquare + squareValue;
        if(squaresSum > 1) {
            double newOtherPanelValue = Math.sqrt(1 - squareValue);
            newOtherPanelValue *= Math.signum(otherPanelValue);
            otherPartPanel.setDrawValue(bin, newOtherPanelValue);
            otherPartPanel.repaint();
        }

        partPanel.setDrawValue(bin, newValue);
    }

    private FFTWindowPanelAbstract getTheOtherPartPanel(FFTWindowPanelAbstract partPanel) {
        FFTWindowPanelAbstract otherPartPanel;
        if(partPanel == imagPartPanel.fftWindowPartPanel) {
            otherPartPanel = realPartPanel.fftWindowPartPanel;
        }
        else {
            otherPartPanel = imagPartPanel.fftWindowPartPanel;
        }

        return otherPartPanel;
    }


    public double[] getIFFTResult(int periodCount) {
        double[] realPart = realPartPanel.fftWindowPartPanel.DRAW_VALUES;
        double[] imagPart = imagPartPanel.fftWindowPartPanel.DRAW_VALUES;
        Program.connectRealAndImagPart(realPart, imagPart, fftResult);
        getComplexIFFT(fftResult, fft);

        double[] ifftResult = Program.copyArr(fftResult, fftResult.length, periodCount);
        return ifftResult;
    }

    public static void getComplexIFFT(double[] arr, DoubleFFT_1D fft) {
        fft.complexInverse(arr, true);
    }

    protected void setTheOtherPartSelectedBin(FFTWindowPanelAbstract partPanel, int bin) {
        FFTWindowPanelAbstract otherPartPanel = getTheOtherPartPanel(partPanel);
        otherPartPanel.setSelectedBin(bin);
        otherPartPanel.repaint();
    }

    @Override
    public void addMenus(JMenuBar menuBar, AddWaveIFace waveAdder) {
        if(!realPartPanel.drawPanel.getIsEditable()) {
            JMenu menu = new JMenu("Options");
            menuBar.add(menu);
            JCheckBoxMenuItem showRelativeCheckbox = new JCheckBoxMenuItem("Show relative");
            showRelativeCheckbox.setSelected(false);
            menu.add(showRelativeCheckbox);
            showRelativeCheckbox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {
                        realPartPanel.fftWindowPartPanel.makeRelativeValues();
                        imagPartPanel.fftWindowPartPanel.makeRelativeValues();
                    }
                    else {
                        realPartPanel.fftWindowPartPanel.makeAbsoluteValues();
                        imagPartPanel.fftWindowPartPanel.makeAbsoluteValues();
                    }
                }
            });
        }
        else {
            JMenu menu = new JMenu("Options");
            menuBar.add(menu);
            JMenuItem optionsMenuItem = new JMenuItem("Set fft window parameters");
            menu.add(optionsMenuItem);
            optionsMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FFTWindowOptionsDialogPanel classWithValues = new FFTWindowOptionsDialogPanel(realPartPanel.fftWindowPartPanel);
                    PluginJPanelBasedOnAnnotations dialogPanel = new PluginJPanelBasedOnAnnotations(classWithValues,
                            classWithValues.getClass());

                    int result = JOptionPane.showConfirmDialog(null, dialogPanel,
                            "Dialog: " + classWithValues.getPluginName(), JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                        FFTWindowPartPanel part = (FFTWindowPartPanel) realPartPanel.fftWindowPartPanel.createNewFFTPanel(
                                classWithValues.getWindowSize(), classWithValues.getShouldChangeWindowSize(),
                                classWithValues.getSampleRate(), classWithValues.getShouldChangeSampleRate());
                        realPartPanel.setDrawPanel(part);

                        part = (FFTWindowPartPanel) imagPartPanel.fftWindowPartPanel.createNewFFTPanel(
                                classWithValues.getWindowSize(), classWithValues.getShouldChangeWindowSize(),
                                classWithValues.getSampleRate(), classWithValues.getShouldChangeSampleRate());
                        imagPartPanel.setDrawPanel(part);
                    }
                }
            });

            menu = new JMenu("Action");
            menuBar.add(menu);
            JMenuItem actionMenuItem = new JMenuItem("Perform IFFT");
            menu.add(actionMenuItem);
            actionMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    IFFTDialogPanel classWithValues = new IFFTDialogPanel();
                    PluginJPanelBasedOnAnnotations performIFFTDialog = new PluginJPanelBasedOnAnnotations(classWithValues,
                            classWithValues.getClass());

                    int result = JOptionPane.showConfirmDialog(null, performIFFTDialog,
                            "Dialog: " + classWithValues.getPluginName(), JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                        double[] wave = getIFFTResult(classWithValues.getPeriodCount());
                        waveAdder.addWave(wave);
                    }
                }
            });

            addFullReset(menu);
            addRealReset(menu);
            addImagReset(menu);
        }
    }


    private void addFullReset(JMenu menu) {
        JMenuItem resetMenuItem = new JMenuItem("Reset both parts");
        resetMenuItem.setToolTipText("Resets both real and imaginary part to neutral values");

        menu.add(resetMenuItem);
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realPartPanel.drawPanel.resetValues();
                imagPartPanel.drawPanel.resetValues();
            }
        });
    }
    private void addRealReset(JMenu menu) {
        JMenuItem resetMenuItem = new JMenuItem("Reset real part");
        resetMenuItem.setToolTipText("Resets the real part to neutral value");

        menu.add(resetMenuItem);
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realPartPanel.drawPanel.resetValues();
            }
        });
    }
    private void addImagReset(JMenu menu) {
        JMenuItem resetMenuItem = new JMenuItem("Reset imaginary part");
        resetMenuItem.setToolTipText("Resets the imaginary part to neutral value");

        menu.add(resetMenuItem);
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagPartPanel.drawPanel.resetValues();
            }
        });
    }


    private static class FFTWindowOptionsDialogPanel implements PluginDefaultIFace {
        public FFTWindowOptionsDialogPanel(FFTWindowPanelAbstract fftPanel) {
            this.windowSize = fftPanel.WINDOW_SIZE;
            // 2* because otherwise it is Nyquist frequency
            this.sampleRate = (int)Math.round(2 * (Program.getBinCountRealForward(windowSize) - 1) * fftPanel.FREQ_JUMP);
        }


        @PluginParametersAnnotation(lowerBound = FFTWindowPanel.MIN_WINDOW_SIZE_STRING,
                upperBound = FFTWindowPanel.MAX_WINDOW_SIZE_STRING,
                parameterTooltip = "Controls number of size of the FFT window.")
        private int windowSize;
        public int getWindowSize() {
            return windowSize;
        }

        @PluginParametersAnnotation(defaultValue = "TRUE",
                parameterTooltip = "If set to true, the window size will be changed after ending the dialog with ok, otherwise it won't be changed")
        private boolean shouldChangeWindowSize;
        public boolean getShouldChangeWindowSize() {
            return shouldChangeWindowSize;
        }

        @PluginParametersAnnotation(lowerBound = "1", parameterTooltip = "Controls the sample rate of the input samples.")
        private int sampleRate;
        public int getSampleRate() {
            return sampleRate;
        }

        @PluginParametersAnnotation(defaultValue = "TRUE",
                parameterTooltip = "If set to true, the sample rate of the original wave for purposes of fft will be changed after ending the dialog with ok, otherwise it won't be changed")
        private boolean shouldChangeSampleRate;
        public boolean getShouldChangeSampleRate() {
            return shouldChangeSampleRate;
        }

        /**
         * @return Returns true if the operation needs parameters - so user needs to put them to the JPanel.
         * If it returns false, then it doesn't need parameters from user and the operation can start immediately
         */
        @Override
        public boolean shouldWaitForParametersFromUser() {
            return true;
        }

        /**
         * This parameter matters only when shouldWaitForParametersFromUser returns true
         *
         * @return
         */
        @Override
        public boolean isUsingDefaultJPane() {
            return true;
        }

        @Override
        public String getPluginName() {
            return "FFT window parameters panel";
        }
    }


    private static class IFFTDialogPanel implements PluginDefaultIFace {
        @PluginParametersAnnotation(lowerBound = "1", defaultValue = "1", parameterTooltip = "Controls the number of periods (repetitions) of IFFT result")
        private int periodCount;
        public int getPeriodCount() {
            return periodCount;
        }


        /**
         * @return Returns true if the operation needs parameters - so user needs to put them to the JPanel.
         * If it returns false, then it doesn't need parameters from user and the operation can start immediately
         */
        @Override
        public boolean shouldWaitForParametersFromUser() {
            return true;
        }

        /**
         * This parameter matters only when shouldWaitForParametersFromUser returns true
         *
         * @return
         */
        @Override
        public boolean isUsingDefaultJPane() {
            return true;
        }

        @Override
        public String getPluginName() {
            return "Perform IFFT";
        }
    }
}