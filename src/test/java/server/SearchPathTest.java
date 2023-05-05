package server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import server.data.ErrorServer;
import server.data.Route;
import server.map.Plan;
import server.map.PlanParser;
import server.map.Section;
import server.map.Time;

class SearchPathTest {
    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA = "map_data";

    private static final String MAP_DATA_ALL = "map_data_fix_dist_time";

    private static final String TIME_DATA_ALL = "time_data_all";

    private void illegalArgumentHelper(Plan plan, String start, String arrival) {
        assertThrows(IllegalArgumentException.class,
                () -> new SearchPath(plan, start, arrival, null, true, false), "null value");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithNullStart() throws Exception {
        Plan map = initMap(MAP_DATA);
        illegalArgumentHelper(map, null, "test");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithNullArrival() throws Exception {
        Plan map = initMap(MAP_DATA);
        illegalArgumentHelper(map, "test", null);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithNullMap() throws Exception {
        illegalArgumentHelper(null, "test", "toto");
    }

    private String getPath(String filename) {
        if (filename == null)
            return null;
        return "src/test/resources/" + filename + ".csv";
    }

    private Plan initMap(String filename) throws Exception {
        return PlanParser.planFromSectionCSV(getPath(filename));
    }

    private void pathNotFoundHelper(String start, String arrival, Time time, boolean distOpt,
            boolean foot) throws Exception {
        Plan map = initMap(MAP_DATA);
        assertEquals("Trajet inexistant",
                ((ErrorServer) new SearchPath(map, start, arrival, time, distOpt, foot).execute())
                        .getError(),
                String.format("Path not found from %s to %s", start, arrival));
    }

    private void pathNotFoundHelperDistOpt(String start, String arrival) throws Exception {
        pathNotFoundHelper(start, arrival, null, true, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void notExistingStart() throws Exception {
        pathNotFoundHelperDistOpt("test", "Commerce");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void notExistingArrival() throws Exception {
        pathNotFoundHelperDistOpt("Lourmel", "test");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void notPathBetween() throws Exception {
        pathNotFoundHelperDistOpt("Commerce", "Lourmel");
    }

    private void findPathMapHelper(boolean timedMap, String start, String arrival, int nbLine,
            Time time, boolean distOpt, boolean foot) throws Exception {
        Plan map = initMap(MAP_DATA_ALL);
        if (timedMap)
            PlanParser.addTimeFromCSV(map, getPath(TIME_DATA_ALL));
        Route route = (Route) new SearchPath(map, start, arrival, time, distOpt, foot).execute();
        List<Section> trajet = route.getPathDistOpt();
        assertEquals(nbLine, trajet.size(), String.format("%s to %s from %s with %s optimisation",
                start, arrival, time, distOpt ? " distance" : "time"));
    }

    private void findPathMapHelper(String start, String arrival, int nbLine, Time time,
            boolean distOpt, boolean foot) throws Exception {
        findPathMapHelper(false, start, arrival, nbLine, time, distOpt, foot);
    }

    private void findPathMapHelper(String start, String arrival, int nbLine, Time time,
            boolean foot) throws Exception {
        findPathMapHelper(start, arrival, nbLine, time, true, foot);
    }

    private void findPathMapHelper(String start, String arrival, int nbLine, boolean foot)
            throws Exception {
        findPathMapHelper(start, arrival, nbLine, null, foot);
    }

    private void findPathMapWithTimeHelper(String start, String arrival, int nbLine, Time time,
            boolean distOpt, boolean foot) throws Exception {
        findPathMapHelper(true, start, arrival, nbLine, time, distOpt, foot);
    }

    private void findPathMapWithTimeHelper(String start, String arrival, int nbLine, Time time,
            boolean foot) throws Exception {
        findPathMapWithTimeHelper(start, arrival, nbLine, time, true, foot);
    }

    private void findPathMapWithTimeHelper(String start, String arrival, int nbLine, boolean foot)
            throws Exception {
        findPathMapWithTimeHelper(start, arrival, nbLine, null, foot);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathSameLine() throws Exception {
        findPathMapHelper("Lourmel", "Commerce", 3, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPath2Line() throws Exception {
        findPathMapHelper("Cité", "Hôtel de Ville", 2, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPath3Line() throws Exception {
        findPathMapHelper("Alma - Marceau", "Invalides", 3, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathNordToLyon() throws Exception {
        findPathMapHelper("Gare du Nord", "Gare de Lyon", 8, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathNordToLyonStartTime() throws Exception {
        findPathMapHelper("Gare du Nord", "Gare de Lyon", 8, new Time(9, 34, 23), false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathNordToLyonTimeOpt() throws Exception {
        pathNotFoundHelper("Gare du Nord", "Gare de Lyon", new Time(17, 58, 32), false, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathBercyToParmentier() throws Exception {
        findPathMapHelper("Bercy", "Parmentier", 7, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathBMaisonBlancheToPigalle() throws Exception {
        findPathMapHelper("Maison Blanche", "Pigalle", 17, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeNordToLyon() throws Exception {
        findPathMapWithTimeHelper("Gare du Nord", "Gare de Lyon", 8, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeNordToLyonStartTime() throws Exception {
        findPathMapWithTimeHelper("Gare du Nord", "Gare de Lyon", 8, new Time(13, 50, 32), false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeNordToLyonStartTimeOpt() throws Exception {
        findPathMapWithTimeHelper("Gare du Nord", "Gare de Lyon", 15, new Time(13, 50, 32), false,
                false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeSameLineTimeOpt() throws Exception {
        findPathMapWithTimeHelper("Balard", "Félix Faure", 3, new Time(13, 50, 32), false, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeJussieuToOdeonTimeOpt() throws Exception {
        findPathMapWithTimeHelper("Jussieu", "Odéon", 4, new Time(14, 50), false, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeBastilleToRepubliqueTimeOpt() throws Exception {
        findPathMapWithTimeHelper("Bastille", "République", 4, new Time(8, 40), false, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimePyramidesToBercyTimeOpt() throws Exception {
        findPathMapWithTimeHelper("Pyramides", "Bercy", 18, new Time(8, 3), false, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeBercyToParmentierTimeOpt() throws Exception {
        findPathMapWithTimeHelper("Bercy", "Parmentier", 7, new Time(12, 40), true, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeBercyToParmentierTimeOptFoot() throws Exception {
        findPathMapWithTimeHelper("Bercy", "Parmentier", 5, new Time(12, 40), true, true);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeMaisonBlancheToPigalleTimeOpt() throws Exception {
        findPathMapWithTimeHelper("Maison Blanche", "Pigalle", 17, new Time(12, 32), true, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeMaisonBlancheToPigalleTimeOptFoot() throws Exception {
        findPathMapWithTimeHelper("Maison Blanche", "Pigalle", 14, new Time(12, 32), true, true);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeStartCoord() throws Exception {
        findPathMapWithTimeHelper("(48.83866086365990, 2.2822419598550800)", "Commerce", 4,
                new Time(12, 32), true, true);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeArrivalCoord() throws Exception {
        findPathMapWithTimeHelper("Lourmel", "(48.84461151236850,2.293796842192860)", 4,
                new Time(12, 32), true, true);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeStartArrivalCoord() throws Exception {
        findPathMapWithTimeHelper("(48.83866086365990, 2.2822419598550800)",
                "(48.84461151236850,2.293796842192860)", 5, new Time(12, 32), true, true);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeCoords() throws Exception {
        findPathMapWithTimeHelper("(48.855402921055045, 2.3443066430543738)",
                "(48.84718353452897, 2.398076946926344)", 9, new Time(9, 0), true, true);

    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathWithTimeCoordsMaisonBlancheToPigalleTimeOptFoot() throws Exception {
        findPathMapWithTimeHelper("(48.824868685169676, 2.358546268381532)",
                "(48.88264085646782, 2.3401402839553964)", 15, new Time(12, 32), true, true);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathStartFarFromStation() throws Exception {
        findPathMapWithTimeHelper("(48.76844682672424,2.3622296824389313)", "Châtelet", 14,
                new Time(6, 32), true, false);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void findPathArrivalFarFromStation() throws Exception {
        findPathMapWithTimeHelper("Châtelet", "(48.76844682672424,2.3622296824389313)", 14,
                new Time(6, 32), true, false);
    }
}
