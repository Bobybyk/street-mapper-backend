package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.FileNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import app.map.Line.DifferentStartException;
import app.map.Line.StartStationNotFoundException;
import app.map.Plan.UndefinedLineException;
import app.map.PlanParser.InconsistentDataException;
import app.map.PlanParser.IncorrectFileFormatException;

public class PlanTest {
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
    public void nbOfSectionsInLine()
            throws IllegalArgumentException, FileNotFoundException, IncorrectFileFormatException,
            UndefinedLineException, StartStationNotFoundException, DifferentStartException {

        Plan map = initMap(MAP_DATA);
        assertEquals(36, map.getLines().get("8 variant 1").getSections().size(),
                "nombre de sections de cette ligne");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void testSectionsTime() throws FileNotFoundException, IncorrectFileFormatException,
            StartStationNotFoundException, DifferentStartException {

        Plan map = initMap(MAP_DATA);
        Line huit_variant_1 = map.getLines().get("8 variant 1");
        huit_variant_1.setStart("Lourmel");
        List<Section> boucicautSections = huit_variant_1.getSections();
        Section lourmel_boucicaut = null;
        for (Section s : boucicautSections) {
            if (s.getStart().getName().equals("Lourmel")
                    && s.getArrival().getName().equals("Boucicaut")) {
                lourmel_boucicaut = s;
                break;
            }
        }

        assertEquals(-1, huit_variant_1.getSectionsMap().get(lourmel_boucicaut),
                "temps associé au depart egal -1");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void testUpdateSectionsTime() throws FileNotFoundException, IncorrectFileFormatException,
            StartStationNotFoundException, DifferentStartException {

        Plan map = initMap(MAP_DATA);
        Line huit_variant_1 = map.getLines().get("8 variant 1");
        huit_variant_1.setStart("Lourmel");
        List<Section> boucicautSections = huit_variant_1.getSections();
        Section lourmel_boucicaut = null;
        for (Section s : boucicautSections) {
            if (s.getStart().getName().equals("Lourmel")
                    && s.getArrival().getName().equals("Boucicaut")) {
                lourmel_boucicaut = s;
                break;
            }
        }

        huit_variant_1.updateSectionsTime();
        assertEquals(254, huit_variant_1.getSectionsMap().get(lourmel_boucicaut),
                "temps associé au depart egal 254sec");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void testUpdateSectionsTime2()
            throws FileNotFoundException, IncorrectFileFormatException, IllegalArgumentException,
            StartStationNotFoundException, DifferentStartException {

        Plan map = initMap(MAP_DATA);
        Line huit_variant_1 = map.getLines().get("8 variant 1");
        huit_variant_1.setStart("Lourmel");
        List<Section> boucicautSections = huit_variant_1.getSections();
        Section boucicaut_felix_faure = null;
        for (Section s : boucicautSections) {
            if (s.getStart().getName().equals("Boucicaut")
                    && s.getArrival().getName().equals("Félix Faure")) {
                boucicaut_felix_faure = s;
                break;
            }
        }

        huit_variant_1.updateSectionsTime();
        assertEquals(452, huit_variant_1.getSectionsMap().get(boucicaut_felix_faure),
                "temps associé a la section boucicaut_felix_faure egal 452sec");
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
