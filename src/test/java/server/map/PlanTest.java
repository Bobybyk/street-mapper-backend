package server.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class PlanTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA = "map_data_ligne8";

    private static final String TIME_DATA = "time_data_ligne8";

    private final Plan plan;

    private final Section unknownSection =
            new Section(new Station("", 0, 0), new Station("", 0, 0), "", 0, 0);


    private String getPath(String filename) {
        if (filename == null)
            return null;
        return "src/test/resources/" + filename + ".csv";
    }

    PlanTest() throws Exception {
        plan = PlanParser.planFromSectionCSV(getPath(MAP_DATA));
        PlanParser.addTimeFromCSV(plan, getPath(TIME_DATA));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void copieEqualsMap() {
        assertEquals(plan.getMap(), new Plan(plan).getMap(), "Plan copie");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void copieEqualsLine() {
        assertEquals(plan.getLines(), new Plan(plan).getLines(), "Plan copie");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void copieEqualsInfo() {
        assertEquals(plan.getStationsInfo(), new Plan(plan).getStationsInfo(), "Plan copie");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void updateSectionTimeNullSection() {
        plan.updateSectionTime(null, new Time(0, 0));
        assertEquals(plan.getMap(), new Plan(plan).getMap(), "Update null section");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void updateSectionTimeUnknownSection() {
        plan.updateSectionTime(unknownSection, new Time(0, 0));
        assertEquals(plan.getMap(), new Plan(plan).getMap(), "Update null section");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void getLineNameNullSection() {
        assertNull(plan.getLineName(null), "Line name of null section");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void getLineNameUnknownSection() {
        assertNull(plan.getLineName(unknownSection), "Line name of unknown section");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void getLineNullSection() {
        assertNull(plan.getLine(null), "Line of null section");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void getLineUnknownSection() {
        assertNull(plan.getLine(unknownSection), "Line of unknown section");
    }
}
