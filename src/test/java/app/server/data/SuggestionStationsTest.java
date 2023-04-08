package app.server.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import app.map.LignedStation;
import app.map.Map;

public class SuggestionStationsTest {

    private static final String SUGESS_CRET = "Crét";
    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA_ALL = "map_data_all";

    private static String getPath(String filename) {
        return "src/test/resources/" + filename + ".csv";
    }

    public static SuggestionStations createSuggestionStations(String prefix) throws Exception {
        Set<LignedStation> stations = new Map(getPath(MAP_DATA_ALL)).getStations();
        return new SuggestionStations(prefix, stations);
    }

    @Test
    @Timeout(value = DEFAULT_TIMEOUT)
    public void testMultipleStationWithPrefix() throws Exception {
        SuggestionStations s = createSuggestionStations(SUGESS_CRET);
        s.getStations().iterator().forEachRemaining(System.out::println);
        assertTrue(s.getStations().size() == 3);
    }


}
