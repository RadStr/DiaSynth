package DiagramSynthPackage.Synth.Generators.ClassicGenerators.NoPhase;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JFileChooserAudioPluginDefault;
import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.InputPort;
import DiagramSynthPackage.Synth.Generators.ClassicGenerators.Phase.WaveTableGeneratorWithPhase;
import DiagramSynthPackage.Synth.Generators.Generator;
import DiagramSynthPackage.Synth.Generators.GeneratorWithPhase;
import DiagramSynthPackage.Synth.Unit;
import DiagramSynthPackage.Synth.WaveTables.WaveTable;
import DiagramSynthPackage.Synth.WaveTables.WaveTableFast;
import RocnikovyProjektIFace.AudioPlayerPanelIFaceImplementation;
import Rocnikovy_Projekt.DoubleWave;
import Rocnikovy_Projekt.MyLogger;
import Rocnikovy_Projekt.Program;

import javax.swing.*;
import java.io.*;

public class WaveTableGenerator extends WaveTableGeneratorWithPhase {
    public WaveTableGenerator(Unit u) {
        super(u);
    }

    public WaveTableGenerator(JPanelWithMovableJPanels panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected InputPort[] createInputPorts(JPanelWithMovableJPanels panelWithUnits, double[] neutralValues) {
        return Generator.createInputPorts(this, panelWithUnits, neutralValues);
    }
}
