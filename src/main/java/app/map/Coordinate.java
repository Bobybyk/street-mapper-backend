package app.map;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Coordonnée GPS en degrés décimaux
 */
public class Coordinate implements Serializable {
    private final double latitude;
    private final double longitude;
    private final double latitudeR;
    private final double longitudeR;

    private static final int EARTH_RADIUS = 6_371_000;

    @Serial
    private static final long serialVersionUID = 2L;

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.latitudeR = Math.toRadians(latitude);
        this.longitudeR = Math.toRadians(longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", latitude, longitude);
    }

    /**
     * @param c une coordonnée
     * @return la distance entre deux coordonnées en mètre
     */
    public int getDistance(Coordinate c) {
        double x = Math.sin(latitudeR) * Math.sin(c.latitudeR);
        double y = Math.cos(latitudeR) * Math.cos(c.latitudeR);
        double z = Math.cos(longitudeR - c.longitudeR);
        return (int) Math.round(Math.acos(x + (y * z)) * EARTH_RADIUS);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate c)
            return c.latitude == latitude && c.longitude == longitude;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
