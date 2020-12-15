package synthesizer.synth;

import synthesizer.gui.diagram.util.ListSortedByY;
import synthesizer.synth.audio.AudioThread;
import util.audio.format.AudioFormatWithSign;
import util.logging.MyLogger;
import Rocnikovy_Projekt.Program;

import javax.sound.sampled.AudioFormat;
import java.util.ConcurrentModificationException;

public class SynthDiagram extends Thread {
    public SynthDiagram(ListSortedByY units, OutputUnitGetter outputUnitGetter, OutputFormatGetter outputFormatGetter,
                        boolean shouldPause) {
        setShouldPause(shouldPause);
        this.units = units;
        timeInSamples = 0;
        this.outputUnitGetter = outputUnitGetter;
        this.outputFormatGetter = outputFormatGetter;
    }

    private OutputUnitGetter outputUnitGetter;
    private OutputFormatGetter outputFormatGetter;
    private ListSortedByY units;
    private int timeInSamples;
    public int getTimeInSamples() {
        return timeInSamples;
    }

    public int getOutputFrequency() {
        return (int)outputFormatGetter.getOutputFormat().getSampleRate();
    }


    public void performOneStep() {
        while(true) {
            try {
                for (Unit unit : units) {
                    if(!unit.performedCalculation) {
                        unit.calculateSamples();
                        unit.markAsCalculated();
                    }
                }

                if (units.getHasChanged()) {
                    units.setHasChanged(false);
                }
                else {
                    break;
                }
            }
            catch(ConcurrentModificationException | NullPointerException e) {
                units.setHasChanged(false);
            }
        }

        int writtenSamplesCount = outputUnitGetter.getOutputUnitWrittenSamples();
        timeInSamples += writtenSamplesCount;

        // Now reset states
        while(true) {
            try {
                for (Unit unit : units) {
                    unit.unmarkAsCalculated();
                }

                if (units.getHasChanged()) {
                    units.setHasChanged(false);
                }
                else {
                    break;
                }
            }
            catch(ConcurrentModificationException | NullPointerException e) {
                units.setHasChanged(false);
            }
        }
    }


    // Basically copy pasted the performOneStep method
    private int performOneStepInstantRecording(double[][] channelRecords, int index, int remainingLen) {
        while(true) {
            try {
                for (Unit unit : units) {
                    if(!unit.performedCalculation) {        // If it wasn't written yet (Since it is possible that it was written and some new unit was added)
                        unit.calculateSamplesInstantRecord(channelRecords, index, remainingLen);
                        unit.markAsCalculated();
                    }
                }

                if (units.getHasChanged()) {
                    units.setHasChanged(false);
                }
                else {
                    break;
                }
            }
            catch(ConcurrentModificationException | NullPointerException e) {
                units.setHasChanged(false);
            }
        }

        int writtenSamplesCount = outputUnitGetter.getOutputUnitWrittenSamples();
        timeInSamples += writtenSamplesCount;

        // Now reset states
        while(true) {
            try {
                for (Unit unit : units) {
                    unit.unmarkAsCalculated();
                }

                if (units.getHasChanged()) {
                    units.setHasChanged(false);
                }
                else {
                    break;
                }
            }
            catch(ConcurrentModificationException | NullPointerException e) {
                units.setHasChanged(false);
            }
        }

        return writtenSamplesCount;
    }



    @Override
    public void run() {
        synchronized (lock) {
            while (true) {
                if (shouldPause) {
// TODO: RML
//                    ProgramTest.debugPrint("SYNTH - WAITING");
// TODO: RML
                    waiting = true;
                    try {
                        lock.wait();        // Passive waiting
                    } catch (InterruptedException e) {
                        MyLogger.logException(e);
                        return;
                    }
                    waiting = false;
                    setShouldPause(false);
// TODO: RML
//                    ProgramTest.debugPrint("SYNTH - STOPPED WAITING");
// TODO: RML
                }
                performOneStep();
            }
        }
    }

    /**
     * Returns 2D array where [i] contains the recorded values of i-th channel
     * @param lenInSeconds
     * @return
     */
    public double[][] recordInstantlyDoubles(double lenInSeconds) {
        double[][] channelRecords;       // For each channel 1 array
        AudioFormat outFormat = outputFormatGetter.getOutputFormat();
        int channelsCount = outFormat.getChannels();
        int sampleRate = (int)outFormat.getSampleRate();
        int lenInSamples = (int)(sampleRate * lenInSeconds);
        channelRecords = new double[channelsCount][];
        for(int i = 0; i < channelRecords.length; i++) {
            channelRecords[i] = new double[lenInSamples];
        }

        int index = 0;
        int remainingLen = lenInSamples;
        while(index < lenInSamples) {
            int writtenSamplesCount = performOneStepInstantRecording(channelRecords, index, remainingLen);
            index += writtenSamplesCount;
            remainingLen -= writtenSamplesCount;
        }

        resetDiagramToDefaultState();
        return channelRecords;
    }

    /**
     * Returns array containing recorded samples.
     * @param lenInSeconds
     * @return
     */
    public byte[] recordInstantlyBytes(double lenInSeconds) {
        double[][] recordedChannels = recordInstantlyDoubles(lenInSeconds);
        AudioFormatWithSign outFormat = outputFormatGetter.getOutputFormat();
        int sampleSizeInBytes = outFormat.getSampleSizeInBits();
        // sampleSizeInBytes == sample size in bits currently
        int maxAbsoluteValue = Program.getMaxAbsoluteValueSigned(sampleSizeInBytes);
        sampleSizeInBytes /= 8;
        int frameSize = recordedChannels.length * sampleSizeInBytes;
        byte[] recordedAudio = new byte[frameSize * recordedChannels[0].length];
        AudioThread.fillByteArrWithChannels(recordedChannels, recordedChannels[0].length, sampleSizeInBytes, recordedAudio,
                maxAbsoluteValue, outFormat.isBigEndian(), outFormat.isSigned);
        return recordedAudio;
    }


    // Basically same as audio thread
    private volatile boolean shouldPause;
    private Object pauseLock = new Object();
    private void setShouldPause(boolean value) {
        synchronized (pauseLock) {
            shouldPause = value;
        }
    }
    private volatile boolean waiting = false;       // Modified only by synth thread
    public boolean isPaused() {
        return waiting;
    }
    private Object lock = new Object();

    public void play() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }
    public void pause() {
        setShouldPause(true);
    }
    public void reset() {
        while(!waiting) {       // Active waiting
            pause();
        }

        resetDiagramToDefaultState();
    }

    private void resetDiagramToDefaultState() {
        timeInSamples = 0;
        resetUnitsToStartState();
    }

    private void resetUnitsToStartState() {
        while(true) {
            try {
                for (Unit unit : units) {
                    unit.resetToDefaultState();
                }

                if (units.getHasChanged()) {
                    units.setHasChanged(false);
                }
                else {
                    break;
                }
            }
            catch(ConcurrentModificationException | NullPointerException e) {
                units.setHasChanged(false);
            }
        }
    }
}