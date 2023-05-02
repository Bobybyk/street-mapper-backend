package app.util;
import java.io.PrintStream;

public class Logger {
    public static enum Type {
        INFO("\u001B[34m"),
        ERROR("\u001B[31m");

        final String colorSequence;

        Type(String colorSequence) {
            this.colorSequence = colorSequence;
        }

        PrintStream getPrintStream() {
            return switch (this) {
                case INFO -> System.out;
                case ERROR -> System.err;
            };
        }

        @Override
        public String toString() {
            return switch (this) {
                case INFO -> "INFO";
                case ERROR -> "ERROR";
            };
        }
    }

    private static final String ASCII_RESET_COLOR = "\u001B[0m";

    public static void log(Logger.Type type, String message) {
        PrintStream stream = type.getPrintStream();
        stream.printf("%s %s : %s %s", type.colorSequence, type.toString(), message, ASCII_RESET_COLOR);
        stream.flush();
    }

    public static void logln(Logger.Type type, String message) {
        PrintStream stream = type.getPrintStream();
        stream.printf("%s %s : %s %s\n", type.colorSequence, type.toString(), message, ASCII_RESET_COLOR);
        stream.flush();
    }
}
