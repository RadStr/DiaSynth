package util.logging;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyLogger {
    private MyLogger() { }          // To make only static access possible

    private static File logFile = new File("LOG.log");
    private static PrintWriter logStream;
    private static int indentation = 0;
    private static String indentationString = "";

    static {
        try {
            // https://stackoverflow.com/questions/25540751/how-do-i-add-data-to-text-file-and-not-overwrite-what-i-have-in-java/25540826
            logStream = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
//            logStream = new PrintWriter(System.out);  // Just for testing
//            logStream = new PrintWriter(System.err);  // Just for testing

            // taken from https://www.javatpoint.com/java-get-current-date
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            logStream.println("-----------------------------------------------");
            logStream.println(formatter.format(date));
            logStream.flush();
        }
        catch (Exception e) {
            logStream = null;
        }
    }

    public static void log(String logMessage, int indentationAddition) {
        if (logStream != null) {
            if (indentationAddition != 0) {
                if (indentationAddition > 0) {
                    indentation += indentationAddition;
                }
                // https://stackoverflow.com/questions/1235179/simple-way-to-repeat-a-string-in-java
                indentationString = new String(new char[indentation]).replace('\0', '\t');
            }
            logStream.println(indentationString + logMessage);
            logStream.flush();
            if (indentationAddition < 0) {
                indentation += indentationAddition;
            }
        }
    }

    public static void logWithoutIndentation(String logMessage) {
        if (logStream != null) {
            logStream.println(logMessage);
            logStream.flush();
        }
    }

    public static void close() {
        logStream.flush();
        logStream.close();
        logStream = null;
    }


    public static void logException(Exception e) {
        String stackTrace = MyLogger.getStackTraceString(e);
        MyLogger.logWithoutIndentation("Message:\t" + e.getMessage() + "\n" +
                                       "Stack trace:\t" + stackTrace);
    }

    // Taken from: https://stackoverflow.com/questions/4812570/how-to-store-printstacktrace-into-a-string
    public static String getStackTraceString(Exception ex) {
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }
}
