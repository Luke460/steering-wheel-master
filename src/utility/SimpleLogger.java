package utility;

public class SimpleLogger {

    private enum LEVEL {
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }

    public static void infoLog(String message) {System.out.println(LEVEL.INFO + " > " + message);}

    public static void warningLog(String message) {System.out.println(LEVEL.WARNING + " > " + message);}

    public static void errorLog(String message) {System.out.println(LEVEL.ERROR + " > " + message);}

    public static void criticalLog(String message) {System.out.println(LEVEL.CRITICAL + " > " + message);}

    public static void emptySpace(String message) {System.out.println();}

}
