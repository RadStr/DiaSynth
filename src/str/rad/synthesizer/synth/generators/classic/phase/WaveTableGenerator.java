package str.rad.synthesizer.synth.generators.classic.phase;

import str.rad.plugin.JFileChooserPlugin;
import str.rad.synthesizer.gui.diagram.DiagramPanel;
import str.rad.synthesizer.synth.generators.GeneratorNoPhase;
import str.rad.synthesizer.synth.generators.Generator;
import str.rad.synthesizer.synth.SynthDiagram;
import str.rad.synthesizer.synth.Unit;
import str.rad.synthesizer.synth.tables.WaveTable;
import str.rad.synthesizer.synth.tables.WaveTableFast;
import str.rad.player.AudioPlayerPanel;
import str.rad.util.Utilities;
import str.rad.util.audio.wave.DoubleWave;
import str.rad.util.logging.DiasynthLogger;

import javax.swing.*;
import java.io.*;

public class WaveTableGenerator extends Generator {
    public WaveTableGenerator(Unit u) {
        super(u);
        copyInternalState(u);
    }

    public WaveTableGenerator(DiagramPanel panelWithUnits) {
        super(panelWithUnits);
        setDefaultWaveTable();
    }

    private void setDefaultWaveTable() {
        double[] sine = SineGenerator.createSine(512, 1, 1, 0);
        setWaveTable(sine, null);
    }

    private WaveTable waveTable;

    public void setWaveTable(double[] wave, String path) {
        waveTable = createWaveTable(wave, path);
    }

    public static WaveTable createWaveTable(double[] wave, String path) {
        WaveTable waveTable;
        if (Utilities.testIfNumberIsPowerOfN(wave.length, 2) > 0) {
            waveTable = new WaveTableFast(wave, path);
        }
        else {
            waveTable = new WaveTable(wave, path);
        }

        return waveTable;
    }

    public void setWaveTable(GeneratorNoPhase g, int len, double amp) {
        double[] wave = new double[len];
        for (int i = 0; i < len; i++) {
            wave[i] = g.generateSampleConst(i, len, amp, 1);
        }
        setWaveTable(wave, null);
    }

    private void setWaveTable(File f) {
        if (f != null && f.exists() && f.isFile()) {
            loadWaveTable(f.getPath());
        }
    }


    private JFileChooser propertiesPanelView;

    @Override
    protected void setPropertiesPanel() {
        propertiesPanel = new JFileChooserPlugin();
        propertiesPanelView = (JFileChooser) propertiesPanel;
    }

    @Override
    public void updateAfterPropertiesCall() {
        File f = propertiesPanelView.getSelectedFile();
        setWaveTable(f);
    }

    @Override
    public double generateSampleConst(double timeInSecs, int diagramFrequency, double amp,
                                      double freq, double phase) {
        double genVal = amp * waveTable.popInterpolated(freq, diagramFrequency, phase);
        return genVal;
    }

    @Override
    public double generateSampleFM(double timeInSeconds, int diagramFrequency, double amp,
                                   double carrierFreq, double modulatingWaveFreq,
                                   double modulatingWaveOutValue,
                                   double currentInputFreq, double phase) {
        return generateSampleConst(timeInSeconds, diagramFrequency, amp, currentInputFreq, phase);
    }


    private boolean isFirstCall = true;

    @Override
    public void calculateSamples() {
        if (waveTable != null) {
            if (isFirstCall) {
                // I have to move it to state in which it would be if it ran from the start, else
                // the results would be inconsistent,
                // because when I add new wave table when the diagram is already running,
                // while the other wave table is somewhere else.
                // For example when adding sine and sine in 180° phase the result wouldn't be 0.
                isFirstCall = false;
                double freq = inputPorts[1].getValue(0);
                SynthDiagram diagram = panelWithUnits.getSynthDiagram();
                waveTable.goToNthWaveIndex(freq, diagram.getOutputFrequency(), diagram.getTimeInSamples());
            }
            super.calculateSamples();
        }
    }

    @Override
    public void resetToDefaultState() {
        waveTable.setWaveIndex(0);
    }

    @Override
    public String getDefaultPanelName() {
        return "WT";
    }

    @Override
    public String getTooltip() {
        return "Generated specified wave using wave table synthesis technique.";
    }

    @Override
    public void save(PrintWriter output) {
        super.save(output);
        if (waveTable.WAVE_PATH == null) {
            double[] wave = waveTable.getWave();
            String PREFIX_PATH = "resources/WaveTables/WT_GEN_";
            int index = 0;
            String path = PREFIX_PATH + index;
            while (new File(path).exists()) {
                index++;
                path = PREFIX_PATH + index;
            }
            DoubleWave.storeDoubleArray(wave, 0, wave.length, path);
            output.println(path);
        }
        else {
            output.println(waveTable.WAVE_PATH);
        }
    }

    @Override
    public void load(BufferedReader input) {
        super.load(input);
        try {
            String line = input.readLine();
            loadWaveTable(line);
        }
        catch (IOException e) {
            DiasynthLogger.logException(e);
        }
    }


    private void loadWaveTable(String path) {
        try {
            DoubleWave doubleWave;
            doubleWave = AudioPlayerPanel.loadMonoDoubleWave(new File(path),
                                                             -1, false);
            if (doubleWave == null || doubleWave.getSongLength() <= 0) {
                RandomAccessFile file = new RandomAccessFile(path, "r");
                double[] wave = DoubleWave.getStoredDoubleArray(file.getChannel());
                if (wave != null) {
                    setWaveTable(wave, path);
                }
                else {
                    setDefaultWaveTable();
                }
            }
            else {
                setWaveTable(doubleWave.getSong(), path);
            }
        }
        catch (IOException e) {
            DiasynthLogger.logException(e);
        }
    }


    @Override
    public void copyInternalState(Unit copySource) {
        WaveTableGenerator copySourceCasted = (WaveTableGenerator) copySource;
        double[] waveTableWave = copySourceCasted.waveTable.getWave();
        double[] copiedWaveTableWave = new double[waveTableWave.length];
        System.arraycopy(waveTableWave, 0, copiedWaveTableWave, 0, waveTableWave.length);
        setWaveTable(copiedWaveTableWave, copySourceCasted.waveTable.WAVE_PATH);
    }
}
