package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class ConnectionTest {
    private static final int DEFAULT_TIMEOUT = 2000;

    private final Station start = new Station("test", 100, 200);
    private final Connection connection = new Connection(start);

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void nullStation() {
        assertThrows(IllegalArgumentException.class, () -> new Connection(null), "null station");
    }

    private Section makeSectionFromStart(String name, double x, double y) {
        Station start = new Station(name, x, y);
        Station arrival = new Station("", 0, 0);
        return new Section(start, arrival, 0, 0);
    }

    private Section makeSectionFromArrival(String name, double x, double y) {
        Station arrival = new Station(name, x, y);
        return new Section(start, arrival, 0, 0);
    }

    private Section makeSectionFromArrival() {
        return makeSectionFromArrival("", 0, 0);
    }

    private void notStartingHereHelper(String name, double x, double y) {
        Section section = makeSectionFromStart(name, x, y);
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

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addSectionWithGoodStart() {
        Section section = makeSectionFromArrival();
        connection.addSection(section);
        assertEquals(section, connection.getSections().get(0), "Add a section");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addTwoDifferentSectionWithGoodStart() {
        Section section1 = makeSectionFromArrival("a", 1, 2);
        Section section2 = makeSectionFromArrival("b", 3, 4);
        connection.addSection(section1);
        connection.addSection(section2);
        assertEquals(2, connection.getSections().size(), "Add two sections");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addTwiceSameSectionWithGoodStart() {
        Section section1 = makeSectionFromArrival();
        Section section2 = makeSectionFromArrival();
        connection.addSection(section1);
        connection.addSection(section2);
        assertEquals(1, connection.getSections().size(), "Add the same section twice");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addNullSection() {
        assertThrows(IllegalArgumentException.class, () -> connection.addSection(null), "Add null section");
    }
}