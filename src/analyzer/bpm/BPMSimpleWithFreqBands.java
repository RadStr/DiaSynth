package analyzer.bpm;

import org.jtransforms.fft.DoubleFFT_1D;
import test.ProgramTest;
import util.Utilities;
import util.audio.FFT;

import java.io.IOException;

public class BPMSimpleWithFreqBands {
    private BPMSimpleWithFreqBands() {}     // Allow only static access

    public static int calculateBPMSimpleWithFreqBands(byte[] samples, int sampleSize, int sampleRate,
                                                      int windowSize, boolean isBigEndian, boolean isSigned,
                                                      int mask, int maxAbsoluteValue, DoubleFFT_1D fft, SubbandSplitterIFace splitter,
                                                      double[][] subbandEnergies, // TODO: 1D are the past values, 2D are the subbands
                                                      double coef, int windowsBetweenBeats, double varianceLimit
    ) throws IOException { // TODO: Predpokladam ,ze subbandEnergies uz je alokovany pole o spravny velikosti
        // TODO: REMOVE
        final double oldCoef = coef;      // TODO: OLD COEF
        double todoMaxEnergy = -1;
        // TODO: REMOVE



        //                double coefBasedOnSampleRate = coef / 1.3;//Math.pow(1., 44100 / (double)sampleRate - 1);

        double divFactor = 1;
        if(sampleRate < 44100) {
            // TODO: REMOVE
//                    divFactor = 1 + 0.3 * ((44100 / (double) sampleRate) - 1);
//                    divFactor = 1.825;
            // TODO: REMOVE

            double log = Utilities.logarithm((44100 / (double) sampleRate) - 1, 2.36);
            divFactor = 1 + 0.3 * (log + 1);

// TODO: REMOVE
//                    double log = Program.logGeneral((44100 / (double) sampleRate) - 1, 1.5);
//                    divFactor = 2;
//                    divFactor = 1.5;
//                    divFactor = 1 + 0.49 * (log + 1);
// TODO: REMOVE
        }
        double coefBasedOnSampleRate = coef / divFactor;        // Has to be done because, the lower the sample rate, the lower needs to be the coefficient

/*
        int beatCount = 0;
        double fft;
        int windowSizeInBytes = sampleSize * windowSize;        // TODO: * frameSize
        for(i = 0; i < windows.length; i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
            if(nextSampleIndex < samples.length) {
                windows[i] = getEnergy(samples, windowSize, numberOfChannels, sampleSize, sampleIndex, mask,
                    isBigEndian, isSigned);
                avg += windows[i];
            }
        }

        for (int index = 0; index < samples.length; index += jumpInBytes) {

            // TODO: Tahle metoda vypocita jen cast FFT o dane velikosti (tedy vraci pole doublu)
            // TODO: V obecnem pripade tahle metoda bude bud taky vracet double[] s tim ze proste vezme hodnoty
            // TODO: kanalu (pres preskakovani tj numberOfChannels * sampleSize je dalsi index ktery mam vzit)
            // TODO: nebo proste tu metodu udelat tak aby vratila double[][] kde to bude double[numberOfChannels][windowSize]
            // TODO: takze tam musim dat index
//            double[] fft = calculateFFTOnlyMeasuresGetOnlyOnePart(samples, sampleSize, sampleSizeInBits, windowSize, isBigEndian, isSigned);
            // TODO: !!!!!!!!! Tak jeste jinak ... rovnou spocitam energie tech subbandu ... zase jen v ty jedny casti
            // TODO: !!!!!!!!! Pro vic kanalu zase musim pres double[][]
            double[] subbandEnergies = getSubbandEnergiesUsingFFT(...); // TODO: !!!! Zase to delat spis jen pres referenci

        }
        return beatCount;
 */


// TODO:
        int numberOfChannels = 1;
        int frameSize = sampleSize;
// TODO:
// TODO:        double varianceLimit = 0;     // TODO:
        int windowsFromLastBeat = windowsBetweenBeats;
        int subbandCount = subbandEnergies[0].length;
        int historySubbandsCount = subbandEnergies.length;
        double[] fftArr = new double[windowSize];

        // TODO: Zbytecny staci aby to pole melo polovicni velikost (viz kod pod tim)
//double[] measuresArr = new double[windowSize];        // TODO: Muzu pouzit fftArr jako measuresArr, ale takhle to je prehlednesji a navic pak chci ty vysledky fft ulozit do souboru abych to uz nemusel pocitat
        double[] measuresArr = new double[FFT.getBinCountRealForward(windowSize)];


        int beatCount = 0;
        int sampleIndex = 0;
        int i;
        int windowSizeInBytes = windowSize * sampleSize;     // TODO: frameSize v vice multichannel variante
        int nextSampleIndex = windowSizeInBytes;
        //TODO: Asi zase predat jako argument ... tady je to pole protoze kazdy subband ma vlastni average
        double[] energySums = new double[subbandCount];  // TODO: Pro vice kanalove to bude double[][]
        double[] currEnergies = new double[subbandCount];
        for(i = 0; i < subbandEnergies.length; // TODO: U multi-channel varianty to bude subbandEnergies[0].length
            i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
            if(nextSampleIndex < samples.length) {
                // TODO: Vymazat ten startIndex
                computeSubbandEnergiesUsingFFT(samples, subbandEnergies[i], sampleIndex,//int startIndex,
                    numberOfChannels, sampleSize, frameSize, mask, fft, fftArr, measuresArr,
                    maxAbsoluteValue, isBigEndian, isSigned, splitter);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy
// TODO:                subbandEnergies[i] = currEnergies;
                for(int j = 0; j < subbandEnergies[i].length; j++) {
                    energySums[j] += subbandEnergies[i][j];
                }
            }
        }

        double avg;


        int oldestIndexInSubbands = 0;
        while(nextSampleIndex < samples.length) {
            // TODO: BPM NOVY
            boolean hasBeat = false;
            // TODO: BPM NOVY
            // TODO: Ten startIndex pod timhle dat pryc
            computeSubbandEnergiesUsingFFT(samples, currEnergies, sampleIndex,//int startIndex,
                numberOfChannels, sampleSize, frameSize, mask, fft, fftArr, measuresArr,
                maxAbsoluteValue, isBigEndian, isSigned, splitter);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy
            //            currEnergies = getSubbandEnergiesUsingFFT(...);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy

            // This is version for Constant splitter The commented coef = 2.5 ... is for logaritmic, but the version with constant seems to work very good
            int j = 0;
            for(; j < currEnergies.length; j++) {
                todoMaxEnergy = Math.max(currEnergies[j], todoMaxEnergy);       // TODO: Finding the difference in coefs

                avg = energySums[j] / historySubbandsCount; // TODO:
                double variance = computeVariance(avg, subbandEnergies, j);
                // TODO: OLD - REMOVE
//                coef = 3;
//                    coef = 6;
                // TODO: OLD - REMOVE

         //       coef = 2.5 + 10000 * variance; For logarithmic with subbandCount == 32 and that version doesn't contain the if with varianceLimit
//                System.out.println(currEnergies[j] + ":\t" + avg + ":\t" + (coef * avg));

                // TODO: DEBUG
//                if(variance > 150) {
//                    ProgramTest.debugPrint("Variance >150:", variance);
//                }
//                if(energySums[j] > 50) {
//                    ProgramTest.debugPrint("energy >50:", energySums[j]);
//                }
//
//                ProgramTest.debugPrint("Variance:", variance);
//                ProgramTest.debugPrint("energy:", energySums[j]);
                // TODO: DEBUG


                // TODO: ENERGIE TED
//                variance *= 5000;
//                coef = oldCoef - variance * (0.0025714 / 2);

//                coef = oldCoef - variance;

                // Code from BPM Simple
//                variance *= 10000;
//                coef = -0.0025714 * variance + 1.8;
                // Code from BPM Simple

                // Modified Code from BPM Simple
//                variance *= 5000;
//                coef = -0.0025714 * variance + 3.6;
//                coef = 10;

//                coef = 3;
                // Modified Code from BPM Simple
                // TODO: ENERGIE TED

                // TODO: DEBUG
                // TODO: Tady beru ze kdyz je beat na libovolnym mistem - pak typicky budu chtit brat beaty jen z urcitych frekvencnich pasem
                if (currEnergies[j] > coefBasedOnSampleRate * avg) {
//                if (currEnergies[j] > coef / Math.max(1, (((44100 / (double)sampleRate) - 1)) * 1) * avg) {
                    // TODO: DEBUG
//                    System.out.println("---------------" + variance);
                    // TODO: DEBUG
                    // TODO: not used anymore - the variance just doesn't seem to work.
//                    double varianceLimit = 0.0000001;
//                    varianceLimit = 1;
//                    varianceLimit = 20;
//                    varianceLimit = 40;
//                    varianceLimit = 75;
//                    varianceLimit = 150;
//                    varianceLimit = 250;
//                    varianceLimit = 300;
                    // TODO: not used anymore - the variance just doesn't seem to work.

/*// TODO: K nicemu, lepsi je mit varianci zahrnutou v tom coef                   */ if(variance > varianceLimit) {
    // TODO: BPM NOVY
//    if(!hasBeat) {
//        beatCount++;
//        hasBeat = true;
//    }
    ////////////
                        if(windowsFromLastBeat >= windowsBetweenBeats) {
//                            System.out.println(sampleIndex + ":\t" + j + ":\t" + samples.length);
                            beatCount++;
                            windowsFromLastBeat = -1;
                            hasBeat = true;
                            break;
/*// TODO:                        */}
    // TODO: BPM NOVY
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
            if (historySubbandsCount % 2 == 0) {       // TODO: U multi-channel verze chci subbandEnegies[i].length
                oldestIndexInSubbands %= historySubbandsCount; // TODO: U multi-channel verze chci subbandEnegies[i].length
            } else {
                if (oldestIndexInSubbands >= historySubbandsCount) { // TODO: U multi-channel verze chci subbandEnegies[i].length
                    oldestIndexInSubbands = 0;
                }
            }
        }

        ProgramTest.debugPrint("MAX_ENERGY:", todoMaxEnergy);
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
        FFT.calculateFFTRealForward(samples, startIndex, numberOfChannels, sampleSize,
                frameSize, mask, fft, fftArray, maxAbsoluteValue, isBigEndian, isSigned);


        // TODO: NORMALIZACE
//        for(int i = 0; i < fftArray.length; i++) {
//            fftArray[i] /= (fftArray.length / 2);
//        }
        // TODO: NORMALIZACE



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
