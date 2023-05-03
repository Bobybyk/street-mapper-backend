package app.util;
import java.io.PrintStream;

/**
 * Classe permettant d'afficher des messages sur les sorties standards
 */
public class Logger {

    private static boolean isEnable = true;

    public static synchronized void enable() {
        isEnable = true;
    }

    public static synchronized void disable() {
        isEnable = false;
    }

    /**
     * Classe descrivant le type de log
     */
    public static enum Type {
        DEFAULT("\u001B[37m"),
        INFO("\u001B[34m"),
        ERROR("\u001B[31m");

        /**
         * sequence ascii echappe pour afficher le texte en couleur
         */
        final String colorSequence;

        Type(String colorSequence) {
            this.colorSequence = colorSequence;
        }

        /**
         * Retoune la sortie sortie standard associe au type du log
         * @return
         */
        PrintStream getPrintStream() {
            return switch (this) {
                case INFO, DEFAULT -> System.out;
                case ERROR -> System.err;
            };
        }

        @Override
        public String toString() {
            return switch (this) {
                case INFO -> "[INFO]";
                case ERROR -> "[ERREUR]";
                case DEFAULT -> "[DEFAULT]";
            };
        }
    }

    private static final String ASCII_RESET_COLOR = "\u001B[0m";

    private static void log(Logger.Type type, String message) {
        if (!isEnable) return;

        PrintStream stream = type.getPrintStream();
        stream.printf("%s %s : %s %s\n", type.colorSequence, type.toString(), message, ASCII_RESET_COLOR);
        stream.flush();
    }

    public static void info(String message) {
        log(Type.INFO, message);
    }

    public static void error(String message) {
        log(Type.ERROR, message);
    }
}
