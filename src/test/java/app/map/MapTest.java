package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.BinaryOperator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import app.map.Map.IncorrectFileFormatException;
import app.map.Map.PathNotFoundException;

public class MapTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA_ALL = "map_data_fix_dist";

    private String getPath(String filename) {
        return "src/test/resources/" + filename + ".csv";
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void nullFileName() {
        assertThrows(IllegalArgumentException.class, () -> new Map(null), "null file name");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void notFoundFile() {
        assertThrows(FileNotFoundException.class, () -> new Map("test"), "File not found");
    }

    @ParameterizedTest
    @ValueSource(strings = { "arrival_missing", "bad_coord_format", "bad_time_format", "coord_missing",
            "line_missing" })
    @Timeout(DEFAULT_TIMEOUT)
    public void incorrectFileFormat(String filename) {
        assertThrows(IncorrectFileFormatException.class, () -> new Map(getPath(filename)),
                "Incorrect file format");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void sameSectionInMapAndLines()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Map map = new Map(getPath("map_data"));
        BinaryOperator<ArrayList<Section>> accumulator = (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        };
        ArrayList<Section> inMap = map.getMap().values()
                .stream()
                .reduce(new ArrayList<>(), accumulator);

        ArrayList<Section> inLines = map.getLines().values()
                .stream()
                .map(Line::getSections)
                .reduce(new ArrayList<>(), accumulator);
        assertTrue(inLines.containsAll(inMap) && inMap.containsAll(inLines),
                "map and lines field contains the sames sections");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithNullStart()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Map map = new Map(getPath("map_data"));
        assertThrows(IllegalArgumentException.class, () -> map.findPathOpt(null, "test", null, true, false),
                "Find path from null station");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithNullArrival()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Map map = new Map(getPath("map_data"));
        assertThrows(IllegalArgumentException.class, () -> map.findPathOpt("test", null, null, true, false),
                "Find path to null station");
    }

    private void pathNotFoundHelper(String start, String arrival)
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Map map = new Map(getPath("map_data"));
        assertThrows(PathNotFoundException.class, () -> map.findPathOpt(start, arrival, null, true, false),
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

    private void findPathHelper(String start, String arrival, int distance, boolean foot)
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        Map map = new Map(getPath(MAP_DATA_ALL));
        LinkedList<Section> trajet = map.findPathOpt(start, arrival, null, true, foot);
        System.out.println("########");
        for (Section s : trajet)
            System.out.println(s);
        System.out.println("########");
        int n = trajet.stream().reduce(0, (x, s) -> x + s.getDistance(), Integer::sum);
        assertEquals(distance, n, String.format("Distance between %s and %s is %d m", start, arrival, distance));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathSameLine() throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        findPathHelper("Lourmel", "Commerce", 1092, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPath2Line() throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        findPathHelper("Cité", "Hôtel de Ville", 813, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPath3Line() throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        findPathHelper("Alma - Marceau", "Invalides", 1950, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathJussieuToOdeon() throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException {
        findPathHelper("Jussieu", "Odéon", 1310, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathBastilleToRepublique()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        findPathHelper("Bastille", "République", 1643, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathPyramidesToBercy()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        findPathHelper("Pyramides", "Bercy", 4336, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathNordToLyon()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        findPathHelper("Gare du Nord", "Gare de Lyon", 3828, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathNordToLyonFoot()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        findPathHelper("Gare du Nord", "Gare de Lyon", 3828, true);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathBercyToParmentier()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        findPathHelper("Bercy", "Parmentier", 3877, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathBercyToParmentierFoot()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        findPathHelper("Bercy", "Parmentier", 3154, true);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathBMaisonBlancheToPigalle()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        findPathHelper("Maison Blanche", "Pigalle", 8420, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathBMaisonBlancheToPigalleFoot()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        findPathHelper("Maison Blanche", "Pigalle", 7871, true);
    }
}
