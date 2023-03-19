package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TimeTest {
    @Test
    public void checkPreconditionHourLt0() {
        assertThrows(IllegalArgumentException.class, () -> new Time(-1, 0, 0));
    }

    @Test
    public void checkPreconditionHourGt23() {
        assertThrows(IllegalArgumentException.class, () -> new Time(24, 0, 0));
    }

    @Test
    public void checkPreconditionMinuteLt0() {
        assertThrows(IllegalArgumentException.class, () -> new Time(0, -1, 0));
    }

    @Test
    public void checkPreconditionMinuteGt59() {
        assertThrows(IllegalArgumentException.class, () -> new Time(0, 60, 0));
    }

    @Test
    public void checkPreconditionSecondLt0() {
        assertThrows(IllegalArgumentException.class, () -> new Time(0, 0, -1));
    }

    @Test
    public void checkPreconditionSecondGt59() {
        assertThrows(IllegalArgumentException.class, () -> new Time(0, 0, 60));
    }

    @Test
    public void midnight() {
        assertEquals(new Time(0, 0, 0), new Time(0, 0, 0), "00:00:00");
    }

    @Test
    public void add0sec() {
        assertEquals(new Time(0, 0, 59), new Time(0, 0, 59).addDuration(0), "Add 0 second");
    }

    @Test
    public void addToNextMinute() {
        assertEquals(new Time(0, 1, 0), new Time(0, 0, 59).addDuration(1), "Add to next minute");
    }

    @Test
    public void addMoreThan1minute() {
        assertEquals(new Time(0, 2, 0), new Time(0, 0, 59).addDuration(61), "Add more than one minute");
    }

    @Test
    public void addToNextHour() {
        assertEquals(new Time(1, 0, 0), new Time(0, 59, 59).addDuration(1), "Add to next hour");
    }

    @Test
    public void addMoreThan1Hour() {
        assertEquals(new Time(2, 0, 0), new Time(0, 59, 59).addDuration(3601), "Add to next hour");
    }
}