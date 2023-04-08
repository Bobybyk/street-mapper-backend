package app.map;

import java.io.Serial;
import java.io.Serializable;

/**
 * Classe représentant une station, avec un nom et ses coordonnées
 */
public class Station implements Serializable {
    private final String name;
    private final Coordinate coordinate;

    public Station(String name, double coordinateX, double coordinateY) {
        this.name = name;
        this.coordinate = new Coordinate(coordinateX, coordinateY);
    }

    @Serial
    private static final long serialVersionUID = 2L;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s %s", name, coordinate);
    }
}
