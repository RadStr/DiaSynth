package util;

import Rocnikovy_Projekt.Program;
import util.audio.AudioUtilities;

public class Rectification {
    private Rectification() {}      // Allow only static access


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Rectification - https://en.wikipedia.org/wiki/Rectifier
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


////////////////////
////////// Half wave rectification
////////////////////
    public static void halfWaveRectificationInt(int[] samples, boolean passPositive, int sampleSize, boolean isSigned) {
        int zeroValue = getZeroValue(isSigned, sampleSize);

        for(int i = 0; i < samples.length; i++) {
            if(passPositive) {
                if(samples[i] < zeroValue) {
                    samples[i] = zeroValue;
                }
            }
            else {
                if(samples[i] > zeroValue) {
                    samples[i] = zeroValue;
                }
            }
        }
    }

    // Double values are shifted so they are between -1 and 1
    public static void halfWaveRectificationDouble(double[] samples, boolean passPositive) {
        for(int i = 0; i < samples.length; i++) {
            if(passPositive) {
                if(samples[i] < 0) {
                    samples[i] = 0;
                }
            }
            else {
                if(samples[i] > 0) {
                    samples[i] = 0;
                }
            }
        }
    }

    public static void halfWaveRectificationBytes(byte[] samples, boolean passPositive, int mask, int sampleSize,
                                                  boolean isBigEndian, boolean isSigned) {
        byte[] zeroValueBytes = new byte[sampleSize];
        int zeroValue = getZeroValue(zeroValueBytes, isBigEndian, isSigned, sampleSize);

        int sample;
        for(int i = 0; i < samples.length;) {
            sample = Program.convertBytesToInt(samples, sampleSize, mask, i, isBigEndian, isSigned);
            if(passPositive) {
                if(sample < zeroValue) {
                    i = setArrayValues(samples, zeroValueBytes, i);
                }
                else {
                    i += sampleSize;
                }
            }
            else {
                if(sample > zeroValue) {
                    if(sample < zeroValue) {
                        i = setArrayValues(samples, zeroValueBytes, i);
                    }
                }
                else {
                    i += sampleSize;
                }
            }
        }
    }


////////////////////
////////// Full wave rectification
////////////////////
    public static void fullWaveRectificationDouble(double[] samples, boolean passPositive) {
        for(int i = 0; i < samples.length; i++) {
            if(passPositive) {
                samples[i] = Math.abs(samples[i]);
            }
            else {
                samples[i] = -Math.abs(samples[i]);
            }
        }
    }

    public static void fullWaveRectificationInt(int[] samples, boolean passPositive, int sampleSize, boolean isSigned) {
        int zero = getZeroValue(isSigned, sampleSize);

        for(int i = 0; i < samples.length; i++) {
            samples[i] = getAbsoluteValueGeneral(samples[i], zero, passPositive);
        }
    }

    // TODO:
    public static void fullWaveRectificationByte(byte[] samples, boolean passPositive, int mask, int sampleSize,
                                                 boolean isBigEndian, boolean isSigned) {
        byte[] sampleBytes = new byte[sampleSize];
        int zeroValue = getZeroValue(isSigned, sampleSize);

        int sample;
        for(int i = 0; i < samples.length;) {
            sample = Program.convertBytesToInt(samples, sampleSize, mask, i, isBigEndian, isSigned);
            getAbsoluteValueGeneral(sample, zeroValue, passPositive, sampleBytes, sampleSize, isBigEndian);

            if(passPositive) {
                if(sample < zeroValue) { // TODO: Tohle je spatne ... 2x delam tutez kontrolu ifu
                    // TODO: ... a kdyz to budu delat bez ifu tak ty hodnoty nastavim i kdyz nemusim protoze jsou stejny
                    // TODO: ... Takze bud se podivat jestli dela prekladac omptimalizaci, nebo to prepsat nejak osklive
                    i = setArrayValues(samples, sampleBytes, i);
//                    for(int j = 0; j < zeroValueBytes.length; j++, i++) {
//                        samples[i] = zeroValueBytes[j];
//                    }
                }
                else {
                    i += sampleSize;
                }
            }
            else {
                if(sample > zeroValue) {
                    i = setArrayValues(samples, sampleBytes, i);
//                        for(int j = 0; j < zeroValueBytes.length; j++, i++) {
//                            samples[i] = zeroValueBytes[j];
//                        }
                }
                else {
                    i += sampleSize;
                }
            }
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Rectification - https://en.wikipedia.org/wiki/Rectifier
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Rectification - help methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int getZeroValue(boolean isSigned, int sampleSize) {
        if(isSigned) {
            return 0;
        }
        else {
            int maxValue = AudioUtilities.getMaxAbsoluteValueUnsigned(8 * sampleSize);
            return maxValue / 2;
        }
    }

    public static int getZeroValue(byte[] zeroValueBytesResult, boolean isBigEndian, boolean isSigned, int sampleSize) {
        int zeroValue = 0;
        if(isSigned) {
            for(int i = 0; i < zeroValueBytesResult.length; i++) {
                zeroValueBytesResult[i] = 0;
            }
        }
        else {
            int maxValue = AudioUtilities.getMaxAbsoluteValueUnsigned(8 * sampleSize);
            zeroValue = maxValue / 2;
            Program.convertIntToByteArr(zeroValueBytesResult, zeroValue, isBigEndian);
        }

        return zeroValue;
    }



    // TODO: Zamyslet se jestli dava smysl tohle pouzivat - v C++ by davalo protoze bych index predal pointerem a neresil bych to
    // TODO: Tady ale pak ten index musim zvysit i mimo metodu ... hodne maly zpomaleni - ale kdyz se provede milionkrat
    // TODO: Tak se to nascita, ... TODO: Podivat se jestli to pouzivam i jinde
    public static int setArrayValues(byte[] array, byte[] arrayWithSetValues, int index) {
        for(int j = 0; j < arrayWithSetValues.length; j++, index++) {
            array[index] = arrayWithSetValues[j];
        }

        return index;
    }

    public static int getAbsoluteValueGeneral(int value, int zero, boolean isPositive,
                                              byte[] resultInBytes, int sampleSize, boolean isBigEndian) {
        int retVal;
        if(isPositive) {
            retVal = getAbsoluteValueGeneralPositive(value, zero);
        }
        else {
            retVal = getAbsoluteValueGeneralNegative(value, zero);
        }

        Program.convertIntToByteArr(resultInBytes, sampleSize, isBigEndian);
        return retVal;
    }

    public static int getAbsoluteValueGeneral(int value, int zero, boolean isPositive) {
        if(isPositive) {
            return getAbsoluteValueGeneralPositive(value, zero);
        }
        else {
            return getAbsoluteValueGeneralNegative(value, zero);
        }
    }

    public static int getAbsoluteValueGeneralPositive(int value, int zero) {
        // Version without branching
        int dif = value - zero;
        int sign = Integer.signum(dif);
        int returnVal = zero + (sign * dif);      // Doesn't need if branching
        return returnVal;
// TODO: Testuju v ProgramTest
//        // Version with branching
//        if(value > zero) {
//            return value;
//        }
//        else {
//            return zero + (zero - value); // zero - value tells how much it is under zero, so we just add that number to zero, to get the positive one
//        }
    }

    public static int getAbsoluteValueGeneralNegative(int value, int zero) {
        // Version without branching
        int dif = value - zero;
        int sign = -Integer.signum(dif);
        int returnVal = zero + (sign * dif);      // Doesn't need if branching
        return returnVal;
// TODO: Testuju v ProgramTest
//        // Version with branching
//        if(value > zero) {
//            return zero - (value - zero); // value - zero tells how much it is above zero, so we subtract that number from zero
//        }
//        else {
//            return value;
//        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Rectification - help methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
