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
import app.map.Line.DifferentStartException;
import app.map.Line.StartStationNotFoundException;
import app.map.PlanParser.IncorrectFileFormatException;
import app.map.PlanParser.UndefinedLineException;

public class PlanParserTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private static final String MAP_DATA = "map_data";

    private static final String MAP_DATA_ALL = "map_data_all";

    private Plan map;
    private Plan map_all;

    private static String getPath(String filename) {
        if (filename == null)
            return null;
        return "src/test/resources/" + filename + ".csv";
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
        Plan map = PlanParser.planFromSectionCSV(getPath(MAP_DATA));
        BinaryOperator<ArrayList<Section>> accumulator = (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        };
        List<Section> inMap = map.getMap().values().stream().map(ArrayList::new)
                .reduce(new ArrayList<>(), accumulator);
        List<Section> inLines = map.getLines().values().stream().map(Line::getSections)
                .map(ArrayList::new).reduce(new ArrayList<>(), accumulator);
        assertTrue(inLines.containsAll(inMap) && inMap.containsAll(inLines),
                "map and lines field contains the sames sections");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addTimeToLines()
            throws IllegalArgumentException, FileNotFoundException, IncorrectFileFormatException,
            UndefinedLineException, StartStationNotFoundException, DifferentStartException {
        PlanParser.addTimeFromCSV(map_all, getPath("time_data"));
        assertEquals(15, map_all.getLines().get("5 variant 2").getDepartures().size(),
                "nombre de départ de cette ligne");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void nullFileNameTime() throws FileNotFoundException, IncorrectFileFormatException {
        assertThrows(IllegalArgumentException.class, () -> PlanParser.addTimeFromCSV(map, null),
                "null file name");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void notFoundFileTime() throws FileNotFoundException, IncorrectFileFormatException {
        assertThrows(FileNotFoundException.class, () -> PlanParser.addTimeFromCSV(map, "test"),
                "File not found");
    }

    @ParameterizedTest
    @ValueSource(strings = {"time_station_missing", "time_bad_time_format", "time_line_missing"})
    @Timeout(DEFAULT_TIMEOUT)
    public void incorrectTimeFileFormat(String filename)
            throws FileNotFoundException, IncorrectFileFormatException {
        assertThrows(IncorrectFileFormatException.class,
                () -> PlanParser.addTimeFromCSV(map, getPath(filename)), "Incorrect file format");
    }
}
