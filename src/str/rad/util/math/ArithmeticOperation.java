package str.rad.util.math;

import str.rad.util.Utilities;

public enum ArithmeticOperation {
    PLUS,
    MULTIPLY,
    LOG,
    POWER;

    public static String[] getEnumsToStrings() {
        ArithmeticOperation[] values = ArithmeticOperation.values();
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = values[i].toString();
        }

        return strings;
    }


    public static ArithmeticOperation convertStringToEnumValue(String s) {
        ArithmeticOperation[] values = ArithmeticOperation.values();
        for (ArithmeticOperation v : values) {
            if (v.toString().equals(s)) {
                return v;
            }
        }

        return null;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// performOperation method
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static double performOperation(double sample, double changeValue, ArithmeticOperation op) {
        switch (op) {
            case PLUS:
                sample += changeValue;
                break;
            case MULTIPLY:
                sample *= changeValue;
                break;
            case LOG:
                sample = Utilities.logarithm(sample, changeValue);      // changeValue is the base
                break;
            case POWER:
                sample = Math.pow(sample, changeValue);
                break;
        }

        return sample;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// performOperation method
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// performOperationOnSamples methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Changes samples values based on type of operation and on size of changeValue.
     * Doesn't change given samples array. Creates new one and puts results in it
     * For op = PLUS we add the changeValue to all samples.
     * For op = MULTIPLY we multiply all samples by changeValue.
     * For op = LOG we take logarithm from the samples. The logarithm is of base changeValue.
     * For op = POWER we raise the samples to the changeValue-th power.
     *
     * @param samples     is the array containing samples.
     * @param startIndex  is the start index from which to start perform operations on.
     * @param len         is the number of samples to perform operation on
     * @param changeValue is the value which will be used on all elements in samples array.
     * @param op          is the operation to be performed on the samples.
     * @return Returns copy of the samples array with corresponding changes.
     */
    public static double[] performOperationOnSamples(double[] samples, int startIndex, int outputStartIndex,
                                                     int len, double changeValue, ArithmeticOperation op) {
        double[] retArr = new double[samples.length];
        System.arraycopy(samples, 0, retArr, 0, retArr.length);
        performOperationOnSamples(samples, retArr, startIndex, outputStartIndex, len, changeValue, op);
        return retArr;
    }

    /**
     * Changes samples values based on type of operation and on size of changeValue.
     * Changes given outputArr array.
     * For op = PLUS we add the changeValue to all samples.
     * For op = MULTIPLY we multiply all samples by changeValue.
     * For op = LOG we take logarithm from the samples. The logarithm is of base changeValue.
     * For op = POWER we raise the samples to the changeValue-th power.
     *
     * @param samples           is the array containing samples.
     * @param outputArr         is the array to put the results in (can be same as the samples array)
     * @param samplesStartIndex is the start index from which to start perform operations on.
     * @param len               is the number of samples to perform operation on
     * @param changeValue       is the value which will be used on all elements in samples array.
     * @param op                is the operation to be performed on the samples.
     */
    public static void performOperationOnSamples(double[] samples, double[] outputArr, int samplesStartIndex, int outputStartIndex,
                                                 int len, double changeValue, ArithmeticOperation op) {
        int endIndex = samplesStartIndex + len;
        for (int i = samplesStartIndex, outIndex = outputStartIndex; i < endIndex; i++, outIndex++) {
            outputArr[outIndex] = performOperation(samples[i], changeValue, op);
        }
    }

    /**
     * Changes samples values based on type of operation and on size of changeValue.
     * Changes given samples array.
     * For op = PLUS we add the changeValue to all samples.
     * For op = MULTIPLY we multiply all samples by changeValue.
     * For op = LOG we take logarithm from the samples. The logarithm is of base changeValue.
     * For op = POWER we raise the samples to the changeValue-th power.
     *
     * @param samples     is the array containing samples.
     * @param startIndex  is the start index in array
     * @param endIndex    is the end index in array
     * @param changeValue is the value which will be used on all elements in samples array.
     * @param op          is the operation to be performed on the samples.
     */
    public static void performOperationOnSamples(double[] samples, int startIndex, int endIndex, double changeValue, ArithmeticOperation op) {
        int len = endIndex - startIndex;
        performOperationOnSamples(samples, samples, startIndex, startIndex, len, changeValue, op);
    }

    /**
     * Not used, this is too general
     *
     * @param input
     * @param changeValues
     * @param output
     * @param inputStartIndex
     * @param inputEndIndex
     * @param changeValuesStartIndex
     * @param changeValuesEndIndex
     * @param outputStartIndex
     * @param outputEndIndex
     * @param op
     */
    public static void performOperationOnSamples(double[] input, double[] changeValues, double[] output,
                                                 int inputStartIndex, int inputEndIndex,
                                                 int changeValuesStartIndex, int changeValuesEndIndex,
                                                 int outputStartIndex, int outputEndIndex, ArithmeticOperation op) {
        int inputLen = inputEndIndex - inputStartIndex;
        boolean isPowerOf2 = Utilities.testIfNumberIsPowerOfN(inputLen, 2) >= 0;

        if (isPowerOf2) {
            int changeValuesLen = changeValuesEndIndex - changeValuesStartIndex;
            boolean isPowerOf2CV = Utilities.testIfNumberIsPowerOfN(inputLen, 2) >= 0;
            if (isPowerOf2CV) {
                for (int oi = outputStartIndex, ii = inputStartIndex, cvi = changeValuesStartIndex; oi < outputEndIndex; oi++, ii++, cvi++) {
                    output[oi] = performOperation(input[inputStartIndex + (ii % inputLen)],
                                                  changeValues[changeValuesStartIndex + (cvi % changeValuesLen)], op);
                }
            }
            else {
                for (int oi = outputStartIndex, ii = inputStartIndex, cvi = changeValuesStartIndex; oi < outputEndIndex; oi++, ii++, cvi++) {
                    if (cvi >= changeValuesEndIndex) {
                        cvi = changeValuesStartIndex;
                    }
                    output[oi] = performOperation(input[inputStartIndex + (ii % inputLen)], changeValues[cvi], op);
                }
            }
        }
        else {
            for (int oi = outputStartIndex, ii = inputStartIndex, cvi = changeValuesStartIndex; oi < outputEndIndex; oi++, ii++, cvi++) {
                if (ii >= inputEndIndex) {
                    ii = inputStartIndex;
                }
                if (cvi >= changeValuesEndIndex) {
                    cvi = changeValuesStartIndex;
                }
                output[oi] = performOperation(input[ii], changeValues[cvi], op);
            }
        }
    }

    /**
     * Less general variant, doesn't contain the change values
     */
    public static void performOperationOnSamples(double[] input, double[] output,
                                                 int inputStartIndex, int inputEndIndex,
                                                 int outputStartIndex, int outputEndIndex, ArithmeticOperation op) {
        int inputLen = inputEndIndex - inputStartIndex;
        boolean isPowerOf2 = Utilities.testIfNumberIsPowerOfN(inputLen, 2) >= 0;

        if (isPowerOf2) {
            for (int oi = outputStartIndex, ii = inputStartIndex; oi < outputEndIndex; oi++, ii++) {
                output[oi] = performOperation(input[inputStartIndex + (ii % inputLen)], output[oi], op);
            }
        }
        else {
            for (int oi = outputStartIndex, ii = inputStartIndex; oi < outputEndIndex; oi++, ii++) {
                if (ii >= inputEndIndex) {
                    ii = inputStartIndex;
                }
                output[oi] = performOperation(input[ii], output[oi], op);
            }
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// performOperationOnSamples methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
