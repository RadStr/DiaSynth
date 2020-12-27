package analyzer.bpm;

import util.audio.wave.ByteWave;
import org.jtransforms.fft.DoubleFFT_1D;
import util.Rectification;
import util.audio.AudioConverter;
import util.audio.FFT;
import util.audio.FFTWindow;
import util.audio.filter.NonRecursiveFilter;

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


    // his is version for mono signal
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
    public default int computeBPM(byte[] samples, double[][][] bpmArrays, int bpmStart, int bpmJump,
                                  int sampleSize, int sampleSizeInBits, int windowSize, int startIndex, int endIndex,
                                  boolean isBigEndian, boolean isSigned, int subbandCount, SubbandSplitterIFace splitter,
                                  DoubleFFT_1D fft, int sampleRate) {
        double[][] energies;
        energies = computeEnergies(samples, bpmArrays, sampleSize, sampleSizeInBits,
            windowSize, startIndex, endIndex, isBigEndian, isSigned, subbandCount, splitter, fft, sampleRate);

        if(energies == null) {
            return -1;
        }


        return calculateBPMFromEnergies(energies, bpmStart, bpmJump, bpmArrays.length);
    }




    public default double[][] computeEnergies(byte[] samples, double[][][] bpmArrays, int sampleSize, int sampleSizeInBits,
                                              int windowSize, int startIndex, int endIndex, boolean isBigEndian, boolean isSigned,
                                              int subbandCount, SubbandSplitterIFace splitter, DoubleFFT_1D fft, int sampleRate) {
        double[][] energies = new double[subbandCount][bpmArrays.length];
        double hahnWindowSizeInSecs = 0.4;
        // This is *2 the value from the source material, because I calculate the hahn window a bit differently -
        // I checked it against the implementation from the source materials and the results are the same now (+/- double error)
        int hahnWindowSize = (int) (hahnWindowSizeInSecs * sampleRate * 2);
        double[] fftRightSideOfHahnWindow = FFTWindow.getHahnWindowWithLimit(windowSize, hahnWindowSize / 2, hahnWindowSize);
        fft.realForward(fftRightSideOfHahnWindow);

        double[] ifftResult = new double[windowSize];
        double[] coefsForFilter = new double[]{-1, 1};

        for (int index = startIndex; index < endIndex; ) {
            double[] fftResult = new double[windowSize];
            try {
                index = AudioConverter.normalizeToDoubles(samples, fftResult, sampleSize, sampleSizeInBits,
                                                          index, isBigEndian, isSigned);
            } catch (IOException e) {
                return null;
            }
            fft.realForward(fftResult);

            double[][] ifftResults = getIFFTBasedOnSubbands(fftResult, subbandCount, fft, splitter);
            for (int subband = 0; subband < ifftResults.length; subband++) {
                Rectification.fullWaveRectificationDouble(ifftResults[subband], true);
                fft.realForward(ifftResults[subband]);
                FFT.convolutionInFreqDomainRealForward(fftRightSideOfHahnWindow, ifftResults[subband], ifftResults[subband]);
                FFT.calculateIFFTRealForward(ifftResults[subband], fft, true);
                NonRecursiveFilter.performNonRecursiveFilter(ifftResults[subband], 0, coefsForFilter,
                                                            1, ifftResult, 0, ifftResult.length);
                System.arraycopy(ifftResult, 0, ifftResults[subband], 0, ifftResult.length);
                Rectification.halfWaveRectificationDouble(ifftResults[subband], true);
                fft.realForward(ifftResults[subband]);
                computeEnergies(ifftResults[subband], bpmArrays, energies[subband]);       // Adds to the energies
            }
        }

        return energies;
    }


    /**
     * The ByteWave has to be in mono otherwise Integer.MIN_VALUE is returned.
     * And has to have the byte buffer loaded, otherwise -2 is returned.
     * @param startBPM
     * @param jumpBPM
     * @param upperBoundBPM
     * @param numberOfSeconds
     * @param subbandCount
     * @param splitter
     * @param numberOfBeats
     * @param byteWave
     * @return
     */
    public default int computeBPM(int startBPM, int jumpBPM, int upperBoundBPM,
                                  double numberOfSeconds,
                                  int subbandCount, SubbandSplitterIFace splitter,
                                  int numberOfBeats, ByteWave byteWave) {
        if(byteWave.getNumberOfChannels() != 1) {
            return Integer.MIN_VALUE;
        }
        int songLen = byteWave.getSongLen();
        if(songLen <= 0) {
            return -2;
        }
        int sampleSizeInBytes = byteWave.getSampleSizeInBytes();
        int lenOfOneSecond = byteWave.calculateSizeOfOneSec();
        int lenInBytes = (int)(numberOfSeconds * lenOfOneSecond);
        int startIndex = songLen / 2;
        int mod = startIndex % sampleSizeInBytes;
        startIndex += (sampleSizeInBytes - mod);
        int endIndex = startIndex + lenInBytes;
        int bytesOver = endIndex - songLen;
        if(bytesOver > 0) {     // Move to left if the endIndex is after the length of buffer
            startIndex -= bytesOver;
            if(startIndex < 0) {
                return -1;
            }
            endIndex -= bytesOver;
        }

        int windowSize = (endIndex - startIndex) / sampleSizeInBytes;  // this is Window size in samples
        DoubleFFT_1D fft = new DoubleFFT_1D(windowSize);

        return computeBPM(startBPM, jumpBPM, upperBoundBPM, numberOfSeconds, windowSize,
                          startIndex, endIndex, subbandCount, splitter, fft, numberOfBeats, byteWave);
    }

    public default int computeBPM(int startBPM, int jumpBPM, int upperBoundBPM,
                                  double numberOfSeconds, int windowSize,
                                  int startIndex, int endIndex,
                                  int subbandCount, SubbandSplitterIFace splitter,
                                  DoubleFFT_1D fft, int numberOfBeats, ByteWave byteWave) {
        double[][][] bpmArrays = createBPMArraysFFT(startBPM, upperBoundBPM, jumpBPM, byteWave.getSampleRate(),
                                                    numberOfSeconds, windowSize, numberOfBeats);
        return computeBPM(bpmArrays, startBPM, jumpBPM, windowSize,
                          startIndex, endIndex, subbandCount, splitter, fft, byteWave);
    }

    public default int computeBPM(double[][][] bpmArrays, int startBPM,
                                  int jumpBPM, int windowSize,
                                  int startIndex, int endIndex,
                                  int subbandCount, SubbandSplitterIFace splitter,
                                  DoubleFFT_1D fft, ByteWave byteWave) {
        return computeBPM(byteWave.getSong(), bpmArrays, startBPM, jumpBPM,
                byteWave.getSampleSizeInBytes(), byteWave.getSampleSizeInBits(), windowSize,
                startIndex, endIndex, byteWave.getIsBigEndian(), byteWave.getIsSigned(),
                subbandCount, splitter, fft, byteWave.getSampleRate());
    }




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Compute comb filter energies - static methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static double computeEnergyRealForward(double[][] fftResults, double[][] bpmArray) {
        double energy = 0;
        for(int i = 0; i < fftResults.length; i++) {
            energy += computeEnergyRealForward(fftResults[i], bpmArray[i]);
        }

        return energy;
    }

    static void computeEnergies(double[] fftResult, double[][][] bpmArray, double[] energies) {
        for (int i = 0; i < bpmArray.length; i++) {
            for(int j = 0; j < bpmArray[i].length; j++) {
                energies[i] += computeEnergyRealForward(fftResult, bpmArray[i][j]);
            }
        }
    }

    static double computeEnergyRealForwardFull(double[] fftResult, double[] bpmArray) {
        double real;
        double imag;
        double energy = 0;

        for(int i = 0; i < fftResult.length; i = i + 2) {
            real = fftResult[i] * bpmArray[i] - fftResult[i+1] * bpmArray[i+1];
            real *= real;
            imag = fftResult[i] * bpmArray[i+1] + fftResult[i+1] * bpmArray[i];
            imag *= imag;
            energy += real + imag;
        }

        return energy;
    }



// From documentation:
////	if n is even then
////	 a[2*k] = Re[k], 0<=k<n/2
////	 a[2*k+1] = Im[k], 0<k<n/2
////	 a[1] = Re[n/2]
////
////
////	if n is odd then
////	 a[2*k] = Re[k], 0<=k<(n+1)/2
////	 a[2*k+1] = Im[k], 0<k<(n-1)/2
////	 a[1] = Im[(n-1)/2]
    /**
     * We don't save the results, only calculate energy, which is equal to sum of measures of the convolution result.
     * @param fftResult
     * @param bpmArray
     * @return
     */
    static double computeEnergyRealForward(double[] fftResult, double[] bpmArray) {
        double energy;
        double real;
        double imag;
        if(fftResult.length % 2 == 0) {			// It's even
            real = fftResult[0] * bpmArray[0];
            energy = FFT.calculateComplexNumMeasure(real, 0);
            real = fftResult[1] * bpmArray[1];
            energy += FFT.calculateComplexNumMeasure(real, 0);
            for(int i = 2; i < fftResult.length; i = i + 2) {
                real = fftResult[i] * bpmArray[i] - fftResult[i+1] * bpmArray[i+1];
                imag = fftResult[i] * bpmArray[i+1] + fftResult[i+1] * bpmArray[i];
                energy += FFT.calculateComplexNumMeasure(real, imag);
            }
        } else {
            real = fftResult[0] * bpmArray[0];
            energy = FFT.calculateComplexNumMeasure(real, 0);
            for(int i = 2; i < fftResult.length - 1; i = i + 2) {
                real = fftResult[i] * bpmArray[i] - fftResult[i+1] * bpmArray[i+1];
                imag = fftResult[i] * bpmArray[i+1] + fftResult[i+1] * bpmArray[i];
                energy += FFT.calculateComplexNumMeasure(real, imag);
            }

            real =  fftResult[fftResult.length - 1] * bpmArray[fftResult.length - 1] - fftResult[1] * bpmArray[1];
            imag = fftResult[fftResult.length - 1] * bpmArray[1] + fftResult[1] * bpmArray[fftResult.length - 1];
            energy += FFT.calculateComplexNumMeasure(real, imag);
        }

        return energy;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Compute comb filter energies - static methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// createBPMArrays methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the FFT results of the generated BPM arrays. The first dimension is equal to the number of BPM arrays.
     * Second dimension is based on the length of BPM array (computed internally) and the fftWindowSize. It responds to
     * the number of FFT's performed and the last dimension are the FFT measures. ... the method is not used anymore
     * @param lowerBoundBPM
     * @param upperBoundBPM
     * @param jumpBPM
     * @param sampleRate
     * @param numberOfSeconds
     * @param fftWindowSize
     * @param numberOfBeats
     * @return
     */
    static double[][][] createBPMArraysFFTMeasures(int lowerBoundBPM, int upperBoundBPM, int jumpBPM, int sampleRate,
                                                   double numberOfSeconds, int fftWindowSize, int numberOfBeats) {
        if(upperBoundBPM < lowerBoundBPM) {
            return null;
        }
        int arrayCount = 1 + (upperBoundBPM - lowerBoundBPM) / jumpBPM;
        int arrayLen = (int)(sampleRate * numberOfSeconds);
        DoubleFFT_1D fft = new DoubleFFT_1D(fftWindowSize);
        int fftWindowsCount = arrayLen / fftWindowSize;     // TODO: Maybe should solve special case when fftWindowsCount == 0
        double[][][] bpmFFTArrays = new double[arrayCount][fftWindowsCount][];
        double[] fftArr = new double[fftWindowSize];

        int impulsePeriod;
        for(int i = 0, currBPM = lowerBoundBPM; i < bpmFFTArrays.length; i++, currBPM += jumpBPM) {
            int totalIndexInBpm = 0;
            impulsePeriod = (60 * sampleRate) / currBPM;
            int beatCount = 0;
            for(int j = 0; j < fftWindowsCount; j++) {
                for (int k = 0; k < fftArr.length; k++, totalIndexInBpm++) {
                    if(beatCount < numberOfBeats) {
                        if ((totalIndexInBpm % impulsePeriod) == 0) {
                            fftArr[k] = 1;
                            beatCount++;
                        }
                        else {
                            fftArr[k] = 0;
                        }
                    }
                    else {
                        fftArr[k] = 0;
                    }
                }

                fft.realForward(fftArr);
                bpmFFTArrays[i][j] = FFT.convertResultsOfFFTToRealRealForward(fftArr);
            }
        }

        return bpmFFTArrays;
    }

    /**
     * Returns the FFT results of the generated BPM arrays. The first dimension is equal to the number of BPM arrays.
     * Second dimension is based on the length of BPM array (computed internally) and the fftWindowSize. It responds to
     * the number of FFT's performed and the last dimension are the FFT results.
     * @param lowerBoundBPM
     * @param upperBoundBPM
     * @param jumpBPM
     * @param sampleRate
     * @param numberOfSeconds
     * @param fftWindowSize
     * @param numberOfBeats
     * @return
     */
    static double[][][] createBPMArraysFFT(int lowerBoundBPM, int upperBoundBPM, int jumpBPM, int sampleRate,
                                           double numberOfSeconds, int fftWindowSize, int numberOfBeats) {
        if(upperBoundBPM < lowerBoundBPM) {
            return null;
        }


        int arrayCount = 1 + (upperBoundBPM - lowerBoundBPM) / jumpBPM;
        int arrayLen = (int)(sampleRate * numberOfSeconds);
        DoubleFFT_1D fft = new DoubleFFT_1D(fftWindowSize);
        int fftWindowsCount = arrayLen / fftWindowSize;     // TODO: Maybe solve special case when fftWindowsCount == 0
        double[][][] bpmFFTArrays = new double[arrayCount][fftWindowsCount][];

        int impulsePeriod;
        for(int i = 0, currBPM = lowerBoundBPM; i < bpmFFTArrays.length; i++, currBPM += jumpBPM) {
            int totalIndexInBpm = 0;
            impulsePeriod = (60 * sampleRate) / currBPM;
            int beatCount = 0;
            for(int j = 0; j < fftWindowsCount; j++) {
                double[] fftArr = new double[fftWindowSize];
                for (int k = 0; k < fftArr.length; k++, totalIndexInBpm++) {
                    int mod = totalIndexInBpm % impulsePeriod;
                    if(beatCount < numberOfBeats) {
                        if (mod == 0) {
                            fftArr[k] = 1;
                            beatCount++;
                        }
                    }
                    else {
                        break;
                    }
                }

                fft.realForward(fftArr);
                bpmFFTArrays[i][j] = fftArr;
            }
        }

        return bpmFFTArrays;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// createBPMArrays methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// GetBPMArrays methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// GetBPMArrays methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// IFFT on sub-bands - static methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static double[][] getIFFTBasedOnSubbands(double[] fftResult, int subbandCount, DoubleFFT_1D fft,
                                             SubbandSplitterIFace splitter) {
        double[][] result = new double[subbandCount][fftResult.length];
        getIFFTBasedOnSubbands(fftResult, subbandCount, fft, splitter, result);
        return result;
    }

    static void getIFFTBasedOnSubbands(double[] fftResult, int subbandCount, DoubleFFT_1D fft,
                                       SubbandSplitterIFace splitter, double[][] result) {
        for(int subband = 0; subband < subbandCount; subband++) {
            splitter.getSubband(fftResult, subbandCount, subband, result[subband]);
            FFT.calculateIFFTRealForward(result[subband], fft, true);
        }
    }

    static void getIFFTBasedOnSubband(double[] fftResult, int subbandCount, int subband, DoubleFFT_1D fft,
                                      SubbandSplitterIFace splitter, double[] result) {
        splitter.getSubband(fftResult, subbandCount, subband, result);
        FFT.calculateIFFTRealForward(result, fft, true);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// IFFT on sub-bands - static methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
