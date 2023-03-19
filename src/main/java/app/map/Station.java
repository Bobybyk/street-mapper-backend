package app.map;

import java.util.Objects;

/**
 * Classe représentant une station, avec un nom et ses coordonnées
 */
public final class Station {
    /**
     * Le nom de la station
     */
    private final String name;
    /**
     * La coordonnée en x
     */
    private final double coordinateX;
    /**
     * La coordonnée en y
     */
    private final double coordinateY;

    /**
     * Créer une station
     * 
     * @param name le nom
     * @param x    la coordonnée en x
     * @param y    la coordonnée en y
     */
    public Station(String name, double x, double y) {
        this.name = name;
        coordinateX = x;
        coordinateY = y;
    }

    @Override
    public String toString() {
        return String.format("%s (%f, %f)", name, coordinateX, coordinateY);
    }

    /**
     * @return le nom de la station
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Station o) {
            return name.equals(o.name) && coordinateX == o.coordinateX && coordinateY == o.coordinateY;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, coordinateX, coordinateY);
    }
}
