package app.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import app.map.Line.DifferentStartException;
import app.map.Line.StartStationNotFoundException;

public final class PlanParser {

    /**
     * Exception pour un fichier mal formé
     */
    public static class IncorrectFileFormatException extends Exception {
        public IncorrectFileFormatException(String filename) {
            super(String.format("Le fichier %s n'est pas bien formé", filename));
        }
    }


    public static class UndefinedLineException extends Exception {
        public UndefinedLineException(String line) {
            super(String.format("La line %s n'existe pas dans la carte", line));
        }
    }

    private PlanParser() {}

    /**
     * @param fileName le nom du fichier à parser
     * @return un plan à partir d'un fichier CSV
     * @throws FileNotFoundException si le fichier n'a pas été trouvé
     * @throws IncorrectFileFormatException si le format du fichier est incorrect
     * @throws IllegalArgumentException si {@code mapFileName} est {@code null}
     */
    public static Plan planFromSectionCSV(String fileName)
            throws FileNotFoundException, IncorrectFileFormatException, IllegalArgumentException {
        if (fileName == null)
            throw new IllegalArgumentException();
        return parsePlan(fileName);
    }

    /**
     * Parse un fichier CSV décrivant un plan et créer le plan associé
     *
     * @param fileName le nom du fichier à parser
     * @throws FileNotFoundException si le fichier n'a pas été trouvé
     * @throws IncorrectFileFormatException si le format du fichier est incorrect
     */
    private static Plan parsePlan(String fileName)
            throws FileNotFoundException, IncorrectFileFormatException {
        Plan plan = new Plan();
        File file = new File(fileName);
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                handlePlanLine(plan, sc.nextLine());
            }
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new IncorrectFileFormatException(file.getName());
        }
        return plan;
    }

    /**
     * Vérifie que {@code input} à au moins 2 double séparés par {@code sep}
     *
     * @param input la chaîne à vérifier
     * @param sep le séparateur
     * @return la liste des double
     * @throws IndexOutOfBoundsException
     * @throws NumberFormatException
     */
    private static double[] parse2DoubleSep(String input, String sep)
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
    private static int[] parse2IntSep(String input, String sep)
            throws IndexOutOfBoundsException, NumberFormatException {
        String[] data = input.trim().split(sep);
        int x = Integer.parseInt(data[0]);
        int y = Integer.parseInt(data[1]);
        return new int[] {x, y};
    }

    /**
     * Parse une ligne d'un fichier CSV contenant une section de trajet du réseau.
     *
     * @param plan le plan où ajouter la section
     * @param input la ligne à parser
     * @throws IndexOutOfBoundsException si la ligne est mal formée
     * @throws NumberFormatException si une des données qui doit être un nombre ne l'est pas
     */
    private static void handlePlanLine(Plan plan, String input)
            throws IndexOutOfBoundsException, NumberFormatException {
        String[] data = input.split(";");
        String startName = data[0].trim();
        double[] startCoord = parse2DoubleSep(data[1], ",");
        String arrivalName = data[2].trim();
        double[] arrivalCoord = parse2DoubleSep(data[3], ",");
        String line = data[4].trim();
        int[] duration = parse2IntSep(data[5].trim(), ":");
        double distance = Double.parseDouble(data[6].trim());
        plan.addSection(startName, startCoord, arrivalName, arrivalCoord, line, duration, distance);
    }

    /**
     * Parse un fichier CSV décrivant les horaires de départ et les ajoute au plan
     *
     * @param plan le plan où ajouté les horaires
     * @param fileName le nom du fichier à parser
     * @throws FileNotFoundException si le fichier n'a pas été trouvé
     * @throws IncorrectFileFormatException si le format du fichier est incorrect
     * @throws UndefinedLineException si la ligne n'existe pas dans la map
     * @throws StartStationNotFoundException si la ligne n'existe pas sur la ligne
     * @throws DifferentStartException s'il y a plusieurs station de départ pour une même ligne
     */
    public static void addTimeFromCSV(Plan plan, String fileName)
            throws FileNotFoundException, IncorrectFileFormatException, UndefinedLineException,
            StartStationNotFoundException, DifferentStartException {
        if (plan == null || fileName == null)
            throw new IllegalArgumentException();
        File file = new File(fileName);
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                handleTimeLine(plan, sc.nextLine());
            }
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            throw new IncorrectFileFormatException(file.getName());
        }
    }

    /**
     * Parse une ligne d'un fichier CSV contenant un horaire de départ d'une ligne
     *
     * @param plan le plan où ajouter la section
     * @param input la ligne à parser
     * @throws IndexOutOfBoundsException si la ligne est mal formée
     * @throws NumberFormatException si une des données qui doit être un nombre ne l'est pas
     * @throws UndefinedLineException si la ligne n'existe pas dans la map
     * @throws StartStationNotFoundException si la ligne n'existe pas sur la ligne
     * @throws DifferentStartException s'il y a plusieurs station de départ pour une même ligne
     */
    private static void handleTimeLine(Plan plan, String input)
            throws IndexOutOfBoundsException, NumberFormatException, UndefinedLineException,
            StartStationNotFoundException, DifferentStartException, IllegalArgumentException {
        String[] data = input.split(";");
        String line = data[0].trim();
        String stationName = data[1].trim();
        int[] time = parse2IntSep(data[2], ":");
        String variant = data[3].trim();
        String ligneVariant = line + " variant " + variant;
        plan.addDepartureTime(ligneVariant, stationName, time);
    }
}
