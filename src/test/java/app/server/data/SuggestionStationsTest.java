package app.server.data;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import app.map.PlanParser;
import app.map.StationInfo;

public class SuggestionStationsTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA_ALL = "map_data_all";

    private static Set<StationInfo> stations = null;

    private static String getPath(String filename) {
        return "src/test/resources/" + filename + ".csv";
    }

    public static SuggestionStations createSuggestionStations(String prefix) throws Exception {
        return new SuggestionStations(prefix, stations);
    }

    public static StationInfo createStationInfo(String stationName, String... lines) {
        return new StationInfo(stationName, Arrays.asList(lines));
    }

    @BeforeAll
    static void init() throws Exception {
        stations = PlanParser.planFromSectionCSV(getPath(MAP_DATA_ALL)).getStationsInfo();
    }

    @ParameterizedTest
    @Timeout(value = DEFAULT_TIMEOUT)
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

    @ParameterizedTest
    @Timeout(value = DEFAULT_TIMEOUT)
    @ValueSource(strings = {"Châtelet", "Chatelet", "chatEl"})
    public void testChatelet(String prefix) throws Exception {
        SuggestionStations s = createSuggestionStations(prefix);
        Set<StationInfo> set = s.getStations();
        StationInfo chatelet = createStationInfo("Châtelet", "1", "4", "7", "11", "14");

        assertTrue(set.size() == 1 && set.contains(chatelet));
    }

    @ParameterizedTest
    @Timeout(value = DEFAULT_TIMEOUT)
    @ValueSource(strings = {"Gare de", "GARE DE", "GaRE De"})
    public void testGareDe(String prefix) throws Exception {
        SuggestionStations s = createSuggestionStations(prefix);
        Set<StationInfo> set = s.getStations();

        var gareDeEst = createStationInfo("Gare de l'Est", "4", "5", "7");

        var gareDeLyon = createStationInfo("Gare de Lyon", "1", "14");

        List<StationInfo> expected = Arrays.asList(gareDeEst, gareDeLyon);
        assertTrue(set.size() == 2 && set.containsAll(expected));
    }

}