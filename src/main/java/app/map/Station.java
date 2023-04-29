package app.map;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Classe représentant une station, avec un nom et ses coordonnées
 */
public class Station implements Serializable {
    private final String name;
    private final Coordinate coordinate;

    /**
     * Vitesse de marche à pied en mètre par seconde
     */
    private static final double WALKING_SPEED = 1.2;

    public Station(String name, double coordinateX, double coordinateY) {
        this.name = name;
        this.coordinate = new Coordinate(coordinateX, coordinateY);
    }

    @Serial
    private static final long serialVersionUID = 4L;

    public String getName() {
        return name;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public String toString() {
        return String.format("%s %s", name, coordinate);
    }

    /**
     * @param station une station
     * @return la distance en mètre pour arriver à station
     */
    public int distanceBetween(Station station) {
        return getCoordinate().getDistance(station.getCoordinate());
    }

    /**
     * @param station une station
     * @return le temps en seconde pour arriver à station en marchant
     */
    public int durationBetween(Station station) {
        return (int) Math.round(distanceBetween(station) * WALKING_SPEED);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Station s)
            return s.name.equals(name) && s.coordinate.equals(coordinate);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, coordinate);
    }
}
