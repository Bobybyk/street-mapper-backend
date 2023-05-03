package app.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import app.map.Line.DifferentStartException;
import app.map.Line.StationNotFoundException;
import app.map.Plan.UndefinedLineException;
import app.util.Parser;

/**
 * Parser de fichier CSV pour plan
 */
public final class PlanParser {
    /**
     * Exception pour un fichier mal formé
     */
    public static class IncorrectFileFormatException extends Exception {
        public IncorrectFileFormatException(String filename) {
            super(String.format("Le fichier %s n'est pas bien formé", filename));
        }
    }

    public static class InconsistentDataException extends Exception {
        public InconsistentDataException(String line) {
            super(line);
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
        double[] startCoord = Parser.parse2DoubleSep(data[1], ",");
        String arrivalName = data[2].trim();
        double[] arrivalCoord = Parser.parse2DoubleSep(data[3], ",");
        String line = data[4].trim();
        int[] duration = Parser.parse2IntSep(data[5].trim(), ":");
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
     * @throws InconsistentDataException si les données du fichier ne correspondent pas avec le plan
     */
    public static void addTimeFromCSV(Plan plan, String fileName)
            throws FileNotFoundException, IncorrectFileFormatException, InconsistentDataException {
        if (plan == null || fileName == null)
            throw new IllegalArgumentException();
        File file = new File(fileName);
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                handleTimeLine(plan, sc.nextLine());
            }
            plan.updateSectionsTime();
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            throw new IncorrectFileFormatException(file.getName());
        } catch (UndefinedLineException | StationNotFoundException | DifferentStartException e) {
            throw new InconsistentDataException(e.getMessage());
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
            StationNotFoundException, DifferentStartException, IllegalArgumentException {
        String[] data = input.split(";");
        String line = data[0].trim();
        String stationName = data[1].trim();
        int[] time = Parser.parse2IntSep(data[2], ":");
        String variant = data[3].trim();
        String ligneVariant = line + " variant " + variant;
        plan.addDepartureTime(ligneVariant, stationName, time);
    }
}
