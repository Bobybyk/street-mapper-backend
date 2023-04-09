package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TimeTest {

    private static final int DEFAULT_TIMEOUT = 2000;

    private void illegalArgumentHelper(int hour, int minute, int second) {
        assertThrows(IllegalArgumentException.class, () -> new Time(hour, minute, second),
                String.format("Invalide values for times %02d:%02d:%02d", hour, minute, second));
    }

    @ParameterizedTest
    @ValueSource(ints = { -2, -1, 24, 25 })
    @Timeout(DEFAULT_TIMEOUT)
    public void illegalNumberOfHour(int hour) {
        illegalArgumentHelper(hour, 0, 0);
    }

    @ParameterizedTest
    @ValueSource(ints = { -2, -1, 60, 61 })
    @Timeout(DEFAULT_TIMEOUT)
    public void illegalNumberOfMinute(int minute) {
        illegalArgumentHelper(0, minute, 0);
    }

    @ParameterizedTest
    @ValueSource(ints = { -2, -1, 60, 61 })
    @Timeout(DEFAULT_TIMEOUT)

    public void illegalNumberOfSecond(int second) {
        illegalArgumentHelper(0, 0, second);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addDurationImmuable() {
        Time t1 = new Time(10, 11, 12);
        Time t2 = t1.addDuration(100);
        assertNotEquals(t1, t2, "Add duration return a new instance");
    }

    private void addDurationHelper(int hour1, int minute1, int second1, int duration, int hour2, int minute2,
            int second2) {
        Time t = new Time(hour1, minute1, second1).addDuration(duration);
        Time expected = new Time(hour2, minute2, second2);
        assertEquals(expected, t, t + " add " + duration + " seconds");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addToNextMinute() {
        addDurationHelper(1, 1, 30, 45, 1, 2, 15);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addMoreThan1minute() {
        addDurationHelper(23, 30, 15, 120, 23, 32, 15);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addToNextHour() {
        addDurationHelper(12, 58, 20, 240 + 5, 13, 2, 25);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addMoreThan1Hour() {
        addDurationHelper(14, 25, 10, 3600 + 1800 + 10, 15, 55, 20);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void addToNextDay() {
        addDurationHelper(23, 50, 0, 645, 0, 0, 45);
    }

    private void timeFromDurationHelper(int duration, int hour, int minute, int second) {
        Time expected = new Time(hour, minute, second);
        Time t = new Time(duration);
        assertEquals(expected, t, String.format("%d to time", duration));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void timeFromDurationSecond() {
        timeFromDurationHelper(30, 0, 0, 30);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void timeFromDurationMinute() {
        timeFromDurationHelper(90, 0, 1, 30);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void timeFromDurationHour() {
        timeFromDurationHelper(3700, 1, 1, 40);
    }
}
