package app.server.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import app.map.StationInfo;
import app.map.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SuggestionStationsTest {

    private static final String SUGESS_CRET = "Crét";
    private static final String SUGESS_CHATELET = "Châtelet";
    private static final String SUGESS_GARE = "Gare de";
    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA_ALL = "map_data_all";

    private static String getPath(String filename) {
        return "src/test/resources/" + filename + ".csv";
    }

    public static SuggestionStations createSuggestionStations(String prefix) throws Exception {
        Set<StationInfo> stations = new Map(getPath(MAP_DATA_ALL)).getStationsInfo();
        return new SuggestionStations(prefix, stations);
    }

    public static StationInfo createStationInfo(String stationName, String... lines) {
        return new StationInfo(stationName, Arrays.asList(lines));
    }

    @Timeout(value = DEFAULT_TIMEOUT)
    @ParameterizedTest
    @ValueSource(strings = {"cret", "CRÉt", "CrÉt"})
    public void testMultipleStationWithPrefixIgnoreCaseSpecialChar(String prefix) throws Exception {
        SuggestionStations s = createSuggestionStations(prefix);
        Set<StationInfo> set = s.getStations();
        StationInfo creteilPref = createStationInfo("Créteil - Préfecture", "8");
        StationInfo creteilUni = createStationInfo("Créteil - Université", "8");
        StationInfo creteilEcha = createStationInfo("Créteil - L'Échat", "8");

        List<StationInfo> expected = Arrays.asList(creteilPref, creteilUni, creteilEcha);
        assertTrue(set.size() == 3 && set.containsAll(expected));

    }

    @Test
    @Timeout(value = DEFAULT_TIMEOUT)
    public void testMultipleStationWithPrefix() throws Exception {
        SuggestionStations s = createSuggestionStations(SUGESS_CRET);
        Set<StationInfo> set = s.getStations();
        StationInfo creteilPref = createStationInfo("Créteil - Préfecture", "8");
        StationInfo creteilUni = createStationInfo("Créteil - Université", "8");
        StationInfo creteilEcha = createStationInfo("Créteil - L'Échat", "8");

        List<StationInfo> expected = Arrays.asList(creteilPref, creteilUni, creteilEcha);
        assertTrue(set.size() == 3 && set.containsAll(expected));
        
    }

    @Test
    @Timeout(value = DEFAULT_TIMEOUT)
    public void testChatelet() throws Exception {
        SuggestionStations s = createSuggestionStations(SUGESS_CHATELET);
        Set<StationInfo> set = s.getStations();
        StationInfo chatelet = createStationInfo("Châtelet", "1", "4", "7", "11", "14");
        
        assertTrue(set.size() == 1 && set.contains(chatelet));
    }

    @Test
    @Timeout(value = DEFAULT_TIMEOUT)
    public void testGareDe() throws Exception {
        SuggestionStations s = createSuggestionStations(SUGESS_GARE);
        Set<StationInfo> set = s.getStations();

        var gareDeEst = createStationInfo("Gare de l'Est", "4", "5", "7");

        var gareDeLyon = createStationInfo("Gare de Lyon", "1", "14");

        List<StationInfo> expected = Arrays.asList(gareDeEst, gareDeLyon);
        assertTrue(set.size() == 5 && set.containsAll(expected));
    }

}
