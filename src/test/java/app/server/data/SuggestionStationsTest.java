package app.server.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import app.map.LignedStation;
import app.map.Map;
import app.map.Station;

public class SuggestionStationsTest {

    private static final String SUGESS_CRET = "Crét";
    private static final String SUGESS_CHATELET = "Châtelet";
    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA_ALL = "map_data_all";

    private static String getPath(String filename) {
        return "src/test/resources/" + filename + ".csv";
    }

    public static SuggestionStations createSuggestionStations(String prefix) throws Exception {
        Set<LignedStation> stations = new Map(getPath(MAP_DATA_ALL)).getStations();
        return new SuggestionStations(prefix, stations);
    }

    public static LignedStation createStation(String stationName, String Ligne) {
        return new LignedStation(new Station(stationName, 0, 0), Ligne);
    }

    @Test
    @Timeout(value = DEFAULT_TIMEOUT)
    public void testMultipleStationWithPrefix() throws Exception {
        SuggestionStations s = createSuggestionStations(SUGESS_CRET);
        Set<LignedStation> set = s.getStations();
        LignedStation creteilPref = createStation("Créteil - Préfecture", "8");
        LignedStation creteilUni = createStation("Créteil - Université", "8");
        LignedStation creteilEcha = createStation("Créteil - L'Échat", "8");

        List<LignedStation> expected = Arrays.asList(creteilPref, creteilUni, creteilEcha);
        assertTrue(set.size() == 3 && set.containsAll(expected));
        
    }

    @Test
    @Timeout(value = DEFAULT_TIMEOUT)
    public void testChatelet() throws Exception {
        SuggestionStations s = createSuggestionStations(SUGESS_CHATELET);
        Set<LignedStation> set = s.getStations();
        LignedStation chatelet14 = createStation("Châtelet", "14");
        LignedStation chatelet11 = createStation("Châtelet", "11");
        LignedStation chatelet7 = createStation("Châtelet", "7");
        LignedStation chatelet4 = createStation("Châtelet", "4");
        LignedStation chatelet1 = createStation("Châtelet", "1");
        
        List<LignedStation> expected = Arrays.asList(chatelet1, chatelet4, chatelet7, chatelet11, chatelet14);
        assertTrue(set.size() == 5 && set.containsAll(expected));
    }

}
