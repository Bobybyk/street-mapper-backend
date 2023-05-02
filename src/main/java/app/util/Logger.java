package app.util;
import java.io.PrintStream;

/**
 * Classe permettant d'afficher des messages sur les sorties standards
 */
public class Logger {

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
