package str.rad.test;

import str.rad.util.Time;

public class TestTime {
    public static void main(String[] args) {
        // Copy-pasted, I just quickly wanted to check something - should have used some general method, but it is just
        // testing so it doesn't really matter.
        ProgramTest.debugPrint("65 seconds:", Time.convertSecondsToTime(65, -1));
        ProgramTest.debugPrint("65 seconds (alignment == 0):", Time.convertSecondsToTime(65, 0));
        ProgramTest.debugPrint("65 seconds (alignment == 1):", Time.convertSecondsToTime(65, 1));
        ProgramTest.debugPrint("65 seconds (alignment == 2):", Time.convertSecondsToTime(65, 2));
        ProgramTest.debugPrint("55 seconds:", Time.convertSecondsToTime(55, -1));
        ProgramTest.debugPrint("55 seconds (alignment == 0):", Time.convertSecondsToTime(55, 0));
        ProgramTest.debugPrint("55 seconds (alignment == 1):", Time.convertSecondsToTime(55, 1));
        ProgramTest.debugPrint("55 seconds (alignment == 2):", Time.convertSecondsToTime(55, 2));

        ProgramTest.debugPrint("5 seconds:", Time.convertSecondsToTime(5, -1));
        ProgramTest.debugPrint("5 seconds (alignment == 0):", Time.convertSecondsToTime(5, 0));

        ProgramTest.debugPrint("1065 milliseconds:", Time.convertMillisecondsToTime(1065, -1));
        ProgramTest.debugPrint("1065 milliseconds (alignment == 0):", Time.convertMillisecondsToTime(1065, 0));
        ProgramTest.debugPrint("1065 milliseconds (alignment == 1):", Time.convertMillisecondsToTime(1065, 1));
        ProgramTest.debugPrint("1065 milliseconds (alignment == 2):", Time.convertMillisecondsToTime(1065, 2));
        ProgramTest.debugPrint("65 milliseconds:", Time.convertMillisecondsToTime(65, -1));
        ProgramTest.debugPrint("65 milliseconds (alignment == 0):", Time.convertMillisecondsToTime(65, 0));
        ProgramTest.debugPrint("65 milliseconds (alignment == 1):", Time.convertMillisecondsToTime(65, 1));
        ProgramTest.debugPrint("65 milliseconds (alignment == 2):", Time.convertMillisecondsToTime(65, 2));
    }
}
