package util;
import java.util.logging.Level;

/**
 * Classe permettant d'afficher des messages sur les sorties standards
 */
public final class Logger {

    private static boolean isEnable = true;

    private static final java.util.logging.Logger shared = java.util.logging.Logger.getGlobal();

    public static synchronized void enable() {
        isEnable = true;
    }

    public static synchronized void disable() {
        isEnable = false;
    }

    public static synchronized boolean isEnable() {
        return isEnable;
    }

    /**
     * Classe descrivant le type de log
     */
    private enum Type {
        INFO("\u001B[34m"),
        ERROR("\u001B[31m");

        /**
         * sequence ascii echappe pour afficher le texte en couleur
         */
        final String colorSequence;

        Type(String colorSequence) {
            this.colorSequence = colorSequence;
        }
        
        public java.util.logging.Level getLevel() {
            return switch (this) {
                case INFO -> Level.INFO;
                case ERROR -> Level.WARNING;
            };
        }
    }

    private Logger() { }

    private static final String ASCII_RESET_COLOR = "\u001B[0m";

    private static void log(Logger.Type type, String message) {
        if (!isEnable) return;

        message = String.format("%s%s %s", type.colorSequence, message, ASCII_RESET_COLOR);
        shared.log(type.getLevel(), "{0}", message);
    }

    public static void info(String message) {
        log(Type.INFO, message);
    }

    public static void error(String message) {
        log(Type.ERROR, message);
    }
}
