package server.map;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Coordonnée GPS en degrés décimaux
 */
public class Coordinate implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    /**
     * La latitude de la coordonnée en degrés décimaux
     */
    private final double latitude;
    /**
     * La longitude de la coordonnée en degrés décimaux
     */
    private final double longitude;
    /**
     * La latitude de la coordonnée en radian
     */
    private final double latitudeR;
    /**
     * La longitude de la coordonnée en radian
     */
    private final double longitudeR;
    /**
     * Le rayon de la terre
     */
    private static final int EARTH_RADIUS = 6_371_000;

    /**
     * @param latitude la latitude de la coordonnée en degrés décimaux
     * @param longitude la longitude de la coordonnée en degrés décimaux
     */
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
        return String.format("(%.15f, %.15f)", latitude, longitude);
    }

    /**
     * @param c une coordonnée
     * @return la distance en mètre avec la coordonnée {@code c}
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
