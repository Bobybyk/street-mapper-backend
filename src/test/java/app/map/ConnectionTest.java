package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class ConnectionTest {
    private static final int DEFAULT_TIMEOUT = 2000;

    private final Station station = new Station("test", 100, 200);
    private final Connection connection = new Connection(station);

    private Section makeSection(String name, double x, double y) {
        Station start = new Station(name, x, y);
        Station arrival = new Station("", 0, 0);
        return new Section(start, arrival, 0, 0);
    }

    private void notStartingHereHelper(String name, double x, double y) {
        Section section = makeSection(name, x, y);
        assertThrows(IllegalArgumentException.class, () -> connection.addSection(section),
                "Add a section not stating at this connection");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addSectionStartingWithDifferentName() {
        notStartingHereHelper("test2", 100, 200);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addSectionStartingWithCoords() {
        notStartingHereHelper("test", 150, 250);
    }

    private Section makeSection(Station start) {
        Station arrival = new Station("", 0, 0);
        return new Section(start, arrival, 0, 0);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addSectionWithGoodStart() {
        Section section = makeSection(station);
        connection.addSection(section);
        assertEquals(section, connection.getSections().get(0), "Add a section");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addTwoDifferentSectionWithGoodStart() {
        Section section1 = makeSection(station);
        Section section2 = makeSection(station);
        connection.addSection(section1);
        connection.addSection(section2);
        assertEquals(2, connection.getSections().size(), "Add two sections");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addTwiceSameSectionWithGoodStart() {
        Section section = makeSection(station);
        connection.addSection(section);
        connection.addSection(section);
        assertEquals(1, connection.getSections().size(), "Add the same section twice");
    }
}
