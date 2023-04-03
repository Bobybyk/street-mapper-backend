package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import app.map.Line.DifferentStartException;
import app.map.Line.StartStationNotFoundException;

public class LineTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private final Line line = new Line("test", 0);

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void nullName() {
        assertThrows(IllegalArgumentException.class, () -> new Line(null, 0), "null line name");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addNullSection() {
        assertThrows(IllegalArgumentException.class, () -> line.addSection(null), "Add null section");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void setNullStart() {
        assertThrows(IllegalArgumentException.class, () -> line.setStart(null), "Set null as start");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void setStartWithNotExistingStation() {
        assertThrows(StartStationNotFoundException.class, () -> line.setStart("test"), "Start station not in line");
    }

    private void initLine() {
        Station station1 = new Station("1", 0, 0);
        Station station2 = new Station("2", 0, 0);
        Station station3 = new Station("3", 0, 0);
        line.addSection(new Section(station1, station2, 0, 0));
        line.addSection(new Section(station2, station3, 0, 0));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void setTwoDifferentStart()
            throws IllegalArgumentException, StartStationNotFoundException, DifferentStartException {
        initLine();
        line.setStart("1");
        assertThrows(DifferentStartException.class, () -> {
            line.setStart("2");
        }, "Set two different start");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void setStartNotNull()
            throws IllegalArgumentException, StartStationNotFoundException, DifferentStartException {
        initLine();
        Section section = line.getSections().get(1);
        String stationName = section.start().name();
        line.setStart(stationName);
        assertEquals(section, line.getStart(), "Set start");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void setStartNotNullTwice()
            throws IllegalArgumentException, StartStationNotFoundException, DifferentStartException {
        initLine();
        Section section = line.getSections().get(1);
        String stationName = section.start().name();
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
    public void addDepartureTimeTw() {
        line.addDepartureTime(12, 34);
        line.addDepartureTime(12, 34);
        assertEquals(1, line.getDepartures().size(), "Add the same departure time twice");
    }
}
