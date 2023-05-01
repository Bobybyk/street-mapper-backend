package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class SectionTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private void illegalArgumentHelper(Station start, Station arrival) {
        assertThrows(IllegalArgumentException.class, () -> new Section(start, arrival, "1", 0, 0),
                "null value");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void sectionWithNullStart() {
        illegalArgumentHelper(null, new Station("test", 0, 0));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void sectionWithNullArrival() {
        illegalArgumentHelper(new Station("test", 0, 0), null);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void distanceTo() {
        Station a = new Station("A", 48.87269027838424, 2.349581904980544);
        Station b = new Station("B", 48.857259804939375, 2.349457279796201);
        Station c = new Station("C", 48.84619669574708, 2.3418737888722356);
        Section s1 = new Section(a, b, "", 1716, 120);
        Section s2 = new Section(b, c, "", 1350, 450);
        assertEquals(1350, s1.distanceTo(s2), "distance between two section arrivals");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void durationTo() {
        Station a = new Station("A", 48.87269027838424, 2.349581904980544);
        Station b = new Station("B", 48.857259804939375, 2.349457279796201);
        Station c = new Station("C", 48.84619669574708, 2.3418737888722356);
        Section s1 = new Section(a, b, "", 1716, 120);
        Section s2 = new Section(b, c, "", 1350, 450);
        s1.setTime(new Time(13, 30));
        s2.setTime(new Time(13, 40));
        assertEquals(930, s1.durationTo(s2), "duration between two section arrivals");
    }
}
