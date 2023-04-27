package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.FileNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import app.map.Plan.PathNotFoundException;
import app.map.PlanParser.InconsistentDataException;
import app.map.PlanParser.IncorrectFileFormatException;

public class DijkstraTest {
    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA = "map_data";

    private static final String MAP_DATA_ALL = "map_data_fix_dist_time";

    private static final String TIME_DATA_ALL = "time_data_all";

    private String getPath(String filename) {
        if (filename == null)
            return null;
        return "src/test/resources/" + filename + ".csv";
    }

    private Plan initMap(String filename)
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        return PlanParser.planFromSectionCSV(getPath(filename));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithNullStart()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Plan map = initMap(MAP_DATA);
        assertThrows(IllegalArgumentException.class,
                () -> map.findPathOpt(null, "test", null, true), "Find path from null station");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithNullArrival()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Plan map = initMap(MAP_DATA);
        assertThrows(IllegalArgumentException.class,
                () -> map.findPathOpt("test", null, null, true), "Find path to null station");
    }

    private void pathNotFoundHelper(String start, String arrival)
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Plan map = initMap(MAP_DATA);
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

    private void findPathMapHelper(boolean timedMap, String start, String arrival, int nbLine,
            Time time, boolean distOpt) throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException, InconsistentDataException {
        Plan map = initMap(MAP_DATA_ALL);
        if (timedMap)
            PlanParser.addTimeFromCSV(map, getPath(TIME_DATA_ALL));
        List<Section> trajet = map.findPathOpt(start, arrival, time, distOpt);
        for (Section s : trajet)
            System.out.println(s);
        assertEquals(nbLine, trajet.size(), String.format("%s to %s from %s with %s optimisation",
                start, arrival, time, distOpt ? " distance" : "time"));
    }

    private void findPathMapHelper(String start, String arrival, int nbLine, Time time,
            boolean distOpt) throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException, InconsistentDataException {
        findPathMapHelper(false, start, arrival, nbLine, time, distOpt);
    }

    private void findPathMapHelper(String start, String arrival, int nbLine, Time time)
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException, InconsistentDataException {
        findPathMapHelper(start, arrival, nbLine, time, true);
    }

    private void findPathMapHelper(String start, String arrival, int nbLine)
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException, InconsistentDataException {
        findPathMapHelper(start, arrival, nbLine, null);
    }

    private void findPathMapWithTimeHelper(String start, String arrival, int nbLine, Time time,
            boolean distOpt) throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException, InconsistentDataException {
        findPathMapHelper(true, start, arrival, nbLine, time, distOpt);
    }

    private void findPathMapWithTimeHelper(String start, String arrival, int nbLine, Time time)
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException, InconsistentDataException {
        findPathMapWithTimeHelper(start, arrival, nbLine, time, true);
    }

    private void findPathMapWithTimeHelper(String start, String arrival, int nbLine)
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException, InconsistentDataException {
        findPathMapWithTimeHelper(start, arrival, nbLine, null);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathSameLine() throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException, InconsistentDataException {
        findPathMapHelper("Lourmel", "Commerce", 1);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPath2Line() throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException, InconsistentDataException {
        findPathMapHelper("Cité", "Hôtel de Ville", 2);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPath3Line() throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException, InconsistentDataException {
        findPathMapHelper("Alma - Marceau", "Invalides", 3);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathNordToLyon() throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException, InconsistentDataException {
        findPathMapHelper("Gare du Nord", "Gare de Lyon", 4);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathNordToLyonStartTime()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException, InconsistentDataException {
        findPathMapHelper("Gare du Nord", "Gare de Lyon", 4, new Time(9, 34, 23));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathNordToLyonTimeOpt() throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException, InconsistentDataException {
        findPathMapHelper("Gare du Nord", "Gare de Lyon", 4, new Time(17, 58, 32), true);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathBercyToParmentier() throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException, InconsistentDataException {
        findPathMapHelper("Bercy", "Parmentier", 4);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathBMaisonBlancheToPigalle()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException, InconsistentDataException {
        findPathMapHelper("Maison Blanche", "Pigalle", 6);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithTimeNordToLyon() throws FileNotFoundException, IllegalArgumentException,
            IncorrectFileFormatException, PathNotFoundException, InconsistentDataException {
        findPathMapWithTimeHelper("Gare du Nord", "Gare de Lyon", 4);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithTimeNordToLyonStartTime()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException, InconsistentDataException {
        findPathMapWithTimeHelper("Gare du Nord", "Gare de Lyon", 4, new Time(13, 50, 32));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithTimeNordToLyonStartTimeOpt()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException,
            PathNotFoundException, InconsistentDataException {
        findPathMapWithTimeHelper("Gare du Nord", "Gare de Lyon", 4, new Time(13, 50, 32), true);
    }
}
