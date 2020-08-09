package DiagramSynthPackage.Synth.Operators;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.Ports.InputPort;
import DiagramSynthPackage.Synth.Unit;

public abstract class Operator extends Unit {
    public Operator(Unit u) { super(u);}
    public Operator(JPanelWithMovableJPanels panelWithUnits) {
        super(panelWithUnits);
    }

    @Override
    protected void setPropertiesPanel() {
        // Doesn't have properties
        propertiesPanel = null;
    }
    @Override
    public void updateAfterPropertiesCall() {
        // EMPTY
    }


    @Override
    public boolean getIsConst() {
        boolean isConst = true;
        for(InputPort ip : inputPorts) {
            isConst = isConst && ip.getIsConst();
        }

        return isConst;
    }
    @Override
    public boolean getIsNoiseGen() {
        boolean isNoiseGen = true;
        for(InputPort ip : inputPorts) {
            isNoiseGen = isNoiseGen && (ip.getIsNoiseGen() || ip.getIsConst());
        }

        return isNoiseGen;
    }


    @Override
    public double getModulationFrequency() {
        double modulationIndex = Double.MAX_VALUE;
        for(int i = 0; i < inputPorts.length; i++) {
            double modIndexFromIP = inputPorts[i].getModulationFrequency();
            if(modulationIndex != modIndexFromIP) {     // It is not Double.MAX_VALUE
                return modIndexFromIP;
            }
        }

        return modulationIndex;
    }


    /**
     * Returns the amplitudes of the modulating wave
     *
     * @return
     */
    @Override
    public double[] getModulatingWaveAmps() {
        for(int i = 0; i < inputPorts.length; i++) {
            double[] amps = inputPorts[i].getModulatingWaveAmps();
            if(amps != null) {
                return amps;
            }
        }

        return null;
    }

    /**
     * Returns the frequencies of the modulating wave.
     *
     * @return
     */
    @Override
    public double[] getModulatingWaveFreqs() {
        for(int i = 0; i < inputPorts.length; i++) {
            double[] freqs = inputPorts[i].getModulatingWaveFreqs();
            if(freqs != null) {
                return freqs;
            }
        }

        return null;
    }


    @Override
    public double[] getWaveAmps(int waveIndex) {
        int index = -1;
        for(int i = 0, waveIndexInInputPort = 0; i < inputPorts.length; i++, waveIndexInInputPort = 0) {
            double[] amps = inputPorts[i].getWaveAmps(waveIndexInInputPort);
            if (amps != null) {
                waveIndexInInputPort++;
                index++;
                if (waveIndex == index) {
                    return amps;
                }
            }
        }

        return null;
    }


    @Override
    public double[] getWaveFreqs(int waveIndex) {
        int index = -1;

        for(int i = 0, waveIndexInInputPort = 0; i < inputPorts.length; i++, waveIndexInInputPort = 0) {
            double[] freqs = inputPorts[i].getWaveFreqs(waveIndexInInputPort);
            if (freqs != null) {
                waveIndexInInputPort++;
                index++;
                if (waveIndex == index) {
                    return freqs;
                }
            }
        }

        return null;
    }
}