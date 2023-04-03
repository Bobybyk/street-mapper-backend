package app.map;

import java.io.Serial;
import java.io.Serializable;

/**
 * Classe représentant une station, avec un nom et ses coordonnées
 */
public record Station(String name, double coordinateX, double coordinateY) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return String.format("%s (%f, %f)", name, coordinateX, coordinateY);
    }
}
