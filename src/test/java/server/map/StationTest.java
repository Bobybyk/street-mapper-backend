package server.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class StationTest {
    private static final int DEFAULT_TIMEOUT = 2000;

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void equalsDifferentCoords() {
        assertNotEquals(new Station("", 0, 0), new Station("", 1, 0),
                "Station with different coordinates are not equals");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void equalsDifferentName() {
        assertNotEquals(new Station("A", 0, 0), new Station("B", 0, 0),
                "Station with different name are not equals");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void equalsSameValue() {
        assertEquals(new Station("A", 0, 0), new Station("A", 0, 0),
                "Station with same value are equals");
    }

    private void distanceBetweenHelper(Station s1, Station s2, double expected) {
        double res = s1.distanceBetween(s2);
        assertEquals(expected, res, String.format("Distance between %s and %s", s1, s2));
    }

    private void durationBetweenHelper(Station s1, Station s2, double expected) {
        double res = s1.durationBetween(s2);
        assertEquals(expected, res, String.format("Distance between %s and %s", s1, s2));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void distanceChatelet14and1() {
        Station s1 = new Station("", 48.85955653272677, 2.346411849769497);
        Station s2 = new Station("", 48.85922471342816, 2.3457609541847755);
        distanceBetweenHelper(s1, s2, 60);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void distanceGareDeLyon14and1() {
        Station s1 = new Station("", 48.8442498880687, 2.372519782814122);
        Station s2 = new Station("", 48.8456832067358, 2.3731565937892047);
        distanceBetweenHelper(s1, s2, 166);
    }


    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void durationChatelet14and1() {
        Station s1 = new Station("", 48.85955653272677, 2.346411849769497);
        Station s2 = new Station("", 48.85922471342816, 2.3457609541847755);
        durationBetweenHelper(s1, s2, 50);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    void durationGareDeLyon14and1() {
        Station s1 = new Station("", 48.8442498880687, 2.372519782814122);
        Station s2 = new Station("", 48.8456832067358, 2.3731565937892047);
        durationBetweenHelper(s1, s2, 138);
    }
}
