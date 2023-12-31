package server.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import server.map.Line.DifferentStartException;
import server.map.Line.StationNotFoundException;

class LineTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private final Line line = new Line("test", "0");
    private final Station t1 = new Station("A", 0, 0);
    private final Station t2 = new Station("B", 0, 0);
    private final Station t3 = new Station("C", 0, 0);
    private final Section s1 = new Section(t1, t2, "test variant 0", 0, 10);
    private final Section s2 = new Section(t2, t3, "test variant 0", 0, 15);

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void nullName() {
        assertThrows(IllegalArgumentException.class, () -> new Line(null, "0"), "null line name");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void addNullSection() {
        assertThrows(IllegalArgumentException.class, () -> line.addSection(null),
                "Add null section");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void setNullStart() {
        assertThrows(IllegalArgumentException.class, () -> line.setStart(null),
                "Set null as start");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void setStartWithNotExistingStation() {
        assertThrows(StationNotFoundException.class, () -> line.setStart("test"),
                "Start station not in line");
    }

    private void initLine() {
        line.addSection(s1);
        line.addSection(s2);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void setTwoDifferentStart() throws Exception {
        initLine();
        line.setStart("A");
        assertThrows(DifferentStartException.class, () -> {
            line.setStart("B");
        }, "Set two different start");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void setStartNotNull() throws Exception {
        initLine();
        line.setStart("A");
        assertEquals(s1, line.getStart(), "Set start");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void setStartNotNullTwice() throws Exception {
        initLine();
        line.setStart("A");
        line.setStart("A");
        assertEquals(s1, line.getStart(), "Set the same start twice");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void addDepartureTime() {
        line.addDepartureTime(12, 34);
        Time expected = new Time(12, 34);
        assertEquals(expected, line.getDepartures().get(0), "Add a departure time");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void addDepartureTimeTwice() {
        line.addDepartureTime(12, 34);
        line.addDepartureTime(12, 34);
        assertEquals(1, line.getDepartures().size(), "Add the same departure time twice");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void updateSectionTime() throws Exception {
        initLine();
        line.setStart("A");
        line.updateSectionsTime();
        assertEquals(45, line.getSectionsMap().get(s2), "Time from start");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void updateSectionTimeSetLast() throws Exception {
        initLine();
        line.setStart("A");
        line.updateSectionsTime();
        assertEquals(s2, line.getLast(), "Find last section");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void updateSectionsTimeWithLoop() throws Exception {
        initLine();
        Section loopSection = new Section(t3, t1, "test variant 0", 0, 30);
        line.addSection(loopSection);
        line.setStart("A");
        line.updateSectionsTime();
        assertEquals(95, line.getSectionsMap().get(loopSection),
                "Time from start with loop in line");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void getNextTimeWithoutDepartureTime() throws Exception {
        initLine();
        assertNull(line.getNextTime(s1, new Time(15, 20)),
                "Next departure time for unknown section");
    }

    private void initStart() throws Exception {
        initLine();
        line.setStart("A");
        line.updateSectionsTime();
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void test() throws Exception {
        initStart();
        assertNull(line.getNextTime(s1, new Time(3, 4)),
                "Next departures time with empty departure time ");
    }

    private void initDepartureTime() throws Exception {
        initStart();
        line.addDepartureTime(6, 30);
        line.addDepartureTime(15, 20);
        line.addDepartureTime(15, 30);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void getNextTimeUnknownSectionDuration() throws Exception {
        initDepartureTime();
        assertNull(line.getNextTime(new Section(t3, t1, "", 0, 0), new Time(0, 0)),
                "Next departure time for unknown section ");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void getNextTimeWithNullTime() throws Exception {
        initDepartureTime();
        assertNull(line.getNextTime(s1, null), "Next departure time for unknown section start");
    }

    private void getNextTimeHelper(Section section, Time after, Time expected) throws Exception {
        initDepartureTime();
        assertEquals(expected, line.getNextTime(section, after),
                String.format("Next departure time after %s", after));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void getNextTimeFromStart() throws Exception {
        getNextTimeHelper(s1, new Time(15, 20), new Time(15, 20));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void getNextTimeFirstDepart() throws Exception {
        getNextTimeHelper(s2, new Time(15, 20), new Time(15, 20, 30));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void getNextTimeSecondDepart() throws Exception {
        getNextTimeHelper(s2, new Time(15, 21), new Time(15, 30, 30));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void getNextTimeNextDay() throws Exception {
        getNextTimeHelper(s2, new Time(16, 0), new Time(6, 30, 30));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void getDeparturesTime() throws Exception {
        initDepartureTime();
        List<Time> expected =
                Arrays.asList(new Time(6, 30, 30), new Time(15, 20, 30), new Time(15, 30, 30));
        List<Time> times = line.getDepartureTime(s2);
        assertEquals(expected, times, "Get section departure times");
    }
}
