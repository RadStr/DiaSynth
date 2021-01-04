package str.rad.analyzer.bpm;

import str.rad.util.Utilities;
import str.rad.util.audio.AudioConverter;
import str.rad.util.audio.AudioUtilities;

public class BPMSimple {
    private BPMSimple() { }       // Allow only static access


    // windows.length == sampleRate / windowSize
    public static int computeBPM(byte[] samples, int windowSize, double[] windows, int numberOfChannels,
                                 int sampleSize, int frameSize,
                                 int sampleRate, int mask, boolean isBigEndian, boolean isSigned,
                                 int windowsBetweenBeats) {
        final int maxAbsValSigned = AudioUtilities.getMaxAbsoluteValueSigned(8 * sampleSize);

        int beatCount = 0;
        int sampleIndex = 0;
        int i;
        int windowSizeInBytes = windowSize * frameSize;
        int nextSampleIndex = windowSizeInBytes;
        double energySum = 0;
        double energyAvg;
        for (i = 0; i < windows.length; i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
            if (nextSampleIndex < samples.length) {
                windows[i] = computeEnergy(samples, windowSize, numberOfChannels, sampleSize, sampleIndex, mask,
                                           isBigEndian, isSigned, maxAbsValSigned);
                energySum += windows[i];
            }
        }


        double maxValueInEnergy = ((double) windowSize) * maxAbsValSigned * maxAbsValSigned;     // max energy
        double maxValueInVariance = 2 * maxValueInEnergy;           // the val - avg (since avg = -val then it is 2*)
        // It is way to strict (The max variance can be much lower), but I don't see how could I make it more accurate
        maxValueInVariance *= maxValueInVariance;                   // Finally the variance of 1 window (we don't divide by the windows.length since we calculated for just 1 window as I said)
        // Just took 10000 because it worked quite nicely, but not for every sample rate,
        // so we have to multiply it with some value based on that
        double varianceMultFactor = 10000 * Math.pow(3.75, 44100d / sampleRate - 1);

        int windowsFromLastBeat = windowsBetweenBeats;
        int oldestIndexInWindows = 0;
        double currEnergy;
        double variance;
        double coef;
        while (nextSampleIndex < samples.length) {
            energyAvg = energySum / windows.length;
            currEnergy = computeEnergy(samples, windowSize, numberOfChannels, sampleSize, sampleIndex, mask,
                                       isBigEndian, isSigned, maxAbsValSigned);
            variance = Utilities.computeVariance(energyAvg, windows);
            variance /= maxValueInVariance;

            variance *= varianceMultFactor;
            coef = -0.0025714 * variance + 1.8;

            if (currEnergy > coef * energyAvg) {
                if (windowsFromLastBeat >= windowsBetweenBeats) {
                    beatCount++;
                    windowsFromLastBeat = -1;
                }
            }

            // Again optimize the case when windows.length is power of 2
            if (windows.length % 2 == 0) {
                energySum = energySum - windows[oldestIndexInWindows % windows.length] + currEnergy;
                windows[oldestIndexInWindows % windows.length] = currEnergy;
            }
            else {
                if (oldestIndexInWindows >= windows.length) {
                    oldestIndexInWindows = 0;
                }
                energySum = energySum - windows[oldestIndexInWindows] + currEnergy;
                windows[oldestIndexInWindows] = currEnergy;
            }

            oldestIndexInWindows++;
            sampleIndex = nextSampleIndex;
            nextSampleIndex += windowSizeInBytes;
            windowsFromLastBeat++;
        }

        int bpm = BPMUtils.convertBeatsToBPM(beatCount, samples.length, sampleSize, numberOfChannels, sampleRate);
        return bpm;
    }


    private static double computeEnergy(byte[] samples, int windowSize, int numberOfChannels, int sampleSize,
                                        int index, int mask, boolean isBigEndian, boolean isSigned,
                                        int maxAbsoluteValueSigned) {
        double energy = 0;

        for (int i = 0; i < windowSize; i++) {
            for (int j = 0; j < numberOfChannels; j++, index += sampleSize) {
                int val = AudioConverter.convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
                if (!isSigned) {     // Convert unsigned sample to signed
                    val -= maxAbsoluteValueSigned;
                }
                energy += val * (double) val;
            }
        }

        return energy;
    }
}
