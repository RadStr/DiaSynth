package util;

import synthesizer.synth.generators.classic.phase.SineGenerator;

import java.io.File;
import java.util.Random;

public class Utilities {
    private Utilities() {
    }      // Allow only static access


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Fill array with values methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void resetTwoDimArr(double[][] arr, int startIndex, int endIndex) {
        setTwoDimArr(arr, startIndex, endIndex, 0);
    }

    public static void setTwoDimArr(double[][] arr, int startIndex, int endIndex, double value) {
        for (int ch = 0; ch < arr.length; ch++) {
            setOneDimArr(arr[ch], startIndex, endIndex, value);
        }
    }

    // Modified code from https://stackoverflow.com/questions/9128737/fastest-way-to-set-all-values-of-an-array
    /*
     * initialize a smaller piece of the array and use the System.arraycopy
     * call to fill in the rest of the array in an expanding binary fashion
     */
    public static void setOneDimArr(double[] array, int startIndex, int endIndex, double value) {
        int len = endIndex - startIndex;
        array[startIndex] = value;

        //Value of i will be [1, 2, 4, 8, 16, 32, ..., len]
        for (int i = 1, outIndex = startIndex + 1; i < len; outIndex += i, i += i) {
            System.arraycopy(array, startIndex, array, outIndex, ((len - i) < i) ? (len - i) : i);
        }
    }

    public static void setOneDimArrWithCheck(double[] array, int startIndex, int endIndex, double value) {
        if (endIndex > startIndex) {
            int len = endIndex - startIndex;
            array[startIndex] = value;

            //Value of i will be [1, 2, 4, 8, 16, 32, ..., len]
            for (int i = 1, outIndex = startIndex + 1; i < len; outIndex += i, i += i) {
                System.arraycopy(array, startIndex, array, outIndex, ((len - i) < i) ? (len - i) : i);
            }
        }
    }

    public static enum CURVE_TYPE {
        SINE {
            public double[] createCurve(int len, double amp, double freq, int sampleRate, double phase) {
                return SineGenerator.createSine(len, amp, freq, sampleRate, phase);
            }
        },
        LINE {
            public double[] createCurve(int len, double amp, double freq, int sampleRate, double phase) {
                double[] line = new double[len];
                setOneDimArr(line, 0, line.length, amp);
                return line;
            }
        },
        RANDOM {
            public double[] createCurve(int len, double amp, double freq, int sampleRate, double phase) {
                double[] arr = new double[len];
                fillArrWithRandomValues(arr, amp);
                return arr;
            }
        };

        /**
         * Fills array with values based on given parameters. Based on curve some parameters may be ignored.
         *
         * @param len
         * @param amp
         * @param freq
         * @param sampleRate
         * @param phase
         * @return
         */
        public abstract double[] createCurve(int len, double amp, double freq, int sampleRate, double phase);
    }

    public static void fillArrWithRandomValues(double[] arr, double amplitude) {
        Random r = new Random();

        for (int j = 0; j < arr.length; j++) {
            arr[j] = r.nextDouble();
            arr[j] *= amplitude;
            if (r.nextDouble() > 0.5) {
                arr[j] = -arr[j];
            }
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Fill array with values methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Array copying methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creates new array of length originalArrLen * copyCount which contains first originalArrLen indices of array arr and they are contained in the result copyCount times.
     *
     * @param arr
     * @param originalArrLen
     * @param copyCount
     * @return
     */
    public static double[] copyArr(double[] arr, int originalArrLen, int copyCount) {
        double[] result = new double[originalArrLen * copyCount];
        copyArr(arr, originalArrLen, result, copyCount);
        return result;
    }


    /**
     * Copies the first originalArrLen indices copyCount times to resultArr.
     *
     * @param arr
     * @param originalArrLen
     * @param resultArr
     * @param copyCount
     */
    public static void copyArr(double[] arr, int originalArrLen, double[] resultArr, int copyCount) {
        for (int i = 0, c = 0; c < copyCount; c++, i += originalArrLen) {
            System.arraycopy(arr, 0, resultArr, i, originalArrLen);
        }
    }

    /**
     * The method takes first len indices of array arr and copies them until end of array is reached.
     * arr.length % len == 0, otherwise the method throws exception.
     *
     * @param arr
     * @param len
     */
    public static void copyArr(double[] arr, int len) {
        for (int i = len; i < arr.length; i += len) {
            System.arraycopy(arr, 0, arr, i, len);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Array copying methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Array modification methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Reverses given array
     *
     * @param arr is the given array
     */
    public static void reverseArr(double[] arr) {
        double tmp;
        for (int i = 0; i < arr.length / 2; i++) {
            tmp = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = tmp;
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Array modification methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Methods for alignment to multiples and powers
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int getFirstPowerOfNBeforeNumber(int startNumber, int num, int n) {
        int result = getFirstPowerOfNAfterNumber(startNumber, num, n);
        return result / n;
    }

    public static int getFirstPowerOfNAfterNumber(int startNumber, int num, int n) {
        int result = startNumber;

        while (result <= num) {
            result *= n;
        }

        return result;
    }


    public static int getFirstPowerExponentOfNBeforeNumber(int startNumber, int num, int n) {
        int e = getFirstPowerExponentOfNAfterNumber(startNumber, num, n);
        return e - 1;
    }

    public static int getFirstPowerExponentOfNAfterNumber(int startNumber, int num, int n) {
        int result = startNumber;
        int e = 0;

        while (result <= num) {
            result *= n;
            e++;
        }

        return e;
    }


    public static int getFirstPowerOfNBeforeNumber(int num, int n) {
        return getFirstPowerOfNBeforeNumber(1, num, n);
    }

    public static int getFirstPowerOfNAfterNumber(int num, int n) {
        return getFirstPowerOfNAfterNumber(1, num, n);
    }


    public static int getFirstPowerExponentOfNBeforeNumber(int num, int n) {
        return getFirstPowerExponentOfNBeforeNumber(1, num, n);
    }

    public static int getFirstPowerExponentOfNAfterNumber(int num, int n) {
        return getFirstPowerExponentOfNAfterNumber(1, num, n);
    }


    /**
     * Tests if number num is power of n.
     *
     * @param num is the number to test.
     * @param n   is the power.
     * @return Returns -1 if it num is not i-th power of n, returns i otherwise.
     */
    public static int testIfNumberIsPowerOfN(int num, int n) {
        int result = 1;

        int i = 0;
        while (result < num) {
            result *= n;
            i++;
        }
        if (result == num) {
            return i;
        } else {
            return -1;
        }
    }


    /**
     * Tests if number num is power of n.
     *
     * @param num is the number to test.
     * @param n   is the power.
     * @return Returns -1 if it num is not i-th power of n, returns i otherwise. Returns -2 if the number is not integer/
     */
    public static int testIfNumberIsPowerOfN(double num, int n) {
        if (num == Math.floor(num)) {
            return testIfNumberIsPowerOfN((int) num, n);
        } else {
            return -2;
        }
    }


    public static int convertToMultipleDown(int val, int multiple) {
        val -= (val % multiple);
        return val;
    }

    public static int convertToMultipleUp(int val, int multiple) {
        val += multiple - (val % multiple);
        return val;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Methods for alignment to multiples and powers
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Filename related methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Separates input to extension and name (part without extension). The name as return value of method.
     *
     * @param input is the input name from which will be taken the name.
     * @return Returns the name without extension. If there was no extension returns the original name.
     */
    public static String getNameWithoutExtension(String input) {
        int ind = input.lastIndexOf('.');
        if (ind == -1) {
            return input;
        }
        String name = input.substring(0, ind);
        return name;
    }


    public static String getFilenameFromPath(String path) {
        String filename;
        int lastIndex = path.lastIndexOf(File.separator);
        if (lastIndex == -1) {
            filename = path;
        } else {
            filename = path.substring(lastIndex + 1);
        }
        return filename;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Filename related methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// String methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int calculateCharOccurrences(String s, char c) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }

        return count;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// String methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
