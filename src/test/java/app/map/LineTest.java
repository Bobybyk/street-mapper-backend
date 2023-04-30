package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import app.map.Line.DifferentStartException;
import app.map.Line.StartStationNotFoundException;

public class LineTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private final Line line = new Line("test", "0");

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void nullName() {
        assertThrows(IllegalArgumentException.class, () -> new Line(null, "0"), "null line name");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addNullSection() {
        assertThrows(IllegalArgumentException.class, () -> line.addSection(null),
                "Add null section");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void setNullStart() {
        assertThrows(IllegalArgumentException.class, () -> line.setStart(null),
                "Set null as start");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void setStartWithNotExistingStation() {
        assertThrows(StartStationNotFoundException.class, () -> line.setStart("test"),
                "Start station not in line");
    }

    private void initLine() {
        Station station1 = new Station("1", 0, 0);
        Station station2 = new Station("2", 0, 0);
        Station station3 = new Station("3", 0, 0);
        line.addSection(new Section(station1, station2, "1 variant 2", 0, 0));
        line.addSection(new Section(station2, station3, "1 variant 2", 0, 0));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void setTwoDifferentStart() throws Exception {
        initLine();
        line.setStart("1");
        assertThrows(DifferentStartException.class, () -> {
            line.setStart("2");
        }, "Set two different start");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void setStartNotNull() throws Exception {
        initLine();
        Section section = line.getSections().get(1);
        String stationName = section.getStart().getName();
        line.setStart(stationName);
        assertEquals(section, line.getStart(), "Set start");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void setStartNotNullTwice() throws Exception {
        initLine();
        Section section = line.getSections().get(1);
        String stationName = section.getStart().getName();
        line.setStart(stationName);
        line.setStart(stationName);
        assertEquals(section, line.getStart(), "Set the same start twice");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addDepartureTime() {
        line.addDepartureTime(12, 34);
        Time expected = new Time(12, 34, 0);
        assertEquals(expected, line.getDepartures().get(0), "Add a departure time");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addDepartureTimeTwice() {
        line.addDepartureTime(12, 34);
        line.addDepartureTime(12, 34);
        assertEquals(1, line.getDepartures().size(), "Add the same departure time twice");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void updateSectionTime() throws Exception {
        Station t1 = new Station("1", 0, 0);
        Station t2 = new Station("2", 0, 0);
        Station t3 = new Station("3", 0, 0);
        line.addSection(new Section(t1, t2, "test variant 0", 0, 10));
        Section section = new Section(t2, t3, "test variant 0", 0, 15);
        line.addSection(section);
        line.setStart("1");
        line.updateSectionsTime();
        Map<Section, Integer> times = line.getSectionsMap();
        assertEquals(45, times.get(section), "Time from start");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void updateSectionTimeSetLast() throws Exception {
        Station t1 = new Station("1", 0, 0);
        Station t2 = new Station("2", 0, 0);
        Station t3 = new Station("3", 0, 0);
        Section s1 = new Section(t1, t2, "test variant 0", 0, 10);
        Section s2 = new Section(t2, t3, "test variant 0", 0, 15);
        line.addSection(s1);
        line.addSection(s2);
        line.setStart("1");
        line.updateSectionsTime();
        assertEquals(s2, line.getLast(), "Find last section");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void getNextTimeFromStart() throws Exception {
        Station t1 = new Station("1", 0, 0);
        Station t2 = new Station("2", 0, 0);
        Station t3 = new Station("3", 0, 0);
        Section s1 = new Section(t1, t2, "test variant 0", 0, 10);
        Section s2 = new Section(t2, t3, "test variant 0", 0, 15);
        line.addSection(s1);
        line.addSection(s2);
        line.setStart("1");
        line.updateSectionsTime();
        line.addDepartureTime(15, 20);
        line.addDepartureTime(15, 30);
        assertEquals(new Time(15, 20), line.getNextTime(s1, new Time(15, 20)),
                "Find departure time after 15:20");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void getNextTimeFirstDepart() throws Exception {
        Station t1 = new Station("1", 0, 0);
        Station t2 = new Station("2", 0, 0);
        Station t3 = new Station("3", 0, 0);
        Section s1 = new Section(t1, t2, "test variant 0", 0, 10);
        Section s2 = new Section(t2, t3, "test variant 0", 0, 15);
        line.addSection(s1);
        line.addSection(s2);
        line.setStart("1");
        line.updateSectionsTime();
        for (Map.Entry<Section, Integer> entry : line.getSectionsMap().entrySet()) {
            System.out.println("section = " + entry.getKey() + " value " + entry.getValue());
        }
        line.addDepartureTime(15, 20);
        line.addDepartureTime(15, 30);
        assertEquals(new Time(15, 20, 30), line.getNextTime(s2, new Time(15, 20)),
                "Find departure time after 15:20");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void getNextTime() throws Exception {
        Station t1 = new Station("1", 0, 0);
        Station t2 = new Station("2", 0, 0);
        Station t3 = new Station("3", 0, 0);
        Section s1 = new Section(t1, t2, "test variant 0", 0, 10);
        Section s2 = new Section(t2, t3, "test variant 0", 0, 15);
        line.addSection(s1);
        line.addSection(s2);
        line.setStart("1");
        line.updateSectionsTime();
        for (Map.Entry<Section, Integer> entry : line.getSectionsMap().entrySet()) {
            System.out.println("section = " + entry.getKey() + " value " + entry.getValue());
        }
        line.addDepartureTime(15, 20);
        line.addDepartureTime(15, 30);
        assertEquals(new Time(15, 30, 30), line.getNextTime(s2, new Time(15, 21)),
                "Find departure time after 15:20");
    }
}
