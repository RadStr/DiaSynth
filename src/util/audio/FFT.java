package util.audio;

import org.jtransforms.fft.DoubleFFT_1D;

import java.io.IOException;
import java.util.Arrays;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* -------------------------------------------- [START] -------------------------------------------- */
/////////////////// FFT NOTES
/* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Full FFT on n complex values produces FFT result with n complex values

// FFT EXAMPLE: Let's say we have window size of 4 real numbers. When we perform FFT on 4 complex numbers,
// with imaginary part being 0 and the real part being the real numbers of the window. And with sample rate == 100Hz
// Then we get 4 bins [0] == 0Hz, [1] == 25Hz, [2] == 50Hz, [3] == 75Hz, where [0,1,2] are unique values and [3] is [1] mirrored,
// which means the real_part[1] == real_part[3] and imag_part[1] == -imag_part[3].
// So that is WINDOW_SIZE / 2 + 1 are unique values

// If we have only 3 real numbers then it is [0] == 0Hz, [1] == 33.33Hz, [2] == 66.66Hz
// Here values [0,1] are unique, and [2] is [1] mirrored.
// So now we have again WINDOW_SIZE / 2 + 1 unique values.

// If input is even, then there is the middle value which isn't mirrored.
// If input is odd,  then there isn't the middle value.

// The length of the window is in number of complex numbers not total length of array


// Result of real forward FFT by library method:
// From documentation:
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* --------------------------------------------- [END] --------------------------------------------- */
/////////////////// FFT NOTES
/* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


public class FFT {
    private FFT() { }        // To disable instantiation - make only static access possible


    /**
     * Transforms the real and imaginary part to real by taking the distance of the complex number from zero,
     * which is calculated as realPart * realPart + imagPart * imagPart.
     *
     * @param fftResult is 1D double array gotten from fft. On even indexes contains real parts, on odd imaginary ones.
     * @return Returns 1D double array of half length of the original array. The new array contains the distances of complex numbers from zero.
     */
    public static double[] convertResultsOfFFTToRealRealForward(double[] fftResult) {
        double[] result;
        int binCount = getBinCountRealForward(fftResult.length);
        result = new double[binCount];
        convertResultsOfFFTToRealRealForward(fftResult, result);
        return result;
    }

    // TODO: Napsat dokumentaci
    public static void convertResultsOfFFTToRealRealForward(double[] fftResult, double[] result) {
        if (fftResult.length % 2 == 0) {            // It's even
            result[0] = calculateComplexNumMeasure(fftResult[0], 0);
            int index = 1;
            for (int i = 2; i < fftResult.length; i = i + 2) {
                result[index] = calculateComplexNumMeasure(fftResult[i], fftResult[i + 1]);
                index++;
            }
            result[result.length - 1] = calculateComplexNumMeasure(0, fftResult[1]);
        }
        else {
            result[0] = calculateComplexNumMeasure(fftResult[0], 0);
            int index = 1;
            for (int i = 2; i < fftResult.length - 1; i = i + 2) {
                result[index] = calculateComplexNumMeasure(fftResult[i], fftResult[i + 1]);
                index++;
            }

            result[result.length - 1] = calculateComplexNumMeasure(fftResult[fftResult.length - 1], fftResult[1]);
        }
    }


// From documentation (this are the values we want convert to):
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]

    /**
     * This method is inverse to convertResultsOfFFTToRealRealForward, but it doesn't preserve imaginary values,
     * it just puts the measures to the real parts of the array and sets the imaginary part to 0.
     *
     * @param fftMeasures
     * @param result
     * @return
     */
    public static double[] convertFFTAmplitudesToClassicFFTArr(double[] fftMeasures, double[] result) {
        if (result.length % 2 == 0) {            // It's even
            result[0] = Math.sqrt(fftMeasures[0]);
            result[1] = Math.sqrt(fftMeasures[fftMeasures.length - 1]);
            for (int i = 2, j = 2; i < fftMeasures.length; i++, j++) {
                result[j] = Math.sqrt(fftMeasures[i]);
                j++;
                result[j] = 0;
            }
        }
        else {
            result[0] = Math.sqrt(fftMeasures[0]);
            for (int i = 1, j = 2; i < fftMeasures.length; i++, j++) {
                result[j] = Math.sqrt(fftMeasures[i]);
                if (i == fftMeasures.length - 2) {
                    result[1] = 0;
                }
                else {
                    j++;
                    result[j] = 0;
                }
            }
        }

        return result;
    }


// From documentation (this are the values we want convert to):
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]

    /**
     * This method is inverse to convertResultsOfFFTToRealRealForward, but instead of the previous variant, it has
     * both real and imaginary part set in such a way that real part is set by random and imaginary is the remainder
     *
     * @param fftMeasures
     * @param result
     * @return
     */
    public static double[] convertFFTAmplitudesToClassicFFTArrRandom(double[] fftMeasures, double[] result) {
        if (result.length % 2 == 0) {            // It's even
            result[0] = Math.sqrt(fftMeasures[0]);
            result[1] = Math.sqrt(fftMeasures[fftMeasures.length - 1]);
            for (int i = 2, j = 2; i < fftMeasures.length; i++, j++) {
                double real = Math.random() * fftMeasures[i];
                double imag = fftMeasures[i] - real;
                result[j] = Math.sqrt(real);
                j++;
                result[j] = Math.sqrt(imag);
            }
        }
        else {
            result[0] = Math.sqrt(fftMeasures[0]);
            for (int i = 1, j = 2; i < fftMeasures.length; i++, j++) {
                double real = Math.random() * fftMeasures[i];
                double imag = fftMeasures[i] - real;
                result[j] = Math.sqrt(real);
                if (i == fftMeasures.length - 2) {
                    result[1] = Math.sqrt(imag);
                }
                else {
                    j++;
                    result[j] = Math.sqrt(imag);
                }
            }
        }

        return result;
    }


    /**
     * To understand the result take a look at FFT NOTES. Also it is important to understand, that this operation is
     * irreversible because of the integer division.
     *
     * @param windowSize
     * @return
     */
    public static int getBinCountRealForward(int windowSize) {
        int binCount;
        binCount = windowSize / 2 + 1;      // Take a look at the FFT notes to understand why it is windowSize / 2 + 1.
        return binCount;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert complex number to real number methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static interface ComplexToRealIFace {
        double complexToReal(double real, double imag);
    }

    /**
     * Calculates the distance of complex number from 0.
     *
     * @param realPart is the real part of complex number.
     * @param imagPart is the imaginary part of complex number.
     * @return Returns the distance of complex number from 0.
     */
    public static double calculateComplexNumMeasure(double realPart, double imagPart) {
        double result = Math.sqrt(realPart * realPart + imagPart * imagPart);
        return result;
    }

    public static double calculateComplexNumPower(double real, double imag) {
        return real * real + imag * imag;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert complex number to real number methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert real numbers to complex numbers methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void realToComplexRealOnly(double[] real, double[] complex, final boolean shouldSetOtherPartToZero) {
        realToComplexRealOnly(real, 0, real.length, complex, 0, shouldSetOtherPartToZero);
    }

    /**
     * Converts array with real values to array with comples values, which as at [% 2 == 0] real values and everywhere else 0.
     */
    public static void realToComplexRealOnly(double[] real, int realArrLen, double[] complex,
                                             final boolean shouldSetOtherPartToZero) {
        realToComplexRealOnly(real, 0, realArrLen, complex, 0, shouldSetOtherPartToZero);
    }

    public static void realToComplexRealOnly(double[] real, int realStartIndex, int realArrLen,
                                             double[] complex, int complexStartIndex,
                                             final boolean shouldSetOtherPartToZero) {
        for (int r = realStartIndex, c = complexStartIndex; r < realArrLen; r++, c++) {
            complex[c] = real[r];
            c++;
            if (shouldSetOtherPartToZero) {
                complex[c] = 0;
            }
        }
    }


    public static void realToComplexImagOnly(double[] real, double[] complex,
                                             final boolean shouldSetOtherPartToZero) {
        realToComplexImagOnly(real, 0, real.length, complex, 0, shouldSetOtherPartToZero);
    }

    public static void realToComplexImagOnly(double[] real, int realArrLen, double[] complex,
                                             final boolean shouldSetOtherPartToZero) {
        realToComplexImagOnly(real, 0, realArrLen, complex, 0, shouldSetOtherPartToZero);
    }

    public static void realToComplexImagOnly(double[] real, int realStartIndex, int realArrLen,
                                             double[] complex, int complexStartIndex,
                                             final boolean shouldSetOtherPartToZero) {
        for (int r = realStartIndex, c = complexStartIndex; r < realArrLen; r++, c++) {
            if (shouldSetOtherPartToZero) {
                complex[c] = 0;
            }
            c++;
            complex[c] = real[r];
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert real numbers to complex numbers methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert complex numbers to real numbers methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void complexToRealPowers(double[] real, int realArrLen, double[] complex) {
        complexToRealPowers(real, 0, realArrLen, complex, 0);
    }

    // These 2 methods could be solved by using interface method, but as micro micro optimization it is called straight.

    /**
     * Converts complex array to real array by taking power
     *
     * @param real
     * @param realStartIndex
     * @param realArrLen
     * @param complex
     * @param complexStartIndex
     */
    public static void complexToRealPowers(double[] real, int realStartIndex, int realArrLen,
                                           double[] complex, int complexStartIndex) {
        for (int r = realStartIndex, c = complexStartIndex; r < realArrLen; r++, c += 2) {
            real[r] = calculateComplexNumPower(complex[c], complex[c + 1]);
        }
    }

    public static void complexToRealMeasures(double[] real, int realArrLen, double[] complex) {
        complexToRealMeasures(real, 0, realArrLen, complex, 0);
    }

    /**
     * Converts complex array to real array by taking measure
     *
     * @param real
     * @param realStartIndex
     * @param realArrLen
     * @param complex
     * @param complexStartIndex
     */
    public static void complexToRealMeasures(double[] real, int realStartIndex, int realArrLen,
                                             double[] complex, int complexStartIndex) {
        for (int r = realStartIndex, c = complexStartIndex; r < realArrLen; r++, c += 2) {
            real[r] = calculateComplexNumMeasure(complex[c], complex[c + 1]);
        }
    }

    public static void complexToReal(double[] real, int realArrLen, double[] complex,
                                     ComplexToRealIFace action) {
        complexToReal(real, 0, realArrLen, complex, 0, action);
    }


    public static void complexToReal(double[] real, int realStartIndex, int realArrLen,
                                     double[] complex, int complexStartIndex,
                                     ComplexToRealIFace action) {
        for (int r = realStartIndex, c = complexStartIndex; r < realArrLen; r++, c += 2) {
            real[r] = action.complexToReal(complex[c], complex[c + 1]);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert complex numbers to real numbers methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Puts the real and imag part to result array in such way that [i % 2 == 0] contains real part and odd indices imaginary part.
     * So the result array should be at least 2 * real.length. And also the result will have real.length complex numbers.
     * And imaginary part should be the same size as real part.
     * That means that on the results needs to be performed full fft. If the imaginary part is == 0, then the second
     * part is just the mirror of the first one.
     * Note: the result array is always even because in order for it to contain both real and imaginary parts it needs to be even.
     * But the number of complex numbers in result doesn't have to be even.
     * Take look at FFT NOTES to understand how many bins are for each window size.
     */
    public static void connectRealAndImagPart(double[] real, double[] imag, double[] result) {
        int i = 0;
        int partIndex = 0;
        for (; partIndex < real.length; i++, partIndex++) {
            result[i] = real[partIndex];
            i++;
            result[i] = imag[partIndex];
        }
        partIndex--;

        // If the windowSize is even, then the middle element is there only one time, so we don't mirror it.
        if ((result.length / 2) % 2 == 0) {
            partIndex--;
        }
        // > 0 because the 0-th frequency isn't copied
        for (; partIndex > 0; i++, partIndex--) {
            result[i] = real[partIndex];
            i++;
            result[i] = -imag[partIndex]; // - because mirrored
        }
    }


// From documentation (this are the values we want convert to):
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]

    /**
     * Takes realForward fft array and divides it to real and imaginary part. The real and imaginary part should be the
     * same length and the length should be (realForwardFFTArr.length + 1) / 2
     *
     * @param real
     * @param imag
     * @param realForwardFFTArr
     * @param fftArrLen         because the realForwardFFTArr can be longer than the result of fft
     */
    public static void separateRealAndImagPart(double[] real, double[] imag, double[] realForwardFFTArr, int fftArrLen) {
        if (fftArrLen % 2 == 0) {            // It's even
            real[0] = realForwardFFTArr[0];
            imag[0] = 0;
            int index = 1;
            for (int i = 2; i < fftArrLen; i++, index++) {
                real[index] = realForwardFFTArr[i];
                i++;
                imag[index] = realForwardFFTArr[i];
            }

            real[real.length - 1] = realForwardFFTArr[1];
            imag[imag.length - 1] = 0;
        }
        else {
            real[0] = realForwardFFTArr[0];
            imag[0] = 0;
            int index = 1;
            for (int i = 2; i < fftArrLen - 1; i++, index++) {
                real[index] = realForwardFFTArr[i];
                i++;
                imag[index] = realForwardFFTArr[i];
            }

            real[real.length - 1] = realForwardFFTArr[fftArrLen - 1];
            imag[imag.length - 1] = realForwardFFTArr[1];
        }
    }

    // Basically separateRealAndImagPart method without setting the imaginary part
    public static void separateOnlyRealPart(double[] real, double[] realForwardFFTArr, int fftArrLen) {
        if (fftArrLen % 2 == 0) {            // It's even
            real[0] = realForwardFFTArr[0];
            int index = 1;
            for (int i = 2; i < fftArrLen; i += 2, index++) {
                real[index] = realForwardFFTArr[i];
            }

            real[real.length - 1] = realForwardFFTArr[1];
        }
        else {
            real[0] = realForwardFFTArr[0];
            int index = 1;
            for (int i = 2; i < fftArrLen - 1; i += 2, index++) {
                real[index] = realForwardFFTArr[i];
            }

            real[real.length - 1] = realForwardFFTArr[fftArrLen - 1];
        }
    }


    // Basically separateRealAndImagPart method without setting the real part
    public static void separateOnlyImagPart(double[] imag, double[] realForwardFFTArr, int fftArrLen) {
        if (fftArrLen % 2 == 0) {            // It's even
            imag[0] = 0;
            int index = 1;
            for (int i = 3; i < fftArrLen; i += 2, index++) {
                imag[index] = realForwardFFTArr[i];
            }

            imag[imag.length - 1] = 0;
        }
        else {
            imag[0] = 0;
            int index = 1;
            for (int i = 3; i < fftArrLen - 1; i += 2, index++) {
                imag[index] = realForwardFFTArr[i];
            }

            imag[imag.length - 1] = realForwardFFTArr[1];
        }
    }


    /**
     * Method takes the results of FFT, converts them to real number by taking the distances of the complex numbers.
     * Then just adds frequencies to corresponding complex numbers.
     * Calculating the frequency for complex number at index indexOfTheComplexNumber:
     * indexOfTheComplexNumber * sampleRate / totalNumberOfComplexNumbers is the frequency
     * and the distance from 0 of the complex number on that index is the measure.
     *
     * @param fftResult             is 1D double array which contains the result of FFT.
     * @param sampleRate            is the sample rate of the data which were transformed to fftResult parameter.
     * @param returnSortedByMeasure is true if we want the output to be sorted by measure, false otherwise.
     * @return Returns the FrequencyWithMeasure[] which contains the frequencies with corresponding measures.
     * Is sorted if returnSortedByMeasure = true
     */
    public static FrequencyWithMeasure[] convertImagPartToRealReturnArrWithFrequenciesRealForward(double[] fftResult, int sampleRate, boolean returnSortedByMeasure) {
        double[] realResult = convertResultsOfFFTToRealRealForward(fftResult);
        return returnArrWithFrequenciesRealForward(realResult, sampleRate, returnSortedByMeasure);
    }
//

    /**
     * Expects the fftResult to be the distances of complex numbers. So it just adds the frequencies to the distances.
     * Calculating the frequency for complex number at index indexOfTheComplexNumber:
     * indexOfTheComplexNumber * sampleRate / totalNumberOfComplexNumbers is the frequency
     * and the distance from 0 of the complex number on that index is the measure.
     *
     * @param fftResult             is 1D double array which contains the result of FFT.
     * @param sampleRate            is the sample rate of the data which were transformed to fftResult parameter.
     * @param returnSortedByMeasure is true if we want the output to be sorted by measure, false otherwise.
     * @return Returns the FrequencyWithMeasure[] which contains the frequencies with corresponding measures.
     * Is sorted if returnSortedByMeasure = true
     */
    @Deprecated
    public static FrequencyWithMeasure[] returnArrWithFrequenciesRealForward(double[] fftResult, int sampleRate, boolean returnSortedByMeasure) {
        FrequencyWithMeasure[] arr = new FrequencyWithMeasure[fftResult.length];
        int number = arr.length - 1;
        int spaceSize = sampleRate / (number * 2);
        for (int i = 0; i < arr.length; i++) {
            int frequency = i * spaceSize;

            arr[i] = new FrequencyWithMeasure(frequency, fftResult[i]);
        }

        if (returnSortedByMeasure) {
            Arrays.sort(arr);
        }

        return arr;
    }

    // TODO: Ty jmena jsou trochu divny !!!!!!!!!!!!!!!!!!!!!!!
// TODO: Napsat komentare
    public static void calculateIFFTRealForward(double[] samples, boolean scale) {
        DoubleFFT_1D fft = new DoubleFFT_1D(samples.length);
        calculateIFFTRealForward(samples, fft, scale);
    }

    // This method is a bit overkill, but whatever
    public static void calculateIFFTRealForward(double[] samples, DoubleFFT_1D fft, boolean scale) {
        fft.realInverse(samples, scale);
    }

    public static double[] calculateIFFTRealForwardCopyVariant(double[] samples, DoubleFFT_1D fft, boolean scale) {
        double[] copy = Arrays.copyOf(samples, samples.length);
        calculateIFFTRealForward(copy, fft, scale);
        return copy;
    }

    public static double[] calculateIFFTRealForwardCopyVariant(double[] samples, boolean scale) {
        double[] copy = Arrays.copyOf(samples, samples.length);
        calculateIFFTRealForward(copy, scale);
        return copy;
    }


    // TODO: Napsat komentare
    public static void calculateIFFTComplexForward(double[] samples, boolean scale) {
        DoubleFFT_1D fft = new DoubleFFT_1D(samples.length);
        calculateIFFTComplexForward(samples, fft, scale);
    }

    // This method is a bit overkill, but whatever
    public static void calculateIFFTComplexForward(double[] samples, DoubleFFT_1D fft, boolean scale) {
        fft.complexInverse(samples, scale);
    }

    public static double[] calculateIFFTComplexForwardCopyVariant(double[] samples, DoubleFFT_1D fft, boolean scale) {
        double[] copy = Arrays.copyOf(samples, samples.length);
        calculateIFFTComplexForward(copy, fft, scale);
        return copy;
    }

    public static double[] calculateIFFTComplexForwardCopyVariant(double[] samples, boolean scale) {
        double[] copy = Arrays.copyOf(samples, samples.length);
        calculateIFFTComplexForward(copy, scale);
        return copy;
    }


    // TODO: Hloupost ... tohle dava smysl u FFT ... ale tak je mozny ze to nekdy vyuziju, takze muzu nechat
    public static double[] calculateIFFTComplexForwardProlongArray(double[] samples, boolean scale) {
        double[] result = Arrays.copyOf(samples, samples.length * 2);
        DoubleFFT_1D fft = new DoubleFFT_1D(samples.length);
        calculateIFFTComplexForward(result, fft, scale);
        return result;
    }

    // TODO: Hloupost ... tohle dava smysl u FFT ... ale tak je mozny ze to nekdy vyuziju, takze muzu nechat
    public static double[] calculateIFFTComplexForwardProlongArray(double[] samples, DoubleFFT_1D fft, boolean scale) {
        double[] result = Arrays.copyOf(samples, samples.length * 2);
        calculateIFFTComplexForward(result, fft, scale);
        return result;
    }


    public static double[] calculateFFTRealForward(double[] samples, int startIndex, int len, int numberOfChannels,
                                                   int resultArrayLen, int fftSize) {
        double[] result = new double[resultArrayLen];
        calculateFFTRealForward(samples, startIndex, len, numberOfChannels, result, fftSize);
        return result;
    }

    public static double[] calculateFFTRealForward(double[] samples, int startIndex, int len, int numberOfChannels, int fftSize) {
        return calculateFFTRealForward(samples, startIndex, len, numberOfChannels, fftSize, fftSize);
    }

    public static double[] calculateFFTRealForward(double[] samples, int startIndex, int len, int numberOfChannels,
                                                   DoubleFFT_1D fft, int resultArrayLen) {
        double[] result = new double[resultArrayLen];
        calculateFFTRealForward(samples, startIndex, len, numberOfChannels, fft, result);
        return result;
    }

    ////////////////////
    public static void calculateFFTRealForward(double[] samples, int startIndex, int len, int numberOfChannels,
                                               double[] result, int fftSize) {
        if (result.length < fftSize) {
            return;
        }
        DoubleFFT_1D fft = new DoubleFFT_1D(fftSize);
        calculateFFTRealForward(samples, startIndex, len, numberOfChannels, fft, result);
    }

    public static void calculateFFTRealForward(double[] samples, int startIndex, int len,
                                               int numberOfChannels, DoubleFFT_1D fft, double[] result) {
        for (int i = 0; i < len; i++, startIndex += numberOfChannels) {
            result[i] = samples[startIndex];
        }
        fft.realForward(result);
    }

    //////////////////////
    //////////////////////
    // MaxAbsoluteValue is result of getMaxAbsoluteValueSigned

    public static double[] calculateFFTRealForward(int[] samples, int startIndex, int numberOfChannels,
                                                   int maxAbsoluteValue, boolean isSigned,
                                                   int resultArrayLen, int fftSize) {
        double[] result = new double[resultArrayLen];
        calculateFFTRealForward(samples, startIndex, numberOfChannels, result, maxAbsoluteValue, isSigned, fftSize);
        return result;
    }

    public static double[] calculateFFTRealForward(int[] samples, int startIndex, int numberOfChannels,
                                                   int maxAbsoluteValue, boolean isSigned, int fftSize) {
        return calculateFFTRealForward(samples, startIndex, numberOfChannels, maxAbsoluteValue, isSigned, fftSize, fftSize);
    }

    public static double[] calculateFFTRealForward(int[] samples, int startIndex, int numberOfChannels,
                                                   DoubleFFT_1D fft, int maxAbsoluteValue,
                                                   boolean isSigned, int resultArrayLen) {
        double[] result = new double[resultArrayLen];
        calculateFFTRealForward(samples, startIndex, numberOfChannels, fft, result, maxAbsoluteValue, isSigned);
        return result;
    }

    public static void calculateFFTRealForward(int[] samples, int startIndex, int numberOfChannels, double[] result,
                                               int maxAbsoluteValue, boolean isSigned, int fftSize) {
        DoubleFFT_1D fft = new DoubleFFT_1D(fftSize);
        calculateFFTRealForward(samples, startIndex, numberOfChannels, fft, result, maxAbsoluteValue, isSigned);
    }

    public static void calculateFFTRealForward(int[] samples, int startIndex, int numberOfChannels, DoubleFFT_1D fft,
                                               double[] result, int maxAbsoluteValue, boolean isSigned) {
        for (int i = 0; i < result.length; i++, startIndex += numberOfChannels) {
            result[i] = AudioConverter.normalizeToDouble(samples[startIndex], maxAbsoluteValue, isSigned);
        }
        fft.realForward(result);
    }

    //////////////////////
    //////////////////////
    public static double[] calculateFFTRealForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize, int frameSize, int mask,
                                                   int maxAbsoluteValue, boolean isBigEndian, boolean isSigned, int resultArrayLen, int fftSize) {
        double[] result = new double[resultArrayLen];
        calculateFFTRealForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask,
                                result, maxAbsoluteValue, isBigEndian, isSigned, fftSize);
        return result;
    }

    public static double[] calculateFFTRealForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize,
                                                   int frameSize, int mask, int maxAbsoluteValue, boolean isBigEndian,
                                                   boolean isSigned, int fftSize) {
        return calculateFFTRealForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, maxAbsoluteValue,
                                       isBigEndian, isSigned, fftSize, fftSize);
    }

    public static double[] calculateFFTRealForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize,
                                                   int frameSize, int mask,
                                                   DoubleFFT_1D fft, int maxAbsoluteValue, boolean isBigEndian,
                                                   boolean isSigned, int resultArrayLen) {
        double[] result = new double[resultArrayLen];
        calculateFFTRealForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, fft, result, maxAbsoluteValue,
                                isBigEndian, isSigned);
        return result;
    }

    public static void calculateFFTRealForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize, int frameSize, int mask,
                                               double[] result, int maxAbsoluteValue, boolean isBigEndian, boolean isSigned, int fftSize) {
        DoubleFFT_1D fft = new DoubleFFT_1D(fftSize);
        calculateFFTRealForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, fft, result, maxAbsoluteValue,
                                isBigEndian, isSigned);
    }

    public static void calculateFFTRealForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize,
                                               int frameSize, int mask,
                                               DoubleFFT_1D fft,
                                               double[] result, int maxAbsoluteValue, boolean isBigEndian, boolean isSigned) {
        int valInt;
        for (int i = 0; i < result.length; i++, startIndex += numberOfChannels * frameSize) {
            valInt = AudioConverter.convertBytesToInt(samples, sampleSize, mask, startIndex, isBigEndian, isSigned);
            result[i] = AudioConverter.normalizeToDouble(valInt, maxAbsoluteValue, isSigned);
        }

        fft.realForward(result);
    }


    public static void calculateFFTRealForward(double[] samples, int startIndex, double[] result, int fftLen) {
        DoubleFFT_1D fft = new DoubleFFT_1D(fftLen);
        calculateFFTRealForward(samples, startIndex, fft, result);
    }

    public static void calculateFFTRealForward(double[] samples, int startIndex, DoubleFFT_1D fft, double[] result) {
        System.arraycopy(samples, startIndex, result, 0, result.length);
        fft.realForward(result);
    }

    public static double[] calculateFFTComplexForward(double[] samples, int startIndex, int numberOfChannels, int resultArrayLen, int fftSize) {
        double[] result = new double[resultArrayLen];
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, result, fftSize);
        return result;
    }

    public static double[] calculateFFTComplexForward(double[] samples, int startIndex, int numberOfChannels, int fftSize) {
        return calculateFFTComplexForward(samples, startIndex, numberOfChannels, 2 * fftSize, fftSize);
    }

    public static double[] calculateFFTComplexForward(double[] samples, int startIndex, int numberOfChannels, DoubleFFT_1D fft, int resultArrayLen) {
        double[] result = new double[resultArrayLen];
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, fft, result);
        return result;
    }

    ////////////////////
    public static void calculateFFTComplexForward(double[] samples, int startIndex, int numberOfChannels, double[] result, int fftSize) {
        if (result.length < fftSize) {
            return;
        }
        DoubleFFT_1D fft = new DoubleFFT_1D(fftSize);
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, fft, result);
    }

    public static void calculateFFTComplexForward(double[] samples, int startIndex, int numberOfChannels, DoubleFFT_1D fft, double[] result) {
        for (int i = 0; i < result.length; i++, startIndex += numberOfChannels - 1) {
            result[i++] = samples[startIndex++];
            result[i] = samples[startIndex];
        }
        fft.realForward(result);
    }

    //////////////////////
    //////////////////////
    public static double[] calculateFFTComplexForward(int[] samples, int startIndex, int numberOfChannels, int maxAbsoluteValue,
                                                      boolean isSigned, int resultArrayLen, int fftSize) {
        double[] result = new double[resultArrayLen];
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, result, maxAbsoluteValue, isSigned, fftSize);
        return result;
    }

    public static double[] calculateFFTComplexForward(int[] samples, int startIndex, int numberOfChannels, int maxAbsoluteValue,
                                                      boolean isSigned, int fftSize) {
        return calculateFFTComplexForward(samples, startIndex, numberOfChannels, maxAbsoluteValue, isSigned, 2 * fftSize, fftSize);
    }

    public static double[] calculateFFTComplexForward(int[] samples, int startIndex, int numberOfChannels, DoubleFFT_1D fft,
                                                      int maxAbsoluteValue, boolean isSigned, int resultArrayLen) {
        double[] result = new double[resultArrayLen];
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, fft, result, maxAbsoluteValue, isSigned);
        return result;
    }

    public static void calculateFFTComplexForward(int[] samples, int startIndex, int numberOfChannels, double[] result, int maxAbsoluteValue, boolean isSigned,
                                                  int fftSize) {
        DoubleFFT_1D fft = new DoubleFFT_1D(fftSize);
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, fft, result, maxAbsoluteValue, isSigned);
    }

    public static void calculateFFTComplexForward(int[] samples, int startIndex, int numberOfChannels, DoubleFFT_1D fft,
                                                  double[] result, int maxAbsoluteValue, boolean isSigned) {
        for (int i = 0; i < result.length; i++, startIndex += numberOfChannels - 1) {
            result[i++] = AudioConverter.normalizeToDouble(samples[startIndex], maxAbsoluteValue, isSigned);
            startIndex++;
            result[i] = AudioConverter.normalizeToDouble(samples[startIndex], maxAbsoluteValue, isSigned);
        }
        fft.realForward(result);
    }

    //////////////////////
    //////////////////////
    public static double[] calculateFFTComplexForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize, int frameSize, int mask,
                                                      int maxAbsoluteValue, boolean isBigEndian, boolean isSigned, int resultArrayLen, int fftSize) {
        double[] result = new double[resultArrayLen];
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, result, maxAbsoluteValue,
                                   isBigEndian, isSigned, fftSize);
        return result;
    }

    public static double[] calculateFFTComplexForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize, int frameSize, int mask,
                                                      int maxAbsoluteValue, boolean isBigEndian, boolean isSigned, int fftSize) {
        return calculateFFTComplexForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, maxAbsoluteValue,
                                          isBigEndian, isSigned, 2 * fftSize, fftSize);
    }

    public static double[] calculateFFTComplexForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize,
                                                      int frameSize, int mask,
                                                      DoubleFFT_1D fft,
                                                      int maxAbsoluteValue, boolean isBigEndian, boolean isSigned, int resultArrayLen) {
        double[] result = new double[resultArrayLen];
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, fft, result, maxAbsoluteValue,
                                   isBigEndian, isSigned);
        return result;
    }

    public static void calculateFFTComplexForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize, int frameSize, int mask,
                                                  double[] result, int maxAbsoluteValue, boolean isBigEndian, boolean isSigned, int fftSize) {
        DoubleFFT_1D fft = new DoubleFFT_1D(fftSize);
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, fft, result, maxAbsoluteValue,
                                   isBigEndian, isSigned);
    }

    public static void calculateFFTComplexForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize,
                                                  int frameSize, int mask,
                                                  DoubleFFT_1D fft,
                                                  double[] result, int maxAbsoluteValue, boolean isBigEndian, boolean isSigned) {
        int valInt;
        for (int i = 0; i < result.length; i++, startIndex += (numberOfChannels - 1) * frameSize) {
            valInt = AudioConverter.convertBytesToInt(samples, sampleSize, mask, startIndex, isBigEndian, isSigned);
            result[i++] = AudioConverter.normalizeToDouble(valInt, maxAbsoluteValue, isSigned);
            startIndex += frameSize;
            valInt = AudioConverter.convertBytesToInt(samples, sampleSize, mask, startIndex, isBigEndian, isSigned);
            result[i] = AudioConverter.normalizeToDouble(valInt, maxAbsoluteValue, isSigned);
        }
        fft.realForward(result);
    }


    public static void calculateFFTComplexForward(double[] samples, int startIndex, double[] result, int fftLen) {
        DoubleFFT_1D fft = new DoubleFFT_1D(fftLen);
        calculateFFTComplexForward(samples, startIndex, fft, result);
    }

    public static void calculateFFTComplexForward(double[] samples, int startIndex, DoubleFFT_1D fft, double[] result) {
        System.arraycopy(samples, startIndex, result, 0, result.length);
        fft.realForward(result);
    }

    /**
     * Takes song parts performs fft on them, converts the complex numbers from result to real numbers by taking the distance from 0. Connects these numbers to corresponding frequencies.
     * Distance is measure of that frequency in the song part.
     * Returns null if the input songParts array is null, or if any of the song part if the array is null
     *
     * @param songParts   are the song parts together with average amplitude, song part is represented by byte array.
     * @param sampleSize  is the size of one sample
     * @param sampleRate  is the sample rate of the audio
     * @param isBigEndian true if the samples are in big endian, false otherwise.
     * @param isSigned    true if the samples are signed numbers, false otherwise.
     * @return Returns 2D array containing the measures with frequencies. 1 array = measures and frequencies for 1 song part.
     * @throws IOException is thrown when sample size is invalid
     */
    public static FrequencyWithMeasure[][] calculateFFTRealForward(SongPartWithAverageValueOfSamples[] songParts, int sampleSize, int sampleRate, boolean isBigEndian, boolean isSigned) throws IOException {
        FrequencyWithMeasure[][] results = new FrequencyWithMeasure[songParts.length][];
        if (songParts == null || songParts[0].songPart == null) {
            return null;
        }
        DoubleFFT_1D fft = new DoubleFFT_1D(songParts[0].songPart.length / sampleSize);
        for (int i = 0; i < songParts.length; i++) {
            if (songParts[i].songPart == null) {
                return null;
            }
            int[] intSamples = AudioConverter.convertBytesToSamples(songParts[i].songPart, sampleSize, isBigEndian, isSigned);
            double[] normalizedSongPart = AudioConverter.normalizeToDoubles(intSamples, sampleSize * 8, isSigned);
            fft.realForward(normalizedSongPart);
            FrequencyWithMeasure[] frequenciesWithMeasures = convertImagPartToRealReturnArrWithFrequenciesRealForward(normalizedSongPart, sampleRate, false);
            results[i] = frequenciesWithMeasures;
        }

        return results;
    }


    /**
     * Calculates FFT for audio with multiple channels. For each channels calculates the fft separately, the complex numbers in result
     * are then transformed to real numbers by taking the distances of complex numbers from 0. Then the results of fft of each channel are put together by averaging.
     * At the end connects frequencies with corresponding measures (the averages).
     *
     * @param songParts  is 2D array with song parts, 1 array is one channel. First dim are channels.
     *                   Second dim are the samples (song parts) of channels. The song parts are 1D double labelReferenceArrs, where each element is normalized sample (value between -1 and 1).
     * @param sampleRate is the sample rate of the audio.
     * @return Returns FrequencyWithMeasure[][] where 1 FrequencyWithMeasure[] represents the measures and frequencies of the song part.
     */
    public FrequencyWithMeasure[][] calculateFFTRealForward(NormalizedSongPartWithAverageValueOfSamples[][] songParts, int sampleRate) {
        FrequencyWithMeasure[][] results = new FrequencyWithMeasure[songParts[0].length][];
        double[][] values = new double[songParts.length][];
        for (int k = 0; k < songParts[0].length; k++) {
            for (int i = 0; i < songParts.length; i++) {
                double[] normalizedSongPart = new double[songParts[i][k].songPart.length];
                DoubleFFT_1D fft = new DoubleFFT_1D(normalizedSongPart.length);
                for (int j = 0; j < normalizedSongPart.length; j++) {
                    normalizedSongPart[j] = songParts[i][k].songPart[j];
                }
                fft.realForward(normalizedSongPart);
                values[i] = convertResultsOfFFTToRealRealForward(normalizedSongPart);
            }

            for (int f = 0; f < values[0].length; f++) {
                for (int l = 1; l < values.length; ) {
                    values[0][f] += values[l][f];
                }
                values[0][f] = values[0][f] / values.length;
            }
            FrequencyWithMeasure[] frequenciesWithMeasures = returnArrWithFrequenciesRealForward(values[0], sampleRate, false);
            results[k] = frequenciesWithMeasures;
        }

        return results;
    }


    /**
     * Calculates fft for each song part, the complex numbers in result are then transformed to real numbers by taking the distances of complex numbers from 0.
     * Then connects frequencies with corresponding measures (the distances from 0).
     *
     * @param songParts  is 1D array with song parts. The song parts are 1D double labelReferenceArrs, where each element is normalized sample (value between -1 and 1).
     * @param sampleRate is the sample rate of the audio.
     * @return Returns FrequencyWithMeasure[][] where 1 FrequencyWithMeasure[] represents the measures and frequencies of the song part.
     * @throws IOException is thrown when some song part is not divisible by 2 (that way it can't store complex numbers)
     */
    public FrequencyWithMeasure[][] calculateFFTRealForward(NormalizedSongPartWithAverageValueOfSamples[] songParts, int sampleRate) throws IOException {
        FrequencyWithMeasure[][] results = new FrequencyWithMeasure[songParts.length][];        // because real and imag part

        for (int i = 0; i < songParts.length; i++) {
            double[] normalizedSongPart = new double[songParts[i].songPart.length];
            DoubleFFT_1D fft = new DoubleFFT_1D(normalizedSongPart.length);
            for (int j = 0; j < normalizedSongPart.length; j++) {
                normalizedSongPart[j] = songParts[i].songPart[j];
            }

            fft.realForward(normalizedSongPart);
            FrequencyWithMeasure[] frequenciesWithMeasures = convertImagPartToRealReturnArrWithFrequenciesRealForward(normalizedSongPart, sampleRate, false);
            results[i] = frequenciesWithMeasures;
        }

        return results;
    }


    /**
     * Input samples are expected in mono (1 channel).
     * Memory efficient variant, we convert byte samples right in to the FFT output.
     * If last part of samples doesn't fill whole window then return that part is not added to the output!!!
     *
     * @param samples          is the array with input samples. Audio is expected to be mono.
     * @param sampleSize       is the size of 1 sample in bytes.
     * @param sampleSizeInBits is the size of 1 sample in bits
     * @param sampleRate       is the sample rate of input samples.
     * @param windowSize       is the size of windows to perform FFT
     * @param isBigEndian      is true if the samples are in big endian, false otherwise.
     * @param isSigned         is true if the samples are signed, is false otherwise.
     * @return Returns frequencies with measures 2D array.
     * @throws IOException if sampleSize is <=0 or >4
     */
    public static FrequencyWithMeasure[][] calculateFFTRealForward(byte[] samples, int sampleSize, int sampleSizeInBits,
                                                                   int sampleRate, int windowSize,
                                                                   boolean isBigEndian, boolean isSigned) throws IOException {
        int windowSizeInBytes = windowSize * sampleSize;
        FrequencyWithMeasure[][] results = new FrequencyWithMeasure[samples.length / windowSizeInBytes][];
        DoubleFFT_1D fft = new DoubleFFT_1D(windowSize);

        for (int index = 0, i = 0; i < results.length; i++, index += windowSizeInBytes) {
            double[] arr = AudioConverter.normalizeToDoubles(samples, sampleSize, sampleSizeInBits,
                                                             index, windowSize, isBigEndian, isSigned);
            fft.realForward(arr);
            FrequencyWithMeasure[] frequenciesWithMeasures = convertImagPartToRealReturnArrWithFrequenciesRealForward(arr,
                                                                                                                      sampleRate, false);
            results[i] = frequenciesWithMeasures;
        }

        return results;
    }


    /**
     * Input samples are expected in mono (1 channel).
     * Memory efficient variant, we convert byte samples right in to the FFT output.
     * If last part of samples doesn't fill whole window then return that part is not added to the output!!!
     *
     * @param samples          is the array with input samples. Audio is expected to be mono.
     * @param sampleSize       is the size of 1 sample in bytes.
     * @param sampleSizeInBits is the size of 1 sample in bits
     * @param windowSize       is the size of windows to perform FFT
     *                         * @param startIndex is the index where should the fft calculations start.
     *                         * @param endIndex is the where should the fft calculations end.
     * @param isBigEndian      is true if the samples are in big endian, false otherwise.
     * @param isSigned         is true if the samples are signed, is false otherwise.
     * @return Returns the fft results of windowSize in 2D double array ... that means we lose information about frequencies.
     * @throws IOException if sampleSize is <=0 or >4
     */
    public static double[][] calculateFFTRealForward(byte[] samples, int sampleSize, int sampleSizeInBits,
                                                     int windowSize, int startIndex, int endIndex,
                                                     boolean isBigEndian, boolean isSigned,
                                                     DoubleFFT_1D fft) throws IOException {
        int windowSizeInBytes = windowSize * sampleSize;
        int len = endIndex - startIndex;
        double[][] results = new double[len / windowSizeInBytes][];

        for (int index = startIndex, i = 0; i < results.length; i++) {
            double[] arr = new double[windowSize];
            index = AudioConverter.normalizeToDoubles(samples, arr, sampleSize, sampleSizeInBits, index, isBigEndian, isSigned);
            fft.realForward(arr);
            results[i] = arr;
        }

        return results;
    }


    /**
     * Input samples are expected in mono (1 channel).
     * Memory efficient variant, we convert byte samples right in to the FFT output.
     * If last part of samples doesn't fill whole window then return that part is not added to the output!!!
     *
     * @param samples          is the array with input samples. Audio is expected to be mono.
     * @param sampleSize       is the size of 1 sample in bytes.
     * @param sampleSizeInBits is the size of 1 sample in bits
     * @param windowSize       is the size of windows to perform FFT
     * @param startIndex       is the index where should the fft calculations start.
     * @param endIndex         is the where should the fft calculations end.
     * @param isBigEndian      is true if the samples are in big endian, false otherwise.
     * @param isSigned         is true if the samples are signed, is false otherwise.
     * @return Returns only measures in 2D double array ... that means we lose information about frequencies.
     * @throws IOException if sampleSize is <=0 or >4
     */
    public static double[][] calculateFFTRealForwardOnlyMeasures(byte[] samples, int sampleSize, int sampleSizeInBits,
                                                                 int windowSize, int startIndex, int endIndex,
                                                                 boolean isBigEndian,
                                                                 boolean isSigned) throws IOException {
        int windowSizeInBytes = windowSize * sampleSize;
        int len = endIndex - startIndex;
        double[][] results = new double[len / windowSizeInBytes][];
        DoubleFFT_1D fft = new DoubleFFT_1D(windowSize);
        double[] arr = new double[windowSize];

        for (int index = startIndex, i = 0; i < results.length; i++) {
            index = AudioConverter.normalizeToDoubles(samples, arr, sampleSize, sampleSizeInBits, index, isBigEndian, isSigned);
            fft.realForward(arr);
            results[i] = convertResultsOfFFTToRealRealForward(arr);
        }

        return results;
    }


    /**
     * Takes n frequencies with highest measures. The result is in descending order - first is the frequency with highest measure
     * and then is followed by frequency with lower measure, etc.
     *
     * @param arr         is 1D array where each elements contains the frequency and measure of that frequency.
     * @param n           is the number of frequencies to be returned.
     * @param arrIsSorted is true if the array arr is already sorted, otherwise is false, so it needs to be sorted.
     * @return Returns n frequencies with highest measure. In descending order.
     * @throws IOException is thrown if the n is larger than size of given array.
     */
    public static FrequencyWithMeasure[] takeNFreqsWithHighestMeasure(FrequencyWithMeasure[] arr, int n,
                                                                      boolean arrIsSorted) throws IOException {
        if (n > arr.length) {
            throw new IOException();
        }
        if (!arrIsSorted) {
            Arrays.sort(arr);
        }

        FrequencyWithMeasure[] result = new FrequencyWithMeasure[n];

        int index = arr.length - 1;
        for (int i = 0; i < result.length; i++) {
            result[i] = arr[index];
            index--;
        }

        return result;
    }


    /**
     * For all song parts: Takes n frequencies with highest measures of that song part. The result is in song part descending order - first is the frequency with highest measure
     * and then is followed by frequency with lower measure, etc.
     *
     * @param arr         is 2D array, where each array represents the frequencies and measures for 1 song part.
     * @param n           is the number of frequencies to be returned for 1 song part.
     * @param arrIsSorted is true if ale the labelReferenceArrs in arr are already sorted, otherwise is false, so they need to be sorted.
     * @return Returns for all song parts n frequencies with highest measures (The measures for each song part are in ascending order).
     * @throws IOException is thrown if the n is larger than size of given array.
     */
    public FrequencyWithMeasure[][] takeNFreqsWithHighestMeasureForAllSongParts(FrequencyWithMeasure[][] arr, int n, boolean arrIsSorted) throws IOException {
        FrequencyWithMeasure[][] result = new FrequencyWithMeasure[arr.length][n];
        for (int i = 0; i < arr.length; i++) {
            result[i] = takeNFreqsWithHighestMeasure(arr[i], n, arrIsSorted);
        }

        return result;
    }


    /**
     * For all song parts: Takes n frequencies with highest measures of that song part. The result is in song part ascending order - first is the frequency with lowest measure
     * and then is followed by frequency with higher measure, etc.
     *
     * @param arr         is 2D array, where each array represents the frequencies and measures for 1 song part.
     * @param n           is the number of frequencies to be returned for 1 song part.
     * @param arrIsSorted is true if ale the labelReferenceArrs in arr are already sorted, otherwise is false, so they need to be sorted.
     * @return Returns for all song parts n frequencies with highest measures (The measures for each song part are in ascending order).
     * @throws IOException is thrown if the n is larger than size of given array.
     */
    public FrequencyWithMeasure[][] takeNFreqsWithLowestMeasureForAllSongParts(FrequencyWithMeasure[][] arr, int n, boolean arrIsSorted) throws IOException {
        FrequencyWithMeasure[][] result = new FrequencyWithMeasure[arr.length][n];
        for (int i = 0; i < arr.length; i++) {
            result[i] = takeNFreqsWithLowestMeasure(arr[i], n, arrIsSorted);
        }

        return result;
    }


    /**
     * Takes n frequencies with lowest measures. The result is in ascending order - first is the frequency with lowest measure
     * and then is followed by frequency with higher measure, etc.
     *
     * @param arr         is 1D array where each elements contains the frequency and measure of that frequency.
     * @param n           is the number of frequencies to be returned.
     * @param arrIsSorted is true if the array arr is already sorted, otherwise is false, so it needs to be sorted.
     * @return Returns n frequencies with lowest measures. In ascending order.
     * @throws IOException is thrown if the n is larger than size of given array.
     */
    public FrequencyWithMeasure[] takeNFreqsWithLowestMeasure(FrequencyWithMeasure[] arr, int n, boolean arrIsSorted) throws IOException {
        if (n > arr.length) {
            throw new IOException();
        }
        if (!arrIsSorted) {
            Arrays.sort(arr);
        }

        FrequencyWithMeasure[] result = new FrequencyWithMeasure[n];
        for (int i = 0; i < result.length; i++) {
            result[i] = arr[i];
        }

        return result;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convolution methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Convolution is like polynomial multiplication

    public static double[] convolutionInFreqDomainRealForwardFull(double[] fftResult, double[] bpmArray) {
        double[] result = new double[fftResult.length];
        convolutionInFreqDomainRealForward(fftResult, bpmArray, result);
        return result;
    }

    public static void convolutionInFreqDomainRealForwardFull(double[] fftResult, double[] bpmArray, double[] result) {
        double real;
        double imag;
        for (int i = 0; i < fftResult.length; ) {
            real = fftResult[i] * bpmArray[i] - fftResult[i + 1] * bpmArray[i + 1];
            imag = fftResult[i] * bpmArray[i + 1] + fftResult[i + 1] * bpmArray[i];
            result[i++] = real;
            result[i++] = imag;
        }
    }


// From documentation:
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]

    /**
     * "Mono" version
     *
     * @param arr1
     * @param arr2
     * @param result
     */
    public static void convolutionInFreqDomainRealForward(double[] arr1, double[] arr2, double[] result) {
        double real;
        double imag;
        if (arr1.length % 2 == 0) {            // It's even
            real = arr1[0] * arr2[0];
            result[0] = real;
            real = arr1[1] * arr2[1];
            result[1] = real;
            for (int i = 2; i < arr1.length; ) {
                real = arr1[i] * arr2[i] - arr1[i + 1] * arr2[i + 1];
                imag = arr1[i] * arr2[i + 1] + arr1[i + 1] * arr2[i];
                result[i++] = real;
                result[i++] = imag;
            }
        }
        else {
            real = arr1[0] * arr2[0];
            result[0] = real;
            for (int i = 2; i < arr1.length - 1; ) {
                real = arr1[i] * arr2[i] - arr1[i + 1] * arr2[i + 1];
                imag = arr1[i] * arr2[i + 1] + arr1[i + 1] * arr2[i];
                result[i++] = real;
                result[i++] = imag;
            }
            real = arr1[arr1.length - 1] * arr2[arr1.length - 1] - arr1[1] * arr2[1];
            imag = arr1[arr1.length - 1] * arr2[1] + arr1[1] * arr2[arr2.length - 1];
            result[result.length - 1] = real;
            result[1] = imag;
        }
    }


// From documentation:
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]

    /**
     * "Mono" version. Rewritten the specific version to be more general.
     *
     * @param arr1
     * @param arr1StartIndex
     * @param arr2
     * @param arr2StartIndex
     * @param result
     * @param resultStartIndex
     * @param convolutionLen
     */
    public static void convolutionInFreqDomainRealForward(double[] arr1, int arr1StartIndex,
                                                          double[] arr2, int arr2StartIndex,
                                                          double[] result, int resultStartIndex, int convolutionLen) {
        double real;
        double imag;
        if (arr1.length % 2 == 0) {            // It's even
            real = arr1[arr1StartIndex++] * arr2[arr2StartIndex++];
            result[resultStartIndex++] = real;
            real = arr1[arr1StartIndex++] * arr2[arr2StartIndex++];
            result[resultStartIndex++] = real;
            for (int i = 2; i < convolutionLen; i += 2, arr1StartIndex += 2, arr2StartIndex += 2) {
                real = arr1[arr1StartIndex] * arr2[arr2StartIndex] - arr1[arr1StartIndex + 1] * arr2[arr2StartIndex + 1];
                imag = arr1[arr1StartIndex] * arr2[arr2StartIndex + 1] + arr1[arr1StartIndex + 1] * arr2[arr2StartIndex];
                result[resultStartIndex++] = real;
                result[resultStartIndex++] = imag;
            }
        }
        else {
            real = arr1[arr1StartIndex++] * arr2[arr2StartIndex++];
            result[resultStartIndex++] = real;
            int resultIndex = resultStartIndex + 1;
            int arr1Index = arr1StartIndex + 1;
            int arr2Index = arr2StartIndex + 1;

            for (int i = 2; i < convolutionLen - 1; i += 2, arr1Index += 2, arr2Index += 2) {
                real = arr1[arr1Index] * arr2[arr2Index] - arr1[arr1Index + 1] * arr2[arr2Index + 1];
                imag = arr1[arr1Index] * arr2[arr2Index + 1] + arr1[arr1Index + 1] * arr2[arr2Index];
                result[resultIndex++] = real;
                result[resultIndex++] = imag;
            }
            real = arr1[arr1Index] * arr2[arr2Index] - arr1[arr1StartIndex] * arr2[arr2StartIndex];
            imag = arr1[arr1Index] * arr2[arr2StartIndex] + arr1[arr1StartIndex] * arr2[arr2Index];
            result[resultIndex] = real;
            result[resultStartIndex] = imag;
        }
    }


    /**
     * "Mono" version
     *
     * @param fftResult
     * @param bpmArray
     * @return
     */
    public static double[] convolutionInFreqDomainRealForward(double[] fftResult, double[] bpmArray) {
        double[] result = new double[fftResult.length];
        convolutionInFreqDomainRealForward(fftResult, bpmArray, result);
        return result;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convolution methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
