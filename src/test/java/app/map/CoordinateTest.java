package app.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import server.map.Coordinate;

public class CoordinateTest {
    private static final int DEFAULT_TIMEOUT = 2000;

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void getLatitude() {
        assertEquals(48.85955653272677,
                new Coordinate(48.85955653272677, 2.346411849769497).getLatitude(), "Get latitude");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void getLongitude() {
        assertEquals(2.346411849769497,
                new Coordinate(48.85955653272677, 2.346411849769497).getLongitude(),
                "Get longitude");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void equalsDifferentLat() {
        assertNotEquals(new Coordinate(0, 0), new Coordinate(1, 0),
                "Different coordinates are not equals");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void equalsDifferentLong() {
        assertNotEquals(new Coordinate(0, 0), new Coordinate(0, 1),
                "Different coordinates are not equals");
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void equalsSameCoordinate() {
        assertEquals(new Coordinate(1, 2), new Coordinate(1, 2), "Same coordinates are equals");
    }

    private void distanceHelper(double lat1, double long1, double lat2, double long2,
            double expected) {
        Coordinate c1 = new Coordinate(lat1, long1);
        Coordinate c2 = new Coordinate(lat2, long2);
        double res = c1.getDistance(c2);
        assertEquals(expected, res, String.format("Distance between %s and %s", c1, c2));
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void testDistance0() {
        distanceHelper(0, 0, 0, 0, 0);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void testDistanceSameStation() {
        distanceHelper(48.83866086365992, 2.2822419598550767, 48.83866086365992, 2.2822419598550767,
                0);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void testDistanceChatelet14and1() {
        distanceHelper(48.85955653272677, 2.346411849769497, 48.85922471342816, 2.3457609541847755,
                60);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void testDistanceGareDeLyon14and1() {
        distanceHelper(48.8442498880687, 2.372519782814122, 48.8456832067358, 2.3731565937892047,
                166);
    }

    @Test
    @Timeout(DEFAULT_TIMEOUT)
    public void testDistanceBalardAndLourmel() {
        distanceHelper(48.8442498880687, 2.278362661809200, 48.8456832067358, 2.3731565937892047,
                6939);
    }
}
