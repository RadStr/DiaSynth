package analyzer.bpm;

import Rocnikovy_Projekt.Program;
import org.jtransforms.fft.DoubleFFT_1D;
import util.Rectification;
import util.audio.FFT;
import util.audio.FFTWindow;
import util.audio.Utilities;

import java.io.IOException;

public interface CombFilterBPMGetterIFace {


    public int calculateBPMFromEnergies(double[][] energies, int startBPM, int jumpBPM, int bpmCount);


    /**
     * Returns BPM based on the given parameters, this is used to get the BPM based on index in the energies array.
     * @param startBPM
     * @param jumpBPM
     * @param index
     * @return
     */
    public static int getBPMFromIndex(int startBPM, int jumpBPM, int index) {
        return startBPM + index * jumpBPM;
    }


    // TODO: This is version for mono signal
    // How it works: https://www.clear.rice.edu/elec301/Projects01/beat_sync/beatalgo.html
    // 1) Take FFT of input samples, split it to subbands (just split the result of FFT to sub labelReferenceArrs (at least logically))
    // 1a) Take IFFT of those subarrays
    // 2) Full-rectify the IFFT labelReferenceArrs, On result of this perform convolution with the right side of Hahn window
    // Convolution is esentially polynom multiplication, so to perform this process faster,
    // we perform FFT on the right side of Hahn window and FFT on the IFFT labelReferenceArrs which were full-rectified, and multiply those FFTs, then we again use IFFT
    // Now we have for each subband smooth time-domain signal.
    // 3) Now Perform simple filter ... y[n] = x[n] - x[n-1]
    // 3b) half-rectify the signal to get the extremes
    // 4) Finally perform the comb filtering, that means:
    // 4a) perform FFT on subbands, and get FFT of bpm labelReferenceArrs (method getBPMArraysFFTMeasures).
    // 4b) multiply FFT of subbands with each FFT of bpm labelReferenceArrs (equivalent to convolution in time-domain).
    // 4c) then we sum of the multiplication results of FFT subbands with 1 bpm array. Put all those sums to vector.
    // Now we have vector of energies and we pick the one with maximum energy and the bpm with which was that energy gotten
    // is the bpm of the song.
    // Additionally I can put emphasis on certain frequency bands.
    public default int calculateBPM(byte[] samples, double[][][] bpmArrays, int bpmStart, int bpmJump,
                                    int sampleSize, int sampleSizeInBits, int windowSize, int startIndex, int endIndex,
                                    boolean isBigEndian, boolean isSigned, int subbandCount, SubbandSplitterIFace splitter,
                                    DoubleFFT_1D fft, int sampleRate) {
        double[][] energies;
        energies = getEnergies(samples, bpmArrays, sampleSize, sampleSizeInBits,
            windowSize, startIndex, endIndex, isBigEndian, isSigned, subbandCount, splitter, fft, sampleRate);

        if(energies == null) {
            return -1;
        }


        return calculateBPMFromEnergies(energies, bpmStart, bpmJump, bpmArrays.length);
    }




    // TODO: Tam kde je 1 je kvuli numberOfChannels
    public default double[][] getEnergies(byte[] samples, double[][][] bpmArrays, int sampleSize, int sampleSizeInBits,
                                          int windowSize, int startIndex, int endIndex, boolean isBigEndian, boolean isSigned,
                                          int subbandCount, SubbandSplitterIFace splitter, DoubleFFT_1D fft, int sampleRate) {
        double[][] energies = new double[subbandCount][bpmArrays.length];

//        double[] fftRightSideOfHahnWindow = getFFTHahnWindow(windowSize);
        // TODO: fft na hahnovo okno bych mel predat v parametru a n to pcoitat pro kazdou pisnicku zvlast
        double hahnWindowSizeInSecs = 0.4;
        // This is *2 the value from the source material, because I calculate the hahn window a bit differently -
        // I checked it against the implementation from the source materials and the results are the same now (+/- double error)
        int hahnWindowSize = (int) (hahnWindowSizeInSecs * sampleRate * 2);
        double[] fftRightSideOfHahnWindow = FFTWindow.getHahnWindowWithLimit(windowSize, hahnWindowSize / 2, hahnWindowSize);
        // TODO: DEBUG
//        for (int i = 0; i < fftRightSideOfHahnWindow.length; i++) {
//            if (fftRightSideOfHahnWindow[i] != 0) {
////                if(i % 1000 == 0)
//                System.out.println(i + "\t" + fftRightSideOfHahnWindow[i] + "\t" + hahnWindowSize);
//            }
//        }
////        System.exit(4848);
        // TODO: DEBUG
        fft.realForward(fftRightSideOfHahnWindow);

        double[] tmpArray = new double[windowSize];         // Will usually contain results of fft, so we don't have to keep allocation labelReferenceArrs
        double[] ifftResult = new double[windowSize];
        double[] coefsForFilter = new double[]{-1, 1};
/*
        try {
            performNonRecursiveFilter(samples, coefsForFilter, 1, sampleSize, sampleSize * 1, isBigEndian, isSigned); // TODO: VYMAZAT !!!!!!!
        } catch (IOException e) { return -1; }
 */
        for (int index = startIndex; index < endIndex; ) {      // TODO: MONO
            // TODO: Stereo veci proste dam do toho for cyklu a to je asi vsechno jak se to lisi od mono verze
//            for(int j = 0; j < numberOfChannels; j++) {
//              calculateFFTClassic(...., channel == j);
//            }

//////////////////////////////////////////////////
            double[] fftResult = new double[windowSize];
// TODO:            System.out.println("calculateFFTRealForward:" + index + "\t" + windowSize + "\t" + sampleSize);
            try {
                index = Program.normalizeToDoubles(samples, fftResult, sampleSize, sampleSizeInBits,
                                                   index, isBigEndian, isSigned);
            } catch (IOException e) {
                return null;
            }
//            for(int l = 0; l < arr.length; l++) {
//// TODO:                System.out.println(l + "\t" + arr[l]);
//            }
            fft.realForward(fftResult);
//            double[] fftResult = calculateFFTClassic(samples, sampleSize, sampleSizeInBits, // TODO: Tahle metoda se casto pouziva se stejnym FFT oknem ... nema smysl vytvaret porad ten samy
//                windowSize, isBigEndian, isSigned, startingByte);     // TODO: tohle vraci measury ... nikoliv imag a real cast ... prizpusobit k tomu tu metodu
/////////////////////////////////////


            double[][] ifftResults = Program.getIFFTBasedOnSubbands(fftResult, subbandCount, fft, splitter);      // TODO: Idealne si chci jen predavat to jedno pole ... ale musim ho nulovat pred pouzitim
            for (int subband = 0; subband < ifftResults.length; subband++) {
                //TODO: DEBUG
//                for(int remove = 0; remove < ifftResults[subband].length; remove++) {
//                    if(maxEnergy < ifftResults[subband][remove]) maxEnergy = ifftResults[subband][remove];
//                }
//                System.out.println("MAX1:"+maxEnergy);
//                maxEnergy = 0;
//
//                byte[] arr = new byte[sampleSize * fftResult.length];
//                try {
//                    Program.convertDoubleArrToByteArr(ifftResults[subband], arr, 0, 0, ifftResults[subband].length, sampleSize,
//                        Program.getMaxAbsoluteValueSigned(sampleSizeInBits), isBigEndian, isSigned);
//                    Program.saveAudio("1WAVE", 22050, sampleSizeInBits, 1, isSigned, isBigEndian, arr, AudioFileFormat.Type.WAVE);
//                }
//                catch(IOException e) { System.ebxit(-500); }
                //TODO: DEBUG

// TODO: Ted nechapu proc to tady pocitam znova kdyz to mam uz v ifftResults getIFFTBasedOnSubband(fftResult, subbandCount, subband, splitter, ifftResult);
                Rectification.fullWaveRectificationDouble(ifftResults[subband], true);
// TODO: Jen debug                System.arraycopy(ifftResults[subband], 0, tmpArray, 0, tmpArray.length); // T
                fft.realForward(ifftResults[subband]); //TODO:   calculateFFTRealForward(ifftResults[subband], 0, 1, fft, tmpArray);
                // TODO: !!!!!!!!!!!!!!!! performConvolutionInFreqDomain je getCombFilterEnergyRealForward ... akorat to vsechno nakonec nesectu a nevratim jen ten soucet ... ale ukladam ty mezivysledky nasobeni do pole
                FFT.convolutionInFreqDomainRealForward(fftRightSideOfHahnWindow, ifftResults[subband], ifftResults[subband]); // TODO: ifftResults[subband] = performConvolutionInFreqDomain(fftRightSideOfHahnWindow, tmpArray);        // TODO: Mozna ten vysledek musim ulozit jinam nez do ifftResults ... zalezi jak funguje to IFFT
                FFT.calculateIFFTRealForward(ifftResults[subband], fft, true);      // TODO: To skalovani nevim
                Program.performNonRecursiveFilter(ifftResults[subband], 0, coefsForFilter,
                    1, ifftResult, 0, ifftResult.length); // TODO: performNonRecursiveFilter(ifftResults[subband], coefsForFilter);  // TODO: Napsat tuhle metodu s nereferencni variantou
                System.arraycopy(ifftResult, 0, ifftResults[subband], 0, ifftResult.length);
                Rectification.halfWaveRectificationDouble(ifftResults[subband], true);

//T
//TODO: DEBUG
//                for(int remove = 0; remove < ifftResults[subband].length; remove++) {
//                    if(maxEnergy < ifftResults[subband][remove]) maxEnergy = ifftResults[subband][remove];
//                }
//                System.out.println("MAX:"+maxEnergy);
//            //    Program.performOperationOnSamples(ifftResults[subband], (double)1/Math.ceil(maxEnergy), ArithmeticOperation.MULTIPLY);
//                maxEnergy = 0;
//TODO: DEBUG

//                System.exit(0);
//                try {
//                    Program.convertDoubleArrToByteArr(ifftResults[subband], arr, 0, 0, ifftResults[subband].length, sampleSize,
//                        Program.getMaxAbsoluteValueSigned(sampleSizeInBits), isBigEndian, isSigned);
//
//                    int[] intArr1 = Program.convertBytesToSamples(arr, sampleSize, isBigEndian, isSigned);
//                    int[] intArr2 = Program.convertDoubleArrToIntArr(ifftResults[subband], Program.getMaxAbsoluteValueSigned(sampleSizeInBits), isSigned);
//                    for(int TODO = 0; TODO < intArr1.length; TODO++) {
//                        if(ifftResults[subband][TODO] < 0) System.exit(-666);
//                        if(intArr2[TODO] < 0) System.exit(-667);
//                        if(intArr1[TODO] != intArr2[TODO]) System.out.println(TODO + "\t" + intArr1[TODO] + "\t" + intArr2[TODO]);
//                    }
//                    Program.saveAudio("2WAVE", 22050, sampleSizeInBits, 1, isSigned, isBigEndian, arr, AudioFileFormat.Type.WAVE);
//                }
//                catch(IOException e) { System.exit(-500); }
//                System.exit(arr.length);
                //T

// TODO: Jen debug                for(int i = 0; i < ifftResults[subband].length; i++) if(ifftResults[subband][i] != tmpArray[i]) System.out.println(i + "\t" + ifftResults[subband][i] + "\t" + tmpArray[i]); // T
                fft.realForward(ifftResults[subband]);
////                for(int debug = 0; debug < ifftResults[subband].length; debug++) {
////                    System.out.println(ifftResults[subband].length + "\t" + ifftResults[subband][debug]);
////                }
                Program.getCombFilterEnergies(ifftResults[subband], bpmArrays, energies[subband]);       // adds to the energies
//                System.out.println("!!!!!!!!!!!!!!!!" + subband);
//                for(int debug = 0; debug < energies[subband].length; debug++) {
//                    System.out.println(getBPMFromIndex(bpmStart, bpmJump, debug) + "\t" + energies[subband][debug]);
//                }
//                System.out.println("\n\n\n\n");
            }
        }

        // TODO: A jeste ten nechci volat na cely song ... vypocetne narocny ... melo by se to delat na nejakou 5ti sekundovou cast
//            // TODO: A funguje na mono
//            // TODO: !!!!!!!!!!!!!!
//            getCombFilterEnergyRealForward(fftResult, bpmArrays[i], energies);

        return energies;
    }


    // TODO: Copy paste ... Lisi se to od getBPMUsingCombFilterMONOWithoutFiltersWithoutSubbands jen v tom ze se vola jina metoda a je tam subbandCount
    // TODO: This is not really ideal, using Program ... flawed design
    public default int calculateBPM(int startBPM, int jumpBPM, int upperBoundBPM,
                                    double numberOfSeconds,
                                    int subbandCount, SubbandSplitterIFace splitter,
                                    int numberOfBeats, Program prog) {

        int lenOfOneSecond = prog.sampleSizeInBytes * prog.sampleRate;
        int lenInBytes = (int)(numberOfSeconds * lenOfOneSecond);
        int startIndex = prog.song.length / 2;
        int mod = startIndex % prog.sampleSizeInBytes;
        startIndex += (prog.sampleSizeInBytes - mod);
        int endIndex = startIndex + lenInBytes;
        int bytesOver = endIndex - prog.song.length;
        if(bytesOver > 0) {     // Move to left if the endIndex is after the length of buffer
            startIndex -= bytesOver;
            if(startIndex < 0) {
                return -1;
            }
            endIndex -= bytesOver;
        }

        //  startIndex = (int)(1.1 * lenOfOneSecond);       // TODO:
        //  endIndex = startIndex + lenInBytes;             // TODO:

        int windowSize = (endIndex - startIndex) / prog.sampleSizeInBytes;  // this is Window size in samples
        DoubleFFT_1D fft = new DoubleFFT_1D(windowSize);

        return calculateBPM(startBPM, jumpBPM, upperBoundBPM,
            numberOfSeconds, windowSize, startIndex, endIndex, subbandCount, splitter, fft, numberOfBeats, prog);
    }

    public default int calculateBPM(int startBPM, int jumpBPM, int upperBoundBPM,
                                    double numberOfSeconds, int windowSize,
                                    int startIndex, int endIndex,
                                    int subbandCount, SubbandSplitterIFace splitter,
                                    DoubleFFT_1D fft, int numberOfBeats, Program prog) {
        double[][][] bpmArrays = Program.getBPMArraysFFT(startBPM, upperBoundBPM, jumpBPM, prog.sampleRate, numberOfSeconds, windowSize, numberOfBeats);
        return calculateBPM(bpmArrays, startBPM, jumpBPM, windowSize,
            startIndex, endIndex, subbandCount, splitter, fft, prog);
    }

    public default int calculateBPM(double[][][] bpmArrays, int startBPM,
                                    int jumpBPM, int windowSize,
                                    int startIndex, int endIndex,
                                    int subbandCount, SubbandSplitterIFace splitter,
                                    DoubleFFT_1D fft, Program prog) {
        // TODO: Napsat metodu getBPMFromIndex array to je to to impulse period + ten for cycklus
        // TODO: Vlastne uz to mam napsany vsechno akorat zmensit ty pole a delat fft hned jakmile mam to jedno window
        return calculateBPM(prog.song, bpmArrays, startBPM, jumpBPM,
            prog.sampleSizeInBytes, prog.sampleSizeInBits, windowSize, startIndex, endIndex,
            prog.isBigEndian, prog.isSigned, subbandCount, splitter, fft, prog.sampleRate);
    }
}
