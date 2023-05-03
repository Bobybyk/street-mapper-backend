package util;

public class Parser {
    private Parser() {}

    /**
     * Vérifie que {@code input} à au moins 2 double séparés par {@code sep}
     *
     * @param input la chaîne à vérifier
     * @param sep le séparateur
     * @return la liste des double
     * @throws IndexOutOfBoundsException
     * @throws NumberFormatException
     */
    public static double[] parse2DoubleSep(String input, String sep)
            throws IndexOutOfBoundsException, NumberFormatException {
        String[] data = input.trim().split(sep);
        double x = Double.parseDouble(data[0]);
        double y = Double.parseDouble(data[1]);
        return new double[] {x, y};
    }

    /**
     * Vérifie que {@code input} à au moins 2 int séparés par {@code sep}
     *
     * @param input la chaîne à vérifier
     * @param sep le séparateur
     * @return la liste des int
     * @throws IndexOutOfBoundsException
     * @throws NumberFormatException
     */
    public static int[] parse2IntSep(String input, String sep)
            throws IndexOutOfBoundsException, NumberFormatException {
        String[] data = input.trim().split(sep);
        int x = Integer.parseInt(data[0]);
        int y = Integer.parseInt(data[1]);
        return new int[] {x, y};
    }
}
