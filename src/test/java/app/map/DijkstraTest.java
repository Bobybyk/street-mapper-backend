package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.FileNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import app.map.Plan.PathNotFoundException;
import app.map.PlanParser.IncorrectFileFormatException;

public class DijkstraTest {
    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA = "map_data";

    private static final String MAP_DATA_ALL = "map_data_all";

    private static final String TIME_DATA_ALL = "time_data_all";

    private String getPath(String filename) {
        if (filename == null)
            return null;
        return "src/test/resources/" + filename + ".csv";
    }

    private Plan initMap()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        return PlanParser.planFromSectionCSV(getPath(MAP_DATA));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithNullStart()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Plan map = initMap();
        assertThrows(IllegalArgumentException.class,
                () -> map.findPathOpt(null, "test", null, true), "Find path from null station");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithNullArrival()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Plan map = initMap();
        assertThrows(IllegalArgumentException.class,
                () -> map.findPathOpt("test", null, null, true), "Find path to null station");
    }

    private void pathNotFoundHelper(String start, String arrival)
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Plan map = initMap();
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
}
