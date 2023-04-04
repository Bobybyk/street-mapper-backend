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
        assertThrows(IllegalArgumentException.class, () -> map.findPathDistOpt(null, "test"),
                "Find path from null station");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithNullArrival()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Map map = new Map(getPath("map_data"));
        assertThrows(IllegalArgumentException.class, () -> map.findPathDistOpt("test", null),
                "Find path to null station");
    }

    private void pathNotFoundHelper(String start, String arrival)
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Map map = new Map(getPath("map_data"));
        assertThrows(PathNotFoundException.class, () -> map.findPathDistOpt(start, arrival),
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

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathSameLine() throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        Map map = new Map(getPath("map_data"));
        LinkedList<Section> trajet = map.findPathDistOpt("Lourmel", "Commerce");
        assertEquals(3, trajet.size(), "Lourmel to Commerce");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPath2Line() throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        Map map = new Map(getPath("map_data_all"));
        LinkedList<Section> trajet = map.findPathDistOpt("Cité", "Hôtel de Ville");
        assertEquals(2, trajet.size(), "Cité to Hôtel de Ville");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPath3Line() throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        Map map = new Map(getPath("map_data_all"));
        LinkedList<Section> trajet = map.findPathDistOpt("Alma - Marceau", "Invalides");
        assertEquals(3, trajet.size(), "Alma - Marceau to Invalides");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathNordToLyon()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        Map map = new Map(getPath("map_data_all"));
        LinkedList<Section> trajet = map.findPathDistOpt("Gare du Nord", "Gare de Lyon");
        assertEquals(8, trajet.size(), "Gare du Nord to Gare de Lyon");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathBercyToParmentier()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException {
        Map map = new Map(getPath("map_data_all"));
        LinkedList<Section> trajet = map.findPathDistOpt("Bercy", "Parmentier");
        assertEquals(9, trajet.size(), "Bercy to Parmentier");
    }
}
