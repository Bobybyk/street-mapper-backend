package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import app.map.Line.DifferentStartException;
import app.map.Line.StartStationNotFoundException;
import app.map.Plan.PathNotFoundException;
import app.map.PlanParser.IncorrectFileFormatException;
import app.map.PlanParser.UndefinedLineException;

public class MapTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA_ALL = "map_data_all";

    private static final String MAP_TIME_ALL = "map_time_all";

    private String getPath(String filename) {
        if (filename == null)
            return null;
        return "src/test/resources/" + filename + ".csv";
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void nullFileName() {
        assertThrows(IllegalArgumentException.class, () -> PlanParser.planFromSectionCSV(null),
                "null file name");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void notFoundFile() {
        assertThrows(FileNotFoundException.class, () -> PlanParser.planFromSectionCSV("test"),
                "File not found");
    }

    @ParameterizedTest
    @ValueSource(strings = {"arrival_missing", "bad_coord_format", "bad_time_format",
            "coord_missing", "line_missing"})
    @Timeout(DEFAULT_TIMEOUT)
    public void incorrectFileFormat(String filename) {
        assertThrows(IncorrectFileFormatException.class,
                () -> PlanParser.planFromSectionCSV(getPath(filename)), "Incorrect file format");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void sameSectionInMapAndLines()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Plan map = PlanParser.planFromSectionCSV(getPath("map_data"));
        BinaryOperator<ArrayList<Section>> accumulator = (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        };
        List<Section> inMap = map.getMap().values().stream().map(ArrayList::new)
                .reduce(new ArrayList<>(), accumulator);

        List<Section> inLines = map.getLines().values().stream().map(Line::getSections)
                .map(ArrayList::new).reduce(new ArrayList<>(), accumulator);
        assertTrue(inLines.containsAll(inMap) && inMap.containsAll(inLines),
                "map and lines field contains the sames sections");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithNullStart()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Plan map = PlanParser.planFromSectionCSV(getPath("map_data"));
        assertThrows(IllegalArgumentException.class,
                () -> map.findPathOpt(null, "test", null, true), "Find path from null station");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithNullArrival()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Plan map = PlanParser.planFromSectionCSV(getPath("map_data"));
        assertThrows(IllegalArgumentException.class,
                () -> map.findPathOpt("test", null, null, true), "Find path to null station");
    }

    private void pathNotFoundHelper(String start, String arrival)
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Plan map = PlanParser.planFromSectionCSV(getPath("map_data"));
        assertThrows(PathNotFoundException.class, () -> map.findPathOpt(start, arrival, null, true),
                "Path not found from " + start + " to " + arrival);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void notExistingStart()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        pathNotFoundHelper("test", "Commerce");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void notExistingArrival()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        pathNotFoundHelper("Lourmel", "test");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void notPathBetween()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        pathNotFoundHelper("Commerce", "Lourmel");
    }

    private void findPathHelper(String start, String arrival, int nbLine)
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        Plan map = PlanParser.planFromSectionCSV(getPath(MAP_DATA_ALL));
        List<Section> trajet = map.findPathOpt(start, arrival, null, true);
        assertEquals(nbLine, trajet.size(), start + " to " + arrival);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathSameLine() throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException {
        findPathHelper("Lourmel", "Commerce", 1);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPath2Line() throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException {
        findPathHelper("Cité", "Hôtel de Ville", 2);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPath3Line() throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException {
        findPathHelper("Alma - Marceau", "Invalides", 3);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathNordToLyon() throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException {
        findPathHelper("Gare du Nord", "Gare de Lyon", 4);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathBercyToParmentier() throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException {
        findPathHelper("Bercy", "Parmentier", 4);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathBMaisonBlancheToPigalle() throws FileNotFoundException,
            IllegalArgumentException, IncorrectFileFormatException, PathNotFoundException {
        findPathHelper("Maison Blanche", "Pigalle", 5);
    }

    public void addTimeToLines()
            throws IllegalArgumentException, FileNotFoundException, IncorrectFileFormatException,
            UndefinedLineException, StartStationNotFoundException, DifferentStartException {
        Plan map = PlanParser.planFromSectionCSV(getPath(MAP_DATA_ALL));
        PlanParser.addTimeFromCSV(map, "time_data");
        assertEquals(15, map.getLines().get("5 variant 2").getDepartures().size(),
                "nombre de départ de cette ligne");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void nullFileNameTime() throws FileNotFoundException, IncorrectFileFormatException {
        Plan map = PlanParser.planFromSectionCSV(getPath("map_data"));
        assertThrows(IllegalArgumentException.class, () -> PlanParser.addTimeFromCSV(map, null),
                "null file name");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void notFoundFileTime() throws FileNotFoundException, IncorrectFileFormatException {
        Plan map = PlanParser.planFromSectionCSV(getPath("map_data"));
        assertThrows(FileNotFoundException.class, () -> PlanParser.addTimeFromCSV(map, "test"),
                "File not found");
    }

    @ParameterizedTest
    @ValueSource(strings = {"time_station_missing", "time_bad_time_format", "time_line_missing"})
    @Timeout(DEFAULT_TIMEOUT)
    public void incorrectTimeFileFormat(String filename)
            throws FileNotFoundException, IncorrectFileFormatException {
        Plan map = PlanParser.planFromSectionCSV(getPath("map_data"));
        assertThrows(IncorrectFileFormatException.class,
                () -> PlanParser.addTimeFromCSV(map, getPath(filename)), "Incorrect file format");
    }
}
