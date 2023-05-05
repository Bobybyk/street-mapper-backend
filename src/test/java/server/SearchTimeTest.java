package server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import server.data.DepartureTimes;
import server.data.StationTime;
import server.map.Plan;
import server.map.PlanParser;
import server.map.Time;

public class SearchTimeTest {
    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA = "map_data_all";

    private static final String TIME_DATA = "time_data_all";

    private static Plan map;

    @BeforeAll
    static void init() throws Exception {
        map = PlanParser.planFromSectionCSV(getPath(MAP_DATA));
        PlanParser.addTimeFromCSV(map, getPath(TIME_DATA));
    }

    private void illegalArgumentHelper(Plan plan, String start, Time time) {
        assertThrows(IllegalArgumentException.class, () -> new SearchTime(plan, start, time),
                "null value");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithNullStart() throws Exception {
        init();
        illegalArgumentHelper(map, null, new Time(14, 22));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithNullTime() throws Exception {
        init();
        illegalArgumentHelper(map, "test", null);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void findPathWithNullMap() throws Exception {
        illegalArgumentHelper(null, "test", new Time(18, 30));
    }

    private static String getPath(String filename) {
        if (filename == null)
            return null;
        return "src/test/resources/" + filename + ".csv";
    }

    static SearchTime createSearchTime(String station, Time time) {
        return new SearchTime(map, station, time);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void timeAtMadeleine() {
        SearchTime search = createSearchTime("Madeleine", new Time(15, 40));
        List<StationTime> times = ((DepartureTimes) search.execute()).getTimes();
        StationTime expected = new StationTime("12", "Mairie d'Issy", new Time(15, 43, 54));
        assertEquals(expected, times.get(19), "Times at a Madeleine");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void timeAtPorteDophne() {
        SearchTime search = createSearchTime("Nation", new Time(6, 4, 0));
        List<StationTime> times = ((DepartureTimes) search.execute()).getTimes();
        StationTime expected = new StationTime("2", "Porte Dauphine", new Time(6, 5, 0));
        assertEquals(expected, times.get(0), "Times at Nation");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void timeAtChateletAfterMidnight() {
        SearchTime search = createSearchTime("Châtelet", new Time(23, 59));
        List<StationTime> times = ((DepartureTimes) search.execute()).getTimes();
        StationTime expected = new StationTime("14", "Mairie de Saint-Ouen", new Time(6, 18, 00));
        assertEquals(expected, times.get(10), "Times at a Châtelet at 23:59");
    }
}
