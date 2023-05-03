package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import server.map.Section;
import server.map.Station;
import server.map.Time;

public class SectionTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private final Station a = new Station("A", 48.87269027838424, 2.349581904980544);
    private final Station b = new Station("B", 48.857259804939375, 2.349457279796201);
    private final Station c = new Station("C", 48.84619669574708, 2.3418737888722356);
    private final Section s1 = new Section(a, b, "", 1716, 120);
    private final Section s2 = new Section(b, c, "", 1350, 450);

    private void illegalArgumentHelper(Station start, Station arrival, String line) {
        assertThrows(IllegalArgumentException.class, () -> new Section(start, arrival, line, 0, 0),
                "null value");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void sectionWithNullStart() {
        illegalArgumentHelper(null, new Station("test", 0, 0), "");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void sectionWithNullArrival() {
        illegalArgumentHelper(new Station("test", 0, 0), null, "");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void distanceToNull() {
        assertThrows(IllegalArgumentException.class, () -> s1.distanceTo(null), "Distance to null");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void distanceTo() {
        assertEquals(1350, s1.distanceTo(s2), "Distance between two section arrivals");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void durationToNull() {
        assertThrows(IllegalArgumentException.class, () -> s1.durationTo(null), "Duration to null");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void durationToS1ArrivalTimeIsNull() {
        s1.setTime(new Time(13, 30));
        assertThrows(IllegalArgumentException.class, () -> s1.durationTo(null), "Duration to null");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void durationToS2ArrivalTimeIsNull() {
        s2.setTime(new Time(13, 30));
        assertThrows(IllegalArgumentException.class, () -> s1.durationTo(null), "Duration to null");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void durationTo() {
        s1.setTime(new Time(13, 30));
        s2.setTime(new Time(13, 40));
        assertEquals(930, s1.durationTo(s2), "Distance between two section arrivals");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void equalsWithDifferentTime() {
        Section s1bis = new Section(a, b, "", 1716, 120);
        s1.setTime(new Time(12, 45));
        s1bis.setTime(new Time(11, 34));
        assertEquals(s1, s1bis, "Sections with different time");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void equalsWithDifferentStartStation() {
        Section s1bis = new Section(c, b, "", 1716, 120);
        assertNotEquals(s1, s1bis, "Sections with different start station");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void equalsWithDifferentArrivalStation() {
        Section s1bis = new Section(a, c, "", 1716, 120);
        assertNotEquals(s1, s1bis, "Sections with different arrival station");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void equalsWithDifferentLineName() {
        Section s1bis = new Section(a, b, "test", 1716, 120);
        assertNotEquals(s1, s1bis, "Sections with different line");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void equalsWithDifferentDistance() {
        Section s1bis = new Section(a, b, "", 0, 120);
        assertNotEquals(s1, s1bis, "Sections with different distance");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void equalsWithDifferentDuration() {
        Section s1bis = new Section(a, b, "", 1716, 0);
        assertNotEquals(s1, s1bis, "Sections with different duration");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void equalsWithOneNullLineName() {
        Section s1bis = new Section(a, b, null, 1716, 120);
        assertNotEquals(s1, s1bis, "Sections comparaison with null line");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void equalsWithOneNullLineNameSwitch() {
        Section s1bis = new Section(a, b, null, 1716, 120);
        Section s1biss = new Section(a, b, "", 1716, 120);
        assertNotEquals(s1bis, s1biss, "Sections comparaison with null line");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void copieWithoutTime() {
        assertEquals(s1, new Section(s1), "Copie section with null time");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void copieWithTime() {
        s1.setTime(new Time(12, 30));
        assertEquals(s1, new Section(a, b, "", 1716, 120), "Copie section with non-null time");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void sectionToRouteNull() {
        assertNull(Section.sectionsToTrajet(null), "null to trajet");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void sectionsToTrajetEmpty() {
        assertTrue(Section.sectionsToTrajet(new ArrayList<Section>()).isEmpty(), "Empty to trajet");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void sectionToRoute() {
        Section s3 = new Section(a, b, "toto", 10, 30);
        Section s4 = new Section(b, c, "toto", 40, 20);
        List<Section> sections = Arrays.asList(s1, s2, s3, s4);
        List<Section> expected =
                Arrays.asList(new Section(a, c, "", 3066, 570), new Section(a, c, "toto", 50, 50));
        assertEquals(expected, Section.sectionsToTrajet(sections), "sectionToRoute");
    }
}
