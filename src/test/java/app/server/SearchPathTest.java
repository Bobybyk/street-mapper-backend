package app.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import app.map.Plan;
import app.map.PlanParser;
import app.map.Section;
import app.map.Time;
import app.server.data.ErrorServer;
import app.server.data.Route;

public class SearchPathTest {
    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA = "map_data";

    private static final String MAP_DATA_ALL = "map_data_fix_dist_time";

    private static final String TIME_DATA_ALL = "time_data_all";

    private String getPath(String filename) {
        if (filename == null)
            return null;
        return "src/test/resources/" + filename + ".csv";
    }

    private Plan initMap(String filename) throws Exception {
        return PlanParser.planFromSectionCSV(getPath(filename));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithNullStart() throws Exception {
        Plan map = initMap(MAP_DATA);
        assertThrows(IllegalArgumentException.class,
                () -> new SearchPath(map, null, "test", null, true), "Find path from null station");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithNullArrival() throws Exception {
        Plan map = initMap(MAP_DATA);
        assertThrows(IllegalArgumentException.class,
                () -> new SearchPath(map, "test", null, null, true), "Find path to null station");
    }

    private void pathNotFoundHelper(String start, String arrival) throws Exception {
        Plan map = initMap(MAP_DATA);
        assertEquals("Trajet inexistant",
                ((ErrorServer) new SearchPath(map, start, arrival, null, true).execute())
                        .getError(),
                "Path not found from " + start + " to " + arrival);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void notExistingStart() throws Exception {
        pathNotFoundHelper("test", "Commerce");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void notExistingArrival() throws Exception {
        pathNotFoundHelper("Lourmel", "test");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void notPathBetween() throws Exception {
        pathNotFoundHelper("Commerce", "Lourmel");
    }

    private void findPathMapHelper(boolean timedMap, String start, String arrival, int nbLine,
            Time time, boolean distOpt) throws Exception {
        Plan map = initMap(MAP_DATA_ALL);
        if (timedMap)
            PlanParser.addTimeFromCSV(map, getPath(TIME_DATA_ALL));
        Route route = (Route) new SearchPath(map, start, arrival, time, distOpt).execute();
        List<Section> trajet = route.getPathDistOpt();
        for (Section s : trajet)
            System.out.println(s);
        assertEquals(nbLine, trajet.size(), String.format("%s to %s from %s with %s optimisation",
                start, arrival, time, distOpt ? " distance" : "time"));
    }

    private void findPathMapHelper(String start, String arrival, int nbLine, Time time,
            boolean distOpt) throws Exception {
        findPathMapHelper(false, start, arrival, nbLine, time, distOpt);
    }

    private void findPathMapHelper(String start, String arrival, int nbLine, Time time)
            throws Exception {
        findPathMapHelper(start, arrival, nbLine, time, true);
    }

    private void findPathMapHelper(String start, String arrival, int nbLine) throws Exception {
        findPathMapHelper(start, arrival, nbLine, null);
    }

    private void findPathMapWithTimeHelper(String start, String arrival, int nbLine, Time time,
            boolean distOpt) throws Exception {
        findPathMapHelper(true, start, arrival, nbLine, time, distOpt);
    }

    private void findPathMapWithTimeHelper(String start, String arrival, int nbLine, Time time)
            throws Exception {
        findPathMapWithTimeHelper(start, arrival, nbLine, time, true);
    }

    private void findPathMapWithTimeHelper(String start, String arrival, int nbLine)
            throws Exception {
        findPathMapWithTimeHelper(start, arrival, nbLine, null);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathSameLine() throws Exception {
        findPathMapHelper("Lourmel", "Commerce", 1);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPath2Line() throws Exception {
        findPathMapHelper("Cité", "Hôtel de Ville", 2);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPath3Line() throws Exception {
        findPathMapHelper("Alma - Marceau", "Invalides", 3);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathNordToLyon() throws Exception {
        findPathMapHelper("Gare du Nord", "Gare de Lyon", 4);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathNordToLyonStartTime() throws Exception {
        findPathMapHelper("Gare du Nord", "Gare de Lyon", 4, new Time(9, 34, 23));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathNordToLyonTimeOpt() throws Exception {
        findPathMapHelper("Gare du Nord", "Gare de Lyon", 4, new Time(17, 58, 32), true);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathBercyToParmentier() throws Exception {
        findPathMapHelper("Bercy", "Parmentier", 4);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathBMaisonBlancheToPigalle() throws Exception {
        findPathMapHelper("Maison Blanche", "Pigalle", 6);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithTimeNordToLyon() throws Exception {
        findPathMapWithTimeHelper("Gare du Nord", "Gare de Lyon", 4);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithTimeNordToLyonStartTime() throws Exception {
        findPathMapWithTimeHelper("Gare du Nord", "Gare de Lyon", 4, new Time(13, 50, 32));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithTimeNordToLyonStartTimeOpt() throws Exception {
        findPathMapWithTimeHelper("Gare du Nord", "Gare de Lyon", 4, new Time(13, 50, 32), true);
    }
}
