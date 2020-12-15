package util;

import Rocnikovy_Projekt.ProgramTest;
import debug.DEBUG_CLASS;

public class Time {
    private Time() {}       // To disable instantiation - only static access available

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Time algorithms
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final StringBuilder timeStringBuilder = new StringBuilder();

    private static final int[] convertMinutesTimeDivArray = new int[] { 60, 60 };
    private static final int[] convertMinutesTimeModArray = new int[] { 10, 10 };
    /**
     * This variant can be used when I want to do parallel processing. Resets the stringbuilder.
     */
    public static String convertMinutesToTime(int mins, StringBuilder timeSB, int alignmentRecursionLevel) {
        String res = convertTimeUniversal(mins, convertMinutesTimeDivArray,
                convertMinutesTimeModArray, timeSB, alignmentRecursionLevel);
        return res;
    }

    /**
     * Doesn't work work for parallel processing
     */
    public static String convertMinutesToTime(int mins, int alignmentRecursionLevel) {
        return convertMinutesToTime(mins, timeStringBuilder, alignmentRecursionLevel);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int[] convertSecondsTimeDivArray = new int[] { 60, 60, 60 };
    private static final int[] convertSecondsTimeModArray = new int[] { 10, 10, 10 };
    /**
     * This variant can be used when I want to do parallel processing. Resets the stringbuilder.
     * @param alignmentRecursionDepth is the depth to which should be the time aligned. To say it easy - The parameter controls number of colons.
     *                                To get the best results with minimal alignment just call the method with argument -1.
     *                                If == 0 then no alignment is performed.
     *                                If < 0 then based on the method called in some special case alignment may be used - for example
     *                                for milliseconds there will always be at least one ':'. When calling it on other
     *                                than the millisecond method, it is the same as == 0 case.
     *                                Otherwise it is performed to the given depth (if the depth > modLimitsToAddZero.length)
     *                                then modLimitsToAddZero.length - 1 is used.
     *                                Example when alignmentRecursionDepth == 2 then the value is 00:00:001
     *                                instead of just 001 for alignmentRecursionDepth == 0.
     *
     */
    public static String convertSecondsToTime(int seconds, StringBuilder timeSB, int alignmentRecursionDepth) {
        String res = convertTimeUniversal(seconds, convertSecondsTimeDivArray,
                convertSecondsTimeModArray, timeSB, alignmentRecursionDepth);
        return res;
    }
    /**
     * Doesn't work work for parallel processing
     * @param alignmentRecursionDepth is the depth to which should be the time aligned. To say it easy - The parameter controls number of colons.
     *                                To get the best results with minimal alignment just call the method with argument -1.
     *                                If == 0 then no alignment is performed.
     *                                If < 0 then based on the method called in some special case alignment may be used - for example
     *                                for milliseconds there will always be at least one ':'. When calling it on other
     *                                than the millisecond method, it is the same as == 0 case.
     *                                Otherwise it is performed to the given depth (if the depth > modLimitsToAddZero.length)
     *                                then modLimitsToAddZero.length - 1 is used.
     *                                Example when alignmentRecursionDepth == 2 then the value is 00:00:001
     *                                instead of just 001 for alignmentRecursionDepth == 0.
     */
    public static String convertSecondsToTime(int seconds, int alignmentRecursionDepth) {
        return convertSecondsToTime(seconds, timeStringBuilder, alignmentRecursionDepth);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int[] convertMillisecondsTimeDivArray = new int[] { 1000, 60, 60, 60 };
    private static final int[] convertMillisecondsTimeModArray = new int[] { 100, 10, 10, 10 };
    /**
     * This variant can be used when I want to do parallel processing. Resets the stringbuilder.
     * @param alignmentRecursionDepth is the depth to which should be the time aligned. To say it easy - The parameter controls number of colons.
     *                                To get the best results with minimal alignment just call the method with argument -1.
     *                                If == 0 then no alignment is performed.
     *                                If < 0 then based on the method called in some special case alignment may be used - for example
     *                                for milliseconds there will always be at least one ':'. When calling it on other
     *                                than the millisecond method, it is the same as == 0 case.
     *                                Otherwise it is performed to the given depth (if the depth > modLimitsToAddZero.length)
     *                                then modLimitsToAddZero.length - 1 is used.
     *                                Example when alignmentRecursionDepth == 2 then the value is 00:00:001
     *                                instead of just 0:001 when calling it with alignmentRecursionDepth == -1 or
     *                                001 when calling it with alignmentRecursionDepth == 0.
     */
    public static String convertMillisecondsToTime(int millis, StringBuilder timeSB, int alignmentRecursionDepth) {
        if(DEBUG_CLASS.DEBUG) {
            ProgramTest.debugPrint("MILLIS in convertMillisecondsToTime", millis);
        }
        String res = "";
        if(alignmentRecursionDepth < 0) {
            if (millis < 1000) {
                res = "0:";
            }
        }
        res += convertTimeUniversal(millis, convertMillisecondsTimeDivArray,
                convertMillisecondsTimeModArray, timeSB, alignmentRecursionDepth);
        return res;
    }
    /**
     * Doesn't work work for parallel processing
     * @param alignmentRecursionDepth is the depth to which should be the time aligned. To say it easy - The parameter controls number of colons.
     *                                To get the best results with minimal alignment just call the method with argument -1.
     *                                If == 0 then no alignment is performed.
     *                                If < 0 then based on the method called in some special case alignment may be used - for example
     *                                for milliseconds there will always be at least one ':'. When calling it on other
     *                                than the millisecond method, it is the same as == 0 case.
     *                                Otherwise it is performed to the given depth (if the depth > modLimitsToAddZero.length)
     *                                then modLimitsToAddZero.length - 1 is used.
     *                                Example when alignmentRecursionDepth == 2 then the value is 00:00:001
     *                                instead of just 0:001 when calling it with alignmentRecursionDepth == -1 or
     *      *                         001 when calling it with alignmentRecursionDepth == 0.
     */
    public static String convertMillisecondsToTime(int millis, int alignmentRecursionDepth) {
        return convertMillisecondsToTime(millis, timeStringBuilder, alignmentRecursionDepth);
    }



    /**
     *
     * @param alignmentRecursionDepth is the depth to which should be the time aligned. To say it easy - The parameter controls number of colons.
     *                                To get the best results with minimal alignment just call the method with argument -1.
     *                                If == 0 then no alignment is performed.
     *                                If < 0 then based on the method called in some special case alignment may be used - for example
     *                                for milliseconds there will always be at least one ':'. When calling it on other
     *                                than the millisecond method, it is the same as == 0 case.
     *                                Otherwise it is performed to the given depth (if the depth > modLimitsToAddZero.length)
     *                                then modLimitsToAddZero.length - 1 is used.
     *                                Example when alignmentRecursionDepth == 2 then the value is 00:00:001
     *                                instead of just 001 for alignmentRecursionDepth == 0.
     */
    private static String convertTimeUniversal(int timeAmount, int[] timeAmountToNextTimeLevel, int[] modLimitsToAddZero,
                                               StringBuilder timeSB, int alignmentRecursionDepth) {
        timeSB.setLength(0);
        convertTimeUniversal(timeAmount, timeAmountToNextTimeLevel, modLimitsToAddZero,
                0, timeSB, alignmentRecursionDepth);
        return timeSB.toString();
    }

    /**
     * Expects the StringBuilder to be of length 0.
     * @param timeAmount
     * @param timeAmountToNextTimeLevel
     * @param modLimitsToAddZero
     * @param depth
     * @param timeSB
     * @param alignmentRecursionDepth is the depth to which should be the time aligned. To say it easy - The parameter controls number of colons.
     *                                To get the best results with minimal alignment just call the method with argument -1.
     *                                If == 0 then no alignment is performed.
     *                                If < 0 then based on the method called in some special case alignment may be used - for example
     *                                for milliseconds there will always be at least one ':'. When calling it on other
     *                                that the millisecond method, it is the same as == 0 case.
     *                                Otherwise it is performed to the given depth (if the depth > modLimitsToAddZero.length)
     *                                then modLimitsToAddZero.length - 1 is used.
     *                                Example when alignmentRecursionDepth == 2 then the value is 00:00:001
     *                                instead of just 001 for alignmentRecursionDepth == 0.
     */
    private static void convertTimeUniversal(int timeAmount, int[] timeAmountToNextTimeLevel, int[] modLimitsToAddZero,
                                             int depth, StringBuilder timeSB, int alignmentRecursionDepth) {
        if(timeAmount != 0) {
            if (depth < timeAmountToNextTimeLevel.length) {
                int nextTimeMeasurementCount = timeAmount / timeAmountToNextTimeLevel[depth];
                convertTimeUniversal(nextTimeMeasurementCount, timeAmountToNextTimeLevel, modLimitsToAddZero,
                        depth + 1, timeSB, alignmentRecursionDepth);
                if(timeSB.length() != 0) {
                    timeSB.append(':');
                }
                int mod = timeAmount % timeAmountToNextTimeLevel[depth];
                int modChanging = mod;
                if(mod == 0) {
                    timeSB.append('0');
                }
                else {
                    for (; modChanging < modLimitsToAddZero[depth]; modChanging *= 10) {
                        timeSB.append('0');
                    }
                }

                timeSB.append(mod);
            }
        }
        else {
            if(alignmentRecursionDepth < 0) {
                if (depth == 0) {
                    timeSB.append('0');
                }
            }
            else {
                if(timeSB.length() > 0) {
                    timeSB.append(':');
                }

                alignmentRecursionDepth = Math.min(alignmentRecursionDepth, modLimitsToAddZero.length - 1);
                for (int i = alignmentRecursionDepth; i >= depth; i--) {
                    for (int mod = 1; mod <= modLimitsToAddZero[i]; mod *= 10) {
                        timeSB.append('0');
                    }
                    if(i != depth) {
                        timeSB.append(':');
                    }
                }
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Time algorithms
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
