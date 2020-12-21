package analyzer.bpm;

import org.jtransforms.fft.DoubleFFT_1D;
import util.Utilities;
import util.audio.FFT;

public class BPMSimpleWithFreqBands {
    private BPMSimpleWithFreqBands() {}     // Allow only static access

    /**
     *
     * @param samples
     * @param sampleSize
     * @param sampleRate
     * @param windowSize is the size of the FFT window.
     * @param isBigEndian
     * @param isSigned
     * @param mask is the mas for the given sampleSize
     * @param maxAbsoluteValue is the max absolute value for the given audio format
     * @param fft
     * @param splitter is the splitter to be used
     * @param subbandEnergies 1dim are past values, 2dim are the sub-bands - for example new double[historySubbandsCount][subbandCount];
     * @param coef is the coefficient which will be used in algorithm
     * @param windowsBetweenBeats is the minimum number of window between registered beats.
     * @param varianceLimit is the varianceLimit which we use inside algorithm
     * @return
     */
    public static int computeBPM(byte[] samples, int sampleSize, int sampleRate,
                                 int windowSize, boolean isBigEndian, boolean isSigned,
                                 int mask, int maxAbsoluteValue, DoubleFFT_1D fft,
                                 SubbandSplitterIFace splitter,
                                 double[][] subbandEnergies, double coef,
                                 int windowsBetweenBeats, double varianceLimit) {
        double divFactor = 1;
        // Has to be done because, the lower the sample rate, the lower needs to be the coefficient
        if(sampleRate < 44100) {
            double log = Utilities.logarithm((44100 / (double) sampleRate) - 1, 2.36);
            divFactor = 1 + 0.3 * (log + 1);
        }
        double coefBasedOnSampleRate = coef / divFactor;



        int numberOfChannels = 1;
        int frameSize = sampleSize;
        int windowsFromLastBeat = windowsBetweenBeats;
        int subbandCount = subbandEnergies[0].length;
        int historySubbandsCount = subbandEnergies.length;
        double[] fftArr = new double[windowSize];
        double[] measuresArr = new double[FFT.getBinCountRealForward(windowSize)];


        int beatCount = 0;
        int sampleIndex = 0;
        int i;
        int windowSizeInBytes = windowSize * sampleSize;
        int nextSampleIndex = windowSizeInBytes;
        double[] energySums = new double[subbandCount];
        double[] currEnergies = new double[subbandCount];
        for(i = 0; i < subbandEnergies.length;
            i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
            if(nextSampleIndex < samples.length) {
                computeSubbandEnergiesUsingFFT(samples, subbandEnergies[i], sampleIndex,
                                               numberOfChannels, sampleSize, frameSize,
                                               mask, fft, fftArr, measuresArr,
                                               maxAbsoluteValue, isBigEndian, isSigned, splitter);
                for(int j = 0; j < subbandEnergies[i].length; j++) {
                    energySums[j] += subbandEnergies[i][j];
                }
            }
        }

        double avg;


        int oldestIndexInSubbands = 0;
        while(nextSampleIndex < samples.length) {
            boolean hasBeat = false;
            computeSubbandEnergiesUsingFFT(samples, currEnergies, sampleIndex, numberOfChannels,
                                           sampleSize, frameSize, mask, fft, fftArr, measuresArr,
                                           maxAbsoluteValue, isBigEndian, isSigned, splitter);
            int j = 0;
            for(; j < currEnergies.length; j++) {
                avg = energySums[j] / historySubbandsCount;
                double variance = computeVariance(avg, subbandEnergies, j);

                if (currEnergies[j] > coefBasedOnSampleRate * avg) {
                    if(variance > varianceLimit) {
                        if(windowsFromLastBeat >= windowsBetweenBeats) {
                            beatCount++;
                            windowsFromLastBeat = -1;
                            hasBeat = true;
                            break;
                        }
                    }
                }
                updateEnergySumsAndSubbands(j, oldestIndexInSubbands, energySums, currEnergies[j], subbandEnergies);
            }

            if(hasBeat) {
                for (; j < currEnergies.length; j++) {
                    updateEnergySumsAndSubbands(j, oldestIndexInSubbands, energySums, currEnergies[j], subbandEnergies);
                }
            }

            oldestIndexInSubbands++;
            sampleIndex = nextSampleIndex;
            nextSampleIndex += windowSizeInBytes;
            windowsFromLastBeat++;


            // Again optimize the case when windows.length is power of 2
            if (historySubbandsCount % 2 == 0) {
                oldestIndexInSubbands %= historySubbandsCount;
            } else {
                if (oldestIndexInSubbands >= historySubbandsCount) {
                    oldestIndexInSubbands = 0;
                }
            }
        }

        int bpm = BPMUtils.convertBeatsToBPM(beatCount, samples.length, sampleSize, numberOfChannels, sampleRate);
        return bpm;
    }

    private static double computeVariance(double average, double[][] values, int subbandIndex) {
        double variance = 0;
        double val;
        for(int i = 0; i < values.length; i++) {
            val = values[i][subbandIndex] - average;
            variance += val*val;
        }

        return variance / values.length;
    }

    public static void computeSubbandEnergiesUsingFFT(byte[] samples, double[] currEnergies,
                                                      int startIndex,
                                                      int numberOfChannels,
                                                      int sampleSize,
                                                      int frameSize,
                                                      int mask,
                                                      DoubleFFT_1D fft,
                                                      double[] fftArray, double[] fftArrayMeasures,
                                                      int maxAbsoluteValue,
                                                      boolean isBigEndian,
                                                      boolean isSigned,
                                                      SubbandSplitterIFace splitter) {
        FFT.calculateFFTRealForward(samples, startIndex, numberOfChannels, sampleSize, frameSize,
                                    mask, fft, fftArray, maxAbsoluteValue, isBigEndian, isSigned);

        FFT.convertResultsOfFFTToRealRealForward(fftArray, fftArrayMeasures);
        for(int subband = 0; subband < currEnergies.length; subband++) {
            currEnergies[subband] = splitter.getSubbandEnergy(fftArrayMeasures, currEnergies.length, subband);
        }
    }

    /**
     *
     * @param subbandInd
     * @param oldestIndexInSubbands should already be in range from 0 to energySums.length (== subbandCount)
     * @param energySums
     * @param currEnergy
     * @param subbandEnergies
     */
    private static void updateEnergySumsAndSubbands(int subbandInd, int oldestIndexInSubbands, double[] energySums,
                                                    double currEnergy, double[][] subbandEnergies) {
        energySums[subbandInd] += -subbandEnergies[oldestIndexInSubbands][subbandInd] + currEnergy;
        subbandEnergies[oldestIndexInSubbands][subbandInd] = currEnergy;
    }
}
