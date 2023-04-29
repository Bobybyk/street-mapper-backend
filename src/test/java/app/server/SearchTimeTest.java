package app.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import app.map.Line;
import app.map.Plan;
import app.map.PlanParser;
import app.map.PlanParser.InconsistentDataException;
import app.map.PlanParser.IncorrectFileFormatException;
import app.map.Station;
import app.map.Time;

public class SearchTimeTest {
    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA = "map_data";

    private static final String MAP_DATA_ALL = "map_data_fix_dist_time";

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
    public void testDeparturesFromStation()
            throws FileNotFoundException, IncorrectFileFormatException, InconsistentDataException,
            IllegalArgumentException, StartStationNotFoundException, DifferentStartException {

        Plan map = initMap(MAP_DATA_ALL);
        PlanParser.addTimeFromCSV(map, getPath("time_data_ligne8"));
        Line huit_variant_1 = map.getLines().get("8 variant 1");
        huit_variant_1.setStart("Lourmel");
        huit_variant_1.updateSectionsTime();

        Station station = map.getMap().get("Félix Faure").get(0).getStart();

        assertEquals(17, map.departuresFromStation(station).get("8 variant 1").size(),
                "nombre d'horaires depuis Félix Faure sur la ligne 8 variant 1");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void testDeparturesFromStationFromTime()
            throws FileNotFoundException, IncorrectFileFormatException, InconsistentDataException,
            IllegalArgumentException, StartStationNotFoundException, DifferentStartException {

        Plan map = initMap(MAP_DATA_ALL);
        PlanParser.addTimeFromCSV(map, getPath("time_data_ligne8"));
        Line huit_variant_1 = map.getLines().get("8 variant 1");
        huit_variant_1.setStart("Lourmel");
        huit_variant_1.updateSectionsTime();

        Station station = map.getMap().get("Félix Faure").get(0).getStart();
        Time time = new Time(16, 0, 0);

        assertEquals(12, map.departuresFromStationFromTime(station, time).get("8 variant 1").size(),
                "nombre d'horaires depuis Félix Faure sur la ligne 8 variant 1 depuis 16h");
    }
}
