package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import app.map.PlanParser.InconsistentDataException;
import app.map.PlanParser.IncorrectFileFormatException;

public class PlanParserTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA = "map_data";

    private static final String MAP_DATA_ALL = "map_data_all";

    private static String getPath(String filename) {
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
    public void nullFileName() {
        assertThrows(IllegalArgumentException.class, () -> PlanParser.planFromSectionCSV(null),
                "null file name");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void notFoundFile() {
        assertThrows(FileNotFoundException.class, () -> PlanParser.planFromSectionCSV("test"),
                "File not found");
    }

    @ParameterizedTest
    @ValueSource(strings = {"arrival_missing", "bad_coord_format", "bad_time_format",
            "coord_missing", "line_missing"})
    @Timeout(DEFAULT_TIMEOUT)
    public void incorrectFileFormat(String filename) {
        assertThrows(IncorrectFileFormatException.class,
                () -> PlanParser.planFromSectionCSV(getPath(filename)), "Incorrect file format");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void sameSectionInMapAndLines()
            throws FileNotFoundException, IllegalArgumentException, IncorrectFileFormatException {
        Plan map = initMap(MAP_DATA);
        BinaryOperator<ArrayList<Section>> accumulator = (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        };
        List<Section> inMap = map.getMap().values().stream().map(ArrayList::new)
                .reduce(new ArrayList<>(), accumulator);
        List<Section> inLines = map.getLines().values().stream().map(Line::getSections)
                .map(ArrayList::new).reduce(new ArrayList<>(), accumulator);
        assertTrue(inLines.containsAll(inMap) && inMap.containsAll(inLines),
                "Map and lines field contains the sames sections");
    }

    private Plan addTimeHelper(String mapFilename, String timeFilename)
            throws FileNotFoundException, IncorrectFileFormatException, InconsistentDataException {
        Plan map = initMap(mapFilename);
        PlanParser.addTimeFromCSV(map, getPath(timeFilename));
        return map;
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void nullFileNameTime() {
        assertThrows(IllegalArgumentException.class, () -> addTimeHelper(MAP_DATA, null),
                "null file name");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void notFoundFileTime() {
        assertThrows(FileNotFoundException.class, () -> addTimeHelper(MAP_DATA, "test"),
                "File not found");
    }

    @ParameterizedTest
    @ValueSource(strings = {"time_station_missing", "time_bad_time_format", "time_line_missing"})
    @Timeout(DEFAULT_TIMEOUT)
    public void incorrectTimeFileFormat(String filename) {
        assertThrows(IncorrectFileFormatException.class, () -> addTimeHelper(MAP_DATA, filename),
                "Incorrect file format");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addUndefinedLine() {
        assertThrows(InconsistentDataException.class, () -> addTimeHelper(MAP_DATA, "time_data"),
                "Add time to a not existing line");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addStartStationNotFound() {
        assertThrows(InconsistentDataException.class,
                () -> addTimeHelper(MAP_DATA, "time_unknow_station"),
                "Add time to a not existing station");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addDifferentStart() {
        assertThrows(InconsistentDataException.class,
                () -> addTimeHelper(MAP_DATA, "time_two_start"),
                "Add two different start station for same line");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addTimeToLines() throws IllegalArgumentException, FileNotFoundException,
            IncorrectFileFormatException, InconsistentDataException {
        Plan map = addTimeHelper(MAP_DATA_ALL, "time_data");
        assertEquals(15, map.getLines().get("5 variant 2").getDepartures().size(),
                "Add time to line");
    }
}
