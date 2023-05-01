package app.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import app.map.Plan;
import app.map.PlanParser;
import app.map.Time;
import app.server.data.DepartureTimes;
import app.server.data.StationTime;

public class SearchTimeTest {
    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA = "map_data_all";

    private static final String TIME_DATA = "time_data_all";

    private static Plan map;

    private static String getPath(String filename) {
        if (filename == null)
            return null;
        return "src/test/resources/" + filename + ".csv";
    }

    static SearchTime createSearchTime(String station, Time time) {
        return new SearchTime(map, station, time);
    }

    @BeforeAll
    static void init() throws Exception {
        map = PlanParser.planFromSectionCSV(getPath(MAP_DATA));
        PlanParser.addTimeFromCSV(map, getPath(TIME_DATA));
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
    public void timeAtChateletAfterMidnight() {
        SearchTime search = createSearchTime("Châtelet", new Time(23, 59));
        List<StationTime> times = ((DepartureTimes) search.execute()).getTimes();
        StationTime expected = new StationTime("14", "Mairie de Saint-Ouen", new Time(6, 18, 00));
        assertEquals(expected, times.get(10), "Times at a Châtelet at 23:59");
    }
}
