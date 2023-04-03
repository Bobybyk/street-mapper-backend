package app.map;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class SectionTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private void illegalArgumentHelper(Station start, Station arrival) {
        assertThrows(IllegalArgumentException.class, () -> new Section(start, arrival, 0, 0), "null value");
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
}
