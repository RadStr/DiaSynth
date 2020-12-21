package test;

import util.Utilities;

// This class isn't really testing, I just used it to understand the results of FFT better.
public class TestFFT {
    public static void main(String[] args) {
        ProgramTest.tryFFTSums(1024);

        ProgramTest.testFFTBinCount(1024, 20);
        ProgramTest.testFFTBinCount(1023, 20);

        // The [3] == -512, which is the imaginary part of the first bin (it is [3], because [0] is 0Hz and [1] is Re[n/2])
        ProgramTest.printRealFFT(1024, 1, 1, 0, 1024, Utilities.CURVE_TYPE.SINE);
        // [2] == 512, which is real part of the first bin
        ProgramTest.printRealFFT(1024, 1,  1, Math.PI / 2, 1024, Utilities.CURVE_TYPE.SINE);

        ProgramTest.debugPrint("-------------------------------------------------------------------------------------");

        ProgramTest.printComplexFFTRealOnly(1024, 1, 1, 0, 1024, Utilities.CURVE_TYPE.SINE);
        ProgramTest.printComplexFFTRealOnly(1024, 1,  1, Math.PI / 2, 1024, Utilities.CURVE_TYPE.SINE);

        ProgramTest.debugPrint("-------------------------------------------------------------------------------------");

        ProgramTest.printComplexFFTImagOnly(1024, 1, 1, 0, 1024, Utilities.CURVE_TYPE.SINE);
        ProgramTest.printComplexFFTImagOnly(1024, 1,  1, Math.PI / 2, 1024, Utilities.CURVE_TYPE.SINE);

        ProgramTest.debugPrint("-------------------------------------------------------------------------------------");

        ProgramTest.printComplexFFT(1024, 0.5, 1, 0,
                1, 1, 0, 1024, Utilities.CURVE_TYPE.SINE);
        ProgramTest.printComplexFFT(1024, 1, 1, 0, 1, 1,
                                    Math.PI / 2, 1024, Utilities.CURVE_TYPE.SINE);
//        printComplexFFT(1024, 1, 1, 0,
//                0.5, 1, 0, 1024, ByteWave.CURVE_TYPE.SINE);

        ProgramTest.debugPrint("-------------------------------------------------------------------------------------");


//        printComplexFFT(1024, 1, 1, 0,
//                1, 1, 0, 1024, ByteWave.CURVE_TYPE.SINE);
//        printComplexFFT(1024, 1, 1, 0,
//                1, 2, 0, 1024, ByteWave.CURVE_TYPE.SINE);
//
//        ProgramTest.debugPrint("-------------------------------------------------------------------------------------");
//
//        printComplexFFT(1024, 0.5, 0, 0,
//                1, 0, 0, 1024, ByteWave.CURVE_TYPE.LINE);
//        printComplexFFT(1024, 1, 0, 0,
//                1, 0, 0, 1024, ByteWave.CURVE_TYPE.LINE);
//
////        printComplexFFT(1024, 1, 1, 0,
////                1, 511, 0, 1024, ByteWave.CURVE_TYPE.SINE);
//
//        ProgramTest.debugPrint("-------------------------------------------------------------------------------------");
//
//        printComplexFFTRealOnly(1024, 1, 1, 0, 1024, ByteWave.CURVE_TYPE.SINE);
//        printComplexFFTRealOnly(1024, 1, 1023, 0, 1024, ByteWave.CURVE_TYPE.SINE);
    }
}
