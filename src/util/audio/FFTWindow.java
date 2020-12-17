package util.audio;

public class FFTWindow {
    private FFTWindow() {}      // Allow only static access

    public static double[] get2CoefWindowUniversalWithoutLimit(int arrayLen, int startIndexForWindowCalculation, double a0, double a1, int windowSize) {
        double[] result = new double[arrayLen];
        get2CoefWindowUniversalWithoutLimit(startIndexForWindowCalculation, a0, a1, result, windowSize);
        return result;
    }
    public static void get2CoefWindowUniversalWithoutLimit(int startIndexForWindowCalculation, double a0, double a1, double[] result, int windowSize) {
        int windowSizePlus1 = windowSize + 1;

        for(int i = 0; i < result.length; i++, startIndexForWindowCalculation++) {
            result[i] = calculateWindowValue(a0, a1, startIndexForWindowCalculation, windowSizePlus1);
        }
    }

    public static double[] get2CoefWindowUniversalWithLimit(int arrayLen, int startIndexForWindowCalculation, double a0, double a1, int windowSize) {
        double[] result = new double[arrayLen];
        get2CoefWindowUniversalWithLimit(startIndexForWindowCalculation, a0, a1, result, windowSize);
        return result;
    }
    public static void get2CoefWindowUniversalWithLimit(int startIndexForWindowCalculation, double a0, double a1, double[] result, int windowSize) {
        int windowSizePlus1 = windowSize + 1;

        int i = 0;
        for(; startIndexForWindowCalculation <= windowSize; i++, startIndexForWindowCalculation++) { // TODO: podle me tu ma byt <=
            result[i] = calculateWindowValue(a0, a1, startIndexForWindowCalculation, windowSizePlus1);
        }
        for(; i < result.length; i++) {
            result[i] = 0;
        }
    }


    public static double[] getHahnWindowWithLimit(int arrayLen, int startIndexForWindowCalculation, int windowSize) {
        return get2CoefWindowUniversalWithLimit(arrayLen, startIndexForWindowCalculation, 0.5, 0.5, windowSize);
    }
    public static void getHahnWindowWithLimit(int startIndexForWindowCalculation, double[] result, int windowSize) {
        get2CoefWindowUniversalWithLimit(startIndexForWindowCalculation, 0.5, 0.5, result, windowSize);
    }
    public static double[] getHammingWindowWithLimit(int arrayLen, int startIndexForWindowCalculation, int windowSize) {
        return get2CoefWindowUniversalWithLimit(arrayLen, startIndexForWindowCalculation, 0.53836,  0.46164, windowSize);
    }
    public static void getHammingWindowWithLimit(int startIndexForWindowCalculation, double[] result, int windowSize) {
        get2CoefWindowUniversalWithLimit(startIndexForWindowCalculation, 0.53836,  0.46164, result, windowSize);
    }


    public static double[] getHahnWindowWithoutLimit(int arrayLen, int startIndexForWindowCalculation, int windowSize) {
        return get2CoefWindowUniversalWithoutLimit(arrayLen, startIndexForWindowCalculation, 0.5, 0.5, windowSize);
    }
    public static void getHahnWindowWithoutLimit(int startIndexForWindowCalculation, double[] result, int windowSize) {
        get2CoefWindowUniversalWithoutLimit(startIndexForWindowCalculation, 0.5, 0.5, result, windowSize);
    }
    public static double[] getHammingWindowWithoutLimit(int arrayLen, int startIndexForWindowCalculation, int windowSize) {
        return get2CoefWindowUniversalWithoutLimit(arrayLen, startIndexForWindowCalculation, 0.53836,  0.46164, windowSize);
    }
    public static void getHammingWindowWithoutLimit(int startIndexForWindowCalculation, double[] result, int windowSize) {
        get2CoefWindowUniversalWithoutLimit(startIndexForWindowCalculation, 0.53836,  0.46164, result, windowSize);
    }


    public static double calculateWindowValue(double a0, double a1, int index, int lengthPlus1) {
// TODO: DEBUG        System.out.println((a0 - a1 * Math.cos(2 * Math.PI * index / lengthPlus1)) + "\t" + lengthPlus1);
        return a0 - a1 * Math.cos(2 * Math.PI * index / lengthPlus1);
    }

    // Alternatively can be implemented like this https://en.wikipedia.org/wiki/Window_function#Hann_and_Hamming_windows
    //  ... cosine-sum windows - but not that fast, I would had to have arrays or variable amount of parameters or something.
}
