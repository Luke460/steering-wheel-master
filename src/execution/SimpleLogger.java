package execution;

public class SimpleLogger {

    private enum LEVEL {
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }

    public static void infoLog(String message) {System.out.println(LEVEL.INFO.toString() + " > " + message);}

    public static void warningLog(String message) {System.out.println(LEVEL.WARNING.toString() + " > " + message);}

    public static void errorLog(String message) {System.out.println(LEVEL.ERROR.toString() + " > " + message);}

    public static void criticalLog(String message) {System.out.println(LEVEL.CRITICAL.toString() + " > " + message);}

    public static void emptySpace(String message) {System.out.println();}

}
