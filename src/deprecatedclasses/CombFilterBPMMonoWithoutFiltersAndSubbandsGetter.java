package deprecatedclasses;

import analyzer.bpm.CombFilterBPMGetterIFace;
import analyzer.bpm.SubbandSplitterIFace;
import org.jtransforms.fft.DoubleFFT_1D;
import util.audio.FFT;

import java.io.IOException;

@Deprecated // It was used for testing or something, idk
public class CombFilterBPMMonoWithoutFiltersAndSubbandsGetter implements CombFilterBPMGetterIFace {

    // TODO: This is version for mono signal
    // double[][][] bpmArrays because first dim is for each bpm and the other 2 are the labelReferenceArrs of fft of windows
    @Override
    public int computeBPM(byte[] samples, double[][][] bpmArrays, int bpmStart, int bpmJump,
                          int sampleSize, int sampleSizeInBits, int windowSize, int startIndex, int endIndex,
                          boolean isBigEndian, boolean isSigned, int subbandCount, SubbandSplitterIFace splitter,
                          DoubleFFT_1D fft, int sampleRate) {
        double[][] fftResults;
        try {
            fftResults = FFT.calculateFFTRealForward(samples, sampleSize, sampleSizeInBits, // TODO: Tahle metoda se casto pouziva se stejnym FFT oknem ... nema smysl vytvaret porad ten samy
                windowSize, startIndex, endIndex, isBigEndian, isSigned, fft);     // TODO: tohle vraci measury ... nikoliv imag a real cast ... prizpusobit k tomu tu metodu
            // TODO: A jeste ten nechci volat na cely song ... vypocetne narocny ... melo by se to delat na nejakou 5ti sekundovou cast
            // TODO: !!!!!!!!!!!!!!
        }
        catch(IOException e) {
            return -1;          // TODO: Nebo bych mel vyhodit exception? - podle me bych to mel resit hned tady
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
// TODO: JUST DEBUG
//        double[] energies = new double[bpmArrays.length];
//        int[] usedIndexes = new int[bpmArrays.length];
//        for(int i = 0; i < usedIndexes.length; i++) {
//            usedIndexes[i] = -1;
//        }
// TODO: JUST DEBUG

        // TODO:        System.out.println(fftResults.length + "\t!\t" + fftResults[0].length);
        for(int i = 0; i < bpmArrays.length; i++) {
// TODO:            System.out.println(bpmArrays[i].length + "\t!\t" + bpmArrays[i][0].length);
            energy = CombFilterBPMGetterIFace.computeEnergyRealForward(fftResults, bpmArrays[i]);
//            System.out.println((startBPM + i * jumpBPM) + ":\t" + energy);

//// TODO: Measure verze ... ale je to jeste horsi
//            //TODO:
//            energy = 0;
//            for(int l0 = 0; l0 < fftResults.length; l0++) {
//                for (int l = 0; l < fftResults[l0].length; l++) {
//                    energy += (fftResults[l0][l] * bpmArrays[i][l0][l]);
//                }
//            }
//            // TODO:

            if(energy > maxEnergy) {
                maxEnergy = energy;
                maxBPMIndex = i;
            }

// TODO: JUST DEBUG            energies[i] = energy;
        }


// TODO: JUST DEBUG
//        int index = -1;
//        for(int i = 0; i < energies.length; i++) {
//            energy = maxEnergy;
//            for (int k = 0; k < energies.length; k++) {
//                if (energies[k] <= energy && usedIndexes[k] == -1) {
//                    index = k;
//                    energy = energies[k];
//                }
//            }
//
//            usedIndexes[index] = index;
//            System.out.println(CombFilterBPMGetterIFace.getBPMFromIndex(startBPM, jumpBPM, index) + ":\t" + energy);
//        }
// TODO: JUST DEBUG

        return CombFilterBPMGetterIFace.getBPMFromIndex(startBPM, jumpBPM, maxBPMIndex);
    }
}
