package deprecatedclasses;

import analyzer.bpm.CombFilterBPMGetterIFace;
import analyzer.bpm.SubbandSplitterIFace;
import org.jtransforms.fft.DoubleFFT_1D;
import util.audio.FFT;

import java.io.IOException;

@Deprecated // It was used for testing or something, idk
public class CombFilterBPMMonoWithoutFiltersAndSubbandsGetter implements CombFilterBPMGetterIFace {

    // This is version for mono signal
    // double[][][] bpmArrays because first dim is for each bpm and the other 2 are the labelReferenceArrs of fft of windows
    @Override
    public int computeBPM(byte[] samples, double[][][] bpmArrays, int bpmStart, int bpmJump,
                          int sampleSize, int sampleSizeInBits, int windowSize, int startIndex, int endIndex,
                          boolean isBigEndian, boolean isSigned, int subbandCount, SubbandSplitterIFace splitter,
                          DoubleFFT_1D fft, int sampleRate) {
        double[][] fftResults;
        try {
            fftResults = FFT.calculateFFTRealForward(samples, sampleSize, sampleSizeInBits, windowSize,
                                                     startIndex, endIndex, isBigEndian, isSigned, fft);
        }
        catch (IOException e) {
            return Integer.MIN_VALUE;
        }


        return calculateBPM(bpmArrays, fftResults, bpmStart, bpmJump);
    }


    @Override
    public int calculateBPMFromEnergies(double[][] energies, int startBPM, int jumpBPM, int bpmCount) {
        return -1;
    }


    public int calculateBPM(double[][][] bpmArrays, double[][] fftResults, int startBPM, int jumpBPM) {
        int maxBPMIndex = 0;
        double maxEnergy = 0;
        double energy;

        for (int i = 0; i < bpmArrays.length; i++) {
            energy = CombFilterBPMGetterIFace.computeEnergyRealForward(fftResults, bpmArrays[i]);

            if (energy > maxEnergy) {
                maxEnergy = energy;
                maxBPMIndex = i;
            }
        }

        return CombFilterBPMGetterIFace.getBPMFromIndex(startBPM, jumpBPM, maxBPMIndex);
    }
}
